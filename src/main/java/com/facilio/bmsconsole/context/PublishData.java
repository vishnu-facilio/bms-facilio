package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.List;

import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;

public class PublishData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long controllerId = -1;
	public long getControllerId() {
		return controllerId;
	}
	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}
	
	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private long acknowledgeTime = -1;
	public long getAcknowledgeTime() {
		return acknowledgeTime;
	}
	public void setAcknowledgeTime(long acknowledgeTime) {
		this.acknowledgeTime = acknowledgeTime;
	}
	
	public boolean isAcknowledged() {
		return acknowledgeTime != -1;
	}
	
	private long responseAckTime = -1; 
	public long getResponseAckTime() {
		return responseAckTime;
	}
	public void setResponseAckTime(long responseAckTime) {
		this.responseAckTime = responseAckTime;
	}

	private List<PublishMessage> messages;
	public List<PublishMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<PublishMessage> messages) {
		this.messages = messages;
	}
	
	private IotCommandType command;
	public IotCommandType getCommandEnum() {
		return command;
	}
	public int getCommand() {
		if (command != null) {
			return command.getValue();
		}
		return -1;
	}
	
	public void setCommand(IotCommandType command) {
		this.command = command;
	}
	public void setCommand(int commandVal) {
		this.command = IotCommandType.valueOf(commandVal);
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder()
					.append("PublishData [")
					.append("id : ").append(id).append(", ")
					.append("controllerId : ").append(controllerId).append(", ")
					.append("createdTime : ").append(createdTime).append(", ")
					.append("acknowledgeTime : ").append(acknowledgeTime).append(", ")
					.toString();
	}
}
