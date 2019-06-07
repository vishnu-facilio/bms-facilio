package com.facilio.workflows.context;

import com.facilio.modules.FieldType;

import java.io.Serializable;

public class ParameterContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ParameterContext() {
		
	}
	public ParameterContext(String name,Object value) {
		this.name = name;
		this.value = value;
	}
	String name;
	String typeString;
	Object value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTypeString() {
		return typeString;
	}
	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}
	public FieldType getType() {
		if(getTypeString() != null) {
			return FieldType.getCFType(getTypeString());
		}
		return null;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	
}
