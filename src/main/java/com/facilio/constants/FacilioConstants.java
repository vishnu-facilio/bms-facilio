package com.facilio.constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.CampusContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskStatusContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.ZoneContext;

public class FacilioConstants {
	
	public static final SimpleDateFormat HTML5_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	
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
	
	public static class Role 
	{
		public static final String ADMINISTRATOR 	= "Administrator";
		public static final String MANAGER 		= "Manager";
		public static final String DISPATCHER 		= "Dispatcher";
		public static final String TECHNICIAN 		= "Technician";
		
		public static final List<String> ALL_ROLES 	= new ArrayList<>();
		
		static 
		{
			ALL_ROLES.add(ADMINISTRATOR);
			ALL_ROLES.add(MANAGER);
			ALL_ROLES.add(DISPATCHER);
			ALL_ROLES.add(TECHNICIAN);
		}
		
		// general permissions
		public static final int CAN_ADMINISTER_ORGANIZATION = 0x1;
		public static final int DASHBOARD 					= 0x1;
		public static final int WORKORDER_MODULE 			= 0x2;
		public static final int WORKORDER_VIEW_ALL 			= 0x4;
		public static final int WORKORDER_VIEW_OWN 			= 0x8;
		public static final int WORKORDER_VIEW_UNASSIGNED 	= 0x10;
		
		public static final String PERMISSION_DASHBOARD						= "dashboard";
		public static final String PERMISSION_WORKORDER_MODULE				= "workorder";
		public static final String PERMISSION_WORKORDER_VIEW_ALL 			= "workorderviewall";
		public static final String PERMISSION_WORKORDER_VIEW_OWN 			= "workorderviewown";
		public static final String PERMISSION_WORKORDER_VIEW_UNASSIGNED 	= "workorderviewunassigned";
		
		public static final Map<String, Integer> permissionsMap 	= new HashMap<>();
		
		static 
		{
			permissionsMap.put(PERMISSION_DASHBOARD, DASHBOARD);
			permissionsMap.put(PERMISSION_WORKORDER_MODULE, WORKORDER_MODULE);
			permissionsMap.put(PERMISSION_WORKORDER_VIEW_ALL, WORKORDER_VIEW_ALL);
			permissionsMap.put(PERMISSION_WORKORDER_VIEW_OWN, WORKORDER_VIEW_OWN);
			permissionsMap.put(PERMISSION_WORKORDER_VIEW_UNASSIGNED, WORKORDER_VIEW_UNASSIGNED);
		}
	}
	
	public static class ContextNames {
		
		public static final String RECORD_ID = "recordId";
		
		public static final String USER_ID = "userId";
		public static final String USER = "user";
		
		public static final String GROUP_ID = "groupId";
		public static final String GROUP = "group";
		public static final String GROUP_MEMBER_IDS = "groupMembers";
		
		public static final String ROLE_ID = "roleId";
		public static final String ROLE = "role";
		
		public static final String TICKET_ID = "ticketId";
		public static final String ID = "Id";
		
		public static final String TICKET_STATUS = "ticketstatus";
		public static final String TICKET_STATUS_LIST = "ticketstatuses";
		
		public static final String TICKET_PRIORITY = "ticketpriority";
		public static final String TICKET_PRIORITY_LIST = "ticketpriorities";
		
		public static final String TICKET = "ticket";
		public static final String TICKET_LIST = "tickets";
		public static final String ASSIGNED_TO_ID = "assignedTo";
		
		public static final String TASK = "task";
		public static final String TASK_LIST = "tasks";
		
		public static final String TASK_STATUS = "taskstatus";
		public static final String TASK_STATUS_LIST = "taskstatuses";
		
		public static final String SCHEDULE_ID = "scheduleId";
		public static final String SCHEDULE_OBJECT = "scheduleObject";
		
		public static final String NOTE_ID = "noteId";
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
		public static final String DEFAULT_FIELD = "defaultField";
		
		public static final String CAMPUS = "campus";
		public static final String CAMPUS_LIST = "campuses";
		
		public static final String BUILDING = "building";
		public static final String BUILDING_LIST = "buildings";
		
		public static final String FLOOR = "floor";
		public static final String FLOOR_LIST = "floors";
		
		public static final String SPACE = "space";
		public static final String SPACE_LIST = "spaces";
		
		public static final String SPACE_CATEGORY = "spacecategory";
		
		public static final String ZONE = "zone";
		public static final String ZONE_LIST = "zones";
		
		public static final String SKILL = "skill";
		public static final String SKILL_LIST = "skills";
		
		public static final String BASE_SPACE_LIST = "basespaces";
		public static final String BASE_SPACE = "basespace";
		
		public static final String PICKLIST = "pickList";
		
		public static final String USERS = "users";
		
		public static final String GROUPS = "groups";
		
		public static final String LOCATION = "location";
		
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

		public static final String CV_NAME = "cvName";
		public static final String CUSTOM_VIEW = "customView";
		
		private static Map<String, Class> classMap = Collections.unmodifiableMap(initClassMap());
		private static Map<String, Class> initClassMap() {
			Map<String, Class> classMap = new HashMap<>();
			classMap.put(TICKET_STATUS, TicketStatusContext.class);
			classMap.put(TICKET_PRIORITY, TicketPriorityContext.class);
			classMap.put(TICKET, TicketContext.class);
			classMap.put(TASK, TaskContext.class);
			classMap.put(TASK_STATUS, TaskStatusContext.class);
			classMap.put(CAMPUS, CampusContext.class);
			classMap.put(BUILDING, BuildingContext.class);
			classMap.put(FLOOR, FloorContext.class);
			classMap.put(SPACE, SpaceContext.class);
			classMap.put(ZONE, ZoneContext.class);
			classMap.put(SPACE_CATEGORY, SpaceCategoryContext.class);
			classMap.put(LOCATION, LocationContext.class);
			classMap.put(SKILL, SkillContext.class);
			
			return classMap;
		}
		
		public static Class getClassFromModuleName(String moduleName) {
			return classMap.get(moduleName);
		}
	}
}