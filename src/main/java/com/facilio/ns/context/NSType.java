package com.facilio.ns.context;

import com.facilio.modules.FacilioIntEnum;

public enum NSType implements FacilioIntEnum {
    READING_RULE("Reading Rule"),
    FAULT_IMPACT_RULE("Fault Impact Rule"),
    KPI_RULE("KPI"),
    SENSOR_RULE("Sensor Rule"),
    VIRTUAL_METER("Virtual Meter")
    ;

    private String name;

    NSType(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return name;
    }

    @Override
    public Integer getIndex() {
        return ordinal() + 1;
    }

    public static NSType valueOf(int type) {
        if (type > 0 && type <= values().length) {
            return values()[type - 1];
        }
        return null;
    }

}
