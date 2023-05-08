package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.LiveSession;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.SessionInfo;

public class SubscribeHandler extends BaseHandler {

    @Override
    public void processIncomingMessage(Message message) {
        String topic = message.getTopic();
        String t = (String) message.getData("topic");
        SessionInfo sessionInfo = SessionInfo.getSessionInfo(message);
        LiveSession liveSession = sessionInfo.getLiveSession();
        if (topic.equals(Topics.System.subscribe)) {
            liveSession.subscribe(t);
        } else if (topic.equals(Topics.System.unsubscribe)) {
            liveSession.unSubscribe(t);
        }
    }
}
