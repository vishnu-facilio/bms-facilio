package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;

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
            LOGGER.info("Exception occurred while discoverControllers command ",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

    public String getControllerOfTypeForAgent(){
        try{
            JSONArray controllerArray = new JSONArray();
            Map<String, Controller> controllerData =  ControllerApiV2.getControllersFromDb(getAgentId(), FacilioControllerType.valueOf(getControllerType()),constructListContext(new FacilioContext()));
            if ((controllerData != null) && (!controllerData.isEmpty())) {
                JSONObject object = new JSONObject();
                for (Controller controller : controllerData.values()) {
                    object.put(AgentConstants.CHILDJSON, controller.getChildJSON());
                    object.putAll(controller.toJSON());
                    controllerArray.add(object);
                }
                /*setResult(AgentConstants.RESULT, SUCCESS);*/
                setResult(AgentConstants.DATA, controllerArray);
                return SUCCESS;
            }else {
                /*setResult(AgentConstants.RESULT, NONE);*/
                setResult(AgentConstants.DATA,controllerArray);
            }
            setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting controllers for type"+getControllerType()+" and agentID "+getAgentId());
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

}
