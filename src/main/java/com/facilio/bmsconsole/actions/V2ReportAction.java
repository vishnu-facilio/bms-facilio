package com.facilio.bmsconsole.actions;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.AddOrUpdateReportCommand;
import com.facilio.bmsconsole.commands.ConstructReportData;
import com.facilio.bmsconsole.commands.GenerateCriteriaFromFilterCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.SendReadingReportMailCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.MLAlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.context.WidgetStaticContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.AggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ReadingRuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReadingAnalysisContext.ReportFilterMode;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportFactoryFields;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.context.ReportUserFilterContext;
import com.facilio.report.context.ReportYAxisContext;
import com.facilio.report.context.WorkorderAnalysisContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.workflows.util.WorkflowUtil;

public class V2ReportAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(V2ReportAction.class.getName());
	
	private ReportContext reportContext;
	private long folderId=-1;
	
	public ReportContext getReportContext() {
		return reportContext;
	}
	public Long getFolderId() {
		return folderId;
	}
	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}
	public void setReportContext(ReportContext reportContext) {
		this.reportContext = reportContext;
	}
	
	private long reportId = -1;
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	
	String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String fetchReportFolders() throws Exception {
		
		List<ReportFolderContext> reportFolders = ReportUtil.getAllReportFolder(moduleName, getIsWithReport(), null);
		setResult("reportFolders", reportFolders);
		setResult("moduleName", moduleName);
		return SUCCESS;
	}
	
	public String fetchReportsOfFolder() throws Exception {
		
		List<ReportContext> reports = ReportUtil.getReportsFromFolderId(folderId);
		setResult("reports", reports);
		return SUCCESS;
	}
	
	private String filters;
	
	
	public String getFilters() {
		return filters;
	}
	public void setFilters(String filters) {
		this.filters = filters;
	}

	private Boolean isWithReport;
	public Boolean getIsWithReport() {
		if (isWithReport == null) {
			return true;
		}
		return isWithReport;
	}
	public void setIsWithReport(Boolean isWithReport) {
		this.isWithReport = isWithReport;
	}
	
	public String fetchReport() throws Exception {
		reportContext = ReportUtil.getReport(reportId);
		setResult("report", reportContext);
		return SUCCESS;
	}
	
	public String fetchReportWithData() throws Exception {
		
		FacilioContext context = new FacilioContext();
		setReportWithDataContext(context); //This could be moved to a command
		
		Chain fetchReadingDataChain = newFormat ? ReadOnlyChainFactory.newFetchReportDataChain() : ReadOnlyChainFactory.fetchReportDataChain(); 
		fetchReadingDataChain.execute(context);
		
		return setReportResult(context);
	}
	
	long dashboardId;
	
	public long getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(long dashboardId) {
		this.dashboardId = dashboardId;
	}
	private void setReportWithDataContext(FacilioContext context) throws Exception {
		reportContext = ReportUtil.getReport(reportId);
		
		if(startTime > 0 && endTime > 0) {
			reportContext.setDateRange(new DateRange(startTime, endTime));
			reportContext.setDateValue(new DateRange(startTime, endTime).toString());
		}
		if(dashboardId > 0) {
			DateRange range = DashboardUtil.getDateFilterFromDashboard(dashboardId);
			if(range != null) {
				reportContext.setDateRange(range);
				reportContext.setDateValue(range.toString());
			}
		}
		if (showAlarms != null) {
			reportContext.addToReportState(FacilioConstants.ContextNames.REPORT_SHOW_ALARMS, showAlarms);
		}
		if (showSafeLimit != null) {
			reportContext.addToReportState(FacilioConstants.ContextNames.REPORT_SHOW_SAFE_LIMIT, showSafeLimit);
		}

		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		context.put(FacilioConstants.ContextNames.REPORT_HANDLE_BOOLEAN, newFormat);
	}
	
	private ReportFolderContext reportFolder;
	public ReportFolderContext getReportFolder() {
		return reportFolder;
	}
	public void setReportFolder(ReportFolderContext reportFolder) {
		this.reportFolder = reportFolder;
	}
	
	public String addReportFolder() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		reportFolder.setOrgId(AccountUtil.getCurrentOrg().getId());
		reportFolder.setModuleId(module.getModuleId());
		
		reportFolder = ReportUtil.addReportFolder(reportFolder);
		setResult("reportFolder", reportFolder);
		return SUCCESS;
	}
	
	public String updateReportFolder() throws Exception {
		
		if(reportFolder != null) {
			ReportUtil.updateReportFolder(reportFolder);
			setResult("reportFolder", reportFolder);
		}
		
		return SUCCESS;
	}
	
	public String deleteReportFolder() throws Exception {
		
		if(reportFolder != null) {
			
			List<Map<String, Object>> reports = ReportUtil.getReportFromFolderId(reportFolder.getId());
			if(reports == null || reports.isEmpty()) {
				ReportUtil.deleteReportFolder(reportFolder);
			}
			else {
				setResult("errorString", "Report present in Folder");
			}
		}
		
		return SUCCESS;
	}
	
	private void setReadingsDataContext(FacilioContext context) throws Exception {
		JSONParser parser = new JSONParser();
		JSONArray fieldArray = (JSONArray) parser.parse(fields);
		JSONArray baseLineList = null;
		if (baseLines != null && !baseLines.isEmpty()) {
			baseLineList = (JSONArray) parser.parse(baseLines);
		}
		if(mode.equals(ReportMode.SERIES)) {
			setxAggr(0);
		}
		
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
		context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, FieldUtil.getAsBeanListFromJsonArray(fieldArray, ReadingAnalysisContext.class));
		context.put(FacilioConstants.ContextNames.REPORT_MODE, mode);
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));
		context.put(FacilioConstants.ContextNames.REPORT_FILTER_MODE, filterMode);
		context.put(FacilioConstants.ContextNames.ASSET_CATEGORY, assetCategory);
		context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, spaceId);
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentId);
		context.put(FacilioConstants.ContextNames.REPORT_SHOW_ALARMS, showAlarms);
		context.put(FacilioConstants.ContextNames.REPORT_SHOW_SAFE_LIMIT, showSafeLimit);
		context.put(FacilioConstants.Workflow.WORKFLOW, transformWorkflow);
		context.put(FacilioConstants.ContextNames.REPORT_HANDLE_BOOLEAN, newFormat);
		
		context.put(FacilioConstants.ContextNames.ALARM_ID, alarmId);
	}
	
	public String addOrUpdateReadingReport() throws Exception {
		FacilioContext context = new FacilioContext();
		setReadingsDataContext(context);
		
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperatorValue);
		context.put(FacilioConstants.ContextNames.CHART_STATE, chartState);
		context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
		Chain addReadingReport = TransactionChainFactory.addOrUpdateReadingReportChain();
		addReadingReport.execute(context);
		
		return setReportResult(context);
	}
	
	public String moveReport() throws Exception {
		if(reportId>0) {
			reportContext = ReportUtil.getReport(reportId);
			reportContext.setReportFolderId(folderId);
			ReportUtil.moveReport(reportContext);
			return SUCCESS;
		}
		return ERROR;
	}
	public String duplicateReport() throws Exception {
		
		if(reportId > 0) {
			reportContext = ReportUtil.getReport(reportId);
			reportContext.setId(-1);
			
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.REPORT, reportContext);
			Chain addReport = TransactionChainFactory.addOrUpdateReportChain();
			addReport.execute(context);
			return setReportResult(context);
		}
		return ERROR;
	}
	
	long alarmId = -1;
	
	public long getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}
	long readingRuleId = -1;
	public long getReadingRuleId() {
		return readingRuleId;
	}
	public void setReadingRuleId(long readingRuleId) {
		this.readingRuleId = readingRuleId;
	}

	boolean isWithPrerequsite;


	public boolean isWithPrerequsite() {
		return isWithPrerequsite;
	}
	public void setWithPrerequsite(boolean isWithPrerequsite) {
		this.isWithPrerequsite = isWithPrerequsite;
	}
	public void setIsWithPrerequsite(boolean isWithPrerequsite) {
		this.isWithPrerequsite = isWithPrerequsite;
	}

	long cardWidgetId;
	
	public long getCardWidgetId() {
		return cardWidgetId;
	}
	JSONObject cardParamJson; 
	public JSONObject getCardParamJson() {
		return cardParamJson;
	}
	public void setCardParamJson(JSONObject cardParamJson) {
		this.cardParamJson = cardParamJson;
	}
	public void setCardWidgetId(long cardWidgetId) {
		this.cardWidgetId = cardWidgetId;
	}
	
	FacilioContext resultContext;
	
	public FacilioContext getResultContext() {
		return resultContext;
	}
	public void setResultContext(FacilioContext resultContext) {
		this.resultContext = resultContext;
	}
	
	public String fetchReadingsFromCard() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		JSONObject params = null;
		
		if(cardParamJson != null) {
			params = cardParamJson;
		}
		else if(cardWidgetId > 0) {
			DashboardWidgetContext dashboardWidgetContext =  DashboardUtil.getWidget(cardWidgetId);
			
			WidgetStaticContext widgetStaticContext = (WidgetStaticContext) dashboardWidgetContext;
			params = widgetStaticContext.getParamsJson();
		}
		params = DashboardUtil.getCardParams(params);
		
		List<ReadingAnalysisContext> metrics = new ArrayList<>();
		if(params != null) {
			
			int xAggrInt = params.get("xAggr") != null ? Integer.parseInt(params.get("xAggr").toString()) : 0;
			setxAggr(xAggrInt);
			
			DateOperators dateOperator = (DateOperators) Operator.OPERATOR_MAP.get(Integer.parseInt(params.get("dateOperator").toString()));
			
			ReportYAxisContext reportaxisContext = new ReportYAxisContext();
			reportaxisContext.setFieldId((Long)params.get("fieldId"));
			reportaxisContext.setAggr(Integer.parseInt(params.get("aggregateFunc").toString()));
			
			ReadingAnalysisContext readingAnalysisContext = new ReadingAnalysisContext();
			readingAnalysisContext.setParentId(Collections.singletonList((Long)params.get("parentId")));
			readingAnalysisContext.setType(1);
			readingAnalysisContext.setyAxis(reportaxisContext);
			
			metrics.add(readingAnalysisContext);
			
			context.put(FacilioConstants.ContextNames.START_TIME, dateOperator.getRange(null).getStartTime());
			context.put(FacilioConstants.ContextNames.END_TIME, dateOperator.getRange(null).getEndTime());
			context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
			context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, metrics);
			context.put(FacilioConstants.ContextNames.REPORT_MODE, mode);
			context.put(FacilioConstants.ContextNames.REPORT_CALLING_FROM, "card");
			
			Chain fetchReadingDataChain = newFormat ? ReadOnlyChainFactory.newFetchReadingReportChain() : ReadOnlyChainFactory.fetchReadingReportChain();
			fetchReadingDataChain.execute(context);
			
			return setReportResult(context);
		}
		
		return SUCCESS;
	}
	
	public String addWorkOrderReport() throws Exception {
		JSONParser parser = new JSONParser();
		JSONArray fieldArray = (JSONArray) parser.parse(fields);
		JSONArray baseLineList = null;
		if (baseLines != null && !baseLines.isEmpty()) {
			baseLineList = (JSONArray) parser.parse(baseLines);
		}
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
		context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperator);
		context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
		context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, FieldUtil.getAsBeanListFromJsonArray(fieldArray, ReadingAnalysisContext.class));
		context.put(FacilioConstants.ContextNames.REPORT_MODE, mode);
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));
		context.put(FacilioConstants.ContextNames.CHART_STATE, chartState);
		context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		
		Chain addWorkOrderChain = TransactionChainFactory.addWorkOrderReportChain();
		addWorkOrderChain.execute(context);
		
		return setReportResult(context);
	}
	
	private boolean newFormat = false;
	public boolean isNewFormat() {
		return newFormat;
	}
	public void setNewFormat(boolean newFormat) {
		this.newFormat = newFormat;
	}
	public String fetchReadingsFromAlarm() throws Exception {
		
		getDataPointFromAlarm();
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.REPORT_FROM_ALARM, true);
		context.put(FacilioConstants.ContextNames.ALARM_RESOURCE, alarmResource);
		if(readingRuleId > 0) {
			context.put(FacilioConstants.ContextNames.FETCH_EVENT_BAR, true);
			context.put(FacilioConstants.ContextNames.READING_RULE_ID, readingRuleId);
		}
		setReadingsDataContext(context);
		
		Chain fetchReadingDataChain = newFormat ? ReadOnlyChainFactory.newFetchReadingReportChain() : ReadOnlyChainFactory.fetchReadingReportChain();
		fetchReadingDataChain.execute(context);
		
		return setReportResult(context);
	}
	public String fetchReadingsData() throws Exception {
		if(alarmId > 0 && fields == null) {
			getDataPointFromAlarm();
		}
		FacilioContext context = new FacilioContext();
		setReadingsDataContext(context);
		
		Chain fetchReadingDataChain = newFormat ? ReadOnlyChainFactory.newFetchReadingReportChain() : ReadOnlyChainFactory.fetchReadingReportChain();
		fetchReadingDataChain.execute(context);
		
		return setReportResult(context);
	}
	
	private JSONObject xField;
	public JSONObject getxField() {
		return xField;
	}
	public void setxField(JSONObject xField) {
		this.xField = xField;
	}
	private JSONObject dateField;
	public JSONObject getDateField() {
		return dateField;
	}
	public void setDateField(JSONObject dateField) {
		this.dateField = dateField;
	}

	private JSONArray yField;
	public JSONArray getyField() {
		return yField;
	}
	public void setyField(JSONArray yField) {
		this.yField = yField;
	}
	private JSONArray groupBy;
	public JSONArray getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(JSONArray groupBy) {
		this.groupBy = groupBy;
	}
	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	private JSONArray sortFields;
	public JSONArray getSortFields() {
		return sortFields;
	}
	public void setSortFields(JSONArray sortFields) {
		this.sortFields = sortFields;
	}
	private int sortOrder;
	public int getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	private int limit = -1;
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	private int moduleType = -1;
	public int getModuleType() {
		return moduleType;
	}
	public void setModuleType(int moduleType) {
		this.moduleType = moduleType;
	}
	private List<ReportUserFilterContext> userFilters;
	public List<ReportUserFilterContext> getUserFilters() {
		return userFilters;
	}
	public void setUserFilters(List<ReportUserFilterContext> userFilters) {
		this.userFilters = userFilters;
	}
	private long pmId = -1;
	
	public long getPmId() {
		return pmId;
	}
	public void setPmId(long pmId) {
		this.pmId = pmId;
	}
	
	private long resourceId = -1;
	
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
//	public String fetchReportDataFromPm() throws Exception{
//		FacilioContext context = new FacilioContext();
//		Chain c = FacilioChain.getNonTransactionChain();
//		if(pmId != -1) {
//			context.put("pmId", pmId);
//			context.put("resourceId", resourceId);
//			c.addCommand(new ConstructReportDataForPM());
//			c.addCommand(ReadOnlyChainFactory.constructAndFetchReportDataChain());
//			c.execute(context);
//		}
//		return setReportResult(context);
//		
//	}
	
	public String fetchReportData() throws Exception {
		FacilioContext context = new FacilioContext();
		Chain c = FacilioChain.getNonTransactionChain();
		updateContext(context);
		c.addCommand(new ConstructReportData());
		c.addCommand(ReadOnlyChainFactory.constructAndFetchReportDataChain());
		c.execute(context);

		return setReportResult(context);
	}
	
	
	private void getReport(FacilioContext context) throws Exception {
		ReportContext reportContext = ReportUtil.getReport(reportId);
		if (reportContext == null) {
			throw new Exception("Report not found");
		}
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, reportContext.getModule().getName());
		if (startTime != -1 && endTime != -1) {
			reportContext.setDateRange(new DateRange(startTime, endTime));
		}
		reportContext.setUserFilters(userFilters, true);
	}
	
	private void updateContext(FacilioContext context) {
		context.put("x-axis", xField);
		context.put("date-field", dateField);
		context.put("y-axis", yField);
		context.put("group-by", groupBy);
		context.put("criteria", criteria);
		context.put("sort_fields", sortFields);
		context.put("sort_order", sortOrder);
		context.put("limit", limit);
		context.put("user-filters", userFilters);
		
		context.put(FacilioConstants.Reports.MODULE_TYPE, moduleType);
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
	}

	public String saveReport() throws Exception {
		Chain chain = FacilioChain.getTransactionChain();
		FacilioContext context = new FacilioContext();
		
		if (reportContext == null) {
			throw new Exception("Report context cannot be empty");
		}
		reportContext.setChartState(chartState);
		reportContext.setTabularState(tabularState);
		reportContext.setType(ReportType.WORKORDER_REPORT);
		reportContext.setModuleType(moduleType);
		
		if (StringUtils.isEmpty(moduleName)) {
			throw new Exception("Module name is mandatory");
		}
		
		if (reportId > 0) {
			ReportContext report = ReportUtil.getReport(reportId);
			reportContext.setId(report.getId());
		}
		
		context.put(FacilioConstants.ContextNames.REPORT, reportContext);
		updateContext(context);
		
		chain.addCommand(new ConstructReportData());
		chain.addCommand(new AddOrUpdateReportCommand());
		chain.execute(context);

		setResult("message", "Report saved");
		setResult("report", reportContext);
		return SUCCESS;
	}
	
	public String executeReport() throws Exception {
		Chain chain = FacilioChain.getNonTransactionChain();
		FacilioContext context = new FacilioContext();

		if(getFilters() != null) {
			chain.addCommand(new GenerateCriteriaFromFilterCommand());
			JSONParser parser = new JSONParser();
			JSONObject filter = (JSONObject)parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, filter);
		}
		getReport(context);
		
		chain.addCommand(ReadOnlyChainFactory.constructAndFetchReportDataChain());
		chain.addCommand(new GetModuleFromReportContextCommand());
		chain.execute(context);

		return setReportResult(context);
	}
	
	public String getReportFields() throws Exception {
		JSONObject reportFields = ReportFactoryFields.getReportFields(moduleName);
		// JSONObject reportFields = ReportUtil.getReportFields(moduleName);
		
		setResult("meta", reportFields);
		return SUCCESS;
	}
	
	private ReportFilterMode xCriteriaMode;
	public int getxCriteriaMode() {
		if (xCriteriaMode != null) {
			return xCriteriaMode.getValue();
		}
		return -1;
	}
	public void setxCriteriaMode(int xCriteriaMode) {
		this.xCriteriaMode = ReportFilterMode.valueOf(xCriteriaMode);
		this.filterMode = this.xCriteriaMode;
	}
	
	private ReportFilterMode filterMode;
	public int getFilterMode() {
		if (filterMode != null) {
			return filterMode.getValue();
		}
		return -1;
	}
	public void setFilterMode(int filterMode) {
		this.filterMode = ReportFilterMode.valueOf(filterMode);
		this.xCriteriaMode = this.filterMode;
	}
	
	private Boolean showSafeLimit;
	public Boolean isShowSafeLimit() {
		return showSafeLimit;
	}
	public void setShowSafeLimit(boolean showSafeLimit) {
		this.showSafeLimit = showSafeLimit;
	}

	private Boolean showAlarms;
	public Boolean isShowAlarms() {
		return showAlarms;
	}
	public void setShowAlarms(boolean showAlarms) {
		this.showAlarms = showAlarms;
	}
	
	private WorkflowContext transformWorkflow;
	public WorkflowContext getTransformWorkflow() {
		return transformWorkflow;
	}
	public void setTransformWorkflow(WorkflowContext transformWorkflow) {
		this.transformWorkflow = transformWorkflow;
	}

	private List<Long> assetCategory;
	public List<Long> getAssetCategory() {
		return assetCategory;
	}
	public void setAssetCategory(List<Long> assetCategory) {
		this.assetCategory = assetCategory;
	}
	
	private List<Long> spaceId;
	public List<Long> getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(List<Long> spaceId) {
		this.spaceId = spaceId;
	}

	private List<Long> parentId;
	public List<Long> getParentId() {
		return parentId;
	}
	public void setParentId(List<Long> parentId) {
		this.parentId = parentId;
	}
	private ResourceContext alarmResource;
	public ResourceContext getAlarmResource() {
		return alarmResource;
	}
	public void setAlarmResource(ResourceContext alarmResource) {
		this.alarmResource = alarmResource;
	}

	public boolean deleteWithWidget;
	
	public boolean isDeleteWithWidget() {
		return deleteWithWidget;
	}
	public void setDeleteWithWidget(boolean deleteWithWidget) {
		this.deleteWithWidget = deleteWithWidget;
	}
	public String deleteReport() throws Exception {
		
		List<WidgetChartContext> widgetCharts = null;
		if(!deleteWithWidget) {
			widgetCharts = DashboardUtil.getWidgetFromDashboard(reportId,true);
		}
		if(widgetCharts == null || widgetCharts.isEmpty()) {
			ReportUtil.deleteReport(reportId);
			return SUCCESS;
		}
		else {
			setResult("errorString", "Report Used In Dashboard");
		}
		return SUCCESS;
	}
	
	private void getDataPointFromAlarm() throws Exception {

		AlarmContext alarmContext = AlarmAPI.getReadingAlarmContext(alarmId);
		
		if(alarmContext == null) {
			alarmContext = AlarmAPI.getMLAlarmContext(alarmId);
		}
		
		List<ReadingRuleContext> readingRules = new ArrayList<>();
		boolean isAnomalyAlarm = false;
		if(isWithPrerequsite) {							// new 1st
			
			ReadingAlarmContext readingAlarmContext = (ReadingAlarmContext)alarmContext;
			AlarmRuleContext alarmRuleContext = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(readingAlarmContext.getRuleId()));
			readingRules.add(alarmRuleContext.getAlarmTriggerRule());
			readingRules.add(alarmRuleContext.getPreRequsite());
			
		}
		else if(readingRuleId > 0) {					// new 2nd
			
			ReadingRuleContext readingruleContext = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(readingRuleId);
			readingRules.add(readingruleContext);
		}
		else {											// old
			long ruleId = -1;
			
			if(alarmContext instanceof ReadingAlarmContext) {
				ruleId = ((ReadingAlarmContext)alarmContext).getRuleId();
			}
			else if (alarmContext instanceof MLAlarmContext) {
				ruleId = ((MLAlarmContext)alarmContext).getRuleId();
			}
			if(ruleId > 0) {
				ReadingRuleContext readingruleContext = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(ruleId);
				readingRules.add(readingruleContext);
			}
			else {
				isAnomalyAlarm = true;
			}
		}
		
		ResourceContext resource = ResourceAPI.getResource(alarmContext.getResource().getId());
		this.alarmResource = resource;
		
		JSONArray dataPoints = new JSONArray();
		
		if(readingRules != null && !readingRules.isEmpty() && readingRules.get(0) != null) {
			
			for(ReadingRuleContext readingRule :readingRules) {
				if(readingRule != null) {
					dataPoints.addAll(getDataPointsJSONFromRule(readingRule,resource,alarmContext));
				}
			}
			
			if(readingRules.get(0).getBaselineId() != -1) {
				JSONArray baselineArray = new JSONArray();
				JSONObject baselineJson = new JSONObject();
				baselineJson.put("baseLineId", readingRules.get(0).getBaselineId());
				baselineArray.add(baselineJson);
				baseLines = baselineArray.toJSONString();
			}
			
		}
		else if(isAnomalyAlarm){
			
			JSONObject dataPoint = new JSONObject();
			
			dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));
			
			JSONObject yAxisJson = new JSONObject();
			ReadingAlarmContext readingAlarmContext = (ReadingAlarmContext) alarmContext;
			yAxisJson.put("fieldId", readingAlarmContext.getReadingFieldId());
			updateTimeRangeAsPerFieldType(readingAlarmContext.getReadingFieldId());
			yAxisJson.put("aggr", 0);
			
			dataPoint.put("yAxis", yAxisJson);
			
			dataPoint.put("type", 1);
			
			dataPoints.add(dataPoint);
			
		}
		String additionalDataPointString = "anomalyreadings";
		if(alarmContext != null && alarmContext.getAdditionInfo() != null && alarmContext.getAdditionInfo().containsKey(additionalDataPointString)) {
			
			this.startTime = DateTimeUtil.getDayStartTimeOf(alarmContext.getCreatedTime());		// specific handling for
			dataPoints = new JSONArray();														// anomaly alarms
			
			JSONArray points = FacilioUtil.parseJsonArray(alarmContext.getAdditionInfo().get(additionalDataPointString).toString());
			
			for(int i=0;i<points.size();i++) {
				long fieldId = Long.parseLong(points.get(i).toString());
				
				JSONObject dataPoint = new JSONObject();
				
				dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));
				
				JSONObject yAxisJson = new JSONObject();
				yAxisJson.put("fieldId", fieldId);
				yAxisJson.put("aggr", 0);
				
				dataPoint.put("yAxis", yAxisJson);
				
				dataPoint.put("type", 1);
				
				dataPoints.add(dataPoint);
			}
			
		}
		if(alarmId == 890083l) {
			LOGGER.error("new data point json -- "+dataPoints);
		}
		if (newFormat) {
			long baselineId = -1l;
			if(readingRules != null && !readingRules.isEmpty() && readingRules.get(0) != null) {
				baselineId = readingRules.get(0).getBaselineId();
			}
			ReportUtil.setAliasForDataPoints(dataPoints, baselineId);
		}
		
		
		if(this.startTime <= 0 && this.endTime <= 0) {
			long modifiedTime = alarmContext.getCreatedTime();
			if(alarmContext.getModifiedTime() > 0) {
				modifiedTime = alarmContext.getModifiedTime();
			}
			
			DateRange range = DateOperators.CURRENT_N_DAY.getRange(""+modifiedTime);
			
			this.startTime = range.getStartTime();
			this.endTime = range.getEndTime();
		}
		
		setxAggr(0);
		fields =  dataPoints.toJSONString();
	}
	
	private JSONArray getDataPointsJSONFromRule(ReadingRuleContext readingruleContext,ResourceContext resource,AlarmContext alarmContext) throws Exception {
		
		JSONArray dataPoints = new JSONArray();
		if(readingruleContext.getReadingRuleTypeEnum() == ReadingRuleType.ML_RULE) {
			
			JSONObject dataPoint = new JSONObject();
			
			dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));
			
			JSONObject yAxisJson = new JSONObject();
			yAxisJson.put("fieldId", readingruleContext.getReadingFieldId());
			yAxisJson.put("aggr", 0);
			
			dataPoint.put("yAxis", yAxisJson);
			
			dataPoint.put("type", 1);
			
			ZonedDateTime zdt = DateTimeUtil.getDateTime(alarmContext.getCreatedTime());
			zdt = zdt.truncatedTo(new SecondsChronoUnit(60 * 60));
			DateTimeUtil.getMillis(zdt, true);
			
			dataPoint.put("predictedTime", DateTimeUtil.getMillis(zdt, true));
			
			dataPoints.add(dataPoint);
			
			dataPoint = new JSONObject();
			
			dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));
			
			yAxisJson = new JSONObject();
			yAxisJson.put("fieldId", readingruleContext.getReadingFieldId());
			yAxisJson.put("aggr", 0);
			
			dataPoint.put("yAxis", yAxisJson);
			
			dataPoint.put("type", 1);
			
			zdt = DateTimeUtil.getDateTime(alarmContext.getModifiedTime());
			zdt = zdt.truncatedTo(new SecondsChronoUnit(1 * 60 * 60));
			if(alarmContext.getClearedTime() > 0) {
				dataPoint.put("predictedTime", DateTimeUtil.getMillis(zdt, true)-(6 * 3600000));
			}
			else {
				dataPoint.put("predictedTime", DateTimeUtil.getMillis(zdt, true));
			}
			dataPoints.add(dataPoint);
			
			if(readingruleContext.getId() == 5085l) {
				
				dataPoint = new JSONObject();
				
				dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));
				
				yAxisJson = new JSONObject();
				yAxisJson.put("fieldId", 253613l);
				yAxisJson.put("aggr", 0);
				
				dataPoint.put("yAxis", yAxisJson);
				
				dataPoint.put("type", 1);
				
				dataPoints.add(dataPoint);
				
			}
			
			if(readingruleContext.getId() == 7504l) {
				
				dataPoint = new JSONObject();
				
				dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));
				
				yAxisJson = new JSONObject();
				yAxisJson.put("fieldId", 517774l);
				yAxisJson.put("aggr", 0);
				
				dataPoint.put("yAxis", yAxisJson);
				
				dataPoint.put("type", 1);
				
				dataPoints.add(dataPoint);
				
			}
			
			zdt = DateTimeUtil.getDateTime(alarmContext.getCreatedTime());
			zdt = zdt.truncatedTo(new SecondsChronoUnit(24 * 60 * 60));
			this.startTime = DateTimeUtil.getMillis(zdt, true);
			
			zdt = DateTimeUtil.getDateTime(alarmContext.getModifiedTime() +432000000);
			zdt = zdt.truncatedTo(new SecondsChronoUnit(24 * 60 * 60));
			this.endTime = DateTimeUtil.getMillis(zdt, true);
			
			
			LOGGER.error("dataPoints -- "+dataPoints);
			LOGGER.error("startTime -- "+startTime);
			LOGGER.error("startTime -- "+this.endTime);
			return dataPoints;
		}
		
		
		ResourceContext currentResource = resource;
		if(readingruleContext.getThresholdType() == ReadingRuleContext.ThresholdType.ADVANCED.getValue()) {
			
			Set readingMap = new HashSet();
			if(readingruleContext.getWorkflowId() > 0) {
				WorkflowContext workflow = WorkflowUtil.getWorkflowContext(readingruleContext.getWorkflowId(), true);
				
				for(WorkflowExpression workflowExp:workflow.getExpressions()) {
					
					ExpressionContext exp = (ExpressionContext) workflowExp;
					if(exp.getModuleName() != null) {
						
						JSONObject dataPoint = new JSONObject();
						
						FacilioField readingField = null;
						if(exp.getFieldName() != null ) {
							readingField = DashboardUtil.getField(exp.getModuleName(), exp.getFieldName());
							
							updateTimeRangeAsPerFieldType(readingField.getFieldId());
							
							JSONObject yAxisJson = new JSONObject();
							yAxisJson.put("fieldId", readingField.getFieldId());
							yAxisJson.put("aggr", 0);
							
							dataPoint.put("yAxis", yAxisJson);
							
						}
						if(exp.getCriteria() != null) {
							Map<String, Condition> conditions = exp.getCriteria().getConditions();
							
							for(String key : conditions.keySet()) {
								
								Condition condition = conditions.get(key);
								
								if(condition.getFieldName().equals("parentId")) {
									resource = condition.getValue().equals("${resourceId}") ? currentResource : ResourceAPI.getResource(Long.parseLong(condition.getValue()));
									
									dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));
									
									break;
								}
							}
						}
						dataPoint.put("type", 1);
						if(!readingMap.contains(resource.getId() + "_" + readingField.getFieldId())) {
							readingMap.add(resource.getId() + "_" + readingField.getFieldId());
							dataPoints.add(dataPoint);								
						}
					}
				}
			}
		}
		else if(readingruleContext.getReadingFieldId() > 0) {
			JSONObject dataPoint = new JSONObject();
			
			dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));
			
			JSONObject yAxisJson = new JSONObject();
			yAxisJson.put("fieldId", readingruleContext.getReadingFieldId());
			updateTimeRangeAsPerFieldType(readingruleContext.getReadingFieldId());
			yAxisJson.put("aggr", 0);
			
			dataPoint.put("yAxis", yAxisJson);
			
			dataPoint.put("type", 1);
			dataPoints.add(dataPoint);
		}
		return dataPoints;
	}
	
	private void updateTimeRangeAsPerFieldType(long fieldId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField readingField = modBean.getField(fieldId);
		if(readingField != null) {
			FormulaFieldContext formulaField = FormulaFieldAPI.getFormulaField(readingField);
			
			if(formulaField != null && formulaField.getFrequencyEnum() == FacilioFrequency.DAILY) {
				this.startTime = DateTimeUtil.addDays(this.endTime, -10);
			}
			else if(formulaField != null && formulaField.getFrequencyEnum() == FacilioFrequency.MONTHLY) {
				this.startTime = DateTimeUtil.addMonths(this.endTime, -3);
			}
			else if(formulaField != null && formulaField.getFrequencyEnum() == FacilioFrequency.WEEKLY) {
				this.startTime = DateTimeUtil.addWeeks(this.endTime, -3);
			}
		}
		LOGGER.error("1.this.startTime --- "+this.startTime);
		LOGGER.error("2.this.endTime --- "+this.endTime);
	}

	private ReadingAnalysisContext.ReportMode mode = ReportMode.TIMESERIES;
	public int getMode() {
		if (mode != null) {
			return mode.getValue();
		}
		return -1;
	}
	public void setMode(int mode) {
		this.mode = ReportMode.valueOf(mode);
	}
	
	public String fetchWorkorderData() throws Exception {
		
		JSONParser parser = new JSONParser();
		JSONArray fieldArray = (JSONArray) parser.parse(fields);
		JSONArray baseLineList = null;
		if (baseLines != null && !baseLines.isEmpty()) {
			baseLineList = (JSONArray) parser.parse(baseLines);
		}
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
		context.put(FacilioConstants.ContextNames.REPORT_FIELDS, FieldUtil.getAsBeanListFromJsonArray(fieldArray, WorkorderAnalysisContext.class));
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));
		
		Chain fetchReadingDataChain = ReadOnlyChainFactory.fetchWorkorderReportChain();
		fetchReadingDataChain.execute(context);
		
		return setReportResult(context);
		
	}
	
	private String setReportResult(FacilioContext context) {
		if(context.get(FacilioConstants.ContextNames.REPORT) != null) {
			
			reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);			
			if(alarmId  > 0) {
				reportContext.setDateOperator(DateOperators.CURRENT_N_DAY.getOperatorId());
			}
			setResult("report", reportContext);
		}
		setResult("reportXValues", context.get(FacilioConstants.ContextNames.REPORT_X_VALUES)); //This can be removed from new format
		setResult("reportData", context.get(FacilioConstants.ContextNames.REPORT_DATA));
		setResult("reportVarianceData", context.get(FacilioConstants.ContextNames.REPORT_VARIANCE_DATA));
