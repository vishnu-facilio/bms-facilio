package com.facilio.bmsconsole.modules;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.criteria.StringOperators;

public enum FieldType {
	MISC(0, "Misc", null, null),
	STRING(1, "String", new String[] {"STRING_CF1", "STRING_CF2", "STRING_CF3", "STRING_CF4", "STRING_CF5"}, StringOperators.getAllOperators()),
	NUMBER(2, "Number", new String[] {"NUMBER_CF1", "NUMBER_CF2", "NUMBER_CF3", "NUMBER_CF4", "NUMBER_CF5"}, NumberOperators.getAllOperators()),
	DECIMAL(3, "Decimal", new String[] {"DECIMAL_CF1", "DECIMAL_CF2", "DECIMAL_CF3", "DECIMAL_CF4", "DECIMAL_CF5"}, NumberOperators.getAllOperators()),
	BOOLEAN(4, "Boolean", new String[] {"BOOLEAN_CF1", "BOOLEAN_CF2", "BOOLEAN_CF3", "BOOLEAN_CF4", "BOOLEAN_CF5"}, BooleanOperators.getAllOperators()),
	DATE(5, "Date", new String[] {"DATE_CF1", "DATE_CF2", "DATE_CF3", "DATE_CF4", "DATE_CF5"}, DateOperators.getAllOperators()),
	DATE_TIME(6, "DateTime", new String[] {"DATETIME_CF1", "DATETIME_CF2", "DATETIME_CF3", "DATETIME_CF4", "DATETIME_CF5"}, DateOperators.getAllOperators()),
	LOOKUP(7, "Lookup", null, LookupOperator.getAllOperators()),
	ENUM(8, "Enum", null, null)
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

	
	public String toString()
	{
		return typeString;
	}
}
