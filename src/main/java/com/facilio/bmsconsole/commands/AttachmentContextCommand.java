package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.context.AttachmentContext.AttachmentType;
import com.facilio.constants.FacilioConstants;

public class AttachmentContextCommand extends FacilioCommand {
	
	private static Logger LOGGER = Logger.getLogger(AttachmentContextCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Long> attachmentIdList = (List<Long>) context.get(FacilioConstants.ContextNames.ATTACHMENT_ID_LIST);
		Long currentTime = (Long) context.get(FacilioConstants.ContextNames.CURRENT_TIME);
		if(currentTime == null){
			currentTime = System.currentTimeMillis();
		}

		if(attachmentIdList != null) {
			AttachmentType type = (AttachmentType) context.get(FacilioConstants.ContextNames.ATTACHMENT_TYPE);
			List<AttachmentContext> attachments = new ArrayList<>();
			if(context.get(FacilioConstants.ContextNames.EXISTING_ATTACHMENT_LIST) != null ) {
				attachments = (List<AttachmentContext>) context.get(FacilioConstants.ContextNames.EXISTING_ATTACHMENT_LIST);
			}
			for(long attachmentId : attachmentIdList) {
				AttachmentContext attachment = new AttachmentContext();
				attachment.setFileId(attachmentId);
				attachment.setCreatedTime(currentTime);
				attachment.setType(type);
				
				attachments.add(attachment);
			}
			context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST, attachments);
		}
		return false;
	}
}