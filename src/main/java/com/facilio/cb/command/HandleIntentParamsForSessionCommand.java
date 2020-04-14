package com.facilio.cb.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.cb.context.ChatBotDateContext;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotIntentParam.ML_Type;
import com.facilio.cb.context.ChatBotMLResponse;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotTimeContext;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotDateTimeUtil;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;

public class HandleIntentParamsForSessionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Boolean skipActionExecution = (Boolean) context.get(ChatBotConstants.CHAT_BOT_SKIP_ACTION_EXECUTION);
		
		if(skipActionExecution != null && skipActionExecution) {
			return false;
		}
		
		ChatBotSession session = (ChatBotSession) context.get(ChatBotConstants.CHAT_BOT_SESSION);
		
		ChatBotIntent intent = (ChatBotIntent) context.get(ChatBotConstants.CHAT_BOT_INTENT);
		
		ChatBotMLResponse chatBotMLResponse = (ChatBotMLResponse) context.get(ChatBotConstants.CHAT_BOT_ML_RESPONSE);
		
		if(intent.isWithParams()) {
			
			int requriedCount = ChatBotUtil.getRequiredParamCount(intent.getChatBotIntentParamList());
			
			int recievedCount = 0;
			
			int size = intent.getChatBotIntentParamList().size();
			
			List<ChatBotIntentParam> filledParams = new ArrayList<>();
			
			ChatBotSession prevoiusSession = ChatBotUtil.checkChildIntentAndGetPrevoisSessionContext(intent.getId());
			
			if(prevoiusSession != null) {
				
				Map<String, Object> resProps = ChatBotUtil.fetchAllSessionParams(prevoiusSession.getId());
				
				for(ChatBotIntentParam chatBotParam : intent.getChatBotIntentParamList()) {
					
					if(chatBotParam.isFillableByParent() && resProps.containsKey(chatBotParam.getName())) {
						
						ChatBotUtil.deleteAndAddSessionParam(chatBotParam, session, resProps.get(chatBotParam.getName()));
						
						filledParams.add(chatBotParam);
						recievedCount++;
					}
				}
				intent.getChatBotIntentParamList().removeAll(filledParams);
			}
			if(chatBotMLResponse.getEntityJson() != null) {
				
				filledParams = new ArrayList<>();
				
				for(ChatBotIntentParam chatBotParam : intent.getChatBotIntentParamList()) {
					
					if(chatBotParam.getMlTypeEnum() != null && chatBotMLResponse.getEntityJson().containsKey(chatBotParam.getMlTypeEnum().getMLName())) {
						
						JSONArray entityList = (JSONArray) chatBotMLResponse.getEntityJson().get(chatBotParam.getMlTypeEnum().getMLName());
						
						if(chatBotParam.isMultipleAllowed()) {
							JSONArray valuesArray = new JSONArray();
							for(int i=0;i < entityList.size();i++) {
								JSONObject json = (JSONObject) entityList.get(i);
								String value = json.get("value").toString();
								valuesArray.add(value);
							}
							ChatBotUtil.deleteAndAddSessionParam(chatBotParam, session, valuesArray);
						}
						else {
							JSONObject json = (JSONObject) entityList.get(0);
							
							Object value = null;
							if(chatBotParam.getMlTypeEnum() == ML_Type.DATE) {
								JSONObject cbDate = (JSONObject) json.get("value");
								ChatBotDateContext cbDateContext = FieldUtil.getAsBeanFromJson(cbDate, ChatBotDateContext.class);
								value = ChatBotDateTimeUtil.compute(chatBotParam, cbDateContext);
							}
							if(chatBotParam.getMlTypeEnum() == ML_Type.TIME) {
								JSONObject cbtime = (JSONObject) json.get("value");
								ChatBotTimeContext cbtimeContext = FieldUtil.getAsBeanFromJson(cbtime, ChatBotTimeContext.class);
								value = ChatBotDateTimeUtil.compute(chatBotParam, cbtimeContext);
							}
							else {
								value = json.get("value").toString();
							}
							
							ChatBotUtil.deleteAndAddSessionParam(chatBotParam, session, value);
						}
						recievedCount++;
					}
				}
			}
			
			session.setRequiredParamCount(requriedCount);
			session.setRecievedParamCount(recievedCount);
			
		}
		
		
		return false;
	}

}
