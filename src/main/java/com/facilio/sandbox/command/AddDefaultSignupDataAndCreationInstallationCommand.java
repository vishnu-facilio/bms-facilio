package com.facilio.sandbox.command;

import com.facilio.backgroundactivity.util.BackgroundActivityAPI;
import com.facilio.backgroundactivity.util.BackgroundActivityService;
import com.facilio.backgroundactivity.util.BackgroundActivityUtil;
import com.facilio.backgroundactivity.util.ChildActivityService;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.LongRunningTaskHandler;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.time.ZoneId;

public class AddDefaultSignupDataAndCreationInstallationCommand extends FacilioCommand {
    BackgroundActivityService backgroundActivityService = null;

    ChildActivityService childActivityService = null;
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SandboxConfigContext sandboxConfig = (SandboxConfigContext) context.get(SandboxConstants.SANDBOX);
        JSONObject content = new JSONObject();
        content.put(SandboxConstants.SANDBOX_ID, sandboxConfig.getId());
        content.put(PackageConstants.SOURCE_ORG_ID, sandboxConfig.getOrgId());
        content.put(PackageConstants.TARGET_ORG_ID, sandboxConfig.getSandboxOrgId());
        content.put(FacilioConstants.ContextNames.SIGNUP_INFO, context.get(FacilioConstants.ContextNames.SIGNUP_INFO));
        content.put(SandboxConstants.SANDBOX_ORG, context.get(SandboxConstants.SANDBOX_ORG));
        content.put(SandboxConstants.SANDBOX_ORG_USER, context.get(SandboxConstants.SANDBOX_ORG_USER));
        content.put(SandboxConstants.PRODUCTION_DOMAIN_NAME, context.get(SandboxConstants.PRODUCTION_DOMAIN_NAME));
        content.put("methodName", "addDefaultSignupDataToSandbox");
        content.put("startTime", DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata"))+"");
        backgroundActivityService= new BackgroundActivityService(sandboxConfig.getId(),"sandbox","sandbox Id: #"+sandboxConfig.getId(),"Call to Ims started for sandboxId:" + + sandboxConfig.getId());
        Messenger.getMessenger().sendMessage(new Message()
                .setKey(LongRunningTaskHandler.KEY+"/"+ DateTimeUtil.getCurrenTime())
                .setOrgId(sandboxConfig.getSandboxOrgId())
                .setContent(content));
        backgroundActivityService = new BackgroundActivityService(BackgroundActivityAPI.parentActivityForRecordIdAndType(sandboxConfig.getId(),"sandbox"));
        if(backgroundActivityService != null) {
            backgroundActivityService.updateActivity(0,"####Sandbox Org Signup Started for sandboxId--" + sandboxConfig.getId());
        }
        return false;
    }
}
