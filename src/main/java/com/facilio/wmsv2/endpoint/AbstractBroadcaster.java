package com.facilio.wmsv2.endpoint;

import com.facilio.wmsv2.message.Message;

public abstract class AbstractBroadcaster extends DefaultBroadcaster {

    @Override
    public void broadcast(Message data, boolean sendToAllWorkers) throws Exception {
        b(data, sendToAllWorkers);
    }

    private void b(Message data, boolean sendToAllWorkers) throws Exception {
        outgoingMessage(data, sendToAllWorkers);
    }

    protected abstract void outgoingMessage(Message message, boolean sendToAllWorkers) throws Exception;
    protected abstract void incomingMessage(Message message);
}
