package com.facilio.bmsconsole.context;

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
		readingAlarm.setRuleId(getRuleId());
		return baseAlarm;
	}
	
	@Override
	public void updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, boolean add) throws Exception {
		super.updateAlarmOccurrenceContext(alarmOccurrence, add);
	}
	
	private long ruleId = -1;
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	
	@Override
	public Type getAlarmType() {
		return Type.READING_ALARM;
	}
}
