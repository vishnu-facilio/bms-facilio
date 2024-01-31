package com.facilio.bmsconsoleV3.enums;

import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.*;

@Log4j
public enum FieldTypeOcrValueProcessor {
    DATE {
        @Override
        public Object process(FacilioField field, String value, String dateFormat) throws Exception {
            if(value == null){
                throw new Exception("Value is empty for date field " + field.getDisplayName());
            }

            try {
                return FieldTypeOcrValueProcessor.processDateTypeData(field, value, dateFormat);
            }catch(Exception e){
                LOGGER.error("Error occurred in type casting date field ", e);
                throw new Exception("Invalid data format for " + dateFormat);
            }
        }
    },
    DATE_TIME {
        @Override
        public Object process(FacilioField field, String value, String dateFormat) throws Exception {
            if(value == null){
                throw new Exception("Value is empty for date time field " + field.getDisplayName());
            }

            try {
                return FieldTypeOcrValueProcessor.processDateTypeData(field, value, dateFormat);
            }catch(Exception e){
                LOGGER.error("Error occurred in type casting date time field ", e);
                throw new Exception("Invalid data time format for " + dateFormat);
            }
        }
    },
    SYSTEM_ENUM {
        @Override
        public Object process(FacilioField field, String enumString, String dateFormat) throws Exception {
            if(enumString == null){
                throw new Exception("Value is empty for enum field " + field.getDisplayName());
            }

            SystemEnumField enumField = (SystemEnumField) field;
            int enumIndex = enumField.getIndex(enumString);

            if(enumIndex >= 0) {
                return enumIndex;
            }else {
                throw new Exception("Enum value not found for " + enumString);
            }
        }
    },
    ENUM {
        @Override
        public Object process(FacilioField field, String enumString, String dateFormat) throws Exception {
            if(enumString == null){
                throw new Exception("Value is empty for enum field " + field.getDisplayName());
            }

            EnumField enumField = (EnumField) field;
            int enumIndex = enumField.getIndex(enumString);

            if(enumIndex >= 0) {
                return enumIndex;
            }else {
                throw new Exception("Enum value not found for " + enumString);
            }
        }
    },
    MULTI_ENUM {
        @Override
        public Object process(FacilioField field, String enumString, String dateFormat) throws Exception {
            if(enumString == null){
                throw new Exception("Value is empty for multi enum field " + field.getDisplayName());
            }

            MultiEnumField multiEnumField = (MultiEnumField) field;
            List<String> failedEnums = new ArrayList<>();
            ArrayList enumIndices = new ArrayList();
            if (StringUtils.isNotEmpty(enumString)) {
                for (String string : FacilioUtil.splitByComma(enumString)) {
                    int enumIndex = multiEnumField.getIndex(string);
                    if (enumIndex > 0) {
                        enumIndices.add(enumIndex);
                    }else {
                        failedEnums.add(string);
                    }
                }
            }

            if(CollectionUtils.isEmpty(failedEnums)){
                return enumIndices;
            }else{
                throw new Exception("Enum value not found for " + String.join(",", failedEnums));
            }
        }
    },
    MULTI_LOOKUP {
        @Override
        public Object process(FacilioField field, String value, String dateFormat) throws Exception {

            return value;
        }
    },
    LOOKUP {
        @Override
        public Object process(FacilioField field, String value, String dateFormat) throws Exception {
            return value;
        }
    },
    NUMBER {
        @Override
        public Object process(FacilioField field, String value, String dateFormat) throws Exception {
            if(value == null){
                throw new Exception("Value is empty for number field " + field.getDisplayName());
            }

            if (value.contains(",")) {
                value = value.replaceAll(",", "");
            }
            try {
                return (long) Double.parseDouble(value);
            }catch (Exception e){
                LOGGER.error("Error occurred in type casting number field ", e);
                throw new Exception(value + " Can not be mapped to number field " + field.getDisplayName());
            }
        }
    },
    DECIMAL {
        @Override
        public Object process(FacilioField field, String value, String dateFormat) throws Exception {
            if(value == null){
                throw new Exception("Value is empty for decimal field " + field.getDisplayName());
            }

            if (value.contains(",")) {
                value = value.replaceAll(",", "");
            }

            try {
                return Double.parseDouble(value);
            }catch(Exception e){
                LOGGER.error("Error occurred in type casting decimal field ", e);
                throw new Exception(value + " Can not be mapped to decimal field " + field.getDisplayName());
            }
        }
    },
    BOOLEAN {
        @Override
        public Object process(FacilioField field, String value, String dateFormat) throws Exception {
            if(value == null){
                throw new Exception("Value is empty for boolean field " + field.getDisplayName());
            }

            try {
                return FacilioUtil.parseBoolean(value);
            } catch(Exception e){
                LOGGER.error("Error occurred in type casting boolean field ", e);
                return null;
            }
        }
    },
    ID {
        @Override
        public Object process(FacilioField field, String value, String dateFormat) throws Exception {
            if(value == null){
                throw new Exception("Value is empty for id field " + field.getDisplayName());
            }

            return (long) Double.parseDouble(value);
        }
    },
    STRING {
        @Override
        public Object process(FacilioField field, String value, String dateFormat) throws Exception {
            return value;
        }
    },
    DEFAULT;

    public Object process(FacilioField field, String value, String dateFormat) throws Exception {
        return value;
    }

    private static Object processDateTypeData(FacilioField field, String value, String dateFormat){
        long millis;
        if (dateFormat == null) {
            throw new IllegalArgumentException("date format cannot be null");
        }
        if (dateFormat.equals(MultiImportApi.ImportProcessConstants.TIME_STAMP_STRING)) {
            millis = new Double(value).longValue();
        } else {
            Instant dateInstant = DateTimeUtil.getTimeInstant(dateFormat, value);
            millis = dateInstant.toEpochMilli();
        }
       return millis;
    }

    private static final Map<String,FieldTypeOcrValueProcessor> FIELD_TYPE_OCR_VALUE_PROCESSOR_MAP = Collections.unmodifiableMap(init());

    public static FieldTypeOcrValueProcessor getFieldTypeOcrValueProcessor(FieldType fieldType){
        if(FIELD_TYPE_OCR_VALUE_PROCESSOR_MAP.containsKey(fieldType.name())){
            return FIELD_TYPE_OCR_VALUE_PROCESSOR_MAP.get(fieldType.name());
        }
        return DEFAULT;
    }

    private static Map<String,FieldTypeOcrValueProcessor> init(){
        Map<String,FieldTypeOcrValueProcessor> map = new HashMap<>();
        for(FieldTypeOcrValueProcessor FieldTypeOcrValueProcessor : FieldTypeOcrValueProcessor.values()){
            map.put(FieldTypeOcrValueProcessor.name(),FieldTypeOcrValueProcessor);
        }
        return map;
    }
}
