package com.facilio.workflows.conditions.context;

import com.facilio.workflows.context.WorkflowContext;

public interface WorkflowCondition {
	
	public boolean evalCriteriaAndExecute(WorkflowContext workflowContext) throws Exception;
}
