package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;

public class DeleteTemplateAttachmentCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long templateId = (long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);	
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<Long> attachmentIds = (List<Long>) context.get(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST);
		FacilioModule module = ModuleFactory.getTemplateFileModule();
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(attachmentIds, module));

		context.put(FacilioConstants.ContextNames.ROWS_UPDATED, builder.delete());
		
		
		return false;
	}

	

}
