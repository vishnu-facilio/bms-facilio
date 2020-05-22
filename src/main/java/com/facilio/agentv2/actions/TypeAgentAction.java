package com.facilio.agentv2.actions;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FieldUtil;

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
            setResponseCode(HttpURLConnection.HTTP_OK);
            setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while discoverControllers command ",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

    public String getControllerOfTypeForAgent(){
        try{
            JSONArray controllerArray = new JSONArray();
            GetControllerRequest getControllerRequest = new GetControllerRequest()
                    .withAgentId(getAgentId())
                    .ofType(FacilioControllerType.valueOf(getControllerType()))
                    .withPagination(constructListContext(new FacilioContext()));
            Map<String, Controller> controllerData =  getControllerRequest.getControllersMap();
            if ((controllerData != null) && (!controllerData.isEmpty())) {
                JSONObject object = new JSONObject();
                for (Controller controller : controllerData.values()) {
                    object.put(AgentConstants.CHILDJSON, controller.getChildJSON());
                    object.putAll(controller.toJSON());
                    controllerArray.add(object);
                }
                /*setResult(AgentConstants.RESULT, SUCCESS);*/
                setResult(AgentConstants.DATA, controllerArray);
                setResponseCode(HttpURLConnection.HTTP_OK);
                return SUCCESS;
            }else {
                /*setResult(AgentConstants.RESULT, NONE);*/
                setResult(AgentConstants.DATA,controllerArray);
                setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
            }
            setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            LOGGER.info("Exception occurred while getting controllers for type"+getControllerType()+" and agentID "+getAgentId());
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }
    
//    public String getControllerTypeList() throws Exception {
//        try {
//            List<Device> devices = FieldUtil.getAsBeanListFromMapList(FieldDeviceApi.getDevicesControllerType(getAgentId(),getControllerType(), constructListContext(new FacilioContext())), Device.class);
//            setResult(AgentConstants.DATA, devices);
//            ok();
//        } catch (Exception e) {
//            LOGGER.info("Exception occurred while getting device controller Type ", e);
//            setResult(AgentConstants.RESULT, ERROR);
//            setResult(AgentConstants.EXCEPTION, e.getMessage());
//            internalError();
//        }
//    	return SUCCESS;
//    }
}
