package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.EmailAttachmentAPI;
import com.facilio.bmsconsole.context.TemplateFileContext;
import com.facilio.bmsconsole.context.TemplateFileFieldContext;
import com.facilio.bmsconsole.context.TemplateUrlContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;


public class GetAttachmentsListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long templateId = (long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);	
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		List<TemplateFileContext> attachmentList = EmailAttachmentAPI.getAttachments(templateId);
		
		List<TemplateUrlContext> urlAttachments = EmailAttachmentAPI.getUrlAttachments(templateId);
		
		List<TemplateFileFieldContext> fileFieldsAttachments = EmailAttachmentAPI.getFileFieldAttachments(templateId);
		
		context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, attachmentList);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_URL_LIST, urlAttachments);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_FIELDID_LIST, fileFieldsAttachments);
		
		
		return false;
	}

	

}
