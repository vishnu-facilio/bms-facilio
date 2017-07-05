package com.facilio.bmsconsole.fields;

import java.util.HashMap;
import java.util.Map;

public enum FieldType {
	STRING(1, "String"),
	NUMBER(2, "Number"),
	LONG_INTEGER(3, "Long Integer"),
	DECIMAL(4, "Decimal"),
	BOOLEAN(5, "Boolean"),
	DATE(6, "Date"),
	DATE_TIME(7, "DateTime");
	
	private int value;
	private String typeString;
	
	private FieldType(int value, String typeString) {
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
	
	private static Map<Integer, FieldType> typeMap = new HashMap<>();
	
	public static void init() {
		for(FieldType type : FieldType.values()) {
			typeMap.put(type.getTypeAsInt(), type);
		}
	}
	
	public static FieldType getCFType(int value) {
		return typeMap.get(value);
	}
}
