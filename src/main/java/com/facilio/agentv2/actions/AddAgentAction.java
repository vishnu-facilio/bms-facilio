package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddAgentAction extends AgentActionV2
{
    @NotNull
    @Size(min = 3,max = 20,message = "Agent name must have 3-15 characters")
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


    public String addAgent() {
        FacilioAgent agent = new FacilioAgent();
        agent.setName(getAgentName());
        agent.setInterval(getDataInterval());
        agent.setSiteId(getSiteId());
        long agentId = AgentApiV2.addAgent(agent);
        agent.setId(agentId);
        setResult(AgentConstants.DATA, agentId);
        setResult(AgentConstants.RESULT, SUCCESS);

        return SUCCESS;
    }
}
