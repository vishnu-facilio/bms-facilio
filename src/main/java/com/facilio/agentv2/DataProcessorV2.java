package com.facilio.agentv2;

import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.DeviceUtil;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
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
    private AgentUtilV2 au;
    private AckUtil ackUtil;
    private ControllerUtilV2 cU;
    private DeviceUtil dU;
    private Map<Long, ControllerUtilV2> agentIdControllerUtilMap = new HashMap<>();




    private static final Logger LOGGER = LogManager.getLogger(DataProcessorV2.class.getName());

    public DataProcessorV2(long orgId, String orgDomainName) {
        LOGGER.info(" loading newProcessor ");
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        try {
            au = new AgentUtilV2(orgId, orgDomainName);
            ackUtil = new AckUtil();
            dU = new DeviceUtil();
            LOGGER.info("done loading newProcessor ");
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }



    private static long getQuarterHourStartTime(long currTime) {
        return 0;
    }

    public boolean processRecord(JSONObject payload) {
        boolean processStatus = false;
        try {

           /* JSONObject payload = record.getData();
            if(payload == null || payload.isEmpty()){
                throw new Exception(" payload can't be null or empty");
            }*/
            LOGGER.info(" processing in processorV2 " + payload);
            String agentName = orgDomainName.trim();
            if (payload.containsKey(AgentConstants.AGENT)) {
                agentName = payload.get(AgentConstants.AGENT).toString().trim();
            }
            FacilioAgent agent = au.getFacilioAgent(agentName);
            if (agent == null) {
                LOGGER.info(" agent is null ");
                agent = AgentUtilV2.makeNewFacilioAgent(agentName);
                long agentId = au.addAgent(agent);
                if (agentId < 1L) {
                    LOGGER.info(" Error in AgentId generation ");
                } else {
                    agent.setId(agentId);
                }
            }
            if (payload.containsKey("clearAgentCache")) {
                au.getAgentMap().clear();
                LOGGER.info(" agent cache cleared ->" + au.getAgentMap());
                return true;
            }
            cU = getControllerUtil(agent.getId());
            if (!payload.containsKey(AgentConstants.PUBLISH_TYPE)) {
                LOGGER.info("Exception Occurred, " + AgentConstants.PUBLISH_TYPE + " is mandatory in payload " + payload);
                return false;
            }
            PublishType publishType = PublishType.valueOf(JsonUtil.getInt((payload.get(AgentConstants.PUBLISH_TYPE)))); // change it to Type
            LOGGER.info(" publish type for this record is " + publishType.name());
            // markMetrices(agent.getId(), publishType, payload);
            switch (publishType) {
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
                    LOGGER.info(" iamcvijay logs processing ack");
                    payload.put(AgentConstants.IS_NEW_AGENT, Boolean.TRUE);
                    ackUtil.processAgentAck(payload, agentName, orgId);
                    //processLog(payload, agent.getId(), recordId);
                    break;
                case TIMESERIES:
                    Controller controller = cU.getControllerFromAgentPayload(payload);

                    JSONObject timeSeriesPayload = (JSONObject) payload.clone();
                    if (controller != null)
                        timeSeriesPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID, controller.getId());
                    else
                        timeSeriesPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID, null);

                    processTimeSeries(timeSeriesPayload, controller);

                    break;
                case COV:
                    processCOV(payload, agentName);
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

    private void markMetrices(long id, com.facilio.agent.fw.constants.PublishType publishType, JSONObject payload) {
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while marking metrices");
        }
    }

    private boolean processDevices(FacilioAgent agent, JSONObject payload) {
        try {
            return dU.processDevices(agent, payload);
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
                throw new Exception(" identifier missing from discoverPoints payload ->" + payload);
            }

            String identifier = (String) payload.get(AgentConstants.CONTROLLER);
            if (identifier == null || identifier.isEmpty()) {
                throw new Exception("Exception occurred, Controller identifier can't be null ->" + payload);
            }

            Device device = FieldDeviceApi.getDevice(agent.getId(), identifier);
            if (device != null) {
                LOGGER.info(" controller not null and so processing point");
                return PointsUtil.processPoints(payload, device);
            } else {
                throw new Exception("Exception occurred, Controller obtained in null");
            }

        } catch (Exception e) {
            LOGGER.info("Exception while processing  points->" + payload);
        }
        return false;
    }

    private boolean processAgent(JSONObject payload, FacilioAgent agent) {
        try {
            if (au.processAgent(payload, agent)) {
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

    private void processTimeSeries(JSONObject payload, Controller controller) {
        LOGGER.info(" calling timeseries processer chain v2");
        try {
            FacilioChain chain = TransactionChainFactory.getTimeSeriesProcessChainV2();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.IS_NEW_AGENT, true);
            //TODO
            context.put(AgentConstants.CONTROLLER,controller);
            if (controller!=null) {
                context.put(AgentConstants.CONTROLLER_ID,controller.getId());
                context.put(AgentConstants.AGENT_ID,controller.getAgentId());
            }
            else {
                context.put(AgentConstants.CONTROLLER_ID,-1L);
                if (payload.containsKey("agent")){
                long agentId =getAgentId(payload.get("agent").toString());
                    if (agentId>0) {
                        context.put(AgentConstants.AGENT_ID, agentId);
                    }
                }
                else  throw new Exception("Agent missing in payload");
            }
            context.put(AgentConstants.DATA,payload);
            context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, true);
            if(payload.containsKey(AgentConstants.TIMESTAMP) && (payload.get(AgentConstants.TIMESTAMP) != null)){
                context.put(AgentConstants.TIMESTAMP,payload.get(AgentConstants.TIMESTAMP));
            }else {
                context.put(AgentConstants.TIMESTAMP,System.currentTimeMillis());
            }
            chain.execute();
            LOGGER.info(" done processes data command ");
            for (Object key : context.keySet()) {
                LOGGER.info(key+"->"+context.get(key));
            }
            /*ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            bean.processNewTimeSeries(payload,controllerTs);*/
        } catch (Exception e) {
            LOGGER.info("Exception while processing timeseries data");
        }
    }

    private long getAgentId(String agent) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewAgentDataModule().getTableName())
                .select(FieldFactory.getNewAgentDataFields())
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
