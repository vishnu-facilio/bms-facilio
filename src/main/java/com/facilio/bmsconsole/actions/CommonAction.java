package com.facilio.bmsconsole.actions;

import com.facilio.workflows.context.WorkflowContext;
import com.opensymphony.xwork2.ActionSupport;

public class CommonAction extends ActionSupport {
	
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	
	private Object result;
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	public String executeWorkflow() throws Exception {
		result = workflow.executeWorkflow(); 
		return SUCCESS;
	}
}
