package com.facilio.workflowv2.contexts;

public class WorkflowReadingContext {
	
	public WorkflowReadingContext() {
		
	}
	public WorkflowReadingContext(long fieldId,long parentId) {
		this.fieldId = fieldId;
		this.parentId = parentId;
	}
	long fieldId;
	long parentId;
	
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	
	@Override
	public String toString() {
		return "WorkflowReadingContext [fieldId=" + fieldId + ", parentId=" + parentId + "]";
	}
}
