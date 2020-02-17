package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.HttpURLConnection;

public class AddAgentAction extends AgentActionV2
{
    private static final Logger LOGGER = LogManager.getLogger(AddAgentAction.class.getName());


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


    public String createAgent() {
        try {
            FacilioChain addAgentChain = TransactionChainFactory.createAgentChain();
            FacilioContext context = addAgentChain.getContext();
            FacilioAgent agent = new FacilioAgent();
            agent.setName(getAgentName());
            agent.setInterval(getDataInterval());
            agent.setSiteId(getSiteId());
            context.put(AgentConstants.AGENT,agent);
            addAgentChain.execute();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AgentConstants.AGENT_ID,agent.getId());
            if (context.containsKey(AgentConstants.DOWNLOAD_LINK) )  {
                jsonObject.put(AgentConstants.DOWNLOAD_LINK,context.get(AgentConstants.DOWNLOAD_LINK));
            }
            setResult(AgentConstants.DATA, agent.getId());
            setResult(AgentConstants.RESULT, SUCCESS);
            setResponseCode(HttpURLConnection.HTTP_CREATED);
        }catch (Exception e){
            LOGGER.info("Exception while adding agent",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }
}
