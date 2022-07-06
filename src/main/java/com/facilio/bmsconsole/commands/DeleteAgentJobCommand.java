package com.facilio.bmsconsole.commands;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

import java.util.List;

public class DeleteAgentJobCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> ids = (List<Long>) context.get(AgentConstants.RECORD_IDS);
        for (Long id : ids) {
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            FacilioAgent agent = agentBean.getAgent(id);
            if (agent != null) {
                FacilioTimer.deleteJob(id, FacilioConstants.Job.POINTS_DATA_MISSING_ALARM_JOB_NAME);

                if (agent.getAgentType() == AgentType.CLOUD.getKey()){
                    FacilioTimer.deleteJob(id, FacilioConstants.Job.CLOUD_AGENT_JOB_NAME);
                }
            }
        }
        return false;
    }
}
