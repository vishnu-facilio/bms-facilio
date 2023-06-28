package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.endpoint.Broadcaster;
import com.facilio.wmsv2.message.WebMessage;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

public class PushHandler extends WmsHandler {

    @Override
    public void processIncomingMessage(WebMessage message) {
        JSONObject content = message.getContent();
        String topic = (String) content.remove("topic");
        if (StringUtils.isNotEmpty(topic)) {
            WebMessage sendMessage = new WebMessage();
            sendMessage.setTopic(topic);
            sendMessage.setContent(content);
            if (content.containsKey("to")) {
                sendMessage.setTo(((Number) content.get("to")).longValue());
            }
            if (content.containsKey("appId")) {
                sendMessage.setAppId(((Number) content.get("appId")).longValue());
            }
            if (content.containsKey("orgId")) {
                sendMessage.setOrgId(((Number) content.get("orgId")).longValue());
            } else {
                sendMessage.setOrgId(message.getOrgId());
            }
            Broadcaster.getBroadcaster().sendMessage(sendMessage);
        }
    }
}
