package com.facilio.bmsconsoleV3.commands.reports;

import com.facilio.command.FacilioCommand;
import com.facilio.report.context.ReportFolderContext;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetReportFoldersCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception{

        boolean isCustomModule = (boolean) context.get("isCustomModule");
        Boolean isWithReport = (Boolean) context.get("isWithReport");
        Long webTabId = (Long) context.get("webTabId");
        if(isCustomModule) {
            List<ReportFolderContext> reportFolders = ReportUtil.getAllCustomModuleReportFolder(isWithReport, null);
            context.put("folders", reportFolders);
        }
        else
        {
            String moduleName = (String) context.get("moduleName");
            Boolean isPivot = (Boolean) context.get("isPivot");
            List<ReportFolderContext> reportFolders = ReportUtil.getAllReportFolder(moduleName, isWithReport, null, isPivot, webTabId);
            context.put("folders", reportFolders);
        }
        return false;
    }
}
