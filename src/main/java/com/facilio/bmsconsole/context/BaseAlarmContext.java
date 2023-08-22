package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.events.commands.NewEventsToAlarmsConversionCommand;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.json.annotations.JSON;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BaseAlarmContext extends V3Context {
	private static final long serialVersionUID = 1L;

	private String subject;

	private String description;

	private String key;

	private ResourceContext resource;

	private AlarmSeverityContext severity;

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

	public Boolean isAcknowledged() {
		if (acknowledged == null) {
			return false;
		}
		return acknowledged;
	}
	
	private User acknowledgedBy;

	private long acknowledgedTime = -1;

	private long noOfOccurrences = -1;

	private long lastCreatedTime = -1;

	public String getLastCreatedTimeString() {
		if(lastCreatedTime != -1) {
			return DateTimeUtil.getFormattedTime(lastCreatedTime);
		}
		return null;
	}

	private long lastOccurredTime = -1;

	public String getLastOccurredTimeString() {
		if(lastOccurredTime != -1) {
			return DateTimeUtil.getFormattedTime(lastOccurredTime);
		}
		return null;
	}
	
	private long lastClearedTime = -1;

	public String getLastClearedTimeString() {
		if(lastClearedTime != -1) {
			return DateTimeUtil.getFormattedTime(lastClearedTime);
		}
		return null;
	}

	private long lastWoId = -1;

	private int noOfNotes = -1;

	private AlarmOccurrenceContext lastOccurrence;

	private AlarmOccurrenceContext prevOccurrence;

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


	public enum Type implements FacilioIntEnum {
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
