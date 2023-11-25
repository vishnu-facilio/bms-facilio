package com.facilio.analytics.v2.action;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.analytics.v2.context.V2AnalyticsContextForDashboardFilter;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.modules.FacilioModule;
import com.facilio.report.context.ReportContext;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.function.Function;


@Getter @Setter
public class V2AnalyticsReportAction extends V3Action {

    public V2ReportContext report = new V2ReportContext();
    public Long appId;
    public Long reportId ;
    private int reportType;
    private int page=1;
    private boolean fetchOnlyKpi = false;
    private String fetchOnlyModule;
    private int perPage=50;
    private boolean withCount;
    private String orderType;
    private long folderId = -1;
    private int moduleType;
    private Boolean defaultModules;
    private Boolean isDataNeeded;

    V2AnalyticsContextForDashboardFilter db_filter;
    public String create()throws Exception
    {
        validateData(FacilioConstants.ContextNames.CREATE);
        FacilioChain chain = V2AnalyticsTransactionChain.getCREDAnalyticsReportChain();
        FacilioContext context = chain.getContext();
        context.put("report_v2", report);
        context.put("actionType", FacilioConstants.ContextNames.CREATE);
        chain.execute();
        ReportContext report = (ReportContext)context.get(FacilioConstants.ContextNames.REPORT);
        setData(FacilioConstants.ContextNames.REPORT_ID, report != null ? report.getId() : -1);
        this.setReportAuditLogs(report.getModule().getDisplayName(), report, "Analytics Report {%s} has been created.", AuditLogHandler.ActionType.ADD);
        return V3Action.SUCCESS;
    }
    public String update()throws Exception
    {
        validateData(FacilioConstants.ContextNames.UPDATE);
        FacilioChain chain = V2AnalyticsTransactionChain.getCREDAnalyticsReportChain();
        FacilioContext context = chain.getContext();
        context.put("report_v2", report);
        context.put("actionType", FacilioConstants.ContextNames.UPDATE);
        chain.execute();
        ReportContext report = (ReportContext)context.get(FacilioConstants.ContextNames.REPORT);
        setData(FacilioConstants.ContextNames.REPORT_ID, report != null ? report.getId() : -1);
        setReportAuditLogs(report.getModule().getDisplayName(), report, "Analytics Report {%s} has been updated.", AuditLogHandler.ActionType.ADD);
        return V3Action.SUCCESS;
    }

