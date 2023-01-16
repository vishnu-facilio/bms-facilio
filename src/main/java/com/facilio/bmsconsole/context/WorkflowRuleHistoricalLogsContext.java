package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowRuleHistoricalLogsContext {

    private long id = -1;
    private long orgId;
    private long parentRuleResourceId;
    private long splitStartTime;
    private long splitEndTime;
    private Status status;
    private LogState logState;
    private long calculationStartTime;
    private long calculationEndTime;
    private String errorMessage;
    private RuleJobType ruleJobType;

    public int getLogState() {
        return (logState != null) ? logState.getIndex() : -1;
    }

    public LogState getLogStateAsEnum() {
        return logState;
    }

    public void setLogState(int logStateint) {
        this.logState = LogState.valueOf(logStateint);
    }

    public Status getStatusAsEnum() {
        return status;
    }

    public int getStatus() {
        return (status != null) ? status.getIndex() : -1;
    }

    public void setStatus(int statusint) {
        this.status = Status.valueOf(statusint);
    }

    public RuleJobType getRuleJobTypeEnum() {
        return ruleJobType;
    }

    public int getRuleJobType() {
        return (ruleJobType == null) ? -1 : ruleJobType.getIndex();
    }
    
    public void setRuleJobType(int ruleJobType) {
        this.ruleJobType = RuleJobType.valueOf(ruleJobType);
    }

    public enum Status implements FacilioIntEnum {

        IN_PROGRESS,
        RESOLVED,
        FAILED,
        YET_TO_BE_SCHEDULED,
        SKIPPED,
        ;

        public static Status valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }
    }

    public enum LogState implements FacilioIntEnum {

        IS_FIRST_JOB,
        IS_LAST_JOB,
        FIRST_AS_WELL_AS_LAST,
        ;

        public static LogState valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }

    }
}
