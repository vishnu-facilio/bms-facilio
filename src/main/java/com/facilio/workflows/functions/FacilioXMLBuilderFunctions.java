package com.facilio.workflows.functions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.xml.builder.XMLBuilder;

public enum FacilioXMLBuilderFunctions implements FacilioWorkflowFunctionInterface  {

	CREATE(1,"create") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			String name = (String) objects[0];
			return XMLBuilder.create(name);
		}
		
	},
	
	ELEMENT(2,"element") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			XMLBuilder xmlBuilder = (XMLBuilder) objects[0];
			
			String elementName = (String) objects[1];
			
			return xmlBuilder.element(elementName);
		}
		
	},
	
	TEXT(3,"text") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			XMLBuilder xmlBuilder = (XMLBuilder) objects[0];
			
			String elementText = (String) objects[1];
			
			return xmlBuilder.text(elementText);
		}
		
	},
	
	ATTRIBUTE(4,"attribute") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			XMLBuilder xmlBuilder = (XMLBuilder) objects[0];
			
			String key = (String) objects[1];
			String value = (String) objects[1];
			return xmlBuilder.attr(key, value);
		}
		
	},
	
	PARENT(5,"parent") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			XMLBuilder xmlBuilder = (XMLBuilder) objects[0];
			return xmlBuilder.parent();
		}
		
	},
	
	
	GETXMLSTRING(6,"getXMLString") {

		@Override
		public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {
			// TODO Auto-generated method stub
			
			XMLBuilder xmlBuilder = (XMLBuilder) objects[0];
			return xmlBuilder.getAsXMLString();
		}
		
	},
	
	;
	
	private Integer value;
	private String functionName;
	private String namespace = "xml";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.XML_BUILDER;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	FacilioXMLBuilderFunctions(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioXMLBuilderFunctions> getAllFunctions() {
		return CONTROL_FUNCTIONS;
	}
	public static FacilioXMLBuilderFunctions getFacilioXMLBuilderFunctions(String functionName) {
		return CONTROL_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioXMLBuilderFunctions> CONTROL_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioXMLBuilderFunctions> initTypeMap() {
		Map<String, FacilioXMLBuilderFunctions> typeMap = new HashMap<>();
		for(FacilioXMLBuilderFunctions type : FacilioXMLBuilderFunctions.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}