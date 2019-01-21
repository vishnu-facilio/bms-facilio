package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.transaction.FTransactionManager;
import com.facilio.transaction.FacilioTransaction;

public class AddAttachmentRelationshipCommand implements Command {

	private static Logger LOGGER = Logger.getLogger(AddAttachmentRelationshipCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME);
		if(moduleName == null ) {
			moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		}
		
		Long recordId = null;
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (recordIds == null || recordIds.isEmpty()) {
			recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		}
		else {
			recordId = recordIds.get(0); //Have to discuss why this is done like this.
		}
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Invalid module name during addition of attachments");
		}
		
		if(recordId == null || recordId == -1) {
			throw new IllegalArgumentException("Invalid record id during addition of attachments");
		}
		context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, Collections.singletonList(recordId));
		List<AttachmentContext> attachments = (List<AttachmentContext>) context.get(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST);
		if(attachments != null && !attachments.isEmpty()) {
			for(AttachmentContext attachment : attachments) {
				attachment.setParentId(recordId);
			}
			
			if (AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 151 || AccountUtil.getCurrentOrg().getId() == 92) {
				LOGGER.info("Inserting attachment for record id : "+recordId);
				TransactionManager tm = FTransactionManager.getTransactionManager();
				if (tm != null) {
					Transaction t = tm.getTransaction();
					LOGGER.info("Connection & free connection size before adding attachments : "+((FacilioTransaction) t).getConnectionSize()+"::"+((FacilioTransaction) t).getFreeConnectionSize());
				}
			}
			
			AttachmentsAPI.addAttachments(attachments, moduleName);
			List<Long> attachmentIds = new ArrayList<>();
			for (AttachmentContext ac : attachments) {
				attachmentIds.add(ac.getId());
			}
			
			context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, AttachmentsAPI.getAttachments(moduleName, attachmentIds));
			if(moduleName.equals(FacilioConstants.ContextNames.TICKET_ATTACHMENTS)) {
				context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ADD_TICKET_ATTACHMENTS);
			}
		}
		
		return false;
	}
}