package com.facilio.agent.alarms;

import com.facilio.agent.FacilioAgent;
import com.facilio.bmsconsole.context.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.chain.Context;

public class AgentEventContext extends BaseEventContext {
    private FacilioAgent agent;

    @Override
    public String constructMessageKey() {
        Long agentId=-1L;
        if (this.agent!=null){
            agentId= this.agent.getId();
        }
        return "AgentAlarm_" +agentId;
    }

    @Override
    @JsonSerialize
    public BaseAlarmContext.Type getEventTypeEnum() {
        return BaseAlarmContext.Type.AGENT_ALARM;
    }

    @Override
    public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
        if (add && alarmOccurrence == null) {
            alarmOccurrence = new AgentAlarmOccurrenceContext();
        }
        AgentAlarmOccurrenceContext agentAlarmOccurrenceContext = (AgentAlarmOccurrenceContext) alarmOccurrence;
        agentAlarmOccurrenceContext.setAgent(getAgent());

        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new AgentAlarmContext();
        }
        super.updateAlarmContext(baseAlarm, add);

        AgentAlarmContext alarm = (AgentAlarmContext) baseAlarm;
        alarm.setAgent(getAgent()); 


        return baseAlarm;
    }

    public void setAgent(FacilioAgent agent) {
        this.agent = agent;
    }

    public FacilioAgent getAgent() {
        return agent;
    }
}
