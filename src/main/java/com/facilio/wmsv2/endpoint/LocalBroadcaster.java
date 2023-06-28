package com.facilio.wmsv2.endpoint;


import com.facilio.wmsv2.message.WebMessage;

public class LocalBroadcaster extends DefaultBroadcaster {

    private static LocalBroadcaster broadcaster = new LocalBroadcaster();

    public static DefaultBroadcaster getBroadcaster() {
        return broadcaster;
    }

    @Override
    protected void broadcast(WebMessage message) {

    }

    @Override
    public void subscribe(String... topics) {

    }

    @Override
    public void unsubscribe(String... topics) {

    }
}
