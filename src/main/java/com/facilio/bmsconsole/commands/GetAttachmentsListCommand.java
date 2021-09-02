package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.EmailAttachmentAPI;
import com.facilio.bmsconsole.context.TemplateFileContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;


public class GetAttachmentsListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long templateId = (long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);	
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		List<TemplateFileContext> attachmentList = EmailAttachmentAPI.getAttachments(moduleName, templateId);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, attachmentList);
		
		
		return false;
	}

	

}
