package com.facilio.ns.factory;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.List;

public class NamespaceModuleAndFieldFactory extends FieldFactory {

    public static FacilioModule getNamespaceModule() {
        FacilioModule module = new FacilioModule();
        module.setName("namespace");
        module.setDisplayName("Namespace Configuration");
        module.setTableName("Namespace");
        module.setType(FacilioModule.ModuleType.CUSTOM);
        return module;
    }

    public static FacilioModule getNamespaceFieldsModule() {
        FacilioModule module = new FacilioModule();
        module.setName("namespace_fields");
        module.setDisplayName("Namespace Field Configuration");
        module.setTableName("Namespace_Fields");
        module.setType(FacilioModule.ModuleType.CUSTOM);
        return module;
    }

    public static FacilioModule getNamespaceInclusionModule() {
        FacilioModule module = new FacilioModule();
        module.setName("namespaceIncl");
        module.setDisplayName("Namespace Inclusions Resources");
        module.setTableName("Namespace_Inclusions");
        module.setType(FacilioModule.ModuleType.CUSTOM);
        return module;
    }

    public static FacilioModule getNamespaceFieldRelatedModule() {
        FacilioModule module = new FacilioModule();
        module.setName("namespaceFieldRelated");
        module.setDisplayName("Namespace Field Related");
        module.setTableName("Namespace_Field_Related");
        module.setType(FacilioModule.ModuleType.CUSTOM);
        return module;
    }

    public static List<FacilioField> getNamespaceFieldFields() {
        FacilioModule module = getNamespaceFieldsModule();
        List<FacilioField> fields = new ArrayList<>();
        FacilioField nsFieldId = getIdField(module);
        nsFieldId.setName("nsFieldId");
        fields.add(nsFieldId);
        fields.add(getStringField("varName", "VAR_NAME", module));
        fields.add(getNumberField("nsId", "NAMESPACE_ID", module));
        fields.add(getNumberField("resourceId", "RESOURCE_ID", module));
        fields.add(getNumberField("fieldId", "FIELD_ID", module));
        fields.add(getNumberField("dataInterval", "DATA_INTERVAL", module));
        fields.add(getNumberField("aggregationTypeI", "AGGREGATION_TYPE", module));
        fields.add(getBooleanField("primary", "IS_PRIMARY", module));
        fields.add(getNumberField("nsFieldTypeI", "NS_FIELD_TYPE", module));
        fields.add(getNumberField("defaultExecutionModeI", "DEFAULT_EXEC_MODE", module));
        fields.add(getNumberField("defaultValue", "DEFAULT_VALUE", module));
        return fields;
    }

    public static List<FacilioField> getNamespaceFields() {
        FacilioModule module = getNamespaceModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getNumberField("parentRuleId", "PARENT_RULE_ID", module));
        fields.add(getNumberField("categoryId", "CATEGORY_ID", module));
        fields.add(getNumberField("execInterval", "EXEC_INTERVAL", module));
        fields.add(getNumberField("type", "TYPE", module));
        fields.add(getNumberField("workflowId", "WORKFLOW_ID", module));
        fields.add(getBooleanField("status", "STATUS", module));
        fields.add(getNumberField("loggerLevel", "LOGGER_LEVEL", module));
        fields.add(getNumberField("execMode", "EXECUTOR_MODE", module));
        fields.add(getNumberField("executorId", "EXECUTOR_ID", module));
        fields.add(getBooleanField("isDeleted", "SYS_DELETED", module));
        fields.add(getNumberField("resourceType", "RESOURCE_TYPE", module));
        return fields;
    }

    public static List<FacilioField> getNamespaceInclusionFields() {
        FacilioModule module = getNamespaceInclusionModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getNumberField("namespaceId", "NAMESPACE_ID", module));
        fields.add(getNumberField("resourceId", "RESOURCE_ID", module));
        return fields;
    }

    public static List<FacilioField> getNamespaceFieldRelatedFields() {
        FacilioModule module = getNamespaceFieldRelatedModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getNumberField("nameSpaceFieldId", "NAMESPACE_FIELD_ID", module));
        fields.add(getNumberField("relMapId", "RELATION_MAPPING_ID", module));
        fields.add(getNumberField("criteriaId", "CRITERIA_ID", module));
        fields.add(getNumberField("relAggregationTypeInt", "REL_AGGREGATION_TYPE", module));
        return fields;
    }

    public static List<FacilioField> getNSAndFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(getNamespaceFields());
        fields.addAll(getNamespaceFieldFields());
        fields.addAll(getNamespaceFieldRelatedFields());
        return fields;
    }

}
