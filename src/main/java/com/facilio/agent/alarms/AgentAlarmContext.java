package com.facilio.agent.alarms;

import com.facilio.agent.FacilioAgent;
import com.facilio.bmsconsole.context.BaseAlarmContext;

public class AgentAlarmContext extends BaseAlarmContext {


    private FacilioAgent agent;

    public FacilioAgent getAgent() {
        return agent;
    }

    public void setAgent(FacilioAgent agent) {
        this.agent = agent;
    }
}
