package com.facilio.analytics.v2.action;

import com.facilio.analytics.v2.chain.V2AnalyticsTransactionChain;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportFactoryFields;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;


public class V2AnalyticsReportAction extends V3Action {

    @Getter @Setter
    public V2ReportContext report = new V2ReportContext();

    public String create()throws Exception
    {
        FacilioChain chain = V2AnalyticsTransactionChain.getCREDAnalyticsReportChain();
        FacilioContext context = chain.getContext();
        context.put("report_v2", report);
        chain.execute();
        ReportContext report = (ReportContext)context.get(FacilioConstants.ContextNames.REPORT);
        setData("reportId", report != null ? report.getId() : -1);
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
        setData("reportXValues", context.get(FacilioConstants.ContextNames.REPORT_X_VALUES)); // This can be removed
        // from new format
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

}
