package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.report.context.ReportContext;
import org.apache.commons.chain.Context;

public class V2AddNewAnalyticReportCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        V2ReportContext v2_report = (V2ReportContext) context.get("report_v2");
        String actionType = (String) context.get("actionType");
        ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        if(actionType != null && actionType.equals(FacilioConstants.ContextNames.CREATE) && report != null && report.getId() > 0 && v2_report != null){
            v2_report.setReportId(report.getId());
            this.generateAndSetMeasureCriteria(v2_report, actionType);
            V2AnalyticsOldUtil.addNewReportV2(v2_report);
        }
        else if(actionType != null && actionType.equals(FacilioConstants.ContextNames.UPDATE) && report != null && report.getId() > 0 && v2_report != null){
            v2_report.setReportId(report.getId());
            this.generateAndSetMeasureCriteria(v2_report, actionType);
            V2AnalyticsOldUtil.updateNewReportV2(v2_report);
        }
        else if(actionType != null && actionType.equals(FacilioConstants.ContextNames.DELETE)){
            Long reportId = (Long) context.get(FacilioConstants.ContextNames.REPORT_ID);
            V2ReportContext v2_reportContext = V2AnalyticsOldUtil.getV2Report(reportId);
            if(v2_reportContext != null) {
                V2AnalyticsOldUtil.deleteNewReportV2(v2_reportContext);
            }
        }
        return false;
    }

    private void generateAndSetMeasureCriteria(V2ReportContext reportContext, String actionType)throws Exception
    {
        for(V2MeasuresContext measure: reportContext.getMeasures())
        {
            if(actionType != null && actionType.equals("update") && measure.getCriteriaId() > 0)
            {
                CriteriaAPI.deleteCriteria(measure.getCriteriaId());
                measure.setCriteriaId(-1l);
            }
            long criteriaId = V2AnalyticsOldUtil.generateCriteriaId(measure.getCriteria(), measure.getParentModuleName());
            if (criteriaId > 0) {
                measure.setCriteriaId(criteriaId);
                measure.setCriteria(null);
            }
        }
    }



}
