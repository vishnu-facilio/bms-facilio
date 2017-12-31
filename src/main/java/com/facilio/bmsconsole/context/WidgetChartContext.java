package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountConstants.GroupMemberRole;

public class WidgetChartContext extends DashboardWidgetContext {

	String headerText;
	String headerSubText;
	Boolean headerIsExport;
	Integer chartDisplayType;
	WidgetChartType widgetChartType;
	Long xAxis;
	String y1Axis;
	Long y2Axis;
	Long y3Axis;
	boolean isComparisionReport;
	Long criteriaId;
	
	public WidgetChartType getWidgetChartType() {
		return WidgetChartType.getWidgetChartType(getChartDisplayType());
	}
	public void setWidgetChartType(WidgetChartType widgetChartType) {
		this.widgetChartType = widgetChartType;
	}
	public boolean getIsComparisionReport() {
		return isComparisionReport;
	}
	public void setIsComparisionReport(boolean isComparisionReport) {
		this.isComparisionReport = isComparisionReport;
	}
	public String getHeaderText() {
		return headerText;
	}
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}
	public String getHeaderSubText() {
		return headerSubText;
	}
	public void setHeaderSubText(String headerSubText) {
		this.headerSubText = headerSubText;
	}
	public Boolean getHeaderIsExport() {
		return headerIsExport;
	}
	public void setHeaderIsExport(Boolean headerIsExport) {
		this.headerIsExport = headerIsExport;
	}
	public Integer getChartDisplayType() {
		return chartDisplayType;
	}
	public void setChartDisplayType(Integer chartDisplayType) {
		this.chartDisplayType = chartDisplayType;
	}
	public Long getXAxis() {
		return xAxis;
	}
	public void setxAxis(Long xAxis) {
		this.xAxis = xAxis;
	}
	public String getY1Axis() {
		return y1Axis;
	}
	public void setY1Axis(String y1Axis) {
		this.y1Axis = y1Axis;
	}
	public Long getY2Axis() {
		return y2Axis;
	}
	public void setY2Axis(Long y2Axis) {
		this.y2Axis = y2Axis;
	}
	public Long getY3Axis() {
		return y3Axis;
	}
	public void setY3Axis(Long y3Axis) {
		this.y3Axis = y3Axis;
	}
	public Long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(Long criteriaId) {
		this.criteriaId = criteriaId;
	}
	public boolean isXaxisOnlyReport() {
		if(y1Axis == null && y2Axis == null && y3Axis == null) {
			return true;
		}
		return false;
	}
	@Override
	public JSONObject getWidgetJsonObject() {
		JSONObject resultJson = new JSONObject();
		
		resultJson.put("type", getType());
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
		
		headerJson.put("export", getHeaderIsExport());
		
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
		dataOptionsJson.put("type", getWidgetChartType().getName());
		dataOptionsJson.put("dataurl", "/app/dashboard/getData?reportId="+getId());
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
	public enum WidgetChartType {
		DOUGHNUT(1,"doughnut"),
		STACKBAR(2,"stackbar"),
		PROGRESS(3,"progress"),
		BAR(4,"bar");
		
		private int value;
		private String name;
		private int minHeight;
		private int minWidth;
		
		WidgetChartType(int value,String name) {
			this.value = value;
			this.name = name;
		}
		WidgetChartType(int value,String name,int minHeight,int minWidth) {
			this.value = value;
			this.name = name;
			this.minHeight = minHeight;
			this.minWidth = minWidth;
		}
		
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getMinHeight() {
			return minHeight;
		}
		public void setMinHeight(int minHeight) {
			this.minHeight = minHeight;
		}
		public int getMinWidth() {
			return minWidth;
		}
		public void setMinWidth(int minWidth) {
			this.minWidth = minWidth;
		}
		
		public static WidgetChartType getWidgetChartType(int value) {
			return WIDGET_CHART_TYPE_MAP.get(value);
		}

		private static final Map<Integer, WidgetChartType> WIDGET_CHART_TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, WidgetChartType> initTypeMap() {
			Map<Integer, WidgetChartType> typeMap = new HashMap<>();
			for(WidgetChartType type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}
}
