package com.facilio.constants;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
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
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
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
		
		public static String getIssURL() {
			return "https://cognito-idp.us-west-2.amazonaws.com/us-west-2_kzN5KrMZU";
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
	
	public static enum Permission {
		
		ORG_ACCESS_ADMINISTER(1), // full control over the organization
		
		ORG_ACCESS_DELETE(2), // permission to close organization
		
		USER_ACCESS_ADMINISTER(4), // view, create or edit users
		
		USER_ACCESS_DELETE(8), // delete users
		
		GROUP_ACCESS_ADMINISTER(16), // view, create or edit groups
		
		GROUP_ACCESS_DELETE(32), // delete groups
		
		WORKORDER_ACCESS_CREATE_ACCESSIBLE_SPACES(64),
		
		WORKORDER_ACCESS_CREATE_ANY(128),
		
		WORKORDER_ACCESS_UPDATE_OWN(256),
		
		WORKORDER_ACCESS_UPDATE_ACCESSIBLE_SPACES(512),
		
		WORKORDER_ACCESS_UPDATE_ANY(1024),
		
		WORKORDER_ACCESS_READ_OWN(2048),
		
		WORKORDER_ACCESS_READ_ACCESSIBLE_SPACES(4096),
		
		WORKORDER_ACCESS_READ_ANY(8192),
		
		WORKORDER_ACCESS_DELETE_OWN(16384),
		
		WORKORDER_ACCESS_DELETE_ACCESSIBLE_SPACES(32768),
		
		WORKORDER_ACCESS_DELETE_ANY(65536),
		
		WORKORDER_ACCESS_ASSIGN_OWN(131072),
		
		WORKORDER_ACCESS_ASSIGN_ACCESSIBLE_SPACES(262144),
		
		WORKORDER_ACCESS_ASSIGN_ANY(524288),
		
		WORKORDER_ACCESS_CAN_BE_ASSIGNED_ACCESSIBLE_SPACES(1048576),
		
		WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY(2097152),
		
		TASK_ACCESS_CREATE_ANY(4194304),
		
		TASK_ACCESS_UPDATE_OWN(8388608),
		
		TASK_ACCESS_UPDATE_ANY(16777216),
		
		TASK_ACCESS_READ_OWN(33554432),
		
		TASK_ACCESS_READ_ANY(67108864),
		
		TASK_ACCESS_DELETE_OWN(134217728),
		
		TASK_ACCESS_DELETE_ANY(268435456),
		
		TASK_ACCESS_ASSIGN_OWN(536870912),
		
		TASK_ACCESS_ASSIGN_ACCESSIBLE_GRPUPS(1073741824),
		
		TASK_ACCESS_ASSIGN_ANY(2147483648L),
		
		TASK_ACCESS_CAN_BE_ASSIGNED_ANY(4294967296L),
		
		DASHBOARD_ACCESS_ENABLE(8589934592L),
		
		REPORTS_ACCESS_ENABLE(17179869184L),
		
		SPACEMANAGEMENT_ACCESS_ENABLE(34359738368L);
		
		long permission;
		
		Permission(long permission) {
			this.permission = permission;
		}
		
		public long getPermission() {
			return this.permission;
		}
		
		public static long getSumOf(Permission... permissions) {
			long sumOf = 0;
			for (Permission perm : permissions) {
				sumOf += perm.getPermission();
			}
			return sumOf;
		}
	}
	
	public static enum PermissionGroup {
		
		SETUP(
				Permission.ORG_ACCESS_ADMINISTER,
				Permission.ORG_ACCESS_DELETE,
				Permission.USER_ACCESS_ADMINISTER,
				Permission.USER_ACCESS_DELETE,
				Permission.GROUP_ACCESS_ADMINISTER,
				Permission.GROUP_ACCESS_DELETE
			),
		
		WORKORDER_CREATE(
				Permission.WORKORDER_ACCESS_CREATE_ACCESSIBLE_SPACES,
				Permission.WORKORDER_ACCESS_CREATE_ANY
				),
		
		WORKORDER_UPDATE(
				Permission.WORKORDER_ACCESS_UPDATE_ACCESSIBLE_SPACES,
				Permission.WORKORDER_ACCESS_UPDATE_OWN,
				Permission.WORKORDER_ACCESS_UPDATE_ANY
				),
		
		WORKORDER_READ(
				Permission.WORKORDER_ACCESS_READ_ACCESSIBLE_SPACES,
				Permission.WORKORDER_ACCESS_READ_OWN,
				Permission.WORKORDER_ACCESS_READ_ANY
				),
		
		WORKORDER_DELETE(
				Permission.WORKORDER_ACCESS_DELETE_ACCESSIBLE_SPACES,
				Permission.WORKORDER_ACCESS_DELETE_OWN,
				Permission.WORKORDER_ACCESS_DELETE_ANY
				),
		
		WORKORDER_ASSIGN(
				Permission.WORKORDER_ACCESS_ASSIGN_ACCESSIBLE_SPACES,
				Permission.WORKORDER_ACCESS_ASSIGN_OWN,
				Permission.WORKORDER_ACCESS_ASSIGN_ANY
				),
		
		TASK_CREATE(
				Permission.TASK_ACCESS_CREATE_ANY
				),
		
		TASK_UPDATE(
				Permission.TASK_ACCESS_UPDATE_OWN,
				Permission.TASK_ACCESS_UPDATE_ANY
				),
		
		TASK_READ(
				Permission.TASK_ACCESS_READ_OWN,
				Permission.TASK_ACCESS_READ_ANY
				),
		
		TASK_DELETE(
				Permission.TASK_ACCESS_DELETE_OWN,
				Permission.TASK_ACCESS_DELETE_ANY
				),
		
		TASK_ASSIGN(
				Permission.TASK_ACCESS_ASSIGN_ACCESSIBLE_GRPUPS,
				Permission.TASK_ACCESS_ASSIGN_OWN,
				Permission.TASK_ACCESS_ASSIGN_ANY
				);
		
		
		Permission[] permission;
		
		PermissionGroup(Permission... permission) {
			this.permission = permission;
		}
		
		public Permission[] getPermission() {
			return this.permission;
		}
		
		public static long getSumOf(Permission... permissions) {
			long sumOf = 0;
			for (Permission perm : permissions) {
				sumOf += perm.getPermission();
			}
			return sumOf;
		}
	}
	
	public static class Role 
	{
		public static final String ADMINISTRATOR 	= "Administrator";
		public static final String MANAGER 		    = "Manager";
		public static final String DISPATCHER 		= "Dispatcher";
		public static final String TECHNICIAN 		= "Technician";
		
		public static final Map<String, Long> DEFAULT_ROLES = new HashMap<>();
		
		static 
		{
			DEFAULT_ROLES.put(ADMINISTRATOR, 0L); // 0 means full permission
			
			DEFAULT_ROLES.put(MANAGER, Permission.getSumOf(
					Permission.USER_ACCESS_ADMINISTER,
					Permission.GROUP_ACCESS_ADMINISTER,
					Permission.WORKORDER_ACCESS_CREATE_ANY,
					Permission.WORKORDER_ACCESS_UPDATE_ANY, 
					Permission.WORKORDER_ACCESS_READ_ANY,
					Permission.WORKORDER_ACCESS_DELETE_ANY,
					Permission.WORKORDER_ACCESS_ASSIGN_ANY,
					Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY,
					Permission.TASK_ACCESS_CREATE_ANY,
					Permission.TASK_ACCESS_UPDATE_ANY,
					Permission.TASK_ACCESS_READ_ANY,
					Permission.TASK_ACCESS_DELETE_ANY,
					Permission.TASK_ACCESS_ASSIGN_ANY,
					Permission.TASK_ACCESS_CAN_BE_ASSIGNED_ANY,
					Permission.DASHBOARD_ACCESS_ENABLE,
					Permission.REPORTS_ACCESS_ENABLE
					));
			
			DEFAULT_ROLES.put(DISPATCHER, Permission.getSumOf(
					Permission.WORKORDER_ACCESS_CREATE_ANY,
					Permission.WORKORDER_ACCESS_UPDATE_ANY, 
					Permission.WORKORDER_ACCESS_READ_ANY,
					Permission.WORKORDER_ACCESS_DELETE_ANY,
					Permission.WORKORDER_ACCESS_ASSIGN_ANY,
					Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY,
					Permission.TASK_ACCESS_CREATE_ANY,
					Permission.TASK_ACCESS_UPDATE_ANY,
					Permission.TASK_ACCESS_READ_ANY,
					Permission.TASK_ACCESS_DELETE_ANY,
					Permission.TASK_ACCESS_ASSIGN_ANY,
					Permission.TASK_ACCESS_CAN_BE_ASSIGNED_ANY
					));
			
			DEFAULT_ROLES.put(TECHNICIAN, Permission.getSumOf(
					Permission.WORKORDER_ACCESS_UPDATE_OWN, 
					Permission.WORKORDER_ACCESS_READ_OWN,
					Permission.WORKORDER_ACCESS_CAN_BE_ASSIGNED_ANY,
					Permission.TASK_ACCESS_UPDATE_OWN,
					Permission.TASK_ACCESS_READ_OWN,
					Permission.TASK_ACCESS_CAN_BE_ASSIGNED_ANY
					));
		}
	}
	
	public static class ContextNames {
		
		public static final String RECORD = "record";
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
		
		public static final String TICKET_CATEGORY = "ticketcategory";
		public static final String TICKET_CATEGORY_LIST = "ticketcategories";
		
		public static final String TICKET = "ticket";
		public static final String TICKET_LIST = "tickets";
		public static final String ASSIGNED_TO_ID = "assignedTo";
		
		public static final String WORK_ORDER = "workorder";
		public static final String WORK_ORDER_LIST = "workorders";
		
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
		
		public static final String REQUESTER = "requester";
		
		public static final String SUPPORT_EMAIL = "supportEmail";
		public static final String SUPPORT_EMAIL_LIST = "supportEmails";
		public static final String EMAIL_SETTING = "emailSetting";
		
		public static final String RESULT = "result";
		
		public static String getPKColumn(String module) {
			if ("Tickets".equalsIgnoreCase(module)) {
				return "WORK_ORDER_ID";
			}
			return "WORK_ORDER_ID";
		}
		
		public static String getAttachmentTableName(String module) {
			if ("Tickets".equalsIgnoreCase(module)) {
				return "WorkOrder_Attachment";
			}
			return "WorkOrder_Attachment";
		}

		public static final String CV_NAME = "cvName";
		public static final String CUSTOM_VIEW = "customView";
		public static final String FILTERS = "filters";
		
		private static Map<String, Class> classMap = Collections.unmodifiableMap(initClassMap());
		private static Map<String, Class> initClassMap() {
			Map<String, Class> classMap = new HashMap<>();
			classMap.put(TICKET_STATUS, TicketStatusContext.class);
			classMap.put(TICKET_PRIORITY, TicketPriorityContext.class);
			classMap.put(TICKET_CATEGORY, TicketCategoryContext.class);
			classMap.put(TICKET, TicketContext.class);
			classMap.put(TASK, TaskContext.class);
			classMap.put(WORK_ORDER, WorkOrderContext.class);
			//classMap.put(TASK_STATUS, TaskStatusContext.class);
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
	
	public static class Workflow 
	{
		public static final String EVENT_TYPE = "eventType";
		
		public static final int EVENT_ADD_WORKORDER = 1;
		
		public static final int ACTION_EMAIL_NOTIFICATION = 1;
		
		public static final int TEMPLATE_WORKORDER_ASSIGN 				= 1;
		public static final int TEMPLATE_WORKORDER_ACTIVITY_FOLLOWUP 	= 2;
	}
	
	public static class Reports 

	{
		public static final String RANGE_FROM = "fromRange";
		public static final String RANGE_END = "endRange";
		public static final String QUERY_STRING = "query";
		public static final String DEVICE_ID = "deviceId";
		
		public static final int THIS_HOUR = 1;
		public static final int LAST_HOUR = 2;
		
		public static final int TODAY = 3;
		public static final int YESTERDAY = 4;
		
		public static final int THIS_WEEK  = 5;
		public static final int LAST_WEEK	= 6;
		
		public static final int THIS_MONTH  = 7;
		public static final int LAST_MONTH	= 8;
		
		public static final int LAST_7_DAYS  = 9;
		public static final int LAST_30_DAYS	= 10;
		
		public static final int THIS_YEAR  = 11;
		public static final int LAST_YEAR  = 12;
		
		public static final int THIS_MONTH_WITH_WEEK  = 13;
		public static final int LAST_MONTH_WITH_WEEK	=  14;
		
		public static final int THIS_YEAR_WITH_WEEK  = 15;
		public static final int LAST_YEAR_WITH_WEEK	= 16;
		
		public static final int CUSTOM_WITH_DATE  = 17;
		public static final int CUSTOM_WITH_WEEK  = 18 ;
		public static final int CUSTOM_WITH_MONTH = 19;

	}
	
}