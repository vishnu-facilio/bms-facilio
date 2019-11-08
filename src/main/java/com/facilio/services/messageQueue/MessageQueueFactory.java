package com.facilio.services.messageQueue;

import com.facilio.aws.util.FacilioProperties;

public class MessageQueueFactory {

    public static MessageQueue getMessageQueue(){
        MessageQueue messageQueue = null;
        String messageQueueProp = FacilioProperties.getMessageQueue();
        switch (messageQueueProp){
            case "kafka": messageQueue = KafkaMessageQueue.getClient(); break;
            default: messageQueue = KinesisMessageQueue.getClient(); break;
        }
        return messageQueue;
    }
}
