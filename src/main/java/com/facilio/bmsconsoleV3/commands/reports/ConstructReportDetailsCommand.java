package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.command.FacilioCommand;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;

public class ConstructReportDetailsCommand extends FacilioCommand
{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long reportId = (Long) context.get("reportId");
        if(reportId != null)
        {
           ReportContext reportContext = ReportUtil.getReport(reportId);
           context.put("reportContext", reportContext);
        }
        return false;
    }
}
