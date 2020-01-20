package com.facilio.cb.command;

import java.util.Collection;
import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotIntentParam.ML_Type;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotMLUtil;
import com.facilio.cb.util.ChatBotWitAIUtil;

public class SendToMlApiCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		ChatBotModel model = (ChatBotModel) context.get(ChatBotConstants.CHAT_BOT_MODEL);
		
		boolean isMLWithFacilio = false;
		
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
			
			ChatBotMLResponse mlResponse = new ChatBotMLResponse();
			
			//send to ml starts
			String intentName = null;
			double accuracy = 0;
			
			
			if(isMLWithFacilio) {
				
				JSONObject responseJSON = ChatBotMLUtil.getIntentFromML(session.getQuery(), null, model);
				
				intentName = (String) responseJSON.get(ChatBotMLUtil.ML_INTENT_STRING);
				accuracy = 0.6789;
			}
			else {
				JSONObject responseJSON = ChatBotWitAIUtil.getIntentFromML(session.getQuery());
				
				JSONObject entityJson = (JSONObject)responseJSON.get(ChatBotWitAIUtil.ENTITY_STRING);
				
				Collection<ChatBotIntentParam.ML_Type> mlDataTypes =  ChatBotIntentParam.ML_Type.getAllMLTypes().values();
				
//				for(ML_Type mlDataType :mlDataTypes) {
//					if(entityJson.containsKey(mlDataType.getMLName())) {
//						JSONArray mlTypesList = (JSONArray)entityJson.get(mlDataType.getMLName());
//						JSONObject mlTypeJson = (JSONObject)mlTypesList.get(0);
//						
//						if((Boolean)mlTypeJson.get("suggested")) {
//							
//						}
//					}
//				}
				
				if(entityJson.containsKey(ChatBotWitAIUtil.INTENT_STRING)) {
					
					JSONArray intentList = (JSONArray)entityJson.get(ChatBotWitAIUtil.INTENT_STRING);
					
					JSONObject firstIntent = (JSONObject) intentList.get(0);
					
					accuracy = (Double) firstIntent.get(ChatBotWitAIUtil.CONFIDENCE_STRING);
					intentName = (String)firstIntent.get(ChatBotWitAIUtil.VALUE_STRING);
				}
				else {
					intentName = "dummy_intent";
					accuracy = 0.01;
				}
				
			}
			
			//ends
			
			mlResponse.setAccuracy(accuracy);
			mlResponse.setIntent(intentName);
			
			context.put(ChatBotConstants.CHAT_BOT_ML_RESPONSE, mlResponse);
			
			return false;
			
		}
		return true;
	}

}
