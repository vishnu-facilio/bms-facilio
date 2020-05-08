package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.chain.Context;
import org.apache.struts2.json.annotations.JSON;

public class RuleRollUpEvent extends BaseEventContext {
    private static final long serialVersionUID = 1L;

    @Override
    public String constructMessageKey() {
        if (getRule() != null) {
            return "RuleRollUp_ " + getRule().getId() + "_" + getEventType();
        }
        return null;
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new RuleRollUpAlarm();
        }
        super.updateAlarmContext(baseAlarm, add);
        RuleRollUpAlarm ruleRollUpAlarm = (RuleRollUpAlarm) baseAlarm;

        ruleRollUpAlarm.setRule(rule);
        return baseAlarm;
    }

    @Override
    public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
        if (add && alarmOccurrence == null) {
            alarmOccurrence = new RuleRollUpOccurrence();
        }

        RuleRollUpOccurrence rollUpOccurrence = (RuleRollUpOccurrence) alarmOccurrence;

        rollUpOccurrence.setRule(rule);
        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }

    private ReadingRuleContext rule;
    public ReadingRuleContext getRule() {
        return rule;
    }
    public void setRule(ReadingRuleContext rule) {
        this.rule = rule;
    }

    @Override
    @JsonSerialize
    public BaseAlarmContext.Type getEventTypeEnum() {
        return BaseAlarmContext.Type.RULE_ROLLUP_ALARM;
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
}
