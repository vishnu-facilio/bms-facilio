package com.facilio.queue;

public class ObjectMessage extends QueueMessage {

    Object serializable;

    public ObjectMessage(String id, String message) {
        super(id, message);
    }

    public Object getSerializable() {
        return serializable;
    }

    public void setSerializable(Object serializable) {
        this.serializable = serializable;
    }
}
