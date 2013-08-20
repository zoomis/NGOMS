/*
 * @(#)Listener.java
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
package dpnm.netma.ngom.comm.xmlrpc;

import dpnm.netma.ngom.Conf;
import dpnm.netma.ngom.util.Logger;
import dpnm.netma.ngom.NGOMException;

import org.apache.xmlrpc.WebServer;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

public class XMLRPCServer {
	private WebServer webServer;

	public XMLRPCServer(int port, String name, Object handler) {
        //  Web Server를 초기화시키고 XML RPC를 시작한다.
		webServer = new WebServer(port);
		webServer.addHandler(name, handler);

		if (Conf.DEBUG) {
			Logger.getInstance().logBackend("comm.xmlrpc.XMLRPCServer",
                    "XmlRpcServer started on port " + port);
		}
	}

    public void start() {
		webServer.start();
    }

	public void terminate() {
		if(webServer != null) {
			webServer.shutdown();
			if (Conf.DEBUG) {
    			Logger.getInstance().logBackend("comm.xmlrpc.XMLRPCServer",
                        "XmlRpcServer closed");
			}
			webServer = null;
		}
	}
}
