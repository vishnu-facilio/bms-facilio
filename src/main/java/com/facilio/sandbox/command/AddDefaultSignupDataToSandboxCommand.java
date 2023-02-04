package com.facilio.sandbox.command;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxConstants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

@Log4j
public class AddDefaultSignupDataToSandboxCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        SandboxConfigContext sandboxConfig = (SandboxConfigContext) context.get(SandboxConstants.SANDBOX);

        FacilioChain signupChain = TransactionChainFactory.getOrgSignupChain();
        FacilioContext signupContext = signupChain.getContext();
        signupContext.put("orgId", sandboxConfig.getSandboxOrgId());
        signupContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, context.get(FacilioConstants.ContextNames.SIGNUP_INFO));
        signupChain.execute();

        return false;
    }

}