//		setResult("reportAlarms", context.get(FacilioConstants.ContextNames.REPORT_ALARMS));
		setResult("safeLimits", context.get(FacilioConstants.ContextNames.REPORT_SAFE_LIMIT));
		setResult(FacilioConstants.ContextNames.REPORT_ALARM_CONTEXT, context.get(FacilioConstants.ContextNames.REPORT_ALARM_CONTEXT));
		
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		if (module != null) {
			setResult("module", module);
		}
		if(moduleType != -1 || reportContext.getModuleType() != -1) {
			setResult("moduleTypes", ReportFactoryFields.addModuleTypes(module.getName()));
		}
		
		resultContext = context;
		
		return SUCCESS;
	}
	
	private String fields;
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
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
	public String chartState;
	public String tabularState;
	
	public String getChartState() {
		return chartState;
	}
	public void setChartState(String chartState) {
		this.chartState = chartState;
	}
	public String getTabularState() {
		return tabularState;
	}
	public void setTabularState(String tabularState) {
		this.tabularState = tabularState;
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
	
	private String baseLines;
	public String getBaseLines() {
		return baseLines;
	}
	public void setBaseLines(String baseLines) {
		this.baseLines = baseLines;
	}
	Integer dateOperator;
	String dateOperatorValue;
	public Integer getDateOperator() {
		return dateOperator;
	}
	public void setDateOperator(Integer dateOperator) {
		this.dateOperator = dateOperator;
	}
	public String getDateOperatorValue() {
		return dateOperatorValue;
	}
	public void setDateOperatorValue(String dateOperatorValue) {
		this.dateOperatorValue = dateOperatorValue;
	}
	
	
	private FileFormat fileFormat;
	public int getFileFormat() {
		if (fileFormat != null) {
			return fileFormat.getIntVal();
		}
		return -1;
	}
	public void setFileFormat(int fileFormat) {
		this.fileFormat = FileFormat.getFileFormat(fileFormat);
	}
	
	private String chartType;	// Temp
	public String getChartType() {
		return chartType;
	}
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	public String exportReport() throws Exception{
		
		FacilioContext context = new FacilioContext();
		
		Chain exportChain;
		if (reportId != -1) {
			exportChain = isNewFormat() ? ReadOnlyChainFactory.getExportNewReportFileChain() : ReadOnlyChainFactory.getExportReportFileChain();
			setReportWithDataContext(context);
			reportContext.setDateOperator(dateOperator);
			reportContext.setDateValue(dateOperatorValue);
		}
		else {
			exportChain = isNewFormat() ? ReadOnlyChainFactory.getExportNewAnalyticsFileChain() : ReadOnlyChainFactory.getExportAnalyticsFileChain();
			setReadingsDataContext(context);
			context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
		}
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
		context.put("chartType", chartType);	// Temp
		
		exportChain.execute(context);
		
		setResult("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));
		
		return SUCCESS;
	}
	
	
	public String exportModuleReport() throws Exception {
		FacilioContext context = new FacilioContext();
		Chain exportChain = null;
		if (reportId != -1) {
			exportChain = ReadOnlyChainFactory.getExportNewModuleReportFileChain();
			getReport(context);
		} else {
			updateContext(context);
			
			exportChain = FacilioChain.getNonTransactionChain();
			exportChain.addCommand(new ConstructReportData());
			exportChain.addCommand(ReadOnlyChainFactory.getExportNewModuleReportFileChain());
		}
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
		context.put("chartType", chartType);	// Temp
		
		exportChain.execute(context);
		setResult("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));
		
		return SUCCESS;
	}
	
	private EMailTemplate emailTemplate;
	public EMailTemplate getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(EMailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	
	public String sendReportMail() throws Exception{

		FacilioContext context = new FacilioContext();
		Chain mailReportChain;
		if (reportId != -1) {
			mailReportChain = isNewFormat() ? ReadOnlyChainFactory.sendNewReportMailChain()  : ReadOnlyChainFactory.sendReportMailChain();
			setReportWithDataContext(context);
			reportContext.setDateOperator(dateOperator);
			reportContext.setDateValue(dateOperatorValue);
		}
		else {
			mailReportChain = isNewFormat() ? ReadOnlyChainFactory.sendNewAnalyticsMailChain() : ReadOnlyChainFactory.sendAnalyticsMailChain();
			setReadingsDataContext(context);
			context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
		}
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		context.put("chartType", chartType);	// Temp
		context.put("isS3Url", true);

		mailReportChain.execute(context);
		
		setResult("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));
		
		return SUCCESS;
	}
	
	public String sendModuleReportMail() throws Exception {
		FacilioContext context = new FacilioContext();
		Chain mailReportChain = FacilioChain.getNonTransactionChain();
		
		if (reportId != -1) {
			getReport(context);
		} else {
			updateContext(context);
			
			mailReportChain.addCommand(new ConstructReportData());
		}
		context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
		context.put("chartType", chartType);	// Temp
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		
		context.put("isS3Url", true);

		mailReportChain.addCommand(ReadOnlyChainFactory.getExportNewModuleReportFileChain());
		mailReportChain.addCommand(new SendReadingReportMailCommand());
		mailReportChain.execute(context);

		setResult("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));

		return SUCCESS;
	}
	
	ReportInfo reportInfo;
	public ReportInfo getReportInfo() {
		return reportInfo;
	}
	public void setReportInfo(ReportInfo reportInfo) {
		this.reportInfo = reportInfo;
	}
	
	public String scheduleReport() throws Exception{
		
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.Workflow.TEMPLATE, reportInfo.getEmailTemplate());
		context.put(FacilioConstants.ContextNames.SCHEDULE_INFO, reportInfo);
 		
		Chain scheduleReportChain = TransactionChainFactory.scheduleReportChain();
		scheduleReportChain.execute(context);
		
		setResult("id", context.get(FacilioConstants.ContextNames.RECORD_ID));
		
		return SUCCESS;
	}
	
	public String scheduledList() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		Chain scheduleReportListChain = ReadOnlyChainFactory.fetchScheduledReportsChain();
		scheduleReportListChain.execute(context);
		setResult("scheduledReports", context.get(FacilioConstants.ContextNames.REPORT_LIST));
		
		return SUCCESS;
	}
	
	public String deleteScheduledReport () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		Chain delteScheduleChain = TransactionChainFactory.deleteScheduledReportsChain();
		delteScheduleChain.execute(context);
		
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
		
		return SUCCESS;
	}
	
	public String editScheduledReport () throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.Workflow.TEMPLATE, reportInfo.getEmailTemplate());
		context.put(FacilioConstants.ContextNames.SCHEDULE_INFO, reportInfo);
		
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
		Chain mailReportChain = TransactionChainFactory.updateScheduledReportsChain();
		mailReportChain.execute(context);
		
		setModuleName(reportInfo.getModuleName());
		scheduledList();
		
		return SUCCESS;
	}
	
	public String searchReportAndFolders() throws Exception {
		
		List<ReportFolderContext> reportFolders = ReportUtil.getAllReportFolder(moduleName, false, getSearch());
		List<ReportContext> reports = ReportUtil.getReports(moduleName, getSearch());
		setResult("reportFolders", reportFolders);
		setResult("reports", reports);
		return SUCCESS;
	}
	
	private List<Long> ids;
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	
}