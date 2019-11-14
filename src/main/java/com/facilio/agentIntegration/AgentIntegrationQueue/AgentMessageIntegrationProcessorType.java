package com.facilio.agentIntegration.AgentIntegrationQueue;

public enum AgentMessageIntegrationProcessorType {

    WATTSENSE(1,"Wattsense"),
    ALTAIR(2,"Altair");

    private int key;
    private String label;

    AgentMessageIntegrationProcessorType(int key, String label) {
        this.key = key;
        this.label = label;
    }

    public  int getKey(){
        return key;
    }

    public  String getLabel() {
        return label;
    }
}
