/*
 * @(#)SnmpInterfacePoller.java
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
package dpnm.netma.ngom.backend;

import dpnm.netma.ngom.Conf;
import dpnm.netma.ngom.NGOMException;
import dpnm.netma.ngom.util.Logger;
import dpnm.netma.ngom.comm.snmp.SnmpReader;

import dpnm.netma.ngom.data.*;

import java.io.IOException;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * SnmpPoller is for polling Snmp data from devices
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
public class SnmpInterfacePoller extends Thread {
	static final int RECONFIGURE_RETRIES = 3;

	private Device device;
	private Interface in;

	private SnmpReader reader;

	public SnmpInterfacePoller(Device device, Interface in) {
		setDaemon(true);
		in.setSampling(true);
		this.device = device;
		this.in = in;
	}

	public void run() {
		try {
			reader = new SnmpReader(device.getHost(), device.getPort(), device.getCommunity());
			if(in.getIfIndex() < 0) {
				findIfIndex();
			}
			if(in.getIfIndex() >= 0) {
                if (Conf.DEBUG) {
                    Logger.getInstance().logBackend("SnmpInterfacePoller", 
				    "Sampling: " + in.getIfDescr() + "@" + device.getHost() +
					" [" + in.getIfIndex() + "]");
                }
				int ix = in.getIfIndex();
				String[] oids = new String[] { // OIDS to catch
                        "sysUpTime", 
                        "ifDescr." + ix, 
                        /*
                        "ifType." + ix, 
                        "ifMtu." +ix, 
                        */
                        
                        "ifSpeed." +ix, 
                        /*
                        "ifPhyAddress." + ix, 
                        */
                        "ifAdminStatus." + ix, 
                        "ifOperStatus." + ix, 
                        /*
                        "ifLastChange." + ix, 
                        */
                        "ifInOctets." + ix, 
                        /*
                        "ifInUcastPkts." + ix, 
                        "ifInNUcastPkts." + ix, 
                        "ifInDiscards." + ix, 
                        "ifInErrors." + ix, 
                        "ifInUnknownProtos." + ix, 
                        */
                        "ifOutOctets." + ix
                        /*
                        "ifOutUcastPkts." + ix, 
                        "ifOutNUcastPkts." + ix, 
                        "ifOutDiscards." + ix, 
                        "ifOutErrors." + ix, 
                        "ifOutDiscards." +ix, 
                        "ifOutQLen." +ix, 
                        "ifSpecific." +ix
                        */
				};
				String[] values = reader.get(oids);
				InterfaceSample sample = createInterfaceSample(values);
                BackendManager.getInstance().getRrdWriter().store(sample);
				in.processSample(sample);
			}
		}
		catch (IOException e) {
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("SnmpManager", 
				    "IOException on " + getLabel() + ": " + e);
			}
		} catch (NGOMException e) {
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("SnmpManager", 
				    "NGOMException on " + getLabel() + ": " + e);
			}
		} finally {
			if(reader != null) {
				reader.close();
			}
			in.setSampling(false);
		}
	}

	private void findIfIndex() throws NGOMException {
		for(int i = 0; i < RECONFIGURE_RETRIES; i++) {
			try {
				int ifIndex = reader.getIfIndexByIfDescr(in.getIfDescr());
				if(ifIndex >= 0) {
					// new interface number found
					String alias = reader.get("ifAlias", ifIndex);
					in.setIfAlias(alias);
					in.switchToIfIndex(ifIndex);
					return;
				}
				else {
					// definitely no such interface
					break;
				}
			}
			catch(IOException ioe) {
                if (Conf.DEBUG) {
                    Logger.getInstance().logBackend("SnmpManager", 
					    "IOError while reconfiguring " + getLabel() + ": " + ioe);
                }
			}
		}
		// new interface number not found after several retries
		in.deactivate();
        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("SnmpManager", 
			    "Link " + getLabel() + " not found, link deactivated");
		}
	}

	private InterfaceSample createInterfaceSample(String[] values) {
		InterfaceSample sample = new InterfaceSample();
		sample.setHost(device.getHost());
        int i = 0;
		if(values[i] != null) {
			sample.setSysUpTime(Long.parseLong(values[i++]));
		}
		if(values[i] != null) {
			sample.setIfDescr(values[i++]);
		}
		/*
		if(values[i] != null) {
			sample.setIfType(Integer.parseInt(values[i++]));
		}
		if(values[i] != null) {
			sample.setIfMtu(Integer.parseInt(values[i++]));
		}
		*/
		if (values[i] != null) {
			sample.setIfSpeed(Long.parseLong(values[i++]));
		}
		/*
		if (values[i] != null) {
			sample.setIfPhyAddress(values[i++].toUpperCase().replaceAll(" ", ":"));
		}
		*/
		if (values[i] != null) {
			sample.setIfAdminStatus(Integer.parseInt(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOperStatus(Integer.parseInt(values[i++]));
		}
		/*
		if (values[i] != null) {
			sample.setIfLastChange(Long.parseLong(values[i++]));
		}
		*/
		if (values[i] != null) {
			sample.setIfInOctets(Long.parseLong(values[i++]));
		}
		/*
		if (values[i] != null) {
			sample.setIfInUcastPkts(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfInNUcastPkts(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfInDiscards(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfInErrors(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfInUnknownProtos(Long.parseLong(values[i++]));
		}
		*/
		if (values[i] != null) {
			sample.setIfOutOctets(Long.parseLong(values[i++]));
		}
		/*
		if (values[i] != null) {
			sample.setIfOutUcastPkts(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOutNUcastPkts(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOutDiscards(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOutErrors(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOutQLen(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfSpecific(Integer.parseInt(values[i++]));
		}
		*/
        return sample;
	}

	String getLabel() {
		return in.getIfDescr() + "@" + device.getHost();
	}
}
