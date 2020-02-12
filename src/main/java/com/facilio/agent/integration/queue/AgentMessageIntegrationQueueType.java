package com.facilio.agent.integration.queue;
/**
 * Enums for Agent Message Integration Queue Type
 */
public enum AgentMessageIntegrationQueueType {
    HTTP(0,"SimpleHTTP"),
    GOOGLE_PUB_SUB(1,"GooglePubSub"),
    KINESIS(2,"Kinesis"),
    KAFKA(3,"Kafka"),
    RABBITMQ(4,"RabbitMQ"),
    EVENTS_HUB(5,"Microsoft Events Hub");
    private int key;
    private String label;

    AgentMessageIntegrationQueueType(int key, String label) {
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
