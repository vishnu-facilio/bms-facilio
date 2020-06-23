package com.facilio.bmsconsoleV3.context.workpermit;

import com.facilio.modules.FacilioEnum;

public enum ValidationType implements FacilioEnum {
    PRE("Pre"),
    POST("Post");
    private String name;

    ValidationType(String name) {
        this.name = name;
    }

    public static ValidationType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }

    @Override
    public int getIndex() {
        return ordinal() + 1;
    }

    @Override
    public String getValue() {
        return name;
    }
}
