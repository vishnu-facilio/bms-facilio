package com.facilio.bmsconsole.context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.struts2.ServletActionContext;

import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.fs.FileInfo;

public class PublicFileContext {

	long id;
	long orgId;
	long fileId;
	long expiresOn = -1;
	String key;
	FileInfo file;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	private Boolean isFromFilesTable;
	public Boolean getIsFromFilesTable() { return isFromFilesTable; }
	public void setIsFromFilesTable(Boolean isFromFilesTable) { this.isFromFilesTable = isFromFilesTable; }
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	public long getExpiresOn() {
		return expiresOn;
	}
	public void setExpiresOn(long expiresOn) {
		this.expiresOn = expiresOn;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public FileInfo getFile() {
		return file;
	}
	public void setFile(FileInfo file) {
		this.file = file;
	}
	public String getPublicUrl() throws UnsupportedEncodingException {
		if(key != null) {
			String clientAppUrl = FacilioProperties.getClientAppUrl();
			if(FacilioProperties.isDevelopment()) {
				clientAppUrl = FacilioProperties.getAppDomain();
			}
			StringBuilder builder = new StringBuilder(clientAppUrl)
					.append("/api/public/v2/files/preview?publicFileKey=").append(URLEncoder.encode(key, "UTF-8"));
			return builder.toString();
		}
		return null;
	}
}
