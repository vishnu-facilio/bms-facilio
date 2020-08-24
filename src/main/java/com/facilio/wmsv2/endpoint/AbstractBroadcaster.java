package com.facilio.wmsv2.endpoint;

import com.facilio.wmsv2.message.Message;

import java.io.IOException;

public abstract class AbstractBroadcaster extends DefaultBroadcaster {

    @Override
    public void broadcast(Message data) throws IOException {
        b(data);
    }

    private void b(Message data) {
        outgoingMessage(data);
    }

    protected abstract void outgoingMessage(Message message);
    protected abstract void incomingMessage(Message message);
}
