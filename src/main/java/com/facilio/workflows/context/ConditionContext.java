package com.facilio.workflows.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.facilio.workflows.conditions.context.ElseContext;
import com.facilio.workflows.conditions.context.ElseIfContext;
import com.facilio.workflows.conditions.context.IfContext;

public class ConditionContext implements Serializable,WorkflowExpression {

	/**
	 * 
	 */
	
	public Object clone() throws CloneNotSupportedException{  
		return super.clone();  
	}  
	private static final long serialVersionUID = 1L;
	IfContext IfContext;
	List<ElseIfContext> elseIfContexts;
	ElseContext elseContext;
	WorkflowContext workflowContext;
	
	@Override
	public Object execute() throws Exception {
		
		if(IfContext.evalCriteriaAndExecute(workflowContext)) {
			return null;
		}
		if(elseIfContexts != null && !elseIfContexts.isEmpty()) {
			
			for(ElseIfContext elseIfContext :elseIfContexts) {
				
				if(elseIfContext.evalCriteriaAndExecute(workflowContext)) {
					return null;
				}
			}
		}
		if(elseContext != null && elseContext.evalCriteriaAndExecute(workflowContext)) {
			return null;
		}
		return null;
	}

	public IfContext getIfContext() {
		return IfContext;
	}

	public void setIfContext(IfContext ifContext) {
		IfContext = ifContext;
	}

	public List<ElseIfContext> getElseIfContexts() {
		return elseIfContexts;
	}

	public void setElseIfContexts(List<ElseIfContext> elseIfContexts) {
		this.elseIfContexts = elseIfContexts;
	}
	
	public void addElseIfContext(ElseIfContext elseIfContext) {
		this.elseIfContexts = this.elseIfContexts == null ? new ArrayList<>() : elseIfContexts;
		elseIfContexts.add(elseIfContext);
	}

	public ElseContext getElseContext() {
		return elseContext;
	}

	public void setElseContext(ElseContext elseContext) {
		this.elseContext = elseContext;
	}

	public WorkflowContext getWorkflowContext() {
		return workflowContext;
	}

	public void setWorkflowContext(WorkflowContext workflowContext) {
		this.workflowContext = workflowContext;
	}

	@Override
	public int getWorkflowExpressionType() {
		
		return WorkflowExpressionType.CONDITION.getValue();
	}

}
