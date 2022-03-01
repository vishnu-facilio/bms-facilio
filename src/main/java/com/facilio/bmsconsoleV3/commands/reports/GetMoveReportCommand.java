package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.command.FacilioCommand;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;

public class GetMoveReportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        ReportContext report = (ReportContext)  context.get("report");
        if(report != null)
        {
            ReportUtil.moveReport(report);
            context.put("result", "success");
        }
         return false;
    }
}
