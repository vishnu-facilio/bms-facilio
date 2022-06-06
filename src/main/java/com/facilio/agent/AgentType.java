package com.facilio.agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum AgentType {
    CUSTOM(0, "custom", true, false, true),
    FACILIO(1, "facilio", false, false, true),
    NIAGARA(2, "niagara", false, false, true),
    CLOUD(3, "cloud", true, false, false),
    REST(4, "rest", true, false, false),
    RDM(6, "RDM", false, true, false),
    CLOUD_ON_SERVICE(7, "cloud-on-service", true, true, false),
    MQTT(8, "mqtt", true, true, false);


    private int key;
    private String label;
    private boolean agentService;
    private boolean allowAutoAddition;
    private boolean isMqttConnectionRequired = false;

    AgentType(int key, String label) {
        this(key, label, false);
    }

    AgentType(int key, String label, boolean allowAutoAddition) {
        this(key, label, allowAutoAddition, false);
    }

    AgentType(int key, String label, boolean allowAutoAddition, boolean agentService) {
        this(key, label, allowAutoAddition, agentService, false);
    }

    AgentType(int key, String label, boolean allowAutoAddition, boolean agentService, boolean isMqttConnectionRequired) {
        this.key = key;
        this.label = label;
        this.agentService = agentService;
        this.allowAutoAddition = allowAutoAddition;
        this.isMqttConnectionRequired = isMqttConnectionRequired;
    }


    public int getKey() {
        return key;
    }

    public  String getLabel() {
        return label;
    }
    
    public boolean isAgentService() {
    	return agentService;
    }

    public static AgentType valueOf(int value) {
        return TYPE_MAP.get(value);
    }

    private static final Map<Integer, AgentType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());

    private static Map<Integer, AgentType> initTypeMap() {
        Map<Integer, AgentType> typeMap = new HashMap<>();
        for (AgentType type : values()) {
            typeMap.put(type.getKey(), type);
        }
        return typeMap;
    }

    public boolean isMqttConnectionRequired() {
        return isMqttConnectionRequired;
    }

    public boolean allowAutoAddition() {
        return allowAutoAddition;
    }
}