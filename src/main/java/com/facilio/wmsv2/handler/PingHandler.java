package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;

@TopicHandler(
        topic = Topics.System.ping,
        priority = -10,
        deliverTo = TopicHandler.DELIVER_TO.SESSION,
        recordTimeout = 15
)
public class PingHandler extends BaseHandler {

    @Override
    public void processIncomingMessage(Message message) {
        SessionManager.getInstance().sendMessage(message);
    }

    @Override
    public Message processOutgoingMessage(Message message) {
        return message;
    }
}
