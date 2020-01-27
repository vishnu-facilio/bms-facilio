package com.facilio.agent.alarms;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;

public class AgentAlarmOccurrenceContext extends AlarmOccurrenceContext {
    long agentId;

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    @Override
    public Type getTypeEnum() {
        return Type.AGENT;
    }
}
