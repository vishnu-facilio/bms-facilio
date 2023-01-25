package com.facilio.agent;

import com.facilio.modules.FacilioIntEnum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum AgentType implements FacilioIntEnum {
    CUSTOM(0, "Custom", true, false, true),
    FACILIO(1, "Facilio", false, false, true),
    NIAGARA(2, "Niagara", false, false, true),
    CLOUD(3, "Cloud", true, false, false),
    REST(4, "Rest", true, false, false),
    RDM(6, "RDM", false, true, false),
    CLOUD_ON_SERVICE(7, "Cloud Service", true, true, false),
    MQTT(8, "MQTT Client", true, true, false),
    E2(9,"E2",false,true,false);


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

    @Override
    public Integer getIndex() {
        return key;
    }
    @Override
    public String getValue(){
        return this.label;
    }
}