package com.facilio.services.messageQueue;

import com.facilio.aws.util.FacilioProperties;

public class MessageQueueFactory {

    // check usage when making changes here.
    public static MessageQueue getMessageQueue(){

        MessageQueue messageQueue;
        String messageQueueProp = FacilioProperties.getMessageQueue();
        if ("kafka".equalsIgnoreCase(messageQueueProp)) {
            messageQueue = KafkaMessageQueue.getClient();
        } else {
            messageQueue = KinesisMessageQueue.getClient();
        }
        return messageQueue;
    }
}
