package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.List;

public class ApplicationContext implements Serializable{
	
	public ApplicationContext() {
		
	}
	
	public ApplicationContext(long orgId, String name, boolean isDefault, long appDomainId) {
		this.name = name;
		this.isDefault = isDefault;
		this.appDomainId = appDomainId;
		this.orgId = orgId;
	}
	
	private long orgId = -1;
	
	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private Boolean isDefault;
	
	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isDefault() {
		if (isDefault != null) {
			return isDefault.booleanValue();
		}
		return false;
	}

	private List<WebTabGroupContext> webTabGroups;
	public List<WebTabGroupContext> getWebTabGroups() {
		return webTabGroups;
	}
	public void setWebTabGroups(List<WebTabGroupContext> webTabGroups) {
		this.webTabGroups = webTabGroups;
	}

	private long appDomainId;
	public long getAppDomainId() {
		return appDomainId;
	}
	public void setAppDomainId(long appDomainId) {
		this.appDomainId = appDomainId;
	}
	
}
