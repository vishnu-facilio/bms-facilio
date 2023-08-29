package com.facilio.bmsconsole.actions;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.context.*;
import com.facilio.alarms.sensor.context.sensoralarm.SensorAlarmContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpAlarmContext;

import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.modules.fields.LookupField;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationDataContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.report.context.*;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.context.ReportUserFilterContext;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.ims.handler.AuditLogHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.AddOrUpdateReportCommand;
import com.facilio.bmsconsole.commands.ConstructReportData;
import com.facilio.bmsconsole.commands.GenerateCriteriaFromFilterCommand;
import com.facilio.bmsconsole.commands.GetCriteriaDataCommand;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ReadingRuleType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fs.FileInfo.FileFormat;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.ReadingAnalysisContext.AnalyticsType;
import com.facilio.report.context.ReadingAnalysisContext.ReportFilterMode;
import com.facilio.report.context.ReadingAnalysisContext.ReportMode;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.workflows.util.WorkflowUtil;

import static com.facilio.ns.context.NsFieldType.RELATED_READING;

public class V2ReportAction extends FacilioAction {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(V2ReportAction.class.getName());
    private List<ReportDrilldownPathContext> reportDrilldownPath;
    private ReportSettings reportSettings;

    @JSON(serialize = false)
    public ReportSettings getReportSettings() {
        return reportSettings;
    }

    public void setReportSettings(ReportSettings reportSettings) {
        this.reportSettings = reportSettings;
    }

    @JSON(serialize = false)
    public List<ReportDrilldownPathContext> getReportDrilldownPath() {
        return reportDrilldownPath;
    }

    public void setReportDrilldownPath(List<ReportDrilldownPathContext> reportDrilldownPath) {
        this.reportDrilldownPath = reportDrilldownPath;
    }

    private ReportDrilldownParamsContext drilldownParams;

    public ReportDrilldownParamsContext getDrilldownParams() {
        return drilldownParams;
    }

    public void setDrilldownParams(ReportDrilldownParamsContext drilldownParams) {
        this.drilldownParams = drilldownParams;
    }

    private ReportContext reportContext;
    private long folderId = -1;

    public String getTtimeFilter() {
        return ttimeFilter;
    }

    public void setTtimeFilter(String ttimeFilter) {
        this.ttimeFilter = ttimeFilter;
    }

    private String ttimeFilter;

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
    private boolean unsavedReport;

    public boolean isUnsavedReport() {
        return unsavedReport;
    }

    public void setUnsavedReport(boolean unsavedReport) {
        this.unsavedReport = unsavedReport;
    }

    String moduleName;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String fetchReportFolders() throws Exception {

        List<ReportFolderContext> reportFolders = ReportUtil.getAllReportFolder(moduleName, getIsWithReport(), null,
                isPivot, null);
        setResult("reportFolders", reportFolders);
        setResult("moduleName", moduleName);
        return SUCCESS;
    }

    public String fetchAllCustomModuleReportFolders() throws Exception {

        List<ReportFolderContext> reportFolders = ReportUtil.getAllCustomModuleReportFolder(getIsWithReport(), null);
        setResult("reportFolders", reportFolders);
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

    private Boolean isPivot = false;

    public Boolean getIsPivot() {
        return isPivot;
    }

    public void setIsPivot(Boolean isPivot) {
        this.isPivot = isPivot;
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

    private Boolean shouldIncludeMarked;

    public Boolean isShouldIncludeMarked() {
        return shouldIncludeMarked;
    }

    public void setShouldIncludeMarked(Boolean shouldIncludeMarked) {
        this.shouldIncludeMarked = shouldIncludeMarked;
    }

    public String fetchReportWithData() throws Exception {

        FacilioContext context = new FacilioContext();
        setReportWithDataContext(context); // This could be moved to a command
        if(spaceId_forExport != null){
            List<Long> list = new ArrayList<Long>();
            list.add(spaceId_forExport);
            context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, list);
            context.put("filterModeValue", filterModeValue_toExport);
        }
        FacilioChain fetchReadingDataChain = newFormat ? ReadOnlyChainFactory.newFetchReportDataChainV3()
                : ReadOnlyChainFactory.fetchReportDataChain();
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

        if (reportContext != null) {
            if (reportContext.getReportState() != null && !reportContext.getReportState().isEmpty()) {
                if (reportContext.getReportState()
                        .containsKey(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR)) {
                    AggregateOperator groupByAggr = AggregateOperator.getAggregateOperator(((Long) reportContext
                            .getReportState().get(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR)).intValue());
                    reportContext.setgroupByTimeAggr(groupByAggr);
                }
            }
        }

        if (startTime > 0 && endTime > 0) {
            reportContext.setDateRange(new DateRange(startTime, endTime));
            reportContext.setDateValue(new DateRange(startTime, endTime).toString());
        }
        if (dashboardId > 0) {
            DateRange range = DashboardUtil.getDateFilterFromDashboard(dashboardId);
            if (range != null) {
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

        if (hmAggr != null) {
            reportContext.addToReportState(FacilioConstants.ContextNames.HEATMAP_AGGR, hmAggr);
        }
        if (groupByTimeAggr != null) {
            reportContext.addToReportState(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR,
                    groupByTimeAggr.getValue());
            reportContext.setgroupByTimeAggr(groupByTimeAggr);
        }
        if (scatterConfig != null) {
            reportContext.addToReportState(FacilioConstants.ContextNames.REPORT_SCATTER_CONFIG, scatterConfig);
        }
        if (xAggr != null) {
            reportContext.setxAggr(xAggr);
        }

        if (template != null) {
            reportContext.setReportTemplate(template);

        }
        if (analyticsType != -1) {
            reportContext.addToReportState(FacilioConstants.ContextNames.ANALYTICS_TYPE, analyticsType);
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
        FacilioContext context = new FacilioContext();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        reportFolder.setOrgId(AccountUtil.getCurrentOrg().getId());
        reportFolder.setModuleId(module.getModuleId());

        reportFolder = ReportUtil.addReportFolder(reportFolder);
        setResult("reportFolder", reportFolder);
        return SUCCESS;
    }

    public String addReportFolderPermission() throws Exception {
        if (folderId > 0) {
            if (!reportFolder.getIds().isEmpty() && reportFolder.getIds() != null) {
                SharingAPI.deleteSharingForParent(reportFolder.getIds(), ModuleFactory.getReportSharingModule());
            }
            SharingAPI.addSharing(reportFolder.getReportSharing(), folderId, ModuleFactory.getReportSharingModule());
        }
        return SUCCESS;

    }

    public String updateReportFolder() throws Exception {

        if (reportFolder != null) {
            ReportUtil.updateReportFolder(reportFolder);
            setResult("reportFolder", reportFolder);
        }

        return SUCCESS;
    }

    public String deleteReportFolder() throws Exception {

        if (reportFolder != null) {

            List<Map<String, Object>> reports = ReportUtil.getReportFromFolderId(reportFolder.getId());
            if (reports == null || reports.isEmpty()) {
                ReportUtil.deleteReportFolder(reportFolder);
            } else {
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
        if (mode.equals(ReportMode.SERIES)) {
            setxAggr(0);
        }

        context.put(FacilioConstants.ContextNames.START_TIME, startTime);
        context.put(FacilioConstants.ContextNames.END_TIME, endTime);
        context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
        context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS,
                FieldUtil.getAsBeanListFromJsonArray(fieldArray, ReadingAnalysisContext.class));
        context.put(FacilioConstants.ContextNames.REPORT_MODE, mode);
        context.put(FacilioConstants.ContextNames.BASE_LINE_LIST,
                FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));
        context.put(FacilioConstants.ContextNames.REPORT_FILTER_MODE, filterMode);
        context.put(FacilioConstants.ContextNames.ASSET_CATEGORY, assetCategory);
        context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, spaceId);
        context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentId);
        context.put(FacilioConstants.ContextNames.REPORT_SHOW_ALARMS, showAlarms);
        context.put(FacilioConstants.ContextNames.REPORT_SHOW_SAFE_LIMIT, showSafeLimit);
        context.put(FacilioConstants.Workflow.WORKFLOW, transformWorkflow);
        context.put(FacilioConstants.ContextNames.REPORT_HANDLE_BOOLEAN, newFormat);
        context.put(FacilioConstants.ContextNames.DEFAULT_DATE, defaultDate);
        context.put(FacilioConstants.ContextNames.ANALYTICS_TYPE, analyticsType);
        if(isUnsavedReport()) {
            context.put(FacilioConstants.ContextNames.UNSAVED_REPORT, unsavedReport);
            context.put(FacilioConstants.ContextNames.CHART_STATE, chartState);
        }
        if (trendLine != null) {
            context.put(FacilioConstants.ContextNames.TREND_LINE, trendLine);
        }

        if (regressionConfig != null && !regressionConfig.isEmpty()) {
            context.put(FacilioConstants.ContextNames.REGRESSION_CONFIG, regressionConfig);
        }

        if (hmAggr != null) {
            context.put(FacilioConstants.ContextNames.HEATMAP_AGGR, hmAggr);
        }
        if (groupByTimeAggr != null) {
            context.put(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR, groupByTimeAggr);
        }
        if (scatterConfig != null && !scatterConfig.isEmpty()) {
            context.put(FacilioConstants.ContextNames.REPORT_SCATTER_CONFIG, scatterConfig);
        }
        if(ttimeFilter != null)
        {
            JSONObject timefilter = (JSONObject) parser.parse(ttimeFilter);
            context.put(FacilioConstants.ContextNames.REPORT_TTIME_FILTER, timefilter);
        }

        context.put(FacilioConstants.ContextNames.ALARM_ID, alarmId);

        context.put(FacilioConstants.ContextNames.TIME_FILTER, timeFilter);
        context.put(FacilioConstants.ContextNames.DATA_FILTER, dataFilter);

    }

    public String fetchReportByType() throws Exception {
        List<ReportContext> reports = ReportUtil.fetchAllReportsByType(reportType);
        setResult(FacilioConstants.ContextNames.REGRESSION_REPORT, reports);
        return SUCCESS;
    }

    public String addOrUpdateReadingReport() throws Exception {
        FacilioContext context = new FacilioContext();
        setReadingsDataContext(context);

        if (reportContext.getTemplate() != null) {
            setTemplate(reportContext.getReportTemplate());
        }

        if (reportContext != null && regressionConfig != null && regressionConfig.size() != 0
                && regressionType != null) {
            JSONObject reportState;
            if (reportContext.getReportState() != null && !reportContext.getReportState().isEmpty()) {
                reportState = reportContext.getReportState();
            } else {
                reportState = new JSONObject();
            }
            List<Map<String, Object>> regressionConfigJSON = new ArrayList<Map<String, Object>>();
            for (RegressionContext config : regressionConfig) {
                regressionConfigJSON.add(FieldUtil.getAsProperties(config));
            }
            reportState.put("regressionConfig", regressionConfigJSON);
            reportState.put("regressionType", regressionType);

            reportContext.setReportState(reportState);
            context.put(FacilioConstants.ContextNames.REGRESSION_CONFIG, regressionConfig);
        }
        if (reportContext != null && hmAggr != null) {
            JSONObject reportState;
            if (reportContext.getReportState() != null && !reportContext.getReportState().isEmpty()) {
                reportState = reportContext.getReportState();
            } else {
                reportState = new JSONObject();
            }
            reportState.put("hmAggr", hmAggr);
            reportContext.setReportState(reportState);
            context.put(FacilioConstants.ContextNames.HEATMAP_AGGR, hmAggr);
        }
        if (reportContext != null && groupByTimeAggr != null) {
            JSONObject reportState;
            if (reportContext.getReportState() != null && !reportContext.getReportState().isEmpty()) {
                reportState = reportContext.getReportState();
            } else {
                reportState = new JSONObject();
            }
            reportState.put("groupByTimeAggr", groupByTimeAggr.getValue());
            reportContext.setReportState(reportState);
            reportContext.setgroupByTimeAggr(groupByTimeAggr);
            context.put(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR, groupByTimeAggr);
        }
        if (reportContext != null && scatterConfig != null) {
            JSONObject reportState;
            if (reportContext.getReportState() != null && !reportContext.getReportState().isEmpty()) {
                reportState = reportContext.getReportState();
            } else {
                reportState = new JSONObject();
            }
            reportState.put("scatterConfig", scatterConfig);
            reportContext.setReportState(reportState);
            context.put(FacilioConstants.ContextNames.REPORT_SCATTER_CONFIG, scatterConfig);
        }
        context.put(FacilioConstants.ContextNames.REPORT_TEMPLATE, template);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperatorValue);
        context.put(FacilioConstants.ContextNames.CHART_STATE, chartState);
        context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);

        FacilioChain addReadingReport = TransactionChainFactory.addOrUpdateReadingReportChain();
        addReadingReport.execute(context);

        return setReportResult(context);
    }

