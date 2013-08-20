package dpnm.netma.ngom.data;

import dpnm.netma.ngom.frontend.DataManager;
import dpnm.netma.ngom.util.Util;

import java.util.Comparator;
import java.util.Date;

public class InterfaceInfo implements TreeElementInfo {
	private String host ="";
	private int ifIndex = 0;
	private String ifDescr ="";
	private String ifAlias = "";
	private String descr = "";
	private int samplingInterval = 0;
	private boolean active = true;
	private long sampleCount = 0;
	private Date lastSampleTime = new Date();
    private long speed = 0;
	private long ifInOctets = 0;
	private long ifOutOctets = 0;
	private long ifInErrors = 0;
	private long ifOutErrors = 0;
	private int  ifAdminStatus = 0;
	private int  ifOperStatus = 0;
	private double inKbps = 0.0;
	private double outKbps = 0.0;
    private double totalKbps = 0.0;
	private double inUtil = 0.0;
	private double outUtil = 0.0;
    private double totalUtil = 0.0;
    //private double peakInUtil = 0;
    //private double peakOutUtil = 0;

    public String getHost() {
    	return host;
    }
    
    public void setHost(String host) {
    	this.host = host;
    }
    
	public double getInUtil() {
		return inUtil;
	}

	/**
     * @return Returns the totalUtil.
     */
    public double getTotalUtil() {
        return totalUtil;
    }

    /**
     * @param totalUtil The totalUtil to set.
     */
    public void setTotalUtil(double totalUtil) {
        this.totalUtil = totalUtil;
    }

    public void setInUtil(double inUtil) {
		this.inUtil = inUtil;
		//if (inUtil > peakInUtil) peakInUtil = inUtil;
	}

	public double getOutUtil() {
		return outUtil;
	}

	public void setOutUtil(double outUtil) {
		this.outUtil = outUtil;
		//if (outUtil > peakOutUtil) peakOutUtil = outUtil;
	}

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public long getSpeed() {
        return this.speed;
    }

	public String getIfDescr() {
		return ifDescr;
	}

	public void setIfDescr(String ifDescr) {
		this.ifDescr = ifDescr;
	}

	public String getIfAlias() {
		return ifAlias;
	}

