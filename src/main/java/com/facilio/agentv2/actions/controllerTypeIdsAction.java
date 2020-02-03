package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointsAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.util.List;

public class controllerTypeIdsAction extends IdsAction {

    private static final Logger LOGGER = LogManager.getLogger(controllerTypeIdsAction.class.getName());


    public Integer getControllerType() {
        return controllerType;
    }

    public void setControllerType(Integer controllerType) {
        this.controllerType = controllerType;
    }

    @NotNull
    @Min(value = 0, message = "controllerType can't be less than 1")
    @Max(99)
    private Integer controllerType;

    public String configurePointsAndMakeController() {
        try {
            List<Long> pointIds = getRecordIds();
            if (!pointIds.isEmpty()) {
                PointsAPI.configurePointsAndMakeController(pointIds, FacilioControllerType.valueOf(getControllerType()));
                setResponseCode(HttpURLConnection.HTTP_OK);
                setResult(AgentConstants.RESULT, SUCCESS);
            } else {
                throw new Exception(" ids can't be empty");
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while configuring controller", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        return SUCCESS;
    }

    public String removePoints() {
        try {
            List<Long> pointIds = getRecordIds();
            if (PointsAPI.deletePointsChain(pointIds, FacilioControllerType.valueOf(getControllerType()))) {
                setResponseCode(HttpURLConnection.HTTP_OK);
                setResult(AgentConstants.RESULT, SUCCESS);
                return SUCCESS;
            } else {
                setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED);
            }
        } catch (Exception e) {
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            LOGGER.info("Exception while deleting point", e);
            setResult(AgentConstants.RESULT, ERROR);
            return SUCCESS;
        }
        setResult(AgentConstants.RESULT, ERROR);
        return SUCCESS;
    }

    public String unConfigurePoints() {
        try {
            setResult(AgentConstants.RESULT, PointsAPI.unConfigurePointsChain(getRecordIds(), FacilioControllerType.valueOf(getControllerType())));
            setResult(AgentConstants.RESULT, SUCCESS);
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while unconfiguring point -> " + getRecordIds() + " -,", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String subscribePoints() {
        try {
            setResult(AgentConstants.RESULT, PointsAPI.subscribeUnsubscribePoints(getRecordIds(), FacilioControllerType.valueOf(getControllerType()), FacilioCommand.SUBSCRIBE));
            setResult(AgentConstants.RESULT, SUCCESS);
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while unconfiguring point -> " + getRecordIds() + " -,", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }

    public String unsubscribePoints() {
        try {
            setResult(AgentConstants.RESULT, PointsAPI.subscribeUnsubscribePoints(getRecordIds(), FacilioControllerType.valueOf(getControllerType()), FacilioCommand.UNSUBSCRIBE));
            setResult(AgentConstants.RESULT, SUCCESS);
            setResponseCode(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while unconfiguring point -> " + getRecordIds() + " -,", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        return SUCCESS;
    }
}
