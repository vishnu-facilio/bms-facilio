package com.facilio.energystar.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Property_Metrics {
	
	SCORE(1, "score"),
	SOURCE_EUI(2, "sourceIntensity"),	
	SITE_EUI(3, "siteIntensity"),		
	Total_GHG_Emissions_Intensity(4, "totalGHGEmissionsIntensity"),
	COST(5, "energyCost"),
	;

	int intVal;
	String name;

	public int getIntVal() {
		return intVal;
	}

	public String getName() {
		return name;
	}

	private Property_Metrics(int intVal, String name) {
		this.intVal = intVal;
		this.name = name;
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
