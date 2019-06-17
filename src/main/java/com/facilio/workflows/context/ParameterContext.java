package com.facilio.workflows.context;

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
	WorkflowFieldType workflowFieldType;
	Object value;							// depricate this in new Workflow  
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTypeString() {
		return workflowFieldType.getStringValue();
	}
	public void setTypeString(String typeString) {
		this.workflowFieldType =  WorkflowFieldType.getStringvaluemap().get(typeString);
	}
	public WorkflowFieldType getWorkflowFieldType() {
		return workflowFieldType;
	}
	public void setWorkflowFieldType(WorkflowFieldType workflowFieldType) {
		this.workflowFieldType = workflowFieldType;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
