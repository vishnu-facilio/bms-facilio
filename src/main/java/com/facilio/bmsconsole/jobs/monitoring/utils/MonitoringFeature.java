package com.facilio.bmsconsole.jobs.monitoring.utils;

public enum MonitoringFeature {
    PM_V1,
    PM_V2,
    INSPECTION;

    public int getVal() {
        return ordinal() + 1;
    }
    public static MonitoringFeature valueOf(int type) {
        if (type > 0 && type <= MONITORING_FEATURE.length) {
            return MONITORING_FEATURE[type - 1];
        }
        return null;
    }
    public static MonitoringFeature[] MONITORING_FEATURE = MonitoringFeature.values();
}
