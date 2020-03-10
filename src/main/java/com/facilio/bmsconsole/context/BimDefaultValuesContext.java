package com.facilio.bmsconsole.context;

public class BimDefaultValuesContext implements Cloneable {
	/**
	 * 
	 */

	private Long id;
	private Long bimId;
	private Long moduleId;
	private Long fieldId;
	private String defaultValue;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBimId() {
		return bimId;
	}

	public void setBimId(Long bimId) {
		this.bimId = bimId;
	}

	public Long getModuleId() {
		return moduleId;
	}

	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public Object clone() throws CloneNotSupportedException { 
		return super.clone(); 
	}

}