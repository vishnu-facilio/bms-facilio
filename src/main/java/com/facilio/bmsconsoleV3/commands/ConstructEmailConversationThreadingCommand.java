package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.EmailToModuleDataContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;

public class ConstructEmailConversationThreadingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		EmailToModuleDataContext emailToModuleData = (EmailToModuleDataContext) context.get(MailMessageUtil.EMAIL_TO_MODULE_DATA_MODULE_NAME);
		FacilioModule module = (FacilioModule)context.get(FacilioConstants.ContextNames.MODULE);
		BaseMailMessageContext mailContext = (BaseMailMessageContext)context.get(MailMessageUtil.BASE_MAIL_CONTEXT);
		
		EmailConversationThreadingContext emailConversationContext = FieldUtil.getAsBeanFromJson(FieldUtil.getAsJSON(mailContext), EmailConversationThreadingContext.class);
		
		emailConversationContext.setParentBaseMail(mailContext);
		emailConversationContext.setDataModuleId(module.getModuleId());
		emailConversationContext.setRecordId(emailToModuleData.getRecordId());
		
		emailConversationContext.setFromType(EmailConversationThreadingContext.From_Type.CLIENT.getValue());
		emailConversationContext.setMessageType(EmailConversationThreadingContext.Message_Type.REPLY.getValue());
		
		context.put(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME, emailConversationContext);
		
		return false;
	}

}
