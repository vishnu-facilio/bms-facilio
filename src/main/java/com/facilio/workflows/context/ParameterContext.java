package com.facilio.workflows.context;

import com.facilio.bmsconsole.modules.FieldType;

public class ParameterContext {

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
