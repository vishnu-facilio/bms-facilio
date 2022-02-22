package com.facilio.ns.context;

import com.facilio.modules.FacilioIntEnum;

public enum AggregationType implements FacilioIntEnum {
    FIRST, LAST, SUM, MAX, MIN, AVG, COUNT;

    public static AggregationType valueOf(int idx) {
        if (idx > 0 && idx <= values().length) {
            return values()[idx - 1];
        }
        return null;
    }
}
