package com.facilio.analytics.v2.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.analytics.v2.context.V2ReportContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReadingAnalysisContext;
import com.facilio.report.context.ReportContext;
import org.apache.commons.chain.Context;

public class V2PopulateAnalyticsDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        V2ReportContext v2_report_context = (V2ReportContext) context.get("report_v2");
        ReportContext report_context = new ReportContext();
        if(v2_report_context != null)
        {
            report_context.setAppId(AccountUtil.getCurrentApp() != null ? AccountUtil.getCurrentApp().getId() : -1L);
            report_context.setName(v2_report_context.getName());
            report_context.setDescription(v2_report_context.getDescription());
            report_context.setChartState(v2_report_context.getChartState());
            report_context.setModuleName(FacilioConstants.ContextNames.ENERGY_DATA_READING);
            report_context.setAnalyticsType(ReadingAnalysisContext.AnalyticsType.PORTFOLIO);
            report_context.setReportFolderId(v2_report_context.getFolderId());
            report_context.setDateOperator(v2_report_context.getTimeFilter().getDateOperator());
            context.put(FacilioConstants.ContextNames.REPORT, report_context);
        }
        return false;
    }
}
