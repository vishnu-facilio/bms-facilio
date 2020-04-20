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
		
		FacilioContext newcontext1 = null;
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
				
				newcontext1 = newcontext;
				
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
					
					newcontext1 = newcontext;
					
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
				
				newcontext1 = newcontext;
			}
		}
		
		if(newcontext1.containsKey(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED) && (Boolean)newcontext1.get(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED)) {
			ChatBotSession chatBotSession = (ChatBotSession)newcontext1.get(ChatBotConstants.CHAT_BOT_SESSION);
			
			context.put(ChatBotConstants.CHAT_BOT_MESSAGE_JSON, chatBotSession.getResponse());
			context.put(ChatBotConstants.CHAT_BOT_SESSION, chatBotSession);
		}
		else {
			ChatBotSessionConversation conversation = (ChatBotSessionConversation)newcontext1.get(ChatBotConstants.NEW_CHAT_BOT_SESSION_CONVERSATION);
			context.put(ChatBotConstants.CHAT_BOT_MESSAGE_JSON, conversation.getQuery());
			context.put(ChatBotConstants.NEW_CHAT_BOT_SESSION_CONVERSATION, conversation);
		}
		context.put(ChatBotConstants.CHAT_BOT_SUGGESTIONS, newcontext1.get(ChatBotConstants.CHAT_BOT_SUGGESTIONS));
		
		return false;
	}

}
