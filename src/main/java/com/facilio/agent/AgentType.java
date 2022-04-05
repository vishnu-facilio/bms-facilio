package com.facilio.agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum AgentType {
	CUSTOM(0, "custom"),
	FACILIO(1, "facilio", false),
	NIAGARA(2, "niagara", false),
    CLOUD(3,"cloud"),
    REST(4,"rest"),
    WATTSENSE(5, "wattsense"),
    RDM(6, "RDM", false, true),
    CLOUD_ON_SERVICE(7, "cloud-on-service", true, true),
    MQTT(8, "mqtt", true, true)
    ;


	private int key;
    private String label;
    private boolean agentService;
    private boolean allowAutoAddition;

    AgentType(int key, String label) {
        this(key, label, true);
    }
    
    AgentType(int key, String label, boolean allowAutoAddition) {
    	this(key, label, allowAutoAddition, false);
    }
    
    AgentType(int key, String label, boolean allowAutoAddition, boolean agentService) {
        this.key = key;
        this.label = label;
        this.agentService = agentService;
        this.allowAutoAddition = allowAutoAddition;
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
    
    public boolean allowAutoAddition() {
    	return allowAutoAddition;
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