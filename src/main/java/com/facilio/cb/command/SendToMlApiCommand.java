package com.facilio.cb.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotMLUtil;

public class SendToMlApiCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		ChatBotModel model = (ChatBotModel) context.get(ChatBotConstants.CHAT_BOT_MODEL);
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		
		if(chatBotSessionConversation != null && chatBotSessionConversation.getResponse() != null) {
			
			String answer = null;
			String intent = null;
			double accuracy;
			
			answer = chatBotSessionConversation.getResponse();
			accuracy = 0.6789;
			
			if(chatBotSessionConversation.getResponse().contains("exit")) {
				intent = "system_terminate_session_intent";
				accuracy = 0.6789;
			}
//			else {
//				answer = chatBotSessionConversation.getResponse();
//				accuracy = 0.0789;
//			}
			
			ChatBotMLResponse mlResponse = new ChatBotMLResponse();
			
			mlResponse.setAccuracy(accuracy);
			mlResponse.setAnswer(answer);
			mlResponse.setIntent(intent);
			
			context.put(ChatBotConstants.CHAT_BOT_ML_RESPONSE, mlResponse);
			
			return false;
		}
		else if(session != null && session.getQuery() != null) {
			
			//send to ml starts
			String intentName = null;
			double accuracy;
			
			JSONObject responseJSON = ChatBotMLUtil.getIntentFromML(session.getQuery(), null, model);
			
			intentName = (String) responseJSON.get(ChatBotMLUtil.ML_INTENT_STRING);
			accuracy = 0.6789;
			
//			if(session.getQuery().equals("hi")) {
//				intentName = "system_greet_intent";
//				accuracy = 0.6789;
//			}
//			else if (session.getQuery().equals("create a wo")) {
//				intentName = "wo_create";
//				accuracy = 0.7789;
//			}
//			else if (session.getQuery().contains("what")) {
//				intentName = "system_greet_me_intent";
//				accuracy = 0.7789;
//			}
//			else {
//				intentName = "system_greet_intent"; 
//				accuracy = 0.1789;
//			}
			
			//ends
			
			ChatBotMLResponse mlResponse = new ChatBotMLResponse();
			
			mlResponse.setAccuracy(accuracy);
			mlResponse.setIntent(intentName);
			
			context.put(ChatBotConstants.CHAT_BOT_ML_RESPONSE, mlResponse);
			
			return false;
			
		}
		return true;
	}

}
