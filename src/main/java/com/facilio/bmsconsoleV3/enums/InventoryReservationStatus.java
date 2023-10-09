package com.facilio.bmsconsoleV3.enums;

import com.facilio.modules.FacilioIntEnum;

public enum InventoryReservationStatus implements FacilioIntEnum {
    NOT_ISSUED("Not Issued"), PARTIALLY_ISSUED("Partially Issued"), ISSUED("Issued");

    private final String value;

    InventoryReservationStatus(String value) {
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

    public static InventoryReservationStatus valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }
}

