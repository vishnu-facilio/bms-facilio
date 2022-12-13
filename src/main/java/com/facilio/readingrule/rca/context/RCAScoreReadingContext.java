package com.facilio.readingrule.rca.context;

import com.facilio.bmsconsole.context.ReadingAlarm;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RCAScoreReadingContext extends V3Context {
    private ReadingAlarm parentId;
    private NewReadingRuleContext rcaRule;
    private ReadingAlarm rcaFault;
    private Long rcaGroupId;
    private Long rcaConditionId;
    private Long score;
    private Long actualTtime;
    private Long ttime;

    private Long count;
    private Long duration;
}
