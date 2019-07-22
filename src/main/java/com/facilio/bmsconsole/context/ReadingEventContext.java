package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ReadingEventContext extends BaseEventContext {
	private static final long serialVersionUID = 1L;

	@Override
	public String constructMessageKey() {
		if (getResource() != null) {
			return ruleId + "_" + getResource().getId();	
		}
		return null;
	}
	
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
	public Type getEventTypeEnum() {
		return Type.READING_ALARM;
	}
}
