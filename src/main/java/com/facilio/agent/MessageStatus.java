package com.facilio.agent;

public enum MessageStatus{
    RECIEVED(0),
    PROCESSED(1),
    DATA_EMPTY(2);

    public int getStatusKey() {
        return statusKey;
    }

    private int statusKey;
    MessageStatus(int statusKey) {
        this.statusKey = statusKey;
    }

}
