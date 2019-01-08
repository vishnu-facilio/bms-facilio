package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum FacilioFunctionNameSpace {

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
	;
	
	public Integer value;
	public String name;
	FacilioFunctionNameSpace(int value,String name) {
		
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
	
	static final Map<Integer, FacilioFunctionNameSpace> NAMESPACES_BY_VALUE = Collections.unmodifiableMap(initTypeMap());
	
	public static Map<Integer, FacilioFunctionNameSpace> getNamespaceMap() {
		return NAMESPACES_BY_VALUE;
	}
	public static FacilioFunctionNameSpace getFacilioDefaultFunction(int value) {
		return NAMESPACES_BY_VALUE.get(value);
	}
	
	static Map<Integer, FacilioFunctionNameSpace> initTypeMap() {
		Map<Integer, FacilioFunctionNameSpace> typeMap = new HashMap<>();
		for(FacilioFunctionNameSpace type : FacilioFunctionNameSpace.values()) {
			typeMap.put(type.getValue(), type);
		}
		return typeMap;
	}
	
	static final Map<String, FacilioFunctionNameSpace> NAMESPACES_BY_NAME = Collections.unmodifiableMap(initTypeMap1());
	
	public static FacilioFunctionNameSpace getFacilioDefaultFunction(String value) {
		return NAMESPACES_BY_NAME.get(value);
	}
	
	static Map<String, FacilioFunctionNameSpace> initTypeMap1() {
		Map<String, FacilioFunctionNameSpace> typeMap = new HashMap<>();
		for(FacilioFunctionNameSpace type : FacilioFunctionNameSpace.values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}
	
}
