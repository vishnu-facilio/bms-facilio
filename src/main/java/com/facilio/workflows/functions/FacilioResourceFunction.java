package com.facilio.workflows.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.workflows.exceptions.FunctionParamException;
import com.facilio.workflows.util.WorkflowUtil;

public enum FacilioResourceFunction implements FacilioWorkflowFunctionInterface {

	GETRESOURCENAME(1,"getResourceName",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.NUMBER.getValue(),"resourceId"),WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.BOOLEAN.getValue(),"isWithSpaceName") ) {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			
			Long resourceId = Long.parseLong(objects[0].toString());
			
			boolean isWithSpaceName = objects.length > 1 && objects[1] != null ? Boolean.parseBoolean(objects[1].toString()) : false; 
			
			ResourceContext resource = ResourceAPI.getResource(resourceId);
			
			if(!isWithSpaceName) {
				return resource.getName();
			}
			else {
				if(resource.getResourceType() == ResourceContext.ResourceType.SPACE.getValue()) {
					return resource.getName();
				}
				else {
					ResourceContext space = ResourceAPI.getResource(resource.getSpaceId());
					return resource.getName() +" - "+space.getName();
				}
			}
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
	private String namespace = "resource";
	private List<FacilioFunctionsParamType> params;
	private FacilioFunctionNameSpace nameSpaceEnum = FacilioFunctionNameSpace.RESOURCE;
	
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
	FacilioResourceFunction(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioResourceFunction> getAllFunctions() {
		return RESOURCE_FUNCTIONS;
	}
	public static FacilioResourceFunction getFacilioResourceFunction(String functionName) {
		return RESOURCE_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioResourceFunction> RESOURCE_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioResourceFunction> initTypeMap() {
		Map<String, FacilioResourceFunction> typeMap = new HashMap<>();
		for(FacilioResourceFunction type : FacilioResourceFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
