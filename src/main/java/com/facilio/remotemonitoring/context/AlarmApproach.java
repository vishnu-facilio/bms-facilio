package com.facilio.remotemonitoring.context;

import com.facilio.modules.FacilioIntEnum;

public enum AlarmApproach implements FacilioIntEnum {

    REPEAT_UNTIL_RESOLVED("Repeat Until Resolved"),
    RETURN_TO_NORMAL("Return To Normal");

    private String displayName;
    AlarmApproach(String displayName) {
        this.displayName = displayName;
    }
    public static AlarmApproach valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }

    @Override
    public Integer getIndex() {
        return ordinal() + 1;
    }
    @Override
    public String getValue() {
        return displayName;
    }

}
