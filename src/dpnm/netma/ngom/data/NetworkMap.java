/*
 * @(#)NetworkMap.java
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
import java.util.Map;
import java.util.Vector;

/**
 * This class is for NetworkMap
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
public class NetworkMap {
    /*
     * constant information
     */
    /** device */
    public static final String MAP        = "map";
    public static final String NAME       = "name";
    public static final String DESCR      = "description";
    public static final String WIDTH      = "width";
    public static final String HEIGHT     = "height";
    public static final String BACKGROUND = "background";
    public static final String DEVICE     = "device";
    public static final String LINK       = "link";
 
	protected String name = "";
	protected String descr = "";
	protected int width = 0;
	protected int height = 0;
    protected String background = "";

	protected Vector<NetworkMapDevice> devices = null;
	protected Vector<NetworkMapLink>   links   = null;

	public NetworkMap() { 
		devices = new Vector<NetworkMapDevice>();
		links = new Vector<NetworkMapLink>();
    }

    /**
     * Constructor
     *
     * @param name map name
     * @param descr map description
     * @param width map width
     * @param height map height
     */
	
	public NetworkMap(String name, String descr, int width, int height) { 
		this();
        setName(name);
        setDescr(descr);
        setWidth(width);
        setHeight(height);
    }

	public NetworkMap(Node mapNode) {
		this();
		NodeList nodes = mapNode.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			String name = node.getNodeName();
			Node firstChild = node.getFirstChild();
			String value = (firstChild != null)? firstChild.getNodeValue().trim(): null;
            if(name.intern() == NAME.intern()) {
				setName(value);
			}
            else if(name.intern() == DESCR.intern()) {
				setDescr(value);
			}
            else if(name.intern() == WIDTH.intern()) {
				setWidth(Integer.parseInt(value));
			}
            else if(name.intern() == HEIGHT.intern()) {
				setHeight(Integer.parseInt(value));
			}
            else if(name.intern() == BACKGROUND.intern()) {
				setBackground(value == null ? "" : value);
			}
            else if(name.intern() == DEVICE.intern()) {
				devices.add(new NetworkMapDevice(node));
			}
            else if(name.intern() == LINK.intern()) {
				links.add(new NetworkMapLink(node));
			}
		}
	}

    /**
     * add device to the map
     *
     * @param device device
     */
	public void addDevice(NetworkMapDevice device) {
        devices.add(device);
	}

    /**
     * remove device from the map
     *
     * @param device device
     */
	public void removeDevice(NetworkMapDevice device) {
        devices.remove(devices);
	}

    /**
     * get the number of devices of this map
     *
     * @return number of devices
     */
	public int getDeviceCount() {
		return devices.size();
	}


    /**
     * add link to the map
     *
     * @param link link
     */
	public void addLink(NetworkMapLink link) {
		links.add(link);
	}

    /**
     * remove the link from the map
     */
	public void removeLink(NetworkMapLink link) {
        links.remove(link);
	}

    /**
     * get the number of links of this map
     *
     * @return number of links
     */
	public int getLinkCount() {
		return links.size();
	}

    /**
     * get the map information
     *
     * @return map information
     */
	public Hashtable getMapInfo() {
		Hashtable table = new Hashtable();
		table.put(NAME, name);
		table.put(DESCR, descr);
		table.put(WIDTH, new Integer(width));
		table.put(HEIGHT, new Integer(height));
		table.put(BACKGROUND, background);
		// add device info
		Vector<Hashtable> deviceData = new Vector<Hashtable>();
		for (int i = 0; i < devices.size(); i++) {
			NetworkMapDevice device = devices.get(i);
			deviceData.add(device.getDeviceInfo());
		}
		table.put(DEVICE, deviceData);
		// add link info
		Vector<Hashtable> linkData = new Vector<Hashtable>();
		for (int i = 0; i < links.size(); i++) {
			NetworkMapLink link = links.get(i);
			linkData.add(link.getLinkInfo());
		}
		table.put(LINK, linkData);
		return table;
	}

	public void appendXml(Element root) {
        Document doc = root.getOwnerDocument();
		Element mapElem = doc.createElement(MAP);
		root.appendChild(mapElem);
		Element nameElem = doc.createElement(NAME);
		nameElem.appendChild(doc.createTextNode(name));
		mapElem.appendChild(nameElem);
		Element descrElem = doc.createElement(DESCR);
		descrElem.appendChild(doc.createTextNode(descr));
		mapElem.appendChild(descrElem);
		Element widthElem = doc.createElement(WIDTH);
		widthElem.appendChild(doc.createTextNode(String.valueOf(width)));
		mapElem.appendChild(widthElem);
		Element heightElem = doc.createElement(HEIGHT);
		heightElem.appendChild(doc.createTextNode(String.valueOf(height)));
		mapElem.appendChild(heightElem);
		Element backgroundElem = doc.createElement(BACKGROUND);
		backgroundElem.appendChild(doc.createTextNode(background));
		mapElem.appendChild(backgroundElem);

		for (int i = 0; i < devices.size(); i++) {
			NetworkMapDevice device = devices.elementAt(i);
			device.appendXml(mapElem);
		}
	
		for (int i = 0; i < links.size(); i++) {
			NetworkMapLink link = (NetworkMapLink) links.elementAt(i);
			link.appendXml(mapElem);
		}
	}

    /**
     * get the description of map
     *
     * @return map description
     */
	public String getDescr() {
		return descr;
	}

    /**
     * set the description of map
     *
     * @param descr description of map
     */
	public void setDescr(String descr) {
		this.descr = descr;
	}

    /**
     * get the network map link
     *
     * @return network map link list
     */
	public Vector<NetworkMapLink> getLinks() {
		return links;
	}

    /**
     * set the network map link
     *
     * @param links network map link
     */
	public void setLinks(Vector<NetworkMapLink> links) {
		this.links = links;
	}

    /**
     * get the name of map
     *
     * @return map name
     */
	public String getName() {
		return name;
	}

    /**
     * set the name of map
     *
     * @param name map name
     */
	public void setName(String name) {
		this.name = name;
	}

    /**
     * get the devices of map
     *
     * @return devices
     */
	public Vector<NetworkMapDevice> getDevices() {
		return devices;
	}

    /**
     * set the devices of map
     *
     * @param devices devices
     */
	public void setDevices(Vector<NetworkMapDevice> devices) {
		this.devices = devices;
	}

    /**
     * get the height of map
     *
     * @return height of map
     */
	public int getHeight() {
		return height;
	}

    /**
     * set the height of map
     *
     * @param height height of map
     */
	public void setHeight(int height) {
		this.height = height;
	}

    /**
     * get the width of map
     *
     * @return width of map
     */
	public int getWidth() {
		return width;
	}

    /**
     * set the width of map
     *
     * @param width width of map
     */
	public void setWidth(int width) {
		this.width = width;
	}

    /**
	 * @return the background
	 */
	public String getBackground() {
		return background;
	}

	/**
	 * @param background the background to set
	 */
	public void setBackground(String background) {
		this.background = background;
	}

	/**
     * get the device by host name
     *
     * @return device
     */
	public NetworkMapDevice getDeviceByHost(String host) {
        for(int i = 0; i < devices.size(); i++) {
			NetworkMapDevice device = devices.get(i);
			if(device.getHost().equalsIgnoreCase(host)) {
				return device;
			}
		}
		return null;
	}

    /**
     * get the link by interface description
     *
     * @return link
     */
	public NetworkMapLink getLinkByIfDescr(String ifDescr) {
		for(int i = 0; i < links.size(); i++) {
			NetworkMapLink link = links.get(i);
            if(link.getIfDescr().equalsIgnoreCase(ifDescr)) {
				return link;
			}
		}
		return null;
	}
	
    /**
     * get the link by interface description, source device and destination
     * device.
     *
     * @return link
     */
	public NetworkMapLink getLink(String ifDescr, String src, String dst) {
		for(int i = 0; i < links.size(); i++) {
			NetworkMapLink link = links.get(i);
            if(link.getIfDescr().equalsIgnoreCase(ifDescr) &&
            		link.getSrc().equalsIgnoreCase(src) &&
            		link.getDst().equalsIgnoreCase(dst)) {
				return link;
			}
		}
		return null;		
	}

	public String toString() {
		String buff = new String();
		buff += "Map: " + name + ", ";
		buff += "descr=" + descr + ", ";
		buff += "("+width+ ", " + height + ")" + "\n";
		buff += " - "+background + "\n";

        if (devices != null) {
            // dump routers
            for(int i = 0; i < devices.size(); i++) {
                NetworkMapDevice router = devices.get(i);
                buff += "  Router: " + router + "\n";
            }
        }

        if (links != null) {
            // dump links
            for(int i = 0; i < links.size(); i++) {
                NetworkMapLink link = links.get(i);
                buff += "  Link: " + link + "\n";
            }
        }
		return buff;
	}

}
