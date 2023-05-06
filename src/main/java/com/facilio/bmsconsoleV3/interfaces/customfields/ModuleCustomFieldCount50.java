package com.facilio.bmsconsoleV3.interfaces.customfields;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

public class ModuleCustomFieldCount50 implements ModuleCustomFieldsCount {


    static final Map<Integer, String[]> typeVsCustomColumns = Collections.unmodifiableMap(initCustomFieldsMap());

    static Map<Integer, String[]> initCustomFieldsMap() {
        Map<Integer, String[]> customFieldsMap = new HashedMap<>();
        customFieldsMap.put(1, new String[]{"STRING_CF1", "STRING_CF2", "STRING_CF3", "STRING_CF4", "STRING_CF5", "STRING_CF6", "STRING_CF7", "STRING_CF8", "STRING_CF9", "STRING_CF10", "STRING_CF11", "STRING_CF12", "STRING_CF13", "STRING_CF14", "STRING_CF15", "STRING_CF16", "STRING_CF17", "STRING_CF18", "STRING_CF19", "STRING_CF20", "STRING_CF21", "STRING_CF22", "STRING_CF23", "STRING_CF24", "STRING_CF25", "STRING_CF26", "STRING_CF27", "STRING_CF28", "STRING_CF29", "STRING_CF30"});
        customFieldsMap.put(2, new String[]{"NUMBER_CF1", "NUMBER_CF2", "NUMBER_CF3", "NUMBER_CF4", "NUMBER_CF5", "NUMBER_CF6", "NUMBER_CF7", "NUMBER_CF8", "NUMBER_CF9", "NUMBER_CF10", "NUMBER_CF11", "NUMBER_CF12", "NUMBER_CF13", "NUMBER_CF14", "NUMBER_CF15", "NUMBER_CF16", "NUMBER_CF17", "NUMBER_CF18", "NUMBER_CF19", "NUMBER_CF20","NUMBER_CF21", "NUMBER_CF22", "NUMBER_CF23", "NUMBER_CF24", "NUMBER_CF25", "NUMBER_CF26", "NUMBER_CF27", "NUMBER_CF28", "NUMBER_CF29", "NUMBER_CF30","NUMBER_CF31", "NUMBER_CF32", "NUMBER_CF33", "NUMBER_CF34", "NUMBER_CF35", "NUMBER_CF36", "NUMBER_CF37", "NUMBER_CF38", "NUMBER_CF39", "NUMBER_CF40","NUMBER_CF41", "NUMBER_CF42", "NUMBER_CF43", "NUMBER_CF44", "NUMBER_CF45", "NUMBER_CF46", "NUMBER_CF47", "NUMBER_CF48", "NUMBER_CF49", "NUMBER_CF50"});
        customFieldsMap.put(3, new String[]{"DECIMAL_CF1", "DECIMAL_CF2", "DECIMAL_CF3", "DECIMAL_CF4", "DECIMAL_CF5", "DECIMAL_CF6", "DECIMAL_CF7", "DECIMAL_CF8", "DECIMAL_CF9", "DECIMAL_CF10", "DECIMAL_CF11", "DECIMAL_CF12", "DECIMAL_CF13", "DECIMAL_CF14", "DECIMAL_CF15", "DECIMAL_CF16", "DECIMAL_CF17", "DECIMAL_CF18", "DECIMAL_CF19", "DECIMAL_CF20", "DECIMAL_CF21", "DECIMAL_CF22", "DECIMAL_CF23", "DECIMAL_CF24", "DECIMAL_CF25", "DECIMAL_CF26", "DECIMAL_CF27", "DECIMAL_CF28", "DECIMAL_CF29", "DECIMAL_CF30","DECIMAL_CF31", "DECIMAL_CF32", "DECIMAL_CF33", "DECIMAL_CF34", "DECIMAL_CF35", "DECIMAL_CF36", "DECIMAL_CF37", "DECIMAL_CF38", "DECIMAL_CF39", "DECIMAL_CF40","DECIMAL_CF41", "DECIMAL_CF42", "DECIMAL_CF43", "DECIMAL_CF44", "DECIMAL_CF45", "DECIMAL_CF46", "DECIMAL_CF47", "DECIMAL_CF48", "DECIMAL_CF49", "DECIMAL_CF50"});
        customFieldsMap.put(4, new String[]{"BOOLEAN_CF1", "BOOLEAN_CF2", "BOOLEAN_CF3", "BOOLEAN_CF4", "BOOLEAN_CF5", "BOOLEAN_CF6", "BOOLEAN_CF7", "BOOLEAN_CF8", "BOOLEAN_CF9", "BOOLEAN_CF10", "BOOLEAN_CF11", "BOOLEAN_CF12", "BOOLEAN_CF13", "BOOLEAN_CF14", "BOOLEAN_CF15", "BOOLEAN_CF16", "BOOLEAN_CF17", "BOOLEAN_CF18", "BOOLEAN_CF19", "BOOLEAN_CF20", "BOOLEAN_CF21", "BOOLEAN_CF22", "BOOLEAN_CF23", "BOOLEAN_CF24", "BOOLEAN_CF25","BOOLEAN_CF26", "BOOLEAN_CF27", "BOOLEAN_CF28", "BOOLEAN_CF29", "BOOLEAN_CF30","BOOLEAN_CF31", "BOOLEAN_CF32", "BOOLEAN_CF33", "BOOLEAN_CF34", "BOOLEAN_CF35","BOOLEAN_CF36", "BOOLEAN_CF37", "BOOLEAN_CF38", "BOOLEAN_CF39", "BOOLEAN_CF40","BOOLEAN_CF41", "BOOLEAN_CF42", "BOOLEAN_CF43", "BOOLEAN_CF44", "BOOLEAN_CF45", "BOOLEAN_CF46", "BOOLEAN_CF47", "BOOLEAN_CF48", "BOOLEAN_CF49", "BOOLEAN_CF50"});
        customFieldsMap.put(5, new String[]{"DATE_CF1", "DATE_CF2", "DATE_CF3", "DATE_CF4", "DATE_CF5", "DATE_CF6", "DATE_CF7", "DATE_CF8", "DATE_CF9", "DATE_CF10", "DATE_CF11", "DATE_CF12", "DATE_CF13", "DATE_CF14", "DATE_CF15", "DATE_CF16", "DATE_CF17", "DATE_CF18", "DATE_CF19", "DATE_CF20", "DATE_CF21", "DATE_CF22", "DATE_CF23", "DATE_CF24", "DATE_CF25", "DATE_CF26", "DATE_CF27", "DATE_CF28", "DATE_CF29", "DATE_CF30","DATE_CF31", "DATE_CF32", "DATE_CF33", "DATE_CF34", "DATE_CF35","DATE_CF36", "DATE_CF37", "DATE_CF38", "DATE_CF39", "DATE_CF40","DATE_CF41", "DATE_CF42", "DATE_CF43", "DATE_CF44", "DATE_CF45", "DATE_CF46", "DATE_CF47", "DATE_CF48", "DATE_CF49", "DATE_CF50"});
        customFieldsMap.put(6, new String[]{"DATETIME_CF1", "DATETIME_CF2", "DATETIME_CF3", "DATETIME_CF4", "DATETIME_CF5", "DATETIME_CF6", "DATETIME_CF7", "DATETIME_CF8", "DATETIME_CF9", "DATETIME_CF10", "DATETIME_CF11", "DATETIME_CF12", "DATETIME_CF13", "DATETIME_CF14", "DATETIME_CF15", "DATETIME_CF16", "DATETIME_CF17", "DATETIME_CF18", "DATETIME_CF19", "DATETIME_CF20", "DATETIME_CF21", "DATETIME_CF22", "DATETIME_CF23", "DATETIME_CF24", "DATETIME_CF25", "DATETIME_CF26", "DATETIME_CF27", "DATETIME_CF28", "DATETIME_CF29", "DATETIME_CF30","DATETIME_CF31", "DATETIME_CF32", "DATETIME_CF33", "DATETIME_CF34", "DATETIME_CF35","DATETIME_CF36", "DATETIME_CF37", "DATETIME_CF38", "DATETIME_CF39", "DATETIME_CF40","DATETIME_CF41", "DATETIME_CF42", "DATETIME_CF43", "DATETIME_CF44", "DATETIME_CF45", "DATETIME_CF46", "DATETIME_CF47", "DATETIME_CF48", "DATETIME_CF49", "DATETIME_CF50"});
        customFieldsMap.put(7, new String[]{"LOOKUP_CF1", "LOOKUP_CF2", "LOOKUP_CF3", "LOOKUP_CF4", "LOOKUP_CF5", "LOOKUP_CF6", "LOOKUP_CF7", "LOOKUP_CF8", "LOOKUP_CF9", "LOOKUP_CF10", "LOOKUP_CF11", "LOOKUP_CF12", "LOOKUP_CF13", "LOOKUP_CF14", "LOOKUP_CF15", "LOOKUP_CF16", "LOOKUP_CF17", "LOOKUP_CF18", "LOOKUP_CF19", "LOOKUP_CF20", "LOOKUP_CF21", "LOOKUP_CF22", "LOOKUP_CF23", "LOOKUP_CF24", "LOOKUP_CF25", "LOOKUP_CF26", "LOOKUP_CF27", "LOOKUP_CF28", "LOOKUP_CF29", "LOOKUP_CF30","LOOKUP_CF31", "LOOKUP_CF32", "LOOKUP_CF33", "LOOKUP_CF34", "LOOKUP_CF35","LOOKUP_CF36", "LOOKUP_CF37", "LOOKUP_CF38", "LOOKUP_CF39", "LOOKUP_CF40","LOOKUP_CF41", "LOOKUP_CF42", "LOOKUP_CF43", "LOOKUP_CF44", "LOOKUP_CF45", "LOOKUP_CF46", "LOOKUP_CF47", "LOOKUP_CF48", "LOOKUP_CF49", "LOOKUP_CF50"});
        customFieldsMap.put(8, new String[]{"ENUM_CF1", "ENUM_CF2", "ENUM_CF3", "ENUM_CF4", "ENUM_CF5", "ENUM_CF6", "ENUM_CF7", "ENUM_CF8", "ENUM_CF9", "ENUM_CF10", "ENUM_CF11", "ENUM_CF12", "ENUM_CF13", "ENUM_CF14", "ENUM_CF15", "ENUM_CF16", "ENUM_CF17", "ENUM_CF18", "ENUM_CF19", "ENUM_CF20", "ENUM_CF21", "ENUM_CF22", "ENUM_CF23", "ENUM_CF24", "ENUM_CF25", "ENUM_CF26", "ENUM_CF27", "ENUM_CF28", "ENUM_CF29", "ENUM_CF30","ENUM_CF31", "ENUM_CF32", "ENUM_CF33", "ENUM_CF34", "ENUM_CF35","ENUM_CF36", "ENUM_CF37", "ENUM_CF38", "ENUM_CF39", "ENUM_CF40","ENUM_CF41", "ENUM_CF42", "ENUM_CF43", "ENUM_CF44", "ENUM_CF45", "ENUM_CF46", "ENUM_CF47", "ENUM_CF48", "ENUM_CF49", "ENUM_CF50"});
        customFieldsMap.put(9, new String[]{"FILE_CF1", "FILE_CF2", "FILE_CF3", "FILE_CF4", "FILE_CF5", "FILE_CF6", "FILE_CF7", "FILE_CF8", "FILE_CF9", "FILE_CF10", "FILE_CF11", "FILE_CF12", "FILE_CF13", "FILE_CF14", "FILE_CF15", "FILE_CF16", "FILE_CF17", "FILE_CF18", "FILE_CF19", "FILE_CF20", "FILE_CF21", "FILE_CF22", "FILE_CF23", "FILE_CF24", "FILE_CF25", "FILE_CF26", "FILE_CF27", "FILE_CF28", "FILE_CF29", "FILE_CF30","FILE_CF31", "FILE_CF32", "FILE_CF33", "FILE_CF34", "FILE_CF35","FILE_CF36", "FILE_CF37", "FILE_CF38", "FILE_CF39", "FILE_CF40","FILE_CF41", "FILE_CF42", "FILE_CF43", "FILE_CF44", "FILE_CF45", "FILE_CF46", "FILE_CF47", "FILE_CF48", "FILE_CF49", "FILE_CF50"});
        customFieldsMap.put(15, new String[]{"SCORE_CF1", "SCORE_CF2", "SCORE_CF3", "SCORE_CF4", "SCORE_CF5", "SCORE_CF6", "SCORE_CF7", "SCORE_CF8", "SCORE_CF9", "SCORE_CF10", "SCORE_CF11", "SCORE_CF12", "SCORE_CF13", "SCORE_CF14", "SCORE_CF15", "SCORE_CF16", "SCORE_CF17", "SCORE_CF18", "SCORE_CF19", "SCORE_CF20", "SCORE_CF21", "SCORE_CF22", "SCORE_CF23", "SCORE_CF24", "SCORE_CF25", "SCORE_CF26", "SCORE_CF27", "SCORE_CF28", "SCORE_CF29", "SCORE_CF30","SCORE_CF31", "SCORE_CF32", "SCORE_CF33", "SCORE_CF34", "SCORE_CF35","SCORE_CF36", "SCORE_CF37", "SCORE_CF38", "SCORE_CF39", "SCORE_CF40","SCORE_CF41", "SCORE_CF42", "SCORE_CF43", "SCORE_CF44", "SCORE_CF45", "SCORE_CF46", "SCORE_CF47", "SCORE_CF48", "SCORE_CF49", "SCORE_CF50"});
        customFieldsMap.put(18, new String[]{"BIGSTRING_CF1","BIGSTRING_CF2"});

        return customFieldsMap;
    }

    @Override
    public String getNewColumnNameForFieldType(Integer fieldTypeInt, List<String> existingColumns) {
        if (typeVsCustomColumns.containsKey(fieldTypeInt)) {
            return ModuleCustomFieldsCount.getColumnName(typeVsCustomColumns.get(fieldTypeInt), existingColumns);
        }
        return null;
    }
}
