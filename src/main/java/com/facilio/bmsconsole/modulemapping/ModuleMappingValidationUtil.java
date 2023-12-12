package com.facilio.bmsconsole.modulemapping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldMappingContext;
import com.facilio.bmsconsole.context.ModuleMappingContext;
import com.facilio.bmsconsole.context.ModuleMappings;
import com.facilio.bmsconsole.context.SubModuleMappingContext;
import com.facilio.bmsconsole.modulemapping.constants.FieldTypeConstants;
import com.facilio.bmsconsoleV3.context.SubFieldMappingContext;
import com.facilio.bmsconsoleV3.signup.util.ModuleMappingsUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleMappingValidationUtil {
    public static void moduleValidate(String moduleName) throws Exception {
        if (!StringUtils.isNotEmpty(moduleName)) {
            throw new IllegalArgumentException("The module name must not be empty.");
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        if (module == null) {
            throw new IllegalArgumentException("No module found with the name: " + moduleName);
        }

    }

    private static void fieldValidate(String fieldName, String moduleName) throws Exception {
        if (!StringUtils.isNotEmpty(fieldName)) {
            throw new IllegalArgumentException("The field must not be empty.");
        }

        Set<String> siteIdAllowedModules = FieldUtil.getSiteIdAllowedModules();

        if (fieldName.equals("siteId") && siteIdAllowedModules.contains(moduleName)) {
            return;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioField field = modBean.getField(fieldName, moduleName);

        if (field == null) {
            throw new IllegalArgumentException("No such field as " + fieldName + " exists within the module " + moduleName);
        }

    }

    private static void fieldMappingValidation(String sourceFieldName, String targetFieldName, String sourceModuleName, String targetModuleName) throws Exception {
        fieldValidate(sourceFieldName, sourceModuleName);
        fieldValidate(targetFieldName, targetModuleName);

    }

    public static void validateMappings(List<ModuleMappingContext> mappings, ModuleMappings moduleMappings) throws Exception {
        for (ModuleMappingContext mapping : mappings) {
            mapping.setSourceModule(moduleMappings.getSourceModuleName());
            mapping.setCustom(false);

            if (mapping.getDisplayName().length() >= 340) {
                throw new IllegalArgumentException("The display name " + mapping.getDisplayName() + " is too long. Please provide a display name with fewer than 340 characters.");
            }

            moduleValidation(mapping.getSourceModule(), mapping.getTargetModule());
            List<FieldMappingContext> fieldMappingsList = mapping.getFieldMappingList();

            if (fieldMappingsList == null) {
                throw new IllegalArgumentException("Module mapping " + mapping.getSourceModule() + " - " + mapping.getTargetModule() + " requires at least one associated field mapping.");
            }

            for (FieldMappingContext fieldMapping : fieldMappingsList) {
                fieldMappingValidation(fieldMapping, mapping);
            }

            List<SubModuleMappingContext> subModuleMappingsList =mapping.getSubModuleMappingList();

            if(subModuleMappingsList!=null){
                for(SubModuleMappingContext subModuleMapping : subModuleMappingsList){

                    subModuleValidation(subModuleMapping.getSourceModuleName(),subModuleMapping.getTargetModuleName(),mapping.getSourceModule(),mapping.getTargetModule());

                    SubModuleMappingValidationUtil.validateMappings(subModuleMapping);
                }
            }

        }
    }

    private static void subModuleValidation(String sourceSubModuleName, String targetSubModuleName, String sourceModuleName, String targetModuleName) throws Exception {

        moduleValidate(sourceSubModuleName);
        moduleValidate(targetSubModuleName);

        validateSubModule(sourceSubModuleName,sourceModuleName);

    }

    public static void validateSubModule(String childModuleName,String parentModuleName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule childModule = modBean.getModule(childModuleName);
        FacilioModule parentModule = modBean.getModule(parentModuleName);


        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("parentModuleId", "PARENT_MODULE_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getField("childModuleId", "CHILD_MODULE_ID", FieldType.NUMBER));

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table("SubModulesRel")
                .andCondition(CriteriaAPI.getCondition("CHILD_MODULE_ID", "childModuleId", String.valueOf(childModule.getModuleId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId", String.valueOf(parentModule.getModuleId()), NumberOperators.EQUALS));

        Map<String, Object> fieldProp = selectBuilder.fetchFirst();

        if(MapUtils.isEmpty(fieldProp)){
            throw new IllegalArgumentException("No parent-child relationship exists between the modules "+parentModuleName+" and "+childModuleName);
        }

    }


    public static void moduleValidation(String sourceModule, String targetModule) throws Exception {
        moduleValidate(sourceModule);
        moduleValidate(targetModule);

        if (sourceModule.equalsIgnoreCase(targetModule)) {
            throw new IllegalArgumentException("Source module and target module cannot be identical :: " + sourceModule);
        }

    }

    public static void fieldMappingValidation(FieldMappingContext fieldMapping, ModuleMappingContext mapping) throws Exception {
        fieldMappingValidation(fieldMapping.getSourceField(), fieldMapping.getTargetField(), mapping.getSourceModule(), mapping.getTargetModule());
        checkForDuplicateTargetFieldMappings(mapping.getFieldMappingList());
        validatePairOfFields(fieldMapping.getSourceField(), fieldMapping.getTargetField(), mapping.getSourceModule(), mapping.getTargetModule(), fieldMapping);
    }
    public static void fieldMappingValidationForSubModule(FieldMappingContext fieldMapping, SubModuleMappingContext mapping) throws Exception {
        fieldMappingValidation(fieldMapping.getSourceField(), fieldMapping.getTargetField(), mapping.getSourceModuleName(), mapping.getTargetModuleName());
        checkForDuplicateTargetFieldMappings(mapping.getFieldMappingList());
        validatePairOfFields(fieldMapping.getSourceField(), fieldMapping.getTargetField(), mapping.getSourceModuleName(), mapping.getTargetModuleName(), fieldMapping);
    }

    private static void checkForDuplicateTargetFieldMappings(List<FieldMappingContext> fieldMappings) {

        Map<String, String> targetToSourceFieldMap = new HashMap<>();

        for (FieldMappingContext mapping : fieldMappings) {

            if (targetToSourceFieldMap.containsKey(mapping.getTargetField())) {
                throw new IllegalArgumentException("Duplicate target field mapping found. Field: " + mapping.getTargetField() +
                        " is already mapped to source field: " + targetToSourceFieldMap.get(mapping.getTargetField()) + ".");
            }

            targetToSourceFieldMap.put(mapping.getTargetField(), mapping.getSourceField());
        }
    }

    private static void validatePairOfFields(String sourceFieldName, String targetFieldName, String sourceModuleName, String targetModuleName, FieldMappingContext fieldMapping) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioField sourceField = moduleBean.getField(sourceFieldName, sourceModuleName);

        FacilioField targetField = moduleBean.getField(targetFieldName, targetModuleName);

        FieldType sourceFieldType = sourceField.getDataTypeEnum();

        FieldType targetFieldType = targetField.getDataTypeEnum();


        boolean areTypesCompatible = areTypesCompatible(sourceFieldType, targetFieldType, sourceField, targetField, fieldMapping, sourceFieldName, targetFieldName);

        if (!areTypesCompatible) {
            dataTypeMisMatchError(sourceFieldType, targetFieldType, sourceField.getName(), targetField.getName());
        }

        if (FieldTypeConstants.LOOKUP_TYPES.contains(sourceFieldType) && !checkFieldsIsPickListType(sourceField, null)) {
            if (CollectionUtils.isNotEmpty(fieldMapping.getSubFieldMappings())) {
                fieldMapping.setSubFieldMappings(null);
            }
        }

    }

    private static void dataTypeMisMatchError(FieldType sourceFieldType, FieldType targetFieldType, String sourceField, String targetField) {
        throw new IllegalArgumentException("Conversion from " + sourceField + " to " + targetField + " is not possible due to a data type mismatch. Unable to cast " + sourceFieldType.getTypeAsString() + " to " + targetFieldType.getTypeAsString() + ".");
    }

    private static boolean areTypesCompatible(FieldType sourceFieldType, FieldType targetFieldType, FacilioField sourceField, FacilioField targetField, FieldMappingContext fieldMapping, String sourceFieldName, String targetFieldName) throws Exception {

        if (sourceFieldType == FieldType.BOOLEAN && isTargteFieldTypeInSet(targetFieldType, targetField, sourceFieldType, sourceField)) {
            List<SubFieldMappingContext> booleanFieldMappingContextList = fieldMapping.getSubFieldMappings();

            if (booleanFieldMappingContextList == null) {
                throw new IllegalArgumentException("Boolean field values are required for the field mapping between " + sourceFieldName + " and " + targetFieldName + ".");
            }

            checkBooleanFieldsAreValid(sourceField, targetField, targetFieldType, booleanFieldMappingContextList);

        }

        if (checkFieldsAreEnumOrPickList(sourceFieldType, targetFieldType, sourceField, targetField)) {
            List<SubFieldMappingContext> enumFieldMappingContextList = fieldMapping.getSubFieldMappings();

            if (enumFieldMappingContextList == null) {
                throw new IllegalArgumentException("Enum values are required for the field mapping between " + sourceFieldName + " and " + targetFieldName + ".");
            }

            checkEnumAndPickListFieldsAreValid(enumFieldMappingContextList, sourceFieldName, targetFieldName, sourceField, targetField, sourceFieldType, targetFieldType);

            return true;
        }

        if (sourceFieldType == FieldType.BOOLEAN || FieldTypeConstants.ENUM_FIELD_TYPES.contains(sourceFieldType) || checkFieldsIsPickListType(sourceField, null)) {
            checkNumberOfTargetFieldsAreValid(targetFieldType, fieldMapping, targetField, sourceField);
        }

        FieldMappingValidationUtil fieldMappingValidationUtil = new FieldMappingValidationUtil(sourceFieldType, targetFieldType, sourceField, targetField);

        fieldMappingValidationUtil.fieldMappingValidation();

        int targetTypeInt = targetFieldType.getTypeAsInt();

        if (targetTypeInt == FieldType.STRING.getTypeAsInt() || targetTypeInt == FieldType.BIG_STRING.getTypeAsInt() || targetTypeInt == FieldType.LARGE_TEXT.getTypeAsInt() || sourceFieldType.getTypeAsInt() == targetTypeInt) {
            return true;
        }

        return true;
    }

    private static void checkNumberOfTargetFieldsAreValid(FieldType targetFieldType, FieldMappingContext fieldMapping, FacilioField targetField, FacilioField sourceField) {
        if (FieldTypeConstants.LOOKUP_TYPES.contains(targetFieldType) || FieldTypeConstants.ENUM_FIELD_TYPES.contains(targetFieldType) || targetFieldType == FieldType.BOOLEAN) {

            List<SubFieldMappingContext> fieldMappingContextList = fieldMapping.getSubFieldMappings();

            if (FieldTypeConstants.LOOKUP_TYPES.contains(targetFieldType)) {

                boolean isLookupPicklist = false;
                boolean isMulitiLookupPicklist = false;

                LookupField lookupField = getFieldLookup(targetField);
                MultiLookupField multiLookupField = getFieldMultiLookup(targetField);

                if (lookupField != null) {
                    FacilioModule lookupModule = lookupField.getLookupModule();
                    if (lookupModule.getType() == FacilioModule.ModuleType.PICK_LIST.getValue()) {
                        isLookupPicklist = true;
                    }

                } else {
                    FacilioModule lookupModule = multiLookupField.getLookupModule();
                    if (lookupModule.getType() == FacilioModule.ModuleType.PICK_LIST.getValue()) {
                        isMulitiLookupPicklist = true;
                    }
                }

                LookupField sourceLookupField = getFieldLookup(sourceField);
                MultiLookupField sourceMultiLookupField = getFieldMultiLookup(sourceField);

                LookupField targetLookupField = getFieldLookup(targetField);
                MultiLookupField targetMultiLookupField = getFieldMultiLookup(targetField);

                FacilioModule sourceModule = sourceLookupField != null ? sourceLookupField.getLookupModule() : sourceMultiLookupField != null ? sourceMultiLookupField.getLookupModule() : null;
                FacilioModule targetModule = targetLookupField != null ? targetLookupField.getLookupModule() : targetMultiLookupField != null ? targetMultiLookupField.getLookupModule() : null;

                boolean isValid = false;

                if (sourceModule != null && targetModule != null) {
                    isValid = sourceModule.getName().equals(targetModule.getName());
                }

                if (!isValid && (isLookupPicklist || isMulitiLookupPicklist)) {
                    if (CollectionUtils.isEmpty(fieldMappingContextList)) {
                        throw new IllegalArgumentException("Lookup picklist values are required for the field mapping between " + sourceField.getName() + " and " + targetField.getName() + ".");
                    } else if (isLookupPicklist) {
                        List<List<String>> targetLookupPicklistFields = fieldMappingContextList.stream()
                                .map(SubFieldMappingContext::getTargetFieldNames)
                                .collect(Collectors.toList());

                        for (List<String> fieldsList : targetLookupPicklistFields) {
                            if (fieldsList.size() > 1) {
                                throw new IllegalArgumentException("Invalid mapping: Source field " + sourceField.getName() + " should map to exactly one target field value, but found multiple values for the target field(s): " + String.join(", ", fieldsList));
                            }

                        }
                    }
                }
            } else {
                if (CollectionUtils.isNotEmpty(fieldMappingContextList)) {
                    List<List<String>> targetEnumFields = fieldMappingContextList.stream()
                            .map(SubFieldMappingContext::getTargetFieldNames)
                            .collect(Collectors.toList());

                    if (targetFieldType != FieldType.MULTI_ENUM) {
                        for (List<String> fieldsList : targetEnumFields) {
                            if (fieldsList.size() > 1) {
                                throw new IllegalArgumentException("Invalid mapping: Source field " + sourceField.getName() + " should map to exactly one target field value, but found multiple values for the target field(s): " + String.join(", ", fieldsList));
                            }
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Field values are required for the field mapping between " + sourceField.getName() + " and " + targetField.getName() + ".");
                }
            }
        }

    }

    private static void checkEnumAndPickListFieldsAreValid(List<SubFieldMappingContext> enumFieldMappingContextList, String sourceFieldName, String targetFieldName, FacilioField sourceField, FacilioField targetField, FieldType sourceFieldType, FieldType targetFieldType) throws Exception {
        if (enumFieldMappingContextList == null) {
            throw new IllegalArgumentException("Enum or Picklist values are required for the field mapping between " + sourceFieldName + " and " + targetFieldName + ".");
        }

        List<String> sourceFieldList = enumFieldMappingContextList.stream()
                .map(SubFieldMappingContext::getSourceField)
                .collect(Collectors.toList());

        List<String> targetFieldList = enumFieldMappingContextList.stream()
                .map(SubFieldMappingContext::getTargetFieldNames)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        checkEnumFieldsAreValid(sourceFieldList, targetFieldList, sourceFieldType, sourceField, targetField, targetFieldType, enumFieldMappingContextList);
        checkPickListFieldsAreValid(sourceFieldList, targetFieldList, sourceFieldType, sourceField, targetField, targetFieldType, enumFieldMappingContextList);

    }

    private static void checkBooleanFieldsAreValid(FacilioField sourceField, FacilioField targetField, FieldType targetFieldType, List<SubFieldMappingContext> booleanFieldMappingContextList) throws Exception {

        if (booleanFieldMappingContextList.size() != 2) {
            throw new IllegalArgumentException("Boolean mapping can only be true and false.");
        }

        Set<String> uniqueBooleanFields = new HashSet<>();

        for (SubFieldMappingContext booleanFieldMappingContext : booleanFieldMappingContextList) {
            String sourceBooleanField = booleanFieldMappingContext.getSourceField();

            Set<String> validValues = new HashSet<>(Arrays.asList(FacilioConstants.ContextNames.BooleanMapping.TRUE, FacilioConstants.ContextNames.BooleanMapping.FALSE));

            String normalizedValue = sourceBooleanField.toLowerCase();

            boolean isValid = validValues.contains(normalizedValue);

            if (!isValid) {
                throw new IllegalArgumentException("Invalid value for the source boolean field in Boolean Field Mapping. It can only be 'true' or 'false'. Received: " + sourceBooleanField);
            }

            if (!uniqueBooleanFields.add(sourceBooleanField)) {
                throw new IllegalArgumentException("Boolean mapping for '" + sourceBooleanField + "' already exists.");
            }

        }

        List<List<String>> targetBooleanFields = booleanFieldMappingContextList.stream()
                .map(SubFieldMappingContext::getTargetFieldNames)
                .collect(Collectors.toList());

        List<String> targetBooleanFieldList = booleanFieldMappingContextList.stream()
                .map(SubFieldMappingContext::getTargetFieldNames)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        checkEnumFieldsAreValid(targetField, targetBooleanFieldList, targetFieldType);

        checkPickListFieldsAreValid(targetField, targetBooleanFieldList, true);

        if (targetFieldType == FieldType.LOOKUP || targetFieldType == FieldType.ENUM || targetFieldType == FieldType.SYSTEM_ENUM) {
            for (List<String> fieldsList : targetBooleanFields) {
                if (fieldsList.size() > 1) {
                    throw new IllegalArgumentException("Invalid mapping: Source field " + sourceField.getName() + " should map to exactly one target field value, but found multiple values for the target field(s): " + String.join(", ", fieldsList));
                }
            }
        }
    }

    private static boolean isTargteFieldTypeInSet(FieldType targetFieldType, FacilioField targetField, FieldType sourceFieldType, FacilioField sourceField) {
        Set<FieldType> enumFieldTypes = FieldTypeConstants.ENUM_FIELD_TYPES;

        Set<FieldType> lookupFieldTypes = FieldTypeConstants.LOOKUP_TYPES;

        if (enumFieldTypes.contains(targetFieldType)) {
            return true;
        } else if (lookupFieldTypes.contains(targetFieldType)) {
            LookupField lookupField = getFieldLookup(targetField);
            MultiLookupField multiLookupField = getFieldMultiLookup(targetField);

            if (lookupField != null) {
                FacilioModule lookupModule = lookupField.getLookupModule();
                if (lookupModule.getType() == FacilioModule.ModuleType.PICK_LIST.getValue()) {
                    return true;
                }

            } else {
                FacilioModule lookupModule = multiLookupField.getLookupModule();
                if (lookupModule.getType() == FacilioModule.ModuleType.PICK_LIST.getValue()) {
                    return true;
                }
            }
            dataTypeMisMatchError(sourceFieldType, targetFieldType, sourceField.getName(), targetField.getName());
        }
        return false;

    }

    private static void checkPickListFieldsAreValid(List<String> sourcePickListFields, List<String> targetPickListFields, FieldType sourceFieldType, FacilioField sourceField, FacilioField targetField, FieldType targetFieldType, List<SubFieldMappingContext> enumFieldMappingContextList) throws Exception {

        checkPickListFieldsAreValid(sourceField, sourcePickListFields, false);
        checkPickListFieldsAreValid(targetField, targetPickListFields, false);

        LookupField sourceLookupField = null;

        if (sourceField.getDataTypeEnum() == FieldType.LOOKUP) {
            sourceLookupField = (LookupField) sourceField;

            if (sourceLookupField != null && (sourceLookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue())) {
                List<Map<String, Object>> props = ModuleMappingsUtil.getPickListEnumField(null, sourceLookupField, true, FieldType.LOOKUP);

                FacilioModule module = sourceLookupField.getLookupModule();
                FacilioField primaryField = ModuleMappingsUtil.getPrimaryFieldForLookupPickListModule(module);

                for (Map<String, Object> prop : props) {
                    String key = (String) prop.get(primaryField.getName());
                    boolean value = sourcePickListFields.contains(key);
                    if (!value) {
                        throw new IllegalArgumentException("At least one target field is required for the source lookup picklist enum field '" + key + "'.");
                    }
                }

            }
        }

    }

    private static void checkPickListFieldsAreValid(FacilioField field, List<String> pickListFields, boolean checkForBoolean) throws Exception {
        if (field.getDataTypeEnum() != FieldType.LOOKUP && field.getDataTypeEnum() != FieldType.MULTI_LOOKUP) {
            return;
        }

        LookupField lookupField = getFieldLookup(field);
        MultiLookupField multiLookupField = getFieldMultiLookup(field);

        if ((lookupField != null && lookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue()) ||
                (multiLookupField != null && multiLookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue())) {

            for (String pickListField : pickListFields) {
                if (!StringUtils.isNotEmpty(pickListField)) {
                    throw new IllegalArgumentException(pickListField + " picklist field cannot be empty");
                }

                FieldType fieldType = (field.getDataTypeEnum() == FieldType.LOOKUP) ? FieldType.LOOKUP : FieldType.MULTI_LOOKUP;
                List<Map<String, Object>> pickListFieldValueList = ModuleMappingsUtil.getPickListEnumField(pickListField, field, false, fieldType);

                if (CollectionUtils.isEmpty(pickListFieldValueList)) {
                    throw new IllegalArgumentException("Invalid lookup picklist field :: " + pickListField);
                }
            }
        }
    }

    private static void checkEnumFieldsAreValid(List<String> sourceEnumFieldList, List<String> targetEnumFieldList, FieldType sourceFieldType, FacilioField sourceField, FacilioField targetField, FieldType targetFieldType, List<SubFieldMappingContext> enumFieldMappingContextList) throws Exception {

        checkEnumFieldsAreValid(sourceField, sourceEnumFieldList, sourceFieldType);
        checkEnumFieldsAreValid(targetField, targetEnumFieldList, targetFieldType);


        Set<FieldType> enumFieldValues = new HashSet<>(Arrays.asList(
                FieldType.ENUM,
                FieldType.MULTI_ENUM,
                FieldType.SYSTEM_ENUM
        ));

        if (enumFieldValues.contains(sourceFieldType)) {

            if (sourceFieldType == FieldType.ENUM) {
                FacilioModule module = ModuleFactory.getEnumFieldValuesModule();
                GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                        .select(FieldFactory.getEnumFieldValuesFields())
                        .table(module.getTableName())
                        .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(sourceField.getFieldId()), NumberOperators.EQUALS));

                List<Map<String, Object>> props = selectBuilder.get();

                for (Map<String, Object> prop : props) {
                    String key = (String) prop.get("value");
                    boolean value = sourceEnumFieldList.contains(key);
                    if (!value) {
                        throw new IllegalArgumentException("At least one target field is required for the source enum field '" + key + "'.");
                    }
                }
            }

            if (sourceFieldType == FieldType.SYSTEM_ENUM) {
                SystemEnumField systemEnumField = (SystemEnumField) sourceField;
                List<EnumFieldValue<Integer>> enumFieldValueList = systemEnumField.getValues();


                for (EnumFieldValue prop : enumFieldValueList) {
                    String key = prop.getValue();
                    boolean value = sourceEnumFieldList.contains(key);
                    if (!value) {
                        throw new IllegalArgumentException("At least one target field is required for the source enum field '" + key + "'.");
                    }
                }

            }
        }
    }

    private static void checkEnumFieldsAreValid(FacilioField field, List<String> enumFieldList, FieldType fieldType) throws Exception {

        Set<FieldType> enumFieldValues = new HashSet<>(Arrays.asList(
                FieldType.ENUM,
                FieldType.MULTI_ENUM,
                FieldType.SYSTEM_ENUM
        ));

        if (enumFieldValues.contains(fieldType)) {
            for (String enumField : enumFieldList) {
                if (!StringUtils.isNotEmpty(enumField)) {
                    throw new IllegalArgumentException(enumField + " enum field cannot be empty");
                }

                if (fieldType == FieldType.ENUM) {
                    EnumFieldValue enumFieldValue = getEnumField(field, enumField);

                    if (enumFieldValue == null) {
                        throw new IllegalArgumentException("Invalid enum field :: " + enumField);
                    }
                } else if (fieldType == FieldType.SYSTEM_ENUM) {
                    SystemEnumField systemEnumField = (SystemEnumField) field;
                    List<EnumFieldValue<Integer>> enumFieldValueList = systemEnumField.getValues();

                    int systemEnumFieldValue = enumFieldValueList.stream()
                            .filter(enumFieldValue -> enumFieldValue.getValue().equals(enumField))
                            .findFirst()
                            .map(EnumFieldValue::getIndex)
                            .orElseThrow(() -> new IllegalArgumentException("Value not found for the field : " + enumField));

                    if (systemEnumFieldValue <= 0) {
                        throw new IllegalArgumentException("Invalid enum field :: " + enumField);
                    }


                }
            }
        }

    }

    public static EnumFieldValue getEnumField(FacilioField field, String enumFieldName) throws Exception {
        FacilioModule module = ModuleFactory.getEnumFieldValuesModule();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getEnumFieldValuesFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(field.getFieldId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("VAL", "fieldName", String.valueOf(enumFieldName), StringOperators.IS));


        Map<String, Object> prop = selectBuilder.fetchFirst();

        return FieldUtil.getAsBeanFromMap(prop, EnumFieldValue.class);

    }

    public static List<Map<String, Object>> getEnumFields(FacilioField field) throws Exception {
        FacilioModule module = ModuleFactory.getEnumFieldValuesModule();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getEnumFieldValuesFields())
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(field.getFieldId()), NumberOperators.EQUALS));


        List<Map<String, Object>> props = selectBuilder.get();

        return props;
    }

    private static boolean checkFieldsAreEnumOrPickList(FieldType sourceFieldType, FieldType targetFieldType, FacilioField sourceField, FacilioField targetField) {

        if (areFieldsLookupOrMultiLookup(sourceField, targetField)) return false;

        if (checkFieldsIsPickListType(sourceField, null) && checkFieldsIsPickListType(null, targetField)) return true;

        if (areFieldsEnumType(sourceFieldType, targetFieldType)) return true;

        boolean isValid = getEnumOrPickListCondition(sourceFieldType, sourceField, targetFieldType, targetField);

        if (isValid) return true;

        return false;

    }

    private static boolean areFieldsSystemEnum(FieldType sourceFieldType, FieldType targetFieldType) {
        return sourceFieldType == FieldType.SYSTEM_ENUM && targetFieldType == FieldType.SYSTEM_ENUM;
    }

    protected static boolean areFieldsPickList(FacilioField sourceField, FacilioField targetField) {
        LookupField sourceLookupField = getFieldLookup(sourceField);
        LookupField targetLookupField = getFieldLookup(targetField);

        return (targetLookupField != null && targetLookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue() &&
                sourceLookupField != null && sourceLookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue());
    }

    protected static boolean areMultiLookupFieldsArePickList(FacilioField sourceField, FacilioField targetField) {
        MultiLookupField sourceLookupField = getFieldMultiLookup(sourceField);
        MultiLookupField targetLookupField = getFieldMultiLookup(targetField);

        return (targetLookupField != null && targetLookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue() &&
                sourceLookupField != null && sourceLookupField.getLookupModule().getType() == FacilioModule.ModuleType.PICK_LIST.getValue());

    }

    private static boolean areFieldsEnumType(FieldType sourceFieldType, FieldType targetFieldType) {

        boolean isSourceIsEnum = FieldTypeConstants.ENUM_FIELD_TYPES.contains(sourceFieldType);
        boolean isTargetIsEnum = FieldTypeConstants.ENUM_FIELD_TYPES.contains(targetFieldType);

        return isSourceIsEnum && isTargetIsEnum;

    }

    private static boolean areFieldsLookupOrMultiLookup(FacilioField sourceField, FacilioField targetField) {

        LookupField sourceLookupField = getFieldLookup(sourceField);
        MultiLookupField sourceMultiLookupField = getFieldMultiLookup(sourceField);

        LookupField targetLookupField = getFieldLookup(targetField);
        MultiLookupField targetMultiLookupField = getFieldMultiLookup(targetField);

        boolean isValid = ((sourceLookupField != null && sourceLookupField.getDataTypeEnum() == FieldType.LOOKUP) || (sourceMultiLookupField != null && sourceMultiLookupField.getDataTypeEnum() == FieldType.MULTI_LOOKUP)) &&
                ((targetLookupField != null && targetLookupField.getDataTypeEnum() == FieldType.LOOKUP) || (targetMultiLookupField != null && targetMultiLookupField.getDataTypeEnum() == FieldType.MULTI_LOOKUP));

        if (isValid) {
            boolean isPickListType = checkFieldsIsPickListType(sourceField, targetField);
            if (isPickListType) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean checkFieldsIsPickListType(FacilioField sourceField, FacilioField targetField) {

        LookupField sourceLookupField = getFieldLookup(sourceField);
        MultiLookupField sourceMultiLookupField = getFieldMultiLookup(sourceField);

        LookupField targetLookupField = getFieldLookup(targetField);
        MultiLookupField targetMultiLookupField = getFieldMultiLookup(targetField);

        FacilioModule.ModuleType sourceModuleType = sourceLookupField != null ? sourceLookupField.getLookupModule().getTypeEnum() : sourceMultiLookupField != null ? sourceMultiLookupField.getLookupModule().getTypeEnum() : null;

        FacilioModule.ModuleType targetModuleType = targetLookupField != null ? targetLookupField.getLookupModule().getTypeEnum() : targetMultiLookupField != null ? targetMultiLookupField.getLookupModule().getTypeEnum() : null;

        FacilioModule sourceModule = sourceLookupField != null ? sourceLookupField.getLookupModule() : sourceMultiLookupField != null ? sourceMultiLookupField.getLookupModule() : null;
        FacilioModule targetModule = targetLookupField != null ? targetLookupField.getLookupModule() : targetMultiLookupField != null ? targetMultiLookupField.getLookupModule() : null;

        if (sourceModule != null && targetModule != null) {
            boolean isValid = sourceModule.getName().equals(targetModule.getName());
            if (isValid) {
                return false;
            }
        }

        if (sourceModuleType == FacilioModule.ModuleType.PICK_LIST || targetModuleType == FacilioModule.ModuleType.PICK_LIST) {
            return true;
        }

        return false;
    }

    static LookupField getFieldLookup(FacilioField field) {
        return (field != null && field.getDataTypeEnum() == FieldType.LOOKUP) ? (LookupField) field : null;
    }

    static MultiLookupField getFieldMultiLookup(FacilioField field) {
        return (field != null && field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) ? (MultiLookupField) field : null;
    }

    private static boolean getEnumOrPickListCondition(FieldType sourceFieldType, FacilioField sourceField, FieldType targetFieldType, FacilioField targetField) {

        boolean isSourceEnum = FieldTypeConstants.ENUM_FIELD_TYPES.contains(sourceFieldType);

        boolean isSourcePickListType = isFieldPickListType(sourceField);

        boolean isTargetEnum = FieldTypeConstants.ENUM_FIELD_TYPES.contains(targetFieldType);

        boolean isTargetPickListType = isFieldPickListType(targetField);

        return (isSourceEnum || isSourcePickListType) && (isTargetEnum || isTargetPickListType);

    }

    private static boolean isFieldPickListType(FacilioField field) {
        LookupField lookupField = getFieldLookup(field);
        MultiLookupField multiLookupField = getFieldMultiLookup(field);

        FacilioModule module = lookupField != null ? lookupField.getLookupModule() : multiLookupField != null ? multiLookupField.getLookupModule() : null;

        return module != null && module.getTypeEnum() == FacilioModule.ModuleType.PICK_LIST;
    }

    private static void checkAllEnumFieldsOfSourceFieldNotEmpty(FieldType sourceFieldType, FacilioField sourceField, List<SubFieldMappingContext> enumFieldMappingContextList) throws Exception {

        List<String> sourceEnumFields = enumFieldMappingContextList.stream()
                .map(SubFieldMappingContext::getSourceField)
                .collect(Collectors.toList());

        if (sourceFieldType == FieldType.ENUM) {
            FacilioModule module = ModuleFactory.getEnumFieldValuesModule();
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getEnumFieldValuesFields())
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(sourceField.getFieldId()), NumberOperators.EQUALS));

            List<Map<String, Object>> props = selectBuilder.get();

            for (Map<String, Object> prop : props) {
                String key = (String) prop.get("value");
                boolean value = sourceEnumFields.contains(key);
                if (!value) {
                    throw new IllegalArgumentException("At least one target field is required for the source enum field '" + key + "'.");
                }
            }
        }

        if (sourceFieldType == FieldType.SYSTEM_ENUM) {
            SystemEnumField systemEnumField = (SystemEnumField) sourceField;
            List<EnumFieldValue<Integer>> enumFieldValueList = systemEnumField.getValues();


            for (EnumFieldValue prop : enumFieldValueList) {
                String key = prop.getValue();
                boolean value = sourceEnumFields.contains(key);
                if (!value) {
                    throw new IllegalArgumentException("At least one target field is required for the source enum field '" + key + "'.");
                }
            }

        }


    }

}
