package com.facilio.workflowv2.modulefunctions;

import java.util.List;
import java.util.Map;

import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.workflowv2.annotation.ScriptModule;

@ScriptModule(moduleName = FacilioConstants.ContextNames.READING_ALARM_OCCURRENCE)
public class FacilioReadingAlarmModuleFunctions extends FacilioAlarmModuleFunctions {

	public List<Map<String, Object>> getTopNAlarms(Map<String,Object> globalParams,List<Object> objects) throws Exception {
		objects.add(1, ContextNames.READING_ALARM_OCCURRENCE);
		return super.getTopNAlarms(globalParams, objects);
	}
}