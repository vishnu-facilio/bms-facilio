package com.facilio.workflows.functions;

import java.util.ArrayList;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.exceptions.FunctionParamException;

public enum FacilioFieldFunctions implements FacilioWorkflowFunctionInterface {

	GETID(1,"id") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			FacilioField field = (FacilioField)objects[0];
			
			return field.getId();
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	AS_MAP(2,"asMap") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			FacilioField field = (FacilioField)objects[0];
			
			return FieldUtil.getAsProperties(field);
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
	private String namespace = "field";
	private List<FacilioFunctionsParamType> params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.FIELD;
	
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
	FacilioFieldFunctions(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioFieldFunctions> getAllFunctions() {
		return MODULE_FUNCTIONS;
	}
	public static FacilioFieldFunctions getFacilioFieldFunctions(String functionName) {
		return MODULE_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioFieldFunctions> MODULE_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioFieldFunctions> initTypeMap() {
		Map<String, FacilioFieldFunctions> typeMap = new HashMap<>();
		for(FacilioFieldFunctions type : FacilioFieldFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}