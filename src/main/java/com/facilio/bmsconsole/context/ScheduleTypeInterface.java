package com.facilio.bmsconsole.context;

import javax.mail.MethodNotSupportedException;

import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsole.context.sensor.SensorRuleType;

public interface ScheduleTypeInterface {
	
	public void createRecords(BaseScheduleContext baseScheduleContext) throws Exception;
	
	public ScheduleType getSchedulerType();
	
}
