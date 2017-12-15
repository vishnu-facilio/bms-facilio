package com.facilio.bmsconsole.util;

import org.json.simple.JSONObject;

import com.twilio.sdk.Twilio;

public class SMSUtil {
	private static final String ACCOUNTS_ID = "AC49fd18185d9f484739aa73b648ba2090"; // Your Account SID from www.twilio.com/user/account
	private static final String AUTH_TOKEN = "3683aa0033af81877501961dc886a52b"; // Your Auth Token from www.twilio.com/user/account
	
	static {
		Twilio.init(ACCOUNTS_ID, AUTH_TOKEN);
	}
	
	public static String sendSMS(JSONObject obj) {
		String message = (String) obj.get("message");
		String to = (String) obj.get("to");
		
		com.twilio.sdk.resource.api.v2010.account.Message tmessage = com.twilio.sdk.resource.api.v2010.account.Message.create(ACCOUNTS_ID,
		    new com.twilio.sdk.type.PhoneNumber(to),  // To number
		    new com.twilio.sdk.type.PhoneNumber("+16106248741"),  // From number
		    message                    // SMS body
		).execute();

		
		//com.twilio.sdk.resource.lookups.v1.PhoneNumber
	//	com.twilio.sdk.resource.api.v2010.account.Message.create(accountSid, to, from, mediaUrl)
		System.out.println(tmessage.getSid());
		System.out.println(to+"=>"+message);
		return tmessage.getSid();
		
		//AwsUtil.sendEmail(obj);
	}
}
