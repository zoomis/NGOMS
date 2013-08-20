package dpnm.netma.ngom.data;

public class NetworkMapDeviceInfo {
	private DeviceInfo deviceInfo;
	private int xpos;
	private int ypos;
	private int icon;

	public NetworkMapDeviceInfo() {
		
	}
	
	public String getDescr() {
		return getDeviceInfo().getDescr();
	}

	public void setDescr(String descr) {
        getDeviceInfo().setDescr(descr);
	}
	
	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	
	public String toString() {
		return deviceInfo.getHost();
	}

	public String getInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("DeviceInfo: " + getDeviceInfo() + "\n");
		buffer.append("xpos: " + getXpos() + "\n");
		buffer.append("ypos: " + getYpos() + "\n");

		return buffer.toString();
	}
}
