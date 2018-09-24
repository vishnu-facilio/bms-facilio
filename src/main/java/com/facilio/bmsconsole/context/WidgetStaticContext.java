package com.facilio.bmsconsole.context;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WidgetStaticContext extends DashboardWidgetContext {

	
	String staticKey;
	Long baseSpaceId;
	JSONObject paramsJson;
	String metaJson;
	
	public String getMetaJson() {
		return metaJson;
	}

	public void setMetaJson(String metaJson) {
		this.metaJson = metaJson;
	}

	public JSONObject getParamsJson() {
		return paramsJson;
	}

	public void setParamsJson(JSONObject paramsJson) {
		this.paramsJson = paramsJson;
	}

	public void setParams(String s) throws ParseException {
		
		JSONParser parser = new JSONParser();
		paramsJson = (JSONObject) parser.parse(s);
	}
	public String getParams() {
		
		if(paramsJson != null) {
			return paramsJson.toJSONString(); 
		}
		return null;
	}
	
	public Long getBaseSpaceId() {
		return baseSpaceId;
	}

	public void setBaseSpaceId(Long baseSpaceId) {
		this.baseSpaceId = baseSpaceId;
	}

	public void setStaticKey(String staticKey) {
		this.staticKey = staticKey;
	}
	
	public String getStaticKey() {
		return this.staticKey;
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
		dataOptionsJson.put("dataurl", "");
		dataOptionsJson.put("name", "dummy");
		dataOptionsJson.put("staticKey", getStaticKey());
		dataOptionsJson.put("metaJson", getMetaJson());
		dataOptionsJson.put("paramsJson", getParamsJson());
		dataOptionsJson.put("refresh_interval", getDataRefreshIntervel());
		
		resultJson.put("dataOptions", dataOptionsJson);
		
		
		JSONObject widgetJson = new JSONObject();
		widgetJson.put("widget", resultJson);
		widgetJson.put("label", getWidgetName());
		System.out.println("resultJson -- "+widgetJson);
		return widgetJson;
	}
}
