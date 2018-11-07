package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum FacilioFunctionsParamType {

	STRING(1, "String"),
	NUMBER(2, "Number"),
	DECIMAL(3, "Decimal"),
	BOOLEAN(4, "Boolean"),
	DATE(5, "Date"),
	DATE_TIME(6, "DateTime"),
	LIST(7, "List"),
	MAP(8, "Map"),
	;
	
	int value;
	String name;
	String fieldName;
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	FacilioFunctionsParamType(int value,String name) {
		this.value = value;
		this.name = name;
	}
	
	public static Map<Integer, FacilioFunctionsParamType> getAllFunctionParams() {
		return DEFAULT_FUNCTIONS;
	}
	public static FacilioFunctionsParamType getFacilioDefaultFunction(int value) {
		return DEFAULT_FUNCTIONS.get(value);
	}
	private static final Map<Integer, FacilioFunctionsParamType> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, FacilioFunctionsParamType> initTypeMap() {
		Map<Integer, FacilioFunctionsParamType> typeMap = new HashMap<>();
		for(FacilioFunctionsParamType type : FacilioFunctionsParamType.values()) {
			typeMap.put(type.getValue(), type);
		}
		return typeMap;
	}
}
