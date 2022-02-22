package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.enums.FaultType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleInterface;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class ReadingAlarm extends BaseAlarmContext {
    private static final long serialVersionUID = 1L;

    private ReadingAlarmCategoryContext readingAlarmCategory;
    public ReadingAlarmCategoryContext getReadingAlarmCategory() {
        return readingAlarmCategory;
    }
    public void setReadingAlarmCategory(ReadingAlarmCategoryContext readingAlarmCategory) {
        this.readingAlarmCategory = readingAlarmCategory;
    }

    @JsonDeserialize(as = ReadingRuleContext.class)
    private ReadingRuleInterface rule;

    public ReadingRuleInterface getRule() {
        return rule;
    }

    public void setRule(ReadingRuleInterface rule) {
        this.rule = rule;
    }

    @JsonDeserialize(as = ReadingRuleContext.class)
    private ReadingRuleInterface subRule;

    public ReadingRuleInterface getSubRule() {
        return subRule;
    }
    public void setSubRule(ReadingRuleInterface subRule) {
        this.subRule = subRule;
    }

    private long readingFieldId;
    public long getReadingFieldId() {
        return readingFieldId;
    }
    public void setReadingFieldId(long readingFieldId) {
        this.readingFieldId = readingFieldId;
    }
    private String readingFieldName;
    public String getReadingFieldName() {
        return readingFieldName;
    }
    public void setReadingFieldName(String readingFieldName) {
        this.readingFieldName = readingFieldName;
    }

    private FaultType faultType;
    public int getFaultType() {
        if (faultType != null) {
            return faultType.getIndex();
        }
        return -1;
    }
    public FaultType getFaultTypeEnum() {
        return faultType;
    }
    public void setFaultType(FaultType faultType) {
        this.faultType = faultType;
    }
    public void setFaultType(int faultType) {
        this.faultType = FaultType.valueOf(faultType);
    }
}
