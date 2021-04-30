package com.facilio.bmsconsoleV3.enums;

import com.facilio.modules.FacilioIntEnum;

public enum Group implements FacilioIntEnum {
    ASSET("Asset"),
    LIABILITY("Liability"),
    EQUITY("Equity"),
    INCOME("Income"),
    EXPENSE("Expense");
    ;
    private String name;

    Group(String name) {
        this.name = name;
    }

    public static Group valueOf(int value) {
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
        return name;
    }
}
