package com.facilio.constants;

import com.facilio.activity.ActivityContext;
import com.facilio.agent.alarms.AgentAlarmContext;
import com.facilio.agentv2.E2.E2ControllerContext;
import com.facilio.agentv2.bacnet.BacnetIpControllerContext;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.lonWorks.LonWorksControllerContext;
import com.facilio.agentv2.metrics.AgentMetrics;
import com.facilio.agentv2.misc.MiscControllerContext;
import com.facilio.agentv2.modbusrtu.ModbusRtuControllerContext;
import com.facilio.agentv2.modbustcp.ModbusTcpControllerContext;
import com.facilio.agentv2.niagara.NiagaraControllerContext;
import com.facilio.agentv2.opcua.OpcUaControllerContext;
import com.facilio.agentv2.opcxmlda.OpcXmlDaControllerContext;
import com.facilio.agentv2.rdm.RdmControllerContext;
import com.facilio.agentv2.system.SystemControllerContext;
import com.facilio.alarms.sensor.context.sensoralarm.SensorAlarmContext;
import com.facilio.alarms.sensor.context.sensoralarm.SensorAlarmOccurrenceContext;
import com.facilio.alarms.sensor.context.sensoralarm.SensorEventContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpAlarmContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpAlarmOccurrenceContext;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpEventContext;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.reservation.ExternalAttendeeContext;
import com.facilio.bmsconsole.context.reservation.InternalAttendeeContext;
import com.facilio.bmsconsole.context.reservation.ReservationContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowCommitmentRuleContext;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceTransaction;
import com.facilio.bmsconsoleV3.context.budget.*;
import com.facilio.bmsconsoleV3.context.communityfeatures.*;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.AnnouncementSharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.announcement.PeopleAnnouncementContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.*;
import com.facilio.bmsconsoleV3.context.floorplan.*;
import com.facilio.bmsconsoleV3.context.inspection.*;
import com.facilio.bmsconsoleV3.context.inventory.*;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.context.meter.*;
import com.facilio.bmsconsoleV3.context.jobplan.*;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PoAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderLineItemContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.PrAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.quotation.*;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationLineItemsContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationVendorsContext;
import com.facilio.bmsconsoleV3.context.reservation.InventoryReservationContext;
import com.facilio.bmsconsoleV3.context.spacebooking.V3SpaceBookingContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesLineItemsContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedServicesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedToolsContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitChecklistContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistCategoryContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeChecklistContext;
import com.facilio.bmsconsoleV3.context.workpermit.WorkPermitTypeContext;
import com.facilio.control.*;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.mv.context.MVAdjustment;
import com.facilio.mv.context.MVBaseline;
import com.facilio.mv.context.MVProjectContext;
import com.facilio.qa.context.*;
import com.facilio.remotemonitoring.context.*;
import com.facilio.remotemonitoring.context.AlarmCategoryContext;
import com.facilio.remotemonitoring.signup.*;
import com.facilio.service.FacilioService;
import com.facilio.taskengine.common.JobConstants;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.AttachmentV3Context;
import com.facilio.v3.util.ChainUtil;
import com.sun.jna.platform.unix.solaris.LibKstat;
import org.apache.kafka.common.protocol.types.Field;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
//import com.facilio.custom.CustomController;

public class FacilioConstants {

	public static final SimpleDateFormat HTML5_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	public static final SimpleDateFormat HTML5_DATE_FORMAT_1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	public static final int INVITATION_EXPIRY_DAYS = 7;

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

	public static class SystemLookup {
		public static final String URL_RECORD = "urlRecord";
		public static final String CURRENCY_RECORD = "currencyRecord";
	}
	public static class MultiCurrency {
		public static final List<String> MULTI_CURRENCY_ENABLED_MODULES = Collections.unmodifiableList(getMultiCurrencyEnabledModuleNames());
		public static final List<String> MULTI_CURRENCY_CUSTOM_FIELD_ADDITION_MODULES = Collections.unmodifiableList(getMultiCurrencyCustomFieldAdditionModules());

		private static List<String> getMultiCurrencyCustomFieldAdditionModules() {
			List<String> moduleNames = new ArrayList<>();
			moduleNames.add(ContextNames.PURCHASE_ORDER);
			moduleNames.add(ContextNames.QUOTE);
			moduleNames.add(ContextNames.PURCHASE_REQUEST);
			moduleNames.add(ContextNames.WORK_ORDER);
			return moduleNames;
		}
		private static List<String> getMultiCurrencyEnabledModuleNames() {
			List<String> moduleNames = new ArrayList<>();
			moduleNames.add(ContextNames.PURCHASE_ORDER);
			moduleNames.add(ContextNames.PURCHASE_ORDER_LINE_ITEMS);
			moduleNames.add(ContextNames.QUOTE);
			moduleNames.add(ContextNames.QUOTE_LINE_ITEMS);
			moduleNames.add(ContextNames.PURCHASE_REQUEST);
			moduleNames.add(ContextNames.PURCHASE_REQUEST_LINE_ITEMS);
			moduleNames.add(ContextNames.WORK_ORDER);
			moduleNames.add(ContextNames.TICKET);
//			moduleNames.add(ContextNames.RESOURCE);
			moduleNames.add(ContextNames.WORKORDER_COST);
			moduleNames.add(ContextNames.WORKORDER_ITEMS);
			moduleNames.add(ContextNames.WORKORDER_TOOLS);
			moduleNames.add(ContextNames.WO_SERVICE);
			moduleNames.add(ContextNames.WO_PLANNED_ITEMS);
			moduleNames.add(ContextNames.WO_PLANNED_TOOLS);
			moduleNames.add(ContextNames.WO_PLANNED_SERVICES);
			moduleNames.add(ContextNames.WorkOrderLabourPlan.WORKORDER_LABOUR_PLAN);
			moduleNames.add(ContextNames.WO_LABOUR);
			moduleNames.add(ContextNames.Budget.BUDGET);
			moduleNames.add(ContextNames.Budget.BUDGET_AMOUNT);
			moduleNames.add(ContextNames.Budget.BUDGET_MONTHLY_AMOUNT);
			moduleNames.add(ContextNames.TRANSACTION);
			moduleNames.add(ContextNames.ITEM);
			moduleNames.add(ContextNames.ITEM_TYPES);
			moduleNames.add(ContextNames.TOOL);
			moduleNames.add(ContextNames.TOOL_TYPES);
			moduleNames.add(ContextNames.SERVICE);
			moduleNames.add(ContextNames.SERVICE_VENDOR);
			moduleNames.add(ContextNames.PURCHASED_ITEM);
			moduleNames.add(ContextNames.PURCHASED_TOOL);
			moduleNames.add(ContextNames.ITEM_TRANSACTIONS);
			moduleNames.add(ContextNames.TOOL_TRANSACTIONS);
			return moduleNames;
		}
	}
	
	public static class PM_V2 {
		public static final String PM_V2_MODULE_NAME = "plannedmaintenance";
		public static final String PM_V2_TRIGGER = "pmTriggerV2";
		
		public static final String PM_V2_RESOURCE_PLANNER = "pmResourcePlanner";
		public static final String PM_V2_PLANNER = "pmPlanner";
		public static final String PLANNER_LIST = "pmPlannerList";
		public static final String AVAILABLE_TRIGGER_FREQUENCIES = "availableTriggerFrequncies";
		public static final String PM_V2_SITES = "plannedMaintenanceSite";
	}
	public static class JOB_PLAN {
		public static final String JOB_PLAN_ID = "jobPlanId";
		public static final String JOB_PLAN_STATUS = "jobPlanStatus";
		public static final String IS_CLONING = "isCloning";
	}

	public static class TicketActivity {
		public static final String TICKET_ACTIVITIES = "ticketActivities";
		public static final String OLD_TICKETS = "oldTickets";
		public static final String MODIFIED_TIME = "modifiedTime";
		public static final String MODIFIED_USER = "modifiedUser";
	}

	public static class Module {
		public static final String SYS_FIELDS_NEEDED = "sysFieldsNeeded";
		public static final String IGNORE_MODIFIED_SYS_FIELDS = "ignoreModifiedSysFields";
		public static final String SKIP_EXISTING_MODULE_WITH_SAME_NAME_CHECK = "skipExistingModuleWithSameNameCheck";
	}

	public static class FeatureAccessConstants {
		public static final String FEATURE = "feature";
		public static final String RECORD_IDS = "recordIds";
		public static final String FEATURE_ENUM = "featureEnum";
		public static final String ACCESS_PERMISSION = "accessPermission";
	}

	public static class ViewConstants {
		public static final String PARENT_VIEW_OBJECT = "parentViewObject";
		public static final String SCHEDULED_VIEW = "scheduledView";
		public static final String CALENDAR_VIEW_AGGREGATE_DATA = "calendarViewAggregateData";
		public static final String CALENDAR_VIEW_V3_DATAMAP = "calendarViewV3DataMap";
		public static final String CALENDAR_VIEW_CUSTOMIZATON_DATAMAP = "calendarViewCustomizationDataMap";
		public static final String CALENDAR_VIEW_REQUEST = "calendarViewRequest";
		public static final String CALENDAR_VIEW_DATA = "calendarViewData";
		public static enum CalendarViewType {
			DAY(1),
			WEEK(2),
			MONTH(3),
			YEAR(4);

			private int intVal;

			private CalendarViewType(int val) {
				// TODO Auto-generated constructor stub
				this.intVal = val;
			}
			public static CalendarViewType getCalendarViewType(int val){
				return TYPE_MAP.get(val);
			}
			public int getIntVal() {
				return intVal;
			}

			public static final Map<Integer, CalendarViewType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
			private static Map<Integer, CalendarViewType> initTypeMap() {
				Map<Integer, CalendarViewType> typeMap = new HashMap<>();
				for(CalendarViewType type : values()) {
					typeMap.put(type.getIntVal(), type);
				}
				return typeMap;
			}
		}
	}
	public static class FieldsConfig {
		public static final String LICENSE_BASED_FIELDS_MAP = "licenseBasedFieldsMap";
		public static final String ACCESS_TYPE = "accessType";
		public static final String SKIP_CONFIG_FIELDS = "skipConfigFields";
		public static final String IS_ADD_FIELD = "isAddField";
		public static final String CHECK_FIELDS_PERMISSION = "checkFieldsPermission";
		public static final String CUSTOM = "custom";
		public static final String FIELD_RESPONSE_CHAIN = "fieldResponseChain";
		public static final String SUPPLEMENTS = "supplements";
		public static final String FIELD_LIST_TYPE = "fieldListType";
		public static final String CRITERIA = "criteria";
		public static final String FIELDS_TO_ADD_LIST = "fieldsToAddList";
		public static final String FIELD_TYPE_NAMES_TO_FETCH = "fieldTypeNamesToFetch";
		public static final String FIELDS_TO_SKIP_LIST = "fieldsToSkipList";
		public static final String FIELD_TYPES_TO_SKIP = "fieldTypesToSkip";
		public static final String FIELD_TYPES_TO_FETCH = "fiedlTypesToFetch";
		public static final String NAME = "name";
		public static final String TYPE_SPECIFIC_FIELDS_MAP = "typeSpecificFieldsMap";
		public static final String FIXED_FIELD_NAMES = "fixedFieldNames";
		public static final String CUSTOMIZATION = "customization";
		public static final String FIXED_SELECTABLE_FIELD_NAMES = "fixedSelectableFieldNames";
	}

	public static class ModuleNames {
		public static final String TOOL_TYPES = "tooltypes";
		public static final String ITEM_TYPES = "itemtypes";
		public static final String STORE_ROOM = "storeroom";
		public static final String DEVICES = "devices";
		public static final String ASSET_BREAKDOWN = "assetbreakdown";
		public static final String ITEM_TRANSACTIONS = "itemtransactions";
		public static final String TOOL_TRANSACTIONS = "tooltransactions";
		public static final String GATE_PASS = "gatepass";
		public static final String ISSUED_TO = "issuedto";
		public static final String TOOL = "tool";
		public static final String ITEM = "item";
		public static final String PURCHASED_TOOL = "purchasedtool";
		public static final String PURCHASED_ITEM = "purchaseditem";
		public static final String REQUEST_LINE_ITEM = "inventoryrequestlineitems";
		public static final String TENANT = "tenant";
		public static final String VENDOR = "vendor";
		public static final String PRINTERS = "printers";
		public static final String VISITOR_KIOSK = "visitorKiosk";
		public static final String FEEDBACK_KIOSK = "feedbackKiosk";
		public static final String SMART_CONTROL_KIOSK = "smartControlKiosk";
		public static final String FEEDBACK_TYPE = "feedbackType";
		public static final String FEEDBACK_TYPE_CATALOG_MAPPING = "feedbackTypeCatalogMapping";
		public static final String RULE_TEMPLATE_MODULE = "ruletemplate";
		public static final String SPACE = "space";
		public static final String WEATHER_SERVICE = "weatherservice";
		public static final String WEATHER_STATION = "weatherstation";
		public static final String DEVICE = "Devices";

	}

	public static class PickList {
		public static final String LOOKUP_LABEL_META = "lookupLabelMeta";
		public static final String LOOKUP_LABELS = "lookupLabels";
		public static final String DEFAULT_ID_LIST = "defaultIdList";
		public static final String SECONDARY_FIELD = "secondaryField";
		public static final String SUBMODULE_FIELD ="subModuleField";
		public static final String FOURTH_FIELD = "fourthField";
		public static final String LOCAL_SEARCH = "localSearch";
	}

	public static class Filters {
		public static final String FILTER_DATA_TYPE = "filterDataType";
		public static final String FILTER_DATA_TYPES = "filterDataTypes";
		public static final String FILTER_FIELDS = "filterFields";
		public static final String FILTER_OPERATORS = "filterOperators";
		public static final String PLACEHOLDER_FIELDS = "placeHolderFields";
	}

	public static class SettingConfigurationContextNames {
		public static final String STATE_FLOW = "stateflow";
		public static final String PAGE_BUILDER = "pageBuilder";
	}
	public static class DecommissionModuleType{
		public static final String ERROR = "error";
		public static final String WARNING = "warning";
		public static final String INFO = "info";
	}

	public static class FormContextNames {
		public static final String SUB_FORM = "subForm";
		public static final String SUB_FORM_MODULE_NAME = "subFormModuleName";
		public static final String NEXT_FORM_FIELD_ID = "nextFormFieldId";
		public static final String NEXT_FORM_ID = "nextFormId";
		public static final String PREVIOUS_FORM_ID = "previousFormId";
		public static final String FORM_RULE_USAGE = "formRuleUsage";
		public static final String PREVIOUS_FORM_FIELD_ID = "previousFormFieldId";
		public static final String FORM_SECTION_ID = "formSectionId";
		public static final String PREVIOUS_FORM_SECTION_ID = "previousFormSectionId";
		public static final String FORM_FIELD_SEQUENCE_NUMBER = "formFieldSequenceNumber";
		public static final String FORM_SECTION_SEQUENCE_NUMBER = "formSectionSequenceNumber";
		public static final String NEXT_FORM_SECTION_ID = "nextFormSectionId";
		public static final String SEQUENCE_NUMBER = "sequenceNumber";
		public static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
	}


	public static class ContextNames {
		public static final String CONDITIONS="conditions";
		public static final String WORKFLOW_RULE_LOGS="workflowRuleLogs";
		public static final String ONE_LEVEL_LOOKUP="oneLevelLookup";
		public static final String IS_ONE_LEVEL_FIELDS="isOneLevelFields";
		public static final String CURRENCY = "currency";
		public static final String OLD_CURRENCY = "oldCurrency";
		public static final String CURRENCIES_LIST = "currencies";
		public static final String SUPPLEMENTS = "supplements";
		public static final String RELATED_LIST = "relatedList";
		public static final String HAS_CURRENCY_FIELD_VALUE = "##currencyField##";
		public static final String CUSTOM_BUTTON_ID = "customButtonId";
		public static final String RELATED_MODULE_NAME = "relatedModuleName";
		public static final String RELATED_FIELD_NAME = "relatedFieldName";
		public static final String AUTO_CAD_FILE_IMPORT ="autoCadFileImport";

		public static final String ODATA_READING_VIEW ="odataReadingView";
		public static final String MODULE_AND_FIELDS ="moduleAndFields";
		public static final String READING_IMPORT_APP = "readingimport";
		public static final String ORDER_TYPE="orderType";
		public static final String ORDER_BY="orderBy";
        public static final String SHIFT_BREAK_REL = "shiftbreakrel";
		public static final String CUSTOM_KIOSK = "customkiosk";
		public static final String CUSTOM_KIOSK_BUTTON = "customkioskbutton";
		public static final String CUSTOM_DEVICE_BUTTON_MAPPING = "customdevicebuttonmapping";

		public static final String CUSTOM_KIOSK_DATA= "customKiosk";
		public static final String SUMMARY_CONTEXT= "summaryContext";
		public static  final String DECOMMISSION = "decommission";
		public static final String ERROR_MODULE_DATA = "errorModuleData";
		public static final String AVATAR_URL = "avatarUrl";
		public static final String IS_TO_FETCH_DECOMMISSIONED_RESOURCE="isToFetchDecommissionedResource";
        public static final String COMMISSION_TIME = "commissionedTime";
		public static final String DECOMMISSIONED_BY = "decommissionedBy";
		public static final String COMMISSIONED_BY = "commissionedBy";
		public static final String VIEW_RECOMMISSION_BTN = "viewRecommissionedBtn";
		public static final String REMARKS = "remarks";
		public static final String DECOMMISSION_LOG = "decommissionLog";
		public static final String LOG_CONTEXT = "logContext";
		public static final String RESOURCE_MODULENAME = "resourceModuleName";
		//public static final String RESOURCE_ID = "resourceId";
		public static final String EMAIL = "email";
		public static final String IS_WEBTAB_PERMISSION = "isWebTabPermission";

		public static final String NOTES_MODULE_NAME = "notesModuleName";
        public static final String WORK_ORDER_FEATURE_SETTINGS_LIST = "workOrderFeatureSettingsList";
		public static final String WORK_ORDER_FEATURE_SETTINGS_LIST_MAP = "workOrderFeatureSettingsListMap";
		public static final String WORK_ORDER_FEATURE_SETTINGS_TYPE = "workOrderFeatureSettingsType";
		public static final String WORK_ORDER_FEATURE_ALLOWED_STATE_ID = "workOrderAllowedStateId";
        public static final String WORK_ORDER_SETTINGS = "workOrderSettings";
		public static final String WORK_ORDER_FEATURE_SETTINGS_VALUES_MAP = "workOrderFeatureSettingsValuesMap";
		public static final String WORK_ORDER_FEATURE_SETTINGS_BASED_BANNER_MESSAGE = "workOrderFeatureSettingsBasedBannerMessage";
		public static final String WORK_ORDER_FEATURE_SETTINGS_HAS_EXECUTE_TASK_PERMISSION = "workOrderFeatureSettingHasExecuteTaskPermission";
		public static final String WORK_ORDER_FEATURE_SETTINGS_HAS_MANAGE_TASK_PERMISSION = "workOrderFeatureSettingHasManageTaskPermission";
		public static final String IS_LOCKED_STATE = "isLockedState";
		public static final String CURRENT_MODULE_STATE = "currentModuleState";
		public static final String NAME = "name";
		public static final String PATCH_FIELD_NAMES = "patchFieldNames";
		public static final String CURRENCY_CODE = "currencyCode";
		public static final String NEW_CURRENCY_CODE = "newCurrencyCode";
		public static final String OLD_CURRENCY_CODE = "oldCurrencyCode";
		public static final String BASE_CURRENCY = "baseCurrency";
		public static final String CURRENCY_MAP = "currencyMap";

		public static class HomePage {
			public static final String HOME_PAGE = "homepage";
			public static final String WIDGET_LINKNAME = "widgetLinkName";
			public static final String WIDGET_DATA = "widgetData";
			public static final String NUTSHLL_WIDGET = "nutshellwidget";
			public static final String ACTION_WIDGET = "groupedactioncard";

			public static final String RECENT_RESERVED_SPACE = "recentreservedspacecard";


		}

		public static final String SUMMARY = "summary";
		public static final String GLIMPSE = "glimpse";



		public static final String SITE_MAP_VIEW = "siteMapView";
		public static final String MARKUP_PREFERENCES = "markuppreferences";

		public static final String QUOTATIONSETTING = "quotationsetting";

		public static final String GLIMPSE_META_DATA = "glimpseMetaData";
		public static final String GLIMPSE_RECORD = "glimpseRecord";
		public static final String GLIMPSE_CONTEXT = "glimpseContext";
		public static final String GLIMPSE_FIELDS = "glimpseFields";
		public static final String MODULE_CONFIGURATION = "ModuleConfiguration";

		public static final String CUSTOMIZATION = "customization";

		public static final String VERIFY_USER = "verifyUser";
		public static final String RULE_LOG_MODULE_DATA = "ruleLogModuleData";

		public static final String OLD_STATE_FLOW = "oldstateflow";
		public static final String RULE_LOG_MODULE_DATA_LIST = "ruleLogModuleDataList";
		public static final String FIELD_ACCESS_TYPE = "fieldAccessType";
		public static final String OPERATION_ALARM = "operationalarm";
		public static final String OPERATION_OCCURRENCE = "operationalarmoccurrence";
		public static final String OPERATION_EVENT = "operationevent";
		public static final String PREVIOUS_SHORT_OF_EVENT = "previousshotofevent";
		public static final String PREVIOUS_EXCEED_EVENT = "previousexceedevent";
		public static final String PREVIOUS_EVENT = "previousevent";
		public static final String SCHEDULE_GENERATION_TIME = "scheduleGenerationTime";
		public static final String OLD_TASKS = "oldTasks";
		public static final String TASKS = "tasks";
		public static final String TASK_READINGS = "taskReadings";
		public static final String IS_USER_TRIGGER = "userTrigger";
		public static final String DO_NOT_EXECUTE = "donNotExecute";
		public static final String SERVICE_CATALOG = "serviceCatalog";
		public static final String SERVICE_CATALOGS = "serviceCatalogs";
		public static final String SERVICE_CATALOG_GROUP = "serviceCatalogGroup";
		public static final String SERVICE_CATALOG_GROUPS = "serviceCatalogGroups";
		public static final String SERVICE_CATALOG_GROUP_ORDER_BY = "serviceCatalogGroupOrderBy";
		public static final String LICENSE_NAME = "licenseName";
		public static final String AHU_READINGS_GENERAL = "ahureadinggeneral";

		public static final String LICENSE_TYPE = "licenseType";

		public static final String LICENSE_NEW_COUNT = "newCount";
		public static final String LICENSING_INFO = "licensingInfo";
		public static final String RESERVATION = "reservation";
		public static final String LICENSING_INFO_IDS = "licensingInfoIds";

		public static final String SUB_MODULES = "submodules";
		public static final String TO_UPDATE_CHILD_MAP = "toUpdateChildMap";
		public static final String CHILD_CRITERIA = "childCriteria";
		public static final String CONNECTION = "connectionContext";
		public static final String CONNECTION_NANE = "connectionName";
		public static final String OFFSET = "offset";
		public static final String TABLE_NAME = "tableName";
		public static final String CRITERIA = "criteria";
		public static final String DEFAULT="default";
		public static final String CLIENT_FILTER_CRITERIA = "clientFilterCriteria";
		public static final String WEB_TAB_GROUP = "webTabGroup";
		public static final String USE_ORDER_FROM_CONTEXT = "useOrderFromContext";
		public static final String WEB_TAB_GROUPS = "webTabGroups";
		public static final String WEB_TAB_WEB_GROUP = "webTabWebGroups";
		public static final String WEB_TAB_GROUP_ID = "webTabGroupId";
		public static final String WEB_TAB = "webTab";
		public static final String WEB_TABS = "webTabs";
		public static final String NEW_PERMISSIONS = "newPermission";
		public static final String NEW_TAB_PERMISSIONS = "newTabPermission";
		public static final String WEB_TAB_ID = "webTabId";
		public static final String WEB_TAB_TYPE = "webTabType";
		public static final String FACE_COLLECTIONS = "faceCollections";
		public static final String VISITOR_FACES = "visitorFaces";
		public static final String VISITOR_PHOTO = "visitorPhoto";
		public static final String ALL_PERMISSIONS = "*";
		public static final String ALL_VALUE = "*";

		public static final String SIGNATURE_CONTENT = "signatureContent";
		public static final String SIGNATURE_FILE_ID = "signatureFileId";
		public static final String SIGNATURE = "signature";
		public static final String SIGNATURE_DELETE = "signatureDelete";

		public static final String IS_MOBILE = "isMobile";

		public static final String IS_WEB = "isWeb";
		public static final String MY_APPS = "myApps";
		public static final String APPLICATION = "application";
		public static final String PORTALS = "portals";
		public static final String APPLICATION_LAYOUT = "applicationLayout";
		public static final String APPLICATION_ID = "applicationId";
		public static final String LAYOUT_APP_TYPE = "layoutAppType";
		public static final String FETCH_ALL_LAYOUTS = "fetchAllLayouts";
		public static final String ADD_APPLICATION_LAYOUT = "addApplicationLayout";
		public static final String LAYOUT_ID = "layoutId";
		public static final String CONSIDER_ROLE = "considerRole";

		public static final String FETCH_NON_APP_USERS = "fetchNonAppUsers";
		public static final String FILTER_SET_UP_TAP = "filterSetUpTab";
		public static final String FETCH_SETUP_TABS = "fetchSetupTabs";

		public static final String APP_ID = "appId";
		public static final String RESTRICT_PERMISSIONS = "restrictPermissions";
		public static final String TENANT_PORTAL_APP_ID = "tenantPortalappId";
		public static final String SERVICE_PORTAL_APP_ID = "servicePortalappId";
		public static final String VENDOR_PORTAL_APP_ID = "vendorPortalappId";
		public static final String CLIENT_PORTAL_APP_ID = "clientPortalappId";
		public static final String EMPLOYEE_PORTAL_APP_ID = "employeePortalappId";

		public static final String EMPLOYEE_PORTAL_ACCESS = "employeePortalAccess";

		public static final String CUSTOM_PAGE = "customPage";
		public static final String CUSTOM_PAGE_WIDGET = "customPageWidget";
		public static final String CUSTOM_WIDGET_GROUP = "customWidgetGroup";
		public static final String CUSTOM_WIDGET_GROUP_FIELDS = "customWidgetGroupFields";
		public static final String PAGE_ID = "pageId";
		public static final String WIDGET_NAME = "widgetName";
		public static final String SCOPING_CONFIG_LIST = "scopingConfigList";
        public static final String USER_SCOPING_LIST = "userScopingList";
        public static final String CREATED_BY = "createBy";

		public static class EmployeePortal {
			public static final String EMPLOYEE_ADMIN_SCOPING_ID = "employeeadminscopingid";
		}

		public static final String FETCH_FULL_FORM = "fetchFullForm";
		public static final String DONT_FETCH_WO_WITH_DELETED_RESOURCES = "dontFetchWOWithDeletedResources";

		public static final String AGENT = "agent";
		public static final String AGENT_DATA = "agentData";
		public static final String ROLL_UP_TYPE = "rollUpType";
		public static final String APPROVAL_STATUS = "approvalStatus";
		public static final String APPROVAL_FLOW_ID = "approvalFlowId";
		public static final String SYSTEM_CONTROLLER_MODULE_NAME = "systemController";
		public static final String CONTROLLER_READINGS_MODULE_NAME = "controllerReadings";
		public static final String AGGREGATION_META = "aggregationMeta";
		public static final String POSITION_TYPE = "positionType";
		public static final String CUSTOM_BUTTONS = "customButtons";
		public static final String GET_CUSTOM_BUTTONS = "getCustomButtons";
		public static final String CUSTOM_BUTTON_LIST = "customButtonList";

		public static final String TRIGGER = "trigger";
		public static final String TRIGGER_TYPE = "triggerType";
		public static final String TYPE_PRIMARY_ID = "typePrimaryId";
		public static final String GLOBAL_VARIABLE_GROUP_LIST = "globalVariableGroupList";
		public static final String GLOBAL_VARIABLE_GROUP = "globalVariableGroup";
		public static final String GLOBAL_VARIABLE_LIST = "globalVariableList";
		public static final String GLOBAL_VARIABLE = "globalVariable";
		public static final String USER_DELEGATION = "userDelegation";
		public static final String TIMELINE_REQUEST = "timelineRequest";
		public static final String TIMELINE_AGGREGATE_DATA = "timelineAggregateData";
		public static final String TIMELINE_V3_DATAMAP = "timelineV3Datamap";
		public static final String TIMELINE_CUSTOMIZATONDATA_MAP = "timelineCustomizationDataMap";
		public static final String TIMELINE_GET_UNSCHEDULED_DATA = "timelineGetUnscheduled";
		public static final String TIMELINE_DATA = "timelineData";
		public static final String TIMELINE_PATCHTYPE = "patchType";
		public static final String TIMELINE_PATCHTYPE_RESCHEDULE = "rescheduled";
		public static final String TIMELINE_PATCHTYPE_REASSIGN = "reassigned";
		public static final String TIMELINE_PATCHTYPE_GROUPASSIGN = "assigned a group";
		public static final String FACILIO_BANNER = "facilioBanner";
		public static final String FACILIO_BANNERS = "facilioBanners";
		public static final String EMAIL_STRUCTURES = "emailStructures";

