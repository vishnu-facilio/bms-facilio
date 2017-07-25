package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class SpaceContext extends ModuleBaseWithCustomFields {
	
	public static final String[] DEFAULT_SPACE_FIELDS = new String[] {"spaceId", "displayName", "name", "buildingId", "floorId", "area", "availability", "currentOccupancy", "maxOccupancy", "spaceCategoryId", "occupiable"};
	
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
	
	private long spaceCategoryId;
	public long getSpaceCategoryId() {
		return spaceCategoryId;
	} 
	public void setSpaceCategoryId(long spaceCategoryId) {
		this.spaceCategoryId = spaceCategoryId;
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
	
	private boolean occupiable;
	public boolean getOccupiable() {
		return occupiable;
	}
	public void setOccupiable(boolean occupiable) {
		this.occupiable = occupiable;
	}
}
