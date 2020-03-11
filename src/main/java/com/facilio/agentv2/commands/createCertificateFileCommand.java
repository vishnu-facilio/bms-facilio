package com.facilio.agentv2.commands;

import com.facilio.agentv2.AgentConstants;
import org.apache.commons.chain.Context;

public class createCertificateFileCommand extends AgentV2Command {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsCheck(AgentConstants.NAME, context)) {
            //AwsUtil.signUpIotToKinesis(AccountUtil.getCurrentOrg().getDomain(), AccountUtil.getCurrentOrg().getName(), AgentType.BACnet.toString());
        }
        return false;
    }
}