		public static final String EMAIL_STRUCTURE = "emailStructure";
		public static final String WITH_QRURL = "withQrUrl";
		public static final String PDF_TEMPLATE = "pdfTemplate";
		public static final String PDF_TEMPLATES = "pdfTemplates";

		public static final String PDF_TEMPLATE_HTML = "pdfTemplateHTML";

		public static class DataProcessor {
			public static final String UNMODELED = "unmodeled";
			public static final String POINT_RECORDS = "pointRecords";
			public static final String DATA_SNAPSHOT = "dataSnapshot";
		}

		public static class Reservation {
			public static final String RESERVATION = "reservation";
			public static final String RESERVATION_LIST = "reservations";
			public static final String RESERVATIONS_INTERNAL_ATTENDEE = "reservationInternalAttendee";
			public static final String RESERVATIONS_EXTERNAL_ATTENDEE = "reservationExternalAttendee";
		}

		public static final String FIELDS = "fields";
		public static final String FIELD = "field";
		public static final String TO_UPDATE_MAP = "toUpdateMap";
		public static final String TO_INSERT_MAP = "toInsertMap";
		public static final String BULK_DATA = "BULK_DATA";
		public static final String FEDGE_ZIP = "/fedge.zip";
		public static final String INNER_JOIN = "innerJoin";
		public static final String ON_CONDITION = "onCondition";
		public static final String AGGREGATOR = "AGGREGATOR";

		public static final String FACILIO_RECORD = "facilioRecord";
		public static final String FACILIO_CONSUMER = "facilioConsumer";
		public static final String FACILIO_PRODUCER = "facilioProducer";

		public static final String KINESIS_RECORD = "kinesisRecord";
		public static final String KINESIS_CHECK_POINTER = "kinesisCheckPointer";
		public static final String READING_TEMPLATE_MODULE = "readingtemplate";
		public static final String SIGNUP_INFO = "signupinfo";
		public static final String WITH_CHANGE_SET = "withChangeSet";
		public static final String CHANGE_SET = "changeSet";
		public static final String CHANGE_SET_MAP = "changeSetMap";
		public static final String PROPAGATE_ERROR = "propagateError";
		public static final String RECORD = "record";
		public static final String REVISED_RECORD = "revisedRecord";
		public static final String PARENT_RECORD = "parentRecord";
		public static final String PARENT_RECORD_ID = "parentRecordId";
		public static final String CONVERSATION_ID = "conversationId";
		public static final String MAIL_CONVERSION_TYPE = "mailConversionType";
		public static final String RECORD_LIST = "records";
		public static final String RECORD_MAP = "recordMap";
		public static final String RECORD_LIST_MAP = "recordListMap";

		public static final String RECORD_ID = "recordId";
		public static final String MODULE_ID = "moduleId";
		public static final String RECORD_ID_LIST = "recordIds";
		public static final String RELATED_APPLICATIONS = "relatedApplications";
		public static final String APPLICATION_RELATED_APPS_LIST = "applicationRelatedAppsList";
		public static final String IS_FROM_SUMMARY = "isFromSummary";
		public static final String IS_FROM_VIEW = "isFromView";
		public static final String PM_INCLUDE_EXCLUDE_LIST = "pmIcludeExcludeList";
		public static final String IS_BULK_EXECUTE_COMMAND = "isbulkExecuteCommand";
		public static final String FETCH_COUNT = "fetchCount";
		public static final String FETCH_SELECTED_FIELDS = "fetchSelectedFields";
		public static final String FETCH_CUSTOM_FIELDS = "fetchCustomFields";
		public static final String FETCH_AS_MAP = "fetchAsMap";
		public static final String RECORD_COUNT = "recordCount";
		public static final String ROWS_UPDATED = "rowsUpdated";
		public static final String ROWS_ADDED = "rowsAdded";
		public static final String EVENT_TYPE = "eventType";
		public static final String EVENT_TYPE_LIST = "eventTypeList";
		public static final String IS_BULK_ACTION = "isBulkAction";
		public static final String ACTIVITY_LIST = "activityList";
		public static final String CURRENT_ACTIVITY = "currentActivity";
		public static final String CURRENT_WO_ACTIVITY = "currentWorkorderActivity";
		public static final String MODIFIED_TIME = "modifiedTime";
		public static final String REGRESSION_CONFIG = "regressionConfig";
		public static final String REGRESSION_RESULT = "regressionResult";
		public static final String REGRESSION_REPORT = "regressionReport";
		public static final String REPORT_TEMPLATE = "reportTemplate";
		public static final String FETCH_SUPPLEMENTS = "fetchSupplement";
		public static final String SELECTABLE_FIELDS = "selectableFields";
		public static final String EXTRA_SELECTABLE_FIELDS = "extraSelectableFields";
		public static final String EXTRA_ADD_FIELDS = "extraAddFields";


		public static final String CONNECTEDAPP_SAML_LIST = "connectedAppSAMLList";

		public static final String TIMEOUT = "timeout";

		public static final String TENANT_STATUS = "tenantStatus";
		public static final String USER_ID = "userId";
		public static final String USER = "user";
		public static final String IS_PORTAL_USER = "isPortalUser";
		public static final String ADD_APP_ACCESS = "addAppAccess";
		public static final String IS_DELETED_USER = "isDeletedUser";

		public static final String USER_OPERATION = "userOperation";
		public static final String USER_STATUS = "userStatus";
		public static final String USER_MOBILE_SETTING = "userMobileSetting";
		public static final String OLD_FCM_TOKEN = "oldFcmToken";
		public static final String NEW_FCM_TOKEN = "newFcmToken";
		public static final String ACCESSIBLE_SPACE = "accessibleSpace";
		public static final String USER_SHIFT_READING = "usershiftreading";
		public static final String USER_WORK_HOURS_READINGS = "userworkhoursreading";
		public static final String PASSWORD = "password";
		public static final String USER_IDS ="userIds";
		public static final String PEOPLE_IDS ="peopleIds";


		public static final String GROUP_ID = "groupId";
		public static final String GROUP = "group";
		public static final String GROUP_MEMBER_IDS = "groupMembers";

		public static final String ROLE_ID = "roleId";
		public static final String ROLE = "role";
		public static final String ROLES_APPS = "rolesApps";
		public static final String PERMISSIONS = "permissions";
		public static final String BOOL_PERMISSIONS = "bool_permissions";
		public static final String CHECK_BOOL = "checkBool";

		public static final String BUSINESS_HOUR_IDS = "businesshourids";
		public static final String BUSINESS_HOUR = "businesshours";
		public static final String BUSINESS_HOUR_LIST = "businesshourlist";

		public static final String DRAFT = "draft";
		public static final String ID = "Id";
		public static final String STARTING_NUMBER = "startingNumber";
		public static final String LINK_NAME = "linkName";
		public static final String PARENT_ID = "parentId";
		public static final String PARENT_NOTE_ID = "parentNoteId";
		public static final String PREV_PARENT_ID = "prevParentId";
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

		public static final String ACCOUNT_TYPE = "accounttype";
		public static final String POINTS = "points";
		public static final String COMMISSIONING_LOG = "commissioninglog";

		public static final String AGENT_DATA_LOGGER = "agentDataLogger";

		public static final String AGENT_DATA_PROCESSING_LOGGER = "agentDataProcessingLogger";
		public static final String WORK_ORDER_LIST = "workorders";
		public static final String BULK_WORK_ORDER_CONTEXT = "bulkworkorders";
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
		public static final String WORKORDER_ID = "workOrderId";

		public static final String PERMALINK_FOR_URL = "permalinkForUrl";
		public static final String PERMALINK_TOKEN_FOR_URL = "permalinkTokenForUrl";
		public static final String USER_EMAIL = "userEmail";
		public static final String ADMIN_USER_EMAIL = "email";
		public static final String SESSION = "session";
		public static final String TOKEN = "token";
		public static final String IDENTIFIER = "identifier";

		public static final String TEMPLATE_ID = "templateId";
		public static final String TEMPLATE_TYPE = "templateType";
		public static final String TEMPLATE_NAME = "templateName";
		public static final Object TEMPLATE_LIST = "templateList";
		public static final Object TEMPLATE = "template";
		public static final String FORM_SHARING = "formSharing";
		public static final String DEFAULT_TEMPLATE = "default_template";
		public static final String WORK_ORDER_TEMPLATE = "workordertemplate";
		public static final String WORK_ORDER_TEMPLATE_LIST = "workordertemplates";

		public static final String FILTERED_NAME = "filteredName";

		public static final String PLACE_HOLDER = "placeHolder";
		public static final String IS_EDIT_TEMPLATE = "isEditTemplate";

		public static final String CONTROLLER_ID = "controllerId";
		public static final String CONTROLLER = "controller";
		public static final String CONTROLLER_LEVEL = "controllerLevel";
		public static final String CONTROLLER_TIME = "controllerTime";
		public static final String CONTROLLER_ACTIVITY_WATCHER = "controllerActivityWatcher";
		public static final String CONTROLLER_LIST = "controllerList";
		public static final String CONTROLLER_SETTINGS = "controllerSettings";
		public static final String FEDGE_CERT_FILE_ID = "fedgeCertFileId";
		public static final String OLD_CONTROLLER = "oldController";
		public static final String WORK_ORDER_REQUEST = "workorderrequest";
		public static final String WORK_ORDER_REQUEST_LIST = "workorderrequests";
		public static final String WORK_ORDER_REQUEST_COUNT = "workorderrequestcount";
		public static final String APPROVAL = "approval";

		public static final String ALARM_SEVERITY = "alarmseverity";
		public static final String ALARM = "alarm";
		public static final String RELATED_ALARMS = "relatedAlarms";
		public static final String READING_ALARM = "readingalarm";
		public static final String IS_RCA = "isRca";
		public static final String ML_ALARM = "mlalarm";
		public static final String ML_RCA_ALARMS = "mlRcaAlarms";
		public static final String ML_ALARM_OCCURRENCE = "mlalarmoccurrence";
		public static final String ALARM_LIST = "alarms";
		public static final String ALARM_ENTITY_ID = "alarmentityid";
		public static final String ALARM_OCCURRENCE = "alarmoccurrence";
		public static final String ALARM_OCCURRENCE_ID = "alarmoccurrenceId";
		public static final String RCA_ALARMS = "rcaAlarm";
		public static final String ANOMALY_ALARM_OCCURRENCE = "anomalyalarmoccurrence";
		public static final String BMS_ALARM_OCCURRENCE = "bmsalarmoccurrence";
		public static final String READING_ALARM_OCCURRENCE = "readingalarmoccurrence";
		public static final String VIOLATION_ALARM_OCCURRENCE = "violationalarmoccurrence";
		public static final String LATEST_ALARM_OCCURRENCE = "latestAlarmOccurrence";
		public static final String AGENT_ALARM = "agentAlarm";
		public static final String AGENT_ALARM_OCCURRENCE = "agentAlarmOccurrence";
		public static final String BASE_ALARM = "basealarm";
		public static final String BASE_EVENT = "baseevent";
		public static final String NEW_READING_ALARM = "newreadingalarm";
		public static final String READING_EVENT = "readingevent";
		public static final String IS_ALARM_CREATED = "isalarmcreated";
		public static final String READING_ALARM_CATEGORY = "readingalarmcategory";
		public static final String ONLY_PREQUISITE_READINGS_PRESENT = "onlyPrequisiteReadingsPresent";
		public static final String BMS_EVENT = "bmsevent";
		public static final String BMS_ALARM = "bmsalarm";
		public static final String VIOLATION_EVENT = "violationevent";
		public static final String VIOLATION_ALARM = "violationalarm";
		public static final String IS_INCLUDE = "isInclude";
		public static final String IS_SCALED_FLOW = "isScaledFlow";
		public static final String ALARM_COUNT = "alarmCount";
		public static final String CARD_CONTEXT = "cardContext";
		public static final String CARD_ID = "cardId";
		public static final String CARD_FILTERS = "cardFilters";
		public static final String CARD_USER_FILTERS = "cardUserFilters";
		public static final String CARD_CUSTOM_SCRIPT_FILTERS = "cardCustomScriptFilters";

		public static final String CARD_RETURN_VALUE = "cardReturnValue";
		public static final String CARD_LAYOUT = "cardLayout";
		public static final String CARD_STATE = "cardState";
		public static final String ALARM_ACTIVITY = "alarmActivity";
		public static final String PRE_ALARM = "prealarm";
		public static final String PRE_ALARM_OCCURRENCE = "prealarmoccurrence";
		public static final String PRE_EVENT = "preevent";
		public static final String IS_PRE_EVENT = "is_pre_event";
		public static final String ML_ANOMALY_ALARM = "mlAnomalyAlarm";
		public static final String FLOORPLAN_VIEW_CONTEXT = "floorPlanViewContext";
		public static final String RULE_ROLLUP_ALARM = "rulerollupalarm";
		public static final String RULE_ROLLUP_OCCURRENCE = "rulerollupoccurrence";
		public static final String RULE_ROLLUP_EVENT = "rulerollupevent";
		public static final String ASSET_ROLLUP_ALARM = "assetrollupalarm";
		public static final String ASSET_ROLLUP_OCCURRENCE = "assetrollupoccurrence";
		public static final String ASSET_ROLLUP_EVENT = "assetrollupevent";
		public static final String SENSOR_ALARM = "sensoralarm";
		public static final String SENSOR_ALARM_OCCURRENCE = "sensoralarmoccurrence";
		public static final String SENSOR_EVENT = "sensorevent";
		public static final String SENSOR_ROLLUP_ALARM = "sensorrollupalarm";
		public static final String SENSOR_ROLLUP_ALARM_OCCURRENCE = "sensorrollupalarmoccurrence";

		public static final String ASSET_CATEGORY_FEATURE_ACTIVATION="assetCategoryFeature";
		public static final String SENSOR_ROLLUP_EVENT = "sensorrollupevent";
		public static final String MULTIVARIATE_ANOMALY_ALARM = "multivariateanomalyalarm";
		public static final String MULTIVARIATE_ANOMALY_ALARM_OCCURRENCE = "multivariateanomalyalarmoccurrence";
		public static final String MULTIVARIATE_ANOMALY_EVENT = "multivariateanomalyevent";

		// public static final String ML_USECASE_ID = "mlUsecaseId";
		public static final String ML_SERVICE_DATA = "mlServiceData";
		// public static final String ML_MODEL_NAME = "modelName";
		// public static final String ML_FILTER = "filteringMethod";
		// public static final String ML_GROUP = "groupingMethod";
		// public static final String ML_VARIABLES = "mlVariables";

		public static final String EVENT = "event";
		public static final String IS_NEW_EVENT = "isNewEvent";

		public static final String INVENTORY = "inventory";
		public static final String INVENTORY_LIST = "inventories";
		public static final String INVENTORY_VENDOR = "inventoryvendor";
		public static final String INVENTORY_VENDORS = "inventory_vendors";
		public static final String INVENTORY_VENDOR_LIST = "inventoryvendors";
		public static final String INVENTORY_CATEGORY = "inventoryCategory";

		public static final String TASK = "task";

		public static final String IS_TASK_ACTION_EXECUTED = "isTaskActionExecuted";
		public static final String TASK_ERRORS = "taskErrors";
		public static final String REQUIRES_REMARKS = "requiresRemarks";
		public static final String REQUIRES_ATTACHMENT = "requiresAttachment";
		public static final String RELOAD_WORK_ORDER = "reloadWorkOrder";

		public static final String HAS_TASK_ERRORS = "hasTaskErrors";
		public static final String TASK_LIST = "tasks";
		public static final String TASK_MAP = "taskMap";
		public static final String TASK_SECTIONS = "taskSections";
		public static final String TASK_SECTION_TEMPLATES = "taskSectionTemplates";
		public static final String PREREQUISITE_APPROVER_TEMPLATES = "prerequisiteApproverTemplates";
		public static final String TASK_SECTION = "taskSection";
		public static final String TASK_SECTION_MODULE = "tasksection";
		public static final String DEFAULT_TASK_SECTION = "default";

		public static final String PRE_REQUEST_STATUS = "prerequestStatus";
		public static final String PRE_REQUEST_MAP = "preRequestMap";
		public static final String PRE_REQUEST_LIST = "preRequests";
		public static final String PRE_REQUEST_SECTIONS = "preRequestSections";
		public static final String PREREQUISITE_APPROVERS = "prerequisiteApprovers";
		public static final String PREREQUISITE_APPROVERS_LIST = "prerequisiteApproversList";

		public static final String ATTACHMENT = "attachment";
		public static final String ATTACHMENT_LIST = "attachments";
		public static final String EXISTING_ATTACHMENT_LIST = "existingAttachments";
		public static final String ATTACHMENT_CONTEXT_LIST = "attachmentsContextList";
		public static final String ATTACHMENT_FILE_LIST = "attachmentFiles";
		public static final String ATTACHMENT_URL_LIST = "attachmentFilesUrl";
		public static final String ATTACHMENT_FILE_FIELDID_LIST = "attachmentFileFieldIds";
		public static final String ATTACHMENT_CONTENT_TYPE = "attachmentContentType";
		public static final String ATTACHMENT_TYPE = "attachmentType";
		public static final String ATTACHMENT_FILE_NAME = "attachmentFileName";
		public static final String ATTACHMENT_ID_LIST = "attachmentIds";
		public static final String ATTACHMENT_MODULE_NAME = "attachmentModuleName";
		public static final String ATTACHMENTS_MODULE_NAME = "attachmentsModuleName";

		public static final String ATTACHMENT_MAP_FILE_LIST = "attachmentsMapFiles";
		public static final String CURRENT_TIME = "currentTime";

		public static final String FILE = "file";
		public static final String FILE_NAME = "fileName";

		public static final String MULTI_MODULE_IMPORT = "multiModuleImport";
		public static final String IMPORT_ID = "importId";
		public static final String IMPORT_FILE_ID = "importFileId";

		public static final String IMPORT_SHEET_LIST = "importSheetList";
		public static final String MULTI_IMPORT_PROCESS_CONTEXT = "multiImportProcessContext";
		public static final String PUBLIC_FILE = "publicFile";
		public static final String PUBLIC_FILE_URL = "publicFileUrl";
		public static final String FILE_CONTENT_TYPE = "fileContentType";
		public static final String FILE_CONTEXT_LIST = "fileContextList";
		public static final String FILE_RESPONSE_STATUS = "fileResponseStatus";
		public static final String RESPONSE_STATUS = "responseStatus";
		public static final String FILE_DOWNLOAD_STREAM = "fileDownloadStream";

		public static final String EXPIRY_ON = "expiryOn";
		public static final String IS_BIM = "isBim";
		public static final String VALID_SHEETS = "validSheets";
		public static final String SELECTED_SHEET_NAMES = "selectedSheetNames";
		public static final String SHEET_NAME = "sheetName";
		public static final String BIM_IMPORT_ID = "bimImportId";

		public static final String LOGO = "logo";

		public static final String ACTION_FORM = "actionForm";

		public static final String VIEW_GROUP_TYPE = "viewGroupType";
		public static final String VIEW_TYPE = "viewType";
		public static final String GET_ONLY_BASIC_VIEW_DETAILS = "getOnlyBasicViewDetails";

		public static final String MODULE_NAME = "moduleName";
		public static final String IS_HIDDEN_MODULE = "isHiddenModule";

		public static final String SUB_MODULE_NAME = "subModuleName";
		public static final String DATA_MODULE_NAME = "dataModuleName";
		public static final String SUMMARY_MODULE_NAME = "summaryModuleName";
		public static final String MODULE_SETTING = "setting";
		public static final String MODULE_SETTING_NAME = "settingName";
		public static final String MODULE_DESCRIPTION = "moduleDescription";
		public static final String MODULE_DISPLAY_NAME = "moduleDisplayName";
		public static final String DUPLICATE_OBJECT = "duplicateObj";
		public static final String STATE_FLOW_ENABLED = "stateFlowEnabled";
		public static final String FAILURE_REPORTING_ENABLED = "failureReportingEnabled";
		public static final String MODULE_DATA_TABLE_NAME = "moduleDataTable";
		public static final String MODULE_DATA = "moduleData";
		public static final String MODULE_DATA_LIST = "moduleDatas";
		public static final String MODULE_ATTACHMENT_TABLE_NAME = "moduleAttachmentTable";
		public static final String MODULE_TYPE = "moduleType";
		public static final String FETCH_DEFAULT_MODULES = "fetchDefaultModules";
		public static final String MODULE_FIELD_COUNT = "moduleFieldCount";
		public static final String MODULE_DATA_INTERVAL = "moduleDataInterval";
		public static final String PARENT_MODULE = "parentModule";
		public static final String PARENT_MODULE_NAME = "parentModuleName";
		public static final String READING_NAME = "readingName";
		public static final String IS_SPECIAL_MODULE = "isSpecialModule";
		public static final String TOTAL_CURRENT_OCCUPANCY = "currentOccupancy";
		public static final String MODULE_CHANGE_TO = "moduleChangeTo";

		public static final String OVER_RIDE_READING_SPLIT = "overRideReadingSplit";
		public static final String MODULE = "module";

		public static final String REPLACED_JSON = "replacedJson";

		public static final String ENCODED_REPLACED_STRING = "encodedReplacedString";
		public static final String IS_NEW_MODULES = "isNewModule";
		public static final String IS_SKIP_COUNTER_FIELD_ADD = "skipCounterFieldAdd";
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
		public static final String SPACE_UPDATE = "spacesUpdate";
		public static final String FETCH_EXTENDED_MODULE_FORMS = "fetchExtendedModuleForms";
		public static final String FETCH_FORM_RULE_FIELDS = "fetchFormRuleFields";
		public static final String FETCH_DISABLED_FORMS = "fetchDisabledForms";
		public static final String FETCH_HIDDEN_FORMS = "fetchHiddenForms";
		public static final String IS_BEFORE = "isbefore";
		public static final String DURATION = "duration";
		public static final String TYPE = "type";

		public static final String READINGS_MAP = "readingsMap";
		public static final String READINGS = "readings";
		public static final String READING = "reading";
		public static final String READINGS_SOURCE = "readingsSource";
		public static final String READING_FIELDS = "readingFields";
		public static final String READING_FIELD = "readingField";
		public static final String APPEND_MODULE_NAME = "appendModuleName";
		public static final String PHOTOS = "photos";
		public static final String PHOTO = "photo";
		public static final String PHOTO_ID = "photoId";
		public static final String PHOTO_TEXTS = "photoTexts";
		public static final String PREVIOUS_VALUE = "previousValue";
		public static final String ONLY_READING = "onylyReading";
		public static final String ADJUST_READING_TTIME = "adjustReadingTtime";
		public static final String KPI = "kpi";
		public static final String KPI_LIST = "kpis";
		public static final String KPI_CATEGORY = "kpiCategory";
		public static final String FORK_POST_READING_PROCESSING = "forkPostReadingProcessing";
		public static final String TIMELOGS = "Timelogs";
		public static final String OFFLINE_STATE_TRANSITION = "offlineStateTransition";

		public static final String DASHBOARD = "dashboard";
		public static final String IS_FROM_REPORT = "isFromReport";
		public static final String DASHBOARD_TAB = "dashboardTab";
		public static final String DASHBOARD_TABS_LIST = "dashboardTabsList";
		public static final String DASHBOARD_TAB_ID = "dashboardTabId";
		public static final String DASHBOARDS = "dashboards";
		public static final String DASHBOARD_ID = "dashboardId";
		public static final String DASHBOARD_ERROR_MESSAGE = "dashboardErrorMessage";
		public static final String DASHBOARD_FOLDERS = "dashboardFolders";
		public static final String DASHBOARD_FOLDER = "dashboardFolder";
		public static final String DASHBOARD_PUBLISH_STATUS = "dashboardPublishStatus";
		public static final String DASHBOARD_FILTER = "dashboardFilter";
		public static final String DASHBOARD_USER_FILTER_ID = "dashboardUserFilterId";
		public static final String DASHBOARD_WIDGET_TIMELINE_FILTER = "widgetTimelineFilter";
		public static final String WIDGET = "widget";

		public static final String WIDGET_UPDATE_LIST = "widgetUpdateList";
		public static final String WIDGET_STATIC_CONTEXT = "widgetStaticContext";
		public static final String REPORT_SPACE_FILTER_CONTEXT = "reportSpaceFilterContext";
		public static final String WIDGET_TYPE = "widgetType";
		public static final String IS_SKIP_LINKNAME_CHECK = "isSkip";
		public static final String DASHBOARD_SHARING = "dashboardSharing";

		public static final String WIDGET_ID = "widgetId";
		public static final String WIDGET_LINK_NAME = "widgetLinkName";
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
		public static final String HEATMAP_AGGR = "hmAggr";
		public static final String REPORT_GROUP_BY_TIME_AGGR = "groupByTimeAggr";
		public static final String REPORT_Y_AGGR = "yAggr";
		public static final String REPORT_DATA = "reportData";
		public static final String PIVOT_TABLE_DATA = "pivotTableData";
		public static final String PIVOT_TEMPLATE_JSON = "pivotTemplateJSON";
		public static final String PIVOT_RECONSTRUCTED_DATA = "pivotReconstructedData";
		public static final String SHOW_TIME_LINE_FILTER = "showTimelineFilter";
		public static final String PIVOT_ALIAS_VS_FIELD = "pivotAliasVsField";
		public static final String PIVOT_LOOKUP_MAP = "pivotLookupMap";
		public static final String PIVOT_DRILL_DOWN = "drillDown";
		public static final String PIVOT_DRILL_DOWN_PATTERN = "drillDownPattern";
		public static final String PIVOT_DRILL_DOWN_FIELDS = "pivotDrillDownFields";
		public static final String PIVOT_DRILL_DOWN_OPERATORS = "pivotDrillDownOperators";
		public static final String IS_TIMELINE_FILTER_APPLIED = "isTimelineFilterApplied";
		public static final String IS_EXPORT_REPORT = "isExportReport";
		public static final String TABLE_ALIAS = "tableAlias";
		public static final String ROW_HEADERS = "rowHeaders";
		public static final String DATA_HEADERS = "dataHeaders";
		public static final String ROW_ALIAS = "rowAlias";
		public static final String DATA_ALIAS = "dataAlias";
		public static final String FORMATTING = "formatting";
		public static final String GET_MODULE_FROM_DP = "getModuleFromDp";
		public static final String REPORT_HANDLE_BOOLEAN = "reportHandleBoolean";
		public static final String SHOULD_INCLUDE_MARKED = "shouldIncludeMarked";
		public static final String ALLOW_FUTURE_DATA = "allowFutureData";
		public static final String REPORT_FROM_ALARM = "reportFromAlarm";
		public static final String ALARM_RESOURCE = "alarmResource";
		public static final String FETCH_EVENT_BAR = "fetchEventBar";
		public static final String FETCH_ALARM_INFO = "fetchAlarmInfo";
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
		public static final String REPORT_SCATTER_CONFIG = "scatterConfig";
		public static final String REPORT_TTIME_FILTER = "ttimeFilter";
		public static final String REPORT_DRILLDOWN_PARAMS = "reportDrilldownParams";
		public static final String TOTAL_CONSUMPTION = "totalConsumption";
		public static final String UNIT = "unit";

		public static final String AGGR_KEY = "aggr";
		public static final String DATA_KEY = "data";
		public static final String FORMULA = "formula";
		public static final String FORMULA_HEADERS = "formulaHeaders";

		public static final String VALUES = "values";
		public static final String IS_BUILDER_V2 = "isBuilderV2";
		public static final String PIVOT_EXTENDED_MODULE_IDS = "pivotExtendedModuleIds";

		public static final String LABEL_MAP = "labelMap";

		public static final String REPORT_CALLING_FROM = "reportCallingFrom";

		public static final String NOTE = "note";
		public static final String NOTE_ID = "noteId";
		public static final String NOTE_MAP = "notesMap";
		public static final String NOTE_LIST = "notes";
		public static final String COMMENT_ATTACHMENTS = "commentattachments";
		public static final String NEED_COMMENT_SHARING = "needCommentSharing";
		public static final String NOTES_LIST = "notesList";
		public static final String COMMENT = "comment";
		public static final String NOTIFY_REQUESTER = "notifyRequester";
		public static final String COMMENT_SHARING_PREFERENCES = "commentSharingPreferences";
		public static final String COMMENT_SHARING_OPTIONS = "commentSharingOptions";

		public static final String LICENSED_PORTAL_APPS = "licensedPortalApps";

