package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.workflows.exceptions.FunctionParamException;

public enum FacilioWorkOrderFunctions implements FacilioWorkflowFunctionInterface {

	
	GET_AVG_RESOLUTION_TIME(1,"getAvgResolutionTime") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			List<Map<String,Object>> avgResolutionTimeByCategory = WorkOrderAPI.getTopNCategoryOnAvgCompletionTime(String.valueOf(objects[0].toString()),Long.valueOf(objects[1].toString()),Long.valueOf(objects[2].toString()));
			
            return avgResolutionTimeByCategory;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_WORK_ORDERS_ON_COMPLETION_TIME(2,"getWorkOrdersByCompletionTime") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			List<Map<String,Object>> siteOnCompletion = WorkOrderAPI.getWorkOrderStatusPercentageForWorkflow(String.valueOf(objects[0]),Long.valueOf(objects[1].toString()),Long.valueOf(objects[2].toString()));
			
            return siteOnCompletion;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	GET_TOP_N_TECHNICIANS(3,"getTopNTechnicians") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			List<Map<String,Object>> siteOnCompletion = WorkOrderAPI.getTopNTechnicians(objects[0].toString(), Long.valueOf(objects[1].toString()), Long.valueOf(objects[2].toString()));
			
            return siteOnCompletion;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	}
	;
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "workorder";
	private String params;
	private FacilioFunctionNameSpace nameSpaceEnum = FacilioFunctionNameSpace.WORKORDER;
	
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
	FacilioWorkOrderFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioWorkOrderFunctions> getAllFunctions() {
		return WORKORDER_FUNCTIONS;
	}
	public static FacilioWorkOrderFunctions getFacilioWorkOrderFunction(String functionName) {
		return WORKORDER_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioWorkOrderFunctions> WORKORDER_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioWorkOrderFunctions> initTypeMap() {
		Map<String, FacilioWorkOrderFunctions> typeMap = new HashMap<>();
		for(FacilioWorkOrderFunctions type : FacilioWorkOrderFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
				
}
