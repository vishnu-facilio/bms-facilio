package com.facilio.bmsconsoleV3.enums;

import com.facilio.modules.FacilioIntEnum;

public enum ReservationSource implements FacilioIntEnum {
    WO_PLANS("Work Order Plans"), INVENTORY_REQUEST("Inventory Request"),SO_PLANS("Work Order Plans");

    private final String value;
    ReservationSource(String value) {
        this.value = value;
    }

    @Override
    public Integer getIndex() {
        return ordinal() + 1;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static ReservationSource valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }
}
