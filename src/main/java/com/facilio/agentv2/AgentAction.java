package com.facilio.agentv2;

import com.facilio.agentv2.actions.AgentActionV2;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.device.FieldDeviceApi;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

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



    public String getOrgControllerCount() {
        try {
            setResult(AgentConstants.RESULT, SUCCESS);
            setResult(AgentConstants.DATA, ControllerApiV2.getCountForOrg());
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting total controller count ");
            setResult(AgentConstants.EXCEPTION,e.getMessage());
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



    public String setValue(){
        setResult(AgentConstants.RESULT,"No implementation");
        return SUCCESS;
    }


  /*  public String configurePoint(){
        try {
            if (checkValue(getPointId())) {
                setResult(AgentConstants.RESULT, PointsAPI.configurePoint(getPointId()));
            } else {
                LOGGER.info("Exception occurred while getting points and agentId can't be null or empty->"+agentId);
                setResult(AgentConstants.EXCEPTION, "Exception, pointId not correct");
                setResult(AgentConstants.RESULT,ERROR);
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting points ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }*/


    public String countDevices(){
        try{
                setResult(AgentConstants.DATA, FieldDeviceApi.getDeviceCount());
                setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting agentDevices count",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }






    //__________________________________________________
    // general utilities



}
