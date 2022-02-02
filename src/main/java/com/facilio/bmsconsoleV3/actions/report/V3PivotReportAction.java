package com.facilio.bmsconsoleV3.actions.report;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportPivotParamsContext;
import com.facilio.report.context.ReportPivotTableDataContext;
import com.facilio.report.context.ReportPivotTableRowsContext;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.wmsv2.handler.AuditLogHandler;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class V3PivotReportAction extends V3Action {

    private long reportId = -1;
    String moduleName;
    ReportContext reportContext;
    private Criteria criteria;
    private JSONObject sortBy;
    private JSONObject templateJSON;
    private long dateFieldId = -1;
    Integer dateOperator;
    String dateValue;
    private long startTime = -1;
    private long endTime = -1;
    private Boolean showTimelineFilter;



    private List<ReportPivotTableRowsContext> rows = new ArrayList<ReportPivotTableRowsContext>();
    private List<ReportPivotTableDataContext> pivotData = new ArrayList<ReportPivotTableDataContext>();

    public void setRows(List<ReportPivotTableRowsContext> rows) {
        this.rows = rows;
    }
    public List<ReportPivotTableRowsContext> getRows() {
        return rows;
    }
    public List<ReportPivotTableDataContext> getPivotData() {
        return pivotData;
    }
    public void setPivotData(List<ReportPivotTableDataContext> data) {
        this.pivotData = data;
    }

    private void setPivotReportContext(FacilioContext context)throws Exception
    {
        context.put(FacilioConstants.Reports.ROWS, rows);
        context.put(FacilioConstants.Reports.DATA, pivotData);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
        context.put(FacilioConstants.ContextNames.SORTING, sortBy);
        context.put(FacilioConstants.ContextNames.TEMPLATE_JSON, templateJSON);
        context.put(FacilioConstants.ContextNames.DATE_FIELD, dateFieldId);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR, dateOperator);
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, dateValue);
        context.put(FacilioConstants.ContextNames.START_TIME, startTime);
        context.put(FacilioConstants.ContextNames.END_TIME, endTime);
        context.put(FacilioConstants.ContextNames.TIME_FILTER, showTimelineFilter);
    }
    private void setPivotParamsContext(ReportPivotParamsContext pivotparams) throws Exception
    {
        pivotparams.setRows(rows);
        pivotparams.setData(pivotData);
        pivotparams.setModuleName(moduleName);
        pivotparams.setCriteria(criteria);
        pivotparams.setSortBy(sortBy);
        pivotparams.setTemplateJSON(templateJSON);
        pivotparams.setDateFieldId(dateFieldId);
        pivotparams.setDateOperator(dateOperator);
        pivotparams.setDateValue(dateValue);
        pivotparams.setStartTime(startTime);
        pivotparams.setEndTime(endTime);
        if (showTimelineFilter != null) {
            pivotparams.setShowTimelineFilter(showTimelineFilter);
        }
    }

    public String create() throws Exception
    {
        validateData(1);
        if (reportContext == null) {
            reportContext = new ReportContext();
        }
        FacilioChain chain = TransactionChainFactoryV3.getCreateOrUpdatePivotReportChain();
        FacilioContext context = chain.getContext();
        ReportPivotParamsContext pivotparams = new ReportPivotParamsContext();
        setPivotReportContext(context);
        setPivotParamsContext(pivotparams);
        createOrUpdatePivotReport(chain, context, pivotparams);
        setMessage("Report Created Successfully");
        AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format("Report {%s} has been successfully created for {%s} Module.", reportContext.getName(), moduleName), reportContext.getDescription(), "", AuditLogHandler.RecordType.MODULE, moduleName, reportContext.getId());
        sendAuditLogs(auditLog);
        return SUCCESS;
    }

    public String update() throws Exception
    {
        validateData(2);
        FacilioChain chain = TransactionChainFactoryV3.getCreateOrUpdatePivotReportChain();
        FacilioContext context = chain.getContext();
        ReportPivotParamsContext pivotparams = new ReportPivotParamsContext();
        setPivotReportContext(context);
        setPivotParamsContext(pivotparams);
        context.put(FacilioConstants.ContextNames.REPORT_ID, reportId);
        reportContext.setId(reportId);
        createOrUpdatePivotReport(chain, context, pivotparams);
        AuditLogHandler.AuditLogContext auditLog = new AuditLogHandler.AuditLogContext(String.format("Report {%s} has been successfully created for {%s} Module.", reportContext.getName(), moduleName), reportContext.getDescription(), "", AuditLogHandler.RecordType.MODULE, moduleName, reportId);
        sendAuditLogs(auditLog);
        setMessage("Report Updated Successfully");
        return SUCCESS;
    }

    private void createOrUpdatePivotReport(FacilioChain chain, FacilioContext context, ReportPivotParamsContext pivotparams)throws Exception
    {
        reportContext.setTabularState(FieldUtil.getAsJSON(pivotparams).toJSONString());
        reportContext.setType(ReportContext.ReportType.PIVOT_REPORT);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);

        chain.execute();

        setData("message",  "Report saved");
        setData("report", reportContext);
        setData(FacilioConstants.ContextNames.ROW_HEADERS, context.get(FacilioConstants.ContextNames.ROW_HEADERS));
        setData(FacilioConstants.ContextNames.DATA_HEADERS, context.get(FacilioConstants.ContextNames.DATA_HEADERS));
        setData(FacilioConstants.ContextNames.ROW_ALIAS, context.get(FacilioConstants.ContextNames.ROW_ALIAS));
        setData(FacilioConstants.ContextNames.DATA_ALIAS, context.get(FacilioConstants.ContextNames.DATA_ALIAS));
        setData(FacilioConstants.ContextNames.PIVOT_TABLE_DATA, context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA));
    }

    private void validateData(Integer action_type) throws Exception
    {
        if ((action_type == 1 && reportId > 0) || (action_type == 2 && reportId <=0)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid ReportId.");
        }
        if (action_type == 2 && reportContext == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ReportContext Data is mandatory.");
        }
        if(StringUtils.isEmpty(moduleName)){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ModuleName is mandatory.");
        }
    }
}
