package com.facilio.services.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.filestore.FileStoreFactory;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;

public class FacilioFactory {


    private FacilioFactory(){}

    public static FileStore getFileStore(){
        return FileStoreFactory.getInstance().getFileStore();
    }

    public static FileStore getFileStore(long ouid) {
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        return getFileStoreFromOrg(orgId, ouid);
    }

    public static EmailClient getEmailClient(){
        return EmailFactory.getEmailClient();
    }

    public static FileStore getFileStoreFromOrg(long id) {
        return FileStoreFactory.getInstance().getFileStoreFromOrg(id);
    }

    public static FileStore getFileStoreFromOrg(long orgId, long ouid) {
        return FileStoreFactory.getInstance().getFileStoreFromOrg(orgId,ouid);
    }

   public static MessageQueue getMessageQueue(){
       return MessageQueueFactory.getMessageQueue();
    }

}
