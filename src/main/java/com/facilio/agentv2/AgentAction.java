package com.facilio.agentv2;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.actions.AgentActionV2;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class AgentAction extends AgentActionV2 {
    private static final Logger LOGGER = LogManager.getLogger(AgentAction.class.getName());


    public String listAgents() {
        List<FacilioAgent> agents = AgentApiV2.getAgents();
        JSONArray jsonArray = new JSONArray();
        for (FacilioAgent agent : agents) {
            jsonArray.add(agent.toJSON());
        }
        setResult(AgentConstants.DATA, jsonArray);
        setResult(AgentConstants.RESULT, SUCCESS);
        return SUCCESS;
    }

    public String download() {
        //TODO not yet implemented
        setResult(SUCCESS, " no implementation");
        return SUCCESS;
    }


    public String getAgentCount() {
        setResult(AgentConstants.RESULT, AgentApiV2.getAgentCount());
        return SUCCESS;
    }


    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    private Long agentId;

    public String getControllerCount() {
        try {
            setResult(AgentConstants.RESULT, SUCCESS);
            if( (getAgentId() == null) || getAgentId() < 1){
                LOGGER.info(" getting org controller count ");
                setResult(AgentConstants.DATA, ControllerApiV2.getCountForOrg());
            }else {
                LOGGER.info(" getting agent controller count ");
                setResult(AgentConstants.DATA, ControllerApiV2.getCountForAgent(agentId));
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting total controller count ");
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

    private Long controllerId;
    private Long deviceId;

    public Long getControllerId() {
        return controllerId;
    }

    public void setControllerId(Long controllerId) {
        this.controllerId = controllerId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getControllerType() {
        return controllerType;
    }

    public void setControllerType(Integer controllerType) {
        this.controllerType = controllerType;
    }

    private Integer controllerType;

    public String listPoints() {
        JSONArray pointData = new JSONArray();
        try {
            List<Point> points = new ArrayList<>();
            if((controllerId != null) && (controllerId > 0) && (controllerType != null) && (controllerType > 0) && (deviceId == null)){
                LOGGER.info(" getting controller points");
                points = PointsAPI.getControllerPoints(FacilioControllerType.valueOf(controllerType), getControllerId());
            }
            else if((deviceId != null) && (deviceId>0) && (controllerType != null) && (controllerType > 0)){
                LOGGER.info(" getting device points");
               points =  PointsAPI.getDevicePoints(getDeviceId(), getControllerType());
            }else {
                if((controllerId == null)&&(deviceId == null)){
                    LOGGER.info(" getting all points");
                    points = PointsAPI.getAllPoints(null,-1);
                }
            }
            LOGGER.info(" in device action " + points);
            if (!points.isEmpty()) {
                for (Point point : points) {
                    pointData.add(point.toJSON());
                }
            }
            setResult(AgentConstants.DATA, pointData);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting points", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
        }
        return SUCCESS;
    }

    public String deleteController(){
        try{

        }catch (Exception e){
            LOGGER.info("Exception while deleting controller",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }



    public String countDevices() {
        try {
            if ((agentId != null) && (agentId > 0)) {
                //TYPE AND AGENT ID
                if ((controllerType != null) && (controllerType > 0)) {
                    setResult(AgentConstants.DATA, FieldDeviceApi.getTypeDeviceCount(getAgentId(), FacilioControllerType.valueOf(getControllerType())));
                }
                // AGENT ID ALONE
                else {
                    setResult(AgentConstants.DATA, FieldDeviceApi.getAgentDeviceCount(getAgentId()));
                }
            }
            // TYPE ALONE
            else if ((controllerType != null) && (controllerType > 0)) {
                setResult(AgentConstants.DATA, FieldDeviceApi.getTypeDeviceCount(getAgentId(), FacilioControllerType.valueOf(getControllerType())));
            }
            //DEVICE POINT COUNT
            else {
                setResult(AgentConstants.DATA, FieldDeviceApi.getDeviceCount());
            }
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting agentDevices count", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        return SUCCESS;
    }


    public String PointsCount(){
        LOGGER.info(" getting points ");
        try{
            long count = 0;
            if((controllerId != null) && (controllerId > 0) && (deviceId == null)){
                LOGGER.info(" contid ");
                count = PointsAPI.getPointsCount( getControllerId(), -1);
            }else if((deviceId != null) && (deviceId > 0) && (controllerId != null)){
                    LOGGER.info(" device id  ");
                    count = PointsAPI.getPointsCount(-1, deviceId);
            }else {
                LOGGER.info(" no point id");
                count = PointsAPI.getPointsCount(-1,-1);
            }

            setResult(AgentConstants.DATA,count);
            setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting all point for agent->"+controllerId+" -",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }



    //__________________________________________________
    // general utilities



}