	public void setIfAlias(String ifAlias) {
		this.ifAlias = ifAlias;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public int getSamplingInterval() {
		return samplingInterval;
	}

	public void setSamplingInterval(int samplingInterval) {
		this.samplingInterval = samplingInterval;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getLastSampleTime() {
		return lastSampleTime;
	}

	public void setLastSampleTime(Date lastSampleTime) {
		this.lastSampleTime = lastSampleTime;
	}

	public long getIfInOctets() {
		return ifInOctets;
	}

	public void setIfInOctets(long ifInOctets) {
		this.ifInOctets = ifInOctets;
	}

	public long getIfOutOctets() {
		return ifOutOctets;
	}

	public void setIfOutOctets(long ifOutOctets) {
		this.ifOutOctets = ifOutOctets;
	}

	public long getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(long sampleCount) {
		this.sampleCount = sampleCount;
	}

	public String toString() {
		return ifDescr + " [" + ifIndex + "] - " + ifAlias;
	}


    public String getComment() {
        StringBuffer b = new StringBuffer();
        StringBuffer c = new StringBuffer();
        b.append("Interface description: " + getIfDescr() + "\n");
        //b.append("NWS description: " + getDescr() + "\n");
        //b.append("Router alias: " + getIfAlias() + "\n");
        b.append("Interface index: " + getIfIndex() + "\n");
        b.append("Last sample time: " + getLastSampleTime() + "\n");
        //b.append("Last sample input octets:  " + getIfInOctets() + "\n");
        //b.append("Last sample output octets: " + getIfOutOctets() + "\n\n");
        b.append("Interface Speed: " + getSpeed()/1000/1000 + " Mbps\n");
        b.append("Average In-data rate: " + Util.getDoubleString(getInKbps()) +" Kbps ("+
        		    Util.getDoubleString((double)getInUtil() * 100) +" %)\n");
        b.append("Average Out-data rate: " + Util.getDoubleString(getOutKbps()) +" Kbps ("+
        		    Util.getDoubleString((double)getOutUtil() * 100) +" %)\n");
        //b.append("Peak In-data rate: " + Util.getDoubleString(peakInUtil*getSpeed()/1024/1024) +" Mbps ("+
    	//	    Util.getDoubleString(peakInUtil * 100) +" %)\n");
        //b.append("Peak Out-data rate: " + Util.getDoubleString(peakOutUtil*getSpeed()/1024/1024) +" Mbps ("+
    	//	    Util.getDoubleString(peakOutUtil * 100) +" %)");
        //b.append("In utilization: " + getInUtil()*100+"%\n");
        //b.append("Out utilization: " + getOutUtil()*100+"%\n");
        //b.append("Total utilization: " + getTotalUtil()*100+"%");
        return b.toString();
    }
    
     public String getHTMLComment() {
        StringBuffer b = new StringBuffer();
        b.append(getIfDescr() + "@"+getHost()+"<br>");
        b.append("Sample time: "+getLastSampleTime() + "<br>");
        b.append("Speed: " + getSpeed()/1000/1000 + " Mbps<br>");
        b.append("Avg In: " + Util.getDoubleString(getInKbps()) +" Kbps ("+
        		    Util.getDoubleString((double)getInUtil() * 100) +" %)<br>");
        b.append("Avg Out: " + Util.getDoubleString(getOutKbps()) +" Kbps ("+
        		    Util.getDoubleString((double)getOutUtil() * 100) +" %)<br>");

        b.append("Error Pkts: " + (getIfInErrors()+getIfOutErrors())+ "<br>");
        //b.append("Peak In-data rate: " + Util.getDoubleString(peakInUtil*getSpeed()/1024/1024) +" Mbps ("+
    	//	    Util.getDoubleString(peakInUtil * 100) +" %)<br>");
        //b.append("Peak Out-data rate: " + Util.getDoubleString(peakOutUtil*getSpeed()/1024/1024) +" Mbps ("+
    	//	    Util.getDoubleString(peakOutUtil * 100) +" %)");
        //b.append("Last sample input octets:  " + getIfInOctets() + "<br>");
        //b.append("Last sample output octets: " + getIfOutOctets() + "<br><br>");
        //b.append("Incoming utilization: <b>" + getDoubleString(getInUtil()*100)+"%</b><br>");
        //b.append("Outgoing utilization: <b>" + getDoubleString(getOutUtil()*100)+"%</b><br>");
        //b.append("Total utilization: <b>" + getDoubleString(getTotalUtil()*100)+"%</b>");
//        b.append("<br><img src=images/"+name+".png>");
		return b.toString().trim().replaceAll("[\n\r]", " ");
    }
    
    public String getHTMLCommentReal() {
        StringBuffer b = new StringBuffer();
        b.append(getIfDescr() + "@"+getHost()+"<br>");
        b.append("Sample time: "+getLastSampleTime() + "<br>");
        b.append("Speed: " + getSpeed()/1000/1000 + " Mbps<br>");
        b.append("Avg In: " + Util.getDoubleString(getInKbps()) +" Kbps ("+
        		    Util.getDoubleString((double)getInUtil() * 100) +" %)<br>");
        b.append("Avg Out: " + Util.getDoubleString(getOutKbps()) +" Kbps ("+
        		    Util.getDoubleString((double)getOutUtil() * 100) +" %)<br>");
        if (getHost() != null) {
      		try {
        		InterfaceSample sample = DataManager.getInstance().getInterfaceSample(
        				getHost(), getIfDescr());
        		b.append(sample.toHTMLComment());
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
        }
	
        //b.append("Peak In-data rate: " + Util.getDoubleString(peakInUtil*getSpeed()/1024/1024) +" Mbps ("+
    	//	    Util.getDoubleString(peakInUtil * 100) +" %)<br>");
        //b.append("Peak Out-data rate: " + Util.getDoubleString(peakOutUtil*getSpeed()/1024/1024) +" Mbps ("+
    	//	    Util.getDoubleString(peakOutUtil * 100) +" %)");
        //b.append("Last sample input octets:  " + getIfInOctets() + "<br>");
        //b.append("Last sample output octets: " + getIfOutOctets() + "<br><br>");
        //b.append("Incoming utilization: <b>" + getDoubleString(getInUtil()*100)+"%</b><br>");
        //b.append("Outgoing utilization: <b>" + getDoubleString(getOutUtil()*100)+"%</b><br>");
        //b.append("Total utilization: <b>" + getDoubleString(getTotalUtil()*100)+"%</b>");
//        b.append("<br><img src=images/"+name+".png>");
		return b.toString().trim().replaceAll("[\n\r]", " ");
    }
 
    public String getHTMLComment2(String name) {
        StringBuffer b = new StringBuffer();
        b.append(getIfDescr() + "@"+getHost()+"<br>");
        b.append("Last sample time: " + getLastSampleTime() + "<br>");
        b.append("Interface Speed: " + getSpeed()/1000/1000 + " Mbps<br>");
        b.append("Average In-data rate: " + Util.getDoubleString(getInKbps()) +" Kbps ("+
        		    Util.getDoubleString((double)getInUtil() * 100) +" %)\n");
        b.append("Average Out-data rate: " + Util.getDoubleString(getOutKbps()) +" Kbps ("+
        		    Util.getDoubleString((double)getOutUtil() * 100) +" %)\n");

        //b.append("Peak In-data rate: " + Util.getDoubleString(peakInUtil*getSpeed()/1024/1024) +" Mbps ("+
    	//	    Util.getDoubleString(peakInUtil * 100) +" %)<br>");
        //b.append("Peak Out-data rate: " + Util.getDoubleString(peakOutUtil*getSpeed()/1024/1024) +" Mbps ("+
    	//	    Util.getDoubleString(peakOutUtil * 100) +" %)");
        //b.append("Last sample input octets:  " + getIfInOctets() + "<br>");
        //b.append("Last sample output octets: " + getIfOutOctets() + "<br><br>");
        //b.append("Incoming utilization: <b>" + getDoubleString(getInUtil()*100)+"%</b><br>");
        //b.append("Outgoing utilization: <b>" + getDoubleString(getOutUtil()*100)+"%</b><br>");
        //b.append("Total utilization: <b>" + getDoubleString(getTotalUtil()*100)+"%</b>");
//        b.append("<br><img src=images/"+name+".png>");
        return b.toString();
    }
    
    private String getDoubleString(double value) {
        StringBuffer sb = new StringBuffer();
        int v = (int)value;
        sb.append(v);
        sb.append(".");
        value = value - v;
        v = (int)((value+0.005) * 100);
        sb.append(v);
        return sb.toString();
    }
    
	public String getInfo() {
		StringBuffer b = new StringBuffer();
		b.append("Interface description: " + getIfDescr() + "\n");
		b.append("description: " + getDescr() + "\n");
		b.append("Router alias: " + getIfAlias() + "\n");
		b.append("Active: " + isActive() + "\n\n");
		b.append("Sampling interval: " + getSamplingInterval() + " sec\n");
		b.append("Last sample time: " + getLastSampleTime() + "\n");
		b.append("Samples collected: " + getSampleCount() + "\n\n");
        b.append("In Bytes: " + Util.getDoubleString(getInKbps())+" Kbps\n");
        b.append("Out Bytes: " + Util.getDoubleString(getOutKbps())+" Kbps\n");
        b.append("In utilization: " + Util.getDoubleString(getInUtil()*100)+"%\n");
        b.append("Out utilization: " + Util.getDoubleString(getOutUtil()*100)+"%\n");
//        b.append("Total utilization: " + getTotalUtil()*100+"%");
		/*
		b.append("Interface index: " + getIfIndex() + "\n");
		b.append("Last sample input octets:  " + getIfInOctets() + "\n");
		b.append("Last sample output octets: " + getIfOutOctets() + "\n");
		*/
		try {
    		InterfaceSample sample = DataManager.getInstance().getInterfaceSample(
    				getHost(), getIfDescr());
    		b.append(sample.toFullString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b.toString();
	}

	public static Comparator getComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
                InterfaceInfo link1 = (InterfaceInfo) o1;
				InterfaceInfo link2 = (InterfaceInfo) o2;
//                return link1.getIfDescr().compareToIgnoreCase(link2.getIfDescr());
                return link1.getIfIndex() - link2.getIfIndex();
			}
		};
	}

	public boolean equals(Object obj) {
		if(obj instanceof InterfaceInfo) {
			Comparator c = getComparator();
			return c.compare(this, obj) == 0;
		}
		return false;
	}

	/**
	 * @return the ifAdminStatus
	 */
	public int getIfAdminStatus() {
		return ifAdminStatus;
	}

	/**
	 * @param ifAdminStatus the ifAdminStatus to set
	 */
	public void setIfAdminStatus(int ifAdminStatus) {
		this.ifAdminStatus = ifAdminStatus;
	}

	/**
	 * @return the ifOperStatus
	 */
	public int getIfOperStatus() {
		return ifOperStatus;
	}

	/**
	 * @param ifOperStatus the ifOperStatus to set
	 */
	public void setIfOperStatus(int ifOperStatus) {
		this.ifOperStatus = ifOperStatus;
	}

	/**
	 * @return the inKbps
	 */
	public double getInKbps() {
		return inKbps;
	}

	/**
	 * @param inKbps the inKbps to set
	 */
	public void setInKbps(double inKbps) {
		this.inKbps = inKbps;
	}

	/**
	 * @return the outKbps
	 */
	public double getOutKbps() {
		return outKbps;
	}

	/**
	 * @param outKbps the outKbps to set
	 */
	public void setOutKbps(double outKbps) {
		this.outKbps = outKbps;
	}

	/**
	 * @return the totalKbps
	 */
	public double getTotalKbps() {
		return totalKbps;
	}

	/**
	 * @param totalKbps the totalKbps to set
	 */
	public void setTotalKbps(double totalKbps) {
		this.totalKbps = totalKbps;
	}

	/**
	 * @return the ifInErrors
	 */
	public long getIfInErrors() {
		return ifInErrors;
	}

	/**
	 * @param ifInErrors the ifInErrors to set
	 */
	public void setIfInErrors(long ifInErrors) {
		this.ifInErrors = ifInErrors;
	}

	/**
	 * @return the ifOutErrors
	 */
	public long getIfOutErrors() {
		return ifOutErrors;
	}

	/**
	 * @param ifOutErrors the ifOutErrors to set
	 */
	public void setIfOutErrors(long ifOutErrors) {
		this.ifOutErrors = ifOutErrors;
	}
	
}
