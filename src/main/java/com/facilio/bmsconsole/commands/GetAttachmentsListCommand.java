package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.TemplateFileContext;
import com.facilio.bmsconsole.context.TemplateFileFieldContext;
import com.facilio.bmsconsole.context.TemplateUrlContext;
import com.facilio.bmsconsole.templates.TemplateAttachment;
import com.facilio.bmsconsole.templates.TemplateAttachmentType;
import com.facilio.bmsconsole.util.TemplateAttachmentUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;


public class GetAttachmentsListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long templateId = (long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);	
		
		List<TemplateFileContext> attachmentList = null;
		List<TemplateUrlContext> urlAttachments = null;
		List<TemplateFileFieldContext> fileFieldsAttachments = null;
		
		for(TemplateAttachmentType type: TemplateAttachmentType.values()) {
			List<? extends TemplateAttachment> attachments = TemplateAttachmentUtil.fetchAttachments(templateId, type);
			if (attachments != null) {
				if (type == TemplateAttachmentType.FILE) {
					attachmentList = (List<TemplateFileContext>) attachments;
				}
				else if (type == TemplateAttachmentType.URL) {
					urlAttachments = (List<TemplateUrlContext>) attachments;
				}
				else if (type == TemplateAttachmentType.FIELD) {
					fileFieldsAttachments = (List<TemplateFileFieldContext>) attachments;
				}
			}
		}

		context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, attachmentList);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_URL_LIST, urlAttachments);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_FIELDID_LIST, fileFieldsAttachments);
		
		
		return false;
	}

}
