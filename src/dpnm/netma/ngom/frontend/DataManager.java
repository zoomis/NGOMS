/*
 * @(#)DataManager.java
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

import dpnm.netma.ngom.data.*;

import org.apache.xmlrpc.XmlRpcException;

import java.io.IOException;
import java.util.*;


/**
 * DataManager class provides all data from Backend
 *
 * @author Eric Kang
 * @since 2008/02/21
 * @version $Revision: 1.1 $
 */
public class DataManager {
	private static DataManager _instance;

	private String ngomHost;

	private Vector devicesInfo = null;
    private Vector mapsInfo = null;
	private Hashtable backendInfo = null;

	private RpcClient client;


	public static synchronized DataManager getInstance() {
		if(_instance == null) {
			_instance =new DataManager();
		}
		return _instance;
	}

	private DataManager() {
        devicesInfo = new Vector();
        mapsInfo = new Vector();
        backendInfo = new Hashtable();
	}

	public void connect(String host) throws IOException, XmlRpcException {
		this.ngomHost = host;
        client = new RpcClient(ngomHost);
	}

	public String getHost() {
		return ngomHost;
	}

	public void reload() throws IOException, XmlRpcException {
		Hashtable mInfo = client.getAllInfo();
		backendInfo = (Hashtable) mInfo.get("backendInfo");
		devicesInfo = (Vector) mInfo.get("devices");
		mapsInfo = (Vector) mInfo.get("maps");
	}

    public void reloadData() throws IOException, XmlRpcException {
        if (client != null) {
            devicesInfo = client.getDevices();
        }
    }

    public void reloadMap() throws IOException, XmlRpcException {
        if (client != null) {
            mapsInfo = client.getMaps();
        }
	}
 
    ////////////////////////////////////////////////////////////////////////
    //  DEVICE OPERATION
    ////////////////////////////////////////////////////////////////////////
    public int addDevice(DeviceInfo deviceInfo)
		throws XmlRpcException, IOException {
		int status = client.addDevice(deviceInfo);
		reloadData();
		return status;
	}

	public int updateDevice(DeviceInfo deviceInfo)
		throws XmlRpcException, IOException {
		int status = client.updateDevice(deviceInfo);
		reloadData();
		return status;
	}

	public int removeDevice(DeviceInfo deviceInfo)
		throws XmlRpcException, IOException {
		int status = client.removeDevice(deviceInfo);
		reloadData();
		return status;
	}

	public String[] getAvailableLinks(DeviceInfo deviceInfo) throws IOException, XmlRpcException {
		Vector links = client.getAvailableInterfaces(deviceInfo);
		return (String[]) links.toArray(new String[0]);
	}
    
    public String[] getAvailableLinksAlias(DeviceInfo deviceInfo) throws IOException, XmlRpcException {
        Vector links = client.getAvailableInterfacesAlias(deviceInfo);
        return (String[]) links.toArray(new String[0]);
    }

    public int getDeviceCount() {
		return devicesInfo != null ? devicesInfo.size() : 0;
	}

	public DeviceInfo getDeviceInfo(int deviceIndex) {
		Hashtable deviceInfo = devicesInfo.get(deviceIndex);
		DeviceInfo rInfo = new DeviceInfo();
		rInfo.setHost((String)deviceInfo.get(Device.HOST));
		rInfo.setNetmask((String)deviceInfo.get(Device.NETMASK));
		rInfo.setCommunity((String)deviceInfo.get(Device.COMMUNITY));
		rInfo.setDescr((String)deviceInfo.get(Device.DESCRIPTION));
		rInfo.setActive((Boolean) deviceInfo.get(Device.ACTIVE));
		rInfo.setType((byte)((Integer)deviceInfo.get(Device.TYPE)).intValue());
		Vector interfaceInfo = new Vector();
        int size = getInterfaceCount(deviceIndex);

		for(int linkIndex = 0; linkIndex < size; linkIndex++) {
			interfaceInfo.add(getInterfaceInfo(deviceIndex, linkIndex));
		}
		Collections.sort(interfaceInfo, InterfaceInfo.getComparator());
		rInfo.setInterfaceInfo((InterfaceInfo[]) interfaceInfo.toArray(new InterfaceInfo[0]));
		return rInfo;
	}

    public DeviceInfo[] getDevices() {
		Vector rInfo = new Vector();
        int size = getDeviceCount();
		for(int deviceIndex = 0; deviceIndex < size; deviceIndex++) {
			rInfo.add(getDeviceInfo(deviceIndex));
		}
		Collections.sort(rInfo, DeviceInfo.getComparator());
		return (DeviceInfo[]) rInfo.toArray(new DeviceInfo[0]);
    }

