package com.facilio.constants;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class FacilioConstants {
	
	public static final SimpleDateFormat HTML5_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
	
	public static class CognitoUserPool {
		
		public static String getAWSAccountId() {
			return "665371858763";
		}
		
		public static String getUserPoolId() {
			return "us-west-2_kzN5KrMZU";
		}
		
		public static String getIdentityPoolId() {
			return "us-west-2:ba15c3b0-a6d9-4f33-8841-5b813d55170e";
		}
		
		public static String getClientId() {
			return "74d026sk7dde4vdsgpkhjhj17m";
		}
		
		public String toString() {
			JSONObject userPool = new JSONObject();
			userPool.put("UserPoolId", getUserPoolId());
			userPool.put("ClientId", getClientId());
			return userPool.toJSONString();
		}
	}
	
	public static class Role {
		
		public static final int ADMIN = 0;
		
		public static final int MANAGER = 1;
		
		public static final int AGENT = 2;
		
		public static final int REQUESTER = 3;
		
		public static final HashMap<Integer, String> ALL_ROLES = new HashMap<Integer, String>();
		
		static {
			ALL_ROLES.put(ADMIN, "Administrator");
			ALL_ROLES.put(MANAGER, "Manager");
			ALL_ROLES.put(AGENT, "Agent");
			ALL_ROLES.put(REQUESTER, "Requester");
		}
	}
	
	public static class ContextNames {
		
		public static final String RECORD_ID = "recordId";
		
		public static final String TICKET_ID = "ticketId";
		public static final String TICKET = "ticket";
		public static final String TICKET_LIST = "tickets";
		
		public static final String TASK_ID = "taskId";
		public static final String TASK = "task";
		public static final String TASK_LIST = "tasks";
		
		public static final String SCHEDULE_ID = "scheduleId";
		public static final String SCHEDULE_OBJECT = "scheduleObject";
		
		public static final String NOTE = "note";
		public static final String NOTE_LIST = "notes";
		
		public static final String ATTACHMENT = "attachment";
		public static final String ATTACHMENT_LIST = "attachments";
		public static final String ATTACHMENT_FILE_LIST = "attachmentFiles";
		public static final String ATTACHMENT_CONTENT_TYPE = "attachmentContentType";
		public static final String ATTACHMENT_FILE_NAME = "attachmentFileName";
		public static final String ATTACHMENT_ID_LIST = "attachmentIds";
		
		public static final String ACTION_FORM = "actionForm";
		
		public static final String MODULE_NAME = "moduleName";
		public static final String MODULE_ID = "moduleId";
		public static final String MODULE_DISPLAY_NAME = "moduleDisplayName";
		public static final String MODULE_DATA_TABLE_NAME = "moduleDataTable";
		public static final String MODULE_ATTACHMENT_TABLE_NAME = "moduleAttachmentTable";
		
		public static final String MODULE_FIELD = "moduleField";
		public static final String MODULE_FIELD_LIST = "moduleFields";
		public static final String EXISTING_FIELD_LIST = "existingFields";
		
		public static final String CAMPUS_ID = "campusId";
		public static final String CAMPUS = "campus";
		public static final String CAMPUS_LIST = "campuses";
		
		public static final String BUILDING_ID = "buildingId";
		public static final String BUILDING = "building";
		public static final String BUILDING_LIST = "buildings";
		
		public static final String FLOOR_ID = "floorId";
		public static final String FLOOR = "floor";
		public static final String FLOOR_LIST = "floors";
		
		public static final String SPACE_ID = "spaceId";
		public static final String SPACE = "space";
		public static final String SPACE_LIST = "spaces";
		
		public static final String ZONE_ID = "zoneId";
		public static final String ZONE = "zone";
		public static final String ZONE_LIST = "zones";
		
		public static String getPKColumn(String module) {
			if ("Tickets".equalsIgnoreCase(module)) {
				return "TICKETID";
			}
			return null;
		}
		
		public static String getAttachmentTableName(String module) {
			if ("Tickets".equalsIgnoreCase(module)) {
				return "Ticket_Attachment";
			}
			return null;
		}
	}
}