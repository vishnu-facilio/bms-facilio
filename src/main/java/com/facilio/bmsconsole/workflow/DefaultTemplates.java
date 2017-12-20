package com.facilio.bmsconsole.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public enum DefaultTemplates implements ActionTemplate {
	WORKORDER_ASSIGN_EMAIL(1),
	WORKORDER_ACTIVITY_FOLLOWUP(2),
	WORKORDER_ASSIGN_SMS(3),
	TASK_COMMENT_EMAIL(4),
	ALARM_CREATION_EMAIL(5),
	ALARM_CREATION_SMS(6),
	ALARM_UPDATION_SMS(7),
	ALARM_CREATION_PUSH(8),
	ALARM_UPDATION_PUSH(9)
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
	
	public JSONObject getOriginalTemplate() {
		JSONParser parser = new JSONParser();
		try {
			return (JSONObject) parser.parse(templateJson);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
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
		JSONObject json = new JSONObject();
		switch(templateVal) {
			case 1:
				json.put("sender", "support@${org.orgDomain}.facilio.com");
				json.put("to", "${workorder.assignedTo.email:-}");
				json.put("subject", "New Workorder Assigned");
				json.put("message", "A new work order has been assigned to you.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 2:
				json.put("sender", "support@${org.orgDomain}.facilio.com");
				json.put("to", "shivaraj@thingscient.com");
				json.put("subject", "Workorder Follow");
				json.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.");
				break;
			case 3:
				json.put("to", "${workorder.assignedTo.phone:-}");
				json.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.\n${workorder.url}");
				break;
			case 4:
				json.put("sender", "support@${org.orgDomain}.facilio.com");
				json.put("to", "${ticket.assignedTo.email:-}");
				json.put("subject", "New Comment added");
				json.put("message", "A new comment has been added in your WorkOrder.");
				break;
			case 5:
				json.put("sender", "support@${org.orgDomain}.facilio.com");
				JSONArray emails = new JSONArray();
				emails.add("${org.superAdmin.email:-}");
				json.put("to", emails);
				json.put("subject", "New Alarm raised");
				json.put("message", "${alarm.description}\n\nPlease follow ${alarm.url} to view the alarm and acknowledge it as soon as possible.\n\nRegards,\nTeam Facilio");
				break;
			case 6:
				JSONArray smsList = new JSONArray();
				smsList.add("${user.phone:-}");
				json.put("to", smsList);
				//json.put("message", "[ALARM] [${alarm.typeVal}] ${alarm.subject} @ ${alarm.space.name}");
				json.put("message", "[${alarm.modifiedTimeString}] [NEW Alarm:#ID${alarm.id}] \"${alarm.severity.severity}\" alarm reported for source \"${alarm.source}\". Alarm message: \"${alarm.subject}\". State: ${alarm.state}.");
				break;
			case 7:
				smsList = new JSONArray();
				smsList.add("${user.phone:-}");
				json.put("to", smsList);
				//json.put("message", "[ALARM] [${alarm.typeVal}] ${alarm.subject} @ ${alarm.space.name}");
				json.put("message", "[${alarm.modifiedTimeString}] [UPDATED Alarm:#ID${alarm.id}] \"${alarm.previousSeverity.severity}\" alarm updated to \"${alarm.severity.severity}\" for source \"${alarm.source}\". Alarm message: \"${alarm.subject}\". State: ${alarm.state}.");
				break;
			case 8:
				JSONObject data = new JSONObject();
				data.put("URL", "${alarm.mobileUrl}");
				data.put("body", "[${alarm.modifiedTimeString}] [NEW Alarm:#ID${alarm.id}] \"${alarm.severity.severity}\" alarm reported for source \"${alarm.source}\". Alarm message: \"${alarm.subject}\". State: ${alarm.state}.");
				data.put("title", "New Alarm");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				break;
			case 9:
				data = new JSONObject();
				data.put("URL", "${alarm.mobileUrl}");
				data.put("title", "New Alarm");
				data.put("body", "[${alarm.modifiedTimeString}] [UPDATED Alarm:#ID${alarm.id}] \"${alarm.previousSeverity.severity}\" alarm updated to \"${alarm.severity.severity}\" for source \"${alarm.source}\". Alarm message: \"${alarm.subject}\". State: ${alarm.state}.");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				break;
				
		}
		return json;
	}
}
