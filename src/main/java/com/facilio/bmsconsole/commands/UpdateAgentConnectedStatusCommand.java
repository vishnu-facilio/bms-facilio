package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdateAgentConnectedStatusCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean isConnected = (Boolean) context.get(AgentConstants.IS_ACTIVE_UPDATE_VALUE);
        List<FacilioAgent> agentList = (List<FacilioAgent>) context.get(AgentConstants.AGENT_LIST);
        AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");

        for (FacilioAgent agent : agentList){
            agent.setConnected(isConnected);
            agentBean.updateAgent(agent);
        }
        return false;
    }
}
