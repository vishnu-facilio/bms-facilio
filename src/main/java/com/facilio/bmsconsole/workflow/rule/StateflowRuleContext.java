package com.facilio.bmsconsole.workflow.rule;

public class StateflowRuleContext extends WorkflowRuleContext {
	private static final long serialVersionUID = 1L;
	
//	private String transistionName;
//	public String getTransistionName() {
//		return transistionName;
//	}
//	public void setTransistionName(String transistionName) {
//		this.transistionName = transistionName;
//	}
	
	private long fromStateId = -1;
	public long getFromStateId() {
		return fromStateId;
	}
	public void setFromStateId(long fromStateId) {
		this.fromStateId = fromStateId;
	}
	
	private long toStateId = -1;
	public long getToStateId() {
		return toStateId;
	}
	public void setToStateId(long toStateId) {
		this.toStateId = toStateId;
	}
	
	private long formId = -1; // check whether it is good to have
	public long getFormId() {
		return formId;
	}
	public void setFormId(long formId) {
		this.formId = formId;
	}
}
