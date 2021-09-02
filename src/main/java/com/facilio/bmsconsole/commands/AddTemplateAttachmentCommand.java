package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.EmailAttachmentAPI;
import com.facilio.bmsconsole.context.TemplateFileContext;
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
		
		EmailAttachmentAPI.addAttachments(attachmentIds, moduleName, templateId);
		
		
		return false;
	}	

	

	

}
