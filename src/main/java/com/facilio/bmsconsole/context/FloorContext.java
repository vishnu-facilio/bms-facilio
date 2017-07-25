package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class FloorContext extends ModuleBaseWithCustomFields {
	
	public static final String[] DEFAULT_FLOOR_FIELDS = new String[] {"floorId", "name", "buildingId", "mainLevel", "area", "currentOccupancy", "maxOccupancy"};
	
	private long floorId;
	public long getFloorId() {
		return floorId;
	}
	public void setFloorId(long floorId) {
		this.floorId = floorId;
	}
	
	private long buildingId;
	public long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(long buildingId) {
		this.buildingId = buildingId;
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
	
	private boolean mainLevel;
	public boolean getMainLevel() {
		return mainLevel;
	}
	public void setMainLevel(boolean mainLevel) {
		this.mainLevel = mainLevel;
	}
}
