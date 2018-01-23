package com.facilio.bmsconsole.context;

import org.json.simple.JSONObject;

public class WidgetListViewContext extends DashboardWidgetContext {

	Long viewId;
	
	public Long getViewId() {
		return viewId;
	}
	public void setViewId(Long viewId) {
		this.viewId = viewId;
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
		headerJson.put("export", isHeaderIsExport());
		if(getHeaderSubText().equals("{today}")) {		// temprovery
			headerJson.put("subtitle", "today");
		}
		else {
			headerJson.put("subtitle", getHeaderSubText());
		}
		
		resultJson.put("header", headerJson);
		
		JSONObject dataOptionsJson = new JSONObject();
		dataOptionsJson.put("dataurl", "/app/dashboard/getData?reportId="+getId());
		dataOptionsJson.put("name", "dummy");
		dataOptionsJson.put("viewId", getViewId());
		dataOptionsJson.put("refresh_interval", getDataRefreshIntervel());
		
		resultJson.put("dataOptions", dataOptionsJson);
		
		
		JSONObject widgetJson = new JSONObject();
		widgetJson.put("widget", resultJson);
		widgetJson.put("label", getWidgetName());
		System.out.println("resultJson -- "+widgetJson);
		return widgetJson;
	}

}
