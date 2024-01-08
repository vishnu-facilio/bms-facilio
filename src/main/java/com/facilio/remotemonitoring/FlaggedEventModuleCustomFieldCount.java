package com.facilio.remotemonitoring;

import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldsCount;
import com.facilio.modules.FieldType;
import org.apache.commons.collections4.map.HashedMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FlaggedEventModuleCustomFieldCount implements ModuleCustomFieldsCount {


    static final Map<Integer, String[]> typeVsCustomColumns = Collections.unmodifiableMap(initCustomFieldsMap());

    static Map<Integer, String[]> initCustomFieldsMap() {
        Map<Integer, String[]> customFieldsMap = new HashedMap<>();
        customFieldsMap.put(1, new String[]{"STRING_CF1", "STRING_CF2", "STRING_CF3", "STRING_CF4", "STRING_CF5", "STRING_CF6", "STRING_CF7", "STRING_CF8", "STRING_CF9", "STRING_CF10"});
        customFieldsMap.put(2, new String[]{"NUMBER_CF1", "NUMBER_CF2", "NUMBER_CF3", "NUMBER_CF4", "NUMBER_CF5", "NUMBER_CF6", "NUMBER_CF7", "NUMBER_CF8", "NUMBER_CF9", "NUMBER_CF10"});
        customFieldsMap.put(3, new String[]{"DECIMAL_CF1", "DECIMAL_CF2", "DECIMAL_CF3", "DECIMAL_CF4", "DECIMAL_CF5", "DECIMAL_CF6", "DECIMAL_CF7", "DECIMAL_CF8", "DECIMAL_CF9", "DECIMAL_CF10"});
        customFieldsMap.put(4, new String[]{"BOOLEAN_CF1", "BOOLEAN_CF2", "BOOLEAN_CF3", "BOOLEAN_CF4", "BOOLEAN_CF5", "BOOLEAN_CF6", "BOOLEAN_CF7", "BOOLEAN_CF8", "BOOLEAN_CF9", "BOOLEAN_CF10"});
        customFieldsMap.put(5, new String[]{"DATE_CF1", "DATE_CF2", "DATE_CF3", "DATE_CF4", "DATE_CF5", "DATE_CF6", "DATE_CF7", "DATE_CF8", "DATE_CF9", "DATE_CF10"});
        customFieldsMap.put(6, new String[]{"DATETIME_CF1", "DATETIME_CF2", "DATETIME_CF3", "DATETIME_CF4", "DATETIME_CF5", "DATETIME_CF6", "DATETIME_CF7", "DATETIME_CF8", "DATETIME_CF9", "DATETIME_CF10"});
        customFieldsMap.put(7, new String[]{"LOOKUP_CF1", "LOOKUP_CF2", "LOOKUP_CF3", "LOOKUP_CF4", "LOOKUP_CF5", "LOOKUP_CF6", "LOOKUP_CF7", "LOOKUP_CF8", "LOOKUP_CF9", "LOOKUP_CF10"});
        customFieldsMap.put(8, new String[]{"ENUM_CF1", "ENUM_CF2", "ENUM_CF3", "ENUM_CF4", "ENUM_CF5", "ENUM_CF6", "ENUM_CF7", "ENUM_CF8", "ENUM_CF9", "ENUM_CF10"});
        customFieldsMap.put(9, new String[]{"FILE_CF1", "FILE_CF2", "FILE_CF3", "FILE_CF4", "FILE_CF5", "FILE_CF6", "FILE_CF7", "FILE_CF8", "FILE_CF9", "FILE_CF10"});
        customFieldsMap.put(15, new String[]{"SCORE_CF1", "SCORE_CF2", "SCORE_CF3", "SCORE_CF4", "SCORE_CF5", "SCORE_CF6", "SCORE_CF7", "SCORE_CF8", "SCORE_CF9", "SCORE_CF10"});
        return customFieldsMap;
    }

    @Override
    public String getNewColumnNameForFieldType(FieldType fieldType, List<String> existingColumns) {
        return ModuleCustomFieldsCount.getColumnName(fieldType, typeVsCustomColumns, existingColumns);
    }
}