		public static final String NOTIFICATION_TYPE = "notificationType";
		public static final String NOTIFICATION_OBJECT = "notificationObject";

		public static final String MODULE_FIELD = "moduleField";
		public static final String MODULE_FIELD_NAME = "fieldName";
		public static final String GLIMPSE_FIELD_NAME = "glimpseFieldName";
		public static final String MODULE_FIELD_LIST = "moduleFields";
		public static final String MODULE_FIELD_MAP = "moduleFieldMap";
		public static final String EXISTING_FIELD_LIST = "existingFields";
		public static final String FORM_FIELD_MAP = "formFieldMap";
		public static final String FIELD_FORM_USAGE_DETAILS = "formUsageDetails";
		public static final String FIELD_NAME_LIST = "fieldList";
		public static final String DEFAULT_FIELD = "defaultField";
		public static final String MODULE_FIELD_IDS = "moduleFieldIds";

		public static final String FIELD_MATCHER="fieldMatcher";
		public static final String CHECK_FIELD_DISPLAY_NAME_DUPLICATION = "avoidFieldDisplayNameDuplication";
		public static final String FIELD_ID = "fieldId";
		public static final String PREV_FIELD_ID = "prevFieldId";
		public static final String LOOKUP_FIELD_META_LIST = "lookupFieldMetaList";
		public static final String FETCH_LOOKUPS = "fetchLookups";
		public static final String FETCH_MAIN_FIELDS = "fetchMainFields";
		public static final String FETCH_CUSTOM_LOOKUPS = "fetchCustomLookups";
		public static final String FETCH_FIELD_DISPLAY_NAMES = "fetchFieldDisplayNames";
		public static final String ALLOW_SAME_FIELD_DISPLAY_NAME = "changeFieldDisplayName";
		public static final String CHILD_FIELD_ID = "childFieldId";
		public static final String CHILD_MODULE_ID = "childModuleId";
		public static final String CHILD_CRITERIA_ID = "childCriteriaId";
		public static final String AGGREGATE_FUNCTION_ID = "aggregateFunctionId";
		public static final String AGGREGATE_FIELD_ID = "aggregateFieldId";
		public static final String PARENT_MODULE_ID = "parentModuleId";
		public static final String PARENT_ROLLUP_FIELD_ID = "parentRollUpFieldId";
		public static final String MODULE_CRITERIA_MAP = "moduleCriteriaMap";
		public static final String ROLL_UP_FIELD_IDS = "rollUpFieldIds";
		public static final String ROLL_UP_FIELDS = "rollUpFields";
		public static final String GEOLOCATION_FIELDS = "geoLocationFields";

		public static final String SITE = "site";
		public static final String SITE_LIST = "sites";
		public static final String SITE_ID = "siteId";
		public static final String SITE_IDS = "siteIds";
		public static final String REPORT_CARDS = "reportCards";
		public static final String REPORT_CARDS_META = "reportCardsMeta";
		public static final String REPORTS = "reports";
		public static final String REPORT_CONTEXT = "reportContext";
		public static final String REPORT_ID = "reportId";
		public static final String REPORT_INFO = "reportInfo";
		public static final String ANALYTICS_TYPE = "analyticsType";
		public static final String CREATE="create";
		public static final String UPDATE="update";
		public static final String DELETE="delete";
		public static final String UNSAVED_REPORT = "unsavedReport";
		public static final String READING_REPORT_EDIT = "readingReportEdit";
		public static final String READING_REPORT = "readingReport";
		public static final String MODULE_REPORT = "modulereport";
		public static final String ALARM_REPORT = "alarmReport";
		public static final String TOTAL_AREA = "totalArea";

		public static final String BUILDING = "building";
		public static final String BUILDING_LIST = "buildings";
		public static final String BUILDING_ID = "buildingId";
		public static final String BUILDING_IDS = "buildingIds";

		public static final String FLOOR = "floor";
		public static final String FLOOR_LIST = "floors";
		public static final String FLOOR_ID = "floorId";
		public static final String SPACE_MODULE_NAME = "spacemodulename";
		public static final String SPACE_TABLE_NAME = "spacetablename";
		public static final String SPACE = "space";
		public static final String SPACE_LIST = "spaces";
		public static final String SPACE_ID = "spaceId";
		public static final String SPACE_ID1 = "spaceId1";
		public static final String SPACE_ID2 = "spaceId2";
		public static final String SPACE_ID3 = "spaceId3";
		public static final String SPACE_ID4 = "spaceId4";
		public static final String SPACE_ID5 = "spaceId5";
		public static final String SPACE_AMENITY = "spaceAmenity";
		public static final String FETCH_DELETED_RECORDS = "FetchDeletedRecords";
		public static final String SPACE_TYPE = "spaceType";
		public static final String SPACE_TYPE_ENUM = "spaceTypeEnum";
		public static final String IS_ZONE = "isZone";

		public static final String SPACE_CATEGORY = "spacecategory";

		public static final String FAILURE_CLASS = "failureclass";
		public static final String FAILURE_CODE = "failurecode";
		public static final String FAILURE_CODE_PROBLEMS = "failurecodeproblems";
		public static final String FAILURE_CODE_CAUSES = "failurecodecauses";
		public static final String FAILURE_CODE_REMEDIES = "failurecoderemedies";

		public static final String SPACE_CATEGORY_FIELD = "spaceCategory";

		public static final String ZONE = "zone";
		public static final String ZONE_LIST = "zones";
		public static final String ZONE_ID = "zoneId";
		public static final String IS_TENANT_ZONE = "tenantzone";

		public static final String SKILL = "skill";
		public static final String SKILL_LIST = "skills";

		public static final String RESOURCE = "resource";
		public static final String ASSIGNMENT = "assignment";
		public static final String RESOURCE_LIST = "resourceList";
		public static final String FETCH_RESOURCE_DETAIL = "fetchresourcedetail";

		public static final String BASE_SPACE_LIST = "basespaces";
		public static final String BASE_SPACE = "basespace";
		public static final String BASE_SPACE_ID = "basespaceId";

		public static final String ASSET = "asset";
		public static final String ASSET_ID = "assetId";
		public static final String ASSET_IDS = "assetIds";
		public static final String ASSET_LIST = "assets";
		public static final String ASSET_TYPE = "assettype";
		public static final String ASSET_CATEGORY = "assetcategory";
		public static final String ASSET_CATEGORY_ID = "assetcategoryId";
		public static final String ASSET_DEPARTMENT = "assetdepartment";
		public static final String ENERGY_METER = "energymeter";
		public static final String ASSET_SPARE_PARTS = "assetSpareParts";
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

		public static final String CONTROLLABLE_ASSET_CATEGORY = "controllableassetcategory";

		public static final String ASSET_BREAKDOWN = "assetbreakdown";
		public static final String BACNET_IP_CONTROLLER_MODULE_NAME = "bacnetipcontroller";
		public static final String NIAGARA_CONTROLLER_MODULE_NAME = "niagaracontroller";
		public static final String LON_WORKS_CONTROLLER_MODULE_NAME = "lonworkscontroller";
		public static final String OPC_XML_DA_CONTROLLER_MODULE_NAME = "opcxmldacontroller";
		public static final String OPC_UA_CONTROLLER_MODULE_NAME = "opcuacontroller";
		public static final String MODBUS_TCP_CONTROLLER_MODULE_NAME = "modbustcpcontroller";
		public static final String MODBUS_RTU_CONTROLLER_MODULE_NAME = "modbusrtucontroller";
		public static final String RTU_NETWORK = "rtuNetwork";
		public static final String MISC_CONTROLLER_MODULE_NAME = "misccontroller";
		public static final String REST_CONTROLLER_MODULE_NAME = "restcontroller";
		public static final String CUSTOM_CONTROLLER_MODULE_NAME = "customcontroller";
		public static final String RDM_CONTROLLER_MODULE_NAME = "rdmcontroller";
		public static final String E2_CONTROLLER_MODULE_NAME = "e2controller";
		public static final String CONTROLLER_MODULE_NAME = "controller";
		public static final String AGENT_METRICS_MODULE = "agentMetrics";
		public static final String CUSTOM_ACTIVITY = "customactivity";

		public static final String COMMISSIONING_ACTIVITY = "commissionactivity";
		public static final String SITE_ACTIVITY = "siteactivity";
		public static final String BUILDING_ACTIVITY = "buildingactivity";
		public static final String FLOOR_ACTIVITY = "flooractivity";
		public static final String SPACE_ACTIVITY = "spaceactivity";
		public static final String VENDOR_ACTIVITY = "vendoractivity";

		public static final String ASSET_DOWNTIME_STATUS = "assetDowntimeStatus";
		public static final String ASSET_DOWNTIME_ID = "assetDowntimeId";

		public static final String STARTTIME = "startTime";
		public static final String ENDTIME = "endTime";
		public static final String INTERVAL = "interval";
		public static final String VM_LIST = "vmList";
		public static final String HISTORICAL_RULE_LOGGER_PROPS = "historicalLoggerInfoProps";
		public static final String HISTORICAL_RULE_LOGGER = "historicalLogger";
		public static final String HISTORICAL_VM_JOB_ID = "historicalVMJobId";
		public static final String HISTORICAL_VM_JOB = "historicalVMJob";
		public static final String HISTORICAL_RULE_JOB_ID = "historicalRuleJobId";
		public static final String HISTORICAL_EVENT_RULE_JOB_ID = "historicalRuleJobId";
		public static final String HISTORICAL_ALARM_OCCURRENCE_DELETION_JOB_ID = "historicalAlarmOccurrenceDeletionJobId";
		public static final String HISTORICAL_ALARM_PROCESSING_JOB_ID = "historicalAlarmProcessingJobId";
		public static final String HISTORICAL_ALARM_OCCURRENCE_DELETION_JOB_RETRY_COUNT = "historicalAlarmOccurrenceDeletionJobRetryCount";
		public static final String HISTORICAL_ALARM_PROCESSING_JOB_RETRY_COUNT = "historicalAlarmProcessingJobRetryCount";
		public static final String HISTORICAL_FORMULA_FIELD_JOB_ID = "historicalFormulaFieldJobId";
		public static final String HISTORICAL_OPERATIONAL_ALARM_PROCESSING_JOB_ID = "historicalOperationalAlarmProcessingJobId";
		public static final String HISTORICAL_OPERATIONAL_EVENT_JOB_ID = "historicalOperationalEventJobId";
		public static final String FORMULA_RESOURCE_JOB_ID = "formulaJobId";
		public static final String FORMULA_RESOURCE = "formulaResource";
		public static final String FORMULA_FREQUENCY_TYPES = "formulaFrequencyTypes";

		public static final String ASSET_BD_SOURCE_DETAILS = "assetbreakdownSourceDetails";
		public static final String LAST_ASSET_BD_SOURCE_DETAILS_ID = "lastAssetBDSourceDetailId";

		public static final String CURRENT_OCCUPANCY_READING = "currentoccupancyreading";
		public static final String ASSIGNED_OCCUPANCY_READING = "assignedoccupancyreading";
		public static final String TEMPERATURE_READING = "temperaturereading";
		public static final String HUMIDITY_READING = "humidityreading";
		public static final String CO2_READING = "co2reading";
		public static final String SET_POINT_READING = "setpointreading";
		public static final String ENERGY_DATA_READING = "energydata";
		public static final String UTILITY_BILL_READING = "utilitybillreading";
		public static final String WATER_READING = "waterreading";

		public static final String PREREQUISITE_PHOTOS = "prerequisitephotos";
		public static final String BASE_SPACE_PHOTOS = "basespacephotos";
		public static final String ASSET_PHOTOS = "assetphotos";
		public static final String STORE_ROOM_PHOTOS = "storeroomphotos";
		public static final String ASSET_ACTIVITY = "assetactivity";

		public static final String TICKET_NOTES = "ticketnotes";
		public static final String SERVICE_REQUEST_NOTES = "servicerequestsnotes";
		public static final String BASE_SPACE_NOTES = "basespacenotes";
		public static final String ASSET_NOTES = "assetnotes";
		public static final String DELIVERY_NOTES = "deliverynotes";
		public static final String ITEM_TYPES_NOTES = "itemTypesNotes";
		public static final String ITEM_NOTES = "itemNotes";
		public static final String TOOL_NOTES = "toolNotes";
		public static final String TOOL_TYPES_NOTES = "toolTypesNotes";
		public static final String BASE_ALARM_NOTES = "basealarmnotes";

		public static final String ASSET_DEPRECIATION = "assetdepreciation";
		public static final String ASSET_DEPRECIATION_REL = "assetdepreciationRel";
		public static final String ASSET_DEPRECIATION_CALCULATION = "assetdepreciationCalculation";
		public static final String ACTIVATE = "activate";

		public static final String STORE_ROOM_NOTES = "storeRoomNotes";

		public static final String TICKET_ATTACHMENTS = "ticketattachments";
		public static final String SERVICE_REQUEST_ATTACHMENTS = "servicerequestsattachments";
		public static final String BASE_SPACE_ATTACHMENTS = "basespaceattachments";
		public static final String ASSET_ATTACHMENTS = "assetattachments";
		public static final String DELIVERY_ATTACHMENTS = "deliveryattachments";
		public static final String TASK_ATTACHMENTS = "taskattachments";
		public static final String INVENTORY_ATTACHMENTS = "inventoryattachments";
		public static final String ITEM_TYPES_ATTACHMENTS = "itemTypesAttachments";
		public static final String TOOL_TYPES_ATTACHMENTS = "toolTypesattachments";
		public static final String STORE_ROOM_ATTACHMENTS = "storeRoomAttachments";
		public static final String VISITOR_LOGGING_ATTACHMENTS = "visitorloggingattachments";
		public static final String VENDOR_ATTACHMENTS = "vendorsAttachments";
		public static final String TENANT_ATTACHMENTS = "tenantattachments";

		public static final String MV_PROJECT_MODULE = "mvproject";
		public static final String MV_BASELINE_MODULE = "mvbaseline";
		public static final String MV_ADJUSTMENT_MODULE = "mvadjustment";

		public static final String CONTROL_ACTION_COMMAND_MODULE = "controlActionCommand";

		public static final String IS_PREREQUISITE = "isPrerequisite";
		public static final String PICKLIST = "pickList";

		public static final String USERS = "users";
		public static final String ORGUSERS = "orgusers";
		public static final String ORG_USER_ID = "orgUserId";
		public static final String OUID = "ouid";
		public static final String IAM_USERS="iam_users";

		public static final String GROUPS = "groups";

		public static final String LOCATION = "location";
		public static final String LOCATION_LIST = "locations";

		public static final String REQUESTER = "requester";

		public static final String IS_PUBLIC_REQUEST = "is_public_request";
		public static final String IS_PLAN_REQUEST = "is_plan_request";
		public static final String SUPPORT_EMAIL = "supportEmail";
		public static final String SUPPORT_EMAIL_LIST = "supportEmails";
		public static final String REQUEST_EMAIL_ID = "requestEmailId";
		public static final String EMAIL_SETTING = "emailSetting";

		public static final String IMPORT_DATA_DETAILS = "importDataDetails";
		public static final String IMPORT_FILE_READERS_MAP = "importFileIdVsImportFileReadersMap";
		public static final String IMPORT_LIST = "importList";
		public static final String MY_IMPORT = "myImport";
		public static final String IS_PREDICTABLE="isPredictable";
		public static final String IMPORT_FILE_LIST = "importFileList";

		public static final String IMPORT_SHEET = "importSheet";
		public static final String IMPORT_SHEETS = "importSheets";
		public static final String IMPORT_ROW_CONTEXT_LIST = "importRowContextList";
		public static final String CHUNK_LIMIT = "chunkLimit";
		public static final String PROCESSED_ROW_COUNT="processedRowCount";

		public static final String CONFIG = "config";
		public static final String RESULT = "result";
		public static final String MESSAGE = "message";
		public static final String REGISTERED_OFFLINE_RECORD = "registeredOfflineRecord";
		public static final String CHECK_FOR_RECORD_UPDATES = "checkForRecordUpdates";

		public static final String COMSUMPTIONDATA_LIST = "comsumptionDataList";

		public static final String LEEDID = "leedID";
		public static final String LEED = "LEED";

		public static final String SYSTEM_BUTTON = "systemButton";
		public static final String SYSTEM_BUTTONS = "systemButtons";

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
		public static final String FETCH_TRIGGERS = "fetchtriggers";
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
		public static final String PM_TRIGGER = "trigger";
		public static final String HIDE_PM_RESOURCE_PLANNER = "hidePMPlanner";
		public static final String PREVENTIVE_MAINTENANCE_SITE_FILTER_VALUES = "preventivemaintenanceSiteFilterValues";
		public static final String PREVENTIVE_MAINTENANCE_RESOURCE_FILTER_VALUES = "preventivemaintenanceResourceFilterValues";
		public static final String SELECTABLE_FIELD_NAMES="selectableFieldNames";
		public static final String IS_SUB_FORM_RECORD="isSubformRecord";

		public static final String WO_DUE_STARTTIME = "woDueStarttime";
		public static final String WO_DUE_ENDTIME = "woDueEndtime";
		public static final String WO_LIST_COUNT = "woListCount";
		public static final String WO_IDS = "woIds";
		public static final String PM_IDS = "pmIds";
		public static final String NOTE_IDS = "noteIds";
		public static final String WO_VIEW_COUNT = "woListAndCount";
		public static final String IS_APPROVAL = "isApproval";
		public static final String WO_FETCH_ALL = "woFetchAll";

		public static final String IS_FETCH_CALL = "isFetchCall";
		public static final String IS_FROM_BUILDER = "isFromBuilder";
		public static final String CV_NAME = "cvName";
		public static final String CUSTOM_VIEW = "customView";
		public static final String FETCH_ONLY_VIEW_GROUP_COLUMN="fetchOnlyViewGroupColumn";
		public static final String NEW_CV = "newCV";
		public static final String EXISTING_CV = "existingView";
		public static final String FILTERS = "filters";
		public static final String QUICK_FILTER = "quickFilter";
		public static final String SPECIAL_FIELDS = "specialFields";
		public static final String VIEW_LIMIT = "viewLimit";
		public static final String IS_S3_VALUE = "isS3Value";
		public static final String FILTER_CONDITIONS = "filterConditions";
		public static final String CRITERIA_IDS = "criteria";
		public static final String COUNT = "count";
		public static final String FILTER = "filter";
		public static final String FILTER_CRITERIA = "filterCriteria";
		public static final String INCLUDE_PARENT_CRITERIA = "includeParentCriteria";
		public static final String SEARCH = "search";
		public static final String EXCLUDE_PARENT_FILTER="excludeParentFilter";
		public static final String WITH_COUNT="withCount";
		public static final String GENERIC_SEARCH = "genericSearch";
		public static final String SEARCH_RESULT = "searchResult";
		public static final String SEARCH_CRITERIA = "searchCriteria";
		public static final String GENERIC_SEARCH_CRITERIA = "genericSearchCriteria";
		public static final String FILTER_SERVER_CRITERIA = "filterServerCriteria";
		public static final String SORTING = "sorting";
		public static final String OVERRIDE_SORTING = "overrideSorting";
		public static final String MAX_LEVEL = "maxLevel";
		public static final String SORTING_QUERY = "sortingQuery";
		public static final String LIMIT_VALUE = "limitValue";
		public static final String WORKFLOW_FETCH_EVENT = "workflowFetchEvent";
		public static final String WORKFLOW_FETCH_CHILDREN = "workflowFetchChildren";
		public static final String WORKFLOW_UPDATE = "workflowUpdate";
		public static final String WORKFLOW_RULE = "workflowRule";
		public static final String WORKFLOW_RULES = "workflowRules";
		public static final String WORKFLOW_RULES_COUNT = "workflowRulesCount";
		public static final String WORKFLOW_RULE_MODULE = "workflowrule";
		public static final String WORKFLOW_RULE_ID = "workflowRuleID";
		public static final String WORKFLOW_RULE_ID_LIST = "workflowRuleIDList";
		public static final String SCHEDULE_RULE_META = "scheduleRuleMeta";
		public static final String WORKFLOW_RULE_PARENT_LOGGER_ID = "workflowRuleParentLoggerId";
		public static final String WORKFLOW_RULE_RESOURCE_ID = "workflowRuleResourceId";
		public static final String WORKFLOW_RULE_LOGGERS = "workflowRuleLoggers";
		public static final String WORKFLOW_RULE_RESOURCE_LOGGERS = "workflowRuleResourceLoggers";
		public static final String WORKFLOW_RULE_HISTORICAL_LOGS = "workflowRuleHistoricalLogs";
		public static final String WORKFLOW_PARALLEL_RULE_EXECUTION_MAP = "workflowParallelRuleExecutionMap";
		public static final String RECORD_CONTEXT_FOR_RULE_EXECUTION = "recordContextForRuleExecution";
		public static final String IS_PARALLEL_RULE_EXECUTION = "isParallelRuleExecution";
		public static final String IS_READING_RULE_WORKFLOW_EXECUTION = "isReadingRuleWorkflowExecution";
		public static final String PARENT_RULE_FIELD = "parentRuleField";
		public static final String ON_SUCCESS_FIELD = "onSuccessField";
		public static final String WORKFLOW_RULE_CACHE_MAP = "workflowRuleCacheMap";
		public static final String EVENT_TYPES = "eventTypes";
		public static final String RULE_TYPES = "ruleTypes";
		public static final String READING_RULE_MODULE = "readingrule";
		public static final String NEW_READING_RULE_MODULE = "newreadingrule";
		public static final String NEW_READING_RULE = "newReadingRule";
		public static final String IS_NEW_READING_RULE = "isnewreadingrule";
		public static final String READING_ALARM_RULES = "readingalarmrules";
		public static final String READING_ALARM_RULE = "readingalarmrule";
		public static final String ALARM_RULE_META = "alarmRuleMeta";
		public static final String WORKORDER_ACTIVITY = "workorderactivity";
		public static final String RULE_COUNT = "ruleCount";
		public static final String RULES = "rules";
		public static final String RULE_ID = "ruleId";
		public static final String PARENT_RULE_ID = "parentRuleId";
		public static final String FAULT_IMPACT_TEMPLATE_ID = "faultImpactTemplateId";
		public static final String FAULT_IMPACT_OBJECT = "faultImpactObject";
		public static final String ALARM_RULE = "alarmRule";
		public static final String RULE_TYPE = "ruleType";
		public static final String IS_SUMMARY = "isSummary";
		public static final String OLD_ALARM_RULE = "oldalarmRule";
		public static final String ALARM_RULE_ACTIVE_ALARM = "alarmRuleActiveAlarm";
		public static final String ALARM_RULE_THIS_WEEK = "alarmRuleThisWeek";
		public static final String ALARM_RULE_TOP_5_ASSETS = "alarmRuleTop5Assets";
		public static final String ALARM_RULE_WO_SUMMARY = "alarmRulewoSummary";
		public static final String WORKFLOW_ALARM_TRIGGER_RULES = "workflowAlarmTriggerRules";
		public static final String WORKFLOW_ALRM_CLEAR_RULE = "workflowAlarmClearRule";
		public static final String WORKFLOW_RULE_LIST = "workflowRuleList";
		public static final String APPROVAL_RULE = "approvalRule";
		public static final String APPROVER_ID_LIST = "approverIdList";
		public static final String APPROVER_LIST = "approverList";
		public static final String READING_RULE_LIST = "readingRules";
		public static final String READING_RULE_ID = "readingRuleId";
		public static final String IS_READING_RULE_EXECUTE_FROM_JOB = "isReadingRulesExecutionFromJob";
		public static final String READING_RULE_ALARM_META = "readingRuleAlarmMeta";
		public static final String SENSOR_RULE_MODULE = "sensorrule";
		public static final String LIST_SIZE= "listSize";

		public static final String SENSOR_RULE_LIST = "sensorRuleList";
		public static final String SENSOR_RULE_TYPES = "sensorRuleTypes";
		public static final String WORKFLOW_RULE_TYPE = "workflowRuleType";
		public static final String WORKFLOW_ACTION_LIST = "workflowActions";
		public static final String WORKFLOW_ACTION_ID = "workflowActionId";
		public static final String PAGINATION = "pagination";
		public static final String SKIP_MODULE_CRITERIA = "skipModuleCriteria";
		public static final String FETCH_COMPLAINT_TYPE = "fetchComplaintType";
		public static final String CURRENT_EXECUTION_TIME = "currentexecutiontime";
		public static final String NEXT_EXECUTION_TIMES = "nextexecutiontimes";
		public static final String PM_JOBS = "pmjobs";
		public static final String GROUP_STATUS = "groupStatus";
		public static final String DEFAULT_IDS = "defaultIds";

		public static final String APPROVAL_LIST = "approvalList";

		public static final String SLA_RULE_MODULE = "slaRuleModule";
		public static final String SLA_RULE_MODULE_LIST = "slaRuleModuleList";
		public static final String SLA_ENTITY = "slaEntity";
		public static final String SLA_ENTITY_LIST = "slaEntityList";
		public static final String SLA_POLICY = "slaPolicy";
		public static final String SLA_POLICY_LIST = "slaPolicyList";
		public static final String SLA_POLICY_ID = "slaPolicyId";
		public static final String SLA_POLICY_ESCALATION_LIST = "slaPolicyEscalationList";

		public static final String SCORING_CONTEXT_LIST = "scoringContextList";

		public static final String ALARM_IMPACT = "alarmImpact";
		public static final String ALARM_IMPACT_LIST = "alarmImpactList";

		public static final String FORMATTED_STRING = "formattedString";
		public static final String REPLACED_STRING = "replacedString";

		public static final String NAMED_CRITERIA_LIST = "namedCriteriaList";
		public static final String NAMED_CRITERIA = "namedCriteria";
		public static final String NAMED_CRITERIA_RESULT = "namedCriteriaResult";

		// public static final String SLA_MODULE = "sla";
		// public static final String SLA_LIST = "sla_list";
		// public static final String SLA = "sla";

		public static final String TECH_COUNT_GROUP_DIGEST = "techCountGroupDigest";
		public static final String SITE_ROLE_WISE_COUNT = "siteRoleWiseCount";
		public static final String SITE_ROLE_WO_COUNT = "siteRoleWoCount";

		public static final String DELEGATION_CONTEXT = "delegation";
		public static final String ONLY_MY_DELEGATION = "onlyMyDelegation";
		public static final String DELEGATION_LIST = "delegationList";

		public static final String WEEKEND_LIST = "weekends";
		public static final String WEEKEND = "weekend";

		public static final String DEPENDENT_FEATURES = "dependentFeatures";

		public static final String VIEWID = "viewId";
		public static final String VIEW_LIST = "views";

		public static final String VIEW_STATUS = "viewStatus";
		public static final String VIEW_GROUP = "viewGroup";

		public static final String VIEW_GROUP_ID = "viewGroupId";
		public static final String CUSTOM_FILTER = "customFilter";
		public static final String CUSTOM_FILTER_CONTEXT = "customFilterContext";
		public static final String CUSTOM_FILTERS_LIST = "customFiltersList";
		public static final String CUSTOM_FILTER_CRITERIA = "customFilterCriteria";
		public static final String CRITERIA_MAP = "criteriaMap";
		public static final String FILTERS_MAP = "filterMap";

		public static final String FIELD_IDS = "fieldIds";
		public static final String QUICK_FILTER_CONTEXT = "quickFilters";
		public static final String FILTER_IDS = "filterIds";
		public static final String FILTER_ID = "filterId";

		public static final String GROUP_VIEWS = "groupViews";

		public static final String VIEW_NAME = "viewName";

		public static final String VIEWCOLUMNS = "viewColumns";
		public static final String PARENT_VIEW = "parentView";
		public static final String VIEW_COUNT = "viewCount";
		public static final String SUB_VIEW = "subView";
		public static final String SUB_VIEW_COUNT = "subViewCount";
		public static final String RESOURCE_ID = "RESOURCE_ID";

		public static final String IS_FROM_V2 = "isFromV2";
		public static final String SHOW_RELATIONS_COUNT = "showRelationsCount";
		public static final String RELATIONS_COUNT = "relationsCount";
		public static final String FETCH_HIERARCHY = "fetchHierarchy";

		public static final String RELATIONSHIP = "relationship";
		public static final String RELATIONSHIP_LIST = "relationshipList";
		public static final String RELATED_ASSETS = "relatedAssets";

		public static final String RELATION = "relation";
		public static final String RELATION_LIST = "relationList";
		public static final String RELATION_MAPPING = "relationMapping";
		public static final String RELATION_NAME = "relationName";
		public static final String RELATION_MODULE_NAME = "relationModuleName";
		public static final String RELATION_POSITION_TYPE = "relationPosition";
		public static final String RELATIONSHIP_ACTION_TYPE = "relationshipActionType";

		public static final String CLASSIFICATION = "classification";
		public static final String CLASSIFICATION_DATA = "classificationData";
		public static final String CLASSIFICATION_ID = "classificationId";
		public static final String CLASSIFICATION_ATTRIBUTE = "classificationAttribute";
		public static final String CLASSIFICATION_RESOLVE_PATH = "classificationResolvePath";

