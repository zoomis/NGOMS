/*
 * @(#)InterfaceSample.java
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

import java.util.*;
/**
 * This class is for Interface Sample Data
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
public class InterfaceSample {
    public static final String TYPE[] = {
    					  "none(0)",
                          "other(1)",
                          "regular1822(2)",
                          "hdh1822(3)",
                          "ddn-x25(4)",
                          "rfc877-x25(5)",
                          "ethernet-csmacd(6)",
                          "iso88023-csmacd(7)",
                          "iso88024-tokenBus(8)",
                          "iso88025-tokenRing(9)",
                          "iso88026-man(10)",
                          "starLan(11)",
                          "proteon-10Mbit(12)",
                          "proteon-80Mbit(13)",
                          "hyperchannel(14)",
                          "fddi(15)",
                          "lapb(16)",
                          "sdlc(17)",
                          "ds1(18)",
                          "e1(19)",
                          "basicISDN(20)",
                          "primaryISDN(21)",
                          "propPointToPointSerial(22)",
                          "ppp(23)",
                          "softwareLoopback(24)",
                          "eon(25)",
                          "ethernet-3Mbit(26)",
                          "nsip(27)",
                          "slip(28)",
                          "ultra(29)",
                          "ds3(30)",
                          "sip(31)",
                          "frame-relay(32)",
                          "unknown-relay(33)"};
    
    public static final String STATUS[] = {
    					  "no(0)",
                          "up(1)",
                          "down(2)",
                          "testing(3)"};
    //  host information
	private String host ="";

    private int ifIndex;
	private String ifDescr = "";
    private int ifType;
    private int ifMtu;
    private long ifSpeed;
    private String ifPhyAddress = "";
    private int ifAdminStatus;
    private int ifOperStatus;
    private long ifLastChange;
    private long ifInOctets;
    private long ifInUcastPkts;
    private long ifInNUcastPkts;
    private long ifInDiscards;
    private long ifInErrors;
    private long ifInUnknownProtos;
    private long ifOutOctets;
    private long ifOutUcastPkts;
    private long ifOutNUcastPkts;
    private long ifOutDiscards;
    private long ifOutErrors;
    private long ifOutQLen;
    private int  ifSpecific;

    private long sysUpTime;

	private boolean valid = true;
	private long timestamp = Util.getTime();
    
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the ifIndex
	 */
	public int getIfIndex() {
		return ifIndex;
	}

	/**
	 * @param ifIndex the ifIndex to set
	 */
	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	/**
	 * @return the ifDescr
	 */
	public String getIfDescr() {
		return ifDescr;
	}

	/**
	 * @param ifDescr the ifDescr to set
	 */
	public void setIfDescr(String ifDescr) {
		this.ifDescr = ifDescr;
	}

	/**
	 * @return the ifType
	 */
	public int getIfType() {
		return ifType;
	}

	/**
	 * @param ifType the ifType to set
	 */
	public void setIfType(int ifType) {
		this.ifType = ifType;
	}

	/**
	 * @return the ifMtu
	 */
	public int getIfMtu() {
		return ifMtu;
	}

	/**
	 * @param ifMtu the ifMtu to set
	 */
	public void setIfMtu(int ifMtu) {
		this.ifMtu = ifMtu;
	}

	/**
	 * @return the ifSpeed
	 */
	public long getIfSpeed() {
		return ifSpeed;
	}

	/**
	 * @param ifSpeed the ifSpeed to set
	 */
	public void setIfSpeed(long ifSpeed) {
		this.ifSpeed = ifSpeed;
	}

	/**
	 * @return the ifPhyAddress
	 */
	public String getIfPhyAddress() {
		return ifPhyAddress.toUpperCase().replaceAll(" ", ":");
	}

	/**
	 * @param ifPhyAddress the ifPhyAddress to set
	 */
	public void setIfPhyAddress(String ifPhyAddress) {
		this.ifPhyAddress = ifPhyAddress;
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
	 * @return the ifLastChange
	 */
	public long getIfLastChange() {
		return ifLastChange;
	}

	/**
	 * @param ifLastChange the ifLastChange to set
	 */
	public void setIfLastChange(long ifLastChange) {
		this.ifLastChange = ifLastChange;
	}

	/**
	 * @return the ifInOctets
	 */
	public long getIfInOctets() {
		return ifInOctets;
	}

	/**
	 * @param ifInOctets the ifInOctets to set
	 */
	public void setIfInOctets(long ifInOctets) {
		this.ifInOctets = ifInOctets;
	}

	/**
	 * @return the ifInUcastPkts
	 */
	public long getIfInUcastPkts() {
		return ifInUcastPkts;
	}

	/**
	 * @param ifInUcastPkts the ifInUcastPkts to set
	 */
	public void setIfInUcastPkts(long ifInUcastPkts) {
		this.ifInUcastPkts = ifInUcastPkts;
	}

	/**
	 * @return the ifInNUcastPkts
	 */
	public long getIfInNUcastPkts() {
		return ifInNUcastPkts;
	}

	/**
	 * @param ifInNUcastPkts the ifInNUcastPkts to set
	 */
	public void setIfInNUcastPkts(long ifInNUcastPkts) {
		this.ifInNUcastPkts = ifInNUcastPkts;
	}

	/**
	 * @return the ifInDiscards
	 */
	public long getIfInDiscards() {
		return ifInDiscards;
	}

	/**
	 * @param ifInDiscards the ifInDiscards to set
	 */
	public void setIfInDiscards(long ifInDiscards) {
		this.ifInDiscards = ifInDiscards;
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
	 * @return the ifInUnknownProtos
	 */
	public long getIfInUnknownProtos() {
		return ifInUnknownProtos;
	}

	/**
	 * @param ifInUnknownProtos the ifInUnknownProtos to set
	 */
	public void setIfInUnknownProtos(long ifInUnknownProtos) {
		this.ifInUnknownProtos = ifInUnknownProtos;
	}

	/**
	 * @return the ifOutOctets
	 */
	public long getIfOutOctets() {
		return ifOutOctets;
	}

	/**
	 * @param ifOutOctets the ifOutOctets to set
	 */
	public void setIfOutOctets(long ifOutOctets) {
		this.ifOutOctets = ifOutOctets;
	}

	/**
	 * @return the ifOutUcastPkts
	 */
	public long getIfOutUcastPkts() {
		return ifOutUcastPkts;
	}

	/**
	 * @param ifOutUcastPkts the ifOutUcastPkts to set
	 */
	public void setIfOutUcastPkts(long ifOutUcastPkts) {
		this.ifOutUcastPkts = ifOutUcastPkts;
	}

	/**
	 * @return the ifOutNUcastPkts
	 */
	public long getIfOutNUcastPkts() {
		return ifOutNUcastPkts;
	}

	/**
	 * @param ifOutNUcastPkts the ifOutNUcastPkts to set
	 */
	public void setIfOutNUcastPkts(long ifOutNUcastPkts) {
		this.ifOutNUcastPkts = ifOutNUcastPkts;
	}

	/**
	 * @return the ifOutDiscards
	 */
	public long getIfOutDiscards() {
		return ifOutDiscards;
	}

	/**
	 * @param ifOutDiscards the ifOutDiscards to set
	 */
	public void setIfOutDiscards(long ifOutDiscards) {
		this.ifOutDiscards = ifOutDiscards;
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

	/**
	 * @return the ifOutQLen
	 */
	public long getIfOutQLen() {
		return ifOutQLen;
	}

	/**
	 * @param ifOutQLen the ifOutQLen to set
	 */
	public void setIfOutQLen(long ifOutQLen) {
		this.ifOutQLen = ifOutQLen;
	}

	/**
	 * @return the ifSpecific
	 */
	public int getIfSpecific() {
		return ifSpecific;
	}

	/**
	 * @param ifSpecific the ifSpecific to set
	 */
	public void setIfSpecific(int ifSpecific) {
		this.ifSpecific = ifSpecific;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	public long getSysUpTime() {
		return sysUpTime;
	}

	public void setSysUpTime(long sysUpTime) {
		this.sysUpTime = sysUpTime;
	}

    public void setInfo(Vector info) {
    	if (info == null || info.size() == 0) {
    		return;
    	}
        Iterator i = info.iterator();
        
        setHost((String)i.next());
        setIfIndex(((Integer)i.next()).intValue());
        setIfDescr((String)i.next());
        setIfType(((Integer)i.next()).intValue());
        setIfMtu(((Integer)i.next()).intValue());
        String str = (String)i.next();
        setIfSpeed(Long.parseLong(str));
        setIfPhyAddress((String)i.next());
        setIfAdminStatus(((Integer)i.next()).intValue());
        setIfOperStatus(((Integer)i.next()).intValue());
        str = (String)i.next();
        setIfLastChange(Long.parseLong(str));
        str = (String)i.next();
        setIfInOctets(Long.parseLong(str));
        str = (String)i.next();
        setIfInUcastPkts(Long.parseLong(str));
        str = (String)i.next();
        setIfInNUcastPkts(Long.parseLong(str));
        str = (String)i.next();
        setIfInDiscards(Long.parseLong(str));
        str = (String)i.next();
        setIfInErrors(Long.parseLong(str));
        str = (String)i.next();
        setIfInUnknownProtos(Long.parseLong(str));
        str = (String)i.next();
        setIfOutOctets(Long.parseLong(str));
        str = (String)i.next();
        setIfOutUcastPkts(Long.parseLong(str));
        str = (String)i.next();
        setIfOutNUcastPkts(Long.parseLong(str));
        str = (String)i.next();
        setIfOutDiscards(Long.parseLong(str));
        str = (String)i.next();
        setIfOutErrors(Long.parseLong(str));
        str = (String)i.next();
        setIfOutQLen(Long.parseLong(str));
    }

    public Vector getInfo() {
        Vector info = new Vector();
        info.add(host);
        info.add(new Integer(ifIndex));
        info.add(ifDescr);
        info.add(new Integer(ifType));
        info.add(new Integer(ifMtu));
        info.add(String.valueOf(ifSpeed));
        info.add(ifPhyAddress);
        info.add(new Integer(ifAdminStatus));
        info.add(new Integer(ifOperStatus));
        info.add(String.valueOf(ifLastChange));
        info.add(String.valueOf(ifInOctets));
        info.add(String.valueOf(ifInUcastPkts));
        info.add(String.valueOf(ifInNUcastPkts));
        info.add(String.valueOf(ifInDiscards));
        info.add(String.valueOf(ifInErrors));
        info.add(String.valueOf(ifInUnknownProtos));
        info.add(String.valueOf(ifOutOctets));
        info.add(String.valueOf(ifOutUcastPkts));
        info.add(String.valueOf(ifOutNUcastPkts));
        info.add(String.valueOf(ifOutDiscards));
        info.add(String.valueOf(ifOutErrors));
        info.add(String.valueOf(ifOutQLen));
        return info;
    }

	public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(ifDescr+"@"+host+": timestamp="+timestamp); 
        sb.append(", valid=" + valid);
        sb.append(",ifIndex:"+getIfIndex());
        sb.append(",ifDescr: "+getIfDescr());
        sb.append(",ifInOctets:" + getIfInOctets());
        /*
        sb.append("ifType:\t "+TYPE[getIfType()-1]);
        sb.append("ifSpeed:\t " +getIfSpeed());
        sb.append("ifPhyAddress:\t"+getIfPhyAddress());
        sb.append("ifAdminStatus:\t"+STATUS[getIfAdminStatus()-1]);
        sb.append("ifOperStatus:\t" +STATUS[getIfOperStatus()-1]);
        sb.append("ifLastChange:\t" + Util.getElapsedTime(getIfLastChange()));
        sb.append("ifInUcastPkts:\t"+getIfInUcastPkts());
        sb.append("ifInNUcastPkts:\t"+getIfInNUcastPkts());
        sb.append("ifInDiscards:\t"+getIfInDiscards());
        sb.append("ifInErrors:\t"+getIfInErrors());
        sb.append("ifInUnknownProtos:\t"+getIfInUnknownProtos());
        sb.append("ifOutOctets:\t"+getIfOutOctets());
        sb.append("ifOutUcastPkts:\t"+getIfOutUcastPkts());
        sb.append("ifOutNUcastPkts:\t"+getIfOutNUcastPkts());
        sb.append("ifOutDiscards:\t"+getIfOutDiscards());
        sb.append("ifOutErrors:\t"+getIfOutErrors());
        sb.append("ifOutQLen:\t"+getIfOutQLen());
        sb.append("ifIfSpecific:\t"+getIfSpecific());
        */
        return sb.toString();
	}
	
	public String toHTMLComment() {
        StringBuffer sb = new StringBuffer();
        int index = getIfType()-1;
        index = index < 0 ? 0 : index;
        index = index > TYPE.length - 1 ? TYPE.length-1 : index;
        sb.append("Type: "+TYPE[index]+"<br>");
        sb.append("PhyAddress:"+getIfPhyAddress()+"<br>");
        sb.append("AdminStatus:"+
        		STATUS[getIfAdminStatus()-1 < 0 ? 0 : getIfAdminStatus()-1]+"<br>");
        sb.append("OperStatus:" +
        		STATUS[getIfOperStatus()-1 < 0 ? 0 : getIfOperStatus()-1]+"<br>");
        sb.append("LastChange:" + Util.getElapsedTime(getIfLastChange()));
        return sb.toString();
	}
	
	public String toFullString() {
        StringBuffer sb = new StringBuffer();
        sb.append(ifDescr+"@"+host+": timestamp="+timestamp); 
        sb.append(", valid=" + valid);
        sb.append("\nifIndex:"+getIfIndex());
        sb.append("\nifDescr: "+getIfDescr());
        int index = getIfType()-1;
        index = index < 0 ? 0 : index;
        index = index > TYPE.length - 1 ? TYPE.length-1 : index;
        sb.append("\nifType: "+TYPE[index]);
        sb.append("\nifSpeed: " +getIfSpeed());
        sb.append("\nifPhyAddress:"+getIfPhyAddress());
        sb.append("\nifAdminStatus:"+STATUS[getIfAdminStatus()-1 < 0 ? 0 : getIfAdminStatus()-1]);
        sb.append("\nifOperStatus:" +STATUS[getIfOperStatus()-1 < 0 ? 0 : getIfOperStatus()-1]);
        sb.append("\nifLastChange:" + Util.getElapsedTime(getIfLastChange()));
        sb.append("\nifInOctets: " + getIfInOctets());
        sb.append("\nifInUcastPkts: "+getIfInUcastPkts());
        sb.append("\nifInNUcastPkts: "+getIfInNUcastPkts());
        sb.append("\nifInDiscards: "+getIfInDiscards());
        sb.append("\nifInErrors: "+getIfInErrors());
        sb.append("\nifInUnknownProtos: "+getIfInUnknownProtos());
        sb.append("\nifOutOctets: "+getIfOutOctets());
        sb.append("\nifOutUcastPkts: "+getIfOutUcastPkts());
        sb.append("\nifOutNUcastPkts: "+getIfOutNUcastPkts());
        sb.append("\nifOutDiscards: "+getIfOutDiscards());
        sb.append("\nifOutErrors: "+getIfOutErrors());
        sb.append("\nifOutQLen: "+getIfOutQLen());
        return sb.toString();
	}}
