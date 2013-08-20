/*
 * @(#)NetworkMapList.java
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

import dpnm.netma.ngom.data.*;

import java.util.Vector;

/**
 * This class is for managing Maps
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
public class NetworkMapList {
	private Vector<NetworkMap> maps = null;

	public NetworkMapList() {	
		maps = new Vector<NetworkMap>();
	}

    /**
     * get the network map list
     *
     * @return network map list
     */
	public Vector<NetworkMap> getMaps() {
		return maps;
	}

    /**
     * set the map list
     *
     * @param maps list
     */
	void setMaps(Vector<NetworkMap> maps) {
		this.maps = maps;
	}

    /**
     * get the map by name
     *
     * @return map
     */
	public NetworkMap getMapByName(String name) {
        if (maps != null) {
            for(int i = 0; i < maps.size(); i++) {
                NetworkMap map = maps.get(i);
                if(map.getName().equalsIgnoreCase(name)) {
                    return map;
                }
            }
        }
		return null;
	}

    /**
     * get devices of the map
     *
     * @return device list
     */
	public Vector<NetworkMapDevice> getDevicesOfMap(String name) {
		NetworkMap map = getMapByName(name);
		if (map == null) {
			return null;
		}
		return map.getDevices();
	}
	
    /**
     * get links of the map
     *
     * @return link list
     */
	public Vector<NetworkMapLink> getLinksOfMap(String name) {
		NetworkMap map = getMapByName(name);
		if (map == null) {
			return null;
		}
		return map.getLinks();
	}
	
    /**
     * add map
     *
     * @param name map name
     * @param descr map description
     * @param width map width
     * @param height map height
     * @return status
     */
	public int addMap(String name, String descr, int width, int height) {
		NetworkMap map = getMapByName(name);
		if(map == null) {
			// not found
			map = new NetworkMap(name, descr, width, height);
			maps.add(map);
			// added
			return Constants.OK;
		}
		// error, already exists
		return Constants.ERROR_MAP_ALREADY_EXISTED;
	}

    /**
     * update map size to the management list
     * 
     * @param name      map name
     * @param descr     map description
     * @param width     map width
     * @param height    map height
     *
     * @return status
     */
    public int updateMap(String name, String descr, int width, int height) {
        NetworkMap map = getMapByName(name);
		if(map != null) {
            map.setName(name);
            map.setDescr(descr);
            map.setWidth(width);
            map.setHeight(height);
			return Constants.OK;
		}
		// not found, not updated
		return Constants.ERROR_MAP_NOT_FOUND;
	}

    /**
     * update map background to the management list
     * 
     * @param name      map name
     * @param background    background
     *
     * @return status
     */
    public int updateMapBackground(String name, String background) {
        NetworkMap map = getMapByName(name);
		if(map != null) {
            map.setBackground(background);
			return Constants.OK;
		}
		// not found, not updated
		return Constants.ERROR_MAP_NOT_FOUND;
	}

    /**
     * remove map to the management list
     * 
     * @param name      map name
     *
     * @return status
     */
	public int removeMap(String name) {
		NetworkMap map = getMapByName(name);
		if(map == null) {
			// not found, cannot remove
			return Constants.ERROR_MAP_NOT_FOUND;
		}
		maps.remove(map);
		return Constants.OK;
	}
	
    /**
     * add device to the given map
     * 
     * @param name      map name
     * @param host      host address
     * @param xpos      x position
     * @param ypos      y position
     * @param icon      icon
     *
     * @return status
     */
	public int addDevice(String name, String host, int xpos, int ypos, int icon) {
        NetworkMap map = getMapByName(name);
		if(map == null) {
			// map not found, router cannot be added
            return Constants.ERROR_MAP_NOT_FOUND;
		}
        NetworkMapDevice device = map.getDeviceByHost(host);
		if(device != null) {
			// such router already exists, router cannot be added
			return Constants.ERROR_DEVICE_ALREADY_EXISTED;
		}
        device  = new NetworkMapDevice(host, xpos, ypos, icon);
		map.addDevice(device);
		return Constants.OK;
    }

    /**
     * update device to the given map
     * 
     * @param name      map name
     * @param host      host address
     * @param xpos      x position
     * @param ypos      y position
     * @param icon      icon
     *
     * @return status
     */
	public int updateDevice(String name, String host, int xpos, int ypos, int icon) {
        NetworkMap map = getMapByName(name);
		if(map == null) {
			// map not found, router cannot be added
            return Constants.ERROR_MAP_NOT_FOUND;
		}
    
        NetworkMapDevice device = map.getDeviceByHost(host);
		if(device == null) {
			// such router cannot be found and updated
			return Constants.ERROR_DEVICE_NOT_FOUND;
		}
		device.setHost(host);
        device.setXpos(xpos);
        device.setYpos(ypos);
        device.setIcon(icon);
		return Constants.OK;
    }

    /**
     * remove device from the given map
     * 
     * @param name      map name
     * @param host      host address
     *
     * @return status
     */
    public int removeDevice(String name, String host) {
        NetworkMap map = getMapByName(name);
		if(map == null) {
			// map not found, router cannot be added
            return -1;
		}

        NetworkMapDevice device = map.getDeviceByHost(host);
		if(device == null) {
			// such router cannot be found and updated
			return Constants.ERROR_DEVICE_NOT_FOUND;
		}
        map.removeDevice(device);
		return Constants.OK;
    }

    /**
     * add link to the given map
     * 
     * @param name      map name
     * @param ifDescr   interface description
     * @param kind      type of interface
     * @param src       source device address
     * @param dst       destination device address
     *
     * @return status
     */
    public int addLink(String name, String ifDescr, int kind, String src, String dst) {
        NetworkMap map = getMapByName(name);
		if(map == null) {
			// map not found, router cannot be added
            return Constants.ERROR_MAP_NOT_FOUND;
		}
        NetworkMapLink link = map.getLink(ifDescr, src, dst);
		if(link != null) {
			// such link already exists, link cannot be added
			return Constants.ERROR_LINK_ALREADY_EXISTED;
		}
        link = new NetworkMapLink(ifDescr, kind, src, dst);
		map.addLink(link);
		return Constants.OK;
    }

    /**
     * update link to the given map
     * 
     * @param name      map name
     * @param ifDescr   interface description
     * @param kind      type of interface
     * @param src       source device address
     * @param dst       destination device address
     *
     * @return status
     */
    public int updateLink(String name, String ifDescr, int kind, String src, String dst) {
        NetworkMap map = getMapByName(name);
		if(map == null) {
			// map not found, router cannot be added
            return Constants.ERROR_MAP_NOT_FOUND;
		}
        NetworkMapLink link = map.getLink(ifDescr, src, dst);
		if(link == null) {
			// such link cannot be found;
			return Constants.ERROR_LINK_NOT_FOUND;
		}
        link.setIfDescr(ifDescr);
        link.setKind(kind);
        link.setSrc(src);
        link.setDst(dst);
		return Constants.OK;
    }

    /**
     * remove link from the given map
     * 
     * @param name      map name
     * @param ifDescr   interface description
     * @param src       source device address
     * @param dst       destination device address
     *
     * @return status
     */
    public int removeLink(String name, String ifDescr, String src, String dst) {
        NetworkMap map = getMapByName(name);
		if(map == null) {
			// map not found, router cannot be added
            return Constants.ERROR_MAP_NOT_FOUND;
		}
        NetworkMapLink link = map.getLink(ifDescr, src, dst);
		if(link == null) {
			// such link cannot be found and updated
			return Constants.ERROR_LINK_NOT_FOUND;
		}
        map.removeLink(link);
		return Constants.OK;
    }

    /**
     * toString
     */
	public String toString() {
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < maps.size(); i++) {
			buff.append(maps.get(i));
		}
		return buff.toString();
	}


}

