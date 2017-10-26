package com.facilio.bmsconsole.context;

import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class AttachmentContext {
	
	private long attachmentId;
	public long getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(long attachmentId) {
		this.attachmentId = attachmentId;
	}
	
	private long fileId;
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private Long uploadedBy;
	public Long getUploadedBy() {
		return uploadedBy;
	}
	public void setUploadedBy(Long uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	
	private Long uploadedTime;
	public Long getUploadedTime() {
		return uploadedTime;
	}
	public void setUploadedTime(Long uploadedTime) {
		this.uploadedTime = uploadedTime;
	}
	
	private String fileName;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	private Long fileSize;
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	
	private String contentType;
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	private long parentId = 0;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private String parentModuleLinkName;
	public String getParentModuleLinkName() {
		return parentModuleLinkName;
	}
	public void setParentModuleLinkName(String parentModuleLinkName) {
		this.parentModuleLinkName = parentModuleLinkName;
	}
	
	public String getPreviewUrl() throws Exception {
		if (this.fileId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			return fs.getPrivateUrl(this.fileId);
		}
		return null;
	}
}
