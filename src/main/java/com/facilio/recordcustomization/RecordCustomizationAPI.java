package com.facilio.recordcustomization;

import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.List;
import java.util.Map;

public class RecordCustomizationAPI {

    public static long addOrUpdateRecordCustomizationValues(RecordCustomizationContext customization) throws Exception
    {
        if(customization != null && (customization.getValues() == null || customization.getValues().isEmpty()))
        {
            throw new IllegalArgumentException("Invalid Customization Values");
        }

        FacilioModule recordModule = ModuleFactory.getRecordCustomizationModule();
        List<FacilioField> recordFields = FieldFactory.getRecordCustomizationFields(recordModule);
        Map<String, Object> customizationProp = FieldUtil.getAsProperties(customization);
        long customizationId = addRow(recordModule, recordFields, customizationProp);

        FacilioModule customizationValueModule = ModuleFactory.getRecordCustomizationValuesModule();
        List<FacilioField> customizationValueFields = FieldFactory.getRecordCustomizationValueFields(customizationValueModule);
        for(RecordCustomizationValuesContext value : customization.getValues())
        {
            value.setParentId(customizationId);
            Map<String, Object> customizationValueProp = FieldUtil.getAsProperties(value);
            long customizationValueId = addRow(customizationValueModule, customizationValueFields, customizationValueProp);
            value.setId(customizationValueId);
        }
        return customizationId;
    }

    private static long addRow(FacilioModule module, List<FacilioField> fields, Map<String, Object> customizationProperties) throws Exception {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(module.getTableName())
                .fields(fields)
                .addRecord(customizationProperties);
        insertBuilder.save();
        return (long) customizationProperties.get("id");
    }

    public static RecordCustomizationContext getRecordCustomization(Long recordCustomizationId) throws Exception {
        FacilioModule module = ModuleFactory.getRecordCustomizationModule();
        List<FacilioField> fields = FieldFactory.getRecordCustomizationFields(module);
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(recordCustomizationId), NumberOperators.EQUALS));
        Map<String, Object> props = selectBuilder.fetchFirst();
        if(props == null || props.isEmpty())
        {
            return null;
        }

        RecordCustomizationContext customization = FieldUtil.getAsBeanFromMap(props, RecordCustomizationContext.class);
        FacilioModule valuesModule = ModuleFactory.getRecordCustomizationValuesModule();
        List<FacilioField> valuesFields = FieldFactory.getRecordCustomizationValueFields(valuesModule);
        GenericSelectRecordBuilder valuesBuilder = new GenericSelectRecordBuilder()
                .select(valuesFields)
                .table(valuesModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("PARENTID", "parentId", String.valueOf(customization.getId()), NumberOperators.EQUALS));

        List<Map<String, Object>> valueProps = valuesBuilder.get();
        if(valueProps == null || valueProps.isEmpty())
        {
            return null;
        }
        List<RecordCustomizationValuesContext> valuesList = FieldUtil.getAsBeanListFromMapList(valueProps, RecordCustomizationValuesContext.class);
        for (RecordCustomizationValuesContext value : valuesList) {
            long namedCriteriaId = value.getNamedCriteriaId();
            if(namedCriteriaId > 0)
            {
                value.setNamedCriteria(NamedCriteriaAPI.getNamedCriteria(namedCriteriaId));
            }
        }
        customization.setValues(valuesList);

        return customization;
    }

    public static int deleteCustomization(Long recordCustomizationId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getRecordCustomizationModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(recordCustomizationId), StringOperators.IS));
        return builder.delete();

    }
}
