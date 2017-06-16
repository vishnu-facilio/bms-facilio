package com.facilio.bmsconsole.customfields;

import java.util.HashMap;
import java.util.Map;

public enum CFType {
	STRING(1, "String"),
	NUMBER(2, "Number"),
	DECIMAL(3, "Decimal"),
	BOOLEAN(4, "Boolean"),
	DATE(5, "Date"),
	DATE_TIME(6, "DateTime");
	
	private int value;
	private String typeString;
	
	private CFType(int value, String typeString) {
		// TODO Auto-generated constructor stub
		this.value = value;
		this.typeString = typeString;
	}
	
	public String getTypeAsString() {
		return typeString;
	}
	
	public int getTypeAsInt() {
		return value;
	}
	
	private static Map<Integer, CFType> typeMap = new HashMap<>();
	
	public static void init() {
		for(CFType type : CFType.values()) {
			typeMap.put(type.getTypeAsInt(), type);
		}
	}
	
	public static CFType getCFType(int value) {
		return typeMap.get(value);
	}
}
