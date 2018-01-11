package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;


public class ReportContext1 {
	
	Long Id;
	Long parentFolderId;
	String name;
	Integer chartType;
	Long xAxis;
	ReportFieldContext xAxisField;
	Integer xAxisaggregateFunction;
	Long y1Axis;
	ReportFieldContext y1AxisField;
	Integer y1AxisaggregateFunction;
	Long y2Axis;
	ReportFieldContext y2AxisField;
	Integer y2AxisaggregateFunction;
	Long y3Axis;
	ReportFieldContext y3AxisField;
	Integer y3AxisaggregateFunction;
	
	Boolean isComparisionReport;
	String xAxisLegend;
	List<ReportCriteriaContext> reportCriteriaContexts;
	List<ReportUserFilterContext> reportUserFilters;
	List<Long> reportCriteriaIds;
	
	List<ReportThreshold> reportThresholds;
	
	public List<ReportUserFilterContext> getReportUserFilters() {
		return reportUserFilters;
	}

	public void setReportUserFilters(List<ReportUserFilterContext> reportUserFilters) {
		this.reportUserFilters = reportUserFilters;
	}
	
	public void addReportUserFilter(ReportUserFilterContext reportUserFilterContext) {
		if(this.reportUserFilters == null) {
			reportUserFilters = new ArrayList<>();
		}
		reportUserFilters.add(reportUserFilterContext);
	}

	public AggregateOperator getXAxisAggregateOpperator() {
		if (getxAxisaggregateFunction() != null) {
			return AggregateOperator.getAggregateOperator(getxAxisaggregateFunction());
		}
		else {
			return AggregateOperator.getAggregateOperator(1);
		}
	}
	
	public AggregateOperator getY1AxisAggregateOpperator() {
		if (getY1AxisaggregateFunction() != null) {
			return AggregateOperator.getAggregateOperator(getY1AxisaggregateFunction());
		}
		else {
			return AggregateOperator.getAggregateOperator(1);
		}
	}
	public AggregateOperator getY2AxisAggregateOpperator() {
		if (getY2AxisaggregateFunction() != null) {
			return AggregateOperator.getAggregateOperator(getY2AxisaggregateFunction());
		}
		else {
			return AggregateOperator.getAggregateOperator(1);
		}
	}
	public AggregateOperator getY3AxisAggregateOpperator() {
		if (getY3AxisaggregateFunction() != null) {
			return AggregateOperator.getAggregateOperator(getY3AxisaggregateFunction());
		}
		else {
			return AggregateOperator.getAggregateOperator(1);
		}
	}
	
	
	public Integer getxAxisaggregateFunction() {
		return xAxisaggregateFunction;
	}
	public void setxAxisaggregateFunction(Integer xAxisaggregateFunction) {
		this.xAxisaggregateFunction = xAxisaggregateFunction;
	}
	public Integer getY1AxisaggregateFunction() {
		return y1AxisaggregateFunction;
	}
	public void setY1AxisaggregateFunction(Integer y1AxisaggregateFunction) {
		this.y1AxisaggregateFunction = y1AxisaggregateFunction;
	}
	public Integer getY2AxisaggregateFunction() {
		return y2AxisaggregateFunction;
	}
	public void setY2AxisaggregateFunction(Integer y2AxisaggregateFunction) {
		this.y2AxisaggregateFunction = y2AxisaggregateFunction;
	}
	public Integer getY3AxisaggregateFunction() {
		return y3AxisaggregateFunction;
	}
	public void setY3AxisaggregateFunction(Integer y3AxisaggregateFunction) {
		this.y3AxisaggregateFunction = y3AxisaggregateFunction;
	}
	