	public String getDeviceHost(int deviceIndex) {
		Hashtable deviceInfo = (Hashtable) devicesInfo.get(deviceIndex);
		return (String) deviceInfo.get(Device.HOST);
	}
	
    public int getDeviceIndexByHost(String host) {
    	for (int i = 0; i < devicesInfo.size(); i++) {
    		if (getDeviceHost(i).equalsIgnoreCase(host)) {
    			return i;
    		}
    	}
    	return -1;
    }

    public DeviceInfo getDeviceInfoByHost(String host) {
    	for (int i = 0; i < devicesInfo.size(); i++) {
    		if (getDeviceHost(i).equalsIgnoreCase(host)) {
    			return getDeviceInfo(i);
    		}
    	}
    	return null;
    }

    public DeviceSample getDeviceSample(String host) throws XmlRpcException, IOException {
        Vector v = client.getDeviceSample(host);
        DeviceSample sample = new DeviceSample();
        sample.setInfo(v);
        return sample;
    }
    ////////////////////////////////////////////////////////////////////////
 
    ////////////////////////////////////////////////////////////////////////
    //  INTERFACE OPERATION
    ////////////////////////////////////////////////////////////////////////
	public int addInterface(DeviceInfo deviceInfo, InterfaceInfo interfaceInfo)
		throws XmlRpcException, IOException {
		int status = client.addInterface(deviceInfo, interfaceInfo);
		reloadData();
		return status;
	}

	public int updateInterface(DeviceInfo deviceInfo, InterfaceInfo interfaceInfo)
		throws XmlRpcException, IOException {
		int status = client.updateInterface(deviceInfo, interfaceInfo);
		reloadData();
		return status;
	}

	public int removeInterface(DeviceInfo deviceInfo, InterfaceInfo interfaceInfo)
		throws XmlRpcException, IOException {
		int status = client.removeInterface(deviceInfo, interfaceInfo);
		reloadData();
		return status;
	}

    public InterfaceSample getInterfaceSample(String host, String ifDescr) throws XmlRpcException, IOException {
        Vector v = client.getInterfaceSample(host, ifDescr);
        InterfaceSample sample = new InterfaceSample();
        sample.setInfo(v);
        return sample;
    }

    public InterfaceInfo getInterfaceInfoByIfDescr(String host, String ifDescr) {
    	return getInterfaceInfoByIfDescr(getDeviceIndexByHost(host), ifDescr);
    }
    
    public InterfaceInfo getInterfaceInfoByIfDescr(int deviceIndex, String ifDescr) {
   		Hashtable deviceInfo = devicesInfo.get(deviceIndex);
		Vector interfacesData = (Vector) deviceInfo.get(Device.INTERFACE);

		for (int i = 0; i < interfacesData.size(); i++) {
			Hashtable interfaceInfo = interfacesData.get(i);
			String originalIfDescr = (String)interfaceInfo.get(
                    Interface.IFDESCR);
			if (originalIfDescr.equalsIgnoreCase(ifDescr)) {
				return this.getInterfaceInfo(deviceIndex, i);
			}
		}
		return null;
    }

    public int getInterfaceCount(int deviceIndex) {
		Hashtable deviceInfo = devicesInfo.get(deviceIndex);
		Vector linksInfo = (Vector) deviceInfo.get(Device.INTERFACE);
		return linksInfo.size();
	}


