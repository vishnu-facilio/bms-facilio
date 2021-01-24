package com.facilio.agentv2;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentType;
import com.facilio.agent.alarms.AgentEvent;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.DeviceUtil;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.iotmessage.IotMessage;
import com.facilio.agentv2.iotmessage.IotMessageApiV2;
import com.facilio.agentv2.metrics.MetricsApi;
import com.facilio.agentv2.misc.MiscController;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.events.util.EventAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.AckUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class DataProcessorV2
{

    private long orgId;
    private String orgDomainName;
    private AgentUtilV2 agentUtil;
    private ControllerUtilV2 controllerUtil;
    private Map<Long, ControllerUtilV2> agentIdControllerUtilMap = new HashMap<>();
    private Map<Long, Long> controllerIdVsLastTimeSeriesTimeStamp = new HashMap<>();



    private static final Logger LOGGER = LogManager.getLogger(DataProcessorV2.class.getName());

    public DataProcessorV2(long orgId, String orgDomainName) {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        try {
            agentUtil = new AgentUtilV2(orgId, orgDomainName);
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }


    public boolean processRecord(JSONObject payload, String partitionKey, EventUtil eventUtil) {
        boolean processStatus = false;
        try {
            if (payload.containsKey("clearAgentCache")) {
                agentUtil.getAgentMap().clear();
                LOGGER.info(" agent cache cleared ->" + agentUtil.getAgentMap());
                return true;
            }

            FacilioAgent agent = getFacilioAgentForPayload(payload);

            Long timeStamp = System.currentTimeMillis();
            if(payload.containsKey(AgentConstants.TIMESTAMP)){
                timeStamp = (Long) payload.get(AgentConstants.TIMESTAMP);
                if (payload.containsKey("actual_timestamp")) {
                    Object actual_timestampObj = payload.get("actual_timestamp");
                    timeStamp = actual_timestampObj instanceof Long ? (Long) actual_timestampObj : Long.parseLong(actual_timestampObj.toString());
                }
            }else {
                payload.put(AgentConstants.TIMESTAMP,timeStamp);
            }
            if (agent.getAgentType() != AgentType.CLOUD.getKey()) {
                agent.setLastDataReceivedTime(timeStamp);
                AgentApiV2.updateAgentLastDataRevievedTime(agent);
            }

            controllerUtil = getControllerUtil(agent.getId());
            if (!payload.containsKey(AgentConstants.PUBLISH_TYPE)) {
                LOGGER.info("Exception Occurred, " + AgentConstants.PUBLISH_TYPE + " is mandatory in payload " + payload);
                return false;
            }
            PublishType publishType = PublishType.valueOf(JsonUtil.getInt((payload.get(AgentConstants.PUBLISH_TYPE)))); // change it to Type
            if(publishType == null){
                throw new Exception(" publish type cant be null "+JsonUtil.getInt((payload.get(AgentConstants.PUBLISH_TYPE))));
            }
            markMetrices(agent,payload);
            switch (publishType) {
            	case CUSTOM:
                    JSONObject customPayload = (JSONObject) payload.clone();
                    processStatus = processCustom(agent,customPayload);
            		break;
                case AGENT:
                    processStatus = processAgent(payload, agent);
                    break;
                case CONTROLLERS:
                    processStatus = processDevices(agent, payload);
                    break;
                case DEVICE_POINTS:
                    processStatus = processDevicePoints(agent, payload);
                    break;
                case ACK:
                    processStatus = processAck(agent,payload);
                    break;
                case TIMESERIES:
                    JSONObject timeSeriesPayload = (JSONObject) payload.clone();
                    Controller timeseriesController = getCachedControllerUsingPayload(payload,agent.getId());
                    if (!controllerIdVsLastTimeSeriesTimeStamp.containsKey(timeseriesController.getId()) ||
                            !controllerIdVsLastTimeSeriesTimeStamp.get(timeseriesController.getId()).equals(timeStamp) ||
                            agent.getAgentType()== AgentType.WATTSENSE.getKey()) {

                        controllerIdVsLastTimeSeriesTimeStamp.put(timeseriesController.getId(), timeStamp);
                        timeSeriesPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID, timeseriesController.getId());
                        processStatus = processTimeSeries(timeSeriesPayload, timeseriesController, true);

                    } else {
                        LOGGER.info("Duplicate message for controller id : " + timeseriesController.getId() +
                                " a_timestamp : " + timeStamp);
                    }
                    break;
                case COV:
                    Controller controller = getCachedControllerUsingPayload(payload,agent.getId());
                    timeSeriesPayload = (JSONObject) payload.clone();
                    if (controller != null) {
                        timeSeriesPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID, controller.getId());
                    } else {
                        timeSeriesPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID, null);
                    }
                    processStatus = processTimeSeries(timeSeriesPayload, controller, false);
                    break;
                    //processTimeSeries(payload,)
                case AGENT_EVENTS:
                    processStatus = processAgentEvents(agent, payload);
                    break;
                case ALARM_SOURCE:
                    processStatus = processAlarmSourceEvents(agent,payload);
                    break;
                case EVENTS:
                    List<EventRuleContext> eventRules = new ArrayList<>();
                    ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
                    List<EventRuleContext> ruleList = bean.getActiveEventRules();
                    if (ruleList != null) {
                        eventRules = ruleList;
                    }
                    processStatus = eventUtil.processEvents(timeStamp, payload, partitionKey, orgId, eventRules);
                    break;
                default:
                    throw new Exception("No such Publish type " + publishType.name());
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ,", e);
        }
        LOGGER.info(" process status " + processStatus);
        return processStatus;
    }

    private boolean processAlarmSourceEvents ( FacilioAgent agent,JSONObject payload ) {
        try {
            if(payload.containsKey(AgentConstants.DATA)) {
                List<Map<String, Object>> props = (List<Map<String, Object>>)payload.getOrDefault(AgentConstants.DATA,Collections.emptyList());
                return !props.isEmpty() && EventAPI.addBulkSources(props,agent.getId());
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while adding bulk alarm source list");
        }
        return false;
    }

    private boolean processAck(FacilioAgent agent, JSONObject payload) {
        try {
            payload.put(AgentConstants.IS_NEW_AGENT, Boolean.TRUE);
            if (containsCheck(AgentConstants.CONTROLLER, payload)) {
                Controller controller = getCachedControllerUsingPayload(payload, agent.getId());
                IotMessage iotMessage = IotMessageApiV2.getIotMessage(AckUtil.getMessageIdFromPayload(payload));
                //for modbus device points are sent as ACK's,
                //so redirecting to devicePoints from ack
                if (iotMessage.getCommand()== FacilioCommand.CONFIGURE.asInt()
                        && (controller.getControllerType() == FacilioControllerType.MODBUS_RTU.asInt()
                        || controller.getControllerType() == FacilioControllerType.MODBUS_IP.asInt())){
                    if (agent.getAgentType() == AgentType.FACILIO.getKey()) {
                        return processDevicePoints(agent, payload);
                    }
                }
                if (AckUtil.handleConfigurationAndSubscription(iotMessage, controller, payload)) {
                    return true;
                }
            } else {
                return AckUtil.processAgentAck(payload, agent.getId(), orgId);
            }
        }catch (Exception e){
            LOGGER.info("Exception while processing ACK ",e);
        }
        return false;
    }

    private boolean processAgentEvents(FacilioAgent agent, JSONObject payload) throws Exception {
        try {
            AgentEvent eventType = getEventType(payload);
            if (eventType != null) {
                if (eventType == AgentEvent.CONTROLLERS_MISSING) {
                    return processControllerMissingEvent(payload, agent);
                }
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred while processing event ",e);
        }
        return false;
    }

    private boolean processControllerMissingEvent(JSONObject payload, FacilioAgent agent) throws Exception {
        if (payload.containsKey(AgentConstants.DATA)){
            try{
                JSONArray controllerArray = (JSONArray) payload.get(AgentConstants.DATA);
                if (controllerArray.size()==0){
                    agentUtil.clearControllerAlarm(agent);
                }
                else{
                    List<Controller> listOfControllers = new ArrayList<>();
                    for (Object controllerObj: controllerArray){
                        JSONObject controllerObject = (JSONObject) controllerObj;
                        if (controllerObject.containsKey(AgentConstants.CONTROLLER_TYPE)) {
                            int type = Integer.parseInt(controllerObject.get(AgentConstants.CONTROLLER_TYPE).toString());
                            FacilioControllerType controllerType = FacilioControllerType.valueOf(type);
                            Controller controllr = controllerUtil.getCachedController(controllerObject, controllerType);
                            if (controllr!=null) {
                                listOfControllers.add(controllr);
                            }else {
                                LOGGER.info("Controller is null while processing event");
                            }
                        }
                    }
                    if (listOfControllers.size()>0){
                        agentUtil.raiseControllerAlarm(agent,listOfControllers);
                    }
                }
                return true;
            }catch (Exception ex){
                LOGGER.info("Exception while creating controller alarm ", ex);
                return false;
            }
        }else{
            return false;
        }
    }

    private AgentEvent getEventType(JSONObject payload) {
        AgentEvent eventType = null;
        if (payload.containsKey("event")){
            eventType = AgentEvent.initTypeMap().get(Integer.parseInt(payload.get("event").toString()));
        }
        return eventType;
    }

    private void markMetrices(FacilioAgent agent, JSONObject payload) {
        try {
            MetricsApi.logMetrics(agent,payload);
        } catch (Exception e) {
            LOGGER.info("Exception while logging metrics for ",e);
        }
    }

    private Controller getCachedControllerUsingPayload(JSONObject payload, long agentId) throws Exception {
            if(containsCheck(AgentConstants.CONTROLLER,payload)) {
                JSONObject controllerJSON = (JSONObject) payload.get(AgentConstants.CONTROLLER);
                if (containsCheck(AgentConstants.CONTROLLER_TYPE, payload)) {
                    FacilioControllerType controllerType = FacilioControllerType.valueOf(((Number) payload.get(AgentConstants.CONTROLLER_TYPE)).intValue());
                    if (!controllerJSON.isEmpty()) {
                        Controller controller = controllerUtil.getCachedController(controllerJSON, controllerType);
                        //Controller controller = ControllerApiV2.getControllerFromDb(controllerJSON, agentId, controllerType);
                        if (controller != null) {
                            return controller;
                        } else {
                            FacilioAgent agent = AgentApiV2.getAgent(agentId);
                            if (agent.getAgentType() == AgentType.CLOUD.getKey()) {
                                MiscController miscController = new MiscController(agent.getId(), AccountUtil.getCurrentOrg().getOrgId());
                                miscController.setName(((JSONObject) (payload.get(AgentConstants.CONTROLLER))).get(AgentConstants.NAME).toString());
                                miscController.setDataInterval(agent.getInterval() * 60 * 1000);
                                Device device = new Device();
                                device.setIdentifier(miscController.getName());
                                device.setName(miscController.getName());
                                device.setAgentId(agent.getId());
                                device.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                                device.setControllerType(FacilioControllerType.MISC.asInt());
                                device.setConfigure(true);
                                device.setSiteId(agent.getSiteId());
                                long deviceId = FieldDeviceApi.addFieldDevice(device);
                                miscController.setDeviceId(deviceId);
                                ControllerApiV2.addController(miscController);
                                return miscController;
                            } else {
                                throw new Exception("controller not found for " + payload);
                            }

                        }
                    } else {
                        throw new Exception(" controllerJSON cant be empty " + controllerJSON);
                    }
                } else {
                    Controller customController = ControllerUtilV2.makeCustomController(orgId, agentId, controllerJSON);
                    return customController;
                }
            } else{
                throw new Exception("payload is missing" + AgentConstants.CONTROLLER);
            }

    }

    public static boolean containsCheck(String key, Map map){
        if( (key != null) && ( ! key.isEmpty()) && ( map != null ) && ( ! map.isEmpty() ) && (map.containsKey(key)) && (map.get(key) != null) ){
            return true;
        }
        return false;
    }

    /**
     * gets {@link FacilioAgent } for a payload.
     * @param payload JSONObject which must contain the key 'agent' to which agent's name is mapped.
     * @return {@link FacilioAgent}
     * @throws Exception if the key 'agent' is missing from payload or if no agent is found for a name.
     * */
    private FacilioAgent getFacilioAgentForPayload(JSONObject payload) throws Exception {
        String agentName = null;
        if (payload.containsKey(AgentConstants.AGENT)) {
            agentName = payload.get(AgentConstants.AGENT).toString().trim();
            FacilioAgent agent = agentUtil.getFacilioAgent(agentName);
            if (agent != null) {
                return agent;
            }else {
                throw new Exception(" No such agent found ");
            }
        }else {
            throw new Exception(" payload missing agent name");
        }
    }


    private boolean processDevices(FacilioAgent agent, JSONObject payload) {
        try {
            return DeviceUtil.processDevices(agent, payload);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while processing device", e);
        }
        return false;
    }

    private void processCOV(JSONObject payload, String agentName) {

    }

    private boolean processDevicePoints(FacilioAgent agent, JSONObject payload) {
        try {
            if (!payload.containsKey(AgentConstants.CONTROLLER)) {
                throw new Exception(" controllerJson missing from discoverPoints payload ->" + payload);
            }

            JSONObject controllerJson = (JSONObject) payload.get(AgentConstants.CONTROLLER);
            if (controllerJson == null || controllerJson.isEmpty()) {
                throw new Exception("Exception occurred, Controller identifier can't be null ->" + payload);
            }
            if( ! payload.containsKey(AgentConstants.CONTROLLER_TYPE)){
                throw new Exception("payload missing controllerType ");
            }
            int type = ((Number)payload.get(AgentConstants.CONTROLLER_TYPE)).intValue();
            Device device = FieldDeviceApi.getDevice(agent.getId(), DeviceUtil.getControllerIdentifier(agent, type, controllerJson));
            if (device != null) {
                return PointsUtil.processPoints(payload, device, agent);
            } else {
                throw new Exception("Exception occurred, Controller obtained in null");
            }

        } catch (Exception e) {
            LOGGER.info("Exception while processing  points->" + payload+" ",e);
        }
        return false;
    }

    private boolean processAgent(JSONObject payload, FacilioAgent agent) {
        try {
            if (agentUtil.processAgent(payload, agent)) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("Exeception while processing agent", e);
        }
        return false;
    }

    private boolean processTimeSeries(JSONObject payload, Controller controller, boolean isTimeSeries) {
        try {
            FacilioChain chain = TransactionChainFactory.getTimeSeriesProcessChainV2();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.IS_NEW_AGENT, true);
            //TODO
            if (controller != null) {
                context.put(AgentConstants.CONTROLLER, controller);
                context.put(AgentConstants.CONTROLLER_ID, controller.getId());
                context.put(AgentConstants.AGENT_ID, controller.getAgentId());
            } else {
                context.put(AgentConstants.CONTROLLER_ID, -1L);
                if (payload.containsKey("agent")) {
                    long agentId = getAgentId(payload.get("agent").toString());
                    if (agentId > 0) {
                        context.put(AgentConstants.AGENT_ID, agentId);
                    }
                } else throw new Exception("Agent missing in payload");
            }
            if (controller == null) {
                int type = Integer.parseInt(payload.get(AgentConstants.CONTROLLER_TYPE).toString());
                FacilioAgent agent = AgentApiV2.getAgent(getAgentId(payload.get("agent").toString()));
                if (type == FacilioControllerType.MISC.asInt() && agent.getAgentType() == AgentType.CLOUD.getKey()) {
                    String name = ((JSONObject) payload.get("controller")).get("name").toString();
                    context.put(AgentConstants.CONTROLLER_NAME, name);
                }
            }
            context.put(AgentConstants.DATA, payload);
            context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, isTimeSeries);
            if (payload.containsKey(AgentConstants.TIMESTAMP) && (payload.get(AgentConstants.TIMESTAMP) != null)) {
                LOGGER.info(" timestamp long instance check " + (payload.get(AgentConstants.TIMESTAMP) instanceof Long));
                LOGGER.info(" timestamp String instance check " + (payload.get(AgentConstants.TIMESTAMP) instanceof String));
                context.put(AgentConstants.TIMESTAMP, payload.get(AgentConstants.TIMESTAMP));
            } else {
                context.put(AgentConstants.TIMESTAMP, System.currentTimeMillis());
            }
            chain.execute();
            LOGGER.info(" done processes data command ");
            /*for (Object key : context.keySet()) {
                LOGGER.info(key+"->"+context.get(key));
            }*/
            return true;
            /*ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            bean.processNewTimeSeries(payload,controllerTs);*/
        } catch (Exception e) {
            LOGGER.info("Exception while processing timeseries data ", e);
        }
        return false;
    }

    private boolean processCustom(FacilioAgent agent,JSONObject payload) {

    	try {
            Controller customController = getCachedControllerUsingPayload(payload,agent.getId());
            JSONObject customPayload = (JSONObject) payload.clone();
            if (customController != null) {
                customPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID, customController.getId());
                customPayload.put(FacilioConstants.ContextNames.CONTROLLER, FieldUtil.getAsJSON(customController));
            }


            FacilioChain chain = TransactionChainFactory.getAddCustomDataChain();
            FacilioContext context = chain.getContext();
            //TODO
            context.put(AgentConstants.CONTROLLER, customController);
            if (customController != null) {
                context.put(AgentConstants.CONTROLLER_ID, customController.getId());
                context.put(AgentConstants.AGENT_ID, customController.getAgentId());
            } 
            
            context.put(AgentConstants.DATA, payload);
            
            if (payload.containsKey(AgentConstants.TIMESTAMP) && (payload.get(AgentConstants.TIMESTAMP) != null)) {
                context.put(AgentConstants.TIMESTAMP, payload.get(AgentConstants.TIMESTAMP));
            } else {
                context.put(AgentConstants.TIMESTAMP, System.currentTimeMillis());
            }
            chain.execute();
            return true;
        } catch (Exception e) {
            LOGGER.info("Exception while processing custom ", e);
        }
        return false;
    }

    private long getAgentId(String agent) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewAgentModule().getTableName())
                .select(FieldFactory.getNewAgentFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(ModuleFactory.getNewAgentModule()),agent, StringOperators.IS));
        List<Map<String, Object>> rows =selectRecordBuilder.get();
        if (rows.size()>0){
            return Long.parseLong(rows.get(0).get(AgentConstants.ID).toString());
        }
        return -1;
    }

    public ControllerUtilV2 getControllerUtil(long agentId){
        ControllerUtilV2 cU;
        if(agentIdControllerUtilMap.containsKey(agentId)){
            cU = agentIdControllerUtilMap.get(agentId);
        }else {
            cU = new ControllerUtilV2(agentId,orgId);
            agentIdControllerUtilMap.put(agentId,cU);
        }
        return cU;
    }


}
