/*
 * @(#)Conf.java
 * 
 * Created on 2005. 12. 14
 *
 *	This software is the confidential and proprietary informatioon of
 *	POSTECH DP&NM. ("Confidential Information"). You shall not
 *	disclose such Confidential Information and shall use it only in
 *	accordance with the terms of the license agreement you entered into
 *	with Eric Kang.
 *
 *	Contact: Eric Kang at eliot@postech.edu
 */
package dpnm.netma.ngom;

/**
 * Class Instruction
 *
 * @author Eric Kang
 * @since 2008/02/14
 * @version $Revision: 1.1 $
 */

/*
 * TODO: Configuration 도 사용자에 의해서 설정이 가능하게 해야 하고, 이를
 * XML에 저장하여 이용할 수 있도록 수정이 되어야 한다.
 *
 * 처음 Backend를 실행할 시에 이 confiruation을 xml로부터 읽어올 수 있어야
 * 한다. 
 * 그런데 전체 Configuration이 아니라 Backend part의 Configuration만을
 * 이렇게 해야 하지 않을까??
 */
public final class Conf {
	// turn debugging on/off
	public static final boolean DEBUG = false;

	// XML-RPC port to listen on
	public static final int SERVER_PORT = 7070;

    // XML-RPC handler name
    public static final String HANDLER_NAME = "ngom";

	// run Scheduler each 5 seconds
	public static final int SCHEDULER_RESOLUTION = 5;

	// pause between poller threads in milliseconds
	public static final int SCHEDULER_DELAY = 20;

    // backend factory to be used
	// WARNING: Change this to "FILE" if you run the Server app
	// on a machine with less RAM. "NIO" consumes a lot of memory
	// but it's fast, very fast.
	public static final String BACKEND_FACTORY_NAME = "NIO";

    //  default sampling interval
    public static final int DEFAULT_SAMPLING_INTERVAL = 300; // seconds

    // number of open RRD files held in the pool
	public static final int POOL_CAPACITY = 500;

    //  default snmp port
    public static final int DEFAULT_SNMPPORT = 161;
    
	// graph dimensions
	public static final int GRAPH_WIDTH = 582;
	public static final int GRAPH_HEIGHT = 350;
	
	public static final String DEFAULT_HOST = "141.223.";
	
	// should we remove a RRD file if link is removed from the client
	public static final boolean REMOVE_RRD_FOR_DEACTIVATED_LINK = true;


    //  XML RPC function list
    //  DEVICE OPERATION
    public static final String ADD_DEVICE    = HANDLER_NAME+".addDevice";
    public static final String UPDATE_DEVICE = HANDLER_NAME+".updateDevice";
    public static final String REMOVE_DEVICE = HANDLER_NAME+".removeDevice";
    public static final String GET_DEVICES   = HANDLER_NAME+".getDevices";
    public static final String GET_DEVICE_SAMPLE   = HANDLER_NAME+".getDeviceSample";
    //  INTERFACE OPERATION
    public static final String ADD_INTERFACE = HANDLER_NAME+".addInterface";
    public static final String UPDATE_INTERFACE = HANDLER_NAME+".updateInterface";
    public static final String REMOVE_INTERFACE = HANDLER_NAME+".removeInterface";
    public static final String GET_AVAILABLE_INTERFACES = HANDLER_NAME+".getAvailableInterfaces";
    public static final String GET_AVAILABLE_INTERFACES_ALIAS = HANDLER_NAME+".getAvailableInterfacesAlias";
    public static final String GET_INTERFACE_SAMPLE   = HANDLER_NAME+".getInterfaceSample";
    //  MAP OPERATION
    public static final String ADD_MAP = HANDLER_NAME+".addMap";
    public static final String UPDATE_MAP = HANDLER_NAME+".updateMap";
    public static final String UPDATE_MAP_BACKGROUND = HANDLER_NAME+".updateMapBackground";
    public static final String REMOVE_MAP = HANDLER_NAME+".removeMap";
    public static final String GET_MAPS = HANDLER_NAME+".getMaps";
    //  MAP DEVICE OPERATION
    public static final String ADD_DEVICE_MAP = HANDLER_NAME+".addDeviceMap";
    public static final String UPDATE_DEVICE_MAP = HANDLER_NAME+".updateDeviceMap";
    public static final String REMOVE_DEVICE_MAP = HANDLER_NAME+".removeDeviceMap";
    public static final String GET_DEVICES_OF_MAP = HANDLER_NAME+".getDevicesOfMap";
    //  MAP INTERFACE OPERATION
    public static final String ADD_LINK_MAP = HANDLER_NAME+".addLinkMap";
    public static final String UPDATE_LINK_MAP = HANDLER_NAME+".updateLinkMap";
    public static final String REMOVE_LINK_MAP = HANDLER_NAME+".removeLinkMap";
    public static final String GET_LINKS_OF_MAP = HANDLER_NAME+".getLinksOfMap";
    //  GENERAL OPERAITON
    public static final String UPDATE_SAMPLING_INTERVAL = HANDLER_NAME+".updateSamplingInterval";
    public static final String GET_PNG_GRAPH = HANDLER_NAME+".getGraphAsPNG";
    public static final String GET_DEVICE_PNG_GRAPH = HANDLER_NAME+".getDeviceGraphAsPNG";
    public static final String GET_BACKEND_MANAGER_INFO = HANDLER_NAME+".getBackendManagerInfo";
    public static final String GET_DATA_INFO = HANDLER_NAME+".getDataInfo";
    public static final String GET_ALL_INFO = HANDLER_NAME+".getAllInfo";
}
