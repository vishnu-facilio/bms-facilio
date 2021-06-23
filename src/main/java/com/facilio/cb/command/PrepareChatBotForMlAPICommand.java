package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSession.State;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.time.DateTimeUtil;

public class PrepareChatBotForMlAPICommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		if(chatBotSessionConversation != null && chatBotSessionConversation.getResponseString() != null) {
			
			chatBotSessionConversation.setOrgId(AccountUtil.getCurrentOrg().getId());
			
			return false;
		}
		else if(session != null && session.getQueryJson() != null) {
			session.setOrgId(AccountUtil.getCurrentOrg().getId());
			session.setUserId(AccountUtil.getCurrentUser().getId());
			session.setStartTime(DateTimeUtil.getCurrenTime());
			return false;
		}
		
		return true;
	}

}
