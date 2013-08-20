/*
 * @(#)FrontCommHandler.java
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
import dpnm.netma.ngom.data.*;

import java.util.Vector;
import java.util.Date;
import java.util.Hashtable;

/**
 * DataManager manages all data of backend server.
 *
 * @author Eric Kang
 * @since 2008/02/18
 * @version $Revision: 1.1 $
 */
public class FrontCommHandler {
    private DataManager _dataManager;

    public FrontCommHandler() {
    }
    
    void setDataManager(DataManager dataManager) {
        this._dataManager = dataManager;
    }

    /* ---------------------------------------------------------------
     *  HANDLER IMPLEMENTATION OF XML RPC
     *
     *  A. Device operation
     *      1. addDevice
     *      2. updateDevice
     *      3. removeDevice
     *      4. getDevices
     *      5. getDeviceSample
     *      6. getAvailableInterfaces
     *      7. getAvailableInterfacesAlias
     *
     *  B. Interface operation
     *      1. addInterface
     *      2. updateInterface
     *      3. removeInterface
     *      4. getInterfaceSample
     *
     *  C. Map operation
     *      1. addMap
     *      2. updateMap
     *      3. updateMapBackground
     *      4. removeMap
     *      5. getMaps
     *
     *  D. Map Device operation
     *      1. addDeviceMap
     *      2. updateDeviceMap
     *      3. remoeDeviceMap
     *      4. getDevicesOfMap
     *
     *  E. Map Link operation
     *      1. addLinkMap
     *      2. updateLinkMap
     *      3. removeLinkMap
     *      4. getLinksOfMap
     *
     *  F. General operation
     *      1. updateSamplingInterval
     *      2. getPngGraph
     *      3. getBackendManagerInfo
     *      4. getManagementInfo
     * --------------------------------------------------------------- */
    ////////////////////////////////////////////////////////////////////////
    //  DEVICE OPERATION
    ////////////////////////////////////////////////////////////////////////
    /**
     * add device to the manaement list
     * 
     * @param host      host address
     * @param port      SNMP port
     * @param community community string
     * @param netmask   netmask
     * @param descr     description
     *
     * @return status
     */
    public int addDevice(String host, int port, String community, String netmask,  String descr) {
        try {
			int status = _dataManager.addDevice(host, port, community, netmask, descr);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			            "Device " + host + " added [" + status + "]");
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
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
    public int updateDevice(String host, int port, String community, String netmask, 
            String descr, boolean active) {
        try {
			int status = _dataManager.updateDevice(host, port, community, netmask, descr, active);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			            "Device " + host + " updated [" + status + "]");
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
   }

    /**
     * remove device from the manaement list
     * 
     * @param host      host address
     *
     * @return status
     */
    public int removeDevice(String host) {
        try {
			int status = _dataManager.removeDevice(host);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			                "Device " + host + " removed [" + status + "]");
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
    }

    /**
     * get all device list
     * 
     * @return Device list vector
     */
    public Vector getDevices() {
        Vector result = _dataManager.getDevices();
        if (Conf.DEBUG) {
           Logger.getInstance().logBackend("FrontCommManager", 
                       "Sending devices data [" + 
                       (result == null ? 0 : result.size()) + " devices found]");
        }
        return result == null ? new Vector() : result;
    }

    /**
     * get device sample using SNMP
     *
     * @param host host address
     */
    public Vector getDeviceSample(String host) {
       Vector sample = null;
       try {
    	    sample = _dataManager.getDeviceSampleVector(host);
        } catch (NGOMException e) {
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("FrontCommManager", 
                    "Event handler error: " + e);
            }
 
        }
        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("FrontCommManager", 
                "Sending Device Sample");
        }
        return sample == null ? new Vector() : sample;
    }

    /**
     * get available Interfaces from given host
     * 
     * @param host      host address
     *
     * @return available links
     */
    public Vector getAvailableInterfaces(String host) {
        Vector result = new Vector();
        try {
            String[] links = _dataManager.getAvailableInterfaces(host);
            for (int i = 0; i < links.length; i++) {
                result.add(links[i]);
            }
        } catch (NGOMException e) {
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("FrontCommManager", 
                    "Event handler error: " + e);
            }
        }
        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("FrontCommManager", 
                result.size() + " interfaces found on " + host);
        }
        return result;
    }

    /**
     * get available interfaces alias list from given host
     * 
     * @param host      host address
     *
     * @return available link alias list
     */
    public Vector getAvailableInterfacesAlias(String host) {
        Vector result = new Vector();
       try {
            String[] alias = _dataManager.getAvailableInterfacesAlias(host);
            for (int i = 0; i < alias.length; i++) {
                result.add(alias[i]);
            }
            
        } catch (NGOMException e) {
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("FrontCommManager", 
                    "Event handler error: " + e);
            }
 
        }
        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("FrontCommManager", 
                result.size() + " interfaces found on " + host);
        }
        return result;
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
    public int addInterface(String host, String ifDescr, String descr, int interval) {
        try {
			int status = _dataManager.addInterface(host, ifDescr, descr, interval);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			            "Interface " + ifDescr + "@" + host + " added [" + status + "]");
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
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
    public int updateInterface(String host, String ifDescr, String descr, 
            int interval, boolean active) {
        try {
			int status = _dataManager.updateInterface(host, ifDescr, descr,
			        interval, active);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			        "Interface " + ifDescr + "@" + host + " updated [" + status + "]");
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
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
        try {
			int status = _dataManager.removeInterface(host, ifDescr);
			
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			            "Interface " + ifDescr + "@" + host + " removed [" + status + "]");
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
    }

    /**
     * get device sample using SNMP
     *
     * @param host host address
     */
    public Vector getInterfaceSample(String host, String ifDescr) {
       Vector sample = null;
       try {
    	    sample = _dataManager.getInterfaceSampleVector(host, ifDescr);
        } catch (NGOMException e) {
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("FrontCommManager", 
                    "Event handler error: " + e);
            }
 
        }
        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("FrontCommManager", 
                "Sending Interface Sample");
        }
        return sample == null ? new Vector() : sample;
 
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
    public int addMap(String name, String descr, int width, int height) {
        try {
			int status = _dataManager.addMap(name, descr, width, height);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			        "Map : " + name + " added [" + status + "]");
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
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
    public int updateMap(String name, String descr, int width, int height) {
        try {
			int status = _dataManager.updateMap(name, descr, width, height);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			            "Map : " + name + " updated [" + status + "]");
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
    }
    
    /**
     * update map background to the management list
     * 
     * @param name      map name
     * @param background map background
     *
     * @return status
     */
    public int updateMapBackground(String name, String background) {
        try {
			int status = _dataManager.updateMapBackground(name, background);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			            "Map : " + name + " updated [" + status + "]");
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
    }

 
    /**
     * remove map from the management list
     * 
     * @param name      map name
     *
     * @return status
     */
    public int removeMap(String name) {
        try {
			int status = _dataManager.removeMap(name);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			                "Map : " + name + " removed [" + status + "]");
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
    }

    /**
     * get all map list
     * 
     * @return all map list
     */
    public Vector getMaps() {
        Vector result = _dataManager.getMaps();
        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("FrontCommManager", 
                "Sending map data [" + 
                (result == null ? 0 : result.size()) + " maps found]");
        }

        return result == null ? new Vector() : result;
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
    public int addDeviceMap(String name, String host, int xpos, int ypos, 
            int icon) {
        try {
			int status = _dataManager.addDeviceMap(name, host, xpos, 
			        ypos, icon);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			        "Device : " + host + " added [" + status + "] to Map : " + name);
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
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
    public int updateDeviceMap(String name, String host, int xpos, int ypos, 
            int icon) {
        try {
			int status = _dataManager.updateDeviceMap(name, host, xpos, 
			        ypos, icon);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			        "Device : " + host + " updated [" + status + "] to Map : " 
			        + name);
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
    }

    /**
     * remove device from the given map
     * 
     * @param name      map name
     * @param host      host address
     *
     * @return status
     */
    public int removeDeviceMap(String name, String host) {
        try {
			int status = _dataManager.removeDeviceMap(name, host);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			        "Device : " + host + " removed [" + status + "] to Map : " + name);
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
    }

    /**
     * get devices from the given map
     * 
     * @param name      map name
     *
     * @return status
     */
    public Vector getDevicesOfMap(String name) {
        Vector result = _dataManager.getDevicesOfMap(name);
       if (Conf.DEBUG) {
            Logger.getInstance().logBackend("FrontCommManager", 
                (result == null ? 0 : result.size()) + " devices found on " + name);
        }
        return result == null ? new Vector() : result;
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
    public int addLinkMap(String name, String ifDescr, int kind, String src, 
            String dst) {
        try {
			int status = _dataManager.addLinkMap(name, ifDescr, kind, 
			        src, dst);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			        "Link : " + ifDescr + " added [" + status + "] to Map : " + 
			        name);
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
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
    public int updateLinkMap(String name, String ifDescr, int kind, String src,
            String dst) {
        try {
			int status = _dataManager.updateLinkMap(name, ifDescr, 
			        kind, src, dst);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			        "Link : " + ifDescr + " updated [" + status + "] to Map : " + name);
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
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
    public int removeLinkMap(String name, String ifDescr, String src, String dst) {
        try {
			int status = _dataManager.removeLinkMap(name, ifDescr, src, dst);
			if (Conf.DEBUG) {
			    Logger.getInstance().logBackend("FrontCommManager", 
			        "Link : " + ifDescr + " removed [" + status + "] to Map : " + name);
			}
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
    }

    /**
     * get links from the given map
     * 
     * @param name      map name
     *
     * @return status
     */
    public Vector getLinksOfMap(String name) {
        Vector result = _dataManager.getLinksOfMap(name);
        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("FrontCommManager", 
                (result == null ? 0 : result.size()) + " interfaces found on " + name);
        }
        return result == null ? new Vector() : result;
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
    public int updateSamplingInterval(int interval) {
        try {
			int status = _dataManager.updateSamplingInterval(interval);
	        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("FrontCommManager", 
                "Sampling interval is changed to " + interval);
            }
 		
			return status;
		} catch (NGOMException e) {
			if (Conf.DEBUG) {
				e.printStackTrace();
			}
			return Constants.EXCEPTION;
		}
    }

    /**
     *  get png graph
     *
     *  @param host host address
     *  @param startDate    start date
     *  @param endDate      end date
     *
     *  @return png graph image (bytes)
     */
    public byte[] getDeviceGraphAsPNG(String host, Date startDate,  Date endDate) {
        byte[] graph = new byte[0];
        long start = startDate.getTime() / 1000L;
        long end = endDate.getTime() / 1000L;
        try {
            graph = _dataManager.getGraphAsPNG(host, start, end);
         } catch (NGOMException e) {
             if (Conf.DEBUG) {
                 Logger.getInstance().logBackend("FrontCommManager", 
                     "Event handler error: " + e);
             }
  
         }
       if (Conf.DEBUG) {
            Logger.getInstance().logBackend("FrontCommManager", 
                "Graph for device " + host +
                " generated [" + graph.length + " bytes]");
       }
        return graph;
    }
    
    /**
     *  get png graph
     *
     *  @param host host address
     *  @param ifDescr  interface description
     *  @param startDate    start date
     *  @param endDate      end date
     *
     *  @return png graph image (bytes)
     */
    public byte[] getGraphAsPNG(String host, String ifDescr, Date startDate, 
            Date endDate) {
        byte[] graph = new byte[0];
        long start = startDate.getTime() / 1000L;
        long end = endDate.getTime() / 1000L;
        try {
            graph = _dataManager.getGraphAsPNG(host, ifDescr, start, end);
         } catch (NGOMException e) {
             if (Conf.DEBUG) {
                 Logger.getInstance().logBackend("FrontCommManager", 
                     "Event handler error: " + e);
             }
  
         }
       if (Conf.DEBUG) {
            Logger.getInstance().logBackend("FrontCommManager", 
                "Graph for interface " + ifDescr + "@" + host +
                " generated [" + graph.length + " bytes]");
       }
        return graph;
    }

    /**
     * get backend manager information
     *
     * @return backend manager information
     */
    public Hashtable getBackendManagerInfo() {
        Hashtable hash =  _dataManager.getBackendManagerInfo();
        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("FrontCommManager", 
                "Sending Backend Manager info");
        }
        return hash == null ? new Hashtable() : hash;
    }

    /**
     * get device and weather information
     *
     * @return network weather service information
     */
    public Hashtable getDataInfo() {
        Hashtable info = new Hashtable();
        info.put("backendInfo", getBackendManagerInfo());
        info.put("devices", getDevices());
        return info;
    }

    /**
     * get device and weather information
     *
     * @return network weather service information
     */
    public Hashtable getAllInfo() {
        Hashtable info = new Hashtable();
        info.put("backendInfo", getBackendManagerInfo());
        info.put("devices", getDevices());
        info.put("maps", getMaps());
        return info;
    }
}
