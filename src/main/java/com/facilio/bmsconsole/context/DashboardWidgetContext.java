package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class DashboardWidgetContext extends ModuleBaseWithCustomFields{

	private String widgetName;
	private String type;
	private Integer layoutWidth;
	private Integer layoutHeight;
	private int layoutPosition;
	private String headerTitle;
	private String headerSubTitle;
	private Boolean headerIsExport;
	private String dataOptionType;
	private String dataOptionName;
	private String dataOptionDataUrl;
	private int dataOptionRefreshIntervel;
	private Long reportId;
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	private List<DashboardWidgetPeriodContext> periods = new ArrayList<>(); 
	
	public JSONObject getWidgetJsonObject() {
		JSONObject resultJson = new JSONObject();
		
		resultJson.put("type", getType());
		JSONObject layoutJson = new JSONObject();
		layoutJson.put("height", getLayoutHeight());
		layoutJson.put("width", getLayoutWidth());
		layoutJson.put("position", getLayoutPosition());
		
		resultJson.put("layout", layoutJson);
		
		JSONObject headerJson = new JSONObject();
		headerJson.put("title", getHeaderTitle());
		headerJson.put("subtitle", getHeaderSubTitle());
		headerJson.put("export", getHeaderIsExport());
		
		if(!this.getPeriods().isEmpty()) {
			JSONArray periods = new JSONArray();
			for(DashboardWidgetPeriodContext period:this.getPeriods()) {
				JSONObject periodJson = new JSONObject();
				periodJson.put("label", period.getPeriodLabel());
//				periodJson.put("value", period.getDataOperator());
				periodJson.put("value", period.getPeriodValue());
				periods.add(periodJson);
			}
			headerJson.put("periods", periods);
		}
		
		resultJson.put("header", headerJson);
		
		JSONObject dataOptionsJson = new JSONObject();
		dataOptionsJson.put("type", getDataOptionType());
		dataOptionsJson.put("name", getDataOptionName());
		dataOptionsJson.put("dataurl", getDataOptionDataUrl());
		dataOptionsJson.put("reportId", getReportId());
		dataOptionsJson.put("refresh_interval", getDataOptionRefreshIntervel());
		
		resultJson.put("dataOptions", dataOptionsJson);
		
		
		JSONObject widgetJson = new JSONObject();
		widgetJson.put("widget", resultJson);
		widgetJson.put("label", getWidgetName());
		System.out.println("resultJson -- "+widgetJson);
		return widgetJson;
	}
	public List<DashboardWidgetPeriodContext> getPeriods() {
		return periods;
	}
	public void setPeriods(List<DashboardWidgetPeriodContext> periods) {
		this.periods = periods;
	}
	public void addPeriod(DashboardWidgetPeriodContext period) {
		periods.add(period);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDataOptionType() {
		return dataOptionType;
	}

	public void setDataOptionType(String dataOptionType) {
		this.dataOptionType = dataOptionType;
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
	public String getHeaderTitle() {
		return headerTitle;
	}
	public void setHeaderTitle(String headerTitle) {
		this.headerTitle = headerTitle;
	}
	public String getHeaderSubTitle() {
		return headerSubTitle;
	}
	public void setHeaderSubTitle(String headerSubTitle) {
		this.headerSubTitle = headerSubTitle;
	}
	public Boolean getHeaderIsExport() {
		return headerIsExport;
	}
	public void setHeaderIsExport(Boolean headerIsExport) {
		this.headerIsExport = headerIsExport;
	}
	public String getDataOptionName() {
		return dataOptionName;
	}
	public void setDataOptionName(String dataOptionName) {
		this.dataOptionName = dataOptionName;
	}
	public String getDataOptionDataUrl() {
		return dataOptionDataUrl;
	}
	public void setDataOptionDataUrl(String dataOptionDataUrl) {
		this.dataOptionDataUrl = dataOptionDataUrl;
	}
	public int getDataOptionRefreshIntervel() {
		return dataOptionRefreshIntervel;
	}
	public void setDataOptionRefreshIntervel(int dataOptionRefreshIntervel) {
		this.dataOptionRefreshIntervel = dataOptionRefreshIntervel;
	}
	
	
}
