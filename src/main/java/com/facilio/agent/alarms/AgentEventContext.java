package com.facilio.agent.alarms;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsole.context.*;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.chain.Context;

import java.util.List;

public class AgentEventContext extends BaseEventContext {
    private FacilioAgent agent;
    private AgentAlarmContext.AgentAlarmType agentAlarmType;
    private List<Controller> controllersList;


    public List<Controller> getControllersList() {
        return controllersList;
    }

    public void setControllersList(List<Controller> controllersList) {
        this.controllersList = controllersList;
    }

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
    public String constructMessageKey() {
        Long agentId=-1L;
        if (this.agent!=null){
            agentId= this.agent.getId();
        }
        return "AgentAlarm_"+getAgentAlarmType()+"_" +agentId;
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
        agentAlarmOccurrenceContext.setAgent(getAgent());
        agentAlarmOccurrenceContext.setAgentAlarmType(getAgentAlarmType());
        return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
    }

    @Override
    public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
        if (add && baseAlarm == null) {
            baseAlarm = new AgentAlarmContext();
        }
        super.updateAlarmContext(baseAlarm, add);

        AgentAlarmContext alarm = (AgentAlarmContext) baseAlarm;
        alarm.setAgent(getAgent());
        alarm.setControllersList(getControllersList());
        alarm.setAgentAlarmType(getAgentAlarmType());
        return baseAlarm;
    }

    public void setAgent(FacilioAgent agent) {
        this.agent = agent;
    }

    public FacilioAgent getAgent() {
        return agent;
    }
}
