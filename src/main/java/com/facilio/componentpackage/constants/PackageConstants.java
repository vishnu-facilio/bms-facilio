package com.facilio.componentpackage.constants;

import java.util.ArrayList;
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
    public static final String APPROVERS="Approvers";
    public static final String APPROVER_LIST="Approver_List";
    public static final String APPROVER_TYPE="Approver_Type";
    public static final String FORM_NAME="Form_Name";
    public static final String NAME = "name";
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
    public static final String XML_FILE_EXTN = "xml";
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
    public static final String PREVIOUS_VERSION = "previousVersion";
    public static final String PACKAGE_CONFIG_XML = "packageConfigXML";
    public static final String USE_LINKNAME_FROM_CONTEXT = "useLinkNameFromContext";
    public static final String DOWNLOAD_URL = "downloadUrl";

    public static final String TYPE_STRING = "typeString";
    public static final String CONSTANT = "constant";

    public static final List<String> ALLOWED_EXTN = new ArrayList<>();

    static {
        ALLOWED_EXTN.add("xml");
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
        public static final String PARENT_CRITERIA = "Parent_Criteria";
        public static final String CONDITIONS_LIST = "Conditions_List";
        public static final String IS_EXPRESSION_VALUE = "Is_Expression_Value";
        public static final String PICKLIST_VALUE = "PickList_Value";
        public static final String PICKLIST_ELEMENT = "PickList_Element";
        public static final String PARENT_MODULE_NAME = "Parent_Module_Name";

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
        public static final String SINGLE_SHARING_CONTEXT = "Single_haring_Context";
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
}
