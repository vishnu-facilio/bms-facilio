package com.facilio.readingkpi.context;

import com.facilio.modules.FacilioIntEnum;

public enum KPIType implements FacilioIntEnum {

    LIVE("Live"),
    SCHEDULED("Scheduled"),
    DYNAMIC("Dynamic");

    String displayName;

    KPIType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getValue() {
        return this.displayName;
    }

    public static KPIType valueOf(int idx) {
        if (idx > 0 && idx <= values().length) {
            return values()[idx - 1];
        }
        return null;
    }
}