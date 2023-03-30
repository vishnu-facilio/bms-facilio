package com.facilio.agentv2.migration;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.migration.beans.AgentMigrationBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;

public class ValidateAgentMigrationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long sourceOrgId = (long) context.get(AgentMigrationConstants.SOURCE_ORG_ID);
        long sourceAgentId = (long) context.get(AgentMigrationConstants.SOURCE_AGENT_ID);
        long targetOrgId = (long) context.get(AgentMigrationConstants.TARGET_ORG_ID);
        long targetAgentId = (long) context.get(AgentMigrationConstants.TARGET_AGENT_ID);
        long migrationId = (long) context.get("migrationId");

        if (sourceOrgId <= 0 || sourceAgentId <= 0 || targetOrgId <= 0 ||
                targetAgentId <= 0 || migrationId <= 0 ) {
            throw new IllegalArgumentException("Send all valid params");
        }

        AgentBean sourceBean = (AgentBean) BeanFactory.lookup("AgentBean", true, sourceOrgId);
        FacilioAgent sourceAgent = sourceBean.getAgent(sourceAgentId);

        AgentBean targetBean = (AgentBean) BeanFactory.lookup("AgentBean", true, targetOrgId);
        FacilioAgent targetAgent = targetBean.getAgent(targetAgentId);

        if (sourceAgent.getAgentTypeEnum() != targetAgent.getAgentTypeEnum()) {
            throw new IllegalArgumentException("Source and target agent should be of same type");
        }

        return false;
    }
}
