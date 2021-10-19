package com.facilio.accounts.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.util.FreeMarkerAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.services.factory.FacilioFactory;

public enum AccountEmailTemplate {
	WELCOME_EMAIL(1),
	INVITE_USER(2),
	EMAIL_VERIFICATION(3),
	RESET_PASSWORD(4),
	ALERT_EMAIL_VERIFICATION(5),
	PORTAL_SIGNUP(6),
	ADDED_TO_APP_EMAIL(7),
	PORTAL_SELF_SIGNUP(8)
	;
	
	private static Logger log = LogManager.getLogger(AccountEmailTemplate.class.getName());
	private int val;
	private JSONObject templateJson;
	private AccountEmailTemplate(int val) {
		this.val = val;
		this.templateJson = getTemplateJson(val);
	}
	
	public int getVal() {
		return val;
	}
	
	public JSONObject getTemplate(boolean isFtl, Map<String, Object> placeHolders) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject parsedJson = new JSONObject();
			if(isFtl) {
				for (Object key : templateJson.keySet()) {
					Object value = templateJson.get(key);
					if (value instanceof JSONArray) {
						JSONArray newArray = new JSONArray();
						for(Object arrayVal: (JSONArray)value) {
							newArray.add(FreeMarkerAPI.processTemplate(arrayVal.toString(), placeHolders));
						}
						parsedJson.put(key, newArray);
					}
					else {
						parsedJson.put(key, FreeMarkerAPI.processTemplate(templateJson.get(key).toString(), placeHolders));
					}
				}
				return parsedJson;
			}
			return (JSONObject) parser.parse(StringSubstitutor.replace(templateJson.toJSONString(), placeHolders));
			
		}
		catch (ParseException e) {
			log.info("Exception occurred ", e);
		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	};
	
	public void send(Map<String, Object> placeHolders, boolean isFtl) throws Exception {
		/*if(AwsUtil.getConfig("accessKeyId") == null) {
			EmailUtil.sendEmail(getTemplate(placeHolders));
		} else {
		}*/
		FacilioFactory.getEmailClient().sendEmail(getTemplate(isFtl, placeHolders));
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
	
	private static String SUPPORTEMAIL= FacilioProperties.getConfig("rebrand.supportemail");//"support@facilio.com"

	private static String IAMEMAIL = StringUtils.isNotEmpty(FacilioProperties.getConfig("rebrand.iamemail")) ?  FacilioProperties.getConfig("rebrand.iamemail") : SUPPORTEMAIL; //noreply@facilioaccounts.com
	
	private static String ALERTEMAIL= FacilioProperties.getConfig("rebrand.alertemail");//alerts@facilio.com
	
	private static String BRAND= FacilioProperties.getConfig("rebrand.brand");//Facilio
	
	
	
	
	@SuppressWarnings("unchecked")
	private static JSONObject getTemplateJson(int templateVal) {
		if (SUPPORTEMAIL == null) {
			SUPPORTEMAIL = FacilioProperties.getConfig("rebrand.supportemail");//"support@facilio.com"
		}
		if (ALERTEMAIL == null) {
			ALERTEMAIL = FacilioProperties.getConfig("rebrand.alertemail");//"alerts@facilio.com"
		}
		if (BRAND == null) {
			BRAND = FacilioProperties.getConfig("rebrand.brand");//Facilio
		}
		
		JSONObject json = new JSONObject();
		Template template = null;
		
		switch(templateVal) {
		case 1:
			json.put("sender", IAMEMAIL);
			json.put("to", "${toUser.email}");
			json.put("subject", "Welcome to "+ BRAND+"!");
			json.put("message", "Hi ${toUser.name}, Thanks for signing up for "+BRAND+".");
			break;
		case 2:
			json.put("sender", IAMEMAIL);
			json.put("to", "${toUser.email}");
			json.put("subject", "[" + BRAND +"] ${inviter.name} has invited you to join the ${org.name} organization");
			try {
				template = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACCOUNTS, templateVal);
				json.put("message", template.getOriginalTemplate().get("message"));
				json.put("mailType", "html");
			}
			catch(Exception e) {
				json.put("message", "Hi ${toUser.name}, ${inviter.name} has invited you to join the ${org.name} organization. Please click the below link to join the organization. ${invitelink}");
				json.put("mailType", "text");
			}
			break;
		case 3:
			json.put("sender", IAMEMAIL);
			json.put("to", "${toUser.email}");
			json.put("subject", "Welcome! And confirm your email");
			try {
				template = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACCOUNTS, templateVal);
				json.put("message", template.getOriginalTemplate().get("message"));
				json.put("mailType", "html");
				
			}
			catch(Exception e) {
				json.put("message", "Hi ${toUser.name}, Please click the below link to verify your email address. ${invitelink}");
				json.put("mailType", "text");
				
			}
			break;
		case 4:
			json.put("sender", IAMEMAIL);
			json.put("to", "${toUser.email}");
			json.put("subject", "Reset your "+BRAND+" password");
			try {
				template = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACCOUNTS, templateVal);
				template.setFtl(true);
				json.put("message",template.getOriginalTemplate().get("message"));
				json.put("mailType", "html");
				
			}
			catch(Exception e) {
				json.put("message", "Hi ${toUser.name}, Please click the below link to reset your password. ${invitelink}");
				json.put("mailType", "text");
			}
			break;
		case 5:
			json.put("sender", IAMEMAIL);
			json.put("to", ALERTEMAIL);
			json.put("subject","${toUser.name} with a mailId  ${toUser.email} has signedUp in ["+BRAND+ "]");
			json.put("message", "Hi ${toUser.name}, Please click the below link to verify your email address. ${invitelink}\n" 
					+ "Name:" + "${toUser.name}\n" + "Email:" + "${toUser.email}" + "Timezone:" + "${toUser.timezone}" );
			break;
		case 6:
			json.put("sender", IAMEMAIL);
			json.put("to", "${toUser.email}");
			json.put("subject","[${org.name}] Welcome and confirm your email" );
			try {
				template = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACCOUNTS, templateVal);
				template.setFtl(true);
				json.put("message",template.getOriginalTemplate().get("message"));
				json.put("mailType", "html");
				
			}
			catch(Exception e) {
				json.put("message", "Hi ${toUser.name}, Please click the below link to verify your email address. ${invitelink}" );
				json.put("mailType", "text");
			}
			break;
		case 7:
			json.put("sender", IAMEMAIL);
			json.put("to", "${toUser.email}");
			json.put("subject","[${org.name}] Welcome!" );
			try {
				template = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACCOUNTS, templateVal);
				template.setFtl(true);
				json.put("message",template.getOriginalTemplate().get("message"));
				json.put("mailType", "html");
				
			}
			catch(Exception e) {
				json.put("message", "Hi ${toUser.name}, Please note that you are now a part of the ${org.name} and have access to the ${appType} of the organization." );
				json.put("mailType", "text");
			}
			break;
		case 8:
			json.put("sender", IAMEMAIL);
			json.put("to", "${toUser.email}");
			json.put("subject","[${org.name}] Welcome!" );
			try {
				template = TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACCOUNTS, templateVal);
				template.setFtl(true);
				json.put("message",template.getOriginalTemplate().get("message"));
				json.put("mailType", "html");
				
			}
			catch(Exception e) {
				json.put("message", "Hi ${toUser.name}, Please note that you have signed up to the ${appType} of the organization ${org.name}. Please click the below link to verify your email address. ${invitelink}" );
				json.put("mailType", "text");
			}
			break;
	}
	return json;
	}
}
