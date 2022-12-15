package com.facilio.bmsconsoleV3.context.floorplan;

import java.util.List;

import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;

public class V3MarkerContext extends V3IndoorFloorPlanObjectContext {

	public V3FloorplanMarkersContext getMarkerType() {
		return markerType;
	}

	public void setMarkerType(V3FloorplanMarkersContext markerType) {
		this.markerType = markerType;
	}

	private V3FloorplanMarkersContext markerType;

	public Long getMarkerModuleId() {
		return markerModuleId;
	}

	public void setMarkerModuleId(Long markerModuleId) {
		this.markerModuleId = markerModuleId;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	private Long markerModuleId;
	private Long recordId;

	public V3DeskContext getDesk() {
		return desk;
	}

	public void setDesk(V3DeskContext desk) {
		this.desk = desk;
	}
	
	public V3ResourceContext getModuleData() {
		return moduleData;
	}

	public void setModuleData(V3ResourceContext moduleData) {
		this.moduleData = moduleData;
	}

	private V3ResourceContext moduleData;

	private V3DeskContext desk;

}