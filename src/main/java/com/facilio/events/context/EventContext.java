package com.facilio.events.context;

import java.util.StringJoiner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.context.ResourceContext;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class EventContext {
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
	
	private String entity;
	public String getEntity() {
		if (entity == null && eventMessage != null) {
			entity = eventMessage;
		}
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}

	private String source;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	private ResourceContext resource;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}

	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	private String eventMessage;
	public String getEventMessage() {
		if (eventMessage == null && entity != null) {
			eventMessage = entity;
		}
		return eventMessage;
	}
	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}
	
	private String messageKey;
	public String getMessageKey() {
		if(messageKey == null)
		{
			StringJoiner joiner = new StringJoiner("_");
			if (source != null) {
				joiner.add(source);
			}
			if (entity != null) {
				joiner.add(entity);
			}
			this.messageKey = joiner.toString();
		}
		return messageKey;
	}
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	private String severity;
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	
	private String priority;
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	private String alarmClass;
	public String getAlarmClass() {
		return alarmClass;
	}
	public void setAlarmClass(String alarmClass) {
		this.alarmClass = alarmClass;
	}
	
	private String state;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private EventState eventState;
	public int getEventState() {
		if(eventState != null) {
			return eventState.getIntVal();
		}
		return -1;
	}
	public void setEventState(int eventState) {
		this.eventState = EVENT_STATES[eventState - 1];
	}
	public void setEventState(EventState eventState) {
		this.eventState = eventState;
	}
	public EventState getEventStateEnum() {
		return eventState;
	}

	private EventInternalState internalState;
	public int getInternalState() {
		if(internalState != null) {
			return internalState.getIntVal();
		}
		return -1;
	}
	public void setInternalState(int internalState) {
		this.internalState = INETERNAL_STATES[internalState-1];
	}
	public void setInternalState(EventInternalState internalState) {
		this.internalState = internalState;
	}
	public EventInternalState getInternalStateEnum() {
		return internalState;
	}
	
	private long eventRuleId = -1;
	public long getEventRuleId() {
		return eventRuleId;
	}
	public void setEventRuleId(long eventRuleId) {
		this.eventRuleId = eventRuleId;
	}
	
	private long alarmId = -1;
	public long getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}

	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	private JSONObject additionInfo;
	@JsonAnyGetter
	public JSONObject getAdditionInfo() {
		return additionInfo;
	}
	public void setAdditionInfo(JSONObject additionInfo) {
		this.additionInfo = additionInfo;
	}
	@JsonAnySetter
	public void addAdditionInfo(String key, Object value) {
		if(this.additionInfo == null) {
			this.additionInfo =  new JSONObject();
		}
		this.additionInfo.put(key,value);
	}
	
	public String getAdditionalInfoJsonStr() {
		if(additionInfo != null) {
			return additionInfo.toJSONString();
		}
		return null;
	}
	public void setAdditionalInfoJsonStr(String jsonStr) throws ParseException {
		JSONParser parser = new JSONParser();
		additionInfo = (JSONObject) parser.parse(jsonStr);
	}

	private static final EventInternalState[] INETERNAL_STATES = EventInternalState.values();
	public static enum EventInternalState {
		ADDED,
		FILTERED,
		TRANSFORMED,
		THRESHOLD_DONE,
		CO_RELATION_DONE,
		COMPLETED
		;
		
		public int getIntVal() {
			return ordinal()+1;
		}
	}
	
	private static final EventState[] EVENT_STATES = EventState.values();
	public static enum EventState {
		READY,
		IGNORED,
		ALARM_CREATED,
		ALARM_UPDATED
		;
		
		public int getIntVal() {
			return ordinal()+1;
		}
	}
}
