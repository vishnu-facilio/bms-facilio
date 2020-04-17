package com.facilio.agent.alarms;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;

public class AgentAlarmOccurrenceContext extends AlarmOccurrenceContext {

    private FacilioAgent agent;

    private AgentAlarmContext.AgentAlarmType agentAlarmType;

    public int getAgentAlarmType() {
        if  (agentAlarmType!=null) {
            return agentAlarmType.getIndex();
        }else {
            return -1;
        }
    }
    public AgentAlarmContext.AgentAlarmType getAgentAlarmTypeEnum(){return agentAlarmType;}
    public void setAgentAlarmType(int agentAlarmType) {
        this.agentAlarmType = AgentAlarmContext.AgentAlarmType.valueOf(agentAlarmType);
    }
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
