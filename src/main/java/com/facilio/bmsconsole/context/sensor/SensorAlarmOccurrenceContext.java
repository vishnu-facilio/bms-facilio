package com.facilio.bmsconsole.context.sensor;

import com.facilio.bmsconsole.context.AlarmOccurrenceContext;
import com.facilio.bmsconsole.context.AlarmOccurrenceContext.Type;

public class SensorAlarmOccurrenceContext extends AlarmOccurrenceContext {
	
	private SensorRuleContext sensorRule;
	public SensorRuleContext getSensorRule() {
		return sensorRule;
	}
	public void setSensorRule(SensorRuleContext sensorRule) {
		this.sensorRule = sensorRule;
	}
	
	private long readingFieldId;
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

	private boolean meterRollUp = false;
	public boolean isMeterRollUp() {
		return meterRollUp;
	}
	public void setMeterRollUp(boolean meterRollUp) {
		this.meterRollUp = meterRollUp;
	}
	
	public Type getTypeEnum() {
        return Type.SENSOR;
    }
}
