package com.facilio.analytics.v2.context;

import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;

public enum AnalyticsVariance implements FacilioStringEnum {
    AVERAGE("avg"),
    COUNT("count"),
    LAST("lastValue"),
    MAX("max"),
    MIN("min"),
    SUM("sum");
    @Getter
    private String linkName;

    AnalyticsVariance(String linkName) {
        this.linkName = linkName;
    }
}
