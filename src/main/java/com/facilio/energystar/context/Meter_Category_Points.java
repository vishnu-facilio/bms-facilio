package com.facilio.energystar.context;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Meter_Category_Points {
	
	ENERGY(1,"Usage kWh","usage",Meter_Category.ELECTRIC,"energydata","totalEnergyConsumptionDelta"),
	ENERGY_COST(2,"Cost","cost",Meter_Category.ELECTRIC,null,null),
//	DEMAND(3,"Demand","demand",Meter_Category.ELECTRIC,"energydata","demand"),
//	DEMAND_COST(4,"demandCost","Demand Cost",Meter_Category.ELECTRIC,null,null),
	;
	
	public static Map<Integer, Meter_Category_Points> getControllableCategoryMap() {
		return POINT_MAP;
	}

	int pointId;
	String displayName;
	String name;
	String moduleName;
	String fieldName;
	
	Meter_Category parentMeterCategory;
	
	public int getPointId() {
		return pointId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getModuleName() {
		return moduleName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public String getName() {
		return name;
	}
	public Meter_Category getparentMeterCategory() {
		return parentMeterCategory;
	}
	Meter_Category_Points(int pointId,String displayName, String name,Meter_Category parentCategory,String moduleName,String fieldName) {
		this.pointId = pointId;
		this.name = name;
		this.parentMeterCategory = parentCategory;
		this.moduleName = moduleName;
		this.displayName = displayName;
		this.fieldName = fieldName;
	}
	
	private static final Map<Integer, Meter_Category_Points> POINT_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, Meter_Category_Points> initTypeMap() {
		Map<Integer, Meter_Category_Points> typeMap = new HashMap<>();
		for(Meter_Category_Points type : values()) {
			typeMap.put(type.getPointId(), type);
		}
		return typeMap;
	}
	
	public static Collection<Meter_Category_Points> getAllPoints() {
		return POINT_MAP.values();
	}
	
	public static Meter_Category_Points valueOf(int categoryId) {
		return POINT_MAP.get(categoryId);
	}
}
