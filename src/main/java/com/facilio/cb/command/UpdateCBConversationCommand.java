package com.facilio.cb.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class UpdateCBConversationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSessionConversation newchatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.NEW_CHAT_BOT_SESSION_CONVERSATION);
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		if(chatBotSessionConversation != null) {
			ChatBotUtil.updateChatBotSessionConversation(chatBotSessionConversation);
		}
		if(newchatBotSessionConversation != null) {
			ChatBotUtil.updateChatBotSessionConversation(newchatBotSessionConversation);
		}
		return false;
	}

}
