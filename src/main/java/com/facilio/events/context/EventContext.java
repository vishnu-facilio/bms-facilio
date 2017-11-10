package com.facilio.events.context;

public class EventContext {
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String source;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	public String eventType;
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String node;
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}

	public String messageKey;
	public String getMessageKey() {
		if(messageKey == null)
		{
			this.messageKey = this.source + "_" + this.node + "_" + this.eventType;
		}
		return messageKey;
	}
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public String severity;
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String additionInfo;
	public String getAdditionInfo() {
		return additionInfo;
	}

	public void setAdditionInfo(String additionInfo) {
		this.additionInfo = additionInfo;
	}
	
	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public String state;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public Integer internalState;
	public Integer getInternalState() {
		return internalState;
	}
	public void setInternalState(Integer internalState) {
		this.internalState = internalState;
	}
	
	private long eventRuleId = -1;
	public long getEventRuleId() {
		return eventRuleId;
	}
	public void setEventRuleId(long eventRuleId) {
		this.eventRuleId = eventRuleId;
	}
}
