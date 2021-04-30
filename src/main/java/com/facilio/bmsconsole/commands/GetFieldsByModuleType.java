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
        Map<String, Map<String, Object>> result = new HashMap<>();

        for (FacilioField field: allFields) {
            switch (field.getDataTypeEnum()) {
                case LOOKUP: {
                    if ((field.getName().equals("status") || field.getName().equals("approvalStatus")) && moduleName.equals("workorder")) {
                        continue;
                    }
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

                    Optional<FacilioField> mainField = lookupModuleFields.stream().filter(i -> i.getMainField() != null && i.getMainField()).findFirst();
                    if (!mainField.isPresent()) {
                        continue;
                    }
                    Map<String, Object> modMap = new HashMap<>();
                    List<Map<String, Object>> values = getValues(lookupModule, mainField.get(), moduleId);
                    modMap.put("displayName", lookupModule.getDisplayName());
                    modMap.put("options", values);
                    result.put(field.getName(), modMap);
                } break;
                case ENUM: {
                    EnumField enumField = (EnumField) field;
                    List<EnumFieldValue<Integer>> enumValues = enumField.getValues();
                    List<Map<String, Object>> values = FieldUtil.getAsMapList(enumValues, EnumFieldValue.class);
                    List<Map<String, Object>> options = new ArrayList<>();
                    for (Map<String, Object> value : values) {
                        Map<String, Object> option = new HashMap<>();
                        option.put("id", value.get("index"));
                        option.put("value", value.get("value"));
                        options.add(option);
                    }
                    Map<String, Object> modMap = new HashMap<>();
                    modMap.put("displayName", enumField.getDisplayName());
                    modMap.put("options", options);
                    result.put(field.getName(), modMap);
                } break;
                case MULTI_ENUM: {
                    MultiEnumField multiEnumField = (MultiEnumField) field;
                    List<EnumFieldValue<Integer>> multiEnumFieldValues = multiEnumField.getValues();
                    List<Map<String, Object>> values = FieldUtil.getAsMapList(multiEnumFieldValues, EnumFieldValue.class);
                    List<Map<String, Object>> options = new ArrayList<>();
                    for (Map<String, Object> value : values) {
                        Map<String, Object> option = new HashMap<>();
                        option.put("id", value.get("index"));
                        option.put("value", value.get("value"));
                        options.add(option);
                    }
                    Map<String, Object> modMap = new HashMap<>();
                    modMap.put("displayName", multiEnumField.getDisplayName());
                    modMap.put("options", options);
                    result.put(field.getName(), modMap);
                } break;
                case SYSTEM_ENUM: {
                    SystemEnumField systemEnumField = (SystemEnumField) field;
                    List<EnumFieldValue<Integer>> systemEnumFieldValues = systemEnumField.getValues();
                    List<Map<String, Object>> values = FieldUtil.getAsMapList(systemEnumFieldValues, EnumFieldValue.class);
                    List<Map<String, Object>> options = new ArrayList<>();
                    for (Map<String, Object> value : values) {
                        Map<String, Object> option = new HashMap<>();
                        option.put("id", value.get("index"));
                        option.put("value", value.get("value"));
                        options.add(option);
                    }
                    Map<String, Object> modMap = new HashMap<>();
                    modMap.put("displayName", systemEnumField.getDisplayName());
                    modMap.put("options", options);
                    result.put(field.getName(), modMap);
                } break;
                case STRING_SYSTEM_ENUM: {
                    StringSystemEnumField systemEnumField = (StringSystemEnumField) field;
                    List<EnumFieldValue<String>> systemEnumFieldValues = systemEnumField.getValues();
                    List<Map<String, Object>> values = FieldUtil.getAsMapList(systemEnumFieldValues, EnumFieldValue.class);
                    List<Map<String, Object>> options = new ArrayList<>();
                    for (Map<String, Object> value : values) {
                        Map<String, Object> option = new HashMap<>();
                        option.put("id", value.get("index"));
                        option.put("value", value.get("value"));
                        options.add(option);
                    }
                    Map<String, Object> modMap = new HashMap<>();
                    modMap.put("displayName", systemEnumField.getDisplayName());
                    modMap.put("options", options);
                    result.put(field.getName(), modMap);
                }
                case MULTI_LOOKUP: {
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

                    Optional<FacilioField> mainField = lookupModuleFields.stream().filter(i -> i.getMainField() != null && i.getMainField()).findFirst();
                    if (!mainField.isPresent()) {
                        continue;
                    }
                    Map<String, Object> modMap = new HashMap<>();
                    List<Map<String, Object>> values = getValues(lookupModule, mainField.get(), moduleId);
                    modMap.put("displayName", lookupModule.getDisplayName());
                    modMap.put("options", values);
                    result.put(field.getName(), modMap);
                } break;
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
