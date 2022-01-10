package com.facilio.workflows.functions;

import java.util.ArrayList;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.util.FacilioUtil;

public enum FacilioScheduleFunctions implements FacilioWorkflowFunctionInterface {

	CREATE(1,"create") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
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
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_NEXT_EXECUTION_TIME(2,"nextExecutionTime") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
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
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "schedule";
	private List<FacilioFunctionsParamType> params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.SCHEDULE;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public List<FacilioFunctionsParamType> getParams() {
		return params;
	}
	public void setParams(List<FacilioFunctionsParamType> params) {
		this.params = params;
	}
	public void addParams(FacilioFunctionsParamType param) {
		this.params = (this.params == null) ? new ArrayList<>() :this.params;
		this.params.add(param);
	}
	FacilioScheduleFunctions(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioScheduleFunctions> getAllFunctions() {
		return MODULE_FUNCTIONS;
	}
	public static FacilioScheduleFunctions getFacilioScheduleFunctions(String functionName) {
		return MODULE_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioScheduleFunctions> MODULE_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioScheduleFunctions> initTypeMap() {
		Map<String, FacilioScheduleFunctions> typeMap = new HashMap<>();
		for(FacilioScheduleFunctions type : FacilioScheduleFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}