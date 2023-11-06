package com.facilio.report.module.v2.action;

import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.module.v2.chain.V2TransactionChainFactory;
import com.facilio.report.module.v2.context.V2ModuleReportContext;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class V2ModuleReportAction extends V3Action {

    public V2ModuleReportContext report;
    public String searchText;
    public Long reportId ;
    public Long appId ;
    private int page=1;
    private int perPage=50;
    private boolean withCount;
    private String orderType;
    private long folderId = -1;

    public String create() throws Exception
    {
        validateData(FacilioConstants.ContextNames.CREATE);
        FacilioChain chain = V2TransactionChainFactory.getCreateModuleReportChain();
        FacilioContext context = chain.getContext();
        context.put("v2_report", report);
        context.put("isKpi",false);
        context.put("actionType", FacilioConstants.ContextNames.CREATE);
        chain.execute();
        ReportContext report = (ReportContext)context.get(FacilioConstants.ContextNames.REPORT);
        setData(FacilioConstants.ContextNames.REPORT_ID, report != null ? report.getId() : -1);
        return V3Action.SUCCESS;
    }
    public String update()throws Exception
    {
        validateData(FacilioConstants.ContextNames.UPDATE);
        FacilioChain chain = V2TransactionChainFactory.getCreateModuleReportChain();
        FacilioContext context = chain.getContext();
        context.put("v2_report", report);
        context.put("isKpi",false);
        context.put("actionType", FacilioConstants.ContextNames.UPDATE);
        chain.execute();
        ReportContext report = (ReportContext)context.get(FacilioConstants.ContextNames.REPORT);
        setData(FacilioConstants.ContextNames.REPORT_ID, report != null ? report.getId() : -1);
        return V3Action.SUCCESS;
    }

    public String folders()throws Exception
    {
        FacilioChain chain = V2TransactionChainFactory.getV2FolderListChain();
        FacilioContext context = chain.getContext();
        context.put("searchText", searchText);
        context.put(FacilioConstants.ContextNames.APP_ID,appId);
        chain.execute();
        setData("folders", context.get("folders"));
        return V3Action.SUCCESS;
    }
    public String edit()throws Exception
    {
        if(reportId != null && reportId > 0)
        {
            FacilioChain chain = V2TransactionChainFactory.getReportDataChain();
            FacilioContext context = chain.getContext();
            context.put("reportId", reportId);
            chain.execute();
            setData("report", context.get(FacilioConstants.ContextNames.REPORT));
            setData("reportData", context.get(FacilioConstants.ContextNames.REPORT_DATA));
            if(context.get("v2_report") != null){
                setData("v2_report", context.get("v2_report"));
            }
        }
        return V3Action.SUCCESS;
    }

    public String fetch()throws Exception
    {
        FacilioChain chain = V2TransactionChainFactory.getV2FetchReportChain();
        FacilioContext context = chain.getContext();
        context.put("v2_report", report);
        chain.execute();
        setData("report", context.get(FacilioConstants.ContextNames.REPORT));
        setData("reportData", context.get(FacilioConstants.ContextNames.REPORT_DATA));
        if(context.get("v2_report") != null){
            setData("v2_report", context.get("v2_report"));
        }
        return V3Action.SUCCESS;
    }
    public String delete()throws Exception
    {
        validateData(FacilioConstants.ContextNames.DELETE);
        FacilioChain chain = V2TransactionChainFactory.getDeleteModuleReportChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.REPORT_ID, reportId);
        context.put("isDeleteWithWidget", true);
        context.put("actionType", FacilioConstants.ContextNames.DELETE);
        chain.execute();
        setData("result", "success");
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
    public String createKpi() throws Exception
    {
        validateData(FacilioConstants.ContextNames.CREATE);
        FacilioChain chain = V2TransactionChainFactory.getCreateModuleKpiChain();
        FacilioContext context = chain.getContext();
        context.put("v2_report", report);
        context.put("isKpi",true);
        context.put("actionType", FacilioConstants.ContextNames.CREATE);
        chain.execute();
        ReportContext report = (ReportContext)context.get(FacilioConstants.ContextNames.REPORT);
        setData(FacilioConstants.ContextNames.REPORT_ID, report != null ? report.getId() : -1);
        return V3Action.SUCCESS;
    }
    public String fetchKpi()throws Exception
    {
        FacilioChain chain = V2TransactionChainFactory.getV2FetchKpiChain();
        FacilioContext context = chain.getContext();
        context.put("v2_report", report);
        chain.execute();
        setData("value", context.get("value"));
        if(context.get("v2_report") != null){
            setData("v2_report", context.get("v2_report"));
        }
        return V3Action.SUCCESS;
    }
    public String deleteKpi()throws Exception
    {
        validateData(FacilioConstants.ContextNames.DELETE);
        FacilioChain chain = V2TransactionChainFactory.getDeleteModuleReportChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.REPORT_ID, reportId);
        context.put("isDeleteWithWidget", true);
        context.put("actionType", FacilioConstants.ContextNames.DELETE);
        chain.execute();
        setData("result", "success");
        return V3Action.SUCCESS;
    }
    public String editKpi()throws Exception
    {
        if(reportId != null && reportId > 0)
        {
            FacilioChain chain = V2TransactionChainFactory.getKpiDataChain();
            FacilioContext context = chain.getContext();
            context.put("reportId", reportId);
            chain.execute();
            setData("value", context.get("value"));
            if(context.get("v2_report") != null){
                setData("v2_report", context.get("v2_report"));
            }
        }
        return V3Action.SUCCESS;
    }
    public String updateKpi() throws Exception
    {
        validateData(FacilioConstants.ContextNames.UPDATE);
        FacilioChain chain = V2TransactionChainFactory.getCreateModuleKpiChain();
        FacilioContext context = chain.getContext();
        context.put("v2_report", report);
        context.put("isKpi",true);
        context.put("actionType", FacilioConstants.ContextNames.UPDATE);
        chain.execute();
        ReportContext report = (ReportContext)context.get(FacilioConstants.ContextNames.REPORT);
        setData(FacilioConstants.ContextNames.REPORT_ID, report != null ? report.getId() : -1);
        return V3Action.SUCCESS;
    }
}
