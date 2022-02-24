package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.workflow.rule.ReadingRuleInterface;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.chain.Context;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.enums.FaultType;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ReadingEventContext extends BaseEventContext {
    private static final long serialVersionUID = 1L;

    @Override
    public String constructMessageKey() {
        if (getResource() != null && getRule() != null) {
            return rule.getId() + "_" + getResource().getId();
        }
        return null;
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new ReadingAlarm();
        }
        super.updateAlarmContext(baseAlarm, add);
        ReadingAlarm readingAlarm = (ReadingAlarm) baseAlarm;

        if (readingAlarmCategory == null) {
            if (getResource() != null) {
                readingAlarmCategory = NewAlarmAPI.getReadingAlarmCategory(getResource().getId());
            }
        }
        readingAlarm.setReadingAlarmCategory(readingAlarmCategory);

        if (this.getFaultTypeEnum() != null) {
            readingAlarm.setFaultType(this.getFaultTypeEnum());
        }
        readingAlarm.setRule(rule);
        readingAlarm.setSubRule(subRule);
        if (readingFieldId != -1) {
            readingAlarm.setReadingFieldId(readingFieldId);
        }
        return baseAlarm;
    }

    @Override
    public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
        if (add && alarmOccurrence == null) {
            alarmOccurrence = new ReadingAlarmOccurrenceContext();
        }

        ReadingAlarmOccurrenceContext readingOccurrence = (ReadingAlarmOccurrenceContext) alarmOccurrence;
        if (readingAlarmCategory == null) {
            if (getResource() != null) {
                readingAlarmCategory = NewAlarmAPI.getReadingAlarmCategory(getResource().getId());
            }
        }
        readingOccurrence.setReadingAlarmCategory(readingAlarmCategory);

        if (this.getFaultTypeEnum() != null) {
            readingOccurrence.setFaultType(this.getFaultTypeEnum());
        }
        readingOccurrence.setRule(rule);
        readingOccurrence.setSubRule(subRule);
        if (readingFieldId != -1) {
            readingOccurrence.setReadingFieldId(readingFieldId);
        }
        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }

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

    private long readingFieldId = -1;
    public long getReadingFieldId() {
        return readingFieldId;
    }
    public void setReadingFieldId(long readingFieldId) {
        this.readingFieldId = readingFieldId;
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

    @Override
    @JsonSerialize
    public Type getEventTypeEnum() {
            return Type.READING_ALARM;
        }

    @JsonIgnore
    @JSON(deserialize = false)
    public void setRuleId(long ruleId) {
        if (ruleId > 0) {
            ReadingRuleContext ruleContext = new ReadingRuleContext();
            ruleContext.setId(ruleId);
            setRule(ruleContext);
        }
    }

    @JsonIgnore
    @JSON(deserialize = false)
    public void setNewRuleId(long ruleId) {
        if (ruleId > 0) {
            NewReadingRuleContext ruleContext = new NewReadingRuleContext();
            ruleContext.setId(ruleId);
            setRule(ruleContext);
        }
    }

    @JsonIgnore
    @JSON(deserialize = false)
    public void setSubRuleId(long subRuleId) {
        if (subRuleId > 0) {
            ReadingRuleContext ruleContext = new ReadingRuleContext();
            ruleContext.setId(subRuleId);
            setSubRule(ruleContext);
        }
    }

    @JsonIgnore
    @JSON(deserialize = false)
    public void setNewSubRuleId(long subRuleId) {
        if (subRuleId > 0) {
            NewReadingRuleContext ruleContext = new NewReadingRuleContext();
            ruleContext.setId(subRuleId);
            setSubRule(ruleContext);
        }
    }
}
