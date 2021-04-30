package com.facilio.bmsconsole.enums;

import com.facilio.modules.FacilioIntEnum;

public enum SourceType implements FacilioIntEnum {
    WEB_ACTION,
    IMPORT,
    FORMULA,
    SHIFT_READING,
    KINESIS,
    ML,
    SCRIPT,
    INTEGRATION,
    SYSTEM;

    @Override
    public Integer getIndex() {
        return ordinal() + 1;
    }

    public String getValue() {
        return name();
    }

    public static SourceType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }
}
