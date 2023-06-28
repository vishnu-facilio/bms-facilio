package com.facilio.permission.factory;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class PermissionSetModuleFactory {

    public static FacilioModule getPeoplePermissionSetModule(){
        FacilioModule userPermissionSetModule = new FacilioModule();
        userPermissionSetModule.setName("peoplepermissionsets");
        userPermissionSetModule.setDisplayName("People Permission Sets");
        userPermissionSetModule.setTableName("PeoplePermissionSets");
        return userPermissionSetModule;
    }

    public static FacilioModule getPermissionSetModule(){
        FacilioModule permissionSetModule = new FacilioModule();
        permissionSetModule.setName("permissionset");
        permissionSetModule.setDisplayName("Permission Set");
        permissionSetModule.setTableName("PermissionSet");
        return permissionSetModule;
    }

    public static FacilioModule getModuleTypePermissionSetModule() {
        FacilioModule moduleTypePermissionSetModule = new FacilioModule();
        moduleTypePermissionSetModule.setName("moduleTypePermissionSet");
        moduleTypePermissionSetModule.setDisplayName("Module Type Permission Set");
        moduleTypePermissionSetModule.setTableName("ModuleTypePermissionSet");
        return moduleTypePermissionSetModule;
    }

    public static FacilioModule getRelatedListPermissionSetModule() {
        FacilioModule relatedListPermissionSetModule = new FacilioModule();
        relatedListPermissionSetModule.setName("relatedListPermissionSet");
        relatedListPermissionSetModule.setDisplayName("Related List Permission Set");
        relatedListPermissionSetModule.setTableName("RelatedListPermissionSet");
        relatedListPermissionSetModule.setExtendModule(getModuleTypePermissionSetModule());
        return relatedListPermissionSetModule;
    }

    public static FacilioModule getFieldPermissionSetModule() {
        FacilioModule relatedListPermissionSetModule = new FacilioModule();
        relatedListPermissionSetModule.setName("fieldPermissionSet");
        relatedListPermissionSetModule.setDisplayName("Field Permission Set");
        relatedListPermissionSetModule.setTableName("FieldPermissionSet");
        relatedListPermissionSetModule.setExtendModule(getModuleTypePermissionSetModule());
        return relatedListPermissionSetModule;
    }
}