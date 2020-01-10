package com.facilio.agentv2.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import org.apache.commons.chain.Context;

public class CreatePolicyCommand extends AgentV2Command {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsCheck(AgentConstants.AGENT, context)) {
            AccountUtil.getCurrentAccount();
        }
        return false;
    }
}
