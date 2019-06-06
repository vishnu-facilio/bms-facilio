package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class SpaceCategoryContext extends ModuleBaseWithCustomFields {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private Boolean commonArea;
	public Boolean getCommonArea() {
		return commonArea;
	}
	public void setCommonArea(boolean commonArea) {
		this.commonArea = commonArea;
	}
	public boolean isCommonArea() {
		if(commonArea != null) {
			return commonArea.booleanValue();
		}
		return false;
	}
	
}
