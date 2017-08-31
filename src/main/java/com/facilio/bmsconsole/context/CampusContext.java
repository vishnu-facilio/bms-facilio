package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class CampusContext extends ModuleBaseWithCustomFields {
	
	@Override
	public String toString() {
		return "CampusContext [baseSpaceId=" + baseSpaceId + ", name=" + name + ", currentOccupancy=" + currentOccupancy
				+ ", maxOccupancy=" + maxOccupancy + ", area=" + area + ", location=" + location + ", managedBy="
				+ managedBy + ", description=" + description + "]";
	}

	private long baseSpaceId;
	public long getBaseSpaceId() {
		return baseSpaceId;
	}
	public void setBaseSpaceId(long baseSpaceId) {
		this.baseSpaceId = baseSpaceId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private int currentOccupancy;
	public int getCurrentOccupancy() {
		return currentOccupancy;
	}
	public void setCurrentOccupancy(int currentOccupancy) {
		this.currentOccupancy = currentOccupancy;
	}
	
	private int maxOccupancy;
	public int getMaxOccupancy() {
		return maxOccupancy;
	}
	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}
	
	private long area;
	public long getArea() {
		return area;
	}
	public void setArea(long area) {
		this.area = area;
	}
	
	private LocationContext location;
	public LocationContext getLocation() {
		return location;
	}
	public void setLocation(LocationContext location) {
		this.location = location;
	}
	
	private UserContext managedBy;
	public UserContext getManagedBy() {
		return managedBy;
	}
	public void setManagedBy(UserContext managedBy) {
		this.managedBy = managedBy;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
