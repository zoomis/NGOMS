/*
 * @(#)DataManager.java
 * 
 * Created on 2008. 2. 18
 *
 *	This software is the confidential and proprietary informatioon of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */

package dpnm.netma.ngom.backend;

import dpnm.netma.ngom.Conf;
import dpnm.netma.ngom.NGOMException;
import dpnm.netma.ngom.util.Logger;
import dpnm.netma.ngom.comm.snmp.SnmpReader;
import dpnm.netma.ngom.comm.xmlrpc.XMLRPCServer;
import dpnm.netma.ngom.data.*;
import dpnm.netma.ngom.db.rrd.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
/**
 * DataManager manages all data of backend server.
 *
 * @author Eric Kang
 * @since 2008/02/18
 * @version $Revision: 1.1 $
 */
class DataManager {
    private static DataManager _instance;

	public synchronized static DataManager getInstance() {
		if (_instance == null) {
			try {
    			_instance = new DataManager();
			} catch (Exception ex) {
	            if (Conf.DEBUG) {
    				ex.printStackTrace();
	                Logger.getInstance().logBackend("DataManager", 
	                    "Event handler error: " + ex);
	            }
			}
		}
		return _instance;
	}

    /*  member variable definition */
    private DeviceList      _deviceList = null;
    private NetworkMapList         _mapList    = null;

    DataManager() throws NGOMException {
 		// load configuration
		String resource = Resource.getResourceFile();
		if(new File(resource).exists()) {
			load();
		}
		else {
			save();
		}
		
		//	load map information
		String mapFile = Resource.getMapFile();
		if (new File(mapFile).exists()) {
			loadNetworkMap();
		} else {
			saveNetworkMap();
		}
    }

	/**
	 * @return the _deviceList
	 */
	synchronized DeviceList getDeviceList() {
		return _deviceList;
	}

	/**
	 * @return the _mapList
	 */
	synchronized NetworkMapList getMapList() {
		return _mapList;
	}

