package com.facilio.bmsconsole.context;

import org.json.simple.JSONObject;

public class WidgetGraphicsContext extends DashboardWidgetContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Long graphicsId;
	public Long getGraphicsId() {
		return graphicsId;
	}
	public void setGraphicsId(Long graphicsId) {
		this.graphicsId = graphicsId;
	}
	
	private String graphicsOptions;
	
	public String getGraphicsOptions() {
		return graphicsOptions;
	}
	public void setGraphicsOptions(String graphicsOptions) {
		this.graphicsOptions = graphicsOptions;
	}

	@Override
	public JSONObject widgetJsonObject(boolean optimize) {
		JSONObject resultJson = new JSONObject();
		
		resultJson.put("id", getId());
		resultJson.put("type", getWidgetType().getName());
		resultJson.put("widgetSettings",getWidgetSettings());
		resultJson.put("helpText",getHelpText());
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
		dataOptionsJson.put("dataurl", "");
		dataOptionsJson.put("name", "dummy");
		dataOptionsJson.put("graphicsId", getGraphicsId());
		dataOptionsJson.put("refresh_interval", getDataRefreshIntervel());
		if (graphicsOptions != null) {
			dataOptionsJson.put("graphicsOptions", graphicsOptions);
		}
		
		resultJson.put("dataOptions", dataOptionsJson);
		resultJson.put("customActions", getCustomActions());

		JSONObject widgetJson = new JSONObject();
		widgetJson.put("widget", resultJson);
		widgetJson.put("label", getWidgetName());
//		System.out.println("resultJson -- "+widgetJson);
		return widgetJson;
	}

	@Override
	public JSONObject widgetMobileJsonObject(boolean optimize, int index) {

		JSONObject widgetJson = new JSONObject();
		widgetJson.put("label", getWidgetName());
		widgetJson.put("id", getId());
		widgetJson.put("graphicsId", getGraphicsId());
		widgetJson.put("refresh_interval", getDataRefreshIntervel());
		widgetJson.put("title", getHeaderText());
		widgetJson.put("type", getWidgetType().getName());
		widgetJson.put("helpText",getHelpText());
		widgetJson.put("link_name", getLinkName());
		widgetJson.put("sequence", index);
		if (graphicsOptions != null) {
			widgetJson.put("graphicsOptions", graphicsOptions);
		}
		return widgetJson;
	}
}
