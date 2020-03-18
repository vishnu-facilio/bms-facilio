package com.facilio.cb.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSessionParam;
import com.facilio.cb.context.ChatBotSuggestionContext;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class HandleEditParamFromSuggestionForConversations extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		ChatBotSuggestionContext suggestion = (ChatBotSuggestionContext) context.get(ChatBotConstants.CHAT_BOT_SUGGESTION);
		
		if(suggestion != null && suggestion.getType() == ChatBotSuggestionContext.Type.EDITABLE_PARAM.getIntVal()) {
			
			ChatBotIntentParam intentParam = ChatBotUtil.getIntentParam(suggestion.getIntentParamId());
			
			ChatBotSessionParam sessionParam = ChatBotUtil.fetchSessionParam(chatBotSessionConversation.getSessionId(), suggestion.getIntentParamId());
			
			ChatBotSessionConversation newChatBotSessionConversation = ChatBotUtil.constructAndAddCBSessionConversationParams(intentParam, chatBotSessionConversation.getChatBotSession(),null,null);
			
			JSONObject json = new JSONObject();
			json.put(ChatBotConstants.CHAT_BOT_LABEL, sessionParam.getValue());
			
			newChatBotSessionConversation.setResponseString(json.toJSONString());
			
			context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, newChatBotSessionConversation);
			
			context.put(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION, Boolean.TRUE);
			
			chatBotSessionConversation.getChatBotSession().setState(ChatBotSession.State.WAITING_FOR_PARAMS.getIntVal());
			ChatBotUtil.updateChatBotSession(chatBotSessionConversation.getChatBotSession());
		}
		return false;
	}

}
