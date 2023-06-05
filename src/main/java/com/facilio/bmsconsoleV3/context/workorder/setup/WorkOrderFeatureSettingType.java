package com.facilio.bmsconsoleV3.context.workorder.setup;


import com.facilio.modules.FacilioStringEnum;

public enum WorkOrderFeatureSettingType {
    PLANNING("Work Order Planning"),
    EXECUTION("Task Execution"),
    ACTUALS("Work Order Actuals");

    private String displayName;

    WorkOrderFeatureSettingType(String displayName) {
        this.displayName = displayName;
    }

    public int getVal() {
        return ordinal() + 1;
    }
    public static WorkOrderFeatureSettingType valueOf(int type) {
        if (type > 0 && type <= WORKORDER_FEATURE_SETTING_TYPE.length) {
            return WORKORDER_FEATURE_SETTING_TYPE[type - 1];
        }
        return null;
    }
    public static WorkOrderFeatureSettingType[] WORKORDER_FEATURE_SETTING_TYPE = WorkOrderFeatureSettingType.values();

    public String getDisplayName() {
        return displayName;
    }
}
