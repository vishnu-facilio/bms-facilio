package com.facilio.bmsconsole.context;

import org.apache.commons.lang.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;

public class AttachmentContext extends ModuleBaseWithCustomFields {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
		if (type != -1) {
			this.type = AttachmentType.getType(type);			
		}
	}
	public void setType(AttachmentType type) {
		this.type = type;
	}
	
	public String getPreviewUrl() throws Exception {
		if (this.fileId > 0) {
			if (!AccountUtil.getCurrentAccount().isFromMobile() && StringUtils.isNotEmpty(attachmentModule) && recordId > 0) {
				StringBuilder builder = new StringBuilder(AwsUtil.getConfig("clientapp.url"))
						.append("/api/v2/").append(attachmentModule).append("/attachment/").append(recordId).append("?fileId=").append(fileId);
				return builder.toString();
			}
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			return fs.getPrivateUrl(this.fileId);
		}
		return null;
	}
	public String getDownloadUrl() throws Exception {
		if (this.fileId > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			return fs.getDownloadUrl(this.fileId);
		}
		return null;
	}
	
	private String attachmentModule;
	public void setAttachmentModule(String attachmentModule) {
		this.attachmentModule = attachmentModule;
	}
	
	private long recordId = -1;
	public void setRecordId(long recordId) {
		this.recordId = recordId;
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
