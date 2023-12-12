package com.facilio.bmsconsoleV3.signup.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldMappingContext;
import com.facilio.bmsconsole.context.ModuleMappingContext;
import com.facilio.bmsconsole.context.ModuleMappings;
import com.facilio.bmsconsole.context.SubModuleMappingContext;
import com.facilio.bmsconsole.modulemapping.FieldMappingConfigurationUtil;
import com.facilio.bmsconsole.modulemapping.ModuleMappingConfig;
import com.facilio.bmsconsole.modulemapping.ModuleMappingValidationUtil;
import com.facilio.bmsconsole.modulemapping.constants.FieldTypeConstants;
import com.facilio.bmsconsoleV3.context.SubFieldMappingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.context.Constants;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

@Log4j
public class ModuleMappingsUtil {

    public static final Map<String, Supplier<ModuleMappings>> MODULE_MAPPING_HANDLER_MAP = initModuleMappings();

    @SneakyThrows
    private static Map<String, Supplier<ModuleMappings>> initModuleMappings() {

        Reflections reflections = new Reflections(ClasspathHelper.forPackage("com\\.facilio\\.bmsconsole\\.modulemapping"), new MethodAnnotationsScanner());

        Set<Method> methodsAnnotatedWithModule = reflections.getMethodsAnnotatedWith(ModuleMappingConfig.class);

        Map<String, Supplier<ModuleMappings>> moduleMappingsMap = new HashMap<>();

        for (Method method : methodsAnnotatedWithModule) {
            validateHandlerMethod(method);

            ModuleMappingConfig moduleMappingConfigAnnotation = method.getAnnotation(ModuleMappingConfig.class);
            String moduleName = moduleMappingConfigAnnotation.value();

            if (!StringUtils.isNotEmpty(moduleName)) {
                throw new IllegalStateException("Module name cannot be empty");
            }

            Supplier<ModuleMappings> moduleMappings = (Supplier<ModuleMappings>) method.invoke(null, null);

            if (moduleMappingsMap.containsKey(moduleName)) {
                throw new IllegalStateException("Module mapping already present");
            }

            moduleMappingsMap.put(moduleName, moduleMappings);

        }
        return Collections.unmodifiableMap(moduleMappingsMap);
    }

    private static void validateHandlerMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        boolean isPresent = declaringClass.isAnnotationPresent(Config.class);
        if (!isPresent) {
            throw new IllegalStateException("Module annotation should be part of " + declaringClass.getName() + " Config class.");
        }

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (!(genericParameterTypes == null || genericParameterTypes.length == 0)) {
            throw new IllegalStateException("Method should not have parameters");
        }

        Class<?> returnType = method.getReturnType();

