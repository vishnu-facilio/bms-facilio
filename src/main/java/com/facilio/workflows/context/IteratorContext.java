package com.facilio.workflows.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;

import com.facilio.bmsconsole.commands.CalculateDerivationCommand;
import com.facilio.bmsconsole.modules.FieldUtil;

public class IteratorContext implements Serializable,WorkflowExpression {

	private static final Logger LOGGER = Logger.getLogger(CalculateDerivationCommand.class.getName());
	String loopVariableIndexName;
	String loopVariableValueName;
	
	String iteratableVariable;
	List<WorkflowExpression> expressions;
	public List<WorkflowExpression> getExpressions() {
		return expressions;
	}

	// only from client
	public void setExpressions(JSONArray workflowExpressions) {
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
				addExpression(workflowExpression);
			}
		}
	}
	public void addExpression(WorkflowExpression expression) {
		expressions = expressions == null ? new ArrayList<>() : expressions; 
		
		expressions.add(expression);
	}
	WorkflowContext workflowContext;
	Map<String,Object> variableToExpresionMap;
	
	public Object execute() throws Exception {
		
		if(iteratableVariable == null || variableToExpresionMap.get(iteratableVariable) == null || !isIteratableVariable(variableToExpresionMap.get(iteratableVariable))) {
			return null;
		}
		
		Object iterateObject = variableToExpresionMap.get(iteratableVariable);
		
		if(iterateObject instanceof Collection) {
			
			List iterateList = new ArrayList((Collection)iterateObject);
			
			for(int i=0 ; i<iterateList.size() ;i++) {
				variableToExpresionMap.put(loopVariableIndexName, i);
				variableToExpresionMap.put(loopVariableValueName, iterateList.get(i));
				
				LOGGER.log(Level.SEVERE, "variableToExpresionMap -- "+variableToExpresionMap);
				
				WorkflowContext.executeExpression(expressions, workflowContext);
			}
			variableToExpresionMap.remove(loopVariableIndexName);
			variableToExpresionMap.remove(loopVariableValueName);
			
		}
		else if(iterateObject instanceof Map) {
			
			Map iterateMap = (Map) iterateObject;
		}
		
		return null;
	}
	
	public String getLoopVariableIndexName() {
		return loopVariableIndexName;
	}

	public void setLoopVariableIndexName(String loopVariableIndexName) {
		this.loopVariableIndexName = loopVariableIndexName;
	}

	public boolean isIteratableVariable(Object var) {
		
		if(var instanceof Collection) {
			return true;
		}
		else if(var instanceof Map) {
			return true;
		}
		return false;
	}
	
	public Map<String, Object> getVariableToExpresionMap() {
		return variableToExpresionMap;
	}
	public void setVariableToExpresionMap(Map<String, Object> variableToExpresionMap) {
		this.variableToExpresionMap = variableToExpresionMap;
	}
	public String getLoopVariableValueName() {
		return loopVariableValueName;
	}
	public void setLoopVariableValueName(String loopVariableValueName) {
		this.loopVariableValueName = loopVariableValueName;
	}
	public String getIteratableVariable() {
		return iteratableVariable;
	}
	public void setIteratableVariable(String iteratableVariable) {
		this.iteratableVariable = iteratableVariable;
	}
	public WorkflowContext getWorkflowContext() {
		return workflowContext;
	}
	public void setWorkflowContext(WorkflowContext workflowContext) {
		this.workflowContext = workflowContext;
	}
	@Override
	public int getWorkflowExpresionType(){
		// TODO Auto-generated method stub
		return WorkflowExpressionType.ITERATION.getValue();
	}
}
