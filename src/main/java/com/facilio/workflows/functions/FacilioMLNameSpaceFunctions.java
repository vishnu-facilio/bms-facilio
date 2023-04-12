package com.facilio.workflows.functions;

import java.util.ArrayList;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.services.FacilioHttpUtils;

public enum FacilioMLNameSpaceFunctions implements FacilioWorkflowFunctionInterface {

	PRINT(1,"print") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			return "HELLO";
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "mlNameSpace";
	private List<FacilioFunctionsParamType> params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.ML_NAMESPACE;
	
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
	public List<FacilioFunctionsParamType> getParams() {
		return params;
	}
	public void setParams(List<FacilioFunctionsParamType> params) {
		this.params = params;
	}
	public void addParams(FacilioFunctionsParamType param) {
		this.params = (this.params == null) ? new ArrayList<>() :this.params;
		this.params.add(param);
	}
	FacilioMLNameSpaceFunctions(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioMLNameSpaceFunctions> getAllFunctions() {
		return MODULE_FUNCTIONS;
	}
	public static FacilioMLNameSpaceFunctions getFacilioMLNameSpaceFunctions(String functionName) {
		return MODULE_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioMLNameSpaceFunctions> MODULE_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioMLNameSpaceFunctions> initTypeMap() {
		Map<String, FacilioMLNameSpaceFunctions> typeMap = new HashMap<>();
		for(FacilioMLNameSpaceFunctions type : FacilioMLNameSpaceFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}