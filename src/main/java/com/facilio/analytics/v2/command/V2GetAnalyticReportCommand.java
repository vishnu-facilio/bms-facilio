package com.facilio.analytics.v2.command;

import com.facilio.analytics.v2.V2AnalyticsOldUtil;
import com.facilio.analytics.v2.context.V2AnalyticsContextForDashboardFilter;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldType;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataPointContext;
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
                report.setDateRange(new DateRange(db_filter.getTimeFilter().getStartTime(), db_filter.getTimeFilter().getEndTime()));
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
            if(db_filter != null && db_filter.getPagination() != null && !db_filter.getPagination().isEmpty()){
                v2_reportContext.setPagination(db_filter.getPagination());
            }
        }
        for(ReportDataPointContext dataPoint : report.getDataPoints())
        {
            if(dataPoint.getxAxis() != null && (dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE) && dataPoint.getyAxis() != null && (dataPoint.getyAxis().getDataTypeEnum() == FieldType.BOOLEAN || dataPoint.getyAxis().getDataTypeEnum() == FieldType.ENUM) && (dataPoint.getyAxis().getAggr() == BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue()))
            {
                dataPoint.setHandleEnum(true);
            }
            else if (dataPoint.getxAxis() != null && (dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dataPoint.getxAxis().getDataTypeEnum() == FieldType.DATE) && (dataPoint.getyAxis() != null && (dataPoint.getyAxis().getDataTypeEnum() == FieldType.BOOLEAN || dataPoint.getyAxis().getDataTypeEnum() == FieldType.ENUM)) &&  (dataPoint.getyAxis().getAggrEnum() == null || dataPoint.getyAxis().getAggr() == BmsAggregateOperators.CommonAggregateOperator.ACTUAL.getValue())) {
                dataPoint.getyAxis().setAggr(null);
                dataPoint.setHandleEnum(true);
            }
        }
        return false;
    }
}
