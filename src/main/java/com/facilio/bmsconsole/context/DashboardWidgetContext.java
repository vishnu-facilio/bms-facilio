package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public abstract class DashboardWidgetContext extends ModuleBaseWithCustomFields{

	private String widgetName;
	private String type;
	private Long dashboardId;
	private Integer layoutWidth;
	private Integer layoutHeight;
	private int layoutPosition;
	private int dataRefreshIntervel;
	private String widgetUrl;
	
	public String getWidgetUrl() {
		return widgetUrl;
	}
	public void setWidgetUrl(String widgetUrl) {
		this.widgetUrl = widgetUrl;
	}
	private List<WidgetPeriodContext> periods = new ArrayList<>(); 
	
	public abstract JSONObject getWidgetJsonObject();
	
	public List<WidgetPeriodContext> getPeriods() {
		return periods;
	}
	public Long getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(Long dashboardId) {
		this.dashboardId = dashboardId;
	}
	public void setPeriods(List<WidgetPeriodContext> periods) {
		this.periods = periods;
	}
	public void addPeriod(WidgetPeriodContext period) {
		periods.add(period);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getWidgetName() {
		return widgetName;
	}
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}
	public Integer getLayoutWidth() {
		return layoutWidth;
	}
	public void setLayoutWidth(Integer layoutWidth) {
		this.layoutWidth = layoutWidth;
	}
	public Integer getLayoutHeight() {
		return layoutHeight;
	}
	public void setLayoutHeight(Integer layoutHeight) {
		this.layoutHeight = layoutHeight;
	}
	public int getLayoutPosition() {
		return layoutPosition;
	}
	public void setLayoutPosition(int layoutPosition) {
		this.layoutPosition = layoutPosition;
	}
	public int getDataRefreshIntervel() {
		return dataRefreshIntervel;
	}
	public void setDataRefreshIntervel(int dataRefreshIntervel) {
		this.dataRefreshIntervel = dataRefreshIntervel;
	}
	
	
}
