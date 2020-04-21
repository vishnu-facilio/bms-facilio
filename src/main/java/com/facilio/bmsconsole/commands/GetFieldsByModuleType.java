package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;

import java.util.*;

public class GetFieldsByModuleType extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule parentModule = modBean.getModule(moduleName);

        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        for (FacilioField field: allFields) {
            FieldType dataTypeEnum = field.getDataTypeEnum();
            if (dataTypeEnum == FieldType.LOOKUP) {
                LookupField lookupField = (LookupField) field;
                long lookupModuleId = lookupField.getLookupModuleId();
                FacilioModule lookupModule = modBean.getModule(lookupModuleId);
                if (lookupModule == null || lookupModule.getTypeEnum() != FacilioModule.ModuleType.PICK_LIST) {
                    continue;
                }
                List<FacilioField> lookupModuleFields = modBean.getAllFields(lookupModule.getName());

                long moduleId = -1L;
                if ("ticketstatus".equals(lookupModule.getName())) {
                    moduleId = parentModule.getModuleId();
                }

                Optional<FacilioField> mainField = lookupModuleFields.stream().filter(i -> i.getMainField() == null || i.getMainField()).findFirst();
                if (!mainField.isPresent()) {
                    continue;
                }

                List<Map<String, Object>> values = getValues(lookupModule, mainField.get(), moduleId);
                result.put(lookupModule.getDisplayName(), values);
            } else if (dataTypeEnum == FieldType.ENUM) {
                EnumField enumField = (EnumField) field;
                List<EnumFieldValue> enumValues = enumField.getValues();
                List<Map<String, Object>> values = FieldUtil.getAsMapList(enumValues, EnumFieldValue.class);
                result.put(enumField.getDisplayName(), values);
            } else if (dataTypeEnum == FieldType.MULTI_ENUM) {
                MultiEnumField multiEnumField = (MultiEnumField) field;
                List<EnumFieldValue> multiEnumFieldValues = multiEnumField.getValues();
                List<Map<String, Object>> values = FieldUtil.getAsMapList(multiEnumFieldValues, EnumFieldValue.class);
                result.put(multiEnumField.getDisplayName(), values);
            } else if (dataTypeEnum == FieldType.MULTI_LOOKUP) {
                MultiLookupField multiLookupField = (MultiLookupField) field;
                long lookupModuleId = multiLookupField.getLookupModuleId();
                FacilioModule lookupModule = modBean.getModule(lookupModuleId);
                if (lookupModule == null || lookupModule.getTypeEnum() != FacilioModule.ModuleType.PICK_LIST) {
                    continue;
                }
                List<FacilioField> lookupModuleFields = modBean.getAllFields(lookupModule.getName());

                long moduleId = -1L;
                if ("ticketstatus".equals(lookupModule.getName())) {
                    moduleId = parentModule.getModuleId();
                }

                Optional<FacilioField> mainField = lookupModuleFields.stream().filter(i -> i.getMainField() == null || i.getMainField()).findFirst();
                if (!mainField.isPresent()) {
                    continue;
                }

                List<Map<String, Object>> values = getValues(lookupModule, mainField.get(), moduleId);
                result.put(lookupModule.getDisplayName(), values);
            } else if (dataTypeEnum == FieldType.SYSTEM_ENUM) {
                SystemEnumField systemEnumField = (SystemEnumField) field;
                List<EnumFieldValue> systemEnumFieldValues = systemEnumField.getValues();
                List<Map<String, Object>> values = FieldUtil.getAsMapList(systemEnumFieldValues, EnumFieldValue.class);
                result.put(systemEnumField.getDisplayName(), values);
            }
        }

        context.put(FacilioConstants.ContextNames.RESULT, result);
        return false;
    }

    private List<Map<String, Object>> getValues(FacilioModule module, FacilioField field, long moduleId) throws Exception {
        FacilioField idField = FieldFactory.getIdField(module);
        SelectRecordsBuilder selectRecordsBuilder = new SelectRecordsBuilder()
                .select(Arrays.asList(field, idField))
                .module(module);

        if (moduleId != -1L) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField parentModuleId = modBean.getField("parentModuleId", "ticketstatus");
            selectRecordsBuilder.andCondition(CriteriaAPI.getCondition(parentModuleId, moduleId+"", NumberOperators.EQUALS));
        }

        List<Map<String, Object>> asProps = selectRecordsBuilder.getAsProps();

        List<Map<String, Object>> result = new ArrayList<>();
        String mainFieldName = field.getName();
        for (Map<String, Object> prop: asProps) {
            Object value = prop.get(mainFieldName);
            long id = (long) prop.get("id");
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("value", value);
            result.add(map);
        }
        return result;
    }
}
