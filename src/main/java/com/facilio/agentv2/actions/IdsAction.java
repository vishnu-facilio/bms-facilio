package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.point.PointsAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class IdsAction extends AgentActionV2
{
    private static final Logger LOGGER = LogManager.getLogger(IdsAction.class.getName());

    @NotNull
    private JSONObject jsonObject;

    public JSONObject getJsonObject() { return jsonObject; }
    public void setJsonObject(JSONObject jsonObject) { this.jsonObject = jsonObject; }



    public String deleteControllers(){
        try {
            if (ControllerApiV2.deleteControllers(getIds())) {
                setResult(AgentConstants.RESULT, SUCCESS);
            } else {
                setResult(AgentConstants.RESULT, ERROR);
            }
        }catch (Exception e){
            LOGGER.info("Exception while deleting controller"+e.getMessage());
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }


    public String discoverDevicePoints(){
        try{
            List<Long> ids = getIds();
            LOGGER.info(" ids ->"+ids);
            if( !ids.isEmpty() ){
                if(FieldDeviceApi.discoverPoints(ids)){
                    setResult(AgentConstants.RESULT,SUCCESS);
                    return SUCCESS;
                }
            }else {
                setResult(AgentConstants.RESULT,ERROR);
                setResult(AgentConstants.EXCEPTION," Ids can't be empty ");
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while discovering points "+e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        setResult(AgentConstants.RESULT,ERROR);
        return SUCCESS;
    }


    List<Long> getIds() {
        if (notNull(getJsonObject()) && (!jsonObject.isEmpty())) {
            if (containsValueCheck(AgentConstants.RECORD_IDS, jsonObject)) {
                List<Long> ids = (List<Long>) jsonObject.get(AgentConstants.RECORD_IDS);
                return ids;
            }
        }
        return new ArrayList<>();
    }


    public String configureController(){
        try{
            List<Long> ids = getIds();

            if( ! ids.isEmpty()){
                    if(containsValueCheck(AgentConstants.TYPE,getJsonObject()) ){
                        int controllerType = ((Number)getJsonObject().get(AgentConstants.TYPE)).intValue();
                        if( controllerType > 0){
                            PointsAPI.configureControllerAndPoints(ids, FacilioControllerType.valueOf(controllerType));
                            setResult(AgentConstants.RESULT,SUCCESS);
                        }else {
                            throw new Exception(" controller type can'e be less than 1");
                        }
                    }else {
                        throw new Exception(" controller type missing");
                    }
            }else {
                throw new Exception(" ids can't be empty");
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while configuring controller",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

    public String deleteAgent() { //TODO test
        List<Long> ids = getIds();
        if (!ids.isEmpty()) {
            if (AgentApiV2.deleteAgent(ids)) {
                setResult(AgentConstants.RESULT, SUCCESS);
                return SUCCESS;
            }
            setResult(AgentConstants.RESULT, ERROR);
            return SUCCESS;
        } else {
            setResult(AgentConstants.EXCEPTION, "agentIds can't be empty");
            setResult(AgentConstants.RESULT, ERROR);
            LOGGER.info("Exception occurred while deleting agent and agentIds can't be empty");
        }
        return ERROR;
    }

    public String shutDownAgent() {
        LOGGER.info(" shutting down agent");
        List<Long> ids = getIds();
        if ((ids != null) && (!ids.isEmpty())) {
            for (Long id : ids) {
                AgentMessenger.shutDown(id);
            }
            setResult(AgentConstants.RESULT, SUCCESS);
            return SUCCESS;
        }
        setResult(AgentConstants.EXCEPTION, "AgentIds empty");
        setResult(AgentConstants.RESULT, ERROR);
        return SUCCESS;
    } //TODO to test



    public String edit() {
        try {
            if (getJsonObject().containsKey(AgentConstants.AGENT_ID)) {
                long agentId = (long) getJsonObject().get(AgentConstants.AGENT_ID);
                setResult(AgentConstants.RESULT, AgentApiV2.editAgent(AgentApiV2.getAgent(agentId), getJsonObject()));
                setResult(AgentConstants.RESULT, SUCCESS);
            } else {
                throw new Exception("Agent Id not present in payload ->" + getJsonObject());
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while editing agent"+e.getMessage());
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }


    public String updateController() {
        try {
            if (containsValueCheck(AgentConstants.CONTROLLER_ID, getJsonObject())) {
                long controllerId = (long) getJsonObject().get(AgentConstants.CONTROLLER_ID);
                if (ControllerApiV2.editController(controllerId, getJsonObject())) {
                    setResult(AgentConstants.RESULT, SUCCESS);
                } else {
                    setResult(AgentConstants.RESULT, ERROR);
                    setResult(AgentConstants.EXCEPTION, "Exception while updating controller");
                }
            } else {
                setResult(AgentConstants.RESULT, ERROR);
                setResult(AgentConstants.EXCEPTION, "Wrong inputs");
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while updating controller",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }

    public String deleteDevice(){
        try{
            if(FieldDeviceApi.deleteDevices(getIds())>0){
                setResult(AgentConstants.RESULT,SUCCESS);
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting agentDevices count",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }



}
