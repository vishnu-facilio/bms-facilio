package com.facilio.sandbox.command;

import com.facilio.command.FacilioCommand;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

public class ChangeSandboxStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SandboxConfigContext sandboxConfig = (SandboxConfigContext) context.get(SandboxConstants.SANDBOX);

        SandboxConfigContext existingSandbox = SandboxAPI.getSandboxById(sandboxConfig.getId());
        V3Util.throwRestException(existingSandbox == null, ErrorCode.VALIDATION_ERROR, "Invalid Sandbox details passed");

        V3Util.throwRestException(existingSandbox.getStatusEnum() != SandboxConfigContext.SandboxStatus.ACTIVE && existingSandbox.getStatusEnum() != SandboxConfigContext.SandboxStatus.INACTIVE,
                ErrorCode.VALIDATION_ERROR, "Status cannot be changed now");

        V3Util.throwRestException(sandboxConfig.getStatus() <= 0 ||
                        (sandboxConfig.getStatusEnum() != SandboxConfigContext.SandboxStatus.ACTIVE && sandboxConfig.getStatusEnum() != SandboxConfigContext.SandboxStatus.INACTIVE),
                ErrorCode.VALIDATION_ERROR, "Invalid status");

        SandboxAPI.changeSandboxStatus(sandboxConfig);

        return false;
    }
}
