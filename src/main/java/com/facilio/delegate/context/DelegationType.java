package com.facilio.delegate.context;

import com.facilio.modules.FacilioIntEnum;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum DelegationType implements FacilioIntEnum {
    EMAIL_NOTIFICATION("Email Notification", 1),
    STATE_FLOW("State Flow", 1 << 1),
    ;

    private String name;
    private int delegationValue;

    DelegationType(String name, int delegationValue) {
        this.name = name;
        this.delegationValue = delegationValue;
    }

    @Override
    public String getValue() {
        return name;
    }

    @Override
    public Integer getIndex() {
        return delegationValue;
    }

    public int getDelegationValue() {
        return delegationValue;
    }

    public static DelegationType valueOf(int delegationValue) {
        return TYPE_MAP.get(delegationValue);
    }

    private static final Map<Integer, DelegationType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
    private static Map<Integer, DelegationType> initTypeMap() {
        Map<Integer, DelegationType> typeMap = new HashMap<>();

        for(DelegationType type : values()) {
            typeMap.put(type.getDelegationValue(), type);
        }
        return typeMap;
    }

    public boolean isPresent(long activity) {
        if(activity > 0) {
            return (activity & this.delegationValue) == this.delegationValue;
        }
        return false;
    }
}
