package com.facilio.permission.factory;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionSetFieldFactory {

    public final static Map<String,List<FacilioField>> MODULE_VS_FIELDS = new HashMap<>(getModuleFields());

    public static List<FacilioField> getUserPermissionSetsFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = PermissionSetModuleFactory.getPeoplePermissionSetModule();

        fields.add(FieldFactory.getIdField(module));
        fields.add(FieldFactory.getNumberField("peopleId", "PEOPLE_ID", module));
        fields.add(FieldFactory.getNumberField("permissionSetId", "PERMISSION_SET_ID", module));
        return fields;
    }
    public static List<FacilioField> getPermissionSetFields() {
        List<FacilioField> fields = new ArrayList<>();
        FacilioModule module = PermissionSetModuleFactory.getPermissionSetModule();

        fields.add(FieldFactory.getIdField(module));
        fields.add(FieldFactory.getField("displayName", "DISPLAY_NAME", module, FieldType.STRING));
        fields.add(FieldFactory.getField("description", "DESCRIPTION", module, FieldType.STRING));
        fields.add(FieldFactory.getSystemField("sysCreatedTime", module));
        fields.add(FieldFactory.getSystemField("sysCreatedBy", module));
        fields.add(FieldFactory.getSystemField("sysModifiedTime", module));
        fields.add(FieldFactory.getSystemField("sysModifiedBy", module));
        fields.add(FieldFactory.getBooleanField("status", "STATUS", module));
        fields.add(FieldFactory.getBooleanField("isPrivileged", "IS_PRIVILEGED", module));
        fields.add(FieldFactory.getStringField("linkName", "LINK_NAME", module));
        fields.add(FieldFactory.getIsDeletedField(module));
        fields.add(FieldFactory.getSysDeletedByField(module));
        fields.add(FieldFactory.getSysDeletedTimeField(module));

        return fields;
    }

    public static List<FacilioField> getModuleTypePermissionSetFields() {
        List<FacilioField> fieldList = new ArrayList<>();
        FacilioModule module = PermissionSetModuleFactory.getModuleTypePermissionSetModule();
        fieldList.add(FieldFactory.getIdField(module));
        fieldList.add(FieldFactory.getNumberField("moduleId", "MODULE_ID", module));
        fieldList.add(FieldFactory.getNumberField("permissionSetId", "PERMISSION_SET_ID", module));
        fieldList.add(FieldFactory.getStringField("permissionType", "PERMISSION_TYPE", module));
        fieldList.add(FieldFactory.getBooleanField("permission", "PERMISSION", module));
        return fieldList;
    }

    public static List<FacilioField> getRelatedListPermissionSetFields() {
        List<FacilioField> fieldList = new ArrayList<>();
        FacilioModule module = PermissionSetModuleFactory.getRelatedListPermissionSetModule();
        fieldList.add(FieldFactory.getIdField(module));
        fieldList.add(FieldFactory.getNumberField("relatedModuleId", "RELATED_MODULE_ID", module));
        fieldList.add(FieldFactory.getNumberField("relatedFieldId", "RELATED_FIELD_ID", module));
        return fieldList;
    }

    private static Map<String,List<FacilioField>> getModuleFields() {
        Map<String,List<FacilioField>> moduleVsFieldMap = new HashMap<>();
        moduleVsFieldMap.put(PermissionSetModuleFactory.getPermissionSetModule().getName(),getPermissionSetFields());
        moduleVsFieldMap.put(PermissionSetModuleFactory.getModuleTypePermissionSetModule().getName(),getModuleTypePermissionSetFields());
        moduleVsFieldMap.put(PermissionSetModuleFactory.getRelatedListPermissionSetModule().getName(),getRelatedListPermissionSetFields());
        return moduleVsFieldMap;
    }
}