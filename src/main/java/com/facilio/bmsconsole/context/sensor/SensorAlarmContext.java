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
}
