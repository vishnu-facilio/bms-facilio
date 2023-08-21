package com.facilio.sandbox.command;

import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.LongRunningTaskHandler;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CreateAndInstallPackageCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long sourceOrgId = (long) context.get(PackageConstants.SOURCE_ORG_ID);
        long sandboxOrgId = (long) context.get(PackageConstants.TARGET_ORG_ID);
        boolean fromAdminTool = (Boolean) context.get(PackageConstants.FROM_ADMIN_TOOL);
        long sandboxId = SandboxAPI.getSandboxIdBySourceAndTargetOrgId(sourceOrgId, sandboxOrgId);
        List<Integer> skipComponents = (List<Integer>)context.getOrDefault(PackageConstants.SKIP_COMPONENTS, new ArrayList<>());
        JSONObject content = new JSONObject();
        content.put(PackageConstants.DISPLAY_NAME, "package_" + sourceOrgId + "_" + System.currentTimeMillis());
        content.put(PackageConstants.SOURCE_ORG_ID, sourceOrgId);
        content.put(PackageConstants.TARGET_ORG_ID, sandboxOrgId);
        content.put(PackageConstants.SKIP_COMPONENTS, skipComponents);
        content.put(SandboxConstants.SANDBOX_ID, sandboxId);
        content.put(PackageConstants.FROM_ADMIN_TOOL, fromAdminTool);
        content.put("methodName", "createPackageForSandboxData");
        content.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata"))+"");
        Messenger.getMessenger().sendMessage(new Message()
                .setKey(LongRunningTaskHandler.KEY+"/"+ DateTimeUtil.getCurrenTime())
                .setOrgId(sourceOrgId)
                .setContent(content));
        return false;
    }
}
