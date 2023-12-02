package com.facilio.services.sandboxfilestore;


import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;

public class SandboxFileStoreFactory {
    private static final SandboxFileStoreFactory INSTANCE = new SandboxFileStoreFactory();

    public static SandboxFileStoreFactory getInstance() {
        return INSTANCE;
    }

    public SandboxFileStore getFileStore(String bucketName) {
        long ouid = AccountUtil.getCurrentUser() != null ? AccountUtil.getCurrentUser().getId() : -1;
        long orgId = AccountUtil.getCurrentOrg() != null ? AccountUtil.getCurrentOrg().getOrgId() : -1;
        return getFileStoreObj(orgId, ouid, bucketName);
    }

    public SandboxFileStore getFileStoreFromOUId(long ouid, String bucketName) {
        long orgId = AccountUtil.getCurrentOrg() != null ? AccountUtil.getCurrentOrg().getOrgId() : -1;
        return getFileStoreObj(orgId, ouid, bucketName);
    }

    public SandboxFileStore getFileStoreFromOrg(long orgId, String bucketName) {
        long ouid = AccountUtil.getCurrentUser() != null ? AccountUtil.getCurrentUser().getId() : -1;
        return getFileStoreObj(orgId, ouid, bucketName);
    }

    public SandboxFileStore getFileStoreObj(long orgId, long ouid, String bucketName) {
        String fileStoreProp = FacilioProperties.getSandboxFileStore();
        SandboxFileStore fs = null;
        switch (fileStoreProp) {
            case "local":
                fs = new SandboxLocalFileStore(orgId, ouid);
                break;
            default:
                fs = new SandboxS3FileStore(orgId, ouid, bucketName);
                break;
        }
        return fs;
    }
}
