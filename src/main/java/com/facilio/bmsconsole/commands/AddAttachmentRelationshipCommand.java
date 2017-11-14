package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.constants.FacilioConstants;

public class AddAttachmentRelationshipCommand implements Command {

	private static Logger LOGGER = Logger.getLogger(AddAttachmentRelationshipCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		List<Long> attachmentIdList = (List<Long>) context.get(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST);
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Invalid module name during addition of attachments");
		}
		
		if(recordId == -1) {
			throw new IllegalArgumentException("Invalid record id during addition of attachments");
		}
		
		if(attachmentIdList != null && !attachmentIdList.isEmpty()) {
			List<AttachmentContext> attachments = new ArrayList<>();
			for(long attachmentId : attachmentIdList) {
				AttachmentContext attachment = new AttachmentContext();
				attachment.setFileId(attachmentId);
				attachment.setParentId(recordId);
				attachment.setCreatedTime(System.currentTimeMillis());
				
				attachments.add(attachment);
			}
			
			AttachmentsAPI.addAttachments(attachments, moduleName);
			List<Long> attachmentIds = new ArrayList<>();
			for (AttachmentContext ac : attachments) {
				attachmentIds.add(ac.getId());
			}
			
			context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, AttachmentsAPI.getAttachments(moduleName, attachmentIds));
		}
		return false;
	}
}