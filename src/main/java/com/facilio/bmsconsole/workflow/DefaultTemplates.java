package com.facilio.bmsconsole.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.fw.OrgInfo;

public enum DefaultTemplates implements ActionTemplate {
	WORKORDER_ASSIGN(1),
	WORKORDER_ACTIVITY_FOLLOWUP(2)
	;
	
	private int val;
	private JSONObject templateJson;
	private DefaultTemplates(int val) {
		// TODO Auto-generated constructor stub
		this.val = val;
		this.templateJson = getTemplateJson(val);
	}
	
	public int getVal() {
		return val;
	}
	
	public JSONObject getTemplate(Map<String, String> placeHolders) {
		return templateJson;
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
				mailJson.put("sender", "support@"+OrgInfo.getCurrentOrgInfo().getOrgDomain()+".facilio.com");
				mailJson.put("to", "${workorder.assignedTo}");
				mailJson.put("subject", "Workorder Assigned");
				mailJson.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.");
				break;
			case 2:
				mailJson = new JSONObject();
				mailJson.put("sender", "support@thingscient.com");
				mailJson.put("to", "shivaraj@thingscient.com");
				mailJson.put("subject", "Workorder Follow");
				mailJson.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.");
				break;
		}
		return mailJson;
	}
}
