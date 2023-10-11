package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
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
        ReportContext report = ReportUtil.getReport(reportId);
        context.put(FacilioConstants.ContextNames.REPORT, report);
        V2ReportContext v2_reportContext = V2AnalyticsOldUtil.getV2Report(reportId);

        if(v2_reportContext != null){
            v2_reportContext.setName(report.getName());
            v2_reportContext.setFolderId(report.getReportFolderId());
            v2_reportContext.setDescription(report.getDescription());
            context.put("v2_report", v2_reportContext);
            DateOperators dateOperators = v2_reportContext.getTimeFilter().getDateOperatorEnum();
            Integer offset = v2_reportContext.getTimeFilter().getOffset();
            if(dateOperators != null && !(dateOperators == DateOperators.BETWEEN || dateOperators == DateOperators.NOT_BETWEEN))
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
        }

        return false;
    }
}
