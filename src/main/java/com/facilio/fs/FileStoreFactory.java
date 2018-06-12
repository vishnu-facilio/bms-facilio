package com.facilio.fs;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;

public class FileStoreFactory {

	private static FileStoreFactory instance;
	
	public static FileStoreFactory getInstance() {
		if (instance == null) {
			instance = new FileStoreFactory();
		}
		return instance;
	}
	
	public FileStore getFileStore() {
		if(AccountUtil.getCurrentUser() != null) {
			return getFileStore(AccountUtil.getCurrentUser().getId());
		} else {
			return getFileStore(-1);
		}
	}
	
	public FileStore getFileStore(long ouid) {
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		return getFileStoreFromOrg(orgId, ouid);
	}
	
	public FileStore getFileStoreFromOrg(long orgId) {
		long ouid = AccountUtil.getCurrentUser() != null ? AccountUtil.getCurrentUser().getId() : -1;
		return getFileStoreFromOrg(orgId, ouid);
	}
	
	public FileStore getFileStoreFromOrg(long orgId, long ouid) {
		String environment = AwsUtil.getConfig("environment"); 
		FileStore fs = null;
		if ("development".equalsIgnoreCase(environment)) {
			// local store
			fs = new LocalFileStore(orgId, ouid);
		} else {
			// S3 store
			fs = new S3FileStore(orgId, ouid);
		}
		return fs;
	}
}
