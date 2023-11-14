package com.facilio.sandbox.command;

import com.facilio.backgroundactivity.util.BackgroundActivityService;
import com.facilio.backgroundactivity.util.ChildActivityService;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.LongRunningTaskHandler;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.time.ZoneId;

public class AddDefaultSignupDataAndCreationInstallationCommand extends FacilioCommand {
    BackgroundActivityService backgroundActivityService = null;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long sandboxId = (long) context.get(SandboxConstants.SANDBOX_ID);
        long productionOrgId = (long) context.get(PackageConstants.SOURCE_ORG_ID);
        long fileId = SandboxAPI.getRecentPackageId((String) context.get(PackageConstants.SANDBOX_DOMAIN_NAME));
        JSONObject content = new JSONObject();
        content.put(SandboxConstants.SANDBOX_ID, sandboxId);
        content.put(PackageConstants.SOURCE_ORG_ID, productionOrgId);
        if(fileId != -1L){
            content.put("methodName", "addDefaultSignupDataToSandbox");
            content.put(SandboxConstants.SANDBOX_PROGRESS, PackageUtil.SandboxProgressCheckPointType.ORG_SIGNUP_STARTED.getIntVal());
        }else {
            content.put("methodName", "createPackageForSandboxData");
            content.put(SandboxConstants.SANDBOX_PROGRESS, PackageUtil.SandboxProgressCheckPointType.PACKAGE_CREATION_STARTED_ON_SETUP.getIntVal());
        }
        content.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata"))+"");
        backgroundActivityService= new BackgroundActivityService(sandboxId,"sandbox","sandbox Id: #"+sandboxId, "####Call to Ims Started for sandbox--"+ sandboxId);
        Messenger.getMessenger().sendMessage(new Message()
                .setKey(LongRunningTaskHandler.KEY+"/"+ DateTimeUtil.getCurrenTime())
                .setOrgId(productionOrgId)
                .setContent(content));
        return false;
    }
}
