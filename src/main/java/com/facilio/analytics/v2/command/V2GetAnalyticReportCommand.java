package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2AnalyticsContextForDashboardFilter;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;

public class V2GetAnalyticReportCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        Long reportId = (Long) context.get("reportId");
        V2AnalyticsContextForDashboardFilter db_filter = (V2AnalyticsContextForDashboardFilter) context.get("db_filter");
        ReportContext report = ReportUtil.getReport(reportId);
        context.put(FacilioConstants.ContextNames.REPORT, report);
        V2ReportContext v2_reportContext = V2AnalyticsOldUtil.getV2Report(reportId);

        if(v2_reportContext != null)
        {
            v2_reportContext.setName(report.getName());
            v2_reportContext.setFolderId(report.getReportFolderId());
            v2_reportContext.setDescription(report.getDescription());
            context.put("v2_report", v2_reportContext);
            DateOperators dateOperators = v2_reportContext.getTimeFilter().getDateOperatorEnum();
            Integer offset = v2_reportContext.getTimeFilter().getOffset();
            if(db_filter != null && db_filter.getTimeFilter() != null && db_filter.getTimeFilter().getStartTime() > 0 && db_filter.getTimeFilter().getEndTime() > 0)
            {
                v2_reportContext.getTimeFilter().setStartTime(db_filter.getTimeFilter().getStartTime());
                v2_reportContext.getTimeFilter().setEndTime(db_filter.getTimeFilter().getEndTime());
            }
            else if(dateOperators != null && !(dateOperators == DateOperators.BETWEEN || dateOperators == DateOperators.NOT_BETWEEN))
            {
                DateRange range = dateOperators.getRange(offset != null && offset >= 0 ? offset.toString() : null);
                if(range == null){
                    range = dateOperators.getRange("");
                }
                if(range != null) {
                    v2_reportContext.getTimeFilter().setStartTime(range.getStartTime());
                    v2_reportContext.getTimeFilter().setEndTime(range.getEndTime());
                }
            }
            if(db_filter != null && db_filter.getDb_user_filter() != null){
                context.put(FacilioConstants.ContextNames.REPORT_USER_FILTER_VALUE, db_filter.getDb_user_filter());
            }
        }

        return false;
    }
}
