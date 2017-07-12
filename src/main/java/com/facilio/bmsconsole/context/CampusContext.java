package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class CampusContext extends ModuleBaseWithCustomFields {
	
	public static final String[] DEFAULT_CAMPUS_FIELDS = new String[] {"campusId", "name", "currentOccupancy", "maxOccupancy", "percentOccupied", "grossArea", "usableArea", "assignableArea", "areaUnit", "locationId", "managedBy"};
	
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
	
	private int percentOccupied;
	public int getPercentOccupied() {
		return percentOccupied;
	} 
	public void setPercentOccupied(int percentOccupied) {
		this.percentOccupied = percentOccupied;
	}
	
	private long grossArea;
	public long getGrossArea() {
		return grossArea;
	}
	public void setGrossArea(long grossArea) {
		this.grossArea = grossArea;
	}
	
	private long usableArea;
	public long getUsableArea() {
		return usableArea;
	}
	public void setUsableArea(long usableArea) {
		this.usableArea = usableArea;
	}
	
	private long assignableArea;
	public long getAssignableArea() {
		return assignableArea;
	}
	public void setAssignableArea(long assignableArea) {
		this.assignableArea = assignableArea;
	}
	
	private int areaUnit;
	public int getAreaUnit() {
		return areaUnit;
	}
	public void setAreaUnit(int areaUnit) {
		this.areaUnit = areaUnit;
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
}
