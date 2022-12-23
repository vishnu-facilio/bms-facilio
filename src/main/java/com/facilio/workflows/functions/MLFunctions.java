package com.facilio.workflows.functions;

import com.facilio.bmsconsole.context.MlForecastingContext;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.ML_FUNCTION)
public class MLFunctions {
	public Object isPredictionValid(Map<String, Object> globalParam, Object... objects) throws Exception {
		// TODO Auto-generated method stub
		checkParam(2, objects);
		long mlForecastingId = FacilioUtil.parseLong(objects[0]);
		List<Long> ttimes = (List<Long>) objects[1];
		if (CollectionUtils.isEmpty(ttimes)) {
			throw new IllegalArgumentException("TTime list cannot be null");
		}
		MlForecastingContext forecast = MLUtil.getContext(mlForecastingId);
		return MLUtil.checkValidPrediction(forecast, ttimes);
	}

	public void checkParam(int minLength, Object... objects) throws Exception {
		if(objects.length < minLength) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}