	public List<ReportThreshold> getReportThresholds() {
		return reportThresholds;
	}
	public void setReportThresholds(List<ReportThreshold> reportThresholds) {
		this.reportThresholds = reportThresholds;
	}
	public void addReportThreshold(ReportThreshold reportThreshold) {
		if(this.reportThresholds == null) {
			reportThresholds = new ArrayList<>();
		}
		reportThresholds.add(reportThreshold);
	}
	public ReportFieldContext getxAxisField() {
		return xAxisField;
	}
	public void setxAxisField(ReportFieldContext xAxisField) {
		this.xAxisField = xAxisField;
	}
	public ReportFieldContext getY1AxisField() {
		return y1AxisField;
	}
	public void setY1AxisField(ReportFieldContext y1AxisField) {
		this.y1AxisField = y1AxisField;
	}
	public ReportFieldContext getY2AxisField() {
		return y2AxisField;
	}
	public void setY2AxisField(ReportFieldContext y2AxisField) {
		this.y2AxisField = y2AxisField;
	}
	public ReportFieldContext getY3AxisField() {
		return y3AxisField;
	}
	public void setY3AxisField(ReportFieldContext y3AxisField) {
		this.y3AxisField = y3AxisField;
	}
	public List<Long> getReportCriteriaIds() {
		return reportCriteriaIds;
	}
	public void setReportCriteriaIds(List<Long> reportCriteriaIds) {
		this.reportCriteriaIds = reportCriteriaIds;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public ReportChartType getReportChartType() {
		if(getChartType() != null) {
			return ReportChartType.getWidgetChartType(getChartType());
		}
		return null;
	}
	public Long getId() {
		return Id;
	}


	public void setId(Long id) {
		Id = id;
	}


	public Long getParentFolderId() {
		return parentFolderId;
	}


	public void setParentFolderId(Long parentFolderId) {
		this.parentFolderId = parentFolderId;
	}


	public Integer getChartType() {
		return chartType;
	}


	public void setChartType(Integer chartType) {
		this.chartType = chartType;
	}


	public Long getxAxis() {
		return xAxis;
	}


	public void setxAxis(Long xAxis) {
		this.xAxis = xAxis;
	}


	public Long getY1Axis() {
		return y1Axis;
	}


	public void setY1Axis(Long y1Axis) {
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


	public Boolean getIsComparisionReport() {
		return isComparisionReport;
	}


	public void setIsComparisionReport(Boolean isComparisionReport) {
		this.isComparisionReport = isComparisionReport;
	}


	public String getxAxisLegend() {
		return xAxisLegend;
	}


	public void setxAxisLegend(String xAxisLegend) {
		this.xAxisLegend = xAxisLegend;
	}


	public List<ReportCriteriaContext> getReportCriteriaContexts() {
		return reportCriteriaContexts;
	}


	public void setReportCriteriaContexts(List<ReportCriteriaContext> reportCriteriaContexts) {
		this.reportCriteriaContexts = reportCriteriaContexts;
	}
	
	public void addReportCriteriaContext(ReportCriteriaContext reportCriteriaContext) {
		if(this.reportCriteriaContexts == null) {
			this.reportCriteriaContexts = new ArrayList<>(); 
		}
		this.reportCriteriaContexts.add(reportCriteriaContext);
	}


	public enum ReportChartType {
		DOUGHNUT(1,"doughnut"),
		STACKBAR(2,"stackbar"),
		PROGRESS(3,"progress"),
		BAR(4,"bar");
		
		private int value;
		private String name;
		private int minHeight;
		private int minWidth;
		
		ReportChartType(int value,String name) {
			this.value = value;
			this.name = name;
		}
		ReportChartType(int value,String name,int minHeight,int minWidth) {
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
		
		public static ReportChartType getWidgetChartType(int value) {
			return REPORT_CHART_TYPE_MAP.get(value);
		}

		private static final Map<Integer, ReportChartType> REPORT_CHART_TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, ReportChartType> initTypeMap() {
			Map<Integer, ReportChartType> typeMap = new HashMap<>();
			for(ReportChartType type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}


	public Object widgetJsonObject() {
		
		JSONObject resultJson = new JSONObject();
		
		resultJson.put("type", "chart");
		JSONObject layoutJson = new JSONObject();
		layoutJson.put("height", 650);
		layoutJson.put("width", null);
		layoutJson.put("position", 1);
		
		resultJson.put("layout", layoutJson);
		
		JSONObject headerJson = new JSONObject();
		headerJson.put("title", getName());
		headerJson.put("subtitle", "today");
		headerJson.put("export",true);
		
//		if(!this.getPeriods().isEmpty()) {
//			JSONArray periods = new JSONArray();
//			for(WidgetPeriodContext period:this.getPeriods()) {
//				JSONObject periodJson = new JSONObject();
//				periodJson.put("label", period.getPeriodLabel());
//				periodJson.put("value", period.getPeriodValue());
//				periods.add(periodJson);
//			}
//			headerJson.put("periods", periods);
//		}
		
		resultJson.put("header", headerJson);
		
		JSONObject dataOptionsJson = new JSONObject();
//		dataOptionsJson.put("type", getWidgetChartType().getName());
		dataOptionsJson.put("dataurl", "/dashboard/getData?reportId="+getId());
		dataOptionsJson.put("name", "dummy");
		dataOptionsJson.put("reportId", getId());
		dataOptionsJson.put("type", getReportChartType().getName());
		dataOptionsJson.put("refresh_interval", 100);
		
		resultJson.put("dataOptions", dataOptionsJson);
		
		
		JSONObject widgetJson = new JSONObject();
		widgetJson.put("widget", resultJson);
		widgetJson.put("label", getName());
		System.out.println("resultJson -- "+widgetJson);
		return widgetJson;
	}
}
