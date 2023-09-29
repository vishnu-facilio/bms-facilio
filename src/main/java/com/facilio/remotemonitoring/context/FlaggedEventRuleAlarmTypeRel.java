package com.facilio.remotemonitoring.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlaggedEventRuleAlarmTypeRel extends V3Context {
    private AlarmTypeContext alarmType;
    private FlaggedEventRuleContext flaggedEventRule;
}