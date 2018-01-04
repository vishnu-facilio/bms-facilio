package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.DashboardUtil;

public class DashboardContext extends ModuleBaseWithCustomFields implements Comparable<DashboardContext> {

	private String dashboardName;
	private String linkName;
	public String getLinkName() {
		if (linkName == null) {
			linkName = dashboardName.replaceAll("[^\\p{IsAlphabetic}]+", "").toLowerCase();
			
			if (this.getId() > 0) {
				try {
					DashboardUtil.updateDashboardLinkName(this.getId(), linkName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	private Long createdByUserId;
	private Integer publishStatus;
	private String dashboardUrl;
	List<DashboardWidgetContext> dashboardWidgets;

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
	public String getDashboardUrl() {
		return dashboardUrl;
	}
	public void setDashboardUrl(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
	}
	public Long getCreatedByUserId() {
		return createdByUserId;
	}
	public void setCreatedByUserId(Long createdByUser) {
		this.createdByUserId = createdByUser;
	}
	public Integer getPublishStatus() {
		return publishStatus;
	}
	public void setPublishStatus(Integer publishStatus) {
		this.publishStatus = publishStatus;
	}
	public String getDashboardName() {
		return dashboardName;
	}
	public void setDashboardName(String dashboardName) {
		this.dashboardName = dashboardName;
	}
	public enum DashboardPublishStatus {
		NONE,
		SUBMITTED,
		REJECTED,
		PUBLISHED
	}
	@Override
	public int compareTo(DashboardContext o) {
		if(this.getId() < o.getId()) {
			return -1;
		}
		else if(this.getId() > o.getId()) {
			return 1;
		}
		return 0;
	}
}
