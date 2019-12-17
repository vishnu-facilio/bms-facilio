package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.device.FieldDeviceApi;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class AgentDeviceAction extends AgentIdAction {

    private static final Logger LOGGER = LogManager.getLogger(TypeControllerAction.class.getName());


    public Integer getControllerType() { return controllerType; }

    public void setControllerType(Integer controllerType) { this.controllerType = controllerType; }

    @NotNull
    @Min(value = 0,message = "Controller Type can't be less than 1")
    private Integer controllerType;

    public String countTypeDevice(){
        try{
                setResult(AgentConstants.DATA, FieldDeviceApi.getTypeDeviceCount(getAgentId(), FacilioControllerType.valueOf(getControllerType())));
                setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting agentDevices count",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }
}
