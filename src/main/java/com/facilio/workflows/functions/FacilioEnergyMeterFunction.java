package com.facilio.workflows.functions;

import java.util.ArrayList;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.workflows.util.WorkflowUtil;

public enum FacilioEnergyMeterFunction implements FacilioWorkflowFunctionInterface {
	
	GET_PHYSICAL_METER_COUNT(1,"getPhysicalMeterCount",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.NUMBER.getValue(),"spaceId"),WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.NUMBER.getValue(),"purposeId")) {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			
			Long spaceId = Long.parseLong(objects[0].toString());
			Long purposeId = null;
			if(objects.length > 1 && objects[1] != null) {
				purposeId = Long.parseLong(objects[1].toString());
			}
			
			List<EnergyMeterContext> meters = DeviceAPI.getPhysicalMeter(spaceId,purposeId);

			return meters.size();
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	GET_VIRTUAL_METER_COUNT(2,"getVirtualMeterCount",WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.NUMBER.getValue(),"spaceId"),WorkflowUtil.getFacilioFunctionParam(FacilioFunctionsParamType.NUMBER.getValue(),"purposeId")) {
		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return null;
			}
			
			Long spaceId = Long.parseLong(objects[0].toString());
			
			Long purposeId = null;
			if(objects.length > 1 && objects[1] != null) {
				purposeId = Long.parseLong(objects[1].toString());
			}
			
			List<EnergyMeterContext> meters = DeviceAPI.getVirtualMeters(spaceId,purposeId);

			return meters.size();
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
	private String namespace = "energyMeter";
	private List<FacilioFunctionsParamType> params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.ENERGYMETER;
	
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
	FacilioEnergyMeterFunction(Integer value,String functionName,FacilioFunctionsParamType... params) {
		this.value = value;
		this.functionName = functionName;
		
		if(params != null ) {
			for(int i=0;i<params.length;i++) {
				addParams(params[i]);
			}
		}
	}
	
	public static Map<String, FacilioEnergyMeterFunction> getAllFunctions() {
		return FACILIO_ENERGY_METER_FUNCTIONS;
	}
	public static FacilioEnergyMeterFunction getFacilioEnergyMeterFunction(String functionName) {
		return FACILIO_ENERGY_METER_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioEnergyMeterFunction> FACILIO_ENERGY_METER_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioEnergyMeterFunction> initTypeMap() {
		Map<String, FacilioEnergyMeterFunction> typeMap = new HashMap<>();
		for(FacilioEnergyMeterFunction type : FacilioEnergyMeterFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}