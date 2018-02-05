package com.facilio.constants;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AlarmSeverityContext;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.AssetDepartmentContext;
import com.facilio.bmsconsole.context.AssetTypeContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.TicketTypeContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.context.ZoneContext;

public class FacilioConstants {
	
	public static final SimpleDateFormat HTML5_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	public static final SimpleDateFormat HTML5_DATE_FORMAT_1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	
	public static final DateTimeFormatter READABLE_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
	
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
	
	public static class TicketActivity {
		public static final String TICKET_ACTIVITIES = "ticketActivities";
		public static final String OLD_TICKETS = "oldTickets";
		public static final String MODIFIED_TIME = "modifiedTime";
		public static final String MODIFIED_USER = "modifiedUser";
	}
	
	public static class ContextNames {
		
		public static final String SIGNUP_INFO = "signupinfo";
		public static final String RECORD = "record";
		public static final String RECORD_LIST = "records";
		public static final String RECORD_ID = "recordId";
		public static final String RECORD_ID_LIST = "recordIds";
		public static final String ROWS_UPDATED = "rowsUpdated";
		public static final String ACTIVITY_TYPE = "activityType";
		
		public static final String USER_ID = "userId";
		public static final String USER = "user";
		public static final String USER_MOBILE_SETTING = "userMobileSetting";
		
		public static final String GROUP_ID = "groupId";
		public static final String GROUP = "group";
		public static final String GROUP_MEMBER_IDS = "groupMembers";
		
		public static final String ROLE_ID = "roleId";
		public static final String ROLE = "role";
		
		public static final String BUSINESS_HOUR = "businesshour";
		
		public static final String ID = "Id";
		public static final String PARENT_ID = "parentId";
		
		public static final String TICKET_ID = "ticketId";
		
		public static final String TICKET_STATUS = "ticketstatus";
		public static final String TICKET_STATUS_LIST = "ticketstatuses";
		
		public static final String TICKET_PRIORITY = "ticketpriority";
		public static final String TICKET_PRIORITY_LIST = "ticketpriorities";
		
		public static final String TICKET_CATEGORY = "ticketcategory";
		public static final String TICKET_CATEGORY_LIST = "ticketcategories";
		
		public static final String TICKET_TYPE = "tickettype";
		public static final String TICKET_TYPE_LIST = "tickettypes";
		
		public static final String TICKET = "ticket";
		public static final String TICKET_LIST = "tickets";
		public static final String ASSIGNED_TO_ID = "assignedTo";
		
		public static final String WORK_ORDER = "workorder";
		public static final String WORK_ORDER_LIST = "workorders";
		
		public static final String WORK_ORDER_TEMPLATE = "workordertemplate";
		public static final String WORK_ORDER_TEMPLATE_LIST = "workordertemplates";
		
		public static final String CONTROLLER = "controller";
		public static final String CONTROLLER_LIST = "controllerSetting";
		public static final String CONTROLLER_SETTINGS = "controllerSettings";
		
		public static final String WORK_ORDER_REQUEST = "workorderrequest";
		public static final String WORK_ORDER_REQUEST_LIST = "workorderrequests";
		
		public static final String ALARM_SEVERITY = "alarmseverity";
		public static final String ALARM = "alarm";
		public static final String ALARM_LIST = "alarms";
		public static final String ALARM_ENTITY_ID = "alarmentityid";
		
		public static final String TASK = "task";
		public static final String TASK_LIST = "tasks";
		public static final String TASK_MAP = "taskMap";
		public static final String TASK_SECTIONS = "taskSections";
		public static final String TASK_SECTION = "taskSection";
		public static final String DEFAULT_TASK_SECTION = "default";
		
		public static final String ATTACHMENT = "attachment";
		public static final String ATTACHMENT_LIST = "attachments";
		public static final String ATTACHMENT_FILE_LIST = "attachmentFiles";
		public static final String ATTACHMENT_CONTENT_TYPE = "attachmentContentType";
		public static final String ATTACHMENT_TYPE = "attachmentType";
		public static final String ATTACHMENT_FILE_NAME = "attachmentFileName";
		public static final String ATTACHMENT_ID_LIST = "attachmentIds";
		
		public static final String ACTION_FORM = "actionForm";
		
