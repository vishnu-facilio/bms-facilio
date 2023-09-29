package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

public enum AlarmStrategy implements FacilioIntEnum {

    REPEAT_UNTIL_RESOLVED("Repeat Until Resolved"),
    RETURN_TO_NORMAL("Return To Normal");

    private String displayName;
    AlarmStrategy(String displayName) {
        this.displayName = displayName;
    }
    public static AlarmStrategy valueOf(int value) {
        if (value > 0 && value <= values().length) {
            return values()[value - 1];
        }
        return null;
    }

    @Override
    public Integer getIndex() {
        return ordinal() + 1;
    }
    @Override
    public String getValue() {
        return displayName;
    }

}
