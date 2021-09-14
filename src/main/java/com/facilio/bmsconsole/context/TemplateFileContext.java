package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.templates.TemplateAttachment;
import com.facilio.bmsconsole.templates.TemplateAttachmentType;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TemplateFileContext extends TemplateAttachment {
	
	private long fileId;
	
	@Override
	public TemplateAttachmentType getType() {
		return TemplateAttachmentType.FILE;
	}
	
	@Override
	protected long fetchFileId(Object record) {
		return getFileId();
	}
}
