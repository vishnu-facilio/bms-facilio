package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.Broadcaster;
import com.facilio.wmsv2.endpoint.LiveSession;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.WebMessage;
import com.facilio.wmsv2.util.WmsUtil;
import lombok.extern.log4j.Log4j;

@Log4j
public class SubscribeHandler extends WmsHandler {

    @Override
    public void processIncomingMessage(WebMessage message) {
        String handlerTopic = message.getTopic();
        if(message.getData("topic")==null) {
            LOGGER.error("Topic name is missing.");
            return;
        }
        String[] topics = ((String) message.getData("topic")).replaceAll(" ", "").split(",");
        LiveSession liveSession = message.getLiveSession();
        try {
            switch (handlerTopic) {
                case Topics.System.subscribe:
                    subscribe(liveSession, topics);
                    break;
                case Topics.System.unsubscribe:
                    unsubscribe(liveSession, topics);
                    break;
            }
        } catch (Exception e){
            LOGGER.error("Sub/Unsub failed with exception ", e);
        }
    }

    private void subscribe(LiveSession liveSession, String[] topics) {
        for(String topic : topics) {
            String redisTopic = WmsUtil.convertToRedisTopic(liveSession, topic);
            if(redisTopic != null) {
                Broadcaster.getBroadcaster().subscribe(redisTopic);
                liveSession.subscribe(redisTopic);
                SessionManager.getInstance().addTopicsLiveSession(redisTopic, liveSession.getId());
            }
        }
    }

    private void unsubscribe(LiveSession liveSession, String[] topics) {
        for(String topic : topics) {
            String redisTopic = WmsUtil.convertToRedisTopic(liveSession, topic);
            liveSession.unSubscribe(redisTopic);
            if(SessionManager.getInstance().removeTopicsLiveSession(redisTopic, liveSession.getId())) {
                Broadcaster.getBroadcaster().unsubscribe(redisTopic);
            }
        }
    }

}
