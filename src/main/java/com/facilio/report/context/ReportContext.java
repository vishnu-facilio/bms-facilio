package com.facilio.report.context;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.AggregateOperator;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReadingAnalysisContext.AnalyticsType;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;

public class ReportContext {

	private long id = -1;
	
	int booleanSetting;
	public int getBooleanSetting() {
		return booleanSetting;
	}
	public void setBooleanSetting(int booleanSetting) {
		this.booleanSetting = booleanSetting;
	}
	
	public boolean getShowHideAlarm() {
		return ((booleanSetting & BooleanSettings.SHOW_HIDE_ALARM.booleanValue) == BooleanSettings.SHOW_HIDE_ALARM.booleanValue) ? true : false;  
	}
	
	public void setShowHideAlarm() {
		booleanSetting =  booleanSetting | BooleanSettings.SHOW_HIDE_ALARM.booleanValue;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private Criteria criteria = null;
	
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private long reportFolderId = -1;
	public long getReportFolderId() {
		return reportFolderId;
	}
	public void setReportFolderId(long reportFolderId) {
		this.reportFolderId = reportFolderId;
	}
	
	private WorkflowContext transformWorkflow;
	public WorkflowContext getTransformWorkflow() {
		return transformWorkflow;
	}
	public void setTransformWorkflow(WorkflowContext transformWorkflow) {
		this.transformWorkflow = transformWorkflow;
	}
	
	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	private String tabularState;
	public String getTabularState() {
		return tabularState;
	}
	public void setTabularState(String tabularState) {
		this.tabularState = tabularState;
	}
	private String commonState;
	public String getCommonState() {
		return commonState;
	}
	public void setCommonState(String commonState) {
		this.commonState = commonState;
	}

	private String chartState;
	public String getChartState() {
		return chartState;
	}
	public void setChartState(String chartState) {
		this.chartState = chartState;
	}
	
	private JSONObject reportState;
//	public JSONObject getReportState() {
//		return reportState;
//	}
//	public void setReportState(JSONObject reportState) {
//		this.reportState = reportState;
//	}
	
	//Only JSON friendly values can be added
	public void addToReportState (String key, JSONObject value) {
		defaultAddToReportState(key, value);
	}
	public void addToReportState (String key, JSONArray value) {
		defaultAddToReportState(key, value);
	}
	public void addToReportState (String key, String value) {
		defaultAddToReportState(key, value);
	}
	public void addToReportState (String key, Number value) {
		defaultAddToReportState(key, value);
	}
	public void addToReportState (String key, boolean value) {
		defaultAddToReportState(key, value);
	}
	
	private void defaultAddToReportState (String key, Object value) {
		if (reportState == null) {
			reportState = new JSONObject();
		}
		reportState.put(key, value);
	}
	public Object getFromReportState (String key) {
		if (reportState != null) {
			return reportState.get(key);
		}
		return null;
	}
	
	public String getReportStateJson() {
		if (reportState != null) {
			return reportState.toJSONString();
		}
		return null;
	}
	public void setReportStateJson(String reportStateJson) throws Exception {
		if (reportStateJson != null) {
			JSONParser parser = new JSONParser();
			this.reportState = (JSONObject) parser.parse(reportStateJson);
		}
	}

	private DateOperators dateOperator;
	public DateOperators getDateOperatorEnum() {
		return dateOperator;
	}
	public void setDateOperator(DateOperators dateOperator) {
		this.dateOperator = dateOperator;
	}
	public int getDateOperator() {
		if (dateOperator != null) {
			return dateOperator.getOperatorId();
		}
		return -1;
	}
	public void setDateOperator(int dateOperator) {
		if(dateOperator > 0) {
			Operator operator = Operator.OPERATOR_MAP.get(dateOperator);
			if (operator instanceof DateOperators) {
				this.dateOperator = (DateOperators) operator;
			}
			else {
				throw new IllegalArgumentException("Invalid Date Operator for report");
			}
		}
	}
	
	private String dateValue;
	public String getDateValue() {
		return dateValue;
	}
	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
	}
	
	private DateRange dateRange;
	public DateRange getDateRange() {
		return dateRange;
	}
	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
	
