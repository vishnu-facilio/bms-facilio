package com.facilio.bmsconsole.context;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;

public class ReadingEventContext extends BaseEventContext {
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessageKey() {
		String key = super.getMessageKey();
		if (StringUtils.isEmpty(key)) {
			if (getResource() != null) {
				setMessageKey(ruleId + "_" + getResource().getId());	
			}
			else {
				setMessageKey(ruleId + "_sampleresource");
			}
		}
		return super.getMessageKey();
	}
	
//	@Override
//	public BaseAlarmContext createAlarm() {
//		ReadingAlarm readingAlarm = new ReadingAlarm();
//		updateAlarmContext(readingAlarm, true);
//		return readingAlarm;
//	}
//	
//	@Override
//	public void updateAlarm(BaseAlarmContext baseAlarm) {
//		updateAlarmContext(baseAlarm, false);
//	}
//	
//	@Override
//	public void updateAlarmOccurrence(AlarmOccurrenceContext alarmOccurrence) {
//		updateAlarmOccurrenceContext(alarmOccurrence, false);
//	}
//	
//	@Override
//	public AlarmOccurrenceContext createAlarmOccurrence() {
//		AlarmOccurrenceContext alarmOccurrence = new AlarmOccurrenceContext();
//		
//		updateAlarmOccurrenceContext(alarmOccurrence, true);
//		
//		return alarmOccurrence;
//	}
	
	@Override
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) {
		if (add && baseAlarm == null) {
			baseAlarm = new ReadingAlarm();
		}
		super.updateAlarmContext(baseAlarm, add);
		ReadingAlarm readingAlarm = (ReadingAlarm) baseAlarm;

		if (ruleId != -1) {
			readingAlarm.setRuleId(ruleId);
		}
		if (subRuleId != -1) {
			readingAlarm.setSubRuleId(subRuleId);
		}
		if (readingFieldId != -1) {
			readingAlarm.setReadingFieldId(readingFieldId);
		}
		return baseAlarm;
	}
	
	private long ruleId = -1;
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}

	private long subRuleId = -1;
	public long getSubRuleId() {
		return subRuleId;
	}
	public void setSubRuleId(long subRuleId) {
		this.subRuleId = subRuleId;
	}

	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}

	@Override
	@JsonSerialize
	public Type getAlarmType() {
		return Type.READING_ALARM;
	}
}
