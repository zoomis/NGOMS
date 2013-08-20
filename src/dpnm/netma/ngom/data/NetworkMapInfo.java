/*
 * @(#)NetworkMapInfo.java
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
package dpnm.netma.ngom.data;

import dpnm.netma.ngom.data.*;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/**
 * This class is for NetworkMap
 *
 * @author Eric Kang
 * @since 2005/12/16
 * @version $Revision: 1.1 $
 */
public class NetworkMapInfo {
	private String name = "";
	private String descr = "";
	private int width;
	private int height;
    private String background = "";

	private NetworkMapDeviceInfo devices[];
	private NetworkMapLinkInfo links[];


	public NetworkMapInfo() { 
        super();
    }

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public NetworkMapLinkInfo[] getLinks() {
		return links;
	}

	public void setLinks(NetworkMapLinkInfo[] links) {
		this.links = links;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NetworkMapDeviceInfo[] getDevices() {
		return devices;
	}

	public void setDevices(NetworkMapDeviceInfo[] devices) {
		this.devices = devices;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

    public String getBackground() {
        return this.background;
    }

    public void setBackground(String background) {
        this.background = background;
    }
	
	public String toString() {
		return name;
	}

	public String getInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Name: " + getName() + "\n");
		buffer.append("Description: " + getDescr() + "\n");
		buffer.append("width: " + getWidth() + "\n");
		buffer.append("height: " + getHeight() + "\n");
		
		for (int i = 0; i < devices.length; i++) {
			buffer.append(devices[i].getInfo());
		}
		for (int i = 0; i < links.length; i++) {
			buffer.append(links[i].getInfo());
		}
		return buffer.toString();
	}
}
