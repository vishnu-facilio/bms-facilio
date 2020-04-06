package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class UpdateCBSessionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		ChatBotSession session = null;
		
		if(chatBotSessionConversation != null) {
			session = chatBotSessionConversation.getChatBotSession();
		}
		else {
			session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		}
		
		ChatBotUtil.updateChatBotSession(session);
		return false;
	}

}
