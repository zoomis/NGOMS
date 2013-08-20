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
class DevicePollingTask implements Task {
    private static final String TASK_NAME = "DevicePolling";
    private DataManager _dataManager;

    DevicePollingTask(DataManager dataManager) {
        _dataManager = dataManager;
    }

    public String getName() {
        return TASK_NAME;
    }

    public void run() {
        DeviceList deviceList = _dataManager.getDeviceList();
        if (deviceList == null)
        	return;
        Vector<Device> devices = deviceList.getDevices();
        if (devices == null)
        	return;
        for(int i = 0; i < devices.size(); i++) {
            Device device = devices.get(i);
            if(device.isActive()  && device.isDue()) {
                new SnmpDevicePoller(device).start();
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
