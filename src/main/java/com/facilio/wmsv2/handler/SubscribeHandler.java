package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.LiveSession;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;

@TopicHandler(
        topic = {Topics.System.subscribe, Topics.System.unsubscribe},
        priority = -9,
        deliverTo = TopicHandler.DELIVER_TO.SESSION,
        recordTimeout = 15
)
public class SubscribeHandler extends BaseHandler {

    @Override
    public void processIncomingMessage(Message message) {
        String topic = message.getTopic();
        String t = (String) message.getData("topic");

        LiveSession liveSession = message.getLiveSession();
        if (topic.equals(Topics.System.subscribe)) {
            liveSession.subscribe(t);
        } else if (topic.equals(Topics.System.unsubscribe)) {
            liveSession.unSubscribe(t);
        }
    }
}
