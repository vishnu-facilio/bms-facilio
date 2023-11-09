package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2AnalyticUserFilterContext;
import com.facilio.analytics.v2.context.V2MeasuresContext;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.analytics.v2.context.V2ReportFiltersContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.report.context.ReportContext;
import org.apache.commons.chain.Context;

import java.util.List;

public class V2AddNewAnalyticReportCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        V2ReportContext v2_report = (V2ReportContext) context.get("report_v2");
        String actionType = (String) context.get("actionType");
        ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
        List<V2AnalyticUserFilterContext> user_filters = this.getReportV2UserFilter(v2_report);
        if(actionType != null && actionType.equals(FacilioConstants.ContextNames.CREATE) && report != null && report.getId() > 0 && v2_report != null)
        {
            v2_report.setReportId(report.getId());
            this.generateAndSetMeasureCriteria(v2_report, actionType);
            V2AnalyticsOldUtil.addNewReportV2(v2_report);
            if(user_filters != null){
                V2AnalyticsOldUtil.CREDActionV2UserFilter(report.getId(), user_filters, FacilioConstants.ContextNames.CREATE);
            }
        }
        else if(actionType != null && actionType.equals(FacilioConstants.ContextNames.UPDATE) && report != null && report.getId() > 0 && v2_report != null){
            v2_report.setReportId(report.getId());
            this.generateAndSetMeasureCriteria(v2_report, actionType);
            V2AnalyticsOldUtil.updateNewReportV2(v2_report);
            if(user_filters != null){
                V2AnalyticsOldUtil.splitCreateUpdateAndDeleteFilter(v2_report.getReportId(), user_filters);
            }
        }
        else if(actionType != null && actionType.equals(FacilioConstants.ContextNames.DELETE)){
            Long reportId = (Long) context.get(FacilioConstants.ContextNames.REPORT_ID);
            V2ReportContext v2_reportContext = V2AnalyticsOldUtil.getV2Report(reportId);
            if(v2_reportContext != null) {
                V2AnalyticsOldUtil.deleteNewReportV2(v2_reportContext);
            }
            if(user_filters != null){
                V2AnalyticsOldUtil.CREDActionV2UserFilter(report.getId(), user_filters, FacilioConstants.ContextNames.DELETE);
            }
        }
        return false;
    }

    private void generateAndSetMeasureCriteria(V2ReportContext reportContext, String actionType)throws Exception
    {
        if(actionType != null && actionType.equals("update") && reportContext.getG_criteria() != null && reportContext.getG_criteria().getCriteriaId() != null && reportContext.getG_criteria().getCriteriaId() > 0)
        {
            Criteria old_global_criteria = CriteriaAPI.getCriteria(reportContext.getG_criteria().getCriteriaId());
            Criteria new_global_criteria = reportContext.getG_criteria().getCriteria();
            if(new_global_criteria == null || (new_global_criteria != null && new_global_criteria != null && !new_global_criteria.equals(old_global_criteria))){
                CriteriaAPI.deleteCriteria(reportContext.getG_criteria().getCriteriaId());
            }
        }
        if(reportContext.getG_criteria() != null && reportContext.getG_criteria().getCriteria() != null && !reportContext.getG_criteria().getCriteria().isEmpty()){
            long criteriaId = V2AnalyticsOldUtil.generateCriteriaId(reportContext.getG_criteria().getCriteria(), reportContext.getG_criteria().getModuleName());
            if(criteriaId > 0) {
                reportContext.setCriteriaId(criteriaId);
            }
        }
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

    public static List<V2AnalyticUserFilterContext> getReportV2UserFilter(V2ReportContext report)throws Exception
    {
        V2ReportFiltersContext global_crt_obj = report.getG_criteria();
        if(global_crt_obj != null && global_crt_obj.getAnalytics_user_filters() != null && global_crt_obj.getAnalytics_user_filters().size() > 0)
        {
            List<V2AnalyticUserFilterContext> user_filters = global_crt_obj.getAnalytics_user_filters();
            if(user_filters != null && user_filters.size() > 0)
            {
              return user_filters;
            }
        }
        return null;
    }


}
