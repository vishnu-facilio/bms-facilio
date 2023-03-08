package com.facilio.agent.alarms;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.modules.FacilioIntEnum;

import java.util.List;

public class AgentAlarmContext extends BaseAlarmContext {


    private FacilioAgent agent;
    private AgentAlarmType agentAlarmType;
    private List<Controller> controllersList;
    private boolean notified;
    private long pointsDataMissingCount;

    public void setAgentAlarmType(AgentAlarmType agentAlarmType) {
        this.agentAlarmType = agentAlarmType;
    }

    public boolean getNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public FacilioAgent getAgent() {
        return agent;
    }

    public void setAgent(FacilioAgent agent) {
        this.agent = agent;
    }

    public int getAgentAlarmType() {
        if  (agentAlarmType!=null) {
            return agentAlarmType.getIndex();
        }else {
            return -1;
        }
    }

    public void setAgentAlarmType(int agentAlarmType) {
        this.agentAlarmType = AgentAlarmType.valueOf(agentAlarmType);
    }

    public AgentAlarmType getAgentAlarmTypeEnum(){
        return agentAlarmType;
    }

    public List<Controller> getControllersList() {
        return controllersList;
    }

    public void setControllersList(List<Controller> controllersList) {
        this.controllersList = controllersList;
    }

    public void setPointsDataMissingCount(long missingCount) { this.pointsDataMissingCount = missingCount; }

    public Long getPointsDataMissingCount() { return pointsDataMissingCount; }

    public static enum AgentAlarmType implements FacilioIntEnum {
        AGENT("Agent"),
        CONTROLLER("Controller"),
        POINT("Point"),
        COMMAND_DELAY("Command Delay")
        ;

        private String name;
        AgentAlarmType(String name) {
            this.name = name;
        }

        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static AgentAlarmType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
}
