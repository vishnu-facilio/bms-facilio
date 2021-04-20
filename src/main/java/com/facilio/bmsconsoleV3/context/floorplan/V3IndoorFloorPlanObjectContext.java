package com.facilio.bmsconsoleV3.context.floorplan;

import com.facilio.v3.context.V3Context;
public class V3IndoorFloorPlanObjectContext  extends V3Context {


	private V3IndoorFloorPlanContext indoorfloorplan;
	private String type;
	private String geometry;
	private String properties;
	private String geoId;
	private String label;

	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	
	public V3IndoorFloorPlanContext getIndoorfloorplan() {
		return indoorfloorplan;
	}
	public void setIndoorfloorplan(V3IndoorFloorPlanContext indoorfloorplan) {
		this.indoorfloorplan = indoorfloorplan;
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


	public String getGeoId() {
		return geoId;
	}
	public void setGeoId(String geoId) {
		this.geoId = geoId;
	}
    


	
}
