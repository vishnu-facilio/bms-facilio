package com.facilio.agentv2.migration;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.json.annotations.JSON;

@Getter @Setter
public class AgentMigrationAction extends FacilioAction {

    private long sourceOrgId;
    private long targetOrgId;
    private long sourceAgentId;
    private long targetAgentId;
    private long migrationId;

    public String migrateAgent() throws Exception {
        FacilioChain chain = getAgentMigrationChain();
        FacilioContext context = chain.getContext();
        context.put(AgentMigrationConstants.SOURCE_ORG_ID, sourceOrgId);
        context.put(AgentMigrationConstants.SOURCE_AGENT_ID, sourceAgentId);
        context.put(AgentMigrationConstants.TARGET_ORG_ID, targetOrgId);
        context.put(AgentMigrationConstants.TARGET_AGENT_ID, targetAgentId);
        context.put("migrationId", migrationId);
        chain.execute();

        setResult("result", "success");
        return SUCCESS;
    }

    @JSON(serialize = false)
    private FacilioChain getAgentMigrationChain() {
        FacilioChain chain = FacilioChain.getTransactionChain(600000);
        chain.addCommand(new ValidateAgentMigrationCommand());
        chain.addCommand(new AgentMigrationCommand());
        return chain;
    }

    private boolean copyModule = false;

    public String migrateReadingMeta() throws Exception {

        FacilioChain chain = FacilioChain.getTransactionChain(600000);
        chain.addCommand(new ReadingMetaMigration());
        FacilioContext context = chain.getContext();
        context.put(AgentMigrationConstants.SOURCE_ORG_ID, sourceOrgId);
        context.put(AgentMigrationConstants.TARGET_ORG_ID, targetOrgId);
        context.put("copyModule", copyModule);
        chain.execute();

        setResult("result", "success");

        return SUCCESS;
    }

}
