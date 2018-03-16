package com.facilio.bmsconsole.templates;

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
	ALARM_UPDATION_PUSH(9),
	PM_EMAIL_PRE_REMINDER(10),
	PM_EMAIL_DUE_REMINDER(11),
	NEW_WORK_REQUEST_EMAIL(12),
	NEW_WORK_REQUEST_SMS(14),
	NEW_WORK_REQUEST_PUSH(13),
	NEW_WORK_REQUEST_WEB(15),
	APPROVE_WOREQ_EMAIL(16),
	APPROVE_WOREQ_SMS(17),
	APPROVE_WO_PUSH(19),
	APPROVE_WO_WEB(18),
	REJECT_WOREQ_EMAIL(20),
	REJECT_WOREQ_SMS(21),
	REJECT_WOREQ_PUSH(23),
	REJECT_WOREQ_WEB(22),
	TECH_SOLVE_WO_EMAIL(24),
	TECH_SOLVE_WO_SMS(25),
	TECH_SOLVE_WO_PUSH(27),
	TECH_SOLVE_WO_WEB(26),
	TECH_CLOSE_WO_EMAIL(28),
	TECH_CLOSE_WO_SMS(29),
	TECH_CLOSE_WO_PUSH(31),
	TECH_CLOSE_WO_WEB(30),
	NEW_WORKORDER_EMAIL(32),
	NEW_WORKORDER_SMS(33),
	NEW_WORKORDER_PUSH(35),
	NEW_WORKORDER_WEB(34),
	WORKORDER_ASSIGN_PUSH(37),
	WORKORDER_ASSIGN_WEB(36),
	WORKORDER_ASSIGN_GROUP_EMAIL(38),
	WORKORDER_ASSIGN_GROUP_SMS(39),
	WORKORDER_ASSIGN_GROUP_PUSH(41),
	WORKORDER_ASSIGN_GROUP_WEB(40),
	TASK_COMMENT_SMS(42),
	TASK_COMMENT_PUSH(43),
	TASK_COMMENT_WEB(44),
	ADD_TASK_EMAIL(45),
	ADD_TASK_SMS(46),
	ADD_TASK_PUSH(48),
	ADD_TASK_WEB(47),
	UPDATE_TASK_EMAIL(49),
	UPDATE_TASK_SMS(50),
	UPDATE_TASK_PUSH(52),
	UPDATE_TASK_WEB(51),
	TASK_RESOLVED_EMAIL(53),
	TASK_RESOLVED_SMS(54),
	TASK_RESOLVED_PUSH(56),
	TASK_RESOLVED_WEBL(55),
	WO_ASSIGN_SMS(57),
	WO_ASSIGN_PUSH(59),
	WO_ASSIGN_WEB(58),
	PM_EMAIL_OVERDUE_REMINDER(60)
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
				json.put("sender", "support@facilio.com");
				json.put("to", "${workorder.assignedTo.email:-}");
				json.put("subject", "New Workorder Assigned");
				json.put("message", "A new work order has been assigned to you.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 2:
				json.put("sender", "support@facilio.com");
				json.put("to", "shivaraj@thingscient.com");
				json.put("subject", "Workorder Follow");
				json.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.");
				break;
			case 3:
				json.put("to", "${workorder.assignedTo.phone:-}");
				json.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.\n${workorder.url}");
				break;
			case 4:
				json.put("sender", "support@facilio.com");
				json.put("to", "${ticket.assignedTo.email:-}");
				json.put("subject", "New Comment added");
				json.put("message", "A new comment has been added in your WorkOrder.");
				break;
			case 5:
				json.put("sender", "support@facilio.com");
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
				json.put("id", "${user.id:-}");
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
				json.put("id", "${user.id:-}");
				break;
			case 10:
				json.put("sender", "support@facilio.com");
				json.put("to", "${workorder.assignedTo.email:-}");
				json.put("subject", "New WorkOrder will be created on ${workorder.createdTimeString}");
				json.put("message", "A new work order will be created on ${workorder.createdTimeString} and will be assigned to you.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nRegards,\nTeam Facilio");
				break;
			case 11:
				json.put("sender", "support@facilio.com");
				json.put("to", "${workorder.assignedTo.email:-}");
				json.put("subject", "Workorder in due");
				json.put("message", "The following work order assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 12: 
				json.put("sender", "support@facilio.com");
				json.put("to", "${workorder.requester.email:-}");
				json.put("subject", "New Workrequest");
				json.put("message", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 13:
				json.put("to", "${workorder.requester.phone:-}");
				json.put("message", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");			
				break;
			case 15:
				data = new JSONObject();
				data.put("URL", "${workorderrequest.mobileUrl}");
				data.put("body", "The following work request assigned to you is still in due.\n\nSubject : ${workorderrequest.subject}\nDescription : \n${workorderrequest.description}\n\nPlease follow ${workorderrequest.url} to view the work order.\n\nRegards,\nTeam Facilio");
				data.put("title", "New Workrequest");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorderrequest.assignedTo.id:-}");
				break;
			case 14:
				json.put("to", "user id");
				json.put("URL", "some url");
				json.put("title", "New Workrequest");
				json.put("icon", "https://fazilio.com/statics/favicon.png");
				json.put("body", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 16:
				json.put("sender", "support@facilio.com");
				json.put("to", "${workorder.assignedTo.email:-}");
				json.put("subject", "Approve Workrequest");
				json.put("message", "The following work order assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 17:
				json.put("to", "${workorder.requester.phone:-}");
				json.put("message", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");			
				break;
			case 19:
				data = new JSONObject();
				data.put("URL", "${workorderrequest.mobileUrl}");
				data.put("body", "The following work request assigned to you is still in due.\n\nSubject : ${workorderrequest.subject}\nDescription : \n${workorderrequest.description}\n\nPlease follow ${workorderrequest.url} to view the work order.\n\nRegards,\nTeam Facilio");
				data.put("title", "Approve Workrequest");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorderrequest.assignedTo.id:-}");
				break;
			case 18:
				break;
			case 20:
				json.put("sender", "support@facilio.com");
				json.put("to", "${workorder.assignedTo.email:-}");
				json.put("subject", "Reject Workrequest");
				json.put("message", "The following work order assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 21:
				json.put("to", "${workorder.requester.phone:-}");
				json.put("message", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");			
				break;
			case 23:
				data = new JSONObject();
				data.put("URL", "${workorder.mobileUrl}");
				data.put("body", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				data.put("title", "Reject Workrequest");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorder.assignedTo.id:-}");
				break;
			case 22:
				break;
			case 24:
				json.put("sender", "support@facilio.com");
				json.put("to", "${workorder.assignedTo.email:-}");
				json.put("subject", "Work order completed");
				json.put("message", "The following work order assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 25:
				json.put("to", "${workorder.requester.phone:-}");
				json.put("message", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");			
				break;
			case 27:
				data = new JSONObject();
				data.put("URL", "${alarm.mobileUrl}");
				data.put("body", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				data.put("title", "Work order completed");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorderrequest.assignedTo.id:-}");
				break;
			case 26:
				break;
			case 28:
				json.put("sender", "support@facilio.com");
				json.put("to", "${workorder.assignedTo.email:-}");
				json.put("subject", "Work order closed");
				json.put("message", "The following work order assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 29:
				json.put("to", "${workorder.requester.phone:-}");
				json.put("message", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");			
				break;
			case 31:
				data = new JSONObject();
				data.put("URL", "${alarm.mobileUrl}");
				data.put("body", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				data.put("title", "Work order closed");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorderrequest.assignedTo.id:-}");
				break;
			case 30:
				break;
			case 32:
				json.put("sender", "support@facilio.com");
				json.put("to", "${workorder.assignedTo.email:-}");
				json.put("subject", "New Workorder");
				json.put("message", "The following work order assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 33:
				json.put("to", "${workorder.requester.phone:-}");
				json.put("message", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");			
				break;
			case 35:
				data = new JSONObject();
				data.put("URL", "${alarm.mobileUrl}");
				data.put("body", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				data.put("title", "New Workorder");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorderrequest.assignedTo.id:-}");
				break;
			case 34:
				break;
			case 37:
				data = new JSONObject();
				data.put("URL", "${workorderrequest.mobileUrl}");
				data.put("body", "The following work request assigned to you is still in due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				data.put("title", "Assign Workorder");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorderrequest.assignedTo.id:-}");
				break;
			case 36:
				break;
			case 38:
				json.put("sender", "support@facilio.com");
				json.put("to", "${workorder.assignedTo.email:-}");
				json.put("subject", "New Workorder Assigned");
				json.put("message", "A new work order has been assigned to you.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 39:
				json.put("to", "${workorder.assignedTo.phone:-}");
				json.put("message", "A new work order has been assigned to you. Please follow the link below to view the work order.\n${workorder.url}");
				break;
			case 41:
				data = new JSONObject();
				data.put("URL", "${workorderrequest.mobileUrl}");
				data.put("body", "The following work request assigned to you is still in due.\n\nSubject : ${workorderrequest.subject}\nDescription : \n${workorderrequest.description}\n\nPlease follow ${workorderrequest.url} to view the work order.\n\nRegards,\nTeam Facilio");
				data.put("title", "Assign Workorder");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorderrequest.assignedTo.id:-}");
				break;
			case 40:
				break;
			case 42:
				json.put("to", "${workorder.assignedTo.phone:-}");
				json.put("message", "A new comment has been added in your WorkOrder.");
				break;
			case 44:
				data = new JSONObject();
				data.put("URL", "${workorder.mobileUrl}");
				data.put("body", "A new comment has been added in your WorkOrder.");
				data.put("title", "New Comment");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorder.assignedTo.id:-}");
				break;
			case 43:
				break;
			case 45:
				json.put("sender", "support@facilio.com");
				json.put("to", "${ticket.assignedTo.email:-}");
				json.put("subject", "New Task added");
				json.put("message", "A new Task has been added in your WorkOrder.");
				break;
			case 46:
				json.put("to", "${workorder.assignedTo.phone:-}");
				json.put("message", "A new Task has been added in your WorkOrder.");
				break;
			case 48:
				data = new JSONObject();
				data.put("URL", "${workorder.mobileUrl}");
				data.put("body", "A new Task has been added in your WorkOrder.");
				data.put("title", "New Task added");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorder.assignedTo.id:-}");
				break;
			case 47:
				break;
			case 49:
				json.put("sender", "support@facilio.com");
				json.put("to", "${ticket.assignedTo.email:-}");
				json.put("subject", "Task Updated");
				json.put("message", "A Task has been Updated in your WorkOrder.");
				break;
			case 50:
				json.put("to", "${workorder.assignedTo.phone:-}");
				json.put("message", "A Task has been Updated in your WorkOrder.");
				break;
			case 52:
				data = new JSONObject();
				data.put("id", "${workorder.assignedTo.id:-}");
				data.put("URL", "${workorder.mobileUrl}");
				data.put("body", "A Task has been Updated in your WorkOrder.");
				data.put("title", "New Task added");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorder.assignedTo.id:-}");
				break;
			case 51:
				break;
			case 53:
				json.put("sender", "support@facilio.com");
				json.put("to", "${ticket.assignedTo.email:-}");
				json.put("subject", "Task closed");
				json.put("message", "A Task has been closed in your WorkOrder.");
				break;
			case 54:
				json.put("to", "${workorder.assignedTo.phone:-}");
				json.put("message", "A Task has been closed in your WorkOrder.");
				break;
			case 56:
				data = new JSONObject();
				data.put("id", "${task.assignedTo.id:-}");
				data.put("URL", "${task.mobileUrl}");
				data.put("body", "A Task has been closed in your WorkOrder.");
				data.put("title", "Task closed");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${task.assignedTo.id:-}");
				break;
			case 55:
				break;
			case 57:
				json.put("to", "${workorder.assignedTo.phone:-}");
				json.put("message", "A new work order has been assigned to you.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
			case 58:
				break;
			case 59:
				data = new JSONObject();
				data.put("URL", "${workorder.mobileUrl}");
				data.put("body", "A work order has been assigned to you.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				data.put("title", "Workorder Assigned");
				data.put("content_available", true);
				data.put("priority", "high");
				data.put("sound", "default");
				json.put("data", data);
				json.put("id", "${workorder.assignedTo.id:-}");
				break;
			case 60:
				json.put("sender", "support@facilio.com");
				json.put("to", "${workorder.assignedTo.email:-}");
				json.put("subject", "Workorder in due");
				json.put("message", "The following work order assigned to you is over due.\n\nSubject : ${workorder.subject}\nDescription : \n${workorder.description}\n\nPlease follow ${workorder.url} to view the work order.\n\nRegards,\nTeam Facilio");
				break;
				
		}
		return json;
	}
}
