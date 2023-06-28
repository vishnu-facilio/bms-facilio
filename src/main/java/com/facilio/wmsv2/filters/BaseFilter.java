package com.facilio.wmsv2.filters;

import com.facilio.wmsv2.message.WebMessage;

public abstract class BaseFilter {
    private int priority;

    public abstract WebMessage incoming(WebMessage message);
    public abstract WebMessage outgoing(WebMessage message);

    public final void setPriority(int priority) {
        this.priority = priority;
    }

    public final int getPriority() {
        return priority;
    }
}
