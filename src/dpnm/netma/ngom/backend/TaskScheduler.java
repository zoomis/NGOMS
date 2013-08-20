/*
 * @(#)TaskScheduler.java
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
import dpnm.netma.ngom.util.Logger;
import java.util.Vector;

/**
 * TaskScheduler class is scheduing task
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
class TaskScheduler extends Thread {
	private volatile boolean active = true;

    private Vector<Task> tasks = null;
    private boolean waiting = true;

	TaskScheduler() {
	}

    void addTask(Task task) {
        if (tasks == null) {
            tasks = new Vector<Task>();
        }
        tasks.add(task);

        if (waiting) {
            synchronized(this) {
                waiting = false;
                notify();
            }
        }
    }

    void removeTask(Task task) {
        if (tasks == null) {
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            if (t.getName().intern() == task.getName().intern()) {
                tasks.remove(t);
            }
        }
        if (tasks.size() == 0) {
            tasks = null;
        }
    }

	public void run() {
        if (Conf.DEBUG) {
    		Logger.getInstance().logBackend("TaskScheduler", 
                    "Scheduler started");
        }
		while(active) {
            if (tasks == null) {
                synchronized(this) {
                    try {
                        if (Conf.DEBUG) {
                            Logger.getInstance().logBackend("TaskScheduler", 
                                    "Waiting...");
                        }
                        waiting = true;
                        wait();
                    } catch (Exception ex) {
                    }
                }
            }
            for (int i = 0; i < tasks.size(); i++) {
                tasks.get(i).run();
            }

            // sleep for a while
			synchronized(this) {
				try {
					wait(Conf.SCHEDULER_RESOLUTION * 1000L);
				}
				catch (InterruptedException e) {
				}
			}
		}
        if (Conf.DEBUG) {
            Logger.getInstance().logBackend("TaskScheduler", 
                    "Scheduler ended");
        }
	}

	void terminate() {
    	active = false;
		synchronized(this) {
			notify();
		}
	}
}
