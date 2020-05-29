package com.facilio.bmsconsole.util;

import java.util.HashMap;
import java.util.Map;

import com.facilio.aws.util.FacilioProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.twilio.rest.api.v2010.account.Message;

public class SMSUtil extends TwilioUtil {
	private static final Logger LOGGER = LogManager.getLogger(SMSUtil.class.getName());

	public static String sendSMS(JSONObject obj) {
		
		if(!FacilioProperties.isProduction())
		{
			LOGGER.info("SMS restricted in development/stage  ");
			return "";

		}
		
		String message = (String) obj.get("message");
		String to = (String) obj.get("to");
		int countrycode = getISDCode(to);
		if(countrycode == 91)
		{
			//TODO send SMS using India vendor
		}
		
		Message tmessage = Message.creator(
                new com.twilio.type.PhoneNumber(to),
                new com.twilio.type.PhoneNumber(FROM_NUMBER),
                message)
            .create();
		
		LOGGER.info("SMS sent successfully. ID : "+tmessage.getSid());
		LOGGER.info(to+"=>"+message);
		log(CommonAPI.NotificationType.SMS, to, message, tmessage.getSid());
		return tmessage.getSid();
		
	}
	
	
	public static String sendOtp(String phonenumber) throws Exception
	{
//		SMSUtil otpsystem = new SMSUtil();
//		String otp = String.valueOf(((int)(Math.random()*(10000-1000)))+ 1000);
//		
//		String user = AccountConstants.getAppUserModule().getTableName();
//		
//		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//				.select(AccountConstants.getAppOrgUserFields())
//				.table(AccountConstants.getAppOrgUserModule().getTableName())
//				.innerJoin(user)
//				.on(AccountConstants.getAppOrgUserModule().getTableName()+".USERID = "+user+".USERID")
//				.andCustomWhere(user+".MOBILE = ? AND DELETED_TIME = -1", phonenumber);
//		
//		List<Map<String, Object>> props = selectBuilder.get();
//		Map<String, Object> prop = props.get(0);
//		Long ouid = (Long) prop.get("ouid");
//		
//		LOGGER.info("###############3The Org-User id is "+ouid);
//		
//		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
//				.table(AccountConstants.getAppOrgUserModule().getTableName())
//				.fields(AccountConstants.getAppOrgUserFields())
//				.andCustomWhere("ORG_USERID = ?", ouid);
//		prop.put("otp", otp);
//		prop.put("otpTime", System.currentTimeMillis());
//		updateBuilder.update(prop);
//		
//		
//		otpsystem.setOtp(otp);
//		otpsystem.setPhonenumber(phonenumber);
//		otpsystem.setExpiryTime(System.currentTimeMillis() + 40000);
//		otpdata.put(phonenumber,otpsystem);
//		
//		
//		PhoneNumber tophone = new com.twilio.sdk.type.PhoneNumber(phonenumber);
//		PhoneNumber fromphone = new com.twilio.sdk.type.PhoneNumber("+16106248741");
//		
//		com.twilio.sdk.resource.api.v2010.account.Message tmessage = com.twilio.sdk.resource.api.v2010.account.Message.create(ACCOUNTS_ID,tophone , 
//				fromphone,"Your OTP is " +otpsystem.getOtp()).execute();
//		
//		System.out.println(tmessage.getSid());
		
		return "Otp Sent successfully";
		
	}
	
//	public static String sendUserLink(User user, String link) throws Exception
//	{
//		PhoneNumber tophone = new com.twilio.sdk.type.PhoneNumber(user.getMobile());
//		PhoneNumber fromphone = new com.twilio.sdk.type.PhoneNumber("+16106248741");
//		String message = "Your OTP is " +link;
//		System.out.println(message);
//		
//		com.twilio.sdk.resource.api.v2010.account.Message tmessage = com.twilio.sdk.resource.api.v2010.account.Message.create(ACCOUNTS_ID,tophone , 
//				fromphone,message).execute();
//		
//		System.out.println(tmessage.getSid());
//		
//		return "Otp Sent successfully";
//		
//	}
	
	
	public static String verifyOtp(String phonenumber, String otp) throws Exception
	{
//		if(phonenumber == null || otp == null){
//			return "invalid Phonenumber and OTP Code";
//		}
//		
//		String user = AccountConstants.getAppUserModule().getTableName();
//		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//				.select(AccountConstants.getAppOrgUserFields())
//				.table(AccountConstants.getAppOrgUserModule().getTableName())
//				.innerJoin(user)
//				.on(AccountConstants.getAppOrgUserModule().getTableName()+".USERID = "+user+".USERID")
//				.andCustomWhere(user+".MOBILE = ? AND DELETED_TIME = -1", phonenumber);
//		
//		
//		List<Map<String, Object>> props = selectBuilder.get();
//		Map<String, Object> prop = props.get(0);
//		String otp_data = (String) prop.get("otp");
//		Long expiryTime = (Long) prop.get("otpTime") + 60000L;
//		Long ouid = (Long) prop.get("ouid");
//		
//		if (otp_data != null && otp_data.equals(otp)) {
//		
//			if (System.currentTimeMillis() > expiryTime){
//				
//				return "Otp has Expired";
//			}
//				
//			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
//					.table(AccountConstants.getAppOrgUserModule().getTableName())
//					.fields(AccountConstants.getAppOrgUserFields())
//					.andCustomWhere("ORG_USERID = ?", ouid);
//
//			prop.put("otpTime", 0L);
//			updateBuilder.update(prop);
//		}
//		else
//		{
//			return "Otp Mismatch";
//		}
//	
//		
//		if(otpdata.containsKey(phonenumber)){
//		 SMSUtil newdata = otpdata.get(phonenumber);
//		 if(newdata != null){
//			 if (newdata.getExpiryTime()>=System.currentTimeMillis()){
//				 if (newdata.getOtp().equals(otp))
//				 {
//					 return "OTP is verified successfully";
//				 }
//				 return "OTP is invalid";
//			 }
//			 return "OTP is expired";
//		 }
//		 return "error";
//		}
//		
		return "Otp Successfully Validated";
		
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
	
	public static void main(String args[])
	{
		JSONObject obj = new JSONObject();
		obj.put("to", "+919965220466");
		obj.put("message", "Hello world");
		sendSMS(obj);
		
//		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
//		try {
//		    // phone must begin with '+'
//			String phone = "+971565387215";
//		    com.google.i18n.phonenumbers.Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phone, "");
//		    int countryCode = numberProto.getCountryCode();
//		    System.out.println("Hello" + countryCode);
//		} catch (NumberParseException e) {
//		    System.err.println("NumberParseException was thrown: " + e.toString());
//		}

	}
	
	private static int  getISDCode(String phone)
	{
		/*JSONObject obj = new JSONObject();
		obj.put("to", "919840425388");
		obj.put("message", "Hello world");
		sendSMS(obj);*/
		
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		try {
		    // phone must begin with '+'
			//String phone = "+971565387215";
		    com.google.i18n.phonenumbers.Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phone, "");
		    int countryCode = numberProto.getCountryCode();
		    System.out.println("Hello" + countryCode);
		    return countryCode;
		} catch (NumberParseException e) {
		    System.err.println("NumberParseException was thrown: " + e.toString());
		    return -1;
		}

	}

	
}
