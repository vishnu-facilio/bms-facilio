package com.facilio.bmsconsole.context;

import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public abstract class BaseEventContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private String condition;
	public String getCondition() {
		if (condition == null && eventMessage != null) {
			condition = eventMessage;
		}
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	private String eventMessage;
	public String getEventMessage() {
		if (eventMessage == null && condition != null) {
			eventMessage = condition;
		}
		return eventMessage;
	}
	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
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
	
	private AlarmOccurrenceContext alarmOccurrence;
	public AlarmOccurrenceContext getAlarmOccurrence() {
		return alarmOccurrence;
	}
	public void setAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence) {
		this.alarmOccurrence = alarmOccurrence;
	}
	
	private String messageKey;
	public String getMessageKey() {
		return messageKey;
	}
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	};
	
	private EventState eventState;
	public int getEventState() {
		if(eventState != null) {
			return eventState.getIntVal();
		}
		return -1;
	}
	public void setEventState(int eventState) {
		this.eventState = EventContext.EVENT_STATES[eventState - 1];
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
		this.internalState = EventContext.INETERNAL_STATES[internalState-1];
	}
	public void setInternalState(EventInternalState internalState) {
		this.internalState = internalState;
	}
	public EventInternalState getInternalStateEnum() {
		return internalState;
	}
	
	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private String state;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	private String priority;
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	private String severityString;
	public String getSeverityString() {
		return severityString;
	}
	public void setSeverityString(String severityString) {
		this.severityString = severityString;
	}
	
	private AlarmSeverityContext severity;
	public AlarmSeverityContext getSeverity() {
		return severity;
	}
	public void setSeverity(AlarmSeverityContext severity) {
		this.severity = severity;
	}
	
	private String comment;
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	private JSONObject additionInfo;
//	@JsonAnyGetter
	public JSONObject getAdditionInfo() {
		return additionInfo;
	}
	public void setAdditionInfo(JSONObject additionInfo) {
		this.additionInfo = additionInfo;
	}
//	@JsonAnySetter
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
	
	private Type alarmType;
	public Type getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(Type alarmType) {
		this.alarmType = alarmType;
	}
	
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) {
		baseAlarm.setSubject(getEventMessage());
		baseAlarm.setDescription(getDescription());

		if (add) {
			baseAlarm.setResource(getResource());
			baseAlarm.setKey(getMessageKey());
		}
		
		if (getAdditionInfo() != null) {
			// TODO check auto clear
//			baseAlarm.put("autoClear", getAdditionInfo().get("autoClear"));
		}
		return baseAlarm;
	}
	
	public void updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, boolean add) {
		alarmOccurrence.setSeverity(getSeverity());
		
		if (add) {
			alarmOccurrence.setCondition(getCondition());
			alarmOccurrence.setSeverity(getSeverity());
//			alarmOccurrence.setProblem(getpro);
		}
	}
}
