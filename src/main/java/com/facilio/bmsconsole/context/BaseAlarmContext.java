package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.accounts.dto.User;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
	public String getLastCreatedTimeString() {
		if(lastCreatedTime != -1) {
			return DateTimeUtil.getFormattedTime(lastCreatedTime);
		}
		return null;
	}

	private long lastOccurredTime = -1;
	public long getLastOccurredTime() {
		return lastOccurredTime;
	}
	public void setLastOccurredTime(long lastOccurredTime) {
		this.lastOccurredTime = lastOccurredTime;
	}
	public String getLastOccurredTimeString() {
		if(lastOccurredTime != -1) {
			return DateTimeUtil.getFormattedTime(lastOccurredTime);
		}
		return null;
	}
	
	private long lastClearedTime = -1;
	public long getLastClearedTime() {
		return lastClearedTime;
	}
	public void setLastClearedTime(long lastClearedTime) {
		this.lastClearedTime = lastClearedTime;
	}
	public String getLastClearedTimeString() {
		if(lastClearedTime != -1) {
			return DateTimeUtil.getFormattedTime(lastClearedTime);
		}
		return null;
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

	@JsonIgnore
	private List<NewEventsToAlarmsConversionCommand.PostTransactionEventListener> posList;
	public void addPostTransactionEventListener(NewEventsToAlarmsConversionCommand.PostTransactionEventListener pos) {
		if (pos == null) {
			return;
		}
		if (posList == null) {
			posList = new ArrayList<>();
		}
		posList.add(pos);
	}
	public List<NewEventsToAlarmsConversionCommand.PostTransactionEventListener> getPosList() {
		return posList;
	}
	public List<NewEventsToAlarmsConversionCommand.PostTransactionEventListener> removePosList() {
		List<NewEventsToAlarmsConversionCommand.PostTransactionEventListener> posList = getPosList();
		this.posList = null;
		return posList;
	}

	public static enum Type implements FacilioIntEnum {
		READING_ALARM,
		ML_ANOMALY_ALARM,
		RCA_ALARM,
		READING_RCA_ALARM,
		BMS_ALARM,
		VIOLATION_ALARM,
		AGENT_ALARM,
		CONTROLLER_ALARM,
		PRE_ALARM,
		OPERATION_ALARM,
		RULE_ROLLUP_ALARM,
		ASSET_ROLLUP_ALARM,
		SENSOR_ALARM, 
		SENSOR_ROLLUP_ALARM,
		MULTIVARIATE_ANOMALY_ALARM,
		;
		public Integer getIndex() {
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
