package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointsAPI;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class controllerTypeIdsAction extends IdsAction {

    private static final Logger LOGGER = LogManager.getLogger(controllerTypeIdsAction.class.getName());

    private List<Map<String,Object>> instances = null;
    
    
    public List<Map<String, Object>> getInstances() {
		return instances;
	}

	public void setInstances(List<Map<String, Object>> instances) {
		this.instances = instances;
	}

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
    
    
    private Boolean logical=false;

    public Boolean getLogical() {
		return logical;
	}

	public void setLogical(Boolean logical) {
		this.logical = logical;
	}

    List<Long> controllerIds;

    public List<Long> getControllerIds() {
        return controllerIds;
    }

    public void setControllerIds(List<Long> controllerIds) {
        this.controllerIds = controllerIds;
    }

    @Getter
    @Setter
    private int interval = -1;

	public String configurePointsAndMakeController() {
        try {
            List<Long> pointIds = getRecordIds();
            if (!pointIds.isEmpty()) {
                PointsAPI.configurePointsAndMakeController(pointIds, FacilioControllerType.valueOf(getControllerType()), getLogical(), getInterval());
                setResponseCode(HttpURLConnection.HTTP_OK);
                setResult(AgentConstants.RESULT, SUCCESS);
                ok();
            } else {
                throw new Exception(" ids can't be empty");
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while configuring controller", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String removePoints() {
        try {
            List<Long> pointIds = getRecordIds();
            if (PointsAPI.deletePointsChain(pointIds, FacilioControllerType.valueOf(getControllerType()))) {
                ok();
                setResult(AgentConstants.RESULT, SUCCESS);
                return SUCCESS;
            } else {
                setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED);
            }
        } catch (Exception e) {
            internalError();
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
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while unconfiguring point -> " + getRecordIds() + " -,", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
        }
        return SUCCESS;
    }

    public String subscribePoints() {
        try {
            setResult(AgentConstants.RESULT, PointsAPI.subscribeUnsubscribePoints(getInstances(), FacilioControllerType.valueOf(getControllerType()), FacilioCommand.SUBSCRIBE));
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while unconfiguring point -> " + getRecordIds() + " -,", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
        }
        return SUCCESS;
    }

    public String unsubscribePoints() {
        try {
            setResult(AgentConstants.RESULT, PointsAPI.subscribeUnsubscribePoints(getInstances(), FacilioControllerType.valueOf(getControllerType()), FacilioCommand.UNSUBSCRIBE));
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while unconfiguring point -> " + getRecordIds() + " -,", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
        }
        return SUCCESS;
    }
}
