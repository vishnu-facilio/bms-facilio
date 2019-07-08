package com.facilio.workflows.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.exceptions.FunctionParamException;

public enum FacilioConnectionFunctions implements FacilioWorkflowFunctionInterface {

	GET(1,"get") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			ConnectionContext field = (ConnectionContext)objects[0];
			
			String url = (String) objects[0];
			
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	POST(2,"post") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
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
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "connection";
	private List<FacilioFunctionsParamType> params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.CONNECTION;
	
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
	FacilioConnectionFunctions(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioConnectionFunctions> getAllFunctions() {
		return CONNECTION_FUNCTIONS;
	}
	public static FacilioConnectionFunctions getFacilioConnectionFunctions(String functionName) {
		return CONNECTION_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioConnectionFunctions> CONNECTION_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioConnectionFunctions> initTypeMap() {
		Map<String, FacilioConnectionFunctions> typeMap = new HashMap<>();
		for(FacilioConnectionFunctions type : FacilioConnectionFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}