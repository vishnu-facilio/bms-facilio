package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.bmsconsole.context.WidgetChartContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class ConstructReportDeleteCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<WidgetChartContext> widgetCharts = null;
        boolean isDeleteWithWidget = (boolean) context.get("isDeleteWithWidget");
        Long reportId = (Long) context.get("reportId");
        ReportContext reportContext = ReportUtil.getReport(reportId);
        if (!isDeleteWithWidget) {
            widgetCharts = DashboardUtil.getWidgetFromDashboard(reportId, true);
        }
        if (widgetCharts == null || widgetCharts.isEmpty()) {
            ReportUtil.deleteReport(reportId);
            context.put("success", "success");
        } else {
            context.put("success", "failure");
        }
        if(reportContext.getModule() != null) {
            context.put("moduleName", reportContext.getModule().getDisplayName());
            context.put("reportName", reportContext.getName());
        }
        return false;
    }
}