		public static final String MODULE_NAME = "moduleName";
		public static final String MODULE_DISPLAY_NAME = "moduleDisplayName";
		public static final String MODULE_DATA_TABLE_NAME = "moduleDataTable";
		public static final String MODULE_ATTACHMENT_TABLE_NAME = "moduleAttachmentTable";
		public static final String PARENT_MODULE = "parentModule";
		public static final String SUB_MODULE_TYPE = "subModuleType";
		public static final String READING_NAME = "readingName";
		public static final String MODULE = "module";
		public static final String MODULE_LIST = "modules";
		public static final String CATEGORY_READING_PARENT_MODULE = "categoryReadingParentModule";
		public static final String PARENT_CATEGORY_ID = "parentCategoryId";
		
		public static final String READINGS = "readings";
		public static final String READING = "reading";
		public static final String READING_FIELDS = "readingFields";
		public static final String PHOTOS = "photos";
		public static final String PHOTO_ID = "photoId";
		
		public static final String DASHBOARD = "dashboard";
		public static final String DASHBOARD_ID = "dashboardId";
		public static final String DASHBOARD_PUBLISH_STATUS = "dashboardPublishStatus";
		public static final String WIDGET = "widget";
		public static final String WIDGET_TYPE = "widgetType";
		
		public static final String NOTE = "note";
		public static final String NOTE_LIST = "notes";
		
		public static final String MODULE_FIELD = "moduleField";
		public static final String MODULE_FIELD_NAME = "fieldName";
		public static final String MODULE_FIELD_LIST = "moduleFields";
		public static final String EXISTING_FIELD_LIST = "existingFields";
		public static final String FIELD_NAME_LIST = "fieldList";
		public static final String DEFAULT_FIELD = "defaultField";
		public static final String MODULE_FIELD_IDS = "moduleFieldIds";
		
		public static final String SITE = "site";
		public static final String SITE_LIST = "sites";
		public static final String SITE_ID = "siteId";
		public static final String REPORT_CARDS = "reportCards";
		public static final String REPORTS = "reports";
		
		public static final String BUILDING = "building";
		public static final String BUILDING_LIST = "buildings";
		public static final String BUILDING_ID = "buildingId";
		
		public static final String FLOOR = "floor";
		public static final String FLOOR_LIST = "floors";
		public static final String FLOOR_ID = "floorId";
		
		public static final String SPACE = "space";
		public static final String SPACE_LIST = "spaces";
		public static final String SPACE_ID = "spaceId";
		public static final String SPACE_TYPE = "spaceType";
		
		public static final String SPACE_CATEGORY = "spacecategory";
		
		public static final String ZONE = "zone";
		public static final String ZONE_LIST = "zones";
		public static final String ZONE_ID = "zoneId";
		
		public static final String SKILL = "skill";
		public static final String SKILL_LIST = "skills";
		
		public static final String BASE_SPACE_LIST = "basespaces";
		public static final String BASE_SPACE = "basespace";
		
		public static final String ASSET = "asset";
		public static final String ASSET_ID = "assetId";
		public static final String ASSET_LIST = "assets";
		public static final String ASSET_TYPE = "assettype";
		public static final String ASSET_CATEGORY = "assetcategory";
		public static final String ASSET_DEPARTMENT = "assetdepartment";
		public static final String ENERGY_METER = "energymeter";
		public static final String ENERGY_METER_PURPOSE = "energymeterpurpose";
		
		public static final String CURRENT_OCCUPANCY_READING = "currentoccupancyreading";
		public static final String ASSIGNED_OCCUPANCY_READING = "assignedoccupancyreading";
		public static final String TEMPERATURE_READING = "temperaturereading";
		public static final String HUMIDITY_READING = "humidityreading";
		public static final String CO2_READING = "co2reading";
		public static final String SET_POINT_READING = "setpointreading";
		public static final String ENERGY_DATA_READING = "energydata";
		
		public static final String BASE_SPACE_PHOTOS = "basespacephotos";
		public static final String ASSET_PHOTOS = "assetphotos";
		
		public static final String TICKET_NOTES = "ticketnotes";
		public static final String BASE_SPACE_NOTES = "basespacenotes";
		public static final String ASSET_NOTES = "assetnotes";
		
		public static final String TICKET_ATTACHMENTS = "ticketattachments";
		public static final String BASE_SPACE_ATTACHMENTS = "basespaceattachments";
		public static final String ASSET_ATTACHMENTS = "assetattachments";
		
		public static final String PICKLIST = "pickList";
		
		public static final String USERS = "users";
		public static final String ORGUSERS = "orgusers";
		
		public static final String GROUPS = "groups";
		
