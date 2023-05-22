package com.facilio.readingrule.rca.util;

import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;

@Getter
public enum RCAFieldType implements FacilioIntEnum {
    FAULT_TYPE("faultType"),
    READING_ALARM_ASSET_CATEGORY("readingAlarmAssetCategory"),
    SEVERITY("severity"),
    ENERGY_IMPACT("energyImpact"),
    COST_IMPACT("costImpact"),
    NO_OF_OCCURRENCES("noOfOccurrences"),
    NO_OF_EVENTS("noOfEvents"),
    DURATION("duration");

    private String fieldName;
    RCAFieldType(String fieldName) {
        this.fieldName = fieldName;
    }
    public static RCAFieldType fromString(String fieldName) {
        for (RCAFieldType fieldType : RCAFieldType.values()) {
            if (fieldType.fieldName.equals(fieldName)) {
                return fieldType;
            }
        }
        return null;
    }
}
