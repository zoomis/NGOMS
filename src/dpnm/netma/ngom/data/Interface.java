/*
 * @(#)Interface.java
 * 
 * Created on 2008. 02. 18
 *
 *	This software is the confidential and proprietary information of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.netma.ngom.data;

import dpnm.netma.ngom.Conf;
import dpnm.netma.ngom.NGOMException;
import dpnm.netma.ngom.util.Logger;
import dpnm.netma.ngom.util.Util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Hashtable;

/**
 * This class is for Interface
 *
 * @author Eric Kang
 * @since 2008/02/18
 * @version $Revision: 1.1 $
 */
public class Interface {
    /*
     * constant information
     */
    public static final String HOST   	    	= "host";
    public static final String IFINDEX       	= "ifIndex";
    public static final String IFDESCR       	= "ifDescr";
    public static final String IFALIAS       	= "ifAlias";
    public static final String DESCR         	= "description";
    public static final String SAMPLINGINTERVAL = "samplingInterval";
    public static final String ACTIVE        	= "active";
    public static final String SAMPLECOUNT   	= "sampleCount";
    public static final String LASTSAMPLETIME   = "lastSampleTime";
    public static final String IFSPEED          = "ifSpeed";
    public static final String IFINOCTETS    	= "ifInOctets";
    public static final String IFOUTOCTETS   	= "ifOutOctets";
    public static final String IFINERRORS    	= "ifInErrors";
    public static final String IFOUTERRORS   	= "ifOutErrors";
    public static final String IFADMINSTATUS   	= "ifAdminStatus";
    public static final String IFOPERSTATUS   	= "ifOperStatus";
    public static final String INKBPS        	= "inKbps";
    public static final String OUTKBPS       	= "outKbps";
    public static final String TOTALKBPS     	= "totalKbps";
    public static final String INUTIL        	= "inUtil";
    public static final String OUTUTIL       	= "outUtil";
    public static final String TOTALUTIL     	= "totalUtil";
    public static final String INTERFACE     	= "interface";
    public static final String LASTSAMPLE     	= "lastSample";


 
	private int    ifIndex          = -1;
	private String ifDescr          = "";
	private String ifAlias          = "";
	private String descr            = "";
	private int    samplingInterval = Conf.DEFAULT_SAMPLING_INTERVAL;
	private boolean active          = true;
	private long   sampleCount;

    private long lastSampleTime;
    private InterfaceSample lastSample;
    private InterfaceSample prevLastSample;
    private boolean sampling;

    //  measurement values
	private double inKbps            = 0.0;
	private double outKbps           = 0.0;
    private double totalKbps         = 0.0;

	private double inUtil           = 0.0;
	private double outUtil          = 0.0;
    private double totalUtil        = 0.0;

	public Interface() { }

    /**
     * constructor
     * 
     * @param ifDescr   interface description
     * @param descr     description
     */
 
    public Interface(String ifDescr, String descr) {
		setDescr(descr);
		setIfDescr(ifDescr);
    }

	public Interface(Node linkNode) {
        parseXml(linkNode);
	}

    /**
     * get interface index
     *
     * @return interface index
     */
	public int getIfIndex() {
		return ifIndex;
	}

    /**
     * set interface index
     *
     * @param ifIndex interface index
     */
	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

    /**
     * get interface description
     *
     * @return interface description
     */
	public String getIfDescr() {
		return ifDescr;
	}

    /**
     * get interface description core
     *
     * @return interface description core
     */
	public String getIfDescrCore() {
		int index = ifDescr.indexOf("#");
		if(index != -1) {
			return ifDescr.substring(0, index);
		}
		else {
			return ifDescr;
		}
	}

    /**
     * set interface description
     *
     * @param ifDescr interface description
     */
	public void setIfDescr(String ifDescr) {
		if(ifDescr != null) {
			this.ifDescr = ifDescr;
		}
	}

    /**
     * get interface alias
     *
     * @return interface alias
     */
	public String getIfAlias() {
		return ifAlias;
	}

    /**
     * set interface alias
     *
     * @param ifAlias interface alias
     */
	public void setIfAlias(String ifAlias) {
		if(ifAlias != null) {
			this.ifAlias = ifAlias;
		}
	}

    /**
     * set sampling interval
     *
     * @return sampling interval
     */
	public int getSamplingInterval() {
		return samplingInterval;
	}

