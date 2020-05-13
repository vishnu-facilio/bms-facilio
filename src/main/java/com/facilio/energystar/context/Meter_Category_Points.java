package com.facilio.energystar.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;

public enum Meter_Category_Points {
	
	ENERGY(1,"Usage kWh","usage",Meter_Category.ELECTRIC,"energydata","totalEnergyConsumptionDelta",NumberAggregateOperator.SUM),
	ENERGY_COST(2,"Cost","cost",Meter_Category.ELECTRIC,null,null,null),
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
	String unitOfMessure;
	NumberAggregateOperator aggr;
	
	Meter_Category parentMeterCategory;
	
	public NumberAggregateOperator getAggr() {
		return aggr;
	}
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
	Meter_Category_Points(int pointId,String displayName, String name,Meter_Category parentCategory,String moduleName,String fieldName,NumberAggregateOperator aggr) {
		this.pointId = pointId;
		this.name = name;
		this.parentMeterCategory = parentCategory;
		this.displayName = displayName;
		this.moduleName = moduleName;
		this.fieldName = fieldName;
		this.aggr = aggr;
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
	
	private static final Map<Integer, List<Meter_Category_Points>> POINT_CATEGORY_MAP = Collections.unmodifiableMap(initPointCategoryMap());
	
	private static Map<Integer, List<Meter_Category_Points>> initPointCategoryMap() {
		Map<Integer, List<Meter_Category_Points>> typeMap = new HashMap<>();
		
		for(Meter_Category_Points type : values()) {
			List<Meter_Category_Points> pointList = typeMap.get(type.getparentMeterCategory().getIntVal()) == null ? new ArrayList<>() : typeMap.get(type.getparentMeterCategory().getIntVal()); 
			pointList.add(type);
			
			typeMap.put(type.getparentMeterCategory().getIntVal(), pointList);
		}
		return typeMap;
	}
	
	
	public static List<Meter_Category_Points> getPointList(int parentCategoryId) {
		return POINT_CATEGORY_MAP.get(parentCategoryId);
	}
}
