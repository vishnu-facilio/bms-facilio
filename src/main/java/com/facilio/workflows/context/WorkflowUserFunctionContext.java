package com.facilio.workflows.context;

public class WorkflowUserFunctionContext extends WorkflowContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String nameSpaceName;
	
	public String getNameSpaceName() {
		return nameSpaceName;
	}
	public void setNameSpaceName(String nameSpaceName) {
		this.nameSpaceName = nameSpaceName;
	}
}
