package com.facilio.agent.integration.queue;
/**
 * Enums for Agent Message Integration Processor type
 */
public enum AgentMessageIntegrationProcessorType {

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
