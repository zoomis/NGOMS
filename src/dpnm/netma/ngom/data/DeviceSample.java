/*
 * @(#)DeviceSample.java
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
package dpnm.netma.ngom.data;

import dpnm.netma.ngom.util.Util;

import java.util.Vector;
import java.util.Iterator;
/**
 * This class is for Device Sample Data
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
public class DeviceSample {
	private String host;

    private String sysDescr="";
    private String sysObjectID="";
    private long   sysUpTime=0;
    private String sysContact="";
    private String sysName="";
    private String sysLocation="";
    private int    sysServices=0;

    private int    cpuUtil = 0;
    private int    memoryUtil = 0;
	private long timestamp = Util.getTime();
	
    public void DeviceSample() {
    }

    /**
     * get the host address
     *
     * @return host address
     */
	public String getHost() {
		return host;
	}

    /**
     * set the host address
     *
     * @param host host address
     */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * get system description
	 * 
	 * @return system description
	 */
	public String getSysDescr() {
		return sysDescr;
	}

	/**
	 * set system description
	 * 
	 * @param sysDescr the sysDescr to set
	 */
	public void setSysDescr(String sysDescr) {
		this.sysDescr = sysDescr;
	}

	/**
	 * get system object ID
	 * 
	 * @return the sysObjectID
	 */
	public String getSysObjectID() {
		return sysObjectID;
	}

	/**
	 * set system object ID
	 * 
	 * @param sysObjectID the sysObjectID to set
	 */
	public void setSysObjectID(String sysObjectID) {
		this.sysObjectID = sysObjectID;
	}

	/**
	 * get system up time
	 * 
	 * @return the sysUpTime
	 */
	public long getSysUpTime() {
		return sysUpTime;
	}

	/**
	 * set system up time
	 * 
	 * @param sysUpTime the sysUpTime to set
	 */
	public void setSysUpTime(long sysUpTime) {
		this.sysUpTime = sysUpTime;
	}

	/**
	 * get system contact information
	 * 
	 * @return the sysContact
	 */
	public String getSysContact() {
		return sysContact;
	}

	/**
	 * set system contact information
	 * 
	 * @param sysContact the sysContact to set
	 */
	public void setSysContact(String sysContact) {
		this.sysContact = sysContact;
	}

	/**
	 * get system name
	 * 
	 * @return the sysName
	 */
	public String getSysName() {
		return sysName;
	}

	/**
	 * set system name
	 * 
	 * @param sysName the sysName to set
	 */
	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	/**
	 * get system location
	 * 
	 * @return the sysLocation
	 */
	public String getSysLocation() {
		return sysLocation;
	}

	/**
	 * set system location
	 * 
	 * @param sysLocation the sysLocation to set
	 */
	public void setSysLocation(String sysLocation) {
		this.sysLocation = sysLocation;
	}

	/**
	 * get system service
	 * 
	 * @return the sysServices
	 */
	public int getSysServices() {
		return sysServices;
	}

	/**
	 * set system service
	 * 
	 * @param sysServices the sysServices to set
	 */
	public void setSysServices(int sysServices) {
		this.sysServices = sysServices;
	}

    /**
	 * @return the cpuUtil
	 */
	public int getCpuUtil() {
		return cpuUtil;
	}

	/**
	 * @param cpuUtil the cpuUtil to set
	 */
	public void setCpuUtil(int cpuUtil) {
		this.cpuUtil = cpuUtil;
	}

	/**
	 * @return the memoryUtil
	 */
	public int getMemoryUtil() {
		return memoryUtil;
	}

	/**
	 * @param memoryUtil the memoryUtil to set
	 */
	public void setMemoryUtil(int memoryUtil) {
		this.memoryUtil = memoryUtil;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	public synchronized void setInfo(Vector info) {
    	if (info == null || info.size() == 0) {
    		return;
    	}
        Iterator i = info.iterator();
        setSysDescr((String)i.next());
        setSysObjectID((String)i.next());
        String str = (String)i.next();
        setSysUpTime(Long.parseLong(str));
        setSysContact((String)i.next());
        setSysName((String)i.next());
        setSysLocation((String)i.next());
        setSysServices(((Integer)i.next()).intValue());
        setCpuUtil(((Integer)i.next()).intValue());
        setMemoryUtil(((Integer)i.next()).intValue());
    }

    public synchronized Vector getInfo() {
        Vector info = new Vector();
        info.add(sysDescr);
        info.add(sysObjectID);
        info.add(String.valueOf(sysUpTime));
        info.add(sysContact);
        info.add(sysName);
        info.add(sysLocation);
        info.add(new Integer(sysServices));
        info.add(new Integer(cpuUtil));
        info.add(new Integer(memoryUtil));
        return info;
    }
    
    public String toHTMLComment() {
    	StringBuffer buffer = new StringBuffer();
//		buffer.append("<br>System Description:" + getSysDescr());
		buffer.append("<br>System ObjectID:" + getSysObjectID());
		buffer.append("<br>System SysUpTime: " + Util.getElapsedTime(getSysUpTime()));
		buffer.append("<br>System Contact:" + getSysContact());
		buffer.append("<br>System Name:" + getSysName());
		buffer.append("<br>System Location:" + getSysLocation());
		buffer.append("<br>System Services:" + 
                getServiceStr(getSysServices()));	
		buffer.append("<br>CPU Utilization:" + getCpuUtil());
		buffer.append("<br>Memory Utilization:" + getMemoryUtil());
		return buffer.toString().trim().replaceAll("[\n\r]", " ");
    }
    
    public String toString() {
    	StringBuffer buffer = new StringBuffer();
		buffer.append("System Description:\t" + getSysDescr());
		buffer.append("\nSystem ObjectID:\t" + getSysObjectID());
		buffer.append("\nSystem SysUpTime:\t " + Util.getElapsedTime(getSysUpTime()));
		buffer.append("\nSystem Contact:\t" + getSysContact());
		buffer.append("\nSystem Name:\t" + getSysName());
		buffer.append("\nSystem Location:\t" + getSysLocation());
		buffer.append("\nSystem Services:\t" + 
                getServiceStr(getSysServices()));	
		buffer.append("\nCPU Utilization:\t" + getCpuUtil());
		buffer.append("\nMemory Utilization:\t" + getMemoryUtil());
		return buffer.toString();
    }

    private String getServiceStr(int s) {
        StringBuffer sb = new StringBuffer();
        String str = Integer.toBinaryString(s);
        for (int i = 0 ; i < 7 - str.length(); i++) {
            sb.append("0");
        }
        sb.append(str);
        return sb.toString();
    }
}
