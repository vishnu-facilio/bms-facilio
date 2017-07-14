package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class FloorContext extends ModuleBaseWithCustomFields {
	
	public static final String[] DEFAULT_FLOOR_FIELDS = new String[] {"floorId", "name", "buildingId", "mainLevel", "assignableArea", "usableArea", "grossArea", "areaUnit", "currentOccupancy", "maxOccupancy", "percentOccupied", "utilizationMin", "utilizationMax"};
	
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
	
	private boolean mainLevel;
	public boolean getMainLevel() {
		return mainLevel;
	}
	public void setMainLevel(boolean mainLevel) {
		this.mainLevel = mainLevel;
	}
	
	private int utilizationMin;
	public int getUtilizationMin() {
		return utilizationMin;
	}
	public void setUtilizationMin(int utilizationMin) {
		this.utilizationMin = utilizationMin;
	}
	
	private int utilizationMax;
	public int getUtilizationMax() {
		return utilizationMax;
	}
	public void setUtilizationMax(int utilizationMax) {
		this.utilizationMax = utilizationMax;
	}
}
