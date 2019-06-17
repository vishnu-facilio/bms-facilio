package com.facilio.workflows.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum WorkflowFieldType {

	STRING(1, "String"),
	NUMBER(2, "Number"),
	BOOLEAN(3, "Boolean"),
	MAP(4,"Map"),
	LIST(5,"List"),
	;
	
	private int intValue;
	private String stringValue;
	
	
	private WorkflowFieldType(int intValue,String stringValue) {
		this.intValue = intValue;
		this.stringValue = stringValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	private static final Map<Integer, WorkflowFieldType> intValueMap = Collections.unmodifiableMap(initIntValueMap());
	private static final Map<String, WorkflowFieldType> stringValueMap = Collections.unmodifiableMap(initStringValueMap());


	private static Map<Integer, WorkflowFieldType> initIntValueMap() {
		Map<Integer, WorkflowFieldType> typeMap = new HashMap<>();
		for(WorkflowFieldType type : values()) {
			typeMap.put(type.getIntValue(), type);
		}
		return typeMap;
	}

	private static Map<String, WorkflowFieldType> initStringValueMap() {
		Map<String, WorkflowFieldType> typeMap = new HashMap<>();
		for(WorkflowFieldType type : values()) {
			typeMap.put(type.getStringValue(), type);
		}
		return typeMap;
	}

	public static Map<Integer, WorkflowFieldType> getIntvaluemap() {
		return intValueMap;
	}

	public static Map<String, WorkflowFieldType> getStringvaluemap() {
		return stringValueMap;
	}
	
}
