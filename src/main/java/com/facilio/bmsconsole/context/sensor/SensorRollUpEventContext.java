package com.facilio.bmsconsole.context.sensor;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.bmsconsole.context.BaseAlarmContext.Type;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class SensorRollUpEventContext extends BaseEventContext {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(SensorRollUpEventContext.class.getName());

	Boolean isMeterRollUpEvent;
	
	public Boolean getIsMeterRollUpEvent() {
		return isMeterRollUpEvent;
	}

	public void setIsMeterRollUpEvent(Boolean isMeterRollUpEvent) {
		this.isMeterRollUpEvent = isMeterRollUpEvent;
	}
	
	public boolean isMeterRollUpEvent() {
		if (isMeterRollUpEvent != null) {
			return isMeterRollUpEvent.booleanValue();
		}
		return false;
	}

	public String constructMessageKey() {
		if(isMeterRollUpEvent()) {
			if (getResource() != null && getSensorRule() != null) {
				return "SensorMeterRollUp_" + getResource().getId();
			}
		}
		else {
			if (getResource() != null && getSensorRule() != null) {
				return "SensorRollUp_" + getReadingFieldId() + "_" + getResource().getId();
			}
		}	
		return null;
	}
	
	@Override
	public BaseAlarmContext updateAlarmContext(BaseAlarmContext baseAlarm, boolean add) throws Exception {
		if (add && baseAlarm == null) {
			baseAlarm = new SensorRollUpAlarmContext();
		}
		super.updateAlarmContext(baseAlarm, add);
		
		SensorRollUpAlarmContext sensorRollUpAlarm = (SensorRollUpAlarmContext) baseAlarm;
		sensorRollUpAlarm.setSensorRule(sensorRule);
		if (readingFieldId != -1) {
			sensorRollUpAlarm.setReadingFieldId(readingFieldId);
		}
		return baseAlarm;
	}

	@Override
	public AlarmOccurrenceContext updateAlarmOccurrenceContext(AlarmOccurrenceContext alarmOccurrence, Context context, boolean add) throws Exception {
		if (add && alarmOccurrence == null) {
			alarmOccurrence = new SensorRollUpAlarmOccurrenceContext();
		}
		SensorRollUpAlarmOccurrenceContext sensorRollUpAlarmOccurrence = (SensorRollUpAlarmOccurrenceContext) alarmOccurrence;
		sensorRollUpAlarmOccurrence.setSensorRule(sensorRule);
		if (readingFieldId != -1) {
			sensorRollUpAlarmOccurrence.setReadingFieldId(readingFieldId);
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
	
	private FacilioField readingField;
	public FacilioField getReadingField(){
		try {
			if(readingField == null && readingFieldId > 0) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				readingField = modBean.getField(readingFieldId);
			}
		}
		catch(Exception e) {
			LOGGER.error("Error in SensorRollUpEventContext while fetching reading fieldid : "+readingFieldId, e);
		}
		return readingField;
	}
	public void setReadingField(FacilioField readingField) {
		this.readingField = readingField;
	}

	@JsonSerialize
	public Type getEventTypeEnum() {
		return Type.SENSOR_ROLLUP_ALARM;
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
