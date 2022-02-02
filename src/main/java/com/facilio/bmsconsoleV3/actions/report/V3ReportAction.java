package com.facilio.bmsconsoleV3.actions.report;

import java.util.List;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
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
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportContext.ReportType;
import com.facilio.report.context.ReportDrilldownPathContext;
import com.facilio.report.context.ReportSettings;
import com.facilio.report.context.ReportUserFilterContext;

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

    public JSONObject getxField() { return xField; }
    public void setxField(JSONObject xField) {
        this.xField = xField;
    }
    private List<ReportUserFilterContext> userFilters;
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
        AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format("Report {%s} has been successfully created for {%s} Module.", reportContext.getName(), moduleName), reportContext.getDescription(), "", AuditLogHandler.RecordType.MODULE, moduleName, reportContext.getId());
        sendAuditLogs(auditLog);
        setMessage("Report created successfully!");
        return SUCCESS;
    }

    public String update() throws Exception {
        validateData(2);
        FacilioChain chain = TransactionChainFactoryV3.getCreateOrUpdateReportChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.REPORT_ID,reportContext.getId());
        createOrUpdateReport(chain, context);
        AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format("Report {%s} has been successfully updated for {%s} Module.", reportContext.getName(), moduleName), "", "", AuditLogHandler.RecordType.MODULE, moduleName, reportContext.getId());
        sendAuditLogs(auditLog);
        setMessage("Report updated successfully!");
        return SUCCESS;
    }

    private void createOrUpdateReport(FacilioChain chain, FacilioContext context) throws Exception
    {
        updateModuleReportContext();
        updateChainContext(context);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);
        chain.execute();
        setData("report", reportContext);
    }

    private void validateData(Integer action_type) throws Exception
    {
        if (action_type == 1 && reportId > 0 ) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid ReportId.");
        }
        if (reportContext == null )
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ReportContext is mandatory.");
        }
        if(StringUtils.isEmpty(moduleName)){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ModuleName is mandatory.");
        }
    }
}