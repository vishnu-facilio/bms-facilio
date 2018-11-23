package com.facilio.bmsconsole.context;

public class SpaceFilteredDashboardSettings {

	long id;
	long dashboardId;
	long baseSpaceId;
	public boolean mobileEnabled;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(long dashboardId) {
		this.dashboardId = dashboardId;
	}
	public long getBaseSpaceId() {
		return baseSpaceId;
	}
	public void setBaseSpaceId(long baseSpaceId) {
		this.baseSpaceId = baseSpaceId;
	}
	public boolean getMobileEnabled() {
		return mobileEnabled;
	}
	public void setMobileEnabled(boolean mobileEnabled) {
		this.mobileEnabled = mobileEnabled;
	}
	
}
