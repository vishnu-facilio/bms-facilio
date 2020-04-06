package com.facilio.cb.command;

import java.util.Collection;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotMLUtil;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.cb.util.ChatBotWitAIUtil;

public class SendToMlApiForSessionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		ChatBotModel model = (ChatBotModel) context.get(ChatBotConstants.CHAT_BOT_MODEL);
		
		boolean isMLWithFacilio = true;
		
		if(session != null && session.getQueryJson() != null) {
			
			ChatBotMLResponse mlResponse = new ChatBotMLResponse();
			
			String intentName = null;
			double accuracy = 0;
			
			
			if(isMLWithFacilio) {
				
				JSONObject responseJSON = ChatBotMLUtil.getIntentFromML(ChatBotUtil.getRequiredFieldFromQueryJson(session.getQueryJson()).toString(), null, model);
				
				JSONObject intentJson = (JSONObject) responseJSON.get(ChatBotMLUtil.ML_INTENT_STRING);
				
				intentName = (String) intentJson.get(ChatBotMLUtil.ML_INTENT_NAME_STRING);
				
				accuracy = (double) intentJson.get(ChatBotMLUtil.ML_INTENT_CONFIDENCE_STRING);
				
				JSONObject entityJson = (JSONObject)responseJSON.get(ChatBotWitAIUtil.ENTITY_STRING);
				
				if(entityJson != null) {
					mlResponse.setEntityJson(entityJson);
				}
			}
			else {
				JSONObject responseJSON = ChatBotWitAIUtil.getIntentFromML(ChatBotUtil.getRequiredFieldFromQueryJson(session.getQueryJson()).toString());
				
				JSONObject entityJson = (JSONObject)responseJSON.get(ChatBotWitAIUtil.ENTITY_STRING);
				
				Collection<ChatBotIntentParam.ML_Type> mlDataTypes =  ChatBotIntentParam.ML_Type.getAllMLTypes().values();
				
//				for(ML_Type mlDataType :mlDataTypes) {
//					if(entityJson.containsKey(mlDataType.getMLName())) {
//						JSONArray mlTypesList = (JSONArray)entityJson.get(mlDataType.getMLName());
//						JSONObject mlTypeJson = (JSONObject)mlTypesList.get(0);
//						
//						if((Boolean)mlTypeJson.get("suggested")) {
//							String value = (String) mlTypeJson.get("value");
//							mlResponse.addMlParams(mlDataType.getMLName(), value);
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
		
		return false;
	}

}
