package com.facilio.bmsconsole.context;

import com.facilio.time.DateTimeUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.fasterxml.jackson.annotation.JsonInclude;

public class AlarmOccurrenceContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	public AlarmOccurrenceContext() {
	}

	private String subject;					// for client,alarm bar handling
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	private AlarmSeverityContext severity;
	public AlarmSeverityContext getSeverity() {
		return severity;
	}
	public void setSeverity(AlarmSeverityContext severity) {
		this.severity = severity;
	}
	
	private String condition;
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	private String source;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	private BaseAlarmContext alarm;
	public BaseAlarmContext getAlarm() {
		return alarm;
	}
	public void setAlarm(BaseAlarmContext alarm) {
		this.alarm = alarm;
	}
	
	private AlarmSeverityContext previousSeverity;
	public AlarmSeverityContext getPreviousSeverity() {
		return previousSeverity;
	}
	public void setPreviousSeverity(AlarmSeverityContext previousSeverity) {
		this.previousSeverity = previousSeverity;
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
	
	private long createdTime = -1;
	public String getLastOccurredTimeString(){
		if(lastOccurredTime != -1) {
			return DateTimeUtil.getZonedDateTime(lastOccurredTime).format(DateTimeUtil.READABLE_DATE_FORMAT);
		}
		return null;
	}
	public String getCreatedTimeString() {
		if(createdTime != -1) {
			return DateTimeUtil.getZonedDateTime(createdTime).format(DateTimeUtil.READABLE_DATE_FORMAT);
		}
		return null;
	}
	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	private long lastOccurredTime = -1;
	public long getLastOccurredTime() {
		return lastOccurredTime;
	}

	public void setLastOccurredTime(long lastOccurredTime) {
		this.lastOccurredTime = lastOccurredTime;
	}
	private long clearedTime = -1;
	public long getClearedTime() {
		return clearedTime;
	}
	public void setClearedTime(long clearedTime) {
		this.clearedTime = clearedTime;
	}
	@JsonInclude
	public long getDuration() {
		if (clearedTime != -1) {
			return (clearedTime - createdTime) / 1000;
		}
		return -1;
	}
	
	private long timeBetweeenOccurrence = -1;

	public long getTimeBetweeenOccurrence() {
		return timeBetweeenOccurrence;
	}

	public void setTimeBetweeenOccurrence(long timeBetweeenOccurrence) {
		this.timeBetweeenOccurrence = timeBetweeenOccurrence;
	}

	private long woId = -1;
	public long getWoId() {
		return woId;
	}
	public void setWoId(long woId) {
		this.woId = woId;
	}
	
	private String problem;
	public String getProblem() {
		return problem;
	}
	public void setProblem(String problem) {
		this.problem = problem;
	}
	
	private String possibleCauses;
	public String getPossibleCauses() {
		return possibleCauses;
	}
	public void setPossibleCauses(String possibleCauses) {
		this.possibleCauses = possibleCauses;
	}
	
	private String recommendation;
	public String getRecommendation() {
		return recommendation;
	}
	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}
	
	private long noOfEvents = -1;
	public long getNoOfEvents() {
		return noOfEvents;
	}
	public void setNoOfEvents(long noOfEvents) {
		this.noOfEvents = noOfEvents;
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
	
	private ResourceContext resource;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}

	public JSONObject additionInfo;
	public JSONObject getAdditionInfo() {
		return additionInfo;
	}
	public void setAdditionInfo(JSONObject additionInfo) {
		this.additionInfo = additionInfo;
	}
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

	private Type type;
	@JsonInclude
	public int getType() {
		type = getTypeEnum();
		if (type != null) {
			return type.getIndex();
		}
		return -1;
	}
	// this will be used only to get data from database
	public void setType(int type) {
		this.type = Type.valueOf(type);
	}
	// this will be used only to get data from database
	public Type getTypeEnum() {
		if (type == null) {
			return Type.NORMAL;
		}
		return type;
	}

	public void updateAlarm(BaseAlarmContext baseAlarm) {
		baseAlarm.setSeverity(severity);
		if (baseAlarm.isAcknowledged()) {
			baseAlarm.setAcknowledged(isAcknowledged() ? false : isAcknowledged());
		}
		else {
			baseAlarm.setAcknowledged(isAcknowledged());
		}

		if (baseAlarm.getAcknowledgedBy() != null && baseAlarm.getAcknowledgedBy().getId() > 0) {
			User acknowledgedBy = getAcknowledgedBy();
			if (acknowledgedBy == null) {
				acknowledgedBy = new User();
				acknowledgedBy.setId(-99);
			}
			baseAlarm.setAcknowledgedBy(acknowledgedBy);
		}
		else {
			baseAlarm.setAcknowledgedBy(getAcknowledgedBy());
		}

		if (baseAlarm.getAcknowledgedTime() > 0) {
			baseAlarm.setAcknowledgedTime(getAcknowledgedTime() < 0 ? -99 : getAcknowledgedTime());
		}
		else {
			baseAlarm.setAcknowledgedTime(getAcknowledgedTime());
		}
		baseAlarm.setLastOccurrence(this);

		if (getWoId() > 0) {
			baseAlarm.setLastWoId(getWoId());
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof AlarmOccurrenceContext) {
			AlarmOccurrenceContext alarmOccurrence = (AlarmOccurrenceContext) obj;
			if (getId() != -1 && alarmOccurrence.getId() != -1) {
				return alarmOccurrence.getId() == getId();
			}
			// check created time.. assumption, no two alarm occurrence will be found with same created time
			else if (getCreatedTime() != -1 && alarmOccurrence.getCreatedTime() != -1) {
				return getCreatedTime() == alarmOccurrence.getCreatedTime();
			}
			return super.equals(obj);
		}
		return false;
	}

	public enum Type implements FacilioEnum {
		NORMAL,
		ANOMALY,
		READING,
		VIOLATION,
		AGENT,
		CONTROLLER,
		PRE_OCCURRENCE,
		OPERATION_OCCURRENCE,
		RULE_ROLLUP,
		ASSET_ROLLUP,
		SENSOR,
		SENSOR_ROLLUP,
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
