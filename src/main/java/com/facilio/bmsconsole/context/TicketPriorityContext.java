package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class TicketPriorityContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String displayName;
	public String getDisplayName() {
		if(displayName != null && !displayName.isEmpty()) {
			return displayName;
		}
		else {
			return priority;
		}
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	private String priority;
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	private int sequenceNumber;
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	private Boolean isDefault;
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	public boolean isDeleted() {
		if (isDefault != null) {
			return isDefault.booleanValue();
		}
		return false;
	}
	private String colour;
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	
	@Override
	public String toString() {
		return priority;
	}
}
