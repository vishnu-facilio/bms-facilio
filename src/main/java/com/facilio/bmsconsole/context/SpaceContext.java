package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class SpaceContext extends ModuleBaseWithCustomFields {
	
	public static final String[] DEFAULT_SPACE_FIELDS = new String[] {"spaceId", "displayName", "name", "buildingId", "floorId", "area", "areaUnit", "status", "availability", "currentOccupancy", "maxOccupancy", "percentOccupied", "occupiable"};
	
	private long spaceId;
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	
	private long buildingId;
	public long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(long buildingId) {
		this.buildingId = buildingId;
	}
	
	private String displayName;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long floorId;
	public long getFloorId() {
		return floorId;
	}
	public void setFloorId(long floorId) {
		this.floorId = floorId;
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
	
	private long area;
	public long getArea() {
		return area;
	}
	public void setArea(long area) {
		this.area = area;
	}
	
	private int areaUnit;
	public int getAreaUnit() {
		return areaUnit;
	}
	public void setAreaUnit(int areaUnit) {
		this.areaUnit = areaUnit;
	}
	
	private int status;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	private int availability;
	public int getAvailability() {
		return availability;
	}
	public void setAvailability(int availability) {
		this.availability = availability;
	}
	
	private boolean occupiable;
	public boolean getOccupiable() {
		return occupiable;
	}
	public void setOccupiable(boolean occupiable) {
		this.occupiable = occupiable;
	}
}
