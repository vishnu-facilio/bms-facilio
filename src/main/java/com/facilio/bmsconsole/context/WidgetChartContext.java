package com.facilio.bmsconsole.context;

import org.json.simple.JSONObject;

public class WidgetChartContext extends DashboardWidgetContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Long reportId;
	Long newReportId;
	public Long getNewReportId() {
		return newReportId;
	}
	public void setNewReportId(Long newReportId) {
		this.newReportId = newReportId;
	}
	Integer chartType;
	
	public Integer getChartType() {
		return chartType;
	}
	public void setChartType(Integer chartType) {
		this.chartType = chartType;
	}
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	Long dateFilterId;
	public Long getDateFilterId() {
		return dateFilterId;
	}
	public void setDateFilterId(Long dateFilter) {
		this.dateFilterId = dateFilter;
	}
	@Override
	public JSONObject widgetJsonObject() {
		JSONObject resultJson = new JSONObject();
		
		resultJson.put("id", getId());
		resultJson.put("type", getWidgetType().getName());
		
		JSONObject layoutJson = new JSONObject();
		layoutJson.put("height", getLayoutHeight());
		layoutJson.put("width", getLayoutWidth());
		layoutJson.put("x", getxPosition());
		layoutJson.put("y", getyPosition());
		layoutJson.put("position", getLayoutPosition());
		
		resultJson.put("layout", layoutJson);
		
		JSONObject mlayoutJson = new JSONObject();
		mlayoutJson.put("height", getmLayoutHeight());
		mlayoutJson.put("width", getmLayoutWidth());
		mlayoutJson.put("x", getmXPosition());
		mlayoutJson.put("y", getmYPosition());
		mlayoutJson.put("position", getmLayoutPosition());
		
		resultJson.put("mLayout", mlayoutJson);
		
		JSONObject headerJson = new JSONObject();
		headerJson.put("title", getHeaderText());
		// Temprovery 
		if(getHeaderSubText() != null && getHeaderSubText().equals("{today}")) {
			headerJson.put("subtitle", "today");
		}
		else {
			headerJson.put("subtitle", getHeaderSubText());
		}
		
		headerJson.put("export", isHeaderIsExport());
		
		resultJson.put("header", headerJson);
		
		JSONObject dataOptionsJson = new JSONObject();
		if(chartType != null && chartType > 0) {
			dataOptionsJson.put("chartType", ReportContext.ReportChartType.getWidgetChartType(chartType).getName());
		}
		dataOptionsJson.put("dataurl", "/dashboard/getData?reportId="+getReportId());
		dataOptionsJson.put("name", "dummy");
		dataOptionsJson.put("reportId", getReportId());
		dataOptionsJson.put("newReportId", getNewReportId());
		dataOptionsJson.put("chartTypeInt", chartType);
		dataOptionsJson.put("dateFilter", dateFilterId);
		dataOptionsJson.put("refresh_interval", getDataRefreshIntervel());
		
		resultJson.put("dataOptions", dataOptionsJson);
		
		
		JSONObject widgetJson = new JSONObject();
		widgetJson.put("widget", resultJson);
		widgetJson.put("label", getWidgetName());
		System.out.println("resultJson -- "+widgetJson);
		return widgetJson;
	}
}
