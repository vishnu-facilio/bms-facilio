package com.facilio.bmsconsole.context;

import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;

public abstract class BaseEventContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private String eventMessage;
	public String getEventMessage() {
		return eventMessage;
	}
	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}
	public String getMessage() {
		return eventMessage;
	}
	public void setMessage(String message) {
		this.eventMessage = message;
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
	
	private Type alarmType;
	public Type getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(Type alarmType) {
		this.alarmType = alarmType;
	}
	
	private Boolean autoClear;
	public Boolean getAutoClear() {
		if (autoClear == null) {
			return false;
		}
		return autoClear;
	}
	public void setAutoClear(Boolean autoClear) {
		this.autoClear = autoClear;
	}
	
	private String possibleCause;
	public String getPossibleCause() {
		return possibleCause;
	}
	public void setPossibleCause(String possibleCause) {
		this.possibleCause = possibleCause;
	}
	
	private String recommendation;
	public String getRecommendation() {
		return recommendation;
	}
	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}
	
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) {
		if (StringUtils.isNotEmpty(getEventMessage())) {
			baseAlarm.setSubject(getEventMessage());
		}
		baseAlarm.setDescription(getDescription());

		if (add) {
			baseAlarm.setResource(getResource());
			baseAlarm.setKey(getMessageKey());
		}
		return baseAlarm;
	}
	
	public void updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, boolean add) throws Exception {
		AlarmSeverityContext previousSeverity = alarmOccurrence.getSeverity();
		alarmOccurrence.setSeverity(getSeverity());
		alarmOccurrence.setAutoClear(getAutoClear());
		alarmOccurrence.setLastOccurredTime(getCreatedTime());
		
		AlarmSeverityContext clearSeverity = AlarmAPI.getAlarmSeverity("Clear");
		if (getSeverity().equals(clearSeverity)) {
			alarmOccurrence.setClearedTime(getCreatedTime());
		}
		
		if (add) {
			alarmOccurrence.setCreatedTime(getCreatedTime());
		} 
		else {
			if (!previousSeverity.equals(getSeverity())) {
				if (!getSeverity().equals(clearSeverity)) {
					alarmOccurrence.setAcknowledged(false);
					alarmOccurrence.setAcknowledgedBy(null);
					alarmOccurrence.setAcknowledgedTime(-1l);
				}
				alarmOccurrence.setPreviousSeverity(previousSeverity);
			}
		}
		
		if (StringUtils.isNotEmpty(getPossibleCause())) {
			String possibleCauses = alarmOccurrence.getPossibleCauses();
			if (StringUtils.isNotEmpty(possibleCauses)) {
				possibleCauses += "\n" + getPossibleCause();
			} else {
				possibleCauses = getPossibleCause();
			}
			alarmOccurrence.setPossibleCauses(possibleCauses);
		}
		
		if (StringUtils.isNotEmpty(getRecommendation())) {
			String recommendations = alarmOccurrence.getRecommendation();
			if (StringUtils.isNotEmpty(recommendations)) {
				recommendations += "\n" + getRecommendation();
			} 
			else {
				recommendations = getRecommendation();
			}
			alarmOccurrence.setRecommendation(recommendations);
		}
	}
	
	public static BaseEventContext createNewEvent(Type typeEnum, ResourceContext resourceContext, AlarmSeverityContext alarmSeverity, String message, String messageKey, long createdTime) {
		BaseEventContext baseEvent = null;
		switch (typeEnum) {
		case READING_ALARM:
			baseEvent = new ReadingEventContext();
			break;

		default:
			throw new IllegalArgumentException("Invalid type");
		}
		
		if (createdTime == -1) {
			createdTime = DateTimeUtil.getCurrenTime();
		}
		
		if (alarmSeverity == null) {
			throw new IllegalArgumentException("Severity cannot be empty");
		}
		
		baseEvent.setCreatedTime(createdTime);
		baseEvent.setSeverity(alarmSeverity);
		baseEvent.setSeverityString(alarmSeverity.getSeverity());
		baseEvent.setAlarmType(typeEnum);
		baseEvent.setEventMessage(message);
		baseEvent.setMessageKey(messageKey);
		baseEvent.setComment("Automated event");
		baseEvent.setResource(resourceContext);
		
		return baseEvent;
	}
}
