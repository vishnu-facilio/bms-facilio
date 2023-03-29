package com.facilio.multiImport.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum MultiImportSetting {
    INSERT,
    INSERT_SKIP,
    UPDATE,
    UPDATE_NOT_NULL,
    BOTH,
    BOTH_NOT_NULL;

    public int getValue() {
        return ordinal() + 1;
    }

    public static MultiImportSetting valueOf(Integer value) {
        return IMPORT_SETTING.get(value);
    }

    private static final Map<Integer, MultiImportSetting> IMPORT_SETTING = Collections.unmodifiableMap(initTypeMap());

    private static Map<Integer, MultiImportSetting> initTypeMap() {
        Map<Integer, MultiImportSetting> typeMap = new HashMap<>();
        for (MultiImportSetting type : MultiImportSetting.values()) {
            typeMap.put(type.getValue(), type);
        }
        return typeMap;
    }
}