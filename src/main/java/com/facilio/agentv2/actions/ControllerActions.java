package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.bacnet.BacnetIpControllerContext;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.controller.ControllerUtilV2;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;

public class ControllerActions extends AgentActionV2 {

    private static final Logger LOGGER = LogManager.getLogger(ControllerActions.class.getName());

    @NotNull
    @Min(value = 0, message = "Controller Id can't be less than 1")
    private Long controllerId;

    public Long getControllerId() {
        return controllerId;
    }

    public void setControllerId(Long controllerId) {
        this.controllerId = controllerId;
    }

    public String getControllerUsingId() {
        try {
            Controller controller = ControllerApiV2.getControllerFromDb(getControllerId());
            if (controller != null) {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResponseCode(HttpURLConnection.HTTP_OK);
                LOGGER.info(" controller instance of "+ (controller instanceof BacnetIpControllerContext));
                LOGGER.info(" controller controller to json  "+controller.toJSON());
                LOGGER.info(" controller controller get child json  "+controller.getChildJSON());
                setResult(AgentConstants.DATA, controller);
                return SUCCESS;
            } else {
                setResult(AgentConstants.RESULT, NONE);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting controller using orgId", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
        }
        setResult(AgentConstants.DATA, new JSONObject());
        setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        return SUCCESS;
    }


    public String discoverPoints() {
        try {
            if (ControllerUtilV2.discoverPoints(getControllerId())) {
                setResult(AgentConstants.RESULT, SUCCESS);
            } else {
                setResult(AgentConstants.RESULT, ERROR);
            }
            setResponseCode(HttpURLConnection.HTTP_OK);
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.info("Exception while discoverPoints", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String resetController() {
        try {
            ControllerApiV2.resetController(getControllerId());
            setResult(AgentConstants.RESULT, SUCCESS);
            setResponseCode(HttpURLConnection.HTTP_OK);
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.info("Exception occurred while reset controller");
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        setResult(AgentConstants.RESULT, ERROR);
        return SUCCESS;
    }


}
