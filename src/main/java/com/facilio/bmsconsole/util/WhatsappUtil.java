package com.facilio.bmsconsole.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.json.simple.JSONObject;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;

public class WhatsappUtil extends TwilioUtil {

	public static final String SANDBOX_NUMBER = "+14155238886";
	
	public static String sendMessage(JSONObject obj) {
		
		String message = (String) obj.get("message");
		String to = (String) obj.get("to");
		String htmlContentPublicUrl = (String) obj.get("htmlContentPublicUrl");		
		
		MessageCreator wmessagecreator = Message.creator(
                new com.twilio.type.PhoneNumber("whatsapp:"+to),
                new com.twilio.type.PhoneNumber("whatsapp:"+SANDBOX_NUMBER),
                message);
				
		if(htmlContentPublicUrl != null)
		{
			wmessagecreator.setMediaUrl(
	                Arrays.asList(URI.create(htmlContentPublicUrl)));
	            
		}
		Message wmessage = wmessagecreator.create();  
				
        System.out.println(wmessage.getSid());
		log(CommonAPI.NotificationType.WHATSAPP, to, message, wmessage.getSid());
		return wmessage.getSid();
	}
	
	public static void main(String[] args) {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("to", "+919677096980");
//		jsonObject.put("to", "+918124008127");
		jsonObject.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order http://bit.ly/2RsSAHI.");
		jsonObject.put("isHtmlContent", "true");
		jsonObject.put("htmlContent", "true");
		
		sendMessage(jsonObject);
	}
}
