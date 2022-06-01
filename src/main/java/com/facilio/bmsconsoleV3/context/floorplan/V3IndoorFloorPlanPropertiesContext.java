package com.facilio.bmsconsoleV3.context.floorplan;

import com.facilio.v3.context.V3Context;

public class V3IndoorFloorPlanPropertiesContext extends V3Context {

   public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

private String label;

private Long objectId;

private String deskCode;

private Long employeeId;

private String secondaryLabel;

private Long departmentId;

private Long deskId;

private Long recordId;

private Long markerModuleId;

private String markerModuleName;

private String iconName;

private Boolean isCustom;

private String markerId;

private Long spaceId;

private String spaceCategory;

private Boolean isBooked;

private Boolean isOccupied;

private Long bookingFormId;


public Boolean getIsOccupied() {
	if(isOccupied != null) {
		return isOccupied;
	}
	return false;
}

	public Long getBookingFormId() {
		if(bookingFormId != null) {
			return bookingFormId;
		}
		return null;
	}

	public void setBookingFormId(Long bookingFormId) {
		this.bookingFormId = bookingFormId;
	}

public void setIsOccupied(Boolean isOccupied) {
	this.isOccupied = isOccupied;
}

private String zoneBackgroundColor;

private String centerLabel;

private Integer deskType;

// this is need for webapp floorplan

public Integer getDeskType() {
	return deskType;
}

public void setDeskType(Integer deskType) {
	this.deskType = deskType;
}

public String getCenterLabel() {
	return centerLabel;
}

public void setCenterLabel(String centerLabel) {
	this.centerLabel = centerLabel;
}

public String getZoneBackgroundColor() {
	return zoneBackgroundColor;
}

public void setZoneBackgroundColor(String zoneBackgroundColor) {
	this.zoneBackgroundColor = zoneBackgroundColor;
}

public Boolean getIsBooked() {
	if(isBooked != null) {
		return isBooked;
	}
	return false;
}

public void setIsBooked(Boolean isBooked) {
	this.isBooked = isBooked;
}

public String getSpaceCategory() {
	return spaceCategory;
}

public void setSpaceCategory(String spaceCategory) {
	this.spaceCategory = spaceCategory;
}

public Long getSpaceId() {
	return spaceId;
}

public void setSpaceId(Long spaceId) {
	this.spaceId = spaceId;
}

private String normalClass;
private String activeClass;
private String hoverClass;

//

private Boolean active;


public Boolean getActive() {
	if(active != null) {
		return active;
	}
	return false;
}

public void setActive(Boolean active) {
	this.active = active;
}

public String getNormalClass() {
	return normalClass;
}

public void setNormalClass(String normalClass) {
	this.normalClass = normalClass;
}

public String getActiveClass() {
	return activeClass;
}

public void setActiveClass(String activeClass) {
	this.activeClass = activeClass;
}

public String getHoverClass() {
	return hoverClass;
}

public void setHoverClass(String hoverClass) {
	this.hoverClass = hoverClass;
}



public String getMarkerId() {
	return markerId;
}

public void setMarkerId(String markerId) {
	this.markerId = markerId;
}

public Boolean getIsCustom() {
	if(isCustom != null) {
		return isCustom;
	}
	return false;
}

public void setIsCustom(Boolean isCustom) {
	this.isCustom = isCustom;
}

public String getIconName() {
	return iconName;
}

public void setIconName(String iconName) {
	this.iconName = iconName;
}

public Long getObjectId() {
	return objectId;
}

public void setObjectId(Long objectId) {
	this.objectId = objectId;
}

public String getDeskCode() {
	return deskCode;
}

public void setDeskCode(String deskCode) {
	this.deskCode = deskCode;
}

public Long getEmployeeId() {
	return employeeId;
}

public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
}

public String getSecondaryLabel() {
	return secondaryLabel;
}

public void setSecondaryLabel(String secondaryLabel) {
	this.secondaryLabel = secondaryLabel;
}

public Long getDepartmentId() {
	return departmentId;
}

public void setDepartmentId(Long departmentId) {
	this.departmentId = departmentId;
}

public Long getDeskId() {
	return deskId;
}

public void setDeskId(Long deskId) {
	this.deskId = deskId;
}

public Long getRecordId() {
	return recordId;
}

public void setRecordId(Long recordId) {
	this.recordId = recordId;
}

public Long getMarkerModuleId() {
	return markerModuleId;
}

public void setMarkerModuleId(Long markerModuleId) {
	this.markerModuleId = markerModuleId;
}

public String getMarkerModuleName() {
	return markerModuleName;
}

public void setMarkerModuleName(String markerModuleName) {
	this.markerModuleName = markerModuleName;
}
private Boolean isOpenDialog= true;
	public Boolean getOpenDialog() {
		return isOpenDialog;
	}

	public void setOpenDialog(Boolean openDialog) {
		isOpenDialog = openDialog;
	}


}
