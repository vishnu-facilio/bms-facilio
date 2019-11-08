package com.facilio.agentv2.iotmessage;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.Point;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class MessengerUtil
{

    public static JSONArray getPointsData(List<Point> points){
        JSONArray array = new JSONArray();
        points.forEach(point -> array.add(point.getChildJSON()));
        return array;
    }

    static IotMessage getMessageObject(JSONObject message) {
        IotMessage msg = new IotMessage();
        JSONObject data = new JSONObject();
        data.putAll(message);
        msg.setMessageData(data);
        return msg;
    }

    static IotData addAndPublishNewAgentData(IotData data) throws Exception {
        FacilioChain addAndPublishIotMessageChain = TransactionChainFactory.getaddAndPublishIotMessageChain();
        FacilioContext context = addAndPublishIotMessageChain.getContext();
        context.put(AgentConstants.DATA,data);
        addAndPublishIotMessageChain.execute();
       return data;
    }
}
