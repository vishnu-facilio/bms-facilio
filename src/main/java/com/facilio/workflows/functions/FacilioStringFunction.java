
package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.workflows.exceptions.FunctionParamException;

public enum FacilioStringFunction implements FacilioWorkflowFunctionInterface {

	STRING_EQUALS(1,"stringEquals") {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			checkParam(objects);
			return (objects[0] == null ? objects[1] == null : objects[0].toString().equals(objects[1].toString()));
		}
		public void checkParam(Object... objects) throws Exception {
			if(objects.length < 2) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	STRING_CONTAINS(2,"stringContains") {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			checkParam(objects);
			
			return (objects[0] == null ? objects[1] == null : objects[0].toString().contains(objects[1].toString()));
		}
		public void checkParam(Object... objects) throws Exception {
			if(objects.length < 2) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	CHAR_AT(3,"charAt") {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			checkParam(objects);
			
			String string = objects[0].toString();
			int index = (int) objects[1];
			
			return string.charAt(index);
		}
		public void checkParam(Object... objects) throws Exception {
			if(objects.length < 2) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	LENGTH(4,"length") {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			checkParam(objects);
			
			String string = objects[0].toString();
			
			return string.length();
		}
		public void checkParam(Object... objects) throws Exception {
			if(objects.length < 1) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	SUB_STRING(5,"subString") {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			checkParam(objects);
			
			String string = objects[0].toString();
			int index1 = (int) objects[1];
			
			if(objects[2] != null) {
				int index2 = (int) objects[2];
				return string.substring(index1, index2);
			}
			return string.substring(index1);
		}
		public void checkParam(Object... objects) throws Exception {
			if(objects.length < 2) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	CONTAINS(6,"contains") {

		@Override
		public Object execute(Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			checkParam(objects);
			
			String string = objects[0].toString();
			String string1 = objects[1].toString();
			
			return string.contains(string1);
		}
		public void checkParam(Object... objects) throws Exception {
			if(objects.length < 2) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	;
	private Integer value;
	private String functionName;
	private String namespace = "string";
	private String params;
	private FacilioFunctionNameSpace nameSpaceEnum = FacilioFunctionNameSpace.STRING;
	
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
	FacilioStringFunction(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioStringFunction> getAllFunctions() {
		return STRING_FUNCTIONS;
	}
	public static FacilioStringFunction getFacilioStringFunction(String functionName) {
		return STRING_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioStringFunction> STRING_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioStringFunction> initTypeMap() {
		Map<String, FacilioStringFunction> typeMap = new HashMap<>();
		for(FacilioStringFunction type : FacilioStringFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}