package com.facilio.workflowv2.modulefunctions;

import java.util.List;
import java.util.Map;

import com.facilio.constants.FacilioConstants.ContextNames;

public class FacilioReadingAlarmModuleFunctions extends FacilioAlarmModuleFunctions {
	public List<Map<String, Object>> getTopNAlarms(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		objects.add(1, ContextNames.NEW_READING_ALARM);
		return super.getTopNAlarms(globalParams, objects);
	}
}