		public static final String LOCATION = "location";
		public static final String LOCATION_LIST = "locations";
		
		public static final String REQUESTER = "requester";
		
		public static final String SUPPORT_EMAIL = "supportEmail";
		public static final String SUPPORT_EMAIL_LIST = "supportEmails";
		public static final String EMAIL_SETTING = "emailSetting";
		
		public static final String RESULT = "result";
		
		public static final String COMSUMPTIONDATA_LIST = "comsumptionDataList";
		
		public static final String LEEDID = "leedID";
		public static final String LEED = "LEED";
		
		public static final String METERID = "meterID";
		public static final String METER = "METER";
		public static final String BUILDINGID = "BUILDINGID";
		public static final String METERNAME = "METERNAME";
		public static final String FUELTYPE = "FUELTYPE";
		public static final String UTILITYPROVIDER = "UtilityProvider";
		public static final String DEVICEID = "DEVICEID";
		public static final String METERLIST = "METERLIST";
		public static final String METERTYPE = "METERTYPE";
		
		public static final String PREVENTIVE_MAINTENANCE = "preventivemaintenance";
		public static final String PM_REMINDERS = "pmreminders";
		public static final String PM_REMINDER = "pmreminder";
		public static final String PM_REMINDER_TYPE = "pmremindertype";
		public static final String PM_TRIGGERS = "pmtriggers";
		public static final String PM_JOB = "pmjob";
		public static final String PM_RESOURCE_ID = "pmresourceid";
		public static final String PM_ID = "pmresourceid";
		public static final String PREVENTIVE_MAINTENANCE_STATUS = "preventivemaintenanceStatus";
		public static final String PREVENTIVE_MAINTENANCE_LIST = "preventivemaintenances";
		public static final String PREVENTIVE_MAINTENANCE_TRIGGERS_LIST = "preventivemaintenancetriggerss";
		public static final String INSERT_LEVEL = "insertLevel";
		public static final String PM_RESET_TRIGGERS = "pmResetTriggers";
		public static final String PM_CURRENT_TRIGGER = "pmCurrentTriggerId";
		public static final String PM_TO_WO = "pmtowo";
		
		public static final String PREVENTIVE_MAINTENANCE_STARTTIME = "preventivemaintenanceStarttime";
		public static final String PREVENTIVE_MAINTENANCE_ENDTIME = "preventivemaintenanceEndtime";
		
		public static final String WO_DUE_STARTTIME = "woDueStarttime";
		public static final String WO_DUE_ENDTIME = "woDueEndtime";
		
		public static final String CV_NAME = "cvName";
		public static final String CUSTOM_VIEW = "customView";
		public static final String NEW_CV = "newCV";
		public static final String FILTERS = "filters";
		public static final String FILTER_CONDITIONS = "filterConditions";
		public static final String FILTER_CRITERIA = "filterCriteria";
		public static final String SEARCH = "search";
		public static final String SEARCH_CRITERIA = "searchCriteria";
		public static final String SORTING = "sorting";
		public static final String MAX_LEVEL = "maxLevel";
		public static final String SORTING_QUERY = "sortingQuery";
		public static final String LIMIT_VALUE = "limitValue";
		public static final String WORKFLOW_UPDATE="workflowUpdate";
		public static final String WORKFLOW_RULE="workflowRule";
		public static final String WORKFLOW_RULE_LIST="workflowRuleList";
		public static final String READING_RULE_LIST ="readingRules";
		public static final String WORKFLOW_RULE_TYPE = "workflowRuleType";
		public static final String WORKFLOW_ACTION="workflowAction";
		public static final String WORKFLOW_ACTION_ID="workflowActionId";
		public static final String NODE = "node";
		public static final String PAGINATION = "pagination";
		public static final String CURRENT_EXECUTION_TIME = "currentexecutiontime";
		public static final String NEXT_EXECUTION_TIMES = "nextexecutiontimes";
		
		public static final String VIEWID = "viewId";
		public static final String VIEW_LIST = "views";
		public static final String VIEWCOLUMNS = "viewColumns";
		
		public static final String SPACECATEGORY = "SPACECATEGORY";
		public static final String SPACECATEGORIESLIST = "SPACECATEGORIESLIST";
		
