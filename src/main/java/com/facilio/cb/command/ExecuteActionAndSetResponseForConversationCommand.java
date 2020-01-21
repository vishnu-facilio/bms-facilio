package com.facilio.cb.command;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSessionParam;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class ExecuteActionAndSetResponseForConversationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotMLResponse chatBotMLResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
			
		if(chatBotSessionConversation.getState() != ChatBotSessionConversation.State.REPLIED_INCORRECTLY.getIntVal()) { 		//correct case
			
			chatBotSessionConversation.setState(ChatBotSessionConversation.State.REPLIED_CORRECTLY.getIntVal());
			
			ChatBotUtil.updateChatBotSessionConversation(chatBotSessionConversation);
			
			ChatBotUtil.addSessionParam(chatBotSessionConversation.getIntentParamId(),chatBotSessionConversation.getChatBotSession().getId(),chatBotMLResponse.getAnswer());
			
			chatBotSessionConversation.getChatBotSession().setRecievedParamCount(chatBotSessionConversation.getChatBotSession().getRecievedParamCount()+1);
			
			ChatBotUtil.updateChatBotSession(chatBotSessionConversation.getChatBotSession());
			
			if(chatBotSessionConversation.getChatBotSession().getRequiredParamCount() == chatBotSessionConversation.getChatBotSession().getRecievedParamCount()) {
				
				ChatBotIntent intent = chatBotSessionConversation.getChatBotSession().getIntent();
				
				List<Object> props = ChatBotUtil.fetchAllSessionParams(chatBotSessionConversation.getSessionId());
				
				context.put(ChatBotConstants.CHAT_BOT_INTENT, intent);
				
				String response = intent.executeActions(context, props);
				
				ChatBotSession session = chatBotSessionConversation.getChatBotSession();
				session.setResponse(response);
				session.setState(ChatBotSession.State.RESPONDED.getIntVal());
				
				ChatBotUtil.updateChatBotSession(session);
				
				context.put(ChatBotConstants.CHAT_BOT_SESSION, chatBotSessionConversation.getChatBotSession());
				context.put(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED, true);
			}
			else {
				
				List<ChatBotIntentParam> remainingIntentParamList = ChatBotUtil.fetchRemainingChatBotIntentParams(chatBotSessionConversation.getChatBotSession().getIntent().getId(),chatBotSessionConversation.getChatBotSession().getId());
				
				ChatBotSessionConversation newChatBotSessionConversation = ChatBotUtil.constructCBSessionConversationParams(remainingIntentParamList.get(0), chatBotSessionConversation.getChatBotSession(),null,null);
				
				context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, newChatBotSessionConversation);
			}
			
		}
		else {
			
			ChatBotUtil.updateChatBotSessionConversation(chatBotSessionConversation);
			
			ChatBotIntentParam intentParam = ChatBotUtil.getIntentParams(chatBotSessionConversation.getIntentParamId());
			
			ChatBotSessionConversation newChatBotSessionConversation = ChatBotUtil.constructCBSessionConversationParams(intentParam, chatBotSessionConversation.getChatBotSession(),chatBotSessionConversation,null);
			
			context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, newChatBotSessionConversation);
		}
			
		return false;
	}
	
}