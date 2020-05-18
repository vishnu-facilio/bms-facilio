package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.iotmessage.IotMessageApiV2;
import com.facilio.agentv2.logs.LogsApi;
import com.facilio.agentv2.metrics.MetricsApi;
import com.facilio.agentv2.modbusrtu.ModbusImportUtils;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.agentv2.sqlitebuilder.SqliteBridge;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
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

    public String devices() {
        try {
            List<Device> devices = FieldUtil.getAsBeanListFromMapList(FieldDeviceApi.getDevices(getAgentId(), constructListContext(new FacilioContext())), Device.class);
            List<Device> newDevices = new ArrayList<>();
            if (!devices.isEmpty()) {
                newDevices.add(devices.get(0));
            }
            setResult(AgentConstants.DATA, devices);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting devices ", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String devicesCount() {
        try {
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
            List<Map<String, Object>> devices = FieldDeviceApi.getDevices(getAgentId(), context);
            long count = 0;
            if (!devices.isEmpty()) {
                count = devices.size();
            }
            
            setResult(AgentConstants.DATA, count);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting devices ", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }


    public String pingAgent() throws Exception {
        try {
            LOGGER.info(" ping agent " + getAgentId());
            AgentMessenger.pingAgent(getAgentId());
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.info("Exception occurred while pingAgent", e);
            internalError();
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            return SUCCESS;
        }
    }

    public String getAgentUsingId() {
        try {
            FacilioAgent agent = AgentApiV2.getAgent(getAgentId());
            if (agent != null) {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResult(AgentConstants.DATA, agent.toJSON());
                ok();
            } else {
                setResult(AgentConstants.RESULT, ERROR);
                setResult(AgentConstants.EXCEPTION, "no such agent");
                noContent();
                return SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting agent ->" + agentId + "  ,", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            return ERROR;
        }
        return SUCCESS;

    }

    public String getControllerCount() {
        try {
            setResult(AgentConstants.RESULT, SUCCESS);
            setResult(AgentConstants.DATA, ControllerApiV2.getCountForAgent(Arrays.asList(getAgentId())));
            ok();
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting controller count for agentId->" + getAgentId());
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

  /*  public String getControllers() {
        JSONArray controllerArray = new JSONArray();
        try {
            LOGGER.info(" getting controller for agentId "+agentId);
                //Map<String, Controller> controllerData = ControllerApiV2.getControllersForAgent(getAgentId(),constructListContext(new FacilioContext()));
                Map<String, Controller> controllerData = ControllerApiV2.getControllerDataForAgent(getAgentId(),constructListContext(new FacilioContext()));
                if ((controllerData != null) && (!controllerData.isEmpty())) {
                    JSONObject object = new JSONObject();
                    for (Controller controller : controllerData.values()) {
                        JSONObject pointCountData = PointsAPI.getControllerPointsCountData(controller.getId());
                        if(pointCountData != null && ( ! pointCountData.isEmpty()) ){
                            object.putAll(pointCountData);
                        }else {
                            LOGGER.info(" points count null ");
                        }
                        object.put(AgentConstants.CHILDJSON, controller.getChildJSON());
                        object.putAll(controller.toJSON());
                        controllerArray.add(object);
                    }
                    *//*setResult(AgentConstants.RESULT, SUCCESS);*//*
                    setResult(AgentConstants.DATA, controllerArray);
                    return SUCCESS;
                }else {
                    *//*setResult(AgentConstants.RESULT, NONE);*//*
                    setResult(AgentConstants.DATA,controllerArray);
                }

        }catch (Exception e){
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }
*/

    public String getControllers() {
        try {
            List<Map<String, Object>> controllers = ControllerApiV2.getControllerDataForAgent(agentId, constructListContext(new FacilioContext()));
            setResult(AgentConstants.DATA, controllers);
            ok();
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            LOGGER.info("exception while fetching controller data fro agent " + agentId + " ", e);
        }
        return SUCCESS;
    }


    public String countAgentDevices() {
        try {
            setResult(AgentConstants.DATA, FieldDeviceApi.getAgentDeviceCount(Arrays.asList(getAgentId())));
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting agentDevices count", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        return SUCCESS;
    }

    public String getjvmStatus() {
        try {
            setResult(AgentConstants.DATA, AgentMessenger.getJVMStatus(getAgentId()));
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getJVM command ", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getThreadDump() {
        try {
            setResult(AgentConstants.DATA, AgentMessenger.getThreadDump(agentId));
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getThreadDump command ", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getSqlite() {
        try {
            long fileId = SqliteBridge.migrateAgentData(getAgentId());
            if (fileId > 0) {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResult(AgentConstants.FILE_ID, fileId);
                ok();
                return SUCCESS;
            } else {
                internalError();
                setResult(AgentConstants.RESULT, ERROR);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while migrating data", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String migrate() {
        try {
            SqliteBridge.migrateToNewAgent(getAgentId());
            ok();
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            LOGGER.info("Exception occurred while migrating to new Agent " + getAgentId()+"  ",e);
        }
        return SUCCESS;
    }

    public String getAgentLogs() {
        try {
            FacilioContext context = constructListContext(new FacilioContext());

            List<Map<String, Object>> data = LogsApi.getLogs(agentId, context);
            setResult(AgentConstants.DATA, data);
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
            LOGGER.info("DATA : " + data);
            return SUCCESS;
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            LOGGER.info("Exception while getting agent logs for ->" + agentId + "  ", e);
        }
        return SUCCESS;
    }

    public String getLogCount() {
        try {
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.FETCH_COUNT, true);
            List<Map<String, Object>> data = LogsApi.getLogs(agentId, context);
            if (!data.isEmpty()) {
                setResult(AgentConstants.DATA, data.get(0).get(AgentConstants.ID));
            }
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
            return SUCCESS;
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            LOGGER.info("Exception while getting agent logs for ->" + agentId + "  ", e);
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
            ok();
            return SUCCESS;
        } catch (Exception e) {
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
            LOGGER.info("Exception while getting agentMetrics for ->" + getAgentId());
        }
        return SUCCESS;
    }

    public String getAgentOverView() {
        try {
            setResult(AgentConstants.DATA, AgentUtilV2.getAgentOverView(getAgentId()));
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting agent overview ", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
        }
        return SUCCESS;
    }

    public String getIotMessages() {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            result = IotMessageApiV2.listIotMessages(agentId, constructListContext(new FacilioContext()));
            setResult(AgentConstants.DATA, result);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting iot messages->" + getAgentId() + " ", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getIotMessageCount(){
        try{
            long count = IotMessageApiV2.getCount(agentId);
            setResult(AgentConstants.DATA,count);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception while getting iot message count",e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String getPointCount() {
        try {
            setResult(AgentConstants.DATA, PointsAPI.getPointsCountData(getAgentId()));
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception while getting point count for agent " + agentId);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
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

    public String getControllerFilter() {
        try {
            List<Map<String, Object>> agentControllerFilterData = ControllerApiV2.getAgentControllerFilterData(getAgentId());
            setResult(AgentConstants.DATA, agentControllerFilterData);
            ok();
            return SUCCESS;
        } catch (Exception e) {
            internalError();
            LOGGER.info("Excdeption occurred while getting controller filter", e);
        }
        return SUCCESS;
    }

    public String getDeviceFilter() {
        try {
            List<Map<String, Object>> deviceFilter = FieldDeviceApi.getDeviceFilterData(getAgentId());
            setResult(AgentConstants.DATA, deviceFilter);
            ok();
        } catch (Exception e) {
            internalError();
            LOGGER.info("Exception occurred while getting device filter ", e);
            internalError();
        }
        return SUCCESS;
    }

    public String getModbusDevice(){
        try{
            List<Map<String, Object>> deviceFilter = FieldDeviceApi.getModbusDeviceFilter(getAgentId());
            setResult(AgentConstants.DATA, deviceFilter);
            ok();
        }catch (Exception e){
            internalError();
            LOGGER.info("Exception occurred while getting device filter ", e);
            internalError();
        }
        return SUCCESS;
    }

    public String getMetrics() {
        try {
            List<Map<String, Object>> metrics = MetricsApi.getMetrics(getAgentId(),constructListContext(new FacilioContext()));
            ok();
            setResult(AgentConstants.DATA, metrics);
        } catch (Exception e) {
            LOGGER.info("Exception while getting metrics ", e);
        }
        return SUCCESS;
    }

    public String getPendingControllerImports(){
        try{
            List<Map<String, Object>> maps = ModbusImportUtils.getpendingControllerImports(getAgentId());
            setResult(AgentConstants.DATA,maps);
            ok();
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting pending controller imports for agentId "+getAgentId()+" ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String pendingDeviceImportCount(){
        try{
            setResult(AgentConstants.DATA,ModbusImportUtils.getPendingControllerImportCount(agentId));
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting pending point imports count for agentId "+getAgentId()+" ",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            internalError();
        }
        return SUCCESS;
    }
}
