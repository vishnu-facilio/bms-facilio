package com.facilio.agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum AgentType {
    Misc(0, "Misc"),

    BACnet(1, "BACnet"),

    Niagara(3, "Niagara");



    private int key;
    private String label;

    AgentType(int key, String label) {
        this.key = key;
        this.label = label;
    }

    public  int getKey(){
        return key;
    }

    public  String getLabel() {
        return label;
    }

    public static AgentType valueOf(int value) {
        return TYPE_MAP.get(value);
    }

    private static final Map<Integer, AgentType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
    private static Map<Integer, AgentType> initTypeMap() {
        Map<Integer, AgentType> typeMap = new HashMap<>();
        for(AgentType type : values()) {
            typeMap.put(type.getKey(), type);
        }
        return typeMap;
    }

}