package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

public class DashboardTabContext {

	long id = -1;
	long orgId = -1;
	long dashboardId = -1;
	long dashboardTabId = -1;
	int sequence;
	String name;
	List<DashboardTabContext> childTabs;
	
	List<DashboardWidgetContext> dashboardWidgets;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(long dashboardId) {
		this.dashboardId = dashboardId;
	}
	public long getDashboardTabId() {
		return dashboardTabId;
	}
	public void setDashboardTabId(long dashboardTabId) {
		this.dashboardTabId = dashboardTabId;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<DashboardTabContext> getChildTabs() {
		return childTabs;
	}
	public void setChildTabs(List<DashboardTabContext> childTabs) {
		this.childTabs = childTabs;
	}

	public List<DashboardWidgetContext> getDashboardWidgets() {
		return dashboardWidgets;
	}
	public void setDashboardWidgets(List<DashboardWidgetContext> dashboardWidgets) {
		this.dashboardWidgets = dashboardWidgets;
	}
	
	public void addDashboardWidget(DashboardWidgetContext dashboardWidgetContext) {
		if(this.dashboardWidgets == null) {
			this.dashboardWidgets = new ArrayList<>();
		}
		dashboardWidgets.add(dashboardWidgetContext);
	}
}
