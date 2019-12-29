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
	
//	public static void main(String[] args) {
//		JSONObject jsonObject = new JSONObject();
//		
//		jsonObject.put("to", "+919677096980");
//		jsonObject.put("message", "hello message");
//		jsonObject.put("isHtmlContent", "true");
//		jsonObject.put("htmlContent", "true");
//		jsonObject.put("htmlContentPublicUrl", "https://demo.twilio.com/owl.png");
//		
//		sendMessage(jsonObject);
//	}
	public static void main(String[] args) {
		List<Long> timestamps = new ArrayList<>();
		timestamps.add(1577523512521L);
		timestamps.add(1577523501531L);
		long test = ((Double)StatUtils.max(timestamps.stream().mapToDouble(Long::doubleValue).toArray())).longValue();
		System.out.println("test ---"+test);
	}
}
