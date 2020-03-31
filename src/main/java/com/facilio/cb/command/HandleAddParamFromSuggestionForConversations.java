package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSuggestionContext;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class HandleAddParamFromSuggestionForConversations extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		ChatBotIntentParam intentParam = (ChatBotIntentParam) context.get(ChatBotConstants.CHAT_BOT_ADD_ACTION_INTENT_PARAM);
		
		if(chatBotSessionConversation.getState() == ChatBotSessionConversation.State.CONFIRMATION_RAISED.getIntVal() && intentParam != null) {
			
			if(chatBotSessionConversation.getChatBotSession().getIntent().getContextWorkflow() != null) {
				
				ChatBotUtil.deleteSessionParam(intentParam.getId(), chatBotSessionConversation.getSessionId());
				ChatBotUtil.executeContextWorkflow(chatBotSessionConversation.getChatBotSession().getIntent(), chatBotSessionConversation.getChatBotSession(), context);
			}
			else {
				
				ChatBotSessionConversation newChatBotSessionConversation = ChatBotUtil.constructAndAddCBSessionConversationParams(intentParam, chatBotSessionConversation.getChatBotSession(),null,null);
				
				context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, newChatBotSessionConversation);
			}
			
			context.put(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION, Boolean.TRUE);
			
			chatBotSessionConversation.getChatBotSession().setState(ChatBotSession.State.WAITING_FOR_PARAMS.getIntVal());
			ChatBotUtil.updateChatBotSession(chatBotSessionConversation.getChatBotSession());
			
		}
		return false;
		
	}

}
