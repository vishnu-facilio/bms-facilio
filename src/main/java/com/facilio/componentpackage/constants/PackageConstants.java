package com.facilio.componentpackage.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PackageConstants {
    public static final String RESEND_APPROVER_LIST="Resend_Approver_List";
    public static final String FIELDNAME="Field_Name";
    public static final String FIELDS_LIST="Fields_List";
    public static final String FIELDS="Fields";
    public static final String FIELD_MODULE_NAME="Field_Module_Name";
    public static final String LOOKUP_FIELD_NAME="Lookup_Field_Name";
    public static final String LOOKUP_MODULE_NAME="Lookup_Module_Name";
    public static final String USER_NAME="User_Name";
    public static final String PACKAGE_ID="packageId";
    public static final String PATCH_INSTALL="patchInstall";
    public static final String APPROVERS="Approvers";
    public static final String APPROVER_LIST="Approver_List";
    public static final String APPROVER_TYPE="Approver_Type";
    public static final String FORM_NAME="Form_Name";
    public static final String NAME = "name";
    public static final String SEQUENCE = "sequence";
    public static final String RECORDS_COUNT = "recordsCount";
    public static final String UNIQUE_NAME = "uniqueName";
    public static final String DISPLAY_NAME = "displayName";
    public static final String DESCRIPTION = "Description";
    public static final String IS_CUSTOM = "IsCustom";
    public static final String IS_DEFAULT = "isDefault";
    public static final String VERSION = "version";
    public static final String TYPE = "type";
    public static final String SEQUENCE_NUMBER = "Sequence_Number";
    public static final String VALUE_ELEMENT = "Value_Element";
    public static final String PACKAGE_TYPE = "packageType";
    public static final String FROM_ADMIN_TOOL = "fromAdminTool";
    public static final String SKIP_COMPONENTS = "skipComponents";
    public static final String COMPONENTS_FOLDER_NAME = "components";
    public static final String META_FILES_FOLDER_NAME = "metafiles";
    public static final String XML_FILE_EXTN = "xml";
    public static final String TXT_FILE_EXTN = "txt";
    public static final String PACKAGE_CONTEXT = "packageContext";
    public static final String CREATED_USER_ID = "createdUserId";
    public static final String RELATED_MODULE = "Related_Module";
    public static final String MODULENAME = "moduleName";
    public static final String UNIQUE_IDENTIFIER = "uniqueIdentifier";
    public static final String FILE_PATH_SEPARATOR = "/";
    public static final String FILE_EXTENSION_SEPARATOR = ".";
    public static final String PACKAGE_CONF_FILE_NAME = "PackageConf";
    public static final String FILE = "file";
    public static final String FILE_ID = "fileId";
    public static final String SOURCE_ORG_ID = "sourceOrgId";
    public static final String TARGET_ORG_ID = "targetOrgId";
    public static final String SANDBOX_DOMAIN_NAME = "sandboxDomainName";
    public static final String PACKAGE_ROOT_FOLDER = "Package_root_folder";
    public static final String TO_BE_FETCH_RECORDS = "toBeFetchRecords";
    public static final String FETCHED_RECORDS = "fetchedRecords";
    public static final String PREVIOUS_VERSION = "previousVersion";
    public static final String PACKAGE_CONFIG_XML = "packageConfigXML";
    public static final String USE_LINKNAME_FROM_CONTEXT = "useLinkNameFromContext";
    public static final String DOWNLOAD_URL = "downloadUrl";

    public static final String TYPE_STRING = "typeString";
    public static final String CONSTANT = "constant";
    public static final String LINK_NAME="linkName";
    public static final String STATUS="status";
    public static final String CSV_FILE_EXTN = "csv";
    public static final String DATA_FOLDER_NAME = "datas";
    public static final String DATA_ATTACHMENT_FILE_FOLDER_NAME = "dataAttachmentFiles";
    public static final String MODULE_NAMES = "moduleNames";
    public static final String MODULES = "modules";
    public static final String MODULE =  "module";
    public static final String DATA_CONF_FILE_NAME = "DataConf";
    public static final String MODULE_SEQUENCE_FILE = "ModuleSequence";
    public static final String DATA = "Data";

    public static final List<String> ALLOWED_EXTN = new ArrayList<>();

    static {
        ALLOWED_EXTN.add("xml");
        ALLOWED_EXTN.add("csv");
        ALLOWED_EXTN.add("fs");
    }


    public static class PackageXMLConstants {
        public static final String UNIQUE_IDENTIFIER = "Unique_Identifier";
        public static final String COMPONENT_TYPE = "Type";
        public static final String COMPONENT_STATUS = "Status";
        public static final String CREATED_VERSION = "Created_Version";
        public static final String MODIFIED_VERSION = "Modified_Version";
        public static final String PARENT_COMPONENT_ID = "Parent_Id";
        public static final String COMPONENTS = "Components";
        public static final String COMPONENT = "Component";
        public static final String PACKAGE = "Package";
    }

    public static class ModuleXMLConstants {
        public static final String MODULE_TYPE = "Module_Type";
        public static final String DESCRIPTION = "Description";
        public static final String STATEFLOW_ENABLED = "Is_StateFlow_Enabled";
        public static final String DUPLICATE_MODULE_ERROR = "Module with name already present";
    }

    public static class FieldXMLConstants {
        public static final String REQUIRED = "Required";
        public static final String DATA_TYPE = "DataType";
        public static final String IS_DEFAULT = "Is_Default";
        public static final String MAIN_FIELD = "Main_Field";
        public static final String FIELDS_LIST = "Fields_List";
        public static final String DISPLAY_TYPE = "Display_Type";
        public static final String ENUM_FIELD_VALUE = "EnumField_Value";
        public static final String ENUM_FIELD_VALUES = "EnumField_Values";
        public static final String DUPLICATE_FIELD_ERROR = "Field with name already present";

    }
    public static class Approvals{
       public static final String CONFIG="Config";
        public static final String REJECT_FORM="Reject_Form";
        public static final String RESEND_FORM="Resend_Form";
        public static final String APPROVAL_FORM="Approval_Form";
        public static final String REJECT_DIALOG_TYPE="Reject_Dialog_Type";
        public static final String APPROVAL_DIALOG_TYPE="Approval_Dialog_Type";
        public static final String CONFIG_JSON="Config_Json";
        public static final String APPROVAL_ACTIONS="Approval_Actions";
        public static final String RESEND_ACTIONS_LIST="Resend_Actions";
        public static final String REJECT_ACTIONS_LIST="Reject_Actions";
        public static final String APPROVAL_ENTRY_ACTIONS_LIST ="Approval_Entry_Actions";
        public static final String IS_ALL_APPROVAL_NEEDED="Is_All_Approval_Needed";
        public static final String EVENT_TYPE="Event_Type";
        public static final String APPROVAL_STATEFLOW_MODULE_NAME="Approval_StateFlow_ModuleName";
        public static final String APPROVAL_STATEFLOW_NAME="Approval_StateFlow_NAME";
        public static final String APPROVAL_STATETRANSITION="Approval_StateFlow_ModuleName";


    }

    public static class AppXMLConstants {
        public static final String DUPLICATE_APPLICATION_ERROR = "Application with name already present";
        public static final String APPLICATION_NAME = "ApplicationName";
        public static final String APPLICATION = "application";
        public static final String APPLICATION_ID = "ApplicationId";
        public static final String APP_CATEGORY = "AppCategory";
        public static final String DESCRIPTION = "Description";
        public static final String DOMAIN_TYPE = "DomainType";
        public static final String IS_DEFAULT = "IsDefault";
        public static final String SCOPING_ID = "ScopingId";
        public static final String APP_LINK_NAME = "AppLinkName";
        public static final String CONFIG = "Config";
        public static final String LAYOUT = "Layout";
        public static final String APP_TYPE = "AppType";
        public static final String LAYOUT_TYPE = "AppLayoutType";
        public static final String DEVICE_TYPE = "LayoutDeviceType";
        public static final String VERSION_NUMBER = "VersionNumber";
        public static final String WEBTAB_GROUP = "WebTabGroup";
        public static final String WEBTAB_GROUPS_LIST = "WebTabGroupsList";
        public static final String WEB_TAB_GROUP_IDS = "webTabGroupIds";

        public static final String ROUTE = "Route";
        public static final String TAB_TYPE = "TabType";
        public static final String LAYOUT_ID = "LayoutId";
        public static final String ICON_TYPE = "IconType";
        public static final String ICON_TYPE_ENUM = "IconTypeEnum";
        public static final String TAB_ORDER = "TabOrder";
        public static final String TABGROUP_ORDER = "TabGroupOrder";
        public static final String MODULE_LIST = "Modules_List";
        public static final String SPEICAL_MODULES = "Special_Modules";
        public static final String FEATURE_LICENSE = "FeatureLicense";


    }

    public static class FormXMLComponents {
        public static final String SUB_FORM = "Sub_Form";

        public static final String FORM_NAME = "Form_Name";
        public static final String FACILIO_FIELD_NAME = "Facilio_Field_Name";
        public static final String SECTION_NAME = "Section_Name";
        public static final String SUB_FORM_NAME = "Sub_Form_Name";
        public static final String LOOKUP_MODULE_NAME = "Lookup_Module_Name";
        public static final String FORM_FIELD_NAME = "Form_Field_Name";
        public static final String FORM_TYPE = "FormType";
        public static final String LABEL_POSITION = "LabelPosition";
        public static final String SHOW_IN_MOBILE = "ShowInMobile";
        public static final String DISPLAY_TYPE = "Display_Type";
        public static final String REQUIRED = "Required";
        public static final String SHOW_IN_WEB = "ShowInWeb";
        public static final String HIDE_IN_LIST = "HideInList";
        public static final String STATE_FLOW_ID = "StateFlowId";
        public static final String PRIMARY_FORM = "PrimaryForm";
        public static final String IS_SYSTEM_FORM = "SsSystemForm";
        public static final String TYPE = "Type";
        public static final String SPAN = "Span";
        public static final String HIDE_FIELD = "Hide_Field";
        public static final String IS_DISABLED = "isDisabled";
        public static final String DEFAULT_VALUE = "Default_Value";
        public static final String CONFIG = "Config";
        public static final String ALLOW_CREATE_OPTIONS = "Allow_Create";
        public static final String IS_LOCKED = "Locked";
        public static final String SHOW_LABEL = "Show_Label";
        public static final String SECTION_TYPE = "Section_Type";
        public static final String FORM_SHARING = "Form_Sharing";
        public static final String LOOKUP_FIELD_NAME = "Lookup_Field_Name";
        public static final String SUB_FORM_DEFAULT_VALUE = "Sub_Form_Default_Value";
        public static final String NUMBER_OF_SUB_FORM_RECORDS = "Number_Of_Sub_Form_Records";
        public static final String DUPLICATE_FORM_ERROR = "Form with name already present";
    }

    public static class ViewConstants {
        public static final String VIEW = "View";
        public static final String VIEW_GROUP = "View_Group";
        public static final String VIEW_GROUPS_LIST = "View_Groups_List";
        public static final String VIEW_FIELD = "View_Field";
        public static final String VIEW_FIELDS_LIST = "View_Fields_List";
        public static final String SORT_FIELDS_LIST = "View_Sort_Fields_List";
        public static final String SORT_FIELD = "View_Sort_Field";
        public static final String VIEW_TYPE = "View_Type";
        public static final String VIEW_GROUP_TYPE = "View_Group_Type";
        public static final String IS_HIDDEN = "Is_Hidden";
        public static final String IS_PRIMARY = "Is_Primary";
        public static final String IS_LOCKED = "Is_Locked";
        public static final String STATUS = "Status";
        public static final String IS_LIST_VIEW = "Is_List_View";
        public static final String IS_CALENDAR_VIEW = "Is_Calendar_View";
        public static final String CALENDAR_VIEW_CONTEXT = "Calendar_View_Context";
        public static final String START_DATE_FIELD_NAME = "Start_Date_Field_Name";
        public static final String END_DATE_FIELD_NAME = "End_Date_Field_Name";
        public static final String DEFAULT_CALENDAR_VIEW = "Default_Calendar_View";

        public static final String PARENT_FIELD_NAME = "Parent_Field_Name";
        public static final String PARENT_MODULE_NAME = "Parent_Module_Name";
        public static final String CUSTOMIZATION = "Customization";
        public static final String IS_ASCENDING = "Is_Ascending";
        public static final String EXCLUDE_MODULE_CRITERIA = "Exclude_Module_Criteria";
        public static final String VIEW_FIELD_NAME = "View_Field_Name";
        public static final String SORT_FIELD_NAME = "Sort_Field_Name";
        public static final String VIEW_SHARING = "View_Sharing";
    }
    

    public static class CriteriaConstants {
        public static final String CRITERIA = "Criteria";
        public static final String CONDITION = "Condition";
        public static final String PATTERN = "Pattern";
        public static final String SEQUENCE = "Sequence";
        public static final String FIELD_NAME = "FieldName";
        public static final String COLUMN_NAME = "Column_Name";
        public static final String OPERATOR = "Operator";
        public static final String VALUE = "Value";
        public static final String JSON_VALUE = "Json_Value";
        public static final String CRITERIA_VALUE = "Criteria_Value";
        public static final String CRITERIA_MODULENAME = "Criteria_ModuleName";
        public static final String CONDITIONS_LIST = "Conditions_List";
        public static final String IS_EXPRESSION_VALUE = "Is_Expression_Value";
        public static final String PICKLIST_VALUE = "PickList_Value";
        public static final String USER_ELEMENT = "User_Element";
        public static final String PEOPLE_ELEMENT = "People_Element";
        public static final String USER_ELEMENT_LIST = "User_Element_List";
        public static final String PEOPLE_ELEMENT_LIST = "People_Element_List";
        public static final String PICKLIST_ELEMENT = "PickList_Element";
        public static final String PARENT_MODULE_NAME = "Parent_Module_Name";
        public static final String DYNAMIC_VALUE = "Dynamic_Value";
        public static final String IS_DYNAMIC_VALUE = "Is_Dynamic_Value";
        public static final String ROLE_NAME = "Role_Name";
        public static final String ROLES_LIST = "Roles_List";
        public static final String GROUP_NAME = "Group_Name";
        public static final String PEOPLE_GROUP_LIST = "People_Group_List";

    }

    public static class RoleConstants {
        public static final String ROLE_NAME="Role_Name";
        public static final String IS_SUPER_ADMIN = "Is_Super_Admin";
        public static final String IS_PRIVILEGED_ROLE = "Is_Privileged_Role";
        public static final String ROLE_APP_MAPPING = "Role_App_Mapping";
        public static final String APPLICATION = "Application";
        public static final String WEB_TAB_NAME = "Web_Tab_Name";
        public static final String PERMISSIONS = "Permissions";
        public static final String PERMISSION = "Permission";
        public static final String PERMISSION_VALUE = "Permission_Value";
    }

    public static class SharingContextConstants {
        public static final String SINGLE_SHARING_CONTEXT = "Single_Sharing_Context";
        public static final String SHARING_CONTEXT = "Sharing_Context";
        public static final String SHARING_TYPE = "Sharing_Type";
        public static final String ROLE_NAME = "Role_Name";
    }

    public static class OrgConstants {
        public static final String ORGNAME = "OrgName";
        public static final String PHONE = "Phone";
        public static final String MOBILE = "Mobile";
        public static final String FAX = "Fax";
        public static final String STREET = "Street";
        public static final String CITY = "City";
        public static final String STATE = "State";
        public static final String ZIP = "Zip";
        public static final String COUNTRY = "Country";
        public static final String CURRENCY = "Currency";
        public static final String TIMEZONE = "Timezone";
        public static final String LOGGER_LEVEL = "Logger_Level";
        public static final String DATE_FORMAT = "Date_Format";
        public static final String TIME_FORMAT = "Time_Format";
        public static final String BUSINESS_HOUR = "Business_Hour";
        public static final String LANGUAGE = "Language";
        public static final String GROUP_NAME = "Group_Name";
        public static final String FACILIODOMAINNAME = "Facilio_Domain_Name";
        public static final String ALLOW_USER_TIMEZONE = "Allow_User_Timezone";
        public static final String FEATURE_LICENSE = "Feature_License";
        public static final String FEATURE_LICENSE_KEY = "Feature_License_Key";
        public static final String FEATURE_LICENSE_VALUE = "Feature_License_Value";
        public static final String FEATURE_LICENSE_LIST = "Feature_License_List";
    }

    public static class WorkFlowRuleConstants {
        public static final String EXECUTION_ORDER = "Execution_Order";
        public static final String STATUS = "Status";
        public static final String RULE_TYPE = "Rule_Type";
        public static final String ACTIVITY_TYPE = "Activity_Type";
        public static final String PARENT_RULE = "Parent_Rule";
        public static final String ON_SUCCESS = "On_Success";
        public static final String VERSION_GROUP_ID = "Version_Group_Id";
        public static final String IS_LATEST_VERSION = "Is_Latest_Version";
        public static final String SCHEDULE_INFO = "Schedule_info";
        public static final String DATE_FIELD_NAME = "Date_Field_Name";
        public static final String DATE_FIELD_MODULE_NAME = "Date_Field_Module_Name";
        public static final String SCHEDULE_TYPE = "Schedule_Type";
        public static final String TIME_INTERVAL = "Time_Interval";
        public static final String VERSION_NUMBER = "Version_Number";
        public static final String JOB_TIME = "Job_Time";
        public static final String LAST_SCHEDULE_RULE_EXECUTED_TIME = "Last_Schedule_Rule_Executed_Time";
        public static final String ACTION = "Action";
        public static final String ACTIONS_LIST = "Actions_List";
        public static final String ACTION_TYPE = "Action_Type";
        public static final String ACTION_STATUS = "Action_Status";

        public static final String BASE_FIELD_NAME = "BaseFieldName";
        public static final String DUE_FIELD_NAME = "DueFieldName";
        public static final String COMMITMENT = "Commitment";
        public static final String COMMITMENTS = "Commitments";
        public static final String ENTITIES = "Entities";
        public static final String VERBNAME = "VerbName";
        public static final String DEFAULT_TEMPLATE = "Default_Template";
        public static final String START_TIME = "Start_Time";

        public static final String FROM_STATE_NAME = "FromStateName";
        public static final String TO_STATE_NAME = "ToStateName";
        public static final String STATEFLOW_NAME = "StateflowName";
        public static final String TYPE = "Type";
        public static final String SHOULD_EXECUTE_FORM_PERMALINK = "ShouldExecuteFromPermalink";
        public static final String SHOW_IN_CLIENT_PORTAL = "ShowInClientPortal";
        public static final String SHOW_IN_OCCUPANT_PORTAL = "ShowInOccupantPortal";
        public static final String SHOW_IN_TENANT_PORTAL = "ShowInTenantPortal";
        public static final String SHOW_IN_VENDOR_PORTAL = "ShowInVendorPortal";
        public static final String ALL_APPROVAL_REQUIRED = "AllApprovalRequired";
        public static final String APPROVER_LIST = "ApproverList";
        public static final String APPROVERS = "Approvers";
        public static final String USERNAME = "UserName";
        public static final String ROLENAME = "RoleName";
        public static final String GROUP_NAME = "GroupName";
        public static final String FIELD_MODULENAME = "FieldModuleName";
        public static final String BUTTON_TYPE = "ButtonType";
        public static final String DIALOG_TYPE = "DialogType";
        public static final String FORMNAME = "FormName";
        public static final String FORM_MODULENAME = "FormModuleName";
        public static final String ISOFFLINE = "IsOffline";
        public static final String SHOULD_FORM_INTERFACE_APPLY = "ShouldFormInterfaceApply";
        public static final String LOCATION_FIELD_NAME = "LocationFieldName";
        public static final String LOCATION_LOOKUP_FIELD_NAME = "LocationLookupFieldName";
        public static final String LOCATION_LOOKUP_FIELD_MODULENAME = "LocationLookupFieldModuleName";
        public static final String RADIUS = "Radius";
        public static final String QR_FIELDNAME = "QrFieldName";
        public static final String QR_LOOKUP_FIELDNAME = "QrLookupFieldName";
        public static final String QR_LOOKUP_FIELD_MODULENAME = "QrLookupFieldModuleName";
        public static final String SCHEDULE_TIME = "ScheduleTime";
        public static final String VALIDATION_LIST = "ValidationList";
        public static final String CONFIRMATION_LIST = "ConfirmationList";

        public static final String ESCALATIONS = "Escalations";
        public static final String LEVELS = "Levels";
        public static final String TEMPLATE = "Template";
        public static final String TEMPLATE_NAME = "Template_Name";

        public static final String TEMPLATE_JSON = "templateJson";
        public static final String TEMPLATE_TYPE = "Template_Type";
        public static final String PLACEHOLDER = "Placeholder";
        public static final String WORKFLOW_CONTEXT = "Workflow_Context";
        public static final String IS_V2_SCRIPT = "IS_V2_Script";
        public static final String WORKFLOW_STRING = "Workflow_String";
        public static final String USER_WORKFLOW = "User_Workflow";
        public static final String IS_FTL = "is_FTL";
        public static final String IS_ATTACHMENT_ADDED = "Is_Attachment_Added";
        public static final String ORIGINAL_TEMPLATE = "Original_Template";
        public static final String VALUE_ELEMENT = "Value_Element";
        public static final String NEW_STATE = "New_State";
        public static final String STATUS_NAME = "Status_Name";
        public static final String PARENT_MODULE_NAME = "Parent_Module_Name";
        public static final String ACTION_FIELD_NAME = "Action_Field_Name";
        public static final String ACTION_FIELD_VALUE = "Action_Field_Value";
        public static final String CONTAINS_RECORD_ID_MAP = "Contains_Record_Id_Map";

        public static final String EMAIL_STRUCTURE_ID = "emailStructureId";

        public static final String EMAIL_STRUCTURE_NAME ="emailStructureName";

        public static final String FROM_ADDR = "fromAddr";
        public static final String WORKFLOW = "workflow";
        public static final String EXPRESSIONS = "expressions";
        public static final String PARAMETERS = "parameters";
        public static final String FIELD_CHANGE_FIELDS = "Field_Change_Fields";
        public static final String CHANGE_FIELD_NAME = "Change_Field_Name";
        public static final String LINK_NAME = "Link_Name";
    }

    public static class FunctionConstants {
        public static final String NAMESPACE_LINK_NAME = "NameSpace_Link_Name";
        public static final String WORKFLOW_XML_STRING = "Workflow_String";
        public static final String WORKFLOW_STRING = "Workflow_V2_String";
        public static final String UI_MODE = "Workflow_UIMode";
        public static final String TYPE = "Type";
        public static final String RETURN_TYPE = "Return_Type";
        public static final String IS_LOG_NEEDED = "Is_LogNeeded";
        public static final String IS_V2 = "Is_V2Script";
        public static final String RUN_AS_ADMIN = "Run_AsAdmin";
    }
    public static class EmailConstants {
        public static final String NAME = "Name";
        public static final String SUBJECT = "Subject";
        public static final String MESSAGE = "Message";
        public static final String HTML = "Html";
        public static final String DRAFT = "Draft";
        public static final String WORKFLOW = "Workflow";
        public static final String USER_WORKFLOW = "UserWorkflow";
        public static final String IS_V2_SCRIPT = "IsV2Script";
        public static final String WORKFLOW_V2_STRING = "WorkflowV2String";
        public static final String ATTACHMENT_LIST = "AttachmentList";
        public static final String ATTACHMENT = "Attachment";
    }
    public static class WorkFlowConstants{
        public static final String PARAMETERS = "Parameters";
        public static final String PARAMETER = "Parameter";
        public static final String EXPRESSIONS = "Expressions";
        public static final String EXPRESSION = "Expression";

    }
    public static class AssetCategoryConstants{
        public static final String ASSET_CATEGORY_NAME = "Asset_Category_Name";
        public static final String ASSET_TYPE = "Asset_Type";
        public static final String DISPLAY_NAME = "Display_Name";
        public static final String PARENT_CATEGORY_NAME = "Parent_Category_Name";
        public static final String ASSET_MODULE_NAME = "Asset_Module_Name";
        public static final String IS_DEFAULT = "Is_Default";
        public static final String DESCRIPTION="Description";
        public static final String ASSET_CATEGORY="AssetCategory";
        public static final String IS_DELETED = "Is_Deleted";
    }
    public static class CustomButtonConstants{
        public static final String FORM_DATA_JSON="formDataJson";
        public static final String WIDGET_LINK_NAME="Widget_Link_Name";
        public static final String RECORD_ID="recordId";
        public static final String CONFIG="config";
        public static final String ACTION_TYPE="actionType";
        public static final String URL="url";
        public static final String CONNECTED_APP_ID="appId";
        public static final String WIDGET_ID="widgetId";
        public static final String NAVIGATE_TO="navigateTo";
        public static final String CONNECTED_APP_LINK_NAME="Connected_App_Link_Name";
        public static final String SELECTED_FIELD_ID="Selected_Field_Id";
        public static final String SELECTED_FIELDS_MODULE_NAME="Selected_Fields_Module_Name";

        public static final String SHOULD_FORM_INTERFACE_APPLY="Should_Form_Interface_Apply";
        public static final String POSITION_TYPE="Position_Type";
        public static final String FORM_MODULE_NAME="Form_Module_Name";
        public static final String FORM_ID="Form_Id";
        public static final String CUSTOM_BUTTON_CRITERIA="Custom_Button_Criteria";
        public static final String BUTTON_TYPE="Button_Type";
    }
    public static class AssetDepartmentConstants{
        public static final String ASSET_DEPARTMENT_NAME = "Asset_Department_Name";
    }
    public static class AssetTypeConstants{
        public static final String ASSET_TYPE_NAME = "Asset_Type_Name";
        public static final String IS_MOVABLE = "Is_Movable";

    }
    public static class TicketCategoryConstants{
        public static final String TICKET_CATEGORY_NAME = "Ticket_Category_Name";
        public static final String TICKET_DISPLAY_NAME = "Ticket_Display_Name";
        public static final String DESCRIPTION = "Description";

    }
    public static class TicketTypeConstants{
        public static final String TICKET_TYPE_NAME = "Asset_Type_Name";
        public static final String DESCRIPTION = "Description";

    }
    public static class TicketPriorityConstants{
        public static final String PRIORITY = "Priority";
        public static final String SEQUENCE_NUMBER = "Sequence_Number";
        public static final String COLOUR = "Colour";
        public static final String DISPLAY_NAME = "Display_name";
        public static final String DESCRIPTION = "Description";
        public static final String IS_DEFAULT = "Is_Default";
    }
    public static class SpaceCategoryConstants{
        public static final String SPACE_CATEGORY_NAME = "Space_Category_Name";
        public static final String DESCRIPTION = "Description";

        public static final String COMMON_AREA = "Common_Area";
        public static final String SPACE_MODULE_NAME = "Space_Module_Name";
    }


    public static class RelationRequestConstants{
        public static final String NAME = "Name";
        public static final String DESCRIPTION = "Description";
        public static final String FROM_MODULE_NAME = "From_Module_Name";
        public static final String TO_MODULE_NAME = "Display_Name";
        public static final String FORWARD_RELATION_NAME = "Relation_Name";
        public static final String REVERSE_RELATION_NAME = "Reverse_Relation_Name";
        public static final String RELATION_TYPE = "Relation_Type";
    }
    public static class SitePackageConstants{
        public static final String NAME = "Name";
        public static final String DESCRIPTION = "Description";
        public static final String MANAGED_BY = "Managed_By";
        public static final String SITE_TYPE = "Site_Type";
        public static final String GROSS_FLOOR_AREA = "Gross_Floor_Area";
        public static final String AREA = "Area";
        public static final String CDD_BASE_TEMPERATURE = "Cdd_Base_Temperature";
        public static final String HDD_BASE_TEMPERATURE = "Hdd_Base_Temperature";
        public static final String WDD_BASE_TEMPERATURE = "Wdd_Base_Temperature";
        public static final String TIMEZONE = "TimeZone";
        public static final String BOUNDARY_RADIUS = "Boundary_Radius";
        public static final String FAILURE_CLASS = "Failure_Class";
        public static final String FORM_NAME = "Form_Name";
    }

    public static class ValidationRuleConstants {
        public static final String NAME = "Name";
        public static final String PARENT_FORM_NAME = "Parent_Form_Name";
        public static final String PARENT_FORM_MODULE_NAME = "Parent_Form_Module_Name";
        public static final String ERROR_MESSAGE = "Error_Message";
        public static final String NAMED_CRITERIA_NAME = "Named_Criteria_Name";
        public static final String PLACE_HOLDER_SCRIPT = "Place_Holder_Script";

    }
    public static final class NamedCriteriaConstants {
        public static final String NAMED_CRITERIA_NAME = "Named_Criteria_Name";
        public static final String NAMED_CONDITION_NAME = "Named_Condition_Name";
        public static final String NAMED_CONDITION_TYPE = "Named_Condition_Type";

        public static final String NAMED_CRITERIA_PATTERN = "Named_Criteria_Pattern";
        public static final String NAMED_CONDITION = "Named_Condition";
        public static final String NAMED_CONDITION_SEQUENCE = "Named_Condition_Sequence";
        public static final String NAMED_CONDITIONS_LIST = "Named_Conditions_List";
    }
    public static class NewPermissionConstants {
        public static final String ROLE_NAME = "Role_Name";
        public static final String ROUTE_NAME = "Route_Name";
        public static final String PERMISSION = "Permission";
        public static final String APP_LINK_NAME = "App_Link_Name";
    }
    public static class GroupConstants {
        public static final String GROUP_NAME="Group_Name";
        public static final String NAME = "Name";
        public static final String DESCRIPTION = "Description";
        public static final String SITE_NAME = "Site_Name";
        public static final String EMAIL = "Email";
        public static final String PHONE = "Phone";
        public static final String PEOPLE_GROUP_MEMBER = "People_Group_Member";
        public static final String IS_ACTIVE = "Is_Active";
        public static final String PEOPLE_GROUP_MEMBERS = "People_Group_Members";
    }
    public static class PeopleGroupMembersConstants {
        public static final String PEOPLE = "People";
        public static final String EMAIL = "Email";
    }

    public static class FormRuleConstants {

        public static final String FORM_RULE_CRITERIA = "Form_Rule_Criteria";
        public static final String SUB_FORM_RULE_CRITERIA = "Sub_Form_Rule_Criteria";
        public static final String RULE_TYPE = "Rule_Type";
        public static final String TYPE = "Type";
        public static final String TRIGGER_TYPE = "Trigger_Type";
        public static final String FORM_NAME = "Form_Name";
        public static final String SUB_FORM_NAME = "Sub_Form_Name";
        public static final String SUB_FORM_MODULE_NAME = "Sub_Form_Module_Name";
        public static final String FORM_ON_LOAD_RULE_TYPE = "Form_On_Load_Rule_Type";
        public static final String EXECUTE_TYPE = "Execute_Type";
        public static final String TRIGGER_FIELDS_LIST = "Trigger_Fields_List";
        public static final String TRIGGER_FIELD = "Trigger_Field";
        public static final String STATUS = "Status";
        public static final String FORM_ACTIONS_LIST = "Form_Rule_Action_List";
        public static final String FORM_RULE_ACTION = "Form_Rule_Action";
        public static final String FORM_RULE_ACTION_TYPE = "Form_Rule_Action_Type";
        public static final String FORM_RULE_ACTION_FIELD = "Form_Rule_Action_Field";
        public static final String FORM_RULE_ACTION_META = "Form_Rule_Action_Meta";

        public static final String FILTER_CRITERIA = "Filter_Criteria";
        public static final String FORM_FIELD_DISPLAY_NAME = "Form_Field_Display_Name";
    }

    public static class ReadingKPIConstants{
        public static final String FREQUENCY="Frequency";
        public static final String KPI_CATEGORY="KPI_Category";
        public static final String METRIC_UNIT="Metric_Unit";
        public static final String UNIT="Unit";
        public static final String KPI_TYPE="KPI_Type";
        public static final String RESOURCE_TYPE="Resource_Type";

    }

    public static class NameSpaceConstants {
        public static final String NAMESPACE = "Namespace";
        public static final String NAMESPACE_FIELD = "Namespace_Fields";
        public static final String EXEC_INTERVAL = "execInterval";
        public static final String NS_TYPE = "type";
        public static final String INCLUDED_ASSETS = "Included_AssetIds";
        public static final String VAR_NAME = "varName";
        public static final String AGG_TYPE = "aggregationType";
        public static final String DATA_INTERVAL = "dataInterval";
        public static final String NS_FIELD_TYPE = "nsFieldType";
        public static final String RELATED_READINGS = "Related_Readings";
        public static final String RELATED_INFO = "Related_Info";
        public static final String RELATION_AGG_TYPE = "relAggregationType";
        public static final String FIELD_NAME = "fieldName";

    }

    public static class ReadingRuleConstants {
        public static final String ALARM_DETAILS = "Alarm_Details";
        public static final String MESSAGE = "message";
        public static final String SEVERITY = "severity";
        public static final String FAULT_TYPE = "faultType";
        public static final String PROBLEM = "problem";
        public static final String POSSIBLE_CAUSES = "possibleCauses";
        public static final String RECOMMENDATIONS = "recommendations";
        public static final String RCA_CONDITIONS = "RCA_Conditions";
        public static final String RCA_GROUPS = "Groups";
        public static final String RCA = "Root_Cause";
        public static final String DATA_SET_INTERVAL = "dataSetInterval";
        public static final String RULE_INTERVAL = "ruleInterval";
        public static final String RCA_RULE_IDS = "RCA_RuleIds";
        public static final String AUTO_CLEAR = "autoClear";
    }

    public static class FaultImpactConstants {
        public static final String PM_ASSIGNMENT_TYPE = "PMAssignment_Type";
        public static final String FAULT_IMPACT = "faultImpact";
    }


    public static class UserConstants {
        public static final String ROLE = "Role";
        public static final String EMAIL = "Email";
        public static final String USERID = "userId";
        public static final String MOBILE = "Mobile";
        public static final String PHONE = "Phone";
        public static final String NAME = "Name";
        public static final String USER_NAME = "UserName";
        public static final String TIMEZONE = "TimeZone";
        public static final String LANGUAGE = "Language";
        public static final String IS_USER = "Is_User";
        public static final String PEOPLE_TYPE = "People_Type";
        public static final String APP_LINK_NAME = "App_Link_Name";
        public static final String IDENTIFIER = "Identifier";
        public static final String DELETED_TIME = "Deleted_Time";
        public static final String PARENT_UID = "Parent_UId";
        public static final String IS_SUPER_USER = "Is_Super_User";
        public static final String OCCUPANT_PORTAL_ACCESS = "Occupant_Portal_Access";
        public static final String EMPLOYEE_PORTAL_ACCESS = "Employee_Portal_Access";
        public static final String ROLE_APP_SCOPING = "Role_App_Scoping";
        public static final String ROLE_APP_SCOPING_LIST = "Role_App_Scoping_List";
        public static final String ADDITIONAL_PROPS = "Additional_Props";
        public static final String IS_PRIMARY_CONTACT = "Is_Primary_Contact";
        public static final String PRIMARY_CONTACT_NAME = "Primary_Contact_Name";
        public static final String PRIMARY_CONTACT_EMAIL = "Primary_Contact_EMail";
        public static final String PRIMARY_CONTACT_PHONE = "Primary_Contact_Phone";

    }

    public class GlobalVariableConstants{
        public static final String VARIABLE_NAME = "Variable_Name";
        public static final String LINK_NAME = "Link_Name";
        public static final String TYPE = "Type";
        public static final String VALUE_STRING = "Value_String";
        public static final String GROUP_LINK_NAME = "Group_Link_Name";
    }
    public class GlobalVariableGroupConstants{
        public static final String LINK_NAME = "Link_Name";
        public static final String GROUP_NAME = "Variable_Name";
    }

    public static class ConnectionContext {

        public static final String NAME = "name";
        public static final String SERVICE_NAME = "serviceName";
        public static final String AUTH_TYPE = "authType";
        public static final String GRANT_TYPE = "grantType";
        public static final String STATE = "state";
        public static final String AUDIENCE = "audience";
        public static final String RESOURCE = "resource";
        public static final String SCOPE = "scope";

    }
    public class FileConstants{
        public static final String FILE_NAME = "File_Name";
        public static final String FILE_PATH = "File_Path";
        public static final String UNIQUE_FILE_IDENTIFIER = "Unique_File_Identifier";
        public static final String CONTENT_TYPE = "Content_Type";
    }

    public static class ValueGeneratorConstants{
        public static final String ID = "Id";
        public static final String SPECIAL_MODULE_NAME = "Special_Module_Name";
        public static final String MODULE_NAME = "Module_Name";
        public static final String LINK_NAME = "Link_Name";
        public static final String DISPLAY_NAME = "Display_Name";
        public static final String IS_CONSTANT = "Is_Constant";
        public static final String IS_HIDDEN = "Is_Hidden";
        public static final String IS_SYSTEM = "Is_System";
        public static final String OPERATOR_ID = "Operator_Id";
        public static final String VALUE_GENERATOR_TYPE = "Value_Generator_Type";
        public static final List<String> DEFAULT_CREATED_VALUE_GENERATOR = Arrays.asList(
                "com.facilio.modules.AccessibleBasespaceValueGenerator",
                "com.facilio.modules.AudienceValueGenerator",
                "com.facilio.modules.TenantValueGenerator",
                "com.facilio.modules.VendorValueGenerator",
                "com.facilio.modules.ClientValueGenerator",
                "com.facilio.modules.TerritoryBasedOnPeopleValueGenerator"
        );
    }

    public static class GlobalScopeVariableConstants{
        public static final String LINK_NAME = "Link_Name";
        public static final String DISPLAY_NAME = "Display_Name";
        public static final String DESCRIPTION = "Description";
        public static final String VALUE_GENERATOR_LINK_NAME = "Value_Generator_Link_Name";
        public static final String APPLICABLE_MODULE_NAME = "Applicable_Module_Name";
        public static final String SCOPE_VARIABLE_MODULES_FIELDS_LIST = "Scope_Variable_Modules_Fields_List";
        public static final String MODULES_FIELD = "Modules_Field";
        public static final String MODULES_NAME = "Modules_Name";
        public static final String FIELD_NAME = "Field_Name";
        public static final String SHOW_SWITCH = "Show_Switch";
        public static final String STATUS = "Status";
        public static final String APP_LINK_NAME = "App_Link_Name";
        public static final String DELETED = "Deleted";
        public static final String TYPE = "Type";
        public static final String VALUE_GENERATOR = "Value_Generator";

        public static final List<String> DEFAULT_CREATED_GLOBAL_SCOPE_VARIABLE = Arrays.asList(
                "default_maintenance_site",
                "default_tenant_user",
                "default_vendor_user",
                "default_client_user",
                "default_remotemonitor_client",
                "default_energy_site",
                "default_fsm_territory"
        );
    }
    public static class UserScopingConstants{
        public static final String SCOPE_LINK_NAME = "Scope_Link_Name";
        public static final String DISPLAY_NAME = "Display_Name";
        public static final String DESCRIPTION = "Description";
        public static final String STATUS = "Status";
        public static final String IS_DEFAULT = "Is_Default";
        public static final String APPLICATION_NAME = "Application_Name";
        public static final String USER_SCOPING_CONFIGS = "User_Scoping_Configs";
        public static final String PEOPLE_USER_SCOPING_CONFIGS = "People_User_Scoping_Configs";

    }

    public static class UserScopingConfigConstants{
        public static final String USER_SCOPING_CONFIG = "User_Scoping_Config";
        public static final String SCOPE_LINK_NAME = "Scope_Link_Name";
        public static final String MODULE_NAME = "Module_Name";
        public static final String CRITERIA_ID = "Criteria_Id";
        public static final String CRITERIA = "Criteria";
        public static final String FIELD_NAME = "Field_Name";
        public static final String VALUE = "Value";
        public static final String OPERATOR_ID = "Operator_Id";
        public static final String VALUE_GENERATOR = "Value_Generator";
    }

    public static class PeopleUserScopingConstants{
        public static final String PEOPLE_USER_SCOPING_CONFIG = "People_User_Scoping_Config";
        public static final String SCOPE_LINK_NAME = "Scope_Link_Name";
        public static final String PEOPLE_MAIL = "People_Mail";
    }
    public static class PermissionSetConstants{
        public static final String LINK_NAME = "Link_Name";
        public static final String DISPLAY_NAME = "Display_Name";
        public static final String DESCRIPTION = "Description";
        public static final String STATUS = "Status";
        public static final String IS_PRIVILEGED = "Is_Privileged";
        public static final String IS_DELETED = "Is_Deleted";
        public static final String PERMISSION_SET_CONFIGS = "Permission_Set_Configs";
        public static final String PEOPLE_PERMISSION_SET_CONFIGS = "People_Permission_Set_Configs";
    }

    public static class PermissionSetConfig{
        public static final String PERMISSION_SET_CONFIG = "Permission_Set_Config";
        public static final String MODULE_NAME = "Module_Name";
        public static final String PERMISSION_TYPE = "Permission_Type";
        public static final String TYPE = "Type";
        public static final String PERMISSION = "Permission";
        public static final String PERMISSION_SET_LINK_NAME = "Permission_Set_Link_Name";

        public static final String RELATED_MODULE_NAME = "Related_Module_Name";
        public static final String RELATED_FIELD_NAME = "Related_Field_Name";

        public static final String FIELD_NAME = "Field_Name";
    }

    public static class PeoplePermissionSetConfig{
        public static final String PEOPLE_PERMISSION_SET_CONFIG = "People_Permission_Set_Config";
        public static final String PERMISSION_SET_LINK_NAME = "Permission_Set_Link_Name";
        public static final String PEOPLE_MAIL = "People_Mail";
    }
    
    public static final class ServiceCatalogConstants{
        public static final String NAME="Name";
        public static final String DESCRIPTION="Description";
        public static final String LINK_NAME="LinkName";
        public static final String GROUP_LINK_NAME="GroupLinkName";
        public static final String TYPE="Type";
        public static final String MODULE_NAME="ModuleName";
        public static final String FORM_NAME="FormName";
        public static final String EXTERNAL_URL="ExternalURL";
        public static final String APP_NAME="AppName";
        public static final String SHARING="Sharing";
        public static final String PHOTO="Photo";
    }

    public static class DashboardConstants {
        public static final String NAME = "Name";
        public static final String DASHBOARD_TAB_ID = "dashboardTabId";
        public static final String APP_NAME = "AppName";
        public static final String MODULE_NAME = "ModuleName";
        public static final String ID = "id";
        public static final String DISPLAY_ORDER = "DisplayOrder";
        public static final String DASHBOARD_FOLDER_ID = "Dashboard_Folder_Id";
        public static final String LINK_NAME ="linkName";
        public static final String CREATED_BY_USER_ID = "createdByUserId";
        public static final String PUBLISH_STATUS = "publishStatus";
        public static final String DASHBOARD_URL = "dashboardUrl";
        public static final String SHOW_HIDE_MOBILE = "showHideMobile";
        public static final String DATE_OPERATOR = "dateOperator";
        public static final String DATE_VALUE = "dateValue";
        public static final String IS_TAB_ENABLED = "isTabEnabled";
        public static final String DASHBOARD_TAB_PLACEMENT = "dashboardTabPlacement";
        public static final String CLIENT_META_JSON = "clientMetaJson";
        public static final String CREATED_BY = "createdBy";
        public static final String MODIFIED_BY = "modifiedBy";
        public static final String CREATED_TIME = "createdTime";
        public static final String MODIFIED_TIME = "modifiedTime";
        public static final String LOCKED = "locked";
        public static final String FOLDER_LINK_NAME = "folderLinkName";
        public static final String SEQUENCE = "sequence";
        public static final String DASHBOARD_LINK_NAME = "dashboardLinkName";
        public static final String SUB_TAB_LINK_NAME = "subTabLinkName";
        public static final String TAB_LINK_NAME = "tabLinkName";
        public static final String TIME_LINE_FILTER_ENABLE = "timeLineFilterEnabled";
        public static final String DATE_LABEL = "dateLabel";
        public static final String WIDGET_TIMELINE_FILTER_STATUS = "widgetTimeLineFilterStatus";
        public static final String WIDGET_NAME = "widgetName";
        public static final String TYPE = "type";
        public static final String WIDGET_URL = "widgetUrl";
        public static final String DATA_REFRESH_INTERTVEL = "dataRefreshIntervel";
        public static final String HEADER_TEXT = "headerText";
        public static final String HEADER_SUB_TEXT = "headerSubText";
        public static final String HEADER_IS_EXPORT = "headerIsExport";
        public static final String META_JSON = "metaJson";
        public static final String WIDGET_SETTINGS_JSON = "widgetSettingsJson";
        public static final String HELP_TEXT = "helpText";
        public static final String SECTION_LINK_NAME = "sectionLinkName";
        public static final String DESCRIPTION = "description";
        public static final String BANNER_DETAILS = "bannerDetails";
        public static final String IS_COLLAPSED = "isCollapsed";
        public static final String IS_RESIZE_ENABLED = "isResizeEnabled";
        public static final String COMPONENT_TYPE = "componentType";
        public static final String OPTION_TYPE = "optionType";
        public static final String IS_OTHERS_OPTION_ENABLED = "isOthersOptionEnabled";
        public static final String IS_ALL_OPTION_ENABLED = "isAllOptionEnabled";
        public static final String SHOW_ONLY_RELEVANT_VALUES = "showOnlyRelevantValues";
        public static final String LABEL = "label";
        public static final String DASHBOARD_USER_FILTER_JSON = "dashboardUserFilterJson";
        public static final String FILTER_ORDER = "filterOrder";
        public static final String CRITERIA_ID = "criteriaId";
        public static final String WIDGET_LINK_NAME = "widgetLinkName";
        public static final String FILTER_LINK_NAME = "filterLinkName";
        public static final String FIELD_LINK_NAME = "fieldLinkName";
        public static final String FIELD_MODULE_NAME = "fieldModuleName";
        public static final String DASHBOARD_TAB = "DASHBOARD_TAB";
        public static final String TAB_ELEMENTS_LIST = "DASHBOARD_TABS";
        public static final String DASHBOARD_FILTER = "DASHBOARD_FILTER";
        public static final String FILTER_MAP = "filterMap";
        public static final String TAB_MAP = "tabMap";
        public static final String SECTION_ID = "sectionId";
        public static final String STATIC_KEY = "staticKey";
        public static final String PARAMS_JSON = "paramsJson";
        public static final String VIEW_NAME = "viewName";
        public static final String WEB_URL = "webUrl";
        public static final String SECTION_DASHBOARD_WIDGET = "SECTION_DASHBOARD_WIDGET";
        public static final String SECTION_WIDGETS = "SECTION_WIDGETS";
        public static final String WIDGET = "widget";
        public static final String SKIP_LINK_NAME = "skipLinkName";
        public static final String FIELD_NAME = "fieldName";
        public static final String DASHBOARD_FILTER_ID = "dashboardFilterId";
        public static final String META_JSON_WIDGET = "metaJsonWidget";
        public static final String CARD_LAYOUT = "cardLayout";
        public static final String SCRIPT_MODE_INT = "scriptModeInt";
        public static final String CUSTOM_SCRIPT = "customScript";
        public static final String CARD_STATE = "cardState";
        public static final String CONDITIONAL_FORMATTING="conditionalFormatting";
        public static final String CARD_DRILL_DOWN = "cardDrillDown";
        public static final String CARD_PARAMS = "cardParams";
        public static final String PARENT_ID = "parentId";
        public static final String GAUGE_LAYOUT_1 = "gauge_layout_1";
        public static final String GAUGE_LAYOUT_2 = "gauge_layout_2";
        public static final String GAUGE_LAYOUT_3 = "gauge_layout_3";
        public static final String GAUGE_LAYOUT_4 = "gauge_layout_4";
        public static final String GAUGE_LAYOUT_5 = "gauge_layout_5";
        public static final String GAUGE_LAYOUT_6 = "gauge_layout_6";
        public static final String GAUGE_LAYOUT_7 = "gauge_layout_7";
        public static final String MIN_SAFE_LIMIT_TYPE  = "minSafeLimitType";
        public static final String MAP_CARD_LAYOUT_1 = "mapcard_layout_1";
        public static final String WEATHER_CARD_LAYOUT_1="weathercard_layout_1";
        public static final String GRAPHICAL_CARD_LAYOUT_1= "graphicalcard_layout_1";
        public static final String KPI_CARD_LAYOUT_1 = "kpicard_layout_1";
        public static final String KPI_CARD_LAYOUT_2 = "kpicard_layout_2";
        public static final String PM_READINGS_LAYOUT= "pmreadings_layout_1";
        public static final String WEB_LAYOUT_1= "web_layout_1";
        public static final String PHOTOS_LAYOUT_1 = "photos_layout_1";
        public static final String TABLE_LAYOUT_1 = "table_layout_1";
        public static final String NEW_REPORT_NAME = "newReportName";
        public static final String REPORT_NAME = "ReportName";

        public static final String CHART_TYPE ="chartType";
        public static final String DATE_FILTER_ID = "dateFilterId";
        public static final String DASHBOARD_SHARING = "DASHBOARD_SHARING";
        public static final String BASE_SPACE_ID = "baseSpaceId";
        public static final String PHOTO_ELEMENT = "photoElement";
    }
    public static class ModuleKpiConstants {
        public static final String METRIC_ID ="metricId";
        public static final String DATE_FIELD_ID = "dateFieldId";
        public static final String METRIC_NAME = "metricName";
        public static final String KPI_CATEGORY = "kpiCategory";
        public static final String TARGET = "target";
        public static final String MIN_TARGET = "minTarget";
        public static final String ACTIVE = "active";
        public static final String PERIOD = "period";
        public static final String AGGREGATION = "Aggregation";
        public static final String METRIC_MODULE_NAME = "metricModuleName";
        public static final String METRIC_FIELD_NAME = "metricFieldName";
        public static final String DATE_FIELD_NAME = "dateFieldName";
        public static final String DATE_MODULE_NAME = "dateModuleName";
        public static final String SITE_ID = "siteId";
    }
    public static class ReportsConstants {

        public static final String CHART_STATE_JSON = "chartStateJson";
        public static final String TABULAR_STATE_JSON = "tabularStateJson";
        public static final String COMMON_STATE_JSON = "commonStateJson";
        public static final String REPORT_STATE_JSON = "reportStateJson";
        public static final String X_AGGR = "xAggr";
        public static final String DATE_RANGE_JSON = "dateRangeJson";
        public static final String DATA_POINT_JSON = "dataPointJson";
        public static final String FILTERS_JSON = "filtersJson";
        public static final String BASELINE_JSON = "baseLineJson";
        public static final String BENCHMARK_JSON = "benchMarkJson";
        public static final String ANALYTICS_TYPE = "analyticsType";
        public static final String REPORT_TYPE = "reportType";
        public static final String BOOLEAN_SETTINGS = "booleansSettings";
        public static final String TRANSFORM_CLASS = "transformClass";
        public static final String IS_TEMP = "isTemp";
        public static final String MODULE_TYPE = "moduleType";
        public static final String USER_FILTER_JSON = "userFilterJson";
        public static final String REPORT_TEMPLATE = "reportTemplate";
        public static final String TIME_FILTER = "timeFilter";
        public static final String DATA_FILTER = "dataFilter";
        public static final String DRILL_DOWN_PATH_JSON = "drillDownPathJson";
        public static final String REPORT_SETTINGS_JSON = "reportSettingsJson";
        public static final String SITE_ID = "siteId";
        public static final String REPORT = "report";
        public static final String REPORT_ID = "reportId";
        public static final String FOLDER_TYPE = "folderType";
        public static final String REPORT_FOLDER="reportFolder";
        public static final String ACTION_TYPE = "actionType";
        public static final String ADD = "ADD";
        public static final String DATA_POINTS = "DataPoints";
        public static final String DATA_POINT = "DataPoint";
        public static final String FILTERS = "Filters";
        public static final String FILTER_ELEMENT = "FilterElement";
        public static final String REPORT_TEMPLATE_ELEMENT = "reportTemplateElement";
        public static final String TABULAR_STATE_ELEMENT = "tabularStateElement";
        public static final String TEMPLATE_JSON_ELEMENT = "templateJSONElement";
        public static final String CONDITIONAL_FORMATTING_ELEMENT = "conditionalFormattingElement";
        public static final String CONDITIONAL_FORMAT_OBJECT = "conditionalFormatObject";
        public static final String CONDITIONAL_FORMAT_JSON = "conditionalFormatJson";
        public static final String CONDITION_ELEMENT = "conditionElement";
        public static final String CONDITION_JSON = "conditionJson";
        public static final String TEMPLATE_JSON = "templateJson";
        public static final String OTHER_CRITERIA = "otherCriteria";
        public static final String DIMENSION_PICK_LIST = "dimensionPickList";
        public static final String GROUP_BY_PICK_LIST = "groupByPickList";
        public static final String USER_FILTER_CHOSEN_LIST = "userFilterChosenList";
        public static final String USER_FILTER_DEFAULT_LIST = "userFilterDefaultList";
        public static final String USER_FILTER_ALL_LIST = "userFilterAllList";
        public static final String CONFIG_CRITERIA = "configCriteria";
        public static final String PARENT_CRITERIA_FILTER = "parentCriteriaFilter";
    }
}
