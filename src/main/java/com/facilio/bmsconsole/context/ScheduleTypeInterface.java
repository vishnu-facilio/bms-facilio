package com.facilio.bmsconsole.context;


import java.util.List;
import java.util.Map;

import javax.mail.MethodNotSupportedException;

import com.facilio.bmsconsole.context.BaseScheduleContext.ScheduleType;
import com.facilio.modules.ModuleBaseWithCustomFields;


public interface ScheduleTypeInterface {
	
	public List<? extends ModuleBaseWithCustomFields> createRecords(BaseScheduleContext baseScheduleContext, boolean isUpdate, List<Map<String, Object>> parentRecordProps, boolean isManualOrScheduleTrigger) throws Exception;
	
	public ScheduleType getSchedulerType();
	
}
