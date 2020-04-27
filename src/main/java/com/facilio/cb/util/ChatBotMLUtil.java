package com.facilio.cb.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentInvokeSample;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.services.FacilioHttpUtils;
import com.facilio.util.FacilioUtil;

public class ChatBotMLUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(ChatBotMLUtil.class.getName());

	
	public static final String ML_INTENT_STRING = "intent";
	
	public static final String ML_INTENT_NAME_STRING = "name";
	
	public static final String ML_INTENT_CONFIDENCE_STRING = "confidence";
	public static final String ML_INTENT_SAMPLE_STRING = "samples";
	
	public static final String ML_TEXT_STRING = "text";
	
	public static final String ML_MODEL_NAME_STRING = "model_name";
	
	public static final String ML_TRANING_DATA_STRING = "training_data";
	
	public static JSONObject getChatBotIntentJSONFromIntents(List<ChatBotIntent> intents) throws Exception {
		
		JSONArray jsonArray = new JSONArray();
		if(intents != null) {
			
			for(ChatBotIntent intent :intents) {
				
				if(intent.getType() != ChatBotIntent.Intent_Type.SYSTEM_SERVER.getIntVal())  {
					
					JSONObject json = new JSONObject();
					
					json.put(ML_INTENT_STRING, intent.getName());
					
					if(intent.getInvokeSamples() != null) {
						JSONArray jsonSamples = new JSONArray();
						
						for(ChatBotIntentInvokeSample sample :intent.getInvokeSamples()) {
							jsonSamples.add(sample.getSample());
						}
						json.put(ML_INTENT_SAMPLE_STRING, jsonSamples);
					}
					
					jsonArray.add(json);
				}
				
			}
			
		}
		
		JSONObject returnJson = new JSONObject();
		
		returnJson.put(ML_TRANING_DATA_STRING, jsonArray);
		
		System.out.println("returnJson --- "+returnJson);
		return returnJson;
	}
	
	
	public static JSONObject pushIntentToML(JSONObject intentJson) throws Exception {

		if (intentJson != null) {

			Map<String, String> headers = new HashMap<String, String>();

			headers.put("Content-Type", "application/json");

			String response = FacilioHttpUtils.doHttpPost("https://newchatbot.facilio.com/api/chatbot/createchatbot", headers,
					null, intentJson.toJSONString());

			System.out.println("respose ---- "+response);
			JSONObject responseJson = FacilioUtil.parseJson(response);

			return responseJson;

		}
		return null;
	}
	
	public static JSONObject getIntentFromML(String text,ChatBotIntent currentIntent,ChatBotModel model) throws Exception {
		
		if (text != null) {
			
			long processStarttime = System.currentTimeMillis();

			Map<String, String> params = new HashMap<String, String>();

			params.put(ML_MODEL_NAME_STRING, model.getMlModel());
			params.put(ML_TEXT_STRING, text);
			
			LOGGER.info("params ---- " + params);

			String response = FacilioHttpUtils.doHttpGet("https://newchatbot.facilio.com/api/chatbot/findchatintent", null,params);

			System.out.println("respose ---- " + response);
			
			LOGGER.info("respose ---- " + response);
			
			JSONObject responseJson = FacilioUtil.parseJson(response);
			
			LOGGER.info("Time taken for ml api in session is "+(System.currentTimeMillis() - processStarttime));

			return responseJson;
			
		}
		return null;
	}
}
