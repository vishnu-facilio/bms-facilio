package com.facilio.connected;

import com.facilio.modules.FacilioIntEnum;

public enum ResourceType implements FacilioIntEnum {
    ASSET_CATEGORY("asset"),
    SPACE_CATEGORY,
    METER_CATEGORY("meter"),
    SITE;

    String moduleName ;
    ResourceType (String moduleName) {
        this.moduleName = moduleName;
    }
    ResourceType () {}
    public static ResourceType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        throw new IllegalArgumentException("Invalid resource Type index");
    }
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
