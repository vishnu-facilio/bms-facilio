package com.facilio.energystar.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;

public enum Meter_Category {

	ELECTRIC(1, "Electric",1,"kWh (thousand Watt-hours)","energydata","totalEnergyConsumptionDelta",NumberAggregateOperator.SUM),
	NATURAL_GAS(2, "Natural Gas",2,null,null,null,null),
	PROPANE(3,"Propane",4,null,null,null,null);
	;

	int intVal;
	String name;
	long license;
	String moduleName;
	String unitOfMessure;
	String fieldName;
	NumberAggregateOperator aggr;
	
	public String getModule() {
		return moduleName;
	}
	public String getField() {
		return fieldName;
	}
	public NumberAggregateOperator getAggr() {
		return aggr;
	}
	public String getUnitOfMessure() {
		return unitOfMessure;
	}

	public int getIntVal() {
		return intVal;
	}

	public String getName() {
		return name;
	}

	public long getLicence() {
		return license;
	}
	private Meter_Category(int intVal, String name,long licenceId,String unitOfMeasure,String moduleName,String fieldName,NumberAggregateOperator aggr) {
		this.intVal = intVal;
		this.name = name;
		this.license = licenceId;
		this.moduleName = moduleName;
		this.unitOfMessure = unitOfMeasure;
		this.fieldName = fieldName;
		this.aggr = aggr;
	}

	private static final Map<Integer, Meter_Category> optionMap = Collections.unmodifiableMap(initTypeMap());
	
	private static final Map<String, Meter_Category> TypeStringMap = Collections.unmodifiableMap(initTypeStringMap());

	private static Map<Integer, Meter_Category> initTypeMap() {
		Map<Integer, Meter_Category> typeMap = new HashMap<>();

		for (Meter_Category type : values()) {
			typeMap.put(type.getIntVal(), type);
		}
		return typeMap;
	}
	
	private static Map<String, Meter_Category> initTypeStringMap() {
		Map<String, Meter_Category> typeMap = new HashMap<>();

		for (Meter_Category type : values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}

	public static Map<Integer, Meter_Category> getAllAppTypes() {
		return optionMap;
	}
	public static Map<String, Meter_Category> getAllTypes() {
		return TypeStringMap;
	}
}


