/*
 * @(#)InterfacePollingTask.java
 * 
 * Created on 2008. 02. 20
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

import dpnm.netma.ngom.Conf;
import dpnm.netma.ngom.data.*;
import dpnm.netma.ngom.comm.snmp.SnmpReader;
import dpnm.netma.ngom.util.Logger;

import java.util.Vector;
/**
 * Interface Polling Task
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
class InterfacePollingTask implements Task {
    private static final String TASK_NAME = "InterfacePolling";
    private DataManager _dataManager;

    InterfacePollingTask(DataManager dataManager) {
        _dataManager = dataManager;
    }

    public String getName() {
        return TASK_NAME;
    }

    public void run() {
//        if (Conf.DEBUG) {
//    		Logger.getInstance().logBackend("Task", 
//                    getName()+" run....");
//        }
        DeviceList deviceList = _dataManager.getDeviceList();
        if (deviceList == null)
        	return;
        Vector<Device> devices = deviceList.getDevices();
        if (devices == null)
        	return;
        for(int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            Vector<Interface> interfaces = device.getInterfaces();
            if (interfaces == null)
            	continue;
            for (int j = 0; j < interfaces.size(); j++) {
                Interface in = interfaces.get(j);
                if(device.isActive() && in.isActive() &&
                    in.isDue() && !in.isSampling()) {
                    new SnmpInterfacePoller(device, in).start();
                    try {
                        Thread.sleep((long)(1 + 
                                    Math.random() * Conf.SCHEDULER_DELAY));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
