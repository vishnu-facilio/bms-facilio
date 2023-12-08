package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentAction;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.fw.BeanFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;

public class UpdateAgentAction extends AgentAction {

    private static final Logger LOGGER = LogManager.getLogger(UpdateAgentAction.class.getName());


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
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            if (agentBean.editAgent(agentBean.getAgent(getAgentId()), getToUpdate(), false)) {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResponseCode(HttpURLConnection.HTTP_OK);
            }else {
                setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED);
                setResult(AgentConstants.RESULT,ERROR);
            }
        }catch (Exception e){
            LOGGER.error("Exception occurred while editing agent", e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }



}
