package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.logs.LogsApi;
import com.facilio.agentv2.sqlitebuilder.SqliteBridge;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AgentIdAction extends AgentActionV2 {
    private static final Logger LOGGER = LogManager.getLogger(AgentIdAction.class.getName());


    @NotNull
    private Long agentId;


    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String devices(){
        try {
            List<Device> devices = FieldUtil.getAsBeanListFromMapList(FieldDeviceApi.getDevices(getAgentId(), null), Device.class);
            List<Device> newDevices = new ArrayList<>();
            newDevices.add(devices.get(0));
            setResult(AgentConstants.DATA, devices);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting devices");
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }


    public String pingAgent() {
        try {
            LOGGER.info(" ping agent " + getAgentId());
            AgentMessenger.pingAgent(getAgentId());
            setResult(AgentConstants.RESULT, SUCCESS);
            setResponseCode(HttpURLConnection.HTTP_OK);
            return SUCCESS;
        }catch (Exception e){
            LOGGER.info("Exception occurred while pingAgent",e);
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
            return SUCCESS;
        }
    }

    public String getAgentUsingId() {
        try {
            FacilioAgent agent = AgentApiV2.getAgent(getAgentId());
            if (agent != null) {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResult(AgentConstants.DATA, agent.toJSON());
                setResponseCode(HttpURLConnection.HTTP_OK);
            } else {
                setResult(AgentConstants.RESULT, ERROR);
                setResult(AgentConstants.EXCEPTION, "no such agent");
                setResponseCode(HttpURLConnection.HTTP_NO_CONTENT);
                return ERROR;
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting agent ->"+agentId+"  ,",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            return ERROR;
        }
        return SUCCESS;

    }

    public String getControllerCount(){
        try {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResult(AgentConstants.DATA, ControllerApiV2.getCountForAgent(getAgentId()));
            return SUCCESS;
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting controller count for agentId->"+getAgentId());
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

    public String getControllers() {
        JSONArray controllerArray = new JSONArray();
        try {
                Map<String, Controller> controllerData = ControllerApiV2.getAllControllersFromDb(getAgentId());
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

        }catch (Exception e){
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }



    public String countAgentDevices(){
        try{
                setResult(AgentConstants.DATA, FieldDeviceApi.getAgentDeviceCount(getAgentId()));
                setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting agentDevices count",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

    public String getjvmStatus(){
        try {
                setResult(AgentConstants.DATA,AgentMessenger.getJVMStatus(getAgentId()));
                setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getJVM command ",e);
            setResult(AgentConstants.RESULT,ERROR);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
        }
        return SUCCESS;
    }

    public String getThreadDump(){
        try {
            setResult(AgentConstants.DATA, AgentMessenger.getThreadDump(agentId));
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getThreadDump command ", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        return SUCCESS;
    }

    public String getSqlite() {
        try {
            long fileId = SqliteBridge.migrateAgentData(getAgentId());
            if (fileId > 0) {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResult(AgentConstants.FILE_ID, fileId);
                return SUCCESS;
            } else {
                setResult(AgentConstants.RESULT, ERROR);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while migrating data", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        return SUCCESS;
    }

    public String migrate(){
        try{
            SqliteBridge.migrateToNewAgent(getAgentId());
            setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            LOGGER.info("Exception occurred while migrating to new Agent "+getAgentId());
        }
        return SUCCESS;
    }

    public String getAgentLogs() {
        try {
            setResult(AgentConstants.DATA, LogsApi.getMetrics(agentId, -1, -1));
            setResult(AgentConstants.RESULT, SUCCESS);
            return SUCCESS;
        }catch (Exception e){
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            LOGGER.info("Exception whiel getting agent logs for ->"+agentId+"  ",e);
        }
        return SUCCESS;
    }

    public String getAgentMetrics() {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();

            jsonObject.put(AgentConstants.ID, 1);
            jsonObject.put(AgentConstants.ORGID, 1);
            jsonObject.put(AgentConstants.AGENT_ID, getAgentId());
            jsonObject.put(AgentConstants.PUBLISH_TYPE, 1);
            jsonObject.put(AgentConstants.NUMBER_OF_MSGS, 101010);
            jsonObject.put(AgentConstants.SIZE, 808080);
            jsonObject.put(AgentConstants.LAST_MODIFIED_TIME, 978546214);
            jsonObject.put(AgentConstants.CREATED_TIME, 978545214);

            array.add(jsonObject);
            array.add(jsonObject);
            array.add(jsonObject);

            setResult(AgentConstants.DATA, array);
            setResult(AgentConstants.RESULT, SUCCESS);
            return SUCCESS;
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            LOGGER.info("Exception while getting agentMetrics for ->" + getAgentId());
        }
        return SUCCESS;
    }

   /* public String listAgentPointsCount(){
        try{
                long count = PointsAPI.getAgentPointsCount(getAgentId(), -1);
                setResult(AgentConstants.DATA,count);
                setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting all point for agent->"+agentId+" -",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }*/
}
