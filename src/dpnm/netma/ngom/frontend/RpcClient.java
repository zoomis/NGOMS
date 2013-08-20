/*
 * @(#)RpcClient.java
 * 
 * Created on 2008. 02. 21
 *
 *	This software is the confidential and proprietary information of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */

package dpnm.netma.ngom.frontend;

import dpnm.netma.ngom.Conf;
import dpnm.netma.ngom.data.*;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import snmp.SNMPv1CommunicationInterface;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

/**
 * RPCClient class for communicating by XML RPC
 *
 * @author Eric Kang
 * @since 2008/02/21
 * @version $Revision: 1.1 $
 */
class RpcClient {

	private XmlRpcClient webClient;

	RpcClient(String host) throws IOException {
		String hostName = host;
		int hostPort = Conf.SERVER_PORT;
		int colonIndex = host.indexOf(":");
		if(colonIndex != -1) {
			// port specified
            hostName = host.substring(0, colonIndex);
			String portStr = host.substring(colonIndex + 1);
			hostPort = Integer.parseInt(portStr);
		}
		webClient = new XmlRpcClient(hostName, hostPort);
	}

    ////////////////////////////////////////////////////////////////////////
    //  DEVICE OPERATION
    ////////////////////////////////////////////////////////////////////////
	int addDevice(DeviceInfo deviceInfo)
		throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(deviceInfo.getHost());
		params.add(new Integer(deviceInfo.getPort()));
		params.add(deviceInfo.getCommunity());
		params.add(deviceInfo.getNetmask());
		params.add(deviceInfo.getDescr());
		return ((Integer) webClient.execute(Conf.ADD_DEVICE, params)).intValue();
	}

	int updateDevice(DeviceInfo deviceInfo)
		throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(deviceInfo.getHost());
		params.add(new Integer(deviceInfo.getPort()));
		params.add(deviceInfo.getCommunity());
		params.add(deviceInfo.getNetmask());
		params.add(deviceInfo.getDescr());
		params.add(new Boolean(deviceInfo.isActive()));
		return ((Integer) webClient.execute(Conf.UPDATE_DEVICE, params)).intValue();
	}

	int removeDevice(DeviceInfo deviceInfo) throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(deviceInfo.getHost());
		return ((Integer) webClient.execute(Conf.REMOVE_DEVICE, params)).intValue();
	}

	Vector<Hashtable> getDevices() throws IOException, XmlRpcException {
		Vector params = new Vector();
		return (Vector<Hashtable>) webClient.execute(Conf.GET_DEVICES, params);
	}

	Vector getAvailableInterfaces(DeviceInfo deviceInfo)
		throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(deviceInfo.getHost());
		Vector result = (Vector) webClient.execute(Conf.GET_AVAILABLE_INTERFACES, params);
		return result;
	}
    
    Vector<String> getAvailableInterfacesAlias(DeviceInfo deviceInfo)
    throws IOException, XmlRpcException {
        Vector params = new Vector();
        params.add(deviceInfo.getHost());
        Vector<String> result = (Vector<String>) webClient.execute(Conf.GET_AVAILABLE_INTERFACES_ALIAS, params);
        return result;
    }

    Vector getDeviceSample(String host)
    throws IOException, XmlRpcException {
        Vector params = new Vector();
        params.add(host);
        Vector result = (Vector) webClient.execute(Conf.GET_DEVICE_SAMPLE, params);
        return result;
    }
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    //  INTERFACE OPERATION
    ////////////////////////////////////////////////////////////////////////
	int addInterface(DeviceInfo deviceInfo, InterfaceInfo interfaceInfo) throws XmlRpcException, IOException {
		Vector params = new Vector();
		params.add(deviceInfo.getHost());
		params.add(interfaceInfo.getIfDescr());
		params.add(interfaceInfo.getDescr());
		params.add(new Integer(interfaceInfo.getSamplingInterval()));
		return ((Integer) webClient.execute(Conf.ADD_INTERFACE, params)).intValue();
	}

	int updateInterface(DeviceInfo deviceInfo, InterfaceInfo interfaceInfo)
		throws XmlRpcException, IOException {
		Vector params = new Vector();
		params.add(deviceInfo.getHost());
		params.add(interfaceInfo.getIfDescr());
		params.add(interfaceInfo.getDescr());
		params.add(new Integer(interfaceInfo.getSamplingInterval()));
		params.add(new Boolean(interfaceInfo.isActive()));
		return ((Integer) webClient.execute(Conf.UPDATE_INTERFACE, params)).intValue();
	}

	int removeInterface(DeviceInfo deviceInfo, InterfaceInfo interfaceInfo)
		throws XmlRpcException, IOException {
		Vector params = new Vector();
		params.add(deviceInfo.getHost());
		params.add(interfaceInfo.getIfDescr());
		return ((Integer) webClient.execute(Conf.REMOVE_INTERFACE, params)).intValue();
	}

    Vector getInterfaceSample(String host, String ifDescr) 
        throws IOException, XmlRpcException {
        Vector params = new Vector();
        params.add(host);
        params.add(ifDescr);
        Vector result = (Vector) webClient.execute(Conf.GET_INTERFACE_SAMPLE, params);
        return result;
    }
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    //  MAP OPERATION
    ////////////////////////////////////////////////////////////////////////
	int addMap(NetworkMapInfo mapInfo)
		throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(mapInfo.getName());
		params.add(mapInfo.getDescr());
		params.add(new Integer(mapInfo.getWidth()));
		params.add(new Integer(mapInfo.getHeight()));
		return ((Integer) webClient.execute(Conf.ADD_MAP, params)).intValue();
	}

	int updateMap(NetworkMapInfo mapInfo) throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(mapInfo.getName());
		params.add(mapInfo.getDescr());
		params.add(new Integer(mapInfo.getWidth()));
		params.add(new Integer(mapInfo.getHeight()));
		params.add(new Integer(mapInfo.getHeight()));
		return ((Integer) webClient.execute(Conf.UPDATE_MAP_BACKGROUND, params)).intValue();
	}

	int updateMapBackground(NetworkMapInfo mapInfo) throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(mapInfo.getName());
        params.add(mapInfo.getBackground());
		return ((Integer) webClient.execute(Conf.UPDATE_MAP_BACKGROUND, params)).intValue();
	}

	int removeMap(NetworkMapInfo mapInfo) throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(mapInfo.getName());
		return ((Integer) webClient.execute(Conf.REMOVE_MAP, params)).intValue();
	}

	Vector<Hashtable> getMaps() throws IOException, XmlRpcException {
		Vector params = new Vector();
		return (Vector<Hashtable>) webClient.execute(Conf.GET_MAPS, params);
	}

	int addDeviceMap(String mapName, NetworkMapDeviceInfo deviceInfo)
		throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(mapName);
		params.add(deviceInfo.getDeviceInfo().getHost());
		params.add(new Integer(deviceInfo.getXpos()));
		params.add(new Integer(deviceInfo.getYpos()));
        params.add(new Integer(deviceInfo.getIcon()));
        return ((Integer) webClient.execute(Conf.ADD_DEVICE_MAP, params)).intValue();
	}

	int updateDeviceMap(String mapName, NetworkMapDeviceInfo deviceInfo)
		throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(mapName);
		params.add(deviceInfo.getDeviceInfo().getHost());
		params.add(new Integer(deviceInfo.getXpos()));
		params.add(new Integer(deviceInfo.getYpos()));
        params.add(new Integer(deviceInfo.getIcon()));
        return ((Integer) webClient.execute(Conf.UPDATE_DEVICE_MAP, params)).intValue();
	}

	int removeDeviceMap(String mapName, NetworkMapDeviceInfo deviceInfo) throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(mapName);
		params.add(deviceInfo.getDeviceInfo().getHost());
		return ((Integer) webClient.execute(Conf.REMOVE_DEVICE_MAP, params)).intValue();
	}

	Vector<Hashtable> getDevicesOfMap(String mapName) throws IOException, XmlRpcException {
		Vector params = new Vector();
        params.add(mapName); 
		return (Vector<Hashtable>) webClient.execute(Conf.GET_DEVICES_OF_MAP, params);
	}

    ////////////////////////////////////////////////////////////////////////
    //  MAP LINK OPERATION
    ////////////////////////////////////////////////////////////////////////
	int addLinkMap(String mapName, NetworkMapLinkInfo linkInfo)
		throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(mapName);
		params.add(linkInfo.getInterfaceInfo().getIfDescr());
        params.add(new Integer(linkInfo.getKind()));
		params.add(linkInfo.getSrc().getDeviceInfo().getHost());
        params.add(linkInfo.getDst().getDeviceInfo().getHost());
		return ((Integer) webClient.execute(Conf.ADD_LINK_MAP, params)).intValue();
	}

	int updateLinkMap(String mapName, NetworkMapLinkInfo linkInfo)
		throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(mapName);
		params.add(linkInfo.getInterfaceInfo().getIfDescr());
        params.add(new Integer(linkInfo.getKind()));
		params.add(linkInfo.getSrc().getDeviceInfo().getHost());
        params.add(linkInfo.getDst().getDeviceInfo().getHost());
		return ((Integer) webClient.execute(Conf.UPDATE_LINK_MAP, params)).intValue();
	}

	int removeLinkMap(String mapName, NetworkMapLinkInfo linkInfo ) throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(mapName);
		params.add(linkInfo.getInterfaceInfo().getIfDescr());
		params.add(linkInfo.getSrc().getDeviceInfo().getHost());
		params.add(linkInfo.getDst().getDeviceInfo().getHost());
		return ((Integer) webClient.execute(Conf.REMOVE_LINK_MAP, params)).intValue();
	}

	Vector<Hashtable> getLinksOfMap(String mapName) throws IOException, XmlRpcException {
		Vector params = new Vector();
        params.add(mapName); 
		return (Vector<Hashtable>) webClient.execute(Conf.GET_LINKS_OF_MAP, params);
	}

    ////////////////////////////////////////////////////////////////////////
    //  GENERAL OPERATION
    ////////////////////////////////////////////////////////////////////////
	int updateSamplingInterval(int interval) throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(new Integer(interval));
		return ((Integer) webClient.execute(Conf.UPDATE_SAMPLING_INTERVAL, params)).intValue();
	}

	byte[] getPngGraph(DeviceInfo deviceInfo, Date start, Date stop)
		throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(deviceInfo.getHost());
		params.add(start);
		params.add(stop);
		return (byte[]) webClient.execute(Conf.GET_DEVICE_PNG_GRAPH, params);
	}
	
	byte[] getPngGraph(DeviceInfo deviceInfo, InterfaceInfo interfaceInfo, Date start, Date stop)
		throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.add(deviceInfo.getHost());
		params.add(interfaceInfo.getIfDescr());
		params.add(start);
		params.add(stop);
		return (byte[]) webClient.execute(Conf.GET_PNG_GRAPH, params);
	}
	
    Hashtable getBackendManagerInfo() throws IOException, XmlRpcException {
		Vector params = new Vector();
		return (Hashtable) webClient.execute(Conf.GET_BACKEND_MANAGER_INFO, params);
    }

    Hashtable getDataInfo() throws IOException, XmlRpcException {
		Vector params = new Vector();
		return (Hashtable) webClient.execute(Conf.GET_DATA_INFO, params);
	}

    Hashtable getAllInfo() throws IOException, XmlRpcException {
		Vector params = new Vector();
		return (Hashtable) webClient.execute(Conf.GET_ALL_INFO, params);
	}
}

