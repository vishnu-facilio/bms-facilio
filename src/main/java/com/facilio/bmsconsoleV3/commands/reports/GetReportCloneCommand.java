package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;

public class GetReportCloneCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long reportId = (Long) context.get(FacilioConstants.ContextNames.REPORT_ID);
        ReportContext reportContext = (ReportContext) ReportUtil.getReport(reportId);
        String cloned_report_name  = (String) context.get("cloned_report_name");
        Long target_app_id = (Long) context.get("target_app_id");
        Long cloned_app_id = (Long) context.get("cloned_app_id");
        context.put(FacilioConstants.ContextNames.REPORT, reportContext);
        ReportUtil.cloneReport(reportId, target_app_id, cloned_app_id);
        return false;
    }
}
