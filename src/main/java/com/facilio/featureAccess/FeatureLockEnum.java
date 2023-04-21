package com.facilio.featureAccess;

import com.facilio.modules.FacilioIntEnum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum FeatureLockEnum implements FacilioIntEnum {
    FORMS,
    FIELDS,
    WORKFLOW,
    STATEFLOW,
    CUSTOM_BUTTON;

    public FeatureLockEnum getFeatureLockType(int val) {
        return TYPE_MAP.get(val);
    }

    public static final Map<Integer, FeatureLockEnum> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
    private static Map<Integer, FeatureLockEnum> initTypeMap() {
        Map<Integer, FeatureLockEnum> typeMap = new HashMap<>();
        for(FeatureLockEnum type : values()) {
            typeMap.put(type.getIndex(), type);
        }
        return typeMap;
    }
}
