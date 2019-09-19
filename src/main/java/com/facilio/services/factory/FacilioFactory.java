package com.facilio.services.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;
import com.facilio.services.filestore.*;
import com.facilio.services.email.AWSemail;
import com.facilio.services.iot.FacilioIot;
import com.facilio.services.iot.FacilioIotFactory;
import com.facilio.services.messageQueue.KinesisMessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.queue.QueueFactory;
import com.facilio.services.queue.SQSQueue;
import com.facilio.services.queue.InMemoryQueueService;
import com.facilio.services.email.FacilioEmail;
import com.facilio.services.messageQueue.KafkaMessageQueue;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.queue.FacilioQueue;

import java.util.Objects;

public class FacilioFactory {

    private static final Object LOCK = new Object();

    private FacilioFactory(){}

    public static FileStore getFileStore(){

        return FileStoreFactory.getInstance().getFileStore();

    }


    public static EmailClient getEmailClient(){
        return EmailFactory.getEmailClient();
    }

    public static MessageQueue getMessageQueue(){
       return MessageQueueFactory.getMessageQueue();
    }
    public static FacilioQueue getQueue(){
        return QueueFactory.getQueue();
    }
    public static FacilioIot getIotClient(){
        return FacilioIotFactory.getIotClient();
    }

}
