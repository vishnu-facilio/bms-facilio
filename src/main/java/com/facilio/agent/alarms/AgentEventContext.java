package com.facilio.agent.alarms;

import com.facilio.bmsconsole.context.BaseEventContext;

public class AgentEventContext extends BaseEventContext {
    long agentId;

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }
}
