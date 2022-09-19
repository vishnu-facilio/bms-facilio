package com.facilio.bmsconsole.commands;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.CloudAgentUtil;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.controller.Controller;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

public class AddAgentServiceControllerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Controller record = (Controller) context.get("record");
        long agentId = record.getAgentId();
        AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
        FacilioAgent agent = agentBean.getAgent(agentId);
        AgentType agentType = AgentType.valueOf(agent.getAgentType());
        if(agentType.isAgentService()) {
            Controller controller = (Controller) context.get(FacilioConstants.ContextNames.RECORD);
            CloudAgentUtil.addController(controller,controller.getControllerType(),agent.getName());
        }
        return false;
    }
}
