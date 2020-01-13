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
			
			
			setResult(ChatBotConstants.CHAT_BOT_MESSAGE_STRING, chatBotReplyMessage);
		}
		
		
//		if(chatBotSession != null) {
//			FacilioChain chain = TransactionChainFactory.HandleChatBotSessionChain();
//			
//			FacilioContext context = chain.getContext();
//			context.put(ChatBotConstants.CHAT_BOT_SESSION, chatBotSession);
//			
//			chain.execute();
//			
//			if(context.containsKey(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION)) {
//				setResult(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION));
//			}
//			else {
//				setResult(ChatBotConstants.CHAT_BOT_SESSION, context.get(ChatBotConstants.CHAT_BOT_SESSION));
//			}
//		}
//		else if(chatBotSessionConversation != null) {
//			
//			FacilioChain chain = TransactionChainFactory.HandleChatBotSessionConversationChain();
//			
//			FacilioContext context = chain.getContext();
//			context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, chatBotSessionConversation);
//			
//			chain.execute();
//			
//			if(context.containsKey(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED) && (Boolean)context.get(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED)) {
//				setResult(ChatBotConstants.CHAT_BOT_SESSION, context.get(ChatBotConstants.CHAT_BOT_SESSION));
//			}
//			else {
//				setResult(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION));
//			}
//		}
		return SUCCESS;
	}
	
}
