package com.facilio.agentnew;

import com.facilio.agent.PublishType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.controller.Controller;
import com.facilio.agentnew.controller.NewControllerAPI;
import com.facilio.agentnew.controller.NewControllerUtil;
import com.facilio.agentnew.device.DeviceUtil;
import com.facilio.agentnew.point.PointsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.fw.BeanFactory;
import com.facilio.util.AckUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

public class NewProcessor
{

    private long orgId;
    private String orgDomainName;
    private NewAgentUtil au;
    private AckUtil ackUtil;
    private NewControllerUtil cU;
    private DeviceUtil dU;
    private Map<Long,NewControllerUtil> agentIdControllerUtilMap = new HashMap<>();
    JSONParser parser = new JSONParser();




    private static final Logger LOGGER = LogManager.getLogger(NewProcessor.class.getName());

    public NewProcessor(long orgId, String orgDomainName) {
        LOGGER.info(" loading newProcessor ");
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        try {
            au = new NewAgentUtil(orgId, orgDomainName);
            ackUtil = new AckUtil();
            dU = new DeviceUtil();
            LOGGER.info("done loading newProcessor ");
        }catch (Exception e){
            LOGGER.info("Exception occurred ",e);
        }
    }

   public void processNewAgentData(JSONObject payload){
        try {
            LOGGER.info(" processing new agent in new processor method " + payload);
            String agentName = orgDomainName.trim();
            if (payload.containsKey(PublishType.agent.getValue())) {
                agentName = payload.remove(PublishType.agent.getValue()).toString().trim();
            }
            LOGGER.info(" agent name is " + agentName);
            FacilioAgent agent = au.getFacilioAgent(agentName);
            LOGGER.info(" agent obtained is "+agent);
            if (agent == null) {
                agent = makeNewFacilioAgent(agentName);
                long agentId = au.addAgent(agent);
                if (agentId < 1L) {
                    LOGGER.info(" Error in AgentId generation ");
                }else {
                    cU = getControllerUtil(agent.getId());
                    agent.setId(agentId);
                }
            }
            LOGGER.info(" agent ID " + agentName + " - " + agent.getId());
            if( ! payload.containsKey(AgentConstants.PUBLISH_TYPE)){
                LOGGER.info("Exception Occurred, "+AgentConstants.PUBLISH_TYPE+" is mandatory in payload "+payload);
            }
            com.facilio.agent.fw.constants.PublishType publishType = com.facilio.agent.fw.constants.PublishType.valueOf(JsonUtil.getInt(payload.get(AgentConstants.PUBLISH_TYPE))); // change it to Type
            LOGGER.info(" publish type for this record is "+publishType.name());
            switch (publishType) {
                case AGENT:
                    long processStatus = au.processAgent(payload,agentName);
                    if(processStatus>0){
                        LOGGER.info(" Agent processing successful "+processStatus);
                    }else {
                        LOGGER.info(" Agent processing failed");
                    }
                case CONTROLLERS:
                    LOGGER.info("payload at case controllers " + payload);
                    dU.processDevices(agent, payload);
                    break;
                case DEVICE_POINTS:
                        Controller controller = getControllerFromAgentPayload(payload, agent.getId());
                        if (controller != null) {
                            LOGGER.info(" controller not null and so processing point");
                            PointsUtil pU = new PointsUtil(agent.getId(), controller.getId());
                            pU.processPoints(payload, controller);
                        } else {
                            LOGGER.info("Exception occurred, Controller obtained in null");
                        }
                        break;
                case ACK:
                    LOGGER.info(" iamcvijay logs processing ack");
                    payload.put(AgentConstants.IS_NEW_AGENT,Boolean.TRUE);
                    ackUtil.processNewAgentAck(payload, agentName, orgId);
                    //processLog(payload, agent.getId(), recordId);
                    break;
                case TIMESERIES:
                    Controller controllerTs = getControllerFromAgentPayload(payload,agent.getId());
                    if( controllerTs != null){
                        processTimeSeries(payload,controllerTs);
                    }
                    break;
                   //processTimeSeries(payload,)
                default:
                    throw new Exception("No such Publish type "+publishType.name());
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred ,",e);
        }
    }

    private void processTimeSeries(JSONObject payload, Controller controllerTs) {
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            bean.processNewTimeSeries(payload,controllerTs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Controller getControllerFromAgentPayload(JSONObject payload, long agentId) {
        if( ! payload.containsKey(AgentConstants.CONTROLLER) ){
            return null;
        }
        String identifier = String.valueOf(payload.get(AgentConstants.CONTROLLER));
        if (payload.containsKey(AgentConstants.CONTROLLER) && payload.containsKey(AgentConstants.TYPE)) {
            FacilioControllerType controllerType = FacilioControllerType.valueOf(((Number) payload.get(AgentConstants.TYPE)).intValue());
            if(controllerType != null){
                return NewControllerAPI.getControllerFromDb(identifier, agentId, controllerType);
            }else{
                return NewControllerUtil.makeCustomController(orgId,agentId,identifier);
            }
        }
        else if (payload.containsKey(AgentConstants.CONTROLLER)){
                return NewControllerUtil.makeCustomController(orgId,agentId,identifier);
        }
        else {
            LOGGER.info(" EXveption occurred, Controller detail missing from payload -> " + payload);
        }
        return null;
    }

    public NewControllerUtil getControllerUtil(long agentId){
        NewControllerUtil cU;
        if(agentIdControllerUtilMap.containsKey(agentId)){
            LOGGER.info(" returning existing controllerUtil");
            cU = agentIdControllerUtilMap.get(agentId);
        }else {
            LOGGER.info(" creating new controllerUtil");
            cU = new NewControllerUtil(agentId);
            LOGGER.info(" created new controllerUtil");
            agentIdControllerUtilMap.put(agentId,cU);
        }
        return cU;
    }

    private com.facilio.agentnew.FacilioAgent makeNewFacilioAgent(String agentName) {

        com.facilio.agentnew.FacilioAgent agent = new com.facilio.agentnew.FacilioAgent();
        agent.setName(agentName);
        agent.setConnectionStatus(Boolean.TRUE);
        agent.setState(1);
        agent.setInterval(15L);
        agent.setWritable(false);
        return agent;
    }
}
