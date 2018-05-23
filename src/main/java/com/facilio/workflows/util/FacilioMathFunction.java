package com.facilio.workflows.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.workflow.exceptions.FunctionParamException;

public enum FacilioMathFunction implements FacilioWorkflowFunctionInterface  {

	ABS(1,"abs") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			double value = (double) objects[0];
			return Math.abs(value);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	CEIL(2,"ceil") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			double value = (double) objects[0];
			return Math.ceil(value);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	FLOOR(3,"floor") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			double value = (double) objects[0];
			return Math.floor(value);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	}
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "math";
	private String params;
	
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
	FacilioMathFunction(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static FacilioMathFunction getFacilioMathFunction(String functionName) {
		return MATH_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioMathFunction> MATH_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioMathFunction> initTypeMap() {
		Map<String, FacilioMathFunction> typeMap = new HashMap<>();
		for(FacilioMathFunction type : FacilioMathFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
