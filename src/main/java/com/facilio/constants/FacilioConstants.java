package com.facilio.constants;

import com.facilio.activity.ActivityContext;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.modules.FacilioStatus;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FacilioConstants {
	
	public static final SimpleDateFormat HTML5_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	public static final SimpleDateFormat HTML5_DATE_FORMAT_1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

	public static final String ERROR_MESSAGE = "Error Occured";
	
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

		public static final String CONNECTION = "connectionContext";
        public static final String OFFSET = "offset";
        public static final String TABLE_NAME = "tableName";
        public static final String CRITERIA = "criteria";
        public static final String FIELDS = "fields";
        public static final String TO_UPDATE_MAP = "toUpdateMap";
        public static final String TO_INSERT_MAP = "toInsertMap";

		public static final String FACILIO_RECORD = "facilioRecord";
		public static final String FACILIO_CONSUMER = "facilioConsumer";
		public static final String FACILIO_PRODUCER = "facilioProducer";
		
		public static final String KINESIS_RECORD = "kinesisRecord";
		public static final String KINESIS_CHECK_POINTER = "kinesisCheckPointer";
		
		public static final String SIGNUP_INFO = "signupinfo";
		public static final String WITH_CHANGE_SET = "withChangeSet";
		public static final String CHANGE_SET = "changeSet";
		public static final String CHANGE_SET_MAP = "changeSetMap";
		public static final String RECORD = "record";
		public static final String RECORD_LIST = "records";
		public static final String RECORD_MAP = "recordMap";
		public static final String RECORD_ID = "recordId";
		public static final String RECORD_ID_LIST = "recordIds";
		public static final String IS_FROM_SUMMARY = "isFromSummary";
		public static final String PM_INCLUDE_EXCLUDE_LIST = "pmIcludeExcludeList";
		public static final String IS_BULK_EXECUTE_COMMAND = "isbulkExecuteCommand";
		public static final String FETCH_COUNT = "fetchCount";
		public static final String FETCH_SELECTED_FIELDS = "fetchSelectedFields";
		public static final String RECORD_COUNT = "recordCount";
		public static final String ROWS_UPDATED = "rowsUpdated";
		public static final String EVENT_TYPE = "eventType";
		public static final String EVENT_TYPE_LIST = "eventTypeList";
		public static final String IS_BULK_ACTION = "isBulkAction";
		public static final String ACTIVITY_LIST = "activityList";
		public static final String CURRENT_ACTIVITY = "currentActivity";
		public static final String MODIFIED_TIME = "modifiedTime";

		public static final String TENANT_STATUS = "tenantStatus";
		public static final String USER_ID = "userId";
		public static final String USER = "user";
		public static final String USER_MOBILE_SETTING = "userMobileSetting";
		public static final String ACCESSIBLE_SPACE = "accessibleSpace";
		public static final String USER_SHIFT_READING = "usershiftreading";
		public static final String USER_WORK_HOURS_READINGS = "userworkhoursreading";
		
		public static final String GROUP_ID = "groupId";
		public static final String GROUP = "group";
		public static final String GROUP_MEMBER_IDS = "groupMembers";
		
		public static final String ROLE_ID = "roleId";
		public static final String ROLE = "role";
		public static final String PERMISSIONS= "permissions";
		
		public static final String BUSINESS_HOUR_IDS = "businesshourids";
		public static final String BUSINESS_HOUR = "businesshour";
		public static final String BUSINESS_HOUR_LIST="businesshourlist";
		
		public static final String ID = "Id";
		public static final String LINK_NAME = "linkName";
		public static final String PARENT_ID = "parentId";
		public static final String PARENT_ID_LIST = "parentIds";
		
		public static final String TICKET_ID = "ticketId";
		public static final String DEPENDENT_TICKET_IDS = "dependentTicketIds";
		
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
		public static final String TICKET_MODULE = "ticketmodule";
		public static final String ASSIGNED_TO_ID = "assignedTo";
		
		public static final String WORK_ORDER = "workorder";
		public static final String WORK_ORDER_LIST = "workorders";
		public static final String WORK_ORDER_COUNT = "workorderscount";
		public static final String WORK_ORDER_STATUS_PERCENTAGE = "workOrderStatusCount";
		public static final String WORK_ORDER_STATUS_PERCENTAGE_RESPONSE = "workOrderStatusResponse";
		public static final String WORK_ORDER_AVG_RESOLUTION_TIME = "avgCompletionTimeByCategory";
		public static final String WORK_ORDER_STARTTIME = "starttime";
		public static final String WORK_ORDER_ENDTIME = "endtime";
		public static final String WORK_ORDER_COUNT_BY_SITE = "workOrderCountBySite";
		public static final String WORK_ORDER_TECHNICIAN_COUNT = "workOrderTechCount";
		public static final String WORK_ORDER_SITE_ID = "siteId";
		public static final String TOP_N_TECHNICIAN = "topNTechnician";
		public static final String WORKORDER_INFO_BY_SITE = "workOrderInfoBySite";
			
		public static final String PERMALINK_FOR_URL = "permalinkForUrl";
		public static final String PERMALINK_TOKEN_FOR_URL = "permalinkTokenForUrl";
		public static final String USER_EMAIL = "userEmail";
		
		
		public static final String TEMPLATE_ID = "templateId";
		public static final String TEMPLATE_TYPE = "templateType";
		public static final String TEMPLATE_NAME = "templateName";
		public static final Object TEMPLATE_LIST = "templateList";
		public static final Object TEMPLATE = "template";
		public static final String DEFAULT_TEMPLATE = "default_template";
		public static final String WORK_ORDER_TEMPLATE = "workordertemplate";
		public static final String WORK_ORDER_TEMPLATE_LIST = "workordertemplates";
		
		
		
		public static final String PLACE_HOLDER = "placeHolder";
		
		public static final String CONTROLLER_ID = "controllerId";
		public static final String CONTROLLER = "controller";
		public static final String CONTROLLER_LEVEL = "controllerLevel";
		public static final String CONTROLLER_TIME = "controllerTime";
		public static final String CONTROLLER_ACTIVITY_WATCHER = "controllerActivityWatcher";
		public static final String CONTROLLER_LIST = "controllerList";
		public static final String CONTROLLER_SETTINGS = "controllerSettings";
		public static final String FEDGE_CERT_FILE_ID = "fedgeCertFileId";
		
		public static final String WORK_ORDER_REQUEST = "workorderrequest";
		public static final String WORK_ORDER_REQUEST_LIST = "workorderrequests";
		public static final String WORK_ORDER_REQUEST_COUNT = "workorderrequestcount";
		public static final String APPROVAL ="approval";

		
		public static final String ALARM_SEVERITY = "alarmseverity";
		public static final String ALARM = "alarm";
		public static final String READING_ALARM = "readingalarm";
		public static final String ML_ALARM = "mlalarm";
		public static final String ML_ALARM_OCCURRENCE = "mlalarmoccurrence";
		public static final String ALARM_LIST = "alarms";
		public static final String ALARM_ENTITY_ID = "alarmentityid";
		
		
		public static final String EVENT = "event";
		public static final String IS_NEW_EVENT = "isNewEvent";
		
		public static final String INVENTORY = "inventory";
		public static final String INVENTORY_LIST = "inventories";
		public static final String INVENTORY_VENDOR ="inventoryvendor";
		public static final String INVENTORY_VENDORS ="inventory_vendors";
		public static final String INVENTORY_VENDOR_LIST ="inventoryvendors";
		public static final String INVENTORY_CATEGORY = "inventoryCategory";
		
		public static final String TASK = "task";
		public static final String TASK_LIST = "tasks";
		public static final String TASK_MAP = "taskMap";
		public static final String TASK_SECTIONS = "taskSections";
		public static final String TASK_SECTION_TEMPLATES = "taskSectionTemplates";
		public static final String TASK_SECTION = "taskSection";
		public static final String DEFAULT_TASK_SECTION = "default";
		
		public static final String PRE_REQUEST_MAP="preRequestMap";
		public static final String PRE_REQUEST_LIST = "preRequests";
		public static final String PRE_REQUEST_SECTIONS = "preRequestSections";

		public static final String ATTACHMENT = "attachment";
		public static final String ATTACHMENT_LIST = "attachments";
		public static final String EXISTING_ATTACHMENT_LIST = "existingAttachments";
		public static final String ATTACHMENT_CONTEXT_LIST = "attachmentsContextList";
		public static final String ATTACHMENT_FILE_LIST = "attachmentFiles";
		public static final String ATTACHMENT_CONTENT_TYPE = "attachmentContentType";
		public static final String ATTACHMENT_TYPE = "attachmentType";
		public static final String ATTACHMENT_FILE_NAME = "attachmentFileName";
		public static final String ATTACHMENT_ID_LIST = "attachmentIds";
		public static final String ATTACHMENT_MODULE_NAME = "attachmentModuleName";

		public static final String FILE = "file";
		public static final String FILE_NAME = "fileName";
		public static final String FILE_CONTENT_TYPE = "fileContentType";
		public static final String FILE_CONTEXT_LIST = "fileContextList";


		public static final String LOGO = "logo";

		public static final String ACTION_FORM = "actionForm";
		
		public static final String MODULE_NAME = "moduleName";
		public static final String MODULE_DISPLAY_NAME = "moduleDisplayName";
		public static final String MODULE_DATA_TABLE_NAME = "moduleDataTable";
		public static final String MODULE_DATA = "moduleData";
		public static final String MODULE_DATA_LIST = "moduleDatas";
		public static final String MODULE_ATTACHMENT_TABLE_NAME = "moduleAttachmentTable";
		public static final String MODULE_TYPE = "moduleType";
		public static final String MODULE_DATA_INTERVAL = "moduleDataInterval";
		public static final String PARENT_MODULE = "parentModule";
		public static final String READING_NAME = "readingName";
		public static final String TOTAL_CURRENT_OCCUPANCY = "currentOccupancy";
		
		public static final String OVER_RIDE_READING_SPLIT = "overRideReadingSplit";
		public static final String MODULE = "module";
		public static final String IS_NEW_MODULES = "isNewModule";
		public static final String MODULE_LIST = "modules";
		public static final String MODULE_MAP = "modulemap";	
		public static final String SPACES = "spaces";
		public static final String CATEGORY_READING_PARENT_MODULE = "categoryReadingParentModule";
		public static final String PARENT_CATEGORY_ID = "parentCategoryId";
		public static final String PARENT_CATEGORY_IDS = "parentCategoryIds";
		public static final String RESOURCE_TYPE = "resourceType";
		public static final String IS_FILTER = "isFilter";
		public static final String CATEGORY_ID = "categoryId";
		public static final String META = "meta";
		public static final String SET_LOCAL_MODULE_ID = "setLocalModuleId";
		
		public static final String READINGS_MAP = "readingsMap";
		public static final String READINGS = "readings";
		public static final String READING = "reading";
		public static final String READINGS_SOURCE = "readingsSource";
		public static final String READING_FIELDS = "readingFields";
		public static final String READING_FIELD = "readingField";
		public static final String PHOTOS = "photos";
		public static final String PHOTO = "photo";
		public static final String PHOTO_ID = "photoId";
		public static final String PHOTO_TEXTS = "photoTexts";
		public static final String PREVIOUS_VALUE = "previousValue";
		public static final String ONLY_READING = "onylyReading";
		public static final String ADJUST_READING_TTIME = "adjustReadingTtime";
		
		public static final String DASHBOARD = "dashboard";
		public static final String DASHBOARDS = "dashboards";
		public static final String DASHBOARD_ID = "dashboardId";
		public static final String DASHBOARD_ERROR_MESSAGE = "dashboardErrorMessage";
		public static final String DASHBOARD_FOLDERS = "dashboardFolders";
		public static final String DASHBOARD_FOLDER = "dashboardFolder";
		public static final String DASHBOARD_PUBLISH_STATUS = "dashboardPublishStatus";
		public static final String WIDGET = "widget";
		public static final String WIDGET_STATIC_CONTEXT = "widgetStaticContext";
		public static final String REPORT_SPACE_FILTER_CONTEXT = "reportSpaceFilterContext";
		public static final String WIDGET_TYPE = "widgetType";
		
		
		public static final String WIDGET_ID = "widgetId";
		public static final String WIDGET_STATIC_KEY = "widgetStaticKey";
		public static final String WIDGET_BASESPACE_ID = "widgetbasespaceId";
		public static final String WIDGET_WORKFLOW = "widgetWorkflow";
		public static final String WIDGET_PARAMJSON = "widgetparamsJson";
		public static final String WIDGET_REPORT_SPACE_FILTER_CONTEXT = "widgetReportSpaceFilterContext";
		
		
		public static final String REPORT = "report";
		public static final String REPORT_SAFE_LIMIT = "reportSafeLimit";
		public static final String REPORT_ALARMS = "reportAlarms";
		public static final String REPORT_ALARM_CONTEXT = "reportAlarmsContext";
		public static final String REPORT_LIST = "reportList";
		public static final String REPORT_COLUMN_LIST = "reportList";
		public static final String REPORT_USER_FILTER_VALUE = "userFilterValue";
		public static final String REPORT_DUMMY_FIELD_TEXT = "dummyField";
		public static final String REPORT_LABEL_FIELD_TEXT = "label";
		public static final String REPORT_VALUE_FIELD_TEXT = "value";
		public static final String REPORT_DATE_FILTER = "dateFilter";
		public static final String REPORT_DATE_FIELD = "reportDateField";
		public static final String REPORT_SELECT_FIELDS = "selectFields";
		public static final String REPORT_GROUP_BY = "groupBy";
		public static final String REPORT_LIMIT = "limit";
		public static final String REPORT_ORDER_BY = "orderBy";
		public static final String REPORT_CUSTOM_WHERE_LIST = "customWhereList";
		public static final String REPORT_CRITERIA_LIST = "reportCriteriaList";
		public static final String REPORT_FILTER_MODE = "reportFilterMode";
		public static final String REPORT_X_AGGR = "xAggr";
		public static final String REPORT_Y_AGGR = "yAggr";
		public static final String REPORT_DATA = "reportData";
		public static final String REPORT_HANDLE_BOOLEAN = "reportHandleBoolean";
		public static final String REPORT_FROM_ALARM = "reportFromAlarm";
		public static final String ALARM_RESOURCE = "alarmResource";
		public static final String FETCH_EVENT_BAR = "fetchEventBar";
		public static final String CALCULATE_REPORT_AGGR_DATA = "calculateReportData";
		public static final String REPORT_CARD_DATA = "reportCardData";
		public static final String REPORT_VARIANCE_DATA = "reportVarianceData";
		public static final String REPORT_X_VALUES = "reportXValues";
		public static final String REPORT_X_VALUE_LIST = "reportXValueList";
		public static final String REPORT_X_FIELD = "reportXField";
		public static final String REPORT_Y_FIELDS = "reportYFields";
		public static final String REPORT_FIELDS = "reportFields";
		public static final String REPORT_MODE = "reportMode";
		public static final String REPORT_SORT_ALIAS = "reportSortAlias";
		public static final String REPORT_DEFAULT_X_ALIAS = "X";
		public static final String REPORT_RESOURCE_ALIASES = "reportResourceAliases";
		public static final String REPORT_SHOW_ALARMS = "showAlarms";
		public static final String REPORT_SHOW_SAFE_LIMIT = "showSafeLimit";
		public static final String REPORT_STARTTIME = "starttime";
		public static final String REPORT_ENDTIME = "endtime";
		
		public static final String TOTAL_CONSUMPTION = "totalConsumption";
		public static final String UNIT = "unit";
		
		
		
		public static final String AGGR_KEY = "aggr";
		public static final String DATA_KEY = "data";
		public static final String LABEL_MAP = "labelMap";

		public static final String REPORT_CALLING_FROM = "reportCallingFrom";
		
		public static final String NOTE = "note";
		public static final String NOTE_LIST = "notes";
		public static final String NOTES_LIST = "notesList";
		public static final String COMMENT = "comment";
		
		public static final String MODULE_FIELD = "moduleField";
		public static final String MODULE_FIELD_NAME = "fieldName";
		public static final String MODULE_FIELD_LIST = "moduleFields";
		public static final String MODULE_FIELD_MAP = "moduleFieldMap";
		public static final String EXISTING_FIELD_LIST = "existingFields";
		public static final String FIELD_NAME_LIST = "fieldList";
		public static final String DEFAULT_FIELD = "defaultField";
		public static final String MODULE_FIELD_IDS = "moduleFieldIds";
		public static final String FIELD_ID = "fieldId";
		public static final String LOOKUP_FIELD_META_LIST = "lookupFieldMetaList";
		public static final String FETCH_FIELD_DISPLAY_NAMES = "fetchFieldDisplayNames";
		public static final String ALLOW_SAME_FIELD_DISPLAY_NAME = "changeFieldDisplayName";

		public static final String SITE = "site";
		public static final String SITE_LIST = "sites";
		public static final String SITE_ID = "siteId";
		public static final String REPORT_CARDS = "reportCards";
		public static final String REPORTS = "reports";
		public static final String REPORT_CONTEXT = "reportContext";
		public static final String REPORT_ID = "reportId";
		public static final String REPORT_INFO = "reportInfo";
		
		public static final String BUILDING = "building";
		public static final String BUILDING_LIST = "buildings";
		public static final String BUILDING_ID = "buildingId";
		public static final String BUILDING_IDS = "buildingIds";
		
		public static final String FLOOR = "floor";
		public static final String FLOOR_LIST = "floors";
		public static final String FLOOR_ID = "floorId";
		public static final String SPACE_MODULE_NAME ="spacemodulename";
		public static final String SPACE_TABLE_NAME = "spacetablename";
		public static final String SPACE = "space";
		public static final String SPACE_LIST = "spaces";
		public static final String SPACE_ID = "spaceId";
		public static final String SPACE_TYPE = "spaceType";
		public static final String IS_ZONE = "isZone";
		
		public static final String SPACE_CATEGORY = "spacecategory";
		
		public static final String ZONE = "zone";
		public static final String ZONE_LIST = "zones";
		public static final String ZONE_ID = "zoneId";
		public static final String IS_TENANT_ZONE = "tenantzone";

		public static final String SKILL = "skill";
		public static final String SKILL_LIST = "skills";
		
		public static final String RESOURCE = "resource";
		public static final String RESOURCE_LIST = "resourceList";
		public static final String FETCH_RESOURCE_DETAIL = "fetchresourcedetail";
		
		public static final String BASE_SPACE_LIST = "basespaces";
		public static final String BASE_SPACE = "basespace";
		
		public static final String ASSET = "asset";
		public static final String ASSET_ID = "assetId";
		public static final String ASSET_LIST = "assets";
		public static final String ASSET_TYPE = "assettype";
		public static final String ASSET_CATEGORY = "assetcategory";
		public static final String ASSET_DEPARTMENT = "assetdepartment";
		public static final String ENERGY_METER = "energymeter";
		public static final String WATER_METER = "watermeter";
		public static final String CHILLER = "chiller";
		public static final String CHILLER_PRIMARY_PUMP = "chillerprimarypump";
		public static final String CHILLER_SECONDARY_PUMP = "chillersecondarypump";
		public static final String CHILLER_CONDENSER_PUMP = "chillercondenserpump";
		public static final String AHU = "ahu";
		public static final String COOLING_TOWER = "coolingtower";
		public static final String FCU = "fcu";
		public static final String HEAT_PUMP = "heatpump";
		public static final String UTILITY_METER = "utilitymeter";
		public static final String ENERGY_METER_PURPOSE = "energymeterpurpose";
		public static final String RO_MODULE_SPI_CINEMAS = "rowaterenpinew";
		
		public static final String ASSET_BREAKDOWN = "assetBreakdown";
		public static final String ASSET_DOWNTIME_STATUS = "assetDowntimeStatus";
		public static final String ASSET_DOWNTIME_ID = "assetDowntimeId";

		public static final String CURRENT_OCCUPANCY_READING = "currentoccupancyreading";
		public static final String ASSIGNED_OCCUPANCY_READING = "assignedoccupancyreading";
		public static final String TEMPERATURE_READING = "temperaturereading";
		public static final String HUMIDITY_READING = "humidityreading";
		public static final String CO2_READING = "co2reading";
		public static final String SET_POINT_READING = "setpointreading";
		public static final String ENERGY_DATA_READING = "energydata";
		public static final String UTILITY_BILL_READING = "utilitybillreading";
		public static final String WATER_READING = "waterreading";
		
		public static final String BASE_SPACE_PHOTOS = "basespacephotos";
		public static final String ASSET_PHOTOS = "assetphotos";
		public static final String STORE_ROOM_PHOTOS = "storeroomphotos";
		public static final String ASSET_ACTIVITY = "assetactivity";

		public static final String TICKET_NOTES = "ticketnotes";
		public static final String BASE_SPACE_NOTES = "basespacenotes";
		public static final String ASSET_NOTES = "assetnotes";
		public static final String ITEM_TYPES_NOTES = "itemTypesNotes";
		public static final String ITEM_NOTES = "itemNotes";
		public static final String TOOL_NOTES = "toolNotes";
		public static final String TOOL_TYPES_NOTES = "toolTypesNotes";


		public static final String STORE_ROOM_NOTES = "storeRoomNotes";

		public static final String TICKET_ATTACHMENTS = "ticketattachments";
		public static final String BASE_SPACE_ATTACHMENTS = "basespaceattachments";
		public static final String ASSET_ATTACHMENTS = "assetattachments";
		public static final String TASK_ATTACHMENTS = "taskattachments";
		public static final String INVENTORY_ATTACHMENTS = "inventoryattachments";
		public static final String ITEM_TYPES_ATTACHMENTS = "itemTypesAttachments";
		public static final String TOOL_TYPES_ATTACHMENTS = "toolTypesattachments";
		public static final String STORE_ROOM_ATTACHMENTS = "storeRoomAttachments";


		public static final String PICKLIST = "pickList";
		
		public static final String USERS = "users";
		public static final String ORGUSERS = "orgusers";
		public static final String ORG_USER_ID = "orgUserId";
		
		public static final String GROUPS = "groups";
		
		public static final String LOCATION = "location";
		public static final String LOCATION_LIST = "locations";
		
		public static final String REQUESTER = "requester";
		
		public static final String IS_PUBLIC_REQUEST = "is_public_request";
		public static final String IS_PLAN_REQUEST = "is_plan_request";
		public static final String SUPPORT_EMAIL = "supportEmail";
		public static final String SUPPORT_EMAIL_LIST = "supportEmails";
		public static final String EMAIL_SETTING = "emailSetting";
		
		public static final String CONFIG = "config";
		public static final String RESULT = "result";
		public static final String MESSAGE = "message";
		
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
		
		public static final String STOP_PM_EXECUTION = "stopPMExecution";
		public static final String PREVENTIVE_MAINTENANCE = "preventivemaintenance";
		public static final String PM_REMINDERS = "pmreminders";
		public static final String PM_REMINDER = "pmreminder";
		public static final String ONLY_POST_REMINDER_TYPE = "onlyPostReminder";
		public static final String PM_REMINDER_TYPE = "pmReminderType";
		public static final String PM_TRIGGERS = "pmtriggers";
		public static final String PM_JOB = "pmjob";
		public static final String PM_RESOURCE_ID = "pmresourceid";
		public static final String MULTI_PM_RESOURCE_IDS = "multipmresourceIds";
		public static final String MULTI_PM_RESOURCES = "multipmresources";
		public static final String PM_ID = "pmid";
		public static final String PREVENTIVE_MAINTENANCE_STATUS = "preventivemaintenanceStatus";
		public static final String PREVENTIVE_MAINTENANCE_LIST = "preventivemaintenances";
		public static final String PREVENTIVE_MAINTENANCE_COUNT = "preventivemaintenancecount";
		public static final String PREVENTIVE_MAINTENANCE_JOBS_LIST = "preventivemaintenancejobs";
		public static final String PREVENTIVE_MAINTENANCE_TRIGGER_VS_PMJOB_MAP = "preventivemaintenanceTriggerVsPmJobsMap";
		public static final String PREVENTIVE_MAINTENANCE_TRIGGERS_LIST = "preventivemaintenancetriggerss";
		public static final String PREVENTIVE_MAINTENANCE_RESOURCES = "preventivemaintenanceresources";
		public static final String INSERT_LEVEL = "insertLevel";
		public static final String PM_RESET_TRIGGERS = "pmResetTriggers";
		public static final String PM_CURRENT_TRIGGER = "pmCurrentTrigger";
		public static final String PM_CURRENT_JOB = "pmCurrentJob";
		public static final String PM_UNCLOSED_WO_COMMENT = "pmUnclosedWOComment";
		public static final String PM_TO_WO = "pmtowo";
		public static final String PM_TO_ASSET_TO_WO = "pmtoassettowo";
		public static final String IS_PM_EXECUTION = "isPMExecution";
		public static final String IS_UPDATE_PM = "isUpdatePM";
		public static final String SCHEDULED_PM_JOBS = "schdeduledPMJobs";
		public static final String SCHEDULED_PM_JOBS_MAP = "schdeduledPMJobsMap";
		public static final String PM_TASK_SECTIONS = "pmTaskSections";
		public static final String PM_PRE_REQUEST_SECTIONS = "pmPreRequestSections";

		public static final String PREVENTIVE_MAINTENANCE_STARTTIME = "preventivemaintenanceStarttime";
		public static final String PREVENTIVE_MAINTENANCE_ENDTIME = "preventivemaintenanceEndtime";
		
		public static final String JOB_PLAN = "jobPlan";
		public static final String JOB_PLAN_LIST = "jobPlans";
		
		public static final String WO_DUE_STARTTIME = "woDueStarttime";
		public static final String WO_DUE_ENDTIME = "woDueEndtime";
		public static final String WO_LIST_COUNT = "woListCount";
		public static final String WO_IDS = "woIds";
		public static final String NOTE_IDS = "noteIds";
		public static final String WO_VIEW_COUNT = "woListAndCount";
		public static final String IS_APPROVAL = "isApproval";
		public static final String WO_FETCH_ALL = "woFetchAll";

		public static final String CV_NAME = "cvName";
		public static final String CUSTOM_VIEW = "customView";
		public static final String NEW_CV = "newCV";
		public static final String VIEW_SHARING_LIST = "viewSharingList";
		public static final String FILTERS = "filters";
		public static final String FILTER_CONDITIONS = "filterConditions";
		public static final String CRITERIA_IDS = "criteria";
		public static final String COUNT = "count";
		public static final String FILTER = "filter";
		public static final String FILTER_CRITERIA = "filterCriteria";
		public static final String INCLUDE_PARENT_CRITERIA = "includeParentCriteria";
		public static final String SEARCH = "search";
		public static final String SEARCH_CRITERIA = "searchCriteria";
		public static final String SORTING = "sorting";
		public static final String OVERRIDE_SORTING = "overrideSorting";
		public static final String MAX_LEVEL = "maxLevel";
		public static final String SORTING_QUERY = "sortingQuery";
		public static final String LIMIT_VALUE = "limitValue";
		public static final String WORKFLOW_FETCH_EVENT = "workflowFetchEvent";
		public static final String WORKFLOW_FETCH_CHILDREN = "workflowFetchChildren";
		public static final String WORKFLOW_UPDATE = "workflowUpdate";
		public static final String WORKFLOW_RULE = "workflowRule";
		public static final String WORKFLOW_RULE_ID = "workflowRuleID";
		public static final String WORKFLOW_RULE_MODULE = "workflowrule";
		public static final String READING_RULE_MODULE = "readingrule";
		public static final String WORKORDER_ACTIVITY = "workorderactivity";
		public static final String RULE_COUNT = "ruleCount";
		public static final String RULES = "rules";
		public static final String ALARM_RULE = "alarmRule";
		public static final String OLD_ALARM_RULE = "oldalarmRule";
		public static final String ALARM_RULE_ACTIVE_ALARM = "alarmRuleActiveAlarm";
		public static final String ALARM_RULE_THIS_WEEK = "alarmRuleThisWeek";
		public static final String ALARM_RULE_TOP_5_ASSETS = "alarmRuleTop5Assets";
		public static final String ALARM_RULE_WO_SUMMARY = "alarmRulewoSummary";
		public static final String WORKFLOW_ALARM_TRIGGER_RULES = "workflowAlarmTriggerRules";
		public static final String WORKFLOW_ALRM_CLEAR_RULE = "workflowAlarmClearRule";
		public static final String WORKFLOW_RULE_LIST="workflowRuleList";
		public static final String APPROVAL_RULE = "approvalRule";
		public static final String APPROVER_ID_LIST = "approverIdList";
		public static final String APPROVER_LIST = "approverList";
		public static final String READING_RULE_LIST ="readingRules";
		public static final String READING_RULE_ID ="readingRuleId";
		public static final String IS_READING_RULE_EXECUTE_FROM_JOB ="isReadingRulesExecutionFromJob";
		public static final String READING_RULE_ALARM_META ="readingRuleAlarmMeta";
		public static final String WORKFLOW_RULE_TYPE = "workflowRuleType";
		public static final String WORKFLOW_ACTION_LIST="workflowActions";
		public static final String WORKFLOW_ACTION_ID="workflowActionId";
		public static final String PAGINATION = "pagination";
		public static final String CURRENT_EXECUTION_TIME = "currentexecutiontime";
		public static final String NEXT_EXECUTION_TIMES = "nextexecutiontimes";
		public static final String PM_JOBS = "pmjobs";
		public static final String GROUP_STATUS = "groupStatus";
		
		public static final String TECH_COUNT_GROUP_DIGEST = "techCountGroupDigest";
		public static final String SITE_ROLE_WISE_COUNT= "siteRoleWiseCount";
		public static final String SITE_ROLE_WO_COUNT= "siteRoleWoCount";
		
		
		public static final String VIEWID = "viewId";
		public static final String VIEW_LIST = "views";
		public static final String GROUP_VIEWS = "groupViews";

		public static final String VIEWCOLUMNS = "viewColumns";
		public static final String PARENT_VIEW = "parentView";
		public static final String VIEW_COUNT = "viewCount";
		public static final String SUB_VIEW = "subView";
		public static final String SUB_VIEW_COUNT = "subViewCount";
		public static final String RESOURCE_ID = "RESOURCE_ID";
		public static final String SHOW_RELATIONS_COUNT = "showRelationsCount";
		public static final String RELATIONS_COUNT = "relationsCount";
		
		public static final String SPACECATEGORY = "SPACECATEGORY";
		public static final String SPACECATEGORIESLIST = "SPACECATEGORIESLIST";
		
		public static final String FILE_FORMAT = "fileFormat";
		public static final String FILE_ID = "fileID";
		public static final String FILE_URL = "fileUrl";
		public static final String DATE_FILTER = "dateFilter";
		public static final String DATE_FIELD = "dateField";
		public static final String START_TIME = "startTime";
		public static final String ALARM_ID = "alarmId";
		public static final String SCHEDULE_INFO = "schedule";
		public static final String END_TIME = "endTime";
		public static final String MAX_COUNT = "maxCount";
		
		public static final String DATE_OPERATOR = "dateOperator";
		public static final String DATE_OPERATOR_VALUE = "dateOperatorValue";
		
		public static final String BASE_LINE = "baseLine";
		public static final String BASE_LINE_LIST = "baseLines";
		public static final String DATE_RANGE = "dateRange";
		
		public static final String CHART_STATE = "chartState";
		public static final String TABULAR_STATE = "tabularState";
		
		public static final String COST = "cost";
		public static final String COST_ASSET = "costAsset";
		public static final String COST_FIRST_BILL_TIME = "costFirstBillTime";
		public static final String COST_READINGS = "costReadings";
		
		public static final String TIMESTAMP = "timestamp";
		public static final String DEVICE_DATA="deviceData";
		public static final String BULK_DEVICE_DATA = "bulkData";
		public static final String DEVICE_LIST="deviceList";
		public static final String PAY_LOAD = "payLoad";
		public static final String MODELED_DATA="modeledData";
		public static final String INSTANCE_INFO="instanceInfo";
		public static final String READING_KEY="readingKey";
		public static final String CONFIGURE="configure";
		public static final String SUBSCRIBE="subscribe";
		public static final String UNSUBSCRIBE_IDS="unsubscribeIds";
		public static final String UPDATE_LAST_READINGS = "updateLastReadings";
		public static final String SKIP_LAST_READING_CHECK = "skipLastReadingCheck";
		public static final String PREVIOUS_READING_DATA_META = "previousReadingDataMeta";
		public static final String CURRRENT_READING_DATA_META = "currentReadingDataMeta";
		public static final String READING_DATA_META_ID = "readingDataMetaId";
		public static final String READING_DATA_META_LIST = "readingDataMetaList";
		public static final String READING_DATA_META_MAP = "readingDataMetaMap";
		public static final String READING_DATA_META_TYPE = "readingDataMetaType";
		public static final String FORMULA_FIELD = "formulaField";
		public static final String FORMULA_UNIT_STRING = "formulaUnit";
		public static final String FORMULA_FIELD_TYPE = "formulaFieldType";
		public static final String FORMULA_LIST = "formulaList";
		public static final String HISTORY_READINGS = "historyReadings";
		public static final String HISTORY_ALARM = "historyAlarm";
		public static final String SKIP_OPTIMISED_WF = "skipOptimisedWorkflow";
		public static final String ANALYTICS_ANAMOLY = "analyticsAnamoly";
		public static final String DERIVATION = "derivation";
		public static final String FETCH_MAPPED="fetchMapped";
		public static final String FETCH_READING_INPUT_VALUES="fetchReadingInputValues";
		public static final String IS_FETCH_RDM_FROM_UI="isFetchRDMFromUI";
		
		public static final String PORTALINFO = "portalInfo";
		public static final String PUBLICKEYFILE = "publicKeyFile";
		public static final String PUBLICKEYFILENAME = "publicKeyFileName";
		public static final String PUBLICKEYFILETYPE = "publicKeyFileNameType";
		public static final String MARKED_READINGS = "markedReadings";
		public static final String WEATHER_READING = "weather";
		public static final String WEATHER_DAILY_READING = "weatherDaily";
		public static final String WEATHER_DAILY_FORECAST_READING = "weatherDailyForecast";
		public static final String WEATHER_HOURLY_FORECAST_READING = "weatherHourlyForecast";
		public static final String CDD_READING="cdd";
		public static final String HDD_READING="hdd";
		public static final String WDD_READING="wdd";
		
		public static final String PSYCHROMETRIC_READING = "psychrometric";
		public static final String WET_BULB_TEMPERATURE="wetBulbTemperature";
		public static final String MODULE_NAMES = "moduleNames";
		public static final String SORT_FIELDS = "sortFields";
		public static final String SORT_FIELDS_OBJECT = "sortFieldsObject";
		
		public static final String EXCLUDE_EMPTY_FIELDS = "exludeEmptyFields";
		public static final String WITH_READINGS = "withReadings";
		public static final String READING_ID = "readingId";
		public static final String INPUT_TYPE = "inputtype";
		
		public static final String BENCHMARK_UNITS = "benchmarkUnits";
		public static final String BENCHMARK_VALUE = "benchmarkValue";
		public static final String BENCHMARK_DATE_AGGR = "benchmarkDateAggr";
		public static final String READING_RULES_LIST = "readingRulesList";
		public static final String READING_FIELD_ID = "readingFieldId";
		public static final String ACTIONS_LIST = "actionsList";
		public static final String DEL_READING_RULE_IDS = "delReadingRuleIDs";
		public static final String DEL_READING_RULE = "delReadingRule";
		public static final String DO_VALIDTION = "doValidation";
		public static final String BENCHMARK_DATE_VAL = "benchmarkDateVal";
		public static final String SHIFT = "shift";
		public static final String SHIFTS = "shifts";
		public static final String SHIFT_USER_MAPPING = "shiftUserMapping";
		public static final String ACTUAL_TIMINGS = "actualTimings";
		public static final String SHIFT_ID = "shiftId";
		
		public static final String LAST_SYNC_TIME = "lastSyncTime";
		public static final String CUSTOM_OBJECT = "customObject";
		public static final String FORM_NAME = "formName";
		public static final String FORM_ID = "formId";
		public static final String FORM = "form";
		public static final String FORMS = "forms";
		public static final String FORM_FIELD = "formField";
		public static final String FORM_FIELDS = "formFields";
		public static final String FORM_OBJECT = "formObject";
		public static final String FORM_TYPE = "formType";
		public static final String FORM_SECTION = "formSection";
		
		
		public static final String QR_VALUE = "qrValue";
		public static final String MAP_QR = "mapqr";
		
		public static final String PUBLISH_DATA = "publishData";
		public static final String PUBLISH_SUCCESS = "publishSucsess";
		public static final String PUBLISH_FAILURE = "publishFailure";
		public static final String ORGUNITS_LIST = "orgUnitsList";
		public static final String ALL_METRICS = "allMetrics";
		public static final String METRICS_WITH_UNITS = "MetricsWithUnits";
		
		public static final String PUBSUB_TOPIC = "pubsubTopic";

		public static final String IDS_TO_UPDATE_TASK_COUNT = "ids_to_update_task_count";
		public static final String IDS_TO_UPDATE_COUNT = "ids_to_update_count";
		
		public static final String PAGE = "page";
		public static final String CURRENT_CALENDAR_VIEW = "currentCalendarView";
		public static final String ML_FORECASTING = "ml_forecasting";
        public static final String CREATE_IN_PREOPEN = "crateInPreOpen";
		public static final String PREOPEN_EXECUTION_TIMES = "preOpenExecTimes";
		public static final String WO_CONTEXTS = "woContexts";
		public static final String SCHEDULED_WO_MAP_MAP = "scheduledWoMap";


		public static final String WORKORDER_PARTS = "workorderParts";
		public static final String WORKORDER_PART = "workorderPart";
		public static final String WORKORDER_PART_LIST = "workorderPartsList";

		public static final String WORKORDER_COST = "workorderCost";
		public static final String WORKORDER_COST_TYPE = "workorderCostType";

		public static final String STORE_ROOM = "storeRoom";
		public static final String STORE_ROOMS = "storeRooms";
		public static final String STORE_ROOM_LIST = "storeRoomList";
		public static final String STORE_ROOM_ID = "storeRoomId";
		
		public static final String ITEM_TYPES = "itemTypes";
		public static final String ITEM_TYPES_LIST = "itemTypesList";
		public static final String ITEM_TYPES_CATEGORY = "itemTypesCategory";
		public static final String ITEM_TYPES_STATUS = "itemTypesStatus";
		public static final String ITEM_TYPES_ID = "itemTypesId";
		public static final String ITEM_TYPES_IDS = "itemTypesIds";



		public static final String TOOL_TYPES = "toolTypes";
		public static final String TOOL_TYPES_LIST = "toolTypesList";
		public static final String TOOL_TYPES_STATUS = "toolTypesStatus";
		public static final String TOOLS_TYPES_CATEGORY = "toolTypesCategory";

		public static final String TOOL_TYPES_ID = "toolTypesId";
		public static final String TOOL_TYPES_IDS = "toolTypesIds";

//		public static final String TOOL = "tool";
		public static final String TOOLS = "tools";
		public static final String TOOLS_STATUS = "toolsStatus";
		public static final String TOOLS_CATEGORY = "toolsCategory";

		public static final String VENDOR = "vendor";
		public static final String VENDORS = "vendors";
		public static final String VENDOR_ID = "vendorsId";

		public static final String ITEM = "item";
		public static final String ITEMS = "items";
		public static final String ITEM_STATUS = "itemStatus";
		public static final String ITEM_ID = "itemId";
		public static final String ITEM_IDS = "itemIds";
		public static final String PURCHASED_ITEM = "purchasedItem";
		public static final String ITEM_ACTIVITY = "itemtypeactivity";


		public static final String WORKORDER_ITEMS = "workorderItem";
		public static final String ITEM_TRANSACTIONS = "itemTransactions";

		public static final String TOOL_LIST = "toolList";
		public static final String TOOL = "tool";
		public static final String TOOL_ID = "toolId";
		public static final String TOOL_IDS = "toolIds";
		public static final String TOOL_STATUS = "toolStatus";
		
		public static final String WORKORDER_TOOLS = "workorderTools";
		public static final String TOOL_TRANSACTIONS = "toolTransactions";
		public static final String TOOL_TRANSACTION_LIST = "toolTransactionList";
		public static final String INVENTORY_TRANSACTIONS = "inventoryTransactions";
		public static final String STOCKED_TOOLS_RETURN_TRACKING = "stockedToolsReturnTracking";
		
		public static final String ITEM_TYPES_COUNT = "itemTypesCount";
		public static final String ITEM_COUNT = "itemCount";
		public static final String PURCHASED_ITEM_COUNT = "purchasedItemCount";
		public static final String GATE_PASS_COUNT = "gatePassCount";
		public static final String TOOL_TYPES_COUNT = "toolTypesCount";
		public static final String TOOL_COUNT = "toolCount";
		public static final String VENDORS_COUNT = "vendorsCount";
		public static final String STORE_ROOM_COUNT = "storeRoomCount";

		public static final String TENANT = "tenant";
		public static final String TENANT_UTILITY_IDS = "utilityIds";
		public static final String TENANT_CONTEXT = "tenantContext";

		public static final String SHOW_TOOLS_FOR_RETURN = "showToolsForReturn";
		public static final String SHOW_ITEMS_FOR_RETURN = "showItemsForReturn";
		public static final String SHOW_ITEMS_FOR_ISSUE = "showItemsForIssue";
		public static final String SHOW_TOOLS_FOR_ISSUE = "showToolsForIssue";

		public static final String SHOW_ITEM_FOR_WORKORDER = "showItemsForWorkorder";

		public static final String ITEM_VENDORS = "itemVendors";
		public static final String TRANSACTION_TYPE = "transactionType";
		public static final String TRANSACTION_STATE = "transactionState";

		public static final String PURCHASED_TOOL = "purchasedTool";
		public static final String PURCHASED_TOOL_IS_USED = "purchasedToolIsUsed";
		public static final String PURCHASED_ITEM_IS_USED = "purchasedItemIsUsed";
		
		public static final String IS_BULK_ITEM_ADD = "isBulkItemAdd";

		public static final String SKIP_WO_CREATION = "skipWOCreation";
		
		public static final String ITEM_TRANSACTION_APPORVED_STATE = "itemTransactionApprovedState";
		public static final String TOOL_TRANSACTION_APPORVED_STATE = "toolTransactionApprovedState";
		public static final String LABOUR = "labour";
		public static final String LABOURS = "labours";
		public static final String LABOUR_ID = "labourId";
		public static final String LABOUR_IDS = "labourIds";
		public static final String WO_LABOUR = "workorderLabour";

		public static final String PURCHASE_REQUEST = "purchaserequest";
		public static final String PURCHASE_REQUESTS = "purchaserequests";
		public static final String PURCHASE_REQUEST_LINE_ITEMS = "purchaserequestlineitems";
		public static final String STATUS = "status";

		public static final String PURCHASE_ORDER = "purchaseorder";
		public static final String PURCHASE_ORDERS = "purchaseorders";
		public static final String PURCHASE_ORDER_LINE_ITEMS = "purchaseorderlineitems";
		
		public static final String RECEIPT = "receipt";
		public static final String RECEIPTS = "receipts";
		
		public static final String RECEIVABLE = "receivable";
		public static final String RECEIVABLES = "receivables";
		public static final String PO_ID = "poId";
		public static final String PR_IDS = "prIds";
		public static final String RECEIVABLE_ID = "receivableId";
		public static final String PURCHASE_ORDER_LINE_ITEMS_ID = "purchaseorderlineitemsId";
        public static final String VALIDATION_RULES = "validationRules";

        private static final String RECEIPT_LINE_ITEMS = "receiptlineitems";
		public static final String ITEM_VENDORS_LIST = "itemTypesVendorsList";
		
		public static final String ML ="ml";
		
		public static final String SITES_FOR_STORE_ROOM = "sitesForStoreRoom";
//		public static final String STATE = "state";

		public static final String PURCHASE_CONTRACTS = "purchasecontracts";
		public static final String PURCHASE_CONTRACT = "purchasecontract";
		public static final String PURCHASE_CONTRACTS_LINE_ITEMS = "purchasecontractlineitems";
		public static final String CONTRACTS = "contracts";
		
		public static final String LABOUR_CONTRACTS = "labourcontracts";
		public static final String LABOUR_CONTRACT = "labourcontract";
		public static final String LABOUR_CONTRACTS_LINE_ITEMS = "labourcontractlineitems";

		public static final String PO_LINE_ITEMS_SERIAL_NUMBERS = "poLineItemSerialNumbers";
		public static final String SERIAL_NUMBERS = "serialNumbers";
		public static final String ASSETS = "assets";
		
		public static final String TOTAL_COST = "totalCost";
		public static final String TOTAL_QUANTITY = "totalQuantity";
		public static final String WO_TOTAL_COST = "woTotalCost";
		public static final String UNIT_PRICE = "unitPrice";

		public static final String GATE_PASS = "gatePass";
		public static final String GATE_PASS_LINE_ITEMS = "gatePassLineItems";
		
		public static final String CONNECTED_APPS = "connectedApps";

		public static final String STATE_FLOW = "stateFlow";
		public static final String STATE_FLOW_LIST = "stateFlows";
		public static final String STATE_FLOW_ID = "stateFlowId";
		public static final String TRANSITION_ID = "transition_id";
		public static final String STATE_TRANSITION_LIST = "stateTransitionList";
		public static final String AVAILABLE_STATES = "states";
		public static final String CURRENT_STATE = "currentState";
		public static final String DEFAULT_STATE = "default_state";
		public static final String DEFAULT_STATE_ID = "default_state_id";
		public static final String DEFAULT_STATE_FLOW_ID = "default_state_flow_id";
		public static final String TRANSITION = "transition";
		public static final String STATE_TRANSITION_ONLY_CONDITIONED_CHECK = "stateTransitionOnlyConditionedCheck";

		public static final String ROTATING_ASSET = "rotatingAsset";
		
		public static final String WO_ITEMS_LIST = "woItemsList";
		public static final String WO_TOOLS_LIST = "woToolsList";
		public static final String WO_LABOUR_LIST = "woLabourList";
		
		public static final String SHIPMENT = "shipment";
		public static final String SHIPMENT_LINE_ITEM = "shipmentLineItem";
		public static final String SHIPMENTS = "shipments";


		public static final String INVENTORY_REQUEST = "inventoryrequest";
		public static final String INVENTORY_REQUESTS = "inventoryrequests";
		public static final String INVENTORY_REQUEST_LINE_ITEMS = "inventoryrequestlineitems";


		public static final String UNIT_POINTS="unit";
		public static final String DEMO_ROLLUP_EXECUTION_TIME="nextexecution";
		public static final String DEMO_ROLLUP_JOB_ORG="rollup_job_org";
		public static final String DATA_POINTS="data_points";
		public static final String POINTS_DATA_RECORD="points_data_record";

		public static final String DATE = "date";
		
		public static final String PM_PLANNER_SETTINGS="pm_planner_settings";

		public static final String ATTENDANCE = "attendance";
		public static final String ATTENDANCE_TRANSACTIONS = "attendanceTransaction";
		public static final String BREAK = "break";
		public static final String BREAK_LIST = "break_list";
		public static final String BREAK_TRANSACTION = "breakTransaction";

		
		private static Map<String, Class> classMap = Collections.unmodifiableMap(initClassMap());
		private static Map<String, Class> initClassMap() {
			Map<String, Class> classMap = new HashMap<>();
			classMap.put(TICKET_STATUS, FacilioStatus.class);
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
			classMap.put(READING_ALARM, ReadingAlarmContext.class);
			classMap.put(ML_ALARM, MLAlarmContext.class);
			classMap.put(ML_ALARM_OCCURRENCE, MLAlarmOccurrenceContext.class);
			classMap.put(RESOURCE, ResourceContext.class);
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
			classMap.put(ASSET_ACTIVITY, ActivityContext.class);
			classMap.put(WORKORDER_ACTIVITY, ActivityContext.class);
			classMap.put(ENERGY_METER, EnergyMeterContext.class);
			classMap.put(CHILLER, ChillerContext.class);
			classMap.put(CHILLER_PRIMARY_PUMP, ChillerPrimaryPumpContext.class);
			classMap.put(CHILLER_CONDENSER_PUMP, ChillerCondenserPumpContext.class);
			classMap.put(CHILLER, ChillerContext.class);
			classMap.put(AHU, AHUContext.class);
			classMap.put(COOLING_TOWER, CoolingTowerContext.class);
			classMap.put(FCU, FCUContext.class);
			classMap.put(HEAT_PUMP, HeatPumpContext.class);
			classMap.put(UTILITY_METER, UtilityMeterContext.class);
			classMap.put(ENERGY_DATA_READING, ReadingContext.class);
			classMap.put(ENERGY_METER_PURPOSE, EnergyMeterPurposeContext.class);
			classMap.put(WEATHER_READING, ReadingContext.class);
			classMap.put(CDD_READING, ReadingContext.class);
			classMap.put(HDD_READING, ReadingContext.class);
			classMap.put(UTILITY_BILL_READING, ReadingContext.class);
			classMap.put(WATER_METER, WaterMeterContext.class);
			classMap.put(CHILLER_SECONDARY_PUMP, ChillerSecondaryPumpContext.class);	
			classMap.put(INVENTORY, InventoryContext.class);
			classMap.put(INVENTORY_VENDOR, InventoryVendorContext.class);
			classMap.put(INVENTORY_VENDORS, InventoryVendorContext.class);
			classMap.put(INVENTORY_CATEGORY, InventoryCategoryContext.class);
			classMap.put(ML_FORECASTING, MlForecastingContext.class);
			classMap.put(WORKORDER_PARTS, WorkorderPartsContext.class);
			classMap.put(WORKORDER_PART, WorkorderPartsContext.class);
			classMap.put(WORKORDER_PART_LIST, WorkorderPartsContext.class);
			classMap.put(WORKORDER_COST, WorkorderCostContext.class);
			classMap.put(STORE_ROOM, StoreRoomContext.class);
			classMap.put(STORE_ROOMS, StoreRoomContext.class);
			classMap.put(ITEM_TYPES, ItemTypesContext.class);
			classMap.put(ITEM_TYPES_CATEGORY,ItemTypesCategoryContext.class);
			classMap.put(ITEM_TYPES_STATUS, ItemTypesStatusContext.class);
			classMap.put(TOOL_TYPES, ToolTypesContext.class);
			classMap.put(ITEM_ACTIVITY, ActivityContext.class);
			classMap.put(TOOLS_TYPES_CATEGORY, ToolTypesCategoryContext.class);
			classMap.put(TOOL_TYPES_STATUS, ToolTypesStatusContext.class);
			classMap.put(VENDOR, VendorContext.class);
			classMap.put(VENDORS, VendorContext.class);
			classMap.put(ITEM, ItemContext.class);
			classMap.put(ITEM_STATUS, ItemStatusContext.class);
			classMap.put(PURCHASED_ITEM, PurchasedItemContext.class);
			classMap.put(WORKORDER_ITEMS, WorkorderItemContext.class);
			classMap.put(ITEM_TRANSACTIONS, ItemTransactionsContext.class);
			classMap.put(TOOL, ToolContext.class);
			classMap.put(TOOL_STATUS, ToolStatusContext.class);
			classMap.put(WORKORDER_TOOLS, WorkorderToolsContext.class);
			classMap.put(TOOL_TRANSACTIONS, ToolTransactionContext.class);
			classMap.put(STOCKED_TOOLS_RETURN_TRACKING, StockedToolsReturnTrackingContext.class);
			classMap.put(ITEM_VENDORS, ItemTypesVendorsContext.class);
			classMap.put(PURCHASED_TOOL, PurchasedToolContext.class);
			classMap.put(TENANT, TenantContext.class);
			classMap.put(LABOUR, LabourContext.class);
			classMap.put(WO_LABOUR, WorkOrderLabourContext.class);
			classMap.put(PURCHASE_REQUEST, PurchaseRequestContext.class);
			classMap.put(PURCHASE_REQUEST_LINE_ITEMS, PurchaseRequestLineItemContext.class);
			classMap.put(PURCHASE_ORDER, PurchaseOrderContext.class);
			classMap.put(PURCHASE_ORDER_LINE_ITEMS, PurchaseOrderLineItemContext.class);
			classMap.put(PURCHASE_CONTRACTS, PurchaseContractContext.class);
			classMap.put(PURCHASE_CONTRACTS_LINE_ITEMS, PurchaseContractLineItemContext.class);
			classMap.put(LABOUR_CONTRACTS, LabourContractContext.class);
			classMap.put(LABOUR_CONTRACTS_LINE_ITEMS, LabourContractLineItemContext.class);
			classMap.put(RECEIVABLE, ReceivableContext.class);
			classMap.put(RECEIPT, ReceiptContext.class);
			classMap.put(RECEIPT_LINE_ITEMS, ReceiptLineItemContext.class);
			classMap.put(SHIPMENT, ShipmentContext.class);
			classMap.put(SHIPMENT_LINE_ITEM, ShipmentLineItemContext.class);
			
			classMap.put(ML, MLContext.class);
			classMap.put(PO_LINE_ITEMS_SERIAL_NUMBERS, PoLineItemsSerialNumberContext.class);
			classMap.put(GATE_PASS, GatePassContext.class);
			classMap.put(GATE_PASS_LINE_ITEMS, GatePassLineItemsContext.class);
			classMap.put(CONNECTED_APPS, ConnectedAppContext.class);
			classMap.put(INVENTORY_REQUEST, InventoryRequestContext.class);
			classMap.put(INVENTORY_REQUEST_LINE_ITEMS, InventoryRequestLineItemContext.class);

			classMap.put(ATTENDANCE, AttendanceContext.class);
			classMap.put(ATTENDANCE_TRANSACTIONS, AttendanceTransactionContext.class);
			return classMap;
		}
		
		public static Class getClassFromModuleName(String moduleName) {
			return classMap.get(moduleName);
		}
		
		public static Collection<Class> getAllClasses() {
			return classMap.values();
		}
	}
	
	public static class OrgInfoKeys {
		public static final String USE_CONTROLLER_DATA_INTERVAL = "useControllerDataInterval";
		public static final String DEFAULT_DATA_INTERVAL = "defaultDataInterval";
		public static final String MODULE_EXPORT_LIMIT = "moduleExportLimit";
	}
	
	public static class Alarm {
		public static final String CLEAR_SEVERITY = "Clear";
		public static final String INFO_SEVERITY = "Info";
		public static final String CRITICAL_SEVERITY = "Critical";
	}
	
	public static class Criteria {
		public static final String LOGGED_IN_USER = "${LOGGED_USER}";
		public static final String LOGGED_IN_USER_GROUP = "${LOGGED_USER_GROUP}";
	}
	
	public static class ApprovalRule {
		public static final String APPROVAL_RULE_ID_FIELD_NAME = "approvalRuleId";
		public static final String APPROVAL_STATE_FIELD_NAME = "approvalState";
		public static final String APPROVAL_REQUESTED_BY_FIELD_NAME = "requestedBy";
		public static final String APPROVAL_REQUESTER = "approvalRequester";
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
		public static final String URGENCY = "urgency";
	}
	public static class Job{
		public static final String NEXT_EXECUTION_TIME = "NEXT_EXECUTION_TIME";
		public static final String TABLE_NAME = "Jobs";
		public static final String FORKED_COMMANDS = "forkedCommands";
		public static final String JOB_CONTEXT = "jobContext";
		
		public static final String EXECUTER_NAME_FACILIO = "facilio";
		public static final String EXECUTER_NAME_PRIORTIY = "priority";
		
		public static final String SCHEDULED_READING_RULE_JOB_NAME = "scheduledReadingRule";
		public static final String SCHEDULED_ALARM_TRIGGER_RULE_JOB_NAME = "scheduledAlarmTriggerRule";
		
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
		public static final String MODULE_TYPE = "module_type";

		public static final String ACTUAL_DATA = "actual";
		
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