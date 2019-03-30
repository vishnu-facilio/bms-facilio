package com.facilio.workflows.functions;

import com.facilio.workflows.exceptions.FunctionParamException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	},
	POWER(4,"pow") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			double a = Double.parseDouble(objects[0].toString());
			double b = Double.parseDouble(objects[1].toString());
			return Math.pow(a, b);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	CUBEROOT(5,"cbrt") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			double a = Double.parseDouble(objects[0].toString());
			return Math.cbrt(a);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	SQRT(6,"sqrt") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			double a = Double.parseDouble(objects[0].toString());
			return Math.sqrt(a);
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
	private String namespace = "math";
	private String params;
	private FacilioFunctionNameSpace nameSpaceEnum = FacilioFunctionNameSpace.MATH;
	
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
	public static Map<String, FacilioMathFunction> getAllFunctions() {
		return MATH_FUNCTIONS;
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
