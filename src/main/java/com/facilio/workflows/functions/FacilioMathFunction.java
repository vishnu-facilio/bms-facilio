package com.facilio.workflows.functions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.facilio.workflows.exceptions.FunctionParamException;

public enum FacilioMathFunction implements FacilioWorkflowFunctionInterface  {

	ABS(1,"abs") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			Double value = (double) objects[0];
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
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
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
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
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
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
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
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
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
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			double a = Double.parseDouble(objects[0].toString());
			return Math.sqrt(a);
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	RANDOM(7,"random") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			int no = (int) Double.parseDouble(objects[0].toString());
			
			Random random = new Random();
			return random.nextInt(no);
			
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	SET_PRECISION(8, "setPrecision") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			double val = Double.parseDouble(objects[0].toString());
			int precision = Integer.parseInt(objects[1].toString());
			
			Double truncatedDouble = BigDecimal.valueOf(val)
				    .setScale(precision, RoundingMode.HALF_UP)
				    .doubleValue();
			
			return truncatedDouble;
		}
		
		private void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length < 2) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	}
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "math";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.MATH;
	
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
