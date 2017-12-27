package com.facilio.bmsconsole.context;

public class WidgetReportContext {

	Long id;
	Long moduleId;
	Long xAxis;
	String y1Axis;
	Long y2Axis;
	Long y3Axis;
	Long timeSeriesField;
	Boolean isComparisionReport;
	Long criteriaId;
	
	public Boolean getIsComparisionReport() {
		return isComparisionReport;
	}
	public void setIsComparisionReport(Boolean isComparisionReport) {
		this.isComparisionReport = isComparisionReport;
	}
	public Long getTimeSeriesField() {
		return timeSeriesField;
	}
	public void setTimeSeriesField(Long timeSeriesField) {
		this.timeSeriesField = timeSeriesField;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getModuleId() {
		return moduleId;
	}
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
	public Long getXAxis() {
		return xAxis;
	}
	public void setxAxis(Long xAxis) {
		this.xAxis = xAxis;
	}
	public String getY1Axis() {
		return y1Axis;
	}
	public void setY1Axis(String y1Axis) {
		this.y1Axis = y1Axis;
	}
	public Long getY2Axis() {
		return y2Axis;
	}
	public void setY2Axis(Long y2Axis) {
		this.y2Axis = y2Axis;
	}
	public Long getY3Axis() {
		return y3Axis;
	}
	public void setY3Axis(Long y3Axis) {
		this.y3Axis = y3Axis;
	}
	public Long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(Long criteriaId) {
		this.criteriaId = criteriaId;
	}
	public boolean isXaxisOnlyReport() {
		if(y1Axis == null && y2Axis == null && y3Axis == null) {
			return true;
		}
		return false;
	}
}
