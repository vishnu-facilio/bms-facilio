package com.facilio.bmsconsole.context;

import java.io.Serializable;
import java.util.List;

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
	
	private List<PublishMessage> messages;
	public List<PublishMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<PublishMessage> messages) {
		this.messages = messages;
	}
}
