package com.facilio.bmsconsole.context;

public class ControllerSettingsContext {
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
	
	private long spaceId = -1;
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	
	private String macAddr;
	public String getMacAddr() {
		return macAddr;
	}
	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}
	
	@Override
	 public String toString() {
		return "ControllerSettingsContext [id=" + id + ", orgId=" + orgId + ", name=" + name + ","
				+ " spaceId =" +spaceId+",macAddr=" + macAddr +"]";
	}
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
