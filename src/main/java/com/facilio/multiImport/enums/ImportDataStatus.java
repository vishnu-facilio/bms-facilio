package com.facilio.multiImport.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ImportDataStatus {

    UPLOAD_COMPLETED, //1
    MODULE_MAPPED,  //2
    FIELD_MAPPED,  //3
    PARSING_STARTED, //4
    PARSING_FAILED,//5

    PARSING_COMPLETED,//6
    ABORTED,//7
    IMPORT_STARTED,//8
    IMPORT_FAILED,//9
    IMPORT_COMPLETED;  //10

    public int getValue() {
        return ordinal() + 1;
    }

    public static ImportDataStatus valueOf(int value) {
        return IMPORT_DATA_STATUS_MAP.get(value);
    }

    private static final Map<Integer, ImportDataStatus> IMPORT_DATA_STATUS_MAP = Collections.unmodifiableMap(initTypeMap());

    private static Map<Integer, ImportDataStatus> initTypeMap() {
        Map<Integer, ImportDataStatus> typeMap = new HashMap<>();
        for (ImportDataStatus type : ImportDataStatus.values()) {
            typeMap.put(type.getValue(), type);
        }
        return typeMap;
    }
}