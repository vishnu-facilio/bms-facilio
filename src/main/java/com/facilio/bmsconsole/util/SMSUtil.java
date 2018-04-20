package com.facilio.bmsconsole.util;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.twilio.sdk.Twilio;
import com.twilio.sdk.type.PhoneNumber;

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
	
	
	public static String sendOtp(String phonenumber)
	{
		SMSUtil otpsystem = new SMSUtil();
		otpsystem.setOtp(String.valueOf(((int)(Math.random()*(10000-1000)))+ 1000));
		otpsystem.setPhonenumber(phonenumber);
		otpsystem.setExpiryTime(System.currentTimeMillis() + 40000);
		otpdata.put(phonenumber,otpsystem);
		
		
		PhoneNumber tophone = new com.twilio.sdk.type.PhoneNumber(phonenumber);
		PhoneNumber fromphone = new com.twilio.sdk.type.PhoneNumber("+16106248741");
		
		com.twilio.sdk.resource.api.v2010.account.Message tmessage = com.twilio.sdk.resource.api.v2010.account.Message.create(ACCOUNTS_ID,tophone , 
				fromphone,"Your OTP is " +otpsystem.getOtp()).execute();
		
		System.out.println(tmessage.getSid());
		
		return "Otp Sent successfully";
		
	}
	
	
	public static String verifyOtp(String phonenumber, String otp)
	{
		
		if(otpdata.containsKey(phonenumber)){
		 SMSUtil newdata = otpdata.get(phonenumber);
		 if(newdata != null){
			 if (newdata.getExpiryTime()>=System.currentTimeMillis()){
				 if (newdata.getOtp().equals(otp))
				 {
					 return "OTP is verified successfully";
				 }
				 return "OTP is invalid";
			 }
			 return "OTP is expired";
		 }
		 return "error";
		}
		
		return "phone number not found";
		
	}	
	private static Map <String,SMSUtil> otpdata = new HashMap<String,SMSUtil>();
	
	
	private String otp;
	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public long getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}

	private String phonenumber;
	private long expiryTime;
	
}
