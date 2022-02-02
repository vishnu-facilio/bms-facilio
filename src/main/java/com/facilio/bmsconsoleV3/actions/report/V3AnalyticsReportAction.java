package com.facilio.bmsconsoleV3.actions.report;

import com.facilio.bmsconsole.context.RegressionContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.*;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.wmsv2.handler.AuditLogHandler;
import com.facilio.workflows.context.WorkflowContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter @Setter
@Log4j
public class V3AnalyticsReportAction extends V3Action {

    private static final long serialVersionUID = 1L;
    private String baseLines;
    private String fields;
    private long startTime = -1;
    private long endTime = -1;
    private List<Long> assetCategory;
    private List<Long> spaceId;
    private List<Long> parentId;
    private ResourceContext alarmResource;
    private Boolean showAlarms;
    private int analyticsType = -1;
    private Boolean showSafeLimit;
    private String hmAggr = null;
    private WorkflowContext transformWorkflow;
    private boolean newFormat = false;
    private boolean defaultDate = false;
    public String trendLine;
    private String timeFilter;
    private String dataFilter;
    private List<RegressionContext> regressionConfig;
    private String scatterConfig;
    long alarmId = -1;
    private ReportContext reportContext;
    private ReportTemplateContext template;
    private String regressionType;
    Integer dateOperator;
    String dateOperatorValue;
    public String chartState;
    public String tabularState;
    private int moduleType = -1;
    FacilioContext resultContext;
    String moduleName;


    private AggregateOperator xAggr;
    private ReadingAnalysisContext.ReportMode mode = ReadingAnalysisContext.ReportMode.TIMESERIES;
    private ReadingAnalysisContext.ReportFilterMode filterMode;
    private ReadingAnalysisContext.ReportFilterMode xCriteriaMode;
    private AggregateOperator groupByTimeAggr;
    public int getxAggr() {
        return xAggr != null ? xAggr.getValue() : -1;
    }
    public void setxAggr(int xAggr) {
        this.xAggr = AggregateOperator.getAggregateOperator(xAggr);
    }
    public int getMode() {
        return mode != null ? mode.getValue() : -1;
    }
    public void setMode(int mode) {
        this.mode = ReadingAnalysisContext.ReportMode.valueOf(mode);
    }
    public int getFilterMode() {
        return filterMode != null ? filterMode.getValue() : -1;
    }
    public void setFilterMode(int filterMode) {
        this.filterMode = ReadingAnalysisContext.ReportFilterMode.valueOf(filterMode);
        this.xCriteriaMode = this.filterMode;
    }
    public int getgroupByTimeAggr() {
        return groupByTimeAggr != null ? groupByTimeAggr.getValue() : -1;
    }
    public void setgroupByTimeAggr(int groupByTimeAggr) {
        this.groupByTimeAggr = AggregateOperator.getAggregateOperator(groupByTimeAggr);
    }
    public void setxCriteriaMode(int xCriteriaMode) {
        this.xCriteriaMode = ReadingAnalysisContext.ReportFilterMode.valueOf(xCriteriaMode);
        this.filterMode = this.xCriteriaMode;
    }
    public int getxCriteriaMode() {
        return xCriteriaMode != null ? xCriteriaMode.getValue() : -1;
    }

