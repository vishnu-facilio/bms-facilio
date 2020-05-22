package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

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

    public Integer getControllerType() { return controllerType; }

    public void setControllerType(Integer controllerType) { this.controllerType = controllerType; }

    @NotNull
    @Min(value = 0,message = "Controller type can't be less than 1")
    private Integer controllerType;

    private Long agentId =-1l;

    public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getControllerUsingId() {
        try {
            List<Map<String, Object>> controller = ControllerApiV2.getControllerData(null,getControllerId(),constructListContext(new FacilioContext()));
            if (controller != null) {
                setResult(AgentConstants.RESULT, SUCCESS);
                ok();
                setResult(AgentConstants.DATA, controller);
                return SUCCESS;
            } else {
                noContent();
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting controller using orgId", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        setResult(AgentConstants.DATA, new JSONObject());
        return SUCCESS;
    }


    public String discoverPoints() {
        try {
            if (ControllerUtilV2.discoverPoints(getControllerId())) {
                setResult(AgentConstants.RESULT, SUCCESS);
            }
            ok();
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.info("Exception while discoverPoints", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
        }
        return SUCCESS;
    }

    public String resetController() {
        try {
            ControllerApiV2.resetController(getControllerId());
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.info("Exception occurred while reset controller");
            internalError();
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        setResult(AgentConstants.RESULT, ERROR);
        return SUCCESS;
    }

    public String getConfiguredPoints(){
            GetPointRequest getPointRequest = new GetPointRequest()
                    .filterConfigurePoints();
            try {
                getPointRequest.withControllerId(getControllerId());
                List<Map<String, Object>> points = getPointRequest.getPointsData();
                setResult(AgentConstants.DATA,points);
                ok();
            } catch (Exception e) {
                LOGGER.info("Exception  occurred while getting points ",e);
                setResult(AgentConstants.EXCEPTION,e.getMessage());
                internalError();
            }
        return SUCCESS;
    }

    public String getSubscribedPoints(){
        GetPointRequest getPointRequest = new GetPointRequest()
                .filterSubsctibedPoints();
        try {
            getPointRequest.withControllerId(getControllerId());
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            setResult(AgentConstants.DATA,points);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception  occurred while getting points ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getUnconfiguredPoints(){
        GetPointRequest getPointRequest = new GetPointRequest()
                .filterUnConfigurePoints();
        try {
            getPointRequest.withControllerId(getControllerId());
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            setResult(AgentConstants.DATA,points);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception  occurred while getting points ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getCommissionedPoints(){
        GetPointRequest getPointRequest = new GetPointRequest()
                .filterCommissionedPoints();
        try {
            getPointRequest.withControllerId(getControllerId());
            List<Map<String, Object>> points = getPointRequest.getPointsData();
            setResult(AgentConstants.DATA,points);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception  occurred while getting points ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
    public String getControllers() {
        try {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());
			context.put(AgentConstants.SEARCH_KEY, getName());
			context.put(AgentConstants.CONTROLLER_TYPE, getControllerType());
			List<Map<String, Object>> controllers = ControllerApiV2.getControllerDataForAgent(getAgentId(),context);
            setResult(AgentConstants.DATA, controllers);
            ok();
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            LOGGER.info("exception while fetching controller data fro agent " + agentId + " ", e);
        }
        return SUCCESS;
    }
}
