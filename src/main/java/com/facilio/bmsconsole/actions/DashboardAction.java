package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.ReportsChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ReportContext.LegendMode;
import com.facilio.bmsconsole.context.ReportContext.ReportChartType;
import com.facilio.bmsconsole.context.WorkOrderRequestContext.WORUrgency;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.BmsAggregateOperators.*;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.report.customreport.CustomReport;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.workflows.util.WorkflowUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.chain.Command;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.CollectionUtils;

//import com.facilio.workflows.context.WorkflowExpression;

public class DashboardAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(DashboardAction.class.getName());
	
	Long baseLineComparisionDiff;
	
	Long reportEntityId;
	
	public Long getReportEntityId() {
		return reportEntityId;
	}
	public void setReportEntityId(Long reportEntityId) {
		this.reportEntityId = reportEntityId;
	}
	public Long getBaseLineComparisionDiff() {
		return baseLineComparisionDiff;
	}
	public void setBaseLineComparisionDiff(Long baseLineComparisionDiff) {
		this.baseLineComparisionDiff = baseLineComparisionDiff;
	}
	boolean isHeatMap = false;
	public boolean getIsHeatMap() {
		return isHeatMap;
	}
	String reportName;
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public void setIsHeatMap(boolean isHeatMap) {
		this.isHeatMap = isHeatMap;
	}
	public String xaxisLegent;
	public String getXaxisLegent() {
		return xaxisLegent;
	}
	public void setXaxisLegent(String xaxisLegent) {
		this.xaxisLegent = xaxisLegent;
	}
	public DashboardContext getDashboard() {
		return dashboard;
	}
	public void setDashboard(DashboardContext dashboardContext) {
		this.dashboard = dashboardContext;
	}
	public DashboardWidgetContext getDashboardWidget() {
		return dashboardWidget;
	}
	public void setDashboardWidget(DashboardWidgetContext dashboardWidgetContext) {
		this.dashboardWidget = dashboardWidgetContext;
	}
	private DashboardContext dashboard;
	public WidgetChartContext getWidgetChartContext() {
		return widgetChartContext;
	}
	public void setWidgetChartContext(WidgetChartContext widgetChartContext) {
		this.widgetChartContext = widgetChartContext;
	}
	public WidgetListViewContext getWidgetListViewContext() {
		return widgetListViewContext;
	}
	public void setWidgetListViewContext(WidgetListViewContext widgetListViewContext) {
		this.widgetListViewContext = widgetListViewContext;
	}
	private List<DashboardContext> dashboards;
	private DashboardWidgetContext dashboardWidget;
	private WidgetChartContext widgetChartContext;
	private WidgetListViewContext widgetListViewContext;
	private WidgetStaticContext widgetStaticContext;
	private WidgetWebContext widgetWebContext;
	private DashboardFilterContext dashboardFilter;
	
	public DashboardFilterContext getDashboardFilter() {
		return this.dashboardFilter;
	}
	public void setDashboardFilter(DashboardFilterContext dashboardFilter) {
		this.dashboardFilter = dashboardFilter;
	}
	public WidgetWebContext getWidgetWebContext() {
		return widgetWebContext;
	}
	public void setWidgetWebContext(WidgetWebContext widgetWebContext) {
		this.widgetWebContext = widgetWebContext;
	}
	
	public WidgetStaticContext getWidgetStaticContext() {
		return widgetStaticContext;
	}
	public void setWidgetStaticContext(WidgetStaticContext widgetStaticContext) {
		this.widgetStaticContext = widgetStaticContext;
	}
	
	public List<DashboardContext> getDashboards() {
		return dashboards;
	}
	public void setDashboards(List<DashboardContext> dashboards) {
		this.dashboards = dashboards;
	}
	
	private Long dashboardId;
	private int dashboardPublishStatus;
	
	public int getDashboardPublishStatus() {
		return dashboardPublishStatus;
	}
	public void setDashboardPublishStatus(int dashboardPublishStatus) {
		this.dashboardPublishStatus = dashboardPublishStatus;
	}
	public Long getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(Long dashboardId) {
		this.dashboardId = dashboardId;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public JSONArray reportData;
	public JSONArray getReportData() {
		return this.reportData;
	}
	
	public void setReportData(JSONArray reportData) {
		this.reportData = reportData;
	}
	private JSONArray relatedAlarms;
	
	public JSONArray getRelatedAlarms() {
		return relatedAlarms;
	}
	public void setRelatedAlarms(JSONArray releatedAlarms) {
		this.relatedAlarms = releatedAlarms;
	}
	Long reportId;
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	long baseLineId = -1;
	public long getBaseLineId() {
		return baseLineId;
	}
	public void setBaseLineId(long baseLineId) {
		this.baseLineId = baseLineId;
	}
	
	long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	private JSONArray dateFilter;
	public JSONArray getDateFilter() {
		return dateFilter;
	}
	public void setDateFilter(JSONArray dateFilter) {
		this.dateFilter = dateFilter;
	}
	public Long dateFilterId;
	
	public Long getDateFilterId() {
		return dateFilterId;
	}
	public void setDateFilterId(Long dateFilterId) {
		this.dateFilterId = dateFilterId;
	}
	ReportSpaceFilterContext reportSpaceFilterContext;
	
	public ReportSpaceFilterContext getReportSpaceFilterContext() {
		return reportSpaceFilterContext;
	}
	public void setReportSpaceFilterContext(ReportSpaceFilterContext reportSpaceFilterContext) {
		this.reportSpaceFilterContext = reportSpaceFilterContext;
	}

	String period;
	public String getPeriod() {
		return period;
	}
	public ReportFolderContext getReportFolderContext() {
		return reportFolderContext;
	}
	public void setReportFolderContext(ReportFolderContext reportFolderContext) {
		this.reportFolderContext = reportFolderContext;
	}
	public ReportContext getReportContext() {
		return reportContext;
	}
	public String getReport() throws Exception {
		
		reportContext = DashboardUtil.getReportContext(reportId);
		return SUCCESS;
	}
	public void setReportContext(ReportContext reportContext) {
		this.reportContext = reportContext;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	private ReportFolderContext reportFolderContext;
	private ReportContext reportContext;
	public ReportThreshold getReportThreshold() {
		return reportThreshold;
	}
	public void setReportThreshold(ReportThreshold reportThreshold) {
		this.reportThreshold = reportThreshold;
	}
	private JSONObject userFilterValues;
	
	public JSONObject getUserFilterValues() {
		return userFilterValues;
	}
	public void setUserFilterValues(JSONObject userFilterValues) {
		this.userFilterValues = userFilterValues;
	}
	private ReportThreshold reportThreshold;
	public JSONObject reportFieldLabelMap;
	
	public JSONObject getReportFieldLabelMap() {
		return reportFieldLabelMap;
	}
	public void setReportFieldLabelMap(JSONObject reportFieldLabelMap) {
		this.reportFieldLabelMap = reportFieldLabelMap;
	}
	public String populateDefaultReports() throws Exception {
		
		LOGGER.severe("From here");
		FacilioContext context = new FacilioContext();
		context.put("orgId", AccountUtil.getCurrentOrg().getId());
		FacilioChain adddefaultReportChain = FacilioChainFactory.addDefaultReportChain();
		adddefaultReportChain.execute(context);
		
		return SUCCESS;
	}

	String errorString = "";
	
	public String getErrorString() {
		return errorString;
	}
	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}
	private List<ReportContext> reports;
	public List<ReportContext> getReports() {
		return reports;
	}
	public void setReports(List<ReportContext> reports) {
		this.reports = reports;
	}
	
	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}
	
	private Integer refreshInterval = null;
	
	public void setRefreshInterval(Integer refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
	
	public Integer getRefreshInterval() {
		return this.refreshInterval;
	}
	
	public String updateRefreshInterval() throws Exception {
		
		if (getWidgetId() != null && getRefreshInterval() != null) {
	
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getWidgetModule().getTableName())
					.fields(FieldFactory.getWidgetFields())
					.andCustomWhere("ID = ?", getWidgetId());

			Map<String, Object> props = new HashMap<String, Object>();
			props.put("dataRefreshIntervel", getRefreshInterval());
			
			updateBuilder.update(props);
		}
		
		return SUCCESS;
	}
	
	private String chartType;
	
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	
	public String getChartType() {
		return this.chartType;
	}
	
	private String secChartType;
	
	public void setSecChartType(String secChartType) {
		this.secChartType = secChartType;
	}
	
	public String getSecChartType() {
		return this.secChartType;
	}
	public boolean isReportUpdateFromDashboard;
	
	public boolean getIsReportUpdateFromDashboard() {
		return isReportUpdateFromDashboard;
	}
	public void setIsReportUpdateFromDashboard(boolean isReportUpdateFromDashboard) {
		this.isReportUpdateFromDashboard = isReportUpdateFromDashboard;
	}
	
	private JSONArray comboChartList;
	
	public void setComboChartList(JSONArray comboChartList) {
		this.comboChartList = comboChartList;
	}
	
	public JSONArray getComboChartList() {
		return this.comboChartList;
	}
	
	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}
	
	private String xLabel;
	public String getxLabel() {
		return xLabel;
	}
	public void setxLabel(String xLabel) {
		this.xLabel = xLabel;
	}
	
	private String yLabel;
	public String getyLabel() {
		return yLabel;
	}
	public void setyLabel(String yLabel) {
		this.yLabel = yLabel;
	}
	
	private String yUnit;
	public String getyUnit() {
		return yUnit;
	}
	public void setyUnit(String yUnit) {
		this.yUnit = yUnit;
	}
	
	private int xAggr = -1;
	public int getxAggr() {
		return xAggr;
	}
	public void setxAggr(int xAggr) {
		this.xAggr = xAggr;
	}
	
	private int yAggr = -1;
	public int getyAggr() {
		return yAggr;
	}
	public void setyAggr(int yAggr) {
		this.yAggr = yAggr;
	}
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private FacilioModule reportModule;
	public FacilioModule getReportModule() {
		return reportModule;
	}
	public void setReportModule(FacilioModule reportModule) {
		this.reportModule = reportModule;
	}
	
	JSONObject consolidateDataPoints;
	
	public JSONObject getConsolidateDataPoints() {
		return consolidateDataPoints;
	}
	public void setConsolidateDataPoints(JSONObject consolidateDataPoints) {
		this.consolidateDataPoints = consolidateDataPoints;
	}
	
	boolean cost = false;
	
	public void setCost(boolean cost) {
		this.cost = cost;
	}
	
	public boolean getCost() {
		return this.cost;
	}
	
	private JSONObject heatMapRange;
	
	public void setHeatMapRange(JSONObject heatMapRange) {
		this.heatMapRange = heatMapRange;
	}
	
	public JSONObject getHeatMapRange() {
		return this.heatMapRange;
	}
	