    private void setReadingsDataContext(FacilioContext context) throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray fieldArray = (JSONArray) parser.parse(fields);
        JSONArray baseLineList = null;
        if (baseLines != null && !baseLines.isEmpty()) {
            baseLineList = (JSONArray) parser.parse(baseLines);
        }
        if (mode.equals(ReadingAnalysisContext.ReportMode.SERIES)) {
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
        context.put(FacilioConstants.ContextNames.DEFAULT_DATE, defaultDate);
        context.put(FacilioConstants.ContextNames.ANALYTICS_TYPE, analyticsType);
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

        context.put(FacilioConstants.ContextNames.ALARM_ID, alarmId);

        context.put(FacilioConstants.ContextNames.TIME_FILTER, timeFilter);
        context.put(FacilioConstants.ContextNames.DATA_FILTER, dataFilter);

    }
    private void setAnalyticsReportContext(FacilioContext context) throws Exception {
        if (reportContext.getTemplate() != null) {
            setTemplate(reportContext.getReportTemplate());
        }
        if (reportContext != null && regressionConfig != null && regressionConfig.size() != 0 && regressionType != null) {
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
    }
    private void setAnalyticsChartInfo(FacilioContext context) throws Exception
    {
        context.put(FacilioConstants.ContextNames.REPORT_TEMPLATE, template);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateOperatorValue);
        context.put(FacilioConstants.ContextNames.CHART_STATE, chartState);
        context.put(FacilioConstants.ContextNames.TABULAR_STATE, tabularState);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
    }
    public String create() throws Exception {
        validateData(1);
        FacilioChain addReadingReport = TransactionChainFactoryV3.getCreateOrUpdateAnalyticsReportChain();
        FacilioContext context = addReadingReport.getContext();
        setReadingsDataContext(context);
        setAnalyticsReportContext(context);
        setAnalyticsChartInfo(context);
        addReadingReport.execute();
        AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format("Analytics Report {%s} has been successfully created.", reportContext.getName()), reportContext.getDescription(), "", AuditLogHandler.RecordType.MODULE, moduleName, reportContext.getId());
        sendAuditLogs(auditLog);
        return setReportResult(context);
    }
    public String update() throws Exception{
        validateData(2);
        FacilioChain addReadingReport = TransactionChainFactoryV3.getCreateOrUpdateAnalyticsReportChain();
        FacilioContext context = addReadingReport.getContext();
        setReadingsDataContext(context);
        setAnalyticsReportContext(context);
        setAnalyticsChartInfo(context);
        addReadingReport.execute();
        AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format("Analytics Report {%s} has been successfully updated.", reportContext.getName()), reportContext.getDescription(), "", AuditLogHandler.RecordType.MODULE, moduleName, reportContext.getId());
        sendAuditLogs(auditLog);
        return setReportResult(context);
    }

    private String setReportResult(FacilioContext context)
    {
        if (context.get(FacilioConstants.ContextNames.REPORT) != null) {

            reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
            if (alarmId > 0) {
                reportContext.setDateOperator(DateOperators.CURRENT_N_DAY.getOperatorId());
            }
            setData("report", reportContext);
        }
        setData("reportXValues", context.get(FacilioConstants.ContextNames.REPORT_X_VALUES)); //This can be removed from new format
        setData("reportData", context.get(FacilioConstants.ContextNames.REPORT_DATA));
        setData("reportVarianceData", context.get(FacilioConstants.ContextNames.REPORT_VARIANCE_DATA));
        setData("safeLimits", context.get(FacilioConstants.ContextNames.REPORT_SAFE_LIMIT));
        setData(FacilioConstants.ContextNames.REPORT_ALARM_CONTEXT, context.get(FacilioConstants.ContextNames.REPORT_ALARM_CONTEXT));

        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        if (module != null) {
            setData("module", module);
        }
        if (moduleType != -1 || reportContext.getModuleType() != -1) {
            setData("moduleTypes", ReportFactoryFields.addModuleTypes(module.getName()));
        }
        if (context.containsKey("criteriaData")) {
            setData("criteriaData", context.get("criteriaData"));
        }

        if (context.containsKey("baselineData")) {
            setData("baselineData", context.get("baselineData"));
        }
        if (context.containsKey("baselineDataColors")) {
            setData("baselineDataColors", context.get("baselineDataColors"));
        }
        resultContext = context;
        return SUCCESS;
    }
    private void validateData(Integer action_type) throws Exception
    {
        if((action_type == 1 && reportContext.getId() > 0 ) && (action_type == 2 && reportContext.getId() <= 0))
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid ReportId.");
        }
        if(reportContext == null)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ReportContext is mandatory.");
        }
    }
}
