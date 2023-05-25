package com.facilio.readingrule.context;

import com.facilio.modules.FacilioIntEnum;

public enum ExecutionStatus implements FacilioIntEnum {
    SUCCESS("Success"),
    FAILED("Failed");

    private String name;

    ExecutionStatus(String name) {
        this.name = name;
    }
    @Override
    public String getValue() {
        return this.name;
    }

    public static ExecutionStatus valueOf(int idx) {
        if (idx > 0 && idx <= values().length) {
            return values()[idx - 1];
        }
        return null;
    }
}
