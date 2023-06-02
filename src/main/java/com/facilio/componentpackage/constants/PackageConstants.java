package com.facilio.componentpackage.constants;

import java.util.ArrayList;
import java.util.List;

public class PackageConstants {
    public static final String NAME = "name";
    public static final String UNIQUE_NAME = "uniqueName";
    public static final String DISPLAY_NAME = "displayName";
    public static final String VERSION = "version";
    public static final String TYPE = "type";
    public static final String PACKAGE_TYPE = "packageType";
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
    public static final String PACKAGE_ROOT_FOLDER = "Package_root_folder";
    public static final String PREVIOUS_VERSION = "previousVersion";
    public static final String PACKAGE_CONFIG_XML = "packageConfigXML";


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
        public static final String MODULE_LIST = "Modules_List";
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
        public static final String DUPLICATE_FIELD_ERROR = "FIELD with name already present";

    }
}
