package com.facilio.agent.alarms;

import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
}
