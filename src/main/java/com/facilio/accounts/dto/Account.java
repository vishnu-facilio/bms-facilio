package com.facilio.accounts.dto;

import java.io.Serializable;

import com.facilio.screen.context.RemoteScreenContext;

public class Account implements Serializable {
	
	private Organization org;
	private User user;
	
	private String deviceType;
	private String appVersion;
	
	private RemoteScreenContext remoteScreen;
	
	public Account(Organization org, User user) {
		this.org = org;
		this.user = user;
	}
	
	public Organization getOrg() {
		return this.org;
	}
	
	public void setOrg(Organization org) {
		this.org = org;
	}
	
	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Boolean isFromMobile() {
		return isFromAndroid() || isFromIos();
	}

	public Boolean isFromIos() {
		return deviceType != null && deviceType.equalsIgnoreCase("ios");
	}

	public Boolean isFromAndroid() {
		return deviceType != null && deviceType.equalsIgnoreCase("android");
	}
	
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	public RemoteScreenContext getRemoteScreen() {
		return this.remoteScreen;
	}
	
	public void setRemoteScreen(RemoteScreenContext remoteScreen) {
		this.remoteScreen = remoteScreen;
	}
}
