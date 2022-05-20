package com.facilio.bmsconsole.templates;

import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class TemplateAttachment {
	
	private long templateId;
	private String fileName;
	long id;
	long orgId;
	
	public TemplateAttachment setTemplateId(long templateId) {
		this.templateId = templateId;
		return this;
	}
	
	protected abstract long fetchFileId(Object record) throws Exception;
	
	public abstract TemplateAttachmentType getType();

	public String getTypeString() {
		return getType().toString();
	}

	public Integer getTypeInteger() {
		return getType().getIndex();
	}
	
	public String fetchFileUrl(Object record) throws Exception {
		long fileId = fetchFileId(record);
		if (fileId > 0 && getFileName() != null) {
			FileStore fs = FacilioFactory.getFileStore();
			return fs.getOrgiDownloadUrl(fileId);
		}
		return null;
	}

}