		public static final String CLASSIFICATION_DONT_RESOLVE_PATH = "classificationDontResolvePath";

		public static final String SPACECATEGORY = "SPACECATEGORY";
		public static final String SPACECATEGORIESLIST = "SPACECATEGORIESLIST";

		public static final String FILE_FORMAT = "fileFormat";
		public static final String FILE_ID = "fileID";

		public static final String PUBLIC_FILE_ID = "publicFileID";
		public static final String FILE_URL = "fileUrl";
		public static final String FILE_NAME_SPACE = "namespace";
		public static final String DATE_FILTER = "dateFilter";
		public static final String DATE_FIELD = "dateField";
		public static final String START_TIME = "startTime";
		public static final String ALARM_ID = "alarmId";
		public static final String PARENT_ALARM_ID = "parentAlarmId";
		public static final String SCHEDULE_INFO = "scheduleInfo";
		public static final String EXPORT_INFO = "exportInfo";
		public static final String END_TIME = "endTime";
		public static final String MAX_COUNT = "maxCount";
		public static final String SCHEDULED_ACTION = "scheduledAction";
		public static final String FREQUENCY = "frequency";
		public static final String IS_HISTORICAL = "isHistorical";
		public static final String RULE_JOB_TYPE = "ruleJobType";

		public static final String FIELD_DEPENDENCY_ID = "fieldDependencyId";

		public static final String DATE_OPERATOR = "dateOperator";
		public static final String DATE_OPERATOR_VALUE = "dateOperatorValue";
		public static final String DATE_OFFSET_VALUE = "dateOffset";

		public static final String BASE_LINE = "baseLine";
		public static final String BASE_LINE_LIST = "baseLines";
		public static final String DATE_RANGE = "dateRange";

		public static final String CHART_STATE = "chartState";
		public static final String TABULAR_STATE = "tabularState";
		public static final String TEMPLATE_JSON = "templateJSON";

		public static final String COST = "cost";
		public static final String COST_TYPE = "costType";
		public static final String COST_ASSET = "costAsset";
		public static final String COST_FIRST_BILL_TIME = "costFirstBillTime";
		public static final String COST_READINGS = "costReadings";

		public static final String PLANNED_TOOLS_COST = "plannedToolsCost";
		public static final String PLANNED_ITEMS_COST = "plannedItemsCost";
		public static final String PLANNED_SERVICES_COST = "plannedServicesCost";
		public static final String PLANNED_LABOUR_COST = "plannedLabourCost";

		public static final String TIMESTAMP = "timestamp";
		public static final String DEVICE_DATA = "deviceData";
		public static final String BULK_DEVICE_DATA = "bulkData";
		public static final String DEVICE_LIST = "deviceList";
		public static final String PAY_LOAD = "payLoad";
		public static final String MODELED_DATA = "modeledData";
		public static final String INSTANCE_INFO = "instanceInfo";
		public static final String READING_KEY = "readingKey";
		public static final String CONFIGURE = "configure";
		public static final String SUBSCRIBE = "subscribe";
		public static final String UNSUBSCRIBE_IDS = "unsubscribeIds";
		public static final String UPDATE_LAST_READINGS = "updateLastReadings";
		public static final String SKIP_LAST_READING_CHECK = "skipLastReadingCheck";
		public static final String SKIP_VALIDATION = "skipValidation";
		public static final String DELTA_RESETTED = "deltaResetted";
		public static final String PREVIOUS_READING_DATA_META = "previousReadingDataMeta";
		public static final String SKIP_PREV_READING_DATA_META = "skipPrevReadingMeta";
		public static final String CURRRENT_READING_DATA_META = "currentReadingDataMeta";
		public static final String READING_DATA_META_ID = "readingDataMetaId";
		public static final String READING_DATA_META_LIST = "readingDataMetaList";
		public static final String READING_DATA_META_COUNT = "readingDataMetaCount";
		public static final String READING_DATA_META = "readingdatameta";
		public static final String READING_DATA_META_MAP = "readingDataMetaMap";
		public static final String READING_DATA_META_TYPE = "readingDataMetaType";
		public static final String FORMULA_FIELD = "formulaField";
		public static final String SKIP_FORMULA_HISTORICAL_SCHEDULING = "skipFormulaHistoricalScheduling";

		public static final String FORMULA_INPUT_UNIT_STRING = "formulaInputUnit";

		public static final String FORMULA_UNIT_STRING = "formulaUnitString";
		public static final String FORMULA_UNIT = "formulaUnit";
		public static final String FORMULA_METRIC = "formulaMetric";
		public static final String DEPENDENT_FIELD_RESOURCE_CONTEXT_LIST = "dependentFieldResourceContextList";
		public static final String FORMULA_RESOURCE_STATUS_LIST = "formulaFieldResourceStatusList";
		public static final String IS_FORMULA_FIELD_OPERATION_FROM_M_AND_V = "isformulaFieldOppFromMandV";

		public static final String FORMULA_FIELD_TYPE = "formulaFieldType";
		public static final String FORMULA_LIST = "formulaList";
		public static final String HISTORY_READINGS = "historyReadings";
		public static final String HISTORY_ALARM = "historyAlarm";
		public static final String SKIP_OPTIMISED_WF = "skipOptimisedWorkflow";
		public static final String ANALYTICS_ANAMOLY = "analyticsAnamoly";
		public static final String DERIVATION = "derivation";
		public static final String FETCH_MAPPED = "fetchMapped";
		public static final String FETCH_READING_INPUT_VALUES = "fetchReadingInputValues";
		public static final String IS_FETCH_RDM_FROM_UI = "isFetchRDMFromUI";
		public static final String RESET_COUNTER_META_LIST = "ResetCounterMetaList";
		public static final String RESET_COUNTER_META = "resetcountermeta";
		public static final String IGNORE_SPL_NULL_HANDLING = "ignoreSplNullHandling";

		public static final String CONTROL_GROUP = ControlActionUtil.CONTROL_ACTION_GROUP_CONTEXT;

		public static final String PORTALINFO = "portalInfo";
		public static final String PUBLICKEYFILE = "publicKeyFile";
		public static final String PUBLICKEYFILENAME = "publicKeyFileName";
		public static final String PUBLICKEYFILETYPE = "publicKeyFileNameType";
		public static final String MARKED_READINGS = "markedReadings";

		public static final String WEATHER_READING = "weather";
		public static final String NEW_WEATHER_READING = "newWeather";
		public static final String WEATHER_DAILY_READING = "weatherDaily";
		public static final String WEATHER_DAILY_FORECAST_READING = "weatherDailyForecast";
		public static final String WEATHER_HOURLY_FORECAST_READING = "weatherHourlyForecast";
		public static final String CDD_READING = "cdd";
		public static final String HDD_READING = "hdd";
		public static final String WDD_READING = "wdd";

		public static final String DEGREE_DAY_READING = "degreeDayReading";
		public static final String PSYCHROMETRIC_READING = "psychrometric";
		public static final String NEW_PSYCHROMETRIC_READING = "newpsychrometric";
		public static final String WET_BULB_TEMPERATURE = "wetBulbTemperature";
		public static final String MODULE_NAMES = "moduleNames";
		public static final String SORT_FIELDS = "sortFields";
		public static final String SORT_FIELDS_OBJECT = "sortFieldsObject";

		public static final String EXCLUDE_EMPTY_FIELDS = "exludeEmptyFields";
		public static final String EXCLUDE_FORECAST = "excludeForecast";
		public static final String FETCH_CONTROLLABLE_FIELDS = "fetchControllableFields";
		public static final String WITH_READINGS = "withReadings";
		public static final String DEFAULT_FIELD_IDS = "sortFieldId";
		public static final String WITH_WRITABLE_READINGS = "withWritableReadings";
		public static final String READING_ID = "readingId";
		public static final String INPUT_TYPE = "inputtype";

		public static final String BENCHMARK_UNITS = "benchmarkUnits";
		public static final String BENCHMARK_VALUE = "benchmarkValue";
		public static final String BENCHMARK_DATE_AGGR = "benchmarkDateAggr";
		public static final String READING_RULES_LIST = "readingRulesList";
		public static final String READING_FIELD_ID = "readingFieldId";
		public static final String ACTIONS_LIST = "actionsList";
		public static final String ACTION = "action";

		public static final String ONLY_PERMITTED_ACTIONS = "onlyPermittedActions";

		public static final String DEL_READING_RULE_IDS = "delReadingRuleIDs";
		public static final String DEL_READING_RULE = "delReadingRule";
		public static final String DO_VALIDTION = "doValidation";
		public static final String BENCHMARK_DATE_VAL = "benchmarkDateVal";
		public static final String SHIFT = "shift";
		public static final String SHIFTS = "shifts";
		public static final String SHIFT_USER_MAPPING = "shiftUserMapping";
		public static final String ACTUAL_TIMINGS = "actualTimings";
		public static final String SHIFT_ID = "shiftID";

		public static final String LAST_SYNC_TIME = "lastSyncTime";
		public static final String CUSTOM_OBJECT = "customObject";
		public static final String FORM_NAME = "formName";
		public static final String FORM_ID = "formId";
		public static final String PARENT_FORM_ID = "parentFormId";
		public static final String SUB_FORM_ID = "subFormId";
		public static final String FORM = "form";
		public static final String SUB_FORM = "subForm";
		public static final String FORMS_LIST = "formsList";
		public static final String FORMS_RESPONSE_LIST = "formsResponseList";
		public static final String FORMS = "forms";
		public static final String FORM_FIELD = "formField";
		public static final String FORM_FIELD_List = "formFieldList";
		public static final String FORM_FIELD_ID = "formFieldId";
		public static final String FORM_FIELDS = "formFields";
		public static final String FORM_OBJECT = "formObject";
		public static final String FORM_TYPE = "formType";
		public static final String APP_LINKNAME = "appLinkName";
		public static final String HIDE_IN_LIST = "hideInList";
		public static final String FORM_SECTION = "formSection";
		public static final String FORM_SOURCE = "formSource";
		public static final String SKIP_TEMPLATE_PERMISSION = "skipTemplatePermission";
		public static final String FOR_CREATE = "forCreate";
		public static final String QR_VALUE = "qrValue";
		public static final String LOCATION_VALUE = "locationValue";
		public static final String CURRENT_LOCATION = "currentLocation";
		public static final String MAP_QR = "mapqr";

		public static final String STATE_FLOW_PUBLISH = "stateflowPublish";
		public static final String PUBLISH_DATA = "publishData";
		public static final String PUBLISH_SUCCESS = "publishSuccess";
		public static final String PUBLISH_FAILURE = "publishFailure";
		public static final String ORGUNITS_LIST = "orgUnitsList";
		public static final String ALL_METRICS = "allMetrics";
		public static final String METRICS_WITH_UNITS = "MetricsWithUnits";

		public static final String PUBSUB_TOPIC = "pubsubTopic";

		public static final String IDS_TO_UPDATE_TASK_COUNT = "ids_to_update_task_count";
		public static final String IDS_TO_UPDATE_COUNT = "ids_to_update_count";
		public static final String DEPENDENT_RESOURCES_DATA = "dependentResourcesData";

		public static final String PAGE = "page";
		public static final String PER_PAGE = "perPage";

		public static final String HOME_PAGE = "homepage";
		public static final String RELATED_LIST_META = "relatedListMeta";
		public static final String RELATIONSHIP_META = "relationshipMeta";
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
		public static final String IS_WORKORDER_COST_CHAIN = "isWorkOrderCostChain";

		public static final String STORE_ROOM = "storeRoom";
		public static final String STORE_ROOMS = "storeRooms";
		public static final String STORE_ROOM_LIST = "storeRoomList";
		public static final String STORE_ROOM_ID = "storeRoomId";

		public static final String ITEM_TYPES = "itemTypes";
		public static final String ITEM_TYPES_LIST = "itemTypesList";
		public static final String ITEM_TYPES_ID = "itemTypesId";
		public static final String ITEM_TYPES_IDS = "itemTypesIds";

		public static final String TOOL_TYPES = "toolTypes";
		public static final String TOOL_TYPES_LIST = "toolTypesList";

		public static final String TOOL_TYPES_ID = "toolTypesId";
		public static final String TOOL_TYPES_IDS = "toolTypesIds";

		// public static final String TOOL = "tool";
		public static final String TOOLS = "tools";
		public static final String TOOLS_STATUS = "toolsStatus";
		public static final String TOOLS_CATEGORY = "toolsCategory";

		public static final String VENDOR = "vendor";
		public static final String VENDORS = "vendors";
		public static final String VENDOR_NOTES = "vendorsNotes";
		public static final String VENDOR_ID = "vendorsId";

		public static final String ITEM = "item";
		public static final String ITEMS = "items";
		public static final String ITEM_STATUS = "itemStatus";
		public static final String ITEM_ID = "itemId";
		public static final String ITEM_IDS = "itemIds";
		public static final String PURCHASED_ITEM = "purchasedItem";
		public static final String ITEM_ACTIVITY = "itemtypeactivity";

		public static final String WORKORDER_ITEMS = "workorderItem";
		public static final String WORKORDER_ITEM_RECORD = "workorderItemRecord";
		public static final String ITEM_TRANSACTIONS = "itemTransactions";
		public static final String ITEM_TRANSACTION_ID = "itemTransactionId";
		public static final String ITEM_TRANSACTIONS_PARAMS = "itemTransactionsParams";

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
		public static final String TENANT_ID = "tenantIds";
		public static final String TENANT_LIST = "tenants";
		public static final String TENANT_SPACES = "tenantspaces";
		public static final String TENANT_UTILITY_IDS = "utilityIds";
		public static final String TENANT_CONTEXT = "tenantContext";
		public static final String TENANT_ACTIVITY = "tenantactivity";
		public static final String TENANT_NOTES = "tenantnotes";

		public static final String BUDGET = "budget";
		public static final String BUDGET_AMOUNT = "budgetamount";
		public static final String BUDGET_MONTHLY_AMOUNT = "budgetmonthlyamount";


		public static final String SHOW_TOOLS_FOR_RETURN = "showToolsForReturn";
		public static final String SHOW_ITEMS_FOR_RETURN = "showItemsForReturn";
		public static final String SHOW_ITEMS_FOR_ISSUE = "showItemsForIssue";
		public static final String SHOW_TOOLS_FOR_ISSUE = "showToolsForIssue";

		public static final String SHOW_ITEM_FOR_WORKORDER = "showItemsForWorkorder";
		public static final String SHOW_TOOL_FOR_WORKORDER = "showToolsForWorkorder";

		public static final String ITEM_VENDORS = "itemVendors";
		public static final String TOOL_VENDORS = "toolVendors";

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
		public static final String PURCHASE_REQUEST_NOTES = "purchaserequestnotes";
		public static final String PURCHASE_REQUESTS = "purchaserequests";
		public static final String PURCHASE_REQUEST_LINE_ITEMS = "purchaserequestlineitems";
		public static final String STATUS = "status";

		public static final String PURCHASE_ORDER = "purchaseorder";
		public static final String PURCHASE_ORDER_NOTES = "purchaseordernotes";
		public static final String PURCHASE_ORDERS = "purchaseorders";

		public static final String PURCHASE_ORDER_ACTIVITY = "poactivity";

		public static final String PURCHASE_REQUEST_ACTIVITY = "practivity";

		public static final String PURCHASE_ORDER_LINE_ITEMS = "purchaseorderlineitems";

		public static final String RECEIPT = "receipt";
		public static final String RECEIPTS = "receipts";

		public static final String RECEIVABLE = "receivable";
		public static final String RECEIVABLE_NOTES = "receivablenotes";
		public static final String RECEIVABLE_ATTACHMENTS = "receivableattachments";
		public static final String RECEIVABLES = "receivables";
		public static final String PO_ID = "poId";
		public static final String PR_IDS = "prIds";
		public static final String RECEIVABLE_ID = "receivableId";
		public static final String PURCHASE_ORDER_LINE_ITEMS_ID = "purchaseorderlineitemsId";
		public static final String VALIDATION_RULES = "validationRules";
		public static final String RESOURCE_MAP = "resourceMap";
		public static final String STATUS_MAP = "statusMap";
		public static final String RECOMMENDED_USERS = "recommendedUsers";
		public static final String RECOMMENDED_COUNT = "recommendedCount";

		private static final String RECEIPT_LINE_ITEMS = "receiptlineitems";
		public static final String ITEM_VENDORS_LIST = "itemTypesVendorsList";

		public static final String ML = "ml";

		public static final String SITES_FOR_STORE_ROOM = "sitesForStoreRoom";
		// public static final String STATE = "state";

		public static final String PURCHASE_CONTRACTS = "purchasecontracts";
		public static final String PURCHASE_CONTRACT = "purchasecontract";
		public static final String PURCHASE_CONTRACTS_LINE_ITEMS = "purchasecontractlineitems";
		public static final String CONTRACTS = "contracts";
		public static final String CONTRACT_NOTES = "contractnotes";
		public static final String CONTRACT_ATTACHMENTS = "contractattachments";

		public static final String LABOUR_CONTRACTS = "labourcontracts";
		public static final String LABOUR_CONTRACT = "labourcontract";
		public static final String LABOUR_CONTRACTS_LINE_ITEMS = "labourcontractlineitems";

		public static final String PO_LINE_ITEMS_SERIAL_NUMBERS = "poLineItemSerialNumbers";
		public static final String SERIAL_NUMBERS = "serialNumbers";
		public static final String ASSETS = "assets";
		public static final String PO_LINE_ITEMS = "poLineItems";

		public static final String TOTAL_COST = "totalCost";
		public static final String TOTAL_QUANTITY = "totalQuantity";
		public static final String WO_TOTAL_COST = "woTotalCost";
		public static final String UNIT_PRICE = "unitPrice";

		public static final String GATE_PASS = "gatePass";
		public static final String GATE_PASS_LINE_ITEMS = "gatePassLineItems";

		public static final String CONNECTED_APPS = "connectedApps";
		public static final String CONNECTED_APP = "connectedApp";
		public static final String CONNECTED_APP_ID = "connectedAppId";
		public static final String CONNECTED_APP_WIDGET = "connectedAppWidget";
		public static final String CONNECTED_APP_WIDGETS = "connectedAppWidgets";
		public static final String CONNECTED_APP_RELATED_LISTS = "connectedAppRelatedLists";
		public static final String CONNECTED_APP_VIEW_URL = "connectedAppViewURL";
		public static final String CONNECTED_APP_CONNECTOR = "connectedAppConnector";
		public static final String CONNECTED_APP_REQUEST = "connectedAppRequest";
		public static final String SANDBOX_MODE = "sandboxMode";

		public static final String STATE_FLOW = "stateFlow";

		public static final String STATE_FLOW_TRANSITION = "stateFlowTransition";
		public static final String SETTING_STATE_FLOW = "stateflow";
		public static final String STATE_FLOW_LIST = "stateFlows";
		public static final String STATE_FLOW_ID = "stateFlowId";
		public static final String TRANSITION_ID = "transition_id";
		public static final String TRANSITION_ACTION_TYPES = "transitionActionTypes";

		public static final String STATEFLOW_TRANSITION_SEQUENCE = "stateFlowTransitionSequence";
		public static final String TRANSITION_ACTION_SEQUENCE = "transitionActionSequence";
		public static final String STATE_TRANSITION_LIST = "stateTransitionList";
		public static final String AVAILABLE_STATES = "states";
		public static final String CURRENT_STATE = "currentState";
		public static final String DEFAULT_STATE = "default_state";
		public static final String DEFAULT_STATE_ID = "default_state_id";
		public static final String DEFAULT_STATE_FLOW_ID = "default_state_flow_id";
		public static final String TRANSITION = "transition";
		public static final String STATE_TRANSITION_ONLY_CONDITIONED_CHECK = "stateTransitionOnlyConditionedCheck";
		public static final String STATE_TRANSITION_GET_PERMALINK_ONLY = "stateTransitionSkipUserCheck";
		public static final String STATEFLOW_DIAGRAM = "stateFlowDiagram";
		public static final String CONFIRMATION_DIALOGS = "confirmationDialogs";
		public static final String VALID_CONFIRMATION_DIALOGS = "validConfirmationDialogs";

		// public static final String DEFAULT_STATEFLOW = "defaultStateflow";

		public static final String FLOW = "flow";
		public static final String FLOWS = "flows";
		public static final String FLOW_TRANSITION = "flowTransition";
		public static final String OLD_FLOW_TRANSITION ="oldFlowTransition";
		public static final String FLOW_TRANSITIONS = "flowTransitions";
		public static final String FLOW_ID = "flowId";

		public static final String AVAILABLE_BLOCKS = "availableBlocks";
		public static final String TO_BLOCK="toBlock";
		public static final String FROM_BLOCK= "fromBlock";
		public static final String HANDLE_POSITION="handlePosition";
		public static final String MEMORY = "memory";
		public static final String APPROVAL_TRANSITION_ID = "approvalTransitionId";
		public static final String SKIP_APPROVAL_CHECK = "approvalMandatorySuccess";

		public static final String SKIP_APPROVAL = "skipApproval";
		public static final String PENDING_APPROVAL_LIST = "pendingApprovalList";

		public static final String AUDIT_LOGS = "auditLogs";
		public static final String WORKFLOW_RULE_ACTION_LOGS = "workflowRuleActionLogs";

		public static final String ROTATING_ASSET = "rotatingAsset";

		public static final String WO_ITEMS_LIST = "woItemsList";
		public static final String WO_TOOLS_LIST = "woToolsList";
		public static final String WO_LABOUR_LIST = "woLabourList";
		public static final String WO_SERVICE_LIST = "woServiceList";

		public static final String SHIPMENT = "shipment";
		public static final String SHIPMENT_LINE_ITEM = "shipmentLineItem";
		public static final String SHIPMENTS = "shipments";

		public static final String INVENTORY_REQUEST = "inventoryrequest";
		public static final String INVENTORY_REQUESTS = "inventoryrequests";
		public static final String INVENTORY_REQUEST_LINE_ITEMS = "inventoryrequestlineitems";
		public static final String INVENTORY_REQUEST_ACTIVITY = "inventoryrequestactivity";

		public static final String LOG = "log";
		public static final String UNIT_POINTS = "unit";
		public static final String DEMO_ROLLUP_EXECUTION_TIME = "nextexecution";
		public static final String DEMO_ROLLUP_JOB_ORG = "rollup_job_org";
		public static final String DATA_POINTS = "data_points";
		public static final String AUTO_COMMISSION_DATA = "autocommissiondata";

		public static final String START_TTIME = "startTtime";
		public static final String END_TTIME = "endTtime";
		public static final String ADMIN_DELTA_ORG = "orgid";
		public static final String ADMIN_DELTA_JOBID = "deltacalculateJobId";
		public static final String ADMIN_DUPLICATE_REMOVE_JOBID = "removeDuplicaeJobId";
		public static final String FIELD_OPTION_TYPE = "fieldOptionType";
		public static final String READING_TOOLS_CONEXT_JOBID = "readingToolsContextJobId";
		public static final String ADMIN_DELTA_CALCULATION = "AdminDeltaCalculation";
		public static final String ADMIN_DUPLICATES_REMOVE = "AdminDuplicatesRemove";

		public static final String DATE = "date";

		public static final String SERVICE = "service";
		public static final String SERVICES = "services";

		public static final String SERVICE_CONTRACTS = "servicecontracts";
		public static final String SERVICE_CONTRACT = "servicecontract";
		public static final String SERVICE_CONTRACTS_LINE_ITEMS = "servicecontractlineitems";

		public static final String PM_PLANNER_SETTINGS = "pm_planner_settings";
		public static final String DEVICE_CODE = "device_code";
		public static final String DEVICE_INFO = "device_info";
		public static final String DEVICES_LIST = "deviceList";
		public static final String DEVICE_DETAILS = "deviceDetails";
		public static final String CONNECTED_DEVICE_ID = "connectedDeviceId";
		public static final String DEVICE_ID = "deviceId";
		public static final String DEVICE_TYPE = "deviceType";

		public static final String ATTENDANCE = "attendance";
		public static final String ATTENDANCE_TRANSACTIONS = "attendanceTransaction";
		public static final String BREAK = "break";
		public static final String BREAK_LIST = "break_list";
		public static final String BREAK_TRANSACTION = "breakTransaction";
		public static final String GRAPHICS = "graphics";
		public static final String GRAPHICS_LIST = "graphics_list";
		public static final String GRAPHICS_FOLDER = "graphicsFolder";
		public static final String GRAPHICS_FOLDERS = "graphicsFolders";
		public static final String SHOW_CHILDREN_GRAPHICS = "showChildrenGraphics";
		public static final String FETCH_ONLY_META = "fetchOnlyMeta";
		public static final String SHIFT_ROTATION = "shiftRotation";
		public static final String SHIFT_ROTATION_DETAILS = "shiftRotationDetails";
		public static final String SHIFT_ROTATION_APPLICABLE_FOR = "shiftRotationApplicableFor";
		public static final String BEAN_CLASS_NAME = "beanClassName";

		public static final String SERVICE_VENDOR = "serviceVendors";

		public static final String WARRANTY_CONTRACTS = "warrantycontracts";
		public static final String WARRANTY_CONTRACT = "warrantycontract";
		public static final String WARRANTY_CONTRACTS_LINE_ITEMS = "warrantycontractlineitems";
		public static final String WO_SERVICE = "workorderService";
		public static final String WO_SERVICES = "workorderServices";
		public static final String WO_SERVICE_IDS = "workorderServiceIds";

		public static final String CONTRACT_ASSET_RELATION = "Contracts_Associated_Assets";
		public static final String WARRANTY_CONTRACT_TYPE = "Warranty_Contract_Type";
		public static final String CONTRACT_ASSOCIATED_ASSETS = "contractassets";
		public static final String CONTRACT_ASSOCIATED_TERMS = "contractterms";
		public static final String PO_ASSOCIATED_TERMS = "poterms";
		public static final String PR_ASSOCIATED_TERMS = "prterms";

		public static final String CONTRACT_TYPE = "Contract_Type";

		public static final String RENTAL_LEASE_CONTRACTS = "rentalleasecontracts";
		public static final String RENTAL_LEASE_CONTRACT = "rentalleasecontract";
		public static final String RENTAL_LEASE_CONTRACTS_LINE_ITEMS = "rentalleasecontractlineitems";

		public static final String TOOL_VENDORS_LIST = "toolTypesVendorsList";
		public static final String IS_CONTRACT_REVISED = "isContractRevised";

		public static final String TERMS_AND_CONDITIONS = "termsandconditions";
		public static final String TERMS_AND_CONDITION = "termsandcondition";

		public static final String IS_MARK_AS_DELETE = "markAsDelete";
		public static final String JOB = "jobContext";
		public static final String JOB_ID = "jobId";
		public static final String JOB_NAME = "jobName";
		public static final String INSTANT_JOB_NAME = "instantJobName";

		// public static final String SUPPORT_STATEFLOW = "supportStateFlow";

		public static final String RECORD_RULE = "recordRule";
		public static final String RECORD_RULE_LIST = "recordRuleList";
		public static final String DIGEST_CONFIG = "digestConfig";
		public static final String DIGEST_CONFIG_LIST = "digestConfigList";

		public static final String DIGEST_CONFIG_ID = "digestConfigId";
		public static final String WORK_FLOW_EXPRESSIONS = "workflowexpressions";
		public static final String WORK_FLOW_PARAMS = "workflowparams";

		public static final String PREFERENCE_META = "preferences";
		public static final String PREFERENCE_LIST = "preferenceList";
		public static final String PREFERENCE_VALUE_LIST = "preferenceValueList";
		public static final String PREFERENCE_NAME = "preferenceName";
		public static final String PREFERENCE_NAMES = "preferenceNames";
		public static final String PREFERENCE_ID = "preferenceId";
		public static final String MODULE_SPECIFIC = "moduleSpecific";
		public static final String PREFERENCE_RULES = "preferenceRules";

		public static final String MAX_FIELDS_PER_MODULE = "maxFieldsPerModule";
		public static final String CONTROLLER_ASSET = "Controller";

		public static final String IMPORT_MODE = "importMode";
		public static final String IMPORT_PROCESS_CONTEXT = "importProcessContext";

		public static final String INCLUDE_SERVING_SITE = "includeServingSite";
		public static final String SEARCH_QUERY = "searchQuery";
		public static final String PARAMS = "params";
		public static final String QUERY_PARAMS = "queryParams";
		public static final Object DEFAULT_DATE = "defaultDate";
		public static final Object READING_RULE_ALARM_OCCURANCE = "readingRuleAlarmOccurance";

		public static final String ASSET_MOVEMENT = "assetmovement";
		public static final String ASSET_MOVEMENT_RECORDS = "assetmovementrecords";

