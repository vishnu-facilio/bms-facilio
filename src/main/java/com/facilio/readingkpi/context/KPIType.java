package com.facilio.readingkpi.context;

import com.facilio.modules.FacilioIntEnum;

public enum KPIType implements FacilioIntEnum {

    LIVE,
    SCHEDULED,
    DYNAMIC;

    public static KPIType valueOf(int idx) {
        if (idx > 0 && idx <= values().length) {
            return values()[idx - 1];
        }
        return null;
    }
}