 	public InterfaceInfo getInterfaceInfo(int deviceIndex, int linkIndex) {
        Hashtable deviceInfo = devicesInfo.get(deviceIndex);
		Vector linksInfo = (Vector) deviceInfo.get(Device.INTERFACE);
		Hashtable interfaceInfo = linksInfo.get(linkIndex);
		InterfaceInfo lInfo = new InterfaceInfo();
		lInfo.setIfIndex(((Integer) interfaceInfo.get(Interface.IFINDEX)).intValue());
		lInfo.setIfDescr((String)interfaceInfo.get(Interface.IFDESCR));
		lInfo.setIfAlias((String)interfaceInfo.get(Interface.IFALIAS));
		lInfo.setDescr((String)interfaceInfo.get(Interface.DESCR));
		lInfo.setSamplingInterval(((Integer)interfaceInfo.get(Interface.SAMPLINGINTERVAL)).intValue());
		lInfo.setActive(((Boolean) interfaceInfo.get(Interface.ACTIVE)).booleanValue());

        //  long은 XML RPC가 기본적으로 전송하지 못하기 때문에 String으로 전송을 해서 다시 Long형으로 Type casting을 해야 한다.
		String str = (String) interfaceInfo.get(Interface.SAMPLECOUNT);
		lInfo.setSampleCount(str != null ?  Long.parseLong(str) : 0);

		str = (String) interfaceInfo.get(Interface.LASTSAMPLETIME);
		lInfo.setLastSampleTime(new Date(str != null ?  Long.parseLong(str) : 0));
		
		lInfo.setHost((String)interfaceInfo.get(Interface.HOST));

		str = (String) interfaceInfo.get(Interface.IFSPEED);
		lInfo.setSpeed(str != null ?  Long.parseLong(str) : 0);

		str = (String) interfaceInfo.get(Interface.IFINOCTETS);
		lInfo.setIfInOctets(str != null ?  Long.parseLong(str) : 0);

		str = (String) interfaceInfo.get(Interface.IFOUTOCTETS);
		lInfo.setIfOutOctets(str != null ?  Long.parseLong(str) : 0);

		str = (String) interfaceInfo.get(Interface.IFINERRORS);
		lInfo.setIfInErrors(str != null ?  Long.parseLong(str) : 0);

		str = (String) interfaceInfo.get(Interface.IFOUTERRORS);
		lInfo.setIfOutErrors(str != null ?  Long.parseLong(str) : 0);
		
		Object o = interfaceInfo.get(Interface.IFADMINSTATUS);
		lInfo.setIfAdminStatus(o != null ?  ((Integer)o).intValue() : 0);
		o = interfaceInfo.get(Interface.IFOPERSTATUS);
		lInfo.setIfOperStatus(o != null ?  ((Integer)o).intValue() : 0);

		lInfo.setInKbps(((Double)interfaceInfo.get(Interface.INKBPS)).doubleValue());
		lInfo.setOutKbps(((Double)interfaceInfo.get(Interface.OUTKBPS)).doubleValue());
        lInfo.setTotalKbps(((Double)interfaceInfo.get(Interface.TOTALKBPS)).doubleValue());
        
		lInfo.setInUtil(((Double)interfaceInfo.get(Interface.INUTIL)).doubleValue());
		lInfo.setOutUtil(((Double)interfaceInfo.get(Interface.OUTUTIL)).doubleValue());
        lInfo.setTotalUtil(((Double)interfaceInfo.get(Interface.TOTALUTIL)).doubleValue());
		return lInfo;
	}
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    //  MAP OPERATION
    ////////////////////////////////////////////////////////////////////////
    public int addMap(NetworkMapInfo mapInfo) throws XmlRpcException, IOException {
        int status = client.addMap(mapInfo);
        reloadMap();
        return status;
    }
    
    public int updateMap(NetworkMapInfo mapInfo) throws XmlRpcException, IOException {
        int status = client.updateMap(mapInfo);
        reloadMap();
        return status;
    }

    public int updateMapBackground(NetworkMapInfo mapInfo) throws XmlRpcException, IOException {
        int status = client.updateMapBackground(mapInfo);
        reloadMap();
        return status;
    }
    public int removeMap(NetworkMapInfo mapsInfo)
        throws XmlRpcException, IOException {
        int status = client.removeMap(mapsInfo);
        reloadMap();
        return status;
    }
 
    public String[] getMapNames() {
		String[] names = new String[mapsInfo.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = getMapName(i);
		}
		return names;
	}
	
	public String getMapName(int mapIndex) {
		Hashtable mi = mapsInfo.get(mapIndex);
		return (String) mi.get(NetworkMap.NAME);
	}
	
    public NetworkMapInfo getNetworkMapInfoByName(String name) {
        for (int i = 0; i < mapsInfo.size(); i++) {
            if (getMapName(i).equalsIgnoreCase(name)) {
                return getNetworkMapInfo(i);
            }
        }
        return null;
    }
	public NetworkMapInfo getNetworkMapInfo(int mapIndex) {
		Hashtable mi = mapsInfo.get(mapIndex);
		NetworkMapInfo map = new NetworkMapInfo();
		map.setName((String)mi.get(NetworkMap.NAME));
		map.setDescr((String)mi.get(NetworkMap.DESCR));
		map.setWidth(((Integer)mi.get(NetworkMap.WIDTH)).intValue());
		map.setHeight(((Integer)mi.get(NetworkMap.HEIGHT)).intValue());
		map.setBackground((String)mi.get(NetworkMap.BACKGROUND));
		
		//	get router information
		Vector devices = 
                (Vector)mi.get(NetworkMap.DEVICE);
		NetworkMapDeviceInfo info[] = new NetworkMapDeviceInfo[devices.size()];
		for (int i = 0; i < info.length; i++) {
			info[i] = new NetworkMapDeviceInfo();
			Hashtable ri = devices.get(i);
			info[i].setXpos(((Integer)ri.get(NetworkMapDevice.XPOS)).intValue());
			info[i].setYpos(((Integer)ri.get(NetworkMapDevice.YPOS)).intValue());
			info[i].setIcon(((Integer)ri.get(NetworkMapDevice.ICON)).intValue());
			info[i].setDeviceInfo(getDeviceInfoByHost((String)ri.get(NetworkMapDevice.HOST)));
		}
		map.setDevices(info);

		//	get links information
		Vector links = (Vector)mi.get(NetworkMap.LINK);
		NetworkMapLinkInfo linksInfo[] = new NetworkMapLinkInfo[links.size()];
		for (int i = 0; i < linksInfo.length; i++) {
			linksInfo[i] = new NetworkMapLinkInfo();
			Hashtable ri = links.get(i);
            String src = (String)ri.get(NetworkMapLink.SRC);
            String dst = (String)ri.get(NetworkMapLink.DST);
            boolean fs = false;
            boolean fd = false;
			for (int j = 0; j < (info.length) && !(fs && fd); j++) {
				if (info[j].getDeviceInfo().getHost().equalsIgnoreCase(src)) {
					linksInfo[i].setSrc(info[j]);
                    fs = true;
				}
				if (info[j].getDeviceInfo().getHost().equalsIgnoreCase(dst)) {
					linksInfo[i].setDst(info[j]);
                    fd = true;
				}
			}
//			System.out.println("LINKS INFO ======> " + linksInfo[i].getInfo());
			linksInfo[i].setInterfaceInfo(getInterfaceInfoByIfDescr(
					getDeviceIndexByHost(linksInfo[i].getSrc().getDeviceInfo().getHost()), 
					(String)ri.get(NetworkMapLink.IFDESCR)));
            linksInfo[i].setKind(((Integer)ri.get(NetworkMapLink.KIND)).intValue());
        }
		map.setLinks(linksInfo);

		return map;
	}
	