		private static Map<String, Class> classMap = Collections.unmodifiableMap(initClassMap());
		private static Map<String, Class> initClassMap() {
			Map<String, Class> classMap = new HashMap<>();
			classMap.put(TICKET_STATUS, TicketStatusContext.class);
			classMap.put(TICKET_PRIORITY, TicketPriorityContext.class);
			classMap.put(TICKET_CATEGORY, TicketCategoryContext.class);
			classMap.put(TICKET_TYPE, TicketTypeContext.class);
			classMap.put(TICKET, TicketContext.class);
			classMap.put(TASK, TaskContext.class);
			classMap.put(WORK_ORDER, WorkOrderContext.class);
//     		classMap.put(CONTROLLER, ControllerSettingsContext.class);
			classMap.put(WORK_ORDER_REQUEST, WorkOrderRequestContext.class);
			classMap.put(ALARM_SEVERITY, AlarmSeverityContext.class);
			classMap.put(ALARM, AlarmContext.class);
			classMap.put(BASE_SPACE, BaseSpaceContext.class);
			classMap.put(ASSIGNED_OCCUPANCY_READING, ReadingContext.class);
			classMap.put(CURRENT_OCCUPANCY_READING, ReadingContext.class);
			classMap.put(BASE_SPACE_PHOTOS, PhotosContext.class);
			classMap.put(SITE, SiteContext.class);
			classMap.put(BUILDING, BuildingContext.class);
			classMap.put(FLOOR, FloorContext.class);
			classMap.put(SPACE, SpaceContext.class);
			classMap.put(ZONE, ZoneContext.class);
			classMap.put(SPACE_CATEGORY, SpaceCategoryContext.class);
			classMap.put(LOCATION, LocationContext.class);
			classMap.put(SKILL, SkillContext.class);
			classMap.put(ASSET, AssetContext.class);
			classMap.put(ASSET_CATEGORY, AssetCategoryContext.class);
			classMap.put(ASSET_TYPE, AssetTypeContext.class);
			classMap.put(ASSET_DEPARTMENT, AssetDepartmentContext.class);
			classMap.put(ASSET_PHOTOS, PhotosContext.class);
			classMap.put(ENERGY_METER, EnergyMeterContext.class);
			classMap.put(ENERGY_DATA_READING, ReadingContext.class);
			classMap.put(ENERGY_METER_PURPOSE, EnergyMeterPurposeContext.class);
			
			return classMap;
		}
		
		public static Class getClassFromModuleName(String moduleName) {
			return classMap.get(moduleName);
		}
	}
	
	public static class Alarm {
		public static final String CLEAR_SEVERITY = "Clear";
		public static final String INFO_SEVERITY = "Info";
	}
	
	public static class Criteria {
		public static final String LOGGED_IN_USER = "${LOGGED_USER}";
		public static final long LOGGED_IN_USER_ID = -99;
	}
	
	public static class Workflow {
		public static final String TEMPLATE = "template";
		public static final String ACTION_TEMPLATE = "actionTemplate";
		public static final String TEMPLATE_TYPE = "templateType";
		public static final String TEMPLATE_ID = "templateId";

		public static final String NOTIFIED_EMAILS = "notifiedEmails";
		public static final String NOTIFICATION_EMAIL = "notificationEMail";
		
		public static final String NOTIFIED_SMS = "notifiedSMS";
		public static final String NOTIFICATION_SMS = "notificationSMS";
		
		public static final String WORKFLOW = "workflow";
		public static final String WORKFLOW_LIST = "workflows";
	}
	public static class WorkOrder{
		public static final String TABLE_NAME = "Workorders";
	}
	public static class WorkOrderRquest{
		public static final String SOURCE_TYPE = "sourceType";
		public static final String CREATED_TIME = "createdTime";
	}
	public static class Job{
		public static final String NEXT_EXECUTION_TIME = "NEXT_EXECUTION_TIME";
		public static final String TABLE_NAME = "Jobs";
	}
	public static class Ticket{
		public static final String STATUS_ID = "status_id";
		public static final String CATEGORY_ID = "category_id";
		public static final String SPACE_ID = "space_id";
		public static final String ASSIGNED_TO_ID = "assignedTo";
		public static final String ASSET_ID = "asset";
		public static final String STATUS = "status";
		public static final String PRIORITY = "priority";
		public static final String SPACE = "space";
		public static final String ACTUAL_WORK_START = "actualWorkStart";
		public static final String ACTUAL_WORK_END = "actualWorkEnd";
		public static final String DUE_DATE = "dueDate";
		public static final String RESOLUTION_TIME = "resolutionTime";
		public static final String FIRST_ACTION_TIME = "firstActionTime";
		public static final String DUE_STATUS = "dueStatus";
		public static final String ON_TIME = "Ontime";
		public static final String OVERDUE = "Overdue";
		public static final String DUE_COUNT = "dueCount";
		public static final String CREATED_TIME = "createdTime";
		public static final String SOURCE_TYPE = "sourceType";
	}
	public static class TicketStatus{
		public static final String STATUS = "status";
	}
	public static class TicketCategory{
		public static final String NAME = "name";
	}
	public static class User{
		public static final String EMAIL = "email";
	}
	public static class TableNames{
		public static final String WORK_ORDER = "Workorders";
		public static final String TICKET_STATUS = "TicketStatus";
		public static final String TICKET_PRIORITY = "TicketPriority";
		public static final String TICKET_CATEGORY = "TicketCategory";
		public static final String TICKET = "Tickets";
		public static final String WORK_ORDER_REQUEST = "WorkOrderRequests";
		public static final String SPACE = "Space";
	}
	public static class Reports 

