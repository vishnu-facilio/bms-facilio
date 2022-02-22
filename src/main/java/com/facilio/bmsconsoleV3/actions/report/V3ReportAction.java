package com.facilio.bmsconsoleV3.actions.report;

import java.util.*;
import java.util.function.Function;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.report.context.*;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.wmsv2.handler.AuditLogHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.ReportContext.ReportType;

@Setter @Getter
@Log4j
public class V3ReportAction extends V3Action {

    private static final long serialVersionUID = 1L;
    private ReportContext reportContext;
    private long reportId = -1;
    public String chartState;
    public String tabularState;
    private int moduleType = -1;
    String moduleName;
    private JSONObject dateField;
    private String baseLines;
    private JSONObject xField;
    private Integer reportType;
    private int limit = -1;
    private int sortOrder;
    private JSONArray sortFields;
    private Criteria criteria;
    private JSONArray groupBy;
    private JSONArray yField;
    private JSONArray having;
    public boolean deleteWithWidget;
    private long startTime = -1;
    private long endTime = -1;
    private boolean needCriteriaData = false;
    private ReportDrilldownParamsContext drilldownParams;
    long alarmId = -1;
    FacilioContext resultContext;
    private Boolean isPivot = false;

    public JSONArray getyField(JSONArray yField) { return yField; }
    public void setyField(JSONArray yField) {
        this.yField = yField;
    }
    public JSONObject getxField() { return xField; }
    public void setxField(JSONObject xField) {
        this.xField = xField;
    }
    private List<ReportUserFilterContext> userFilters;
    private List<ReportDrilldownPathContext> reportDrilldownPath;
    private ReportSettings reportSettings;
    private Boolean isWithReport;

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
    public Boolean getIsWithReport() {
        return isWithReport == null? true: isWithReport;
    }
    public void setIsWithReport(Boolean isWithReport) {
        this.isWithReport = isWithReport;
    }

    private void updateChainContext(FacilioContext context) throws Exception {
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
        context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, FieldUtil.getAsBeanListFromJsonArray(baseLineList, ReportBaseLineContext.class));

