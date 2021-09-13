package com.facilio.bmsconsole.context;

public class TemplateFileFieldContext {
	private long id;
	private long orgId;
	private long templateId;
	public long getId() {
		return id;
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
	public long getFileFileId() {
		return fileFileId;
	}
	public void setFileFileId(long fileFileId) {
		this.fileFileId = fileFileId;
	}
	private long fileFileId;
}
