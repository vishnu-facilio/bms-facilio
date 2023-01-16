package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowRuleLoggerContext {

    long id = -1;
    long orgId;
    long ruleId;
    long noOfResources;
    long resolvedResourcesCount;
    Status status;
    long totalAlarmCount;
    long startTime;
    long endTime;
    long createdTime;
    long createdBy;
    long calculationStartTime;
    long calculationEndTime;
    RuleJobType ruleJobType;

    public int getStatus() {
        return (status != null) ? status.getIndex() : -1;
    }

    public Status getStatusAsEnum() {
        return status;
    }

    public void setStatus(int idx) {
        this.status = Status.valueOf(idx);
    }

    public RuleJobType getRuleJobTypeEnum() {
        return ruleJobType;
    }

    public int getRuleJobType() {
        if (ruleJobType == null) {
            return -1;
        }
        return ruleJobType.getIndex();
    }

    public void setRuleJobType(int ruleJobType) {
        this.ruleJobType = RuleJobType.valueOf(ruleJobType);
    }

    public enum Status implements FacilioIntEnum {
        IN_PROGRESS,
        RESOLVED,
        FAILED,
        PARTIALLY_COMPLETED,
        RESCHEDULED,
        ;

        public static Status valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }
}
