package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class IndoorFloorPlanObjectContext extends ModuleBaseWithCustomFields{
	
	private long objectId;
	public long getObjectId() {
		return objectId;
	}
	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}
	public long getFloorplanId() {
		return floorplanId;
	}
	public void setFloorplanId(long floorplanId) {
		this.floorplanId = floorplanId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getGeometry() {
		return geometry;
	}
	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long id;
	private long indoorFloorPlanId;
	public long getIndoorFloorPlanId() {
		return indoorFloorPlanId;
	}
	public void setIndoorFloorPlanId(long indoorFloorPlanId) {
		this.indoorFloorPlanId = indoorFloorPlanId;
	}

	private long floorplanId;
	private String type;
	private String geometry;
	private String properties;
	private long spaceId;
	private long assetId;
	private long userId;
	
	
}
