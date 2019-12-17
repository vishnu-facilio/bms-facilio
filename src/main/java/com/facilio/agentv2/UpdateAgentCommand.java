package com.facilio.agentv2;

import com.facilio.agentv2.commands.AgentV2Command;
import org.apache.commons.chain.Context;

public class UpdateAgentCommand extends AgentV2Command {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        if( (context != null) && ( ! context.isEmpty() ) && ( context.containsKey(AgentConstants.AGENT) ) ){
            return AgentApiV2.updateAgent((FacilioAgent) context.get(AgentConstants.AGENT));
        }
        return false;
    }

}
