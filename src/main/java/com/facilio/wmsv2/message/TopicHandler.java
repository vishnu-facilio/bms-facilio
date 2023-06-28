package com.facilio.wmsv2.message;

public interface TopicHandler {
    enum DELIVER_TO { USER, ORG, APP }

    interface DEFAULT {
        String deliverTo = "ORG";
        int priority = -5;
    }

}
