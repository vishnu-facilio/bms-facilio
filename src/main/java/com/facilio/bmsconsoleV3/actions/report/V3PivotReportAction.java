package com.facilio.bmsconsoleV3.actions.report;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fs.FileInfo;
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.modules.FieldUtil;
import com.facilio.report.context.PivotDataColumnContext;
import com.facilio.report.context.PivotRowColumnContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportPivotParamsContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
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
    private EMailTemplate emailTemplate;
    private FileInfo.FileFormat fileFormat;
    public int getFileFormat() {
        return  fileFormat != null ? fileFormat.getIntVal() : -1;
    }
    public void setFileFormat(int fileFormat) {
        this.fileFormat = FileInfo.FileFormat.getFileFormat(fileFormat);
    }
    private List<PivotRowColumnContext> rows = new ArrayList<PivotRowColumnContext>();
    private List<PivotDataColumnContext> pivotData = new ArrayList<PivotDataColumnContext>();

    public void setRows(List<PivotRowColumnContext> rows) {
        this.rows = rows;
    }

    public List<PivotRowColumnContext> getRows() {
        return rows;
    }

    public List<PivotDataColumnContext> getPivotData() {
        return pivotData;
    }

    public void setPivotData(List<PivotDataColumnContext> data) {
        this.pivotData = data;
    }

    private void setPivotReportContext(FacilioContext context) throws Exception {
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

    private void setPivotParamsContext(ReportPivotParamsContext pivotparams) throws Exception {
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

    public String create() throws Exception {
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
        String log_message = "Report {%s} has been created for {%s} Module.";
        V3ReportAction reportAction = new V3ReportAction();
        reportAction.setReportAuditLogs(reportContext.getModule().getDisplayName(), reportContext, log_message,
                AuditLogHandler.ActionType.ADD);
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
    public String sendPivotMail() throws Exception {
        FacilioContext context = new FacilioContext();
        FacilioChain mailReportChain;
        ReportContext reportContext = ReportUtil.getReport(reportId);
        context.put(FacilioConstants.ContextNames.FILE_FORMAT, fileFormat);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);
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
        context.put(FacilioConstants.ContextNames.IS_BUILDER_V2, pivotparams.isBuilderV2());
        context.put(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED, false);
        context.put(FacilioConstants.ContextNames.START_TIME, pivotparams.getStartTime());
        context.put(FacilioConstants.ContextNames.END_TIME, pivotparams.getEndTime());
        mailReportChain = TransactionChainFactory.sendPivotReportMailChain();
        context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
        context.put("isS3Url", true);
        mailReportChain.execute(context);
        return SUCCESS;
    }

    public String update() throws Exception {
        validateData(2);
        FacilioChain chain = TransactionChainFactoryV3.getCreateOrUpdatePivotReportChain();
        FacilioContext context = chain.getContext();
        ReportPivotParamsContext pivotparams = new ReportPivotParamsContext();
        setPivotReportContext(context);
        setPivotParamsContext(pivotparams);
        context.put(FacilioConstants.ContextNames.REPORT_ID, reportId);
        reportContext.setId(reportId);
        createOrUpdatePivotReport(chain, context, pivotparams);
        String log_message = "Report {%s} has been created for {%s} Module.";
        V3ReportAction reportAction = new V3ReportAction();
        reportAction.setReportAuditLogs(reportContext.getModule().getDisplayName(), reportContext, log_message,
                AuditLogHandler.ActionType.UPDATE);
        setMessage("Report Updated Successfully");
        return SUCCESS;
    }

    private void setContextForExecute(FacilioContext context, ReportPivotParamsContext pivotparams) throws Exception {
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, reportContext.getModule().getName());
        context.put(FacilioConstants.Reports.ROWS, pivotparams.getRows());
        context.put(FacilioConstants.Reports.DATA, pivotparams.getData());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, pivotparams.getModuleName());
        context.put(FacilioConstants.ContextNames.CRITERIA, pivotparams.getCriteria());
        context.put(FacilioConstants.ContextNames.SORTING, sortBy != null ? sortBy : pivotparams.getSortBy());
        context.put(FacilioConstants.ContextNames.TEMPLATE_JSON, pivotparams.getTemplateJSON());
        context.put(FacilioConstants.ContextNames.DATE_FIELD, pivotparams.getDateFieldId());
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR, pivotparams.getDateOperator());
        if (getStartTime() > 0 && getEndTime() > 0) {
            context.put(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED, true);
            context.put(FacilioConstants.ContextNames.START_TIME, getStartTime());
            context.put(FacilioConstants.ContextNames.END_TIME, getEndTime());
        } else {
            context.put(FacilioConstants.ContextNames.IS_TIMELINE_FILTER_APPLIED, false);
            context.put(FacilioConstants.ContextNames.START_TIME, pivotparams.getStartTime());
            context.put(FacilioConstants.ContextNames.END_TIME, pivotparams.getEndTime());
        }
        context.put(FacilioConstants.ContextNames.TIME_FILTER, pivotparams.getShowTimelineFilter());
        context.put(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, pivotparams.getDateValue());
        if (getFilters() != null) {
            JSONParser parser = new JSONParser();
            JSONObject filter = (JSONObject) parser.parse(getFilters());
            context.put(FacilioConstants.ContextNames.FILTERS, filter);
        }

    }

    private void setReportContextForExecute() throws Exception {

        FacilioChain c = TransactionChainFactoryV3.getReportContextChain();
        FacilioContext context = c.getContext();
        context.put("reportId", reportId);
        c.execute();
        reportContext = (ReportContext) context.get("reportContext");
        if (reportContext == null) {
            throw new Exception("Report not found");
        }
        if (startTime != -1 && endTime != -1) {
            reportContext.setDateRange(new DateRange(startTime, endTime));
        }
    }

    public String execute() throws Exception {
        if (reportId <= 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid ReportId.");
        }
        FacilioChain chain = TransactionChainFactoryV3.getExecutePivotReportChain(getFilters());
        FacilioContext context = chain.getContext();
        setReportContextForExecute();
        JSONParser parser = new JSONParser();
        ReportPivotParamsContext pivotparams = FieldUtil.getAsBeanFromJson(
                (JSONObject) parser.parse(reportContext.getTabularState()), ReportPivotParamsContext.class);
        setContextForExecute(context, pivotparams);
        chain.execute();
        setPivotReportResponse(context, pivotparams);

        return SUCCESS;
    }

    private void setPivotReportResponse(FacilioContext context, ReportPivotParamsContext pivotparams) throws Exception {
        setData("report", reportContext);
        setData(FacilioConstants.ContextNames.ROW_HEADERS, context.get(FacilioConstants.ContextNames.ROW_HEADERS));
        setData(FacilioConstants.ContextNames.DATA_HEADERS, context.get(FacilioConstants.ContextNames.DATA_HEADERS));
        setData(FacilioConstants.ContextNames.ROW_ALIAS, context.get(FacilioConstants.ContextNames.ROW_ALIAS));
        setData(FacilioConstants.ContextNames.DATA_ALIAS, context.get(FacilioConstants.ContextNames.DATA_ALIAS));
        setData(FacilioConstants.ContextNames.PIVOT_TABLE_DATA,
                context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA));
        setData(FacilioConstants.ContextNames.SORTING, pivotparams.getSortBy());
        setData(FacilioConstants.ContextNames.CRITERIA, pivotparams.getCriteria());
        setData(FacilioConstants.ContextNames.PIVOT_TEMPLATE_JSON, pivotparams.getTemplateJSON());
        setData(FacilioConstants.Reports.ROWS, pivotparams.getRows());
        setData(FacilioConstants.Reports.DATA, pivotparams.getData());
        setData(FacilioConstants.ContextNames.MODULE_NAME, pivotparams.getModuleName());
        setData(FacilioConstants.ContextNames.DATE_FIELD, pivotparams.getDateFieldId());
        setData(FacilioConstants.ContextNames.DATE_OPERATOR, pivotparams.getDateOperator());
        setData(FacilioConstants.ContextNames.START_TIME, pivotparams.getStartTime());
        setData(FacilioConstants.ContextNames.END_TIME, pivotparams.getEndTime());
        setData((String) FacilioConstants.ContextNames.TIME_FILTER, pivotparams.getShowTimelineFilter());
        setData(FacilioConstants.ContextNames.DATE_OPERATOR_VALUE, pivotparams.getDateValue());
        setData(FacilioConstants.ContextNames.PIVOT_ALIAS_VS_FIELD, context.get(FacilioConstants.ContextNames.PIVOT_ALIAS_VS_FIELD));
        setData(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA, context.get(FacilioConstants.ContextNames.PIVOT_RECONSTRUCTED_DATA));
    }

    private void createOrUpdatePivotReport(FacilioChain chain, FacilioContext context,
            ReportPivotParamsContext pivotparams) throws Exception {
        reportContext.setTabularState(FieldUtil.getAsJSON(pivotparams).toJSONString());
        reportContext.setType(ReportContext.ReportType.PIVOT_REPORT);
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);

        chain.execute();

        setData("message", "Report saved");
        setData("report", reportContext);
        setData(FacilioConstants.ContextNames.ROW_HEADERS, context.get(FacilioConstants.ContextNames.ROW_HEADERS));
        setData(FacilioConstants.ContextNames.DATA_HEADERS, context.get(FacilioConstants.ContextNames.DATA_HEADERS));
        setData(FacilioConstants.ContextNames.ROW_ALIAS, context.get(FacilioConstants.ContextNames.ROW_ALIAS));
        setData(FacilioConstants.ContextNames.DATA_ALIAS, context.get(FacilioConstants.ContextNames.DATA_ALIAS));
        setData(FacilioConstants.ContextNames.PIVOT_TABLE_DATA,
                context.get(FacilioConstants.ContextNames.PIVOT_TABLE_DATA));
    }

    public String exportReport()throws Exception
    {
        FacilioChain chain = TransactionChainFactoryV3.getReportContextChain();
        FacilioContext reportDetailContext = chain.getContext();
        reportDetailContext.put("reportId", reportId);
        chain.execute();
        reportContext = (ReportContext) reportDetailContext.get("reportContext");
        if(reportContext == null)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Report does not exists.");
        }
        JSONParser parser = new JSONParser();
        ReportPivotParamsContext pivotparams = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(reportContext.getTabularState()), ReportPivotParamsContext.class);
        chain = TransactionChainFactoryV3.getExportPivotReportChain();
        FacilioContext context = chain.getContext();
        setContextForExecute(context, pivotparams);
        chain.execute();
        setData("fileUrl", context.get(FacilioConstants.ContextNames.FILE_URL));

        return SUCCESS;
    }
    private void validateData(Integer action_type) throws Exception
    {
        if ((action_type == 1 && reportId > 0) || (action_type == 2 && reportId <=0)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid ReportId.");
        }
        if (action_type == 2 && reportContext == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ReportContext Data is mandatory.");
        }
        if (StringUtils.isEmpty(moduleName)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "ModuleName is mandatory.");
        }
    }
}
