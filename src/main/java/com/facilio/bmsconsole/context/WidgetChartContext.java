package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants.GroupMemberRole;

public class WidgetChartContext extends DashboardWidgetContext {

	Long reportId;
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	@Override
	public JSONObject widgetJsonObject() {
		JSONObject resultJson = new JSONObject();
		
		resultJson.put("type", getWidgetType().getName());
		JSONObject layoutJson = new JSONObject();
		layoutJson.put("height", getLayoutHeight());
		layoutJson.put("width", getLayoutWidth());
		layoutJson.put("position", getLayoutPosition());
		
		resultJson.put("layout", layoutJson);
		
		JSONObject headerJson = new JSONObject();
		headerJson.put("title", getHeaderText());
		// Temprovery 
		if(getHeaderSubText().equals("{today}")) {
			headerJson.put("subtitle", "today");
		}
		else {
			headerJson.put("subtitle", getHeaderSubText());
		}
		
		headerJson.put("export", isHeaderIsExport());
		
		if(!this.getPeriods().isEmpty()) {
			JSONArray periods = new JSONArray();
			for(WidgetPeriodContext period:this.getPeriods()) {
				JSONObject periodJson = new JSONObject();
				periodJson.put("label", period.getPeriodLabel());
				periodJson.put("value", period.getPeriodValue());
				periods.add(periodJson);
			}
			headerJson.put("periods", periods);
		}
		
		resultJson.put("header", headerJson);
		
		JSONObject dataOptionsJson = new JSONObject();
//		dataOptionsJson.put("type", getWidgetChartType().getName());
		dataOptionsJson.put("dataurl", "/dashboard/getData?reportId="+getId());
		dataOptionsJson.put("name", "dummy");
		dataOptionsJson.put("reportId", getId());
		dataOptionsJson.put("refresh_interval", getDataRefreshIntervel());
		
		resultJson.put("dataOptions", dataOptionsJson);
		
		
		JSONObject widgetJson = new JSONObject();
		widgetJson.put("widget", resultJson);
		widgetJson.put("label", getWidgetName());
		System.out.println("resultJson -- "+widgetJson);
		return widgetJson;
	}
}
