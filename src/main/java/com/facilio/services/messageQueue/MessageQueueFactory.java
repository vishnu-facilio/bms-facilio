package com.facilio.services.messageQueue;

public class MessageQueueFactory {

    public static MessageQueue getMessageQueue(){
        return KafkaMessageQueue.getClient();
    }
}
