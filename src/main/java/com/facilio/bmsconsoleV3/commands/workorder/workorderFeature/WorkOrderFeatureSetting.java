package com.facilio.bmsconsoleV3.commands.workorder.workorderFeature;

import lombok.Getter;
import lombok.Setter;

/**
 * WorkOrderFeatureSetting is a POJO class that holds properties that defines if the action is allowed or not and reason for the same
 * allowed - if action is allowed or not
 * reason  - reason if the action isn't allowed
 */
@Getter
@Setter
public class WorkOrderFeatureSetting {
    private Boolean allowed;
    private String reason;

    public WorkOrderFeatureSetting() {
        this.allowed = null;
        this.reason = "";
    }
}
