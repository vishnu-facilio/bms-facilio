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
		
		Boolean skipActionExecution = (Boolean) context.get(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION);
		
		if(skipActionExecution != null && skipActionExecution) {
			return false;
		}
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		ChatBotIntentParam intentParam = (ChatBotIntentParam) context.get(ChatBotConstants.CHAT_BOT_EDIT_ACTION_INTENT_PARAM);
		
		if(chatBotSessionConversation.getState() == ChatBotSessionConversation.State.CONFIRMATION_RAISED.getIntVal() && intentParam != null) {
			
			ChatBotSessionConversation prevoiusConversation = ChatBotUtil.getSessionConversationForParam(chatBotSessionConversation.getChatBotSession().getId(), intentParam.getId());
			
			context.put(ChatBotConstants.PREVIOUS_CHAT_BOT_SESSION_CONVERSATION, prevoiusConversation);
			
			if(chatBotSessionConversation.getChatBotSession().getIntent().getContextWorkflow() != null) {
				
				chatBotSessionConversation.getChatBotSession().addParamsObject("client_message", intentParam.getUpdateParamTriggerText());
				
//				chatBotSessionConversation.getChatBotSession().deleteParamObject(intentParam.getName());
				
				ChatBotUtil.executeContextWorkflow(chatBotSessionConversation.getChatBotSession().getIntent(), chatBotSessionConversation.getChatBotSession(), context);
			}
			else {
				
				ChatBotSessionParam sessionParam = ChatBotUtil.fetchSessionParam(chatBotSessionConversation.getSessionId(), intentParam.getId());
				
				ChatBotSessionConversation newChatBotSessionConversation = ChatBotUtil.constructAndAddCBSessionConversationParams(intentParam, chatBotSessionConversation.getChatBotSession(),null,null);
				
				JSONObject json = new JSONObject();
				json.put(ChatBotConstants.CHAT_BOT_LABEL, sessionParam.getValue());
				
				newChatBotSessionConversation.setResponseString(json.toJSONString());
				
				context.put(ChatBotConstants.NEW_CHAT_BOT_SESSION_CONVERSATION, newChatBotSessionConversation);
				
			}
			chatBotSessionConversation.getChatBotSession().setState(ChatBotSession.State.WAITING_FOR_PARAMS.getIntVal());
			
			chatBotSessionConversation.setState(ChatBotSessionConversation.State.REPLIED_CORRECTLY.getIntVal());
			
			context.put(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION, Boolean.TRUE);
			
			
		}
		return false;
	}

}
