package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

@TopicHandler(
        topic = Topics.Push.push,
        priority = -5
)
public class PushHandler extends BaseHandler {

    @Override
    public void processIncomingMessage(Message message) {
        JSONObject content = message.getContent();
        String topic = (String) content.remove("topic");
        if (StringUtils.isNotEmpty(topic)) {
            Message sendMessage = new Message();
            sendMessage.setTopic(topic);
            sendMessage.setContent(content);
            sendMessage.setOrgId(message.getOrgId());
            SessionManager.getInstance().sendMessage(sendMessage);
        }
    }
}
