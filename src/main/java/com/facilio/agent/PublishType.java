package com.facilio.agent;

/**
 * This enum is to map PublishType with its integer number.
 * Use this enum to handle publish-types in database.
 */
public enum PublishType {
    agent("agent",1),
    timeseries("timeseries",2),
    cov("cov",3),
    ack("ack",4),
    event("event",5),
    devicepoints("devicepoints",6);

    private int key;

    public String getValue() {
        return value;
    }

    private String value;

    PublishType(String value, int key) {
        this.value = value;
        this.key=key;
    }

    public int getKey() {
        return key;
    }

}



