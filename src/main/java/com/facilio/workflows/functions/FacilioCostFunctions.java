package com.facilio.workflows.functions;

import java.util.Collections;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.HashMap;
import java.util.Map;

import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.workflows.util.FunctionUtil;

public enum FacilioCostFunctions implements FacilioWorkflowFunctionInterface {

	GET_COST_FROM_KWH(1,"getCostFromKwh") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			
			Double kwh = Double.parseDouble(objects[0].toString());
			
			double temp = kwh;
			double total = 0.0;
			for(int i=1; i<5; i++) {
				
				double cost = FunctionUtil.getCostValueForSlab(i);
				temp = kwh - 2000;
				if(temp < 0) {
					total = total + (kwh * cost);
					break;
				}
				else {
					if(i == 4) {
						total = total + (kwh * cost);
					}
					else {
						total = total + (2000 * cost);
					}
					kwh = temp;
				}
			}
			return total;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	}
	;
	private Integer value;
	private String functionName;
	private String namespace = "cost";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.COST;
	
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
	FacilioCostFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioCostFunctions> getAllFunctions() {
		return DEFAULT_FUNCTIONS;
	}
	public static FacilioCostFunctions getFacilioCostFunction(String functionName) {
		return DEFAULT_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioCostFunctions> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioCostFunctions> initTypeMap() {
		Map<String, FacilioCostFunctions> typeMap = new HashMap<>();
		for(FacilioCostFunctions type : FacilioCostFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
