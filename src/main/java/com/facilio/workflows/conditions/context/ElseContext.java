package com.facilio.workflows.conditions.context;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.IteratorContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.workflows.context.WorkflowExpression.WorkflowExpressionType;
import org.json.simple.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElseContext implements WorkflowCondition,Serializable {

	private static final long serialVersionUID = 1L;
	List<WorkflowExpression> expressions;
	
	public List<WorkflowExpression> getExpressions() {
		return expressions;
	}
	
	// only from client
	public void setExpressions(JSONArray workflowExpressions) throws Exception {
		if(workflowExpressions != null) {
			
			for(int i=0 ;i<workflowExpressions.size();i++) {
				
				WorkflowExpression workflowExpression = null;
				
				Map workflowExp = (Map)workflowExpressions.get(i);
				Integer workflowExpressionType = (Integer) workflowExp.get("workflowExpressionType");
				if(workflowExpressionType == null) {
					workflowExpressionType = 0;
				}
				if(workflowExpressionType <= 0 || workflowExpressionType == WorkflowExpressionType.EXPRESSION.getValue()) {
					workflowExpression = new ExpressionContext();
					workflowExpression = FieldUtil.getAsBeanFromMap(workflowExp, ExpressionContext.class);
				}
				else if(workflowExpressionType == WorkflowExpressionType.ITERATION.getValue()) {
					workflowExpression = new IteratorContext();
					workflowExpression = FieldUtil.getAsBeanFromMap(workflowExp, IteratorContext.class);
				}
				addWorkflowExpression(workflowExpression);
			}
		}
	}
	
	public void setWorkflowExpressions(List<WorkflowExpression> workflowExpressions) throws Exception {
		
		this.expressions = workflowExpressions;
	}
	
	public void addWorkflowExpression(WorkflowExpression expression) {
		expressions = expressions == null ? new ArrayList<>() : expressions;
		expressions.add(expression);
	}
	@Override
	public boolean evalCriteriaAndExecute(WorkflowContext workflowContext) throws Exception {
		
		WorkflowContext.executeExpression(expressions, workflowContext);
		return true;
	}
	
}
