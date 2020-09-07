package com.facilio.bmsconsole.context;

public class FormulaFieldResourceContext {
	
	private long id;
	private long fieldId;			//only for client
	private Long resourceId;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	public Long getResourceId() {
		return resourceId;
	}
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}	
}
