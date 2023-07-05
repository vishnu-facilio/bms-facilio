package com.facilio.alarms.sensor.context.sensoralarm;

import com.facilio.alarms.sensor.*;
import com.facilio.alarms.sensor.context.sensorrollup.SensorRollUpEventContext;
import com.facilio.alarms.sensor.context.SensorRuleContext;
import com.facilio.alarms.sensor.context.SensorRulePropContext;
import com.facilio.alarms.sensor.util.SensorRuleUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.AssetsAPI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Getter
@Setter
@Log4j
public class SensorEventContext extends BaseEventContext {
	
	public String constructMessageKey() {
		if (getResource() != null && getSensorRule() != null) {
			return "Sensor_" + getReadingFieldId() +"_"+ getResource().getId() +"_"+ getSensorRule().getId()+"_"+getSensorRuleProp().getId();
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
	private SensorRulePropContext sensorRuleProp;
	private long readingFieldId = -1;
	private SensorRuleType sensorRuleType;
	public SensorRuleType getSensorRuleTypeEnum() {
		return sensorRuleType;
	}
	public void setSensorRuleType(SensorRuleType sensorRuleType) {
		this.sensorRuleType = sensorRuleType;
	}
	public int getSensorRuleType() {
		return sensorRuleType!=null?sensorRuleType.getIndex():-1;
	}
	public void setSensorRuleType(int sensorRuleType) {
		this.sensorRuleType = SensorRuleType.valueOf(sensorRuleType);
	}

	private Boolean meterRollUp;
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
	
	public SensorRollUpEventContext constructRollUpEvent(ReadingContext reading, JSONObject defaultSeverityProps, boolean isHistorical, boolean isMeterRollUpEvent, SensorRuleContext sensorRule) throws Exception {
		
		SensorRollUpEventContext sensorRollUpEvent = new SensorRollUpEventContext();
		String severity = (String) defaultSeverityProps.get("severity");
		AssetContext resource = AssetsAPI.getAssetInfo(reading.getParentId());
		
		if (StringUtils.isNotEmpty(severity)) {
			sensorRollUpEvent.setSeverityString(severity);
		}
		sensorRollUpEvent.setIsMeterRollUpEvent(isMeterRollUpEvent);
		String eventMessage = new String();
		if(!isMeterRollUpEvent) {
			sensorRollUpEvent.setReadingFieldId(this.getReadingFieldId());
			eventMessage = "Faulty " + sensorRule.getSensorField().getDisplayName().toLowerCase() + " sensor of " + resource.getName().toLowerCase();
		}
		else {
			sensorRollUpEvent.setReadingFieldId(this.getReadingFieldId());
			eventMessage = "Faulty " + resource.getName().toLowerCase();
		}
		
		sensorRollUpEvent.setEventMessage(eventMessage);
		sensorRollUpEvent.setSensorRule(this.getSensorRule());
		
		SensorRuleUtil.addDefaultEventProps(reading, null, sensorRollUpEvent);
		
		return sensorRollUpEvent;
	}
	
	@Override
	public String toString() {
		
		StringBuilder builder =  new StringBuilder()
				.append(" sensorRule ID : ").append(sensorRule)
				.append(", readingFieldId : ").append(readingFieldId)
				.append(", sensorRuleType : ").append(sensorRuleType)
				.append(", meterRollUp : ").append(meterRollUp)
				.append(", messageKey : "+getMessageKey())
				.append(", resource : ").append(getResource());
			
		return builder.toString();
	}

}