	@JSON(serialize=false)
	public String getDateRangeJson() throws Exception {
		if (dateRange != null) {
			return FieldUtil.getAsJSON(dateRange).toJSONString();
		}
		return null;
	}
	public void setDateRangeJson(String dateRange) throws Exception {
		JSONObject json = FacilioUtil.parseJson(dateRange);
		this.dateRange = FieldUtil.getAsBeanFromJson(json, DateRange.class);
	}
	
	private List<ReportDataPointContext> dataPoints;
	public List<ReportDataPointContext> getDataPoints() {
		return dataPoints;
	}
	public void setDataPoints(List<ReportDataPointContext> dataPoints) {
		this.dataPoints = dataPoints;
	}
	
	public void addDataPoint(ReportDataPointContext dataPoint) {
		
		this.dataPoints = this.dataPoints == null ? new ArrayList<>() : this.dataPoints;
		this.dataPoints.add(dataPoint);
	}
	
	
	public void setDataPointJson(String dataPointJson) throws Exception {
		
		JSONArray jsonarray = FacilioUtil.parseJsonArray(dataPointJson);
		
		for( Object jsonObject :jsonarray) {
			
			JSONObject json = (JSONObject) jsonObject;
			ReportDataPointContext dataPoint = FieldUtil.getAsBeanFromJson(json, ReportDataPointContext.class);
			
			addDataPoint(dataPoint);
		}
		
		if (xAggr == null) { //Temp code until all reports are migrated to new format
			JSONObject firstXAxisObj = (JSONObject) ((JSONObject) jsonarray.get(0)).get("xAxis");
			Long xAggrOpr = (Long) firstXAxisObj.get("aggr");
			if (xAggrOpr != null) {
				xAggr = AggregateOperator.getAggregateOperator(xAggrOpr.intValue());
			}
		}
	}
	
	@JSON(serialize=false)
	public String getDataPointJson() throws Exception {
		
		if (dataPoints != null) {
			return FieldUtil.getAsJSONArray(dataPoints, ReportDataPointContext.class).toJSONString();
		}
		return null;
	}
	
	private List<ReportBaseLineContext> baseLines;
	public List<ReportBaseLineContext> getBaseLines() {
		return baseLines;
	}
	public void setBaseLines(List<ReportBaseLineContext> baseLines) {
		this.baseLines = baseLines;
	}
	public void addBaseLines(ReportBaseLineContext baseLine) {
		
		this.baseLines = this.baseLines == null ? new ArrayList<>() : this.baseLines;
		baseLines.add(baseLine);
	}
	
	public void setBaselineJson(String baselineJson) throws Exception {
		JSONArray jsonarray = FacilioUtil.parseJsonArray(baselineJson);
		for( Object jsonObject :jsonarray) {
			JSONObject json = (JSONObject) jsonObject;
			ReportBaseLineContext dataPoint = FieldUtil.getAsBeanFromJson(json, ReportBaseLineContext.class);
			addBaseLines(dataPoint);
		}
	}
	@JSON(serialize=false)
	public String getBaselineJson() throws Exception {
		if (baseLines != null) {
			return FieldUtil.getAsJSONArray(baseLines, ReportBaseLineContext.class).toJSONString();
		}
		return null;
	}
	
	private List<ReportFilterContext> filters;
	public List<ReportFilterContext> getFilters() {
		return filters;
	}
	public void setFilters(List<ReportFilterContext> filters) {
		this.filters = filters;
	}
	
	@JSON(serialize=false)
	public String getFiltersJson() throws Exception {
		if (filters != null) {
			return FieldUtil.getAsJSONArray(filters, ReportFilterContext.class).toJSONString();
		}
		return null;
	}
	public void setFiltersJson(String filtersJson) throws Exception {
		JSONArray json = FacilioUtil.parseJsonArray(filtersJson);
		this.filters = FieldUtil.getAsBeanListFromJsonArray(json, ReportFilterContext.class);
	}
	
	private String xAlias;
	public String getxAlias() {
		return xAlias;
	}
	public void setxAlias(String xAlias) {
		this.xAlias = xAlias;
	}
	
	private AggregateOperator xAggr;
	public int getxAggr() {
		if (xAggr != null) {
			return xAggr.getValue();
		}
		return -1;
	}
	public void setxAggr(int xAggr) {
		this.xAggr = AggregateOperator.getAggregateOperator(xAggr);
	}
	public AggregateOperator getxAggrEnum() {
		return xAggr;
	}
	public void setxAggr(AggregateOperator xAggr) {
		this.xAggr = xAggr;
	}
	
