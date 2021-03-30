package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.control.util.ControlScheduleUtil;

public class SendEmailForEmailConversationThreadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		EmailConversationThreadingContext emailConversation = (EmailConversationThreadingContext) ControlScheduleUtil.getObjectFromRecordMap(context, MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
		
		
		if(emailConversation.getFromType() == EmailConversationThreadingContext.From_Type.ADMIN.getValue()) {
			if(emailConversation.getMessageType() == EmailConversationThreadingContext.Message_Type.REPLY.getValue()) {
				// send mail to parent thread
			}
		}
		return false;
	}

}
