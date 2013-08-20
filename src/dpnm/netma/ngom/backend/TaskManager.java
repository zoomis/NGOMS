/*
 * @(#)TaskManager.java
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

/**
 * TaskScheduler class is scheduing task
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
class TaskManager {
    TaskScheduler   _scheduler;

    TaskManager(DataManager dataManager) {
        _scheduler = new TaskScheduler();
        _scheduler.addTask(new InterfacePollingTask(dataManager));
        _scheduler.addTask(new DevicePollingTask(dataManager));
    }

    void start() {
        _scheduler.start();
    }
}
