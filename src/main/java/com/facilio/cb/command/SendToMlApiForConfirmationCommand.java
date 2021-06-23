package com.facilio.cb.command;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;

public class SendToMlApiForConfirmationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		
		if(chatBotSessionConversation != null && chatBotSessionConversation.getResponseString() != null) {
			
			String answer = null;
			String intent = null;
			double accuracy;
			
			answer = ChatBotUtil.getRequiredFieldFromQueryJson(chatBotSessionConversation.getResponseJson()).toString();
			accuracy = 0.6789;
			
			if(answer.contains("yes")) {
				intent = "system_affirmative_intent";
				accuracy = 0.6789;
				chatBotSessionConversation.getChatBotSession().setConfirmed(Boolean.TRUE);
			}
			
			ChatBotMLResponse mlResponse = new ChatBotMLResponse();
			
			mlResponse.setAccuracy(accuracy);
			mlResponse.setAnswer(answer);
			mlResponse.setIntent(intent);
			
			context.put(ChatBotConstants.CHAT_BOT_ML_RESPONSE, mlResponse);
			
			return false;
		}
		
		return false;
	}

}
