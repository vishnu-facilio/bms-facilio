package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Getter
@Setter
public class AlarmOccurrenceContext extends V3Context {
	private static final long serialVersionUID = 1L;

	public AlarmOccurrenceContext() {
	}

	private String subject;					// for client,alarm bar handling

	private AlarmSeverityContext severity;

	private BaseAlarmContext alarm;

	private AlarmSeverityContext previousSeverity;

	private Boolean acknowledged;

	public Boolean isAcknowledged() {
		if (acknowledged == null) {
			return false;
		}
		return acknowledged;
	}
	
	private User acknowledgedBy;

	private long acknowledgedTime = -1;

	private long createdTime = -1;

	public String getCreatedTimeString() {
		if(createdTime != -1) {
			return DateTimeUtil.getZonedDateTime(createdTime).format(DateTimeUtil.READABLE_DATE_FORMAT);
		}
		return null;
	}

	private long lastOccurredTime = -1;

	public String getLastOccurredTimeString(){
		if(lastOccurredTime != -1) {
			return DateTimeUtil.getZonedDateTime(lastOccurredTime).format(DateTimeUtil.READABLE_DATE_FORMAT);
		}
		return null;
	}

	private long clearedTime = -1;

	@JsonInclude
	public long getDuration() {
		if (clearedTime != -1) {
			return (clearedTime - createdTime) / 1000;
		}
		return -1;
	}
	
	private long timeBetweeenOccurrence = -1;

	private long woId = -1;

	private String problem;

	private String possibleCauses;

	private String recommendation;

	private long noOfEvents = -1;

	private Boolean autoClear;
	public Boolean getAutoClear() {
		if (autoClear == null) {
			return false;
		}
		return autoClear;
	}
	
	private ResourceContext resource;

	public JSONObject additionInfo;

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

	public boolean isNewReadingRule;

    public void setNewReadingRule(boolean isNewReadingRule) {
        this.isNewReadingRule = isNewReadingRule;
    }

    public boolean getIsNewReadingRule() {
        return isNewReadingRule;
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

	public enum Type implements FacilioIntEnum {
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
		MULTIVARIATE_ANOMALY,
		BMS,
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
