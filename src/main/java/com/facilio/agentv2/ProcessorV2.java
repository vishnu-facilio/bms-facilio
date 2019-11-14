package com.facilio.agentv2;

import com.facilio.agent.PublishType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.device.DeviceUtil;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.fw.BeanFactory;
import com.facilio.util.AckUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;

public class ProcessorV2
{

    private long orgId;
    private String orgDomainName;
    private AgentUtilV2 au;
    private AckUtil ackUtil;
    private ControllerUtilV2 cU;
    private DeviceUtil dU;
    private Map<Long, ControllerUtilV2> agentIdControllerUtilMap = new HashMap<>();
    JSONParser parser = new JSONParser();




    private static final Logger LOGGER = LogManager.getLogger(ProcessorV2.class.getName());

    public ProcessorV2(long orgId, String orgDomainName) {
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
                        Controller controller = cU.getControllerFromAgentPayload(payload);
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
                    Controller controllerTs = cU.getControllerFromAgentPayload(payload);
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
