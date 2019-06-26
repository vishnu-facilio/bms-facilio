package com.facilio.bmsconsole.workflow.rule;

public class RecordSpecificRuleContext extends WorkflowRuleContext{

private static final long serialVersionUID = 1L;
	
	
	private long moduleId;

	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private String moduleName;

	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	
	
}