		public static final String VISITOR = "visitor";
		public static final String VISITORS = "visitors";
		public static final String VISITOR_LOGGING = "visitorlogging";
		public static final String VISITOR_INVITE_REL = "visitorinviterel";
		public static final String VISITOR_INVITE = "visitorinvite";
		public static final String VISITOR_INVITES = "visitorinvites";
		public static final String VISITOR_INVITE_ID = "visitorInviteId";
		public static final String INVITEES = "invitees";
		public static final String VISITOR_LOGGING_RECORDS = "visitorloggingrecords";
		public static final String VISITOR_ID = "visitorId";
		public static final String VISITOR_TYPE = "visitorType";
		public static final String VISITOR_TYPE_ID = "visitorTypeId";
		public static final String IS_EDIT = "isEdit";
		public static final String PHONE_NUMBER = "phoneNumber";
		public static final String PASSCODE = "passCode";
		public static final String VISITOR_LOG_RECORDS = "visitorlogrecords";
		public static final String INVITE_VISITOR_RECORDS = "visitorlogrecords";
		public static final String BASE_VISIT = "basevisit";
		public static final String VISITOR_LOG = "visitorlog";
		public static final String VISIT_CUSTOM_RESPONSE = "visitcustomresponse";
		public static final String VISITOR_LOG_NOTES = "visitorlognotes";
		public static final String VISITOR_LOG_ATTACHMENTS = "visitorlogattachments";
		public static final String INVITE_VISITOR = "invitevisitor";
		public static final String INVITE_VISITOR_NOTES = "invitevisitornotes";
		public static final String INVITE_VISITOR_ATTACHMENTS = "invitevisitorattachments";
		public static final String RECURRING_INVITE_VISITOR = "recurringinvitevisitor";
		public static final String GROUP_VISITOR_INVITE = "groupinvite";
		public static final String BASE_SCHEDULE_ID = "baseScheduleId";
		public static final String DO_NOT_ROLLUP_VISITOR = "doNotRollUpVisitor";
		public static final String OLD_INVITES = "oldInvites";

		public static final int SITE_BOUNDARY_RADIUS = 1000; // meter
		public static final int ASSET_BOUNDARY_RADIUS = 10; // meter

		public static final String IS_EMAIL_VERIFICATION_NEEDED = "isEmailVerificationNeeded";
		public static final String IS_PORTAL = "isPortal";
		public static final String IS_PORTAL_ACCESS = "isPortalAccess";

		public static final String POINTS_PROCESS_CONTEXT = "pointsProcessContext";

		public static final String NEXT_PAYMENT_DATE = "nextPaymentDate";
		public static final String DATA = "data";
		public static final String CONDITIONAL_FORMATTING_RESULT = "conditionalFormattingResult";
		public static final String CONDITIONAL_FORMATTINGS = "conditionalFormattings";
		public static final String IMPORT_PROCESS_CONTEXT_LIST = "importProcessContextList";
		public static final String DIGITAL_LOG_BOOK = "digitalLogBook";
		public static final Object SHOULD_VERIFY_QR = "shouldVerifyQr";
		public static final String TREND_LINE = "trendLine";
		public static final String FETCH_ALL = "fetchAll";
		public static final String IS_VISITOR_SETTING = "isVisitorSetting";
		public static final String VISITOR_TYPE_PICKLIST_OPTION = "visitorTypePicklistOption";
		public static final String VISITOR_SETTINGS = "visitorSettings";
		public static final String DEFAULT_VISITOR_LOG_FORM_NAME = "default_visitor_log_form";
		public static final String DEFAULT_VISITOR_INVITE_FORM_NAME = "default_visitor_invite_form";
		public static final String DEFAULT_VISITOR_LOG_CHECKIN_FORM_NAME = "default_visitor_log_checkin_form";
		public static final String DEFAULT_INVITE_VISITOR_FORM_NAME = "default_invite_visitor_form";

		public static final String CONTACT = "contact";
		public static final String CONTACTS = "contacts";
		public static final String INSURANCES = "insurances";
		public static final String INSURANCE = "insurance";
		public static final String WATCHLIST_RECORDS = "watchListRecords";
		public static final String WATCHLIST = "watchlist";

		public static final String WORKPERMIT_RECORDS = "workPermitRecords";
		public static final String PORTAL_USER_TYPE = "portalUserType";
		public static final String IS_VENDOR_PORTAL = "isVendorPortal";
		public static final String IS_TENANT_PORTAL = "isTenantPortal";
		public static final String FILE_URL_STRING = "fileUrlString";
		public static final String FILE_TOKEN_STRING = "fileTokenString";
		public static final String IS_DOWNLOAD = "isDownload";

		public static final Object TIME_FILTER = "timeFilter";
		public static final Object DATA_FILTER = "dataFilter";
		public static final String PRINTERS = "printers";

		public static final String VISITOR_KIOSKS = "visitorKiosks";

		public static final String OCCUPANT = "occupant";
		public static final String OCCUPANTS = "occupants";

		public static final String DOCUMENT = "document";
		public static final String DOCUMENTS = "documents";

		public static final String VENDOR_DOCUMENTS = "vendorDocuments";

		public static final String COPY_SOURCE_ORG_ID = "sourceOrgId";
		public static final String COPY_TARGET_ORG_ID = "targetOrgId";
		public static final String COPY_START_TIME = "startTime";
		public static final String COPY_END_TIME = "endTime";
		public static final String COPY_ASSET_LIST = "assetList";
		public static final String COPY_TIME_DIFF = "timeDiff";

		public static final String SERVICE_REQUEST_PRIORITY = "servicerequestpriority";
		public static final String SERVICE_REQUESTS = "serviceRequests";
		public static final String SERVICE_REQUEST = "serviceRequest";
		public static final String SERVICE_REQUEST_ACTIVITY = "servicerequestsactivity";
		public static final String SERVICE_REQUEST_ATTACHMENT = "servicerequestsattachments";

		public static final String SAFETY_PLAN = "safetyPlan";
		public static final String HAZARD = "hazard";

		public static final String SAFETY_PLANS = "safetyPlans";
		public static final String HAZARDS = "hazards";

		public static final String PRECAUTION = "precaution";
		public static final String PRECAUTIONS = "precautions";

		public static final String SAFETYPLAN_HAZARD = "safetyPlanHazard";
		public static final String HAZARD_PRECAUTION = "hazardPrecaution";

		public static final String SAFETYPLAN_HAZARD_LIST = "safetyPlanHazardList";
		public static final String HAZARD_PRECAUTION_LIST = "hazardPrecautionList";

		public static final String WORKORDER_HAZARD = "workorderHazard";
		public static final String WORKORDER_HAZARDS = "workorderHazards";

		public static final String WORKORDER_HAZARD_PRECAUTION = "workorderHazardPrecaution";

		public static final String ASSET_HAZARD = "assetHazard";
		public static final String ASSET_HAZARDS = "assetHazards";

		public static final String BASESPACE_HAZARD = "spaceHazard";
		public static final String BASESPACE_HAZARDS = "spaceHazards";

		public static final String WORK_ASSET = "workAsset";

		public static final String TIME_DIFF = "timeDiff";
		public static final String NEED_CRITERIAREPORT = "needCriteriaReport";
		public static final String FILL_CATALOG_FORM = "fillCatalogForm";

		public static final String CLIENT = "client";
		public static final String CLIENT_IDS = "clientIds";
		public static final String CLIENT_ID="clientId";
		public static final String CLIENTS = "clients";

		public static final String SPACE_RATING = "spaceRating";
		public static final String RATING = "rating";

		public static final String SOURCE_TYPE = "sourceType";
		public static final String SOURCE_ID = "sourceId";
		public static final String FIELD_MIGRATION_JOB_ID = "fieldMigrationJobId";

		public static final String TARGET_ID = "targetId";

		public static final String FLOOR_PLAN = "floorPlan";
		public static final String FLOOR_PLAN_ID = "floorPlanId";
		public static final String FLOOR_PLANS = "floorPlans";
		public static final String FLOOR_PLAN_VIEW = "floorPlanView";
		public static final String FLOORPLAN_OBJECT = "floorPlanObject";
		public static final String RULE_ASSET_COUNT = "ruleassetcount";

		public static final String INDOOR_FLOOR_PLAN = "indoorfloorplan";
		public static final String INDOOR_FLOOR_PLANS = "indoorFloorPlans";
		public static final String INDOOR_FLOOR_PLAN_OBJECTS = "indoorfloorplanobjects";
		public static final String INDOOR_FLOOR_PLAN_MARKER = "markertype";

		public static final String EMPLOYEE = "employee";
		public static final String EMPLOYEES = "employees";

		public static final String TENANT_CONTACT = "tenantcontact";
		public static final String TENANT_ID_VS_PRIMARY_TENANT_CONTACT = "tenantIdVsPrimaryTenantContactMap";
		public static final String VENDOR_ID_VS_PRIMARY_TENANT_CONTACT = "vendorIdVsPrimaryTenantContactMap";

		public static final String TENANT_CONTACTS = "tenantcontacts";

		public static final String CLIENT_CONTACT = "clientcontact";
		public static final String CLIENT_CONTACTS = "clientcontacts";

		public static final String VENDOR_CONTACT = "vendorcontact";
		public static final String VENDOR_CONTACTS = "vendorcontacts";

		public static final String PEOPLE = "people";
		public static final String EMAIL_VS_PEOPLE_MAP = "emailVsPeopleMap";
		public static final String PEOPLE_TYPE_LIST = "peopleTypeList";
		public static final String PEOPLE_ID = "peopleId";

		public static final String PEOPLE_TYPE = "peopleType";
		public static final String SECURITY_POLICY_ID = "securityPolicyId";

		public static final String WORKORDER_FAILURE_CLASS_RELATIONSHIP = "workorderFailureClassRelationship";
		public static final String CUSTOM_MODULE_DATA_FAILURE_CLASS_RELATIONSHIP = "customModuleDataFailureClassRelationship";

		public static final String ACCESS_NEEDED_FOR = "accessNeeded";

		public static final String TENANT_UNIT_SPACE = "tenantunit";
		public static final String ALARM_TYPE = "alarmType";
		public static final String PERMISSION_TYPE = "permissionType";

		public static final String QUOTE = "quote";
		public static final String QUOTE_LINE_ITEMS = "quotelineitems";
		public static final String TAX = "tax";
		public static final String DO_FIELD_PERMISSIONS_VALIDATION = "doFieldPermissionValidation";

		public static final String TAX_GROUPS = "taxgroup";
		public static final String QUOTE_ASSOCIATED_TERMS = "quoteterms";
		public static final String OLD_TAX_ID = "oldTaxId";
		public static final String OLD_RECORD_ID = "oldRecordId";
		public static final String OLD_RECORD_MAP = "oldRecordMap";
		public static final String QUOTE_NOTES = "quotenotes";
		public static final String QUOTE_ATTACHMENTS = "quoteattachments";
		public static final String QUOTE_MAIL_ATTACHMENTS = "quoteMailAttachments";
		public static final String QUOTE_PDF_URL = "quotePdfUrl";
		public static final String QUOTE_ACTIVITY = "quoteactivity";

		public static final String JOB_PLAN_ACTIVITY = "jobplanactivity";

		public static final String FETCH_MY_APPS = "fetchMyApps";
		public static final String APP_DOMAIN = "applicationDomain";

		public static final String NOTES_COMMENT = "Comment";
		public static final String RAW_COMMENT = "rawComment";
		public static final String MODULE_STATE = "moduleState";
		public static final String TERMS_NAME = "termsName";

		public static final String BASE_MAIL_MESSAGE = "customMailMessages";
		public static final String LATEST_MESSAGE_UID = "latestMessageUID";
		public static final String MESSAGES = "messages";
		public static final String MAIL_ATTACHMENT = "mailAttachments";
		public static final String ANNOUNCEMENT = "announcement";
		public static final String ANNOUNCEMENT_ATTACHMENTS = "announcementattachments";
		public static final String ANNOUNCEMENT_NOTES = "announcementnotes";
		public static final String PEOPLE_ANNOUNCEMENTS = "peopleannouncement";
		public static final String ANNOUNCEMENTS_SHARING_INFO = "announcementsharing";
		public static final String ANNOUNCEMENTS = "announcements";
		public static final String ANNOUNCEMENT_ACTION = "announcementaction";

		public static final String USER_NOTIFICATION = "usernotification";
		public static final String TRANSACTION = "transaction";

		public static final String ORGID = "orgId";
		public static final String IAM_USER_ID = "iamUserId";
		public static final String ORG_INITIALIZATION_STATUS = "OrgInitializationStatus";
		public static final String TIME_ZONE = "timezone";
		public static final String WEATHER_STATION_ID = "weatherStationId";

		public static final String JOB_PLAN = "jobplan";
		public static final String JOB_PLAN_LIST = "jobPlans";
		public static final String JOB_PLAN_SECTION = "jobplansection";
		public static final String JOB_PLAN_TASK = "jobplantask";
		public static final String JOB_PLAN_ATTACHMENTS = "jobplanattachments";
		public static final String JOB_PLAN_NOTES = "jobplannotes";

		public static final String JOB_PLAN_ITEMS = "jobPlanItems";
		public static final String JOB_PLAN_TOOLS = "jobPlanTools";
		public static final String JOB_PLAN_SERVICES = "jobPlanServices";

		public static final String JOB_PLAN_LABOURS = "jobPlanLabours";
		public static final String JOB_PLAN_TASK_INPUT_OPTIONS = "jobPlanTaskInputOptions";
		public static final String JOB_PLAN_SECTION_INPUT_OPTIONS = "jobPlanSectionInputOptions";

		public static final String JOB_PLAN_CRAFTS= "jobPlanCrafts";

		public static final String WO_PLANNED_ITEMS = "workOrderPlannedItems";
		public static final String WO_PLANNED_TOOLS = "workOrderPlannedTools";
		public static final String WO_PLANNED_SERVICES = "workOrderPlannedServices";

		public static final String INVENTORY_RESERVATION = "inventoryReservation";

		public static final String AUDIENCE_SHARING = "audienceSharing";
		public static final String AUDIENCE = "audience";

		public static final String DEPARTMENT = "department";
		public static final String MOVES = "moves";
		public static final String DELIVERIES = "deliveries";
		public static final String DELIVERY_AREA = "deliveryArea";
		public static final String LOCKERS = "lockers";
		public static final String PARKING_STALL = "parkingstall";

		public static final String SCATTER_GRAPH_LABEL = "scatterGraphLabel";
		public static final String SCATTER_GRAPH_VALUE = "scatterGraphValue";
		public static final String SCATTER_GRAPH_ID = "scatterGraphId";
		public static final String SCATTER_GRAPH_RESULT = "scatterGraphResult";
		public static final String SCATTER_BASELINE_DATA = "baselineData";
		public static final String SCATTER_BASELINE_COLOR = "baselineDataColor";
		public static final String TRANSFER_REQUEST = "transferrequest";
		public static final String TRANSFER_REQUEST_LINE_ITEM = "transferrequestlineitems";
		public static final String TRANSFER_REQUEST_PURCHASED_ITEMS = "transferrequestpurchaseditems";
		public static final String TRANSFER_REQUEST_SHIPMENT = "transferrequestshipment";
		public static final String TRANSFER_REQUEST_SHIPMENT_RECEIVABLES = "transferrequestshipmentreceivables";
		public static final String REQUEST_FOR_QUOTATION = "requestForQuotation";
		public static final String REQUEST_FOR_QUOTATION_VENDORS = "requestForQuotationVendors";
		public static final String REQUEST_FOR_QUOTATION_LINE_ITEMS = "requestForQuotationLineItems";
		public static final String REQUEST_FOR_QUOTATION_CONTEXT = "requestForQuotationContext";
		public static final String VENDOR_QUOTES = "vendorQuotes";
		public static final String VENDOR_QUOTES_LINE_ITEMS = "vendorQuotesLineItems";
		public static final String VENDOR_QUOTES_ACTIVITY = "vendorQuotesActivity";
		public static final String VENDOR_QUOTE_ID = "vendorQuoteId";
		public static final String REQUEST_FOR_QUOTATION_ACTIVITY = "requestForQuotationActivity";
		public static final String INVITE_ACCEPT_STATUS = "inviteacceptstatus";
		public static final String SEND_INVITE = "sendinvite";
		public static final String CONTACT_DIRECTORY = "contactdirectory";
		public static final String ADMIN_DOCUMENTS = "admindocuments";
		public static final String FACILITY_BOOKING = "facilitybooking";

		public static final String SCOPING_CONTEXT = "scopingContext";
		public static final String SCOPING_CONTEXT_LIST = "scopingContextList";
		public static final String SCOPING_ID = "scopingId";

		public static final String SPACE_BOOKING = "spacebooking";
		public static final String ROOMS = "rooms";
		public static final String CLOSE_ALL_FROM_BULK_ACTION ="closeAllFromBulkAction";

		public static class Maintenance {
			public static final String MAINTENANCE_ADMIN_SCOPING_ID = "maintenanceadminscopingid";
		}

		public static class WorkOrderLabourPlan {
			public static final String WORKORDER_LABOUR_PLAN = "workorderLabourPlan";
		}

		public static final String ROUTES = "routes";

		public static final String MULTIRESOURCE = "multiresource";
		public static final String VALUE_GENERATORS = "valueGenerators";
		public static final String RCA_RULE_IDS = "rca_rule_ids";
		public static final String CALL_FROM_STORM = "req_from_storm_service";

		public static final String PLANNEDMAINTENANCE = "plannedmaintenance";
		public static final String PLANNEDMAINTENANCE_START_TIME = "plannedmaintenanceStartTime";
		public static final String PLANNEDMAINTENANCE_END_TIME = "plannedmaintenanceEndTime";
		public static final String PLANNEDMAINTENANCE_MAP = "plannedmaintenanceMap";
		public static final String PLANNEDMAINTENANCE_JOB_LIST = "plannedmaintenanceJobList";
		public static final String PLANNEDMAINTENANCE_TRIGGER_LIST = "planedmaintenaceTriggerList";
		public static final String PLANNEDMAINTENANCE_RESOURCE_LIST = "plannedmaintenanceResourceList";
		public static final String PLANNED_MAINTENANCE_LIST = "plannedMaintenanceList";
		public static final String INSPECTION_TEMPLATE_LIST = "inspectionTemplateList";


		public static final String PMPLANNER = "pmPlanner";
		public static final String PLANNER_ID = "plannerId";
		public static class WorkPermit {
			public static final String WORK_PERMIT_TYPE_CHECKLIST = "workpermittypechecklist";
			public static final String WORK_PERMIT_TYPE_CHECKLIST_CATEGORY = "workpermittypechecklistcategory";
			public static final String WORK_PERMIT_CHECKLIST = "workpermitchecklist";
			public static final String WORK_PERMIT_TYPE = "workpermittype";
			public static final String WORK_PERMIT_ACTIVITY = "workpermitactivity";
			public static final String WORK_PERMIT_NOTES = "workpermitnotes";
			public static final String WORK_PERMIT_ATTACHMENTS = "workpermitattachments";
			public static final String WORKPERMIT = "workpermit";
		}

		public static class ValueGenerators {
			public static final String ACCESSIBLE_SPACES = "In Current User Accessible Spaces";
			public static final String AUDIENCE = "Audience Sharing";
			public static final String BUILDING = "In Buildings based on current user sites";
			public static final String PEOPLE_LIST = "In People List Based on Current User";
			public static final String PEOPLE = "Is Current People";
			public static final String SITE = "In Current User Sites";
			public static final String STOREROOM = "In Current User Storerooms";
			public static final String TENANT = "Is Current Tenant";
			public static final String USER = "Is Current User";
			public static final String CONTAINS_USER = "Contains Current User";
			public static final String CONTAINS_SITE = "Contains Current User Sites";
			public static final String SITE_TENANT = "Tenants Of Current User Sites";
			public static final String BASESPACE_HAVING = "Space Available in User Accessible Spaces";
			public static final String VENDOR = "Is Current Vendor";
			public static final String CURRENT_USER = "Is Current User";
			public static final String CURRENT_ORG_USER_USER = "Is Current Org User";
			public static final String CLIENT = "Is Current Client";

		}

		public static class Budget {
			public static final String ACCOUNT_TYPE = "accounttype";
			public static final String CHART_OF_ACCOUNT = "chartofaccount";
			public static final String CHART_OF_ACCOUNT_ATTACHMENTS = "chartofaccountattachments";
			public static final String CHART_OF_ACCOUNT_NOTES = "chartofaccountnotes";
			public static final String BUDGET = "budget";
			public static final String BUDGET_ATTACHMENTS = "budgetattachments";
			public static final String BUDGET_NOTES = "budgetnotes";
			public static final String BUDGET_AMOUNT = "budgetamount";
			public static final String BUDGET_MONTHLY_AMOUNT = "budgetmonthlyamount";

		}

		public static class NewTabPermission {
			public static final String MODULE_APP_PERMISSION = "moduleAppPermission";
			public static final String MODULE_PERMISSION = "modulePermission";
		}

		public static class FacilityBooking {
			public static final String FACILITY = "facility";
			public static final String FACILITY_ATTACHMENTS = "facilityattachments";
			public static final String FACILITY_NOTES = "facilitynotes";
			public static final String FACILITY_AMENITIES = "facilityamenity";
			public static final String FACILITY_WEEKDAY_AVAILABILITY = "facilityWeekdayAvailability";
			public static final String FACILITY_SPECIAL_AVAILABILITY = "facilitySpecialAvailability";
			public static final String FACILITY_BOOKING = "facilitybooking";
			public static final String PARKING_BOOKING = "parkingbooking";
			public static final String FACILITY_BOOKING_INTERNAL_ATTENDEE = "facilityBookingInternalAttendee";
			public static final String FACILITY_BOOKING_EXTERNAL_ATTENDEE = "facilityBookingExternalAttendee";
			public static final String FACILITY_BOOKING_ATTACHMENTS = "facilitybookingattachments";
			public static final String FACILITY_ID = "facilityId";
			public static final String START_DATE_TIME = "startDateTime";
			public static final String SLOTS = "slot";
			public static final String AMENITY = "amenity";
			public static final String BOOKING_SLOTS = "bookingslot";
			public static final String FACILITY_BOOKING_PAYMENTS = "bookingpayment";
			public static final String RESERVED_FOR = "reservedFor";
			public static final String FACILITY_PHOTOS = "facilityphotos";
			public static final String END_DATE_TIME = "endDateTime";
			public static final String BOOKING_REQUESTER = "bookingRequestedBy";
			public static final String BOOKING_RESERVEDFOR = "reservedFor";

		}

		public static class Tenant {
			public static final String ANNOUNCEMENT = "announcement";
			public static final String ANNOUNCEMENT_ATTACHMENTS = "announcementattachments";
			public static final String ANNOUNCEMENT_NOTES = "announcementnotes";
			public static final String PEOPLE_ANNOUNCEMENTS = "peopleannouncement";
			public static final String ANNOUNCEMENTS_SHARING_INFO = "announcementsharing";
			public static final String ANNOUNCEMENTS = "announcements";
			public static final String ANNOUNCEMENT_ACTION = "announcementaction";

			public static final String NEWS_AND_INFORMATION = "newsandinformation";
			public static final String NEWS_AND_INFORMATION_ATTACHMENTS = "newsandinformationattachments";
			public static final String NEWS_AND_INFORMATION_SHARING = "newsandinformationsharing";
			public static final String NEWS_AND_INFORMATION_NOTES = "newsandinformationnotes";

			public static final String NEIGHBOURHOOD = "neighbourhood";
			public static final String NEIGHBOURHOOD_ATTACHMENTS = "neighbourhoodattachments";
			public static final String NEIGHBOURHOOD_NOTES = "neighbourhoodnotes";
			public static final String NEIGHBOURHOOD_SHARING = "neighbourhoodsharing";

			public static final String DEALS_AND_OFFERS = "dealsandoffers";
			public static final String DEALS_AND_OFFERS_ATTACHMENTS = "dealsandoffersattachments";
			public static final String DEALS_AND_OFFERS_NOTES = "dealsandoffersnotes";
			public static final String DEALS_AND_OFFERS_SHARING = "dealsandofferssharing";

			public static final String CONTACT_DIRECTORY = "contactdirectory";
			public static final String CONTACT_DIRECTORY_ATTACHMENTS = "contactdirectoryattachments";
			public static final String CONTACT_DIRECTORY_NOTES = "contactdirectorynotes";
			public static final String CONTACT_DIRECTORY_SHARING = "contactdirectorysharing";

			public static final String ADMIN_DOCUMENTS = "admindocuments";
			public static final String ADMIN_DOCUMENTS_NOTES = "admindocumentnotes";
			public static final String ADMIN_DOCUMENTS_SHARING = "admindocumentsharing";

			public static final String AUDIENCE = "audience";
			public static final String AUDIENCE_SHARING = "audienceSharing";
			public static final String ANNOUNCEMENT_AUDIENCE = "Announcement_Audience";
			public static final String NEIGHBOURHOOD_AUDIENCE = "Neighbourhood_Audience";
			public static final String DEALSANDOFFERS_AUDIENCE= "Dealsandoffers_Audience";
			public static final String NEWSANDINFORMATION_AUDIENCE= "Newsandinformation_Audience";
			public static final String ADMINDOCUMENTS_AUDIENCE = "Admindocuments_Audience";
			public static final String CONTACTDIRECTORY_AUDIENCE= "Contactdirectory_Audience";

		}

		public static class Floorplan {
			public static final String SPACE_BOOKING_MAP = "spacebookingmap";
			public static final String INDOOR_FLOORPLAN_ID = "indoorfloorplanId";
			public static final String INDOOR_FLOORPLAN = "indoorfloorplan";
			public static final String INDOOR_FLOORPLAN_OBJECTS = "indoorfloorplanobjects";
			public static final String MARKER_TYPE = "markertype";
			public static final String MARKER = "floorplanmarker";
			public static final String MARKED_ZONES = "floorplanmarkedzone";
			public static final String DESKS = "desks";
			public static final String FETCH_ONLY_DESKS = "fetchOnlydesks";
			public static final String BOOKED_DESKS = "bookedDesks";
			public static final String FEATURES = "features";
			public static final String MARKERS = "markers";
			public static final String MARKERS_FIELDS = "markersFields";
			public static final String RECORD_FIELDS = "recordFields";
			public static final String ZONES = "zones";
			public static final String OBJECT_IDS = "objectIds";
			public static final String VIEW_MODE = "viewMode";
			public static final String ASSIGNMENT_VIEW = "ASSIGNMENT";
			public static final String BOOKING_VIEW = "BOOKING";
			public static final String OBJECTID = "objectId";
			public static final String PROPERTIES = "properties";
			public static final String OBJECT = "object";
			public static final String OBJECT_FIEDS = "objectFields";
			public static final String RECORD = "record";
			public static final String MARKER_RECORD_OBJECTMAP = "markerRecordObjectMap";
			public static final String ZONE_RECORD_OBJECTMAP = "zoneRecordObjectMap";
			public static final String MARKER_LIST = "markerList";
			public static final String ZONE_LIST = "zoneList";
			public static final String SPACE_MAP = "spaceMap";
			public static final String CUSTOMIZATION = "customizationJSON";
			public static final String CUSTOMIZATION_BOOKING = "customizationBookingJSON";
			public static final String PARKING = "parkingstall";
			public static final String FLOORPLAN_LAYER = "floorplanlayer";
			public static final String FLOORPLAN_CLIENT_LAYER = "floorplanlayers";

		}

		public static class FormValidationRuleConstants {
			public static final String RULE = "formValidationRuleContext";
			public static final String RULES = "formValidationRuleContexts";
			public static final String RESULT = "formValidationRuleResult";
		}

		public static class SpaceCategory {
			public static final String DESK = "Desk";
			public static final String LOCKERS = "Lockers";
			public static final String PARKING_STALL = "Parking Stall";
		}


		public static class SpaceBooking {
			public static final String SPACE_BOOKING = "spacebooking";
			public static final String HOST = "host";
			public static final String RESERVED_BY = "reservedBy";
			public static final String BOOKING_START_TIME = "bookingStartTime";
			public static final String BOOKING_END_TIME = "bookingEndTime";
			public static final String DESK_BOOKING = "deskbooking";
			public static final String PARKING_BOOKING = "parkingbooking";
			public static final String EXTERNAL_ATTENDEE = "externalAttendee";
			public static final String SPACE_BOOKING_EXTERNAL_ATTENDEE = "spaceBookingExternalAttendee";
			public static final String SPACE_BOOKING_INTERNAL_ATTENDEE = "spaceBookingInternalAttendee";
			public static final String FORM = "formId";
			public static final String SPACE_MODULE_ID = "spaceModuleId";
			public static final String AMENITY = "amenity";
			public static final String SPACE_ID = "space";
			public static final String PARENT_MODULE_ID = "parentModuleId";
			public static final String PARENT_MODULE = "parentModule";
			public static final String PARENT_MODULE_NAME = "parentModuleName";
			public static final String BUILDING_ID = "buildingId";
			public static final String OPERATING_HOUR = "operatingHour";
			public static final String CURRENT_TIME = "currentTime";
			public static final String SPACE_BOOKING_POLICY = "spaceBookingPolicy";
			public static final String CRITERIAID = "criteriaId";
			public static final String POLICY_JSON = "policyJson";
			public static final String CRITERIA ="criteria";

