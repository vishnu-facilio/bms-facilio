package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdateAgentConnectedStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean isConnected = (Boolean) context.get(AgentConstants.IS_ACTIVE_UPDATE_VALUE);
        List<FacilioAgent> agentList = (List<FacilioAgent>) context.get(AgentConstants.AGENT_LIST);
        for (FacilioAgent agent : agentList){
            agent.setConnected(isConnected);
            AgentApiV2.updateAgent(agent);
        }
        return false;
    }
}
