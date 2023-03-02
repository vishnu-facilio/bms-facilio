package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;

public class UpdateControllerAction extends AgentActionV2 {

    private static final Logger LOGGER = LogManager.getLogger(UpdateControllerAction.class.getName());

    @NotNull
    @Min(1)
    private Long controllerId;
//    @NotNull
    private JSONObject toUpdate;

    public Long getControllerId() {
        return controllerId;
    }

    public void setControllerId(Long controllerId) {
        this.controllerId = controllerId;
    }

    public JSONObject getToUpdate() {
        return toUpdate;
    }
    public void setToUpdate(JSONObject toUpdate) {
        this.toUpdate = toUpdate;
    }

    public String updateController() {
        try {
                if (AgentConstants.getControllerBean().editController(getControllerId(),getToUpdate())) {
                    setResult(AgentConstants.RESULT, SUCCESS);
                    setResponseCode(HttpURLConnection.HTTP_OK);
                } else {
                    setResult(AgentConstants.RESULT, ERROR);
                    setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
                    setResult(AgentConstants.EXCEPTION, "Exception while updating controller");
                }

        }catch (Exception e){
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            LOGGER.info("Exception occurred while updating controller",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }
}