			public static final String SPACE_BOOKING_LIST = "spacebookinglist";
			public static final String EXTEND_TIME = "extendTime";

			public static final String CANCEL = "cancel";

			public static final String EXTEND="extend";


		}
		public static class ControlGroup {
			public static final String CONTROL_GROUP_V2 = "controlGroupv2";
			public static final String CONTROL_GROUP_V2_TENANT_SHARING = "controlGroupv2TenantSharing";
			public static final String CONTROL_SCHEDULE = "controlSchedule";
			public static final String CONTROL_SCHEDULE_EXCEPTION = "controlScheduleException";
			public static final String CONTROL_SCHEDULE_EXCEPTION_TENANT = "controlScheduleExceptionTenant";
		}
		public static class WebTab {
			public static final String MODULE_NAME = "moduleName";
			public static final String PARENT_MODULE_NAME = "parentModuleName";
		}

		public static class SpaceCategoryFormRelation {
			public static final String SPACE_CATEGORY_FORM_RELATION = "spaceCategoryFormRelation";
			public static final String MODULE_FORM_ID = "moduleFormId";

		}

		// etisalat changes
		public static final String BILL_ALERT = "custom_alert";
		public static final String BILL_INVOICE = "custom_invoices";
		public static final String BILL_TARIFF = "custom_tariffinfo";
		public static final String BILL_UTILITY = "custom_utilityaccounts_1";
		//

		public static final String SCOPE_VARIABLE = "scopeVariable";
		public static final String SCOPE_VARIABLE_LIST = "scopeVariableList";

		public static final String SWITCH_VARIABLE = "switchVariable";

		public static final String ACTIVITY_MODULE_NAME_FROM_SCRIPT = "activityModuleNameFromScript";

		public static final String READ_PERMISSION = "READ";

		public static final String READ_PERMISSIONS = "READ,READ_TEAM,READ_OWN,VIEW";
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
			// classMap.put(CONTROLLER, ControllerSettingsContext.class);
			classMap.put(WORK_ORDER_REQUEST, WorkOrderRequestContext.class);
			classMap.put(ALARM_SEVERITY, AlarmSeverityContext.class);
			classMap.put(ALARM, AlarmContext.class);
			classMap.put(SENSOR_ALARM, SensorAlarmContext.class);
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
			classMap.put(ASSET_MOVEMENT, AssetMovementContext.class);
			classMap.put(ASSET_BREAKDOWN, AssetBreakdownContext.class);
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
			classMap.put(CUSTOM_ACTIVITY, ActivityContext.class);
			classMap.put(SITE_ACTIVITY, ActivityContext.class);
			classMap.put(BUILDING_ACTIVITY, ActivityContext.class);
			classMap.put(FLOOR_ACTIVITY, ActivityContext.class);
			classMap.put(FLOOR_ACTIVITY, ActivityContext.class);
			classMap.put(SPACE_ACTIVITY, ActivityContext.class);
			classMap.put(VENDOR_ACTIVITY, ActivityContext.class);
			classMap.put(TENANT_ACTIVITY, ActivityContext.class);
			classMap.put(SERVICE_REQUEST_ACTIVITY, ActivityContext.class);
			classMap.put(Shift.SHIFT_ACTIVITY, ActivityContext.class);
			classMap.put(Meter.METER_ACTIVITY, ActivityContext.class);
			classMap.put(Meter.VIRTUAL_METER_TEMPLATE_ACTIVITY, ActivityContext.class);

			classMap.put(MODBUS_TCP_CONTROLLER_MODULE_NAME, ModbusTcpControllerContext.class);
			classMap.put(MODBUS_RTU_CONTROLLER_MODULE_NAME, ModbusRtuControllerContext.class);
			classMap.put(BACNET_IP_CONTROLLER_MODULE_NAME, BacnetIpControllerContext.class);
			classMap.put(E2_CONTROLLER_MODULE_NAME, E2ControllerContext.class);
			classMap.put(OPC_UA_CONTROLLER_MODULE_NAME, OpcUaControllerContext.class);
			classMap.put(OPC_XML_DA_CONTROLLER_MODULE_NAME, OpcXmlDaControllerContext.class);
			classMap.put(NIAGARA_CONTROLLER_MODULE_NAME, NiagaraControllerContext.class);
			classMap.put(CONTROLLER_MODULE_NAME, Controller.class);
			classMap.put(MISC_CONTROLLER_MODULE_NAME, MiscControllerContext.class);
			classMap.put(AGENT_METRICS_MODULE, AgentMetrics.class);
			classMap.put(SYSTEM_CONTROLLER_MODULE_NAME, SystemControllerContext.class);
			classMap.put(LON_WORKS_CONTROLLER_MODULE_NAME, LonWorksControllerContext.class);
			classMap.put(RDM_CONTROLLER_MODULE_NAME, RdmControllerContext.class);
			classMap.put(REST_CONTROLLER_MODULE_NAME, MiscControllerContext.class);
			classMap.put(CUSTOM_CONTROLLER_MODULE_NAME, MiscControllerContext.class);

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
			classMap.put(TOOL_TYPES, ToolTypesContext.class);
			classMap.put(ITEM_ACTIVITY, ActivityContext.class);
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
			classMap.put(LABOUR, LabourContext.class);
			classMap.put(WO_LABOUR, WorkOrderLabourContext.class);
			classMap.put(PURCHASE_REQUEST, V3PurchaseRequestContext.class);
			classMap.put(PURCHASE_REQUEST_LINE_ITEMS, V3PurchaseRequestLineItemContext.class);
			classMap.put(PURCHASE_ORDER, V3PurchaseOrderContext.class);
			classMap.put(PURCHASE_ORDER_LINE_ITEMS, V3PurchaseOrderLineItemContext.class);
			classMap.put(PURCHASE_CONTRACTS, PurchaseContractContext.class);
			classMap.put(PURCHASE_CONTRACTS_LINE_ITEMS, PurchaseContractLineItemContext.class);
			classMap.put(LABOUR_CONTRACTS, LabourContractContext.class);
			classMap.put(LABOUR_CONTRACTS_LINE_ITEMS, LabourContractLineItemContext.class);
			classMap.put(RECEIVABLE, ReceivableContext.class);
			classMap.put(RECEIPT, ReceiptContext.class);
			classMap.put(RECEIPT_LINE_ITEMS, ReceiptLineItemContext.class);
			classMap.put(SHIPMENT, ShipmentContext.class);
			classMap.put(SHIPMENT_LINE_ITEM, ShipmentLineItemContext.class);
			classMap.put(CONTRACTS, ContractsContext.class);

			classMap.put(ML, MLContext.class);
			classMap.put(PO_LINE_ITEMS_SERIAL_NUMBERS, PoLineItemsSerialNumberContext.class);
			classMap.put(GATE_PASS, GatePassContext.class);
			classMap.put(GATE_PASS_LINE_ITEMS, GatePassLineItemsContext.class);
			classMap.put(CONNECTED_APPS, ConnectedAppContext.class);
			classMap.put(INVENTORY_REQUEST, V3InventoryRequestContext.class);
			classMap.put(INVENTORY_REQUEST_LINE_ITEMS, V3InventoryRequestLineItemContext.class);
			classMap.put(SERVICE, V3ServiceContext.class);
			classMap.put(WARRANTY_CONTRACTS, WarrantyContractContext.class);
			classMap.put(WARRANTY_CONTRACTS_LINE_ITEMS, WarrantyContractLineItemContext.class);
			classMap.put(WO_SERVICE, WorkOrderServiceContext.class);

			classMap.put(SERVICE_VENDOR, ServiceVendorContext.class);
			classMap.put(RENTAL_LEASE_CONTRACTS, RentalLeaseContractContext.class);
			classMap.put(RENTAL_LEASE_CONTRACTS_LINE_ITEMS, RentalLeaseContractLineItemsContext.class);
			classMap.put(RENTAL_LEASE_CONTRACTS_LINE_ITEMS, RentalLeaseContractLineItemsContext.class);
			classMap.put(TOOL_VENDORS, ToolTypeVendorContext.class);
			classMap.put(TERMS_AND_CONDITIONS, V3TermsAndConditionContext.class);
			classMap.put(CONTRACT_ASSOCIATED_ASSETS, ContractAssociatedAssetsContext.class);
			classMap.put(CONTRACT_ASSOCIATED_TERMS, ContractAssociatedTermsContext.class);
			classMap.put(ATTENDANCE, Attendance.class);
			classMap.put(ATTENDANCE_TRANSACTIONS, AttendanceTransaction.class);
			classMap.put(SHIFT, com.facilio.bmsconsoleV3.context.shift.Shift.class);
			classMap.put(BREAK, BreakContext.class);
			classMap.put(GRAPHICS, GraphicsContext.class);
			classMap.put(SHIFT_ROTATION, ShiftRotationContext.class);
			classMap.put(BREAK_TRANSACTION, BreakTransactionContext.class);

			classMap.put(MV_PROJECT_MODULE, MVProjectContext.class);
			classMap.put(MV_BASELINE_MODULE, MVBaseline.class);
			classMap.put(MV_ADJUSTMENT_MODULE, MVAdjustment.class);
			classMap.put(FORMULA_FIELD, FormulaFieldContext.class);

			classMap.put(ALARM_OCCURRENCE, AlarmOccurrenceContext.class);
			classMap.put(BASE_ALARM, BaseAlarmContext.class);
			classMap.put(NEW_READING_ALARM, ReadingAlarm.class);
			classMap.put(BASE_EVENT, BaseEventContext.class);
			classMap.put(READING_EVENT, ReadingEventContext.class);
			classMap.put(READING_ALARM_OCCURRENCE, ReadingAlarmOccurrenceContext.class);
			classMap.put(PRE_ALARM, PreAlarmContext.class);
			classMap.put(PRE_ALARM_OCCURRENCE, PreAlarmOccurrenceContext.class);
			classMap.put(PRE_EVENT, PreEventContext.class);
			classMap.put(READING_ALARM_CATEGORY, ReadingAlarmCategoryContext.class);
			classMap.put(AGENT_ALARM, AgentAlarmContext.class);
			classMap.put(OPERATION_ALARM, OperationAlarmContext.class);
			classMap.put(OPERATION_OCCURRENCE, OperationAlarmOccurenceContext.class);
			classMap.put(RULE_ROLLUP_EVENT, RuleRollUpEvent.class);
			classMap.put(RULE_ROLLUP_OCCURRENCE, RuleRollUpOccurrence.class);
			classMap.put(RULE_ROLLUP_ALARM, RuleRollUpAlarm.class);
			classMap.put(ASSET_ROLLUP_EVENT, AssetRollUpEvent.class);
			classMap.put(ASSET_ROLLUP_OCCURRENCE, AssetRollUpOccurrence.class);
			classMap.put(ASSET_ROLLUP_ALARM, AssetRollUpAlarm.class);
			classMap.put(SENSOR_ALARM, SensorAlarmContext.class);
			classMap.put(SENSOR_ALARM_OCCURRENCE, SensorAlarmOccurrenceContext.class);
			classMap.put(SENSOR_EVENT, SensorEventContext.class);
			classMap.put(SENSOR_ROLLUP_ALARM, SensorRollUpAlarmContext.class);
			classMap.put(SENSOR_ROLLUP_ALARM_OCCURRENCE, SensorRollUpAlarmOccurrenceContext.class);
			classMap.put(SENSOR_ROLLUP_EVENT, SensorRollUpEventContext.class);
			classMap.put(MULTIVARIATE_ANOMALY_ALARM, MultiVariateAnomalyAlarm.class);
			classMap.put(MULTIVARIATE_ANOMALY_ALARM_OCCURRENCE, MultiVariateAnomalyAlarmOccurrence.class);
			classMap.put(MULTIVARIATE_ANOMALY_EVENT, MultiVariateAnomalyEvent.class);

			classMap.put(VISITOR, VisitorContext.class);
			classMap.put(VISITOR_INVITE, VisitorInviteContext.class);
			classMap.put(VISITOR_LOGGING, VisitorLoggingContext.class);
			classMap.put(VISITOR_INVITE_REL, InviteVisitorRelContext.class);
			classMap.put(VISITOR_TYPE, VisitorTypeContext.class);
			classMap.put(BASE_VISIT, BaseVisitContextV3.class);
			classMap.put(VISITOR_LOG, VisitorLogContextV3.class);
			classMap.put(VISITOR_LOG_ATTACHMENTS, AttachmentV3Context.class);
			classMap.put(INVITE_VISITOR, InviteVisitorContextV3.class);
			classMap.put(INVITE_VISITOR_ATTACHMENTS, AttachmentV3Context.class);
			classMap.put(GROUP_VISITOR_INVITE, GroupInviteContextV3.class);

			classMap.put(ModuleNames.DEVICES, DeviceContext.class);

			classMap.put(Reservation.RESERVATION, ReservationContext.class);
			classMap.put(Reservation.RESERVATIONS_INTERNAL_ATTENDEE, InternalAttendeeContext.class);
			classMap.put(Reservation.RESERVATIONS_EXTERNAL_ATTENDEE, ExternalAttendeeContext.class);

			classMap.put(CONTACT, ContactsContext.class);
			classMap.put(INSURANCE, InsuranceContext.class);
			classMap.put(WATCHLIST, WatchListContext.class);
			classMap.put(PRINTERS, PrinterContext.class);

			classMap.put(OCCUPANT, OccupantsContext.class);
			classMap.put(WEB_TAB_GROUP, WebTabGroupContext.class);

			classMap.put(SERVICE_REQUEST, V3ServiceRequestContext.class);
			classMap.put(SERVICE_REQUEST_PRIORITY, ServiceRequestPriorityContext.class);
			classMap.put(DOCUMENT, DocumentContext.class);

			classMap.put(SLA_RULE_MODULE, SLAWorkflowCommitmentRuleContext.class);

			classMap.put(VENDOR_DOCUMENTS, DocumentContext.class);

			classMap.put(TENANT, TenantContext.class);

			classMap.put(SAFETY_PLAN, SafetyPlanContext.class);
			classMap.put(HAZARD, HazardContext.class);
			classMap.put(PRECAUTION, PrecautionContext.class);
			classMap.put(SAFETYPLAN_HAZARD, SafetyPlanHazardContext.class);
			classMap.put(HAZARD_PRECAUTION, HazardPrecautionContext.class);
			classMap.put(WORKORDER_HAZARD, WorkorderHazardContext.class);
			classMap.put(ASSET_HAZARD, AssetHazardContext.class);
			classMap.put(CLIENT, ClientContext.class);
			classMap.put(SPACE_RATING, RatingContext.class);
			classMap.put(ASSET_HAZARD, AssetHazardContext.class);
			classMap.put(FLOOR_PLAN, FloorPlanContext.class);
			classMap.put(FLOOR_PLAN_VIEW, FloorPlanContext.class);
			classMap.put(FLOORPLAN_OBJECT, FloorPlanObjectContext.class);
			classMap.put(PEOPLE, PeopleContext.class);
			classMap.put(EMPLOYEE, EmployeeContext.class);
			classMap.put(TENANT_CONTACT, TenantContactContext.class);
			classMap.put(CLIENT_CONTACT, ClientContactContext.class);
			classMap.put(VENDOR_CONTACT, VendorContactContext.class);
			classMap.put(TENANT_SPACES, TenantSpaceContext.class);

			classMap.put(TENANT_UNIT_SPACE, TenantUnitSpaceContext.class);

			classMap.put(ASSET_DEPRECIATION, AssetDepreciationContext.class);

			classMap.put(QUOTE, QuotationContext.class);
			classMap.put(QUOTE_LINE_ITEMS, QuotationLineItemsContext.class);
			classMap.put(TAX, TaxContext.class);
			classMap.put(TAX_GROUPS, TaxGroupContext.class);
			classMap.put(QUOTE_ASSOCIATED_TERMS, QuotationAssociatedTermsContext.class);
			classMap.put(PO_ASSOCIATED_TERMS, V3PoAssociatedTermsContext.class);
			classMap.put(PR_ASSOCIATED_TERMS, PrAssociatedTermsContext.class);

			classMap.put(PURCHASE_ORDER_ACTIVITY, ActivityContext.class);
			classMap.put(PURCHASE_REQUEST_ACTIVITY, ActivityContext.class);
			classMap.put(QUOTE_ACTIVITY, ActivityContext.class);

			classMap.put(WorkPermit.WORKPERMIT, WorkPermitContext.class);
			classMap.put(WorkPermit.WORK_PERMIT_CHECKLIST, WorkPermitChecklistContext.class);
			classMap.put(WorkPermit.WORK_PERMIT_TYPE_CHECKLIST, WorkPermitTypeChecklistContext.class);
			classMap.put(WorkPermit.WORK_PERMIT_TYPE_CHECKLIST_CATEGORY, WorkPermitTypeChecklistCategoryContext.class);
			classMap.put(WorkPermit.WORK_PERMIT_TYPE, WorkPermitTypeContext.class);
			classMap.put(WorkPermit.WORK_PERMIT_ACTIVITY, ActivityContext.class);

			classMap.put(ContextNames.BASE_MAIL_MESSAGE, BaseMailMessageContext.class);
			classMap.put(ContextNames.MAIL_ATTACHMENT, AttachmentV3Context.class);
			classMap.put(ContextNames.TICKET_ATTACHMENTS, AttachmentV3Context.class);

			classMap.put(Tenant.PEOPLE_ANNOUNCEMENTS, PeopleAnnouncementContext.class);
			classMap.put(Tenant.ANNOUNCEMENT_ATTACHMENTS, AttachmentV3Context.class);
			classMap.put(Tenant.ANNOUNCEMENTS_SHARING_INFO, AnnouncementSharingInfoContext.class);
			classMap.put(Tenant.ANNOUNCEMENT, AnnouncementContext.class);
			classMap.put(ContextNames.USER_NOTIFICATION, UserNotificationContext.class);
			classMap.put(Tenant.NEWS_AND_INFORMATION, NewsAndInformationContext.class);
			classMap.put(Tenant.NEIGHBOURHOOD, NeighbourhoodContext.class);
			classMap.put(Tenant.DEALS_AND_OFFERS, DealsAndOffersContext.class);
			classMap.put(Tenant.NEWS_AND_INFORMATION_ATTACHMENTS, AttachmentV3Context.class);
			classMap.put(Tenant.NEWS_AND_INFORMATION_SHARING, NewsAndInformationSharingContext.class);
			classMap.put(Tenant.DEALS_AND_OFFERS_ATTACHMENTS, AttachmentV3Context.class);
			classMap.put(Tenant.NEIGHBOURHOOD_ATTACHMENTS, AttachmentV3Context.class);
			classMap.put(Tenant.CONTACT_DIRECTORY_ATTACHMENTS, AttachmentV3Context.class);
			classMap.put(Tenant.DEALS_AND_OFFERS_SHARING, DealsAndOffersSharingContext.class);
			classMap.put(Tenant.NEIGHBOURHOOD_SHARING, NeighbourhoodSharingContext.class);
			classMap.put(Tenant.ADMIN_DOCUMENTS, AdminDocumentsContext.class);
			classMap.put(Tenant.ADMIN_DOCUMENTS_SHARING, AdminDocumentsSharingContext.class);
			classMap.put(Tenant.CONTACT_DIRECTORY, ContactDirectoryContext.class);
			classMap.put(Tenant.CONTACT_DIRECTORY_SHARING, ContactDirectorySharingContext.class);
			classMap.put(Tenant.AUDIENCE, AudienceContext.class);
			classMap.put(Tenant.AUDIENCE_SHARING, CommunitySharingInfoContext.class);

			classMap.put(Budget.ACCOUNT_TYPE, AccountTypeContext.class);
			classMap.put(Budget.CHART_OF_ACCOUNT, ChartOfAccountContext.class);
			classMap.put(Budget.BUDGET, BudgetContext.class);
			classMap.put(Budget.BUDGET_AMOUNT, BudgetAmountContext.class);
			classMap.put(Budget.BUDGET_MONTHLY_AMOUNT, BudgetMonthlyAmountContext.class);
			classMap.put(TRANSACTION, V3TransactionContext.class);

			classMap.put(FacilityBooking.FACILITY, FacilityContext.class);
			classMap.put(FacilityBooking.FACILITY_AMENITIES, FacilityAmenitiesContext.class);
			classMap.put(FacilityBooking.FACILITY_WEEKDAY_AVAILABILITY, WeekDayAvailability.class);
			classMap.put(FacilityBooking.FACILITY_SPECIAL_AVAILABILITY, FacilitySpecialAvailabilityContext.class);
			classMap.put(FacilityBooking.FACILITY_BOOKING, V3FacilityBookingContext.class);
			classMap.put(FacilityBooking.FACILITY_BOOKING_INTERNAL_ATTENDEE, V3InternalAttendeeContext.class);
			classMap.put(FacilityBooking.FACILITY_BOOKING_EXTERNAL_ATTENDEE, V3ExternalAttendeeContext.class);
			classMap.put(FacilityBooking.SLOTS, SlotContext.class);
			classMap.put(FacilityBooking.BOOKING_SLOTS, BookingSlotsContext.class);
			classMap.put(FacilityBooking.FACILITY_BOOKING_PAYMENTS, BookingPaymentContext.class);
			classMap.put(FacilityBooking.FACILITY_AMENITIES, FacilityAmenitiesContext.class);
			classMap.put(FacilityBooking.AMENITY, AmenitiesContext.class);
			classMap.put(FacilityBooking.FACILITY_ATTACHMENTS, AttachmentV3Context.class);
			classMap.put(FacilityBooking.FACILITY_BOOKING_ATTACHMENTS, AttachmentV3Context.class);
			classMap.put(FacilityBooking.FACILITY_PHOTOS, V3PhotosContext.class);

			classMap.put(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME, ControlScheduleContext.class);
			classMap.put(ControlScheduleUtil.CONTROL_SCHEDULE_TENANT_SHARING_MODULE_NAME,
					ControlScheduleTenantContext.class);

			classMap.put(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, ControlGroupContext.class);
			classMap.put(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME, ControlGroupTenentContext.class);

			classMap.put(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME,
					ControlScheduleExceptionContext.class);
			classMap.put(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME,
					ControlScheduleExceptionTenantContext.class);

			classMap.put(ControlScheduleUtil.CONTROL_GROUP_SECTION_MODULE_NAME, ControlGroupSection.class);

			classMap.put(ControlScheduleUtil.CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME, ControlGroupAssetCategory.class);
			classMap.put(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME, ControlGroupAssetContext.class);
			classMap.put(ControlScheduleUtil.CONTROL_GROUP_ASSET_FIELD_MODULE_NAME, ControlGroupFieldContext.class);

			classMap.put(JOB_PLAN, JobPlanContext.class);
			classMap.put(JOB_PLAN_SECTION, JobPlanTaskSectionContext.class);
			classMap.put(JOB_PLAN_TASK, JobPlanTasksContext.class);
			classMap.put(JOB_PLAN_ATTACHMENTS, AttachmentV3Context.class);

			classMap.put(AUDIT_LOGS, AuditLogHandler.AuditLogContext.class);

			classMap.put(Floorplan.INDOOR_FLOORPLAN, V3IndoorFloorPlanContext.class);
			classMap.put(Floorplan.INDOOR_FLOORPLAN_OBJECTS, V3IndoorFloorPlanObjectContext.class);
			classMap.put(Floorplan.MARKER_TYPE, V3FloorplanMarkersContext.class);
			classMap.put(Floorplan.MARKER, V3MarkerContext.class);
			classMap.put(Floorplan.MARKED_ZONES, V3MarkerdZonesContext.class);
			classMap.put(Floorplan.DESKS, V3DeskContext.class);
			classMap.put(DEPARTMENT, V3DepartmentContext.class);
			classMap.put(MOVES, V3MovesContext.class);
			classMap.put(DELIVERIES, V3DeliveriesContext.class);
			classMap.put(DELIVERY_AREA, V3DeliveryAreaContext.class);
			classMap.put(LOCKERS, V3LockersContext.class);
			classMap.put(PARKING_STALL, V3ParkingStallContext.class);

			classMap.put(QAndA.Q_AND_A_TEMPLATE, QAndATemplateContext.class);
			classMap.put(QAndA.PAGE, PageContext.class);
			classMap.put(QAndA.QUESTION, QuestionContext.class);
			classMap.put(QAndA.RESPONSE, ResponseContext.class);
			classMap.put(QAndA.ANSWER, AnswerContext.class);

			classMap.put(Inspection.INSPECTION_TEMPLATE, InspectionTemplateContext.class);
			classMap.put(Inspection.INSPECTION_RESPONSE, InspectionResponseContext.class);
			classMap.put(Inspection.INSPECTION_CATEGORY, InspectionCategoryContext.class);
			classMap.put(Inspection.INSPECTION_PRIORITY, InspectionPriorityContext.class);
			classMap.put(Inspection.INSPECTION_TRIGGER, InspectionTriggerContext.class);

			classMap.put(Inspection.INSPECTION_RESPONSE_ACTIVITY, ActivityContext.class);
			classMap.put(Induction.INDUCTION_RESPONSE_ACTIVITY, ActivityContext.class);
			classMap.put(TRANSFER_REQUEST, V3TransferRequestContext.class);
			classMap.put(TRANSFER_REQUEST_LINE_ITEM, V3TransferRequestLineItemContext.class);
			classMap.put(TRANSFER_REQUEST_PURCHASED_ITEMS, V3TransferRequestPurchasedItems.class);
			classMap.put(TRANSFER_REQUEST_SHIPMENT, V3TransferRequestShipmentContext.class);
			classMap.put(TRANSFER_REQUEST_SHIPMENT_RECEIVABLES, V3TransferRequestShipmentReceivablesContext.class);
			classMap.put(REQUEST_FOR_QUOTATION_VENDORS, V3RequestForQuotationVendorsContext.class);
			classMap.put(REQUEST_FOR_QUOTATION, V3RequestForQuotationContext.class);
			classMap.put(REQUEST_FOR_QUOTATION_LINE_ITEMS, V3RequestForQuotationLineItemsContext.class);
			classMap.put(REQUEST_FOR_QUOTATION_ACTIVITY, ActivityContext.class);
			classMap.put(VENDOR_QUOTES, V3VendorQuotesContext.class);
			classMap.put(VENDOR_QUOTES_LINE_ITEMS, V3VendorQuotesLineItemsContext.class);
			classMap.put(JOB_PLAN_ACTIVITY, ActivityContext.class);
			classMap.put(JOB_PLAN_ITEMS, JobPlanItemsContext.class);
			classMap.put(JOB_PLAN_TOOLS, JobPlanToolsContext.class);
			classMap.put(JOB_PLAN_SERVICES, JobPlanServicesContext.class);
			classMap.put(WO_PLANNED_ITEMS, WorkOrderPlannedItemsContext.class);
			classMap.put(WO_PLANNED_TOOLS, WorkOrderPlannedToolsContext.class);
			classMap.put(WO_PLANNED_SERVICES, WorkOrderPlannedServicesContext.class);
			classMap.put(SpaceBooking.SPACE_BOOKING, V3SpaceBookingContext.class);
			classMap.put(INVENTORY_RESERVATION, InventoryReservationContext.class);
			classMap.put(VENDOR_QUOTES_ACTIVITY, ActivityContext.class);
			classMap.put(UTILITY_INTEGRATION_CUSTOMER_ACTIVITY,ActivityContext.class);
			classMap.put(UTILITY_INTEGRATION_BILL_ACTIVITY,ActivityContext.class);
			classMap.put(UTILITY_DISPUTE_ACTIVITY,ActivityContext.class);
			classMap.put(AlarmTypeModule.MODULE_NAME, AlarmTypeContext.class);
			classMap.put(AlarmCategoryModule.MODULE_NAME, AlarmCategoryContext.class);
			classMap.put(AlarmDefinitionModule.MODULE_NAME, AlarmDefinitionContext.class);
			classMap.put(AlarmDefinitionMappingModule.MODULE_NAME, AlarmDefinitionMappingContext.class);
			classMap.put(AlarmFilterRuleModule.MODULE_NAME, AlarmFilterRuleContext.class);
			classMap.put(FilteredAlarmModule.MODULE_NAME, FilteredAlarmContext.class);
			classMap.put(FlaggedEventRuleModule.MODULE_NAME, FlaggedEventRuleContext.class);
			classMap.put(FlaggedEventModule.MODULE_NAME, FlaggedEventContext.class);
			classMap.put(AlarmDefinitionTaggingModule.MODULE_NAME, AlarmDefinitionTaggingContext.class);
			classMap.put(AlarmFilterRuleCriteriaModule.MODULE_NAME, FilterRuleCriteriaContext.class);
			classMap.put(RawAlarmModule.MODULE_NAME, RawAlarmContext.class);
			classMap.put(FlaggedEventAlarmTypeRelModule.MODULE_NAME, FlaggedEventRuleAlarmTypeRel.class);
			classMap.put(ControllerAlarmInfoModule.MODULE_NAME, ControllerAlarmInfoContext.class);
			classMap.put(FlaggedEventBureauEvaluationModule.MODULE_NAME, FlaggedEventRuleBureauEvaluationContext.class);
			classMap.put(FlaggedEventBureauActionModule.MODULE_NAME, FlaggedEventBureauActionsContext.class);
			classMap.put(BureauInhibitReasonListModule.MODULE_NAME, BureauInhibitReasonListContext.class);


