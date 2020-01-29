package com.facilio.cb.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;

public class ChatBotAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String chatMessage;
	
	public String getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(String chatMessage) {
		this.chatMessage = chatMessage;
	}

	ChatBotSessionConversation chatBotSessionConversation;
	ChatBotSession chatBotSession;

	public ChatBotSessionConversation getChatBotSessionConversation() {
		return chatBotSessionConversation;
	}

	public void setChatBotSessionConversation(ChatBotSessionConversation chatBotSessionConversation) {
		this.chatBotSessionConversation = chatBotSessionConversation;
	}

	public ChatBotSession getChatBotSession() {
		return chatBotSession;
	}

	public void setChatBotSession(ChatBotSession chatBotSession) {
		this.chatBotSession = chatBotSession;
	}

	public String chat() throws Exception {
		
		if(chatMessage != null) {
			
			FacilioChain chain = TransactionChainFactory.HandleChatBotMessageChain();
			
			FacilioContext context = chain.getContext();
			context.put(ChatBotConstants.CHAT_BOT_MESSAGE_STRING, chatMessage);
			
			chain.execute();
			
			String chatBotReplyMessage = (String)context.get(ChatBotConstants.CHAT_BOT_MESSAGE_STRING);
			if(context.get(ChatBotConstants.CHAT_BOT_OPTION_STRING) != null) {
				chatBotReplyMessage += context.get(ChatBotConstants.CHAT_BOT_OPTION_STRING).toString();
			}
			
			setResult(ChatBotConstants.CHAT_BOT_MESSAGE_STRING, chatBotReplyMessage);
			
			setResult(ChatBotConstants.CHAT_BOT_OPTION_STRING, context.get(ChatBotConstants.CHAT_BOT_OPTION_STRING));
		}
		
		return SUCCESS;
	}
	
}
