package com.facilio.bmsconsoleV3.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;

public class AddEmailsToPeopleCommandV3 extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<EmailConversationThreadingContext> emailConversations = Constants.getRecordList((FacilioContext) context);
		for(EmailConversationThreadingContext emailConversation : emailConversations) {
			if(emailConversation.getCc() != null && !emailConversation.getCc().isEmpty())
			{
				String[] cclist = emailConversation.getCc().toLowerCase().split(",");
				if(cclist.length!=0) {
					for(String cc : cclist) {
							PeopleAPI.getOrAddRequester(cc);		
					}
				}
			}
			if(emailConversation.getBcc() != null && !emailConversation.getBcc().isEmpty()) {
				String[] bcclist = emailConversation.getBcc().toLowerCase().split(",");
				if(bcclist.length!=0) {
					for(String bcc : bcclist) {
							PeopleAPI.getOrAddRequester(bcc);
					}
				}
			}
		}
	return false;

	}

}
