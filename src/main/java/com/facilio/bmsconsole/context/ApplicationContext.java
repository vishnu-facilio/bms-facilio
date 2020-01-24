package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.List;

public class ApplicationContext implements Serializable{
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

}
