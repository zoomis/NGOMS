package dpnm.netma.ngom.data;

import dpnm.netma.ngom.Conf;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Collections;

import dpnm.netma.ngom.frontend.DataManager;

public class DeviceInfo implements TreeElementInfo {
    /** Device host address */
	private String host             = "";
    /** Device netmask string */
	private int port                = Conf.DEFAULT_SNMPPORT;
    /** Device netmask string */
	private String netmask          = "";
    /** Device community string */
	private String community        = "";
    /** Device description */
	private String descr            = "";
    /** Device type */
    private byte type               = Device.TYPE_NONE;
    /** Device activation flag */
	protected boolean active        = true;
 
	private InterfaceInfo[]         interfaceInfo;

    public DeviceInfo() {
    }

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
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
	 * @param netmask the netmask to set
	 */
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	/**
	 * @param community the community to set
	 */
	public void setCommunity(String community) {
		this.community = community;
	}

	/**
	 * @param descr the descr to set
	 */
	public void setDescr(String descr) {
		this.descr = descr;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(byte type) {
		this.type = type;
	}

	/**
	 * @param interfaceInfo the interfaceInfo to set
	 */
	public void setInterfaceInfo(InterfaceInfo[] interfaceInfo) {
		this.interfaceInfo = interfaceInfo;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the netmask
	 */
	public String getNetmask() {
		return netmask;
	}

	/**
	 * @return the community
	 */
	public String getCommunity() {
		return community;
	}

	/**
	 * @return the descr
	 */
	public String getDescr() {
		return descr;
	}

	/**
	 * @return the type
	 */
	public byte getType() {
		return type;
	}

	/**
	 * @return the interfaceInfo
	 */
	public InterfaceInfo[] getInterfaceInfo() {
		return interfaceInfo;
	}
	
	public InterfaceInfo[] getInterfaceInfoSortedByIfIndex() {
		Vector<InterfaceInfo> linkInfo = new Vector<InterfaceInfo>();
		for(int linkIndex = 0; linkIndex < interfaceInfo.length; linkIndex++) {
			linkInfo.add(interfaceInfo[linkIndex]);
		}
		Collections.sort(linkInfo, InterfaceInfo.getComparator());
		return (InterfaceInfo[]) linkInfo.toArray(new InterfaceInfo[0]);
	}

	public String toString() {
		return host + "(" + descr +")";
	}

    public String getComment() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Device address: " + getHost() + "\n");
        buffer.append("port: " + getPort() + "\n");
        buffer.append("Community: " + getCommunity() + "\n");
        buffer.append("Description: " + getDescr());
        return buffer.toString();
    }
    
    public String getHTMLComment() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Device address: " + getHost() + "<br>");
        buffer.append("port: " + getPort() + "<br>");
        buffer.append("Description: " + getDescr()+"<br>");
		buffer.append("Sampled links: " + getInterfaceInfo().length+"<br>");
		buffer.append("Type: " + Device.getTypeStr(getType()));
        return buffer.toString();
    }

    public String getHTMLCommentReal() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Device address: " + getHost() + "<br>");
        buffer.append("Description: " + getDescr()+"<br>");
		buffer.append("Sampled links: " + getInterfaceInfo().length+"<br>");
        if (getHost() != null) {
    		try {
        		DeviceSample sample = DataManager.getInstance().getDeviceSample(getHost());
        		buffer.append(sample.toHTMLComment());
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
        }
        return buffer.toString();
    }
    
	public String getInfo() {
		StringBuffer buffer = new StringBuffer();
    	buffer.append("Device address: " + getHost() + "\n");
        buffer.append("port: " + getPort() + "\n");
		buffer.append("Community: " + getCommunity() + "\n");
		buffer.append("Description: " + getDescr() + "\n");
		buffer.append("Type: " + Device.getTypeStr(getType())+"\n");
		buffer.append("Active: " + isActive() + "\n");
		buffer.append("Sampled links: " + getInterfaceInfo().length+"\n\n");
		
		try {
    		DeviceSample sample = DataManager.getInstance().getDeviceSample(getHost());
    		buffer.append(sample.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	public String getInfoTest() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Device address: " + getHost() + "\n");
        buffer.append("port: " + getPort() + "\n");
		buffer.append("Community: " + getCommunity() + "\n");
		buffer.append("Description: " + getDescr() + "\n");
		buffer.append("Active: " + isActive() + "\n");
		buffer.append("Sampled links: " + getInterfaceInfo().length);
		return buffer.toString();
	}

	public static Comparator getComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
                DeviceInfo router1 = (DeviceInfo) o1;
				DeviceInfo router2 = (DeviceInfo) o2;
                return router1.getHost().compareToIgnoreCase(router2.getHost());
			}
		};
	}

	public boolean equals(Object obj) {
		if(obj instanceof DeviceInfo) {
			Comparator c = getComparator();
			return c.compare(this, obj) == 0;
		}
		return false;
	}

	public String[] getInterfaces() {
		String[] interfaces = new String[interfaceInfo.length];
		for(int i = 0; i < interfaceInfo.length; i++) {
			interfaces[i] = interfaceInfo[i].getIfDescr();
		}
		return interfaces;
	}
	
    public String[] getAlias() {
        String[] alias = new String[interfaceInfo.length];
        for(int i = 0; i < interfaceInfo.length; i++) {
            alias[i] = interfaceInfo[i].getIfAlias();
        }
        return alias;
    }
}
