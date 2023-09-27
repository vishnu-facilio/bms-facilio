package com.facilio.readingrule.rca.context;

import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private List<Long[]> booleanChartData;

    private Long count;
    private Long duration;
    public void setParentAlarm(ReadingAlarm parentAlarm){
        this.parentId = parentAlarm.getId();
        this.parentAlarm = parentAlarm;
    }
    public void setRcaRule(NewReadingRuleContext rcaRule){
        this.rcaRuleId = rcaRule.getId();
        this.rcaRule = rcaRule;
    }
    public void setRcaFault(ReadingAlarm rcaFault){
        this.rcaFaultId = rcaFault.getId();
        this.rcaFault = rcaFault;
    }
}
