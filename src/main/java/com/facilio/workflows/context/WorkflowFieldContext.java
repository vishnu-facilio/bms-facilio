package com.facilio.workflows.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class WorkflowFieldContext extends ModuleBaseWithCustomFields {

	Long workflowId;
	Long fieldId;
	public Long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
	
	
}
