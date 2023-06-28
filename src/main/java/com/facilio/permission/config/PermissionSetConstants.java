package com.facilio.permission.config;

import com.facilio.modules.FacilioModule;
import com.facilio.permission.factory.PermissionSetModuleFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PermissionSetConstants {
    public static final Map<String,FacilioModule> EXTENDED_MODULE_REL = Collections.unmodifiableMap(getPermissionSetExtendModuleRel());

    private static Map<String, FacilioModule> getPermissionSetExtendModuleRel() {
        Map<String,FacilioModule> extendModuleRelation = new HashMap<>();
        extendModuleRelation.put(
                    PermissionSetModuleFactory.getRelatedListPermissionSetModule().getName(),
                    PermissionSetModuleFactory.getModuleTypePermissionSetModule()
                );
        extendModuleRelation.put(
                PermissionSetModuleFactory.getFieldPermissionSetModule().getName(),
                PermissionSetModuleFactory.getModuleTypePermissionSetModule()
        );
        return extendModuleRelation;
    }

    public static String PERMISSION_SET = "permissionSet";
    public static String PERMISSION_SET_GROUPING = "permissionSetGrouping";
    public static String PERMISSION_SET_GROUP_ID = "permissionSetGroupId";
    public static String PERMISSION_SET_GROUP_ITEMS = "permissionSetGroupItems";
    public static String PERMISSION_SET_TYPES = "permissionSetTypes";

    public static String PERMISSION_SET_TYPE = "permissionSetType";
    public static String PERMISSION_SET_ID = "permissionSetId";
    public static String PERMISSION_SET_GROUP_TYPE = "permissionSetGroupType";

    public static String PERMISSION_ITEMS = "permissionItems";

    public static String PERMISSION_FIELDS_MAP = "permissionFieldsMap";
    public static String PERMISSIONS_SET_GROUP_KEYS = "permissionSetGroupKeys";

}