    /**
     * set sampling interval
     *
     * @param interval sampling interval
     */
	public void setSamplingInterval(int interval) {
		this.samplingInterval = interval;
	}

    /**
     * whether this interface is active or not
     *
     * @return true if this interface is true, otherwise, false
     */
	public boolean isActive() {
		return active;
	}

    /**
     * set activation flag
     *
     * @param active activation flag
     */
	public void setActive(boolean active) {
		this.active = active;
	}

    /**
     * get description
     *
     * @return description
     */
	public String getDescr() {
		return descr;
	}

    /**
     * set description
     *
     * @param descr description
     */
	public void setDescr(String descr) {
		if(descr != null) {
			this.descr = descr;
		}
	}

    /**
     * get last sample time
     *
     * @return last sample time
     */
	public long getLastSampleTime() {
		return lastSampleTime;
	}

    /**
     * set last sample time
     *
     * @param time last sample time
     */
	public void setLastSampleTime(long time) {
		this.lastSampleTime = time;
	}

    /**
     * whether sampling or not
     *
     * @return sampling
     */
    public boolean isSampling() {
        return sampling;
    }

    /**
     * set sampling
     *
     * @param sampling
     */
    public void setSampling(boolean sampling) {
        this.sampling = sampling;
    }

	public boolean isDue() {
		if(lastSampleTime == 0) {
			return true;
		}
		long elapsedTime = Util.getTime() - lastSampleTime;
		return elapsedTime >= samplingInterval;
	}

	public void switchToIfIndex(int ifIndex) {
        this.ifIndex = ifIndex;
	}

	public void deactivate() {
		this.active = false;
	}

