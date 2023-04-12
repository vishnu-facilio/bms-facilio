package com.facilio.workflows.functions;

import java.util.Collections;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.util.PsychrometricUtil;
import com.facilio.scriptengine.exceptions.FunctionParamException;

public enum FacilioPsychrometricsFunction implements FacilioWorkflowFunctionInterface  {
	
	GET_MOIST_AIR_ENTHALPY(1,"getMoistAirEnthalpy") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			if(objects == null || objects.length <2 || objects[0] == null || objects[1] == null || objects[2] == null) {
				return null;
			}
			double dryBulbTemperature = Double.parseDouble(objects[0].toString());
			double pressure = Double.parseDouble(objects[1].toString());
			double relativeHumidity = Double.parseDouble(objects[2].toString());
			
			return PsychrometricUtil.getMoistAirEnthalpy(dryBulbTemperature, pressure, relativeHumidity);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	GET_DEW_POINT_TEMPERATURE(2,"getDewPointTemperature") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			if(objects == null || objects.length <2 || objects[0] == null || objects[1] == null || objects[2] == null) {
				return null;
			}
			double dryBulbTemperature = Double.parseDouble(objects[0].toString());
			double pressure = Double.parseDouble(objects[1].toString());
			double relativeHumidity = Double.parseDouble(objects[2].toString());
			
			return PsychrometricUtil.getDewPointTemperatureFromRelativeHumidity(dryBulbTemperature, relativeHumidity, pressure);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	
	GET_WET_BULB_TEMPERATURE(3,"getWetBulbTemperature") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			if(objects == null || objects.length <2 || objects[0] == null || objects[1] == null || objects[2] == null) {
				return null;
			}
			double dryBulbTemperature = Double.parseDouble(objects[0].toString());
			double pressure = Double.parseDouble(objects[1].toString());
			double relativeHumidity = Double.parseDouble(objects[2].toString());
			
			return PsychrometricUtil.getWetBulbTemperatureFromRelativeHumidity(dryBulbTemperature, relativeHumidity, pressure);
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
	private String namespace = "psychrometrics";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.PSYCHROMETRICS;
	
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
	FacilioPsychrometricsFunction(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioPsychrometricsFunction> getAllFunctions() {
		return PSYC_FUNCTIONS;
	}
	public static FacilioPsychrometricsFunction getFacilioMathFunction(String functionName) {
		return PSYC_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioPsychrometricsFunction> PSYC_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioPsychrometricsFunction> initTypeMap() {
		Map<String, FacilioPsychrometricsFunction> typeMap = new HashMap<>();
		for(FacilioPsychrometricsFunction type : FacilioPsychrometricsFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}

}