        context.put(FacilioConstants.Reports.MODULE_TYPE, moduleType);

        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
    }
    private void updateModuleReportContext() throws Exception
    {
        reportContext.setChartState(chartState);
        reportContext.setTabularState(tabularState);
        reportContext.setType(ReportType.WORKORDER_REPORT);
        reportContext.setModuleType(moduleType);
        reportContext.setReportDrilldownPath(getReportDrilldownPath());
        reportContext.setReportSettings(getReportSettings());
        if (dateField != null && (Boolean) dateField.get(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA) != null) {
            JSONObject reportState;
            if (reportContext.getReportState() != null && !reportContext.getReportState().isEmpty()) {
                reportState = reportContext.getReportState();
            } else {
                reportState = new JSONObject();
            }
            reportState.put(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA, (Boolean) dateField.get(FacilioConstants.ContextNames.ALLOW_FUTURE_DATA));
            reportContext.setReportState(reportState);
        }
    }
    public String create() throws Exception {
        validateData(1);
        FacilioChain chain = TransactionChainFactoryV3.getCreateOrUpdateReportChain();
        FacilioContext context = chain.getContext();
        createOrUpdateReport(chain, context);
        String log_message="Report {%s} has been created for {%s} Module.";
        setReportAuditLogs((String) context.get("ModuleDisplayName"), reportContext, log_message, AuditLogHandler.ActionType.ADD);
        setMessage("Report created successfully!");
        return SUCCESS;
    }

    public String update() throws Exception {
        validateData(2);
        FacilioChain chain = TransactionChainFactoryV3.getCreateOrUpdateReportChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.REPORT_ID,reportContext.getId());
        createOrUpdateReport(chain, context);
        String log_message="Report {%s} has been updated for {%s} Module.";
        setReportAuditLogs((String)context.get("ModuleDisplayName"), reportContext, log_message, AuditLogHandler.ActionType.UPDATE);
        setMessage("Report updated successfully!");
        return SUCCESS;
    }

    public String delete() throws Exception
    {
        validateData(3);
        FacilioChain chain = TransactionChainFactoryV3.getDeleteReportChain();
        FacilioContext context = chain.getContext();
        context.put("isDeleteWithWidget", deleteWithWidget);
        context.put("reportId", reportId);
        chain.execute();
        if(!context.get("success").equals("success")) {
            setData("errorString", "Report Used In Dashboard");
        }else{
            String log_message="Report {%s} has been deleted for {%s} Module.";
            if(context.get("moduleName") == null || context.get("moduleName").equals("energydata")){
                log_message="Analytics Report {%s} has been deleted.";
                AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format(log_message, (String)context.get("reportName")), "", AuditLogHandler.RecordType.MODULE, (String) context.get("moduleName"), reportId);
            }
            AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format(log_message, (String)context.get("reportName"), (String) context.get("moduleName")), "", AuditLogHandler.RecordType.MODULE, (String) context.get("moduleName"), reportId);
            sendAuditLogs(auditLog);
            setData("success","deleted successfully");
        }
        setMessage("Report Deleted Successfully");
        return SUCCESS;
    }

    private void setExecuteReportContextData(FacilioContext context) throws Exception
    {
        if(getFilters() != null){
            JSONParser parser = new JSONParser();
            JSONObject filter = (JSONObject) parser.parse(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, filter);
        }
        FacilioChain chain = TransactionChainFactoryV3.getReportContextChain();
        FacilioContext reportDetailContext = chain.getContext();
        reportDetailContext.put("reportId", reportId);
        chain.execute();
        ReportContext reportContext = (ReportContext) reportDetailContext.get("reportContext");
        if(reportContext != null){
            context.put(FacilioConstants.ContextNames.REPORT, reportContext);
            context.put(FacilioConstants.ContextNames.MODULE_NAME, reportContext.getModule().getName());
            if (startTime != -1 && endTime != -1) {
                reportContext.setDateRange(new DateRange(startTime, endTime));
                reportContext.setDateOperator(DateOperators.BETWEEN);
            }
            reportContext.setUserFilters(userFilters, true);
        }
        else{
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Report.");
        }
        context.put(FacilioConstants.ContextNames.REPORT_DRILLDOWN_PARAMS, getDrilldownParams());
    }
    public String execute() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getExecuteReportChain( getFilters() , needCriteriaData);
        FacilioContext context = chain.getContext();
        setExecuteReportContextData(context);
        chain.execute();
        setData("result", "Success");
        setMessage("Report Executed Successfully");
        return setReportResult(context);
    }
    private void createOrUpdateReport(FacilioChain chain, FacilioContext context) throws Exception
    {
        updateModuleReportContext();
        updateChainContext(context);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);
        chain.execute();
        setData("report", reportContext);
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
        if ((action_type == 1 && reportId > 0) || (action_type == 3 && reportId <= 0) ) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid ReportId.");
        }
        if (action_type != 3 && reportContext == null )
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ReportContext is mandatory.");
        }
        if(action_type != 3 && StringUtils.isEmpty(moduleName)){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ModuleName is mandatory.");
        }

    }

    public String folders() throws Exception
    {
        FacilioChain chain = TransactionChainFactoryV3.getFoldersListChain();
        FacilioContext context = chain.getContext();
        context.put("isWithReport", getIsWithReport());
        context.put("isCustomModule", false);
        if(moduleName != null && moduleName.equals("custommodule")){
            context.put("isCustomModule", true);
        }else{
            context.put("moduleName", moduleName);
            context.put("isPivot", isPivot);
        }
        chain.execute();
        List<ReportFolderContext> reportFolders = (List<ReportFolderContext>)context.get("folders");
        setData("reportFolders", reportFolders);
        if(moduleName != null && !moduleName.equals("custommodule")){
            setData("moduleName", moduleName);
        }
        return SUCCESS;
    }

    public String getFields() throws Exception
    {
        if(moduleName == null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ModuleName is mandatory.");
        }
        FacilioChain chain = TransactionChainFactoryV3.getReportFieldsChain();
        FacilioContext context = chain.getContext();
        context.put("moduleName", moduleName);
        chain.execute();
        JSONObject reportFields = (JSONObject) context.get("reportFields");
        setData("meta", reportFields);
        return SUCCESS;
    }

    public String subModulesList() throws Exception {
        if(moduleName == null)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ModuleName is mandatory.");
        }
        FacilioChain chain = TransactionChainFactoryV3.getSubModulesListChain();
        FacilioContext context = chain.getContext();
        context.put("moduleName", moduleName);
        chain.execute();
        Set<FacilioModule> subModulesList = (Set<FacilioModule>) context.get("subModulesList");
        if(subModulesList == null){
            subModulesList = new HashSet<>();
        }
        setData("modules", subModulesList);
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
        sendAuditLogs(auditLog);
    }
}