package com.facilio.services.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.services.email.EmailClient;
import com.facilio.services.email.EmailFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.filestore.FileStoreFactory;
import com.facilio.services.sandboxfilestore.SandboxFileStore;
import com.facilio.services.sandboxfilestore.SandboxFileStoreFactory;

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

    public static SandboxFileStore getSandboxFileStore() {
        return getSandboxFileStore(null);
    }

    public static SandboxFileStore getSandboxFileStore(String bucketName) {
        return SandboxFileStoreFactory.getInstance().getFileStore(bucketName);
    }

    public static SandboxFileStore getSandboxFileStoreFromOUId(long ouid) {
        return getSandboxFileStoreFromOUId(ouid, null);
    }

    public static SandboxFileStore getSandboxFileStoreFromOUId(long ouid, String bucketName) {
        return SandboxFileStoreFactory.getInstance().getFileStoreFromOUId(ouid, bucketName);
    }

    public static SandboxFileStore getSandboxFileStoreFromOrg(long orgId) {
        return getSandboxFileStoreFromOrg(orgId, null);
    }

    public static SandboxFileStore getSandboxFileStoreFromOrg(long orgId, String bucketName) {
        return SandboxFileStoreFactory.getInstance().getFileStoreFromOrg(orgId, bucketName);
    }

    public static SandboxFileStore getSandboxFileStore(long orgId, long ouid) {
        return getSandboxFileStore(orgId, ouid, null);
    }

    public static SandboxFileStore getSandboxFileStore(long orgId, long ouid, String bucketName) {
        return SandboxFileStoreFactory.getInstance().getFileStoreObj(orgId, ouid, bucketName);
    }

}
