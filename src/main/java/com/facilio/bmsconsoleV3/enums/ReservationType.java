package com.facilio.bmsconsoleV3.enums;

import com.facilio.modules.FacilioIntEnum;

public enum ReservationType implements FacilioIntEnum {
    HARD, SOFT;

    @Override
    public Integer getIndex() {
        return ordinal() + 1;
    }

    @Override
    public String getValue() {
        return name();
    }

    public static ReservationType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }
}