//	
	public String getReadingReportData() throws Exception {							// report functions
		if (derivation != null) {
			return getDerivationData();
		}
		
		if(consolidateDataPoints != null) {
			List<JSONArray> reportDatas = new ArrayList<>();
			for(Object parentId : consolidateDataPoints.keySet()) {
				readingFieldId = (long) consolidateDataPoints.get(parentId);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField readingField = modBean.getFieldFromDB(readingFieldId);
				reportModule = readingField.getModule();
				if(yAggr == 0) {
					if(readingField instanceof NumberField) {
						NumberField numberField = (NumberField) readingField;
						if(numberField.getMetricEnum() != null && numberField.getMetric() == Metric.ENERGY.getMetricId()) {
							yAggr = NumberAggregateOperator.SUM.getValue();
						}
						else if (readingField.getName().equals("totalEnergyConsumptionDelta")) {
							yAggr = NumberAggregateOperator.SUM.getValue();
						}
						else {
							yAggr = NumberAggregateOperator.AVERAGE.getValue();
						}
					}
					else {
						yAggr = NumberAggregateOperator.AVERAGE.getValue();
					}
				}
				reportContext = constructReportObjectForReadingReport(readingField.getModule(), readingField,Long.parseLong(parentId.toString()));
				reportData = getDataForReadings(reportContext, readingField.getModule(), dateFilter, null, baseLineId, -1);
				reportDatas.add(reportData);
			}
			reportData = DashboardUtil.consolidateResult(reportDatas, xAggr, yAggr);
			return SUCCESS;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField readingField = modBean.getFieldFromDB(readingFieldId);
		
		String parentName = ResourceAPI.getResource(this.parentId).getName();
		this.entityName = parentName + " ("+readingField.getDisplayName()+")";
		
		FacilioModule module = readingField.getModule();
		reportContext = constructReportObjectForReadingReport(module, readingField,parentId);
		if (getCost() && reportContext.getY1AxisField() != null) {
			reportContext.setY1AxisUnit("cost");
		}
		reportModule = module;
		reportData = getDataForReadings(reportContext, module, dateFilter, null, baseLineId, -1);
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	private String getDerivationData() throws Exception {												// report functions
		WorkflowContext workflow = WorkflowUtil.getWorkflowContext(derivation.getWorkflowId(), true);
//		workflow.setFromDerivation(true);
		List<DateRange> intervals;
		if (xAggr != 0) {
			FacilioFrequency frequency = DashboardUtil.getAggrFrequency(xAggr);
			ScheduleInfo schedule = FormulaFieldAPI.getSchedule(frequency);
			intervals= schedule.getTimeIntervals((Long)dateFilter.get(0),(Long) dateFilter.get(1));
		}
		else {
			int minuteInterval = ReadingsAPI.getDataInterval(workflow);
			intervals= DateTimeUtil.getTimeIntervals((Long)dateFilter.get(0),(Long) dateFilter.get(1), minuteInterval);
		}
		
		List<ReadingContext> readingValues = FormulaFieldAPI.calculateFormulaReadings(-1, derivation.getName(), derivation.getName(), intervals, workflow, false, false);
		reportData = new JSONArray();
		if (readingValues != null) {
			readingValues.forEach(value -> {
				JSONObject obj =  new JSONObject(); 
				obj.put("label", value.getReading("startTime"));
				obj.put("value", value.getReading(derivation.getName()));
				reportData.add(obj);
			});
		}
		yLabel = derivation.getName();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("energydata");
		FacilioField readingField = new FacilioField();
		readingField.setName(derivation.getName());
		reportContext = constructReportObjectForReadingReport(module, readingField,parentId);
		setEntityName(derivation.getName());
		
		return SUCCESS;
	}
	
	Long alarmId;
	
	public Long getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(Long alarmId) {
		this.alarmId = alarmId;
	}
	JSONObject reportMeta;
	
	public JSONObject getReportMeta() {
		return reportMeta;
	}
	public void setReportMeta(JSONObject reportMeta) {
		this.reportMeta = reportMeta;
	}
	public String getReportMetaForAlarms() throws Exception {								// report functions
		
		AlarmContext alarm = AlarmAPI.getAlarm(alarmId);
		
		ReadingAlarmContext readingAlarmContext = AlarmAPI.getReadingAlarmContext(alarmId);
		
		ReadingRuleContext readingruleContext = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(readingAlarmContext.getRuleId());

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		JSONArray dataPoints = new JSONArray();
		
		if(readingruleContext != null) {
			ResourceContext resource = ResourceAPI.getResource(alarm.getResource().getId());
			ResourceContext currentResource = resource;
			
			reportMeta = new JSONObject();
			
			if(readingruleContext.getThresholdType() == ReadingRuleContext.ThresholdType.ADVANCED.getValue()) {
				
				//TODO get all resources in a single query
				
				Set readingMap = new HashSet();
				if(readingruleContext.getWorkflowId() > 0) {
					WorkflowContext workflow = WorkflowUtil.getWorkflowContext(readingruleContext.getWorkflowId(), true);
					
					for(WorkflowExpression workflowExp:workflow.getExpressions()) {
						
						ExpressionContext exp = (ExpressionContext) workflowExp;
						if(exp.getModuleName() != null) {
							
							JSONObject dataPoint = new JSONObject();
							
							modBean.getModule(exp.getModuleName());
							
							dataPoint.put("module", exp.getModuleName());
							
							dataPoint.put("yAggr", "0");
							dataPoint.put("xAggr", "0");
							
							FacilioField readingField = null;
							if(exp.getFieldName() != null ) {
								readingField = DashboardUtil.getField(exp.getModuleName(), exp.getFieldName());
								dataPoint.put("readingFieldId", readingField.getFieldId());
								dataPoint.put("readingField", readingField);
							}
							if(exp.getCriteria() != null) {
								Map<String, Condition> conditions = exp.getCriteria().getConditions();
								
								for(String key : conditions.keySet()) {
									
									Condition condition = conditions.get(key);
									
									if(condition.getFieldName().equals("parentId")) {
										resource = condition.getValue().equals("${resourceId}") ? currentResource : ResourceAPI.getResource(Long.parseLong(condition.getValue()));
										dataPoint.put("parentId", resource.getId());
										break;
									}
								}
							}
							String name = resource.getName();
							if (readingField != null) {
								name += " (" + readingField.getDisplayName() + ")";
							}
							dataPoint.put("name", name);
							dataPoint.put("parent", resource);
							dataPoint.put("parentType", resource.getResourceType() == 1 ? "space": "asset");
							
							if(!readingMap.contains(dataPoint.get("parentId") + "_" + dataPoint.get("readingFieldId"))) {
								readingMap.add(dataPoint.get("parentId") + "_" + dataPoint.get("readingFieldId"));
								dataPoints.add(dataPoint);								
							}
						}
					}
				}
			}
			else {
				JSONObject dataPoint = new JSONObject();
				
				dataPoint.put("readingFieldId", readingruleContext.getReadingFieldId());
				dataPoint.put("readingField", readingruleContext.getReadingField());
				dataPoint.put("yAggr", "0");
				String name = resource.getName();
				if (readingruleContext.getReadingField() != null) {
					name += " (" + readingruleContext.getReadingField().getDisplayName() + ")";
				}
				dataPoint.put("name", name);
				dataPoint.put("parentId", resource.getId());
				dataPoint.put("parent", resource);
				dataPoint.put("parentType", resource.getResourceType() == 1 ? "space": "asset");
				
				dataPoints.add(dataPoint);
			}
			
			if(readingruleContext.getBaselineId() != -1) {
				reportMeta.put("baselineId", readingruleContext.getBaselineId());
			}
			JSONObject datefilter = new JSONObject();
			
			ZonedDateTime startTime = DateTimeUtil.getDayStartTimeOf(DateTimeUtil.getZonedDateTime(readingAlarmContext.getStartTime()));
			ZonedDateTime endTime = null;
			if(readingAlarmContext.getEndTime() > 0) {
				endTime = DateTimeUtil.getDayEndTimeOf(DateTimeUtil.getZonedDateTime(readingAlarmContext.getEndTime()));
			}
			else {
				endTime = DateTimeUtil.getDayEndTimeOf(startTime);
			}
			
			JSONArray time = new JSONArray();
			time.add(startTime.toInstant().toEpochMilli());
			time.add(endTime.toInstant().toEpochMilli());
			datefilter.put("time", time);
			datefilter.put("filter", "D");
			reportMeta.put("dateFilter", datefilter);
			
			reportMeta.put("dataPoints", dataPoints);
		}
		return SUCCESS;
	}
	
	private ReportContext constructReportObjectForReadingReport(FacilioModule module, FacilioField readingField,Long parentid) throws Exception {	// report functions
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		LOGGER.log(Level.SEVERE,"module.getName() -- "+module.getName());
		ReportContext readingReport = new ReportContext();
		
		readingReport.setModuleId(module.getModuleId());
		readingReport.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		ReportFieldContext xAxis = new ReportFieldContext();
		xAxis.setModuleField(modBean.getField("ttime", module.getName()));
		xAxis.setModuleFieldId(xAxis.getModuleField().getId());
		if(xLabel != null && !xLabel.isEmpty()) {
			xAxis.setFieldLabel(xLabel);
		}
		else {
			xAxis.setFieldLabel("Time");
		}
		xAxis.setIsFormulaField(false);
		readingReport.setxAxisField(xAxis);
		readingReport.setxAxisLabel(xAxis.getFieldLabel());
		if(xAggr != -1) {
			readingReport.setxAxisaggregateFunction(xAggr);
		}
		if (xAggr == 0) {
			readingReport.setIsHighResolutionReport(true);
		}
		
		ReportFieldContext yAxis = new ReportFieldContext();
		yAxis.setModuleField(readingField);
		yAxis.setModuleFieldId(yAxis.getModuleField().getId());
		if(yLabel != null && !yLabel.isEmpty()) {
			yAxis.setFieldLabel(yLabel);
		}
		else {
			yAxis.setFieldLabel(readingField.getDisplayName());
		}
		if (yUnit != null && !yUnit.isEmpty()) {
			yAxis.setUnit(yUnit);
		}
		else if (readingField instanceof NumberField) {
			yAxis.setUnit(((NumberField) readingField).getUnit());
		}
		yAxis.setIsFormulaField(false);
		readingReport.setY1AxisField(yAxis);
		readingReport.setY1AxisLabel(yAxis.getFieldLabel());
		readingReport.setY1AxisUnit(yAxis.getUnit());
		if(yAggr != -1) {
			readingReport.setY1AxisaggregateFunction(yAggr);
		}
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("parentId", module.getName()), String.valueOf(parentid), PickListOperators.IS));
		readingReport.setCriteria(criteria);
		
		ReportDateFilterContext dateFilter = new ReportDateFilterContext();
		dateFilter.setField(modBean.getField("ttime", module.getName()));
		dateFilter.setFieldId(dateFilter.getField().getId());
		dateFilter.setOperatorId(DateOperators.TODAY_UPTO_NOW.getOperatorId());
		readingReport.setDateFilter(dateFilter);
		
		if(reportEntityId != null) {
			readingReport.setReportEntityId(reportEntityId);
		}
		
		if(reportName != null) {
			readingReport.setName(reportName);
		}
		
		return readingReport;
	}
	
	private List<ReportColumnContext> reportColumns;
	public List<ReportColumnContext> getReportColumns() {
		return reportColumns;
	}
	public void setReportColumns(List<ReportColumnContext> reportColumns) {
		this.reportColumns = reportColumns;
	}
	Long widgetId;
	public Long getWidgetId() {
		return widgetId;
	}
	public void setWidgetId(Long widgetId) {
		this.widgetId = widgetId;
	}
	Object cardResult;
	public Object getCardResult() {
		return cardResult;
	}
	public void setCardResult(Object cardResult) {
		this.cardResult = cardResult;
	}
	Long baseSpaceId;
	public Long getBaseSpaceId() {
		return baseSpaceId;
	}
	public void setBaseSpaceId(Long baseSpaceId) {
		this.baseSpaceId = baseSpaceId;
	}
	public String getStaticKey() {
		return staticKey;
	}
	public void setStaticKey(String staticKey) {
		this.staticKey = staticKey;
	}
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	String staticKey;
	
	JSONObject paramsJson;
	
	public JSONObject getParamsJson() {
		return paramsJson;
	}
	public void setParamsJson(JSONObject paramsJson) {
		this.paramsJson = paramsJson;
	}
	public String getTabularData() throws Exception {						// report functions
		
		if (reportContext == null) {
			reportContext = DashboardUtil.getReportContext(reportId);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		reportColumns = reportContext.getReportColumns();
		reportContext.setReportColumns(null);
		for (ReportColumnContext column : reportColumns) {
			if (column.getReportId() == reportContext.getId()) {
				column.setReport(reportContext);
			}
			else {
				column.setReport(DashboardUtil.getReportContext(column.getReportId()));
			}
			FacilioModule module = modBean.getModule(column.getReport().getModuleId());
			
			if(module.getName().equals("workorder")) {
				column.setData(getDataForTickets(column.getReport(), module, dateFilter, null, column.getBaseLineId(), -1));
			}
			else {
				column.setData(getDataForReadings(column.getReport(), module, dateFilter, null, column.getBaseLineId(), -1));
			}
			//LOGGER.info(column.getReportId()+"  ----  "+column.getData());
		}
		Multimap<String, Object> resultMap = ArrayListMultimap.create();
		Multimap<String, Object> resultMap1 = ArrayListMultimap.create();
		Map<String,Long> dateMap = new HashMap<>();
		Map<Long,String> dateKeyMap = new HashMap<>();
		
		JSONArray dataJsonArray = new JSONArray();
		
		for (ReportColumnContext column : reportColumns) {
			
			JSONArray datas = column.getData();
			Iterator dataIterator = datas.iterator();
			while(dataIterator.hasNext()) {
				JSONObject data = (JSONObject) dataIterator.next();
				
				if(reportContext.getId() == 3755l) {
					if(column.getSequence() != 1) {
						String asset = (String) data.get("label");
						resultMap1.put(asset, data.get("value"));
					}
				}
				else {
					
					Long time = (Long) data.get("label");
					
					String timeValue = DashboardUtil.getDataFromValue(time, column.getReport().getXAxisAggregateOpperator());
					if(column.getSequence() == 1) {
						dateMap.put(timeValue, time);
						dateKeyMap.put(time, timeValue);
					}
					else {
						resultMap.put(timeValue, data.get("value"));
					}
				}
			}
		}
		LOGGER.log(Level.SEVERE, "dateCondition -- "+resultMap +" --- "+dateMap);
		if(reportContext.getId() == 3755l) {
			for(String key :resultMap1.keySet()) {
				
				Collection<Object> d = resultMap1.get(key);
				
				JSONArray data = new JSONArray();
				data.add(key);
				for(Object s:d) {
					if(s == null) {
						data.add("null");
					}
					data.add(s);
				}
				dataJsonArray.add(data);
			}
		}
		else {
			
			List<Long> keys = new ArrayList<>(dateKeyMap.keySet());
			Collections.sort(keys);
			//LOGGER.info("keys --- "+ keys);
			 for(Long key : keys){
				String timeKey = dateKeyMap.get(key);
				Collection<Object> d = resultMap.get(timeKey);
				JSONArray data = new JSONArray();
				data.add(dateMap.get(timeKey));
				for(Object s:d) {
					if(s == null) {
						data.add("null");
					}
					data.add(s);
				}
				dataJsonArray.add(data);
			 }
		}

		//LOGGER.info("datas --- "+dataJsonArray);
		
		reportData = dataJsonArray;
		
		return SUCCESS;
	}
	
	private String filters;
	
	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getFilters() {
		return this.filters;
	}
	public Long getDashboardVsWidgetId() {
		return dashboardVsWidgetId;
	}
	public void setDashboardVsWidgetId(Long dashboardVsWidgetId) {
		this.dashboardVsWidgetId = dashboardVsWidgetId;
	}
	public Long dashboardVsWidgetId;
	public String deleteWidgetFromDashboard() throws Exception {		// check and remove
		
		if(dashboardId != null && widgetId != null) {

			if(DashboardUtil.deleteWidgetFromDashboard(dashboardId,widgetId)) {
				return SUCCESS;
			}
		}
		return ERROR;
	}
	JSONObject variance;
	
	
	public JSONObject getVariance() {
		return variance;
	}
	public void setVariance(JSONObject variance) {
		this.variance = variance;
	}
	public String getData() throws Exception {			// report functions
		
		if (reportContext == null) {
			reportContext = DashboardUtil.getReportContext(reportId);
		}
		
		// chart setting overide for widget starts
		if(chartType != null) {
			int reportType = ReportContext.ReportChartType.getWidgetChartType(chartType).getValue();
			reportContext.setChartType(reportType);
		}
		
		if(dateFilterId != null) {
			ReportDateFilterContext reportDateFilter = DashboardUtil.getReportDateFilter(dateFilterId);
			if(reportDateFilter != null) {
				reportContext.setDateFilter(reportDateFilter);
			}
		}
		// chart setting overide for widget ends
		
		if (reportContext.getReportChartType() == ReportContext.ReportChartType.TABULAR) {
			getTabularData();
			return SUCCESS;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = null;
		if(moduleName != null) {
			module = modBean.getModule(moduleName);
		}
		else {
			module = modBean.getModule(reportContext.getModuleId());
		}
		
		if(reportContext.getReportSpaceFilterContext() != null && reportSpaceFilterContext != null) {
			if(reportSpaceFilterContext.getBuildingId() != null) {
				reportContext.getReportSpaceFilterContext().setBuildingId(reportSpaceFilterContext.getBuildingId());
			}
			else if (reportSpaceFilterContext.getSiteId() != null) {
				reportContext.getReportSpaceFilterContext().setSiteId(reportSpaceFilterContext.getSiteId());
			}
			else if (reportSpaceFilterContext.getChillerId() != null) {
				reportContext.getReportSpaceFilterContext().setChillerId(reportSpaceFilterContext.getChillerId());
			}
		}
		else if(buildingId != null) {
			reportSpaceFilterContext = new ReportSpaceFilterContext();
			reportSpaceFilterContext.setBuildingId(buildingId);
			reportContext.setReportSpaceFilterContext(reportSpaceFilterContext);
		}
		if(dashboardId != null) {
			DateRange range = DashboardUtil.getDateFilterFromDashboard(dashboardId);
			if(range != null) {
				dateFilter = new JSONArray();
				dateFilter.add(range.getStartTime());
				dateFilter.add(range.getEndTime());
			}
		}
		if(module.getName().equals("workorder") || module.getName().equals("alarm") || module.getName().equals("workorderrequest")) {
			reportData = getDataForTickets(reportContext, module, dateFilter, userFilterValues, baseLineId, criteriaId);
		}
		else {
			reportData = getDataForReadings(reportContext, module, dateFilter, userFilterValues, baseLineId, criteriaId);
		}
		return SUCCESS;
	}
	
	
	private String[] meterIds;
	
	public void setMeterIds(String[] meterIds) {
		this.meterIds = meterIds;
	}
	
	public String[] getMeterIds() {
		return this.meterIds;
	}
	
	private String entityName;
	
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	public String getEntityName() {
		return this.entityName;
	}
	
	private String legendMode;
	
	public void setLegendMode(String legendMode) {
		this.legendMode = legendMode;
	}
	
	public String getLegendMode() {
		return this.legendMode;
	}
	
	private boolean excludeViolatedReadings = false;
	
	public void setExcludeViolatedReadings(boolean excludeViolatedReadings) {
		this.excludeViolatedReadings = excludeViolatedReadings;
	}
	
	public boolean getExcludeViolatedReadings() {
		return this.excludeViolatedReadings;
	}
	
	private void setConditions(String moduleName, String fieldName, JSONObject fieldJson,List<Condition> conditionList) throws Exception { // check and remove	
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField field = modBean.getField(fieldName, moduleName);
		JSONArray value = (JSONArray) fieldJson.get("value");
		int operatorId;
		String operatorName;
		if (fieldJson.containsKey("operatorId")) {
			operatorId = (int) (long) fieldJson.get("operatorId");
			operatorName = Operator.getOperator(operatorId).getOperator();
		} else {
			operatorName = (String) fieldJson.get("operator");
			operatorId = field.getDataTypeEnum().getOperator(operatorName).getOperatorId();
		}
		
		if((value!=null && value.size() > 0) || (operatorName != null && !(operatorName.equals("is")) ) ) {
			
			Condition condition = new Condition();
			condition.setField(field);
			condition.setOperatorId(operatorId);
			
			if(value!=null && value.size()>0) {
				StringBuilder values = new StringBuilder();
				boolean isFirst = true;
				Iterator<String> iterator = value.iterator();
				while(iterator.hasNext())
				{
					String obj = iterator.next();
					if(!isFirst) {
						values.append(",");
					}
					else {
						isFirst = false;
					}
					if (obj.indexOf("_") != -1) {
						try {
							String filterValue = obj.split("_")[0];
							values.append(filterValue);
						}
						catch (Exception e) {
							values.append(obj);
						}
					}
					else {
						values.append(obj);
					}
				}
				condition.setValue(values.toString());
			}
			conditionList.add(condition);
		}
	}
	public JSONObject reportFieldsJson;
	public JSONObject getReportFieldsJson() {
		return reportFieldsJson;
	}
	public void setReportFieldsJson(JSONObject reportFieldsJson) {
		this.reportFieldsJson = reportFieldsJson;
	}
	
	private JSONArray getDataForTickets(ReportContext report, FacilioModule module, JSONArray dateFilter, JSONObject userFilterValues, long baseLineId, long criteriaId) throws Exception {	// report functions
		JSONArray ticketData = null;
		
		if(AccountUtil.getCurrentOrg().getOrgId() == 108l) {
			
			if(dateFilter == null) {
				dateFilter = new JSONArray();
				dateFilter.add(1522521000000l);
				dateFilter.add(1530383400000l);
			}
			
			if(report.getId() == 2349l) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					int compliance = 0,nonCompliance = 0,repeatFinding = 0;
					
					JSONObject buildingres = new JSONObject();
					for(BuildingContext building : SpaceAPI.getAllBuildings()) {
						
						compliance = 0;nonCompliance = 0;repeatFinding = 0;
						for(WorkOrderContext workorder:workorders) {
							
						//	LOGGER.log(Level.SEVERE, "buildingId --- "+building.getId());
							if(workorder.getResource() != null) {
								LOGGER.log(Level.INFO, "workorder.getResource().getId() --- "+workorder.getResource().getId());
							}
							
							if(workorder.getResource().getId() != building.getId()) {
								continue;
							}
							if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
								continue;
							}
							
							Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
							FacilioContext context = new FacilioContext();
							
							context.put(FacilioConstants.ContextNames.ID, workorder.getId());
							context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
							context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
							context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
							context.put("isAsMap", true);
							chain.execute(context);
							
							List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
							
							for(Map<String, Object> task : taskMap) {
								
								if(task.get("inputValue") != null) {
									
									String subject = (String) task.get("inputValue");
									
									subject = subject.trim();
									
									if (subject.endsWith("Non Compliance")) {
										nonCompliance += aswaqnonComp;
									}
									else if(subject.endsWith("Compliance")) {
										compliance += aswaqComp;
									}
									else if (subject.endsWith("Repeat Findings")) {
										repeatFinding += aswaqrep;
									}
								}
							}
						}
						JSONArray array = new JSONArray();
						
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("label", "Compliance");
						jsonObject.put("value", Math.abs(compliance));
						array.add(jsonObject);
						
						jsonObject = new JSONObject();
						jsonObject.put("label", "Non Compliance");
						jsonObject.put("value", Math.abs(nonCompliance));
						array.add(jsonObject);
						
						jsonObject = new JSONObject();
						jsonObject.put("label", "Repeat Finding");
						jsonObject.put("value", Math.abs(repeatFinding));
						array.add(jsonObject);
						
						buildingres = new JSONObject();
						
						buildingres.put("label", building.getName());
						buildingres.put("value", array);
						
						ticketData.add(buildingres);
					}
					
					LOGGER.log(Level.INFO, "23611l buildingres ----"+ticketData);
					
					return ticketData;
				}
			}
			
			else if(report.getId() == 2361l) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					LOGGER.log(Level.INFO, "23611l passed Category ----"+category.getName());
					int compliance = 0,nonCompliance = 0,repeatFinding = 0;
					
					for(BuildingContext building : SpaceAPI.getAllBuildings()) {
						
						compliance = 0;nonCompliance = 0;repeatFinding = 0;
						for(WorkOrderContext workorder:workorders) {
							
							if(workorder.getResource().getId() != building.getId()) {
								continue;
							}
							LOGGER.log(Level.INFO, "dateFilter --- "+dateFilter);
							if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
								continue;
							}
							LOGGER.log(Level.INFO, "passed --- "+workorder.getId());
							Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
							FacilioContext context = new FacilioContext();
							
							context.put(FacilioConstants.ContextNames.ID, workorder.getId());
							context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
							context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
							context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
							context.put("isAsMap", true);
							chain.execute(context);
							
							List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
							
							LOGGER.log(Level.SEVERE, "passed1 --- "+taskMap.size());
							for(Map<String, Object> task : taskMap) {
								
								if(task.get("inputValue") != null) {
									
									String subject = (String) task.get("inputValue");
									
									subject = subject.trim();
									
									if (subject.endsWith("Non Compliance")) {
										nonCompliance += aswaqnonComp;
									}
									else if(subject.endsWith("Compliance")) {
										compliance += aswaqComp;
									}
									else if (subject.endsWith("Repeat Findings")) {
										repeatFinding += aswaqrep;
									}
								}
							}
						}
						nonCompliance = Math.abs(nonCompliance);
						compliance = Math.abs(compliance);
						repeatFinding = Math.abs(repeatFinding);
						
						JSONObject buildingres = new JSONObject();
						buildingres.put("label",building.getName()); 
						buildingres.put("value", compliance+nonCompliance+repeatFinding);
						
						ticketData.add(buildingres);
						
					}
					
					LOGGER.log(Level.INFO, "2362ll buildingres ----"+ticketData);
					
					return ticketData;
				}
			}
			
			else if (report.getId() == 2381l || report.getId() == 2380l || report.getId() == 2379l) {

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					int compliance = 0,nonCompliance = 0,repeatFinding = 0;
					
					for(BuildingContext building : SpaceAPI.getAllBuildings()) {
						
						compliance = 0;nonCompliance = 0;repeatFinding = 0;
						for(WorkOrderContext workorder:workorders) {
							
							if(workorder.getResource().getId() != building.getId()) {
								continue;
							}
							LOGGER.log(Level.SEVERE, "dateFilter --- "+dateFilter);
							if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
								continue;
							}
							LOGGER.log(Level.SEVERE, "passed --- "+workorder.getId());
							Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
							FacilioContext context = new FacilioContext();
							
							context.put(FacilioConstants.ContextNames.ID, workorder.getId());
							context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
							context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
							context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
							context.put("isAsMap", true);
							chain.execute(context);
							
							List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
							
							LOGGER.log(Level.SEVERE, "passed1 --- "+taskMap.size());
							for(Map<String, Object> task : taskMap) {
								
								if(task.get("inputValue") != null) {
									
									String subject = (String) task.get("inputValue");
									
									subject = subject.trim();
									
									if (subject.endsWith("Non Compliance")) {
										nonCompliance += aswaqnonComp;
									}
									else if(subject.endsWith("Compliance")) {
										compliance += aswaqComp;
									}
									else if (subject.endsWith("Repeat Findings")) {
										repeatFinding += aswaqrep;
									}
								}
							}
						}
						nonCompliance = Math.abs(nonCompliance);
						compliance = Math.abs(compliance);
						repeatFinding = Math.abs(repeatFinding);
						
						JSONObject buildingres = new JSONObject();
						buildingres.put("label",building.getName()); 
						if(report.getId() == 2381l) {
							buildingres.put("value", repeatFinding);
						}
						else if (report.getId() == 2380l) {
							buildingres.put("value", nonCompliance);
						}
						else if (report.getId() == 2379l) {
							buildingres.put("value", compliance);
						}
						
						ticketData.add(buildingres);
					}
					return ticketData;
				}
			}
			
			else if(report.getId() == 2362l) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				reportData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					int compliance = 0,nonCompliance = 0,repeatFinding = 0;
					
					
					Map<Long,Long> qDateRange = new HashMap<>();
					
					qDateRange.put(1514745000000l, 1522521000000l);
					qDateRange.put(1522521000000l, 1530383400000l);
					qDateRange.put(1530383400000l, 1538332200000l);
					qDateRange.put(1538332200000l, 1546194600000l);
					
					ticketData = new JSONArray();
					for(int i=0;i<qDateRange.size();i++) {
						
						long fromTime = 0l;
						if(i==0) {
							fromTime = 1514745000000l;
						}
						else if(i == 1) {
							fromTime = 1522521000000l;
						}
						else if(i == 2) {
							fromTime = 1530383400000l;				
						}
						else if(i == 3) {
							fromTime = 1538332200000l;
						}
						long toTime = qDateRange.get(fromTime);
						JSONArray array = new JSONArray(); 
						for(BuildingContext building : SpaceAPI.getAllBuildings()) {
							
							compliance = 0;nonCompliance = 0;repeatFinding = 0;
							for(WorkOrderContext workorder:workorders) {
								
								LOGGER.log(Level.SEVERE, "buildingId --- "+buildingId);
								if(workorder.getResource() != null) {
									LOGGER.log(Level.SEVERE, "workorder.getResource().getId() --- "+workorder.getResource().getId());
								}
								
								if(workorder.getResource().getId() != building.getId()) {
									continue;
								}
								if(dateFilter != null && !(fromTime < workorder.getCreatedTime() && workorder.getCreatedTime() < toTime)) {
									continue;
								}
								
								Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
								FacilioContext context = new FacilioContext();
								
								context.put(FacilioConstants.ContextNames.ID, workorder.getId());
								context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
								context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
								context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
								
								context.put("isAsMap", true);
								chain.execute(context);
								
								List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
								
								for(Map<String, Object> task : taskMap) {
									
										if(task.get("inputValue") != null) {
											
											String subject = (String) task.get("inputValue");
											
											subject = subject.trim();
											
											if (subject.endsWith("Non Compliance")) {
												nonCompliance += aswaqnonComp;
											}
											else if(subject.endsWith("Compliance")) {
												compliance += aswaqComp;
											}
											else if (subject.endsWith("Repeat Findings")) {
												repeatFinding += aswaqrep;
											}
										}
								}
							}
							
							JSONObject buildingres = new JSONObject();
							buildingres.put("label", building.getName());
							buildingres.put("value", compliance+nonCompliance+repeatFinding);
							
							array.add(buildingres);
						}
						
						JSONObject qres = new JSONObject();
						qres.put("value", array);
						
						
						if(fromTime == 1514745000000l) {
							qres.put("label", "Q1 2018");
						}
						else if(fromTime == 1522521000000l) {
							qres.put("label", "Q2 2018");
						}
						else if(fromTime == 1530383400000l) {
							qres.put("label", "Q3 2018");
						}
						else if(fromTime == 1538332200000l) {
							qres.put("label", "Q4 2018");
						}
						new LinkedHashMap();
						ticketData.add(qres);
						
					}
					
					LOGGER.log(Level.SEVERE, "last buildingres ----"+ticketData);
					
					return ticketData;
				}
			}
			else if (report.getId() == 2407l) {


				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					for(BuildingContext building : SpaceAPI.getAllBuildings()) {
						
						int completed = 0,pending = 0;
						for(WorkOrderContext workorder:workorders) {
							
							if(workorder.getResource().getId() != building.getId()) {
								continue;
							}
							LOGGER.log(Level.INFO, "dateFilter --- "+dateFilter);
							if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
								continue;
							}
							Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
							FacilioContext context = new FacilioContext();
							
							context.put(FacilioConstants.ContextNames.ID, workorder.getId());
							context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
							context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
							context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
							context.put("isAsMap", true);
							chain.execute(context);
							
							List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
							
							LOGGER.log(Level.INFO, "passed1 --- "+taskMap.size());
							for(Map<String, Object> task : taskMap) {
								
								if(task.get("inputValue") != null) {
									completed ++;
								}
								else {
									pending ++;
								}
							}
						}
						
						JSONObject buildingres = new JSONObject();
						
						JSONArray resArray = new JSONArray();
						
						JSONObject res = new JSONObject();
						res.put("label", "Completed");
						res.put("value", completed);
						resArray.add(res);
						
						res = new JSONObject();
						res.put("label", "Pending");
						res.put("value", pending);
						resArray.add(res);
						
						buildingres.put("label", building.getName());
						buildingres.put("value", resArray);
						ticketData.add(buildingres);
					}
					return ticketData;
				}
			}
			
			else if (report.getId() == 2410l) {

				BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					for(BuildingContext building : SpaceAPI.getAllBuildings()) {
						
						int inTime = 0,overdue = 0;
						for(WorkOrderContext workorder:workorders) {
							
							if(workorder.getResource().getId() != building.getId()) {
								continue;
							}
							LOGGER.log(Level.INFO, "dateFilter --- "+dateFilter);
							if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
								continue;
							}
							if(workorder.getStatus() != null && workorder.getStatus().getId() > 0) {
								FacilioStatus status = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getId(), workorder.getStatus().getId());
								workorder.setStatus(status);
							}
							if(workorder.getStatus() != null && workorder.getStatus().getType() != null && workorder.getStatus().getType().equals(StatusType.CLOSED)) {
								
								if(workorder.getEstimatedEnd() != -1 && workorder.getActualWorkEnd() != -1) {
									if(workorder.getEstimatedEnd() < workorder.getActualWorkEnd()) {
										overdue++;
									}
									else {
										inTime++;
									}
								}
							}
							else {
								if(workorder.getEstimatedEnd() != -1) {
									if(workorder.getEstimatedEnd() < DateTimeUtil.getCurrenTime()) {
										overdue++;
									}
									else {
										inTime++;
									}
								}
							}
						}
						
						JSONObject buildingres = new JSONObject();
						
						JSONArray resArray = new JSONArray();
						
						JSONObject res = new JSONObject();
						res.put("label", "On Time");
						res.put("value", inTime);
						resArray.add(res);
						
						res = new JSONObject();
						res.put("label", "Overdue");
						res.put("value", overdue);
						resArray.add(res);
						
						buildingres.put("label", building.getName());
						buildingres.put("value", resArray);
						ticketData.add(buildingres);
					}
					return ticketData;
				}
			
			}
			
			else if (report.getId() == 2408l) {


				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					int completed = 0,pending = 0;
					for(WorkOrderContext workorder:workorders) {
						
						if(workorder.getResource().getId() != report.getReportSpaceFilterContext().getBuildingId()) {
							continue;
						}
						LOGGER.log(Level.INFO, "dateFilter --- "+dateFilter);
						if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
							continue;
						}
						Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
						FacilioContext context = new FacilioContext();
						
						context.put(FacilioConstants.ContextNames.ID, workorder.getId());
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
						context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
						context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
						context.put("isAsMap", true);
						chain.execute(context);
						
						List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
						
						LOGGER.log(Level.INFO, "passed1 --- "+taskMap.size());
						for(Map<String, Object> task : taskMap) {
							
							if(task.get("inputValue") != null) {
								completed ++;
							}
							else {
								pending ++;
							}
						}
					}
					
					JSONObject res = new JSONObject();
					res.put("label", "Completed");
					res.put("value", completed);
					ticketData.add(res);
					
					res = new JSONObject();
					res.put("label", "Pending");
					res.put("value", pending);
					ticketData.add(res);
					
					return ticketData;
				}
			}
			
			else if (report.getId() == 2409l) {

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					Map<Long,Long> qDateRange = new HashMap<>();
					
					qDateRange.put(1514745000000l, 1522521000000l);
					qDateRange.put(1522521000000l, 1530383400000l);
					qDateRange.put(1530383400000l, 1538332200000l);
					qDateRange.put(1538332200000l, 1546194600000l);
					
					ticketData = new JSONArray();
					for(int i=0;i<qDateRange.size();i++) {
						
						int compliance = 0,nonCompliance = 0,repeatFinding = 0;
						long fromTime = 0l;
						if(i==0) {
							fromTime = 1514745000000l;
						}
						else if(i == 1) {
							fromTime = 1522521000000l;
						}
						else if(i == 2) {
							fromTime = 1530383400000l;				
						}
						else if(i == 3) {
							fromTime = 1538332200000l;
						}
						long toTime = qDateRange.get(fromTime);
							
						for(WorkOrderContext workorder:workorders) {
							
							LOGGER.log(Level.INFO, "buildingId --- "+buildingId);
							if(workorder.getResource() != null) {
								LOGGER.log(Level.INFO, "workorder.getResource().getId() --- "+workorder.getResource().getId());
							}
							
							if(workorder.getResource().getId() != report.getReportSpaceFilterContext().getBuildingId()) {
								continue;
							}
							if(dateFilter != null && !(fromTime < workorder.getCreatedTime() && workorder.getCreatedTime() < toTime)) {
								continue;
							}
							
							Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
							FacilioContext context = new FacilioContext();
							
							context.put(FacilioConstants.ContextNames.ID, workorder.getId());
							context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
							context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
							context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
							
							context.put("isAsMap", true);
							chain.execute(context);
							
							List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
							
							for(Map<String, Object> task : taskMap) {
								
								if(task.get("inputValue") != null) {
									
									String subject = (String) task.get("inputValue");
									
									subject = subject.trim();
									
									if (subject.endsWith("Non Compliance")) {
										nonCompliance += aswaqnonComp;
									}
									else if(subject.endsWith("Compliance")) {
										compliance += aswaqComp;
									}
									else if (subject.endsWith("Repeat Findings")) {
										repeatFinding += aswaqrep;
									}
								}
							}
						}
						
						JSONObject buildingres = new JSONObject();
						buildingres.put("value", compliance+nonCompliance+repeatFinding);
						
						if(fromTime == 1514745000000l) {
							buildingres.put("label", "Q1 2018");
						}
						else if(fromTime == 1522521000000l) {
							buildingres.put("label", "Q2 2018");
						}
						else if(fromTime == 1530383400000l) {
							buildingres.put("label", "Q3 2018");
						}
						else if(fromTime == 1538332200000l) {
							buildingres.put("label", "Q4 2018");
						}
						ticketData.add(buildingres);
					}
					
					LOGGER.log(Level.SEVERE, "last buildingres ----"+ticketData);
					
					return ticketData;
				}
			}
			
			else if (report.getId() == 2382l) {
				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					int compliance = 0,nonCompliance = 0,repeatFinding = 0;
					
					for(WorkOrderContext workorder:workorders) {
						
						if(workorder.getResource().getId() != report.getReportSpaceFilterContext().getBuildingId()) {
							continue;
						}
						LOGGER.log(Level.INFO, "dateFilter --- "+dateFilter);
						if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
							continue;
						}
						LOGGER.log(Level.INFO, "passed --- "+workorder.getId());
						Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
						FacilioContext context = new FacilioContext();
						
						context.put(FacilioConstants.ContextNames.ID, workorder.getId());
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
						context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
						context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
						context.put("isAsMap", true);
						chain.execute(context);
						
						List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
						
						LOGGER.log(Level.INFO, "passed1 --- "+taskMap.size());
						for(Map<String, Object> task : taskMap) {
							
							if(task.get("inputValue") != null) {
								
								String subject = (String) task.get("inputValue");
								
								subject = subject.trim();
								
								if (subject.endsWith("Non Compliance")) {
									nonCompliance += aswaqnonComp;
								}
								else if(subject.endsWith("Compliance")) {
									compliance += aswaqComp;
								}
								else if (subject.endsWith("Repeat Findings")) {
									repeatFinding += aswaqrep;
								}
							}
						}
					}
					nonCompliance = Math.abs(nonCompliance);
					compliance = Math.abs(compliance);
					repeatFinding = Math.abs(repeatFinding);
					
					JSONObject buildingres = new JSONObject();
					
					buildingres.put("label", "Compliance");
					buildingres.put("value", compliance);
					ticketData.add(buildingres);
					
					
					buildingres = new JSONObject();
					
					buildingres.put("label", "Non Compliance");
					buildingres.put("value", nonCompliance);
					ticketData.add(buildingres);
					
					buildingres = new JSONObject();
					
					buildingres.put("label", "Repeat Finding");
					buildingres.put("value", repeatFinding);
					ticketData.add(buildingres);
						
					return ticketData;
				}
			}
			
		}
		
		if(AccountUtil.getCurrentOrg().getOrgId() == 129l) {
			
			if(dateFilter == null && report.getDateFilter() != null) {
				
				DateOperators dateOperator = report.getDateFilter().getOperator();
				DateRange range = dateOperator.getRange(report.getDateFilter().getValue());
				dateFilter = new JSONArray();
				dateFilter.add(range.getStartTime());
				dateFilter.add(range.getEndTime());
			}
			
			if(baseLineId != -1) {
				BaseLineContext baseLineContext = BaseLineAPI.getBaseLine(baseLineId);
				DateRange dateRange;
				if(dateFilter != null) {
					LOGGER.severe("dateFilter --- "+dateFilter);
					dateRange = new DateRange((long)dateFilter.get(0), (long)dateFilter.get(1));
				}
				else {
					dateRange = report.getDateFilter().getOperator().getRange(report.getDateFilter().getValue());
				}
				Condition dateCondition = baseLineContext.getBaseLineCondition(report.getDateFilter().getField(), dateRange);
				
				if(dateCondition != null) {
					if(dateCondition.getValue() != null && dateCondition.getValue().contains(",")) {
						String startTimeString  = dateCondition.getValue().substring(0, dateCondition.getValue().indexOf(",")).trim();
						String endTimeString  = dateCondition.getValue().substring( dateCondition.getValue().indexOf(",")+1,dateCondition.getValue().length()).trim();
						this.startTime = Long.parseLong(startTimeString);
						this.endTime = Long.parseLong(endTimeString);
						
						dateFilter = new JSONArray();
						dateFilter.add(this.startTime);
						dateFilter.add(this.endTime);
					}
				}
			}
			this.dateFilter = dateFilter;
			if(report.getId() == 3940l) { 	// 7P 
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					if(!(category.getName().equals("Auditing"))) {
						continue;
					}
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					JSONObject buildingres = new JSONObject();
					for(BuildingContext building : SpaceAPI.getAllBuildings()) {
						
						int buildingScore = 0;
						for(WorkOrderContext workorder:workorders) {
							
//							if( !(workorder.getSubject().contains("Daily Maintenance") || workorder.getSubject().contains("Fortnightly Maintenance") || workorder.getSubject().contains(" Monthly Maintenance"))) {
//								continue;
//							}
							
							if(workorder.getResource().getId() != building.getId()) {
								continue;
							}
							if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
								continue;
							}
							
							Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
							FacilioContext context = new FacilioContext();
							
							context.put(FacilioConstants.ContextNames.ID, workorder.getId());
							context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
							context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
							context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
							context.put("isAsMap", true);
							chain.execute(context);
							
							List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
							
							for(Map<String, Object> task : taskMap) {
								
								if(task.get("inputValue") != null) {
									
									String stringValue = task.get("inputValue").toString();
									
									Integer value = 0;
									if("Met".equals(stringValue) ) {
										value = 5;
									}
									else if ("Not Met".equals(stringValue)) {
										value = 0;
									}
//									else {
//										value = Integer.parseInt(stringValue);
//									}
									
									buildingScore = buildingScore +value;
								}
							}
						}
						
						buildingres = new JSONObject();
						
						buildingres.put("label", building.getName());
						buildingres.put("value", buildingScore);
						
						ticketData.add(buildingres);
					}
					
					LOGGER.log(Level.INFO, "23611l buildingres ----"+ticketData);
					
				}
				return ticketData;
			}
			
			if(report.getId() == 3941l) { // 1P

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					if(category.getId() != 606l) {
						continue;
					}
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					LOGGER.log(Level.INFO, "workorders.size() --- "+workorders.size());
					if(workorders.isEmpty()) {
						continue;
					}
					
					for(BuildingContext building : SpaceAPI.getAllBuildings()) {
						
						int completed = 0,pending = 0;
						LOGGER.log(Level.INFO, "buildingid --- "+building.getId());
						for(WorkOrderContext workorder:workorders) {
							
							LOGGER.log(Level.INFO, "workorderid --- "+workorder.getId());
							if(workorder.getResource().getId() != building.getId()) {
								continue;
							}
							LOGGER.log(Level.INFO, "dateFilter --- "+dateFilter);
							if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
								continue;
							}
							LOGGER.log(Level.INFO, "passed --- "+workorder.getId());
							Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
							FacilioContext context = new FacilioContext();
							
							context.put(FacilioConstants.ContextNames.ID, workorder.getId());
							context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
							context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
							context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
							context.put("isAsMap", true);
							chain.execute(context);
							
							List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
							
							LOGGER.log(Level.INFO, "passed1 --- "+taskMap.size());
							for(Map<String, Object> task : taskMap) {
								Long statusId = null;
								if(task.get("status") != null) {
									statusId = (Long) ((Map<String, Object>)task.get("status")).get("id");
								}
								if(statusId != null && statusId.equals(628l)) {
									completed ++;
								}
								else {
									pending ++;
								}
							}
						}
						
						JSONObject buildingres = new JSONObject();
						
						JSONArray resArray = new JSONArray();
						
						JSONObject res = new JSONObject();
						res.put("label", "Completed");
						res.put("value", completed);
						resArray.add(res);
						
						res = new JSONObject();
						res.put("label", "Pending");
						res.put("value", pending);
						resArray.add(res);
						
						buildingres.put("label", building.getName());
						buildingres.put("value", resArray);
						ticketData.add(buildingres);
					}
					return ticketData;
				}
			}
			
			else if (report.getId() == 3942l) {	//2P

				BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					for(BuildingContext building : SpaceAPI.getAllBuildings()) {
						
						int inTime = 0,overdue = 0;
						for(WorkOrderContext workorder:workorders) {
							
							if(workorder.getResource().getId() != building.getId()) {
								continue;
							}
							if(workorder.getStatus() != null && workorder.getStatus().getId() > 0) {
								FacilioStatus status = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getId(), workorder.getStatus().getId());
								workorder.setStatus(status);
							}
							if(workorder.getStatus() != null && workorder.getStatus().getType() != null && workorder.getStatus().getType().equals(StatusType.CLOSED)) {
								
								LOGGER.log(Level.INFO, "dateFilter --- "+dateFilter);
								if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getActualWorkEnd() && workorder.getActualWorkEnd() < (Long)dateFilter.get(1))) {
									continue;
								}
								
								if(workorder.getEstimatedEnd() != -1 && workorder.getActualWorkEnd() != -1) {
									if(workorder.getEstimatedEnd() < workorder.getActualWorkEnd()) {
										overdue++;
									}
									else {
										inTime++;
									}
								}
								else {
									inTime++;
								}
							}
						}
						
						JSONObject buildingres = new JSONObject();
						
						JSONArray resArray = new JSONArray();
						
						JSONObject res = new JSONObject();
						res.put("label", "On Time");
						res.put("value", inTime);
						resArray.add(res);
						
						res = new JSONObject();
						res.put("label", "Overdue");
						res.put("value", overdue);
						resArray.add(res);
						
						buildingres.put("label", building.getName());
						buildingres.put("value", resArray);
						ticketData.add(buildingres);
					}
					return ticketData;
				}
			}
			
			else if (report.getId() == 4056l) {	//2B

				BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					int inTime = 0,overdue = 0;
					for(WorkOrderContext workorder:workorders) {
						
						if(workorder.getResource().getId() != report.getReportSpaceFilterContext().getBuildingId()) {
							continue;
						}
						if(workorder.getStatus() != null && workorder.getStatus().getId() > 0) {
							FacilioStatus status = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getId(), workorder.getStatus().getId());
							workorder.setStatus(status);
						}
						if(workorder.getStatus() != null && workorder.getStatus().getType() != null && workorder.getStatus().getType().equals(StatusType.CLOSED)) {
							
							LOGGER.log(Level.INFO, "dateFilter --- "+dateFilter);
							if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getActualWorkEnd() && workorder.getActualWorkEnd() < (Long)dateFilter.get(1))) {
								continue;
							}
							
							if(workorder.getEstimatedEnd() != -1 && workorder.getActualWorkEnd() != -1) {
								if(workorder.getEstimatedEnd() < workorder.getActualWorkEnd()) {
									overdue++;
								}
								else {
									inTime++;
								}
							}
							else {
								inTime++;
							}
						}
					}
					
					JSONObject res = new JSONObject();
					res.put("label", "On Time");
					res.put("value", inTime);
					ticketData.add(res);
					
					res = new JSONObject();
					res.put("label", "Overdue");
					res.put("value", overdue);
					ticketData.add(res);
					
					return ticketData;
				}
				
			}
			
			if(report.getId() == 3943l) { 	// 7P baseline
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					if( !(category.getName().equals("Auditing"))) {
						continue;
					}
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					JSONObject buildingres = new JSONObject();
					for(BuildingContext building : SpaceAPI.getAllBuildings()) {
						
						int buildingScore = 0;
						for(WorkOrderContext workorder:workorders) {
							
//							if( !(workorder.getSubject().contains("Daily Maintenance") || workorder.getSubject().contains("Fortnightly Maintenance") || workorder.getSubject().contains(" Monthly Maintenance"))) {
//								
//								continue;
//							}
							if(workorder.getResource().getId() != building.getId()) {
								continue;
							}
							if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
								continue;
							}
							
							Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
							FacilioContext context = new FacilioContext();
							
							context.put(FacilioConstants.ContextNames.ID, workorder.getId());
							context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
							context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
							context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
							context.put("isAsMap", true);
							chain.execute(context);
							
							List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
							
							for(Map<String, Object> task : taskMap) {
								
								if(task.get("inputValue") != null) {
									
									String stringValue = task.get("inputValue").toString();
									
									Integer value = 0;
									if("Met".equals(stringValue) ) {
										value = 5;
									}
									else if ("Not Met".equals(stringValue)) {
										value = 0;
									}
//									else {
//										value = Integer.parseInt(stringValue);
//									}
									
									buildingScore = buildingScore +value;
								}
							}
						}
						
						buildingres = new JSONObject();
						
						buildingres.put("label", building.getName());
						buildingres.put("value", buildingScore);
						
						ticketData.add(buildingres);
					}
					
					LOGGER.log(Level.INFO, "23611l buildingres ----"+ticketData);
					
				}
				return ticketData;
			}
			// building
			if(report.getId() == 3944l) { 	//1B

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					int completed = 0,pending = 0;
					for(WorkOrderContext workorder:workorders) {
						
						if(workorder.getResource().getId() != report.getReportSpaceFilterContext().getBuildingId()) {
							continue;
						}
						LOGGER.log(Level.INFO, "dateFilter --- "+dateFilter);
						if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
							continue;
						}
						Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
						FacilioContext context = new FacilioContext();
						
						context.put(FacilioConstants.ContextNames.ID, workorder.getId());
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
						context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
						context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
						context.put("isAsMap", true);
						chain.execute(context);
						
						List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
						
						for(Map<String, Object> task : taskMap) {
							
							Long statusId = null;
							if(task.get("status") != null) {
								statusId = (Long) ((Map<String, Object>)task.get("status")).get("id");
							}
							if(statusId != null && statusId.equals(628l)) {
								completed ++;
							}
							else {
								pending ++;
							}
						}
					}
					
					JSONObject res = new JSONObject();
					res.put("label", "Completed");
					res.put("value", completed);
					ticketData.add(res);
					
					res = new JSONObject();
					res.put("label", "Pending");
					res.put("value", pending);
					ticketData.add(res);
					
					return ticketData;
				}
			}
			
			if(report.getId() == 3945l) { 	//7B

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					if( !(category.getName().equals("Auditing"))) {
						continue;
					}
					
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					JSONObject buildingres = new JSONObject();
						
					for(WorkOrderContext workorder:workorders) {
						
						int buildingScore = 0;
						if(!(workorder.getSubject().contains("Daily Maintenance"))) {
							
							continue;
						}
						if(workorder.getResource().getId() != report.getReportSpaceFilterContext().getBuildingId()) {
							continue;
						}
						
						if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
							continue;
						}
						
						Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
						FacilioContext context = new FacilioContext();
						
						context.put(FacilioConstants.ContextNames.ID, workorder.getId());
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
						context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
						context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
						context.put("isAsMap", true);
						chain.execute(context);
						
						List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
						
						for(Map<String, Object> task : taskMap) {
							
							if(task.get("inputValue") != null) {
								
								String stringValue = task.get("inputValue").toString();
								
								Integer value = 0;
								if("Met".equals(stringValue) ) {
									value = 5;
								}
								else if ("Not Met".equals(stringValue)) {
									value = 0;
								}
//								else {
//									value = Integer.parseInt(stringValue);
//								}
								
								buildingScore = buildingScore +value;
							}
						}
						buildingres = new JSONObject();
						
						buildingres.put("label", workorder.getCreatedTime());
						buildingres.put("value", buildingScore);
						
						ticketData.add(buildingres);
					}
						
					LOGGER.log(Level.INFO, "23611l buildingres ----"+ticketData);
				}
				return ticketData;
			}
			
			if(report.getId() == 3946l) { 	//6B

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					
					if( !(category.getName().equals("Auditing"))) {
						continue;
					}
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					for(WorkOrderContext workorder:workorders) {
						
						int passed = 0,failed = 0;
						JSONObject buildingres = new JSONObject();
						
						if(!(workorder.getSubject().contains("Daily Maintenance"))) {
						
						continue;
						}
						if(workorder.getResource().getId() != report.getReportSpaceFilterContext().getBuildingId()) {
							continue;
						}
						
						if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
							continue;
						}
						
						Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
						FacilioContext context = new FacilioContext();
						
						context.put(FacilioConstants.ContextNames.ID, workorder.getId());
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
						context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
						context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
						context.put("isAsMap", true);
						chain.execute(context);
						
						List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
						
						for(Map<String, Object> task : taskMap) {
							
							if(task.get("inputValue") != null) {
								
								String stringValue = task.get("inputValue").toString();
								
								if("Met".equals(stringValue) ) {
									passed = passed + 1;
								}
								else if ("Not Met".equals(stringValue)) {
									failed = failed + 1;
								}
//								else {
//									value = Integer.parseInt(stringValue);
//									if(value <= 2) {
//										failed = failed + 1;
//									}else {
//										passed = passed + 1;
//									}
//								}
							}
						}
						
						JSONArray resArray = new JSONArray();
						
						JSONObject res = new JSONObject();
						res.put("label", "Met");
						res.put("value", passed);
						resArray.add(res);
						
						res = new JSONObject();
						res.put("label", "Not Met");
						res.put("value", failed);
						resArray.add(res);
						
						buildingres.put("label", workorder.getCreatedTime());
						buildingres.put("value", resArray);
						ticketData.add(buildingres);
						
						ticketData.add(buildingres);
					}
						
					
					LOGGER.log(Level.INFO, "23611l buildingres ----"+ticketData);
					
				}
				return ticketData;
			}
			
			if(report.getId() == 3947l)	//6P
			{
			 	
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				ticketData = new JSONArray();

				for(TicketCategoryContext category:categories) {
					if( !(category.getName().equals("Auditing"))) {
						continue;
					}
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					
					for(BuildingContext building : SpaceAPI.getAllBuildings()) {
						
						int passed = 0,failed = 0;
						JSONArray resArray = new JSONArray();
						for(WorkOrderContext workorder:workorders) {
							
//							if( !(workorder.getSubject().contains("Daily Maintenance") || workorder.getSubject().contains("Fortnightly Maintenance") || workorder.getSubject().contains(" Monthly Maintenance"))) {
//								
//								continue;
//							}
							if(workorder.getResource().getId() != building.getId()) {
								continue;
							}
							if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
								continue;
							}
							
							Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
							FacilioContext context = new FacilioContext();
							
							context.put(FacilioConstants.ContextNames.ID, workorder.getId());
							context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
							context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
							context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
							context.put("isAsMap", true);
							chain.execute(context);
							
							List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
							
							for(Map<String, Object> task : taskMap) {
								
								if(task.get("inputValue") != null) {
									
									String stringValue = task.get("inputValue").toString();
									
									if("Met".equals(stringValue)) {
										passed = passed + 1;
									}
									else if ("Not Met".equals(stringValue)) {
										failed = failed + 1;
									}
//									else {
//										value = Integer.parseInt(stringValue);
//										if(value <= 2) {
//											failed = failed + 1;
//										}else {
//											passed = passed + 1;
//										}
//									}
								}
							}
						}
						JSONObject res = new JSONObject();
						res.put("label", "Met");
						res.put("value", passed);
						resArray.add(res);
						
						res = new JSONObject();
						res.put("label", "Not Met");
						res.put("value", failed);
						resArray.add(res);
						
						JSONObject buildingres = new JSONObject();
						buildingres.put("label", building.getName());
						buildingres.put("value", resArray);
						
						ticketData.add(buildingres);
					}
					
					LOGGER.log(Level.INFO, "23611l buildingres ----"+ticketData);
					
				}
				return ticketData;
			}
			if(report.getId() == 4057l) { // B3

				
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getAllPMs(AccountUtil.getCurrentOrg().getId(),true);
					
				ticketData = new JSONArray();
				for(PreventiveMaintenance pm :pms) {
					
					if(!(pm.getName().contains("Daily : Cross verify BMS readings with Meter Readings") || pm.getName().contains("Daily : Cleaning of IBMS equipments") || pm.getName().contains("Daily : BMS hardware/software problems checking") || pm.getName().contains("Daily : Ensure that all equipment operate at design"))) {
						continue;
					}
					
					int failed =0,passed = 0;
					 List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrderFromPMId(pm.getId());
					 
					 for(WorkOrderContext workorder : workorders) {
						 
						if(workorder.getResource().getId() != report.getReportSpaceFilterContext().getBuildingId()) {
							continue;
						}
						if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
							continue;
						}
						Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
						FacilioContext context = new FacilioContext();
						
						context.put(FacilioConstants.ContextNames.ID, workorder.getId());
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
						context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
						context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
						context.put("isAsMap", true);
						chain.execute(context);
						
						List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
						for(Map<String, Object> task : taskMap) {
							
							if(task.get("inputValue") != null) {
								
								String subject = (String) task.get("inputValue");
								subject = subject.trim();
								
								if (subject.startsWith("Not")) {
									failed++;
								}
								else {
									passed++;
								}
							}
						}
					}
					
					if(passed >0 || failed >0) {
						JSONArray resArray = new JSONArray();
						
						JSONObject res = new JSONObject();
						res.put("label", "Ok");
						res.put("value", passed);
						resArray.add(res);
						
						res = new JSONObject();
						res.put("label", "Not Ok");
						res.put("value", failed);
						resArray.add(res);
						
						JSONObject buildingres = new JSONObject();
						buildingres.put("label", pm.getName());
						buildingres.put("value", resArray);
						
						ticketData.add(buildingres);
					}
				}
				return ticketData;
			}
			
			if(report.getId() == 1234l) { // B4a

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getAllPMs(AccountUtil.getCurrentOrg().getId(),true);
				
				for(PreventiveMaintenance pm :pms) {
					
					if(!(pm.getName().contains("Daily : Cross verify BMS readings with Meter Readings") || pm.getName().contains("Daily : Cleaning of IBMS equipments") || pm.getName().contains("Daily : BMS hardware/software problems checking") || pm.getName().contains("Daily : Ensure that all equipment operate at design"))) {
						continue;
					}
					int failed =0;
					 List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrderFromPMId(pm.getId());
					 
					 for(WorkOrderContext workorder : workorders) {
						
						if(workorder.getResource().getId() != report.getReportSpaceFilterContext().getBuildingId()) {
							continue;
						}
						
						if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
							continue;
						}
									
						Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
						FacilioContext context = new FacilioContext();
						
						context.put(FacilioConstants.ContextNames.ID, workorder.getId());
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
						context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
						context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
						context.put("isAsMap", true);
						chain.execute(context);
						
						List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
						for(Map<String, Object> task : taskMap) {
							
							if(task.get("inputValue") != null) {
								
								String subject = (String) task.get("inputValue");
								subject = subject.trim();
								
								if (subject.startsWith("Not")) {
									failed++;
								}
								else {
								}
							}
						}
					}
					 
					JSONObject buildingres = new JSONObject();
					buildingres.put("label", pm.getName());
					buildingres.put("value", failed);
					
					ticketData.add(buildingres);
				}
			}
			if(report.getId() == 4058l) { // B4b

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getAllPMs(AccountUtil.getCurrentOrg().getId(),true);
				
				ticketData = new JSONArray();
				for(PreventiveMaintenance pm :pms) {
					
					if(!(pm.getName().contains("Daily : Cross verify BMS readings with Meter Readings") || pm.getName().contains("Daily : Cleaning of IBMS equipments") || pm.getName().contains("Daily : BMS hardware/software problems checking") || pm.getName().contains("Daily : Ensure that all equipment operate at design"))) {
						continue;
					}
					int failed =0;
					 List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrderFromPMId(pm.getId());
					 
					 for(WorkOrderContext workorder : workorders) {
						 if(workorder.getResource().getId() != report.getReportSpaceFilterContext().getBuildingId()) {
								continue;
							}
						if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
							continue;
						}
									
						Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
						FacilioContext context = new FacilioContext();
						
						context.put(FacilioConstants.ContextNames.ID, workorder.getId());
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
						context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
						context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
						context.put("isAsMap", true);
						chain.execute(context);
						
						List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
						for(Map<String, Object> task : taskMap) {
							
							if(task.get("inputValue") != null) {
								
								String subject = (String) task.get("inputValue");
								subject = subject.trim();
								
								if (subject.startsWith("Not")) {
									failed++;
								}
							}
						}
					}
					 
					if(failed > 0) {
						JSONObject buildingres = new JSONObject();
						buildingres.put("label", pm.getName());
						buildingres.put("value", failed);
						
						ticketData.add(buildingres);
					}
				}
				return ticketData;
			}
			
			if(report.getId() == 4060l) { // 3P

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getAllPMs(AccountUtil.getCurrentOrg().getId(),true);
				
				Map<Long,Integer> passedMap = new HashMap<>();
				Map<Long,Integer> failedMap = new HashMap<>();
				ticketData = new JSONArray();
				for(PreventiveMaintenance pm :pms) {
					
					if(!(pm.getName().contains("Daily : Cross verify BMS readings with Meter Readings") || pm.getName().contains("Daily : Cleaning of IBMS equipments") || pm.getName().contains("Daily : BMS hardware/software problems checking") || pm.getName().contains("Daily : Ensure that all equipment operate at design"))) {
						continue;
					}
					int failed =0,passed = 0;
					 List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrderFromPMId(pm.getId());
					 
					 for(WorkOrderContext workorder : workorders) {
						 
						if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
							continue;
						}
						Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
						FacilioContext context = new FacilioContext();
						
						context.put(FacilioConstants.ContextNames.ID, workorder.getId());
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
						context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
						context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
						context.put("isAsMap", true);
						chain.execute(context);
						
						List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
						for(Map<String, Object> task : taskMap) {
							
							if(task.get("inputValue") != null) {
								
								String subject = (String) task.get("inputValue");
								subject = subject.trim();
								
								if (subject.startsWith("Not")) {
									failed++;
								}
								else {
									passed++;
								}
							}
						}
						
						if(passedMap.containsKey(workorder.getResource().getId())) {
							
							Integer passedValue = passedMap.get(workorder.getResource().getId());
							passedValue = passedValue + passed;
							passedMap.put(workorder.getResource().getId(), passedValue);
						}
						else {
							passedMap.put(workorder.getResource().getId(), passed);
						}
						
						if(failedMap.containsKey(workorder.getResource().getId())) {
							
							Integer failedValue = failedMap.get(workorder.getResource().getId());
							failedValue = failedValue + failed;
							failedMap.put(workorder.getResource().getId(), failedValue);
						}
						else {
							failedMap.put(workorder.getResource().getId(), failed);
						}
					}
				}
				
				
				
				for(BuildingContext building :SpaceAPI.getAllBuildings()) {
					
					JSONArray resArray = new JSONArray();
					
					Integer passed = passedMap.get(building.getId());
					Integer failed = failedMap.get(building.getId());
					
					JSONObject res = new JSONObject();
					res.put("label", "Passed");
					res.put("value", passed);
					resArray.add(res);
					
					res = new JSONObject();
					res.put("label", "Failed");
					res.put("value", failed);
					resArray.add(res);
					
					JSONObject buildingres = new JSONObject();
					buildingres.put("label", building.getName());
					buildingres.put("value", resArray);
					
					ticketData.add(buildingres);
				}
				
				return ticketData;
			}
			
			if(report.getId() == 4061l) { // 4Pa

				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getAllPMs(AccountUtil.getCurrentOrg().getId(),true);
				
				Map<Long,Integer> failedMap = new HashMap<>();
				ticketData = new JSONArray();
				for(PreventiveMaintenance pm :pms) {
					
					if(!(pm.getName().contains("Daily : Cross verify BMS readings with Meter Readings") || pm.getName().contains("Daily : Cleaning of IBMS equipments") || pm.getName().contains("Daily : BMS hardware/software problems checking") || pm.getName().contains("Daily : Ensure that all equipment operate at design"))) {
						continue;
					}
					int failed =0;
					 List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrderFromPMId(pm.getId());
					 
					 for(WorkOrderContext workorder : workorders) {
						 
						if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
							continue;
						}
						Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
						FacilioContext context = new FacilioContext();
						
						context.put(FacilioConstants.ContextNames.ID, workorder.getId());
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
						context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
						context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
						context.put("isAsMap", true);
						chain.execute(context);
						
						List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
						for(Map<String, Object> task : taskMap) {
							
							if(task.get("inputValue") != null) {
								
								String subject = (String) task.get("inputValue");
								subject = subject.trim();
								
								if (subject.startsWith("Not")) {
									failed++;
								}
								else {
								}
							}
						}
						
						if(failedMap.containsKey(workorder.getResource().getId())) {
							
							Integer failedValue = failedMap.get(workorder.getResource().getId());
							failedValue = failedValue + failed;
							failedMap.put(workorder.getResource().getId(), failedValue);
						}
						else {
							failedMap.put(workorder.getResource().getId(), failed);
						}
					}
				}
				
				new JSONArray();
				
				for(BuildingContext building :SpaceAPI.getAllBuildings()) {
					
					Integer failed = failedMap.get(building.getId());
					
					JSONObject buildingres = new JSONObject();
					buildingres.put("label", building.getName());
					buildingres.put("value", failed);
					
					ticketData.add(buildingres);
				}
				
				return ticketData;
			}
			
			if(report.getId() == 4062l) { // 1NP

				ticketData = new JSONArray();
				for( BuildingContext building :SpaceAPI.getAllBuildings()) {
					
					buildingId = building.getId();
					
					getUTCData();
					
					Iterator ittr = reportData.iterator();
					
					JSONArray newList = new JSONArray();
					while(ittr.hasNext()) {
						
						JSONObject json = (JSONObject) ittr.next();
						
						String name = (String) json.get("Criteria");
						
						JSONObject newJSON = new JSONObject();
						
						newJSON.put("label", name);
						newJSON.put("value", json.get("C"));
						
						newList.add(newJSON);
					}
					
					JSONObject buildingres = new JSONObject();
					buildingres.put("label", building.getName());
					buildingres.put("value", newList);
					ticketData.add(buildingres);
				}
				return ticketData;
			}
			
			if(report.getId() == 4063l) { // 2NP

				ticketData = new JSONArray();
				for( BuildingContext building :SpaceAPI.getAllBuildings()) {
					
					buildingId = building.getId();
					
					getUTCData();
					
					Iterator ittr = reportData.iterator();
					
					Object score =  null;
					while(ittr.hasNext()) {
						
						JSONObject json = (JSONObject) ittr.next();
						
						String name = (String) json.get("Criteria");
						
						if(name.contains("Critical Service")) {
							score = json.get("B");
							break;
						}
					}
					
					JSONObject buildingres = new JSONObject();
					buildingres.put("label", building.getName());
					buildingres.put("value", score);
					ticketData.add(buildingres);
				}
				return ticketData;
			}
			
			if(report.getId() == 4064l) { // 3NP

				ticketData = new JSONArray();
				for( BuildingContext building :SpaceAPI.getAllBuildings()) {
					
					buildingId = building.getId();
					
					getUTCData();
					
					Iterator ittr = reportData.iterator();
					
					Object score =  null;
					while(ittr.hasNext()) {
						
						JSONObject json = (JSONObject) ittr.next();
						
						String name = (String) json.get("Criteria");
						
						if(name.contains("(TAT)")) {
							score = json.get("B");
							break;
						}
					}
					
					JSONObject buildingres = new JSONObject();
					buildingres.put("label", building.getName());
					buildingres.put("value", score);
					ticketData.add(buildingres);
				}
				return ticketData;
			}

			if(report.getId() == 4065l) { // 4NP
			
				ticketData = new JSONArray();
				for( BuildingContext building :SpaceAPI.getAllBuildings()) {
					
					buildingId = building.getId();
					
					getUTCData();
					
					Iterator ittr = reportData.iterator();
					
					Object score =  null;
					while(ittr.hasNext()) {
						
						JSONObject json = (JSONObject) ittr.next();
						
						String name = (String) json.get("Criteria");
						
						if(name.contains("PD&PSI")) {
							score = json.get("B");
							break;
						}
					}
					
					JSONObject buildingres = new JSONObject();
					buildingres.put("label", building.getName());
					buildingres.put("value", score);
					ticketData.add(buildingres);
				}
				return ticketData;
			}
			
			if(report.getId() == 4066l) { // 5NP
			
				ticketData = new JSONArray();
				for( BuildingContext building :SpaceAPI.getAllBuildings()) {
					
					buildingId = building.getId();
					
					getUTCData();
					
					Iterator ittr = reportData.iterator();
					
					Object score =  null;
					while(ittr.hasNext()) {
						
						JSONObject json = (JSONObject) ittr.next();
						
						String name = (String) json.get("Criteria");
						
						if(name.contains("FAS")) {
							score = json.get("B");
							break;
						}
					}
					
					JSONObject buildingres = new JSONObject();
					buildingres.put("label", building.getName());
					buildingres.put("value", score);
					ticketData.add(buildingres);
				}
				return ticketData;
			}
			
			if(report.getId() == 4067l) { // 1NB

				buildingId = report.getReportSpaceFilterContext().getBuildingId();
				
				SpaceAPI.getBuildingSpace(buildingId);
				
				getUTCData();
				
				Iterator ittr = reportData.iterator();
				
				ticketData = new JSONArray();
				while(ittr.hasNext()) {
					
					JSONObject json = (JSONObject) ittr.next();
					
					String name = (String) json.get("Criteria");
					
					JSONObject newJSON = new JSONObject();
					
					newJSON.put("label", name);
					newJSON.put("value", json.get("C"));
					
					ticketData.add(newJSON);
				}
				
				return ticketData;
			}
			
			if(report.getId() == 4068l) { // 2NB
				
				ticketData = new JSONArray();
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				
				List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
				
				if(report.getReportSpaceFilterContext() != null && report.getReportSpaceFilterContext().getBuildingId() != null) {
					
					buildingId = report.getReportSpaceFilterContext().getBuildingId();
					
				}
				
				DecimalFormat df = new DecimalFormat(".##");
				
				for(TicketCategoryContext category:categories) {
					
					if(!(category.getName().equals("Auditing"))) {
						continue;
					}
					List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
					
					if(workorders.isEmpty()) {
						continue;
					}
					for(WorkOrderContext workorder:workorders) {
						
						LOGGER.log(Level.SEVERE, "buildingId --- "+buildingId);
						if(workorder.getResource() != null) {
							LOGGER.log(Level.SEVERE, "workorder.getResource().getId() --- "+workorder.getResource().getId());
						}
						
						if(buildingId != null && workorder.getResource() != null && workorder.getResource().getId() != buildingId) {
							continue;
						}
						
						if( !(workorder.getSubject().contains("Daily"))) {
							continue;
						}
						LOGGER.log(Level.SEVERE, "dateFilter --- "+dateFilter);
						LOGGER.log(Level.SEVERE, "workorder.getResource().getId() --- "+workorder.getCreatedTime());
						
						if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
							continue;
						}
						
						int criticalCount = 0, tatCount =0, pdCount = 0, fasCountdaily = 0;
						Map<String,Double> daily = new HashMap<>();
						
						Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
						FacilioContext context = new FacilioContext();
						
						context.put(FacilioConstants.ContextNames.ID, workorder.getId());
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
						context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
						context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
						chain.execute(context);
						
						Map<Long, List<TaskContext>> taksSectionMap = (Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
						
						for(Long sectionid :taksSectionMap.keySet()) {
							
							TaskSectionContext section = TicketAPI.getTaskSection(sectionid);
							
							for(TaskContext task : taksSectionMap.get(sectionid)) {
								
								if(task.getInputValue() != null) {
									
									if(task.getInputValue().equals("Met")) {
										value = 5.0;
									}
									else if(task.getInputValue().equals("Not Met")) {
										value = 0d;
									}
									
									if(workorder.getSubject().contains("Daily")) {
										
										if(section.getName().contains("Critical Service")) {
											
											criticalCount++;
											double value1 = 0;
											if(daily.containsKey("Critical Service")) {
												value1 = daily.get("Critical Service");
											}
											value1 = value1 + value;
											daily.put("Critical Service", value1);
										}
										else if(section.getName().contains("TAT")) {
											
											tatCount++;
											double value1 = 0;
											if(daily.containsKey("TAT")) {
												value1 = daily.get("TAT");
											}
											value1 = value1 + value;
											daily.put("TAT", value1);
										}
										else if(section.getName().contains("PD&PSI")) {
											
											pdCount++;
											double value1 = 0;
											if(daily.containsKey("PD&PSI")) {
												value1 = daily.get("PD&PSI");
											}
											value1 = value1 + value;
											daily.put("PD&PSI", value1);
										}
										else if(section.getName().contains("Financials & Ops")) {
											
											fasCountdaily++;
											double value1 = 0;
											if(daily.containsKey("Financials & Ops")) {
												value1 = daily.get("Financials & Ops");
											}
											value1 = value1 + value;
											daily.put("Financials & Ops", value1);
										}
									}
								}
							}
						}
						
						JSONArray resList = new JSONArray();
						LOGGER.log(Level.SEVERE, "daily --- "+daily);
							
						if(daily.containsKey("Critical Service")) {
							value = daily.get("Critical Service");
							if(value > 0) {
								value = value / criticalCount;
							}
							JSONObject json = new JSONObject();
							json.put("label", "Critical Service");
							json.put("value", df.format(value * (40.0d/100.0d)));
							resList.add(json);
						}
						
						if(daily.containsKey("TAT")) {
							value = daily.get("TAT");
							if(value > 0) {
								value = value / tatCount;
							}
							JSONObject json = new JSONObject();
							json.put("label", "TAT");
							json.put("value", df.format(value * (25.0d/100.0d)));
							resList.add(json);
							
						}
						if(daily.containsKey("PD&PSI")) {
							value = daily.get("PD&PSI");
							if(value > 0) {
								value = value / pdCount;
							}
							JSONObject json = new JSONObject();
							json.put("label", "PD&PSI");
							json.put("value", df.format(value * (10.0d/100.0d)));
							resList.add(json);
						}
						
						if(daily.containsKey("Financials & Ops")) {
							value = daily.get("Financials & Ops");
							if(value > 0) {
								value = value / fasCountdaily;
							}
							JSONObject json = new JSONObject();
							json.put("label", "Financials & Ops");
							json.put("value", df.format(value * (25.0d/100.0d)));
							resList.add(json);
						}
							
						JSONObject finalRes1 = new JSONObject();
						
						finalRes1.put("label", workorder.getCreatedTime());
						finalRes1.put("value", resList);
						
						ticketData.add(finalRes1);
					}
				}
				return ticketData;
			}
		}
		
		if(report.getCustomReportClass() != null) {
			
			if(dateFilter == null && report.getDateFilter() != null) {
				
				DateOperators dateOperator = report.getDateFilter().getOperator();
				DateRange range = dateOperator.getRange(report.getDateFilter().getValue());
				dateFilter = new JSONArray();
				dateFilter.add(range.getStartTime());
				dateFilter.add(range.getEndTime());
			}
			
			if(baseLineId != -1) {
				BaseLineContext baseLineContext = BaseLineAPI.getBaseLine(baseLineId);
				DateRange dateRange;
				if(dateFilter != null) {
					LOGGER.severe("dateFilter --- "+dateFilter);
					dateRange = new DateRange((long)dateFilter.get(0), (long)dateFilter.get(1));
				}
				else {
					dateRange = report.getDateFilter().getOperator().getRange(report.getDateFilter().getValue());
				}
				Condition dateCondition = baseLineContext.getBaseLineCondition(report.getDateFilter().getField(), dateRange);
				
				if(dateCondition != null) {
					if(dateCondition.getValue() != null && dateCondition.getValue().contains(",")) {
						String startTimeString  = dateCondition.getValue().substring(0, dateCondition.getValue().indexOf(",")).trim();
						String endTimeString  = dateCondition.getValue().substring( dateCondition.getValue().indexOf(",")+1,dateCondition.getValue().length()).trim();
						this.startTime = Long.parseLong(startTimeString);
						this.endTime = Long.parseLong(endTimeString);
						
						dateFilter = new JSONArray();
						dateFilter.add(this.startTime);
						dateFilter.add(this.endTime);
					}
				}
			}
			this.dateFilter = dateFilter;
			
			Class<? extends CustomReport> classObject = (Class<? extends CustomReport>) Class.forName(report.getCustomReportClass());
			CustomReport job = classObject.newInstance();
			ticketData = job.getData(report, module, dateFilter, userFilterValues, baseLineId, criteriaId);
			return ticketData;
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				;
		
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(module.getName());
		if (scopeCriteria != null) {
			builder.andCriteria(scopeCriteria);
		}
		
		try {
			Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria(module.getName(),"read");
			if (permissionCriteria != null) {
				builder.andCriteria(permissionCriteria);
			}
		}
		catch(Exception e) {
		}
		
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
	 		JSONObject filterJson = (JSONObject) parser.parse(getFilters());
	 		
	 		if (filterJson.size() > 0) {
	 			Iterator<String> filterIterator = filterJson.keySet().iterator();
				String moduleName = module.getName();
				Criteria criteria = new Criteria();
				while(filterIterator.hasNext()) {
					String fieldName = filterIterator.next();
					Object fieldJson = filterJson.get(fieldName);
					List<Condition> conditionList = new ArrayList<>();
					if(fieldJson!=null && fieldJson instanceof JSONArray) {
						JSONArray fieldJsonArr = (JSONArray) fieldJson;
						for(int i=0;i<fieldJsonArr.size();i++) {
							JSONObject fieldJsonObj = (JSONObject) fieldJsonArr.get(i);
							setConditions(moduleName, fieldName, fieldJsonObj, conditionList);
						}
					}
					else if(fieldJson!=null && fieldJson instanceof JSONObject) {
						JSONObject fieldJsonObj = (JSONObject) fieldJson;
						setConditions(moduleName, fieldName, fieldJsonObj, conditionList);
					}
					criteria.groupOrConditions(conditionList);
				}
				builder.andCriteria(criteria);
	 		}
		}
		
		if(module.getExtendModule() != null) {
			builder.innerJoin(module.getExtendModule().getTableName())
				.on(module.getTableName()+".Id="+module.getExtendModule().getTableName()+".Id");
		}
		
		if(AccountUtil.getCurrentOrg().getOrgId() == 182l) {	// remove this after checking
			
			if(module.getName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {
				if(report.isWorkRequestReport()) {
//					builder.andCustomWhere("APPROVAL_STATE != 3");
				}
				else {
					builder.andCustomWhere("APPROVAL_STATE = 3 OR APPROVAL_STATE is null");
				}
			}
		}
		
		if(report.getId() == 4144l) {
			builder.having("value is not null");
		}
		ReportFieldContext reportXAxisField = DashboardUtil.getReportField(report.getxAxisField());
		report.setxAxisField(reportXAxisField);
		FacilioField xAxisField = reportXAxisField.getField();
		
		if(!module.getName().equals(xAxisField.getModule().getName()) && !module.getName().equals(FacilioConstants.ContextNames.TICKET)) {
			if(xAxisField.getModule().getName().equals(FacilioConstants.ContextNames.ASSET) || xAxisField.getModule().getName().equals(FacilioConstants.ContextNames.RESOURCE)) {
				builder.innerJoin(xAxisField.getModule().getTableName())
				.on(xAxisField.getModule().getTableName()+".ID = Tickets.RESOURCE_ID");
				FacilioModule facilioModule = xAxisField.getModule();
				if(!facilioModule.getExtendModule().getName().equals(xAxisField.getModule().getName())) {
					builder.innerJoin(facilioModule.getExtendModule().getTableName())
					.on(xAxisField.getModule().getTableName()+".ID = "+facilioModule.getExtendModule().getTableName()+".ID");
				}
			}
		}
		
		Multimap<Long, Long> spaceResourceMap = ArrayListMultimap.create();
		
		if(xAxisField.getName().equals("resource")) {
			if(reportContext.getxAxisaggregateFunction().equals(SpaceAggregateOperator.BUILDING.getValue())) {
				
				for(BuildingContext building:SpaceAPI.getAllBuildings()) {
					List<Long> resourceList = DashboardUtil.getAllResources(building.getId());
					spaceResourceMap.putAll(building.getId(), resourceList);
				}
				report.getxAxisField().getField().setDisplayName("Building");
				report.getxAxisField().getField().setName("building");
			}
			else if(reportContext.getxAxisaggregateFunction().equals(SpaceAggregateOperator.SITE.getValue())) {
				
				for( SiteContext site:SpaceAPI.getAllSites()) {
					List<Long> resourceList = DashboardUtil.getAllResources(site.getId());
					spaceResourceMap.putAll(site.getId(), resourceList);
				}
				report.getxAxisField().getField().setDisplayName("Site");
				report.getxAxisField().getField().setName("Site");
			}
		}
		
		List<FacilioField> fields = new ArrayList<>();
		if(xAxisField.getDataTypeEnum().equals(FieldType.DATE_TIME)) {
			FacilioField dummyField = new FacilioField();
			dummyField.setColumnName(xAxisField.getColumnName());
			dummyField.setName("dummyField");
			if (report.getXAxisAggregateOpperator() == CommonAggregateOperator.ACTUAL) {
				dummyField = CommonAggregateOperator.ACTUAL.getSelectField(dummyField);
			}
			else {
				dummyField = NumberAggregateOperator.MAX.getSelectField(dummyField);
			}
			fields.add(dummyField);
		}
		
		FacilioField y1AxisField = null;
		ReportFieldContext reportY1AxisField;
		AggregateOperator xAggregateOpperator = report.getXAxisAggregateOpperator();
		if(xAggregateOpperator.getValue() != CommonAggregateOperator.COUNT.getValue()) {
			if ((dateFilter != null || report.getDateFilter() != null) && xAxisField.getDataTypeEnum().equals(FieldType.DATE_TIME)) {
				
				int oprId =  dateFilter != null ? DashboardUtil.predictDateOpperator(dateFilter) : report.getDateFilter().getOperatorId();
				
				if (oprId == DateOperators.TODAY.getOperatorId() || oprId == DateOperators.YESTERDAY.getOperatorId()) {
					xAggregateOpperator = DateAggregateOperator.HOURSOFDAY;
				}
				else if (oprId == DateOperators.CURRENT_WEEK.getOperatorId() || oprId == DateOperators.LAST_WEEK.getOperatorId() || oprId == DateOperators.CURRENT_WEEK_UPTO_NOW.getOperatorId()) {
					xAggregateOpperator = DateAggregateOperator.WEEKDAY;
				}
				else if (oprId == DateOperators.CURRENT_MONTH.getOperatorId() || oprId == DateOperators.LAST_MONTH.getOperatorId() || oprId == DateOperators.CURRENT_MONTH_UPTO_NOW.getOperatorId() || oprId == DateOperators.LAST_N_DAYS.getOperatorId()) {
					xAggregateOpperator = DateAggregateOperator.DAYSOFMONTH;
				}
				else if (oprId == DateOperators.CURRENT_YEAR.getOperatorId() || oprId == DateOperators.LAST_YEAR.getOperatorId() || oprId == DateOperators.CURRENT_YEAR_UPTO_NOW.getOperatorId()) {
					xAggregateOpperator = DateAggregateOperator.MONTHANDYEAR;
				}
				report.setxAxisaggregateFunction(xAggregateOpperator.getValue());
				
				if (getIsHeatMap() || (reportContext.getChartType() != null && reportContext.getChartType().equals(ReportChartType.HEATMAP.getValue())) || !report.getIsHighResolutionReport()) {
					
					xAxisField = xAggregateOpperator.getSelectField(xAxisField);
					
				}
			}
		}

		if(report.getY1Axis() != null || report.getY1AxisField() != null && (report.getY1AxisField().getModuleFieldId() != null || report.getY1AxisField().getFormulaFieldId() != null)) {
			reportY1AxisField = DashboardUtil.getReportField(report.getY1AxisField());
			AggregateOperator y1AggregateOpperator = report.getY1AxisAggregateOpperator();
			y1AxisField = reportY1AxisField.getField();
			y1AxisField = y1AggregateOpperator.getSelectField(y1AxisField);
		}
		else {
			y1AxisField = CommonAggregateOperator.COUNT.getSelectField(xAxisField);
			reportY1AxisField = new ReportFieldContext();
			reportY1AxisField.setModuleField(y1AxisField);
		}
		JSONObject reportFieldLabelMap = new JSONObject();
		reportFieldLabelMap.put("label", xAxisField.getName());
		reportFieldLabelMap.put("value", y1AxisField.getName());
		
		xAxisField.setName("label");
		y1AxisField.setName("value");
		
		report.setY1AxisField(reportY1AxisField);

		if(userFilterValues != null && report.getReportUserFilters() != null) {
			for(ReportUserFilterContext userFilter : report.getReportUserFilters()) {
				if(userFilterValues.containsKey(userFilter.getId().toString())) {
					Object userFilterValue = userFilterValues.get(userFilter.getId().toString());
					if(userFilterValue instanceof JSONArray) {
						JSONArray userFilterJsonValue = (JSONArray) userFilterValue;
						builder.andCustomWhere(userFilter.getWhereClause(), userFilterJsonValue.get(0),userFilterJsonValue.get(1));
					}
					else {
						builder.andCustomWhere(userFilter.getWhereClause(), userFilterValue);
					}
				}
			}
		}
		
		String groupByString = "label";
		
		FacilioField severityfield = modBean.getField("severity", FacilioConstants.ContextNames.ALARM);
		
		
		if(report.getxAxisField() != null && severityfield != null && severityfield.getId() == report.getxAxisField().getId()) {
			JSONObject metaJson = new JSONObject();
			
			metaJson.put("Critical", "#ff5959");
			metaJson.put("Major", "#e79958");
			metaJson.put("Minor", "#e3c920");
			metaJson.put("Warning", "#7daeec");
			metaJson.put("Clear", "#6cbd85");
			metaJson.put("Fire", "#ca0a0a");
			metaJson.put("Info", "#086826");
			
			report.setMetaJson(metaJson);
		}
		if(report.getGroupBy() != null) {
			ReportFieldContext reportGroupByField = DashboardUtil.getReportField(report.getGroupByField());
			report.setGroupByField(reportGroupByField);
			FacilioField groupByField = reportGroupByField.getField();
			reportFieldLabelMap.put("groupBy", groupByField.getName());
			groupByField.setName("groupBy");
			fields.add(groupByField);
			builder.groupBy("groupBy");
			if(severityfield != null && severityfield.getId() == groupByField.getId()) {
				JSONObject metaJson = new JSONObject();
				
				metaJson.put("Critical", "#ff5959");
				metaJson.put("Major", "#e79958");
				metaJson.put("Minor", "#e3c920");
				metaJson.put("Warning", "#7daeec");
				metaJson.put("Clear", "#6cbd85");
				metaJson.put("Fire", "#ca0a0a");
				metaJson.put("Info", "#086826");
				
				report.setMetaJson(metaJson);
			}
			groupByString = groupByString + ",groupBy";
		}
			
		if (report.getY1AxisAggregateOpperator() != CommonAggregateOperator.ACTUAL) {
			builder.groupBy(groupByString);
		}
		
		if(report.getLimit() != null) {
			builder.limit(report.getLimit());
		}
		
		if(report.getOrderBy() != null) {
			if(report.getOrderByFunc() != null) {
				builder.orderBy(report.getOrderBy() +" "+report.getOrderByFunc().getStringValue());
			}
			else {
				builder.orderBy(report.getOrderBy());
			}
		}
		
		Condition dateCondition = null;
		Criteria criteria = null;
		if(baseLineId != -1) {
			BaseLineContext baseLineContext = BaseLineAPI.getBaseLine(baseLineId);
			DateRange dateRange;
			if(dateFilter != null) {
				LOGGER.severe("dateFilter --- "+dateFilter);
				dateRange = new DateRange((long)dateFilter.get(0), (long)dateFilter.get(1));
			}
			else {
				dateRange = report.getDateFilter().getOperator().getRange(report.getDateFilter().getValue());
			}
			LOGGER.severe("start -- "+dateRange.getStartTime() +" end -- "+dateRange.getEndTime());
			dateCondition = baseLineContext.getBaseLineCondition(report.getDateFilter().getField(), dateRange);
			String baseLineStartValue = dateCondition.getValue().substring(0,dateCondition.getValue().indexOf(","));
			this.baseLineComparisionDiff = dateRange.getStartTime() - Long.parseLong(baseLineStartValue);
			LOGGER.severe(""+dateCondition);
			builder.andCondition(dateCondition);
		}
		else if(report.getDateFilter() != null) {
			dateCondition = DashboardUtil.getDateCondition(report, dateFilter, module);
			builder.andCondition(dateCondition);
		}
		if(dateCondition != null) {
			if(dateCondition.getValue() != null && dateCondition.getValue().contains(",")) {
				String startTimeString  = dateCondition.getValue().substring(0, dateCondition.getValue().indexOf(",")).trim();
				String endTimeString  = dateCondition.getValue().substring( dateCondition.getValue().indexOf(",")+1,dateCondition.getValue().length()).trim();
				this.startTime = Long.parseLong(startTimeString);
				this.endTime = Long.parseLong(endTimeString);
			}
			else if(dateCondition.getOperator() != null && dateCondition.getOperator() instanceof DateOperators) {
				DateOperators dateOpp = (DateOperators)dateCondition.getOperator();
				DateRange range = dateOpp.getRange(dateCondition.getValue());
				this.startTime = range.getStartTime();
				this.endTime = range.getEndTime();
			}
		}
		if(criteriaId != -1) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), criteriaId);
		}
		if(criteria != null) {
			builder.andCriteria(criteria);
		}
		if(report.getCriteria() != null) {
			builder.andCriteria(report.getCriteria());
		}
		
		List<Map<String, Object>> rs = new ArrayList<>();
		boolean isWorkHourReport = false;
		if(report.getReportSpaceFilterContext() != null) {
			if(report.getReportSpaceFilterContext().getBuildingId() != null) {
				
				Long buildingId = report.getReportSpaceFilterContext().getBuildingId();
				
				if(buildingId.equals(-1l)) {
					List<BuildingContext> buildings = SpaceAPI.getAllBuildings();
					if(buildings != null && !buildings.isEmpty()) {
						buildingId = buildings.get(0).getId();
					}
				}
				
				List<Long> resourceList = DashboardUtil.getAllResources(buildingId);
				
				Condition spaceCondition = CriteriaAPI.getCondition("RESOURCE_ID", "resourceId",  StringUtils.join(resourceList, ","), NumberOperators.EQUALS);
				
				builder.andCondition(spaceCondition);
			}
			
			else if(report.getReportSpaceFilterContext().getSiteId() != null) {
				
				Long siteId = report.getReportSpaceFilterContext().getSiteId();
				
				if(siteId.equals(-1l)) {
					List<SiteContext> sites = SpaceAPI.getAllSites();
					if(sites != null && !sites.isEmpty()) {
						siteId = sites.get(0).getId();
					}
				}
				
				List<Long> resourceList = DashboardUtil.getAllResources(siteId);
				
				Condition spaceCondition = CriteriaAPI.getCondition("RESOURCE_ID", "resourceId",  StringUtils.join(resourceList, ","), NumberOperators.EQUALS);
				
				builder.andCondition(spaceCondition);
			}
			
			if(report.getReportSpaceFilterContext().getGroupBy() != null && report.getReportSpaceFilterContext().getGroupBy().contains("workhour")) {
				
				isWorkHourReport = true;
				FacilioModule whModule = modBean.getModule(FacilioConstants.ContextNames.USER_WORK_HOURS_READINGS);
				GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
				selectRecordBuilder.select(modBean.getAllFields(FacilioConstants.ContextNames.USER_WORK_HOURS_READINGS));
				selectRecordBuilder.table(whModule.getTableName());
				selectRecordBuilder.andCustomWhere("TTIME between ? and ?", startTime,endTime);
				selectRecordBuilder.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId());
				selectRecordBuilder.orderBy("TTIME");
				
				List<Map<String, Object>> props = selectRecordBuilder.get();
				Map<Long, Double> workhoursProp = null;
				if(report.getReportSpaceFilterContext().getGroupBy().contains("avgworkhour")) {
					
					workhoursProp = DashboardUtil.calculateWorkHours(props, startTime, endTime,true,false);
				}
				else if(report.getReportSpaceFilterContext().getGroupBy().contains("percentworkhour")) {
					
					workhoursProp = DashboardUtil.calculateWorkHours(props, startTime, endTime,false,true);
				}
				else {
					workhoursProp = DashboardUtil.calculateWorkHours(props, startTime, endTime,false,false);
				}
				if(workhoursProp != null && !workhoursProp.isEmpty()) {
					rs = DashboardUtil.convertMapToProps(workhoursProp);
				}
			}
		}
		
		fields.add(y1AxisField);
		fields.add(xAxisField);
		builder.select(fields);
		if(!isWorkHourReport) {
			rs = builder.get();
		}
		
		if(report.getGroupBy() != null) {
			
			Multimap<Object, JSONObject> res = ArrayListMultimap.create();
			HashMap<String, Object> labelMapping = new HashMap<>();
			HashMap<Long, JSONObject> buildingRes = new HashMap<>();
			if(reportContext.getxAxisaggregateFunction().equals(SpaceAggregateOperator.BUILDING.getValue()) || reportContext.getxAxisaggregateFunction().equals(SpaceAggregateOperator.SITE.getValue())) {
				for(int i=0;i<rs.size();i++) {
					Map<String, Object> thisMap = rs.get(i);
					if(thisMap.get("label") != null) {
						Long spaceId = (Long) thisMap.get("label");
						Object groupBy = thisMap.get("groupBy");
						Double value = Double.parseDouble(thisMap.get("value").toString());
						
						for(Long buildingId : spaceResourceMap.keySet()) {
							
							if(spaceResourceMap.get(buildingId).contains(spaceId)) {
								
								if(buildingRes.get(buildingId) != null) {
									JSONObject map = buildingRes.get(buildingId);
									if(map.containsKey(groupBy)) {
										Double value1 = (Double) map.get(groupBy);
										value1 = value1 + value;
										map.put(groupBy, value1);
									}
									else {
										map.put(groupBy, value);
									}
									buildingRes.put(buildingId, map);
								}
								else {
									JSONObject map = new JSONObject();
									map.put(groupBy, value);
									buildingRes.put(buildingId, map);
								}
								break;
							}
						}
					}
				}
				for(Long buildingId :buildingRes.keySet()) {
					JSONObject json = buildingRes.get(buildingId);
					
					if(report.getId() == 4131l) {
						
						JSONObject value = new JSONObject();
						value.put("label", "Ontime");
		 				value.put("value", json.get("Ontime"));
		 				
		 				res.put(buildingId, value);
		 				
		 				value = new JSONObject();
		 				value.put("label", "Overdue");
		 				value.put("value", json.get("Overdue"));
		 				
		 				res.put(buildingId, value);
					}
					else {
						if(json != null) {
							for(Object jsonKey : json.keySet()) {
								
								JSONObject value = new JSONObject();
								value.put("label", jsonKey);
				 				value.put("value", json.get(jsonKey));
				 				
				 				if(reportContext.getxAxisaggregateFunction().equals(SpaceAggregateOperator.SITE.getValue())) {
				 					SiteContext site = SpaceAPI.getSiteSpace(buildingId);
				 					res.put(site.getName(), value);
				 				}
				 				else {
				 					res.put(buildingId, value);
				 				}
							}
	 					}
					}
				}
			}
			else {
				for(int i=0;i<rs.size();i++) {
		 			Map<String, Object> thisMap = rs.get(i);
		 			if(thisMap!=null) {
		 				
		 					String strLabel = (thisMap.get("label") != null) ? thisMap.get("label").toString() : "Unknown";
		 					JSONObject value = new JSONObject();
			 				value.put("label", thisMap.get("groupBy"));
			 				value.put("value", thisMap.get("value"));
			 				
			 				Object xlabel = thisMap.get("label");
			 				if(thisMap.get("dummyField") != null) {
			 					xlabel = thisMap.get("dummyField");
			 				}
			 				if (labelMapping.containsKey(strLabel)) {
			 					xlabel = labelMapping.get(strLabel);
			 				}
			 				else {
			 					labelMapping.put(strLabel, xlabel);
			 				}
			 				res.put(xlabel, value);
		 			}
			 	}
			}
			JSONArray finalres = new JSONArray();
			for(Object key : res.keySet()) {
				JSONObject j1 = new JSONObject();
				j1.put("label", key);
				j1.put("value", res.get(key));
				finalres.add(j1);
			}
			
			ticketData = finalres;
		}
		else {
			variance = DashboardUtil.getStandardVariance(report,rs,null);
			JSONArray res = new JSONArray();
			Map<Long, Long> buildingResult = new HashMap<>();
			for(int i=0;i<rs.size();i++) {

				Map<String, Object> thisMap = rs.get(i);
				JSONObject component = new JSONObject();
				if(reportContext.getxAxisaggregateFunction().equals(SpaceAggregateOperator.BUILDING.getValue()) || reportContext.getxAxisaggregateFunction().equals(SpaceAggregateOperator.SITE.getValue())) {
					
					Object label = thisMap.get("label");
					Object value = thisMap.get("value");
					for(Long spaceId : spaceResourceMap.keySet()) {
						
						if(spaceResourceMap.get(spaceId).contains(label)) {
							Long buildingValue = buildingResult.get(spaceId);
							
							buildingValue = buildingValue != null ? buildingValue += (Long)value:(Long)value;
							
							buildingResult.put(spaceId, buildingValue);
							break;
						}
					}
				}
	 			else if(module.getName().equals(FacilioConstants.ContextNames.WORK_ORDER_REQUEST) && xAxisField.getColumnName().equals("URGENCY")) {
	 				if(thisMap!=null) {
	 					
	 					Object label = thisMap.get("label");
	 					if(label == null) {
	 						component.put("label", "Unknown");
	 					}
	 					else {
	 						Integer intval = (Integer)label;
	 						WORUrgency urgency = WorkOrderRequestContext.WORUrgency.getWORUrgency(intval);
	 						component.put("label", urgency.getStringVal());
	 					}
	 					component.put("value", thisMap.get("value"));
	 					res.add(component);
	 				}
	 			}
	 			else {
	 				if(thisMap!=null) {
		 				if(thisMap.get("dummyField") != null) {
		 					component.put("label", thisMap.get("dummyField"));
		 				}
		 				else {
		 					Object lbl = thisMap.get("label");
		 					if(report.getxAxisLabel() != null && report.getxAxisLabel().equals("Assets")) {
		 						ResourceContext resource = ResourceAPI.getResource((Long) lbl);
		 						if(resource != null) {
		 							component.put("label", resource.getName());
			 						component.put("labelId", lbl);
		 						}
		 						else {
		 							component.put("label", lbl);
		 						}
		 					}
		 					else {
		 						component.put("label", lbl);
		 					}
		 				}
	 					component.put("value", thisMap.get("value"));
	 					res.add(component);
		 			}
	 			}
		 	}
			for( Long key:buildingResult.keySet()) {
				JSONObject component = new JSONObject();
				Object key1 = key;
				if(reportContext.getxAxisaggregateFunction().equals(SpaceAggregateOperator.SITE.getValue())) {
					SiteContext site = SpaceAPI.getSiteSpace(key);
					if(site != null) {
						key1 = site.getName();
					}
				}
				component.put("label", key1);
				component.put("value", buildingResult.get(key));
				res.add(component);
			}
			LOGGER.severe("res -- "+res);
			ticketData = res;
		}
		this.reportFieldLabelMap = reportFieldLabelMap; 
		return ticketData;
	}
	public boolean isDeleteWithWidget;
	
	public boolean isDeleteWithWidget() {
		return isDeleteWithWidget;
	}
	public void setDeleteWithWidget(boolean isDeleteWithWidget) {
		this.isDeleteWithWidget = isDeleteWithWidget;
	}
	public String deleteReport() throws Exception {									// report functions
		
		List<WidgetChartContext> widgetCharts = null;
		if(!isDeleteWithWidget) {
			widgetCharts = DashboardUtil.getWidgetFromDashboard(reportId,false);
		}
		if(widgetCharts == null || widgetCharts.isEmpty()) {
			DashboardUtil.deleteReport(reportId);
			return SUCCESS;
		}
		else {
			errorString = "Report Used In Dashboard";
		}
		return ERROR;
	}
	public String updateReport() throws Exception {										// report functions
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(moduleName != null) {
			
			FacilioModule module =  modBean.getModule(moduleName);
			reportContext.setModuleId(module.getModuleId());
		}
		
		reportContext = DashboardUtil.UpdateReport(reportContext);
		return SUCCESS;
	}
	
	private boolean excludeWeekends;
	
	public void setExcludeWeekends(boolean excludeWeekends) {
		this.excludeWeekends = excludeWeekends;
	}
	
	public boolean getExcludeWeekends() {
		return this.excludeWeekends;
	}
	
	public JSONArray safelimit;
	
	
	public JSONArray getSafelimit() {
		return safelimit;
	}
	public void setSafelimit(JSONArray safelimit) {
		this.safelimit = safelimit;
	}
	
	
	public static Double getTotalKwh(List<String> parentIds, long startTime, long endTime) throws Exception {			// report functions
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("energydata");
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
			.table(module.getTableName())
			.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
			.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
			;
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("value", "sum(TOTAL_ENERGY_CONSUMPTION_DELTA)", FieldType.NUMBER));
		builder.select(fields);
		
		builder.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentMeterId", StringUtils.join(parentIds, ','), NumberOperators.EQUALS));
		builder.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN));
		
		List<Map<String, Object>> rs = builder.get();
		Double res = 0d;
		if (rs != null && rs.size() > 0) {
			Double res1 = (Double) rs.get(0).get("value");
			if(res1 != null) {
				res = res1;
			}
		}
		return res;
	}
	
	JSONArray booleanResultGrouping;
	public JSONArray getBooleanResultOptions() {
		return booleanResultOptions;
	}
	public void setBooleanResultOptions(JSONArray booleanResultOptions) {
		this.booleanResultOptions = booleanResultOptions;
	}
	JSONArray booleanResultOptions;
	
	public JSONArray getBooleanResultGrouping() {
		return booleanResultGrouping;
	}
	public void setBooleanResultGrouping(JSONArray booleanResultGrouping) {
		this.booleanResultGrouping = booleanResultGrouping;
	}
	private JSONArray getDataForReadings(ReportContext report, FacilioModule module, JSONArray dateFilter, JSONObject userFilterValues, long baseLineId, long criteriaId) throws Exception {	// report functions
		JSONArray readingData = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(report.getCustomReportClass() != null) {
			Class<? extends CustomReport> classObject = (Class<? extends CustomReport>) Class.forName(report.getCustomReportClass());
			CustomReport job = classObject.newInstance();
			readingData = job.getData(report, module, dateFilter, userFilterValues, baseLineId, criteriaId);
			return readingData;
		}
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.andCustomWhere(module.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
				;
		
		if(!"development".equals(FacilioProperties.getConfig("environment"))) {
			builder.orderBy("TTIME");
		}
		if(module.getExtendModule() != null) {
			builder.innerJoin(module.getExtendModule().getTableName())
				.on(module.getTableName()+".Id="+module.getExtendModule().getTableName()+".Id");
		}
		
		ReportFieldContext reportXAxisField = report.getxAxisField();
		FacilioField xAxisField = reportXAxisField.getField();
		
		GenericSelectRecordBuilder subBuilder = null;
		FacilioModule energyMeterModule = modBean.getModule("energymeter");
		
		FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		if(!module.getName().equals(xAxisField.getModule().getName()) || (report.getId() == 4218l || report.getId() == 2868l || report.getId() == 4225l || report.getId() == 4226l)) {

			subBuilder = new GenericSelectRecordBuilder();
			subBuilder.table(energyMeterModule.getTableName());
			
			subBuilder.innerJoin(resourceModule.getTableName())
			.on(energyMeterModule.getTableName()+".ID="+resourceModule.getTableName()+".ID")
			.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", String.valueOf(false), BooleanOperators.IS));
			
			Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria("asset");
			
			if(scopeCriteria != null) {
				subBuilder.andCriteria(scopeCriteria);
			}
			
			if(!(report.getId() == 4218l || report.getId() == 2868l || report.getId() == 4225l || report.getId() == 4226l)) { 
				xAxisField.setColumnName("PARENT_METER_ID");
			}
			xAxisField.setName("parentId");
			xAxisField.setModule(module);
		}
		
		boolean isEnergyDataWithTimeFrame = false;
		if(xAxisField.getColumnName().contains("TTIME")) {
			isEnergyDataWithTimeFrame = true;
		}
		
		List<FacilioField> fields = new ArrayList<>();
		if(xAxisField.getDataTypeEnum().equals(FieldType.DATE_TIME)) {
			FacilioField dummyField = new FacilioField();
			dummyField.setColumnName(xAxisField.getColumnName());
			dummyField.setName("dummyField");
			if (report.getXAxisAggregateOpperator() == CommonAggregateOperator.ACTUAL) {
				dummyField = CommonAggregateOperator.ACTUAL.getSelectField(dummyField);
			}
			else {
				dummyField = NumberAggregateOperator.MAX.getSelectField(dummyField);
			}
			fields.add(dummyField);
		}
		
		AggregateOperator xAggregateOpperator = report.getXAxisAggregateOpperator();
		boolean isGroupBySpace = false;
		if(xAggregateOpperator.getValue() != CommonAggregateOperator.COUNT.getValue()) {
			if (xAggr > 0) {
				xAggregateOpperator = AggregateOperator.getAggregateOperator(xAggr);
				report.setxAxisaggregateFunction(xAggr);
			}
			else if ((dateFilter != null || report.getDateFilter() != null) && xAxisField.getDataTypeEnum().equals(FieldType.DATE_TIME)) {
				
				int oprId =  dateFilter != null ? DashboardUtil.predictDateOpperator(dateFilter) : report.getDateFilter().getOperatorId();
				
				boolean isRegression = (reportContext.getChartType() != null && reportContext.getChartType().equals(ReportChartType.REGRESSION.getValue()));
				if (isRegression) {
					xAggregateOpperator = DateAggregateOperator.FULLDATE;
				}
				else if(getIsHeatMap() || (reportContext.getChartType() != null && reportContext.getChartType().equals(ReportChartType.HEATMAP.getValue()))) {
					xAggregateOpperator = DateAggregateOperator.HOURSOFDAYONLY;
					report.setChartType(ReportChartType.HEATMAP.getValue());
				}
				else if (oprId == DateOperators.TODAY.getOperatorId() || oprId == DateOperators.YESTERDAY.getOperatorId() || oprId == DateOperators.TODAY_UPTO_NOW.getOperatorId()) {
					xAggregateOpperator = DateAggregateOperator.HOURSOFDAY;
				}
				else if (oprId == DateOperators.CURRENT_WEEK.getOperatorId() || oprId == DateOperators.LAST_WEEK.getOperatorId() || oprId == DateOperators.CURRENT_WEEK_UPTO_NOW.getOperatorId()) {
					if(!(xAggregateOpperator.equals(DateAggregateOperator.WEEKDAY) || xAggregateOpperator.equals(DateAggregateOperator.FULLDATE) )) {
						xAggregateOpperator = DateAggregateOperator.WEEKDAY;
					}
				}
				else if (oprId == DateOperators.CURRENT_MONTH.getOperatorId() || oprId == DateOperators.LAST_MONTH.getOperatorId() || oprId == DateOperators.CURRENT_MONTH_UPTO_NOW.getOperatorId() || oprId == DateOperators.LAST_N_DAYS.getOperatorId()) {
					
					if(!(xAggregateOpperator.equals(DateAggregateOperator.WEEK) || xAggregateOpperator.equals(DateAggregateOperator.WEEKANDYEAR) || xAggregateOpperator.equals(DateAggregateOperator.WEEKDAY) || xAggregateOpperator.equals(DateAggregateOperator.DAYSOFMONTH) || xAggregateOpperator.equals(DateAggregateOperator.FULLDATE))) {
						xAggregateOpperator = DateAggregateOperator.DAYSOFMONTH;
					}
				}
				else if (oprId == DateOperators.CURRENT_YEAR.getOperatorId() || oprId == DateOperators.LAST_YEAR.getOperatorId() || oprId == DateOperators.CURRENT_YEAR_UPTO_NOW.getOperatorId()) {
					
					if(!(xAggregateOpperator.equals(DateAggregateOperator.MONTH) || xAggregateOpperator.equals(DateAggregateOperator.WEEK) || xAggregateOpperator.equals(DateAggregateOperator.MONTHANDYEAR) || xAggregateOpperator.equals(DateAggregateOperator.WEEKANDYEAR) || xAggregateOpperator.equals(DateAggregateOperator.WEEKDAY) || xAggregateOpperator.equals(DateAggregateOperator.DAYSOFMONTH) || xAggregateOpperator.equals(DateAggregateOperator.FULLDATE))) {
						xAggregateOpperator = DateAggregateOperator.MONTHANDYEAR;
					}
				}
				dateAggr = (DateAggregateOperator) xAggregateOpperator;
				report.setxAxisaggregateFunction(xAggregateOpperator.getValue());
			}
		}
			
		if(xAggregateOpperator instanceof SpaceAggregateOperator) {
			isGroupBySpace = true;
			
			FacilioModule baseSpaceModule = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
			FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
			
			subBuilder.innerJoin(baseSpaceModule.getTableName())
			.on(baseSpaceModule.getTableName()+".ID=Energy_Meter.PURPOSE_SPACE_ID");
			
			subBuilder.andCustomWhere(baseSpaceModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId());
			
			if(xAggregateOpperator.equals(SpaceAggregateOperator.BUILDING) || xAggregateOpperator.equals(SpaceAggregateOperator.SITE)) {
				
				subBuilder.innerJoin(assetModule.getTableName())
				.on(assetModule.getTableName()+".ID = Energy_Meter.ID");

				subBuilder.andCustomWhere(baseSpaceModule.getTableName()+".SPACE_TYPE = " + BaseSpaceContext.SpaceType.BUILDING.getIntVal());
				
				EnergyMeterPurposeContext energyMeterPurpose = DeviceAPI.getEnergyMetersPurposeByName(DashboardUtil.ENERGY_METER_PURPOSE_MAIN);
				subBuilder.andCustomWhere("Energy_Meter.PURPOSE_ID = "+energyMeterPurpose.getId());
				subBuilder.andCustomWhere("Energy_Meter.IS_ROOT = 1");
				subBuilder.andCustomWhere("Assets.PARENT_ASSET_ID is null");
										
				report.getxAxisField().getField().setDisplayName("Building");
				report.getxAxisField().getField().setName("building");
			}
		}
		else if(xAggregateOpperator instanceof EnergyPurposeAggregateOperator) {
			
			FacilioModule energyMeterPurposeModule = modBean.getModule("energymeterpurpose");
			
			subBuilder.innerJoin(energyMeterPurposeModule.getTableName())
			.on(energyMeterPurposeModule.getTableName()+".ID=Energy_Meter.PURPOSE_ID")
			.andCustomWhere(energyMeterPurposeModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
			.andCustomWhere("Energy_Meter.IS_ROOT = 1")
			.andCustomWhere(energyMeterPurposeModule.getTableName()+".Name!=\"Main\"");
			if(reportContext.getReportSpaceFilterContext() != null && reportContext.getReportSpaceFilterContext().getSpaceFilterId() != null) {
				subBuilder.andCustomWhere(energyMeterModule.getTableName()+".PURPOSE_SPACE_ID="+reportContext.getReportSpaceFilterContext().getSpaceFilterId());
			}
			
			if(reportContext.getId() == 2952l || reportContext.getId() == 2951l) {
				subBuilder.andCustomWhere(energyMeterPurposeModule.getTableName()+".Name in (\"EB Main\",\"DG Main\")");
			}
			if(reportContext.getId() == 1154l || reportContext.getId() == 2950l) {
				subBuilder.andCustomWhere(energyMeterPurposeModule.getTableName()+".Name!=\"EB Main\"");
				subBuilder.andCustomWhere(energyMeterPurposeModule.getTableName()+".Name!=\"DG Main\"");
			}
			
			report.getxAxisField().getField().setDisplayName("Service");
			report.getxAxisField().getField().setName("Service");
		}
		else if(!report.getIsHighResolutionReport() && xAggr != 0 && xAggregateOpperator.getValue() != CommonAggregateOperator.COUNT.getValue()) {
			xAxisField = xAggregateOpperator.getSelectField(xAxisField);
		}
		
		FacilioField y1AxisField = null;
		ReportFieldContext reportY1AxisField;
		String yAxisFieldName = null;
		if(report.getY1Axis() != null || report.getY1AxisField() != null) {
			reportY1AxisField = report.getY1AxisField();
			AggregateOperator y1AggregateOpperator = report.getY1AxisAggregateOpperator();
			y1AxisField = reportY1AxisField.getField();
			yAxisFieldName = y1AxisField.getDisplayName();
			if(y1AggregateOpperator != null) {
				y1AxisField = y1AggregateOpperator.getSelectField(y1AxisField);
				if(y1AxisField.getId() > 0) {
					Pair<Double, Double> safelimitPair = CommonCommandUtil.getSafeLimitForField(y1AxisField.getId());
					
					JSONArray safeLimtJson = new JSONArray();
					safeLimtJson.add(safelimitPair.getKey());
					safeLimtJson.add(safelimitPair.getValue());
					
					setSafelimit(safeLimtJson);
				}
			}
		}
		else {
			y1AxisField = CommonAggregateOperator.COUNT.getSelectField(xAxisField);
			reportY1AxisField = new ReportFieldContext();
			reportY1AxisField.setModuleField(y1AxisField);
		}

		if(y1AxisField!= null && y1AxisField.getColumnName() != null && report.getIsHighResolutionReport() || xAggr == 0) {
			builder.andCustomWhere(y1AxisField.getColumnName()+" is not null");
		}
		JSONObject reportFieldLabelMap = new JSONObject();
		reportFieldLabelMap.put("label", xAxisField.getName());
		reportFieldLabelMap.put("value", y1AxisField.getName());
		
		xAxisField.setName("label");
		y1AxisField.setName("value");
		report.setY1AxisField(reportY1AxisField);

		if(userFilterValues != null && report.getReportUserFilters() != null) {
			for(ReportUserFilterContext userFilter : report.getReportUserFilters()) {
				if(userFilterValues.containsKey(userFilter.getId().toString())) {
					Object userFilterValue = userFilterValues.get(userFilter.getId().toString());
					if(userFilterValue instanceof JSONArray) {
						JSONArray userFilterJsonValue = (JSONArray) userFilterValue;
						builder.andCustomWhere(userFilter.getWhereClause(), userFilterJsonValue.get(0),userFilterJsonValue.get(1));
					}
					else {
						builder.andCustomWhere(userFilter.getWhereClause(), userFilterValue);
					}
				}
			}
		}
		String groupByString = "label";
		if(report.getGroupBy() != null) {
			ReportFieldContext reportGroupByField = report.getGroupByField();
			FacilioField groupByField = reportGroupByField.getField();
			reportFieldLabelMap.put("groupBy", groupByField.getName());
			groupByField.setName("groupBy");
			
			fields.add(groupByField);
			groupByString = groupByString + ",groupBy";
		}
			
		if (report.getY1AxisAggregateOpperator() != CommonAggregateOperator.ACTUAL) {
			builder.groupBy(groupByString);
		}
		
		if(report.getLimit() != null) {
			builder.limit(report.getLimit());
		}
		if(report.getOrderBy() != null) {
			if(report.getOrderByFunc() != null) {
				builder.orderBy(report.getOrderBy() +" "+report.getOrderByFunc().getStringValue());
			}
			else {
				builder.orderBy(report.getOrderBy());
			}
		}
		
		Condition dateCondition = null;
		Criteria criteria = null;
		String baseLineName = null;
		if(baseLineId != -1) {
			
			if(report != null && (AccountUtil.getCurrentOrg().getId() == 116l || AccountUtil.getCurrentOrg().getId() == 104l  || AccountUtil.getCurrentOrg().getId() == 133l)) {
				report.setReportColor("#ec598c");
			}
			BaseLineContext baseLineContext = BaseLineAPI.getBaseLine(baseLineId);
			if(baseLineContext.getAdjustType() <= 0) {
				baseLineContext.setAdjustType(1);
			}
			baseLineName = baseLineContext.getName();
			DateRange dateRange;
			if(dateFilter != null) {
				LOGGER.severe("dateFilter --- "+dateFilter);
				dateRange = new DateRange((long)dateFilter.get(0), (long)dateFilter.get(1));
			}
			else {
				dateRange = report.getDateFilter().getOperator().getRange(report.getDateFilter().getValue());
			}
			LOGGER.severe("isEnergyDataWithTimeFrame -- "+isEnergyDataWithTimeFrame+" DateTimeUtil.getDayStartTime(1) -- "+DateTimeUtil.getDayStartTime(1));
			if(!isEnergyDataWithTimeFrame && ((DateTimeUtil.getDayStartTime(1) - 5000) < dateRange.getEndTime() && dateRange.getEndTime() <= DateTimeUtil.getDayStartTime(1)) ) {
				dateRange.setEndTime(DateTimeUtil.getCurrenTime());
			}
			if(module != null && module.getName().equals("dewabill")) {
				dateRange.setEndTime(dateRange.getEndTime()+1001);
			}
			LOGGER.severe("start -- "+dateRange.getStartTime() +" end -- "+dateRange.getEndTime());
			dateCondition = baseLineContext.getBaseLineCondition(report.getDateFilter().getField(), dateRange);
			String baseLineStartValue = dateCondition.getValue().substring(0,dateCondition.getValue().indexOf(","));
			this.baseLineComparisionDiff = dateRange.getStartTime() - Long.parseLong(baseLineStartValue);
			builder.andCondition(dateCondition);
		}
		else if(report.getDateFilter() != null) {
			
			dateCondition = DashboardUtil.getDateCondition(report, dateFilter, module);
			LOGGER.log(Level.SEVERE, "dateCondition -- "+dateCondition);
			builder.andCondition(dateCondition);
		}
		LOGGER.log(Level.SEVERE, "report.getDateFilter() -- "+report.getDateFilter());
		if(dateCondition != null) {
			if(dateCondition.getValue() != null && dateCondition.getValue().contains(",")) {
				String startTimeString  = dateCondition.getValue().substring(0, dateCondition.getValue().indexOf(",")).trim();
				String endTimeString  = dateCondition.getValue().substring( dateCondition.getValue().indexOf(",")+1,dateCondition.getValue().length()).trim();
				this.startTime = Long.parseLong(startTimeString);
				this.endTime = Long.parseLong(endTimeString);
			}
			else if(dateCondition.getOperator() != null && dateCondition.getOperator() instanceof DateOperators) {
				DateOperators dateOpp = (DateOperators)dateCondition.getOperator();
				DateRange range = dateOpp.getRange(dateCondition.getValue());
				this.startTime = range.getStartTime();
				this.endTime = range.getEndTime();
			}
		}
		List<String> meterIdsUsed = new ArrayList<>();
		if(criteriaId != -1) {
			criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), criteriaId);
		}
		if(criteria != null) {
			builder.andCriteria(criteria);
			String parentIdString = WorkflowUtil.getParentIdFromCriteria(criteria);
			meterIdsUsed.add(parentIdString);
		}
		if(report.getCriteria() != null) {
			builder.andCriteria(report.getCriteria());
			String parentIdString = WorkflowUtil.getParentIdFromCriteria(report.getCriteria());
			meterIdsUsed.add(parentIdString);
		}
		
		String energyMeterValue = "";
		JSONObject buildingVsMeter = new JSONObject();
		Map<Long,Double> buildingVsArea = null;
		HashMap <Long, ArrayList<Long>> purposeVsMeter= new HashMap<Long,ArrayList<Long>>();
		JSONObject purposeVsMeter1 = new JSONObject();
		Condition buildingCondition = null;
		Multimap<Long,EnergyMeterContext> energyMeterMap =  ArrayListMultimap.create();
		
		if(subBuilder != null) {
			List<FacilioField> subFields = new ArrayList<>(); 
			
			FacilioField facilioField = new FacilioField();
			facilioField.setColumnName("ID");
			facilioField.setModule(energyMeterModule);
			facilioField.setName("id");
			facilioField.setDataType(FieldType.NUMBER);
			subFields.add(facilioField);
			
			FacilioField facilioField1 = new FacilioField();
			facilioField1.setColumnName("PURPOSE_SPACE_ID");
			facilioField1.setModule(energyMeterModule);
			facilioField1.setName("purposeSpace");
			facilioField1.setDataType(FieldType.NUMBER);
			subFields.add(facilioField1);
			
			FacilioField facilioField2 = new FacilioField();
			facilioField2.setColumnName("PURPOSE_ID");
			facilioField2.setModule(energyMeterModule);
			facilioField2.setDataType(FieldType.NUMBER);
			facilioField2.setName("purpose");
			
			subFields.add(facilioField2);
			subBuilder.select(subFields);
			
			List<Map<String, Object>> props = subBuilder.get();
			LOGGER.severe("subBuilder -- "+subBuilder);
			if(props != null && !props.isEmpty()) {
				
				List<Long> meterIds = new ArrayList<Long>();
				for(Map<String, Object> prop:props) {
					
					meterIds.add((long) prop.get("id"));
					
					if(isGroupBySpace) {
						buildingVsMeter.put((long) prop.get("id"), (long) prop.get("purposeSpace"));
					}
					
					ArrayList<Long> meterList = purposeVsMeter.get((long) prop.get("purpose"));
					if (meterList == null) {
						meterList = new ArrayList<Long>();
					}
					meterList.add((long) prop.get("id"));
					purposeVsMeter.put((long) prop.get("purpose"), meterList);
					
					purposeVsMeter1.put((long) prop.get("id"), (long) prop.get("purpose"));
				}
				if(isGroupBySpace) {
					String buildingList = StringUtils.join(buildingVsMeter.values(),",");
					buildingVsArea = ReportsUtil.getMapping(SpaceAPI.getBuildingArea(buildingList), "ID", "AREA");
				}
				
				String meterIdStr = StringUtils.join(meterIds, ",");
				buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","parentId", meterIdStr, NumberOperators.EQUALS);
				if(report.getId() == 4218l || report.getId() == 2868l || report.getId() == 4225l || report.getId() == 4226l) {
					buildingCondition = CriteriaAPI.getCondition("PARENT_ID","PARENT_ID", meterIdStr, NumberOperators.EQUALS);
				}
			}
			LOGGER.severe("buildingCondition -- "+buildingCondition);
		}
		
		if (buildingCondition == null && report.getReportSpaceFilterContext() != null) {
			
			if("chiller".equals(report.getReportSpaceFilterContext().getGroupBy())) {
				
				if(report.getReportSpaceFilterContext().getChillerId() != null) {
					Long chillerId = report.getReportSpaceFilterContext().getChillerId();
					if(report.getReportSpaceFilterContext().getChillerId() < 0) {
						List<AssetContext> chillers = DeviceAPI.getAllChillerMeters();
						if(chillers != null && !chillers.isEmpty()) {
							chillerId = chillers.get(0).getId();
						}
					}
					buildingCondition = CriteriaAPI.getCondition("PARENT_ID","parentId", chillerId+"", NumberOperators.EQUALS);
				}
			}
			else if("chillerPlant".equals(report.getReportSpaceFilterContext().getGroupBy())) {
				
				if(report.getReportSpaceFilterContext().getChillerId() != null) {
					Long chillerId = report.getReportSpaceFilterContext().getChillerId();
					if(report.getReportSpaceFilterContext().getChillerId() < 0) {
						List<AssetContext> chillers = DeviceAPI.getAllChillerPlants();
						if(chillers != null && !chillers.isEmpty()) {
							chillerId = chillers.get(0).getId();
						}
					}
					buildingCondition = CriteriaAPI.getCondition("PARENT_ID","parentId", chillerId+"", NumberOperators.EQUALS);
				}
			}
			else if (report.getReportSpaceFilterContext().getBuildingId() != null) {
				if ("service".equalsIgnoreCase(report.getReportSpaceFilterContext().getGroupBy())) {
					
					List<EnergyMeterContext> meters = DeviceAPI.getRootServiceMeters(report.getReportSpaceFilterContext().getBuildingId()+"");
					if(report.getId() == 2957l || report.getId() == 2795l) {
						List<EnergyMeterContext> omitmeters = new ArrayList<>();
						for(EnergyMeterContext meter :meters) {
							if(meter.getPurpose().getId() == 372l || meter.getPurpose().getId() == 373l) {
								omitmeters.add(meter);
							}
						}
						meters.removeAll(omitmeters);
					}
					if (meters != null && meters.size() > 0) {
						List<Long> meterIds = new ArrayList<Long>();
						for (EnergyMeterContext meter : meters) {
							meterIds.add(meter.getId());
							buildingVsMeter.put(meter.getId(), meter.getPurpose().getId());
						}
						
						String meterIdStr = StringUtils.join(meterIds, ",");
						energyMeterValue = meterIdStr;
						buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
					}
					
					FacilioField groupByField = new FacilioField();
					groupByField.setName("groupBy");
					groupByField.setDataType(FieldType.NUMBER);
					groupByField.setColumnName("PARENT_METER_ID");
					groupByField.setDisplayName("Service");
					groupByField.setModule(module);
					fields.add(groupByField);
					builder.groupBy("label, groupBy");
					
					ReportFieldContext groupByReportField = new ReportFieldContext();
					groupByReportField.setModuleField(groupByField);
					
					report.setGroupByField(groupByReportField);
					
					report.setGroupBy(-1L);
				}
				else if ("floor".equalsIgnoreCase(report.getReportSpaceFilterContext().getGroupBy())) {
					
					EnergyMeterPurposeContext purpose = DeviceAPI.getEnergyMetersPurposeByName(DashboardUtil.ENERGY_METER_PURPOSE_MAIN);
					
					SelectRecordsBuilder<EnergyMeterContext> builder12 = new SelectRecordsBuilder<EnergyMeterContext>()
							.table(modBean.getModule("energymeter").getTableName())
							.moduleName("energymeter")
							.beanClass(EnergyMeterContext.class)
							.select(modBean.getAllFields("energymeter"))
							.innerJoin(modBean.getModule("basespace").getTableName())
							.on(modBean.getModule("basespace").getTableName()+".ID = "+modBean.getModule("energymeter").getTableName()+".PURPOSE_SPACE_ID")
							.andCustomWhere(modBean.getModule("basespace").getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
							.andCustomWhere(modBean.getModule("basespace").getTableName()+".BUILDING_ID="+ report.getReportSpaceFilterContext().getBuildingId())
							.andCustomWhere(modBean.getModule("basespace").getTableName()+".SPACE_TYPE= ?",SpaceType.FLOOR.getIntVal())
							.andCustomWhere(modBean.getModule("energymeter").getTableName()+".IS_ROOT= 1")
							.andCustomWhere(modBean.getModule("energymeter").getTableName()+".PURPOSE_ID= ?",purpose.getId());
					
					
					
					List<EnergyMeterContext> props = builder12.get();
					
					// LOGGER.severe("builder12 -- "+builder12);
					List<Long> meterIds = new ArrayList<Long>();
					if(props != null && !props.isEmpty()) {
						for(EnergyMeterContext energyMeterContext:props) {

							energyMeterMap.put(energyMeterContext.getPurposeSpace().getId(), energyMeterContext);
						}
						for(EnergyMeterContext energyMeterContext:props) {
							
							if(energyMeterContext.isRoot() && energyMeterContext.getPurpose() != null && DashboardUtil.ENERGY_METER_PURPOSE_MAIN.equals(energyMeterContext.getPurpose().getName())) {
								if(energyMeterMap.containsKey(energyMeterContext.getSpaceId())) {
									energyMeterMap.removeAll(energyMeterContext.getSpaceId());
								}
								energyMeterMap.put(energyMeterContext.getPurposeSpace().getId(), energyMeterContext);
							}
						}
						for(EnergyMeterContext em : energyMeterMap.values()) {
							meterIds.add(em.getId());
						}
					}
					// LOGGER.severe("energyMeterMap -- "+energyMeterMap);
					// LOGGER.severe("meterIds -- "+meterIds);
					if(!meterIds.isEmpty()) {
						String meterIdStr = StringUtils.join(meterIds, ",");
						energyMeterValue = meterIdStr;
						buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
					}
					FacilioField groupByField = new FacilioField();
					groupByField.setName("groupBy");
					groupByField.setDataType(FieldType.NUMBER);
					groupByField.setColumnName("PARENT_METER_ID");
					groupByField.setDisplayName("Service");
					groupByField.setModule(module);
					fields.add(groupByField);
					builder.groupBy("label, groupBy");
					
					ReportFieldContext groupByReportField = new ReportFieldContext();
					groupByReportField.setModuleField(groupByField);
					
					report.setGroupByField(groupByReportField);
					
					report.setGroupBy(-1L);
				}
				else if ("cost".equalsIgnoreCase(report.getReportSpaceFilterContext().getGroupBy())) {
					List<EnergyMeterContext> meters = DashboardUtil.getMainEnergyMeter(report.getReportSpaceFilterContext().getBuildingId()+"");
					if (meters != null && meters.size() > 0) {
						List<Long> meterIds = new ArrayList<Long>();
						for (EnergyMeterContext meter : meters) {
							meterIds.add(meter.getId());
						}
						
						String meterIdStr = StringUtils.join(meterIds, ",");
						energyMeterValue = meterIdStr;
						buildingCondition = CriteriaAPI.getCondition("PARENT_ID","PARENT_ID", meterIdStr, NumberOperators.EQUALS);
					}
				}
				else if ("lpg".equalsIgnoreCase(report.getReportSpaceFilterContext().getGroupBy())) {
					AssetCategoryContext category = AssetsAPI.getCategory("LPG");
					if(category != null) {
						
						List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(category.getId(),report.getReportSpaceFilterContext().getBuildingId());
						
						if (assets != null && assets.size() > 0) {
							
							List<Long> meterIds = new ArrayList<Long>();
							for (AssetContext asset : assets) {
								
								meterIds.add(asset.getId());
							}
							
							String meterIdStr = StringUtils.join(meterIds, ",");
							energyMeterValue = meterIdStr;
							buildingCondition = CriteriaAPI.getCondition("PARENT_ID","PARENT_ID", meterIdStr, NumberOperators.EQUALS);
						}
					}
					
				}
				else if ("main_nv".equalsIgnoreCase(report.getReportSpaceFilterContext().getGroupBy())) {
					
					FacilioModule module123 = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
					
					EnergyMeterPurposeContext energyMeterPurpose = DeviceAPI.getEnergyMetersPurposeByName(DashboardUtil.ENERGY_METER_PURPOSE_MAIN);
					SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
							new SelectRecordsBuilder<EnergyMeterContext>()
							.select(modBean.getAllFields(module123.getName()))
							.module(module123)
							.beanClass(EnergyMeterContext.class)
							.andCustomWhere("IS_ROOT= ?", false)
							.andCustomWhere("IS_VIRTUAL= ?", false)
							.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE_SPACE_ID",report.getReportSpaceFilterContext().getBuildingId()+"",NumberOperators.EQUALS))
							.andCondition(CriteriaAPI.getCondition("PURPOSE_ID","PURPOSE_ID",energyMeterPurpose.getId()+"",NumberOperators.EQUALS))
							.maxLevel(0);
					
					List<EnergyMeterContext> meters = selectBuilder.get();
					if (meters != null && meters.size() > 0) {
						List<Long> meterIds = new ArrayList<Long>();
						for (EnergyMeterContext meter : meters) {
							meterIds.add(meter.getId());
						}
						
						String meterIdStr = StringUtils.join(meterIds, ",");
						energyMeterValue = meterIdStr;
						buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
					}
					
				}
				else {
					List<EnergyMeterContext> meters = DashboardUtil.getMainEnergyMeter(report.getReportSpaceFilterContext().getBuildingId()+"");
					if (meters != null && meters.size() > 0) {
						List<Long> meterIds = new ArrayList<Long>();
						for (EnergyMeterContext meter : meters) {
							meterIds.add(meter.getId());
						}
						
						String meterIdStr = StringUtils.join(meterIds, ",");
						energyMeterValue = meterIdStr;
						buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
						if(report.getId() == 4217l) {
							buildingCondition = CriteriaAPI.getCondition("PARENT_ID","PARENT_ID", meterIdStr, NumberOperators.EQUALS);
						}
					}
				}
			}
			else if (report.getReportSpaceFilterContext() != null && report.getReportSpaceFilterContext().getSiteId() != null) {
				
				if ("service".equalsIgnoreCase(report.getReportSpaceFilterContext().getGroupBy())) {
					
					List<EnergyMeterContext> meters = DashboardUtil.getRootServiceMeters(report.getReportSpaceFilterContext().getSiteId()+"");
					if(report.getId() == 2957l || report.getId() == 2795l) {
						List<EnergyMeterContext> omitmeters = new ArrayList<>();
						for(EnergyMeterContext meter :meters) {
							if(meter.getPurpose().getId() == 372l || meter.getPurpose().getId() == 373l) {
								omitmeters.add(meter);
							}
						}
						meters.removeAll(omitmeters);
					}
					if (meters != null && meters.size() > 0) {
						List<Long> meterIds = new ArrayList<Long>();
						for (EnergyMeterContext meter : meters) {
							meterIds.add(meter.getId());
							buildingVsMeter.put(meter.getId(), meter.getPurpose().getId());
						}
						
						String meterIdStr = StringUtils.join(meterIds, ",");
						energyMeterValue = meterIdStr;
						buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
					}
					
					FacilioField groupByField = new FacilioField();
					groupByField.setName("groupBy");
					groupByField.setDataType(FieldType.NUMBER);
					groupByField.setColumnName("PARENT_METER_ID");
					groupByField.setDisplayName("Service");
					groupByField.setModule(module);
					fields.add(groupByField);
					builder.groupBy("label, groupBy");
					
					ReportFieldContext groupByReportField = new ReportFieldContext();
					groupByReportField.setModuleField(groupByField);
					
					report.setGroupByField(groupByReportField);
					
					report.setGroupBy(-1L);
				}
				else {
					if(report.getReportSpaceFilterContext().getSiteId().equals(-1l)) {
						
						List<SiteContext> sites = SpaceAPI.getAllSites();
						if(sites != null && !sites.isEmpty()) {
							Long siteId = sites.get(0).getId();
							report.getReportSpaceFilterContext().setSiteId(siteId);
						}
					}
					Long siteId = report.getReportSpaceFilterContext().getSiteId();
					
					List<EnergyMeterContext> meters = DashboardUtil.getMainEnergyMeter(siteId+"");
					
					if (meters != null && meters.size() > 0) {
						List<Long> meterIds = new ArrayList<Long>();
						for (EnergyMeterContext meter : meters) {
							meterIds.add(meter.getId());
						}
						
						String meterIdStr = StringUtils.join(meterIds, ",");
						energyMeterValue = meterIdStr;
						buildingCondition = CriteriaAPI.getCondition("PARENT_METER_ID","PARENT_METER_ID", meterIdStr, NumberOperators.EQUALS);
					}
				}
			}
		}
		fields.add(y1AxisField);
		fields.add(xAxisField);
		if (module != null && "energydata".equalsIgnoreCase(module.getName()) && ((report != null && report.getIsHighResolutionReport()) || xAggr == 0)) {
			FacilioField markedField = modBean.getField("marked", module.getName());
			if (markedField != null) {
				fields.add(markedField);
			}
		}
		if (module != null && "energydata".equalsIgnoreCase(module.getName())) {
			FacilioField markedField = modBean.getField("marked", module.getName());
			builder.andCondition(CriteriaAPI.getCondition(markedField, "false", BooleanOperators.IS));
		}
				
		builder.select(fields);
		if(buildingCondition != null) {
			builder.andCondition(buildingCondition);
			String meterIds = buildingCondition.getValue();
			if(meterIds != null) {
				String[] meterArray = meterIds.split(",");
				for(String meter :meterArray) {
					meterIdsUsed.add(meter);
				}
			}
		}
		
		if (excludeWeekends) {
			String timeZone = DateTimeUtil.getDateTime().getOffset().toString().equalsIgnoreCase("Z") ? "+00:00":DateTimeUtil.getDateTime().getOffset().toString();
			
			int[] weekendDays = DateTimeUtil.getWeekendDays(null);
			if (weekendDays.length == 1) {
				builder.andCustomWhere("DAYOFWEEK(CONVERT_TZ(from_unixtime(floor(TTIME/1000)),@@session.time_zone,'" + timeZone + "')) <> ?", weekendDays[0]);
			}
			else {
				builder.andCustomWhere("DAYOFWEEK(CONVERT_TZ(from_unixtime(floor(TTIME/1000)),@@session.time_zone,'" + timeZone + "')) <> ? AND DAYOFWEEK(CONVERT_TZ(from_unixtime(floor(TTIME/1000)),@@session.time_zone,'" + timeZone + "')) <> ?", weekendDays[0], weekendDays[1]);
			}
		}
		List<Map<String, Object>> rs = null;
		
		// LOGGER.severe("buildingCondition 1 -- "+buildingCondition);
		if((subBuilder != null || report.getReportSpaceFilterContext() != null) && buildingCondition == null) {
//			dont query return null;
			rs  = new ArrayList<>();
		}
		else {
			rs = builder.get();
			
			if (getIsHeatMap() || (report != null && report.getChartType() != null && report.getChartType() == ReportContext.ReportChartType.HEATMAP.getValue())) {
				builder.orderBy("value");
//				this.calculateHeatMapRange(reportDataSQL, fields);
			}
		}
		
//		LOGGER.severe("builder --- "+reportContext.getId() +"   "+baseLineId);
		// LOGGER.severe("builder --- "+builder);
		
		if (report.getCriteria() != null) {
			criteria = report.getCriteria();
			if(report.getCriteria().getCriteriaId() != -1) {
				criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getOrgId(), report.getCriteria().getCriteriaId());
			}
			if(criteria != null && criteria.getConditions() != null) {
				Map<String, Condition> conditions = criteria.getConditions();
				for(Condition condition:conditions.values()) {
					if(condition.getFieldName().equals("parentId")) {
						energyMeterValue = energyMeterValue + condition.getValue() +",";
					}
				}
			}
		}
		
		if (energyMeterValue != null && !"".equalsIgnoreCase(energyMeterValue.trim())) {
			this.meterIds = energyMeterValue.split(",");
			
			LegendMode legendMode = (this.legendMode != null && !this.legendMode.trim().equals("")) ? LegendMode.valueOf(this.legendMode) : reportContext.getLegendMode();
			if (legendMode == null) {
				legendMode = LegendMode.RESOURCE_WITH_READING_NAME;
			}
			
			String resourceName = null;
			String readingName = null;
			ResourceContext res = ResourceAPI.getResource(Long.parseLong(this.meterIds[0]));
			if (res != null) {
				resourceName = res.getName();
			}
			if (yAxisFieldName != null) {
				readingName = yAxisFieldName; 
			}
			
			if (legendMode == LegendMode.READING_NAME && readingName != null) {
				this.entityName = readingName;
			}
			else if (legendMode == LegendMode.RESOURCE_NAME && resourceName != null) {
				this.entityName = resourceName;
			}
			else {
				this.entityName = resourceName;
				if (readingName != null) {
					this.entityName = readingName + (this.entityName != null ? " ("+this.entityName+")" : "");
				}
			}
		}
		if (this.entityName != null && baseLineName != null) {
			this.entityName += " - " + baseLineName;
		}
		if(this.reportContext != null && this.reportContext.getReportEntityId() != null && this.reportContext.getReportEntityId().equals(2912l)) { 
			this.entityName = report.getName();
		}
		Map<String, Double> violatedReadings = getViolatedReadings(report, dateFilter, baseLineId);
		
		if(report.getGroupBy() != null) {
			
			Multimap<Object, JSONObject> res = ArrayListMultimap.create();
			
			HashMap<String, Object> labelMapping = new HashMap<>();
			
			HashMap<String, Object> purposeMapping = new HashMap<>();
			
			if (report.getReportSpaceFilterContext() != null && report.getReportSpaceFilterContext().getBuildingId() != null && "floor".equalsIgnoreCase(report.getReportSpaceFilterContext().getGroupBy())) {
				List<String> lables = getDistinctLabel(rs);
				JSONArray totalconsumptionBySpace = new JSONArray();
				// LOGGER.severe("lables -- "+lables);
				if(!energyMeterMap.isEmpty()) {
					for(String label:lables) {
						JSONArray temp = new JSONArray();
						Long labelDummyValue = null;
						Set<Long> keys = energyMeterMap.keySet();
						ArrayList<Long> keys1 = new ArrayList<>();

						for(Long key:keys) {
							keys1.add(key);
						}
						Collections.sort(keys1);
						for(Long spaceId :keys1) {
							Double sumValue = 0.0;  
							List<EnergyMeterContext> energyMeterList = (List<EnergyMeterContext>) energyMeterMap.get(spaceId);
							for(EnergyMeterContext energyMeter:energyMeterList) {
								for(Map<String, Object> prop:rs) {
									if(label.equals(prop.get("label").toString()) && energyMeter.getId() == (long)prop.get("groupBy")) {
										Double value1 = 0.0d;
										if(prop.get("value") != null) {
											value1 = (Double) prop.get("value");
										}
										sumValue = sumValue + value1;
										labelDummyValue = (Long) prop.get("dummyField");
									}
								}
							}
							JSONObject value = new JSONObject();
							ResourceContext resource = ResourceAPI.getResource(spaceId);
							value.put("label", resource.getName());
							value.put("value", sumValue);
							temp.add(value);
						}
						JSONObject temp1 = new JSONObject();
						temp1.put("label", labelDummyValue);
						temp1.put("value", temp);
						totalconsumptionBySpace.add(temp1);
					}
				}
				// LOGGER.severe("totalconsumptionBySpace -- "+totalconsumptionBySpace);
				readingData = totalconsumptionBySpace;
			}
			else {
				for(int i=0;i<rs.size();i++) {
		 			Map<String, Object> thisMap = rs.get(i);
		 			if(thisMap!=null) {
		 				
		 				if ( report.getReportSpaceFilterContext() != null && ((report.getReportSpaceFilterContext().getBuildingId() == null || report.getReportSpaceFilterContext().getBuildingId() <= 0) && (report.getReportSpaceFilterContext().getSiteId() == null || report.getReportSpaceFilterContext().getSiteId() <= 0)) && "service".equalsIgnoreCase(report.getReportSpaceFilterContext().getGroupBy())) {
		 					Object xlabel = thisMap.get("label");
		 					if(thisMap.get("dummyField") != null) {
			 					xlabel = thisMap.get("dummyField");
			 				}
			 				if (labelMapping.containsKey(thisMap.get("label").toString())) {
			 					xlabel = labelMapping.get(thisMap.get("label").toString());
			 				}
			 				else {
			 					labelMapping.put(thisMap.get("label").toString(), xlabel);
			 				}
			 				
		 					Double xvalue = (Double) thisMap.get("value");
		 					String groupBy = purposeVsMeter.containsKey(thisMap.get("groupBy")) ? purposeVsMeter.get(thisMap.get("groupBy")).toString() : thisMap.get("groupBy").toString();
		 					
		 					if (purposeMapping.containsKey(xlabel + "-" + groupBy)) {
		 						JSONObject value = (JSONObject) res.get(xlabel);
		 						Double existingValue = (Double) value.get("value");
		 						value.put("value", existingValue + xvalue);
		 					}
		 					else {
		 						JSONObject value = new JSONObject();
		 						value.put("label", purposeVsMeter.containsKey(thisMap.get("groupBy")) ? purposeVsMeter.get(thisMap.get("groupBy")) : thisMap.get("groupBy"));
		 						value.put("value", xvalue);
		 						res.put(xlabel, value);
		 					}
		 				}
		 				else {
		 					String strLabel = (thisMap.get("label") != null) ? thisMap.get("label").toString() : "Unknown";
		 					JSONObject value = new JSONObject();
			 				value.put("label", buildingVsMeter.containsKey(thisMap.get("groupBy")) ? buildingVsMeter.get(thisMap.get("groupBy")) : thisMap.get("groupBy"));
			 				if ("cost".equalsIgnoreCase(report.getY1AxisUnit())) {
			 					Double d = (Double) thisMap.get("value");
			 					if(d == null) {
			 						d = 0d;
			 					}
			 					value.put("value", d *  ReportsUtil.getUnitCost(AccountUtil.getCurrentOrg().getOrgId()));
			 					value.put("orig_value", d);
			 				}
			 				else {
			 					value.put("value", thisMap.get("value"));
			 				}
			 				
			 				Object xlabel = thisMap.get("label");
			 				if(thisMap.get("dummyField") != null) {
			 					xlabel = thisMap.get("dummyField");
			 				}
			 				if (labelMapping.containsKey(strLabel)) {
			 					xlabel = labelMapping.get(strLabel);
			 				}
			 				else {
			 					labelMapping.put(strLabel, xlabel);
			 				}
			 				res.put(xlabel, value);
		 				}
		 			}
			 	}
			JSONArray finalres = new JSONArray();
			for(Object key : res.keySet()) {
				JSONObject j1 = new JSONObject();
				j1.put("label", key);
				j1.put("value", res.get(key));
				finalres.add(j1);
			}
				
			readingData = finalres;
			}
		}
		else {
			JSONArray res = new JSONArray();
			
			if(report.getY1AxisField() != null && report.getY1AxisField().getField() != null) {
				
				FacilioField yAxisField = report.getY1AxisField().getField();
				
				if(yAxisField.getDataTypeEnum().equals(FieldType.BOOLEAN)) {
					
					try {
						booleanResultOptions = new JSONArray();
						booleanResultGrouping = DashboardUtil.getGroupedBooleanFields(rs,booleanResultOptions);
						LOGGER.log(Level.SEVERE, "booleanResultGrouping -- "+booleanResultGrouping);
					}
					catch(Exception e) {
						LOGGER.log(Level.SEVERE, e.getMessage(),e);
					}
					
				}
			}
			
			JSONObject purposeIndexMapping = new JSONObject();
			
			if(!"eui".equalsIgnoreCase(report.getY1AxisUnit())) {
				variance = DashboardUtil.getStandardVariance(report,rs,meterIdsUsed);
				try {
					if ((report.getY1AxisField() != null && report.getY1AxisField().getField().getName().contains("cost")) || (reportFieldLabelMap != null && reportFieldLabelMap.containsKey(report.getY1AxisField().getField().getName()) && reportFieldLabelMap.get(report.getY1AxisField().getField().getName()) != null && reportFieldLabelMap.get(report.getY1AxisField().getField().getName()).toString().contains("cost"))) {
						
						if(meterIdsUsed != null ) {
							report.getCriteria();
							Double totalKwh = DashboardAction.getTotalKwh(meterIdsUsed, this.startTime, this.endTime);
							Double totalCost = (Double) variance.get("sum");
							
							Long startTime = DateTimeUtil.getDayStartTime(-1);
							Long endTime = DateTimeUtil.getDayStartTime();
							Double yesterdayKwh = DashboardAction.getTotalKwh(meterIdsUsed, startTime, endTime);
							Double yesterdayCost = (Double) rs.get(rs.size() - 1).get("value");
							
							variance = new JSONObject();
							variance.put("total_kwh", totalKwh);
							variance.put("total_cost", totalCost);
							if(meterIdsUsed.size() == 1) {
								variance.put("yesterday_kwh", yesterdayKwh);
								variance.put("yesterday_cost", yesterdayCost);
							}
						}
						
					}
				}
				catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Exception in variance calculation for cost report ", e);
				}
			}
			
			
			if(report.getId() == 3668l || report.getId() == 3754l) {
				
				Map<Long,Long> groupingData = new HashMap<>();
				
				groupingData.put(895467l, 895378l);
				groupingData.put(895470l, 895379l);
				groupingData.put(895457l, 895380l);
				groupingData.put(895471l, 895381l);
				groupingData.put(895469l, 895382l);
				groupingData.put(895458l, 895384l);
				groupingData.put(895465l, 895385l);
				groupingData.put(895466l, 895386l);
				groupingData.put(895482l, 890464l);
				groupingData.put(895486l, 890473l);
				groupingData.put(895487l, 890474l);
				groupingData.put(895484l, 890475l);
				groupingData.put(895479l, 890476l);
				groupingData.put(895480l, 890465l);
				groupingData.put(895478l, 890466l);
				groupingData.put(895481l, 890467l);
				groupingData.put(895483l, 890468l);
				groupingData.put(895485l, 890469l);
				groupingData.put(895477l, 890470l);
				groupingData.put(895475l, 890471l);
				groupingData.put(895476l, 890472l);
				
				Map<Long,JSONObject> result1 = new HashMap<>();
				
				for(int i=0;i<rs.size();i++) {
					Map<String, Object> thisMap = rs.get(i);
					if(thisMap!=null) {
						
						JSONObject temp = new JSONObject();
						Long label = (Long) thisMap.get("label");
						
						if(groupingData.containsKey(label)) {
							temp.put("label", "Dewa Meter");
						}
						else {
							temp.put("label", "Sub Meter");
						}
						
						temp.put("value", thisMap.get("value"));
						
						result1.put(label, temp);
					}
				}
				
				for(Long key :groupingData.keySet()) {
					
					JSONObject finalResult = new JSONObject(); 
					
					JSONObject jsonDewa = result1.get(key);
					JSONObject jsonMeter = result1.get(groupingData.get(key));
					
					JSONArray res1 = new JSONArray();
					
					if(jsonMeter != null || jsonDewa != null) {
						
						if(jsonDewa != null) {
							res1.add(jsonDewa);
						}
						if(jsonMeter != null) {
							res1.add(jsonMeter);
						}
						
						AssetContext asset = AssetsAPI.getAssetInfo(key);
						finalResult.put("label",asset.getName());
						
						finalResult.put("value", res1);
						
					}
					
					res.add(finalResult);
				}
				xAxisField.setDataType(1);
				report.setGroupBy(report.getxAxis());
				report.getGroupByField().getField().setDataType(1);
				
			}
			else {
				
				for(int i=0;i<rs.size();i++) {
					boolean newPurpose = false;
		 			Map<String, Object> thisMap = rs.get(i);
		 			JSONObject component = new JSONObject();
		 			if(thisMap!=null) {
		 				// checking and excluding the violated points
		 				if(AccountUtil.getCurrentOrg().getOrgId() == 116l) {
		 					
		 					if(thisMap.get("dummyField") != null) {
		 						
		 						Long currtime = (Long) thisMap.get("dummyField");
		 						
		 						if(report.getxAxisaggregateFunction() != null && report.getxAxisaggregateFunction().equals(DateAggregateOperator.FULLDATE.getValue())) {
		 							
		 							DateRange range = DateOperators.TODAY.getRange(null);
			 						if(currtime < range.getEndTime() && currtime >= range.getStartTime()) {
			 							continue;
			 						}
		 						}
		 					}
		 				}
		 				if (violatedReadings != null && thisMap.get("label") != null &&violatedReadings.containsKey(thisMap.get("label").toString())) {
		 					Double violatedValue = violatedReadings.get(thisMap.get("label").toString());
		 					Double d = (Double) thisMap.get("value");
		 					if (d != null) {
//		 						Double newValue = d - violatedValue;
//		 						if (newValue < 0) {
//		 							newValue = 0d;
//		 						}
		 						thisMap.put("value", 0d);
		 						component.put("violated_value", d);
		 						component.put("marked_value", violatedValue);
		 					}
		 				}
		 				
		 				if(thisMap.get("dummyField") != null) {
		 					component.put("label", thisMap.get("dummyField"));
		 				}
		 				else {
		 					Object lbl = thisMap.get("label");
		 					if (buildingVsMeter.containsKey(thisMap.get("label"))) {
		 						lbl = buildingVsMeter.get(thisMap.get("label"));
		 					}
		 					else if (purposeVsMeter1.containsKey(thisMap.get("label"))) {
		 						lbl = purposeVsMeter1.get(thisMap.get("label"));
		 						if (!purposeIndexMapping.containsKey(lbl)) {
		 							purposeIndexMapping.put(lbl, res.size());
		 							newPurpose = true;
		 						}
		 					}
		 					else if((report.getId() == 1963l || report.getId() == 3481l || report.getId() == 3653l || report.getId() == 3664l || report.getId() == 3663l || report.getId() == 3748l || report.getId() == 3754l || report.getId() == 3755l || report.getId() == 3756l || report.getId() == 3757l || report.getId() == 3758l || report.getId() == 3759l || report.getId() == 4225l || report.getId() == 4226l || report.getId() == 4306l) && xAxisField != null) {
		 						AssetContext context = AssetsAPI.getAssetInfo((Long) lbl);
		 						if(context != null) {
		 							lbl = context.getName();
		 						}
		 						xAxisField.setDataType(1);
		 					}
		 					component.put("label", lbl);
		 				}
		 				if (!newPurpose && purposeIndexMapping.containsKey(component.get("label"))) {
		 					JSONObject tmpComp = (JSONObject) res.get((Integer) purposeIndexMapping.get(component.get("label")));
		 					if ("cost".equalsIgnoreCase(report.getY1AxisUnit())) {
		 						Double d = (Double) thisMap.get("value");
		 						Double concatVal = d + (Double) tmpComp.get("orig_value");
		 						tmpComp.put("value", concatVal*ReportsUtil.getUnitCost(AccountUtil.getCurrentOrg().getOrgId()));
		 						tmpComp.put("orig_value", concatVal);
		 					}
		 					else {
		 						Double d = (Double) thisMap.get("value");
		 						tmpComp.get("value");
		 						tmpComp.put("value", thisMap.get("value"));
		 					}
		 				}
		 				else {
		 					if ("cost".equalsIgnoreCase(report.getY1AxisUnit())) {
		 						Double d = (Double) thisMap.get("value");
		 						component.put("value", d*ReportsUtil.getUnitCost(AccountUtil.getCurrentOrg().getOrgId()));
		 						component.put("orig_value", d);
		 					}
		 					else if ("eui".equalsIgnoreCase(report.getY1AxisUnit())) {
		 						Double d = (Double) thisMap.get("value");
		 						LOGGER.severe("(Long) component.get -- "+component.get("label"));
		 						Double buildingArea = buildingVsArea.get(component.get("label"));
		 						double eui = ReportsUtil.getEUI(d, buildingArea);
		 						component.put("value", eui);
		 						component.put("orig_value", d);
		 					}
		 					else {
		 						component.put("value", thisMap.get("value"));
		 					}
		 					if (thisMap.get("marked") != null) {
		 						component.put("marked", thisMap.get("marked"));
		 					}
		 					res.add(component);
		 				}
		 			}
			 	}
			}
			readingData = res;
			if(report.getId() == 1064l) {
				variance = DashboardUtil.getStandardVariance1(report,res,null);
			}
		}
		
		if(reportContext.getReportBenchmarkRelContexts() != null && !reportContext.getReportBenchmarkRelContexts().isEmpty()) {
			if(variance != null && variance.containsKey("space")) {
				spaceId = (Long) variance.get("space");
			}
			if(dateAggr == null && startTime > 0 && endTime > 0) {
				LOGGER.log(Level.SEVERE, "DATE AGGR NULL");
				dateAggr = DateAggregateOperator.FULLDATE;
				noOfDaysInReport = DashboardUtil.getNoOfDaysBetweenDateRange(startTime, endTime);
				LOGGER.log(Level.SEVERE, "DATE AGGR NULL -- "+noOfDaysInReport);
			}
			if(dateAggr != null) {
				if(!(dateAggr.equals(DateAggregateOperator.DATEANDTIME) || dateAggr.equals(DateAggregateOperator.HOURSOFDAY) || dateAggr.equals(DateAggregateOperator.HOURSOFDAYONLY))) {
					for(ReportBenchmarkRelContext reportBenchmarkRel : reportContext.getReportBenchmarkRelContexts()) {
						
						benchmarkId = reportBenchmarkRel.getBenchmarkId();
						
						calculateBenchmarkValue();
						BenchmarkContext benchmark = BenchmarkAPI.getBenchmark(benchmarkId);
						benchmark.setValue(value);
						String unit = "/"+Unit.SQUARE_METER.getSymbol();
						benchmark.setDisplayUnit(unit);
						addBenchmarks(benchmark);
						
					}
				}
			}
		}
		
		
		if(energyMeterValue != null && !"".equalsIgnoreCase(energyMeterValue.trim()) && isEnergyDataWithTimeFrame && !report.getIsComparisionReport() && report.getY1AxisField() != null) {
			
			JSONObject filterJson = new JSONObject();
				
			FacilioField field = report.getY1AxisField().getField();
			
			JSONObject fieldJson = new JSONObject();
			fieldJson.put("operatorId", NumberOperators.EQUALS.getOperatorId());
			JSONArray value = new JSONArray();
			value.add(""+field.getId());
			fieldJson.put("value", value);
			
			filterJson.put("readingFieldId", fieldJson);
			
			JSONObject resourceJson = new JSONObject();
			resourceJson.put("operatorId", PickListOperators.IS.getOperatorId());
			value = new JSONArray();
			for(String meterId:meterIds) {
				value.add(meterId);
			}
			resourceJson.put("value", value);
			
			filterJson.put("resource", resourceJson);
			
			if (dateCondition != null) {
				JSONObject startTimeJson = new JSONObject();
				startTimeJson.put("operatorId", DateOperators.BETWEEN.getOperatorId());
				if(dateCondition.getValue() != null && dateCondition.getValue().contains(",")) {
					value = new JSONArray();
					for(String s:dateCondition.getValue().split(",")) {
						value.add(s);
					}
					startTimeJson.put("value", value);
				}
				else if(dateCondition.getOperator() instanceof DateOperators) {
					DateOperators dateOperator = (DateOperators) dateCondition.getOperator();
					DateRange range = dateOperator.getRange(reportContext.getDateFilter().getValue());
					value = new JSONArray();
					value.add(""+range.getStartTime());
					value.add(""+range.getEndTime());
					startTimeJson.put("value", value);
				}
				
				filterJson.put("startTime", startTimeJson);
			}
			
			AlarmAction alarmAction = new AlarmAction();
			// LOGGER.severe("filterJson.toJSONString() -- "+filterJson.toJSONString());
			alarmAction.setFilters(filterJson.toJSONString());
			
			alarmAction.fetchReadingAlarms();
			
			setReadingAlarms(alarmAction.getReadingAlarms());
			
		}
		this.reportFieldLabelMap = reportFieldLabelMap;
		return readingData;
	}
	
	private List<ReadingAlarmContext> readingAlarms;
	public List<ReadingAlarmContext> getReadingAlarms() {
		return readingAlarms;
	}
	public void setReadingAlarms(List<ReadingAlarmContext> readingAlarms) {
		this.readingAlarms = readingAlarms;
	}
	
	private Map<String, Double> getViolatedReadings(ReportContext reportContext, JSONArray dateFilter, Long baseLineId) throws Exception {		// report functions
		
		Map<String, Double> violatedReadings = new HashMap<>();
		
		List<Integer> markType = new ArrayList<>();
		if (reportContext.getXAxisAggregateOpperator().getValue() == DateAggregateOperator.DAYSOFMONTH.getValue() || reportContext.getXAxisAggregateOpperator().getValue() == DateAggregateOperator.FULLDATE.getValue()) {
			markType.add(MarkedReadingContext.MarkType.HIGH_VALUE_DAILY_VIOLATION.getValue());
			markType.add(MarkedReadingContext.MarkType.DECREMENTAL_VALUE.getValue());
			// markType.add(MarkedReadingContext.MarkType.HIGH_VALUE_HOURLY_VIOLATION.getValue());
		}
		else if (reportContext.getXAxisAggregateOpperator().getValue() == DateAggregateOperator.HOURSOFDAY.getValue() || reportContext.getXAxisAggregateOpperator().getValue() == DateAggregateOperator.HOURSOFDAYONLY.getValue()) {
			markType.add(MarkedReadingContext.MarkType.HIGH_VALUE_HOURLY_VIOLATION.getValue());
			markType.add(MarkedReadingContext.MarkType.HIGH_VALUE_DAILY_VIOLATION.getValue());
			markType.add(MarkedReadingContext.MarkType.DECREMENTAL_VALUE.getValue());
		}
		
		if ((dateFilter != null || reportContext.getDateFilter() != null) && !excludeViolatedReadings && !markType.isEmpty()) {
			List<Long> timeRange = new ArrayList<>();
			if (dateFilter != null) {
				timeRange.add((Long) dateFilter.get(0));
				timeRange.add((Long) dateFilter.get(1));
			}
			else if (reportContext.getDateFilter() != null) {
				DateOperators operator = (DateOperators) Operator.getOperator(reportContext.getDateFilter().getOperatorId());
				
				DateRange range = operator.getRange(reportContext.getDateFilter().getValue());
				timeRange.add(range.getStartTime());
				timeRange.add(range.getEndTime());
			}
			
			if (baseLineId != -1) {
				BaseLineContext baseLineContext = BaseLineAPI.getBaseLine(baseLineId);
				
				Condition condition = baseLineContext.getBaseLineCondition(reportContext.getDateFilter().getField(), new DateRange(timeRange.get(0), timeRange.get(1)));
				String baseLineStartValue = condition.getValue().substring(0,condition.getValue().indexOf(",")).trim();
				String baseLineEndValue = condition.getValue().substring(condition.getValue().indexOf(",")+1, condition.getValue().length()).trim();
				
				List<Long> baseLineTimeRange = new ArrayList<>();
				baseLineTimeRange.add(Long.parseLong(baseLineStartValue));
				baseLineTimeRange.add(Long.parseLong(baseLineEndValue));
				timeRange = baseLineTimeRange;
			}
			
			List<Long> deviceList = new ArrayList<>();
			if (meterIds != null) {
				for (String meterId : meterIds) {
					deviceList.add(Long.parseLong(meterId));
				}
			}
			if (parentId >= 0) {
				deviceList.add(parentId);
			}
			
			List<Long> moduleList = new ArrayList<>();
			moduleList.add(reportContext.getModuleId());
			
			List<Long> fieldList = new ArrayList<Long>();
			fieldList.add(reportContext.getY1AxisField().getModuleField().getId());
			
			FacilioField label = new FacilioField();
			label.setColumnName("TTIME");
			label.setName("label");
			label = reportContext.getXAxisAggregateOpperator().getSelectField(label);
			
			FacilioField value = new FacilioField();
			value.setColumnName("ACTUAL_VALUE");
			value = NumberAggregateOperator.SUM.getSelectField(value);
			value.setName("value");
			
			List<Map<String, Object>> markedReadings = TimeSeriesAPI.getMarkedReadings(TimeSeriesAPI.getCriteria(timeRange, deviceList, moduleList, fieldList, markType), label, value);
			if (markedReadings != null && !markedReadings.isEmpty()) {
				for (Map<String, Object> reading : markedReadings) {
					if(reading.get("label") != null) {
						violatedReadings.put(reading.get("label").toString(), (Double) reading.get("value"));
					}
				}
			}
		}
		return violatedReadings; 
	}
	
	
	public static final int aswaqComp = 1,aswaqnonComp = 1,aswaqrep = 1,aswaqna = 1;
	
	public String getAswaqData() throws Exception {		// report functions
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		int tcompliance = 0,tnonCompliance = 0,trepeatFinding = 0,total = 0;
		
		List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
		
		reportData = new JSONArray();
		if(reportSpaceFilterContext != null) {
			buildingId = reportSpaceFilterContext.getBuildingId();
		}
		
		for(TicketCategoryContext category:categories) {
			
			List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
			
			if(workorders.isEmpty()) {
				continue;
			}
			
			int compliance = 0,nonCompliance = 0,repeatFinding = 0;
			for(WorkOrderContext workorder:workorders) {
				
				// LOGGER.log(Level.SEVERE, "buildingId --- "+buildingId);
				if(workorder.getResource() != null) {
					LOGGER.log(Level.SEVERE, "workorder.getResource().getId() --- "+workorder.getResource().getId());
				}
				if(buildingId != null && workorder.getResource() != null && workorder.getResource().getId() != buildingId) {
					continue;
				}
				// LOGGER.log(Level.SEVERE, "dateFilter --- "+dateFilter);
				// LOGGER.log(Level.SEVERE, "workorder.getResource().getId() --- "+workorder.getCreatedTime());
				if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
					continue;
				}
				if(AccountUtil.getCurrentOrg().getOrgId() == 108l) {
					compliance = 0;nonCompliance = 0;repeatFinding = 0;
				}
				
				Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
				FacilioContext context = new FacilioContext();
				
				context.put(FacilioConstants.ContextNames.ID, workorder.getId());
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
				context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
				context.put("isAsMap", true);
				chain.execute(context);
				
				List<Map<String, Object>> taskMap = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
				
				for(Map<String, Object> task : taskMap) {
					
					if(task.get("inputValue") != null) {
						
						String subject = (String) task.get("inputValue");
						
						subject = subject.trim();
						
						if (subject.endsWith("Non Compliance")) {
							nonCompliance += aswaqnonComp;
						}
						else if(subject.endsWith("Compliance")) {
							compliance += aswaqComp;
						}
						else if (subject.endsWith("Repeat Findings")) {
							repeatFinding += aswaqrep;
						}
						else if (subject.endsWith("Not Applicable")) {
						}
					}
				}
				if(AccountUtil.getCurrentOrg().getOrgId() == 108l) {
					JSONObject jsonObject = new JSONObject();
					
					jsonObject.put("area", workorder.getSubject());
					jsonObject.put("workOrderId", workorder.getId());
					jsonObject.put("fullScore", compliance);
					jsonObject.put("nonCompliance", nonCompliance);
					jsonObject.put("repeatFinding", repeatFinding);
					jsonObject.put("totalPointsEarned", compliance + nonCompliance + repeatFinding);
					
					reportData.add(jsonObject);
					
					tcompliance += compliance;
					tnonCompliance += nonCompliance;
					trepeatFinding += repeatFinding;
					total += (compliance + nonCompliance + repeatFinding);
				}
			}
			if(AccountUtil.getCurrentOrg().getOrgId() == 113l) {
				JSONObject jsonObject = new JSONObject();
				
				jsonObject.put("area", category.getName());
				jsonObject.put("fullScore", compliance);
				jsonObject.put("nonCompliance", nonCompliance);
				jsonObject.put("repeatFinding", repeatFinding);
				jsonObject.put("totalPointsEarned", compliance + nonCompliance + repeatFinding);
				
				reportData.add(jsonObject);
				
				tcompliance += compliance;
				tnonCompliance += nonCompliance;
				trepeatFinding += repeatFinding;
				total += (compliance + nonCompliance + repeatFinding);
			}
			
		}
		JSONObject finalres = new JSONObject();
		
		finalres.put("area", "Total");
		finalres.put("fullScore", tcompliance);
		finalres.put("nonCompliance", tnonCompliance);
		finalres.put("repeatFinding", trepeatFinding);
		finalres.put("totalPointsEarned", total);
		
		reportData.add(finalres);
		
		resultJSON = new JSONObject();
		
		double overallRating = 0;
		if (tcompliance != 0) {
			overallRating = (double)total/tcompliance;
		}
		
		overallRating = overallRating * 100;
		DecimalFormat df = new DecimalFormat(".##");
		resultJSON.put("tableData", reportData);
		resultJSON.put("overallRating", df.format(overallRating));
		
		return SUCCESS;
	}
	
	public String getUTCData() throws Exception {		// report functions
	
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<TicketCategoryContext> categories = TicketAPI.getCategories(AccountUtil.getCurrentOrg().getOrgId());
		
		reportData = new JSONArray();
		if(reportSpaceFilterContext != null && reportSpaceFilterContext.getBuildingId() != null) {
			buildingId = reportSpaceFilterContext.getBuildingId();
		}
		
		DecimalFormat df = new DecimalFormat(".##");
		Map<String,Double> daily = new HashMap<>();
		Map<String,Double> forthnightly = new HashMap<>();
		Map<String,Double> monthly = new HashMap<>();
		
		int criticalCount = 0, tatCount =0, pdCount = 0, fasCountdaily = 0, 
			fasCountforthnight = 0,fasCountmonthly = 0;
		
		Map<String,Double> finalRes = new HashMap<>();
		Map<String,Double> finalpercentRes = new HashMap<>();
		for(TicketCategoryContext category:categories) {
			
			if(!(category.getName().equals("Auditing"))) {
				continue;
			}
			List<WorkOrderContext> workorders = WorkOrderAPI.getWorkOrders(category.getId());
			
			if(workorders.isEmpty()) {
				continue;
			}
			for(WorkOrderContext workorder:workorders) {
				
				// LOGGER.log(Level.SEVERE, "buildingId --- "+buildingId);
				if(workorder.getResource() != null) {
					LOGGER.log(Level.SEVERE, "workorder.getResource().getId() --- "+workorder.getResource().getId());
				}
				
				if(buildingId != null && workorder.getResource() != null && workorder.getResource().getId() != buildingId) {
					continue;
				}
				
				// LOGGER.log(Level.SEVERE, "dateFilter --- "+dateFilter);
				// LOGGER.log(Level.SEVERE, "workorder.getResource().getId() --- "+workorder.getCreatedTime());
				
				if(dateFilter != null && !((Long)dateFilter.get(0) < workorder.getCreatedTime() && workorder.getCreatedTime() < (Long)dateFilter.get(1))) {
					continue;
				}
				
				Command chain = FacilioChainFactory.getGetTasksOfTicketCommand();
				FacilioContext context = new FacilioContext();
				
				context.put(FacilioConstants.ContextNames.ID, workorder.getId());
				context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.TASK);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME,"Tasks");
				context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(FacilioConstants.ContextNames.TASK));
				chain.execute(context);
				
				Map<Long, List<TaskContext>> taksSectionMap = (Map<Long, List<TaskContext>>) context.get(FacilioConstants.ContextNames.TASK_MAP);
				
				for(Long sectionid :taksSectionMap.keySet()) {
					
					TaskSectionContext section = TicketAPI.getTaskSection(sectionid);
					
					for(TaskContext task : taksSectionMap.get(sectionid)) {
						
						if(task.getInputValue() != null) {
							
							if(task.getInputValue().equals("Met")) {
								value = 5.0;
							}
							else if(task.getInputValue().equals("Not Met")) {
								value = 0d;
							}
							
							if(workorder.getSubject().contains("Daily")) {
								
								if(section.getName().contains("Critical Service")) {
									
									criticalCount++;
									double value1 = 0;
									if(daily.containsKey("Critical Service")) {
										value1 = daily.get("Critical Service");
									}
									value1 = value1 + value;
									daily.put("Critical Service", value1);
								}
								else if(section.getName().contains("TAT")) {
									
									tatCount++;
									double value1 = 0;
									if(daily.containsKey("TAT")) {
										value1 = daily.get("TAT");
									}
									value1 = value1 + value;
									daily.put("TAT", value1);
								}
								else if(section.getName().contains("PD&PSI")) {
									
									pdCount++;
									double value1 = 0;
									if(daily.containsKey("PD&PSI")) {
										value1 = daily.get("PD&PSI");
									}
									value1 = value1 + value;
									daily.put("PD&PSI", value1);
								}
								else if(section.getName().contains("Financials & Ops")) {
									
									fasCountdaily++;
									double value1 = 0;
									if(daily.containsKey("Financials & Ops")) {
										value1 = daily.get("Financials & Ops");
									}
									value1 = value1 + value;
									daily.put("Financials & Ops", value1);
								}
							}
							else if(workorder.getSubject().contains("Fortnightly")) {
								
								if(section.getName().contains("Financials & Ops")) {
									
									fasCountforthnight++;
									double value1 = 0;
									if(forthnightly.containsKey("Financials & Ops")) {
										value1 = forthnightly.get("Financials & Ops");
									}
									value1 = value1 + value;
									forthnightly.put("Financials & Ops", value1);
								}
							}
							else if(workorder.getSubject().contains("Monthly")) {
								
								if(section.getName().contains("Financials & Ops")) {
									
									fasCountmonthly++;
									double value1 = 0;
									if(monthly.containsKey("Financials & Ops")) {
										value1 = monthly.get("Financials & Ops");
									}
									value1 = value1 + value;
									monthly.put("Financials & Ops", value1);
								}
							}
						}
					}
				}
			}
			
			JSONObject finalresJson = new JSONObject();
			reportData = new JSONArray();
			finalRes.put("Critical Service", 0d);
			finalRes.put("TAT", 0d);
			finalRes.put("PD&PSI", 0d);
			finalRes.put("Financials & Ops", 0d);
			
			if(daily.containsKey("Critical Service")) {
				value = daily.get("Critical Service");
				if(value > 0) {
					value = value / criticalCount;
				}
				finalRes.put("Critical Service", value);
				
				finalresJson.put("Criteria", "Critical Service");
				finalresJson.put("A", "40%");
				finalresJson.put("B", df.format(value));
				
				finalpercentRes.put("Critical Service", value * (40.0d/100.0d));
				finalresJson.put("C", df.format(value * (40.0d/100.0d)));
				
				reportData.add(finalresJson);
				
			}
			
			if(daily.containsKey("TAT")) {
				value = daily.get("TAT");
				if(value > 0) {
					value = value / tatCount;
				}
				finalRes.put("TAT", value);
				
				finalresJson = new JSONObject();
				finalresJson.put("Criteria", "Turn Around Time (TAT)");
				finalresJson.put("A", "25%");
				finalresJson.put("B", df.format(value));
				
				finalpercentRes.put("TAT", value * (25.0d/100.0d));
				
				finalresJson.put("C", df.format(value * (25.0d/100.0d)));
				
				reportData.add(finalresJson);
				
			}
			if(daily.containsKey("PD&PSI")) {
				value = daily.get("PD&PSI");
				if(value > 0) {
					value = value / pdCount;
				}
				finalRes.put("PD&PSI", value);
				
				finalresJson = new JSONObject();
				finalresJson.put("Criteria", "PD&PSI");
				finalresJson.put("A", "10%");
				finalresJson.put("B", df.format(value));
				
				finalpercentRes.put("PD&PSI", value * (10.0d/100.0d));
				
				finalresJson.put("C", df.format(value * (10.0d/100.0d)));
				
				reportData.add(finalresJson);
			}
			
			int financialOpsCount = 0;
			double finValue = 0;
			if(daily.containsKey("Financials & Ops")) {
				value = daily.get("Financials & Ops");
				if(value > 0) {
					value = value / fasCountdaily;
				}
				financialOpsCount++;
				daily.put("Financials & Ops", value);
				finValue = finValue + value;
			}
			
			if(forthnightly.containsKey("Financials & Ops")) {
				value = forthnightly.get("Financials & Ops");
				if(value > 0) {
					value = value / fasCountforthnight;
				}
				financialOpsCount++;
				forthnightly.put("Financials & Ops", value);
				finValue = finValue + value;
			}
			
			if(monthly.containsKey("Financials & Ops")) {
				value = monthly.get("Financials & Ops");
				if(value > 0) {
					value = value / fasCountmonthly;
				}
				financialOpsCount++;
				monthly.put("Financials & Ops", value);
				finValue = finValue + value;
			}
			
			if(financialOpsCount > 0) {
				finalRes.put("Financials & Ops", finValue/financialOpsCount);
				
				finalpercentRes.put("Financials & Ops", (finValue/financialOpsCount) * (25.0d/100.0d));
				
				finalresJson = new JSONObject();
				finalresJson.put("Criteria", "FAS");
				finalresJson.put("A", "25%");
				finalresJson.put("B", df.format(finValue/financialOpsCount));
				finalresJson.put("C", df.format((finValue/financialOpsCount) * (25.0d/100.0d)));
				
				reportData.add(finalresJson);
			}
		}
		double total =  0;
		for(double value :finalpercentRes.values()) {
			total = total + value;
		}
		double achievedPercentage =  (total/5) * 100;
		
		double overallRating = 0;
		
		overallRating = overallRating * 100;
		
		resultJSON = new JSONObject();
		resultJSON.put("tableData", reportData);
		
		resultJSON.put("Maximum Score", 5.0);
		resultJSON.put("Total Weightage Score Achieved", df.format(total));
		resultJSON.put("Performance Score", df.format(achievedPercentage));
		
		return SUCCESS;
	
	}
	public JSONObject resultJSON;
	
	public JSONObject getResultJSON() {
		return resultJSON;
	}
	public void setResultJSON(JSONObject resultJSON) {
		this.resultJSON = resultJSON;
	}
	
	
	private List<String> getDistinctLabel(List<Map<String, Object>> rs) {
		List<String> labels = new ArrayList<>();
		for(Map<String, Object> prop:rs) {
			if(!labels.contains(prop.get("label"))) {
				labels.add((String)prop.get("label"));
			}
		}
		return labels;
	}
	
	private Object getKey(Long modifiedTime,int formatter) {
		
		Object key=null;
		ZonedDateTime zdt= DateTimeUtil.getDateTime(modifiedTime);
		if(formatter==19) {
			
			 key= zdt.getHour();
		}
		else if (formatter==12 || formatter==13){
			
			key=zdt.toLocalDate().toString();
		}
		else if(formatter==15 || formatter==10) {
			key=zdt.getMonth();
		}
		else if (formatter==11) {
			key=zdt.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
		}
		else if(formatter==17) {
			key=zdt.getDayOfWeek().getValue();
			
		}
		return key;
	}
	
	private List<FacilioField> displayFields;
	
	public void setDisplayFields(List<FacilioField> displayFields) {
		this.displayFields = displayFields;
	}
	
	public List<FacilioField> getDisplayFields() {
		return this.displayFields;
	}
	
	public String getUnderlyingData() throws Exception {		// report functions
		
		FacilioModule module = getModule();
		
		List<ModuleBaseWithCustomFields> records = getRawData(null, module);
		
		JSONArray result = new JSONArray();
		if(records != null) {
			for(ModuleBaseWithCustomFields r:records) {
				result.add(r);
			}
		}
		
		setReportData(result);
		// setDisplayFields(getDisplayColumns(module.getName()));

		return SUCCESS;
	}
	
	private List<Map<String, Object>> analyticsDataList;
	public List<Map<String, Object>> getAnalyticsDataList() {
		return analyticsDataList;
	}
	public void setAnalyticsDataList(List<Map<String, Object>> analyticsDataList) {
		this.analyticsDataList = analyticsDataList;
	}
	
	private Map<String, Object> analyticsConfig;
	public Map<String, Object> getAnalyticsConfig() {
		return analyticsConfig;
	}
	public void setAnalyticsConfig(Map<String, Object> analyticsConfig) {
		this.analyticsConfig = analyticsConfig;
	}
	
	private List<ModuleBaseWithCustomFields> getRawData(FacilioContext context, FacilioModule module) throws Exception {	// report functions
		
		if(context == null) {
			context = new FacilioContext();
		}
		
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.REPORT_CONTEXT, reportContext);
		context.put(FacilioConstants.ContextNames.DATE_FILTER, dateFilter);
		
		FacilioChain dataChain = ReportsChainFactory.getReportUnderlyingDataChain();
		dataChain.execute(context);
		
		return (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
	}
	
	private FacilioModule getModule() throws Exception {
		reportContext = DashboardUtil.getReportContext(reportId);
		return ReportsUtil.getReportModule(reportContext);
	}
	
	private Long buildingId;
	
	public void setBuildingId(Long buildingId) {
		this.buildingId = buildingId;
	}
	
	public Long getBuildingId() {
		return this.buildingId;
	}
	
	private Long serviceId;
	
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	
	public Long getServiceId() {
		return this.serviceId;
	}
	
	private Long subMeterId;
	
	public void setSubMeterId(Long subMeterId) {
		this.subMeterId = subMeterId;
	}
	
	public Long getSubMeterId() {
		return this.subMeterId;
	}
	
	List<EnergyMeterContext> energyMeters;
	
	public void setEnergyMeters(List<EnergyMeterContext> energyMeters) {
		this.energyMeters = energyMeters;
	}
	
	public List<EnergyMeterContext> getEnergyMeters() {
		return this.energyMeters;
	}
	
	// get all sub meters of building
	public String getSubMeters() throws Exception {
		
		setEnergyMeters(DeviceAPI.getAllEnergyMeters(buildingId, serviceId));
		
		return SUCCESS;
	}

	JSONObject dashboardMeta;
	
	public void setDashboardMeta(JSONObject dashboardMeta) {
		this.dashboardMeta = dashboardMeta;
	}
	
	public JSONObject getDashboardMeta() {
		return this.dashboardMeta;
	}
	
	public String addWidget() throws Exception {			// check and remove
		
		FacilioContext context = new FacilioContext();
		
		if(widgetChartContext != null) {
			context.put(FacilioConstants.ContextNames.WIDGET, widgetChartContext);
			context.put(FacilioConstants.ContextNames.WIDGET_TYPE, DashboardWidgetContext.WidgetType.CHART);
			context.put(FacilioConstants.ContextNames.DASHBOARD_ID, widgetChartContext.getDashboardId());
			widgetChartContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		}
		else if (widgetListViewContext != null) {
			context.put(FacilioConstants.ContextNames.WIDGET, widgetListViewContext);
			context.put(FacilioConstants.ContextNames.WIDGET_TYPE, DashboardWidgetContext.WidgetType.LIST_VIEW);
			context.put(FacilioConstants.ContextNames.DASHBOARD_ID, widgetListViewContext.getDashboardId());
			widgetListViewContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		}
		else if (widgetStaticContext != null) {
			context.put(FacilioConstants.ContextNames.WIDGET, widgetStaticContext);
			context.put(FacilioConstants.ContextNames.WIDGET_TYPE, DashboardWidgetContext.WidgetType.STATIC);
			context.put(FacilioConstants.ContextNames.DASHBOARD_ID, widgetStaticContext.getDashboardId());
			widgetStaticContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		}
		else if (widgetWebContext != null) {
			context.put(FacilioConstants.ContextNames.WIDGET, widgetWebContext);
			context.put(FacilioConstants.ContextNames.WIDGET_TYPE, DashboardWidgetContext.WidgetType.WEB);
			context.put(FacilioConstants.ContextNames.DASHBOARD_ID, widgetWebContext.getDashboardId());
			widgetWebContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		}
		
		FacilioChain addWidgetChain = null;
		if(dashboardWidget != null && dashboardWidget.getId() != -1) {
//			addWidgetChain = FacilioChainFactory.getAddDashboardVsWidgetChain();
		}
		else {
			addWidgetChain = TransactionChainFactory.getAddWidgetChain();
		}
		addWidgetChain.execute(context);
		
		return SUCCESS;
	}
	
	private List<FacilioModule> modules;
	public List<FacilioModule> getModules() {
		return modules;
	}
	public void setModules(List<FacilioModule> modules) {
		this.modules = modules;
	}
	
	public String getSupportedModules() throws Exception {
		List<FacilioModule> supportedModules = new ArrayList<>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String[] mods = new String[]{"workorder", "alarm", "energydata","asset"};
		
		for (String mod : mods) {
			supportedModules.add(modBean.getModule(mod));
		}
		if(AccountUtil.isFeatureEnabled(FeatureLicense.INVENTORY)) {
			String[] inventorySupportedModules = new String[] {"item", "inventoryrequest"};
			for (String mod : inventorySupportedModules) {
				supportedModules.add(modBean.getModule(mod));
			}
		}
		if(AccountUtil.isFeatureEnabled(FeatureLicense.PURCHASE)) {
			supportedModules.add(modBean.getModule(ContextNames.PURCHASE_ORDER));
			supportedModules.add(modBean.getModule(ContextNames.PURCHASE_REQUEST));
		}
		if(AccountUtil.isFeatureEnabled(FeatureLicense.CONTRACT)) {
			supportedModules.add(modBean.getModule("contracts"));
		}
		setModules(supportedModules);
		return SUCCESS;
	}
	
	public DashboardFolderContext dashboardFolderContext;
	public DashboardFolderContext getDashboardFolderContext() {
		return dashboardFolderContext;
	}
	public void setDashboardFolderContext(DashboardFolderContext dashboardFolderContext) {
		this.dashboardFolderContext = dashboardFolderContext;
	}
	List<DashboardFolderContext> dashboardFolders;
	
	public List<DashboardFolderContext> getDashboardFolders() {
		return dashboardFolders;
	}
	public void setDashboardFolders(List<DashboardFolderContext> dashboardFolders) {
		this.dashboardFolders = dashboardFolders;
	}
	boolean getOnlyMobileDashboard;
	
	boolean optimize;
	public boolean getOptimize() {
		return optimize;
	}
	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}
	public boolean getGetOnlyMobileDashboard() {
		return getOnlyMobileDashboard;
	}
	public void setGetOnlyMobileDashboard(boolean getOnlyMobileDashboard) {
		this.getOnlyMobileDashboard = getOnlyMobileDashboard;
	}
	
	private List<DashboardSharingContext> dashboardSharing;					// remove this
	public List<DashboardSharingContext> getDashboardSharing() {
		return dashboardSharing;
	}
	public void setDashboardSharing(List<DashboardSharingContext> dashboardSharing) {
		this.dashboardSharing = dashboardSharing;
	}
	
	private List<DashboardPublishContext> dashboardPublishing;
	
	public List<DashboardPublishContext> getDashboardPublishing() {
		return dashboardPublishing;
	}
	public void setDashboardPublishing(List<DashboardPublishContext> dashboardPublishing) {
		this.dashboardPublishing = dashboardPublishing;
	}
	private JSONArray dashboardJson;
	
	public void setDashboardJson(JSONArray dashboardJson) {
		this.dashboardJson = dashboardJson;
	}
	public JSONArray getDashboardJson() {
		return dashboardJson;
	}

	private String linkName;

	public String getLinkName() {
		return linkName;
	}
	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}
	
	private int type=1;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	private String fileUrl;
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String url) {
		this.fileUrl = url;
	}
	
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	private int maxCount = -1;
	public int getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
	private EMailTemplate emailTemplate;
	public EMailTemplate getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(EMailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	
	private int rowsUpdated = -1;
	public int getRowsUpdated() {
		return rowsUpdated;
	}
	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
	
	private DerivationContext derivation;
	public DerivationContext getDerivation() {
		return derivation;
	}
	public void setDerivation(DerivationContext derivation) {
		this.derivation = derivation;
	}
	public String addDerivation() throws Exception {
		
		long id = DerivationAPI.addDerivation(derivation);
		derivation.setId(id);
		
		return SUCCESS;
	}
	
	public String updateDerivation() throws Exception {
		derivation.setFormulaId(-1);
		derivation = DerivationAPI.updateDerivation(derivation);
		return SUCCESS;
	}
	
	public String deleteDerivation() throws Exception {
		DerivationAPI.deleteDerivation(id.get(0));
		rowsUpdated = 1;
		return SUCCESS;
	}
	
	private Boolean isRca;
	
	public Boolean getIsRca() {
		if(isRca != null) {
			return isRca.booleanValue();
		}
		return false;
		}
	public void setIsRca(Boolean isRca) {
		this.isRca = isRca;
	}
	private List<DerivationContext> derivations;
	public List<DerivationContext> getDerivations() {
		return derivations;
	}
	public void setDerivations(List<DerivationContext> derivations) {
		this.derivations = derivations;
	}
	public String derivationList() throws Exception {
		
		setDerivations(DerivationAPI.getDerivationList(type));
		
		return SUCCESS;
	}
	
	public String calculateBenchmarkValue() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, benchmarkId);
		context.put(FacilioConstants.ContextNames.SPACE_ID, spaceId);
		context.put(FacilioConstants.ContextNames.BENCHMARK_UNITS, units);
		context.put(FacilioConstants.ContextNames.BENCHMARK_DATE_AGGR, dateAggr);
		if(noOfDaysInReport > 0) {
			context.put(FacilioConstants.ContextNames.BENCHMARK_DATE_VAL, noOfDaysInReport);
		}
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		
		FacilioChain calculateBenchmarkChain = FacilioChainFactory.calculateBenchmarkValueChain();
		calculateBenchmarkChain.execute(context);
		
		LOGGER.log(Level.SEVERE, "benchmarkId -- "+benchmarkId+" spaceId -- "+spaceId+" units -- "+units+" dateAggr -- "+dateAggr+" startTime -- "+startTime);
		if(context.get(FacilioConstants.ContextNames.BENCHMARK_VALUE) != null) {
			value = (double) context.get(FacilioConstants.ContextNames.BENCHMARK_VALUE);
		}
		LOGGER.log(Level.SEVERE, "value -- "+value);
		return SUCCESS;
	}
	List<BenchmarkContext> benchmarks;
	public List<BenchmarkContext> getBenchmarks() {
		return benchmarks;
	}
	public void setBenchmarks(List<BenchmarkContext> benchmarks) {
		this.benchmarks = benchmarks;
	}
	public void addBenchmarks(BenchmarkContext benchmark) {
		if(this.benchmarks == null) {
			this.benchmarks = new ArrayList<>();
		}
		this.benchmarks.add(benchmark);
	}
	private long benchmarkId = -1;
	public long getBenchmarkId() {
		return benchmarkId;
	}
	public void setBenchmarkId(long benchmarkId) {
		this.benchmarkId = benchmarkId;
	}
	
	private double value = -1;
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}

	private long spaceId = -1;
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	
	private DateAggregateOperator dateAggr;
	public int getDateAggr() {
		if (dateAggr != null) {
			return dateAggr.getValue();
		}
		return -1;
	}
	public void setDateAggr(int dateAggr) {
		this.dateAggr = (DateAggregateOperator) AggregateOperator.getAggregateOperator(dateAggr);
	}
	private int noOfDaysInReport;
	
	public int getNoOfDaysInReport() {
		return noOfDaysInReport;
	}
	public void setNoOfDaysInReport(int noOfDaysInReport) {
		this.noOfDaysInReport = noOfDaysInReport;
	}
	private List<BenchmarkUnit> units;
	public List<BenchmarkUnit> getUnits() {
		return units;
	}
	public void setUnits(List<BenchmarkUnit> units) {
		this.units = units;
	}
	
	/*********************** needed methods *********************/
	
	/*********************** v2 *********************/
	public String v2getDashboardListWithFolder() throws Exception {
		getDashboardListWithFolder();
		setResult(FacilioConstants.ContextNames.DASHBOARD_FOLDERS, dashboardFolders);
		return SUCCESS;
	}
	
	public String addDashboardFolder() throws Exception {
		
		if(dashboardFolderContext != null) {
			dashboardFolderContext.setOrgId(AccountUtil.getCurrentOrg().getId());
			if(dashboardFolderContext.getAppId() == -1 ) {
				dashboardFolderContext.setAppId(AccountUtil.getCurrentApp().getId());
			}
			
			if(moduleName != null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(moduleName);
				dashboardFolderContext.setModuleId(module.getModuleId());
			}
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.DASHBOARD_FOLDER, dashboardFolderContext);
			
			FacilioChain chain = TransactionChainFactory.getAddDashboardFolderChain();
			chain.execute(context);
		}
		return SUCCESS;
	}
	
	public String updateDashboardFolder() throws Exception {
		
		FacilioContext context = new FacilioContext();
		if(dashboardFolderContext != null) {
			context.put(FacilioConstants.ContextNames.DASHBOARD_FOLDERS, Collections.singletonList(dashboardFolderContext));
		}
		else if(dashboardFolders != null) {
			context.put(FacilioConstants.ContextNames.DASHBOARD_FOLDERS, dashboardFolders);
		}
		FacilioChain chain = TransactionChainFactory.getUpdateDashboardFolderChain();
		chain.execute(context);
		return SUCCESS;
	}

	public String deleteDashboardFolder() throws Exception {
		
		if(dashboardFolderContext != null) {
			FacilioContext context = new FacilioContext();
			
			context.put(FacilioConstants.ContextNames.DASHBOARD_FOLDER, dashboardFolderContext);
			
			FacilioChain chain = TransactionChainFactory.getDeleteDashboardFolderChain();
			chain.execute(context);
			
			if(context.containsKey(FacilioConstants.ContextNames.DASHBOARD_ERROR_MESSAGE)) {
				errorString = (String) context.get(FacilioConstants.ContextNames.DASHBOARD_ERROR_MESSAGE);
				return ERROR;
			}
		}
		return SUCCESS;
	}
	
	public String addDashboard() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
		FacilioChain addDashboardChain = TransactionChainFactory.getAddDashboardChain();
		addDashboardChain.execute(context);
		return SUCCESS;
	}
	
	public String addDashboardTab() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTabContext);
		FacilioChain addDashboardChain = TransactionChainFactory.getAddDashboardTabChain();
		addDashboardChain.execute(context);
		return SUCCESS;
	}
	
	public String updateDashboardTabsList() throws Exception {
			
			FacilioContext context = new FacilioContext();
			if(dashboardTabContexts != null) {
				context.put(FacilioConstants.ContextNames.DASHBOARD_TABS_LIST, dashboardTabContexts);
			}
			FacilioChain chain = TransactionChainFactory.getUpdateDashboardTabsListChain();
			chain.execute(context);
			return SUCCESS;
		}
	
	public String updateDashboardWithWidgets() throws Exception {
		
		Long dashboardId = (Long) dashboardMeta.get("id");
		this.dashboard = DashboardUtil.getDashboardWithWidgets(dashboardId);
		
		if(dashboardMeta.get("dashboardFolderId") != null) {
			this.dashboard.setDashboardFolderId((Long) dashboardMeta.get("dashboardFolderId")); 
		}
		if(dashboardMeta.get("mobileEnabled") != null) {
			this.dashboard.setMobileEnabled((boolean)dashboardMeta.get("mobileEnabled"));
		}
		if(dashboard.isTabEnabled() && dashboardMeta.get("tabEnabled") != null && !((Boolean)dashboardMeta.get("tabEnabled")))
		{
			dashboard.setSkipDefaultWidgetDeletion(true);
		}
		if (dashboardMeta.get("tabEnabled") != null) {
			this.dashboard.setTabEnabled((boolean) dashboardMeta.get("tabEnabled"));
		}
		if (dashboardMeta.get("dashboardTabPlacement") != null) {
			int dashboardTabPlacement  = ((Long) dashboardMeta.get("dashboardTabPlacement")).intValue();
			this.dashboard.setDashboardTabPlacement(dashboardTabPlacement);
		}
		this.dashboard.setDashboardName((String) dashboardMeta.get("dashboardName"));
		
		if(dashboardMeta.get("clientMetaJsonString") != null) {
			
			this.dashboard.setClientMetaJsonString(dashboardMeta.get("clientMetaJsonString").toString());
		}
		
		List dashboardWidgets = (List) dashboardMeta.get("dashboardWidgets");
		
		List<DashboardWidgetContext> widgets = getDashboardWidgetsFromWidgetMeta(dashboardWidgets);

		this.dashboard.setDashboardWidgets(widgets);
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
		context.put(FacilioConstants.ContextNames.IS_FROM_REPORT, dashboardMeta.containsKey("isFromReport") ? (Boolean) dashboardMeta.get("isFromReport"): Boolean.FALSE);
		context.put(FacilioConstants.ContextNames.BUILDING_ID, buildingId);
		
		
		
		
		FacilioChain updateDashboardChain = TransactionChainFactory.getUpdateDashboardChain();
		updateDashboardChain.execute(context);
		
		
		

		
		return SUCCESS;
	}
	
	public String updateDashboardTabWithWidgets() throws Exception {
		
		Long dashboardTabId = (Long) dashboardMeta.get("tabId");
		dashboardTabContext = DashboardUtil.getDashboardTabWithWidgets(dashboardTabId);
		dashboardTabContext.setName((String)dashboardMeta.get("dashboardTabName"));

		if (dashboardMeta.containsKey("dashboardId")) {
			Long dashboardId = (Long) dashboardMeta.get("dashboardId");
			dashboard = DashboardUtil.getDashboardWithWidgets(dashboardId);
			dashboard.setDashboardName((String) dashboardMeta.get("dashboardName"));
			dashboard.setDashboardFolderId((Long) dashboardMeta.get("dashboardFolderId"));
			if (dashboardMeta.get("dashboardTabPlacement") != null) {
				int dashboardTabPlacement  = ((Long) dashboardMeta.get("dashboardTabPlacement")).intValue();
				dashboard.setDashboardTabPlacement(dashboardTabPlacement);
			}
		}
		List dashboardWidgets = (List) dashboardMeta.get("dashboardWidgets");
		
		List<DashboardWidgetContext> widgets = getDashboardWidgetsFromWidgetMeta(dashboardWidgets);

		dashboardTabContext.setDashboardWidgets(widgets);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTabContext);
		context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
		context.put(FacilioConstants.ContextNames.IS_FROM_REPORT, dashboardMeta.containsKey("isFromReport") ? (Boolean) dashboardMeta.get("isFromReport"): Boolean.FALSE);
		
		FacilioChain updateDashboardChain = TransactionChainFactory.getUpdateDashboardTabChain();
		updateDashboardChain.execute(context);


		return SUCCESS;
	}
	public String enableDashboardTabs()throws Exception
	{
		if(dashboardId != null) {
			List<DashboardTabContext> list = DashboardUtil.getDashboardTabs(dashboardId);
			if(list != null)
			{
				this.dashboard = DashboardUtil.getDashboardWithWidgets(dashboardId);
				this.dashboard.setTabEnabled(true);
				DashboardUtil.updateDashboard(this.dashboard);
			}
		}
		return SUCCESS;
	}
	public String updateDashboardTabsListWithWidgets() throws Exception {
		if (dashboardMeta.containsKey("dashboardId")) {
			Long dashboardId = (Long) dashboardMeta.get("dashboardId");
			dashboard = DashboardUtil.getDashboardWithWidgets(dashboardId);
			dashboard.setDashboardName((String) dashboardMeta.get("dashboardName"));
			dashboard.setDashboardFolderId((Long) dashboardMeta.get("dashboardFolderId"));
			if (dashboardMeta.get("dashboardTabPlacement") != null) {
				int dashboardTabPlacement  = ((Long) dashboardMeta.get("dashboardTabPlacement")).intValue();
				dashboard.setDashboardTabPlacement(dashboardTabPlacement);
			}
		}
		
		List dashboardTabs = (List) dashboardMeta.get("tabs");
		for (int i=0; i < dashboardTabs.size(); i++) {
			Map tab = (Map) dashboardTabs.get(i);
			Long dashboardTabId = (Long) tab.get("tabId");
			dashboardTabContext = DashboardUtil.getDashboardTabWithWidgets(dashboardTabId);
			dashboardTabContext.setName((String)tab.get("dashboardTabName"));
			List dashboardWidgets = (List) tab.get("dashboardWidgets");
			
			List<DashboardWidgetContext> widgets = getDashboardWidgetsFromWidgetMeta(dashboardWidgets);

			dashboardTabContext.setDashboardWidgets(widgets);
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTabContext);
			context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
			context.put(FacilioConstants.ContextNames.IS_FROM_REPORT, dashboardMeta.containsKey("isFromReport") ? (Boolean) dashboardMeta.get("isFromReport"): Boolean.FALSE);
			
			FacilioChain updateDashboardChain = TransactionChainFactory.getUpdateDashboardTabChain();
			updateDashboardChain.execute(context);
		}
		return SUCCESS;
		
	}
	
