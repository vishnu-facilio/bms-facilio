package com.facilio.services.filestore;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;

public class FileStoreFactory {

	private static final FileStoreFactory INSTANCE = new FileStoreFactory();


	public static FileStoreFactory getInstance() {
		return INSTANCE;
	}
	
	public FileStore getFileStore() {
		if(AccountUtil.getCurrentUser() != null) {
			return getFileStore(AccountUtil.getCurrentUser().getId());
		} else {
			return getFileStore(-1);
		}
	}
	
	public FileStore getFileStore(long ouid) {
		if (ouid == -1) return getFileStoreFromOrg(-1L,ouid);
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		return getFileStoreFromOrg(orgId, ouid);
	}
	
	public FileStore getFileStoreFromOrg(long orgId) {
		long ouid = AccountUtil.getCurrentUser() != null ? AccountUtil.getCurrentUser().getId() : -1;
		return getFileStoreFromOrg(orgId, ouid);
	}
	
	public FileStore getFileStoreFromOrg(long orgId, long ouid) {
		String fileStoreProp = FacilioProperties.getFileStore();
		FileStore fs = null;
		switch (fileStoreProp){
			case "local": fs = new LocalFileStore(orgId,ouid); break;
			case "s3"	: fs = new S3FileStore(orgId,ouid); break;
			case "onpremise": fs = new FacilioFileStore(orgId,ouid); break;
			case "azure" : fs = new AzureFileStore(orgId,ouid); break;
			case "oci": fs = new OracleFileStore(orgId,ouid); break;
			default: fs = new S3FileStore(orgId,ouid); break;
		}
		return fs;
	}
}
