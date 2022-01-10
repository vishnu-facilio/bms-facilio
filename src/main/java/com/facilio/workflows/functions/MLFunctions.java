package com.facilio.workflows.functions;

import java.util.Collections;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.MlForecastingContext;
import com.facilio.bmsconsole.util.MLUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.util.FacilioUtil;

public enum MLFunctions implements FacilioWorkflowFunctionInterface {
	CHECK_IF_VALID_PREDICTION (1, "isPredictionValid") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
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
		
	}
	;
	private Integer value;
	private String functionName;
	private String namespace = "asset";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.ML;
	
	
	public void checkParam(int minLength, Object... objects) throws Exception {
		if(objects.length < minLength) {
			throw new FunctionParamException("Required Object is null");
		}
	}
	public int getValue() {
		return value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public String getParams() {
		return params;
	}
	MLFunctions (int value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	
	public static Map<String, MLFunctions> getAllFunctions() {
		return ML_FUNCTIONS;
	}
	public static MLFunctions getFacilioMLFunction(String functionName) {
		return ML_FUNCTIONS.get(functionName);
	}
	static final Map<String, MLFunctions> ML_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, MLFunctions> initTypeMap() {
		Map<String, MLFunctions> typeMap = new HashMap<>();
		for(MLFunctions type : MLFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
