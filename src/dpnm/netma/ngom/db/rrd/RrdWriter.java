/*
 * @(#)RrdWriter.java
 * 
 * Created on 2008. 02. 19
 *
 *	This software is the confidential and proprietary information of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.netma.ngom.db.rrd;

import dpnm.netma.ngom.Conf;
import dpnm.netma.ngom.backend.Resource;
import dpnm.netma.ngom.NGOMException;
import dpnm.netma.ngom.util.Logger;
import dpnm.netma.ngom.data.InterfaceSample;
import dpnm.netma.ngom.data.DeviceSample;

import org.jrobin.core.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is for Device
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
public class RrdWriter extends Thread {
	private RrdDefTemplate rrdDefTemplate;
	private long sampleCount, badSavesCount, goodSavesCount;
	private List queue = Collections.synchronizedList(new LinkedList());

	private static RrdDbPool pool = null;
	
	static {
		try {
			pool = RrdDbPool.getInstance();
		} catch (Exception ex) {
		}
	}

	private volatile boolean active = true;

	public RrdWriter() throws NGOMException {
		// get definition from template
		try {
			rrdDefTemplate = 
                new RrdDefTemplate(new File(Resource.getRrdTemplateFile()));
		} catch (IOException e) {
			throw new NGOMException(e);
		} catch (RrdException e) {
			throw new NGOMException(e);
		}
		
		start();
	}

    public void run() {
        if (Conf.DEBUG) {
              Logger.getInstance().logBackend("RrdWriter", 
                    "Rrd Archiver started");
        }

		// the code is plain ugly but it should work
		while(active) {
           	while(active && queue.size() == 0) {
			   synchronized(this) {
				   try {
					   wait();
				   } catch (InterruptedException e) {
                       if (Conf.DEBUG) {
                          Logger.getInstance().logBackend("FrontCommManager", 
                                  e.toString());
                        }
				   }
			   }
			}
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("RrdWriter", 
    	            "RRD Archiver processing queue size = " + queue.size());
    		}
			if(active && queue.size() > 0) {
				Object sample = queue.remove(0);
				if (sample instanceof DeviceSample) {
					process((DeviceSample)sample);
				} else if (sample instanceof InterfaceSample) {
					process((InterfaceSample)sample);
				}
			}
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("RrdWriter", 
    	            "RRD Archiver processed");
    		}
		}
        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("RrdWriter", 
	            "RRD Archiver ended");
        }
	}

	public void terminate() {
		active = false;
		synchronized(this) {
			notify();
		}
	}
	
	private void process(DeviceSample deviceSample) {
		RrdDb rrdDb = null;
		try {
			rrdDb = openRrdFileFor(deviceSample);
			Sample sample = rrdDb.createSample();
			sample.setTime(deviceSample.getTimestamp());
				sample.setValue("cpu", deviceSample.getCpuUtil());
				sample.setValue("memory", deviceSample.getMemoryUtil());
                if (Conf.DEBUG) {
                    Logger.getInstance().logBackend("RrdWriter", 
                    		"sample (cpu) = " + deviceSample.getCpuUtil());
                    Logger.getInstance().logBackend("RrdWriter", 
                    		"sample (memory) = " + deviceSample.getMemoryUtil());
                }
			sample.update();
			goodSavesCount++;
		} catch (IOException e) {
            if (Conf.DEBUG) {
                  Logger.getInstance().logBackend("FrontCommManager", 
                        e.toString());
            }
			badSavesCount++;
		} catch (RrdException e) {
            if (Conf.DEBUG) {
                  Logger.getInstance().logBackend("FrontCommManager", 
                        e.toString());
            }
			badSavesCount++;
		} finally {
			try {
				pool.release(rrdDb);
			} catch (IOException e) {
                if (Conf.DEBUG) {
                    Logger.getInstance().logBackend("RrdWriter", 
                        e.toString());
                }
			} catch (RrdException e) {
                if (Conf.DEBUG) {
                    Logger.getInstance().logBackend("RrdWriter", 
                        e.toString());
                }
			}
		}		
	}

	private void process(InterfaceSample interfaceSample) {
		RrdDb rrdDb = null;
		try {
			rrdDb = openRrdFileFor(interfaceSample);
			Sample sample = rrdDb.createSample();
			sample.setTime(interfaceSample.getTimestamp());
			if(interfaceSample.isValid()) {
				sample.setValue("in", interfaceSample.getIfInOctets());
				sample.setValue("out", interfaceSample.getIfOutOctets());
                if (Conf.DEBUG) {
                    Logger.getInstance().logBackend("RrdWriter", 
                    		"sample (in) = " + interfaceSample.getIfInOctets());
                    Logger.getInstance().logBackend("RrdWriter", 
                    		"sample (out) = " + interfaceSample.getIfOutOctets());
                }
			}
			sample.update();
			goodSavesCount++;
		} catch (IOException e) {
            if (Conf.DEBUG) {
                  Logger.getInstance().logBackend("FrontCommManager", 
                        e.toString());
            }
			badSavesCount++;
		} catch (RrdException e) {
            if (Conf.DEBUG) {
                  Logger.getInstance().logBackend("FrontCommManager", 
                        e.toString());
            }
			badSavesCount++;
		} finally {
			try {
				pool.release(rrdDb);
			} catch (IOException e) {
                if (Conf.DEBUG) {
                    Logger.getInstance().logBackend("RrdWriter", 
                        e.toString());
                }
			} catch (RrdException e) {
                if (Conf.DEBUG) {
                    Logger.getInstance().logBackend("RrdWriter", 
                        e.toString());
                }
			}
		}
	}

	private String getRrdFilenameFor(DeviceSample deviceSample) {
		return getRrdFilename(deviceSample.getHost());
	}
	
	private String getRrdFilenameFor(InterfaceSample interfaceSample) {
		return getRrdFilename(interfaceSample.getHost(), interfaceSample.getIfDescr());
	}

	static String getRrdFilename(String host) {
		String filename =  host.replaceFirst(":", "_") + ".rrd";
		return Resource.getRrdDir() + filename;
	}
	static String getRrdFilename(String host, String ifDescr) {
		String filename = ifDescr.replaceAll("[^0-9a-zA-Z]", "_") +
			"@" + host.replaceFirst(":", "_") + ".rrd";
		return Resource.getRrdDir() + filename;
	}

	public synchronized void store(Object sample) {
		queue.add(sample);
		sampleCount++;
		notify();
	}

	private RrdDb openRrdFileFor(DeviceSample deviceSample)
		throws IOException, RrdException {
		String rrdFile = getRrdFilenameFor(deviceSample);
		if(new File(rrdFile).exists()) {
			return pool.requestRrdDb(rrdFile);
		}
		else {
			// create RRD file first
			RrdDef rrdDef = new RrdDef(rrdFile);
			rrdDef.addDatasource("cpu", DsTypes.DT_GAUGE,  600, 0, 100);
			rrdDef.addDatasource("memory", DsTypes.DT_GAUGE,  600, 0, 100);
			rrdDef.addArchive(ConsolFuns.CF_AVERAGE, 0.5, 1, 1200);
			rrdDef.addArchive(ConsolFuns.CF_MIN, 0.5, 12, 2400);
			rrdDef.addArchive(ConsolFuns.CF_MAX, 0.5, 12, 2400);
			rrdDef.addArchive(ConsolFuns.CF_AVERAGE, 0.5, 12, 2400);
			/*
			rrdDef.addArchive(ConsolFuns.CF_AVERAGE, 0.5, 1, 600);
			rrdDef.addArchive(ConsolFuns.CF_AVERAGE, 0.5, 6, 700);
			rrdDef.addArchive(ConsolFuns.CF_AVERAGE, 0.5, 24, 775);
			rrdDef.addArchive(ConsolFuns.CF_AVERAGE, 0.5, 288, 797);
			rrdDef.addArchive(ConsolFuns.CF_MAX, 0.5, 1, 600);
			rrdDef.addArchive(ConsolFuns.CF_MAX, 0.5, 6, 700);
			rrdDef.addArchive(ConsolFuns.CF_MAX, 0.5, 24, 775);
			rrdDef.addArchive(ConsolFuns.CF_MAX, 0.5, 288, 797);
			rrdDef.addArchive(ConsolFuns.CF_MIN, 0.5, 1, 600);
			rrdDef.addArchive(ConsolFuns.CF_MIN, 0.5, 6, 700);
			rrdDef.addArchive(ConsolFuns.CF_MIN, 0.5, 24, 775);
			rrdDef.addArchive(ConsolFuns.CF_MIN, 0.5, 288, 797);
			*/
			RrdDb rrdDb = new RrdDb(rrdDef);	
			
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("RrdWriter", 
				    "Creating: " + rrdFile);
            }
            
			return pool.requestRrdDb(rrdDef);
		}
	}
	private RrdDb openRrdFileFor(InterfaceSample interfaceSample)
		throws IOException, RrdException {
		String rrdFile = getRrdFilenameFor(interfaceSample);
		if(new File(rrdFile).exists()) {
			return pool.requestRrdDb(rrdFile);
		}
		else {
			// create RRD file first
			rrdDefTemplate.setVariable("path", rrdFile);
			RrdDef rrdDef = rrdDefTemplate.getRrdDef();
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("RrdWriter", 
				    "Creating: " + rrdFile);
            }
			return pool.requestRrdDb(rrdDef);
		}
	}

	public long getSampleCount() {
		return sampleCount;
	}

	public long getBadSavesCount() {
		return badSavesCount;
	}

	public long getGoodSavesCount() {
		return goodSavesCount;
	}

	public long getSavesCount() {
		return getGoodSavesCount() + getBadSavesCount();
	}

    public double getPoolEfficency() {
    	return 0;
//        return pool.getPoolEfficency();
    }
}