	public synchronized void processSample(InterfaceSample sample) throws NGOMException {
		// check if ifDescr match
        if(!getIfDescrCore().equals(sample.getIfDescr())) {
			// something changed on the router
			switchToIfIndex(-1);
			return;
		}
        if (prevLastSample != null) {
            ////////////////////////////////////////////////////////////////////////
            //  LINK UTILIZATION CALCULATION
        	long time = sample.getTimestamp()-prevLastSample.getTimestamp();
            long in = ((long)Integer.MAX_VALUE + (long)(sample.getIfInOctets()-
                    prevLastSample.getIfInOctets()))%(long)Integer.MAX_VALUE;
        	long inBits = in * 8;
            long out = ((long)Integer.MAX_VALUE + (long)(sample.getIfOutOctets()-
                    prevLastSample.getIfOutOctets()))%(long)Integer.MAX_VALUE;
        	long outBits = out * 8;
            long total = in+out;
            long totalBits = inBits + outBits;
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("Interface", 
                        "sample : " + sample.toString());
                Logger.getInstance().logBackend("Interface", 
                        "prevSample : " + prevLastSample.toString());
            }

            if (time > 0 && inBits > 0) {
                inKbps = in/time/128;	//	in (bytes) * 8 (bits) / 시간(sec) / 1024 (Kbps로 전환)
            }
            if (time > 0 && outBits > 0) {
                outKbps = out/time/128;
            }
            if (time > 0 && totalBits > 0) {
                totalKbps = total/time/128;
            }
        	long speed = sample.getIfSpeed();
        	if (speed != 0) {
                if (time > 0 && inBits > 0) {
                    inUtil = (double)inBits/time/speed;
                }
                if (time > 0 && outBits > 0) {
                    outUtil = (double)outBits/time/speed;
                }
                if (time > 0 && totalBits > 0) {
                    totalUtil = (double)totalBits/time/speed;
                }
        	}
 
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("Interface", 
                    "InKbps: " + inKbps+", OutKbps: " + outKbps);
                Logger.getInstance().logBackend("Interface", 
                    "inUtil: " + inUtil+", OutUtil: " + outUtil);
            }
//            if (inUtil < 0 || outUtil < 0) {
//                try {
//                    FileWriter writer = new FileWriter(new File(Config.getErrorFile()), true);
//                    writer.write("sample     : " + sample.toString()+"\n");
//                    writer.write("prevSample : " + prevLastSample.toString()+"\n");
//                    writer.write("InBytes : " + inBytes+"\n");
//                    writer.write("OutBytes: " + outBytes+"\n");
//                    writer.write("INT_MAX : " + Integer.MAX_VALUE+"\n");
//                    writer.write("--------------------------------------------------------\n");
//                    writer.close();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
            ////////////////////////////////////////////////////////////////////////
        }
        prevLastSample = sample;
		sample.setIfDescr(ifDescr);
		if(lastSample != null && lastSample.getSysUpTime() >= sample.getSysUpTime() ) {
            // sysUpTime decreased
			sample.setValid(false);
		}
        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("Interface", 
                "Saving sample: " + sample);
        }
		sampleCount++;
        lastSample = sample;
		lastSampleTime = sample.getTimestamp();
	}

	public synchronized Hashtable getInterfaceInfo() {
		Hashtable link = new Hashtable();
		link.put(IFINDEX, new Integer(ifIndex));
		link.put(IFDESCR, ifDescr);
		link.put(IFALIAS, ifAlias);
		link.put(DESCR, descr);
		link.put(SAMPLINGINTERVAL, new Integer(samplingInterval));
		link.put(ACTIVE, new Boolean(active));
        link.put(SAMPLECOUNT, String.valueOf(sampleCount));
		if(lastSample != null) {
    		link.put(HOST, lastSample.getHost());
			link.put(LASTSAMPLETIME, String.valueOf(lastSampleTime));
			link.put(IFSPEED, String.valueOf(lastSample.getIfSpeed()));
			link.put(IFINOCTETS, String.valueOf(lastSample.getIfInOctets()));
			link.put(IFOUTOCTETS, String.valueOf(lastSample.getIfOutOctets()));
			link.put(IFINERRORS, String.valueOf(lastSample.getIfInErrors()));
			link.put(IFOUTERRORS, String.valueOf(lastSample.getIfOutErrors()));
			link.put(IFADMINSTATUS, lastSample.getIfAdminStatus());
			link.put(IFOPERSTATUS, lastSample.getIfOperStatus());
		}
        link.put(INKBPS, new Double(inKbps));
        link.put(OUTKBPS, new Double(outKbps));
        link.put(TOTALKBPS, new Double(totalKbps));
		link.put(INUTIL, new Double(inUtil));
		link.put(OUTUTIL, new Double(outUtil));
        link.put(TOTALUTIL, new Double(totalUtil));
		return link;
	}

	private void parseXml(Node linkNode) {
		NodeList nodes = linkNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? 
                                    firstChild.getNodeValue().trim(): null;
            if(name.equals(IFINDEX)) {
				setIfIndex(Integer.parseInt(value));
			}
			else if(name.equals(IFDESCR)) {
				setIfDescr(value);
			}
			else if(name.equals(IFALIAS)) {
				setIfAlias(value);
			}
			else if(name.equals(DESCR)) {
				setDescr(value);
			}
			else if(name.equals(SAMPLINGINTERVAL)) {
				setSamplingInterval(Integer.parseInt(value));
			}
			else if(name.equals(ACTIVE)) {
				setActive(new Boolean(value).booleanValue());
			}
		}
    }

	public void appendXml(Element deviceElem) {
        Document doc = deviceElem.getOwnerDocument();
		Element linkElem = doc.createElement(INTERFACE);
		deviceElem.appendChild(linkElem);
		Element ifIndexElem = doc.createElement(IFINDEX);
		ifIndexElem.appendChild(doc.createTextNode("" + ifIndex));
		linkElem.appendChild(ifIndexElem);
		Element ifDescrElem = doc.createElement(IFDESCR);
		ifDescrElem.appendChild(doc.createTextNode(ifDescr));
		linkElem.appendChild(ifDescrElem);
		Element ifAliasElem = doc.createElement(IFALIAS);
		ifAliasElem.appendChild(doc.createTextNode(ifAlias));
		linkElem.appendChild(ifAliasElem);
		Element descrElem = doc.createElement(DESCR);
		descrElem.appendChild(doc.createTextNode(descr));
		linkElem.appendChild(descrElem);
		Element samplElem = doc.createElement(SAMPLINGINTERVAL);
		samplElem.appendChild(doc.createTextNode("" + samplingInterval));
		linkElem.appendChild(samplElem);
		Element activeElem = doc.createElement(ACTIVE);
		activeElem.appendChild(doc.createTextNode("" + active));
		linkElem.appendChild(activeElem);
	}

	public String toString() {
		return ifDescr + " [" + ifIndex + "] " + descr + ", " + ifAlias + " (each " +
			samplingInterval + "sec, active=" + active + ")";
	}
}
