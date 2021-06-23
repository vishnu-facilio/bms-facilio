package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AttachmentContext;
import com.facilio.bmsconsole.util.AttachmentsAPI;
import com.facilio.constants.FacilioConstants;

public class GetAttachmentsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(moduleName == null || recordId == null) {
			return false;
		}
		
		List<AttachmentContext> attachments = AttachmentsAPI.getAttachments(moduleName, recordId);
		
		context.put(FacilioConstants.ContextNames.ATTACHMENT_LIST, attachments);
		return false;
	}

}
