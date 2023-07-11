package com.facilio.remotemonitoring.context;

import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ControllerAlarmInfoContext extends V3Context {
    private Controller controller;
    private AlarmDefinitionContext alarmDefinition;
    private AlarmTypeContext alarmType;
    private Long alarmLastReceivedTime;
    private AlarmStrategy strategy;
    private Boolean filtered;
    public AlarmStrategy getStrategyEnum() {
        return strategy;
    }
    public Integer getStrategy() {
        if (strategy != null) {
            return strategy.getIndex();
        }
        return null;
    }
    public void setStrategy(Integer strategy) {
        if(strategy != null) {
            this.strategy = AlarmStrategy.valueOf(strategy);
        }
    }
}