package com.facilio.bmsconsole.modulemapping;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modulemapping.constants.FieldTypeConstants;
import com.facilio.bmsconsoleV3.signup.util.ModuleMappingsUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.qa.context.questions.handler.CommonStringQuestionHandler;
import com.facilio.report.formatter.DateFormatter;
import com.facilio.util.FacilioUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FieldMappingConfigurationUtil {

    FacilioField sourceField;
    FacilioField targetField;

    Object sourceValue;

    FieldType sourceFieldType;

    FieldType targetFieldType;

    long id = -1L;

    public FieldMappingConfigurationUtil() {

    }

    public FieldMappingConfigurationUtil(FacilioField sourceField, FacilioField targetField, Object sourceValue, FieldType sourceFieldType, FieldType targetFieldType, long id) {
        this.sourceField = sourceField;
        this.targetField = targetField;
        this.sourceValue = sourceValue;
        this.sourceFieldType = sourceFieldType;
        this.targetFieldType = targetFieldType;
        this.id = id;
    }

    public Object getTargetValue() throws Exception {

        switch (sourceFieldType) {
            case STRING:
                return stringToTargetValueConversion();
            case NUMBER:
                return numberToTargetValueConversion();
            case DECIMAL:
                return decimalToTargetValueConversion();
            case BOOLEAN:
                return booleanToTargetValueConversion();
            case DATE:
            case DATE_TIME:
                return dateTimeToTargetValueConversion();
            case LOOKUP:
                return lookupToTargetValueConversion();
            case ENUM:
            case SYSTEM_ENUM:
                return enumToTargetValueConversion();
            case ID:
                return idToTargetValueConversion();
            case MULTI_LOOKUP:
                return multiLookupToTargetValueConversion();
            case MULTI_ENUM:
                return multiEnumToTargetValueConversion();
            case LARGE_TEXT:
                return largeTextToTargetValueConversion();
            case BIG_STRING:
                return bigStringToTargetValueConversion();
            case STRING_SYSTEM_ENUM:
                return stringSystemEnumToTargetValueConversion();
            case CURRENCY_FIELD:
                return currencyFieldToTargetValueConversion();
            case MULTI_CURRENCY_FIELD:
                return multiCurrencyFieldToTargetValueConversion();
            case FILE:
                return fileToTargetValueConversion();
            case URL_FIELD:
                return urlFieldToTargetValueConversion();
            default:
                return sourceToTargetValueConversion();

        }

    }

    private Object urlFieldToTargetValueConversion() {
        switch (targetFieldType) {
            case URL_FIELD:
                return sourceValue;
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return urlToStringConversion();
        }
        return null;
    }

    private String urlToStringConversion() {
        HashMap<String, Object> sourceMap = (HashMap<String, Object>) sourceValue;

        String result = String.valueOf(sourceMap.get("href"));

        if (targetFieldType == FieldType.STRING && result.length() > CommonStringQuestionHandler.SHORT_STRING_MAX_LENGTH) {
            return result.substring(0, 250);
        } else if (targetFieldType == FieldType.BIG_STRING && result.length() > CommonStringQuestionHandler.BIG_STRING_MAX_LENGTH) {
            return result.substring(0, 2000);
        }

        return result;
    }

    private Object fileToTargetValueConversion() {
        return sourceValue;
    }

    private Object multiCurrencyFieldToTargetValueConversion() {
        switch (targetFieldType) {
            case MULTI_CURRENCY_FIELD:
                return sourceValue;
            case STRING:
            case LARGE_TEXT:
            case BIG_STRING:
                return String.valueOf(sourceValue);
        }
        return null;
    }

    private Object currencyFieldToTargetValueConversion() {
        switch (targetFieldType) {
            case CURRENCY_FIELD:
                return sourceValue;
            case MULTI_CURRENCY_FIELD:
                return null;
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return String.valueOf(sourceValue);
        }
        return null;
    }

    private Object stringSystemEnumToTargetValueConversion() {
        switch (targetFieldType) {
            case STRING_SYSTEM_ENUM:
                return sourceValue;
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return String.valueOf(sourceValue);
        }
        return null;
    }

    private Object bigStringToTargetValueConversion() {
        switch (targetFieldType) {
            case LARGE_TEXT:
            case BIG_STRING:
                return sourceValue;
            case STRING:
                return getSubString(sourceValue, CommonStringQuestionHandler.SHORT_STRING_MAX_LENGTH);
        }
        return null;
    }

    private Object largeTextToTargetValueConversion() {
        switch (targetFieldType) {
            case LARGE_TEXT:
                return sourceValue;
            case BIG_STRING:
                return getSubString(sourceValue, CommonStringQuestionHandler.BIG_STRING_MAX_LENGTH);
            case STRING:
                return getSubString(sourceValue, CommonStringQuestionHandler.SHORT_STRING_MAX_LENGTH);
        }
        return null;
    }

    private Object getSubString(Object sourceValue, int length) {

        String largeText = String.valueOf(sourceValue);

        return largeText.substring(0, length);

    }

    private Object multiEnumToTargetValueConversion() throws Exception {
        switch (targetFieldType) {
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return multiEnumToStringConversion();
            case MULTI_ENUM:
                return multiEnumToMultiEnumConversion();
            case MULTI_LOOKUP:
                return multiEnumToMultiLookupConversion();

        }
        return null;
    }

    private Object multiEnumToMultiEnumConversion() throws Exception {

        List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

        if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

            ArrayList<Long> list = new ArrayList<>();

            List<Map<String, Object>> desiredMapList = getResultForMultiEnum(subFieldMappingList);

            for (Map<String, Object> desiredMap : desiredMapList) {

                if (desiredMap != null && !desiredMap.isEmpty()) {

                    long targetEnumFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                    list.add(targetEnumFieldId);
                }
            }
            return list;
        }

        return null;

    }

    private List<Map<String, Object>> getResultForMultiEnum(List<Map<String, Object>> subFieldMappingList) {

        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Map<String, Object> map : subFieldMappingList) {

            long sourceFieldResultId = FacilioUtil.parseLong(map.get("sourceFieldId"));

            ArrayList<Object> sourceValues = (ArrayList<Object>) sourceValue;

            for (Object value : sourceValues) {
                if (FacilioUtil.parseLong(value) == sourceFieldResultId) {
                    resultList.add(map);
                }
            }
        }
        return resultList;
    }

    private Object multiEnumToMultiLookupConversion() throws Exception {
        List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

        if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

            List<Map<String, Object>> desiredMapList = getResultForMultiEnum(subFieldMappingList);
            ArrayList<Map<String, Object>> list = new ArrayList<>();

            for (Map<String, Object> desiredMap : desiredMapList) {
                if (desiredMap != null && !desiredMap.isEmpty()) {

                    if (desiredMap.get("targetFieldId") != null) {
                        long targetLookupFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));


                        Map<String, Object> resultMap = createMap("id", targetLookupFieldId);

                        list.add(resultMap);


                    } else if (desiredMap.get("targetField") != null) {
                        ModuleBaseWithCustomFields record = ModuleMappingsUtil.fetchRecordIdForLookup(String.valueOf(desiredMap.get("targetField")), targetField);

                        if (record != null) {

                            Map<String, Object> resultMap = createMap("id", record.getId());

                            list.add(resultMap);


                        }
                        throw new IllegalArgumentException("No record found for module " + targetField.getModule().getName() + " :: " + desiredMap.get("targetField"));

                    }
                }
            }
            return list;
        }

        return null;
    }

    private Object multiEnumToStringConversion() {
        MultiEnumField enumField = (MultiEnumField) sourceField;
        List<EnumFieldValue<Integer>> enumFieldValueList = enumField.getValues();

        ArrayList<Integer> sourceValues = (ArrayList<Integer>) sourceValue;

        List<String> values = enumFieldValueList.stream()
                .filter(enumFieldValue -> sourceValues.contains((int) enumFieldValue.getIndex()))
                .map(EnumFieldValue::getValue)
                .collect(Collectors.toList());

        String result = null;

        if (CollectionUtils.isNotEmpty(values)) {
            result = String.join(", ", values);
        }

        if (targetFieldType == FieldType.STRING && result.length() > 250) {
            result = result.substring(0, 250);
        }

        return result;

    }

    private Object multiLookupToTargetValueConversion() throws Exception {
        switch (targetFieldType) {
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return multiLookupToStringConversion();
            case MULTI_LOOKUP:
                return multiLookupToMultiLookupConversion();
            case MULTI_ENUM:
                return multiLookupToMultiEnumConversion();

        }
        return null;
    }

    private Object multiLookupToMultiLookupConversion() throws Exception {
        boolean areFieldsPickList = ModuleMappingValidationUtil.checkFieldsIsPickListType(sourceField, targetField);

        if (areFieldsPickList) {
            List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

            if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

                List<Map<String, Object>> desiredMapList = getResultMapForMultiLookup(subFieldMappingList);
                ArrayList<Map<String, Object>> list = new ArrayList<>();

                for (Map<String, Object> desiredMap : desiredMapList) {
                    if (desiredMap.get("targetFieldId") != null) {
                        long targetLookupFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                        Map<String, Object> resultMap = createMap("id", targetLookupFieldId);

                        list.add(resultMap);

                    } else if (desiredMap.get("targetField") != null) {
                        ModuleBaseWithCustomFields record = ModuleMappingsUtil.fetchRecordIdForLookup(String.valueOf(desiredMap.get("targetField")), targetField);

                        if (record != null) {

                            Map<String, Object> resultMap = createMap("id", record.getId());

                            list.add(resultMap);

                        }
                        throw new IllegalArgumentException("No record found for module " + targetField.getModule().getName() + " :: " + desiredMap.get("targetField"));

                    }
                }
                return list;

            }
        } else {

            List<HashMap<String, Object>> resultList = (List<HashMap<String, Object>>) sourceValue;

            ArrayList<Map<String, Object>> list = new ArrayList<>();

            for (HashMap<String, Object> resultMap : resultList) {
                long targetLookupFieldId = FacilioUtil.parseLong(resultMap.get("id"));

                Map<String, Object> map = createMap("id", targetLookupFieldId);

                list.add(map);
            }

            return list;

        }

        return null;
    }

    private Object multiLookupToMultiEnumConversion() throws Exception {

        List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

        if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

            List<Map<String, Object>> desiredMapList = getResultMapForMultiLookup(subFieldMappingList);

            ArrayList<Long> list = new ArrayList<>();

            for (Map<String, Object> desiredMap : desiredMapList) {
                if (desiredMap != null && !desiredMap.isEmpty()) {

                    long targetLookupFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                    list.add(targetLookupFieldId);
                }
            }
            return list;
        }
        return null;
    }

    private Object multiLookupToStringConversion() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField mainField = getMainFieldOfMultiLookup(sourceField, modBean);

        List<HashMap<String, Object>> resultList = (List<HashMap<String, Object>>) sourceValue;

        ArrayList<String> resultStringList = new ArrayList<>();

        for (Map<String, Object> resultMap : resultList) {

            String recordName = String.valueOf(resultMap.get(mainField.getName()));

            resultStringList.add(recordName);

        }

        String result = null;

        if (CollectionUtils.isNotEmpty(resultStringList)) {
            result = String.join(", ", resultStringList);
        }

        if (targetFieldType == FieldType.STRING && result.length() > 250) {
            result = result.substring(0, 250);
        }

        return result;


    }

    private Object systemEnumToTargetValueConversion() {
        return null;
    }

    private Object idToTargetValueConversion() {
        switch (targetFieldType) {
            case NUMBER:
            case DECIMAL:
                return FacilioUtil.parseLong(sourceValue);
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return String.valueOf(sourceValue);
            case LOOKUP:
                return idToLookupConversion();
        }
        return null;
    }

    private Object idToLookupConversion() {

        long sourceId = FacilioUtil.parseLong(sourceValue);

        Map<String, Object> resultMap = createMap("id", sourceId);
        return resultMap;

    }

    private Object enumToTargetValueConversion() throws Exception {
        switch (targetFieldType) {
            case ENUM:
            case SYSTEM_ENUM:
                return enumToEnumConversion();
            case STRING_SYSTEM_ENUM:
                return enumToStringSystemEnumConversion();
            case LOOKUP:
                return enumToLookupConversion();
            case MULTI_ENUM:
                return enumToMultiEnumConversion();
            case MULTI_LOOKUP:
                return enumToMultiLookupConversion();
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return enumToStringConversion();

        }
        return null;
    }

    private Object enumToStringSystemEnumConversion() {
        return null;
    }

    private Object enumToMultiLookupConversion() throws Exception {
        List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

        if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

            Map<String, Object> desiredMap = getResultForEnum(subFieldMappingList);

            if (desiredMap != null && !desiredMap.isEmpty()) {

                if (desiredMap.get("targetFieldId") != null) {
                    long targetLookupFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                    ArrayList<Map<String, Object>> list = new ArrayList<>();

                    Map<String, Object> resultMap = createMap("id", targetLookupFieldId);

                    list.add(resultMap);

                    return list;
                } else if (desiredMap.get("targetField") != null) {
                    ModuleBaseWithCustomFields record = ModuleMappingsUtil.fetchRecordIdForLookup(String.valueOf(desiredMap.get("targetField")), targetField);

                    if (record != null) {

                        ArrayList<Map<String, Object>> list = new ArrayList<>();

                        Map<String, Object> resultMap = createMap("id", record.getId());

                        list.add(resultMap);

                        return list;

                    }
                    throw new IllegalArgumentException("No record found for module " + targetField.getModule().getName() + " :: " + desiredMap.get("targetField"));

                }
            }
        }

        return null;
    }

    private Object enumToMultiEnumConversion() throws Exception {
        List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

        if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

            Map<String, Object> desiredMap = getResultForEnum(subFieldMappingList);

            if (desiredMap != null && !desiredMap.isEmpty()) {

                long targetEnumFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                ArrayList<Long> list = new ArrayList<>();

                list.add(targetEnumFieldId);

                return list;
            }
        }


        return null;
    }

    private Object enumToEnumConversion() throws Exception {
        List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

        if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

            Map<String, Object> desiredMap = getResultForEnum(subFieldMappingList);

            if (desiredMap != null && !desiredMap.isEmpty()) {

                long targetEnumFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                return targetEnumFieldId;
            }
        }


        return null;

    }

    private Object enumToStringConversion() throws Exception {

        EnumField enumField = (EnumField) sourceField;
        List<EnumFieldValue<Integer>> enumFieldValueList = enumField.getValues();

        for (EnumFieldValue enumFieldValue : enumFieldValueList) {
            if (enumFieldValue.getIndex() == sourceValue) {
                return enumFieldValue.getValue();
            }
        }
        return null;

    }

    private Object enumToLookupConversion() throws Exception {
        List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

        if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

            Map<String, Object> desiredMap = getResultForEnum(subFieldMappingList);

            if (desiredMap.get("targetFieldId") != null) {
                long targetLookupFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                Map<String, Object> resultMap = createMap("id", targetLookupFieldId);
                return resultMap;
            } else if (desiredMap.get("targetField") != null) {
                ModuleBaseWithCustomFields record = ModuleMappingsUtil.fetchRecordIdForLookup(String.valueOf(desiredMap.get("targetField")), targetField);

                if (record != null) {
                    return record.getId();
                }
                throw new IllegalArgumentException("No record found for module " + targetField.getModule().getName() + " :: " + desiredMap.get("targetField"));

            }
        }

        return null;
    }

    private Object lookupToTargetValueConversion() throws Exception {
        switch (targetFieldType) {
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return lookupToStringConversion();
            case LOOKUP:
                return lookupToLookupConversion();
            case MULTI_LOOKUP:
                return lookupToMultiLookupConversion();
            case ENUM:
            case SYSTEM_ENUM:
                return lookupToEnumConversion();
            case MULTI_ENUM:
                return lookupToMultiEnumConversion();

        }
        return null;
    }

    private Object lookupToLookupConversion() throws Exception {

        boolean areFieldsPickList = ModuleMappingValidationUtil.checkFieldsIsPickListType(sourceField, targetField);

        if (areFieldsPickList) {
            return lookupPickListLookupPicklistConversion();

        } else {
            return sourceValue;
        }


    }

    private Object lookupPickListLookupPicklistConversion() throws Exception {
        List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

        if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

            List<Map<String, Object>> desiredMapList = getResultMapForLookup(subFieldMappingList);

            for (Map<String, Object> desiredMap : desiredMapList) {
                if (desiredMap != null && !desiredMap.isEmpty()) {

                    long targetLookupFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                    Map<String, Object> resultMap = createMap("id", targetLookupFieldId);
                    return resultMap;
                }
            }

        }


        return null;
    }

    private Object lookupToMultiLookupConversion() throws Exception {
        boolean areFieldsPickList = ModuleMappingValidationUtil.checkFieldsIsPickListType(sourceField, targetField);

        if (areFieldsPickList) {
            List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

            if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

                List<Map<String, Object>> desiredMapList = getResultMapForLookup(subFieldMappingList);
                ArrayList<Map<String, Object>> list = new ArrayList<>();

                for (Map<String, Object> desiredMap : desiredMapList) {
                    if (desiredMap.get("targetFieldId") != null) {
                        long targetLookupFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                        Map<String, Object> resultMap = createMap("id", targetLookupFieldId);

                        list.add(resultMap);

                    } else if (desiredMap.get("targetField") != null) {
                        ModuleBaseWithCustomFields record = ModuleMappingsUtil.fetchRecordIdForLookup(String.valueOf(desiredMap.get("targetField")), targetField);

                        if (record != null) {

                            Map<String, Object> resultMap = createMap("id", record.getId());

                            list.add(resultMap);

                        }
                        throw new IllegalArgumentException("No record found for module " + targetField.getModule().getName() + " :: " + desiredMap.get("targetField"));

                    }
                }
                return list;

            }
        } else {

            HashMap<String, Object> result = convertToHashMap(sourceValue);

            long targetLookupFieldId = FacilioUtil.parseLong(result.get("id"));

            ArrayList<Map<String, Object>> list = new ArrayList<>();

            Map<String, Object> resultMap = createMap("id", targetLookupFieldId);

            list.add(resultMap);

            return list;
        }

        return null;
    }

    private Object lookupToEnumConversion() throws Exception {

        List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

        if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

            List<Map<String, Object>> desiredMapList = getResultMapForLookup(subFieldMappingList);

            for (Map<String, Object> desiredMap : desiredMapList) {
                if (desiredMap != null && !desiredMap.isEmpty()) {

                    long targetLookupFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                    return targetLookupFieldId;
                }
            }

        }


        return null;

    }

    private Object lookupToMultiEnumConversion() throws Exception {
        List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

        if (subFieldMappingList != null && subFieldMappingList.size() > 0) {

            List<Map<String, Object>> desiredMapList = getResultMapForLookup(subFieldMappingList);

            ArrayList<Long> list = new ArrayList<>();

            for (Map<String, Object> desiredMap : desiredMapList) {
                if (desiredMap != null && !desiredMap.isEmpty()) {

                    long targetLookupFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                    list.add(targetLookupFieldId);
                }
            }
            return list;
        }
        return null;
    }

    private String lookupToStringConversion() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField mainField = getMainFieldOfLookup(sourceField, modBean);

        HashMap<String, Object> result = convertToHashMap(sourceValue);
        return (String) result.get(mainField.getName());

    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> convertToHashMap(Object sourceObject) {
        if (sourceObject instanceof HashMap) {
            return (HashMap<String, Object>) sourceObject;
        } else {
            throw new IllegalArgumentException("Cannot convert Object to HashMap :: " + sourceObject);
        }
    }

    public static FacilioField getMainFieldOfLookup(FacilioField field, ModuleBean modBean) throws Exception {
        if (field != null && field instanceof LookupField && StringUtils.isEmpty(((LookupField) field).getSpecialType())) {
            return modBean.getPrimaryField(((LookupField) field).getLookupModule().getName());
        }
        return null;
    }

    public static FacilioField getMainFieldOfMultiLookup(FacilioField field, ModuleBean modBean) throws Exception {
        if (field != null && field instanceof MultiLookupField && StringUtils.isEmpty(((MultiLookupField) field).getSpecialType())) {
            return modBean.getPrimaryField(((MultiLookupField) field).getLookupModule().getName());
        }
        return null;
    }

    private Object dateTimeToTargetValueConversion() {
        switch (targetFieldType) {
            case DATE_TIME:
            case DATE:
            case NUMBER:
            case DECIMAL:
                return sourceValue;
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return dateTimeToStringConversion();
        }
        return null;
    }

    private Object dateTimeToStringConversion() {
        DateFormatter formatter = new DateFormatter(sourceField);
        return formatter.format(sourceValue);
    }

    private Object booleanToTargetValueConversion() throws Exception {
        switch (targetFieldType) {
            case BOOLEAN:
                return sourceValue;
            case ENUM:
            case MULTI_ENUM:
            case SYSTEM_ENUM:
            case LOOKUP:
            case MULTI_LOOKUP:
                return booleanToSubFieldConversion();
            case STRING:
            case LARGE_TEXT:
            case BIG_STRING:
                return String.valueOf(sourceValue);
            default:
                defaultErrorMsg();
        }
        return null;
    }

    private Object booleanToSubFieldConversion() throws Exception {

        if (FieldTypeConstants.ENUM_FIELD_TYPES.contains(targetFieldType) || targetFieldType == FieldType.STRING_SYSTEM_ENUM) {
            return booleanToEnumConversion();
        } else {
            return booleanToLookupConversion();
        }
    }

    private Object booleanToLookupConversion() throws Exception {

        if (targetFieldType == FieldType.LOOKUP) {

            List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);


            if (subFieldMappingList != null) {

                List<Map<String, Object>> desiredMapList = getResultMap(subFieldMappingList);

                for (Map<String, Object> desiredMap : desiredMapList) {

                    if (desiredMap != null && !desiredMap.isEmpty()) {

                        if (desiredMap.get("targetFieldId") != null) {
                            long targetLookupFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                            Map<String, Object> resultMap = createMap("id", targetLookupFieldId);
                            return resultMap;
                        } else if (desiredMap.get("targetField") != null) {
                            ModuleBaseWithCustomFields record = ModuleMappingsUtil.fetchRecordIdForLookup(String.valueOf(desiredMap.get("targetField")), targetField);

                            if (record != null) {
                                return record.getId();
                            }
                            throw new IllegalArgumentException("No record found for module " + targetField.getModule().getName() + " :: " + desiredMap.get("targetField"));

                        }
                    }
                }
            }
        } else if (targetFieldType == FieldType.MULTI_LOOKUP) {
            List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);


            if (subFieldMappingList != null) {

                List<Map<String, Object>> desiredMapList = getResultMap(subFieldMappingList);

                ArrayList<Map<String, Object>> resultList = new ArrayList<>();

                for (Map<String, Object> desiredMap : desiredMapList) {

                    if (desiredMap != null && !desiredMap.isEmpty()) {

                        if (desiredMap.get("targetFieldId") != null) {
                            long targetLookupFieldId = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                            Map<String, Object> resultMap = createMap("id", targetLookupFieldId);

                            resultList.add(resultMap);

                        } else if (desiredMap.get("targetField") != null) {
                            ModuleBaseWithCustomFields record = ModuleMappingsUtil.fetchRecordIdForLookup(String.valueOf(desiredMap.get("targetField")), targetField);

                            if (record != null) {
                                Map<String, Object> resultMap = createMap("id", record.getId());

                                resultList.add(resultMap);

                            }
                            throw new IllegalArgumentException("No record found for module " + targetField.getModule().getName() + " :: " + desiredMap.get("targetField"));

                        }
                    }
                }
                return resultList;
            }
        }

        return null;
    }

    private Object booleanToEnumConversion() throws Exception {

        if (targetFieldType == FieldType.MULTI_ENUM) {

            List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

            if (subFieldMappingList != null) {

                ArrayList<Long> targetEnumFieldList = new ArrayList<>();

                List<Map<String, Object>> desiredMapList = getResultMap(subFieldMappingList);

                for (Map<String, Object> desiredMap : desiredMapList) {

                    if (desiredMap != null && !desiredMap.isEmpty()) {

                        long targetEnumFieldIndex = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                        targetEnumFieldList.add(targetEnumFieldIndex);

                    }
                }
                return targetEnumFieldList;
            } else {
                throw new IllegalArgumentException("Sub Field mapping cannot be null for field mapping id :: " + id);
            }
        } else if (targetFieldType == FieldType.ENUM || targetFieldType == FieldType.SYSTEM_ENUM || targetFieldType == FieldType.STRING_SYSTEM_ENUM) {

            List<Map<String, Object>> subFieldMappingList = getSubFieldMapFromFieldMapping(id);

            if (subFieldMappingList != null) {

                List<Map<String, Object>> desiredMapList = getResultMap(subFieldMappingList);

                for (Map<String, Object> desiredMap : desiredMapList) {

                    if (desiredMap != null && !desiredMap.isEmpty()) {

                        long targetEnumFieldIndex = FacilioUtil.parseLong(desiredMap.get("targetFieldId"));

                        return targetEnumFieldIndex;
                    }
                }
            } else {
                throw new IllegalArgumentException("Sub Field mapping cannot be null for field mapping id :: " + id);
            }
        }

        return null;
    }

    private static Map<String, Object> createMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private List<Map<String, Object>> getResultMap(List<Map<String, Object>> subFieldMappingList) {
        List<Map<String, Object>> resultMap = new ArrayList<>();
        for (Map<String, Object> map : subFieldMappingList) {
            if (Boolean.toString((Boolean) sourceValue).equals(map.get("sourceField"))) {
                resultMap.add(map);
            }
        }
        return resultMap;
    }

    private Map<String, Object> getResultForEnum(List<Map<String, Object>> subFieldMappingList) {
        for (Map<String, Object> map : subFieldMappingList) {

            long sourceFieldResultId = FacilioUtil.parseLong(map.get("sourceFieldId"));

            if (FacilioUtil.parseLong(sourceValue) == sourceFieldResultId) {
                return map;
            }
        }
        return null;
    }

    private List<Map<String, Object>> getResultMapForLookup(List<Map<String, Object>> subFieldMappingList) {
        HashMap<String, Object> result = convertToHashMap(sourceValue);

        List<Map<String, Object>> resultMap = new ArrayList<>();

        long sourceSubFieldResultId = (long) result.get("id");

        for (Map<String, Object> map : subFieldMappingList) {

            long sourceFieldResultId = FacilioUtil.parseLong(map.get("sourceFieldId"));

            if (sourceSubFieldResultId == sourceFieldResultId) {
                resultMap.add(map);
            }
        }
        return resultMap;
    }

    private List<Map<String, Object>> getResultMapForMultiLookup(List<Map<String, Object>> subFieldMappingList) {

        List<HashMap<String, Object>> resultList = (List<HashMap<String, Object>>) sourceValue;

        List<Map<String, Object>> resultMap = new ArrayList<>();

        for (Map<String, Object> result : resultList) {

            long sourceSubFieldResultId = (long) result.get("id");

            for (Map<String, Object> map : subFieldMappingList) {

                long sourceFieldResultId = FacilioUtil.parseLong(map.get("sourceFieldId"));

                if (sourceSubFieldResultId == sourceFieldResultId) {
                    resultMap.add(map);
                }
            }
        }
        return resultMap;

    }

    private Object decimalToTargetValueConversion() {
        switch (targetFieldType) {
            case NUMBER:
                return decimalToNumberConversion();
            case DECIMAL:
                return sourceValue;
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return String.valueOf(sourceValue);
            default:
                defaultErrorMsg();
        }
        return null;
    }

    private long decimalToNumberConversion() {
        return FacilioUtil.parseLong(sourceValue);
    }

    private Object numberToTargetValueConversion() throws Exception {
        switch (targetFieldType) {
            case NUMBER:
                return FacilioUtil.parseLong(sourceValue);
            case DECIMAL:
                return numberToDecimalConversion();
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return String.valueOf(sourceValue);
            case LOOKUP:
            case MULTI_LOOKUP:
                return numberToLookupConversion(FacilioUtil.parseLong(sourceValue));
            default:
                defaultErrorMsg();
        }
        return null;
    }

    private Object numberToLookupConversion(long sourceValue) throws Exception {
        if (targetFieldType == FieldType.LOOKUP) {
            FieldMappingValidationUtil fieldMappingValidationUtil = new FieldMappingValidationUtil(sourceFieldType, targetFieldType, sourceField, targetField);
            fieldMappingValidationUtil.isNumberFieldCompatibleForLookup();

            Map<String, Object> resultMap = createMap("id", sourceValue);
            return resultMap;
        } else if (targetFieldType == FieldType.MULTI_LOOKUP) {
            FieldMappingValidationUtil fieldMappingValidationUtil = new FieldMappingValidationUtil(sourceFieldType, targetFieldType, sourceField, targetField);
            fieldMappingValidationUtil.isNumberFieldCompatibleForLookup();

            ArrayList<Map<String, Object>> list = new ArrayList<>();

            Map<String, Object> resultMap = createMap("id", sourceValue);

            list.add(resultMap);

            return list;
        }
        return null;

    }

    private double numberToDecimalConversion() {
        return FacilioUtil.parseDouble(sourceValue);
    }

    private void defaultErrorMsg() {
        throw new IllegalArgumentException("We cannot convert " + sourceField + " to " + targetField);
    }

    private String stringToTargetValueConversion() {
        switch (targetFieldType) {
            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                return String.valueOf(sourceValue);
            default:
                defaultErrorMsg();
                break;
        }
        return null;
    }

    private Object sourceToTargetValueConversion() {
        return sourceValue;
    }


    protected static List<Map<String, Object>> getSubFieldMapFromFieldMapping(long fieldMappingId) throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getSubFieldMappingModule().getTableName())
                .select(FieldFactory.getSubFieldMappingFields())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(fieldMappingId), NumberOperators.EQUALS));

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


}
