package com.facilio.bmsconsole.util;

import java.net.URI;
import java.util.Arrays;

import org.json.simple.JSONObject;
import com.twilio.rest.api.v2010.account.Message;

public class WhatsappUtil extends TwilioUtil {

	public static final String SANDBOX_NUMBER = "+14155238886";
	
	public static String sendMessage(JSONObject obj) {
		
		String message = (String) obj.get("message");
		String to = (String) obj.get("to");
		

		Message wmessage = Message.creator(
                new com.twilio.type.PhoneNumber("whatsapp:"+to),
                new com.twilio.type.PhoneNumber("whatsapp:"+SANDBOX_NUMBER),
                message)
            .setMediaUrl(
                Arrays.asList(URI.create("https://demo.twilio.com/owl.png")))
            .create();
		
		
        System.out.println(wmessage.getSid());
		return wmessage.getSid();
	}
	
	public static void main(String[] args) {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("to", "+919677096980");
		jsonObject.put("message", "hello message");
		
		sendMessage(jsonObject);
	}
}
