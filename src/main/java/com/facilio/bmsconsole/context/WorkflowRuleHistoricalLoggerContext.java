package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkflowRuleHistoricalLoggerContext {

    private long id = -1;
    private long orgId;
    private Type type;
    private long ruleId;
    private Long resourceId;
    private ResourceContext resourceContext;
    private Status status;
    private long alarmCount;
    private long loggerGroupId;
    private long resourceLogCount;
    private long resolvedLogCount;
    private int totalChildAlarmCount;
    private long startTime;
    private long endTime;
    private long calculationStartTime;
    private long calculationEndTime;
    private long createdTime;
    private long createdBy;

    public int getStatus() {
        return (status != null) ? status.getIndex() : -1;
    }

    public Status getStatusAsEnum() {
        return status;
    }

    public void setStatus(int idx) {
        this.status = Status.valueOf(idx);
    }

    public int getType() {
        return (type != null) ? type.getIndex() : -1;
    }

    public void setType(int ix) {
        this.type = Type.valueOf(ix);
    }

    public enum Type implements FacilioIntEnum {

        READING_RULE;

        public static Type valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }

    }

    public enum Status implements FacilioIntEnum {

        IN_PROGRESS,
        RESOLVED,
        FAILED,
        EVENT_GENERATING_STATE,
        ALARM_PROCESSING_STATE;

        public static Status valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }


    }
}
