package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.templates.TemplateAttachment;
import com.facilio.bmsconsole.templates.TemplateAttachmentType;

public class TemplateFileContext extends TemplateAttachment {
	
	public String getDownloadUrl() throws Exception {
		return null;
	}
	
	@Override
	public TemplateAttachmentType getType() {
		return TemplateAttachmentType.FILE;
	}
	
	@Override
	protected long fetchFileId(Object record) {
		return getFileId();
	}
}
