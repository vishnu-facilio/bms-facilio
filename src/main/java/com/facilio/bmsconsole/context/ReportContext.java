package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.ModuleBaseWithCustomFields;


public class ReportContext extends ModuleBaseWithCustomFields {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Long parentFolderId;
	
	boolean isWorkRequestReport;
	
	
	public boolean isWorkRequestReport() {
		return isWorkRequestReport;
	}

	public void setWorkRequestReport(boolean isWorkRequestReport) {
		this.isWorkRequestReport = isWorkRequestReport;
	}
	
	public boolean getIsWorkRequestReport() {
		return isWorkRequestReport;
	}

	public void setIsWorkRequestReport(boolean isWorkRequestReport) {
		this.isWorkRequestReport = isWorkRequestReport;
	}

	ReportFolderContext reportFolderContext;
	String customReportClass;
	public String getCustomReportClass() {
		return customReportClass;
	}

	public void setCustomReportClass(String customReportClass) {
		this.customReportClass = customReportClass;
	}

	public ReportFolderContext getReportFolderContext() {
		return reportFolderContext;
	}

	public void setReportFolderContext(ReportFolderContext reportFolderContext) {
		this.reportFolderContext = reportFolderContext;
	}

	List<ReportBenchmarkRelContext> reportBenchmarkRelContexts;
	
	public List<ReportBenchmarkRelContext> getReportBenchmarkRelContexts() {
		return reportBenchmarkRelContexts;
	}

	public void setReportBenchmarkRelContexts(List<ReportBenchmarkRelContext> reportBenchmarkRelContexts) {
		this.reportBenchmarkRelContexts = reportBenchmarkRelContexts;
	}
	
	public void addReportBenchmarkRelContexts(ReportBenchmarkRelContext reportBenchmarkRelContext) {
		if(reportBenchmarkRelContexts == null) {
			reportBenchmarkRelContexts = new ArrayList<>();
		}
		reportBenchmarkRelContexts.add(reportBenchmarkRelContext);
	}

	String name;
	String description;
	Integer chartType;
	Integer secChartType;
	Long xAxis;
	ReportFieldContext xAxisField;
	Integer xAxisaggregateFunction;
	String xAxisLabel;
	String xAxisUnit;
	Long y1Axis;
	ReportFieldContext y1AxisField;
	Integer y1AxisaggregateFunction;
	String y1AxisLabel;
	String y1AxisUnit;
	Long y2Axis;
	ReportFieldContext y2AxisField;
	Integer y2AxisaggregateFunction;
	Long y3Axis;
	ReportFieldContext y3AxisField;
	Integer y3AxisaggregateFunction;
	ReportEnergyMeterContext energyMeter;
	String orderBy;
	Integer orderByFunction;
	ReportSpaceFilterContext reportSpaceFilterContext;
	Long groupBy;
	ReportFieldContext groupByField;
	String groupByLabel;
	String groupByUnit;
	Integer groupByFieldAggregateFunction;
	Integer limit;
	Long reportEntityId;
	List<ReportContext> comparingReportContexts;
	JSONObject metaJson;
	
	public JSONObject getMetaJson() {
		return metaJson;
	}

	public void setMetaJson(JSONObject metaJson) {
		this.metaJson = metaJson;
	}

	private LegendMode legendMode;
	
	public void setLegendMode(LegendMode legendMode) {
		this.legendMode = legendMode;
	}
	
	public LegendMode getLegendMode() {
		return this.legendMode;
	}
	
	public Long getReportEntityId() {
		return reportEntityId;
	}

	public void setReportEntityId(Long reportEntityId) {
		this.reportEntityId = reportEntityId;
	}

	public ReportSpaceFilterContext getReportSpaceFilterContext() {
		return reportSpaceFilterContext;
	}

	public void setReportSpaceFilterContext(ReportSpaceFilterContext reportSpaceFilterContext) {
		this.reportSpaceFilterContext = reportSpaceFilterContext;
	}

	public List<ReportContext> getComparingReportContexts() {
		return comparingReportContexts;
	}

	public void setComparingReportContexts(List<ReportContext> comparingReportContexts) {
		this.comparingReportContexts = comparingReportContexts;
	}
	
