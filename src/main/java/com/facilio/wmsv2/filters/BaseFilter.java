package com.facilio.wmsv2.filters;

import com.facilio.wmsv2.message.Message;

public abstract class BaseFilter {
    private int priority;

    public abstract Message incoming(Message message);
    public abstract Message outgoing(Message message);

    public final void setPriority(int priority) {
        this.priority = priority;
    }

    public final int getPriority() {
        return priority;
    }
}
