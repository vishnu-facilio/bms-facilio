package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.EmailAttachmentAPI;
import com.facilio.bmsconsole.context.TemplateFileContext;
import com.facilio.bmsconsole.context.TemplateUrlContext;
import com.facilio.bmsconsole.forms.FormRuleTriggerFieldContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddTemplateAttachmentCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> attachmentIds = (List<Long>) context.get(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST);
		long templateId = (long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);	
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<String> urlList = (List<String>) context.get(FacilioConstants.ContextNames.ATTACHMENT_URL_LIST);
		
		if(attachmentIds != null && !attachmentIds.isEmpty()) {
			List<TemplateFileContext> attachments = new ArrayList<>();
			for(long attachmentId : attachmentIds) {
				TemplateFileContext attachment = new TemplateFileContext();	
				attachment.setFileId(attachmentId);
				attachment.setTemplateId(templateId);
				
				attachments.add(attachment);
			}
			List<FacilioField> fields = FieldFactory.getTemplateFileFields();
			FacilioModule module = ModuleFactory.getTemplateFileModule();
			EmailAttachmentAPI.addAttachments(attachments, module, templateId, TemplateFileContext.class, fields);
		}
		
		if(urlList != null && !urlList.isEmpty()) {
			List<TemplateUrlContext> attachments = new ArrayList<>();
			for(String url : urlList) {
				TemplateUrlContext attachment = new TemplateUrlContext();	
				attachment.setUrlString(url);
				attachment.setTemplateId(templateId);	
				attachments.add(attachment);
			}
			List<FacilioField> fields = FieldFactory.getTemplateUrlFields();
			FacilioModule module = ModuleFactory.getTemplateUrlAttachmentModule();
			EmailAttachmentAPI.addAttachments(attachments, module, templateId, TemplateUrlContext.class, fields);
		}
		
		

		return false;

	}	

}
