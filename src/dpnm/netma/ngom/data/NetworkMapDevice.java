/*
 * @(#)NetworkMapDevice.java
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.Hashtable;
/**
 * This class is for Map Device
 *
 * @author Eric Kang
 * @since 2005/02/19
 * @version $Revision: 1.1 $
 */
public class NetworkMapDevice {
    /** device */
    public static final String DEVICE     = "device";
    public static final String HOST       = "host";
    public static final String XPOS       = "xpos";
    public static final String YPOS       = "ypos";
    public static final String ICON       = "icon";
 
    //  private member variable
	private String host = "";
    private int xpos = 0;
    private int ypos = 0;
    private int icon = 0;

	public NetworkMapDevice() { 
    }

    /**
     * Constructor
     * 
     * @param host      host address
     * @param xpos      x position
     * @param ypos      y position
     * @param icon      icon
     */
    public NetworkMapDevice(String host, int xpos, int ypos, int icon) {
		setHost(host);
        setXpos(xpos);
        setYpos(ypos);
        setIcon(icon);
    }

	public NetworkMapDevice(Node deviceNode) {
		NodeList nodes = deviceNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
            if(name.intern() == HOST.intern()) {
				setHost(value);
			}
			else if(name.intern() == XPOS.intern()) {
				setXpos(Integer.parseInt(value));
			}
			else if(name.intern() == YPOS.intern()) {
				setYpos(Integer.parseInt(value));
			}
			else if(name.intern() == ICON.intern()) {
				setIcon(Integer.parseInt(value));
			}
		}
	}

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
	 * @return the xpos
	 */
	public int getXpos() {
		return xpos;
	}

	/**
	 * @param xpos the xpos to set
	 */
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	/**
	 * @return the ypos
	 */
	public int getYpos() {
		return ypos;
	}

	/**
	 * @param ypos the ypos to set
	 */
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	/**
	 * @return the icon
	 */
	public int getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String toString() {
		String buff = new String();
		buff += "Router: " + host;
		buff += " (" + xpos + ", " + ypos + ") - " + icon;
		return buff;
	}

	public Hashtable getDeviceInfo() {
		Hashtable table = new Hashtable();
		table.put(HOST, host);
		table.put(XPOS, new Integer(xpos));
		table.put(YPOS, new Integer(ypos));
		table.put(ICON, new Integer(icon));
		return table;
	}

	void appendXml(Element mapElem) {
        Document doc = mapElem.getOwnerDocument();
		Element deviceElem = doc.createElement(DEVICE);
		mapElem.appendChild(deviceElem);
		Element hostElem = doc.createElement(HOST);
		hostElem.appendChild(doc.createTextNode(host));
		deviceElem.appendChild(hostElem);
		Element xposElem = doc.createElement(XPOS);
		xposElem.appendChild(doc.createTextNode(String.valueOf(xpos)));
		deviceElem.appendChild(xposElem);
		Element yposElem = doc.createElement(YPOS);
		yposElem.appendChild(doc.createTextNode(String.valueOf(ypos)));
		deviceElem.appendChild(yposElem);
		Element iconElem = doc.createElement(ICON);
		iconElem.appendChild(doc.createTextNode(String.valueOf(icon)));
		deviceElem.appendChild(iconElem);
	}
}