			classMap.put(AddSubModuleRelations.ALARM_TYPE_ACTIVITY, ActivityContext.class);
			classMap.put(AddSubModuleRelations.ALARM_CATEGORY_ACTIVITY, ActivityContext.class);
			classMap.put(AddSubModuleRelations.ALARM_DEFINITION_ACTIVITY, ActivityContext.class);
			classMap.put(AddSubModuleRelations.ALARM_DEFINITION_MAPPING_ACTIVITY, ActivityContext.class);
			classMap.put(AddSubModuleRelations.ALARM_DEFINITION_TAGGING_ACTIVITY, ActivityContext.class);
			classMap.put(AddSubModuleRelations.ALARM_FILTER_RULE_ACTIVITY, ActivityContext.class);
			classMap.put(AddSubModuleRelations.RAW_ALARM_ACTIVITY, ActivityContext.class);
			classMap.put(AddSubModuleRelations.FILTER_ALARM_ACTIVITY, ActivityContext.class);
			classMap.put(AddSubModuleRelations.FLAGGED_EVENT_ACTIVITY, ActivityContext.class);
			classMap.put(AddSubModuleRelations.FLAGGED_EVENT_RULE_ACTIVITY, ActivityContext.class);

			classMap.put(Meter.METER, V3MeterContext.class);
			classMap.put(Meter.ELECTRICITY_METER, V3ElectricityUtilityMeterContext.class);
			classMap.put(Meter.WATER_METER, V3WaterUtilityMeterContext.class);
			classMap.put(Meter.GAS_METER, V3GasUtilityMeterContext.class);
			classMap.put(Meter.HEAT_METER, V3HeatUtilityMeterContext.class);
			classMap.put(Meter.BTU_METER, V3BTUUtilityMeterContext.class);

			classMap.put(Calendar.CALENDAR_ACTIVITY_MODULE,ActivityContext.class);
			classMap.put(Calendar.EVENT_ACTIVITY_MODULE,ActivityContext.class);
			classMap.put(Control_Action.CONTROL_ACTION_ACTIVITY_MODULE_NAME, ActivityContext.class);
			classMap.put(Control_Action.CONTROL_ACTION_TEMPLATE_ACTIVITY_MODULE_NAME, ActivityContext.class);
			classMap.put(Control_Action.COMMAND_ACTIVITY_MODULE_NAME,ActivityContext.class);


			for (QuestionType type : QuestionType.values()) {
				classMap.put(type.getSubModuleName(), type.getSubClass());
			}

			return classMap;

		}

		@Deprecated
		public static Class getClassFromModuleName(String moduleName) {
			return classMap.get(moduleName);
		}

		public static Class getClassFromModule(FacilioModule module) {
			return getClassFromModule(module, true);
		}

		public static Class getClassFromModule(FacilioModule module, boolean checkParent) {
			Class moduleClass = null;
			if (module != null) {
				moduleClass = classMap.get(module.getName());

				// Temp fix until Asset is entirely moved to V3
				if (moduleClass == null && checkParent && module.instanceOf(ContextNames.ASSET)) {
					return getClassFromModule(module.getExtendModule(), checkParent);
				}
				
				if (moduleClass == null && checkParent && module.instanceOf(FacilioConstants.Meter.METER)) {
					return getClassFromModule(module.getExtendModule(), checkParent);
				}

				if (moduleClass == null) {
					V3Config config = ChainUtil.getV3Config(module, checkParent);
					moduleClass = config == null ? null : config.getBeanClass();
				}

				if (moduleClass == null && checkParent && module.getExtendModule() != null) {
					return getClassFromModule(module.getExtendModule(), true);
				}

				if (moduleClass == null && module.isCustom()) {
					if (module.getTypeEnum() == FacilioModule.ModuleType.ATTACHMENTS) {
						moduleClass = AttachmentV3Context.class;
					} else {
						moduleClass = CustomModuleData.class;
					}
				}

			}

			return moduleClass;
		}

		public static Collection<Class> getAllClasses() {
			return classMap.values();
		}

		public static final String DELETE_TYPE = "deleteType";
		public static final Object LAST_EXECUTION_TIME = "lastExecutionTime";

		public static final String FETCH_ORIGINAL = "fetchOriginal";

		public static final List<String> MONTH_LIST = Arrays.asList("JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC");

