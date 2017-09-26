package com.facilio.bmsconsole.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public enum DefaultTemplates implements ActionTemplate {
	WORKORDER_ASSIGN_EMAIL(1),
	WORKORDER_ACTIVITY_FOLLOWUP(2),
	WORKORDER_ASSIGN_SMS(3),
	TASK_COMMENT_EMAIL(4),
	ALARM_CREATION_EMAIL(5)
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
		JSONObject json = null;
		switch(templateVal) {
			case 1:
				json = new JSONObject();
				json.put("sender", "support@${org.orgDomain}.facilio.com");
				json.put("to", "${workorder.ticket.assignedTo.email}");
				json.put("subject", "Workorder Assigned");
				json.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.\n${workorder.url}");
				break;
			case 2:
				json = new JSONObject();
				json.put("sender", "support@${org.orgDomain}.facilio.com");
				json.put("to", "shivaraj@thingscient.com");
				json.put("subject", "Workorder Follow");
				json.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.");
				break;
			case 3:
				json = new JSONObject();
				json.put("to", "+919003625354");
				json.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.\n${workorder.url}");
				break;
			case 4:
				json = new JSONObject();
				json.put("sender", "support@${org.orgDomain}.facilio.com");
				json.put("to", "${ticket.assignedTo.email}");
				json.put("subject", "New Comment added");
				json.put("message", "A new comment has been added in your WorkOrder.");
				break;
			case 5:
				json = new JSONObject();
				json.put("sender", "support@${org.orgDomain}.facilio.com");
				json.put("to", "${follower.email}");
				json.put("subject", "New Alarm created [Alarm ${alarm.id}]");
				json.put("message", "${alarm.ticket.description}. Please follow the link below to view the alarm.\n${alarm.url}");
				break;
				
		}
		return json;
	}
}
