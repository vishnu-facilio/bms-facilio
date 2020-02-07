package com.facilio.bmsconsole.context;

import org.apache.commons.lang3.StringUtils;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class PrecautionContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String description;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = StringUtils.upperCase(name);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
