package com.facilio.agent.alarms;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.context.BaseAlarmContext;

public class ControllerAlarmContext extends BaseAlarmContext {

    private FacilioAgent agent;

    private String controllerList;

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
