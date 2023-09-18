package com.facilio.bmsconsoleV3.enums;

import com.facilio.modules.FacilioIntEnum;

public enum CostType implements FacilioIntEnum {
    FIFO, LIFO;

    @Override
    public Integer getIndex() {
        return ordinal() + 1;
    }

    @Override
    public String getValue() {
        return name();
    }

    public static CostType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }
}
