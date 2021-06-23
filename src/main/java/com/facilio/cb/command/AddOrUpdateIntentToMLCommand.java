package com.facilio.cb.command;

import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.command.FacilioCommand;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotMLUtil;

public class AddOrUpdateIntentToMLCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if(context.get(ChatBotConstants.CHAT_BOT_ML_MODEL_NAME) == null) {
			
			List<ChatBotIntent> chatBotIntents = (List<ChatBotIntent>)context.get(ChatBotConstants.CHAT_BOT_INTENT_LIST);
			
			JSONObject intentJSON = ChatBotMLUtil.getChatBotIntentJSONFromIntents(chatBotIntents);
			
			JSONObject respjson = ChatBotMLUtil.pushIntentToML(intentJSON);
			
			String mlModelName = (String) respjson.get(ChatBotMLUtil.ML_MODEL_NAME_STRING);
			
			context.put(ChatBotConstants.CHAT_BOT_ML_MODEL_NAME, mlModelName);
		}

		return false;
	}

}
