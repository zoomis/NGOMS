/*
 * @(#)Device.java
 * 
 * Created on 2005. 12. 14
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dpnm.netma.ngom.Conf;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
/**
 * This class is for Device
 *
 * @author Eric Kang
 * @since 2008/02/18
 * @version $Revision: 1.1 $
 */
public class Device {
    /*
     * constant information
     */
    /** device */
    public static final String DEVICE       = "device";
    /** host address */
    public static final String HOST         = "host";
    /** SNMP port*/
    public static final String PORT         = "port";
    /** community string */
    public static final String NETMASK      = "netmask";
    /** community string */
    public static final String COMMUNITY    = "community";
    /** type of device */
    public static final String TYPE         = "type";
    /** community string */
    public static final String DESCRIPTION  = "description";
    /** description */
    public static final String ACTIVE       = "active";
    /** interface */
    public static final String INTERFACE    = "interface";

    /*  DEVICE type */
    /** none */
    public static final byte TYPE_NONE            = 0x00;
    /** server type */
    public static final byte TYPE_SERVER          = 0x01;
    /** router type */
    public static final byte TYPE_ROUTER          = 0x02;
    /** firewall type */
    public static final byte TYPE_FIREWALL        = 0x03;
    /** switch type */
    public static final byte TYPE_SWITCH          = 0x04;
    /** printer type */
    public static final byte TYPE_PRINTER         = 0x05;
    /** desktop type */
    public static final byte TYPE_DESKTOP         = 0x06;
    /** UPS type */
    public static final byte TYPE_UPS             = 0x07;
    /** wireless type */
    public static final byte TYPE_WIRELESS        = 0x08;
    /** Domain controller */
    public static final byte TYPE_DOMAINCONTROLLER = 0x09;


    //////////////////////////////////////////////////////////////////////////////
    //  MEMBER VARIABLES
    //////////////////////////////////////////////////////////////////////////////
    /** Device host address */
	protected String host             = "";
    /** Device SNMP port */
	protected int port                = Conf.DEFAULT_SNMPPORT;
    /** Device netmask string */
	protected String netmask          = "";
    /** Device community string */
	protected String community        = "";
    /** Device description */
	protected String descr            = "";
    /** Device type */
    protected byte type               = TYPE_NONE;
    /** Device activation flag */
	protected boolean active          = true;
	protected Vector<Interface> interfaces = null;

    /** Device sample */
    protected DeviceSample sample = null;

	private int    samplingInterval = Conf.DEFAULT_SAMPLING_INTERVAL;

    private long lastSampleTime;
    /**
     * Constructor
     */
	public Device() { 
		interfaces = new Vector<Interface>();
    }

    public Device(String host, int port, String community, String netmask, String descr) {
    	this();
        setHost(host);
        setPort(port);
        setNetmask(netmask);
        setCommunity(community);
        setDescr(descr);

        //TODO: type이 후에 결정되어져야 한다.
    }

    /**
     * Constructor
     *
     * @param deviceNode device node information
     */
	public Device(Node deviceNode) {
		this();
        parseXml(deviceNode);
	}

	/**
	 * get host address
	 * 
	 * @return host address
	 */
	public String getHost() {
		return host;
	}

