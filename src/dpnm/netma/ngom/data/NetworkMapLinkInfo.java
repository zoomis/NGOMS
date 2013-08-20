package dpnm.netma.ngom.data;

public class NetworkMapLinkInfo {
	private InterfaceInfo interfaceInfo;
	private NetworkMapDeviceInfo src;
	private NetworkMapDeviceInfo dst;
    private int kind = 0;
	
	public NetworkMapLinkInfo() {
		
	}

	public NetworkMapDeviceInfo getDst() {
		return dst;
	}

	public void setDst(NetworkMapDeviceInfo dst) {
		this.dst= dst;
	}

	public InterfaceInfo getInterfaceInfo() {
		return interfaceInfo;
	}

	public void setInterfaceInfo(InterfaceInfo linkInfo) {
		this.interfaceInfo = linkInfo;
	}

	public NetworkMapDeviceInfo getSrc() {
		return src;
	}

	public void setSrc(NetworkMapDeviceInfo src) {
		this.src= src;
	}
	
	public String toString() {
		return interfaceInfo.getIfDescr();
	}

	public String getInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("InterfaceInfo: " + getInterfaceInfo() + "\n");
		buffer.append("SrcRouter: " + getSrc() + "\n");
		buffer.append("DstRouter: " + getDst() + "\n");

		return buffer.toString();
	}

    /**
     * @return Returns the kind.
     */
    public int getKind() {
        return kind;
    }

    /**
     * @param kind The kind to set.
     */
    public void setKind(int kind) {
        this.kind = kind;
    }
}
