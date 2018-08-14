package com.facilio.accounts.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.aws.util.AwsUtil;

public enum AccountEmailTemplate {
	WELCOME_EMAIL(1),
	INVITE_USER(2),
	EMAIL_VERIFICATION(3),
	RESET_PASSWORD(4),
	ALERT_EMAIL_VERIFICATION(5),
	PORTAL_SIGNUP(6);
	
	private static Logger log = LogManager.getLogger(AccountEmailTemplate.class.getName());
	private int val;
	private String templateJson;
	private AccountEmailTemplate(int val) {
		this.val = val;
		this.templateJson = getTemplateJson(val).toJSONString();
	}
	
	public int getVal() {
		return val;
	}
	
	public JSONObject getTemplate(Map<String, Object> placeHolders) {
		JSONParser parser = new JSONParser();
		try {
			return (JSONObject) parser.parse(StrSubstitutor.replace(templateJson, placeHolders));
		} catch (ParseException e) {
			log.info("Exception occurred ", e);
		}
		return null;
	};
	
	public JSONObject getOriginalTemplate() {
		JSONParser parser = new JSONParser();
		try {
			return (JSONObject) parser.parse(templateJson);
		} catch (ParseException e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}
	
	public void send(Map<String, Object> placeHolders) throws Exception {
		/*if(AwsUtil.getConfig("accessKeyId") == null) {
			EmailUtil.sendEmail(getTemplate(placeHolders));
		} else {
		}*/
		AwsUtil.sendEmail(getTemplate(placeHolders));
	}
	
	public static AccountEmailTemplate getEmailTemplate(int val) {
		return TYPE_MAP.get(val);
	}
	
	private static final Map<Integer, AccountEmailTemplate> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, AccountEmailTemplate> initTypeMap() {
		Map<Integer, AccountEmailTemplate> typeMap = new HashMap<>();
		for(AccountEmailTemplate type : values()) {
			typeMap.put(type.getVal(), type);
		}
		return typeMap;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject getTemplateJson(int templateVal) {
		JSONObject json = new JSONObject();
		switch(templateVal) {
			case 1:
				json.put("sender", "support@facilio.com");
				json.put("to", "${user.email}");
				json.put("subject", "Welcome to Facilio!");
				json.put("message", "Hi ${user.name}, Thanks for signing up for Facilio.");
				break;
			case 2:
				json.put("sender", "support@facilio.com");
				json.put("to", "${user.email}");
				json.put("subject", "[Facilio] ${inviter.name} has invited you to join the ${org.name} organization");
				json.put("message", "Hi ${user.name}, ${inviter.name} has invited you to join the ${org.name} organization. Please click the below link to join the organization. ${invitelink}");
				break;
			case 3:
				json.put("sender", "support@facilio.com");
				json.put("to", "${user.email}");
				json.put("subject", "Welcome! And confirm your email");
				json.put("message", "Hi ${user.name}, Please click the below link to verify your email address. ${invitelink}");
				break;
			case 4:
				json.put("sender", "support@facilio.com");
				json.put("to", "${user.email}");
				json.put("subject", "Reset your Facilio password");
				json.put("message", "Hi ${user.name}, Please click the below link to reset your password. ${invitelink}");
				break;
			case 5:
				json.put("sender", "support@facilio.com");
				json.put("to", "alert@facilio.com");
				json.put("subject","${user.name} with a mailId  ${user.email} has signedUp in [Facilio]" );
				json.put("message", "Hi ${user.name}, Please click the below link to verify your email address. ${invitelink}\n" 
						+ "Name:" + "${user.name}\n" + "Email:" + "${user.email}" + "Timezone:" + "${user.timezone}" );
				break;
			case 6:
				json.put("sender", "support@facilio.com");
				json.put("to", "${user.email}");
				json.put("subject","[${org.name}] Welcome and confirm your email" );
				json.put("message", "Hi ${user.name}, Please click the below link to verify your email address. ${invitelink}" );
				break;				
		}
		return json;
	}
}
