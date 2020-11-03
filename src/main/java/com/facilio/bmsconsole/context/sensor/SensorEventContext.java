package com.facilio.bmsconsole.context.sensor;

import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class SensorEventContext extends BaseEventContext {
	
	public String constructMessageKey() {
		if (getResource() != null && getSensorRule() != null) {
			return "Sensor_" + getReadingFieldId() +"_"+ getResource().getId() +"_"+ getSensorRule().getId();
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
		sensorAlarm.setSensorRuleType(sensorRuleType);
		sensorAlarm.setMeterRollUp(meterRollUp);
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
		sensorAlarmOccurrence.setSensorRuleType(sensorRuleType);
		sensorAlarmOccurrence.setMeterRollUp(meterRollUp);
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

	private SensorRuleType sensorRuleType;
	public SensorRuleType getSensorRuleTypeEnum() {
		return sensorRuleType;
	}
	public void setSensorRuleType(SensorRuleType sensorRuleType) {
		this.sensorRuleType = sensorRuleType;
	}
	public int getSensorRuleType() {
		if (sensorRuleType != null) {
			return sensorRuleType.getIndex();
		}
		return -1;
	}
	public void setSensorRuleType(int sensorRuleType) {
		this.sensorRuleType = SensorRuleType.valuOf(sensorRuleType);
	}

	private Boolean meterRollUp;
	public Boolean getMeterRollUp() {
		return meterRollUp;
	}
	public void setMeterRollUp(Boolean meterRollUp) {
		this.meterRollUp = meterRollUp;
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
	
	public SensorRollUpEventContext constructRollUpEvent(ReadingContext reading, JSONObject defaultSeverityProps, boolean isHistorical, boolean isMeterRollUpEvent) throws Exception {
		
		SensorRollUpEventContext sensorRollUpEvent = new SensorRollUpEventContext();
		String severity = (String) defaultSeverityProps.get("severity");
		if (StringUtils.isNotEmpty(severity)) {
			sensorRollUpEvent.setSeverityString(severity);
		}
		sensorRollUpEvent.setIsMeterRollUpEvent(isMeterRollUpEvent);
		if(!isMeterRollUpEvent) {
			sensorRollUpEvent.setReadingFieldId(this.getReadingFieldId());
			sensorRollUpEvent.setEventMessage("Sensor turns fault");
		}
		else {
			sensorRollUpEvent.setEventMessage("Asset has faulty sensors");
		}

		sensorRollUpEvent.setSensorRule(this.getSensorRule());
		
		SensorRuleUtil.addDefaultEventProps(reading, null, sensorRollUpEvent);
		
		return sensorRollUpEvent;
	}

}
