package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.workflows.exceptions.FunctionParamException;

public enum FacilioNumberFunctions implements FacilioWorkflowFunctionInterface  {

	INT_VALUE(1,"intValue") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			if(objects[0] instanceof Integer) {
	    		return (Integer)objects[0];
	    	}
	    	Double d = Double.parseDouble(objects[0].toString());
	        return d.intValue();
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	LONG_VALUE(1,"longValue") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			if(objects[0] instanceof Long) {
	    		return (Long)objects[0];
	    	}
	    	Double d = Double.parseDouble(objects[0].toString());
	        return d.longValue();
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "number";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.NUMBER;
	
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
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	FacilioNumberFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioNumberFunctions> getAllFunctions() {
		return NUMBER_FUNCTIONS;
	}
	public static FacilioNumberFunctions getFacilioNumberFunction(String functionName) {
		return NUMBER_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioNumberFunctions> NUMBER_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioNumberFunctions> initTypeMap() {
		Map<String, FacilioNumberFunctions> typeMap = new HashMap<>();
		for(FacilioNumberFunctions type : FacilioNumberFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}