	{
		public static final String RANGE_FROM = "fromRange";
		public static final String RANGE_END = "endRange";
		public static final String GROUPBY="groupBy";
		public static final String GROUPBY_COLUMN = "groupByCol";
		public static final String GROUPBY_COLUMNS = "groupByCols";
  		public static final String ORDERBY_COLUMN = "orderByCol";
		public static final String ORDERBY_COLUMNS = "orderByCols";
		public static final String COUNT_COLUMN = "count";
		public static final String ALL_COLUMN = "*";
		public static final String UNIQUE_COLUMN = "distinct";
		public static final String JOIN_TYPE = "joinType";
		public static final String INNER_JOIN = "innerJoin";
		public static final String LEFT_JOIN = "leftJoin";
		public static final String RIGHT_JOIN = "rightJoin";
		public static final String FULL_JOIN = "fullJoin";
		public static final String LIMIT = "limit";
		

		public static final String X_AXIS = "xAxis";
		public static final String Y_AXIS = "yAxis";
		public static final String CATEGORY_COLUMN = "categoryCol";
		public static final String FIELD_ALIAS = "fieldAlias";
		public static final String REPORT_TYPE = "reportType";
		public static final String SUMMARY_REPORT_TYPE = "summaryReport";
		public static final String TABULAR_REPORT_TYPE = "tabularReport";
		public static final String TOP_N_SUMMARY_REPORT_TYPE = "topNSummaryReport";
		public static final String TOP_N_NUMERIC_REPORT_TYPE = "topNNumericReport";
		public static final String TREND_REPORT_TYPE = "trendReport";
		public static final String NUMERIC_REPORT_TYPE = "numericReport";	
		public static final String TOP_N_TABULAR_REPORT_TYPE = "topNTabularReport";	
		public static final String TOP_N_DATA = "topNData";	
		public static final String TOP_N = "topN";
		public static final String BOTTOM_N = "bottomN";
		public static final String SUM_FUNC = "sum";
		public static final String MIN_FUNC = "min";
		public static final String MAX_FUNC = "max";
		public static final String AVG_FUNC = "avg";
		public static final String AGG_FUNC = "aggregate";
		public static final String LABEL = "label";
		public static final String VALUE = "value";
		public static final String JOINS = "joins";
		public static final String JOIN_TABLE = "joinTable";
		public static final String JOIN_ON = "joinOn";
		public static final String REPORT_FIELD = "reportField";
		public static final String FIELD_MODULE = "fieldModule";
		public static final String ORG_CONDITION = "orgCondition";
		public static final String CUSTOM_WHERE_CONDITION = "customWhere";
		public static final String RESULT_SET = "resultSet";
		
