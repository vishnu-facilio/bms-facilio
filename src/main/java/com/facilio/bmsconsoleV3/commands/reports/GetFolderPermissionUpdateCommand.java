package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleFactory;
import com.facilio.report.context.ReportFolderContext;
import org.apache.commons.chain.Context;

public class GetFolderPermissionUpdateCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        ReportFolderContext reportFolder = (ReportFolderContext) context.get("reportFolder");
        long folderId = (long) context.get("folderId");
        if (!reportFolder.getIds().isEmpty() && reportFolder.getIds() != null) {
            SharingAPI.deleteSharingForParent(reportFolder.getIds(), ModuleFactory.getReportSharingModule());
        }
        SharingAPI.addSharing(reportFolder.getReportSharing(), folderId, ModuleFactory.getReportSharingModule());
        context.put("result", "success");
        return false;
    }
}
