package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class StoreRoomContext extends ModuleBaseWithCustomFields {
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

	private LocationContext location;

	public LocationContext getLocation() {
		return location;
	}

	public void setLocation(LocationContext location) {
		this.location = location;
	}

	public long getLocationId() {
		// TODO Auto-generated method stub
		if (location != null) {
			return location.getId();
		}
		return -1;
	}

	private User owner;

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	private long ttime, modifiedTime;

	public long getTtime() {
		return ttime;
	}

	public void setTtime(long ttime) {
		this.ttime = ttime;
	}

	public long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public long noOfItemTypes;

	public long getNoOfItemTypes() {
		return noOfItemTypes;
	}

	public void setNoOfItemTypes(long noOfItemTypes) {
		this.noOfItemTypes = noOfItemTypes;
	}

	public long noOfToolTypes;

	public long getNoOfToolTypes() {
		return noOfToolTypes;
	}

	public void setNoOfToolTypes(long noOfToolTypes) {
		this.noOfToolTypes = noOfToolTypes;
	}
	
	private Boolean isApprovalNeeded;

	public Boolean getIsApprovalNeeded() {
		return isApprovalNeeded;
	}

	public void setIsApprovalNeeded(Boolean isApprovalNeeded) {
		this.isApprovalNeeded = isApprovalNeeded;
	}

	public boolean isApprovalNeeded() {
		if (isApprovalNeeded != null) {
			return isApprovalNeeded.booleanValue();
		}
		return false;
	}

}