		public static final String DAILY = "daily";
		public static final String HOURLY = "hourly";
		public static final String WEEKLY = "weekly";
		public static final String MONTHLY = "monthly";
		
		
		public static final StringBuilder ENERGY_TABLE= new StringBuilder(" ENERGY_DATA ");
		
		
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
		public static final Map<String, Integer> DateFilter = new HashMap<>();
		static {
			 DateFilter.put("THIS_HOUR",THIS_HOUR);
				DateFilter.put("LAST_HOUR",LAST_HOUR);
				
				DateFilter.put("TODAY",TODAY);
				DateFilter.put("YESTERDAY",YESTERDAY);
				
				DateFilter.put("THIS_WEEK",THIS_WEEK);
				DateFilter.put("LAST_WEEK",LAST_WEEK);
				
				DateFilter.put("THIS_MONTH",THIS_MONTH);
				DateFilter.put("LAST_MONTH",LAST_MONTH);
				
				DateFilter.put("LAST_7_DAYS",LAST_7_DAYS);
				DateFilter.put("LAST_30_DAYS",LAST_30_DAYS);
				
				DateFilter.put("THIS_YEAR",THIS_YEAR);
				DateFilter.put("LAST_YEAR",LAST_YEAR);
				
				DateFilter.put("THIS_MONTH_WITH_WEEK ",THIS_MONTH_WITH_WEEK);
				DateFilter.put("LAST_MONTH_WITH_WEEK",LAST_MONTH_WITH_WEEK);
				
				DateFilter.put("THIS_YEAR_WITH_WEEK ",THIS_YEAR_WITH_WEEK);
				DateFilter.put("LAST_YEAR_WITH_WEEK",LAST_YEAR_WITH_WEEK);
				
				DateFilter.put("CUSTOM_WITH_DATE",CUSTOM_WITH_DATE);
				DateFilter.put("CUSTOM_WITH_WEEK",CUSTOM_WITH_WEEK);
				DateFilter.put("CUSTOM_WITH_MONTH",CUSTOM_WITH_MONTH);
		}
		
		
		public static class Energy

		{
			public static final int TOTAL_ENERGY_CONSUMPTION_DELTA=1;
			public static final int TOTAL_ENERGY_CONSUMPTION_DELTA_SUM=2;
			public static final int TOTAL_ENERGY_CONSUMPTION_DELTA_COST=3;
			
			public static final int PHASE_ENERGY_R_DELTA=4;
			public static final int PHASE_ENERGY_R_DELTA_SUM=5;
			public static final int PHASE_ENERGY_R_DELTA_COST=6;
			
			public static final int PHASE_ENERGY_Y_DELTA=7;
			public static final int PHASE_ENERGY_Y_DELTA_SUM=8;
			public static final int PHASE_ENERGY_Y_DELTA_COST=9;
			
			public static final int PHASE_ENERGY_B_DELTA=10;
			public static final int PHASE_ENERGY_B_DELTA_SUM=11;
			public static final int PHASE_ENERGY_B_DELTA_COST=12;
			
			public static final int POWER_FACTOR_R=13;
			public static final int POWER_FACTOR_R_AVERAGE=14;
			
			public static final int POWER_FACTOR_Y=15;
			public static final int POWER_FACTOR_Y_AVERAGE=16;
			
			public static final int POWER_FACTOR_B=17;
			public static final int POWER_FACTOR_B_AVERAGE=18;
			
			public static final int ACTIVE_POWER_R=19;
			public static final int ACTIVE_POWER_R_SUM=20;
			
			public static final int ACTIVE_POWER_Y=21;
			public static final int ACTIVE_POWER_Y_SUM=22;
			
			public static final int ACTIVE_POWER_B=23;
			public static final int ACTIVE_POWER_B_SUM=24;
			
			
			public static final int APPARANT_POWER_R=25;
			public static final int APPARANT_POWER_R_SUM=26;
			
			public static final int APPARANT_POWER_Y=27;
			public static final int APPARANT_POWER_Y_SUM=28;
			
			public static final int APPARANT_POWER_B=29;
			public static final int APPARANT_POWER_B_SUM=30;
			
			public static final int REACTIVE_POWER_R=31;
			public static final int REACTIVE_POWER_R_SUM=32;
			
			public static final int REACTIVE_POWER_Y=33;
			public static final int REACTIVE_POWER_Y_SUM=34;
			
			public static final int REACTIVE_POWER_B=35;
			public static final int REACTIVE_POWER_B_SUM=36;
			
			public static final int PHASE_VOLTAGE_R=37;
			public static final int PHASE_VOLTAGE_R_AVERAGE=38;
			
			public static final int PHASE_VOLTAGE_Y=39;
			public static final int PHASE_VOLTAGE_Y_AVERAGE=40;
			
			public static final int PHASE_VOLTAGE_B=41;
			public static final int PHASE_VOLTAGE_B_AVERAGE=42;
			
			public static final int LINE_VOLTAGE_R=43;
			public static final int LINE_VOLTAGE_R_AVERAGE=44;
			
			public static final int LINE_VOLTAGE_Y=45;
			public static final int LINE_VOLTAGE_Y_AVERAGE=46;
			
