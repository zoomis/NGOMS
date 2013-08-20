package dpnm.netma.ngom.data;

import java.util.Date;

public class BackendInfo implements TreeElementInfo {
	private String serverHost;
	private long sampleCount, savesCount, goodSavesCount, badSavesCount;
	private double poolEfficency;
	private Date startDate;

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public long getSampleCount() {
		return sampleCount;
	}

	public void setSampleCount(long sampleCount) {
		this.sampleCount = sampleCount;
	}

	public long getSavesCount() {
		return savesCount;
	}

	public void setSavesCount(long savesCount) {
		this.savesCount = savesCount;
	}

	public long getGoodSavesCount() {
		return goodSavesCount;
	}

	public void setGoodSavesCount(long goodSavesCount) {
		this.goodSavesCount = goodSavesCount;
	}

	public long getBadSavesCount() {
		return badSavesCount;
	}

	public void setBadSavesCount(long badSavesCount) {
		this.badSavesCount = badSavesCount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String toString() {
		return getServerHost() + " [right click to reload]";
	}

	public String getInfo() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Server host: " + getServerHost() + "\n");
		buffer.append("Server started on: " + getStartDate() + "\n");
		buffer.append("Samples created: " + getSampleCount() + "\n");
		buffer.append("Total samples processed: " + getSavesCount() + "\n");
		buffer.append("Samples stored OK: " + getGoodSavesCount() + "\n");
		buffer.append("Samples not stored: " + getBadSavesCount() + "\n");
		buffer.append("Pool efficency: " + getPoolEfficency() + "\n");
		return buffer.toString();
	}

	public boolean equals(Object obj) {
		return obj instanceof BackendInfo;
	}

	public double getPoolEfficency() {
		return poolEfficency;
	}

	public void setPoolEfficency(double poolEfficency) {
		this.poolEfficency = poolEfficency;
	}

}
