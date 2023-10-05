package com.facilio.connected;

import com.facilio.modules.FacilioIntEnum;

public enum ResourceType implements FacilioIntEnum {
    ASSET_CATEGORY,
    SPACE_CATEGORY,
    METER_CATEGORY,
    SITE;

    public static ResourceType valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }
}
