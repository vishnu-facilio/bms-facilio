package com.facilio.bmsconsole.context;

import java.io.Serializable;

public class TabIdAppIdMappingContext implements Serializable{
	
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long tabId;
	public long getTabId() {
		return tabId;
	}
	public void setTabId(long tabId) {
		this.tabId = tabId;
	}
	
	private long moduleId;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private long appId;
	public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}

	private String specialType;

	public String getSpecialType() {
		return specialType;
	}

	public void setSpecialType(String specialType) {
		this.specialType = specialType;
	}

	public TabIdAppIdMappingContext(long tabId, long moduleId, long appId) {
		super();
		this.tabId = tabId;
		this.moduleId = moduleId;
		this.appId = appId;
	}
	public TabIdAppIdMappingContext() {
		super();
	}

	public TabIdAppIdMappingContext(long tabId,long appId, String specialType) {
		super();
		this.tabId = tabId;
		this.appId = appId;
		this.specialType = specialType;
	}
}
