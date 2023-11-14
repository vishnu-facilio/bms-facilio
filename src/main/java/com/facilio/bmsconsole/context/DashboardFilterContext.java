package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import lombok.Getter;
import lombok.Setter;
import com.facilio.modules.ModuleBaseWithCustomFields;
@Getter @Setter
public class DashboardFilterContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(DashboardFilterContext.class.getName());
	
	private long dashboardId=-1;
	private long dashboardTabId=-1;
	private Boolean isTimelineFilterEnabled;
	private long dateOperator;
	private String dateValue;
	private String dateLabel;
	private Map<Long,List<Long>> widgetUserFiltersMap;
	private List<Long > customScriptWidgets=new ArrayList<>();
	private boolean hideFilterInsideWidgets;

	public boolean isHideFilterInsideWidgets() {
		return hideFilterInsideWidgets;
	}

	public void setHideFilterInsideWidgets(boolean hideFilterInsideWidgets) {
		this.hideFilterInsideWidgets = hideFilterInsideWidgets;
	}
	
	
	

	public List<Long> getCustomScriptWidgets() {
		return customScriptWidgets;
	}
	public void setCustomScriptWidgets(List<Long> customScriptWidgets) {
		this.customScriptWidgets = customScriptWidgets;
	}
	public Map<Long, List<Long>> getWidgetUserFiltersMap() {
		return widgetUserFiltersMap;
	}
	public void setWidgetUserFiltersMap(Map<Long, List<Long>> widgetUserFiltersMap) {
		this.widgetUserFiltersMap = widgetUserFiltersMap;
	}
	public String getDateLabel() {
		return dateLabel;
	}
	public void setDateLabel(String dateLabel) {
		this.dateLabel = dateLabel;
	}
	private List<DashboardUserFilterContext> dashboardUserFilters;
	Map<Long, Map<String,String>> widgetTimelineFilterMap;
	

	
	
	public Map<Long, Map<String, String>> getWidgetTimelineFilterMap() {
		return widgetTimelineFilterMap;
	}
	public void setWidgetTimelineFilterMap(Map<Long, Map<String, String>> widgetTimelineFilterMap) {
		this.widgetTimelineFilterMap = widgetTimelineFilterMap;
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
	private String linkName;

}
