package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.service.FacilioService;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class VersionLogAction extends AgentIdAction {

    private static final Logger LOGGER = LogManager.getLogger(VersionLogAction.class.getName());


    @NotNull
    @Min(1)
    private long versionId;

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public String addAgentVerionLog() {
        try {
            //FacilioService.runAsService(()->AgentVersionApi.logAgentUpgrateRequest(getAgentId(), getVersionId(), authKey));
            ok();
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            LOGGER.info("Exception while adding version log ", e);
            internalError();
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        return SUCCESS;
    }

    public String upgradeAgent() {
        try {
        	FacilioService.runAsService(()-> upgradeAgentVersion());
        	setResult(AgentConstants.DATA, SUCCESS);
        	ok();
        } catch (Exception e) {
            LOGGER.info("Exception while upgrading agent version", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

	private void upgradeAgentVersion() throws Exception {
		FacilioChain chain = TransactionChainFactory.upgradeAgentChain();
		FacilioContext context = chain.getContext();
		context.put(AgentConstants.VERSION_ID, versionId);
		context.put(AgentConstants.AGENT_ID, getAgentId());
		chain.execute();
	}
}
