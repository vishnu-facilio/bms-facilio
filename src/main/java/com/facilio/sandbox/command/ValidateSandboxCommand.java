package com.facilio.sandbox.command;

import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class ValidateSandboxCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        SandboxConfigContext sandboxConfig = (SandboxConfigContext) context.get(SandboxConstants.SANDBOX);
        V3Util.throwRestException(StringUtils.isEmpty(sandboxConfig.getName()), ErrorCode.VALIDATION_ERROR, "Sandbox name cannot be null");

        if(sandboxConfig.getId() > 0) {
            SandboxConfigContext existingSandbox = SandboxAPI.getSandboxById(sandboxConfig.getId());
            V3Util.throwRestException(existingSandbox == null, ErrorCode.VALIDATION_ERROR, "Invalid Sandbox details passed");
        }else{
            V3Util.throwRestException(StringUtils.isEmpty(sandboxConfig.getSubDomain()), ErrorCode.VALIDATION_ERROR, "Sandbox domain cannot be null");
            V3Util.throwRestException(CollectionUtils.isEmpty(sandboxConfig.getSandboxSharing()), ErrorCode.VALIDATION_ERROR, "Sandbox sharing cannot be empty");
            V3Util.throwRestException((sandboxConfig.getSandboxSharing().stream().anyMatch(value -> value.getTypeEnum() != SingleSharingContext.SharingType.USER)), ErrorCode.VALIDATION_ERROR, "Sandbox can be shared only to users");
        }
        return false;
    }
}
