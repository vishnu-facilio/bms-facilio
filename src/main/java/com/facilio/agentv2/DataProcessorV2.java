package com.facilio.agentv2;

import com.facilio.agent.PublishType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.DeviceUtil;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.AckUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.HashMap;
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
        }catch (Exception e){
            LOGGER.info("Exception occurred ",e);
        }
    }

   public void processNewAgentData(JSONObject payload){
        try {
            LOGGER.info(" processing in processorV2 " + payload);
            String agentName = orgDomainName.trim();
            if (payload.containsKey(PublishType.agent.getValue())) {
                agentName = payload.get(PublishType.agent.getValue()).toString().trim();
            }
            FacilioAgent agent = au.getFacilioAgent(agentName);
            if (agent == null) {
                LOGGER.info(" agent is null ");
                agent = AgentUtilV2.makeNewFacilioAgent(agentName);
                long agentId = au.addAgent(agent);
                if (agentId < 1L) {
                    LOGGER.info(" Error in AgentId generation ");
                }else {
                    agent.setId(agentId);
                }
            }
            cU = getControllerUtil(agent.getId());
            if( ! payload.containsKey(AgentConstants.PUBLISH_TYPE)){
                LOGGER.info("Exception Occurred, "+AgentConstants.PUBLISH_TYPE+" is mandatory in payload "+payload);
            }
            com.facilio.agent.fw.constants.PublishType publishType = com.facilio.agent.fw.constants.PublishType.valueOf(JsonUtil.getInt(payload.get(AgentConstants.PUBLISH_TYPE))); // change it to Type
            LOGGER.info(" publish type for this record is "+publishType.name());
            switch (publishType) {
                case AGENT:
                    processAgent(payload,agentName);
                    break;
                case CONTROLLERS:
                    dU.processDevices(agent, payload);
                    break;
                case DEVICE_POINTS:
                        processController(payload,agent);
                    break;
                case ACK:
                    LOGGER.info(" iamcvijay logs processing ack");
                    payload.put(AgentConstants.IS_NEW_AGENT,Boolean.TRUE);
                    ackUtil.processNewAgentAck(payload, agentName, orgId);
                    //processLog(payload, agent.getId(), recordId);
                    break;
                case TIMESERIES:
                    Controller controllerTs = cU.getControllerFromAgentPayload(payload);
                    if( controllerTs != null){
                        JSONObject timeseriesPayload = (JSONObject) payload.clone();
                        timeseriesPayload.put(FacilioConstants.ContextNames.CONTROLLER_ID,controllerTs.getId());
                        processTimeSeries(timeseriesPayload,controllerTs);
                    }
                    break;
                case COV:
                    processCOV(payload,agentName);
                   //processTimeSeries(payload,)
                default:
                    throw new Exception("No such Publish type "+publishType.name());
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred ,",e);
        }
    }

    private void processCOV(JSONObject payload, String agentName) {

    }

    private boolean processController(JSONObject payload, FacilioAgent agent) throws Exception {
       // Controller controller = cU.getControllerFromAgentPayload(payload);
        if( ! payload.containsKey(AgentConstants.CONTROLLER)){
            LOGGER.info(" identifier missing from discoverPoints payload ->"+payload);
            return false;
        }
        String identifier = (String) payload.get(AgentConstants.CONTROLLER);
        if(identifier == null || identifier.isEmpty()){
            LOGGER.info("Exception occurred, Controller identifier can't be null ->"+payload);
            return false;
        }
        Device device = FieldDeviceApi.getDevice(agent.getId(),identifier);
        if (device != null) {
            LOGGER.info(" controller not null and so processing point");
            return PointsUtil.processPoints(payload, device);
        } else {
            throw new Exception("Exception occurred, Controller obtained in null");
        }
    }

    private boolean processAgent(JSONObject payload, String agentName) {
        long processStatus = au.processAgent(payload,agentName);
        if(processStatus>0){
            LOGGER.info(" Agent processing successful "+processStatus);
            return true;
        }else {
            LOGGER.info(" Agent processing failed");
        }
        return false;
    }

    private void processTimeSeries(JSONObject payload, Controller controllerTs) {
        LOGGER.info(" calling timeseries processer chain v2");
        try {
            FacilioChain chain = TransactionChainFactory.getTimeSeriesProcessChainV2();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.IS_NEW_AGENT,true);
            context.put(AgentConstants.CONTROLLER,controllerTs);
            context.put(AgentConstants.CONTROLLER_ID,controllerTs.getId());
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
            e.printStackTrace();
        }
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
