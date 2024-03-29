package com.facilio.cb.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.command.FacilioCommand;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotIntentParam.ML_Type;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotDateTimeUtil;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.date.calenderandclock.CalenderAndClockContext;
import com.facilio.modules.FieldUtil;

public class HandleIntentParamsForConversationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Boolean skipActionExecution = (Boolean) context.get(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION);
		
		if(skipActionExecution != null && skipActionExecution) {
			return false;
		}
		
		ChatBotSessionConversation chatBotSessionConversation = (ChatBotSessionConversation) context.get(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION);
		
		ChatBotMLResponse chatBotMLResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);
		
		if(chatBotSessionConversation.getState() != ChatBotSessionConversation.State.REPLIED_INCORRECTLY.getIntVal()) { 		//correct case
			
			boolean isFilledByEntityJson = false;
			if(chatBotMLResponse.getEntityJson() != null) {
				
				ChatBotIntentParam chatBotParam = chatBotSessionConversation.getIntentParam();
				
				if(chatBotParam.getMlTypeEnum() != null && chatBotMLResponse.getEntityJson().containsKey(chatBotParam.getMlTypeEnum().getMLName())) {
					
					JSONArray entityList = (JSONArray) chatBotMLResponse.getEntityJson().get(chatBotParam.getMlTypeEnum().getMLName());
					isFilledByEntityJson = true;
					if(chatBotParam.isMultipleAllowed()) {
						JSONArray valuesArray = new JSONArray();
						for(int i=0;i < entityList.size();i++) {
							JSONObject json = (JSONObject) entityList.get(i);
							String value = json.get("value").toString();
							valuesArray.add(value);
						}
						ChatBotUtil.deleteAndAddSessionParam(chatBotParam, chatBotSessionConversation.getChatBotSession(), valuesArray);
					}
					else {
						JSONObject json = (JSONObject) entityList.get(0);
						Object value = null;
						if(chatBotParam.getMlTypeEnum() == ML_Type.DATE) {
							JSONObject cbDate = (JSONObject) json.get("value");
							CalenderAndClockContext cbDateContext = FieldUtil.getAsBeanFromJson(cbDate, CalenderAndClockContext.class);
							value = ChatBotDateTimeUtil.compute(chatBotParam, cbDateContext);
						}
						else {
							value = json.get("value").toString();
						}
						
						ChatBotUtil.deleteAndAddSessionParam(chatBotParam, chatBotSessionConversation.getChatBotSession(), value);
					}
				}
			}
			
			if(chatBotSessionConversation.getIntentParamId() > 0 && !isFilledByEntityJson) {
				ChatBotUtil.deleteAndAddSessionParam(chatBotSessionConversation.getIntentParam(),chatBotSessionConversation.getChatBotSession(),chatBotMLResponse.getAnswer());	// even user may enter wrong answer on that case ML will correct it, so answer is taken form mlResponse
			}
			
			chatBotSessionConversation.getChatBotSession().setRecievedParamCount(chatBotSessionConversation.getChatBotSession().getRecievedParamCount()+1);
			
			ChatBotUtil.updateChatBotSessionConversation(chatBotSessionConversation);
			
		}
		
		
		return false;
	}

}
