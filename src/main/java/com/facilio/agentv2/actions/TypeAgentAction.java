package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TypeAgentAction extends AgentIdAction {

    private static final Logger LOGGER = LogManager.getLogger(TypeControllerAction.class.getName());


    public Integer getControllerType() { return controllerType; }

    public void setControllerType(Integer controllerType) { this.controllerType = controllerType; }

    @Min(value = 0,message = " controllerType can't be less than 1")
    @NotNull
    private  Integer controllerType;

    public String discoverControllers(){
        try {
            setResult(AgentConstants.DATA, AgentMessenger.discoverController(getAgentId(), FacilioControllerType.valueOf(getControllerType())));
            setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getThreadDump command ",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

}
