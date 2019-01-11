package com.facilio.bmsconsole.context;

import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class WidgetVsWorkflowContext {
	
	Long id,workflowId,baseSpaceId,widgetId;
	String workflowName,workflowString;
	WorkflowContext workflow;

	public WorkflowContext getWorkflow() throws Exception {
		
		if(workflow == null && workflowString != null) {
			workflow = WorkflowUtil.getWorkflowContextFromString(workflowString);
		}
		return workflow;
	}

	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}

	public String getWorkflowString() throws Exception {
		if(workflowString == null && workflow != null) {
			if(workflow.getWorkflowString() != null) {
				workflowString =  workflow.getWorkflowString();
			}
			else {
				workflowString = WorkflowUtil.getXmlStringFromWorkflow(workflow);
			}
		}
		return workflowString;
		
	}

	public void setWorkflowString(String workflowString) {
		this.workflowString = workflowString;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}

	public Long getBaseSpaceId() {
		return baseSpaceId;
	}

	public void setBaseSpaceId(Long baseSpaceId) {
		this.baseSpaceId = baseSpaceId;
	}

	public Long getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(Long widgetId) {
		this.widgetId = widgetId;
	}

}
