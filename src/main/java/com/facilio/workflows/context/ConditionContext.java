package com.facilio.workflows.context;

import java.io.Serializable;
import java.util.List;

import com.facilio.workflows.conditions.context.ElseContext;
import com.facilio.workflows.conditions.context.ElseIfContext;
import com.facilio.workflows.conditions.context.IfContext;

public class ConditionContext implements Serializable,WorkflowExpression {

	IfContext IfContext;
	List<ElseIfContext> elesIfContexts;
	ElseContext elseContext;
	WorkflowContext workflowContext;
	
	@Override
	public Object execute() throws Exception {
		
		if(IfContext.evalCriteriaAndExecute(workflowContext)) {
			return null;
		}
		if(elesIfContexts != null && !elesIfContexts.isEmpty()) {
			
			for(ElseIfContext elesIfContext :elesIfContexts) {
				
				if(elesIfContext.evalCriteriaAndExecute(workflowContext)) {
					return null;
				}
			}
		}
		if(elseContext != null && elseContext.evalCriteriaAndExecute(workflowContext)) {
			return null;
		}
		return null;
	}

	@Override
	public int getWorkflowExpressionType() {
		
		return WorkflowExpressionType.CONDITION.getValue();
	}

}
