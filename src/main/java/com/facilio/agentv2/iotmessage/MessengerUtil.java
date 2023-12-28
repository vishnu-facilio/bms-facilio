package com.facilio.agentv2.iotmessage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;

public class MessengerUtil {
    private static final Logger LOGGER = LogManager.getLogger(MessengerUtil.class.getName());

    public static JSONArray getPointsData(List<Point> points) {
        JSONArray array = new JSONArray();
        points.forEach(point -> {
            JSONObject pointJson = point.getChildJSON();
            if (point.getName() != null && !point.getName().isEmpty()) {
                pointJson.put(AgentConstants.NAME, point.getName());
            }
            if (point.getThresholdJSON() != null && !point.getThresholdJSON().isEmpty()) {
                pointJson.put("thresholdJson", point.getThresholdJSON());
            }
            array.add(pointJson);
        });
        return array;
    }

    static IotMessage getMessageObject(JSONObject message, FacilioCommand command) {
        IotMessage msg = new IotMessage();
        msg.setCommand(command.asInt());
        JSONObject data = new JSONObject();
        data.putAll(message);
        msg.setMessageData(data);
        setControlMessageMap(message, command, msg);
        return msg;
    }

    private static void setControlMessageMap(JSONObject message, FacilioCommand command, IotMessage msg) {
        if (command == FacilioCommand.SET) {
            List<Long> controlIds = new ArrayList<>();
            JSONArray pointsArray = (JSONArray) message.get(AgentConstants.POINTS);
            for (int i = 0; i < pointsArray.size(); i++) {
                JSONObject point = (JSONObject) pointsArray.get(i);
                long controlId = (long) point.get("controlId");
                controlIds.add(controlId);
            }
            msg.setControlIds(controlIds);
        }
    }

    static IotData addAndPublishNewAgentData(IotData data) throws Exception {
        FacilioChain addAndPublishIotMessageChain = TransactionChainFactory.getaddAndPublishIotMessageChain();
        FacilioContext context = addAndPublishIotMessageChain.getContext();
        context.put(AgentConstants.DATA, data);
        addAndPublishIotMessageChain.execute();
        return data;
    }
}
