package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class BaseSpaceContext extends ModuleBaseWithCustomFields {
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String displayName;
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private long photoId = -1;
	public long getPhotoId() {
		return photoId;
	}
	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}
	
	public String getAvatarUrl() throws Exception {
		if (this.photoId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			return fs.getPrivateUrl(this.photoId);
		}
		return null;
	}
	
	private double area = -1;
	public double getArea() {
		return area;
	}
	public void setArea(double area) {
		this.area = area;
	}
	
	private SpaceType spaceType;
	public int getSpaceType() {
		if(spaceType != null) {
			return spaceType.getIntVal();
		}
		return -1;
	}
	public void setSpaceType(int type) {
		this.spaceType = SpaceType.typeMap.get(type);
	}
	public void setSpaceType(SpaceType type) {
		this.spaceType = type;
	}
	public String getSpaceTypeVal() {
		if(spaceType != null) {
			return spaceType.getStringVal();
		}
		return null;
	}
	public SpaceType getSpaceTypeEnum() {
		return spaceType;
	}
	
	private float operationHoursStart = -1;
	public float getOperationHoursStart() {
		return operationHoursStart;
	}
	public void setOperationHoursStart(float operationHoursStart) {
		this.operationHoursStart = operationHoursStart;
	}
	
	private float operationHoursEnd = -1;
	public float getOperationHoursEnd() {
		return operationHoursEnd;
	}
	public void setOperationHoursEnd(float operationHoursEnd) {
		this.operationHoursEnd = operationHoursEnd;
	}
	
	private int maxOccupancy = -1;
	public int getMaxOccupancy() {
		return maxOccupancy;
	}
	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}
	
	private int lastCurrentOccupancy = -1;
	public int getLastCurrentOccupancy() {
		return lastCurrentOccupancy;
	}
	public void setLastCurrentOccupancy(int lastCurrentOccupancy) {
		this.lastCurrentOccupancy = lastCurrentOccupancy;
	}
	
	private long lastCurrentOccupancyTime = -1;
	public long getLastCurrentOccupancyTime() {
		return lastCurrentOccupancyTime;
	}
	public void setLastCurrentOccupancyTime(long lastCurrentOccupancyTime) {
		this.lastCurrentOccupancyTime = lastCurrentOccupancyTime;
	}
	
	private int lastAssignedOccupancy = -1;
	public int getLastAssignedOccupancy() {
		return lastAssignedOccupancy;
	}
	public void setLastAssignedOccupancy(int lastAssignedOccupancy) {
		this.lastAssignedOccupancy = lastAssignedOccupancy;
	}
	
	private long lastAssignedOccupancyTime = -1;
	public long getLastAssignedOccupancyTime() {
		return lastAssignedOccupancyTime;
	}
	public void setLastAssignedOccupancyTime(long lastAssignedOccupancyTime) {
		this.lastAssignedOccupancyTime = lastAssignedOccupancyTime;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private long buildingId = -1;
	public long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(long buildingId) {
		this.buildingId = buildingId;
	}
	
	private long floorId = -1;
	public long getFloorId() {
		return floorId;
	}
	public void setFloorId(long floorId) {
		this.floorId = floorId;
	}
	
	public enum SpaceType {
		SITE(1, "Site"),
		BUILDING(2, "Building"),
		FLOOR(3, "Floor"),
		SPACE(4, "Space"),
		ZONE(5, "Zone")
		;
		
		private int intVal;
		private String strVal;
		
		private SpaceType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		public static SpaceType getType(int val) {
			return typeMap.get(val);
		}
		
		private static final Map<Integer, SpaceType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, SpaceType> initTypeMap() {
			Map<Integer, SpaceType> typeMap = new HashMap<>();
			
			for(SpaceType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, SpaceType> getAllTypes() {
			return typeMap;
		}
	}
}
