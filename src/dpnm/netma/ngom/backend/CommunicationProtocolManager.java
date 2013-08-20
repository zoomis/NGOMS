/*
 * @(#)CommunicationProtocolManager.java
 * 
 * Created on 2005. 12. 15
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
import dpnm.netma.ngom.comm.xmlrpc.XMLRPCServer;
/**
 * Backend manager monitors devices
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */ 
class CommunicationProtocolManager {
	private static CommunicationProtocolManager _instance;

    /*  XML Rpc Server class */
    private XMLRPCServer            _rpcServer = null;

	public synchronized static CommunicationProtocolManager getInstance() {
		if (_instance == null) {
			_instance = new CommunicationProtocolManager();
		}
		return _instance;
	}

    CommunicationProtocolManager() {
    }

    void startXMLRPC(int port, String name, Object handler) {
    	if (Conf.DEBUG) {
    		Logger.getInstance().logBackend("backend.CommunicationProtocolManager", 
    				"XML RPC Server is created.");
    	}
        //  initialize rpc server
        _rpcServer = new XMLRPCServer(port,
                                     name,
                                     handler);
        _rpcServer.start();
        System.out.println("XML RPC Server is started. (port="+port+", name="+name+")");
    }

    void stopXMLRPC() {
    	if (_rpcServer != null) {
    		_rpcServer.terminate();
    	}
    }
}
