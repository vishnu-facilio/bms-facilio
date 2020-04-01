package com.facilio.agent;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum is to map PublishType with its integer number.
 * Use this enum to handle publish-types in database.
 */
public enum PublishType {
	CUSTOM("agent",0),
    AGENT("agent",1),
    TIMESERIES("timeseries",2),
    COV("cov",3),
    ACK("ack",4),
    EVENT("event",5),
    DEVICE_POINTS("devicepoints",6),
    CONTROLLERS("controllers",7),
    AGENT_EVENTS("agentEvents",8);

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

    public static Map<Integer, PublishType> initTypeMap() {
        Map<Integer, PublishType> typeMap = new HashMap<>();
        for(PublishType type : values()) {
            typeMap.put(type.getKey(), type);
        }
        return typeMap;
    }

}



