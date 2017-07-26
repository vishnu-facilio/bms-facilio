package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class CampusContext extends ModuleBaseWithCustomFields {
	
	public static final String[] DEFAULT_CAMPUS_FIELDS = new String[] {"campusId", "name", "description", "currentOccupancy", "maxOccupancy", "area", "locationId", "managedBy"};
	
	private long campusId;
	public long getCampusId() {
		return campusId;
	}
	public void setCampusId(long campusId) {
		this.campusId = campusId;
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
	
	private long locationId;
	public long getLocationId() {
		return locationId;
	}
	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}
	
	private long managedBy;
	public long getManagedBy() {
		return managedBy;
	}
	public void setManagedBy(long managedBy) {
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
