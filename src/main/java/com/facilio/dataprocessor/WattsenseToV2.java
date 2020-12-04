package com.facilio.dataprocessor;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.integration.queue.preprocessor.AgentMessagePreProcessor;
import com.facilio.agentv2.AgentConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class WattsenseToV2 implements AgentMessagePreProcessor {
    @Override
    public List<JSONObject> preProcess(Object o) throws Exception {

        JSONObject processedMessage = new JSONObject();
        JSONObject jsonObject = (JSONObject) o;
        JSONObject data = (JSONObject) jsonObject.get(AgentConstants.DATA);
        JSONArray dataArray = new JSONArray();
        JSONObject dataItem = new JSONObject();
        Object payload;
        if (data.containsKey("scaledPayload")) {
            payload = data.get("scaledPayload");
        } else {
            payload = data.get("payload");
        }
        dataItem.put(data.get("property").toString(), payload);
        dataArray.add(dataItem);
        JSONObject controllerObject = new JSONObject();
        controllerObject.put(AgentConstants.NAME, data.get(AgentConstants.DEVICE_ID).toString());
        processedMessage.put(AgentConstants.AGENT, jsonObject.get(AgentConstants.AGENT));
        processedMessage.put(AgentConstants.PUBLISH_TYPE, 6);
        processedMessage.put(AgentConstants.CONTROLLER, controllerObject);
        processedMessage.put(AgentConstants.CONTROLLER_TYPE, FacilioControllerType.MISC.asInt());
        processedMessage.put(AgentConstants.TIMESTAMP, data.get(AgentConstants.TIMESTAMP));
        processedMessage.put(AgentConstants.DATA, dataArray);

        List<JSONObject> list = new ArrayList<>();
        list.add(processedMessage);
        return list;

    }
}
