package com.facilio.bmsconsole.modulemapping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.ChainUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class ModuleMappingConfigUtil {

    public void getTargetModuleRecords(Context context) throws Exception {

        long recordId = (long) context.get(FacilioConstants.ContextNames.ModuleMapping.RECORD_ID);
        long templateId = (long) context.get(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_ID);
        String templateName = (String) context.get(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_NAME);
        String sourceModuleName = (String) context.get(FacilioConstants.ContextNames.ModuleMapping.SOURCE_MODULE);
        String targetModuleName = (String) context.get(FacilioConstants.ContextNames.ModuleMapping.TARGET_MODULE);
        JSONObject rawRecord = (JSONObject) context.get(FacilioConstants.ContextNames.ModuleMapping.RAW_RECORD);

        V3Context record;

        if (templateId > 0) {
            record = getRecord(recordId, templateId, true, rawRecord);
        } else {
            ModuleMappingValidationUtil.moduleValidation(sourceModuleName, targetModuleName);
            Map<String, Object> sourceTargetMap = getParentId(sourceModuleName, targetModuleName);

            Map<String, Object> templateMap = getTemplateMap(FacilioUtil.parseLong(sourceTargetMap.get("id")), templateName);

            record = getRecord(recordId, FacilioUtil.parseLong(templateMap.get("id")), true, sourceTargetMap, rawRecord);
        }

        context.put(FacilioConstants.ContextNames.ModuleMapping.DATA, record);

    }

    private static V3Context getTargetObjectFromSourceObject(long recordId, long templateId, String sourceModuleName, String targetModuleName, boolean viewOnly, JSONObject rawRecord) throws Exception {

        List<Map<String, Object>> fieldMappingList = getFieldMapFromModuleMappingTemplate(templateId, false);

        return constructFieldMapping(fieldMappingList, recordId, sourceModuleName, targetModuleName, viewOnly, templateId, rawRecord);


    }

    private static V3Context constructFieldMapping(List<Map<String, Object>> fieldMappingList, long recordId, String sourceModuleName, String targetModuleName, boolean viewOnly, long templateId, JSONObject rawRecord) throws Exception {

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule targetModule = moduleBean.getModule(targetModuleName);

        Object record = V3Util.getRecord(sourceModuleName, recordId, null);

        Map<String, Object> sourceRecordMap = FieldUtil.getAsProperties(record);

        JSONObject targetRecordObj = new JSONObject();


        if (CollectionUtils.isNotEmpty(fieldMappingList)) {

            constructTargetFieldMapBasedOnSourceFieldMap(fieldMappingList, targetRecordObj, sourceRecordMap, sourceModuleName, targetModuleName);

            constructSubModuleFieldMapBasedOnSourceFieldMap(templateId, sourceRecordMap, targetRecordObj);

            V3Config targetModuleConfig = ChainUtil.getV3Config(targetModuleName);

            Class targetClassBeanName = ChainUtil.getBeanClass(targetModuleConfig, targetModule);

            if (rawRecord!=null && !rawRecord.isEmpty()) {
                List<String> keys = new ArrayList<>(rawRecord.keySet());
                for (String key : keys) {
                    targetRecordObj.put(key, rawRecord.get(key));
                }
            }

            V3Context targetRecord = (V3Context) FieldUtil.getAsBeanFromJson(targetRecordObj, targetClassBeanName);

            if (!viewOnly) {
                FacilioContext createdRecord = V3Util.createRecord(targetModule, targetRecordObj);
                Map<String, List> recordMap = CommonCommandUtil.getRecordMap(createdRecord);

                if (MapUtils.isNotEmpty(recordMap)) {
                    List<ModuleBaseWithCustomFields> records = recordMap.get(targetModuleName);

                    if (CollectionUtils.isNotEmpty(records)) {
                        return (V3Context) records.get(0);
                    }
                }
            }

            return targetRecord;
        }
        return null;
    }

    private static void constructSubModuleFieldMapBasedOnSourceFieldMap(long templateId, Map<String, Object> sourceRecordMap, JSONObject targetRecordObj) throws Exception {

        List<Map<String, Object>> subModuleMappingList = getSubModuleList(templateId);

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (CollectionUtils.isNotEmpty(subModuleMappingList)) {

            for (Map<String, Object> subModuleMap : subModuleMappingList) {

                long subModuleMappingId = FacilioUtil.parseLong(subModuleMap.get("id"));

                long sourceSubModuleId = FacilioUtil.parseLong(subModuleMap.get("sourceModuleId"));
                long targetSubModuleId = FacilioUtil.parseLong(subModuleMap.get("targetModuleId"));

                String sourceContextName = String.valueOf(subModuleMap.get("sourceContextName"));
                String targetContextName = String.valueOf(subModuleMap.get("targetContextName"));

                Object sourceSubModuleRecordObject = sourceRecordMap.get(sourceContextName);

                List<Map<String, Object>> sourceRecordLineItems = (List<Map<String, Object>>) sourceSubModuleRecordObject;

                FacilioModule sourceModule = moduleBean.getModule(sourceSubModuleId);

                FacilioModule targetModule = moduleBean.getModule(targetSubModuleId);

                List<Map<String, Object>> targetRecordLineItems = new ArrayList<>();

                if (CollectionUtils.isNotEmpty(sourceRecordLineItems)) {

                    for (Map<String, Object> lineItem : sourceRecordLineItems) {

                        JSONObject targetSubModuleRecordObj = new JSONObject();

                        Map<String, Object> sourceSubModuleRecordMap = FieldUtil.getAsProperties(lineItem);

                        List<Map<String, Object>> subModuleFieldMappingList = getFieldMapFromModuleMappingTemplate(subModuleMappingId, true);

                        constructTargetFieldMapBasedOnSourceFieldMap(subModuleFieldMappingList, targetSubModuleRecordObj, sourceSubModuleRecordMap, sourceModule.getName(), targetModule.getName());

                        Map<String, Object> recordObj = targetSubModuleRecordObj;

                        targetRecordLineItems.add(recordObj);


                    }
                }

                targetRecordObj.put(targetContextName, targetRecordLineItems);


            }

        }

    }


    private static List<Map<String, Object>> getSubModuleList(long templateId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSubModuleMappingModule().getTableName())
                .select(FieldFactory.getSubModuleMappingFields())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(templateId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();

        return props;

    }

    private static void constructTargetFieldMapBasedOnSourceFieldMap(List<Map<String, Object>> fieldMappingList, JSONObject targetRecordObj, Map<String, Object> sourceRecordMap, String sourceModuleName, String targetModuleName) throws Exception {

        for (Map<String, Object> fieldMap : fieldMappingList) {

            String targetFieldName = String.valueOf(fieldMap.get("targetField"));

            String sourceFieldName = String.valueOf(fieldMap.get("sourceField"));

            Long id = FacilioUtil.parseLong(fieldMap.get("id"));


            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            FacilioModule sourceModule = moduleBean.getModule(sourceModuleName);

            FacilioModule targetModule = moduleBean.getModule(targetModuleName);

            FacilioField sourceField = null;

            FacilioField targetField = null;

            Set<String> siteIdAllowedModules = FieldUtil.getSiteIdAllowedModules();

            if (fieldMap.get("sourceFieldId") != null) {
                long sourceFieldId = FacilioUtil.parseLong(fieldMap.get("sourceFieldId"));
                sourceField = moduleBean.getField(sourceFieldId, sourceModule.getModuleId());
            } else {
                if (sourceFieldName.equals("siteId") && siteIdAllowedModules.contains(sourceModule.getName())) {
                    FacilioField siteIdField = FieldFactory.getSiteIdField(sourceModule);
                    sourceField = siteIdField;
                } else if (fieldMap.get("sourceField").equals("id")) {
                    FacilioField idField = FieldFactory.getIdField(sourceModule);
                    sourceField = idField;
                } else if (StringUtils.isNotEmpty(sourceFieldName)) {
                    FacilioField field = moduleBean.getField(sourceFieldName, sourceModuleName);
                    sourceField = field;
                }
            }

            if (fieldMap.get("targetFieldId") != null) {
                long targetFieldId = FacilioUtil.parseLong(fieldMap.get("targetFieldId"));
                targetField = moduleBean.getField(targetFieldId, targetModule.getModuleId());
            } else {
                if (targetFieldName.equals("siteId") && siteIdAllowedModules.contains(targetModule.getName())) {
                    FacilioField siteIdField = FieldFactory.getSiteIdField(targetModule);
                    targetField = siteIdField;
                } else if (fieldMap.get("targetField").equals("id")) {
                    FacilioField idField = FieldFactory.getIdField(targetModule);
                    targetField = idField;
                } else if (StringUtils.isNotEmpty(targetFieldName)) {
                    FacilioField field = moduleBean.getField(targetFieldName, sourceModuleName);
                    targetField = field;
                }
            }


            Object sourceValue = null;

            if (sourceField != null) {

                if (sourceField.getDataTypeEnum() == FieldType.FILE && sourceRecordMap.containsKey(sourceField.getName() + "Id")) {
                    sourceValue = sourceRecordMap.get(sourceField.getName() + "Id");
                } else {
                    sourceValue = sourceRecordMap.get(sourceField.getName());
                }
            }

            if (sourceValue != null) {

                String key = null;

                if (targetField.getDataTypeEnum() == FieldType.FILE) {
                    key = targetField.getName() + "Id";
                } else {
                    key = targetField.getName();
                }

                targetRecordObj.put(key, constructValueBasedOnFieldType(sourceField, targetField, sourceRecordMap, id));
            }

        }

    }

    private static Object constructValueBasedOnFieldType(FacilioField sourceField, FacilioField targetField, Map<String, Object> sourceRecordMap, Long id) throws Exception {

        FieldType sourceFieldType = sourceField.getDataTypeEnum();

        FieldType targetFieldType = targetField.getDataTypeEnum();


        Object sourceValue = null;

        if (sourceField.getDataTypeEnum() == FieldType.FILE && sourceRecordMap.containsKey(sourceField.getName() + "Id")) {
            sourceValue = sourceRecordMap.get(sourceField.getName() + "Id");
        } else {
            sourceValue = sourceRecordMap.get(sourceField.getName());
        }

        FieldMappingConfigurationUtil fieldMappingConfiguration = new FieldMappingConfigurationUtil(sourceField, targetField, sourceValue, sourceFieldType, targetFieldType, id);

        Object targetValue = fieldMappingConfiguration.getTargetValue();


        return targetValue;

    }

    private static List<Map<String, Object>> getFieldMapFromModuleMappingTemplate(long templateId, boolean isSubModule) throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getFieldMappingModule().getTableName())
                .select(FieldFactory.getFieldMappingFields());

        if (!isSubModule) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(templateId), NumberOperators.EQUALS));
        } else {
            selectBuilder.andCondition(CriteriaAPI.getCondition("SUB_MODULE_MAPPING_ID", "subModuleMappingId", String.valueOf(templateId), NumberOperators.EQUALS));
        }

        List<Map<String, Object>> props = selectBuilder.get();

        if (props == null) {
            throw new IllegalStateException("Module mapping configuration not found.");
        }


        List<Map<String, Object>> fieldMappingList = new ArrayList<>();

        for (Map<String, Object> prop : props) {

            Map<String, Object> fieldMapping = new HashMap<>();

            String sourceField = String.valueOf(prop.get("sourceField"));
            String targetField = String.valueOf(prop.get("targetField"));

            long id = FacilioUtil.parseLong(prop.get("id"));

            if (prop.get("sourceFieldId") != null) {
                long sourceFieldId = FacilioUtil.parseLong(prop.get("sourceFieldId"));
                fieldMapping.put("sourceFieldId", sourceFieldId);
            }

            if (prop.get("targetFieldId") != null) {
                long targetFieldId = FacilioUtil.parseLong(prop.get("targetFieldId"));
                fieldMapping.put("targetFieldId", targetFieldId);
            }

            fieldMapping.put("sourceField", sourceField);
            fieldMapping.put("targetField", targetField);
            fieldMapping.put("id", id);

            fieldMappingList.add(fieldMapping);
        }

        return fieldMappingList;
    }

    private static Map<String, Object> getSourceAndTargetModuleMap(long templateId) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = ModuleFactory.getModuleMappingTemplateModule();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getModuleMappingTemplateFields())
                .andCondition(CriteriaAPI.getIdCondition(templateId, module));


        Map<String, Object> prop = selectBuilder.fetchFirst();

        Map<String, Object> sourceAndTargetModuleMap = new HashMap<>();

        if (prop != null) {
            long parentId = FacilioUtil.parseLong(prop.get("parentId"));

            sourceAndTargetModuleMap = getModuleMapping(parentId);

        }

        return sourceAndTargetModuleMap;

    }


    private static Map<String, Object> getModuleMapping(long parentId) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = ModuleFactory.getModuleMappingModule();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getModuleMappingFields())
                .andCondition(CriteriaAPI.getIdCondition(parentId, module));


        Map<String, Object> prop = selectBuilder.fetchFirst();

        return prop;

    }

    public void createTargetModuleRecord(Context context) throws Exception {

        long recordId = (long) context.get(FacilioConstants.ContextNames.ModuleMapping.RECORD_ID);
        long templateId = (long) context.get(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_ID);
        String templateName = (String) context.get(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_NAME);
        String sourceModuleName = (String) context.get(FacilioConstants.ContextNames.ModuleMapping.SOURCE_MODULE);
        String targetModuleName = (String) context.get(FacilioConstants.ContextNames.ModuleMapping.TARGET_MODULE);
        JSONObject rawRecord = (JSONObject) context.get(FacilioConstants.ContextNames.ModuleMapping.RAW_RECORD);

        V3Context record;

        if (templateId > 0) {
            record = getRecord(recordId, templateId, false, rawRecord);
        } else {
            ModuleMappingValidationUtil.moduleValidation(sourceModuleName, targetModuleName);
            Map<String, Object> sourceTargetMap = getParentId(sourceModuleName, targetModuleName);
            Map<String, Object> templateMap = getTemplateMap(FacilioUtil.parseLong(sourceTargetMap.get("id")), templateName);

            record = getRecord(recordId, FacilioUtil.parseLong(templateMap.get("id")), false, sourceTargetMap, rawRecord);
        }

        context.put(FacilioConstants.ContextNames.ModuleMapping.DATA, record);

    }

    private static V3Context createTargetModuleRecordFromSourceModuleRecord(long recordId, long templateId, String
            sourceModuleName, String targetModuleName) throws Exception {

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule sourceModule = moduleBean.getModule(sourceModuleName);

        FacilioModule targetModule = moduleBean.getModule(targetModuleName);

        Object record = V3Util.getRecord(sourceModuleName, recordId, null);

        Map<String, Object> sourceRecordMap = FieldUtil.getAsProperties(record);

        JSONObject targetRecordObj = new JSONObject();

        Map<String, String> fieldMap = new HashMap<>();

        fieldMap.put("subject", "subject");
        fieldMap.put("description", "description");

        for (String sourceKey : fieldMap.keySet()) {
            targetRecordObj.put(fieldMap.get(sourceKey), sourceRecordMap.get(sourceKey));
        }


        V3Config targetModuleConfig = ChainUtil.getV3Config(targetModuleName);

        Class targetClassBeanName = ChainUtil.getBeanClass(targetModuleConfig, targetModule);


        V3Context targetRecord = (V3Context) FieldUtil.getAsBeanFromJson(targetRecordObj, targetClassBeanName);

        V3Util.createRecord(targetModule, targetRecordObj);

        return targetRecord;

    }

    public V3Context getRecord(long recordId, long templateId, boolean viewOnly, JSONObject rawRecord) throws Exception {
        return getRecord(recordId, templateId, viewOnly, null, rawRecord);
    }

    public V3Context getRecord(long recordId, long templateId, boolean viewOnly, Map<String, Object> sourceTargetMap, JSONObject rawRecord) throws Exception {
        Map<String, Object> prop = null;

        if (templateId > 0) {
            prop = getSourceAndTargetModuleMap(templateId);
        } else if (MapUtils.isNotEmpty(sourceTargetMap)) {
            prop = sourceTargetMap;
        }

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (MapUtils.isNotEmpty(prop)) {

            long sourceModuleId = FacilioUtil.parseLong(prop.get("sourceModuleId"));
            long targetModuleId = FacilioUtil.parseLong(prop.get("targetModuleId"));

            if (templateId < 1) {
                templateId = FacilioUtil.parseLong(prop.get("id"));
            }

            FacilioModule sourceModule = moduleBean.getModule(sourceModuleId);

            FacilioModule targetModule = moduleBean.getModule(targetModuleId);

            V3Context record = getTargetObjectFromSourceObject(recordId, templateId, sourceModule.getName(), targetModule.getName(), viewOnly, rawRecord);

            return record;

        }
        return null;
    }


    public Object getTargetModuleRecordObj(String sourceModule, String targetModule, long recordId, long templateId, String templateName, JSONObject rawRecord) throws Exception {

        if (templateId > 0) {
            return getRecord(recordId, templateId, true, rawRecord);
        } else {
            ModuleMappingValidationUtil.moduleValidation(sourceModule, targetModule);
            Map<String, Object> sourceTargetMap = getParentId(sourceModule, targetModule);

            Map<String, Object> templateMap = getTemplateMap(FacilioUtil.parseLong(sourceTargetMap.get("id")), templateName);

            return getRecord(recordId, FacilioUtil.parseLong(templateMap.get("id")), true, sourceTargetMap, rawRecord);
        }
    }

    private Map<String, Object> getParentId(String sourceModuleName, String targetModuleName) throws Exception {
        FacilioModule module = ModuleFactory.getModuleMappingModule();

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule sourceModule = moduleBean.getModule(sourceModuleName);

        FacilioModule targetModule = moduleBean.getModule(targetModuleName);


        List<FacilioField> fields = FieldFactory.getModuleMappingFields();

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceModuleId"), String.valueOf(sourceModule.getModuleId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("targetModuleId"), String.valueOf(targetModule.getModuleId()), NumberOperators.EQUALS));

        Map<String, Object> prop = selectBuilder.fetchFirst();


        return prop;

    }

    private static Map<String, Object> getTemplateMap(long parentId, String templateName) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getModuleMappingTemplateModule().getTableName())
                .select(FieldFactory.getModuleMappingTemplateFields())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(parentId), NumberOperators.EQUALS));

        if (StringUtils.isNotEmpty(templateName)) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", templateName, StringOperators.IS));
        } else {
            selectBuilder.andCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", String.valueOf(true), BooleanOperators.IS));
        }

        Map<String, Object> props = selectBuilder.fetchFirst();

        return props;
    }

    public V3Context createTargetModuleRecord(String sourceModule, String targetModule, long sourceRecordId, long templateId, String templateName, JSONObject rawRecord) throws Exception {

        if (templateId > 0) {
            return getRecord(sourceRecordId, templateId, false, rawRecord);
        } else {
            ModuleMappingValidationUtil.moduleValidation(sourceModule, targetModule);
            Map<String, Object> sourceTargetMap = getParentId(sourceModule, targetModule);
            Map<String, Object> templateMap = getTemplateMap(FacilioUtil.parseLong(sourceTargetMap.get("id")), templateName);

            return getRecord(sourceRecordId, FacilioUtil.parseLong(templateMap.get("id")), false, sourceTargetMap, rawRecord);
        }

    }

}
