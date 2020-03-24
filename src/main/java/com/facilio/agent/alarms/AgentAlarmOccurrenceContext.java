package com.facilio.agent.alarms;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;

public class AgentAlarmOccurrenceContext extends AlarmOccurrenceContext {

    private FacilioAgent agent;

    @Override
    public Type getTypeEnum() {
        return Type.AGENT;
    }

    public void setAgent(FacilioAgent agent) {
        this.agent = agent;
    }

    public FacilioAgent getAgent() {
        return agent;
    }
}
