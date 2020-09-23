package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;

@TopicHandler(
        topic = Topics.System.livereading,
        priority = -10,
        deliverTo = TopicHandler.DELIVER_TO.ORG
)
public class LiveReadingHandler extends BaseHandler {

    @Override
    public void processIncomingMessage(Message message) {
        //SessionManager.getInstance().sendMessage(message);
    }

    @Override
    public Message processOutgoingMessage(Message message) {
        return message;
    }
}
