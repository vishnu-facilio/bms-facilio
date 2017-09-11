package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class SpaceContext extends ModuleBaseWithCustomFields {
	
	private static final Map<Integer, String> ALL_AVAILABILITY = Collections.unmodifiableMap(initAvailabilityMap());
	
	private static Map<Integer, String> initAvailabilityMap() {
		Map<Integer, String> availability = new HashMap<>();
		availability.put(1, "Vacant");
		availability.put(2, "Partially Occupied");
		availability.put(3, "At Capacity");
		availability.put(4, "Over Capacity");
		availability.put(5, "Reserved");
		return availability;
	}
	public static  Map<Integer, String> getAllAvailability() {
		return ALL_AVAILABILITY;
	}
	
	private long baseSpaceId;
	public long getBaseSpaceId() {
		return baseSpaceId;
	}
	public void setBaseSpaceId(long baseSpaceId) {
		this.baseSpaceId = baseSpaceId;
	}
	
	private BuildingContext building;
	public BuildingContext getBuilding() {
		return building;
	}
	public void setBuilding(BuildingContext building) {
		this.building = building;
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
	
	private FloorContext floor;
	public FloorContext getFloor() {
		return floor;
	}
	public void setFloor(FloorContext floor) {
		this.floor = floor;
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
	
	private SpaceCategoryContext spaceCategory;
	public SpaceCategoryContext getSpaceCategory() {
		return spaceCategory;
	} 
	public void setSpaceCategory(SpaceCategoryContext spaceCategory) {
		this.spaceCategory = spaceCategory;
	}
	
	private long area;
	public long getArea() {
		return area;
	}
	public void setArea(long area) {
		this.area = area;
	}
	
	private int availability;
	public int getAvailability() {
		return availability;
	}
	public void setAvailability(int availability) {
		this.availability = availability;
	}
	
	private Boolean occupiable;
	public Boolean getOccupiable() {
		return occupiable;
	}
	public void setOccupiable(Boolean occupiable) {
		this.occupiable = occupiable;
	}
}
