package com.facilio.cb.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentInvokeSample;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.util.FacilioUtil;
import com.rabbitmq.http.client.HttpException;

public class ChatBotMLUtil {

	
	public static final String ML_INTENT_STRING = "intent";
	public static final String ML_INTENT_SAMPLE_STRING = "samples";
	
	public static final String ML_MODEL_ID_STRING = "modelID";
	
	public static final String ML_TEXT_STRING = "text";
	
	public static final String ML_MODEL_NAME_STRING = "model_name";
	
	public static JSONArray getChatBotIntentJSONFromIntents(List<ChatBotIntent> intents) throws Exception {
		
		if(intents != null) {
			
			JSONArray jsonArray = new JSONArray();
			
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
			
			return jsonArray;
		}
		return null;
	}
	
	
	public static JSONObject pushIntentToML(JSONArray intentJson) throws Exception {
		
		if(intentJson != null) {
			
			 HttpClient client = new HttpClient();

			 PostMethod method = new PostMethod("http://192.168.2.87:7446/api/chatbot/createchatbot");

			    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
			    
				InputStream result = new ByteArrayInputStream(intentJson.toJSONString().getBytes(StandardCharsets.UTF_8));

			    method.setRequestBody(result);
			    method.setRequestHeader("Content-Type", "application/json");

			    try {
			      
			      int statusCode = client.executeMethod(method);

			      if (statusCode != HttpStatus.SC_OK) {
			        System.err.println("Method failed: " + method.getStatusLine());
			      }

			      byte[] responseBody = method.getResponseBody();

			      String response = new String(responseBody);
			      
			      System.out.println(response);
			      JSONObject responseJson = FacilioUtil.parseJson(response);
			      
			      return responseJson;

			    } catch (HttpException e) {
			      System.err.println("Fatal protocol violation: " + e.getMessage());
			      e.printStackTrace();
			    } catch (IOException e) {
			      System.err.println("Fatal transport error: " + e.getMessage());
			      e.printStackTrace();
			    } finally {
			      method.releaseConnection();
			    }  
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		JSONObject resp = getIntentFromML("create invite request", null,null);
		System.out.println(resp);
	}
	
	
	public static JSONObject getIntentFromML(String text,ChatBotIntent currentIntent,ChatBotModel model) throws Exception {
		
		if(text != null) {
			
			 HttpClient client = new HttpClient();

			 PostMethod method = new PostMethod("http://192.168.2.87:7446/api/chatbot/findchatintent");

//			 method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
		    
		    JSONObject reqJson = new JSONObject();
		    
		    reqJson.put(ML_MODEL_ID_STRING, model.getMlModel());
		    reqJson.put(ML_TEXT_STRING, text);
		    
		    if(currentIntent != null) {
		    	
		    }
		    
			InputStream result = new ByteArrayInputStream(reqJson.toString().getBytes(StandardCharsets.UTF_8));

		    method.setRequestBody(result);
		    method.setRequestHeader("Content-Type", "application/json");

		    try {
		      
		      int statusCode = client.executeMethod(method);

		      if (statusCode != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + method.getStatusLine());
		      }

		      byte[] responseBody = method.getResponseBody();

		      String response = new String(responseBody);
		      
		      System.out.println(response);
		      JSONObject responseJson = FacilioUtil.parseJson(response);
		      
		      return responseJson;

		    } catch (HttpException e) {
		      System.err.println("Fatal protocol violation: " + e.getMessage());
		      e.printStackTrace();
		    } catch (IOException e) {
		      System.err.println("Fatal transport error: " + e.getMessage());
		      e.printStackTrace();
		    } finally {
		      method.releaseConnection();
		    }  
		}
		return null;
	}
	
	
	
	
	
	
	
	
}
