package com.facilio.workflows.functions;

import com.facilio.workflows.exceptions.FunctionParamException;
import com.facilio.workflows.util.WorkflowUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public enum ThermoPhysicalR134aFunctions implements FacilioWorkflowFunctionInterface {

	GET_TEMP_FROM_PRESSURE(1,"getTempratureFromPresure") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			if(objects[0] == null) {
				return null;
			}
			double presure = Double.parseDouble(objects[0].toString());
			
			Map<Double, Double> chillerMap = WorkflowUtil.getChillerTempVsPressureMap();
			
			if(chillerMap.get(presure) != null ) {
				return chillerMap.get(presure);
			}
			else {
				TreeSet<Double> keySet = new TreeSet<Double>();
				
				keySet.addAll(chillerMap.keySet());

				Double lesserValue = null,greaterValue = null;
				Double lesserValueDelta = null,greaterValueDelta = null;
				
				Object[] keysetArray = keySet.toArray();
				
				for(int i=0;i<keysetArray.length;i++) {
					double key = (double) keysetArray[i];
					if(presure < key) {
						if( i== 0) {
							return null;
						}
						greaterValue = key;
						lesserValue = (double) keysetArray[i-1];
						break;
					}
				}
				lesserValueDelta = presure - lesserValue;
				greaterValueDelta = greaterValue - presure;
				
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