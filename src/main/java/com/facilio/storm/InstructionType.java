package com.facilio.storm;

import com.facilio.modules.FacilioEnum;

public enum InstructionType implements FacilioEnum {
    LIVE_KPI_HISTORICAL;

    public Integer getIndex() {
        return ordinal() + 1;
    }

    @Override
    public String getValue() {
        return name();
    }

}
