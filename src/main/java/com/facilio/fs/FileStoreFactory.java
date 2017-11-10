package com.facilio.fs;

import com.facilio.aws.util.AwsUtil;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;

public class FileStoreFactory {

	private static FileStoreFactory instance;
	
	public static FileStoreFactory getInstance() {
		if (instance == null) {
			instance = new FileStoreFactory();
		}
		return instance;
	}
	
	public FileStore getFileStore() {
		return getFileStore(UserInfo.getCurrentUser().getOrgUserId());
	}
	
	public FileStore getFileStore(long userId) {
		String environment = AwsUtil.getConfig("environment"); 
		
		FileStore fs = null;
		
		if ("production".equalsIgnoreCase(environment)) {
			// S3 store
			fs = new S3FileStore(OrgInfo.getCurrentOrgInfo().getOrgid(), userId);
		}
		else {
			// local store
			fs = new LocalFileStore(OrgInfo.getCurrentOrgInfo().getOrgid(), userId);
		}
		return fs;
	}
}
