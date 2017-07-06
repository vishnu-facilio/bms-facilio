package com.facilio.bmsconsole.fields;

import java.util.HashMap;
import java.util.Map;

public enum FieldType {
	STRING(1, "String", new String[] {"STRING_CF1", "STRING_CF2", "STRING_CF3", "STRING_CF4", "STRING_CF5"}),
	NUMBER(2, "Number", new String[] {"NUMBER_CF1", "NUMBER_CF2", "NUMBER_CF3", "NUMBER_CF4", "NUMBER_CF5"}),
	LONG_INTEGER(3, "Long Integer", new String[] {"LONG_INTEGER_CF1", "LONG_INTEGER_CF2", "LONG_INTEGER_CF3", "LONG_INTEGER_CF4", "LONG_INTEGER_CF5"}),
	DECIMAL(4, "Decimal", new String[] {"DECIMAL_CF1", "DECIMAL_CF2", "DECIMAL_CF3", "DECIMAL_CF4", "DECIMAL_CF5"}),
	BOOLEAN(5, "Boolean", new String[] {"BOOLEAN_CF1", "BOOLEAN_CF2", "BOOLEAN_CF3", "BOOLEAN_CF4", "BOOLEAN_CF5"}),
	DATE(6, "Date", new String[] {"DATE_CF1", "DATE_CF2", "DATE_CF3", "DATE_CF4", "DATE_CF5"}),
	DATE_TIME(7, "DateTime", new String[] {"DATETIME_CF1", "DATETIME_CF2", "DATETIME_CF3", "DATETIME_CF4", "DATETIME_CF5"});
	
	private int value;
	private String typeString;
	private String[] columnNames;
	
	private FieldType(int value, String typeString, String[] columnNames) {
		// TODO Auto-generated constructor stub
		this.value = value;
		this.typeString = typeString;
		this.columnNames = columnNames;
	}
	
	public String getTypeAsString() {
		return typeString;
	}
	
	public int getTypeAsInt() {
		return value;
	}
	
	public String[] getColumnNames() {
		return columnNames;
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
