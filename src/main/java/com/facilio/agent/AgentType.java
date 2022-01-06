package com.facilio.agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum AgentType {
	CUSTOM(0, "custom"),
	FACILIO(1, "facilio"),
	NIAGARA(2, "niagara"),
    CLOUD(3,"cloud"),
    REST(4,"rest"),
    WATTSENSE(5, "wattsense"),
    RDM(6, "RDM", true),
    CLOUD_ON_SERVICE(7, "cloud-on-service", true),
    MQTT(8, "mqtt", true)
    ;


	private int key;
    private String label;
    private boolean agentService;

    AgentType(int key, String label) {
        this(key, label, false);
    }
    
    AgentType(int key, String label, boolean agentService) {
        this.key = key;
        this.label = label;
        this.agentService = agentService;
    }

    public  int getKey(){
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
        for(AgentType type : values()) {
            typeMap.put(type.getKey(), type);
        }
        return typeMap;
    }

}