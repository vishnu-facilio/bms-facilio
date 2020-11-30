package com.facilio.agentv2.actions;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.workflows.context.WorkflowContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddAgentAction extends AgentActionV2
{
    private static final Logger LOGGER = LogManager.getLogger(AddAgentAction.class.getName());


    @NotNull
    @Size(min = 3,max = 100,message = "Agent name must have 3-100 characters")
    private String agentName;

    @Min(value = 10,message = "Data interval can't be less than 10")
    @NotNull
    private Long dataInterval;

    @NotNull
    @Min(value = 1,message = "Site can't be less than 1")
    private Long siteId;

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }

    public Long getDataInterval() { return dataInterval; }
    public void setDataInterval(Long dataInterval) { this.dataInterval = dataInterval; }

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type;}

    private String type;
    
    

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @NotNull @Size(min = 2,max = 100)
    private String displayName;

    private WorkflowContext workflow;
    public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}

    public Integer getAgentType() {
        return agentType;
    }

    public void setAgentType(Integer agentType) {
        this.agentType = agentType;
    }

    private Integer agentType;

	public String createAgent() {
        try {
            FacilioChain addAgentChain = TransactionChainFactory.createAgentChain();
            FacilioContext context = addAgentChain.getContext();
            FacilioAgent agent = new FacilioAgent();
            agent.setName(getAgentName());
            agent.setInterval(getDataInterval());
            agent.setSiteId(getSiteId()); //TODO validate SITE ID.
            agent.setType(getType());
            agent.setAgentType(getAgentType());
            agent.setDisplayName(getDisplayName());
            agent.setWorkflow(getWorkflow());
            if (AgentType.valueOf(getAgentType()) != AgentType.Custom) {
                agent.setProcessorVersion(2);
            }
            context.put(AgentConstants.AGENT,agent);
            addAgentChain.execute();
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while adding agent",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
            internalError();
        }
        return SUCCESS;
    }
}
