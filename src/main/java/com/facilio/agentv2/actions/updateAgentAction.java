package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentAction;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;

public class updateAgentAction extends AgentAction {

    private static final Logger LOGGER = LogManager.getLogger(updateAgentAction.class.getName());


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
            if (AgentApiV2.editAgent(AgentApiV2.getAgent(getAgentId()), getToUpdate(), false)) {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResponseCode(HttpURLConnection.HTTP_OK);
            }else {
                setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED);
                setResult(AgentConstants.RESULT,ERROR);
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while editing agent"+e.getMessage());
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }



}
