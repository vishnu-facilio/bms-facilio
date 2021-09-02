package com.facilio.bmsconsole.context;

import java.io.File;

import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class TemplateFileContext {
	
	long id;
	long orgId;
	long templateId;
	long fileId;
	File file;
	private long createdTime = -1;
	public String getDownloadUrl() throws Exception {
		if (this.downloadUrl == null && this.fileId > 0) {
			FileStore fs = FacilioFactory.getFileStore();
			downloadUrl = fs.getDownloadUrl(this.fileId);
		}
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	private String fileName;
	private String contentType;
	private String downloadUrl;
	
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public long getId() {
		return id;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
}
