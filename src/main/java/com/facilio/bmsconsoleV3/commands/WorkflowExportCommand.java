package com.facilio.bmsconsoleV3.commands;

import com.facilio.bundle.context.BundleFileContext;
import com.facilio.bundle.context.BundleFolderContext;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class WorkflowExportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<WorkflowUserFunctionContext> workflows = UserFunctionAPI.getAllFunctions();

        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(workflows), "Please add Functions to download script");

        BundleFolderContext rootFolder = new BundleFolderContext("Script_Bundle_" + DateTimeUtil.getFormattedTime(DateTimeUtil.getCurrenTime()));

        for (WorkflowUserFunctionContext workflow : workflows) {
            String workflowString = workflow.getWorkflowV2String();
            if (workflowString == null) {
                continue;
            }

            BundleFileContext file = new BundleFileContext(workflow.getNameSpaceName() + "_" + workflow.getLinkName(), "txt", workflowString);
            rootFolder.addFile(workflow.getNameSpaceName() + "_" + workflow.getLinkName(), file);
        }

        long fileId = BundleUtil.saveAsZipFile(rootFolder);

        context.put("downloadURL", fileId > 0 ? FacilioFactory.getFileStore().getDownloadUrl(fileId) : "v2 script is empty");

        return false;
    }

}
