package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum FacilioSystemFunctionNameSpace {

	DEFAULT(1,"default"),
	DATE(2,"date"),
	COST(3,"cost"),
	MATH(4,"math"),
	LIST(5,"list"),
	MAP(6,"map"),
	STRING(7,"string"),
	THERMOPHYSICALR134A(8,"thermoPhysical.R134a"),
	READINGS(9,"readings"),
	PSYCHROMETRICS(10,"psychrometrics"),
	ENERGYMETER(11,"energyMeter"),
	MODULE(12,"module"),
	RESOURCE(13,"resource"),
	SYSTEM(14, "system"), //For internal purpose only. Should never be exposed for Users
	ASSET(15, "asset"),
	WORKORDER(16, "workorder"),
	CONSUMPTION(17, "consumption"),
	ML(18,"ml"),
	NOTIFICATION(19,"notification"),
	DATE_RANGE(20,"dateRange"),
	;
	
	
	public Integer value;
	public String name;
	FacilioSystemFunctionNameSpace(int value,String name) {
		
		this.value = value;
		this.name = name;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	static final Map<Integer, FacilioSystemFunctionNameSpace> NAMESPACES_BY_VALUE = Collections.unmodifiableMap(initTypeMap());
	
	public static Map<Integer, FacilioSystemFunctionNameSpace> getNamespaceMap() {
		return NAMESPACES_BY_VALUE;
	}
	public static FacilioSystemFunctionNameSpace getFacilioDefaultFunction(int value) {
		return NAMESPACES_BY_VALUE.get(value);
	}
	
	static Map<Integer, FacilioSystemFunctionNameSpace> initTypeMap() {
		Map<Integer, FacilioSystemFunctionNameSpace> typeMap = new HashMap<>();
		for(FacilioSystemFunctionNameSpace type : FacilioSystemFunctionNameSpace.values()) {
			typeMap.put(type.getValue(), type);
		}
		return typeMap;
	}
	
	static final Map<String, FacilioSystemFunctionNameSpace> NAMESPACES_BY_NAME = Collections.unmodifiableMap(initTypeMap1());
	
	public static FacilioSystemFunctionNameSpace getFacilioDefaultFunction(String value) {
		return NAMESPACES_BY_NAME.get(value);
	}
	
	static Map<String, FacilioSystemFunctionNameSpace> initTypeMap1() {
		Map<String, FacilioSystemFunctionNameSpace> typeMap = new HashMap<>();
		for(FacilioSystemFunctionNameSpace type : FacilioSystemFunctionNameSpace.values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}
	
}
