package com.facilio.bmsconsole.commands;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Objects;

public class DeleteCloudAgentJobCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> ids = (List<Long>) context.get(AgentConstants.RECORD_IDS);
        for (Long id : ids) {
            if (Objects.requireNonNull(AgentApiV2.getAgent(id)).getAgentType() == AgentType.REST.getKey()) {
                FacilioTimer.deleteJob(id, "CloudAgent");
            }
        }
        return false;
    }
}
