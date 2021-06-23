package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;

public class GetCurrentSession extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		chatBotSessionConversation.setChatBotSession(ChatBotUtil.getSession(chatBotSessionConversation.getSessionId()));
		
		return false;
	}

}
