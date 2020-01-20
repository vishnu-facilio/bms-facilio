package com.facilio.cb.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONObject;

import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.util.FacilioUtil;
import com.facilio.workflowv2.Visitor.WorkflowFunctionVisitor;
import com.rabbitmq.http.client.HttpException;

public class ChatBotWitAIUtil {
	
	private static final Logger LOGGER = Logger.getLogger(ChatBotWitAIUtil.class.getName());
	
	
	public static final String MAIN_APP_TOKEN = "H7REGEA6BYHYRG5FZRQAPREILVZHS6F6";
	
	public static final String ENTITY_STRING = "entities"; 
	
	public static final String INTENT_STRING = "intent"; 
	public static final String CONFIDENCE_STRING = "confidence";
	public static final String VALUE_STRING = "value";
	
	public static void main(String[] args) throws Exception {
		
		getIntentFromML("dfsdbvd ?");
	}
	
	
	public static JSONObject getIntentFromML(String text) throws Exception {
		
		if(text != null) {
			
			 HttpClient client = new HttpClient();
			 
			 String encodedparam=URLEncoder.encode(text, "UTF-8" );

			 System.out.println(encodedparam);
			 GetMethod method = new GetMethod("https://api.wit.ai/message?q="+encodedparam);
			 
			 method.setRequestHeader("Authorization", "Bearer "+MAIN_APP_TOKEN);

		    try {
		      
		      int statusCode = client.executeMethod(method);

		      if (statusCode != HttpStatus.SC_OK) {
		        System.err.println("Method failed: " + method.getStatusLine());
		      }

		      byte[] responseBody = method.getResponseBody();

		      String response = new String(responseBody);
		      
		      System.out.println(response);
		      
		      LOGGER.log(Level.SEVERE, "text -- "+text);
		      LOGGER.log(Level.SEVERE, "response -- "+response);
		      JSONObject responseJson = FacilioUtil.parseJson(response);
		      
		      
		      return responseJson;

		    } catch (HttpException e) {
		      System.err.println("Fatal protocol violation: " + e.getMessage());
		      LOGGER.log(Level.SEVERE, "Fatal protocol violation: " + e.getMessage());
		      e.printStackTrace();
		    } catch (IOException e) {
		      System.err.println("Fatal transport error: " + e.getMessage());
		      LOGGER.log(Level.SEVERE, "Fatal transport error: " + e.getMessage());
		      e.printStackTrace();
		    } finally {
		      method.releaseConnection();
		    }  
		}
		return null;
	}

}
