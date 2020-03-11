package com.facilio.agentv2;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.DeviceUtil;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.metrics.MetricsApi;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.AckUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataProcessorV2
{

    private long orgId;
    private String orgDomainName;
    private AgentUtilV2 agentUtil;
    private ControllerUtilV2 controllerUtil;
    private Map<Long, ControllerUtilV2> agentIdControllerUtilMap = new HashMap<>();




    private static final Logger LOGGER = LogManager.getLogger(DataProcessorV2.class.getName());

    public DataProcessorV2(long orgId, String orgDomainName) {
        LOGGER.info(" loading newProcessor ");
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        try {
            agentUtil = new AgentUtilV2(orgId, orgDomainName);
            LOGGER.info("done loading newProcessor ");
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }


    public boolean processRecord(JSONObject payload) {
        boolean processStatus = false;
        try {
            LOGGER.info(" processing in processorV2 " + payload);
            if (payload.containsKey("clearAgentCache")) {
                agentUtil.getAgentMap().clear();
                LOGGER.info(" agent cache cleared ->" + agentUtil.getAgentMap());
                return true;
            }

            FacilioAgent agent = getFacilioAgentForPayload(payload);

            Long timeStamp = System.currentTimeMillis();
            if(payload.containsKey(AgentConstants.TIMESTAMP)){
                timeStamp = (Long) payload.get(AgentConstants.TIMESTAMP);
            }else {
                payload.put(AgentConstants.TIMESTAMP,timeStamp);
            }

            agent.setLastDataReceivedTime(timeStamp);
            AgentApiV2.updateAgentLastDataRevievedTime(agent);

            controllerUtil = getControllerUtil(agent.getId());
            if (!payload.containsKey(AgentConstants.PUBLISH_TYPE)) {
                LOGGER.info("Exception Occurred, " + AgentConstants.PUBLISH_TYPE + " is mandatory in payload " + payload);
                return false;
            }
            PublishType publishType = PublishType.valueOf(JsonUtil.getInt((payload.get(AgentConstants.PUBLISH_TYPE)))); // change it to Type
            if(publishType == null){
                throw new Exception(" publish type cant be null "+JsonUtil.getInt((payload.get(AgentConstants.PUBLISH_TYPE))));
            }
            LOGGER.info(" publish type for this record is " + publishType.name());

            markMetrices(agent,payload);
            switch (publishType) {
            	case CUSTOM:
            		Controller customController = getCachedControllerUsingPayload(payload,agent.getId());

	                 JSONObject customPayload = (JSONObject) payload.clone();
	                 if (customController != null) {
	                	 customPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID, customController.getId());
	                	 customPayload.put(FacilioConstants.ContextNames.CONTROLLER, FieldUtil.getAsJSON(customController));
	                 }
	
	                 processCustom(customPayload,customController);
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
                    payload.put(AgentConstants.IS_NEW_AGENT, Boolean.TRUE);
                    if(containsCheck(AgentConstants.CONTROLLER,payload)){
                        Controller controller = getCachedControllerUsingPayload(payload,agent.getId());
                        if (AckUtil.handleConfigurationAndSubscription(AckUtil.getMessageIdFromPayload(payload),controller,payload)) {
                            processStatus = true;
                            break;
                        }
                    }
                    processStatus = AckUtil.processAgentAck(payload, agent.getId(), orgId);
                    break;
                case TIMESERIES:
                    Controller timeseriesController = getCachedControllerUsingPayload(payload,agent.getId());
                    JSONObject timeSeriesPayload = (JSONObject) payload.clone();
                    if (timeseriesController != null) {
                        LOGGER.info(" timeseries controller found ");
                        timeSeriesPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID, timeseriesController.getId());
                    } else {
                        LOGGER.info(" timeseries controller not found ");
                        timeSeriesPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID, null);
                    }

                    processStatus = processTimeSeries(timeSeriesPayload, timeseriesController, true);

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
                default:
                    throw new Exception("No such Publish type " + publishType.name());
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ,", e);
        }
        LOGGER.info(" process status " + processStatus);
        return processStatus;
    }

    private void markMetrices(FacilioAgent agent, JSONObject payload) {
        LOGGER.info(" marking metrics "+agent.getName());
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
                            LOGGER.info(" goet controller " + controller.getId());
                            return controller;
                        } else {
                            throw new Exception("No controller found " + controllerJSON);
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
            LOGGER.info(" processing device ");
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
            LOGGER.info(" controller json "+controllerJson);
            if( ! payload.containsKey(AgentConstants.CONTROLLER_TYPE)){
                throw new Exception("payload missing controllerType ");
            }
            int type = ((Number)payload.get(AgentConstants.CONTROLLER_TYPE)).intValue();
            Device device = FieldDeviceApi.getDevice(agent.getId(), DeviceUtil.getControllerIdentifier(type,controllerJson));
            if (device != null) {
                LOGGER.info(" device not null and so processing point");
                return PointsUtil.processPoints(payload, device);
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
                LOGGER.info(" Agent processing successful ");
                return true;
            } else {
                LOGGER.info(" Agent processing failed");
            }
        } catch (Exception e) {
            LOGGER.info("Exeception while processing agent", e);
        }
        return false;
    }

    private boolean processTimeSeries(JSONObject payload, Controller controller, boolean isTimeSeries) {
        LOGGER.info(" calling timeseries processer chain v2");
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
    
    private boolean processCustom(JSONObject payload, Controller controller) {

    	try {
        	
            FacilioChain chain = TransactionChainFactory.getAddCustomDataChain();
            FacilioContext context = chain.getContext();
            //TODO
            context.put(AgentConstants.CONTROLLER, controller);
            if (controller != null) {
                context.put(AgentConstants.CONTROLLER_ID, controller.getId());
                context.put(AgentConstants.AGENT_ID, controller.getAgentId());
            } 
            
            context.put(AgentConstants.DATA, payload);
            
            if (payload.containsKey(AgentConstants.TIMESTAMP) && (payload.get(AgentConstants.TIMESTAMP) != null)) {
                context.put(AgentConstants.TIMESTAMP, payload.get(AgentConstants.TIMESTAMP));
            } else {
                context.put(AgentConstants.TIMESTAMP, System.currentTimeMillis());
            }
            chain.execute();
            LOGGER.info(" done processes custom data command ");
            return true;
        } catch (Exception e) {
            LOGGER.info("Exception while processing timeseries data ", e);
        }
        return false;
    }

    private long getAgentId(String agent) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewAgentDataModule().getTableName())
                .select(FieldFactory.getNewAgentFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(ModuleFactory.getNewAgentDataModule()),agent, StringOperators.IS));
        List<Map<String, Object>> rows =selectRecordBuilder.get();
        if (rows.size()>0){
            return Long.parseLong(rows.get(0).get(AgentConstants.ID).toString());
        }
        return -1;
    }

    public ControllerUtilV2 getControllerUtil(long agentId){
        ControllerUtilV2 cU;
        if(agentIdControllerUtilMap.containsKey(agentId)){
            LOGGER.info(" returning existing controllerUtil");
            cU = agentIdControllerUtilMap.get(agentId);
        }else {
            LOGGER.info(" creating new controllerUtil");
            cU = new ControllerUtilV2(agentId,orgId);
            LOGGER.info(" created new controllerUtil");
            agentIdControllerUtilMap.put(agentId,cU);
        }
        return cU;
    }


}
