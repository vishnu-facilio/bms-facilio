
package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;
import lombok.Setter;

public enum RuleType implements FacilioStringEnum {

    INDIVIDUAL("Filter"),
    CONTROLLER_OFFLINE("Connection Loss"),
    ROLL_UP("Roll Up"),
    SITE_OFFLINE("Site Offline");

    private String displayName;

    RuleType(String displayName) {
        this.displayName = displayName;
    }
    @Override
    public String getValue() {
        return displayName;
    }

}
