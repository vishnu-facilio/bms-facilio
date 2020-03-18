package com.facilio.cb.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;

public class HandleChatBotMessageCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		JSONObject chatMessageQuery = (JSONObject) context.get(ChatBotConstants.CHAT_BOT_MESSAGE_JSON);
		
		ChatBotSession waitingForParamSession = ChatBotUtil.getLastWaitingForParamSession();
		
		
		if(waitingForParamSession == null) {
			
			FacilioChain chain = TransactionChainFactory.HandleChatBotSessionChain();
			
			FacilioContext newcontext = chain.getContext();
			
			ChatBotSession chatBotSession = new ChatBotSession();
			chatBotSession.setQueryJson(chatMessageQuery);
			newcontext.put(ChatBotConstants.CHAT_BOT_SESSION, chatBotSession);
			
			chain.execute();
			
			if(newcontext.containsKey(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION)) {
				ChatBotSessionConversation conversation = (ChatBotSessionConversation)newcontext.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
				context.put(ChatBotConstants.CHAT_BOT_MESSAGE_JSON, conversation.getQuery());
			}
			else {
				chatBotSession = (ChatBotSession)newcontext.get(ChatBotConstants.CHAT_BOT_SESSION);
				
				context.put(ChatBotConstants.CHAT_BOT_MESSAGE_JSON, chatBotSession.getResponse());
			}
		}
		else {
			
			ChatBotSessionConversation lastWaitingForParamConversation = ChatBotUtil.getLastWaitingForParamConversation(waitingForParamSession.getId());
			
			if(lastWaitingForParamConversation != null) {
				
				FacilioChain chain = TransactionChainFactory.HandleChatBotSessionConversationChain();
				
				lastWaitingForParamConversation.setResponseJson(chatMessageQuery);
				lastWaitingForParamConversation.setChatBotSession(waitingForParamSession);
				
				FacilioContext newcontext = chain.getContext();
				newcontext.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, lastWaitingForParamConversation);
				
				chain.execute();
				
				if(newcontext.containsKey(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED) && (Boolean)newcontext.get(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED)) {
					ChatBotSession chatBotSession = (ChatBotSession)newcontext.get(ChatBotConstants.CHAT_BOT_SESSION);
					
					context.put(ChatBotConstants.CHAT_BOT_MESSAGE_JSON, chatBotSession.getResponse());
				}
				else {
					ChatBotSessionConversation conversation = (ChatBotSessionConversation)newcontext.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
					context.put(ChatBotConstants.CHAT_BOT_MESSAGE_JSON, conversation.getQuery());
				}
				
				return false;
			}
		}
		
		return false;
	}

}