    public String moveReport() throws Exception {
        if (reportId > 0) {
            reportContext = ReportUtil.getReport(reportId);
            reportContext.setReportFolderId(folderId);
            ReportUtil.moveReport(reportContext);
            return SUCCESS;
        }
        return ERROR;
    }

    public String duplicateReport() throws Exception {

        if (reportId > 0) {
            reportContext = ReportUtil.getReport(reportId);
            reportContext.setId(-1);

            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.REPORT, reportContext);
            FacilioChain addReport = TransactionChainFactory.addOrUpdateReportChain();
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

        if (cardParamJson != null) {
            params = cardParamJson;
        } else if (cardWidgetId > 0) {
            DashboardWidgetContext dashboardWidgetContext = DashboardUtil.getWidget(cardWidgetId);

            WidgetStaticContext widgetStaticContext = (WidgetStaticContext) dashboardWidgetContext;
            params = widgetStaticContext.getParamsJson();
        }
        params = DashboardUtil.getCardParams(params);

        List<ReadingAnalysisContext> metrics = new ArrayList<>();
        if (params != null) {

            int xAggrInt = params.get("xAggr") != null ? Integer.parseInt(params.get("xAggr").toString()) : 0;
            setxAggr(xAggrInt);

            DateOperators dateOperator = (DateOperators) Operator
                    .getOperator(Integer.parseInt(params.get("dateOperator").toString()));

            ReportYAxisContext reportaxisContext = new ReportYAxisContext();
            reportaxisContext.setFieldId((Long) params.get("fieldId"));
            reportaxisContext.setAggr(Integer.parseInt(params.get("aggregateFunc").toString()));

            Map<String, String> aliases = new HashMap<>();
            aliases.put("actual", "A");

            ReadingAnalysisContext readingAnalysisContext = new ReadingAnalysisContext();
            readingAnalysisContext.setParentId(Collections.singletonList((Long) params.get("parentId")));
            readingAnalysisContext.setType(1);
            readingAnalysisContext.setAliases(aliases);
            readingAnalysisContext.setyAxis(reportaxisContext);

            metrics.add(readingAnalysisContext);

            if (startTime > 0 && endTime > 0) {

                context.put(FacilioConstants.ContextNames.START_TIME, startTime);
                context.put(FacilioConstants.ContextNames.END_TIME, endTime);
            } else {

                context.put(FacilioConstants.ContextNames.START_TIME, dateOperator.getRange(null).getStartTime());
                context.put(FacilioConstants.ContextNames.END_TIME, dateOperator.getRange(null).getEndTime());
            }

            context.put(FacilioConstants.ContextNames.REPORT_X_AGGR, xAggr);
            context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS, metrics);
            context.put(FacilioConstants.ContextNames.REPORT_MODE, mode);
            context.put(FacilioConstants.ContextNames.REPORT_CALLING_FROM, "card");
            context.put(FacilioConstants.ContextNames.REPORT_HANDLE_BOOLEAN, newFormat);
            context.put(FacilioConstants.ContextNames.ANALYTICS_TYPE, AnalyticsType.READINGS.getIntVal());

            newFormat = true;
            FacilioChain fetchReadingDataChain = newFormat ? ReadOnlyChainFactory.newFetchReadingReportChain()
                    : ReadOnlyChainFactory.fetchReadingReportChain();
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
        context.put(FacilioConstants.ContextNames.REPORT_Y_FIELDS,
                FieldUtil.getAsBeanListFromJsonArray(fieldArray, ReadingAnalysisContext.class));
        context.put(FacilioConstants.ContextNames.REPORT_MODE, mode);
        context.put(FacilioConstants.ContextNames.BASE_LINE_LIST,
                FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));
        context.put(FacilioConstants.ContextNames.CHART_STATE, chartState);
        context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);

        FacilioChain addWorkOrderChain = TransactionChainFactory.addWorkOrderReportChain();
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

    private Integer alarmType;

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    private boolean isNewReadingRule;

    public String fetchReadingsFromAlarm() throws Exception {

        if (AccountUtil.isFeatureEnabled(FeatureLicense.NEW_ALARMS)) {
            getDataPointFromNewAlarm();
        } else {
            getDataPointFromAlarm();
        }

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.SHOULD_INCLUDE_MARKED, shouldIncludeMarked);
        context.put(FacilioConstants.ContextNames.REPORT_FROM_ALARM, true);
        context.put(FacilioConstants.ContextNames.ALARM_RESOURCE, alarmResource);
        context.put(FacilioConstants.ContextNames.ALARM_TYPE, alarmType);
        context.put(FacilioConstants.ContextNames.IS_NEW_READING_RULE, this.isNewReadingRule);
        if (readingRuleId > 0) {
            context.put(FacilioConstants.ContextNames.FETCH_EVENT_BAR, true);
            context.put(FacilioConstants.ContextNames.READING_RULE_ID, readingRuleId);
        }
        setReadingsDataContext(context);

        FacilioChain fetchReadingDataChain = newFormat ? ReadOnlyChainFactory.newFetchReadingReportChain()
                : ReadOnlyChainFactory.fetchReadingReportChain();
        fetchReadingDataChain.execute(context);

        return setReportResult(context);
    }

    public String fetchReadingsData() throws Exception {
        if (alarmId > 0 && fields == null) {
            if (AccountUtil.isFeatureEnabled(FeatureLicense.NEW_ALARMS)) {
                getDataPointFromNewAlarm();
            } else {
                getDataPointFromAlarm();
            }
        }
        FacilioContext context = new FacilioContext();
        setReadingsDataContext(context);

        // temporary variable
        context.put(FacilioConstants.ContextNames.SHOULD_INCLUDE_MARKED, shouldIncludeMarked);

        if (template != null) {
            context.put(FacilioConstants.ContextNames.REPORT_TEMPLATE, template);
        }

        FacilioChain fetchReadingDataChain = newFormat ? ReadOnlyChainFactory.newFetchReadingReportChain()
                : ReadOnlyChainFactory.fetchReadingReportChain();
        fetchReadingDataChain.execute(context);

        return setReportResult(context);
    }

    public String fetchRegressionReport() throws Exception {
        FacilioContext context = new FacilioContext();
        setReadingsDataContext(context);

        if (regressionConfig == null || regressionConfig.isEmpty()) {
            setResult("error", "Regression Config cannot be empty.");
            return SUCCESS;
        } else {
            FacilioChain c = ReadOnlyChainFactory.fetchRegressionReportChain();
            c.execute(context);
            return setReportResult(context);
        }

    }

    private Integer reportType;

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    private List<RegressionContext> regressionConfig;

    public List<RegressionContext> getRegressionConfig() {
        return regressionConfig;
    }

    public void setRegressionConfig(List<RegressionContext> regressionConfig) {
        this.regressionConfig = regressionConfig;
    }

    private String regressionType;

    public String getRegressionType() {
        return regressionType;
    }

    public void setRegressionType(String regressionType) {
        this.regressionType = regressionType;
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

    private JSONArray having;

    public JSONArray getHaving() {
        return having;
    }

    public void setHaving(JSONArray having) {
        this.having = having;
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

    private boolean defaultDate = false;

    public boolean getDefaultDate() {
        return defaultDate;
    }

    public void setDefaultDate(boolean date) {
        this.defaultDate = date;
    }

    private String scatterConfig;

    public String getScatterConfig() {
        return scatterConfig;
    }

    public void setScatterConfig(String scatterConfig) {
        this.scatterConfig = scatterConfig;
    }

    // public String fetchReportDataFromPm() throws Exception{
    // FacilioContext context = new FacilioContext();
    // FacilioChain c = FacilioChain.getNonTransactionChain();
    // if(pmId != -1) {
    // context.put("pmId", pmId);
    // context.put("resourceId", resourceId);
    // c.addCommand(new ConstructReportDataForPM());
    // c.addCommand(ReadOnlyChainFactory.constructAndFetchReportDataChain());
    // c.execute(context);
    // }
    // return setReportResult(context);
    //
    // }

    public String fetchReportData() throws Exception {
        FacilioChain c = FacilioChain.getNonTransactionChain();
        FacilioContext context = c.getContext();
        updateContext(context);
        c.addCommand(new ConstructReportData());
        c.addCommand(ReadOnlyChainFactory.constructAndFetchReportDataChain());
        c.execute();

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
            reportContext.setDateOperator(DateOperators.BETWEEN);
        }
        reportContext.setUserFilters(userFilters, true);
    }

    private void updateContext(FacilioContext context) throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray baseLineList = null;
        if (baseLines != null && !baseLines.isEmpty()) {
            baseLineList = (JSONArray) parser.parse(baseLines);
        }
        context.put("x-axis", xField);
        context.put("date-field", dateField);
        context.put("y-axis", yField);
        context.put("group-by", groupBy);
        context.put("criteria", criteria);
        context.put("sort_fields", sortFields);
        context.put("sort_order", sortOrder);
        context.put("limit", limit);
        context.put("user-filters", userFilters);
        context.put("chartState", chartState);
        context.put("having", having);
        context.put(FacilioConstants.ContextNames.BASE_LINE_LIST,
                FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));

        context.put(FacilioConstants.Reports.MODULE_TYPE, moduleType);

        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
    }

    public String saveReport() throws Exception {
        FacilioChain chain = FacilioChain.getTransactionChain();
        FacilioContext context = new FacilioContext();

        if (reportContext == null) {
            throw new Exception("Report context cannot be empty");
        }
        reportContext.setChartState(chartState);
        reportContext.setTabularState(tabularState);
        reportContext.setType(ReportType.WORKORDER_REPORT);
        reportContext.setModuleType(moduleType);
        reportContext.setReportDrilldownPath(getReportDrilldownPath());
        reportContext.setReportSettings(getReportSettings());
        if (StringUtils.isEmpty(moduleName)) {
            throw new Exception("Module name is mandatory");
        }

        if (reportId > 0) {
            ReportContext report = ReportUtil.getReport(reportId);
            reportContext.setId(report.getId());
        }
        if (dateField != null && (Boolean) dateField.get(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA) != null) {
            JSONObject reportState;
            if (reportContext.getReportState() != null && !reportContext.getReportState().isEmpty()) {
                reportState = reportContext.getReportState();
            } else {
                reportState = new JSONObject();
            }
            reportState.put(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA,
                    (Boolean) dateField.get(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA));
            reportContext.setReportState(reportState);
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

    private boolean needCriteriaData = false;

    public boolean getNeedCriteriaData() {
        return needCriteriaData;
    }

    public void setNeedCriteriaData(boolean needCriteriaData) {
        this.needCriteriaData = needCriteriaData;
    }

    public String executeReport() throws Exception {

        try {
            FacilioChain chain = FacilioChain.getNonTransactionChain();
            FacilioContext context = new FacilioContext();
            if (getFilters() != null) {
                chain.addCommand(new GenerateCriteriaFromFilterCommand());
                JSONParser parser = new JSONParser();
                JSONObject filter = (JSONObject) parser.parse(getFilters());
                context.put(FacilioConstants.ContextNames.FILTERS, filter);
            }
            getReport(context);

            chain.addCommand(ReadOnlyChainFactory.constructAndFetchReportDataChain());
            chain.addCommand(new GetModuleFromReportContextCommand());
            if (needCriteriaData) {
                chain.addCommand(new GetCriteriaDataCommand());
            }
            context.put(FacilioConstants.ContextNames.REPORT_DRILLDOWN_PARAMS, getDrilldownParams());
            chain.execute(context);
            return setReportResult(context);
        }
        catch(Exception e){
            LOGGER.info("Error occurred in executeReport",e);
            throw new Exception("Error occurred in executeReport",e);
        }
    }

    public String getReportFields() throws Exception {
        JSONObject reportFields = ReportFactoryFields.getReportFields(moduleName);

        setResult("meta", reportFields);
        return SUCCESS;
    }

    public String getSubModulesListForReports() throws Exception {
        Set<FacilioModule> subModulesList = ReportFactoryFields.getSubModulesList(moduleName);

        setResult("modules", subModulesList);
        return SUCCESS;
    }

    public String getDataModuleList() throws Exception {
        Set<FacilioModule> dataModulesList = ReportFactoryFields.getDataModulesList(moduleName);
        setResult("modules", dataModulesList);
        return SUCCESS;
    }

    public String getTabularRowReportFields() throws Exception {
        JSONObject reportFields = ReportFactoryFields.getTabularRowReportFields(moduleName);

        setResult("meta", reportFields);
        return SUCCESS;
    }

    public String getMetricsList() throws Exception {
        Set<FacilioField> reportFields = ReportFactoryFields.getMetricsList(moduleName);

        setResult("Fields", reportFields);
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

    private String hmAggr = null;

    public String hmAggr() {
        return hmAggr;
    }

    public void sethmAggr(String hmAggr) {
        this.hmAggr = hmAggr;
    }

    private AggregateOperator groupByTimeAggr;

    public int getgroupByTimeAggr() {
        if (groupByTimeAggr != null) {
            return groupByTimeAggr.getValue();
        }
        return -1;
    }

    public void setgroupByTimeAggr(int groupByTimeAggr) {
        this.groupByTimeAggr = AggregateOperator.getAggregateOperator(groupByTimeAggr);
    }

    private int analyticsType = -1;

    public int isanalyticsType() {
        return analyticsType;
    }

    public void setAnalyticsType(int analyticsType) {
        this.analyticsType = analyticsType;
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
    private Long spaceId_forExport;
    public Integer getFilterModeValue_toExport() {
        return filterModeValue_toExport;
    }

    public void setFilterModeValue_toExport(Integer filterModeValue_toExport) {
        this.filterModeValue_toExport = filterModeValue_toExport;
    }

    private Integer filterModeValue_toExport;

    public Long getSpaceId_forExport() {
        return spaceId_forExport;
    }

    public void setSpaceId_forExport(Long spaceId_forExport) {
        this.spaceId_forExport = spaceId_forExport;
    }

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
        if (!deleteWithWidget) {
            widgetCharts = DashboardUtil.getWidgetFromDashboard(reportId, true);
        }
        if (widgetCharts == null || widgetCharts.isEmpty()) {
            ReportContext report = ReportUtil.getReport(reportId);
            ReportUtil.deleteReport(reportId);
            if(report != null) {
                String log_message = "Report {%s} has been deleted for {%s} Module.";
                FacilioModule module = report.getModule();
                if(module != null) {
                    AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format(log_message, report.getName(), module.getName()), "", AuditLogHandler.RecordType.MODULE, module.getName(), reportId);
                    AuditLogUtil.sendAuditLogs(auditLog);
                }
            }
            return SUCCESS;
        } else {
            setResult("errorString", "Report Used In Dashboard");
        }
        return SUCCESS;
    }

    private void getDataPointFromNewAlarm() throws Exception {
        AlarmOccurrenceContext alarmOccurrence = NewAlarmAPI.getAlarmOccurrence(alarmId);
        ReadingAlarm readingAlarmContext = (ReadingAlarm) alarmOccurrence.getAlarm();
        this.isNewReadingRule = alarmOccurrence instanceof ReadingAlarmOccurrenceContext ? ((ReadingAlarmOccurrenceContext) alarmOccurrence).getIsNewReadingRule() : false;
        List<ReadingRuleInterface> readingRules = new ArrayList<>();
        if (isWithPrerequsite) { // new 1st
            if (isNewReadingRule) {
                NewReadingRuleContext newRuleCtx = NewReadingRuleAPI.getReadingRules(Collections.singletonList(readingAlarmContext.getRule().getId())).get(0);
                readingRules.add(newRuleCtx);
            } else {
                AlarmRuleContext alarmRuleContext = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(readingAlarmContext.getRule().getId()));
                readingRules.add(alarmRuleContext.getAlarmTriggerRule());
                readingRules.add(alarmRuleContext.getPreRequsite());
            }
        } else if (readingRuleId > 0) { // new 2nd
            if (isNewReadingRule) {
                NewReadingRuleContext newRuleCtx = NewReadingRuleAPI.getRule(readingRuleId);
                readingRules.add(newRuleCtx);
            } else {
                ReadingRuleContext readingruleContext = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(readingRuleId);
                readingRules.add(readingruleContext);
            }
        } else { // old
            long ruleId = -1;

            if (alarmOccurrence.getAlarm() instanceof ReadingAlarm) {
                ruleId = ((ReadingAlarm) alarmOccurrence.getAlarm()).getRule().getId();
            }
            // else if (alarm instanceof MLAlarmContext) {
            // ruleId = ((MLAlarmContext)alarm).getRuleId();
            // }
            else if (alarmOccurrence.getAlarm() instanceof MLAnomalyAlarm) {
                MLAnomalyAlarm mlAnomalyAlarm = (MLAnomalyAlarm) alarmOccurrence.getAlarm();
            } else if (alarmOccurrence.getAlarm() instanceof OperationAlarmContext) {
                OperationAlarmContext opAlarm = (OperationAlarmContext) alarmOccurrence.getAlarm();
            } else if (alarmOccurrence.getAlarm() instanceof SensorRollUpAlarmContext) {
                SensorRollUpAlarmContext sensorAlarm = (SensorRollUpAlarmContext) alarmOccurrence.getAlarm();
            }
            if (ruleId > 0) {
                ReadingRuleContext readingruleContext = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(ruleId);
                readingRules.add(readingruleContext);
            } else {
                // isAnomalyAlarm = true;
            }
        }

        ResourceContext resource = ResourceAPI.getResource(alarmOccurrence.getResource().getId());
        this.alarmResource = resource;

        alarmType = alarmOccurrence.getAlarm().getType();
        JSONArray dataPoints = new JSONArray();
        if (readingRules != null && !readingRules.isEmpty() && readingRules.get(0) != null) {

            Set readingMap = new HashSet();
            for (ReadingRuleInterface readingRule : readingRules) {
                if (readingRule != null) {
                    if (readingRule instanceof ReadingRuleContext) {
                        dataPoints.addAll(getDataPointsJSONFromRule((ReadingRuleContext) readingRule, resource, alarmOccurrence, readingMap));
                    } else {//For NewReadingRuleContext
                        dataPoints.addAll(getDataPointsJSONFromNewRule((NewReadingRuleContext) readingRule, resource));
                    }
                }
            }

            if (!isNewReadingRule) {
                if (((ReadingRuleContext) readingRules.get(0)).getBaselineId() != -1) {
                    JSONArray baselineArray = new JSONArray();
                    JSONObject baselineJson = new JSONObject();
                    baselineJson.put("baseLineId", ((ReadingRuleContext) readingRules.get(0)).getBaselineId());
                    baselineArray.add(baselineJson);
                    baseLines = baselineArray.toJSONString();
                }
            }

        } else if (alarmOccurrence.getAlarm() instanceof MLAnomalyAlarm) {
            MLAnomalyAlarm mlAnomalyAlarm = (MLAnomalyAlarm) alarmOccurrence.getAlarm();
            dataPoints.addAll(getDataPointsJSONForMLAnomalyAlarm(mlAnomalyAlarm, resource));

        } else if (alarmOccurrence.getAlarm() instanceof OperationAlarmContext) {
            OperationAlarmContext opAlarm = (OperationAlarmContext) alarmOccurrence.getAlarm();
            dataPoints.addAll(getDataPointsJSONForOpAlarm(opAlarm, resource));

        } else if (alarmOccurrence.getAlarm() instanceof SensorRollUpAlarmContext) {
            SensorRollUpAlarmContext sensorAlarm = (SensorRollUpAlarmContext) alarmOccurrence.getAlarm();
            dataPoints.addAll(getDataPointsJSONForSensorAlarm(sensorAlarm, resource));

        }

        if (newFormat) {
            long baselineId = -1l;
            if (readingRules != null && !readingRules.isEmpty() && readingRules.get(0) != null) {
                if(readingRules.get(0) instanceof ReadingRuleContext) {
                    baselineId = ((ReadingRuleContext) readingRules.get(0)).getBaselineId();
                }
            }
            ReportUtil.setAliasForDataPoints(dataPoints, baselineId);
        }

        if (this.startTime <= 0 && this.endTime <= 0) {
            long modifiedTime = alarmOccurrence.getCreatedTime();
            if (alarmOccurrence.getLastOccurredTime() > 0) {
                modifiedTime = alarmOccurrence.getLastOccurredTime();
            }

            DateRange range = DateOperators.CURRENT_N_DAY.getRange("" + modifiedTime);

            this.startTime = range.getStartTime();
            this.endTime = range.getEndTime();
        }

        // for ML, aggr value is hourly
        if (xAggr != null && xAggr != BmsAggregateOperators.DateAggregateOperator.HOURSOFDAYONLY) {
            setxAggr(0);
        } else {
            for (int i = 0; i < dataPoints.size(); i++) {
                JSONObject json = (JSONObject) dataPoints.get(i);
                JSONObject yAxisJson = (JSONObject) json.get("yAxis");
                yAxisJson.put("aggr", BmsAggregateOperators.NumberAggregateOperator.SUM.getValue());
            }
        }
        List<String> resourseNdParentList = new ArrayList<>();
        // removing duplicate field Id in alarm report
        for (int i = 0; i < dataPoints.size(); i++) {
            JSONObject json = (JSONObject) dataPoints.get(i);
            JSONObject yAxisJson = (JSONObject) json.get("yAxis");
            JSONArray parentIds = (JSONArray) json.get("parentId");
            String parentFldKey = yAxisJson.get("fieldId") + "_" + parentIds.get(0);
            if (resourseNdParentList.contains(parentFldKey)) {
                dataPoints.remove(i);
            } else {
                resourseNdParentList.add(parentFldKey);
            }
        }
        fields = dataPoints.toJSONString();
    }

    private void getDataPointFromAlarm() throws Exception {

        AlarmContext alarmContext = AlarmAPI.getReadingAlarmContext(alarmId);

        if (alarmContext == null) {
            alarmContext = AlarmAPI.getMLAlarmContext(alarmId);
        }

        List<ReadingRuleContext> readingRules = new ArrayList<>();
        boolean isAnomalyAlarm = false;
        if (isWithPrerequsite) { // new 1st

            ReadingAlarmContext readingAlarmContext = (ReadingAlarmContext) alarmContext;
            AlarmRuleContext alarmRuleContext = new AlarmRuleContext(
                    ReadingRuleAPI.getReadingRulesList(readingAlarmContext.getRuleId()));
            readingRules.add((ReadingRuleContext) alarmRuleContext.getAlarmTriggerRule());
            readingRules.add(alarmRuleContext.getPreRequsite());

        } else if (readingRuleId > 0) { // new 2nd

            ReadingRuleContext readingruleContext = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(readingRuleId);
            readingRules.add(readingruleContext);
        } else { // old
            long ruleId = -1;

            if (alarmContext instanceof ReadingAlarmContext) {
                ruleId = ((ReadingAlarmContext) alarmContext).getRuleId();
            } else if (alarmContext instanceof MLAlarmContext) {
                ruleId = ((MLAlarmContext) alarmContext).getRuleId();
            }
            if (ruleId > 0) {
                ReadingRuleContext readingruleContext = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(ruleId);
                readingRules.add(readingruleContext);
            } else {
                isAnomalyAlarm = true;
            }
        }

        ResourceContext resource = ResourceAPI.getResource(alarmContext.getResource().getId());
        this.alarmResource = resource;

        JSONArray dataPoints = new JSONArray();

        if (readingRules != null && !readingRules.isEmpty() && readingRules.get(0) != null) {

            for (ReadingRuleContext readingRule : readingRules) {
                if (readingRule != null) {
                    dataPoints.addAll(getDataPointsJSONFromRule(readingRule, resource, alarmContext));
                }
            }

            if (readingRules.get(0).getBaselineId() != -1) {
                JSONArray baselineArray = new JSONArray();
                JSONObject baselineJson = new JSONObject();
                baselineJson.put("baseLineId", readingRules.get(0).getBaselineId());
                baselineArray.add(baselineJson);
                baseLines = baselineArray.toJSONString();
            }

        } else if (isAnomalyAlarm) {

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
        if (alarmContext != null && alarmContext.getAdditionInfo() != null
                && alarmContext.getAdditionInfo().containsKey(additionalDataPointString)) {

            this.startTime = DateTimeUtil.getDayStartTimeOf(alarmContext.getCreatedTime()); // specific handling for
            dataPoints = new JSONArray(); // anomaly alarms

            JSONArray points = FacilioUtil.parseJsonArray(alarmContext.getAdditionInfo().get(additionalDataPointString).toString());

            for (int i = 0; i < points.size(); i++) {
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
        if (alarmId == 890083l) {
            LOGGER.error("new data point json -- " + dataPoints);
        }
        if (newFormat) {
            long baselineId = -1l;
            if (readingRules != null && !readingRules.isEmpty() && readingRules.get(0) != null) {
                baselineId = readingRules.get(0).getBaselineId();
            }
            ReportUtil.setAliasForDataPoints(dataPoints, baselineId);
        }

        if (this.startTime <= 0 && this.endTime <= 0) {
            long modifiedTime = alarmContext.getCreatedTime();
            if (alarmContext.getModifiedTime() > 0) {
                modifiedTime = alarmContext.getModifiedTime();
            }

            DateRange range = DateOperators.CURRENT_N_DAY.getRange("" + modifiedTime);

            this.startTime = range.getStartTime();
            this.endTime = range.getEndTime();
        }

        setxAggr(0);
        fields = dataPoints.toJSONString();
    }

    private Collection getDataPointsJSONFromNewRule(NewReadingRuleContext readingRule, ResourceContext resource) throws Exception {
        JSONArray dataPoints = new JSONArray();

        List<NameSpaceField> fields = readingRule.getNs().getFields();

        for (NameSpaceField nsField : fields) {
            Long readingFieldId = nsField.getFieldId();

            if (readingFieldId > 0) {
                JSONObject dataPoint = new JSONObject();

                List<Long> parentId = new ArrayList<>();
                parentId.add(nsField.getResourceId() != null ? nsField.getResourceId() : resource.getId());

                if (nsField.getNsFieldType().equals(RELATED_READING)) {
                    parentId = getRelatedResourcesWithPosition(nsField.getRelatedInfo().getRelMapContext(),resource.getId());
                }
                dataPoint.put("parentId",FieldUtil.getAsJSONArray(parentId,Long.class));

                JSONObject yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", readingFieldId);
                updateTimeRangeAsPerFieldType(readingFieldId);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);
                dataPoints.add(dataPoint);
            }
        }
        return dataPoints;
    }

    private List<Long> getRelatedResourcesWithPosition(RelationMappingContext relMapCtx, Long resourceId) throws Exception {
        RelationMappingContext.Position position = relMapCtx.getReversePosition();
        List<Long> relResourceIds = RelationUtil.getAllCustomRelationsForRecId(relMapCtx, resourceId);
        return relResourceIds;
    }

    private JSONArray getDataPointsJSONFromRule(ReadingRuleContext readingruleContext, ResourceContext resource,
                                                AlarmOccurrenceContext alarm, Set readingMap) throws Exception {
        JSONArray dataPoints = new JSONArray();
        ResourceContext currentResource = resource;

        if (readingruleContext.getRuleMetrics() != null && !readingruleContext.getRuleMetrics().isEmpty()) {

            for (ReadingRuleMetricContext ruleMetric : readingruleContext.getRuleMetrics()) {
                long resourceId = resource.getId();
                if (ruleMetric.getResourceId() > 0) {
                    resourceId = ruleMetric.getResourceId();
                }
                JSONObject dataPoint = new JSONObject();

                JSONObject yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", ruleMetric.getFieldId());
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resourceId));

                dataPoints.add(dataPoint);
            }

            return dataPoints;
        }

        if (readingruleContext.getThresholdType() == ReadingRuleContext.ThresholdType.ADVANCED.getValue()) {

            if (readingruleContext.getWorkflowId() > 0) {

                WorkflowContext workflow = new WorkflowContext();
                FacilioModule module = ModuleFactory.getWorkflowModule();
                GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                        .select(FieldFactory.getWorkflowFields())
                        .table(module.getTableName())
                        .andCondition(CriteriaAPI.getIdCondition(readingruleContext.getWorkflowId(), module));

                List<Map<String, Object>> props = selectBuilder.get();

                WorkflowContext workflowContext = null;
                if (props != null && !props.isEmpty() && props.get(0) != null) {
                    Map<String, Object> prop = props.get(0);
                    boolean isWithExpParsed = true;

                    workflowContext = FieldUtil.getAsBeanFromMap(prop, WorkflowContext.class);
                    if (workflowContext.isV2Script()) {
                        if (workflowContext.getWorkflowUIMode() == WorkflowContext.WorkflowUIMode.XML.getValue()) {
                            workflowContext = WorkflowUtil
                                    .getWorkflowContextFromString(workflowContext.getWorkflowString(), workflowContext);
                            if (isWithExpParsed) {
                                WorkflowUtil.parseExpression(workflowContext);
                            }
                        } else if (workflowContext.getWorkflowUIMode() == WorkflowContext.WorkflowUIMode.GUI
                                .getValue()) {
                            workflowContext.parseScript();
                        }
                        workflow = workflowContext;
                    } else {
                        workflow = WorkflowUtil.getWorkflowContext(readingruleContext.getWorkflowId(), true);
                    }
                }

                for (WorkflowExpression workflowExp : workflow.getExpressions()) {

                    if (!(workflowExp instanceof ExpressionContext)) {
                        continue;
                    }
                    ExpressionContext exp = (ExpressionContext) workflowExp;
                    if (exp.getModuleName() != null) {

                        JSONObject dataPoint = new JSONObject();

                        FacilioField readingField = null;
                        if (exp.getFieldName() != null) {
                            readingField = DashboardUtil.getField(exp.getModuleName(), exp.getFieldName());

                            updateTimeRangeAsPerFieldType(readingField.getFieldId());

                            JSONObject yAxisJson = new JSONObject();
                            yAxisJson.put("fieldId", readingField.getFieldId());
                            yAxisJson.put("aggr", 0);

                            dataPoint.put("yAxis", yAxisJson);

                        }
                        if (exp.getCriteria() != null) {
                            Map<String, Condition> conditions = exp.getCriteria().getConditions();

                            for (String key : conditions.keySet()) {

                                Condition condition = conditions.get(key);

                                if (condition.getFieldName().equals("parentId")) {
                                    resource = condition.getValue().equals("${resourceId}") ? currentResource
                                            : ResourceAPI.getResource(Long.parseLong(condition.getValue()));

                                    dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                                    break;
                                }
                            }
                        }
                        dataPoint.put("type", 1);
                        if (!readingMap.contains(resource.getId() + "_" + readingField.getFieldId())) {
                            readingMap.add(resource.getId() + "_" + readingField.getFieldId());
                            dataPoints.add(dataPoint);
                        }
                    }
                }
            }
        } else if (readingruleContext.getReadingFieldId() > 0
                && (readingruleContext.getWorkflow() != null || readingruleContext.getCriteria() != null)) {
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

    private JSONArray getDataPointsJSONForMLAnomalyAlarm(MLAnomalyAlarm mlAlarm, ResourceContext resource)
            throws Exception {
        JSONArray dataPoints = new JSONArray();
        JSONObject dataPoint = new JSONObject();

        dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

        JSONObject yAxisJson = new JSONObject();
        yAxisJson.put("fieldId", mlAlarm.getEnergyDataFieldid());
        yAxisJson.put("aggr", 0);

        dataPoint.put("yAxis", yAxisJson);

        dataPoint.put("type", 1);
        dataPoints.add(dataPoint);

        dataPoint = new JSONObject();

        dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

        yAxisJson = new JSONObject();
        yAxisJson.put("fieldId", mlAlarm.getUpperAnomalyFieldid());
        yAxisJson.put("aggr", 0);

        dataPoint.put("yAxis", yAxisJson);

        dataPoint.put("type", 1);

        dataPoints.add(dataPoint);

        return dataPoints;

    }

    private JSONArray getDataPointsJSONForOpAlarm(OperationAlarmContext opAlarm, ResourceContext resource)
            throws Exception {
        JSONArray dataPoints = new JSONArray();
        JSONObject dataPoint = new JSONObject();

        dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

        JSONObject yAxisJson = new JSONObject();
        yAxisJson.put("fieldId", opAlarm.getReadingFieldId());
        yAxisJson.put("aggr", 0);

        dataPoint.put("yAxis", yAxisJson);

        dataPoint.put("type", 1);
        dataPoints.add(dataPoint);

        return dataPoints;

    }

    private Collection getDataPointsJSONForSensorAlarm(SensorRollUpAlarmContext sensorAlarm, ResourceContext resource)
            throws Exception {
        JSONArray dataPoints = new JSONArray();

        List<SensorAlarmContext> sensorAlarms = AlarmAPI.getSensorChildAlarms(sensorAlarm, startTime, endTime);
        if (sensorAlarms != null && !sensorAlarms.isEmpty()) {
            List<Long> duplication = new ArrayList<>();
            for (SensorAlarmContext senAlarm : sensorAlarms) {
                JSONObject dataPoint = new JSONObject();

                if (duplication.isEmpty() || !duplication.contains(senAlarm.getReadingFieldId())) {

                    ResourceContext sensorResource = senAlarm.getResource();

                    dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(sensorResource.getId()));

                    JSONObject yAxisJson = new JSONObject();
                    yAxisJson.put("fieldId", senAlarm.getReadingFieldId());
                    duplication.add(senAlarm.getReadingFieldId());

                    yAxisJson.put("aggr", 0);

                    dataPoint.put("yAxis", yAxisJson);

                    dataPoint.put("type", 1);
                    dataPoints.add(dataPoint);
                }

            }
        } else if (sensorAlarm.getReadingFieldId() > 0 && (dataPoints == null || dataPoints.isEmpty())) {
            ResourceContext sensorResource = sensorAlarm.getResource();

            JSONObject dataPoint = new JSONObject();
            dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(sensorResource.getId()));

            JSONObject yAxisJson = new JSONObject();
            yAxisJson.put("fieldId", sensorAlarm.getReadingFieldId());

            yAxisJson.put("aggr", 0);

            dataPoint.put("yAxis", yAxisJson);

            dataPoint.put("type", 1);
            dataPoints.add(dataPoint);

        }

        return dataPoints;
    }

    private JSONArray getDataPointsJSONFromRule(ReadingRuleContext readingruleContext, ResourceContext resource,
                                                AlarmContext alarmContext) throws Exception {

        JSONArray dataPoints = new JSONArray();
        if (readingruleContext.getReadingRuleTypeEnum() == ReadingRuleType.ML_RULE) {

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
            if (alarmContext.getClearedTime() > 0) {
                dataPoint.put("predictedTime", DateTimeUtil.getMillis(zdt, true) - (6 * 3600000));
            } else {
                dataPoint.put("predictedTime", DateTimeUtil.getMillis(zdt, true));
            }
            dataPoints.add(dataPoint);

            if (readingruleContext.getId() == 5085l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253613l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 7504l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 517774l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8165l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253677l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8749l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253686l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8756l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253612l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8761l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253725l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8766l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 351301l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            if (readingruleContext.getId() == 8771l) {

                dataPoint = new JSONObject();

                dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                yAxisJson = new JSONObject();
                yAxisJson.put("fieldId", 253738l);
                yAxisJson.put("aggr", 0);

                dataPoint.put("yAxis", yAxisJson);

                dataPoint.put("type", 1);

                dataPoints.add(dataPoint);

            }

            zdt = DateTimeUtil.getDateTime(alarmContext.getCreatedTime());
            zdt = zdt.truncatedTo(new SecondsChronoUnit(24 * 60 * 60));
            this.startTime = DateTimeUtil.getMillis(zdt, true);

            zdt = DateTimeUtil.getDateTime(alarmContext.getModifiedTime() + 432000000);
            zdt = zdt.truncatedTo(new SecondsChronoUnit(24 * 60 * 60));
            this.endTime = DateTimeUtil.getMillis(zdt, true);

            LOGGER.error("dataPoints -- " + dataPoints);
            LOGGER.error("startTime -- " + startTime);
            LOGGER.error("startTime -- " + this.endTime);
            return dataPoints;
        }

        ResourceContext currentResource = resource;
        if (readingruleContext.getThresholdType() == ReadingRuleContext.ThresholdType.ADVANCED.getValue()) {

            Set readingMap = new HashSet();
            if (readingruleContext.getWorkflowId() > 0) {
                WorkflowContext workflow = WorkflowUtil.getWorkflowContext(readingruleContext.getWorkflowId(), true);

                for (WorkflowExpression workflowExp : workflow.getExpressions()) {

                    if (!(workflowExp instanceof ExpressionContext)) {
                        continue;
                    }
                    ExpressionContext exp = (ExpressionContext) workflowExp;
                    if (exp.getModuleName() != null) {

                        JSONObject dataPoint = new JSONObject();

                        FacilioField readingField = null;
                        if (exp.getFieldName() != null) {
                            readingField = DashboardUtil.getField(exp.getModuleName(), exp.getFieldName());

                            updateTimeRangeAsPerFieldType(readingField.getFieldId());

                            JSONObject yAxisJson = new JSONObject();
                            yAxisJson.put("fieldId", readingField.getFieldId());
                            yAxisJson.put("aggr", 0);

                            dataPoint.put("yAxis", yAxisJson);

                        }
                        if (exp.getCriteria() != null) {
                            Map<String, Condition> conditions = exp.getCriteria().getConditions();

                            for (String key : conditions.keySet()) {

                                Condition condition = conditions.get(key);

                                if (condition.getFieldName().equals("parentId")) {
                                    resource = condition.getValue().equals("${resourceId}") ? currentResource
                                            : ResourceAPI.getResource(Long.parseLong(condition.getValue()));

                                    dataPoint.put("parentId", FacilioUtil.getSingleTonJsonArray(resource.getId()));

                                    break;
                                }
                            }
                        }
                        dataPoint.put("type", 1);
                        if (!readingMap.contains(resource.getId() + "_" + readingField.getFieldId())) {
                            readingMap.add(resource.getId() + "_" + readingField.getFieldId());
                            dataPoints.add(dataPoint);
                        }
                    }
                }
            }
        } else if (readingruleContext.getReadingFieldId() > 0
                && (readingruleContext.getWorkflow() != null || readingruleContext.getCriteria() != null)) {
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
        if (readingField != null) {
            FormulaFieldContext formulaField = FormulaFieldAPI.getFormulaField(readingField);

            if (formulaField != null && formulaField.getFrequencyEnum() == FacilioFrequency.DAILY) {
                this.startTime = DateTimeUtil.addDays(this.endTime, -10);
            } else if (formulaField != null && formulaField.getFrequencyEnum() == FacilioFrequency.MONTHLY) {
                this.startTime = DateTimeUtil.addMonths(this.endTime, -3);
            } else if (formulaField != null && formulaField.getFrequencyEnum() == FacilioFrequency.WEEKLY) {
                this.startTime = DateTimeUtil.addWeeks(this.endTime, -3);
            }
        }
        LOGGER.error("1.this.startTime --- " + this.startTime);
        LOGGER.error("2.this.endTime --- " + this.endTime);
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
        context.put(FacilioConstants.ContextNames.REPORT_FIELDS,
                FieldUtil.getAsBeanListFromJsonArray(fieldArray, WorkorderAnalysisContext.class));
        context.put(FacilioConstants.ContextNames.BASE_LINE_LIST,
                FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));

        FacilioChain fetchReadingDataChain = ReadOnlyChainFactory.fetchWorkorderReportChain();
        fetchReadingDataChain.execute(context);

        return setReportResult(context);

    }

    private String setReportResult(FacilioContext context) {
        if (context.get(FacilioConstants.ContextNames.REPORT) != null) {

            reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
            if (alarmId > 0) {
                reportContext.setDateOperator(DateOperators.CURRENT_N_DAY.getOperatorId());
            }
            setResult("report", reportContext);
        }
        setResult("reportXValues", context.get(FacilioConstants.ContextNames.REPORT_X_VALUES)); // This can be removed
        // from new format
        setResult("reportData", context.get(FacilioConstants.ContextNames.REPORT_DATA));
        setResult("reportVarianceData", context.get(FacilioConstants.ContextNames.REPORT_VARIANCE_DATA));
        // setResult("reportAlarms",
        // context.get(FacilioConstants.ContextNames.REPORT_ALARMS));
        setResult("safeLimits", context.get(FacilioConstants.ContextNames.REPORT_SAFE_LIMIT));
        setResult(FacilioConstants.ContextNames.REPORT_ALARM_CONTEXT,
                context.get(FacilioConstants.ContextNames.REPORT_ALARM_CONTEXT));

        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        if (module != null) {
            setResult("module", module);
        }
        if (moduleType != -1 || reportContext.getModuleType() != -1) {
            setResult("moduleTypes", ReportFactoryFields.addModuleTypes(module.getName()));
        }
        if (context.containsKey("criteriaData")) {
            setResult("criteriaData", context.get("criteriaData"));
        }

        if (context.containsKey("baselineData")) {
            setResult("baselineData", context.get("baselineData"));
        }
        if (context.containsKey("baselineDataColors")) {
            setResult("baselineDataColors", context.get("baselineDataColors"));
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
    private String url;

    public String getUrl() { return url;}

    public void setUrl(String url) { this.url = url; }

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

    public String trendLine;

    public String getTrendLine() {
        return this.trendLine;
    }

    public void setTrendLine(String trendLine) {
        this.trendLine = trendLine;
    }

    private String timeFilter;

    public String getTimeFilter() {
        return timeFilter;
    }

    public void setTimeFilter(String timeFilter) {
        this.timeFilter = timeFilter;
    }

    private String dataFilter;

    public String getDataFilter() {
        return dataFilter;
    }

    public void setDataFilter(String dataFilter) {
        this.dataFilter = dataFilter;
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

    private String chartType; // Temp

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    private Map<String, Object> exportParams;

    public Map<String, Object> getExportParams() {
        return exportParams;
    }

    public void setExportParams(Map<String, Object> exportParams) {
        this.exportParams = exportParams;
    }

    private Map<String, Object> renderParams;

    public Map<String, Object> getRenderParams() {
        return renderParams;
    }

    public void setRenderParams(Map<String, Object> renderParams) {
        this.renderParams = renderParams;
    }

    private ReportTemplateContext template;

    public ReportTemplateContext getTemplate() {
        return template;
    }

    public void setTemplate(ReportTemplateContext template) {
        this.template = template;
    }

    public void setTemplateString(String templateJSON) throws Exception {
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(templateJSON);
        this.template = FieldUtil.getAsBeanFromJson(jsonObject, ReportTemplateContext.class);
    }

    public String exportReport() throws Exception {

        FacilioContext context = new FacilioContext();

        FacilioChain exportChain;
        if (reportId != -1) {
            exportChain = TransactionChainFactory.getExportReportFileChain();
            setReportWithDataContext(context);
            reportContext.setDateOperator(dateOperator);
            reportContext.setDateValue(dateOperatorValue);
        } else {
            exportChain = TransactionChainFactory.getExportAnalyticsFileChain();
            setReadingsDataContext(context);
            if (template != null) {
                context.put(FacilioConstants.ContextNames.REPORT_TEMPLATE, template);
            }
            context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
        }
        context.put("url",getUrl());
        setExportParamsInContext(context);

        exportChain.execute(context);

        setResult("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));

        return SUCCESS;
    }

    public String exportModuleReport() throws Exception {
        FacilioChain exportChain = null;
        FacilioContext context;

        if (reportId != -1) {
            boolean fetchData = fileFormat != FileFormat.IMAGE && fileFormat != FileFormat.PDF;
            exportChain = TransactionChainFactory.getExportModuleReportFileChain(fetchData);
            context = exportChain.getContext();
            if(getFilters() != null) {
                JSONParser parser = new JSONParser();
                context.put(FacilioConstants.ContextNames.FILTERS, (JSONObject) parser.parse(getFilters()));
            }
            context.put("url",getUrl());
            context.put("is_export_report", true);
            // pass filterJsonString as is, will be set in summary url in export command
            getReport(context);
        } else {
            exportChain = TransactionChainFactory.getExportModuleAnalyticsFileChain();
            context = exportChain.getContext();
            updateContext(context);
        }

        setExportParamsInContext(context);

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

    public String sendReportMail() throws Exception {

        FacilioContext context = new FacilioContext();
        FacilioChain mailReportChain;
        if (reportId != -1) {
            mailReportChain = TransactionChainFactory.sendReportMailChain();
            setReportWithDataContext(context);
            reportContext.setDateOperator(dateOperator);
            reportContext.setDateValue(dateOperatorValue);
        } else {
            mailReportChain = TransactionChainFactory.sendAnalyticsMailChain();
            setReadingsDataContext(context);
            if (template != null) {
                context.put(FacilioConstants.ContextNames.REPORT_TEMPLATE, template);
            }
            context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
        }
        context.put("url",getUrl());
        context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
        context.put("isS3Url", true);
        setExportParamsInContext(context);

        mailReportChain.execute(context);

        setResult("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));

        return SUCCESS;
    }

    public String sendModuleReportMail() throws Exception {
        FacilioContext context = new FacilioContext();
        FacilioChain mailReportChain = FacilioChain.getNonTransactionChain();

        if (reportId != -1) {
            getReport(context);
            boolean fetchData = fileFormat != FileFormat.IMAGE && fileFormat != FileFormat.PDF;
            mailReportChain = TransactionChainFactory.sendModuleReportMailChain(fetchData);
        } else {
            updateContext(context);
            mailReportChain = TransactionChainFactory.sendModuleAnalyticsMailChain();
        }
        context.put("url",getUrl());
        context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
        context.put("isS3Url", true);
        setExportParamsInContext(context);

        mailReportChain.execute(context);

        setResult("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));

        return SUCCESS;
    }

    private void setExportParamsInContext(FacilioContext context) {
        context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
        context.put("chartType", chartType); // Temp
        context.put("exportParams", exportParams);
        context.put("renderParams", renderParams);
    }

    ReportInfo reportInfo;

    public ReportInfo getReportInfo() {
        return reportInfo;
    }

    public void setReportInfo(ReportInfo reportInfo) {
        this.reportInfo = reportInfo;
    }

    public String scheduleReport() throws Exception {

        FacilioContext context = new FacilioContext();

        context.put(FacilioConstants.Workflow.TEMPLATE, reportInfo.getEmailTemplate());
        context.put(FacilioConstants.ContextNames.SCHEDULE_INFO, reportInfo);

        FacilioChain scheduleReportChain = TransactionChainFactory.scheduleReportChain();
        scheduleReportChain.execute(context);

        setResult("id", context.get(FacilioConstants.ContextNames.RECORD_ID));

        return SUCCESS;
    }

    public String scheduledList() throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
        FacilioChain scheduleReportListChain = ReadOnlyChainFactory.fetchScheduledReportsChain();
        scheduleReportListChain.execute(context);
        setResult("scheduledReports", context.get(FacilioConstants.ContextNames.REPORT_LIST));

        return SUCCESS;
    }

    public String deleteScheduledReport() throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
        FacilioChain delteScheduleChain = TransactionChainFactory.deleteScheduledReportsChain();
        delteScheduleChain.execute(context);

        setResult(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));

        return SUCCESS;
    }

    public String editScheduledReport() throws Exception {
        FacilioContext context = new FacilioContext();

        context.put(FacilioConstants.Workflow.TEMPLATE, reportInfo.getEmailTemplate());
        context.put(FacilioConstants.ContextNames.SCHEDULE_INFO, reportInfo);

        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
        context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
        FacilioChain mailReportChain = TransactionChainFactory.updateScheduledReportsChain();
        mailReportChain.execute(context);

        setModuleName(reportInfo.getModuleName());
        scheduledList();

        return SUCCESS;
    }

    public String searchReportAndFolders() throws Exception {

        List<ReportFolderContext> reportFolders = ReportUtil.getAllReportFolder(moduleName, false, getSearch(),
                isPivot, null);
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

    private List<PivotRowColumnContext> rows = new ArrayList<PivotRowColumnContext>();

    public List<PivotRowColumnContext> getRows() {
        return rows;
    }

    public void setRows(List<PivotRowColumnContext> rows) {
        this.rows = rows;
    }

    private List<PivotDataColumnContext> data = new ArrayList<PivotDataColumnContext>();

    public List<PivotDataColumnContext> getData() {
        return data;
    }

    public void setData(List<PivotDataColumnContext> data) {
        this.data = data;
    }

    public List<PivotFormulaColumnContext> formula = new ArrayList<>();

    public List<PivotFormulaColumnContext> getFormula() {
        return formula;
    }

    public List<PivotValueColumnContext> getValues() {
        return values;
    }

    public void setValues(List<PivotValueColumnContext> values) {
        this.values = values;
    }

    public List<PivotValueColumnContext> values = new ArrayList<>();

    public void setFormula(List<PivotFormulaColumnContext> formula) {
        this.formula = formula;
    }


    public boolean isBuilderV2() {
        return builderV2;
    }

    public void setBuilderV2(boolean builderV2) {
        this.builderV2 = builderV2;
    }

    private boolean builderV2;

    private JSONObject sortBy;

    public JSONObject getSortBy() {
        return sortBy;
    }

    public void setSortBy(JSONObject sortBy) {
        this.sortBy = sortBy;
    }

    private JSONObject templateJSON;

    public JSONObject getTemplateJSON() {
        return templateJSON;
    }

    public void setTemplateJSON(JSONObject templateJSON) {
        this.templateJSON = templateJSON;
    }


    private long drillDown = -1;

    public long getDrillDown() {
        return drillDown;
    }

    public void setDrillDown(long drillDown) {
        this.drillDown = drillDown;
    }

    public void setPivotResult(FacilioContext context){
        setResult(FacilioConstants.ContextNames.ROW_HEADERS, context.get(FacilioConstants.ContextNames.ROW_HEADERS));
        setResult(FacilioConstants.ContextNames.DATA_HEADERS, context.get(FacilioConstants.ContextNames.DATA_HEADERS));
        setResult(FacilioConstants.ContextNames.FORMULA_HEADERS,
                context.get(FacilioConstants.ContextNames.FORMULA_HEADERS));
        setResult(FacilioConstants.ContextNames.ROW_ALIAS, context.get(FacilioConstants.ContextNames.ROW_ALIAS));
        setResult(FacilioConstants.ContextNames.DATA_ALIAS, context.get(FacilioConstants.ContextNames.DATA_ALIAS));
        setResult(FacilioConstants.ContextNames.PIVOT_TABLE_DATA,
                context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA));
        setResult(FacilioConstants.ContextNames.SORTING, context.get(FacilioConstants.ContextNames.SORTING));
        setResult(FacilioConstants.ContextNames.CRITERIA, context.get(FacilioConstants.ContextNames.CRITERIA));
        setResult(FacilioConstants.ContextNames.PIVOT_TEMPLATE_JSON, context.get(FacilioConstants.ContextNames.TEMPLATE_JSON));
        setResult(FacilioConstants.Reports.ROWS, context.get(FacilioConstants.Reports.ROWS));
        setResult(FacilioConstants.Reports.DATA, context.get(FacilioConstants.ContextNames.DATA));
        setResult(FacilioConstants.ContextNames.VALUES, context.get(FacilioConstants.ContextNames.VALUES));
        setResult(FacilioConstants.ContextNames.FORMULA, context.get(FacilioConstants.ContextNames.FORMULA));
        setResult(FacilioConstants.ContextNames.MODULE_NAME, context.get(FacilioConstants.ContextNames.MODULE_NAME));
        setResult(FacilioConstants.ContextNames.CRITERIA, context.get(FacilioConstants.ContextNames.CRITERIA));
        setResult(FacilioConstants.ContextNames.DATE_FIELD, context.get(FacilioConstants.ContextNames.DATE_FIELD));
        setResult(FacilioConstants.ContextNames.DATE_OPERATOR, context.get(FacilioConstants.ContextNames.DATE_OPERATOR));
        setResult(FacilioConstants.ContextNames.START_TIME, context.get(FacilioConstants.ContextNames.START_TIME));
        setResult(FacilioConstants.ContextNames.END_TIME, context.get(FacilioConstants.ContextNames.END_TIME));
        setResult((String) FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER, context.get(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER));
        setResult(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, context.get(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE));
        if(context.containsKey(FacilioConstants.ContextNames.DATE_OFFSET_VALUE) && context.get(FacilioConstants.ContextNames.DATE_OFFSET_VALUE) != null) {
            setResult(FacilioConstants.ContextNames.DATE_OFFSET_VALUE, context.get(FacilioConstants.ContextNames.DATE_OFFSET_VALUE));
        }
        setResult(FacilioConstants.ContextNames.PIVOT_ALIAS_VS_FIELD,
                context.get(FacilioConstants.ContextNames.PIVOT_ALIAS_VS_FIELD));

        try
        {
            HashMap<String, Object> dataresult = (HashMap<String, Object>) context.get(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA);
            JSONObject sortBy = (JSONObject) context.get(FacilioConstants.ContextNames.SORTING);
            if (sortBy != null && sortBy.containsKey("limit") && dataresult != null && dataresult.containsKey("records")) {
                Long limit = sortBy.get("limit") instanceof Integer ? Long.valueOf((Integer) sortBy.get("limit")) : (Long) sortBy.get("limit");
                String sort_alias = (String) sortBy.get("alias");
                Long order = sortBy.get("order") instanceof Integer ? Long.valueOf((Integer) sortBy.get("order")) : (Long) sortBy.get("order");
                List<LinkedHashMap<String, HashMap>> records_list = (ArrayList<LinkedHashMap<String, HashMap>>) dataresult.get("records");
                if (sort_alias != null && order != null) {
                    HashMap<String, FacilioField> pivotVs_Alias = (HashMap<String, FacilioField>) context.get(FacilioConstants.ContextNames.PIVOT_ALIAS_VS_FIELD);
                    if (pivotVs_Alias != null && pivotVs_Alias.containsKey(sort_alias)) {
                        if (pivotVs_Alias.get(sort_alias) != null && (pivotVs_Alias.get(sort_alias) instanceof LookupField)) {
                            Collections.sort(records_list, new HashMapValueComparator(sort_alias, true, order));
                        } else {
                            Collections.sort(records_list, new HashMapValueComparator(sort_alias, false, order));
                        }
                        if(records_list != null && records_list.size() > 0){
                            int counter=1;
                           for(LinkedHashMap<String, HashMap> record : records_list)
                           {
                               if(record != null && record.containsKey("number"))
                               {
                                   JSONObject formatted_val = new JSONObject();
                                   formatted_val.put("formattedValue", counter++);
                                   record.put("number", formatted_val);
                               }
                           }
                        }
                    }
                }
                if (records_list != null && records_list.size() > limit) {
                    setResult("showLimitBubble", Boolean.TRUE);
                    List<LinkedHashMap> firstNElementsList = records_list.stream().limit(limit).collect(Collectors.toList());
                    dataresult.put("records", firstNElementsList);
                    setResult(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA, dataresult);
                } else {
                    setResult(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA,
                            context.get(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA));
                }
            } else {
                setResult(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA,
                        context.get(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA));
            }
        }
        catch (Exception e)
        {
            LOGGER.info("error while sorting the records");
            setResult(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA,
                    context.get(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA));
        }

        setResult(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_FIELDS, context.get(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_FIELDS));
        setResult(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_OPERATORS, context.get(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN_OPERATORS));
        setResult(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN, context.get(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN));
        setResult(FacilioConstants.ContextNames.PIVOT_LOOKUP_MAP, context.get(FacilioConstants.ContextNames.PIVOT_LOOKUP_MAP));
        setResult(FacilioConstants.ContextNames.PIVOT_EXTENDED_MODULE_IDS, context.get(FacilioConstants.ContextNames.PIVOT_EXTENDED_MODULE_IDS));
    }

    public String fetchTabularReportData() throws Exception {
        FacilioChain c = ReadOnlyChainFactory.fetchPivotReportChain();
        FacilioContext context = c.getContext();

        context.put(FacilioConstants.Reports.ROWS, rows);
        context.put(FacilioConstants.Reports.DATA, data);
        context.put(FacilioConstants.ContextNames.FORMULA, formula);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
        context.put(FacilioConstants.ContextNames.SORTING, sortBy);
        context.put(FacilioConstants.ContextNames.TEMPLATE_JSON, templateJSON);
        context.put(FacilioConstants.ContextNames.DATE_FIELD, dateFieldId);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateValue);
        context.put(FacilioConstants.ContextNames.DATE_OFFSET_VALUE, dateOffset);
        context.put(FacilioConstants.ContextNames.START_TIME, startTime);
        context.put(FacilioConstants.ContextNames.END_TIME, endTime);
        context.put(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER, showTimelineFilter);
        context.put(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN, drillDown);
        context.put(FacilioConstants.ContextNames.VALUES, values);
        context.put(FacilioConstants.ContextNames.IS_BUILDER_V2, isBuilderV2());
        c.execute();

        setResult("report", reportContext);
        setPivotResult(context);

        return SUCCESS;
    }

    public String savePivotReport() throws Exception {
        FacilioChain chain = TransactionChainFactory.addOrUpdatePivotReport();
        FacilioContext context = new FacilioContext();

        context.put(FacilioConstants.Reports.ROWS, rows);
        context.put(FacilioConstants.Reports.DATA, data);
        context.put(FacilioConstants.ContextNames.VALUES, values);
        context.put(FacilioConstants.ContextNames.FORMULA, formula);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
        context.put(FacilioConstants.ContextNames.SORTING, sortBy);
        context.put(FacilioConstants.ContextNames.TEMPLATE_JSON, templateJSON);
        context.put(FacilioConstants.ContextNames.DATE_FIELD, dateFieldId);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateValue);
        context.put(FacilioConstants.ContextNames.DATE_OFFSET_VALUE, dateOffset);
        context.put(FacilioConstants.ContextNames.START_TIME, startTime);
        context.put(FacilioConstants.ContextNames.END_TIME, endTime);
        context.put(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER, showTimelineFilter);
        context.put(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN, drillDown);
        context.put(FacilioConstants.ContextNames.IS_BUILDER_V2, isBuilderV2());

        ReportPivotParamsContext pivotparams = new ReportPivotParamsContext();
        pivotparams.setRows(rows);
        pivotparams.setData(data);
        pivotparams.setValues(values);
        pivotparams.setModuleName(moduleName);
        pivotparams.setCriteria(criteria);
        pivotparams.setSortBy(sortBy);
        pivotparams.setTemplateJSON(templateJSON);
        pivotparams.setDateFieldId(dateFieldId);
        pivotparams.setDateOperator(dateOperator);
        pivotparams.setDateValue(dateValue);
        pivotparams.setStartTime(startTime);
        pivotparams.setEndTime(endTime);
        pivotparams.setFormula(formula);
        pivotparams.setDrillDown(drillDown);
        pivotparams.setBuilderV2(isBuilderV2());
        pivotparams.setShowTimelineFilter(getShowTimelineFilter());
        pivotparams.setDateOffset(dateOffset);


        if (reportContext == null) {
            reportContext = new ReportContext();
        }

        if (reportId > 0) {
            ReportContext report = ReportUtil.getReport(reportId);
            context.put(FacilioConstants.ContextNames.REPORT_ID, report.getId());
            reportContext.setId(report.getId());
        }
        reportContext.setAppId(AccountUtil.getCurrentUser().getApplicationId());
        reportContext.setTabularState(FieldUtil.getAsJSON(pivotparams, true).toJSONString());
        reportContext.setType(ReportType.PIVOT_REPORT);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);

        chain.execute(context);

        String log_message= reportId > 0 ? "Report {%s} has been updated for {%s} Module." : "Report {%s} has been created for {%s} Module.";
        setReportAuditLogs((String) context.get("ModuleDisplayName"), reportContext, log_message, reportId > 0 ? AuditLogHandler.ActionType.UPDATE : AuditLogHandler.ActionType.ADD);
        setResult("message", "Report saved");
        setResult("report", reportContext);

        return SUCCESS;
    }
    public void setReportAuditLogs(String moduleDisplayName, ReportContext reportContext, String log_message, AuditLogHandler.ActionType actionType) throws Exception
    {
        Boolean isCustomModule= reportContext.getModule().getCustom();
        long moduleId = reportContext.getModule().getModuleId();
        String moduleName = reportContext.getModule().getName();
        AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format(log_message, reportContext.getName(), moduleDisplayName), reportContext.getDescription(), AuditLogHandler.RecordType.MODULE, moduleName, reportContext.getId())
                .setActionType(actionType)
                .setLinkConfig(((Function<Void, String>) o -> {
                    JSONArray array = new JSONArray();
                    JSONObject json = new JSONObject();
                    json.put("reportId", reportContext.getId());
                    json.put("moduleName", moduleName);
                    json.put("moduleId", moduleId);
                    json.put("reportType", reportContext.getType());
                    json.put("moduleType", isCustomModule);
                    array.add(json);
                    return array.toJSONString();
                }).apply(null));
        AuditLogUtil.sendAuditLogs(auditLog);
    }
    public String exportPivotReport() throws Exception {
        FacilioChain c = ReadOnlyChainFactory.fetchPivotReportChain();
        c.addCommand(new ExportPivotReport());

        FacilioContext context = c.getContext();

        ReportContext reportContext = ReportUtil.getReport(reportId);
        if (reportContext == null) {
            throw new Exception("Report not found");
        }
        context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, reportContext.getModule().getName());
        if (startTime != -1 && endTime != -1) {
            reportContext.setDateRange(new DateRange(startTime, endTime));
        }

        JSONParser parser = new JSONParser();
        ReportPivotParamsContext pivotparams = FieldUtil.getAsBeanFromJson(
                (JSONObject) parser.parse(reportContext.getTabularState()), ReportPivotParamsContext.class);
        context.put(FacilioConstants.Reports.ROWS, pivotparams.getRows());
        context.put(FacilioConstants.Reports.DATA, pivotparams.getData());
        context.put(FacilioConstants.ContextNames.VALUES, pivotparams.getValues());
        context.put(FacilioConstants.ContextNames.FORMULA, pivotparams.getFormula());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, pivotparams.getModuleName());
        context.put(FacilioConstants.ContextNames.CRITERIA, pivotparams.getCriteria());
        context.put(FacilioConstants.ContextNames.SORTING, pivotparams.getSortBy());
        context.put(FacilioConstants.ContextNames.TEMPLATE_JSON, pivotparams.getTemplateJSON());
        context.put(FacilioConstants.ContextNames.DATE_FIELD, pivotparams.getDateFieldId());
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR, pivotparams.getDateOperator());
        context.put(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER, pivotparams.getShowTimelineFilter());
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, pivotparams.getDateValue());
        context.put(FacilioConstants.ContextNames.IS_EXPORT_REPORT, true);
        context.put(FacilioConstants.ContextNames.IS_BUILDER_V2, pivotparams.isBuilderV2());

        if (getStartTime() > 0 && getEndTime() > 0) {
            context.put(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED, true);
            context.put(FacilioConstants.ContextNames.START_TIME, getStartTime());
            context.put(FacilioConstants.ContextNames.END_TIME, getEndTime());
        } else {
            context.put(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED, false);
            context.put(FacilioConstants.ContextNames.START_TIME, pivotparams.getStartTime());
            context.put(FacilioConstants.ContextNames.END_TIME, pivotparams.getEndTime());
        }

        if (getFilters() != null) {
            c.addCommand(new GenerateCriteriaFromFilterCommand());
            JSONObject filter = (JSONObject) parser.parse(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, filter);
        }

        c.execute();

        setResult("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));
        return SUCCESS;
    }

    public String executePivotReport() throws Exception {
        try {
            FacilioChain chain = ReadOnlyChainFactory.fetchPivotReportChain();
            FacilioContext context = new FacilioContext();

            ReportContext reportContext = ReportUtil.getReport(reportId);
            if (reportContext == null) {
                throw new Exception("Report not found");
            }
            context.put(FacilioConstants.ContextNames.REPORT, reportContext);
            context.put(FacilioConstants.ContextNames.MODULE_NAME, reportContext.getModule().getName());
            if (startTime != -1 && endTime != -1) {
                reportContext.setDateRange(new DateRange(startTime, endTime));
            }
            JSONParser parser = new JSONParser();
            ReportPivotParamsContext pivotparams = FieldUtil.getAsBeanFromJson(
                    (JSONObject) parser.parse(reportContext.getTabularState()), ReportPivotParamsContext.class);

            if (getFilters() != null) {
                JSONObject filter = (JSONObject) parser.parse(getFilters());
                context.put(FacilioConstants.ContextNames.FILTERS, filter);
            }

            context.put(FacilioConstants.Reports.ROWS, pivotparams.getRows());
            context.put(FacilioConstants.Reports.DATA, pivotparams.getData());
            context.put(FacilioConstants.ContextNames.VALUES, pivotparams.getValues());
            context.put(FacilioConstants.ContextNames.FORMULA, pivotparams.getFormula());
            context.put(FacilioConstants.ContextNames.MODULE_NAME, pivotparams.getModuleName());
            context.put(FacilioConstants.ContextNames.CRITERIA, pivotparams.getCriteria());
            context.put(FacilioConstants.ContextNames.SHOW_TIME_LINE_FILTER, pivotparams.getShowTimelineFilter());
            context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, pivotparams.getDateValue());
            context.put(FacilioConstants.ContextNames.TEMPLATE_JSON, pivotparams.getTemplateJSON());
            context.put(FacilioConstants.ContextNames.DATE_FIELD, pivotparams.getDateFieldId());
            context.put(FacilioConstants.ContextNames.DATE_OPERATOR, pivotparams.getDateOperator());
            context.put(FacilioConstants.ContextNames.PIVOT_DRILL_DOWN, pivotparams.getDrillDown());
            context.put(FacilioConstants.ContextNames.IS_BUILDER_V2, pivotparams.isBuilderV2());
            context.put(FacilioConstants.ContextNames.DATE_OFFSET_VALUE, pivotparams.getDateOffset());

            if (sortBy != null) {
                context.put(FacilioConstants.ContextNames.SORTING, sortBy);
            } else {
                context.put(FacilioConstants.ContextNames.SORTING, pivotparams.getSortBy());
            }
            if (getStartTime() > 0 && getEndTime() > 0) {
                context.put(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED, true);
                context.put(FacilioConstants.ContextNames.START_TIME, getStartTime());
                context.put(FacilioConstants.ContextNames.END_TIME, getEndTime());
            } else {
                context.put(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED, false);
                context.put(FacilioConstants.ContextNames.START_TIME, pivotparams.getStartTime());
                context.put(FacilioConstants.ContextNames.END_TIME, pivotparams.getEndTime());
            }

            chain.execute(context);

            setResult("report", reportContext);
            setPivotResult(context);
        }
        catch(Exception e){
            LOGGER.info("Error Occurred in executePivotReport",e);
            throw new Exception("Error occurred in executePivotReport",e);
        }
        return SUCCESS;
    }

    private int scatterGraphId;
    private String scatterGraphLabel;
    private String scatterGraphValue;
    private String scatterGraphAction;

    public int getScatterGraphId() {
        return scatterGraphId;
    }

    public void setScatterGraphId(int scatterGraphId) {
        this.scatterGraphId = scatterGraphId;
    }

    public String getScatterGraphLabel() {
        return scatterGraphLabel;
    }

    public void setScatterGraphLabel(String scatterGraphLabel) {
        this.scatterGraphLabel = scatterGraphLabel;
    }

    public String getScatterGraphValue() {
        return scatterGraphValue;
    }

    public void setScatterGraphValue(String scatterGraphValue) {
        this.scatterGraphValue = scatterGraphValue;
    }

    public String getScatterGraphAction() {
        return scatterGraphAction;
    }

    public void setScatterGraphAction(String scatterGraphAction) {
        this.scatterGraphAction = scatterGraphAction;
    }

    public String scatterLineGraph() throws Exception {
        // setResult("result","success");
        FacilioModule module = ModuleFactory.getScatterGraphLineModule();
        FacilioContext context = new FacilioContext();
        // FacilioChain chain = FacilioChain.
        if (getScatterGraphAction() != null
                && (getScatterGraphAction().equals("ADD") || getScatterGraphAction().equals("MODIFY"))) {
            System.out.println("Add or update data to database");
            if (getScatterGraphValue() != null && getScatterGraphLabel() != null) {
                FacilioChain chain = TransactionChainFactory.addOrUpdateScatterGraph();
                context.put(FacilioConstants.ContextNames.SCATTER_GRAPH_ID, getScatterGraphId());
                context.put(FacilioConstants.ContextNames.SCATTER_GRAPH_LABEL, getScatterGraphLabel());
                context.put(FacilioConstants.ContextNames.SCATTER_GRAPH_VALUE, getScatterGraphValue());
                chain.execute(context);
                setResult("id", context.get(FacilioConstants.ContextNames.SCATTER_GRAPH_RESULT));
            }

        } else if (getScatterGraphAction() != null && getScatterGraphAction().equals("DELETE")) {
            System.out.println("Delete data in database");
            FacilioChain chain = TransactionChainFactory.deleteScatterGraph();
            context.put(FacilioConstants.ContextNames.SCATTER_GRAPH_ID, getScatterGraphId());
            chain.execute(context);
            setResult("result", "Success");
        } else if (getScatterGraphAction() != null && getScatterGraphAction().equals("GET_BY_ID")) {
            System.out.println("Get data from database by Id");
            context.put(FacilioConstants.ContextNames.SCATTER_GRAPH_ID, getScatterGraphId());
            FacilioChain chain = ReadOnlyChainFactory.getScatterGraphById();
            chain.execute(context);
            setResult("result", context.get(FacilioConstants.ContextNames.SCATTER_GRAPH_RESULT));
        } else {
            System.out.println("Get data from database");
            FacilioChain chain = ReadOnlyChainFactory.getScatterGraph();
            chain.execute(context);
            setResult("result", context.get(FacilioConstants.ContextNames.SCATTER_GRAPH_RESULT));
        }
        return SUCCESS;
    }

    private boolean showTimelineFilter;

    public boolean getShowTimelineFilter() {
        return showTimelineFilter;
    }

    public void setShowTimelineFilter(boolean showTimelineFilter) {
        this.showTimelineFilter = showTimelineFilter;
    }

    private long dateFieldId = -1;

    public long getDateFieldId() {
        return dateFieldId;
    }

    public void setDateFieldId(long dateFieldId) {
        this.dateFieldId = dateFieldId;
    }

    String dateValue;

    public Integer getDateOffset() {
        return dateOffset;
    }

    public void setDateOffset(Integer dateOffset) {
        this.dateOffset = dateOffset;
    }

    public Integer dateOffset;
    public String getDateValue() {
        return dateValue;
    }

    public void setDateValue(String dateValue) {
        this.dateValue = dateValue;
    }
}
class HashMapValueComparator implements Comparator<LinkedHashMap<String, HashMap>> {
    String alias;
    boolean isLookupField;
    Long sortOrder;


