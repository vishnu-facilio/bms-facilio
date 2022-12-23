package com.facilio.workflows.functions;

import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.util.FacilioUtil;
import org.json.simple.JSONObject;

import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.SCHEDULE_FUNCTION)
public class FacilioScheduleFunctions {
	public Object create(Map<String, Object> globalParam, Object... objects) throws Exception {

		Object schduleInfoObj = objects[0];

		ScheduleInfo scheduleInfo = null;

		if(schduleInfoObj instanceof Map) {

			scheduleInfo = FieldUtil.getAsBeanFromMap((Map<String, Object>)schduleInfoObj, ScheduleInfo.class);
		}
		else if (schduleInfoObj instanceof String) {
			JSONObject json = FacilioUtil.parseJson((String)schduleInfoObj);
			scheduleInfo = FieldUtil.getAsBeanFromJson(json, ScheduleInfo.class);
		}

		return scheduleInfo;
	}

	public Object nextExecutionTime(Map<String, Object> globalParam, Object... objects) throws Exception {

		ScheduleInfo scheduleInfo = (ScheduleInfo)objects[0];
		Long startTime = (long) Double.parseDouble(objects[1].toString());

		Long endTime = null;
		if(objects.length >2) {
			endTime = (long) Double.parseDouble(objects[2].toString());
		}

		if(endTime == null) {
			return scheduleInfo.nextExecutionTime(startTime);
		}
		else {
			return scheduleInfo.nextExecutionTimes(startTime, endTime);
		}
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}