package com.facilio.storm;

import com.facilio.modules.FacilioIntEnum;

public enum InstructionType implements FacilioIntEnum {
    LIVE_KPI_HISTORICAL,
    READING_RULE_HISTORICAL,
    VM_HISTORICAL
    ;

    public static InstructionType valueOf(int type) {
        if (type > 0 && type <= values().length) {
            return values()[type - 1];
        }
        return null;
    }

}
