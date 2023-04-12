package com.facilio.workflows.functions;

import java.util.Collections;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.BaseLineContext.AdjustType;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.time.DateRange;

public enum FacilioDateRangeFunctions implements FacilioWorkflowFunctionInterface {
	
	
	GET_START_TIME(1,"getStartTime") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			DateRange dateRange = (DateRange) objects[0];
			return dateRange.getStartTime();
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	GET_END_TIME(2,"getEndTime") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			DateRange dateRange = (DateRange) objects[0];
			return dateRange.getEndTime();
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	CREATE(3,"create") {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			long startTime = (long) Double.parseDouble(objects[0].toString());
			long endTime = (long) Double.parseDouble(objects[1].toString());
			
			DateRange dateRange = new DateRange(startTime, endTime);
			if(objects.length > 2 && objects[2] != null) {
				String baselineName = objects[2].toString();
				BaseLineContext baseline = BaseLineAPI.getBaseLine(baselineName);
				if(baseline != null) {
					if(objects.length > 3 && objects[3] != null) {
						String baselineAdjustmentType = objects[3].toString();
						baseline.setAdjustType(BaseLineContext.AdjustType.getAllAdjustments().get(baselineAdjustmentType));
					}
					else {
						baseline.setAdjustType(AdjustType.WEEK);
					}
					dateRange = baseline.calculateBaseLineRange(dateRange, baseline.getAdjustTypeEnum());
				}
			}
			
			return dateRange;
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
	private String namespace = "dateRange";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.DATE_RANGE;
	
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
	FacilioDateRangeFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioDateRangeFunctions> getAllFunctions() {
		return DATE_FUNCTIONS;
	}
	public static FacilioDateRangeFunctions getFacilioDateFunction(String functionName) {
		return DATE_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioDateRangeFunctions> DATE_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioDateRangeFunctions> initTypeMap() {
		Map<String, FacilioDateRangeFunctions> typeMap = new HashMap<>();
		for(FacilioDateRangeFunctions type : FacilioDateRangeFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}