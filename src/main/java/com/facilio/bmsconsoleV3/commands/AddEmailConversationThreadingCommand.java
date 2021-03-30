package com.facilio.bmsconsoleV3.commands;

import java.util.Collections;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.fw.BeanFactory;

public class AddEmailConversationThreadingCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EmailConversationThreadingContext emailConversationContext = (EmailConversationThreadingContext)context.get(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		V3RecordAPI.addRecord(false, Collections.singletonList(emailConversationContext), modBean.getModule(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME), modBean.getAllFields(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME));
		return false;
	}

}
