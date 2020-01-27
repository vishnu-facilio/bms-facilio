package com.facilio.agent.alarms;

import com.facilio.bmsconsole.context.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.chain.Context;

public class AgentEventContext extends BaseEventContext {
    long agentId;

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    @Override
    public String constructMessageKey() {
        return "AgentAlarm_" + getAgentId();
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
        agentAlarmOccurrenceContext.setAgentId(agentId);

        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new AgentAlarmContext();
        }
        super.updateAlarmContext(baseAlarm, add);

        AgentAlarmContext alarm = (AgentAlarmContext) baseAlarm;
        alarm.setAgentId(getAgentId());


        return baseAlarm;
    }
}
