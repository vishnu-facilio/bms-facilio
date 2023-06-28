package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.message.TopicHandler;
import com.facilio.wmsv2.message.WebMessage;
import lombok.Data;

@Data
public class WmsHandler {

    private String[] topics;
    private TopicHandler.DELIVER_TO deliverTo;
    private int priority;

    protected void setTopics(String... topics) {
        this.topics = topics;
    }
    public void processIncomingMessage(WebMessage message) { }

    public void processOutgoingMessage(WebMessage message) { }

}
