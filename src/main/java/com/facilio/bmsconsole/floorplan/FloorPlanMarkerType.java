package com.facilio.bmsconsole.floorplan;

public enum FloorPlanMarkerType {
	CONTROL_TEMPERATURE ("control_temperature"),
	CONTROL_LIGHT ("control_light"),
	ASSET ("asset"),
	MAINTENANCE ("maintenance"),
	ALARM ("alarm");
	
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private FloorPlanMarkerType(String name) {
		this.name = name;
	}
}
