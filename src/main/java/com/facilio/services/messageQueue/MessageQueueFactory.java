package com.facilio.services.messageQueue;

public class MessageQueueFactory {

    // check usage when making changes here.
    public static MessageQueue getMessageQueue(){
        return KafkaMessageQueue.getClient();
    }
}
