package com.facilio.bmsconsole.commands;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Objects;

public class DeleteCloudAgentJobCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> ids = (List<Long>) context.get(AgentConstants.RECORD_IDS);
        for (Long id : ids) {
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            if (Objects.requireNonNull(agentBean.getAgent(id)).getAgentType() == AgentType.CLOUD.getKey()) {
                FacilioTimer.deleteJob(id, "CloudAgent");
            }
        }
        return false;
    }
}