//	
	private List<DashboardWidgetContext> getDashboardWidgetsFromWidgetMeta(List dashboardWidgets) {
		List<DashboardWidgetContext> widgets = new ArrayList<>();
		if (dashboardWidgets != null) {
			for (int i=0; i < dashboardWidgets.size(); i++) {
				Map widget = (Map) dashboardWidgets.get(i);
				Integer widgetType = DashboardWidgetContext.WidgetType.getWidgetType(widget.get("type").toString()).getValue();
				
				DashboardWidgetContext widgetContext = null;
				if (widgetType == DashboardWidgetContext.WidgetType.CHART.getValue()) {
					widgetContext = new WidgetChartContext();
					widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetChartContext.class);
				}
				else if (widgetType == DashboardWidgetContext.WidgetType.LIST_VIEW.getValue()) {
					widgetContext = new WidgetListViewContext();
					widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetListViewContext.class);
				}
				else if (widgetType == DashboardWidgetContext.WidgetType.STATIC.getValue()) {
					widgetContext = new WidgetStaticContext();
					widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetStaticContext.class);
				}
				else if (widgetType == DashboardWidgetContext.WidgetType.WEB.getValue()) {
					widgetContext = new WidgetWebContext();
					widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetWebContext.class);
				}
				else if (widgetType == DashboardWidgetContext.WidgetType.GRAPHICS.getValue()) {
					widgetContext = new WidgetGraphicsContext();
					widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetGraphicsContext.class);
				}
				else if (widgetType == DashboardWidgetContext.WidgetType.CARD.getValue()) {
					widgetContext = new WidgetCardContext();
					widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetCardContext.class);
				}
				
				widgetContext.setLayoutPosition(Integer.parseInt(widget.get("order").toString()));
				widgetContext.setType(widgetType);
				
				widgets.add(widgetContext);
			}
		}
		return widgets;
	}
	
	public String updateDashboards() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		if(dashboards == null && dashboard != null) {
			dashboards = Collections.singletonList(dashboard);
		}
		context.put(FacilioConstants.ContextNames.DASHBOARDS, dashboards);
		
		FacilioChain updateDashboardChain = TransactionChainFactory.getUpdateDashboardsChain();
		updateDashboardChain.execute(context);
		
		return SUCCESS;
	}
	
	public String deleteDashboard() throws Exception {

		if(dashboardId != null) {

			List<DashboardTabContext>  tabs = DashboardUtil.getDashboardTabs(dashboardId);
			if (!CollectionUtils.isEmpty(tabs)) {
                  for (DashboardTabContext tab : tabs){
					  Long tabId = tab.getId();
					  if(tabId>0) {
						  DashboardUtil.deleteDashboardTab(tabId);
					  }
				  }
			}

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.DASHBOARD_ID, dashboardId);

			FacilioChain deleteDashboardChain = TransactionChainFactory.getDeleteDashboardChain();
			deleteDashboardChain.execute(context);

			if(context.get(FacilioConstants.ContextNames.DASHBOARD_ERROR_MESSAGE) != null) {
				errorString = (String) context.get(FacilioConstants.ContextNames.DASHBOARD_ERROR_MESSAGE);
				return ERROR;
			}
		}
		return SUCCESS;
	}
	
	public String deleteDashboardTab() throws Exception {
		
		if(dashboardTabId > 0 ) {
			DashboardUtil.deleteDashboardTab(dashboardTabId);
		}
		return SUCCESS;
	}
	
	public String viewDashboardSharing() throws Exception {						// Using in client should be removed after removed in client
		setDashboardSharing(DashboardUtil.getDashboardSharing(dashboardId));
		return SUCCESS;
	}
	
	public String viewDashboardPublish() throws Exception {						// Using in client should be removed after removed in client
		setDashboardPublishing(DashboardUtil.getDashboardPublishing(dashboardId));
		return SUCCESS;
	}
	
	public String getDashboardFolder() throws Exception {
		
		dashboardFolders = DashboardUtil.getDashboardFolder(moduleName, appId);
		return SUCCESS;
	}
	public String getPortalDashboardFolder() throws Exception {
		dashboardFolders = DashboardUtil.getDashboardListWithFolder(getOnlyMobileDashboard,true);
		return SUCCESS;
	}
	public String getDashboardList() throws Exception {		// depricated
		if (moduleName != null) {
			dashboards = DashboardUtil.getDashboardList(moduleName);
		}
		return SUCCESS;
	}
	
	private long appId = -1;
	
	public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}
	
	public String getDashboardListWithFolder() throws Exception {
		if(AccountUtil.getCurrentUser().isPortalUser()) {
			getPortalDashboardFolder();
		}
		else {
			dashboardFolders = DashboardUtil.getDashboardList(moduleName, getOnlyMobileDashboard, appId);
		}
		return SUCCESS;
	}
	
	public String getDashboardTree() throws Exception {		// depricated
		if (moduleName != null) {
			dashboardFolders = DashboardUtil.getDashboardTree(moduleName);
		}
		return SUCCESS;
	}
	
	public String viewDashboard() throws Exception {
		if(buildingId != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			dashboard = DashboardUtil.getDashboardForBaseSpace(buildingId, module.getModuleId());
			linkName = (dashboard != null) ? dashboard.getLinkName() : linkName;
			if(dashboard != null) {
				dashboard = DashboardUtil.getDashboardWithWidgets(dashboard.getId());
				setDashboardJson(DashboardUtil.getDashboardResponseJson(dashboard, false));
				return SUCCESS;
			}
		}
		dashboard = DashboardUtil.getDashboardWithWidgets(linkName, moduleName);
		
		FacilioChain getDashboardFilterChain=ReadOnlyChainFactory.getFetchDashboardFilterAndWidgetFilterMappingChain();
		FacilioContext getDashboardFilterContext=getDashboardFilterChain.getContext();
		getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD,getDashboard());
		try {
			getDashboardFilterChain.execute();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error occured  getting filters for dashboard ID="+dashboard.getId()+"setting filters to null" ,e);
			dashboard.setDashboardFilter(null);
		}
		
		
		setDashboardJson(DashboardUtil.getDashboardResponseJson(dashboard, optimize));
		return SUCCESS;
	}
	
	long dashboardTabId;
	
	public long getDashboardTabId() {
		return dashboardTabId;
	}
	public void setDashboardTabId(long dashboardTabId) {
		this.dashboardTabId = dashboardTabId;
	}
	
	DashboardTabContext dashboardTabContext;
	
	public DashboardTabContext getDashboardTabContext() {
		return dashboardTabContext;
	}
	public void setDashboardTabContext(DashboardTabContext dashboardTabContext) {
		this.dashboardTabContext = dashboardTabContext;
	}
	List<DashboardTabContext> dashboardTabContexts;
	public List<DashboardTabContext> getDashboardTabContexts() {
		return dashboardTabContexts;
	}
	public void setDashboardTabContexts(List<DashboardTabContext> dashboardTabContexts) {
		this.dashboardTabContexts = dashboardTabContexts;
	}
	public String viewDashboardTab() throws Exception {

		dashboardTabContext = DashboardUtil.getDashboardTabWithWidgets(dashboardTabId);
		FacilioChain getDashboardFilterChain=ReadOnlyChainFactory.getFetchDashboardFilterAndWidgetFilterMappingChain();
		FacilioContext getDashboardFilterContext=getDashboardFilterChain.getContext();
		getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD_TAB,getDashboardTabContext());
		getDashboardFilterChain.execute();
		
		DashboardUtil.getDashboardTabResponseJson(Collections.singletonList(dashboardTabContext));
		return SUCCESS;
	}
	
	public String updateDashboardPublishStatus() throws Exception {
		dashboard = new DashboardContext();
		dashboard.setPublishStatus(dashboardPublishStatus);
		dashboard.setId(dashboardId);
		DashboardUtil.updateDashboardPublishStatus(dashboard);
		return SUCCESS;
	}
	
	public String updateDashboardPublish() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		if(dashboards == null && dashboard != null) {
			dashboards = Collections.singletonList(dashboard);
		}
		context.put(FacilioConstants.ContextNames.DASHBOARDS, dashboards);
		
		FacilioChain updateDashboardChain = TransactionChainFactory.getUpdateDashboardPublishChain();
		updateDashboardChain.execute(context);
		
		return SUCCESS;
	}
	
	
	public String toggleMobileDashboard() throws Exception {
		if(buildingId != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			dashboard = DashboardUtil.getDashboardForBaseSpace(buildingId, module.getModuleId());
			linkName = (dashboard != null) ? dashboard.getLinkName() : linkName;
		}
		if(dashboard == null) {
			dashboard = DashboardUtil.getDashboardWithWidgets(linkName, moduleName);
		}
		Boolean mobileEnabled = false;
		if(dashboard.getLinkName().equals(DashboardUtil.BUILDING_DASHBOARD_KEY) && (dashboard.getBaseSpaceId() == null || dashboard.getBaseSpaceId() <= 0) && buildingId != null && buildingId > 0 ) {
			
			SpaceFilteredDashboardSettings spaceFilteredDashboardSettings = DashboardUtil.getSpaceFilteredDashboardSettings(dashboard.getId(), buildingId);
			
			if(spaceFilteredDashboardSettings != null) {
				mobileEnabled = !spaceFilteredDashboardSettings.getMobileEnabled();
				spaceFilteredDashboardSettings.setMobileEnabled(mobileEnabled);
				DashboardUtil.updateSpaceFilteredDashboardSettings(spaceFilteredDashboardSettings);
			}
			else {
				spaceFilteredDashboardSettings = new SpaceFilteredDashboardSettings();
				spaceFilteredDashboardSettings.setDashboardId(dashboard.getId());
				spaceFilteredDashboardSettings.setBaseSpaceId(buildingId);
				spaceFilteredDashboardSettings.setMobileEnabled(true);
				
				DashboardUtil.addSpaceFilteredDashboardSettings(spaceFilteredDashboardSettings);
			}
			if(!dashboard.isMobileEnabled()) {
				dashboard.setMobileEnabled(true);
				DashboardUtil.updateDashboardPublishStatus(dashboard);
			}
		}
		else {
			
			mobileEnabled = !dashboard.isMobileEnabled();
			
			dashboard.setMobileEnabled(mobileEnabled);
			dashboard.setId(dashboard.getId());
			DashboardUtil.updateDashboardPublishStatus(dashboard);
		}
		
		if(mobileEnabled) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
			
			FacilioChain enableMobileDashboardChain = FacilioChainFactory.getEnableMobileDashboardChain();
			enableMobileDashboardChain.execute(context);
		}
		
		return SUCCESS;
	}
	
	// data part starts
	public String getCardData() throws Exception {
		
		if (widgetId == null  && staticKey== null && baseSpaceId == null && workflow == null && paramsJson == null && reportSpaceFilterContext == null ) {
	
			throw new IllegalArgumentException(" Send Atleast one of widgetId,staticKey,baseSpaceId,workflow,paramsJson,reportSpaceFilterContext Params Should be specified");
		}
		
		FacilioChain fetchCardData = ReadOnlyChainFactory.fetchCardDataChain();
		FacilioContext context = new FacilioContext();
		
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
	 		JSONObject filterJson = (JSONObject) parser.parse(getFilters());
	 		
	 		if (filterJson.size() > 0) {
	 			Iterator<String> filterIterator = filterJson.keySet().iterator();
				Criteria criteria = new Criteria();
				while(filterIterator.hasNext()) {
					String fieldName = filterIterator.next();
					Object fieldJson = filterJson.get(fieldName);
					List<Condition> conditionList = new ArrayList<>();
					if(fieldJson!=null && fieldJson instanceof JSONArray) {
						JSONArray fieldJsonArr = (JSONArray) fieldJson;
						for(int i=0;i<fieldJsonArr.size();i++) {
							JSONObject fieldJsonObj = (JSONObject) fieldJsonArr.get(i);
							String moduleName = (String) fieldJsonObj.get("module");
							setConditions(moduleName, fieldName, fieldJsonObj, conditionList);
						}
					}
					else if(fieldJson!=null && fieldJson instanceof JSONObject) {
						JSONObject fieldJsonObj = (JSONObject) fieldJson;
						String moduleName = (String) fieldJsonObj.get("module");
						setConditions(moduleName, fieldName, fieldJsonObj, conditionList);
					}
					criteria.groupOrConditions(conditionList);
				}
				context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
	 		}
		}
		
		context.put(FacilioConstants.ContextNames.WIDGET_ID, widgetId);
		context.put(FacilioConstants.ContextNames.WIDGET_STATIC_KEY, staticKey);
		context.put(FacilioConstants.ContextNames.WIDGET_BASESPACE_ID, baseSpaceId);
		context.put(FacilioConstants.ContextNames.WIDGET_WORKFLOW, workflow);
		context.put(FacilioConstants.ContextNames.WIDGET_PARAMJSON, paramsJson);
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.WIDGET_REPORT_SPACE_FILTER_CONTEXT, reportSpaceFilterContext);
		context.put(FacilioConstants.ContextNames.IS_RCA, getIsRca());
		context.put(FacilioConstants.ContextNames.FETCH_ALARM_INFO, isFetchAlarmInfo());
		fetchCardData.execute(context);
		
		setCardResult(context.get(FacilioConstants.ContextNames.RESULT));
		return SUCCESS;
	}
	
	private Boolean fetchAlarmInfo;
	public boolean isFetchAlarmInfo() {
		if(fetchAlarmInfo != null) {
			return fetchAlarmInfo.booleanValue();
		}
		return true;
	}
	
	public void setFetchAlarmInfo(Boolean fetchAlarmInfo) {
		this.fetchAlarmInfo = fetchAlarmInfo;
	}
	public void setFetchAlarmInfo(boolean fetchAlarmInfo) {
		this.fetchAlarmInfo = fetchAlarmInfo;
	}
	
	private JSONArray liveUpdateFields;
	
	public void setLiveUpdateFields(JSONArray liveUpdateFields) {
		this.liveUpdateFields = liveUpdateFields;
	}
	
	public JSONArray getLiveUpdateFields() {
		return this.liveUpdateFields;
	}
	
	public String fetchLiveUpdateFields() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getId());
		
		JSONArray modifiedLiveUpdateFields = new JSONArray();
		
		if (getLiveUpdateFields() != null) {
			for (int i=0; i < getLiveUpdateFields().size(); i++) {
				HashMap jobj = (HashMap) getLiveUpdateFields().get(i);
				
				if (jobj.containsKey("fieldName") && jobj.containsKey("moduleName")) {
					String fieldName = (String) jobj.get("fieldName");
					String moduleName = (String) jobj.get("moduleName");
					Long parentId = Long.parseLong(jobj.get("parentId").toString());
					
					FacilioField field = modBean.getField(fieldName, moduleName);
					if (field != null) {
						JSONObject modifiedField = new JSONObject();
						modifiedField.put("parentId", parentId);
						modifiedField.put("fieldId", field.getId());
						
						modifiedLiveUpdateFields.add(modifiedField);
					}
				}
				if (jobj.containsKey("fieldId")) {
					Long fieldId = Long.parseLong(jobj.get("fieldId").toString());
					Long parentId = Long.parseLong(jobj.get("parentId").toString());
					
					JSONObject modifiedField = new JSONObject();
					modifiedField.put("parentId", parentId);
					modifiedField.put("fieldId", fieldId);
					
					modifiedLiveUpdateFields.add(modifiedField);
				}
			}
		}
		
		setResult("liveUpdateFields", modifiedLiveUpdateFields);
		return SUCCESS;
	}
	// data part ends
}
