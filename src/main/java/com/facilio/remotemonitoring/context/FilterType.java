
package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.budget.BudgetAmountContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;
import lombok.Setter;

public enum FilterType implements FacilioStringEnum {

    INDIVIDUAL("Individual"),
    CONTROLLER_OFFLINE("Connectivity Loss Alarm"),
    ROLL_UP("Roll Up");

    private String displayName;

    FilterType(String displayName) {
        this.displayName = displayName;
    }
    @Override
    public String getValue() {
        return displayName;
    }

}