        if (!returnType.equals(Supplier.class)) {
            throw new IllegalStateException("Return type should be Supplier<ModuleMappings>.");
        }
    }

    public static ModuleMappings fetchModuleMappings(String moduleName) {
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "Invalid module name");

        ModuleMappings moduleMappings;
        if (MapUtils.isNotEmpty(MODULE_MAPPING_HANDLER_MAP) && MODULE_MAPPING_HANDLER_MAP.containsKey(moduleName)) {
            Supplier<ModuleMappings> supplierModulemappings = MODULE_MAPPING_HANDLER_MAP.get(moduleName);
            if (supplierModulemappings != null) {
                moduleMappings = supplierModulemappings.get();
                moduleMappings.setSourceModuleName(moduleName);

                if (moduleMappings != null) {
                    return moduleMappings;
                }
            }

        }
        return null;
    }

    public static void addModuleMapping(ModuleMappings mappings) throws Exception {


        if (mappings.getMappingList() == null) {
            throw new IllegalArgumentException("Field mapping list cannot be null for module: " + mappings.getSourceModuleName());
        }
        List<ModuleMappingContext> mappingList = mappings.getMappingList();
        ModuleMappingValidationUtil.validateMappings(mappingList, mappings);

        insertModuleMapping(mappings);


    }

    public static void addSubModuleMapping(ModuleMappings mappings) throws Exception {

        if (mappings.getMappingList() == null) {
            throw new IllegalArgumentException("Field mapping list cannot be null for module: " + mappings.getSourceModuleName());
        }
        List<ModuleMappingContext> mappingList = mappings.getMappingList();
        ModuleMappingValidationUtil.validateMappings(mappingList, mappings);

        insertModuleMapping(mappings);


    }

    private static Map<Long, Long> getSubFieldMapFromFieldMapping(long fieldMappingId) throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSubFieldMappingModule().getTableName())
                .select(FieldFactory.getFieldMappingFields())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(fieldMappingId), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();

        if (props == null) {
            throw new IllegalStateException("Module mapping configuration not found.");
        }


        Map<Long, Long> fieldMapping = new HashMap<>();

        for (Map<String, Object> prop : props) {
            long sourceFieldId = FacilioUtil.parseLong(prop.get("sourceFieldId"));
            long targetFieldId = FacilioUtil.parseLong(prop.get("targetFieldId"));

            fieldMapping.put(sourceFieldId, targetFieldId);

        }

        return fieldMapping;
    }

    private static void addModuleMappingTemplate(ModuleMappingContext mapping) throws Exception {

        Set<String> siteIdAllowedModules = FieldUtil.getSiteIdAllowedModules();

        setUniqueNameForModuleMappingTemplate(mapping);

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        for (FieldMappingContext fieldMapping : mapping.getFieldMappingList()) {

            FacilioField sourceField = moduleBean.getField(fieldMapping.getSourceField(), mapping.getSourceModule());

            FacilioField targetField = moduleBean.getField(fieldMapping.getTargetField(), mapping.getTargetModule());

            boolean isSourceFieldIsSite = fieldMapping.getSourceField().equals("siteId") && siteIdAllowedModules.contains(mapping.getSourceModule());

            boolean isTargetFieldIsSite = fieldMapping.getTargetField().equals("siteId") && siteIdAllowedModules.contains(mapping.getTargetModule());

            if (!isSourceFieldIsSite) {
                fieldMapping.setSourceFieldId(sourceField.getFieldId());
            }

            if (!isTargetFieldIsSite) {
                fieldMapping.setTargetFieldId(targetField.getFieldId());
            }

            fieldMapping.setSysCreatedTime(System.currentTimeMillis());

            fieldMapping.setSysCreatedTime(System.currentTimeMillis());

            fieldMapping.setSysModifiedTime(System.currentTimeMillis());

        }

//        checkAlreadyMappingConfigExists(mapping);

        long templateId = insertModuleMappingTemplate(mapping);

        addModuleMappingConfig(templateId, mapping);

        List<SubModuleMappingContext> subModuleMappingContextList = mapping.getSubModuleMappingList();

        if (CollectionUtils.isNotEmpty(subModuleMappingContextList)) {

            for (SubModuleMappingContext subModuleMappingContext : subModuleMappingContextList) {
                subModuleMappingContext.setParentId(templateId);

                FacilioModule sourceSubModule = moduleBean.getModule(subModuleMappingContext.getSourceModuleName());

                FacilioModule targetSubModule = moduleBean.getModule(subModuleMappingContext.getTargetModuleName());

                subModuleMappingContext.setSourceModuleId(sourceSubModule.getModuleId());
                subModuleMappingContext.setTargetModuleId(targetSubModule.getModuleId());

                subModuleMappingContext.setSourceModuleId(sourceSubModule.getModuleId());
                subModuleMappingContext.setTargetModuleId(targetSubModule.getModuleId());

                long subModuleMappingId = insertSubModuleMapping(subModuleMappingContext);

                addSubModuleMappingConfig(subModuleMappingId, subModuleMappingContext);

            }

        }


    }

    private static long insertSubModuleMapping(SubModuleMappingContext subModuleMappingContext) throws Exception {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getSubModuleMappingModule().getTableName())
                .fields(FieldFactory.getSubModuleMappingFields());

        Map<String, Object> props = FieldUtil.getAsProperties(subModuleMappingContext);

        props.put("sysCreatedTime", System.currentTimeMillis());
        props.put("sysModifiedTime", System.currentTimeMillis());
        if (AccountUtil.getCurrentUser() != null) {
            props.put("sysCreatedByPeople", AccountUtil.getCurrentUser().getPeopleId());
            props.put("sysModifiedByPeople", AccountUtil.getCurrentUser().getPeopleId());
        }

        insertBuilder.addRecord(props);

        insertBuilder.save();

        return FacilioUtil.parseLong(props.get("id"));
    }

    private static void checkAlreadyMappingConfigExists(ModuleMappingContext mapping) throws Exception {

        Map<Object, Object> targetAndSourceFieldMap = new HashMap<>();

        for (FieldMappingContext fieldMapping : mapping.getFieldMappingList()) {
            Object sourceField = null;
            Object targetField = null;

            if (fieldMapping.getSourceFieldId() < 1) {
                sourceField = fieldMapping.getSourceField();
            } else {
                sourceField = fieldMapping.getSourceFieldId();
            }

            if (fieldMapping.getTargetFieldId() < 1) {
                targetField = fieldMapping.getTargetField();
            } else {
                targetField = fieldMapping.getTargetFieldId();
            }

            targetAndSourceFieldMap.put(targetField, sourceField);
        }

        Map<Object, Object> targetAndSourceFieldMapInDB = new HashMap<>();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getModuleMappingTemplateModule().getTableName())
                .select(FieldFactory.getModuleMappingTemplateFields())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(mapping.getParentId()), NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();

        if (props != null) {
            for (Map<String, Object> prop : props) {
                long templateId = FacilioUtil.parseLong(prop.get("id"));

                GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                        .table(ModuleFactory.getFieldMappingModule().getTableName())
                        .select(FieldFactory.getFieldMappingFields())
                        .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(templateId), NumberOperators.EQUALS));

                List<Map<String, Object>> moduleMappingTemplateList = select.get();

                for (Map<String, Object> moduleMappingTemplate : moduleMappingTemplateList) {

                    Object sourceField = null;
                    Object targetField = null;

                    if (moduleMappingTemplate.get("targetFieldId") == null) {
                        targetField = moduleMappingTemplate.get("targetField");
                    } else {
                        targetField = FacilioUtil.parseLong(moduleMappingTemplate.get("targetFieldId"));
                    }

                    if (moduleMappingTemplate.get("sourceFieldId") == null) {
                        sourceField = moduleMappingTemplate.get("sourceField");
                    } else {
                        sourceField = FacilioUtil.parseLong(moduleMappingTemplate.get("sourceFieldId"));
                    }

                    targetAndSourceFieldMapInDB.put(targetField, sourceField);
                }

            }

        }

        if ((targetAndSourceFieldMap != null && targetAndSourceFieldMapInDB != null) && (targetAndSourceFieldMap.size() == targetAndSourceFieldMapInDB.size())) {

            boolean areMapsEqual = targetAndSourceFieldMap.equals(targetAndSourceFieldMapInDB);
            if (areMapsEqual) {
                throw new IllegalArgumentException("A mapping already exists for the source module '" + mapping.getSourceModule() + "' and target module '" + mapping.getTargetModule() + "'.");
            }

        }

    }

    private static void setUniqueNameForModuleMappingTemplate(ModuleMappingContext mapping) throws Exception {

        if (mapping.getName() == null || mapping.getName().equals("")) {
            String tempName = mapping.getSourceModule() + "To" + mapping.getTargetModule() + "Template";
            mapping.setName(tempName);
        }

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getModuleMappingTemplateModule().getTableName())
                .select(FieldFactory.getModuleMappingTemplateFields())
                .andCustomWhere("NAME = ? OR NAME LIKE ?", mapping.getName(), mapping.getName() + "\\_%")
                .orderBy("ID desc")
                .limit(1);

        Map<String, Object> prop = selectBuilder.fetchFirst();

        String name = mapping.getName();

        if (name.length() > 300) {
            name = name.substring(0, 300);
        }

        if (prop != null) {

            String moduleMappingName = constructUniqueName(name, prop, true);

            mapping.setName(moduleMappingName);
        }

    }

    private static void addModuleMappingConfig(long templateId, ModuleMappingContext mapping) throws Exception {

        for (FieldMappingContext fieldMapping : mapping.getFieldMappingList()) {
            fieldMapping.setParentId(templateId);

            long id = insertFieldMappingConfig(fieldMapping);

            if (fieldMapping.getSubFieldMappings() != null) {
                addSubFieldMapping(id, fieldMapping, mapping.getSourceModule(), mapping.getTargetModule());
            }
        }
    }

    private static void addSubModuleMappingConfig(long templateId, SubModuleMappingContext mapping) throws Exception {

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        for (FieldMappingContext fieldMapping : mapping.getFieldMappingList()) {

            FacilioField sourceField = moduleBean.getField(fieldMapping.getSourceField(), mapping.getSourceModuleName());

            FacilioField targetField = moduleBean.getField(fieldMapping.getTargetField(), mapping.getTargetModuleName());

            fieldMapping.setSourceFieldId(sourceField.getFieldId());
            fieldMapping.setTargetFieldId(targetField.getFieldId());

            fieldMapping.setSubModuleMappingId(templateId);

            long id = insertFieldMappingConfig(fieldMapping);

            if (fieldMapping.getSubFieldMappings() != null) {
                addSubFieldMapping(id, fieldMapping, mapping.getSourceModuleName(), mapping.getTargetModuleName());
            }
        }
    }

    private static void addSubFieldMapping(long parentId, FieldMappingContext fieldMapping, String sourceModuleName, String targetModuleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<SubFieldMappingContext> subFieldMappingContextList = fieldMapping.getSubFieldMappings();

        if (subFieldMappingContextList != null) {

            for (SubFieldMappingContext subFieldMappingContext : subFieldMappingContextList) {
                subFieldMappingContext.setParentId(parentId);

                FacilioField sourceField = moduleBean.getField(fieldMapping.getSourceField(), sourceModuleName);

                FacilioField targetField = moduleBean.getField(fieldMapping.getTargetField(), targetModuleName);

                FieldType sourceFieldType = sourceField.getDataTypeEnum();

                FieldType targetFieldType = targetField.getDataTypeEnum();

                long sourceSubFieldId = -1L;

                if (sourceFieldType == FieldType.BOOLEAN) {
                    String normalizedValue = subFieldMappingContext.getSourceField().toLowerCase();

                    subFieldMappingContext.setSourceField(normalizedValue);

                } else {
                    sourceSubFieldId = fetchFieldIdForEnumOrPickListField(subFieldMappingContext.getSourceField(), sourceField, sourceFieldType);

                    if (sourceFieldType == FieldType.STRING_SYSTEM_ENUM) {
                        String fieldName = subFieldMappingContext.getSourceField();
                        StringSystemEnumField stringSystemEnumField = (StringSystemEnumField) targetField;
                        List<EnumFieldValue<String>> enumFieldValueList = stringSystemEnumField.getValues();

                        String sourceSubField = enumFieldValueList.stream()
                                .filter(enumFieldValue -> enumFieldValue.getValue().equals(fieldName))
                                .findFirst()
                                .map(EnumFieldValue::getIndex)
                                .orElseThrow(() -> new IllegalArgumentException("Value not found: " + fieldName));

                        subFieldMappingContext.setSourceField(sourceSubField);

                    }

                }

                long targetSubFieldId = -1L;

                if (FieldTypeConstants.LOOKUP_TYPES.contains(targetFieldType) || FieldTypeConstants.ENUM_FIELD_TYPES.contains(targetFieldType)) {

                    List<String> targetFieldNames = subFieldMappingContext.getTargetFieldNames();

                    if (sourceSubFieldId > -1) {
                        subFieldMappingContext.setSourceFieldId(sourceSubFieldId);
                    }

                    for (String fieldName : targetFieldNames) {
                        subFieldMappingContext.setTargetField(fieldName);

                        targetSubFieldId = fetchFieldIdForEnumOrPickListField(fieldName, targetField, targetFieldType);

                        if (targetFieldType == FieldType.STRING_SYSTEM_ENUM) {
                            StringSystemEnumField stringSystemEnumField = (StringSystemEnumField) targetField;
                            List<EnumFieldValue<String>> enumFieldValueList = stringSystemEnumField.getValues();

                            String targetSubField = enumFieldValueList.stream()
                                    .filter(enumFieldValue -> enumFieldValue.getValue().equals(fieldName))
                                    .findFirst()
                                    .map(EnumFieldValue::getIndex)
                                    .orElseThrow(() -> new IllegalArgumentException("Value not found: " + fieldName));

                            subFieldMappingContext.setTargetField(targetSubField);

                        }

                        if (targetSubFieldId > -1) {
                            subFieldMappingContext.setTargetFieldId(targetSubFieldId);
                        }

                        Map<String, Object> props = FieldUtil.getAsProperties(subFieldMappingContext);

                        insertSubFieldMappingConfig(props);
                    }

                } else {
                    targetSubFieldId = targetField.getFieldId();

                    if (targetSubFieldId > -1) {
                        subFieldMappingContext.setTargetFieldId(targetSubFieldId);
                    }

                    if (sourceSubFieldId > -1) {
                        subFieldMappingContext.setSourceFieldId(sourceSubFieldId);
                    }

                    Map<String, Object> props = FieldUtil.getAsProperties(subFieldMappingContext);

                    insertSubFieldMappingConfig(props);
                }
            }
        }
    }


    private static void insertSubFieldMappingConfig(Map<String, Object> props) throws Exception {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getSubFieldMappingModule().getTableName())
                .fields(FieldFactory.getSubFieldMappingFields());

        insertBuilder.addRecord(props);

        insertBuilder.save();

    }

    private static long fetchFieldIdForEnumOrPickListField(String fieldName, FacilioField field, FieldType fieldType) throws Exception {
        if (fieldType.getTypeAsInt() == FieldType.ENUM.getTypeAsInt()) {
            EnumFieldValue enumValue = ModuleMappingValidationUtil.getEnumField(field, fieldName);
            if (enumValue != null) {
                return FacilioUtil.parseLong(enumValue.getIndex());
            }
            throw new IllegalArgumentException(String.format("No Enum Fields found with the name '%s' for field '%s'.", fieldName, field.getName()));
        } else if (field.getDataTypeEnum() == FieldType.LOOKUP) {
            LookupField lookupField = null;

            lookupField = (LookupField) field;

            if (lookupField != null && (lookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue())) {
                return fetchFieldIdForLookupPickListField(fieldName, field, fieldType);
            }

        } else if (fieldType == FieldType.SYSTEM_ENUM) {
            SystemEnumField systemEnumField = (SystemEnumField) field;
            List<EnumFieldValue<Integer>> enumFieldValueList = systemEnumField.getValues();

            return enumFieldValueList.stream()
                    .filter(enumFieldValue -> enumFieldValue.getValue().equals(fieldName))
                    .findFirst()
                    .map(EnumFieldValue::getIndex)
                    .orElseThrow(() -> new IllegalArgumentException("Value not found: " + fieldName));

        } else if (fieldType == FieldType.MULTI_ENUM) {
            MultiEnumField systemEnumField = (MultiEnumField) field;
            List<EnumFieldValue<Integer>> enumFieldValueList = systemEnumField.getValues();

            return enumFieldValueList.stream()
                    .filter(enumFieldValue -> enumFieldValue.getValue().equals(fieldName))
                    .findFirst()
                    .map(EnumFieldValue::getIndex)
                    .orElseThrow(() -> new IllegalArgumentException("Value not found: " + fieldName));
        } else if (fieldType == FieldType.MULTI_LOOKUP) {
            MultiLookupField lookupField = null;

            lookupField = (MultiLookupField) field;

            if (lookupField != null && (lookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue())) {
                return fetchFieldIdForLookupPickListField(fieldName, field, fieldType);
            }

        }
        return -1l;
    }

    public static ModuleBaseWithCustomFields fetchRecordIdForLookup(String recordName, FacilioField field) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioField primaryField = null;

        FacilioModule module = null;

        if (field.getDataTypeEnum() == FieldType.LOOKUP) {

            LookupField fieldLookup = (LookupField) field;

            module = fieldLookup.getLookupModule();

            primaryField = FieldMappingConfigurationUtil.getMainFieldOfLookup(fieldLookup, modBean);
        } else if (field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
            MultiLookupField fieldLookup = (MultiLookupField) field;

            module = fieldLookup.getLookupModule();

            primaryField = FieldMappingConfigurationUtil.getMainFieldOfMultiLookup(fieldLookup, modBean);
        }

        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModuleName(module.getName());

        SelectRecordsBuilder<ModuleBaseWithCustomFields> objectValue = new SelectRecordsBuilder<>()
                .moduleName(module.getName())
                .select(fields)
                .beanClass(moduleClass)
                .andCondition(CriteriaAPI.getCondition(primaryField.getColumnName(), primaryField.getName(), recordName, StringOperators.IS));

        List<ModuleBaseWithCustomFields> records = objectValue.get();
        if (records != null && !records.isEmpty()) {
            return records.get(records.size() - 1);
        }

        return null;
    }

    public static FacilioField getPrimaryFieldForLookupPickListModule(FacilioModule module) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getFieldFields())
                .table(ModuleFactory.getFieldsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("IS_MAIN_FIELD", "isMainField", String.valueOf(true), BooleanOperators.IS));

        Map<String, Object> prop = selectBuilder.fetchFirst();
        return FieldUtil.getAsBeanFromMap(prop, FacilioField.class);

    }

    public static List<Map<String, Object>> getPickListEnumField(String pickListField, FacilioField field, boolean getAllRecord, FieldType type) throws Exception {

        FacilioModule module = null;

        if (type == FieldType.LOOKUP) {
            LookupField sourceLookupField = (LookupField) field;
            module = sourceLookupField.getLookupModule();
        } else if (type == FieldType.MULTI_LOOKUP) {
            MultiLookupField sourceMultiLookupField = (MultiLookupField) field;
            module = sourceMultiLookupField.getLookupModule();
        }

        if (module != null && module.getType() == FacilioModule.ModuleType.PICK_LIST.getValue()) {

            ModuleBean modBean = Constants.getModBean();

            List<FacilioField> fields = modBean.getAllFields(module.getName());
            fields.add(FieldFactory.getIdField(module));

            FacilioField primaryField = ModuleMappingsUtil.getPrimaryFieldForLookupPickListModule(module);

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(fields)
                    .table(module.getTableName());

            if (!getAllRecord) {
                selectBuilder.andCondition(CriteriaAPI.getCondition(primaryField.getColumnName(), primaryField.getName(), String.valueOf(pickListField), StringOperators.IS));
            }

            List<Map<String, Object>> props = selectBuilder.get();

            return props;
        }
        return null;

    }

    private static long fetchFieldIdForLookupPickListField(String fieldName, FacilioField field, FieldType type) throws Exception {

        List<Map<String, Object>> props = getPickListEnumField(fieldName, field, false, field.getDataTypeEnum());

        if (props != null || (props != null && props.get(0) != null)) {
            Map<String, Object> prop = props.get(0);
            return FacilioUtil.parseLong(prop.get("id"));
        }

        return -1l;
    }

    private static long fetchFieldIdForEnumField(FacilioField field) throws Exception {
        FacilioModule module = ModuleFactory.getEnumFieldValuesModule();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getEnumFieldValuesFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(field.getFieldId()), NumberOperators.EQUALS));

        Map<String, Object> prop = selectBuilder.fetchFirst();

        return FacilioUtil.parseLong(prop.get("fieldId"));

    }

    private static long insertFieldMappingConfig(FieldMappingContext fieldMapping) throws Exception {

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getFieldMappingModule().getTableName())
                .fields(FieldFactory.getFieldMappingFields());

        Map<String, Object> props = FieldUtil.getAsProperties(fieldMapping);

        props.put("sysCreatedTime", System.currentTimeMillis());
        props.put("sysModifiedTime", System.currentTimeMillis());
        if (AccountUtil.getCurrentUser() != null) {
            props.put("sysCreatedByPeople", AccountUtil.getCurrentUser().getPeopleId());
            props.put("sysModifiedByPeople", AccountUtil.getCurrentUser().getPeopleId());
        }

        insertBuilder.addRecord(props);

        insertBuilder.save();

        return FacilioUtil.parseLong(props.get("id"));
    }

    private static long insertModuleMappingTemplate(ModuleMappingContext mapping) throws Exception {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getModuleMappingTemplateModule().getTableName())
                .fields(FieldFactory.getModuleMappingTemplateFields());

        Map<String, Object> props = FieldUtil.getAsProperties(mapping);

        props.put("sysCreatedTime", System.currentTimeMillis());
        props.put("sysModifiedTime", System.currentTimeMillis());
        if (AccountUtil.getCurrentUser() != null) {
            props.put("sysCreatedByPeople", AccountUtil.getCurrentUser().getPeopleId());
            props.put("sysModifiedByPeople", AccountUtil.getCurrentUser().getPeopleId());
        }

        props.put("isDefault", mapping.isDefault());
        props.put("isCustom", mapping.isCustom());

        insertBuilder.addRecord(props);

        insertBuilder.save();

        return FacilioUtil.parseLong(props.get("id"));
    }

    private static void insertModuleMapping(ModuleMappings mappings) throws Exception {
        List<ModuleMappingContext> moduleMappingContextList = mappings.getMappingList();

        for (ModuleMappingContext moduleMappingTemplateContext : moduleMappingContextList) {

            checkAndAddModuleMapping(moduleMappingTemplateContext, mappings);

        }

    }

    private static void checkAndAddModuleMapping(ModuleMappingContext moduleMappingTemplateContext, ModuleMappings mappings) throws Exception {

        mappings.setTargetModuleName(moduleMappingTemplateContext.getTargetModule());

        setUniqueNameForModuleMapping(mappings);

        long id = checkAndGetModuleMappingId(moduleMappingTemplateContext, mappings);

        if (id <= 0) {
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getModuleMappingModule().getTableName())
                    .fields(FieldFactory.getModuleMappingFields());

            Map<String, Object> props = FieldUtil.getAsProperties(mappings);

            props.put("sysCreatedTime", System.currentTimeMillis());
            props.put("sysModifiedTime", System.currentTimeMillis());
            if (AccountUtil.getCurrentUser() != null) {
                props.put("sysCreatedByPeople", AccountUtil.getCurrentUser().getPeopleId());
                props.put("sysModifiedByPeople", AccountUtil.getCurrentUser().getPeopleId());
            }

            insertBuilder.addRecord(props);

            insertBuilder.save();

            id = FacilioUtil.parseLong(props.get("id"));
        }

        moduleMappingTemplateContext.setParentId(id);
        addModuleMappingTemplate(moduleMappingTemplateContext);

    }

    private static void setUniqueNameForModuleMapping(ModuleMappings mappings) throws Exception {

        if (mappings.getName() == null || mappings.getName().equals("")) {
            String tempName = mappings.getSourceModuleName() + "To" + mappings.getTargetModuleName() + "Mapping";
            mappings.setName(tempName);
        }

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getModuleMappingModule().getTableName())
                .select(FieldFactory.getModuleMappingFields())
                .andCustomWhere("NAME = ? OR NAME LIKE ?", mappings.getName(), mappings.getName() + "\\_%")
                .orderBy("ID desc")
                .limit(1);

        Map<String, Object> prop = selectBuilder.fetchFirst();

        String name = mappings.getName();

        if (name.length() > 300) {
            name = name.substring(0, 300);
        }

        if (prop != null) {

            String moduleMappingName = constructUniqueName(name, prop, true);

            mappings.setName(moduleMappingName);
        }


    }

    private static String constructUniqueName(String name, Map<String, Object> prop, boolean isSystem) {

        long id = FacilioUtil.parseLong(prop.get("id")) + 1;

        String uniqueName = name + "_" + prop.get("orgId") + "_" + id;
        if (!isSystem) {
            uniqueName = uniqueName + "__c";
        }
        return uniqueName;
    }

    private static long checkAndGetModuleMappingId(ModuleMappingContext moduleMappingTemplateContext, ModuleMappings mappings) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule sourceModule = modBean.getModule(moduleMappingTemplateContext.getSourceModule());

        FacilioModule targetModule = modBean.getModule(moduleMappingTemplateContext.getTargetModule());

        mappings.setSourceModuleId(sourceModule.getModuleId());
        mappings.setTargetModuleId(targetModule.getModuleId());


        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getModuleMappingFields())
                .table(ModuleFactory.getModuleMappingModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("SOURCE_MODULE_ID", "sourceModuleId", String.valueOf(sourceModule.getModuleId()), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("TARGET_MODULE_ID", "targetModuleId", String.valueOf(targetModule.getModuleId()), StringOperators.IS));

        Map<String, Object> prop = selectBuilder.fetchFirst();

        if (prop == null) {
            return -1;
        }

        return FacilioUtil.parseLong(prop.get("id"));

    }

    public static void addModuleMapping(String moduleName) throws Exception {
        ModuleMappings mappings = fetchModuleMappings(moduleName);

        if (mappings != null) {
            addModuleMapping(mappings);
        }
    }

}
