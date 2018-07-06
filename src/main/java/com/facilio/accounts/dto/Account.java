package com.facilio.accounts.dto;

public class Account {
	
	private Organization org;
	private User user;
	
	private Boolean isFromIos = false;
	private Boolean isFromAndroid = false;
	private String appVersion;
	
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
		return isFromAndroid || isFromIos;
	}

	public Boolean getIsFromIos() {
		return isFromIos;
	}

	public void setIsFromIos(Boolean isFromIos) {
		this.isFromIos = isFromIos;
	}

	public Boolean getIsFromAndroid() {
		return isFromAndroid;
	}

	public void setIsFromAndroid(Boolean isFromAndroid) {
		this.isFromAndroid = isFromAndroid;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	
	
}
