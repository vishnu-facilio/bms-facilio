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
		return getFileStore(AccountUtil.getCurrentUser().getId());
	}
	
	public FileStore getFileStore(long ouid) {
		String environment = AwsUtil.getConfig("environment"); 
		
		FileStore fs = null;
		
		if ("production".equalsIgnoreCase(environment)) {
			// S3 store
			fs = new S3FileStore(AccountUtil.getCurrentOrg().getOrgId(), ouid);
		}
		else {
			// local store
			fs = new LocalFileStore(AccountUtil.getCurrentOrg().getOrgId(), ouid);
		}
		return fs;
	}
}
