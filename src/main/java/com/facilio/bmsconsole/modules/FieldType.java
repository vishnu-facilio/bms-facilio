package com.facilio.bmsconsole.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.EnumOperators;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.criteria.StringOperators;

public enum FieldType {
	MISC(0, "Misc", null, null),
	STRING(1, "String", new String[] {"STRING_CF1", "STRING_CF2", "STRING_CF3", "STRING_CF4", "STRING_CF5", "STRING_CF6", "STRING_CF7", "STRING_CF8", "STRING_CF9", "STRING_CF10", "STRING_CF11", "STRING_CF12", "STRING_CF3", "STRING_CF14", "STRING_CF15"}, StringOperators.getAllOperators()),
	NUMBER(2, "Number", new String[] {"NUMBER_CF1", "NUMBER_CF2", "NUMBER_CF3", "NUMBER_CF4", "NUMBER_CF5", "NUMBER_CF6", "NUMBER_CF7", "NUMBER_CF8", "NUMBER_CF9", "NUMBER_CF10", "NUMBER_CF11", "NUMBER_CF12", "NUMBER_CF13", "NUMBER_CF14", "NUMBER_CF15"}, NumberOperators.getAllOperators()),
	DECIMAL(3, "Decimal", new String[] {"DECIMAL_CF1", "DECIMAL_CF2", "DECIMAL_CF3", "DECIMAL_CF4", "DECIMAL_CF5","DECIMAL_CF6", "DECIMAL_CF7", "DECIMAL_CF8", "DECIMAL_CF9", "DECIMAL_CF10", "DECIMAL_CF11", "DECIMAL_CF12", "DECIMAL_CF13", "DECIMAL_CF14", "DECIMAL_CF15"}, NumberOperators.getAllOperators()),
	BOOLEAN(4, "Boolean", new String[] {"BOOLEAN_CF1", "BOOLEAN_CF2", "BOOLEAN_CF3", "BOOLEAN_CF4", "BOOLEAN_CF5","BOOLEAN_CF6", "BOOLEAN_CF7", "BOOLEAN_CF8", "BOOLEAN_CF9", "BOOLEAN_CF10", "BOOLEAN_CF11", "BOOLEAN_CF12", "BOOLEAN_CF13", "BOOLEAN_CF14", "BOOLEAN_CF15"}, BooleanOperators.getAllOperators()),
	DATE(5, "Date", new String[] {"DATE_CF1", "DATE_CF2", "DATE_CF3", "DATE_CF4", "DATE_CF5","DATE_CF6", "DATE_CF7", "DATE_CF8", "DATE_CF9", "DATE_CF10"}, DateOperators.getAllOperators()),
	DATE_TIME(6, "DateTime", new String[] {"DATETIME_CF1", "DATETIME_CF2", "DATETIME_CF3", "DATETIME_CF4", "DATETIME_CF5", "DATETIME_CF6", "DATETIME_CF7", "DATETIME_CF8", "DATETIME_CF9", "DATETIME_CF10", "DATETIME_CF11", "DATETIME_CF12", "DATETIME_CF13", "DATETIME_CF14", "DATETIME_CF15"}, DateOperators.getAllOperators()),
	LOOKUP(7, "Lookup", null, LookupOperator.getAllOperators()),
	ENUM(8, "Enum", new String[] {"ENUM_CF1", "ENUM_CF2", "ENUM_CF3", "ENUM_CF4", "ENUM_CF5","ENUM_CF6", "ENUM_CF7", "ENUM_CF8", "ENUM_CF9", "ENUM_CF10"}, EnumOperators.getAllOperators()),
	FILE(9, "File", null, null),
	COUNTER(10, "Counter", null, null)
	;
	
	private int value;
	private String typeString;
	private String[] columnNames;
	private Map<String, Operator> operators;
	
	private FieldType(int value, String typeString, String[] columnNames, Map<String, Operator> operators) {
		// TODO Auto-generated constructor stub
		this.value = value;
		this.typeString = typeString;
		this.columnNames = columnNames;
		this.operators = operators;
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
	
	public Map<String, Operator> getOperators() {
		return operators;
	}
	public Operator getOperator(String operator) {
		return operators.get(operator);
	}
	
	private static final Map<Integer, FieldType> typeMap = Collections.unmodifiableMap(initTypeMap());
	
	private static Map<Integer, FieldType> initTypeMap() {
		Map<Integer, FieldType> typeMap = new HashMap<>();
		for(FieldType type : values()) {
			typeMap.put(type.getTypeAsInt(), type);
		}
		return typeMap;
	}
	
	public static FieldType getCFType(int value) {
		return typeMap.get(value);
	}

	private static final Map<String, FieldType> typeStringMap = Collections.unmodifiableMap(initTypeStringMap());
	
	private static Map<String, FieldType> initTypeStringMap() {
		Map<String, FieldType> typeMap = new HashMap<>();
		for(FieldType type : values()) {
			typeMap.put(type.getTypeAsString(), type);
		}
		return typeMap;
	}
	
	public static FieldType getCFType(String value) {
		return typeStringMap.get(value);
	}

	
	public String toString() {
		return typeString;
	}
}
