package com.facilio.componentpackage.constants;

import java.util.ArrayList;
import java.util.List;

public class PackageConstants {
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
    public static final String COMPONENTS_FOLDER_NAME = "components";
    public static final String XML_FILE_EXTN = "xml";
    public static final String PACKAGE_CONTEXT = "packageContext";
    public static final String CREATED_USER_ID = "createdUserId";
    public static final String MODULENAME = "moduleName";
    public static final String UNIQUE_IDENTIFIER = "uniqueIdentifier";
    public static final String FILE_PATH_SEPARATOR = "/";
    public static final String FILE_EXTENSION_SEPARATOR = ".";
    public static final String PACKAGE_CONF_FILE_NAME = "PackageConf";
    public static final String FILE = "file";
    public static final String FILE_ID = "fileId";
    public static final String SOURCE_ORG_ID = "sourceOrgId";
    public static final String TARGET_ORG_ID = "targetOrgId";
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

    public static class AppXMLConstants {
        public static final String DUPLICATE_APPLICATION_ERROR = "Application with name already present";
        public static final String APPLICATION_NAME = "ApplicationName";
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
    }

    public static class RoleConstants {
        public static final String IS_SUPER_ADMIN = "Is_Super_Admin";
        public static final String IS_PRIVILEGED_ROLE = "Is_Privileged_Role";
        public static final String ROLE_APP_MAPPING = "Role_App_Mapping";
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
        public static final String DEFAULT_TEMPLATE = "Default_Template";

        public static final String TEMPLATE = "Template";
        public static final String TEMPLATE_NAME = "Template_Name";
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
        public static final String FIELD_CHANGE_FIELDS = "Field_Change_Fields";
        public static final String CHANGE_FIELD_NAME = "Change_Field_Name";
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
        public static final String NAME = "name";
        public static final String SUBJECT = "subject";
        public static final String MESSAGE = "message";
        public static final String HTML = "html";
        public static final String DRAFT = "draft";
        public static final String WORKFLOW = "workflow";
        public static final String USER_WORKFLOW = "userWorkflow";
        public static final String IS_V2_SCRIPT = "isV2Script";
        public static final String WORKFLOW_V2_STRING = "workflowV2String";
        public static final String ATTACHMENT_LIST = "attachmentList";

    }
    public static class WorkFlowConstants{
        public static final String PARAMETERS = "parameters";
        public static final String PARAMETER = "parameter";
        public static final String EXPRESSIONS = "expressions";
        public static final String EXPRESSION = "expression";

    }
    public static class AssetCategoryConstants{
        public static final String ASSET_CATEGORY_NAME = "asset_category_name";
        public static final String ASSET_TYPE = "asset_type";
        public static final String DISPLAY_NAME = "display_name";
        public static final String PARENT_CATEGORY_NAME = "parent_category_name";
        public static final String ASSET_MODULE_NAME = "asset_module_name";
        public static final String IS_DEFAULT = "Is_Default";

    }
    public static class AssetDepartmentConstants{
        public static final String ASSET_DEPARTMENT_NAME = "asset_department_name";

    }
    public static class AssetTypeConstants{
        public static final String ASSET_TYPE_NAME = "asset_type_name";
        public static final String IS_MOVABLE = "isMovable";

    }
    public static class TicketCategoryConstants{
        public static final String TICKET_CATEGORY_NAME = "ticket_category_name";
        public static final String TICKET_DISPLAY_NAME = "ticket_display_name";
        public static final String DESCRIPTION = "description";

    }
    public static class TicketTypeConstants{
        public static final String TICKET_TYPE_NAME = "asset_type_name";
        public static final String DESCRIPTION = "description";

    }
    public static class TicketPriorityConstants{
        public static final String PRIORITY = "priority";
        public static final String SEQUENCE_NUMBER = "sequence_number";
        public static final String COLOUR = "colour";
        public static final String DISPLAY_NAME = "display_name";
        public static final String DESCRIPTION = "description";
        public static final String IS_DEFAULT = "Is_Default";
    }
    public static class RelationRequestConstants{
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String LINK_NAME = "link_name";
        public static final String FROM_MODULE_NAME = "from_module_name";
        public static final String TO_MODULE_NAME = "display_name";
        public static final String FORWARD_RELATION_NAME = "relation_name";
        public static final String REVERSE_RELATION_NAME = "reverse_relation_name";
        public static final String RELATION_TYPE = "relation_type";
    }
    public static class SitePackageConstants{
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String MANAGED_BY = "managed_by";
        public static final String SITE_TYPE = "site_type";
        public static final String GROSS_FLOOR_AREA = "gross_floor_area";
        public static final String AREA = "area";
        public static final String CDD_BASE_TEMPERATURE = "cdd_base_temperature";
        public static final String HDD_BASE_TEMPERATURE = "hdd_base_temperature";
        public static final String WDD_BASE_TEMPERATURE = "wdd_base_temperature";
        public static final String TIMEZONE = "timezone";
        public static final String BOUNDARY_RADIUS = "boundary_radius";
        public static final String FAILURE_CLASS = "failure_class";
        public static final String FORM_NAME = "form_name";
    }
}
