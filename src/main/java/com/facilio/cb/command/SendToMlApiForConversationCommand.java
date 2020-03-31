package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSuggestionContext;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class SendToMlApiForConversationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		if(chatBotSessionConversation != null && chatBotSessionConversation.getResponseString() != null) {
			
			String answer = null;
			String intent = null;
			double accuracy;
			
			answer = ChatBotUtil.getRequiredFieldFromQueryJson(chatBotSessionConversation.getResponseJson()).toString();
			accuracy = 0.6789;
			
			if(answer.toLowerCase().contains("exit") || answer.toLowerCase().contains("cancel")) {
				intent = "system_terminate_session_intent";
				accuracy = 0.6789;
			}
			else if(chatBotSessionConversation.getState() == ChatBotSessionConversation.State.CONFIRMATION_RAISED.getIntVal()) {
				
				String chatMeg = ChatBotUtil.getRequiredFieldFromQueryJson(chatBotSessionConversation.getResponseJson()).toString();
				
				if(chatMeg.startsWith("Add")) {
					ChatBotIntentParam intentParam = ChatBotUtil.getIntentParamWithAddTriggerText(chatMeg);
					if(intentParam == null) {
						intent = "dummy_intent";
						accuracy = 0.01;
					}
					else {
						intent = "dummy_intent";
						context.put(ChatBotConstants.CHAT_BOT_ADD_ACTION_INTENT_PARAM, intentParam);
					}
				}
				else if(chatMeg.startsWith("Edit")) {
					ChatBotIntentParam intentParam = ChatBotUtil.getIntentParamWithUpdateTriggerText(chatMeg);
					if(intentParam == null) {
						intent = "dummy_intent";
						accuracy = 0.01;
					}
					else {
						intent = "dummy_intent";
						context.put(ChatBotConstants.CHAT_BOT_EDIT_ACTION_INTENT_PARAM, intentParam);
					}
				}
				else if(answer.toLowerCase().contains("yes") || answer.toLowerCase().contains("ok") || answer.toLowerCase().contains("confirm")) { 
					intent = "system_affirmative_intent";
					accuracy = 0.6789;
					chatBotSessionConversation.getChatBotSession().setConfirmed(Boolean.TRUE);
				}
				else if(answer.toLowerCase().contains("no")) {
					intent = "system_terminate_session_intent";
					accuracy = 0.6789;
					chatBotSessionConversation.getChatBotSession().setConfirmed(Boolean.FALSE);
				}
				else {
					intent = "dummy_intent";
					accuracy = 0.01;
				}
			}
			
			ChatBotMLResponse mlResponse = new ChatBotMLResponse();
			
			mlResponse.setAccuracy(accuracy);
			mlResponse.setAnswer(answer);
			mlResponse.setIntent(intent);
			
			context.put(ChatBotConstants.CHAT_BOT_ML_RESPONSE, mlResponse);
			
			return false;
		}
		return true;
	}

}