	public void addComparingReportContext(ReportContext comparingReportContext) {
		if(this.comparingReportContexts == null) {
			this.comparingReportContexts = new ArrayList<>();
		}
		this.comparingReportContexts.add(comparingReportContext);
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getOrderByFunction() {
		return orderByFunction;
	}
	public OrderByFunction getOrderByFunc() {
		if(getOrderByFunction() != null) {
			return OrderByFunction.getOrderByFunction(getOrderByFunction());
		}
		return null;
	}
	public void setOrderByFunction(Integer orderByFunction) {
		this.orderByFunction = orderByFunction;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Long getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(Long groupBy) {
		this.groupBy = groupBy;
	}

	public ReportFieldContext getGroupByField() throws Exception {
		if (groupByField == null && groupBy != null) {
			groupByField = new ReportFieldContext();
			groupByField.setId(groupBy);
			groupByField = DashboardUtil.getReportField(groupByField);
		}
		return groupByField;
	}

	public void setGroupByField(ReportFieldContext groupByField) {
		this.groupByField = groupByField;
	}

	public Integer getGroupByFieldAggregateFunction() {
		return groupByFieldAggregateFunction;
	}

	public void setGroupByFieldAggregateFunction(Integer groupByFieldAggregateFunction) {
		this.groupByFieldAggregateFunction = groupByFieldAggregateFunction;
	}


	Boolean excludeBaseline;
	Integer reportOrder;
	
	public Boolean getExcludeBaseline() {
		return excludeBaseline;
	}

	public void setExcludeBaseline(Boolean excludeBaseline) {
		this.excludeBaseline = excludeBaseline;
	}

	public Integer getReportOrder() {
		return reportOrder;
	}

	public void setReportOrder(Integer reportOrder) {
		this.reportOrder = reportOrder;
	}


	Boolean isComparisionReport;
	Boolean isHighResolutionReport;
	Boolean isCombinationReport;
	String xAxisLegend;
	List<BaseLineContext> baseLineContexts;
	public List<BaseLineContext> getBaseLineContexts() {
		return baseLineContexts;
	}

	public void setBaseLineContexts(List<BaseLineContext> baseLineContexts) {
		this.baseLineContexts = baseLineContexts;
	}
	public void addReportBaseLineContext(BaseLineContext baseLineContext) {
		if(baseLineContexts == null) {
			baseLineContexts = new ArrayList<>();
		}
		baseLineContexts.add(baseLineContext);
	}

	public BaseLineContext getBaseLineContext(long baseLineId) {
		if(baseLineContexts != null) {
			for(BaseLineContext baseLineContext :baseLineContexts) {
				if(baseLineContext.getId() == baseLineId) {
					return baseLineContext;
				}
			}
		}
		return null;
	}

	List<ReportUserFilterContext> reportUserFilters;
	List<Long> reportCriteriaIds;
	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	Criteria criteria;
	
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
	
	public AggregateOperator getGroupByAggregateOpperator() {
		if (getGroupByFieldAggregateFunction() != null) {
			return AggregateOperator.getAggregateOperator(getGroupByFieldAggregateFunction());
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
	public ReportFieldContext getxAxisField() throws Exception {
		if (xAxisField == null && xAxis != null) {
			xAxisField = new ReportFieldContext();
			xAxisField.setId(xAxis);
			DashboardUtil.getReportField(xAxisField);
		}
		return xAxisField;
	}
	public void setxAxisField(ReportFieldContext xAxisField) {
		this.xAxisField = xAxisField;
	}
	public ReportFieldContext getY1AxisField() throws Exception {
		if (y1AxisField == null && y1Axis != null) {
			y1AxisField = new ReportFieldContext();
			y1AxisField.setId(y1Axis);
			DashboardUtil.getReportField(y1AxisField);
		}
		return y1AxisField;
	}
	public void setY1AxisField(ReportFieldContext y1AxisField) {
		this.y1AxisField = y1AxisField;
	}
	public ReportFieldContext getY2AxisField() {
		if (y2AxisField == null && y2Axis != null) {
			y2AxisField = new ReportFieldContext();
			y2AxisField.setId(y2Axis);
		}
		return y2AxisField;
	}
	public void setY2AxisField(ReportFieldContext y2AxisField) {
		this.y2AxisField = y2AxisField;
	}
	public ReportFieldContext getY3AxisField() {
		if (y3AxisField == null && y3Axis != null) {
			y3AxisField = new ReportFieldContext();
			y3AxisField.setId(y3Axis);
		}
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public ReportChartType getReportChartType() {
		if(getChartType() != null) {
			return ReportChartType.getWidgetChartType(getChartType());
		}
		return null;
	}
	
	public ReportChartType getReportSecChartType() {
		if(getSecChartType() != null) {
			return ReportChartType.getWidgetChartType(getSecChartType());
		}
		return null;
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
	
	public Integer getSecChartType() {
		return secChartType;
	}
	
	private String chartTypeString;
	
	public void setChartTypeString(String chartTypeString) {
		this.chartTypeString = chartTypeString;
		if(chartTypeString != null) {
			this.chartType = ReportChartType.getWidgetChartType(chartTypeString).getValue();
		}
	}
	
	public String getChartTypeString() {
		return this.chartTypeString;
	}

	public void setSecChartType(Integer secChartType) {
		this.secChartType = secChartType;
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
		if(isComparisionReport != null) {
			return isComparisionReport;
		}
		return Boolean.FALSE;
	}

	public List<ReportColumnContext> reportColumns;
	public List<ReportColumnContext> getReportColumns() {
		return reportColumns;
	}
	public void setReportColumns(List<ReportColumnContext> reportColumns) {
		this.reportColumns = reportColumns;
	}

	public void setIsComparisionReport(Boolean isComparisionReport) {
		this.isComparisionReport = isComparisionReport;
	}
	
	public Boolean getIsHighResolutionReport() {
		if(isHighResolutionReport != null) {
			return isHighResolutionReport;
		}
		return Boolean.FALSE;
	}

	public void setIsHighResolutionReport(Boolean isHighResolutionReport) {
		this.isHighResolutionReport = isHighResolutionReport;
	}
	
	public Boolean getIsCombinationReport() {
		if (isCombinationReport != null) {
			return isCombinationReport;
		}
		return Boolean.FALSE;
	}

	public void setIsCombinationReport(Boolean isCombinationReport) {
		this.isCombinationReport = isCombinationReport;
	}


	public String getxAxisLegend() {
		return xAxisLegend;
	}


	public void setxAxisLegend(String xAxisLegend) {
		this.xAxisLegend = xAxisLegend;
	}
	
	public enum OrderByFunction {
		ACCENDING (1,"ASC"),
		DESCENDING (2,"DESC");
		
		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getStringValue() {
			return stringValue;
		}

		public void setStringValue(String stringValue) {
			this.stringValue = stringValue;
		}

		private int value;
		private String stringValue;
		
		OrderByFunction(int value,String stringValue) {
			
			this.value = value;
			this.stringValue = stringValue;
		}
		
		public static OrderByFunction getOrderByFunction(int value) {
			return ORDER_BY_FUNCTION_MAP.get(value);
		}

		private static final Map<Integer, OrderByFunction> ORDER_BY_FUNCTION_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, OrderByFunction> initTypeMap() {
			Map<Integer, OrderByFunction> typeMap = new HashMap<>();
			for(OrderByFunction type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}


	public enum ReportChartType {
		PIE (1, "pie", 1, 1),
		DOUGHNUT (2, "doughnut", 1, 1),
		BAR (3, "bar", 1, 1),
		STACKED_BAR (4, "stackedbar", 1, 2),
		GROUPED_BAR (5, "groupedbar", 1, 2),
		LINE (6, "line", 1, 1),
		AREA (7, "area", 1, 1),
		FUNNEL (8, "funel", 1, 1),
		PROGRESS (9, "progress", 1, 1),
		GAUGE (10, "gauge", 1, 1),
		HEATMAP(11,"heatMap",1,1),
		TIMESERIES (12,"timeseries",1,1),
		MATRIX (13,"matrix",1,1),
		TABULAR (14,"tabular",1,1),
		SCATTER (15,"scatter",1,1),
		REGRESSION (16,"regression",1,1),
		TREEMAP (17,"treemap",1,1),
		BOOLEANGRAPH (18,"boolean",1,1),
		HISTOGRAM(19,"histogram",1,1);
		
		private int value;
		private String name;
		private int minHeight;
		private int minWidth;
		private int minXAxis = 1;
		private int minYAxis = 1;
		
		ReportChartType(int value,String name,int minXAxis,int minYAxis) {
			this.value = value;
			this.name = name;
			this.minXAxis = minXAxis;
			this.minYAxis = minYAxis;
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
		
		public static ReportChartType getWidgetChartType(String chartType) {
			for (ReportChartType rtype : ReportChartType.values()) {
				if (rtype.getName().equalsIgnoreCase(chartType)) {
					return rtype;
				}
			}
			return null;
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
		if(getReportChartType() != null) {
			dataOptionsJson.put("type", getReportChartType().getName());
		}
		else {
			dataOptionsJson.put("type", ReportChartType.PIE.getName());
		}
		dataOptionsJson.put("refresh_interval", 100);
		
		resultJson.put("dataOptions", dataOptionsJson);
		
		
		JSONObject widgetJson = new JSONObject();
		widgetJson.put("widget", resultJson);
		widgetJson.put("label", getName());
//		System.out.println("resultJson -- "+widgetJson);
		return widgetJson;
	}
	
	private ReportDateFilterContext dateFilter;
	
	public void setDateFilter(ReportDateFilterContext dateFilter) {
		this.dateFilter = dateFilter;
	}
	
	public ReportDateFilterContext getDateFilter() {
		return this.dateFilter;
	}
	
	public void setEnergyMeter(ReportEnergyMeterContext energyMeter) {
		this.energyMeter = energyMeter;
	}
	
//	public ReportEnergyMeterContext getEnergyMeter() {
//		return this.energyMeter;
//	}

	public String getxAxisLabel() throws Exception {
		if (this.xAxisLabel == null && this.getxAxisField() != null && this.getxAxisField().getField() != null) {
			return this.getxAxisField().getField().getDisplayName();
		}
		return xAxisLabel;
	}

	public void setxAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	public String getxAxisUnit() {
		return xAxisUnit;
	}

	public void setxAxisUnit(String xAxisUnit) {
		this.xAxisUnit = xAxisUnit;
	}

	public String getY1AxisLabel() throws Exception {
		if (this.y1AxisLabel == null && this.getY1AxisField() != null && this.getY1AxisField().getField() != null) {
			return this.getY1AxisField().getField().getDisplayName();
		}
		return y1AxisLabel;
	}

	public void setY1AxisLabel(String y1AxisLabel) {
		this.y1AxisLabel = y1AxisLabel;
	}

	public String getY1AxisUnit() {
		return y1AxisUnit;
	}

	public void setY1AxisUnit(String y1AxisUnit) {
		this.y1AxisUnit = y1AxisUnit;
	}

	public String getGroupByLabel() throws Exception {
		if (this.groupByLabel == null && this.getGroupByField() != null && this.getGroupByField().getField() != null) {
			return this.getGroupByField().getField().getDisplayName();
		}
		return groupByLabel;
	}

	public void setGroupByLabel(String groupByLabel) {
		this.groupByLabel = groupByLabel;
	}

	public String getGroupByUnit() {
		return groupByUnit;
	}

	public void setGroupByUnit(String groupByUnit) {
		this.groupByUnit = groupByUnit;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long baseLineId = -1;
	public long getBaseLineId() {
		return baseLineId;
	}
	public void setBaseLineId(long baseLineId) {
		this.baseLineId = baseLineId;
	}
	
	private String reportColor;
	
	public String getReportColor() {
		return this.reportColor;
	}
	
	public void setReportColor(String reportColor) {
		this.reportColor = reportColor;
	}
	
	private DerivationContext derivation;
	public DerivationContext getDerivation() {
		return derivation;
	}
	public void setDerivation(DerivationContext derivation) {
		this.derivation = derivation;
	}

	public enum LegendMode {
		RESOURCE_NAME (1),
		READING_NAME (2),
		RESOURCE_WITH_READING_NAME (3);
		
		private int value;
		
		LegendMode(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}
}
