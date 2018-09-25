package com.facilio.workflows.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.workflows.exceptions.FunctionParamException;

public enum FacilioReadingFunctions implements FacilioWorkflowFunctionInterface  {

	ENERGY_READINGS(1,"getEnergyReading") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			Long baseSpaceId = Long.parseLong(objects[0].toString());
			int dateOperator = Integer.parseInt(objects[1].toString());
			
			String dateOperatorValue = null;
			if(objects[2] != null) {
				dateOperatorValue = objects[2].toString();
			}
			
			
			DateOperators oper = (DateOperators) Operator.OPERATOR_MAP.get(dateOperator);
			BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(baseSpaceId);
			
			if(baseSpace != null && oper != null) {
				
				DateRange range = oper.getRange(dateOperatorValue);
				
				switch(baseSpace.getSpaceTypeEnum()) {
				
				case SITE:
					
					List<EnergyMeterContext> energyMeters = DashboardUtil.getMainEnergyMeter(baseSpaceId+"");
					if(energyMeters != null && !energyMeters.isEmpty()) {
						EnergyMeterContext meter = energyMeters.get(0);
						
						List<Map<String, Object>> data = ReportsUtil.fetchMeterData(meter.getId()+"",range.getStartTime(),range.getEndTime(),true);
						if(data != null && !data.isEmpty()) {
							return data.get(0).get("CONSUMPTION");
						}
					}
					else {
						List<BuildingContext> buildings = SpaceAPI.getSiteBuildings(baseSpaceId);
						List<Long> buildingMeters = new ArrayList<>();
						for(BuildingContext building :buildings) {
							
							energyMeters = DashboardUtil.getMainEnergyMeter(building.getId()+"");
							if(energyMeters != null && !energyMeters.isEmpty()) {
								buildingMeters.add(energyMeters.get(0).getId());
							}
						}
						if(!buildingMeters.isEmpty()) {
							List<Map<String, Object>> data = ReportsUtil.fetchMeterData(StringUtils.join(buildingMeters, ","),range.getStartTime(),range.getEndTime(),true);
							double consumption = 0.0;
							if(data != null && !data.isEmpty()) {
								
								for(Map<String, Object> energyData :data) {
									Double consumptionTemp = (Double) energyData.get("CONSUMPTION");
									
									consumption += consumptionTemp;
								}
							}
							return consumption;
						}
					}
				break;
				case BUILDING:
				case FLOOR:
				case SPACE:
					
					energyMeters = DashboardUtil.getMainEnergyMeter(baseSpaceId+"");
					if(energyMeters != null && !energyMeters.isEmpty()) {
						EnergyMeterContext meter = energyMeters.get(0);
						
						List<Map<String, Object>> data = ReportsUtil.fetchMeterData(meter.getId()+"",range.getStartTime(),range.getEndTime(),true);
						if(data != null && !data.isEmpty()) {
							return data.get(0).get("CONSUMPTION");
						}
					}
					break;
				}
			}
			
			return null;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects == null || objects.length == 0) {
				throw new FunctionParamException("Required Object is null or empty");
			}
		}
	},
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "readings";
	private String params;
	private FacilioFunctionNameSpace nameSpaceEnum = FacilioFunctionNameSpace.READINGS;
	
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
	FacilioReadingFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioReadingFunctions> getAllFunctions() {
		return READING_FUNCTIONS;
	}
	public static FacilioReadingFunctions getFacilioReadingFunctions(String functionName) {
		return READING_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioReadingFunctions> READING_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioReadingFunctions> initTypeMap() {
		Map<String, FacilioReadingFunctions> typeMap = new HashMap<>();
		for(FacilioReadingFunctions type : FacilioReadingFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
