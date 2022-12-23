package com.facilio.workflows.functions;

import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.BaseLineContext.AdjustType;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.time.DateRange;

import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.DATE_RANGE_FUNCTION)
public class FacilioDateRangeFunctions {
	public Object getStartTime(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return false;
		}
		DateRange dateRange = (DateRange) objects[0];
		return dateRange.getStartTime();
	}

	public Object getEndTime(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return false;
		}
		DateRange dateRange = (DateRange) objects[0];
		return dateRange.getEndTime();
	}

	public Object create(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		long startTime = (long) Double.parseDouble(objects[0].toString());
		long endTime = (long) Double.parseDouble(objects[1].toString());

		DateRange dateRange = new DateRange(startTime, endTime);
		if(objects.length > 2 && objects[2] != null) {
			String baselineName = objects[2].toString();
			BaseLineContext baseline = BaseLineAPI.getBaseLine(baselineName);
			if(baseline != null) {
				if(objects.length > 3 && objects[3] != null) {
					String baselineAdjustmentType = objects[3].toString();
					baseline.setAdjustType(BaseLineContext.AdjustType.getAllAdjustments().get(baselineAdjustmentType));
				}
				else {
					baseline.setAdjustType(AdjustType.WEEK);
				}
				dateRange = baseline.calculateBaseLineRange(dateRange, baseline.getAdjustTypeEnum());
			}
		}

		return dateRange;
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}