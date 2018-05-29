package com.facilio.bmsconsole.context;

import com.facilio.time.SecondsChronoUnit;

public class ControllerContext {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String macAddr;
	public String getMacAddr() {
		return macAddr;
	}
	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}
	
	private long dataInterval = -1; //In minutes
	public long getDataInterval() {
		return dataInterval;
	}
	public void setDataInterval(long dataInterval) {
		this.dataInterval = dataInterval;
		this.dateIntervalUnit = new SecondsChronoUnit(dataInterval * 60);
	}
	
	private SecondsChronoUnit dateIntervalUnit;
	public SecondsChronoUnit getDateIntervalUnit() {
		return dateIntervalUnit;
	}
	
	@Override
	 public String toString() {
		return "ControllerSettingsContext [id=" + id + ", orgId=" + orgId + ", name=" + name + ","
				+ " ,macAddr=" + macAddr +"]";
	}
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
