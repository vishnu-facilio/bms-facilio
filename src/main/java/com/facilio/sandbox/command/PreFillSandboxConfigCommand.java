package com.facilio.sandbox.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

public class PreFillSandboxConfigCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        SandboxConfigContext sandboxConfig = (SandboxConfigContext) context.get(SandboxConstants.SANDBOX);

        if(sandboxConfig.getId() < 0) {
            sandboxConfig.setSysCreatedBy(AccountUtil.getCurrentUser().getOuid());
            sandboxConfig.setSysCreatedTime(DateTimeUtil.getCurrenTime());
            if(sandboxConfig.getStatusEnum() == null) {
                sandboxConfig.setStatus(SandboxConfigContext.SandboxStatus.SANDBOX_ORG_CREATION_IN_PROGRESS);
            }
            sandboxConfig.setSubDomain(sandboxConfig.getSubDomain().replaceAll("[^a-zA-Z0-9_]+", "_").replaceAll("_+", "_"));
            if(!SandboxAPI.checkSandboxDomainIfExist(sandboxConfig.getSubDomain())){
                V3Util.throwRestException(true, ErrorCode.DEPENDENCY_EXISTS, "Sandbox domain Already Exists");
            }
            sandboxConfig.getSandboxSharing().stream().forEach((sharingContext) -> {
                if (sharingContext.getId() < 0 && sharingContext.getSharedBy() < 0)
                {
                    sharingContext.setSharedBy(AccountUtil.getCurrentUser().getOuid());
                }
            });
        }
        else {
            sandboxConfig.setSysModifiedTime(DateTimeUtil.getCurrenTime());
            sandboxConfig.setSysModifiedBy(AccountUtil.getCurrentUser().getOuid());
        }
        return false;
    }
}
