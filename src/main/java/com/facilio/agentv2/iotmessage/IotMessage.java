package com.facilio.agentv2.iotmessage;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lombok.Getter;
import lombok.Setter;

public class IotMessage
{
    /**
     *ID
     * 	 * ORGID
     * 	 * PARENT_ID
     * 	 * STATUS
     * 	 * SENT_TIME
     * 	 * ACKNOWLEDGE_TIME
     * 	 * COMPLETED_TIME
     * 	 * MSG_DATA
     * 	 * @return
     */

    private long id = -1;
    private long orgId = -1;
    private long parentId = -1;
    private int status = -1;
    //private String msgData;
    private long ackTime = -1;
    private long completedTime = -1;
    private long sentTime = -1;
    private int command = -1;
    @Getter @Setter
    private List<Long> controlIds;
    private long agentId = -1;

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }


    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsgData() {
        if (messageData != null) {
            return messageData.toJSONString();
        }
        return new String();
    }

    public void setMsgData(String msgData) throws ParseException {
        JSONParser parser = new JSONParser();
        messageData = (JSONObject) parser.parse(msgData);
    }

    public long getAckTime() { return ackTime; }
    public void setAckTime(long ackTime) { this.ackTime = ackTime; }

    public long getCompletedTime() { return completedTime; }
    public void setCompletedTime(long completedTime) { this.completedTime = completedTime; }


    private JSONObject messageData;
    public JSONObject getMessageData() { return messageData; }
    public void setMessageData(JSONObject messageData) { this.messageData = messageData; }

    public long getAgentId() { return agentId; }
    public void setAgentId(long agentId) { this.agentId = agentId; }
}
