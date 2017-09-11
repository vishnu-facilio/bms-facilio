package com.facilio.bmsconsole.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.fw.OrgInfo;

public enum DefaultTemplates implements ActionTemplate {
	WORKORDER_ASSIGN(1),
	WORKORDER_ACTIVITY_FOLLOWUP(2)
	;
	
	private int val;
	private String templateJson;
	private DefaultTemplates(int val) {
		// TODO Auto-generated constructor stub
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	};
	
	public static DefaultTemplates getDefaultTemplate(int val) {
		return TYPE_MAP.get(val);
	}
	
	private static final Map<Integer, DefaultTemplates> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, DefaultTemplates> initTypeMap() {
		Map<Integer, DefaultTemplates> typeMap = new HashMap<>();
		for(DefaultTemplates type : values()) {
			typeMap.put(type.getVal(), type);
		}
		return typeMap;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject getTemplateJson(int templateVal) {
		JSONObject mailJson = null;
		switch(templateVal) {
			case 1:
				mailJson = new JSONObject();
				mailJson.put("sender", "support@${org.orgDomain}.facilio.com");
				mailJson.put("to", "${workorder.ticket.assignedTo.email}");
				mailJson.put("subject", "Workorder Assigned");
				mailJson.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.\n${workorder.url}");
				break;
			case 2:
				mailJson = new JSONObject();
				mailJson.put("sender", "support@${org.orgDomain}.facilio.com");
				mailJson.put("to", "shivaraj@thingscient.com");
				mailJson.put("subject", "Workorder Follow");
				mailJson.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.");
				break;
		}
		return mailJson;
	}
}
