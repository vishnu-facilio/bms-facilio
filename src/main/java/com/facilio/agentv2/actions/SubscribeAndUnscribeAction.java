package com.facilio.agentv2.actions;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointsAPI;

public class SubscribeAndUnscribeAction extends AgentActionV2{
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(SubscribeAndUnscribeAction.class.getName());
     private List<Map<String,Object>> instances = null;
     private Integer controllerType;
    
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

	 public String subscribePoints() {
	        try {
	            setResult(AgentConstants.RESULT, PointsAPI.subscribeUnsubscribePoints(getInstances(), FacilioControllerType.valueOf(getControllerType()), FacilioCommand.SUBSCRIBE));
	            setResult(AgentConstants.RESULT, SUCCESS);
	            ok();
	        } catch (Exception e) {
	            LOGGER.info("Exception occurred while subscribePoints point -> " + getInstances() + " -,", e);
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
	            LOGGER.info("Exception occurred while unsubscribePoints point -> " + getInstances() + " -,", e);
	            setResult(AgentConstants.EXCEPTION, e.getMessage());
	            setResult(AgentConstants.RESULT, ERROR);
	            internalError();
	        }
	        return SUCCESS;
	    }
}
