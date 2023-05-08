package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.endpoint.WmsBroadcaster;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

public class PushHandler extends BaseHandler {

    @Override
    public void processIncomingMessage(Message message) {
        JSONObject content = message.getContent();
        String topic = (String) content.remove("topic");
        if (StringUtils.isNotEmpty(topic)) {
            Message sendMessage = new Message();
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
            WmsBroadcaster.getBroadcaster().sendMessage(sendMessage);
        }
    }
}
