package com.facilio.services.factory;

import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;


import java.util.Objects;

public class FacilioFactory {

    private static final Object LOCK = new Object();

    private FacilioFactory(){}

   /* public static FileStore getFileStore(){

        return FileStoreFactory.getInstance().getFileStore();

    }*/


    public static EmailClient getEmailClient(){
        return EmailFactory.getEmailClient();
    }

 /*   public static MessageQueue getMessageQueue(){
       return MessageQueueFactory.getMessageQueue();
    }
    public static FacilioQueue getQueue(){
        return QueueFactory.getQueue();
    }
    public static FacilioIot getIotClient(){
        return FacilioIotFactory.getIotClient();
    }*/

}
