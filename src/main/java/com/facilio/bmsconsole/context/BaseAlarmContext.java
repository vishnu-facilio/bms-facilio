package com.facilio.bmsconsole.context;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BaseAlarmContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private String key;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	private ResourceContext resource;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}
	
	private AlarmSeverityContext severity;
	public AlarmSeverityContext getSeverity() {
		return severity;
	}
	public void setSeverity(AlarmSeverityContext severity) {
		this.severity = severity;
	}
	
	private Type type;
	public int getType() {
		if (type == null) {
			return -1;
		}
		return type.getIndex();
	}
	public Type getTypeEnum() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public void setType(int type) {
		this.type = Type.valueOf(type);
	}
	
	private Boolean acknowledged;
	public Boolean getAcknowledged() {
		return acknowledged;
	}
	public void setAcknowledged(Boolean acknowledged) {
		this.acknowledged = acknowledged;
	}
	public Boolean isAcknowledged() {
		if (acknowledged == null) {
			return false;
		}
		return acknowledged;
	}
	
	private User acknowledgedBy;
	public User getAcknowledgedBy() {
		return acknowledgedBy;
	}
	public void setAcknowledgedBy(User acknowledgedBy) {
		this.acknowledgedBy = acknowledgedBy;
	}
	
	private long acknowledgedTime = -1;
	public long getAcknowledgedTime() {
		return acknowledgedTime;
	}
	public void setAcknowledgedTime(long acknowledgedTime) {
		this.acknowledgedTime = acknowledgedTime;
	}

	private long noOfOccurrences = -1;
	public long getNoOfOccurrences() {
		return noOfOccurrences;
	}
	public void setNoOfOccurrences(long noOfOccurrences) {
		this.noOfOccurrences = noOfOccurrences;
	}

	private long lastCreatedTime = -1;
	public long getLastCreatedTime() {
		return lastCreatedTime;
	}
	public void setLastCreatedTime(long lastCreatedTime) {
		this.lastCreatedTime = lastCreatedTime;
	}

	private long lastOccurredTime = -1;
	public long getLastOccurredTime() {
		return lastOccurredTime;
	}
	public void setLastOccurredTime(long lastOccurredTime) {
		this.lastOccurredTime = lastOccurredTime;
	}
	
	private long lastClearedTime = -1;
	public long getLastClearedTime() {
		return lastClearedTime;
	}
	public void setLastClearedTime(long lastClearedTime) {
		this.lastClearedTime = lastClearedTime;
	}

	private long lastWoId = -1;
	public long getLastWoId() {
		return lastWoId;
	}
	public void setLastWoId(long lastWoId) {
		this.lastWoId = lastWoId;
	}
	
	private int noOfNotes = -1;
	public int getNoOfNotes() {
		return noOfNotes;
	}
	public void setNoOfNotes(int noOfNotes) {
		this.noOfNotes = noOfNotes;
	}

	private AlarmOccurrenceContext lastOccurrence;
	@JsonIgnore
	public AlarmOccurrenceContext getLastOccurrence() {
		return lastOccurrence;
	}
	@JSON(serialize = false)
	@JsonIgnore
	public void setLastOccurrence(AlarmOccurrenceContext lastOccurrence) {
		this.lastOccurrence = lastOccurrence;
	}
	public long getLastOccurrenceId() {
		if (lastOccurrence != null) {
			return lastOccurrence.getId();
		}
		return -1;
	}
	public void setLastOccurrenceId(long occurrenceId) {
		if (occurrenceId > 0) {
			lastOccurrence = new AlarmOccurrenceContext();
			lastOccurrence.setId(occurrenceId);
		}
	}

	private List<BaseEventContext> additionalEvents;
	public List<BaseEventContext> getAdditionalEvents() {
		return additionalEvents;
	}
	public void setAdditionalEvents(List<BaseEventContext> additionalEvents) {
		this.additionalEvents = additionalEvents;
	}
	public void addAdditionalEvent(BaseEventContext baseEvent) {
		if (additionalEvents == null) {
			additionalEvents = new ArrayList<>();
		}
		additionalEvents.add(baseEvent);
	}

	public List<BaseEventContext> removeAdditionalEvents() {
		List<BaseEventContext> additionalEvents = getAdditionalEvents();
		setAdditionalEvents(null);
		return additionalEvents;
	}

	public static enum Type implements FacilioEnum {
		READING_ALARM,
		ML_ANOMALY_ALARM,
		RCA_ALARM,
		READING_RCA_ALARM,
		BMS_ALARM,
		VIOLATION_ALARM,
		AGENT_ALARM,
		CONTROLLER_ALARM,
		PRE_ALARM,
		OPERATION_ALARM;
		;
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}
		
		public static Type valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
