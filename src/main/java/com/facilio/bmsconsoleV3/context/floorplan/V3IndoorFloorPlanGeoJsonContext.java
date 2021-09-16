package com.facilio.bmsconsoleV3.context.floorplan;

import java.util.Map;

import com.facilio.v3.context.V3Context;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class V3IndoorFloorPlanGeoJsonContext extends V3Context {
	
	private String objectId;
	public String getGeometryStr() {
		return geometryStr;
	}

	public void setGeometryStr(String geometryStr) {
		this.geometryStr = geometryStr;
	}

	public JSONObject getGeometry() {
		return geometry;
	}

	public void setGeometry(JSONObject geometry) {
		this.geometry = geometry;
	}

	private String geometryStr;
	private JSONObject geometry;
    private String type;
    
    private Boolean active;
    
	public JSONObject getTooltipData() {
		return tooltipData;
	}

	public void setTooltipData(JSONObject tooltipData) {
		this.tooltipData = tooltipData;
	}

	private JSONObject tooltipData;




	public Boolean getActive() {
		if(active != null) {
			return active;
		}
		return false;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}

    private V3IndoorFloorPlanPropertiesContext properties;

    public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
  
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public V3IndoorFloorPlanPropertiesContext getProperties() {
		return properties;
	}
	public void setProperties(V3IndoorFloorPlanPropertiesContext properties) {
		this.properties = properties;
	}
	
	public long getObjectType() {
		return objectType;
	}

	public void setObjectType(long objectType) {
		this.objectType = objectType;
	}
	
	private Boolean isReservable;

	public Boolean getIsReservable() {
		if(isReservable != null) {
			return isReservable;
		}
		return false;
	}

	public void setIsReservable(Boolean isReservable) {
		this.isReservable = isReservable;
	}

	private long objectType;
	
	public V3FloorplanMarkersContext getMarkerType() {
		return markerType;
	}

	public void setMarkerType(V3FloorplanMarkersContext markerType) {
		this.markerType = markerType;
	}

	private V3FloorplanMarkersContext markerType;


}
