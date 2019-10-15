package com.facilio.bmsconsole.context;

import java.io.Serializable;

public class ConnectedDeviceContext implements Serializable{
	private static final long serialVersionUID = 1L;
	long id;
	long deviceId;
	long orgId;
	long sessionStartTime;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(long deviceId) {
		this.deviceId = deviceId;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getSessionStartTime() {
		return sessionStartTime;
	}
	public void setSessionStartTime(long sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}
}