			public static final int LINE_VOLTAGE_B=47;
			public static final int LINE_VOLTAGE_B_AVERAGE=48;
			
			public static final int LINE_CURRENT_R=49;
			public static final int LINE_CURRENT_R_AVERAGE=50;
			
			public static final int LINE_CURRENT_Y=51;
			public static final int LINE_CURRENT_Y_AVERAGE=52;
			
			public static final int LINE_CURRENT_B=53;
			public static final int LINE_CURRENT_B_AVERAGE=54;
			
			
			public static final int FREQUENCY_R=55;
			public static final int FREQUENCY_R_AVERAGE=56;
			
			public static final int FREQUENCY_Y=57;
			public static final int FREQUENCY_Y_AVERAGE=58;
			
			public static final int FREQUENCY_B=59;
			public static final int FREQUENCY_B_AVERAGE=60;
			public static final Map<String, Integer> Energy_Data = new HashMap<>();
			
			static 
			{
				//Energy_Data.put("TOTAL ENERGY CONSUMPTION DELTA", TOTAL_ENERGY_CONSUMPTION_DELTA);
				Energy_Data.put("TOTAL_ENERGY_CONSUMPTION_DELTA_SUM",TOTAL_ENERGY_CONSUMPTION_DELTA_SUM);
				Energy_Data.put("TOTAL_ENERGY_CONSUMPTION_DELTA_COST",TOTAL_ENERGY_CONSUMPTION_DELTA_COST);
				
				//Energy_Data.put("PHASE_ENERGY_R_DELTA",PHASE_ENERGY_R_DELTA);
				Energy_Data.put("PHASE_ENERGY_R_DELTA_SUM" , PHASE_ENERGY_R_DELTA_SUM);
			    Energy_Data.put("PHASE_ENERGY_R_DELTA_COST",PHASE_ENERGY_R_DELTA_COST);
			    
				//Energy_Data.put("PHASE_ENERGY_Y_DELTA",PHASE_ENERGY_Y_DELTA);
				Energy_Data.put("PHASE_ENERGY_Y_DELTA_SUM",PHASE_ENERGY_Y_DELTA_SUM);
				Energy_Data.put("PHASE_ENERGY_Y_DELTA_COST",PHASE_ENERGY_Y_DELTA_COST);
				
				//Energy_Data.put("PHASE_ENERGY_B_DELTA",PHASE_ENERGY_B_DELTA);
		        Energy_Data.put("PHASE_ENERGY_B_DELTA_SUM",PHASE_ENERGY_B_DELTA_SUM);
			    Energy_Data.put("PHASE_ENERGY_B_DELTA_COST",PHASE_ENERGY_B_DELTA_COST);	
			    
                //Energy_Data.put("POWER_FACTOR_R",POWER_FACTOR_R);
			    Energy_Data.put("POWER_FACTOR_R_AVERAGE",POWER_FACTOR_R_AVERAGE);
			    
				//Energy_Data.put("POWER_FACTOR_Y",POWER_FACTOR_Y);
				Energy_Data.put("POWER_FACTOR_Y_AVERAGE",POWER_FACTOR_Y_AVERAGE);
				
				//Energy_Data.put("POWER_FACTOR_B",POWER_FACTOR_B);
				Energy_Data.put("POWER_FACTOR_B_AVERAGE",POWER_FACTOR_B_AVERAGE);
				
				//Energy_Data.put("ACTIVE_POWER_R",ACTIVE_POWER_R);
				Energy_Data.put("ACTIVE_POWER_R_SUM",ACTIVE_POWER_R_SUM);
				
				//Energy_Data.put("ACTIVE_POWER_Y",ACTIVE_POWER_Y);
				Energy_Data.put("ACTIVE_POWER_Y_SUM",ACTIVE_POWER_Y_SUM);
				
				//Energy_Data.put("ACTIVE_POWER_B",ACTIVE_POWER_B);
				Energy_Data.put("ACTIVE_POWER_B_SUM",ACTIVE_POWER_B_SUM);
				
				
				//Energy_Data.put("APPARANT_POWER_R",APPARANT_POWER_R);
				Energy_Data.put("APPARANT_POWER_R_SUM",APPARANT_POWER_R_SUM);
				
				//Energy_Data.put("APPARANT_POWER_Y",APPARANT_POWER_Y);
				Energy_Data.put("APPARANT_POWER_Y_SUM",APPARANT_POWER_Y_SUM);
				
				//Energy_Data.put("APPARANT_POWER_B",APPARANT_POWER_B);
				Energy_Data.put("APPARANT_POWER_B_SUM",APPARANT_POWER_B_SUM);
				
				//Energy_Data.put("REACTIVE_POWER_R",REACTIVE_POWER_R);
				Energy_Data.put("REACTIVE_POWER_R_SUM",REACTIVE_POWER_R_SUM);
				
				//Energy_Data.put("REACTIVE_POWER_Y",REACTIVE_POWER_Y);
				Energy_Data.put("REACTIVE_POWER_Y_SUM",REACTIVE_POWER_Y_SUM);
				
				//Energy_Data.put("REACTIVE_POWER_B",REACTIVE_POWER_B);
				Energy_Data.put("REACTIVE_POWER_B_SUM",REACTIVE_POWER_B_SUM);
				
				//Energy_Data.put("PHASE_VOLTAGE_R",PHASE_VOLTAGE_R);
				Energy_Data.put("PHASE_VOLTAGE_R_AVERAGE",PHASE_VOLTAGE_R_AVERAGE);
				
				//Energy_Data.put("PHASE_VOLTAGE_Y",PHASE_VOLTAGE_Y);
				Energy_Data.put("PHASE_VOLTAGE_Y_AVERAGE",PHASE_VOLTAGE_Y_AVERAGE);
				
				//Energy_Data.put("PHASE_VOLTAGE_B",PHASE_VOLTAGE_B);
				Energy_Data.put("PHASE_VOLTAGE_B_AVERAGE",PHASE_VOLTAGE_B_AVERAGE);
				
				//Energy_Data.put("LINE_VOLTAGE_R",LINE_VOLTAGE_R);
				Energy_Data.put("LINE_VOLTAGE_R_AVERAGE",LINE_VOLTAGE_R_AVERAGE);
				
				//Energy_Data.put("LINE_VOLTAGE_Y",LINE_VOLTAGE_Y);
				Energy_Data.put("LINE_VOLTAGE_Y_AVERAGE",LINE_VOLTAGE_Y_AVERAGE);
				
				//Energy_Data.put("LINE_VOLTAGE_B",LINE_VOLTAGE_B);
				Energy_Data.put("LINE_VOLTAGE_B_AVERAGE",LINE_VOLTAGE_B_AVERAGE);
				
				//Energy_Data.put("LINE_CURRENT_R",LINE_CURRENT_R);
				Energy_Data.put("LINE_CURRENT_R_AVERAGE",LINE_CURRENT_R_AVERAGE);
				
				//Energy_Data.put("LINE_CURRENT_Y",LINE_CURRENT_Y);
				Energy_Data.put("LINE_CURRENT_Y_AVERAGE",LINE_CURRENT_Y_AVERAGE);
				
				//Energy_Data.put("LINE_CURRENT_B",LINE_CURRENT_B);
				Energy_Data.put("LINE_CURRENT_B_AVERAGE",LINE_CURRENT_B_AVERAGE);
				
				
				Energy_Data.put("FREQUENCY_R",FREQUENCY_R);
				Energy_Data.put("FREQUENCY_R_AVERAGE",FREQUENCY_R_AVERAGE);
				
				Energy_Data.put("FREQUENCY_Y",FREQUENCY_Y);
				Energy_Data.put("FREQUENCY_Y_AVERAGE",FREQUENCY_Y_AVERAGE);
				
				Energy_Data.put("FREQUENCY_B",FREQUENCY_B);
				Energy_Data.put("FREQUENCY_B_AVERAGE",FREQUENCY_B_AVERAGE);
				
			}
			
		}
	}
	
	public static enum UserType {
	    
		USER(1),
		REQUESTER(2)
		;

	    private int userType;

	    UserType(int userType) {
	        this.userType = userType;
	    }

	    public int getValue() {
	        return userType;
	    }
	    
	    public boolean isUser(int userType) {
	    	return (userType & this.userType) == this.userType;
	    }
	    
	    public int setUser(int userType) {
			return userType | this.userType;
		}
		
		public int unSetUser(int userType) {
			return userType & ~this.userType;
		}
	    
	    public static UserType valueOf(int eventTypeVal) {
	    	return typeMap.get(eventTypeVal);
	    }
	    
	    private static final Map<Integer, UserType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, UserType> initTypeMap() {
			Map<Integer, UserType> typeMap = new HashMap<>();
			
			for(UserType type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
		public Map<Integer, UserType> getAllTypes() {
			return typeMap;
		}
	}
}