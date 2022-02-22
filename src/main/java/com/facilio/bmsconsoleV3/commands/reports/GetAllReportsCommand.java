package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.command.FacilioCommand;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetAllReportsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception{
        Integer reportType = (Integer) context.get("reportType");
        Integer moduleName = (Integer) context.get("moduleName");
        if(reportType != null){
            List<ReportContext> reports = ReportUtil.fetchAllReportsByType(reportType);
            context.put("reports", reports);
        }
        return false;
    }
}
