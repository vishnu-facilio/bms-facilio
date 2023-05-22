package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.enums.RuleJobType;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.Serializable;

@Getter
@Setter
public class WorkflowRuleResourceLoggerContext implements Serializable {

    long id = -1;
    long orgId;
    long parentRuleLoggerId;
    long resourceId;
    JSONObject loggerInfo;
    ResourceContext resourceContext;
    Status status;
    long alarmCount;
    long modifiedStartTime;
    long modifiedEndTime;
    long calculationStartTime;
    long calculationEndTime;
    RuleJobType ruleJobType;


    public void addLoggerInfo(String key, Object value) {
        if (this.loggerInfo == null) {
            this.loggerInfo = new JSONObject();
        }
        this.loggerInfo.put(key, value);
    }

    private String loggerInfoStr;

    public String getLoggerInfoStr() {
        return (loggerInfo != null) ? loggerInfo.toJSONString() : loggerInfoStr;
    }

    public void setLoggerInfoStr(String loggerInfoStr) throws ParseException {
        this.loggerInfoStr = loggerInfoStr;
        JSONParser parser = new JSONParser();
        this.loggerInfo = (JSONObject) parser.parse(loggerInfoStr);
    }

    public int getStatus() {
        return (status != null) ? status.getIndex() : -1;
    }

    public Status getStatusAsEnum() {
        return status;
    }

    public void setStatus(int idx) {
        this.status = Status.valueOf(idx);
    }

    public void setStatusAsEnum(Status status) {
        this.status = status;
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
        EVENT_GENERATING_STATE,
        ALARM_PROCESSING_STATE,
        RESOLVED,
        FAILED,
        PARTIALLY_PROCESSED_STATE,
        PARTIALLY_COMPLETED_STATE,
        RESCHEDULED,
        ALARM_OCCURRENCES_DELETION_STATE,
        FAULT_IMPACT_CALCULATION_STATE,
        RCA_CALCULATION_STATE;

        public static Status valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }

    }

}
