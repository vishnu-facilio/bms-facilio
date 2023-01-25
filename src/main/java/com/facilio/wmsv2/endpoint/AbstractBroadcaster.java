package com.facilio.wmsv2.endpoint;

import com.facilio.wmsv2.message.Group;
import com.facilio.wmsv2.message.Message;

public abstract class AbstractBroadcaster extends DefaultBroadcaster {

    @Override
    public void broadcast(Message data, Group group) throws Exception {
        b(data, group);
    }

    private void b(Message data, Group group) throws Exception {
        outgoingMessage(data, group);
    }

    protected abstract void outgoingMessage(Message message, Group group) throws Exception;
    protected abstract void incomingMessage(Message message);
}
