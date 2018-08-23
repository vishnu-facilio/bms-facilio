package com.facilio.bmsconsole.context;

public class ReportSpaceFilterContext {

	Long id;
	Long reportId,siteId,buildingId,floorId,dashboardId;
	Long chillerId;
	
	public Long getChillerId() {
		return chillerId;
	}
	public void setChillerId(Long chillerId) {
		this.chillerId = chillerId;
	}

	String groupBy;
	public Long getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(Long dashboardId) {
		this.dashboardId = dashboardId;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	public Long getSiteId() {
		return siteId;
	}
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	public Long getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(Long buildingId) {
		this.buildingId = buildingId;
	}
	public Long getFloorId() {
		return floorId;
	}
	public void setFloorId(Long floorId) {
		this.floorId = floorId;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	
	public Long getSpaceFilterId() {
		if(siteId != null) {
			return siteId;
		}
		else if (buildingId != null) {
			return buildingId;
		}
		else if (floorId != null) {
			return floorId;
		}
		return null;
	}
}