    public HashMapValueComparator(String alias, boolean isLookupField, Long sortOrder)
    {
        this.alias = alias;
        this.isLookupField = isLookupField;
        this.sortOrder = sortOrder;
    }

    @Override
    public int compare(LinkedHashMap<String, HashMap> map1, LinkedHashMap<String, HashMap> map2) {

        HashMap  alias_object1 = map1.get(alias);
        HashMap  alias_object2 = map2.get(alias);

        Object value1, value2;
        if(isLookupField )
        {
            value1 = alias_object1.get("formattedValue");
            value2 = alias_object2.get("formattedValue");
        }else{
            value1 = alias_object1.get("value");
            value2 = alias_object2.get("value");
        }
        Comparator<String> ascComparator = Comparator.naturalOrder();
        Comparator<String> descComparator = Comparator.naturalOrder();
        if (value1 == null && value2 == null) {
            return 0;
        } else if (value1 == null) {
            return -1; // obj1 is considered less than obj2
        } else if (value2 == null) {
            return 1; // obj1 is considered greater than obj2
        } else {
            try {
                Double number1 = Double.parseDouble(value1.toString());
                Double number2 = Double.parseDouble(value2.toString());
                Comparator<Double> doubleAscComparator = Comparator.naturalOrder();
                Comparator<Double> doubleDescComparator = Comparator.naturalOrder();
                return sortOrder == 3 ? doubleAscComparator.compare(number1, number2) : doubleDescComparator.compare(number2, number1);
            } catch (Exception e) {
                String str1 = value1.toString();
                String str2 = value2.toString();
                return sortOrder == 3 ? ascComparator.compare(str1, str2) : descComparator.compare(str2, str1);
            }
        }
    }
}