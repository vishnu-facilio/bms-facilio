package com.facilio.workflows.functions;

import java.util.Collections;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.workflows.util.WorkflowUtil;

public enum ThermoPhysicalR134aFunctions implements FacilioWorkflowFunctionInterface {

	GET_TEMP_FROM_PRESSURE(1,"getTempratureFromPresure") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			if(objects[0] == null) {
				return null;
			}
			double pressure = Double.parseDouble(objects[0].toString());
			
			Map<Double, Double> chillerMap = WorkflowUtil.getChillerTempVsPressureMap();
			
			if(chillerMap.get(pressure) != null ) {
				return chillerMap.get(pressure);
			}
			else {
				TreeSet<Double> keySet = new TreeSet<Double>();
				
				keySet.addAll(chillerMap.keySet());

				Double lesserValue = null,greaterValue = null;
				Double lesserValueDelta = null,greaterValueDelta = null;
				
				Object[] keysetArray = keySet.toArray();
				
				for(int i=0;i<keysetArray.length;i++) {
					double key = (double) keysetArray[i];
					if(pressure < key) {
						if( i== 0) {										// pressure value is lesser than the first value in sheet
							return null;
						}
						greaterValue = key;
						lesserValue = (double) keysetArray[i-1];
						break;
					}
				}
				
				if(greaterValue == null || lesserValue == null) {			// pressure value is higher than the last value in sheet
					return null;
				}
				lesserValueDelta = pressure - lesserValue;
				greaterValueDelta = greaterValue - pressure;
				
				if(lesserValueDelta > greaterValueDelta) {
					return chillerMap.get(greaterValue);
				}
				else {
					return chillerMap.get(lesserValue);
				}
			}
		}
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "thermoPhysical.R134a";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.THERMOPHYSICALR134A;
	
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
	ThermoPhysicalR134aFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, ThermoPhysicalR134aFunctions> getAllFunctions() {
		return DEFAULT_FUNCTIONS;
	}
	public static ThermoPhysicalR134aFunctions getThermoPhysicalR134aFunction(String functionName) {
		return DEFAULT_FUNCTIONS.get(functionName);
	}
	static final Map<String, ThermoPhysicalR134aFunctions> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, ThermoPhysicalR134aFunctions> initTypeMap() {
		Map<String, ThermoPhysicalR134aFunctions> typeMap = new HashMap<>();
		for(ThermoPhysicalR134aFunctions type : ThermoPhysicalR134aFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}