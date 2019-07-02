package com.facilio.workflows.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public enum WorkflowFieldType {

	VOID(0, "void"),
	STRING(1, "String",String.class),
	NUMBER(2, "Number",Integer.class,Long.class,Double.class),
	BOOLEAN(3, "Boolean",Boolean.class),
	MAP(4,"Map",HashMap.class,Map.class),
	LIST(5,"List",ArrayList.class,List.class),
	;
	
	public Class[] getObjectClass() {
		return objectClass;
	}

	private int intValue;
	private String stringValue;
	private Class[] objectClass;
	
	private WorkflowFieldType(int intValue,String stringValue) {
		this.intValue = intValue;
		this.stringValue = stringValue;
	}
	private WorkflowFieldType(int intValue,String stringValue,Class<?>... objectClass) {
		this.intValue = intValue;
		this.stringValue = stringValue;
		this.objectClass = objectClass;
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
