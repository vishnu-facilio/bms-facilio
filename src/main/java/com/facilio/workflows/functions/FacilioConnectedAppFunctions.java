package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.context.VariableContext;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.workflows.exceptions.FunctionParamException;
import com.facilio.workflows.util.FunctionUtil;

public enum FacilioConnectedAppFunctions implements FacilioWorkflowFunctionInterface {

	GET_VARIABLE(1,"getVariable") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			String connectedAppLinkName = objects[0].toString();
			String varName = objects[1].toString();
			
			VariableContext var = ConnectedAppAPI.getVariable(connectedAppLinkName, varName);
			if(var != null) {
				return var.getValue();
			}
			return null;
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
	private String namespace = "connectedApp";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.CONNECTED_APP;
	
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
	FacilioConnectedAppFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioConnectedAppFunctions> getAllFunctions() {
		return DEFAULT_FUNCTIONS;
	}
	public static FacilioConnectedAppFunctions getFacilioConnectedAppFunction(String functionName) {
		return DEFAULT_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioConnectedAppFunctions> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioConnectedAppFunctions> initTypeMap() {
		Map<String, FacilioConnectedAppFunctions> typeMap = new HashMap<>();
		for(FacilioConnectedAppFunctions type : FacilioConnectedAppFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
