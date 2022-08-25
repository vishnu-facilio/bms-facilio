package com.facilio.readingkpi.context;

import com.facilio.modules.FacilioIntEnum;

public enum KPIType implements FacilioIntEnum {
    LIVE_FORMULA,
    SCHEDULED_FORMULA;

    public static KPIType valueOf(int idx) {
        if (idx > 0 && idx <= values().length) {
            return values()[idx - 1];
        }
        return null;
    }
}