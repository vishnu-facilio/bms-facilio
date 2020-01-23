package com.facilio.agent.alarms;

import com.facilio.bmsconsole.context.BaseAlarmContext;

public class AgentAlarmContext extends BaseAlarmContext {
    long agentId;

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }
}
