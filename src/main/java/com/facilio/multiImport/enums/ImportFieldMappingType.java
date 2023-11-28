package com.facilio.multiImport.enums;

import com.facilio.modules.FacilioIntEnum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ImportFieldMappingType implements FacilioIntEnum {
    NORMAL,
    RELATIONSHIP,
    ONE_LEVEL,
    UNIQUE_FIELD;

    private static final Map<Integer,ImportFieldMappingType> TYPE_MAP = Collections.unmodifiableMap(initMap());
    private static Map<Integer,ImportFieldMappingType> initMap(){
        Map<Integer,ImportFieldMappingType> typeMap = new HashMap<>();
        for(ImportFieldMappingType type:ImportFieldMappingType.values()){
            typeMap.put(type.getIndex(),type);
        }
        return typeMap;
    }

    public static ImportFieldMappingType getImportFieldType(int type){
        return TYPE_MAP.get(type);
    }
}
