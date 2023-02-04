package com.facilio.sandbox.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Context;

public class PreFillSandboxConfigCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        SandboxConfigContext sandboxConfig = (SandboxConfigContext) context.get(SandboxConstants.SANDBOX);

        if(sandboxConfig.getId() < 0) {
            sandboxConfig.setSysCreatedBy(AccountUtil.getCurrentUser().getOuid());
            sandboxConfig.setSysCreatedTime(DateTimeUtil.getCurrenTime());
            if(sandboxConfig.getStatusEnum() == null) {
                sandboxConfig.setStatus(SandboxConfigContext.SandboxStatus.CREATION_IN_PROGRESS);
            }
            if(!sandboxConfig.getSubDomain().startsWith("sandbox-")) {
                sandboxConfig.setSubDomain("sandbox-"+sandboxConfig.getSubDomain());
            }
        }
        else {
            sandboxConfig.setSysModifiedTime(DateTimeUtil.getCurrenTime());
            sandboxConfig.setSysModifiedBy(AccountUtil.getCurrentUser().getOuid());
        }

        sandboxConfig.getSandboxSharing().stream().forEach((sharingContext) -> {
            if (sharingContext.getId() < 0 && sharingContext.getSharedBy() < 0)
            {
                sharingContext.setSharedBy(AccountUtil.getCurrentUser().getOuid());
            }
        });

        return false;
    }
}
