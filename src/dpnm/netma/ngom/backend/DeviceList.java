/*
 * @(#)DeviceList.java
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
import java.util.Iterator;

/**
 * This class is for managing Devices
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
public class DeviceList {
	private Vector<Device> devices = null;

	public DeviceList() {	
		devices = new Vector<Device>();
    }

    /**
     * get device list as vector
     *
     * @return device list
     */
	public Vector<Device> getDevices() {
		return devices;
	}

    /**
     * get device list as iterator
     *
     * @return device list iterator
     */
    public Iterator getDevicesIterator() {
        return devices.iterator();
    }

    /**
     * set devices to the list
     *
     * @param devices device list
     */
	public void setDevices(Vector<Device> devices) {
		this.devices = devices;
	}

    /**
     * get device by host address
     *
     * @return device of host address
     */
	public Device getDeviceByHost(String host) {
        for(int i = 0; i < devices.size(); i++) {
			Device device = devices.get(i);
			if(device.getHost().intern() == host.intern()) {
				return device;
			}
		}
		return null;
	}

	/**
     * add device to the manaement list
     * 
     * @param host      host address
     * @param port      SNMP port
     * @param netmask 	netmask
     * @param community community string
     * @param descr     description
     *
     * @return status
     */
     public int addDevice(String host, int port, String community, String netmask, 
            String descr) {
		Device device = getDeviceByHost(host);
		if(device == null) {
			// not found
            //TODO: type이 후에 결정되어져야 한다.
			device = new Device(host, port, community, netmask, descr);
            if (devices == null) {
                devices = new Vector<Device>();
            }
			devices.add(device);
			return Constants.OK;
		}
		// error, already exists
        return Constants.ERROR_DEVICE_ALREADY_EXISTED;
	}

    /**
     * update device to the management list
     * 
     * @param host      host address
     * @param port      SNMP port
     * @param community community string
     * @param netmask   netmask
     * @param descr     description
     * @param active    active status flag
     *
     * @return status
     */
    public int updateDevice(String host, int port, String community, String netmask, 
            String descr, boolean active) {
        //  find device from the list
        Device device = getDeviceByHost(host);
		if(device != null) {
			device.setNetmask(netmask);
			device.setPort(port);
			device.setCommunity(community);
			device.setDescr(descr);
			device.setActive(active);
			return Constants.OK;
		}
		// not found, not updated
		return Constants.ERROR_DEVICE_NOT_FOUND;
	}

    /**
     * remove device from the manaement list
     * 
     * @param host      host address
     *
     * @return status
     */
	public int removeDevice(String host) {
		Device device = getDeviceByHost(host);
		if(device == null) {
			// not found, cannot remove
			return Constants.ERROR_DEVICE_NOT_FOUND;
		}
		// remove router only if no interfaces are attached
		if(device.getInterfaceCount() > 0) {
			return Constants.ERROR_DEVICE_NOT_REMOVED;
		}
		devices.remove(device);
        if (devices.size() == 0) {
            devices = null;
        }
		return Constants.OK;
	}

    /**
     * add interface to the management list
     * 
     * @param host      host address
     * @param ifDescr   interface description
     * @param descr     description
     *
     * @return status
     */
 
	public int addInterface(String host, String ifDescr, String descr, int interval) {
        Device device = getDeviceByHost(host);
		if(device == null) {
			// router not found, interface cannot be added
            return Constants.ERROR_DEVICE_NOT_FOUND;
		}
        Interface in = device.getInterfaceByIfDescr(ifDescr);
		if(in != null) {
			// such interface already exists, interface cannot be added
			return Constants.ERROR_INTERFACE_ALREADY_EXISTED;
		}
        in = new Interface(ifDescr, descr);
        in.setSamplingInterval(interval);
		device.addInterface(in);
		return 0;
	}

    /**
     * update interface to the management list
     * 
     * @param host      host address
     * @param ifDescr   interface description
     * @param descr     description
     * @param interval  sampling interval (seconds)
     * @param active    active status flag
     *
     * @return status
     */
	public int updateInterface(String host, String ifDescr, String descr, 
            int interval, boolean active) {
		Device device = getDeviceByHost(host);
		if(device == null) {
			// router not found, interface cannot be updated
            return Constants.ERROR_DEVICE_NOT_FOUND;
		}
        Interface in = device.getInterfaceByIfDescr(ifDescr);
		if(in == null) {
			// such interface cannot be found and updated
			return Constants.ERROR_INTERFACE_NOT_FOUND;
		}
		in.setDescr(descr);
		in.setSamplingInterval(interval);
		in.setActive(active);
		return Constants.OK;
	}

    /**
     * remove interface from the management list
     * 
     * @param host      host address
     * @param ifDescr   interface description
     *
     * @return status
     */
	public int removeInterface(String host, String ifDescr) {
		Device device = getDeviceByHost(host);
		if(device == null) {
			// router not found, interface cannot be removed
            return Constants.ERROR_DEVICE_NOT_FOUND;
		}
        Interface in = device.getInterfaceByIfDescr(ifDescr);
		if(in == null) {
			// such interface cannot be found and removed
			return Constants.ERROR_INTERFACE_NOT_FOUND;
		}
		device.removeInterface(in);
		return Constants.OK;
	}

	/**
     * update sampling interval
     *
     * @param interval sampling interval (seconds)
     *
     * @return status
     */
	public int updateSamplingInterval(int interval) {
		for(int i = 0; i < devices.size(); i++) {
			Device device = devices.get(i);
			
			Vector<Interface> interfaces = device.getInterfaces();
			for(int j = 0; j < interfaces.size(); j++) {
	            Interface in = interfaces.get(j);
				in.setSamplingInterval(interval);
			}
		}
		return 0;
	}

	public String toString() {
        if (devices != null) {
            StringBuffer buff = new StringBuffer();
            for(int i = 0; i < devices.size(); i++) {
                buff.append(devices.get(i).toString());
            }
            return buff.toString();
        }
        return null;
	}
}

