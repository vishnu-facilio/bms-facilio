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
			
			ChatBotSession waitingForConfirmationSession = ChatBotUtil.getLastWaitingForConfirmationSession();
			
			if(waitingForConfirmationSession == null) {
				
				FacilioChain chain = TransactionChainFactory.HandleChatBotSessionChain();
				
				FacilioContext newcontext = chain.getContext();
				
				newcontext.putAll(context);
				
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
				
				context.put(ChatBotConstants.CHAT_BOT_SUGGESTIONS, newcontext.get(ChatBotConstants.CHAT_BOT_SUGGESTIONS));
				context.put(ChatBotConstants.PREVIOUS_CHAT_BOT_SESSION_CONVERSATION, newcontext.get(ChatBotConstants.PREVIOUS_CHAT_BOT_SESSION_CONVERSATION));
			}
			else {
				ChatBotSessionConversation lastWaitingForConfirmationConversation = ChatBotUtil.getLastWaitingForConfirmationConversation(waitingForConfirmationSession.getId());
				
				if(lastWaitingForConfirmationConversation != null) {
					
					FacilioChain chain = TransactionChainFactory.HandleChatBotSessionConversationChain();
					
					FacilioContext newcontext = chain.getContext();
					
					newcontext.putAll(context);
					
					lastWaitingForConfirmationConversation.setResponseJson(chatMessageQuery);
					lastWaitingForConfirmationConversation.setChatBotSession(waitingForConfirmationSession);
					
					newcontext.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, lastWaitingForConfirmationConversation);
					
					chain.execute();
					
					if(newcontext.containsKey(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED) && (Boolean)newcontext.get(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED)) {
						ChatBotSession chatBotSession = (ChatBotSession)newcontext.get(ChatBotConstants.CHAT_BOT_SESSION);
						
						context.put(ChatBotConstants.CHAT_BOT_MESSAGE_JSON, chatBotSession.getResponse());
					}
					else {
						ChatBotSessionConversation conversation = (ChatBotSessionConversation)newcontext.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
						context.put(ChatBotConstants.CHAT_BOT_MESSAGE_JSON, conversation.getQuery());
					}
					context.put(ChatBotConstants.CHAT_BOT_SUGGESTIONS, newcontext.get(ChatBotConstants.CHAT_BOT_SUGGESTIONS));
					context.put(ChatBotConstants.PREVIOUS_CHAT_BOT_SESSION_CONVERSATION, newcontext.get(ChatBotConstants.PREVIOUS_CHAT_BOT_SESSION_CONVERSATION));
					return false;
				}
			}
		}
		else {
			
			ChatBotSessionConversation lastWaitingForParamConversation = ChatBotUtil.getLastWaitingForParamConversation(waitingForParamSession.getId());
			
			if(lastWaitingForParamConversation != null) {
				
				FacilioChain chain = TransactionChainFactory.HandleChatBotSessionConversationChain();
				
				FacilioContext newcontext = chain.getContext();
				
				newcontext.putAll(context);
				
				lastWaitingForParamConversation.setResponseJson(chatMessageQuery);
				lastWaitingForParamConversation.setChatBotSession(waitingForParamSession);
				
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
				
				context.put(ChatBotConstants.CHAT_BOT_SUGGESTIONS, newcontext.get(ChatBotConstants.CHAT_BOT_SUGGESTIONS));
				
				context.put(ChatBotConstants.PREVIOUS_CHAT_BOT_SESSION_CONVERSATION, newcontext.get(ChatBotConstants.PREVIOUS_CHAT_BOT_SESSION_CONVERSATION));
				
				return false;
			}
		}
		
		return false;
	}

}
