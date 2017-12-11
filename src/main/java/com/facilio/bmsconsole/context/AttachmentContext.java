package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class AttachmentContext extends ModuleBaseWithCustomFields {
	
	private long parentId = -1;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private long fileId = -1;
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	
	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private String fileName;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	private long fileSize = -1;
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	private long uploadedBy = -1;
	public long getUploadedBy() {
		return uploadedBy;
	}
	public void setUploadedBy(long uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	
	private long uploadedTime = -1;
	public long getUploadedTime() {
		return uploadedTime;
	}
	public void setUploadedTime(long uploadedTime) {
		this.uploadedTime = uploadedTime;
	}
	
	private String contentType;
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	private AttachmentType type;
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = AttachmentType.getType(type);
	}
	public void setType(AttachmentType type) {
		this.type = type;
	}
	
	public String getPreviewUrl() throws Exception {
		if (this.fileId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			return fs.getPrivateUrl(this.fileId);
		}
		return null;
	}
	
	public static enum AttachmentType {
		BEFORE,
		AFTER
		;
		
		public int getIntVal() {
			return ordinal()+1;
		}
		
		public static AttachmentType getType(int type) {
			return ATTACHMENT_TYPES[type -1];
		}
		
		private static final AttachmentType[] ATTACHMENT_TYPES = AttachmentType.values();
	}
}
