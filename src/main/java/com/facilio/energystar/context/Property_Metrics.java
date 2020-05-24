package com.facilio.energystar.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Property_Metrics {
	
	SCORE(1, "score","Score"),
	SOURCE_EUI(2, "sourceIntensity","Source EUI"),	
	SITE_EUI(3, "siteIntensity","Site EUI"),		
	Total_GHG_Emissions_Intensity(4, "totalGHGEmissionsIntensity","Total GHG Emissions"),
	COST(5, "energyCost","Energy Cost"),
	;

	int intVal;
	String name;
	String displayName;

	public int getIntVal() {
		return intVal;
	}

	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	private Property_Metrics(int intVal, String name,String displayName) {
		this.intVal = intVal;
		this.name = name;
		this.displayName = displayName;
	}

	private static final Map<Integer, Property_Metrics> optionMap = Collections.unmodifiableMap(initTypeMap());
	
	private static final Map<String, Property_Metrics> nameMap = Collections.unmodifiableMap(initNameTypeMap());
	
	private static Map<Integer, Property_Metrics> initTypeMap() {
		Map<Integer, Property_Metrics> typeMap = new HashMap<>();

		for (Property_Metrics type : values()) {
			typeMap.put(type.getIntVal(), type);
		}
		return typeMap;
	}
	
	private static Map<String, Property_Metrics> initNameTypeMap() {
		Map<String, Property_Metrics> typeMap = new HashMap<>();

		for (Property_Metrics type : values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}

	public static Map<Integer, Property_Metrics> getAllMetrics() {
		return optionMap;
	}
	
	public static Map<String, Property_Metrics> getNameMap() {
		return nameMap;
	}

}
