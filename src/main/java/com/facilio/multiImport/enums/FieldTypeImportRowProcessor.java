package com.facilio.multiImport.enums;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.*;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.multiImportExceptions.ImportLookupModuleValueNotFoundException;
import com.facilio.multiImport.util.LoadLookupHelper;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.facilio.multiImport.util.LoadLookupHelper.VALUES_SEPARATOR;

public enum FieldTypeImportRowProcessor {
    DATE {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext,ImportRowContext rowContext,
                            FacilioField field, Object cellValue,
                            HashMap<String, Object> props, LoadLookupHelper lookupHelper) throws Exception {

           FieldTypeImportRowProcessor.processDateTypeData(importFieldMappingContext,field,cellValue,props);
        }

        @Override
        public boolean isSupportedFieldTypeForHistory() {
            return true;
        }
    },
    DATE_TIME {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext,ImportRowContext rowContext,
                            FacilioField field, Object cellValue,
                            HashMap<String, Object> props,LoadLookupHelper lookupHelper) throws Exception {

            FieldTypeImportRowProcessor.processDateTypeData(importFieldMappingContext,field,cellValue,props);
        }
        @Override
        public boolean isSupportedFieldTypeForHistory() {
            return true;
        }
    },
    SYSTEM_ENUM {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext,ImportRowContext rowContext,
                            FacilioField field, Object cellValue,
                            HashMap<String, Object> props,LoadLookupHelper lookupHelper) throws Exception {

            SystemEnumField enumField = (SystemEnumField) field;
            String enumString = (String) cellValue;
            int enumIndex = enumField.getIndex(enumString);

            if (!props.containsKey(field.getName())) {
                props.put(field.getName(), enumIndex);
            }
        }
        @Override
        public boolean isSupportedFieldTypeForHistory() {
            return true;
        }
    },
    ENUM {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext,ImportRowContext rowContext,
                            FacilioField field, Object cellValue,
                            HashMap<String, Object> props,LoadLookupHelper lookupHelper) throws Exception {

            EnumField enumField = (EnumField) field;
            String enumString = (String) cellValue;
            int enumIndex = enumField.getIndex(enumString);

            if (!props.containsKey(field.getName())) {
                props.put(field.getName(), enumIndex);
            }
        }
        @Override
        public boolean isSupportedFieldTypeForHistory() {
            return true;
        }
    },
    MULTI_ENUM {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext,ImportRowContext rowContext,
                            FacilioField field, Object cellValue,
                            HashMap<String, Object> props,LoadLookupHelper lookupHelper) throws Exception {

            MultiEnumField multiEnumField = (MultiEnumField) field;
            String enumString = (String) cellValue;
            ArrayList enumIndices = new ArrayList();
            if (StringUtils.isNotEmpty(enumString)) {
                for (String string : FacilioUtil.splitByComma(enumString)) {
                    int enumIndex = multiEnumField.getIndex(string);
                    if (enumIndex > 0) {
                        enumIndices.add(enumIndex);
                    }
                }
            }
            if (!props.containsKey(field.getName())) {
                props.put(field.getName(), enumIndices);
            }

        }
    },
    MULTI_LOOKUP {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext, ImportRowContext rowContext,
                            FacilioField field, Object cellValue,
                            HashMap<String, Object> props,LoadLookupHelper lookupHelper) throws Exception {

            String sheetColumnName = importFieldMappingContext.getSheetColumnName();
            String multiLookupValue = lookupHelper.getLookupIdentifierData((BaseLookupField) field,cellValue.toString().trim());
            String[] split = multiLookupValue.split(",");
            List<Object> values = new ArrayList<>();

            String lookUpModuleDisplayName = ((BaseLookupField) field).getLookupModule().getDisplayName();
            List<FacilioField> uniqueFields = lookupHelper.getImportLookupUniqueFields((BaseLookupField) field);
            Map<String, Map<String, Object>> nameVsIds = lookupHelper.getLookupMap().get(field);
            for (String s : split) {
                String name = s.trim();
                String lookUpValueKey = lookupHelper.getLookUKeyValueFromSheet((BaseLookupField) field,uniqueFields,rowContext, true);

                if (!lookUpValueKey.isEmpty()) {
                    name = name + VALUES_SEPARATOR + lookUpValueKey;
                }
                Object value = nameVsIds.get(name);

                if(value == null && !lookupHelper.getSkipLookupNotFoundExceptionFields().contains(field.getName())){
                    throw new ImportLookupModuleValueNotFoundException(lookUpModuleDisplayName,sheetColumnName,null);
                }

                if (nameVsIds.containsKey(name) && nameVsIds.get(name) != null) {
                    values.add(nameVsIds.get(name));
                }
            }
            props.put(field.getName(), values);
        }
    },
    LOOKUP {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext,ImportRowContext rowContext,
                            FacilioField field, Object cellValue, HashMap<String, Object> props,LoadLookupHelper lookupHelper) throws Exception {

            String sheetColumnName = importFieldMappingContext.getSheetColumnName();
            String lookUpModuleName = ((BaseLookupField) field).getLookupModule().getName();
            Map<String, Map<String, Object>> nameVsIds = lookupHelper.getLookupMap().get(field);
            List<FacilioField> uniqueFields = lookupHelper.getImportLookupUniqueFields((BaseLookupField) field);
            String lookUpValueKey = lookupHelper.getLookUKeyValueFromSheet((BaseLookupField) field,uniqueFields,rowContext, true);
            String name = lookupHelper.getLookupIdentifierData((BaseLookupField) field,cellValue.toString().trim());
            if (!lookUpValueKey.isEmpty()) {
                name = name + VALUES_SEPARATOR + lookUpValueKey;
            }
            Object value = nameVsIds.get(name);

            if(value == null && !lookupHelper.getSkipLookupNotFoundExceptionFields().contains(field.getName())){ //lookup record not in DP and not in Sheet means mark error
                throw new ImportLookupModuleValueNotFoundException(lookUpModuleName,sheetColumnName,null);
            }

            props.put(field.getName(), value);
        }
        @Override
        public boolean isSupportedFieldTypeForHistory() {
            return true;
        }
    },
    NUMBER {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext,ImportRowContext rowContext,
                            FacilioField field, Object cellValue,
                            HashMap<String, Object> props,LoadLookupHelper lookupHelper) throws Exception {

            String cellValueString = cellValue.toString();
            if (cellValueString.contains(",")) {
                cellValueString = cellValueString.replaceAll(",", "");
            }

            long cellLongValue =(long)Double.parseDouble(cellValueString);
            if (!props.containsKey(field.getName())) {
                props.put(field.getName(), cellLongValue);
            }
            NumberField numberField = (NumberField) field;
            if(numberField.getMetric()!=-1 && numberField.getMetricEnum()!=null && importFieldMappingContext.getUnitId()!=-1){
                props.put(field.getName()+"Unit",importFieldMappingContext.getUnitId());
            }
        }
        @Override
        public boolean isSupportedFieldTypeForHistory() {
            return true;
        }

    },
    DECIMAL {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext,ImportRowContext rowContext,
                            FacilioField field, Object cellValue,
                            HashMap<String, Object> props,LoadLookupHelper lookupHelper) throws Exception {

            String cellValueString = cellValue.toString();
            if (cellValueString.contains(",")) {
                cellValueString = cellValueString.replaceAll(",", "");
            }

            Double cellDoubleValue = Double.parseDouble(cellValueString);
            if (!props.containsKey(field.getName())) {
                props.put(field.getName(), cellDoubleValue);
            }
            NumberField decimalField = (NumberField) field;
            if(decimalField.getMetric()!=-1 && decimalField.getMetricEnum()!=null && importFieldMappingContext.getUnitId()!=-1){
                props.put(field.getName()+"Unit",importFieldMappingContext.getUnitId());
            }

        }
        @Override
        public boolean isSupportedFieldTypeForHistory() {
            return true;
        }
    },
    BOOLEAN {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext,ImportRowContext rowContext,
                            FacilioField field, Object cellValue,
                            HashMap<String, Object> props,LoadLookupHelper lookupHelper) throws Exception {

            String cellValueString = cellValue.toString();
            boolean booleanValue = FacilioUtil.parseBoolean(cellValueString);
            props.put(field.getName(), booleanValue);
        }
        @Override
        public boolean isSupportedFieldTypeForHistory() {
            return true;
        }

    },
    ID {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext,ImportRowContext rowContext,
                            FacilioField field, Object cellValue,
                            HashMap<String, Object> props,LoadLookupHelper lookupHelper) throws Exception {

            String cellValueString = cellValue.toString();
            long id = (long) Double.parseDouble(cellValueString);
            props.put(field.getName(), id);
        }
    },
    STRING {
        @Override
        public void process(ImportFieldMappingContext importFieldMappingContext,ImportRowContext rowContext,
                            FacilioField field, Object cellValue,
                            HashMap<String, Object> props,LoadLookupHelper lookupHelper) throws Exception {
            if(field.getDisplayType()== FacilioField.FieldDisplayType.EMAIL){
                String cellValueString = cellValue.toString();
                String regex = FacilioConstants.FormContextNames.EMAIL_REGEX;
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(cellValueString);
                FacilioUtil.throwIllegalArgumentException(!matcher.matches(), "Invalid Email");
            }

            if(cellValue instanceof Double){
                String cellValueString = cellValue.toString();
                Long number = (long) Double.parseDouble(cellValueString);
                props.put(field.getName(), number.toString());
            }else {
                props.put(field.getName(), cellValue);
            }
        }
        @Override
        public boolean isSupportedFieldTypeForHistory() {
            return true;
        }
    },
    DEFAULT;

    public void process(ImportFieldMappingContext importFieldMappingContext,
                        ImportRowContext rowContext,
                        FacilioField field,
                        Object cellValue,
                        HashMap<String, Object> props, LoadLookupHelper lookupHelper) throws Exception {

        if (!props.containsKey(field.getName())){
            props.put(field.getName(), cellValue);
        }

    }

    private static void processDateTypeData(ImportFieldMappingContext importFieldMappingContext,
                                            FacilioField field,
                                            Object cellValue,
                                            HashMap<String, Object> props){
        long millis;
        if (!(cellValue instanceof Number)) {
            String dateFormat = importFieldMappingContext.getDateFormat();
            if (dateFormat == null) {
                throw new IllegalArgumentException("date format cannot be null");
            }
            if (dateFormat.equals(MultiImportApi.ImportProcessConstants.TIME_STAMP_STRING)) {
                millis = new Double(cellValue.toString()).longValue();
            } else {
                Instant dateInstant = DateTimeUtil.getTimeInstant(dateFormat, cellValue.toString());
                millis = dateInstant.toEpochMilli();
            }
        } else {
            millis = (long) Double.parseDouble(cellValue.toString());
        }
        if (!props.containsKey(field.getName())) {
            props.put(field.getName(), millis);
        }
    }

    private static final Map<String,FieldTypeImportRowProcessor> FIELD_TYPE_IMPORT_ROW_PROCESSOR_MAP = Collections.unmodifiableMap(init());

    public static FieldTypeImportRowProcessor getFieldTypeImportRowProcessor(FieldType fieldType){
        if(FIELD_TYPE_IMPORT_ROW_PROCESSOR_MAP.containsKey(fieldType.name())){
            return FIELD_TYPE_IMPORT_ROW_PROCESSOR_MAP.get(fieldType.name());
        }
        return DEFAULT;
    }
    private static Map<String,FieldTypeImportRowProcessor> init(){
        Map<String,FieldTypeImportRowProcessor> map = new HashMap<>();
        for(FieldTypeImportRowProcessor fieldTypeImportRowProcessor : FieldTypeImportRowProcessor.values()){
            map.put(fieldTypeImportRowProcessor.name(),fieldTypeImportRowProcessor);
        }
        return map;
    }

    public boolean isSupportedFieldTypeForHistory(){
        return false;
    }
    public List<UpdateChangeSet> constructChangeSet(long recordId,FacilioField field,Object oldValue,Object newValue){
        if(!isSupportedFieldTypeForHistory() || isUnsupportedFieldName(field.getName())){
            return null;
        }
        UpdateChangeSet currentChange = new UpdateChangeSet();
        currentChange.setFieldId(field.getFieldId());
        currentChange.setOldValue(oldValue);
        currentChange.setNewValue(newValue);
        currentChange.setRecordId(recordId);
        return Collections.singletonList(currentChange);
    }

    private static final List<String> UN_SUPPORTED_FIELD_NAMES = Collections.unmodifiableList(Arrays.asList(new String[]{"currencyCode","exchangeRate"}));
    private static boolean isUnsupportedFieldName(String fieldName){
        if(UN_SUPPORTED_FIELD_NAMES.contains(fieldName) || fieldName.endsWith("##baseCurrencyValue")){
            return true;
        }
        return false;
    }

    public static List<FacilioField> getExtraSelectableFieldsForHistory(List<FacilioField> patchFields){
        return patchFields.stream()
                .filter(field->getFieldTypeImportRowProcessor(field.getDataTypeEnum()).isSupportedFieldTypeForHistory())
                .collect(Collectors.toList());
    }

}
