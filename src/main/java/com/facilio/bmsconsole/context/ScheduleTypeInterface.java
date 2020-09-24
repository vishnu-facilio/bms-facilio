package com.facilio.bmsconsole.context;


import java.util.List;
import javax.mail.MethodNotSupportedException;

import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.bmsconsole.context.sensor.SensorRuleType;
import com.facilio.modules.ModuleBaseWithCustomFields;


public interface ScheduleTypeInterface {
	
	public List<ModuleBaseWithCustomFields> createRecords(BaseScheduleContext baseScheduleContext) throws Exception;
	
	public ScheduleType getSchedulerType();
	
}
