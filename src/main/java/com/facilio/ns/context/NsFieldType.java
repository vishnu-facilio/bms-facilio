package com.facilio.ns.context;

import com.facilio.modules.FacilioIntEnum;

public enum NsFieldType implements FacilioIntEnum {
    ASSET_READING,
    SPACE_READING,
    RELATED_READING,
    ASSET,
    SPACE,
    METER_READING,
    METER,
    SITE,
    SITE_READING,
    ;

    public static NsFieldType valueOf(int idx) {
        if (idx > 0 && idx <= values().length) {
            return values()[idx - 1];
        }
        return null;
    }
}