    ////////////////////////////////////////////////////////////////////////
    //  MAP DEVICE OPERATION
    ////////////////////////////////////////////////////////////////////////
    public int addDeviceMap(String mapName, NetworkMapDeviceInfo deviceInfo) throws XmlRpcException, IOException {
        int status = client.addDeviceMap(mapName, deviceInfo);
        reloadMap();
        return status;
    }
    
    public int updateDeviceMap(String mapName, NetworkMapDeviceInfo deviceInfo) throws XmlRpcException, IOException {
        int status = client.updateDeviceMap(mapName, deviceInfo);
        reloadMap();
        return status;
    }

    public int removeDeviceMap(String mapName, NetworkMapDeviceInfo deviceInfo)
        throws XmlRpcException, IOException {
        int status = client.removeDeviceMap(mapName, deviceInfo);
        reloadMap();
        return status;
    }

    ////////////////////////////////////////////////////////////////////////
    //  MAP LINK OPERATION
    ////////////////////////////////////////////////////////////////////////
    public int addLinkMap(String mapName, NetworkMapLinkInfo linkInfo) throws XmlRpcException, IOException {
        int status = client.addLinkMap(mapName, linkInfo);
        reloadMap();
        return status;
    }
    
    public int updateLinkMap(String mapName, NetworkMapLinkInfo linkInfo) throws XmlRpcException, IOException {
        int status = client.updateLinkMap(mapName, linkInfo);
        reloadMap();
        return status;
    }

    public int removeLinkMap(String mapName, NetworkMapLinkInfo linkInfo)
        throws XmlRpcException, IOException {
        int status = client.removeLinkMap(mapName, linkInfo);
        reloadMap();
        return status;
    }
 
    ////////////////////////////////////////////////////////////////////////
    //  GENERAL OPERATION
    ////////////////////////////////////////////////////////////////////////
    public int updateSamplingInterval(int sampling)
        throws XmlRpcException, IOException {
        int status = client.updateSamplingInterval(sampling);
        reloadData();
        return status;
    }

    public byte[] getPngGraph(DeviceInfo deviceInfo, InterfaceInfo interfaceInfo, 
            Date start, Date stop)
		throws IOException, XmlRpcException {
		return interfaceInfo == null ?
				client.getPngGraph(deviceInfo, start, stop) :
				client.getPngGraph(deviceInfo, interfaceInfo, start, stop);
	}

	public BackendInfo getBackendInfo() {
		BackendInfo info = new BackendInfo();
		info.setServerHost(ngomHost);
		String str = (String) backendInfo.get("badSavesCount");
		info.setBadSavesCount(str != null ?  Long.parseLong(str) : 0);
		str = (String) backendInfo.get("goodSavesCount");
		info.setGoodSavesCount(str != null ?  Long.parseLong(str) : 0);
		str = (String) backendInfo.get("savesCount");
		info.setSavesCount(str != null ?  Long.parseLong(str) : 0);
		str = (String) backendInfo.get("sampleCount");
		info.setSampleCount(str != null ?  Long.parseLong(str) : 0);
		info.setPoolEfficency(((Double)backendInfo.get("poolEfficency")).doubleValue());
		info.setStartDate((Date)backendInfo.get("startDate"));
		return info;
	}

}
