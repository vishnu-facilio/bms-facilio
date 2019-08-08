package com.facilio.bmsconsole.context;

public class ReadingRCAEvent extends BaseEventContext {

    private long parentId = -1;
    public long getParentId() {
        return parentId;
    }
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    private long ruleId = -1;
    public long getRuleId() {
        return ruleId;
    }
    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    private long subRuleId = -1;
    public long getSubRuleId() {
        return subRuleId;
    }
    public void setSubRuleId(long subRuleId) {
        this.subRuleId = subRuleId;
    }

    @Override
    public String constructMessageKey() {
        return "Reading_RCA_" + parentId;
    }

    @Override
    public BaseAlarmContext.Type getEventTypeEnum() {
        return BaseAlarmContext.Type.READING_RCA_ALARM;
    }
}
