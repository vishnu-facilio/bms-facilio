package com.facilio.bmsconsoleV3.actions.report;

import java.util.*;
import java.util.function.Function;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ConstructReportData;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReportInfo;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.report.V3DashboardRuleDPContext;
import com.facilio.bmsconsoleV3.context.report.V3DashboardRuleReportActionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.report.context.*;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.time.DateRange;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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
    private long moduleId;
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
    private Long webTabId;
    private ReportFolderContext reportFolder;
    private long folderId = -1;
    private long appId = -1;
    private FileInfo.FileFormat fileFormat;
    private Boolean showAlarms;
    private int analyticsType = -1;
    private Boolean showSafeLimit;
    private String scatterConfig;
    private String searchText;
    private long dashboardId;
    private String hmAggr = null;
    private boolean newFormat = false;
    private EMailTemplate emailTemplate;
    private List<ReportUserFilterContext> userFilters;
    private List<ReportDrilldownPathContext> reportDrilldownPath;
    private ReportSettings reportSettings;
    private Boolean isWithReport;
    private String chartType;
    private Map<String, Object> exportParams;
    private Map<String, Object> renderParams;
    private AggregateOperator xAggr;
    private AggregateOperator groupByTimeAggr;
    private ReportTemplateContext template;
    private List<Long> ids;
    private int page;
    private int perPage;
    private boolean withCount;
    private String orderType;



    public List<SingleSharingContext> getReportShareInfo() {
        return reportShareInfo;
    }

    public void setReportShareInfo(List<SingleSharingContext> reportShareInfo) {
        this.reportShareInfo = reportShareInfo;
    }

    public List<SingleSharingContext> reportShareInfo;
    ReportInfo reportInfo;
    public V3DashboardRuleReportActionContext ruleInfo;

    public JSONArray getyField(JSONArray yField) { return yField; }
    public void setyField(JSONArray yField) {
        this.yField = yField;
    }
    public JSONObject getxField() { return xField; }
    public void setxField(JSONObject xField) {
        this.xField = xField;
    }

    public int getgroupByTimeAggr() {
        return groupByTimeAggr != null ? groupByTimeAggr.getValue() : -1;
    }
    public void setgroupByTimeAggr(int groupByTimeAggr) {
        this.groupByTimeAggr = AggregateOperator.getAggregateOperator(groupByTimeAggr);
    }
    public int getxAggr() {
        return xAggr != null ? xAggr.getValue() : -1;
    }
    public void setxAggr(int xAggr) {
        this.xAggr = AggregateOperator.getAggregateOperator(xAggr);
    }

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
    public int getFileFormat() {
        return  fileFormat != null ? fileFormat.getIntVal() : -1;
    }
    public void setFileFormat(int fileFormat) {
        this.fileFormat = FileInfo.FileFormat.getFileFormat(fileFormat);
    }
    public EMailTemplate getEmailTemplate() {
        return emailTemplate;
    }
    public void setEmailTemplate(EMailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    public void updateChainContext(FacilioContext context) throws Exception {
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

        if(chartState != null && !chartState.equals("null"))
        {
            try
            {
                JSONObject data_point_vs_convert_to_unit = null;
                JSONObject chart_json = (JSONObject) parser.parse(chartState);
                if (chart_json != null && chart_json.containsKey("dataPoints")) {
                    data_point_vs_convert_to_unit = new JSONObject();
                    JSONArray data_point_arr = (JSONArray) chart_json.get("dataPoints");
                    int len = data_point_arr.size();
                    for (int i = 0; i < len; i++) {
                        JSONObject dataPoint = (JSONObject) data_point_arr.get(i);
                        if (dataPoint != null && dataPoint.containsKey("convertTounit") && dataPoint.get("convertTounit") != null &&  dataPoint.containsKey("fieldName")) {
                            data_point_vs_convert_to_unit.put((String) dataPoint.get("fieldName"), dataPoint.get("convertTounit"));
                        }
                    }
                    if (data_point_vs_convert_to_unit != null && !data_point_vs_convert_to_unit.isEmpty()) {
                        context.put("datapoint_vs_convert_unit", data_point_vs_convert_to_unit);
                    }
                }
            }
            catch(Exception e)
            {
                LOGGER.debug("Errot while forming the convertounit map");
            }
        }

    }

    public void updateReportShareContext(FacilioContext context) throws Exception{
        context.put("reportId", reportId);
        context.put("reportShareInfo",reportShareInfo);
        context.put("isCreate",true);
    }
    private void updateModuleReportContext() throws Exception
    {
        reportContext.setChartState(chartState);
        reportContext.setAppId(AccountUtil.getCurrentApp().getId());
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

    public String share() throws Exception {
        if(reportId == -1 || reportId <=0 ){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Report.");
        }
        try
        {
            FacilioChain chain = TransactionChainFactoryV3.getreportShareChain();
            FacilioContext context = chain.getContext();
            updateReportShareContext(context);
            chain.execute();
        }
        catch(Exception e)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Error while sharing report");
        }
        return SUCCESS;
    }

    public String getReportShare() throws Exception{
        if(reportId ==-1 || reportId<=0){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Report.");
        }
        try{

            FacilioChain chain = TransactionChainFactoryV3.getreportShareChain();
            FacilioContext context = chain.getContext();
            context.put("reportId",reportId);
            context.put("isGet",true);
            chain.execute();
            if(context.containsKey("reportShareDetails")){
                setData("reportShareDetails",context.get("reportShareDetails"));
            }
        }
        catch(Exception e){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Error while getting report share details");
        }
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
            if(context.get("moduleName") != null) {
                AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format(log_message, (String) context.get("reportName"), (String) context.get("moduleName")), "", AuditLogHandler.RecordType.MODULE, (String) context.get("moduleName"), reportId);
                sendAuditLogs(auditLog);
            }
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
        if(ruleInfo != null && ruleInfo.getCriteria() != null)
        {
            LOGGER.debug("CRITERIA INFO FOR DASHBOARD ACTION"+ ruleInfo.getCriteria());
            context.put("rule_criteria", ruleInfo.getCriteria());
            if(ruleInfo.getTrigger_widget_criteria() != null) {
                context.put("trigger_widget_criteria", ruleInfo.getTrigger_widget_criteria());
            }
        }
        if(ruleInfo != null && ruleInfo.getDatapointList() != null && ruleInfo.getDatapointList().size()>0){
            JSONObject dpAlias_vs_criteria = new JSONObject();
            for(V3DashboardRuleDPContext dp_rule_context : ruleInfo.getDatapointList())
            {
                dpAlias_vs_criteria.put(dp_rule_context.getDatapoint_link(), dp_rule_context.getCriteria());
            }
            context.put("datapoint_rule", dpAlias_vs_criteria);
        }
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
        context.put("webTabId", webTabId);
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
    public String foldersNew() throws Exception
    {
        FacilioChain chain = TransactionChainFactoryV3.getFoldersListChainNew();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put("isWithReport", getIsWithReport());
        context.put("webTabId", webTabId);
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

    public String reportListView() throws Exception
    {
        FacilioChain chain = TransactionChainFactoryV3.getReportsListViewChain();
        FacilioContext context = chain.getContext();
        context.put("isCustomModule", false);
        if(moduleName != null && moduleName.equals("custommodule")){
            context.put("isCustomModule", true);
        }else{
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleId);
            context.put("moduleName", module.getName());
        }
        context.put("searchText",searchText);
        context.put("folderId",folderId);
        context.put("isPivot", isPivot);
        JSONObject pagination = new JSONObject();
        pagination.put("page", page);
        pagination.put("perPage", perPage);

        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put(Constants.WITH_COUNT, withCount);
        context.put(FacilioConstants.ContextNames.ORDER_TYPE, orderType);

        chain.execute(context);
        List<ReportContext> reportsList = (List<ReportContext>)context.get("reportsList");
        if(context.containsKey(Constants.WITH_COUNT) && context.get(Constants.WITH_COUNT).equals(Boolean.TRUE)) {
            setData("count", context.get(Constants.COUNT) );
        }
        else{
            setData("reportsList", reportsList);
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

    public String createFolder() throws Exception
    {
        getReportFolderLinkName(reportFolder);
        FacilioChain chain = TransactionChainFactoryV3.getCreateReportFolderChain();
        FacilioContext context = chain.getContext();
        context.put("actionType", "ADD");
        context.put("reportFolder", reportFolder);
        context.put("moduleName", moduleName);
        chain.execute();
        if(context.get("reportFolder") !=  null) {
            setData("reportFolder", reportFolder);
        }else{
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Error while creating report folder.");
        }
        return SUCCESS;
    }

    public String updateFolder() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getCreateReportFolderChain();
        FacilioContext context = chain.getContext();
        context.put("actionType", "UPDATE");
        context.put("reportFolder", reportFolder);
        chain.execute();
        if(context.get("reportFolder") !=  null) {
            setData("reportFolder", reportFolder);
        }else{
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Error while updating report folder.");
        }
        return SUCCESS;
    }

    public String moveToFolder() throws Exception
    {
        if (reportId <= 0)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Error while moving Report to folder.");
        }
        FacilioChain chain = TransactionChainFactoryV3.getReportContextChain();
        FacilioContext reportDetailContext = chain.getContext();
        reportDetailContext.put("reportId", reportId);
        chain.execute();
        ReportContext reportContext = (ReportContext) reportDetailContext.get("reportContext");
        if(reportContext != null)
        {
            reportContext.setReportFolderId(folderId);
            FacilioChain moveToFolderChain = TransactionChainFactoryV3.getMoveReportChain();
            FacilioContext moveToContext = moveToFolderChain.getContext();
            moveToContext.put("report", reportContext);
            moveToFolderChain.execute();
            String result = (String) moveToContext.get("result");
            if(result == null)
            {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Error while moving Report to folder.");
            }
        }
        else{
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Error while getting Report details.");
        }
        return SUCCESS;
    }


    public String deleteFolder() throws Exception {
        if (reportFolder != null)
        {
            FacilioChain chain = TransactionChainFactoryV3.getCreateReportFolderChain();
            FacilioContext context = chain.getContext();
            context.put("reportFolder", reportFolder);
            context.put("actionType", "DELETE");
            chain.execute();
            if(context.get("isReportExists") != null) {
                setData("errorString", "Report present in Folder");
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Reports are present in Folder.");
            }
        }
        return SUCCESS;
    }

    public String updateFolderPermission() throws Exception
    {
        if (folderId <= 0)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Error while moving Report to folder.");
        }
        FacilioChain chain = TransactionChainFactoryV3.getFolderPermissionUpdateChain();
        FacilioContext context = chain.getContext();
        context.put("reportFolder",reportFolder);
        context.put("folderId",folderId);
        chain.execute();
        String result = (String) context.get("result");
        if(result == null)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Error while updating report folder permission.");
        }
        setData("result", SUCCESS);
        return SUCCESS;
    }

    public void setExportParamsInContext(FacilioContext context)
    {
        context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
        context.put("chartType", chartType);    // Temp
        context.put("exportParams", exportParams);
        context.put("renderParams", renderParams);
    }

    private void setReportWithDataContext(FacilioContext context) throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getReportContextChain();
        FacilioContext reportDetailContext = chain.getContext();
        reportDetailContext.put("reportId", reportId);
        chain.execute();
        reportContext = (ReportContext) reportDetailContext.get("reportContext");
        if (reportContext != null && reportContext.getReportState() != null && !reportContext.getReportState().isEmpty() && reportContext.getReportState().containsKey(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR))
        {
            AggregateOperator groupByAggr = AggregateOperator.getAggregateOperator(((Long) reportContext.getReportState().get(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR)).intValue());
            reportContext.setgroupByTimeAggr(groupByAggr);
        }

        if (startTime > 0 && endTime > 0) {
            reportContext.setDateRange(new DateRange(startTime, endTime));
            reportContext.setDateValue(new DateRange(startTime, endTime).toString());
        }
        if (dashboardId > 0)
        {
            FacilioChain dateFilterChain = TransactionChainFactoryV3.getReportContextChain();
            FacilioContext dateFilterContext = dateFilterChain.getContext();
            dateFilterContext.put("dashboardId", dashboardId);
            dateFilterChain.execute();
            DateRange range = (DateRange) dateFilterContext.get("range");
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
            reportContext.addToReportState(FacilioConstants.ContextNames.REPORT_GROUP_BY_TIME_AGGR, groupByTimeAggr.getValue());
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

    public String exportReport() throws Exception
    {
        FacilioContext context = null;
        FacilioChain exportChain = null;
        if(reportId != -1)
        {
            boolean fetchData = fileFormat != FileInfo.FileFormat.IMAGE && fileFormat != FileInfo.FileFormat.PDF;
            exportChain = TransactionChainFactory.getExportModuleReportFileChain(fetchData);
            context = exportChain.getContext();
            if(getFilters() != null) {
                JSONParser parser = new JSONParser();
                context.put(FacilioConstants.ContextNames.FILTERS, (JSONObject) parser.parse(getFilters()));
            }
            context.put("is_export_report", true);
            setReportWithDataContext(context);
        }
        else
        {
            exportChain = TransactionChainFactory.getExportModuleAnalyticsFileChain();
            context = exportChain.getContext();
            V3ReportAction v3actionObj = new V3ReportAction();
            v3actionObj.updateChainContext(context);
        }
        setExportParamsInContext(context);

        exportChain.execute();
        setData("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));
        return SUCCESS;
    }
    public String sendMail() throws Exception
    {
        FacilioChain mailReportChain = FacilioChain.getNonTransactionChain();
        FacilioContext context = new FacilioContext();
        if (reportId != -1)
        {
            setExecuteReportContextData(context);
            boolean fetchData = fileFormat != FileInfo.FileFormat.IMAGE && fileFormat != FileInfo.FileFormat.PDF;
            mailReportChain = TransactionChainFactory.sendModuleReportMailChain(fetchData);
        } else {
            updateChainContext(context);
            mailReportChain = TransactionChainFactory.sendModuleAnalyticsMailChain();
        }
        context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
        context.put("isS3Url", true);
        setExportParamsInContext(context);
        mailReportChain.execute(context);
        setData("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));
        return SUCCESS;
    }
    public String clonetoAnotherApp() throws Exception {
        try
        {
            JSONObject cloned_json = this.getData();
            FacilioChain chain = TransactionChainFactoryV3.getCloneReportChain();
            FacilioContext context = chain.getContext();
            updateContextToClone(context, cloned_json);
            chain.execute();
        }
        catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while cloning dashboard");
        }
        return SUCCESS;
    }
    private void updateContextToClone(Context context, JSONObject cloned_json) throws Exception{
        context.put("cloned_report_name", cloned_json.get("cloned_report_name"));
        context.put("target_app_id", cloned_json.get("target_app_id"));
        context.put("cloned_app_id", cloned_json.get("cloned_app_id"));
        context.put(FacilioConstants.ContextNames.REPORT_ID, cloned_json.get("report_id"));
    }
    public String getReport() throws Exception
    {
        if(reportId <= 0)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid ReportId.");
        }
        FacilioChain chain = TransactionChainFactoryV3.getReportContextChain();
        FacilioContext reportDetailContext = chain.getContext();
        reportDetailContext.put("reportId", reportId);
        chain.execute();
        ReportContext reportContext = (ReportContext) reportDetailContext.get("reportContext");
        if(reportContext == null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Report does not exists.");
        }
        setData("report", reportContext);
        return SUCCESS;
    }

    public String createOrUpdateScheduledReport() throws Exception
    {
        FacilioChain chain = TransactionChainFactoryV3.getScheduledReportChain(false);
        FacilioContext context = chain.getContext();
        if(ids != null && ids.size()>0)
        {
            chain = TransactionChainFactoryV3.getScheduledReportChain(true);
            context = chain.getContext();
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
            context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
        }
        context.put(FacilioConstants.Workflow.TEMPLATE, reportInfo.getEmailTemplate());
        context.put(FacilioConstants.ContextNames.SCHEDULE_INFO, reportInfo);
        chain.execute();
        setModuleName(reportInfo.getModuleName());
        setData("id", context.get(FacilioConstants.ContextNames.RECORD_ID));
        if(ids != null && ids.size()>0) {
            scheduledList();
        }
        return SUCCESS;
    }
    public String deleteScheduledReport() throws Exception
    {
        if(ids == null || ids.size()<=0)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Scheduled Report Ids can not be null.");
        }
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
        FacilioChain delteScheduleChain = TransactionChainFactory.deleteScheduledReportsChain();
        delteScheduleChain.execute(context);
        setData(FacilioConstants.ContextNames.ROWS_UPDATED, context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
        return SUCCESS;
    }

    public String scheduledList() throws Exception
    {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
        FacilioChain scheduleReportListChain = ReadOnlyChainFactory.fetchScheduledReportsChain();
        scheduleReportListChain.execute(context);
        setData("scheduledReports", context.get(FacilioConstants.ContextNames.REPORT_LIST));
        return SUCCESS;
    }

    public String fetchData() throws Exception {
        FacilioChain c = FacilioChain.getNonTransactionChain();
        FacilioContext context = c.getContext();
        updateChainContext(context);
        c.addCommand(new ConstructReportData());
        c.addCommand(ReadOnlyChainFactory.constructAndFetchReportDataChain());
        c.execute();
        return setReportResult(context);
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
    public String getModuleList() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getReportModuleListChain();
        FacilioContext context = chain.getContext();
        chain.execute();
        setData("moduleList", context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return SUCCESS;
    }
    public String reportList()throws Exception
    {
            FacilioChain chain = TransactionChainFactoryV3.getReportListAsOptionChain();
            FacilioContext context = chain.getContext();
            context.put("searchText", searchText);
            context.put("webTabId", webTabId);
            context.put("appId", appId);
            context.put("isPivot", isPivot);
            context.put("moduleName", moduleName);
            chain.execute();
            JSONArray reports = (JSONArray) context.get("reports");
            setData("reports", reports);
        return SUCCESS;
    }

    public String reportModulesList()throws Exception
    {
        FacilioChain chain = TransactionChainFactoryV3.getPivotModulesList();
        FacilioContext context = chain.getContext();
        context.put("webTabId", webTabId);
        chain.execute();

        setData("systemModules", context.get("systemModules"));
        setData("customModules", context.get("customModules"));

        return SUCCESS;
    }
    private void getReportFolderLinkName(ReportFolderContext folder) throws Exception {
        Map<String, FacilioField> reportFolderFields = FieldFactory.getAsMap(FieldFactory.getReport1FolderFields());
        FacilioField folderLinkName = reportFolderFields.get(FacilioConstants.ContextNames.LINK_NAME);
        FacilioModule module = ModuleFactory.getReportFolderModule();
        List<String> linkNames = DashboardUtil.getExistingLinkNames(module.getTableName(),folderLinkName);
        if(folder.getLinkName() == null){
            String name = folder.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            String linkName = DashboardUtil.getLinkName(name,linkNames);
            folder.setLinkName(linkName);
        }
    }
}