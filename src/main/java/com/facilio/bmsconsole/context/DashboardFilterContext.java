package com.facilio.bmsconsole.context;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class DashboardFilterContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(DashboardFilterContext.class.getName());
	
	private long dashboardId;
	private long dashboardTabId;
	private Boolean isTimelineFilterEnabled;
	private long dateOperator;
	private String dateValue;
	private String dateLabel;
	private Map<Long,List<Long>> filterWidgetMapping;
	
	
	public String getDateLabel() {
		return dateLabel;
	}
	public void setDateLabel(String dateLabel) {
		this.dateLabel = dateLabel;
	}
	private List<DashboardUserFilterContext> dashboardUserFilters;
	JSONObject widgetTimelineFilters;
	
	
	public JSONObject getWidgetTimelineFilters() {
		return widgetTimelineFilters;
	}
	public void setWidgetTimelineFilters(JSONObject widgetTimelineFilters) {
		this.widgetTimelineFilters = widgetTimelineFilters;
	}
	public long getDashboardId() {
		return dashboardId;
	}
	public long getDashboardTabId() {
		return dashboardTabId;
	}
	public List<DashboardUserFilterContext> getDashboardUserFilters() {
		return dashboardUserFilters;
	}
	public long getDateOperator() {
		return dateOperator;
	}
	public String getDateValue() {
		return dateValue;
	}
	public Boolean getIsTimelineFilterEnabled() {
		return isTimelineFilterEnabled;
	}
	public void setDashboardId(long dashboardId) {
		this.dashboardId = dashboardId;
	}
	public void setDashboardTabId(long dashboardTabId) {
		this.dashboardTabId = dashboardTabId;
	}
	public void setDashboardUserFilters(List<DashboardUserFilterContext> dashboardUserFilters) {
		this.dashboardUserFilters = dashboardUserFilters;
	}
	public void setDateOperator(long dateOperator) {
		this.dateOperator = dateOperator;
	}
	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
	}
	public void setIsTimelineFilterEnabled(Boolean isTimelineFilterEnabled) {
		this.isTimelineFilterEnabled = isTimelineFilterEnabled;
	}
	public Map<Long,List<Long>> getFilterWidgetMapping() {
		return filterWidgetMapping;
	}
	public void setFilterWidgetMapping(Map<Long,List<Long>> filterWidgetMapping) {
		this.filterWidgetMapping = filterWidgetMapping;
	}
	

}
