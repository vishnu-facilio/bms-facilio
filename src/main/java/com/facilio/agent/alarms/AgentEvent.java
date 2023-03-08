package com.facilio.agent.alarms;

import com.facilio.agent.PublishType;
import org.apache.log4j.jmx.Agent;

import java.util.HashMap;
import java.util.Map;

public enum AgentEvent {

    TIMESERIES_DATA_COLLECTION_START("timeseriesDataCollectionStart",1),
    CONTROLLERS_MISSING("controllersMissing",3),
    POINTS_MISSING("pointsMissing",5),
    TIMESERIES_DATA_COLLECTION_END("timeseriesDataCollectionEnd",10),
    COMMAND_DELAY("commandDelay", 11)
    ;


    private int key;

    public String getValue() {
        return value;
    }

    private String value;

    AgentEvent(String value, int key) {
        this.value = value;
        this.key=key;
    }
    public int getKey() {
        return key;
    }

    public static Map<Integer, AgentEvent> initTypeMap() {
        Map<Integer, AgentEvent> typeMap = new HashMap<>();
        for(AgentEvent type : values()) {
            typeMap.put(type.getKey(), type);
        }
        return typeMap;
    }
}