	private AnalyticsType analyticsType;
	public int getAnalyticsType() {
		if(analyticsType != null) {
			return analyticsType.getIntVal();
		}
		return -1;
	}
	public AnalyticsType getAnalyticsTypeEnum() {
		return analyticsType;
	}
	public void setAnalyticsType(int type) {
		this.analyticsType = AnalyticsType.getType(type);
	}
	public void setAnalyticsType(AnalyticsType analyticsType) {
		this.analyticsType = analyticsType;
	}
	
	private ReportType type;
	public ReportType getTypeEnum() {
		return type;
	}
	public void setType(ReportType type) {
		this.type = type;
	}
	public int getType() {
		if (type != null) {
			return type.getValue();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = ReportType.valueOf(type);
	}
	
	private int moduleType = -1;
	public int getModuleType() {
		return moduleType;
	}
	public void setModuleType(int moduleType) {
		this.moduleType = moduleType;
	}

	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private FacilioModule module;

	public FacilioModule getModule() throws Exception {
		if(module == null && moduleId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			module = modBean.getModule(moduleId);
		}
		return module;
	}
	public void setModule(FacilioModule module) {
		this.module = module;
	}

	private Class<? extends TransformReportDataIfc> transformClass;
	public Class<? extends TransformReportDataIfc> getTransformClassObject() {
		return transformClass;
	}
	public void setTransformClass(Class<? extends TransformReportDataIfc> transformClass) {
		this.transformClass = transformClass;
	}
	public String getTransformClass() {
		if (transformClass != null) {
			return transformClass.getName();
		}
		return null;
	}
	public void setTransformClass(String transformClass) throws ClassNotFoundException {
		this.transformClass = (Class<? extends TransformReportDataIfc>) Class.forName(transformClass);
	}
	
	private List<ReportUserFilterContext> userFilters;
	public List<ReportUserFilterContext> getUserFilters() {
		return userFilters;
	}
	public void setUserFilters(List<ReportUserFilterContext> userFilters) {
		this.userFilters = userFilters;
	}
	// called when setting from client
	public void setUserFilters(List<ReportUserFilterContext> userFilters, boolean updateField) {
		if (userFilters == null) {	// return if its null
			return;
		}
		setUserFilters(userFilters);
		if (updateField && CollectionUtils.isNotEmpty(userFilters)) {
			for (ReportUserFilterContext userFilterContext : userFilters) {
				try {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					userFilterContext.setField(modBean.getField(userFilterContext.getFieldId()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setUserFiltersJson(String data) throws Exception {
		JSONArray jsonarray = FacilioUtil.parseJsonArray(data);
		
		List<ReportUserFilterContext> userFilters = null;
		for( Object jsonObject :jsonarray) {
			if (userFilters == null) {
				userFilters = new ArrayList<>();
			}
			
			JSONObject json = (JSONObject) jsonObject;
			ReportUserFilterContext userFilter = FieldUtil.getAsBeanFromJson(json, ReportUserFilterContext.class);
			
			userFilters.add(userFilter);
		}
		setUserFilters(userFilters);
	}
	
	@JSON(serialize=false)
	public String getUserFiltersJson() throws Exception {
		
		if (userFilters != null) {
			return FieldUtil.getAsJSONArray(userFilters, ReportUserFilterContext.class).toJSONString();
		}
		return null;
	}

	public static enum BooleanSettings {
		SHOW_HIDE_ALARM("Alarm",1),
		SHOW_HIDE_SAFELIMIT("Safe Limit",2),
		SHOW_HIDE_LEGENT("Legent",4),
		;
		
		String name;
		int booleanValue;
		public int getIntValue() {
			return ordinal() + 1;
		}
		public String getName() {
			return name;
		}
		BooleanSettings(String name, int booleanValue) {
			this.name = name;
			this.booleanValue = booleanValue;
		}
		
		public static BooleanSettings valueOf (int value) {
			if(value >= 0 && value < values().length) {
				return values()[value+1];
			}
			return null;
		}

	}
	
	public enum ReportType {
		READING_REPORT,
		WORKORDER_REPORT
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		public static ReportType valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
}
