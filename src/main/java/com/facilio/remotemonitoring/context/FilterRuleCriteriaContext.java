package com.facilio.remotemonitoring.context;

import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FilterRuleCriteriaContext extends V3Context {
    private AlarmDefinitionContext alarmDefinition;
    private AlarmFilterRuleContext alarmFilterRule;
    private ControllerType controllerType;
    private AlarmFilterCriteriaType filterCriteria;
    private Long alarmDuration;
    private Long alarmCount;
    private Long alarmCountPeriod;
    private Long alarmClearPeriod;

    public Integer getControllerTypeIndex() {
        if (controllerType != null) {
            return controllerType.getIndex();
        }
        return null;
    }

    public Integer getControllerType() {
        if (controllerType != null) {
            return controllerType.getIndex();
        }
        return null;
    }
    public void setControllerType(Integer controllerType) {
        if(controllerType != null && controllerType >= 0) {
            this.controllerType = ControllerType.valueOf(controllerType);
        }
    }
}