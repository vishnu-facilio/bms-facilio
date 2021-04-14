

package com.facilio.bmsconsoleV3.context.floorplan;
import com.facilio.v3.context.V3Context;

public class V3FloorPlanMarkerTypeContext  extends V3Context {
	
	private Long startNumber;
	private Long currentNumber;
	private String prefix;
	private String suffix;
	private String previewcard;

private V3IndoorFloorPlanContext indoorfloorplan;
public V3IndoorFloorPlanContext getIndoorfloorplan() {
	return indoorfloorplan;
}
public void setIndoorfloorplan(V3IndoorFloorPlanContext indoorfloorplan) {
	this.indoorfloorplan = indoorfloorplan;
}
public V3FloorplanMarkersContext getMarkerType() {
	return markerType;
}
public void setMarkerType(V3FloorplanMarkersContext markerType) {
	this.markerType = markerType;
}
public Long getStartNumber() {
	return startNumber;
}
public void setStartNumber(Long startNumber) {
	this.startNumber = startNumber;
}
public Long getCurrentNumber() {
	return currentNumber;
}
public void setCurrentNumber(Long currentNumber) {
	this.currentNumber = currentNumber;
}
public String getPrefix() {
	return prefix;
}
public void setPrefix(String prefix) {
	this.prefix = prefix;
}
public String getSuffix() {
	return suffix;
}
public void setSuffix(String suffix) {
	this.suffix = suffix;
}
public String getPreviewcard() {
	return previewcard;
}
public void setPreviewcard(String previewcard) {
	this.previewcard = previewcard;
}
private V3FloorplanMarkersContext markerType;



}
