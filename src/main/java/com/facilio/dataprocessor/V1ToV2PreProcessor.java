package com.facilio.dataprocessor;

import com.facilio.agent.AgentType;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.PublishType;
import com.facilio.agent.integration.queue.preprocessor.AgentMessagePreProcessor;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.bacnet.BacnetIpControllerContext;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.misc.MiscControllerContext;
import com.facilio.agentv2.niagara.NiagaraControllerContext;
import com.facilio.fw.BeanFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class V1ToV2PreProcessor implements AgentMessagePreProcessor {
    private static final Logger LOGGER = LogManager.getLogger(V1ToV2PreProcessor.class.getName());
    public static final String PUBLISH_TYPE = "PUBLISH_TYPE";

    @Override
    public List<JSONObject> preProcess(Object o) throws Exception {

        List<JSONObject> messages = new ArrayList<>();
        JSONObject payload = (JSONObject) o;
        if (payload.containsKey(PUBLISH_TYPE) && payload.get(PUBLISH_TYPE)!=null){
            String publishType = payload.get(PUBLISH_TYPE).toString();
            switch (publishType){
                case "agent":
                    messages = processAgent(payload);
                    break;
                case "timeseries":
                    messages = processTimeseries(payload, false);
                    break;
                case "devicepoints":
                    if(payload.containsKey(AgentConstants.POINTS) &&
                            payload.get(AgentConstants.POINTS) instanceof JSONArray &&
                            ((JSONArray)payload.get(AgentConstants.POINTS)).size()==0 ){
                        messages = processDevicepointsToControllers(payload);
                    }else if (payload.containsKey(AgentConstants.POINTS)){
                        messages = processDevicepoints(payload);
                    }
                    break;
                case "cov":
                    messages = processTimeseries(payload, true);
                    break;
                case "ack":
                default:
                    LOGGER.info("Unknown PUBLISH_TYPE");
            }
        }
        return messages;
    }
    private List<JSONObject> processDevicepointsToControllers(JSONObject payload) throws Exception {
        JSONObject msg = new JSONObject();
        msg.put(AgentConstants.PUBLISH_TYPE, PublishType.CONTROLLERS.asInt());
        if (payload.containsKey(AgentConstants.DEVICE_NAME) && payload.get(AgentConstants.DEVICE_NAME) != null) {
            String deviceName = payload.get(AgentConstants.DEVICE_NAME).toString();
            JSONObject controllerJson = fillControllerTypeAndGetControllerChildJSON(payload, msg);
            JSONArray data = new JSONArray();
            JSONObject dataItem = new JSONObject();
            dataItem.put(AgentConstants.CONTROLLER, controllerJson);
            dataItem.put(AgentConstants.NAME, deviceName);
            dataItem.put(AgentConstants.CONTROLLER_TYPE,msg.get(AgentConstants.CONTROLLER_TYPE));
            data.add(dataItem);
            msg.put(AgentConstants.DATA, data);
        }
        List<JSONObject> msgs = new ArrayList<>();
        msgs.add(msg);
        return msgs;
    }

    private JSONObject fillControllerTypeAndGetControllerChildJSON(JSONObject payload, JSONObject msg) throws Exception {
        JSONObject controllerJson = new JSONObject();
        if (payload.containsKey(AgentConstants.TIMESTAMP)){
            msg.put(AgentConstants.TIMESTAMP,payload.get(AgentConstants.TIMESTAMP));
        }else{
            msg.put(AgentConstants.TIMESTAMP,System.currentTimeMillis());
        }
        if (payload.containsKey(AgentConstants.AGENT) && payload.get(AgentConstants.AGENT) != null) {
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            FacilioAgent agent = agentBean.getAgent(payload.get(AgentConstants.AGENT).toString());
            if (agent != null) {
                msg.put(AgentConstants.AGENT, agent.getName());

                    switch (AgentType.valueOf(agent.getAgentType())) {
                        case FACILIO:
                            //TODO @Anand
                            msg.put(AgentConstants.CONTROLLER_TYPE, FacilioControllerType.BACNET_IP.asInt());
                            break;
                        case NIAGARA:
                            msg.put(AgentConstants.CONTROLLER_TYPE, FacilioControllerType.NIAGARA.asInt());
                            controllerJson.put(AgentConstants.IP_ADDRESS,payload.get(AgentConstants.IP_ADDRESS));
                            controllerJson.put(AgentConstants.PORT_NUMBER,payload.get(AgentConstants.PORT_NUMBER));
                            break;
                        default:
                            LOGGER.info("Unsupported controller");
                    }
            }
        }
        return controllerJson;
    }

    private List<JSONObject> processDevicepoints(JSONObject payload) throws Exception {
        JSONObject msg= new JSONObject();
        msg.put(AgentConstants.PUBLISH_TYPE, PublishType.DEVICE_POINTS.asInt());
        if (payload.containsKey(AgentConstants.DEVICE_NAME) && payload.get(AgentConstants.DEVICE_NAME) != null) {
            String deviceName = payload.get(AgentConstants.DEVICE_NAME).toString();
            JSONObject controllerJson = fillControllerTypeAndGetControllerChildJSON(payload,msg);
            controllerJson.put(AgentConstants.NAME,deviceName);
            msg.put(AgentConstants.CONTROLLER,controllerJson);
            JSONArray data = new JSONArray();
            JSONArray points = (JSONArray) payload.get(AgentConstants.POINTS);
            for (Object p :
                    points) {
                JSONObject newPoint = new JSONObject();
                JSONObject point = (JSONObject) p;
                String instance = point.get("instance").toString();
                newPoint.put(AgentConstants.NAME,instance);
                newPoint.put(AgentConstants.PATH,instance);
                data.add(newPoint);

            }
            msg.put(AgentConstants.DATA,data);
        }
        List<JSONObject> msgs = new ArrayList<>();
        msgs.add(msg);
        return msgs;
    }

    private List<JSONObject> processTimeseries(JSONObject payload, boolean isCov) throws Exception {
        JSONObject msg = new JSONObject();
        FacilioAgent agent;
        String deviceId = null;
        for (Object key: payload.keySet()){
            if (payload.get(key) instanceof JSONObject){
                deviceId = key.toString();
            }
        }
        if (deviceId == null){
            LOGGER.info("Device ID is null ");
        }
        payload.put("deviceId",deviceId);
        if (payload.containsKey(AgentConstants.AGENT) && payload.get(AgentConstants.AGENT) != null) {
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            agent = agentBean.getAgent(payload.get(AgentConstants.AGENT).toString());
            if (agent !=null) {
                msg.put(AgentConstants.AGENT, payload.get(AgentConstants.AGENT).toString());
                if (isCov) {
                    msg.put(AgentConstants.PUBLISH_TYPE, PublishType.COV.asInt());
                } else {
                    msg.put(AgentConstants.PUBLISH_TYPE, PublishType.TIMESERIES.asInt());
                }
                if (payload.containsKey("deviceId")) {
                    if(payload.containsKey(AgentConstants.TIMESTAMP)) {
                        msg.put(AgentConstants.TIMESTAMP, payload.get(AgentConstants.TIMESTAMP));
                    } else {
                        long currTime = System.currentTimeMillis();
                        msg.put(AgentConstants.TIMESTAMP, currTime);
                    }
                    if (payload.containsKey(deviceId)) {
                        JSONObject points = (JSONObject) payload.get(deviceId);
                        Controller controller = AgentConstants.getControllerBean().getControllerByName(agent.getId(), deviceId);
                        if (controller != null){
                            FacilioControllerType controllerType = FacilioControllerType.valueOf(controller.getControllerType());
                            msg.put(AgentConstants.CONTROLLER_TYPE,controllerType.asInt());
                            switch (controllerType) {
                                case BACNET_IP:
                                    LOGGER.info("Bacnet controller :" + controller.getName());
                                    BacnetIpControllerContext bacnetController = (BacnetIpControllerContext) controller;
                                    msg.put(AgentConstants.CONTROLLER, bacnetController.getChildJSON());
                                    break;
                                case NIAGARA:
                                    LOGGER.info("Niagara controller");
                                    NiagaraControllerContext niagaraController = (NiagaraControllerContext) controller;
                                    msg.put(AgentConstants.CONTROLLER,niagaraController.getChildJSON());
                                    break;
                                case MISC:
                                    LOGGER.info("Misc controller");
                                    MiscControllerContext miscController = (MiscControllerContext) controller;
                                    msg.put(AgentConstants.CONTROLLER, miscController.getChildJSON());
                                    break;
                                default:
                                    LOGGER.info("Unsupported controller");
                            }

                            JSONArray data = new JSONArray();
                            data.add(points);
                            msg.put(AgentConstants.DATA, data);
                        }
                    } else {
                        LOGGER.info("deviceId key missing");
                    }
                } else {
                    LOGGER.info("Keys missing in old messages");
                }
            }else{
                LOGGER.info("Agent not found " + payload.get(AgentConstants.AGENT).toString());
            }
        } else {
            LOGGER.info("Agent key missing");
        }
        List<JSONObject> messages = new ArrayList<>();
        messages.add(msg);
        LOGGER.info("Messages " + messages);
        return messages;
    }

    private List<JSONObject> processAgent(JSONObject payload) {
        JSONObject msg = new JSONObject();
        msg.put(AgentConstants.PUBLISH_TYPE, PublishType.AGENT.asInt());
        msg.put(AgentConstants.AGENT,payload.get(AgentConstants.AGENT).toString());
        if (payload.containsKey(AgentConstants.TIMESTAMP)) {
            msg.put(AgentConstants.TIMESTAMP, payload.get(AgentConstants.TIMESTAMP));
        } else if (payload.containsKey("inittime")){
            msg.put(AgentConstants.TIMESTAMP, payload.get("inittime"));
        }
        msg.put(AgentConstants.STATUS,payload.get(AgentConstants.STATUS));
        List<JSONObject> msgs = new ArrayList<>();
        msgs.add(msg);
        return msgs;
    }
}
