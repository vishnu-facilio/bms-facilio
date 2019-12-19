package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class updateAgentAction extends IdsAction {

    private static final Logger LOGGER = LogManager.getLogger(updateAgentAction.class.getName());


    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    @NotNull
    @Min(value = 1,message = "agentId can't be less than 1")
    private Long agentId;

    public JSONObject getToUpdate() {
        return toUpdate;
    }

    public void setToUpdate(JSONObject toUpdate) {
        this.toUpdate = toUpdate;
    }

    @NotNull
    private JSONObject toUpdate;


    public String edit() {
        try {
                setResult(AgentConstants.RESULT, AgentApiV2.editAgent(AgentApiV2.getAgent(getAgentId()),getToUpdate()));
                setResult(AgentConstants.RESULT, SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while editing agent"+e.getMessage());
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }



}