	synchronized void save() throws NGOMException {
		if(_deviceList == null) {
			_deviceList = new DeviceList();
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement(Conf.HANDLER_NAME);
			doc.appendChild(root);
			Vector<Device> devices = _deviceList.getDevices();
			if (devices != null) {
    			for(int i = 0; i < devices.size(); i++) {
    				devices.get(i).appendXml(root);
    			}
			}
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "EUC-KR");
			DOMSource source = new DOMSource(root);
			FileOutputStream destination = new FileOutputStream(Resource.getResourceFile());
			StreamResult result = new StreamResult(destination);
			transformer.transform(source, result);
			destination.close();
		} catch (Exception e) {
			if (Conf.DEBUG) {
    			e.printStackTrace();
			}
			throw new NGOMException(e);
		}
	}

	private synchronized void load() throws NGOMException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(Resource.getResourceFile()));
			Element root = doc.getDocumentElement();
			NodeList nodes = root.getElementsByTagName(Device.DEVICE);
			_deviceList = new DeviceList();
			Vector<Device> devices = _deviceList.getDevices();
			for(int i = 0; i < nodes.getLength(); i++) {
				devices.add(new Device(nodes.item(i)));
			}
		} catch (Exception e) {
			throw new NGOMException(e);
		}
	}

	private synchronized void saveNetworkMap() throws NGOMException {
		if(_mapList == null) {
			_mapList = new NetworkMapList();
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement(Conf.HANDLER_NAME);
			doc.appendChild(root);
			Vector<NetworkMap> maps = _mapList.getMaps();
			if (maps != null) {
    			for(int i = 0; i < maps.size(); i++) {
    				maps.get(i).appendXml(root);
    			}
			}
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "EUC-KR");
			DOMSource source = new DOMSource(root);
			FileOutputStream destination = new FileOutputStream(Resource.getMapFile());
			StreamResult result = new StreamResult(destination);
			transformer.transform(source, result);
			destination.close();
		} catch (Exception e) {
			throw new NGOMException(e);
		}
	}

	private synchronized void loadNetworkMap() throws NGOMException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(Resource.getMapFile()));
			Element root = doc.getDocumentElement();
			NodeList nodes = root.getElementsByTagName(NetworkMap.MAP);
			_mapList = new NetworkMapList();
			Vector<NetworkMap> maps = _mapList.getMaps();
			for(int i = 0; i < nodes.getLength(); i++) {
				maps.add(new NetworkMap(nodes.item(i)));
			}
		} catch (Exception e) {
			throw new NGOMException(e);
		}
	}

	////////////////////////////////////////////////////////////////////////
    //  DEVICE OPERATION
    ////////////////////////////////////////////////////////////////////////
    /**
     * add device to the management list
     * 
     * @param host      host address
     * @param port      SNMP port
     * @param community community string
     * @param netmask   netmask
     * @param descr     description
     *
     * @return status
     */
    synchronized int addDevice(String host, int port, String community, String netmask,  String descr) throws NGOMException {
        int status = getDeviceList().addDevice(host, port, community, netmask, descr);
        
        if (status == Constants.OK) {
            /*
             * TODO: device를 add할 때에는 그 device의 type을 체크해야 한다.
             * Type 체크하는 방법은?
             * 1. DeviceSample을 얻어서 그것의 System Description
             */
        	Device device = getDeviceList().getDeviceByHost(host);
        	DeviceSample sample = getDeviceSample(host);
        	if (sample != null) {
            	device.setType(sample);
        	}
        	/*
        	 * TODO: device type이 switch, router이면 모든 interface들을 관리 리스트에 추가한다.
        	 */
        	if (device.getType() == Device.TYPE_SWITCH ||
        			device.getType() == Device.TYPE_ROUTER) {
        		String[] ifList = getAvailableInterfaces(host);
        		for (int i = 0; i < ifList.length; i++) {
        			addInterface(host, ifList[i], ifList[i],
        					Conf.DEFAULT_SAMPLING_INTERVAL);
        		}
        	}
            save();
        }
        return status;
    }

    /**
     * update device to the manaement list
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
    synchronized int updateDevice(String host, int port, String community, 
            String netmask, String descr, boolean active) throws NGOMException {
        int status = getDeviceList().updateDevice(host, port, community, netmask, descr, active);
        if (status == Constants.OK) {
            save();
        }
        return status;
   }

    /**
     * remove device from the manaement list
     * 
     * @param host      host address
     *
     * @return status
     */
    synchronized int removeDevice(String host) throws NGOMException {
        int status = getDeviceList().removeDevice(host);
        if (status == Constants.OK) {
            save();
        }
        return status;
    }

    /**
     * get all device list
     * 
     * @return Device list vector
     */
    public Vector<Hashtable> getDevices() { 
        Vector<Device> devices = _deviceList.getDevices();
        Vector<Hashtable> result = new Vector<Hashtable>();
        if (devices != null) {
            for (int i = 0; i < devices.size(); i++) {
                result.add(devices.get(i).getDeviceInfo());
            }
        }
        return result;
    }
    
    /**
     * get device sample using SNMP
     *
     * @param host host address
     */
    synchronized Vector getDeviceSampleVector(String host) throws NGOMException {
    	return getDeviceSample(host).getInfo();
    }
    
    /**
     * get device sample using SNMP
     *
     * @param host host address
     */
    synchronized DeviceSample getDeviceSample(String host) throws NGOMException {
        Device device = _deviceList.getDeviceByHost(host);
        if (device == null) {
            return null;
        }
        SnmpReader reader = null;
        try {
            reader = new SnmpReader(device.getHost(), device.getPort(), device.getCommunity());
            String[] oids = new String[] {
                "sysDescr",
                "sysObjectID",
                "sysUpTime",
                "sysContact",
                "sysName",
                "sysLocation",
                "sysServices",
                "avg5util",
                "prevmemory",
                "aftermemory"};
            String[] values = reader.get(oids);
            DeviceSample sample = createDeviceSample(values);
            sample.setHost(device.getHost());
            return sample;
        } catch (IOException ex) {
            throw new NGOMException(ex);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
 
   
    private DeviceSample createDeviceSample(String[] values) {
		DeviceSample sample = new DeviceSample();
        int i = 0;
		if(values[i] != null) {
			sample.setSysDescr(values[i++]);
		}
		if(values[i] != null) {
			sample.setSysObjectID(values[i++]);
		}
		if (values[i] != null) {
			sample.setSysUpTime(Long.parseLong(values[i++]));
		}
		if(values[i] != null) {
			sample.setSysContact(values[i++]);
		}
		if(values[i] != null) {
			sample.setSysName(values[i++]);
		}
		if(values[i] != null) {
			sample.setSysLocation(values[i++]);
		}
        if (values[i] != null) {
			sample.setSysServices(Integer.parseInt(values[i++]));
		}
        if (values[i] != null) {
			sample.setCpuUtil(Integer.parseInt(values[i++]));
		}
        if (values[i] != null) {
        	Long previous = Long.parseLong(values[i++]);
        	Long after = Long.parseLong(values[i++]);
        	sample.setMemoryUtil((int)(previous*100/(previous+after)));
		}
        return sample;
	}


    /**
     * get available Interfaces from given host
     * 
     * @param host      host address
     *
     * @return available links
     */
    synchronized String[] getAvailableInterfaces(String host) throws NGOMException {
        Device device = _deviceList.getDeviceByHost(host);
        if (device == null) {
            return null;
        }
        SnmpReader reader = null;
        try {
            reader = new SnmpReader(device.getHost(), device.getPort(), device.getCommunity());
            Map interfaces = reader.walkIfDescr();
            return (String[]) interfaces.values().toArray(new String[0]);
        } catch (IOException ex) {
            throw new NGOMException(ex);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * get available interfaces alias list from given host
     * 
     * @param host      host address
     *
     * @return available link alias list
     */
    synchronized String[] getAvailableInterfacesAlias(String host) throws NGOMException {
        Device device = _deviceList.getDeviceByHost(host);
        if (device == null) {
            return null;
        }
        SnmpReader reader = null;
        try {
            reader = new SnmpReader(device.getHost(), device.getPort(), device.getCommunity());
            Map interfaces = reader.walk("ifAlias");
            return (String[]) interfaces.values().toArray(new String[0]);
        } catch (IOException ex) {
            throw new NGOMException(ex);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
   
    ////////////////////////////////////////////////////////////////////////
    //  INTERFACE OPERATION
    ////////////////////////////////////////////////////////////////////////
    /**
     * add interface to the management list
     * 
     * @param host      host address
     * @param ifDescr   interface description
     * @param descr     description
     * @param interval  sampling interval
     *
     * @return status
     */
    synchronized int addInterface(String host, String ifDescr, String descr, int interval) throws NGOMException {
        int status = getDeviceList().addInterface(host, ifDescr, descr, interval);
        if (status == Constants.OK) {
            save();
        }
        return status;
    }

     /**
     * update Interface to the management list
     * 
     * @param host      host address
     * @param ifDescr   interface description
     * @param descr     description
     * @param interval  sampling interval (seconds)
     * @param active    active status flag
     *
     * @return status
     */
    synchronized int updateInterface(String host, String ifDescr, String descr, 
            int interval, boolean active) throws NGOMException {
        int status = getDeviceList().updateInterface(host, ifDescr, descr,
                interval, active);
        if (status == Constants.OK) {
            save();
        }
        return status;
    }

    /**
     * remove interface from the management list
     * 
     * @param host      host address
     * @param ifDescr   interface description
     *
     * @return status
     */
    synchronized int removeInterface(String host, String ifDescr) throws NGOMException {
        int status = getDeviceList().removeInterface(host, ifDescr);
        
        if (status == Constants.OK) {
            save();
        }
        return status;
    }
    
    /**
     * get device sample using SNMP
     *
     * @param host host address
     */
     synchronized Vector getInterfaceSampleVector(String host, String ifDescr) 
        throws NGOMException {
    	 return getInterfaceSample(host, ifDescr).getInfo();
    }
 
    /**
     * get device sample using SNMP
     *
     * @param host host address
     */
     synchronized InterfaceSample getInterfaceSample(String host, String ifDescr) 
        throws NGOMException {
        Device device = _deviceList.getDeviceByHost(host);
        if (device == null) {
            return null;
        }
        Interface in = device.getInterfaceByIfDescr(ifDescr);
        if (in == null) {
            return null;
        }

        SnmpReader reader = null;
        try {
            reader = new SnmpReader(device.getHost(), device.getPort(), device.getCommunity());
            int ix = in.getIfIndex();
            String[] oids = new String[] { // OIDS to catch
                    "sysUpTime", 
                    "ifDescr." + ix, 
                    "ifType." + ix, 
                    "ifMtu." +ix, 
                    "ifSpeed." +ix, 
                    "ifPhyAddress." + ix, 
                    "ifAdminStatus." + ix, 
                    "ifOperStatus." + ix, 
                    "ifLastChange." + ix, 
                    "ifInOctets." + ix, 
                    "ifInUcastPkts." + ix, 
                    "ifInNUcastPkts." + ix, 
                    "ifInDiscards." + ix, 
                    "ifInErrors." + ix, 
                    "ifInUnknownProtos." + ix, 
                    "ifOutOctets." + ix, 
                    "ifOutUcastPkts." + ix, 
                    "ifOutNUcastPkts." + ix, 
                    "ifOutDiscards." + ix, 
                    "ifOutErrors." + ix, 
                    "ifOutDiscards." +ix, 
                    "ifOutQLen." +ix, 
                    "ifSpecific." +ix
            };
            String[] values = reader.get(oids);
            InterfaceSample sample = createInterfaceSample(values);
            sample.setHost(device.getHost());
            return sample;
        } catch (IOException ex) {
            throw new NGOMException(ex);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

 	private InterfaceSample createInterfaceSample(String[] values) {
		InterfaceSample sample = new InterfaceSample();
        int i = 0;
		if(values[i] != null) {
			sample.setSysUpTime(Long.parseLong(values[i++]));
		}
		if(values[i] != null) {
			sample.setIfDescr(values[i++]);
		}
		if(values[i] != null) {
			sample.setIfType(Integer.parseInt(values[i++]));
		}
		if(values[i] != null) {
			sample.setIfMtu(Integer.parseInt(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfSpeed(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfPhyAddress(values[i++]);
		}
		if (values[i] != null) {
			sample.setIfAdminStatus(Integer.parseInt(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOperStatus(Integer.parseInt(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfLastChange(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfInOctets(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfInUcastPkts(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfInNUcastPkts(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfInDiscards(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfInErrors(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfInUnknownProtos(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOutOctets(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOutUcastPkts(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOutNUcastPkts(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOutDiscards(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOutErrors(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfOutQLen(Long.parseLong(values[i++]));
		}
		if (values[i] != null) {
			sample.setIfSpecific(Integer.parseInt(values[i++]));
		}
        return sample;
	}


    ////////////////////////////////////////////////////////////////////////
    //  MAP OPERATION
    ////////////////////////////////////////////////////////////////////////
    /**
     * add map to the management list
     * 
     * @param name      map name
     * @param descr     map description
     * @param width     map width
     * @param height    map height
     *
     * @return status
     */
    synchronized int addMap(String name, String descr, int width, int height) throws NGOMException {
        int status = getMapList().addMap(name, descr, width, height);
        if (status == Constants.OK) {
            saveNetworkMap();
        }
        return status;
    }

    /**
     * update map to the management list
     * 
     * @param name      map name
     * @param descr     map description
     * @param width     map width
     * @param height    map height
     *
     * @return status
     */
    synchronized int updateMap(String name, String descr, int width, int height) throws NGOMException {
        int status = getMapList().updateMap(name, descr, width, height);
        if (status == Constants.OK) {
            saveNetworkMap();
        }
        return status;
    }
    
    /**
     * update map background to the management list
     * 
     * @param name      map name
     * @param background map background
     *
     * @return status
     */
    synchronized int updateMapBackground(String name, String background) throws NGOMException {
        int status = getMapList().updateMapBackground(name, background);
        if (status == Constants.OK) {
            saveNetworkMap();
        }
        return status;
    }

 
    /**
     * remove map from the management list
     * 
     * @param name      map name
     *
     * @return status
     */
    synchronized int removeMap(String name) throws NGOMException {
        int status = getMapList().removeMap(name);
        if (status == Constants.OK) {
            saveNetworkMap();
        }
        return status;
    }

    /**
     * get all map list
     * 
     * @return all map list
     */
    synchronized Vector<Hashtable> getMaps() {
        Vector<Hashtable> result = new Vector<Hashtable>();
        if (getMapList() == null) {
        	return result;
        }
        Vector<NetworkMap> maps = getMapList().getMaps();
        if (maps != null) {
            for (int i = 0; i < maps.size(); i++) {
                result.add(maps.get(i).getMapInfo());
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////
    //  MAP DEVICE OPERATION
    ////////////////////////////////////////////////////////////////////////
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
    synchronized int addDeviceMap(String name, String host, int xpos, int ypos, 
            int icon) throws NGOMException {
        int status = getMapList().addDevice(name, host, xpos, 
                ypos, icon);
        if (status == Constants.OK) {
            saveNetworkMap();
        }
        return status;
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
    synchronized int updateDeviceMap(String name, String host, int xpos, int ypos, int icon) throws NGOMException {
        int status = getMapList().updateDevice(name, host, xpos, 
                ypos, icon);
        if (status == Constants.OK) {
            saveNetworkMap();
        }
        return status;
    }

    /**
     * remove device from the given map
     * 
     * @param name      map name
     * @param host      host address
     *
     * @return status
     */
    synchronized int removeDeviceMap(String name, String host) throws NGOMException {
        int status = getMapList().removeDevice(name, host);
        if (status == Constants.OK) {
            saveNetworkMap();
        }
        return status;
    }

    /**
     * get devices from the given map
     * 
     * @param name      map name
     *
     * @return status
     */
    synchronized Vector<Hashtable> getDevicesOfMap(String name) {
        Vector<Hashtable> result = new Vector<Hashtable>();
        Vector<NetworkMapDevice> devices = 
            getMapList().getDevicesOfMap(name);
        if (devices != null) {
            for (int i = 0; i < devices.size(); i++) {
                result.add(devices.get(i).getDeviceInfo());
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////
    //  MAP LINK OPERATION
    ////////////////////////////////////////////////////////////////////////
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
    synchronized int addLinkMap(String name, String ifDescr, int kind, String src, 
            String dst) throws NGOMException {
        int status = getMapList().addLink(name, ifDescr, kind, 
                src, dst);
        if (status == Constants.OK) {
            saveNetworkMap();
        }
        return status;
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
    synchronized int updateLinkMap(String name, String ifDescr, int kind, String src,
            String dst) throws NGOMException {
        int status = getMapList().updateLink(name, ifDescr, 
                kind, src, dst);
        if (status == Constants.OK) {
            saveNetworkMap();
        }
        return status;
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
    synchronized int removeLinkMap(String name, String ifDescr, String src, String dst) throws NGOMException {
        int status = getMapList().removeLink(name, ifDescr, src, dst);
        if (status == Constants.OK) {
            saveNetworkMap();
        }
        return status;
    }

    /**
     * get links from the given map
     * 
     * @param name      map name
     *
     * @return status
     */
    synchronized Vector<Hashtable> getLinksOfMap(String name) {
        Vector<Hashtable> result = new Vector<Hashtable>();
        Vector<NetworkMapLink> links = 
            getMapList().getLinksOfMap(name);
        if (links != null) {
            for (int i = 0; i < links.size(); i++) {
                result.add(links.get(i).getLinkInfo());
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////
    //  GENERAL OPERATION
    ////////////////////////////////////////////////////////////////////////
    /**
     * update sampling interval
     *
     * @param interval sampling interval (seconds)
     *
     * @return status
     */
    synchronized int updateSamplingInterval(int interval) throws NGOMException {
        int status = getDeviceList().updateSamplingInterval(interval);
        if (status == Constants.OK) {
            save();
        }
        return status;
    }

    /**
     *  get png graph
     *
     *  @param host host address
     *  @param start    start date
     *  @param end      end date
     *
     *  @return png graph image (bytes)
     */
    synchronized byte[] getGraphAsPNG(String host, long start,  long end)  throws NGOMException {
    	Device device = _deviceList.getDeviceByHost(host);
    	if (device == null) {
    		return new byte[0];
    	}
        RrdPlotter grapher = new RrdPlotter(host, null, device.getDescr());
        return grapher.getDevicePngGraphBytes(start, end);
    }
    
    /**
     *  get png graph
     *
     *  @param host host address
     *  @param ifDescr  interface description
     *  @param start    start date
     *  @param end      end date
     *
     *  @return png graph image (bytes)
     */
    synchronized byte[] getGraphAsPNG(String host, String ifDescr, long start, 
            long end) throws NGOMException {
    	Device device = _deviceList.getDeviceByHost(host);
    	if (device == null) {
    		return new byte[0];
    	}
    	Interface in = device.getInterfaceByIfDescr(ifDescr);
    	if (in == null) {
    		return new byte[0];
    	}
    	
        RrdPlotter grapher = new RrdPlotter(host, ifDescr, in.getIfAlias());
        return grapher.getInterfacePngGraphBytes(start, end);
    }


    /**
     * get backend manager information
     *
     * @return backend manager information
     */
    synchronized Hashtable getBackendManagerInfo() {
        Hashtable hash =  BackendManager.getInstance().getInfo();
        return hash;
    }
}
