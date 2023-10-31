package com.facilio.remotemonitoring.context;

import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
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
    private AlarmApproach alarmApproach;
    private Boolean filtered;
    private V3AssetContext asset;
    public AlarmApproach getAlarmApproachEnum() {
        return alarmApproach;
    }
    public Integer getAlarmApproach() {
        if (alarmApproach != null) {
            return alarmApproach.getIndex();
        }
        return null;
    }
    public void setAlarmApproach(Integer alarmApproach) {
        if(alarmApproach != null) {
            this.alarmApproach = AlarmApproach.valueOf(alarmApproach);
        }
    }
}