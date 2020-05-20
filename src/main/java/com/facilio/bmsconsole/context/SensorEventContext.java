package com.facilio.bmsconsole.context;

import org.apache.commons.chain.Context;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class SensorEventContext extends BaseEventContext {
	
	public String constructMessageKey() {
		if (getResource() != null && getSensorRule() != null) {
			return getSensorRule().getId() + "_" + getResource().getId()+"_"+getEventType();
		}
		return null;
	}
	
	@Override
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
		if (add && baseAlarm == null) {
			baseAlarm = new SensorAlarmContext();
		}
		super.updateAlarmContext(baseAlarm, add);
		SensorAlarmContext sensorAlarm = (SensorAlarmContext) baseAlarm;

		sensorAlarm.setSensorRule(sensorRule);
		if (readingFieldId != -1) {
			sensorAlarm.setReadingFieldId(readingFieldId);
		}
		return baseAlarm;
	}

	@Override
	public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
		if (add && alarmOccurrence == null) {
			alarmOccurrence = new SensorAlarmOccurrenceContext();
		}
		SensorAlarmOccurrenceContext sensorAlarmOccurrence = (SensorAlarmOccurrenceContext) alarmOccurrence;
		sensorAlarmOccurrence.setSensorRule(sensorRule);
		if (readingFieldId != -1) {
			sensorAlarmOccurrence.setReadingFieldId(readingFieldId);
		}
		return super.updateAlarmOccurrenceContext(alarmOccurrence, context, add);
	}

	private SensorRuleContext sensorRule;
	public SensorRuleContext getSensorRule() {
		return sensorRule;
	}
	public void setSensorRule(SensorRuleContext sensorRule) {
		this.sensorRule = sensorRule;
	}

	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}

	@JsonSerialize
	public Type getEventTypeEnum() {
		return Type.SENSOR_ALARM;
	}

	@JsonIgnore
	@JSON(deserialize = false)
	public void setRuleId(long ruleId) {
		if (ruleId > 0) {
			SensorRuleContext sensorRuleContext = new SensorRuleContext();
			sensorRuleContext.setId(ruleId);
			setSensorRule(sensorRuleContext);
		}
	}
}
