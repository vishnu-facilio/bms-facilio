package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.workflow.exceptions.FunctionParamException;

public enum FacilioDateFunction implements FacilioWorkflowFunctionInterface {

	MINS_TO_HOUR(1,"minsToHour") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			Double mins = Double.parseDouble(objects[0].toString());
			Double hours = UnitsUtil.convert(mins, Unit.MIN, Unit.HOUR);
			return hours;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	SEC_TO_HOUR(2,"secToHour") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			Double secs = Double.parseDouble(objects[0].toString());
			Double hours = UnitsUtil.convert(secs, Unit.SEC, Unit.HOUR);
			return hours;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	HOUR_TO_DAY(3,"hourToDay") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			Double hours = Double.parseDouble(objects[0].toString());
			Double day = UnitsUtil.convert(hours, Unit.HOUR, Unit.DAY);
			return day;
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
	private String namespace = "date";
	private String params;
	
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
	FacilioDateFunction(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static FacilioDateFunction getFacilioDefaultFunction(String functionName) {
		return DATE_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioDateFunction> DATE_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioDateFunction> initTypeMap() {
		Map<String, FacilioDateFunction> typeMap = new HashMap<>();
		for(FacilioDateFunction type : FacilioDateFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
