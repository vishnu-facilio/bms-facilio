package com.facilio.workflows.context;

import java.util.logging.Logger;

public class WorkflowUserFunctionContext extends WorkflowContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(WorkflowUserFunctionContext.class.getName());
	
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
