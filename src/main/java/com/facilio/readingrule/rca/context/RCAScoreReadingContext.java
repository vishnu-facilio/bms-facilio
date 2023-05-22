package com.facilio.readingrule.rca.context;

import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RCAScoreReadingContext extends V3Context {
    private Long parentId;
    private Long rcaRuleId;
    private Long rcaFaultId;
    private ReadingAlarm parentAlarm;
    private NewReadingRuleContext rcaRule;
    private ReadingAlarm rcaFault;
    private Long rcaGroupId;
    private Long rcaConditionId;
    private Long score;
    private Long actualTtime;
    private Long ttime;

    private Long count;
    private Long duration;
    public void setParentId(Long parentId){
        this.parentId = parentId;
        ReadingAlarm alarm = new ReadingAlarm();
        alarm.setId(parentId);
        this.setParentAlarm(alarm);
    }
    public void setRcaRuleId(Long rcaRuleId){
        this.rcaRuleId = rcaRuleId;
        NewReadingRuleContext rule = new NewReadingRuleContext();
        rule.setId(rcaRuleId);
        this.setRcaRule(rule);
    }
    public void setRcaFaultId(Long rcaFaultId){
        this.rcaFaultId = rcaFaultId;
        ReadingAlarm alarm = new ReadingAlarm();
        alarm.setId(rcaFaultId);
        this.setRcaFault(alarm);
    }
}
