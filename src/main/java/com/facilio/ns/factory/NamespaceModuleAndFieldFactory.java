package com.facilio.ns.factory;

import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.List;

import static com.facilio.modules.ModuleFactory.getRuleBuilderConfigModule;

public class NamespaceModuleAndFieldFactory extends FieldFactory {

    public static FacilioModule getNamespaceModule() {
        FacilioModule module = new FacilioModule();
        module.setName("namespace");
        module.setDisplayName("Namespace Configuration");
        module.setTableName("Namespace");
        return module;
    }

    public static FacilioModule getNamespaceFieldsModule() {
        FacilioModule module = new FacilioModule();
        module.setName("namespace_fields");
        module.setDisplayName("Namespace Field Configuration");
        module.setTableName("Namespace_Fields");
        return module;
    }

    public static List<FacilioField> getNamespaceFieldFields() {
        FacilioModule module = getNamespaceFieldsModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getStringField("varName", "VAR_NAME", module));
        fields.add(getNumberField("nsId", "NAMESPACE_ID", module));
        fields.add(getNumberField("resourceId", "RESOURCE_ID", module));
        fields.add(getNumberField("fieldId", "FIELD_ID", module));
        fields.add(getNumberField("dataInterval", "DATA_INTERVAL", module));
        fields.add(getField("aggregationTypeI", "AGGREGATION_TYPE", module, FieldType.NUMBER));
        return fields;
    }

    public static List<FacilioField> getNamespaceFields() {
        FacilioModule module = getNamespaceModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(getIdField(module));
        fields.add(getNumberField("parentRuleId", "PARENT_RULE_ID", module));
        fields.add(getNumberField("execInterval", "EXEC_INTERVAL", module));
        return fields;
    }

    public static List<FacilioField> getNSAndFields() {
        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(getNamespaceFields());
        fields.addAll(getNamespaceFieldFields());
        return fields;
    }
}