		public static final String BACKGROUND_ACTIVITY = "backgroundActivity";
	}


	public static class Inspection {
		public static final String INSPECTION_TEMPLATE = "inspectionTemplate";
		public static final String INSPECTION_RESPONSE = "inspectionResponse";
		public static final String INSPECTION_TRIGGER = "inspectionTrigger";
		public static final String INSPECTION_TRIGGERS = "inspectionTriggers";

		public static final String INSPECTION_RESPONSE_ACTIVITY = "inspectionResponseActivity";

		public static final String INSPECTION_TRIGGER_INCL_EXCL = "inspectionTriggerResourceInclExcl";

		public static final String INSPECTION_CATEGORY = "inspectionCategory";
		public static final String INSPECTION_PRIORITY = "inspectionPriority";
	}

	public static class Survey {
		public static final String SURVEY_TEMPLATE = "surveyTemplate";
		public static final String SURVEY_RESPONSE = "surveyResponse";
		public static final String SURVEY_TRIGGER = "surveyTrigger";

		public static final String SURVEY_RESPONSE_ACTIVITY = "surveyResponseActivity";

		public static final String SURVEY_TRIGGER_INCL_EXCL = "surveyTriggerResourceInclExcl";

		public static final String EXECUTE_CREATE_ACTIONS = "executeCreateActions";
		public static final String EXECUTE_RESPONSE_ACTIONS = "executeResponseActions";

		public static final String IS_RESULT_SURVEY = "isResultSurvey";
	}

	public static class Routes {
		public static final String DISPLAY_NAME = "Routes";
		public static final String NAME = "routes";
		public static final String TABLE_NAME = "Routes";
	}

	public static class Induction {
		public static final String INDUCTION_TEMPLATE = "inductionTemplate";
		public static final String INDUCTION_RESPONSE = "inductionResponse";

		public static final String INDUCTION_RESPONSE_ACTIVITY = "inductionResponseActivity";
		public static final String INDUCTION_TRIGGER = "inductionTrigger";

		public static final String INDUCTION_TRIGGER_INCL_EXCL = "inductionTriggerResourceInclExcl";

		public static final String MULTI_SITE_REL = "inductionMultiSiteRel";

	}

	public static class Email {
		public static final String EMAIL_FROM_ADDRESS_MODULE_NAME = "emailFromAddress";
	}

	public static class FaultImpact {
		public static final String MODULE_NAME = "faultImpact";
		public static final String FAULT_IMPACT_FIELDS_MODULE_NAME = "faultImpactFields";

	}

	public static class QAndA {
		public static final String Q_AND_A_TEMPLATE = "qandaTemplate";
		public static final String PAGE = "qandaPage";
		public static final String QUESTION = "qandaQuestion";
		public static final String RESPONSE = "qandaResponse";
		public static final String ANSWER = "qandaAnswer";

		public static class Questions {
			public static final String HEADING_QUESTION = "qandaHeadingQuestion";
			public static final String HEADING_QUESTION_RICH_TEXT = "qandaHeadingQuestionRichText";
			public static final String NUMBER_QUESTION = "qandaNumberQuestion";
			public static final String DECIMAL_QUESTION = "qandaDecimalQuestion";
			public static final String SHORT_STRING_QUESTION = "qandaShortStringQuestion";
			public static final String LONG_STRING_QUESTION = "qandaLongStringQuestion";
			public static final String DATE_TIME_QUESTION = "qandaDateTimeQuestion";
			public static final String MCQ_SINGLE = "qandaMcqSingle";
			public static final String MCQ_SINGLE_OPTIONS = "qandaMcqSingleOptions";
			public static final String MCQ_MULTI = "qandaMcqMulti";
			public static final String MCQ_MULTI_OPTIONS = "qandaMcqMultiOptions";
			public static final String FILE_UPLOAD_QUESTION = "qandaFileUploadQuestion";
			public static final String MULTI_FILE_UPLOAD_QUESTION = "qandaMultiFileUploadQuestion";
			public static final String BOOLEAN_QUESTION = "qandaBooleanQuestion";
			public static final String RATING_QUESTION = "qandaRatingQuestion";
			public static final String MATRIX_QUESTION = "matrixQuestion";
			public static final String MULTI_QUESTION = "multiQuestion";

			public static final String MATRIX_QUESTION_ROW = "qandaMatrixQuestionRow";
			public static final String MATRIX_QUESTION_COLUMN = "qandaMatrixQuestionColumn";
		}

		public static class Answers {

			public static final String ATTACHMENT = "qandaAnswerattachments";

			public static final String MATRIX_ANSWER = "qandaMatrixAnswer";

			public static final String MULTI_FILE_ANSWER = "qandaMultiFileAnswer";
			public static final String MCQ_MULTI_ANSWER_REL = "qandaMcqMultiAnswerRel";
		}

		public static class Command {
			public static final String TEMPLATE_ID = "templateId";
			public static final String PAGE_ID = "pageId";
			public static final String ANSWER_ERRORS = "answerErrors";
			public static final String ANSWER_DATA = "answerData";
			public static final String ANSWER_LIST = "answerList";
			public static final String RESPONSE_LIST = "qandaResponseList";
			public static final String CLIENT_ANSWER_LIST = "clientAnswerList";
			public static final String QUESTION_LIST = "questionList";
			public static final String QUESTION_MAP = "questionMap";
			public static final String QUESTION_VS_ANSWER = "questionVsAnswer";
			public static final String ANSWERS_TO_BE_ADDED = "answersToBeAdded";
			public static final String ANSWERS_TO_BE_UPDATED = "answersToBeUpdated";
			public static final String QUESTION_ID = "questionId";
			public static final String ANSWER_RANGE = "answerRange";
			public static final String IS_SINGLE_RESPONSE = "isSingleResponse";
			public static final String OTHER_RESPONSES = "otherResponses";
			public static final String RESPONSE_ID = "responseId";
		}
	}

	public static class LicenseKeys {
		public static final String GROUP_1_LICENSE = "group1";
		public static final String GROUP_2_LICENSE = "group2";
		public static final String LICENSE1 = "license1";
		public static final String LICENSE2 = "license2";

		public static final String LICENSE3 = "license3";
	}

	public static class PermissionKeys {
		public static final String GROUP_1_PERMISSION = "group1permission";
		public static final String GROUP_2_PERMISSION = "group2permission";
		public static final String PERMISSION1 = "permission1";
		public static final String PERMISSION2 = "permission2";
	}

	public static class OrgInfoKeys {
		public static final String USE_CONTROLLER_DATA_INTERVAL = "useControllerDataInterval";
		public static final String DEFAULT_DATA_INTERVAL = "defaultDataInterval";
		public static final String MODULE_EXPORT_LIMIT = "moduleExportLimit";
		public static final String ENERGY_COST_CONSTANT = "energyCostConstant";
		public static final String ENERGY_CARBON_CONSTANT = "energyCarbonConstant";
		public static final String IS_PARALLEL_RULE_EXECUTION = "isParallelRuleExecution";
		public static final String IS_READING_RULE_WORKFLOW_EXECUTION = "isReadingRuleWorkflowExecution";
		public static final String EXECUTE_READING_RULE_COMMAND = "executeReadingRuleCommand";
		public static final String EXECUTE_READING_RULE_THROUGH_AUTOMATED_HISTORY = "executeReadingRuleThroughAutomatedHistory";
		public static final String HISTORICAL_READING_RULE_JOBS_THRESHOLD = "historicalReadingRuleJobsThreshold";
		public static final String IS_NEW_SCHEDULE_RULE_EXECUTION = "isNewScheduleRuleExecution";
		public static final String FORK_READING_POST_PROCESSING = "forkReadingPostProcessing";
		public static final String IS_PARALLEL_FORMULA_EXECUTION = "isParallelFormulaExecution";
		public static final String CALCULATE_VM_THROUGH_FORMULA = "calculateVmThroughFormula";
		public static final String IS_OLD_AGENT = "isOldAgent";
		public static final String DATA_PROCESSING_LOGGER_LEVEL = "dataProcessingLoggerLevel";
		public static final String MAX_AGENT_PARTITION = "maxAgentPartition";
		public static final String MESSAGE_QUEUE_SOURCE = "messageQueueSource";
		public static final String CAN_EXECUTE_FROM_STORM = "canExecuteStorm";
		public static final String OMIT_BASESPACE_API = "omitBasespaceAPI";
		public static final String FETCH_ROLE_SPECIFIC_WEB_TABS = "fetchRoleSpecificTabs";

		public static final String SKIP_MAINTENANCE_APP_SCOPING = "skipMaintenanceAppScoping";
		public static final String HAS_MOBILE_SUPPORTED_OLD_APP = "hasMobileSupportedOldApp";
		public static final String HIDE_COMMENTS_AND_INFORMATION_SECTION_IN_SR="hideCommentsAndInformationSectionInSR";

		public static final String NOTES_EDIT_AVAILABLE = "notesEditAvailable";
		public static final String ODATA_READING_RECORDS_LIMIT = "odataReadingRecordsLimit";
		public static final String ODATA_MODULE_RECORDS_LIMIT = "odataModuleRecordsLimit";

		public static final String WORK_ORDER_BULKUPDATE = "workorderbulkupdate";
		public static final String PPM_BULKUPDATE = "ppmbulkupdate";
		public static final String PPM_BULKPUBLISH = "ppmbulkpublish";
	}

	public static class Services {
		public static final String APP_SERVICE = "app";
		public static final String IAM_SERVICE = "iam";
		public static final String JOB_SERVICE = JobConstants.JOB_SERVICE;
		public static final String AGENT_SERVICE = "agent";
		public static final String WEATHER_SERVICE = "weather";
		public static final String DEFAULT_SERVICE = FacilioService.DEFAULT_SERVICE;
		public static final String INSTANT_JOB_SERVICE = JobConstants.INSTANT_JOB_SERVICE;
		public static final String WMS_SERVICE = "wms";
		public static final String TEMP_JOBS = "tempjobs";
		public static final String AUDIT_SERVICE = "auditlogs";
	}

	public static class Alarm {
		public static final String CLEAR_SEVERITY = "Clear";
		public static final String INFO_SEVERITY = "Info";
		public static final String CRITICAL_SEVERITY = "Critical";
		public static final String MAJOR_SEVERITY = "Major";
		public static final String WARNING_SEVERITY = "Warning";
	}

	public static class Criteria {
		public static final String LOGGED_IN_USER = "${LOGGED_USER}";
		public static final String LOGGED_IN_USER_DISPLAY_NAME = "Logged in User";
		public static final String LOGGED_IN_USER_TENANT = "${LOGGED_TENANT}";
		public static final String LOGGED_IN_USER_VENDOR = "${LOGGED_VENDOR}";
		public static final String LOGGED_IN_USER_CLIENT = "${LOGGED_CLIENT}";
		public static final String LOGGED_IN_USER_GROUP = "${LOGGED_USER_GROUP}";
		public static final String LOGGED_IN_USER_GROUP_DISPLAY_NAME = "Logged in User Teams";
		public static final String LOGGED_IN_USER_SITES = "${LOGGED_USER_SITES}";
		public static final String LOGGED_IN_USER_TENANT_SITES = "${LOGGED_USER_TENANT_SITES}";
		public static final String CURRENT_SITE = "${CURRENT_SITE}";
		public static final String CURRENT_SITE_DISPLAY_NAME = "Selected Site";
		public static final String CURRENT_DATE = "${CURRENT_DATE}";
		public static final String CURRENT_TIME = "${CURRENT_TIME}";
		public static final String LOGGED_IN_PEOPLE = "${LOGGED_PEOPLE}";
		public static final String LOGGED_IN_PEOPLE_DISPLAY_NAME = "Logged in people";

	}

	public static class Builder {
		public static final String GROUP_BY = "groupBy";
		public static final String LIMIT = "limit";
		public static final String ORDER_BY = "orderBy";
		public static final String ORDER_BY_TYPE = "orderByType";
		public static final String PAGE = "page";
		public static final String PER_PAGE = "perPage";

	}

	public static class ApprovalRule {
		public static final String APPROVAL_RULE_ID_FIELD_NAME = "approvalRuleId";
		public static final String APPROVAL_STATE_FIELD_NAME = "approvalState";
		public static final String APPROVAL_REQUESTED_BY_FIELD_NAME = "requestedBy";
		public static final String APPROVAL_REQUESTER = "approvalRequester";
	}
	public static class Flow{
		public static final String FLOW_LOG="flowLog";
	}
	public static class Workflow {
		public static final String TEMPLATE = "template";
		public static final String ACTION_TEMPLATE = "actionTemplate";
		public static final String TEMPLATE_TYPE = "templateType";
		public static final String TEMPLATE_ID = "templateId";

		public static final String NOTIFICATION_EMAIL = "notificationEMail";

		public static final String NOTIFICATION_SMS = "notificationSMS";

		public static final String WORKFLOW = "workflow";
		public static final String WORKFLOW_LIST = "workflows";

		public static final String WORKFLOW_LOG = "workflowLog";
		public static final String WORKFLOW_VERSION_HISTORY = "workflowVersionHistory";
		public static final String WORKFLOW_LOG_VIEW = "workflowLogView";

		public static final String WORKFLOW_LOG_TABLENAME = "Workflow_Log";

		public static final String WORKFLOW_LOG_PARENT_ID = "workflowLogParentId";
		public static final String WORKFLOW_LOG_PARENT_TYPE = "workflowLogRecordType";
		public static final String WORKFLOW_LOG_RECORD_ID = "workflowLogRecordId";

		public static final String WORKFLOW_LOG_RECORD_MODULE_ID = "recordModuleId";
	}

	public static class WorkOrder {
		public static final String TABLE_NAME = "Workorders";
	}

	public static class MultiResource {
		public static final String DISPLAY_NAME = "Multi Resource";
		public static final String NAME = "multiResource";
		public static final String TABLE_NAME = "Multi_Resource";
	}

	public static class WorkOrderRquest {
		public static final String SOURCE_TYPE = "sourceType";
		public static final String CREATED_TIME = "createdTime";
		public static final String URGENCY = "urgency";
	}

	public static class Job {
		public static final String NEXT_EXECUTION_TIME = "NEXT_EXECUTION_TIME";
		public static final String TABLE_NAME = "Jobs";
		public static final String FORKED_COMMANDS = "forkedCommands";
		public static final String JOB_CONTEXT = "jobContext";

		public static final String EXECUTER_NAME_FACILIO = "facilio";
		public static final String EXECUTER_NAME_PRIORTIY = "priority";

		public static final String SCHEDULED_READING_RULE_JOB_NAME = "scheduledReadingRule";
		public static final String SCHEDULED_ALARM_TRIGGER_RULE_JOB_NAME = "scheduledAlarmTriggerRule";

		public static final String RECORD_SPECIFIC_RULE_JOB_NAME = "singleRecordRuleExecution";

		public static final String BREACH_JOB = "AddBreachJob";
		public static final String DIGEST_JOB_NAME = "ScheduledActionExecution";
		public static final String AUTO_CHECKOUT_JOB_NAME = "AutoCheckOutVisitors";
		public static final String CLOUD_AGENT_JOB_NAME = "CloudAgent";
		public static final String POINTS_DATA_MISSING_ALARM_JOB_NAME = "PointsDataMissingAlarmJob";

		public static final String ML_BMS_POINTS_TAGGING_JOB = "MLBmsPointsTaggingJob";

		public static final String DATA_LOG_DELETE_RECORDS_JOB = "DeleteDataLogsRecordsJob";
		public static final String DATA_UN_MODELED_RECORDS_JOB = "DeleteUnModeledRecordsJob";

	}

	public static class WidgetNames {
		public static final String MAIN_SUMMARY_WIDGET = "mainsummarywidget";
		public static final String WIDGET_GROUP = "widgetGroup";
		public static final String BULK_RELATION_SHIP_WIDGET = "bulkRelationShipWidget";
		public static final String BULK_RELATED_LIST_WIDGET = "bulkRelationShipWidget";
	}

	public static class Ticket {
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

	public static class TicketStatus {
		public static final String STATUS = "status";
	}

	public static class TicketCategory {
		public static final String NAME = "name";
	}

	public static class User {
		public static final String EMAIL = "email";
	}

	public static class ApplicationLinkNames {
		public static final String FACILIO_MAIN_APP = "newapp";
		public static final String FACILIO_AGENT_APP = "iot";
		public static final String TENANT_PORTAL_APP = "tenant";
		public static final String CLIENT_PORTAL_APP = "client";
		public static final String VENDOR_PORTAL_APP = "vendor";
		public static final String OCCUPANT_PORTAL_APP = "service";
		public static final String OPERATIONAL_VISIBILITY_APP = "operations";
		public static final String MAINTENANCE_APP = "maintenance";
		public static final String WORKPLACE_APP = "wokplace";
		public static final String DEVELOPER_APP = "dev";
		public static final String DATA_LOADER_APP = "dataloader";
		public static final String KIOSK_APP = "visitor_kiosk";

		public static final String EMPLOYEE_PORTAL_APP = "employee";
		public static final String SERVICE_APP = "service";
		public static final String IWMS_APP = "iwms";
		public static final String ENERGY_APP = "energy";
		public static final String REMOTE_MONITORING = "remotemonitor";

	}

	public static class DefaultRoleNames {
		public static final String TENANT_USER = "Tenant User";
		public static final String VENDOR_USER = "Vendor User";
		public static final String OCCUPANT_USER = "Occupant User";
		public static final String CLIENT_USER = "Client User";
		public static final String OPERATIONS_ADMIN = "Operations Admin";
		public static final String AGENT_ADMIN = "Agent Admin";
		public static final String SUPER_ADMIN = "Super Administrator";
		public static final String ADMIN = "Administrator";
		public static final String MAINTENANCE_SUPER_ADMIN = "CAFM Super Administrator";
		public static final String MAINTENANCE_ADMIN = "CAFM Administrator";
		public static final String CAFM_ADMIN = "CAFM Admin";
		public static final String KIOSK_ADMIN = "Kiosk Admin";
		public static final String DEV_ADMIN = "Dev Admin";
		public static final String MAINTENANCE_MANAGER = "CAFM Manager";
		public static final String MAINTENANCE_TECHNICIAN = "CAFM Technician";
		public static final String DATA_LOADER_ADMIN = "Data Loader Admin";

		public static final String EMPLOYEE_ADMIN = "Employee Admin";

		public static final String IWMS_ADMIN = "IWMS Admin";

		public static final String REMOTE_MONITORING_ADMIN = "Remote Monitoring Admin";

	}

	public static class TableNames {
		public static final String WORK_ORDER = "Workorders";
		public static final String TICKET_STATUS = "TicketStatus";
		public static final String TICKET_PRIORITY = "TicketPriority";
		public static final String TICKET_CATEGORY = "TicketCategory";
		public static final String TICKET = "Tickets";
		public static final String WORK_ORDER_REQUEST = "WorkOrderRequests";
		public static final String SPACE = "Space";
	}

	public static class LinkNamePrefix {
		public static final String SUB_FORM_PREFIX = "subform__";
	}

	public static class CraftAndSKills {
		public static final String CRAFT = "crafts";
		public static final String SKILLS = "craftSkill";

		public static final String LABOUR_CRAFT = "labourCraftSkill";
	}

	public static class PeopleGroup {
		public static final String PEOPLE_GROUP = "peopleGroup";
		public static final String PEOPLE_GROUP_MEMBER = "peopleGroupMember";
	}

	public static class Shift {
		public static final String SHIFT = "shift";

		public static final String SHIFT_ACTIVITY = "shiftactivity";

		public static final String SHIFT_RICH_TEXT = "shiftrichtext";
		public static final String EMPLOYEES = "employees";
		public static final String ACTIVE_STATE = "activeState";
		public static final String URL = "url";
		public static final String EXPORT_LIST = "exportList";
		public static final String FORMAT = "format";
		public static final String RANGE = "range";
		public static final String RANGE_FROM = "rangeFrom";
		public static final String RANGE_TO = "rangeTo";
		public static final String EMPLOYEE_ID = "employeeID";
		public static final String SHIFTS = "shifts";
		public static final String SHIFT_ID = "shiftID";
		public static final String SHIFT_START = "shiftStart";
		public static final String SHIFT_END = "shiftEnd";
        public static final String CREATING_DEFAULT_SHIFT = "creatingDefaultShift";
    }


	public static class Reports {
		public static final String MODULE_TYPE = "module_type";

		public static final String ACTUAL_DATA = "actual";

		public static final String RANGE_FROM = "fromRange";
		public static final String RANGE_END = "endRange";
		public static final String GROUPBY = "groupBy";
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

		public static final String ROWS = "rows";
		public static final String DATA = "data";
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

		public static final StringBuilder ENERGY_TABLE = new StringBuilder(" ENERGY_DATA ");

		public static final int THIS_HOUR = 1;
		public static final int LAST_HOUR = 2;

		public static final int TODAY = 3;
		public static final int YESTERDAY = 4;

		public static final int THIS_WEEK = 5;
		public static final int LAST_WEEK = 6;

		public static final int THIS_MONTH = 7;
		public static final int LAST_MONTH = 8;

		public static final int LAST_7_DAYS = 9;
		public static final int LAST_30_DAYS = 10;

		public static final int THIS_YEAR = 11;
		public static final int LAST_YEAR = 12;

		public static final int THIS_MONTH_WITH_WEEK = 13;
		public static final int LAST_MONTH_WITH_WEEK = 14;

		public static final int THIS_YEAR_WITH_WEEK = 15;
		public static final int LAST_YEAR_WITH_WEEK = 16;

		public static final int CUSTOM_WITH_DATE = 17;
		public static final int CUSTOM_WITH_WEEK = 18;
		public static final int CUSTOM_WITH_MONTH = 19;
		public static final Map<String, Integer> DateFilter = new HashMap<>();

		static {
			DateFilter.put("THIS_HOUR", THIS_HOUR);
			DateFilter.put("LAST_HOUR", LAST_HOUR);

			DateFilter.put("TODAY", TODAY);
			DateFilter.put("YESTERDAY", YESTERDAY);

			DateFilter.put("THIS_WEEK", THIS_WEEK);
			DateFilter.put("LAST_WEEK", LAST_WEEK);

			DateFilter.put("THIS_MONTH", THIS_MONTH);
			DateFilter.put("LAST_MONTH", LAST_MONTH);

			DateFilter.put("LAST_7_DAYS", LAST_7_DAYS);
			DateFilter.put("LAST_30_DAYS", LAST_30_DAYS);

			DateFilter.put("THIS_YEAR", THIS_YEAR);
			DateFilter.put("LAST_YEAR", LAST_YEAR);

			DateFilter.put("THIS_MONTH_WITH_WEEK ", THIS_MONTH_WITH_WEEK);
			DateFilter.put("LAST_MONTH_WITH_WEEK", LAST_MONTH_WITH_WEEK);

			DateFilter.put("THIS_YEAR_WITH_WEEK ", THIS_YEAR_WITH_WEEK);
			DateFilter.put("LAST_YEAR_WITH_WEEK", LAST_YEAR_WITH_WEEK);

			DateFilter.put("CUSTOM_WITH_DATE", CUSTOM_WITH_DATE);
			DateFilter.put("CUSTOM_WITH_WEEK", CUSTOM_WITH_WEEK);
			DateFilter.put("CUSTOM_WITH_MONTH", CUSTOM_WITH_MONTH);
		}

		public static class Energy {
			public static final int TOTAL_ENERGY_CONSUMPTION_DELTA = 1;
			public static final int TOTAL_ENERGY_CONSUMPTION_DELTA_SUM = 2;
			public static final int TOTAL_ENERGY_CONSUMPTION_DELTA_COST = 3;

			public static final int PHASE_ENERGY_R_DELTA = 4;
			public static final int PHASE_ENERGY_R_DELTA_SUM = 5;
			public static final int PHASE_ENERGY_R_DELTA_COST = 6;

			public static final int PHASE_ENERGY_Y_DELTA = 7;
			public static final int PHASE_ENERGY_Y_DELTA_SUM = 8;
			public static final int PHASE_ENERGY_Y_DELTA_COST = 9;

			public static final int PHASE_ENERGY_B_DELTA = 10;
			public static final int PHASE_ENERGY_B_DELTA_SUM = 11;
			public static final int PHASE_ENERGY_B_DELTA_COST = 12;

			public static final int POWER_FACTOR_R = 13;
			public static final int POWER_FACTOR_R_AVERAGE = 14;

			public static final int POWER_FACTOR_Y = 15;
			public static final int POWER_FACTOR_Y_AVERAGE = 16;

			public static final int POWER_FACTOR_B = 17;
			public static final int POWER_FACTOR_B_AVERAGE = 18;

			public static final int ACTIVE_POWER_R = 19;
			public static final int ACTIVE_POWER_R_SUM = 20;

			public static final int ACTIVE_POWER_Y = 21;
			public static final int ACTIVE_POWER_Y_SUM = 22;

			public static final int ACTIVE_POWER_B = 23;
			public static final int ACTIVE_POWER_B_SUM = 24;

			public static final int APPARANT_POWER_R = 25;
			public static final int APPARANT_POWER_R_SUM = 26;

			public static final int APPARANT_POWER_Y = 27;
			public static final int APPARANT_POWER_Y_SUM = 28;

			public static final int APPARANT_POWER_B = 29;
			public static final int APPARANT_POWER_B_SUM = 30;

			public static final int REACTIVE_POWER_R = 31;
			public static final int REACTIVE_POWER_R_SUM = 32;

			public static final int REACTIVE_POWER_Y = 33;
			public static final int REACTIVE_POWER_Y_SUM = 34;

			public static final int REACTIVE_POWER_B = 35;
			public static final int REACTIVE_POWER_B_SUM = 36;

			public static final int PHASE_VOLTAGE_R = 37;
			public static final int PHASE_VOLTAGE_R_AVERAGE = 38;

			public static final int PHASE_VOLTAGE_Y = 39;
			public static final int PHASE_VOLTAGE_Y_AVERAGE = 40;

			public static final int PHASE_VOLTAGE_B = 41;
			public static final int PHASE_VOLTAGE_B_AVERAGE = 42;

			public static final int LINE_VOLTAGE_R = 43;
			public static final int LINE_VOLTAGE_R_AVERAGE = 44;

			public static final int LINE_VOLTAGE_Y = 45;
			public static final int LINE_VOLTAGE_Y_AVERAGE = 46;

			public static final int LINE_VOLTAGE_B = 47;
			public static final int LINE_VOLTAGE_B_AVERAGE = 48;

			public static final int LINE_CURRENT_R = 49;
			public static final int LINE_CURRENT_R_AVERAGE = 50;

			public static final int LINE_CURRENT_Y = 51;
			public static final int LINE_CURRENT_Y_AVERAGE = 52;

			public static final int LINE_CURRENT_B = 53;
			public static final int LINE_CURRENT_B_AVERAGE = 54;

			public static final int FREQUENCY_R = 55;
			public static final int FREQUENCY_R_AVERAGE = 56;

			public static final int FREQUENCY_Y = 57;
			public static final int FREQUENCY_Y_AVERAGE = 58;

			public static final int FREQUENCY_B = 59;
			public static final int FREQUENCY_B_AVERAGE = 60;
			public static final Map<String, Integer> Energy_Data = new HashMap<>();

			static {
				// Energy_Data.put("TOTAL ENERGY CONSUMPTION DELTA",
				// TOTAL_ENERGY_CONSUMPTION_DELTA);
				Energy_Data.put("TOTAL_ENERGY_CONSUMPTION_DELTA_SUM", TOTAL_ENERGY_CONSUMPTION_DELTA_SUM);
				Energy_Data.put("TOTAL_ENERGY_CONSUMPTION_DELTA_COST", TOTAL_ENERGY_CONSUMPTION_DELTA_COST);

				// Energy_Data.put("PHASE_ENERGY_R_DELTA",PHASE_ENERGY_R_DELTA);
				Energy_Data.put("PHASE_ENERGY_R_DELTA_SUM", PHASE_ENERGY_R_DELTA_SUM);
				Energy_Data.put("PHASE_ENERGY_R_DELTA_COST", PHASE_ENERGY_R_DELTA_COST);

				// Energy_Data.put("PHASE_ENERGY_Y_DELTA",PHASE_ENERGY_Y_DELTA);
				Energy_Data.put("PHASE_ENERGY_Y_DELTA_SUM", PHASE_ENERGY_Y_DELTA_SUM);
				Energy_Data.put("PHASE_ENERGY_Y_DELTA_COST", PHASE_ENERGY_Y_DELTA_COST);

				// Energy_Data.put("PHASE_ENERGY_B_DELTA",PHASE_ENERGY_B_DELTA);
				Energy_Data.put("PHASE_ENERGY_B_DELTA_SUM", PHASE_ENERGY_B_DELTA_SUM);
				Energy_Data.put("PHASE_ENERGY_B_DELTA_COST", PHASE_ENERGY_B_DELTA_COST);

				// Energy_Data.put("POWER_FACTOR_R",POWER_FACTOR_R);
				Energy_Data.put("POWER_FACTOR_R_AVERAGE", POWER_FACTOR_R_AVERAGE);

				// Energy_Data.put("POWER_FACTOR_Y",POWER_FACTOR_Y);
				Energy_Data.put("POWER_FACTOR_Y_AVERAGE", POWER_FACTOR_Y_AVERAGE);

				// Energy_Data.put("POWER_FACTOR_B",POWER_FACTOR_B);
				Energy_Data.put("POWER_FACTOR_B_AVERAGE", POWER_FACTOR_B_AVERAGE);

				// Energy_Data.put("ACTIVE_POWER_R",ACTIVE_POWER_R);
				Energy_Data.put("ACTIVE_POWER_R_SUM", ACTIVE_POWER_R_SUM);

				// Energy_Data.put("ACTIVE_POWER_Y",ACTIVE_POWER_Y);
				Energy_Data.put("ACTIVE_POWER_Y_SUM", ACTIVE_POWER_Y_SUM);

				// Energy_Data.put("ACTIVE_POWER_B",ACTIVE_POWER_B);
				Energy_Data.put("ACTIVE_POWER_B_SUM", ACTIVE_POWER_B_SUM);

				// Energy_Data.put("APPARANT_POWER_R",APPARANT_POWER_R);
				Energy_Data.put("APPARANT_POWER_R_SUM", APPARANT_POWER_R_SUM);

				// Energy_Data.put("APPARANT_POWER_Y",APPARANT_POWER_Y);
				Energy_Data.put("APPARANT_POWER_Y_SUM", APPARANT_POWER_Y_SUM);

				// Energy_Data.put("APPARANT_POWER_B",APPARANT_POWER_B);
				Energy_Data.put("APPARANT_POWER_B_SUM", APPARANT_POWER_B_SUM);

				// Energy_Data.put("REACTIVE_POWER_R",REACTIVE_POWER_R);
				Energy_Data.put("REACTIVE_POWER_R_SUM", REACTIVE_POWER_R_SUM);

				// Energy_Data.put("REACTIVE_POWER_Y",REACTIVE_POWER_Y);
				Energy_Data.put("REACTIVE_POWER_Y_SUM", REACTIVE_POWER_Y_SUM);

				// Energy_Data.put("REACTIVE_POWER_B",REACTIVE_POWER_B);
				Energy_Data.put("REACTIVE_POWER_B_SUM", REACTIVE_POWER_B_SUM);

				// Energy_Data.put("PHASE_VOLTAGE_R",PHASE_VOLTAGE_R);
				Energy_Data.put("PHASE_VOLTAGE_R_AVERAGE", PHASE_VOLTAGE_R_AVERAGE);

				// Energy_Data.put("PHASE_VOLTAGE_Y",PHASE_VOLTAGE_Y);
				Energy_Data.put("PHASE_VOLTAGE_Y_AVERAGE", PHASE_VOLTAGE_Y_AVERAGE);

				// Energy_Data.put("PHASE_VOLTAGE_B",PHASE_VOLTAGE_B);
				Energy_Data.put("PHASE_VOLTAGE_B_AVERAGE", PHASE_VOLTAGE_B_AVERAGE);

				// Energy_Data.put("LINE_VOLTAGE_R",LINE_VOLTAGE_R);
				Energy_Data.put("LINE_VOLTAGE_R_AVERAGE", LINE_VOLTAGE_R_AVERAGE);

				// Energy_Data.put("LINE_VOLTAGE_Y",LINE_VOLTAGE_Y);
				Energy_Data.put("LINE_VOLTAGE_Y_AVERAGE", LINE_VOLTAGE_Y_AVERAGE);

				// Energy_Data.put("LINE_VOLTAGE_B",LINE_VOLTAGE_B);
				Energy_Data.put("LINE_VOLTAGE_B_AVERAGE", LINE_VOLTAGE_B_AVERAGE);

				// Energy_Data.put("LINE_CURRENT_R",LINE_CURRENT_R);
				Energy_Data.put("LINE_CURRENT_R_AVERAGE", LINE_CURRENT_R_AVERAGE);

				// Energy_Data.put("LINE_CURRENT_Y",LINE_CURRENT_Y);
				Energy_Data.put("LINE_CURRENT_Y_AVERAGE", LINE_CURRENT_Y_AVERAGE);

				// Energy_Data.put("LINE_CURRENT_B",LINE_CURRENT_B);
				Energy_Data.put("LINE_CURRENT_B_AVERAGE", LINE_CURRENT_B_AVERAGE);

				Energy_Data.put("FREQUENCY_R", FREQUENCY_R);
				Energy_Data.put("FREQUENCY_R_AVERAGE", FREQUENCY_R_AVERAGE);

				Energy_Data.put("FREQUENCY_Y", FREQUENCY_Y);
				Energy_Data.put("FREQUENCY_Y_AVERAGE", FREQUENCY_Y_AVERAGE);

				Energy_Data.put("FREQUENCY_B", FREQUENCY_B);
				Energy_Data.put("FREQUENCY_B_AVERAGE", FREQUENCY_B_AVERAGE);

			}

		}
	}

	public static enum UserType {

		USER(1),
		REQUESTER(2);

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

			for (UserType type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}

		public Map<Integer, UserType> getAllTypes() {
			return typeMap;
		}
	}

	public static enum ParallelRuleExecutionProp {

		RECORD_BASED(1),
		MODULE_BASED(2),
		RULE_RECORD_BASED(3),
		;

		public int getIndex() {
			return ordinal() + 1;
		}

		public String getValue() {
			return name();
		}

		private ParallelRuleExecutionProp() {
		}

		private int instantJobType;

		public int getInstantJobType() {
			return instantJobType;
		}

		private ParallelRuleExecutionProp(int parallelRuleExecutionProp) {
			this.instantJobType = instantJobType;
		}

		public static ParallelRuleExecutionProp valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}

	}

	public static class AuditLogRecordTypes {
		public static final String VIEW_FOLDER = "ViewFolder";
		public static final String VIEW = "View";
		public static final String GLOBAL_SCOPE = "GlobalScope";
		public static final String FORM = "Form";
		public static final String SUB_FORM = "SubForm";
		public static final String FORM_FIELD = "FormField";
		public static final String FORM_SECTION = "FormSection";
		public static final String FORM_RULE = "FormRule";
		public static final String SUB_FORM_RULE = "SubFormRule";
		public static final String DELETED = "Deleted";
		public static final String CREATED = "Created";
		public static final String UPDATED = "Updated";

	}

	public static class ColourPalette {
		public static final String COLOR_PALETTE = "colorPalette";
		public static final String COLOR_KEYS = "colorKeys";
	}

	public static class Break {
		public static final String BREAK = "break";
        public static final String BREAK_RICH_TEXT = "breakrichtext";
    }

	public static class Attendance {
		public static final String ATTENDANCE = "attendance";
		public static final String ATTENDANCE_TRANSACTION = "attendanceTransaction";
		public static final String ATTENDANCE_TRANSACTION_RICH_TEXT = "attendanceTransactionRichText";
		public static final String RANGE_FROM = "rangeFrom";
		public static final String RANGE_TO = "rangeTo";
		public static final String PEOPLE_ID = "peopleID";

		public static final String MODE = "mode";

		public static final String DURATION = "duration";
		public static final String ATTENDANCE_TRANSITIONS = "attendanceTransitions";

		public static String DATE = "date";
    }

	public static class ReadingKpi {
		public static final String READING_KPI = "readingkpi";
		public static final String READING_KPI_READINGS_TABLE = "KPIReadings";
		public static final String READING_KPI_RESULT = "kpiResult";
		public static final String IS_HISTORICAL = "isHistorical";
		public static final String INDEPENDENT_KPIS = "independentKpis";
		public static final String DEPENDENT_KPIS = "dependentKpis";
		public static final String DEPENDENCY_GRAPH_LIST = "dependencyGraphList";
		public static final String SCHEDULE_TYPE = "scheduleType";
		public static final String NEXT_SCHEDULE_TYPE = "nextScheduleType";
		public static final String READING_KPI_JOB_NAME = "ExecuteScheduledReadingKpi";
		public static final String READING_KPI_HISTORICAL_JOB = "ScheduledKpiHistoricalCalculationJob";
		public static final String KPI_LOGGER_MODULE = "kpiLogger";
		public static final String KPI_RESOURCE_LOGGER_MODULE = "kpiResourceLogger";
		public static final String KPI_TYPE = "kpiType";
		public static final String PARENT_LOGGER_ID = "parentLoggerId";
		public static final String READING_KPI_LOGS_MODULE = "readingkpilogs";
		public static final String KPI_SCRIPT_LOGS_MODULE = "kpiscriptlogs";
		public static final String IS_DYNAMIC_KPI = "isDynamicKpi";
	}


	public static class ReadingRules {
		public static final String NEW_READING_RULE = "newreadingrules";

		public static final String NEW_READING_RULE_LIST = "newreadingrulelist";

		public static final String FAULT_TO_WORKORDER="faultToWorkorder";

		public static final String READING_RULE_LOGS_MODULE = "readingrulelogs";

		public static final String RULE_SCRIPT_LOGS_MODULE = "rulescriptlogs";

		public static final String SCRIPT_LOGS = "scriptLogs";

		public static class RCA {
			public static final String RCA_MODULE = "readingrulerca";
			public static final String RCA_GROUP_MODULE = "readingrulerca_group";
			public static final String RCA_SCORE_CONDITION_MODULE = "readingrulerca_score_condition";
			public static final String RCA_SCORE_READINGS_MODULE = "readingrulerca_score_readings";
			public static final String RCA_GROUP = "rcagroups";
			public static final String RCA_SCORE_READINGS = "rcaScoreReadings";
			public static final String RCA_RULE_DETAILS = "rcaRuleDetails";
		}
	}

	public static class TransactionRule {
		public static final String CreationModuleName = "transaction";
		public static final String TransactionRollUpModuleName = "budgetmonthlyamount";
		public static final String TransactionRollUpFieldName = "actualMonthlyAmount";
	}

	public static class Action{

		public static final String TRANSITION="transition";
		public static final String CUSTOM_BUTTON="customButton";
		public static final String APPROVAL="approval";
	}

	public static class HTTPParameter {
		public static final List<String> KEYS = Arrays.asList("permission","moduleName","setupTab","permissionModuleName",FacilioConstants.ContextNames.WebTab.PARENT_MODULE_NAME,"isFileApi");
	}

	public static class SummaryWidget{
		public static final String EXISTING_SUMMARY_WIDGET="existingSummaryWidget";
		public static final String SUMMARY_WIDGET_NAME="summaryWidgetName";
		public static final String SUMMARY_WIDGET="summaryWidget";
		public static final String EXISTING_SUMMARY_WIDGET_GROUPS ="existingSummaryWidgetGroups";
		public static final String SUMMARY_WIDGET_GROUPS ="summaryWidgetGroups";
		public static final String SUMMARY_WIDGET_GROUP_FIELDS="summaryWidgetGroupFields";
		public static final String UPDATABLE_SUMMARY_WIDGET_GROUP_FIELDS="updatableSummaryWidgetGroupFields";
		public static final String EXISTING_SUMMARY_WIDGET_GROUP_FIELD_IDS ="existingSummaryWidgetGroupFieldIds";
	}

	public static class WidgetGroup {
		public static final String WIDGETGROUP = "widgetGroup";
		public static final String WIDGETGROUP_ID = "widgetGroupId";
		public static final String WIDGETGROUP_SECTION = "widgetGroupSection";
		public static final String WIDGETGROUP_SECTIONS = "widgetGroupSections";
		public static final String WIDGETGROUP_SECTIONS_MAP = "widgetGroupSectionsMap";
		public static final String WIDGETGROUP_SECTION_ID = "WGsectionId";
		public static final String WIDGETGROUP_SECTION_IDS = "WGsectionIds";
		public static final String WIDGETGROUP_WIDGET = "widgetGroupWidget";
		public static final String WIDGETGROUP_WIDGETS = "widgetGroupWidgets";
		public static final String WIDGETGROUP_SECTION_WIDGETS_MAP = "widgetGroupSectionWidgetsMap";
		public static final String WIDGETGROUP_WIDGET_ID = "widgetGroupWidgetId";
	}
	public static class CustomPage{
		public static final String SEQUENCE_NUMBER = "sequenceNumber";
		public static final String SECTION_WIDGETS_MAP = "sectionWidgetsMap";
		public static final String WIDGET ="widget";
		public static final String PREVIOUS_ID = "previousId";
		public static final String COLUMNS = "columns";
		public static final String NEXT_ID = "nextId";
		public static final String IS_TEMPLATE = "isTemplate";
		public static final String IS_FETCH_FOR_CLONE = "isFetchForClone";
		public static final String IS_SYSTEM = "isSystem";
		public static final String LAYOUT_TYPE = "layoutType";
		public static final String LAYOUT_TABS_MAP = "layoutTabsMap";
		public static final String CLONED_PAGE = "clonedPage";
		public static final String IS_DEFAULT_PAGE = "isDefaultPage";
		public static final String CUSTOM_PAGE = "customPage";
		public static final String CUSTOM_PAGES = "customPages";
		public static final String PAGE_ID = "pageId";
		public static final String WIDGET_NAME = "widgetName";
		public static final String LAYOUT_ID = "layoutId";
		public static final String LAYOUT_IDS = "layoutIds";
		public static final String TAB = "tab";
		public static final String TAB_TYPE="tabType";
		public static final String PAGE_TABS = "tabs";
		public static final String TAB_ID = "tabId";
		public static final String COLUMN = "column";
		public static final String PAGE_COLUMNS = "columns";
		public static final String COLUMN_ID = "columnId";
		public static final String COLUMN_IDS = "columnIds";
		public static final String COLUMN_SECTIONS_MAP = "columnSectionsMap";
		public static final String TAB_COLUMNS_MAP = "tabColumnsMap";
		public static final String SECTION = "section";
		public static final String PAGE_SECTIONS = "sections";
		public static final String SECTION_ID = "sectionId";
		public static final String SECTION_IDS = "sectionIds";
		public static final String PAGE_SECTION_WIDGET = "pageSectionWidget";
		public static final String WIDGETID = "widgetId";
		public static final String WIDGETGROUP_WIDGET_ID = "widgetGroupSectionWidgetId";
		public static final String WIDGET_WRAPPER_TYPE = "widgetWrapperType";
		public static final String PAGE_SECTION_WIDGETS = "widgets";
		public static final String WIDGETS_POSITIONS = "widgetsPosition";
		public static final String MESSAGE = "message";
		public static final String PAGE_SECTION_WIDGET_IDS = "widgetIds";
		public static final String TAB_NAME = "tabName";
		public static final String IS_BUILDER_REQUEST = "isBuilderRequest";
		public static final String PAGE_ENABLED = "enablePage";
		public static final String PAGE_DISABLED = "disablePage";
		public static final String TAB_ENABLED = "enableTab";
		public static final String TAB_DISABLED = "disableTab";
		public static final String TYPE = "type";
        public static final String PARENT_ID = "parentId";
        public static final String EXCLUDE_TABS = "excludeTabs";
		public static final String EXCLUDE_COLUMNS = "excludeColumns";
		public static final String COLUMN_WIDTHS = "columnWidths";
		public static final String WIDGET_TYPE = "widgetType";
		public static final String WIDGET_DETAIL = "widgetDetail";
		public static final String WIDGET_DETAIL_TYPE = "widgetDetailType";
        public static final String IS_CLONE_PAGE = "isClonePage";
    }

	public static class Widget{
		public static final String MODULE_NAME = "moduleName";
        public static final String WIDGETS = "widgets";
		public static final String WIDGET_IDS = "widgetIds";
		public static final String WIDGET_MODULE = "widgetToModule";
		public static final String WIDGET_CONFIGS = "widgetConfigs";
	}
	public static class UserPeopleKeys {
		public static final String TAB_NAME="tabName";
		public static final String VENDOR = "VENDOR";
		public static final String TENANT = "TENANT";
		public static final String CLIENT = "CLIENT";
		public static final String INVITED_TIME = "INVITED TIME";
		public static final String LAST_LOGIN_TIME = "LAST LOGIN TIME";
		public static final String NAME ="NAME";
		public static final String EMAIL ="EMAIL";
		public static final String ROLE ="ROLE";
		public static final String USER_STATUS ="USER STATUS";
		public static final String INVITE_STATUS ="INVITE STATUS";
	}



	public static class Meter {
		public static final String METER = "meter";
		public static final String UTILITY_TYPE = "utilitytype";
		public static final String GAS_METER = "gasmeter";
		public static final String WATER_METER = "waterutilitymeter";
		public static final String ELECTRICITY_METER = "electricitymeter";
		public static final String HEAT_METER = "heatmeter";
		public static final String BTU_METER = "btumeter";
		public static final String PARENT_UTILITY_TYPE_ID = "parentUtilityTypeId";
		public static final String METER_NOTES = "meternotes";
		public static final String METER_ATTACHMENTS = "meterattachments";
		public static final String METER_PHOTOS = "meterphotos";
		public static final String ELECTRICITY_DATA_READING = "electricitydata";
		public static final String GAS_DATA_READING = "gasdata";
		public static final String WATER_DATA_READING = "waterdata";
		public static final String HEAT_DATA_READING = "heatdata";
		public static final String BTU_DATA_READING = "btudata";
		public static final String METER_ACTIVITY = "meteractivity";
		public static final String METER_RICH_TEXT = "meterrichtext";
		
		public static final String VIRTUAL_METER_TEMPLATE_RESOURCE = "virtualMeterTemplateResource";
		public static final String VIRTUAL_METER_TEMPLATE_READING = "virtualMeterTemplateReading";
		public static final String VIRTUAL_METER_TEMPLATE_SITES = "virtualMeterTemplateSites";
		public static final String VIRTUAL_METER_TEMPLATE = "virtualMeterTemplate";
		
		public static final String VIRTUAL_METER_TEMPLATE_ID = "virtualMeterTemplateId";
		public static final String VIRTUAL_METER_TEMPLATE_BUILDINGS = "virtualMeterTemplateBuildings";
		public static final String VIRTUAL_METER_TEMPLATE_ACTIVITY = "virtualMeterTemplateActivity";
	}

	public static class SensorRule{
		public  static final String SENSOR_RULE_TABLE_NAME = "Sensor_Readings";

	}

	public static class Calendar{
		public static final String CALENDAR_MODULE_NAME = "calendar";
		public static final String EVENT_MODULE_NAME = "calendarEvent";
		public static final String CALENDAR_SLOTS_MODULE_NAME = "calendarSlots";
		public static final String CALENDAR_ID = "calendarId";
		public static final String CALENDAR_ID_LIST = "calendarIds";
		public static final String CALENDAR_EVENT_ID = "eventId";
		public static final String CALENDAR_EVENT_ID_LIST = "eventIds";
		public static final String IS_EVENT_EDITED = "eventEdited";
		public static final String EVENT_TIME_SLOT_MODULE_NAME = "eventTimeSlot";
		public static final String CALENDAR_TIME_SLOT_MODULE_NAME = "calendarTimeSlot";
		public static final String CALENDAR_EVENT_MAPPING_MODULE_NAME = "calendarEventMapping";
		public static final String CALENDAR_ACTIVITY_MODULE = "calendarActivity";
		public static final String EVENT_ACTIVITY_MODULE = "eventActivity";
		public static final String SAVE_CALENDAR_ID_LIST = "saveCalendarIds";
	}
	public static class Control_Action {
		public static final String CONTROL_ACTION_MODULE_NAME = "controlAction";
		public static final String ACTION_MODULE_NAME = "action";
		public static final String COMMAND_MODULE_NAME = "command";
		public static final String CONTROL_ACTION_FIRST_LEVEL_APPROVAL_MODULE_NAME = "controlActionFirstLevelApproval";
		public static final String CONTROL_ACTION_SECOND_LEVEL_APPROVAL_MODULE_NAME = "controlActionSecondLevelApproval";
		public static final String CONTROL_ACTION_ID = "controlActionId";
		public static final String CONTROL_ACTION_TEMPLATE_MODULE_NAME = "controlActionTemplate";
		public static final String CONTROL_ACTION_TEMPLATE_ID = "controlActionTemplateId";
		public static final String CONTROL_ACTION_ACTIVITY_MODULE_NAME = "controlActionActivity";
		public static final String CONTROL_ACTION_TEMPLATE_ACTIVITY_MODULE_NAME = "controlActionTemplateActivity";
		public static final String CONTROL_ACTION_NOTES_MODULE_NAME = "controlActionNotes";
		public static final String CONTROL_ACTION_ATTACHMENT_MODULE_NAME = "controlActionAttachment";
		public static final String CONTROL_ACTION_TEMPLATE_NOTES_MODULE_NAME = "controlActionTemplateNotes";
		public static final String CONTROL_ACTION_TEMPLATE_ATTACHMENT_MODULE_NAME = "controlActionTemplateAttachment";
		public static final String COMMAND_ACTIVITY_MODULE_NAME = "commandActivity";
	}

	public static final String UTILITY_INTEGRATION_CUSTOMER = "utilityIntegrationCustomer";

	public static final String UTILITY_INTEGRATION_METER = "utilityIntegrationMeter";
	public static final String UTILITY_INTEGRATION_LINE_ITEMS = "utilityIntegrationLineItems";

	public static final String UTILITY_INTEGRATION_TIER_ITEMS = "utilityIntegrationTierItems";
	public static final String UTILITY_INTEGRATION_SUPPLIER_LINE_ITEMS = "utilityIntegrationSupplierLineItems";

	public static final String UTILITY_INTEGRATION_TOU = "utilityIntegrationTou";

	public static final String UTILITY_INTEGRATION_DEMAND = "utilityIntegrationDemand";

	public static final String UTILITY_INTEGRATION_POWER = "utilityIntegrationPower";

	public static final String UTILITY_INTEGRATION_BILLS = "utilityIntegrationBills";

	public static final String REFERRALS = "referrals";

	public static final String FREQUENCY = "frequency";

	public static final String PREPAY = "prepay";

	public static final String UTILITY_INTEGRATION_CUSTOMER_ACTIVITY = "utilityIntegrationCustomerActivity";
	public static final String UTILITY_INTEGRATION_METER_ACTIVITY = "utilityIntegrationMeterActivity";

	public static final String UTILITY_INTEGRATION_CUSTOMER_LIST = "utilityIntegrationCustomerList";

	public static final String UTILITY_INTEGRATION_METER_LIST = "utilityIntegrationMeterList";

	public static final String UTILITY_INTEGRATION_CUSTOMER_NOTES = "utilityIntegrationCustomerNotes";

	public static final String UTILITY_INTEGRATION_CUSTOMER_ATTACHMENTS = "utilityIntegrationCustomerAttachments";

	public static final String  UTILITY_INTEGRATION_BILL_ACTIVITY = "utilityIntegrationBillActivity";

	public static final String UTILITY_INTEGRATION_BILL_NOTES = "utilityIntegrationBillNotes";

	public static final String UTILITY_INTEGRATION_BILL_ATTACHMENTS = "utilityIntegrationBillAttachments";

	public static final String CUSTOMER_TYPE = "customerType";
	
	public static final String STATE = "state";

	public static final String UTILITY_INTEGRATION_TARIFF = "utilityIntegrationTariff";
	public static final String UTILITY_INTEGRATION_TARIFF_SLAB = "utilityIntegrationTariffSlab";
	public static final String UTILITY_DISPUTE = "utilityDispute";
	public static final String UTILITY_DISPUTE_NOTES = "utilityDisputeNotes";
	public static final String UTILITY_DISPUTE_ATTACHMENTS = "utilityDisputeAttachments";
	public static final String UTILITY_DISPUTE_ACTIVITY = "utilityDisputeActivity";
	public static final String UNDER_DISPUTE = "underDispute";
	public static final String RESOLVE_DISPUTE = "resolveDispute";
	public static final String UTILITY_DISPUTE_STATUS = "utilityDisputeStatus";


}