    public String delete()throws Exception
    {
        validateData(FacilioConstants.ContextNames.DELETE);
        FacilioChain chain = V2AnalyticsTransactionChain.getDeleteAnalyticsReportChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.REPORT_ID, reportId);
        context.put("isDeleteWithWidget", true);
        context.put("actionType", FacilioConstants.ContextNames.DELETE);
        chain.execute();
        setData("result", "success");
        sendAuditLogs(new AuditLogHandler.AuditLogContext(String.format("Analytics Report {%s} has been deleted.", (String)context.get("reportName")), "", AuditLogHandler.RecordType.MODULE, (String) context.get("moduleName"), reportId));
        return V3Action.SUCCESS;
    }

    public String list()throws Exception
    {
        FacilioChain chain = V2AnalyticsTransactionChain.getAnalyticReportListChain();
        FacilioContext context = chain.getContext();
        JSONObject pagination = new JSONObject();
        pagination.put("page", page);
        pagination.put("perPage", perPage);
        context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
        context.put("fetchOnlyKpi",fetchOnlyKpi);
        context.put("fetchOnlyModule",fetchOnlyModule);
        context.put(FacilioConstants.ContextNames.ORDER_TYPE, orderType);
        context.put(FacilioConstants.ContextNames.WITH_COUNT, withCount);
        context.put(FacilioConstants.ContextNames.APP_ID,appId);
        context.put("reportType",reportType);
        context.put("folderId", folderId);
        chain.execute();
        setData("reports", context.get("reports"));
        setData("count", context.get(Constants.COUNT));
        return V3Action.SUCCESS;
    }
    public String edit()throws Exception
    {
        if(reportId != null && reportId > 0)
        {
            FacilioChain chain = V2AnalyticsTransactionChain.getReportWithDataChain(isDataNeeded);
            FacilioContext context = chain.getContext();
            context.put("reportId", reportId);
            context.put("db_filter", db_filter);
            chain.execute();
            this.setReportResult(context);
        }
        return V3Action.SUCCESS;
    }
    public String fetchOld() throws Exception
    {
        FacilioChain chain = V2AnalyticsTransactionChain.getAnalyticsReportDataOldChain();
        FacilioContext context = chain.getContext();
        context.put("report_v2", report);
        chain.execute();
        setReportResult(context);
        return V3Action.SUCCESS;
    }


    private String setReportResult(FacilioContext context)
    {
        if (context.get(FacilioConstants.ContextNames.REPORT) != null) {

            ReportContext reportContext = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
            setData("report", reportContext);
        }
        if(context.get("v2_report") != null){
            setData("v2_report", context.get("v2_report"));
        }
        setData("reportXValues", context.get(FacilioConstants.ContextNames.REPORT_X_VALUES)); // This can be removed
        // from new format
        if(context.get(FacilioConstants.ContextNames.REPORT_DATA) != null)
        {
            JSONObject data = (JSONObject) context.get(FacilioConstants.ContextNames.REPORT_DATA);
            if(data != null && data.containsKey("heatMapData")){
                data.put("data", data.get("heatMapData"));
                data.remove("heatMapData");
            }
        }
        setData("reportData", context.get(FacilioConstants.ContextNames.REPORT_DATA));
        setData("safeLimits", context.get(FacilioConstants.ContextNames.REPORT_SAFE_LIMIT));

        FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
        if (module != null) {
            setData("module", module);
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

        return SUCCESS;
    }

    public String getAllReportModes()throws Exception
    {
        setData("dimension_modes", V2AnalyticsOldUtil.getDimensionModes());
        return V3Action.SUCCESS;
    }

    private void validateData(String action_type) throws Exception
    {
        if ((action_type != null &&  ((action_type.equals(FacilioConstants.ContextNames.CREATE) &&  reportId != null && reportId > 0) || (action_type.equals(FacilioConstants.ContextNames.DELETE) && reportId != null && reportId <= 0)))) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid ReportId.");
        }
        if (action_type != null && (action_type.equals(FacilioConstants.ContextNames.CREATE) || action_type.equals(FacilioConstants.ContextNames.UPDATE)) && report == null )
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ReportContext is mandatory.");
        }
    }
    public void setReportAuditLogs(String moduleDisplayName, ReportContext reportContext, String log_message, AuditLogHandler.ActionType actionType) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.ENERGY_DATA_READING;
        Long moduleId = modBean.getModule(moduleName).getModuleId();
        AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format(log_message, reportContext.getName(), moduleDisplayName), reportContext.getDescription(), AuditLogHandler.RecordType.MODULE, moduleName, reportContext.getId())
                .setActionType(actionType)
                .setLinkConfig(((Function<Void, String>) o -> {
                    JSONArray array = new JSONArray();
                    JSONObject json = new JSONObject();
                    json.put("reportId", reportContext.getId());
                    json.put("moduleName", moduleName);
                    json.put("moduleId", moduleId);
                    json.put("reportType", reportContext.getType());
                    json.put("moduleType", false);
                    array.add(json);
                    return array.toJSONString();
                }).apply(null));
        sendAuditLogs(auditLog);
    }
    public String getModuleList() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getModuleList();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType);
        context.put(FacilioConstants.ContextNames.FETCH_DEFAULT_MODULES, defaultModules);

        chain.execute();

        setData("moduleList", context.get(FacilioConstants.ContextNames.MODULE_LIST));
        return V3Action.SUCCESS;
    }
}
