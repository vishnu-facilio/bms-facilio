package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.workflow.exceptions.FunctionParamException;
import com.facilio.workflows.util.FunctionUtil;

public enum FacilioCostFunction implements FacilioWorkflowFunctionInterface {

	GET_COST_FROM_KWH(1,"getCostFromKwh") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
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
	FacilioCostFunction(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static FacilioCostFunction getFacilioCostFunction(String functionName) {
		return DEFAULT_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioCostFunction> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioCostFunction> initTypeMap() {
		Map<String, FacilioCostFunction> typeMap = new HashMap<>();
		for(FacilioCostFunction type : FacilioCostFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
