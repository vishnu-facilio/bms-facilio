package com.facilio.bmsconsole.context.sensor;

import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;

public class SensorAlarmContext extends BaseAlarmContext{

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

	private Boolean meterRollUp;
	public Boolean getMeterRollUp() {
		return meterRollUp;
	}
	public void setMeterRollUp(Boolean meterRollUp) {
		this.meterRollUp = meterRollUp;
	}
}
