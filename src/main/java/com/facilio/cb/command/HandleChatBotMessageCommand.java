package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.cards.util.ChatBotUtil;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;

public class HandleChatBotMessageCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String chatMessageQuery = (String) context.get(ChatBotConstants.CHAT_BOT_MESSAGE_STRING);
		
		ChatBotSession waitingForParamSession = ChatBotUtil.getLastWaitingForParamSession();
		
		if(waitingForParamSession != null) {
			
			ChatBotSessionConversation lastWaitingForParamConversation = ChatBotUtil.getLastWaitingForParamConversation(waitingForParamSession.getId());
			
			if(lastWaitingForParamConversation != null) {
				
				FacilioChain chain = TransactionChainFactory.HandleChatBotSessionConversationChain();
				
				lastWaitingForParamConversation.setResponse(chatMessageQuery);
				
				FacilioContext newcontext = chain.getContext();
				newcontext.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, lastWaitingForParamConversation);
				
				chain.execute();
				
				if(newcontext.containsKey(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED) && (Boolean)newcontext.get(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED)) {
					ChatBotSession chatBotSession = (ChatBotSession)newcontext.get(ChatBotConstants.CHAT_BOT_SESSION);
					
					context.put(ChatBotConstants.CHAT_BOT_MESSAGE_STRING, chatBotSession.getResponse());
				}
				else {
					ChatBotSessionConversation conversation = (ChatBotSessionConversation)newcontext.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
					context.put(ChatBotConstants.CHAT_BOT_MESSAGE_STRING, conversation.getQuery());
				}
				
				
				return false;
			}
		}
		
		FacilioChain chain = TransactionChainFactory.HandleChatBotSessionChain();
		
		FacilioContext newcontext = chain.getContext();
		
		ChatBotSession chatBotSession = new ChatBotSession();
		chatBotSession.setQuery(chatMessageQuery);
		newcontext.put(ChatBotConstants.CHAT_BOT_SESSION, chatBotSession);
		
		chain.execute();
		
		if(newcontext.containsKey(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION)) {
			ChatBotSessionConversation conversation = (ChatBotSessionConversation)newcontext.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
			context.put(ChatBotConstants.CHAT_BOT_MESSAGE_STRING, conversation.getQuery());
		}
		else {
			chatBotSession = (ChatBotSession)newcontext.get(ChatBotConstants.CHAT_BOT_SESSION);
			
			context.put(ChatBotConstants.CHAT_BOT_MESSAGE_STRING, chatBotSession.getResponse());
		}
		
		
		return false;
	}

}
