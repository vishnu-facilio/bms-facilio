package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.util.V3AttachmentAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.context.Constants;

public class AddAttachmentsForEmailConversationThreadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<EmailConversationThreadingContext> emailConversations = Constants.getRecordList((FacilioContext) context);
		
		
		if(emailConversations != null && !emailConversations.isEmpty()) {
			
			List<Long> ids = emailConversations.stream().map(EmailConversationThreadingContext::getId).collect(Collectors.toList());
			
			List<AttachmentV3Context> attachments = V3AttachmentAPI.getAttachments(ids, MailMessageUtil.EMAIL_CONVERSATION_THREADING_ATTACHMENT_MODULE);
			
			if(attachments != null && !attachments.isEmpty()) {
				Map<Long, List<AttachmentV3Context>> attachmentMap = attachments.stream().collect(Collectors.groupingBy(AttachmentV3Context::getParentId));
				
				for(EmailConversationThreadingContext emailConversation : emailConversations) {
					
					if(attachmentMap.containsKey(emailConversation.getId())) {
						
						emailConversation.setAttachmentsList(FieldUtil.getAsMapList(attachmentMap.get(emailConversation.getId()), AttachmentV3Context.class));
					}
				}
			}
			
		}
		
		return false;
	}

}
