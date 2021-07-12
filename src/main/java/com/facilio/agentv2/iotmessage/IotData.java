package com.facilio.agentv2.iotmessage;

import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.FacilioAgent;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class IotData {
    /* ID
     * ORGID
     * AGENT_ID
     * CONTROLLER_ID
     * COMMAND
     * CREATED_TIME*/

    private long orgId = -1;
    private long id = -1;
    private long agentId = -1;
    private long controllerId = -1;
    FacilioCommand facilioCommand;
    private long createdTime = -1;
    private List<IotMessage> messages;

    @JsonIgnore
    private FacilioAgent agent;

    @JsonIgnore
    public FacilioAgent getAgent() {
        return agent;
    }

    @JsonIgnore
    public void setAgent(FacilioAgent agent) {
        this.agent = agent;
    }

    public long getOrgId() { return orgId; }
    public void setOrgId(long orgId) { this.orgId = orgId; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getAgentId() { return agentId; }
    public void setAgentId(long agentId) { this.agentId = agentId; }

    public long getControllerId() { return controllerId; }
    public void setControllerId(long controllerId) { this.controllerId = controllerId; }

    @JsonIgnore
    public FacilioCommand getFacilioCommand() { return facilioCommand; }
    public void setFacilioCommand(FacilioCommand facilioCommand) { this.facilioCommand = facilioCommand; }

    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }

    public int getCommand() {
        if(facilioCommand != null){
           return facilioCommand.asInt();
        }
        return -1;
    }
    public void setCommand(int command) { this.facilioCommand = FacilioCommand.valueOf(command); }

    @JsonIgnore
    public List<IotMessage> getMessages() { return messages; }

    public void setMessages(List<IotMessage> messages) { this.messages = messages; }

}
