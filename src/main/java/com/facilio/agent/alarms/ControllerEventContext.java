package com.facilio.agent.alarms;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.chain.Context;

public class ControllerEventContext extends BaseEventContext {
    
    private FacilioAgent agent;
    
    private String controllerList;

    public String constructMessageKey() {
        Long agentId=-1L;
        if (this.agent!=null){
            agentId= this.agent.getId();
        }
        return "ControllerAlarm_" +agentId;
    }
    @Override
    @JsonSerialize
    public BaseAlarmContext.Type getEventTypeEnum() {
        return BaseAlarmContext.Type.CONTROLLER_ALARM;
    }

    @Override
    public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
        if (add && alarmOccurrence == null) {
            alarmOccurrence = new ControllerOccurrenceContext();
        }
        ControllerOccurrenceContext controllerOccurrenceContext = (ControllerOccurrenceContext) alarmOccurrence;
        controllerOccurrenceContext.setAgent(getAgent());
        
        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new ControllerAlarmContext();
        }
        super.updateAlarmContext(baseAlarm, add);

        ControllerAlarmContext alarm = (ControllerAlarmContext) baseAlarm;
        alarm.setAgent(getAgent());
        alarm.setControllerList(getControllerList());


        return baseAlarm;
    }
    public FacilioAgent getAgent() {
        return agent;
    }
    public void setAgent(FacilioAgent agent) {
        this.agent = agent;
    }

    public String getControllerList() {
        return controllerList;
    }

    public void setControllerList(String controllerList) {
        this.controllerList = controllerList;
    }
}
