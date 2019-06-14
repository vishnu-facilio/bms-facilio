package com.facilio.workflows.context;

public class WorkflowUserFunctionContext extends WorkflowContext {
	
	Long nameSpaceId;
	String name;
	
	public Long getNameSpaceId() {
		return nameSpaceId;
	}
	public void setNameSpaceId(Long nameSpaceId) {
		this.nameSpaceId = nameSpaceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
