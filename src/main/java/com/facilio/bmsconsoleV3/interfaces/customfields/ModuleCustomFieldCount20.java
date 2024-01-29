package com.facilio.bmsconsoleV3.interfaces.customfields;

import com.facilio.modules.FieldType;
import org.apache.commons.collections4.map.HashedMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ModuleCustomFieldCount20 implements ModuleCustomFieldsCount {

    static final Map<Integer, String[]> typeVsCustomColumns = Collections.unmodifiableMap(initCustomFieldsMap());

    static Map<Integer, String[]> initCustomFieldsMap() {
        Map<Integer, String[]> customFieldsMap = new HashedMap<>();
        customFieldsMap.put(1, new String[]{"STRING_CF1", "STRING_CF2", "STRING_CF3", "STRING_CF4", "STRING_CF5", "STRING_CF6", "STRING_CF7", "STRING_CF8", "STRING_CF9", "STRING_CF10", "STRING_CF11", "STRING_CF12", "STRING_CF3", "STRING_CF14", "STRING_CF15", "STRING_CF16", "STRING_CF17", "STRING_CF18", "STRING_CF19", "STRING_CF20"});
        customFieldsMap.put(2, new String[]{"NUMBER_CF1", "NUMBER_CF2", "NUMBER_CF3", "NUMBER_CF4", "NUMBER_CF5", "NUMBER_CF6", "NUMBER_CF7", "NUMBER_CF8", "NUMBER_CF9", "NUMBER_CF10", "NUMBER_CF11", "NUMBER_CF12", "NUMBER_CF13", "NUMBER_CF14", "NUMBER_CF15", "NUMBER_CF16", "NUMBER_CF17", "NUMBER_CF18", "NUMBER_CF19", "NUMBER_CF20"});
        customFieldsMap.put(3, new String[]{"DECIMAL_CF1", "DECIMAL_CF2", "DECIMAL_CF3", "DECIMAL_CF4", "DECIMAL_CF5", "DECIMAL_CF6", "DECIMAL_CF7", "DECIMAL_CF8", "DECIMAL_CF9", "DECIMAL_CF10", "DECIMAL_CF11", "DECIMAL_CF12", "DECIMAL_CF13", "DECIMAL_CF14", "DECIMAL_CF15", "DECIMAL_CF16", "DECIMAL_CF17", "DECIMAL_CF18", "DECIMAL_CF19", "DECIMAL_CF20"});
        customFieldsMap.put(4, new String[]{"BOOLEAN_CF1", "BOOLEAN_CF2", "BOOLEAN_CF3", "BOOLEAN_CF4", "BOOLEAN_CF5", "BOOLEAN_CF6", "BOOLEAN_CF7", "BOOLEAN_CF8", "BOOLEAN_CF9", "BOOLEAN_CF10", "BOOLEAN_CF11", "BOOLEAN_CF12", "BOOLEAN_CF13", "BOOLEAN_CF14", "BOOLEAN_CF15", "BOOLEAN_CF16", "BOOLEAN_CF17", "BOOLEAN_CF18", "BOOLEAN_CF19", "BOOLEAN_CF20"});
        customFieldsMap.put(5, new String[]{"DATE_CF1", "DATE_CF2", "DATE_CF3", "DATE_CF4", "DATE_CF5", "DATE_CF6", "DATE_CF7", "DATE_CF8", "DATE_CF9", "DATE_CF10", "DATE_CF11", "DATE_CF12", "DATE_CF13", "DATE_CF14", "DATE_CF15", "DATE_CF16", "DATE_CF17", "DATE_CF18", "DATE_CF19", "DATE_CF20"});
        customFieldsMap.put(6, new String[]{"DATETIME_CF1", "DATETIME_CF2", "DATETIME_CF3", "DATETIME_CF4", "DATETIME_CF5", "DATETIME_CF6", "DATETIME_CF7", "DATETIME_CF8", "DATETIME_CF9", "DATETIME_CF10", "DATETIME_CF11", "DATETIME_CF12", "DATETIME_CF13", "DATETIME_CF14", "DATETIME_CF15", "DATETIME_CF16", "DATETIME_CF17", "DATETIME_CF18", "DATETIME_CF19", "DATETIME_CF20"});
        customFieldsMap.put(7, new String[]{"LOOKUP_CF1", "LOOKUP_CF2", "LOOKUP_CF3", "LOOKUP_CF4", "LOOKUP_CF5", "LOOKUP_CF6", "LOOKUP_CF7", "LOOKUP_CF8", "LOOKUP_CF9", "LOOKUP_CF10", "LOOKUP_CF11", "LOOKUP_CF12", "LOOKUP_CF13", "LOOKUP_CF14", "LOOKUP_CF15", "LOOKUP_CF16", "LOOKUP_CF17", "LOOKUP_CF18", "LOOKUP_CF19", "LOOKUP_CF20"});
        customFieldsMap.put(8, new String[]{"ENUM_CF1", "ENUM_CF2", "ENUM_CF3", "ENUM_CF4", "ENUM_CF5", "ENUM_CF6", "ENUM_CF7", "ENUM_CF8", "ENUM_CF9", "ENUM_CF10", "ENUM_CF11", "ENUM_CF12", "ENUM_CF13", "ENUM_CF14", "ENUM_CF15", "ENUM_CF16", "ENUM_CF17", "ENUM_CF18", "ENUM_CF19", "ENUM_CF20"});
        customFieldsMap.put(9, new String[]{"FILE_CF1", "FILE_CF2", "FILE_CF3", "FILE_CF4", "FILE_CF5", "FILE_CF6", "FILE_CF7", "FILE_CF8", "FILE_CF9", "FILE_CF10", "FILE_CF11", "FILE_CF12", "FILE_CF13", "FILE_CF14", "FILE_CF15", "FILE_CF16", "FILE_CF17", "FILE_CF18", "FILE_CF19", "FILE_CF20"});
        customFieldsMap.put(15, new String[]{"SCORE_CF1", "SCORE_CF2", "SCORE_CF3", "SCORE_CF4", "SCORE_CF5", "SCORE_CF6", "SCORE_CF7", "SCORE_CF8", "SCORE_CF9", "SCORE_CF10", "SCORE_CF11", "SCORE_CF12", "SCORE_CF13", "SCORE_CF14", "SCORE_CF15", "SCORE_CF16", "SCORE_CF17", "SCORE_CF18", "SCORE_CF19", "SCORE_CF20"});
        customFieldsMap.put(18, new String[]{"BIGSTRING_CF1","BIGSTRING_CF2","BIGSTRING_CF3"});
        customFieldsMap.put(24,new String[]{"AUTO_NUMBER_CF1"});

        return customFieldsMap;
    }

    @Override
    public String getNewColumnNameForFieldType(FieldType fieldType, List<String> existingColumns) {
        return ModuleCustomFieldsCount.getColumnName(fieldType, typeVsCustomColumns, existingColumns);
    }
}
