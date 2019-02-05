package com.facilio.workflows.functions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.TicketStatusContext.StatusType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.ConsumptionAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.workflows.exceptions.FunctionParamException;

public enum FacilioConsumptionFunctions implements FacilioWorkflowFunctionInterface {

	GET_ENERGY_DATA_BY_BUILDINGS(1,"getEnergyDataByBuilding") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			Map<String,Object> totalConsumptionByBuilding = ConsumptionAPI.getTotalConsumptionByBuildings(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()), "energydata","totalEnergyConsumptionDelta");
			return totalConsumptionByBuilding;
			    
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	GET_WATER_DATA_BY_BUILDINGS(2,"getWaterDataByBuilding") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			Map<String,Object> totalConsumptionByBuilding = ConsumptionAPI.getTotalConsumptionByBuildings(Long.valueOf(objects[0].toString()), Long.valueOf(objects[1].toString()), "waterreading","waterConsumptionDelta");
			return totalConsumptionByBuilding;
			    
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	};
		
	
	
	private Integer value;
	private String functionName;
	private String namespace = "consumption";
	private String params;
	private FacilioFunctionNameSpace nameSpaceEnum = FacilioFunctionNameSpace.CONSUMPTION;
	
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
	FacilioConsumptionFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioConsumptionFunctions> getAllFunctions() {
		return CONSUMPTION_FUNCTIONS;
	}
	public static FacilioConsumptionFunctions getFacilioConsumptionFunction(String functionName) {
		return CONSUMPTION_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioConsumptionFunctions> CONSUMPTION_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioConsumptionFunctions> initTypeMap() {
		Map<String, FacilioConsumptionFunctions> typeMap = new HashMap<>();
		for(FacilioConsumptionFunctions type : FacilioConsumptionFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
	
			
}
