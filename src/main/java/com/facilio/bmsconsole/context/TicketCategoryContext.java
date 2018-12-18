package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class TicketCategoryContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private String displayName;
	public String getDisplayName() {
		if(displayName != null && !displayName.isEmpty()) {
			return displayName;
		}
		else {
			return name;
		}
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
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
	
	@Override
	public String toString() {
		return name;
	}
}