	/**
	 * set host address
	 * 
	 * @param host address
	 */
	public void setHost(String host) {
		if(host != null) {
			this.host = host;
		}
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the netmask
	 */
	public String getNetmask() {
		return netmask;
	}

	/**
	 * @param netmask the netmask to set
	 */
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	/**
	 * get community string
	 * 
	 * @return community string
	 */
	public String getCommunity() {
		return community;
	}

	/**
	 * set community string
	 * 
	 * @param community string
	 */
	public void setCommunity(String community) {
		if(community != null) {
			this.community = community;
		}
	}

	/**
	 * get device type
	 * 
	 * @return device type
	 */
	public byte getType() {
		return type;
	}

	
	/**
	 * set device type
	 * 
	 * @param sample device sample
	 */
	public void setType(DeviceSample sample) {
		int services = sample.getSysServices();
		
		if (services == 2 ||
				services == 3 ||
				services == 6 ||
				services == 78) {
			setType(TYPE_SWITCH);
		}
	}

	/**
	 * set device type
	 * 
	 * @param type device type
	 */
	public void setType(byte type) {
        this.type = type;
	}

	/**
	 * get device description
	 * 
	 * @return device description
	 */
	public String getDescr() {
		return descr;
	}

	/**
	 * set device description
	 * 
	 * @param descr device description
	 */
	public void setDescr(String descr) {
		if(descr != null) {
			this.descr = descr;
		}
	}

	/**
	 * return whether this device is active or not
	 * 
	 * @return true if active, otherwise, false
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * set active flag
	 * 
	 * @param active activation flag
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * get interface list
	 * 
	 * @return interface list
	 */
	public Vector<Interface> getInterfaces() {
		return interfaces;
	}

	/**
	 * set interface list
	 * 
	 * @param interfaces interface list
	 */
	public void setInterfaces(Vector<Interface> interfaces) {
		this.interfaces = interfaces;
	}

	/**
	 * add interface to this device
	 * 
	 * @param i interface
	 */
	public void addInterface(Interface i) {
		interfaces.add(i);
	}

	/**
	 * remove interface from this device
	 * 
	 * @param i interface
	 */
	public void removeInterface(Interface i) {
		interfaces.remove(i);
	}

	/**
	 * get the number of interfaces of this device
	 * 
	 * @return interface count
	 */
	public int getInterfaceCount() {
		return interfaces.size();
	}

    /**
     * get interface by interface description
     *
     * @param ifDescr interface description
     * @return interface
     */
	public Interface getInterfaceByIfDescr(String ifDescr) {
		for(int i = 0; i < interfaces.size(); i++) {
			Interface in = interfaces.get(i);
            if(in.getIfDescr().equalsIgnoreCase(ifDescr)) {
				return in;
			}
		}
		return null;
	}

	public boolean isDue() {
		if(lastSampleTime == 0) {
			return true;
		}
		long elapsedTime = Util.getTime() - lastSampleTime;
		return elapsedTime >= samplingInterval;
	}
	
	public synchronized void processSample(DeviceSample sample) {
		lastSampleTime = sample.getTimestamp();
	}
    /**
     * get device information
     *
     * @return device information
     */
	public Hashtable getDeviceInfo() {
		Hashtable table = new Hashtable();
		table.put(HOST, host);
		table.put(PORT, new Integer(port));
		table.put(NETMASK, netmask);
		table.put(COMMUNITY, community);
		table.put(DESCRIPTION, descr);
        table.put(TYPE, new Integer(type));
		table.put(ACTIVE, new Boolean(active));

		// add interface info
		Vector<Hashtable> interfaceData = new Vector<Hashtable>();
		for (int i = 0; interfaces != null && i < interfaces.size(); i++) {
			Interface in = interfaces.get(i);
			interfaceData.add(in.getInterfaceInfo());
		}
		table.put(INTERFACE, interfaceData);
		return table;
	}

	/**
	 * return information of this device
	 * 
	 * @return device information
	 */	
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Device ("+host+":"+port+" -- ");
        sb.append(TYPE+"="+getTypeStr(type));
        sb.append(", "+COMMUNITY+"="+community);
        sb.append(", "+DESCRIPTION+"="+descr);
        sb.append(", "+ACTIVE+"="+active+")\n");

		// dump interfaces
		for(int i = 0; i < interfaces.size(); i++) {
            Interface in = interfaces.get(i);
			sb.append("  Interface: " + in.toString() + "\n");
		}
		return sb.toString();
	}


    /**
     * parse XML node
     *
     * @param node xml node
     */
    private void parseXml(Node rootNode) {
    	if (rootNode == null) {
    		return;
    	}
		NodeList nodes = rootNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null) ? 
                        firstChild.getNodeValue().trim(): 
                        null;
            if(name.equals(HOST)) {
				setHost(value);
			} else if(name.equals(PORT)) {
				setPort(new Integer(value));
			} else if(name.equals(NETMASK)) {
				setNetmask(value);
			} else if(name.equals(COMMUNITY)) {
				setCommunity(value);
			} else if(name.equals(DESCRIPTION)) {
				setDescr(value);
			} else if(name.equals(TYPE)) {
				setType(Byte.parseByte(value));
			} else if(name.equals(ACTIVE)) {
				setActive(
                    new Boolean(
                        node.getFirstChild().getNodeValue().trim()).booleanValue());
			} else if(name.equals(INTERFACE)) {
				interfaces.add(new Interface(node));
			}
		}

    }
    /**
     * parse XML node
     *
     * @param root root xml node
     */
	public void appendXml(Element root) {
        Document doc = root.getOwnerDocument();
		Element deviceElem = doc.createElement(DEVICE);
		root.appendChild(deviceElem);

		Element hostElem = doc.createElement(HOST);
		hostElem.appendChild(doc.createTextNode(host));
		deviceElem.appendChild(hostElem);

		Element portElem = doc.createElement(PORT);
		portElem.appendChild(doc.createTextNode(String.valueOf(port)));
		deviceElem.appendChild(portElem);

		Element netmaskElem = doc.createElement(NETMASK);
		netmaskElem.appendChild(doc.createTextNode(String.valueOf(netmask)));
		deviceElem.appendChild(netmaskElem);
		
		Element commElem = doc.createElement(COMMUNITY);
		commElem.appendChild(doc.createTextNode(community));
		deviceElem.appendChild(commElem);

		Element typeElem = doc.createElement(TYPE);
		typeElem.appendChild(doc.createTextNode(""+type));
		deviceElem.appendChild(typeElem);
        
		Element descrElem = doc.createElement(DESCRIPTION);
		descrElem.appendChild(doc.createTextNode(descr));
		deviceElem.appendChild(descrElem);

		Element activeElem = doc.createElement(ACTIVE);
		activeElem.appendChild(doc.createTextNode("" + active));
		deviceElem.appendChild(activeElem);

		if (interfaces != null) {
    		for (int i = 0; i < interfaces.size(); i++) {
    			Interface in = interfaces.elementAt(i);
    			in.appendXml(deviceElem);
    		}
		}
	}

    /**
     * get type string
     *
     * @return type string
     */
    public static String getTypeStr(byte type) {
        switch(type) {
            case TYPE_NONE:              return "NONE";
            case TYPE_SERVER:            return "Server";
            case TYPE_ROUTER:            return "Router";
            case TYPE_FIREWALL:          return "Firewall";
            case TYPE_SWITCH:            return "Switch";
            case TYPE_PRINTER:           return "Printer";
            case TYPE_DESKTOP:           return "Desktop";
            case TYPE_UPS:               return "UPS";
            case TYPE_WIRELESS:          return "Wireless";
            case TYPE_DOMAINCONTROLLER:  return "DomainController";
        }
        return "";
    }
}
