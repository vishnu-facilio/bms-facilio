package com.facilio.util;

import com.facilio.agent.AgentKeys;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.iotmessage.IotMessage;
import com.facilio.agentv2.logs.LogsApi;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AckUtil
{
    private static final Logger LOGGER = LogManager.getLogger(AckUtil.class.getName());

    /**
     *
     * @param payLoad
     * @param orgId
     */
    public static void processAck(JSONObject payLoad, String agent, long orgId)  {
        try {
            long msgId = getMessageIdFromPayload(payLoad);
            String message = (String) payLoad.get(AgentKeys.MESSAGE);
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            JSONObject newObj = new JSONObject();
            newObj.putAll(payLoad);
            newObj.put("agent", agent);
            bean.acknowledgePublishedMessage(msgId, message, newObj);
        }catch(Exception e){
            LOGGER.info("EXxception occured",e);
        }
    }

    public static boolean processAgentAck(JSONObject payload, long agentId, long orgId) throws Exception {
                long msgId = getMessageIdFromPayload(payload);
                if(payload.containsKey(AgentConstants.STATUS)){
                    int status = ((Number) payload.get(AgentConstants.STATUS)).intValue();
                    long timeStamp = getTimestampFromPayload(payload);
                    ackAndLogAgentAck(agentId, orgId, msgId, status, timeStamp);
                    return true;
                }else {
                    throw new Exception(" status missing from payload ");
                }

    }

    public static boolean handleConfigurationAndSubscription(IotMessage iotMessage, Controller controller, JSONObject payload) {
        try {
            if ( iotMessage != null ) {
                List<String> pointNames = getPointsNameFromPayload(payload);
                return PointsAPI.handlePointConfigurationAndSubscription(FacilioCommand.valueOf(iotMessage.getCommand()),controller,pointNames, -1);
            }else {
                LOGGER.info("Exception occurred, iotmessage is null");
            }
        } catch (Exception e) {
            LOGGER.info("Exception while handling subscription and configuration handling",e);
        }
        return false;
    }

    public static boolean configureAllPoints(JSONObject payload, FacilioAgent agent) throws Exception {
        List<JSONObject> jsonControllers = (List<JSONObject>) payload.get(AgentConstants.DATA);
        int controllerType = Integer.parseInt(payload.get(AgentConstants.CONTROLLER_TYPE).toString());
        int interval = 0;
        if(payload.containsKey(AgentConstants.DATA_INTERVAL)){
            interval = Integer.parseInt(payload.get(AgentConstants.DATA_INTERVAL).toString());
        }
        List<Controller> controllers = getControllers(jsonControllers, agent, controllerType);
        for (Controller controller : controllers) {
            List<Map<String, Object>> data = getPoints(agent, controllerType, controller, AgentConstants.UNCONFIGURED);
            List<String> pointNames = data.stream()
                    .map(point -> (String) point.get(AgentConstants.NAME))
                    .collect(Collectors.toList());
            if(!pointNames.isEmpty()){
                PointsAPI.handlePointConfigurationAndSubscription(FacilioCommand.CONFIGURE, controller, pointNames, interval);
            }
        }
        return true;
    }

    private static List<Controller> getControllers(List<JSONObject> jsonControllers, FacilioAgent agent, int controllerType) throws Exception {
        List<Controller> controllers = new ArrayList<>();
        if(jsonControllers.isEmpty()){
            controllers = AgentConstants.getControllerBean().getControllers(controllerType, agent.getId());
        } else {
            JSONObject object = new JSONObject();
            object.put(AgentConstants.CONTROLLER_TYPE, controllerType);
            for (JSONObject controllerObj : jsonControllers) {
                object.put(AgentConstants.CONTROLLER, controllerObj);
                Controller controller = AgentConstants.getControllerBean().getController(object, agent.getId());
                if(controller!=null){
                    controllers.add(controller);
                }
            }
        }
        return controllers;
    }

    private static List<Map<String, Object>> getPoints(FacilioAgent agent, int controllerType, Controller controller, String status) throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getPointsdataCommand();
        FacilioContext context = chain.getContext();
        context.put(AgentConstants.CONTROLLER_ID, controller.getId());
        context.put(AgentConstants.CONTROLLER_TYPE, controllerType);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.POINTS);
        context.put(AgentConstants.AGENT_ID, agent.getId());
        context.put(AgentConstants.CONTROLLERIDS, new ArrayList<>());
        context.put(AgentConstants.STATUS, status);
        context.put("limit", -1);
        chain.execute();
        return (List<Map<String, Object>>) context.get("data");
    }

    public static boolean containsCheck(String key, Map map){
        if( (key != null) && ( ! key.isEmpty()) && ( map != null ) && ( ! map.isEmpty() ) && (map.containsKey(key)) && (map.get(key) != null) ){
            return true;
        }
        return false;
    }

    private static List<String> getPointsNameFromPayload(JSONObject payload) {
        List<String> pointNames = new ArrayList<>();
        if (containsCheck(AgentConstants.DATA,payload)) {
            JSONArray jsonArray = (JSONArray) payload.get(AgentConstants.DATA);
            JSONObject pointStatusData = (JSONObject) jsonArray.get(0);
            pointNames = getToUpdatePointNames(pointStatusData);
        }
        return pointNames;
    }

    private static List<String> getToUpdatePointNames(JSONObject jsonObject) {
        List<String> pointToConfigure = new ArrayList<>();
        if( ! jsonObject.isEmpty()){
            for (Object o : jsonObject.keySet()) {
                String pointName = (String) o;
                if((Boolean) jsonObject.get(pointName)){
                    pointToConfigure.add(pointName.trim());
                }
            }
            return pointToConfigure;
        }else {
            LOGGER.info(" points data empty ");
        }
        return pointToConfigure;
    }

    private static void ackAndLogAgentAck(long agentId, long orgId, long msgId, int status, long timeStamp) throws Exception {
        LogsApi.logAgentMessages(agentId,msgId, Status.valueOf(status),timeStamp);
        FacilioChain chain = TransactionChainFactory.getAckProcessorChain();
        FacilioContext context = chain.getContext();
        context.put(AgentConstants.ID, msgId);
        context.put(AgentConstants.STATUS, Status.valueOf(status));
        context.put(AgentConstants.ORGID, orgId);
        chain.execute();
    }

    public static boolean ackPing(long agentId,long orgId, JSONObject payload) throws Exception{
        long timestamp = getTimestampFromPayload(payload);
        long msgId = getMessageIdFromPayload(payload);
        ackAndLogAgentAck(agentId,orgId,msgId,Status.MESSAGE_PROCESSING_SUCCESS.asInt(),timestamp);
        return true;
    }

    public static long getMessageIdFromPayload(JSONObject payload) throws Exception {
        if(payload.containsKey(AgentConstants.MESSAGE_ID)) {
            return Long.parseLong(payload.get(AgentConstants.MESSAGE_ID).toString());
        }
        throw new Exception(" msgid missing from payload ");
    }

    private static long getTimestampFromPayload(JSONObject payload) {
        long timeStamp = System.currentTimeMillis();
        if (payload.containsKey(payload.get(AgentConstants.TIMESTAMP)) && (payload.get(AgentConstants.TIMESTAMP) != null)) {
            timeStamp = Long.parseLong(payload.get(AgentConstants.TIMESTAMP).toString());
        }
        return timeStamp;
    }

}
