/*
 * @(#)SnmpInterfacePoller.java
 * 
 * Created on 2008. 02. 19
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
import dpnm.netma.ngom.NGOMException;
import dpnm.netma.ngom.util.Logger;
import dpnm.netma.ngom.comm.snmp.SnmpReader;

import dpnm.netma.ngom.data.*;

import java.io.IOException;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * SnmpPoller is for polling Snmp data from devices
 *
 * @author Eric Kang
 * @since 2008/02/19
 * @version $Revision: 1.1 $
 */
public class SnmpDevicePoller extends Thread {
	static final int RECONFIGURE_RETRIES = 3;

	private Device device;

	private SnmpReader reader;

	public SnmpDevicePoller(Device device) {
		setDaemon(true);
		this.device = device;
	}

	public void run() {
		try {
			reader = new SnmpReader(device.getHost(), device.getPort(), device.getCommunity());
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("SnmpDevicePoller", 
			    "Sampling: " + device.getHost());
            }
            reader = new SnmpReader(device.getHost(), device.getPort(), device.getCommunity());
            String[] oids = new String[] {
                "avg5util",
                "prevmemory",
                "aftermemory"};
			String[] values = reader.get(oids);
			DeviceSample sample = createDeviceSample(values);
            BackendManager.getInstance().getRrdWriter().store(sample);
			device.processSample(sample);
		}
		catch (IOException e) {
            if (Conf.DEBUG) {
                Logger.getInstance().logBackend("SnmpManager", 
				    "IOException on " + getLabel() + ": " + e);
			}
		} finally {
			if(reader != null) {
				reader.close();
			}
		}
	}


	private DeviceSample createDeviceSample(String[] values) {
		DeviceSample sample = new DeviceSample();
		sample.setHost(device.getHost());
        int i = 0;
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

	String getLabel() {
		return device.getHost();
	}
}
