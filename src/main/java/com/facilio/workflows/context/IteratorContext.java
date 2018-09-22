package com.facilio.workflows.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.workflows.util.WorkflowUtil;

public class IteratorContext implements Serializable,WorkflowExpression {

	String loopVariableIndexName;
	String loopVariableValueName;
	
	String iteratableVariable;
	List<WorkflowExpression> workflowExpressions;
	public List<WorkflowExpression> getWorkflowExpressions() {
		return workflowExpressions;
	}

	public void setWorkflowExpressions(List<WorkflowExpression> workflowExpressions) {
		this.workflowExpressions = workflowExpressions;
	}
	public void addWorkflowExpression(WorkflowExpression expression) {
		workflowExpressions = workflowExpressions == null ? new ArrayList<>() : workflowExpressions; 
		
		workflowExpressions.add(expression);
	}
	WorkflowContext workflowContext;
	Map<String,Object> variableToExpresionMap;
	
	public Object execute() throws Exception {
		
		if(iteratableVariable == null || variableToExpresionMap.get(iteratableVariable) == null || !isIteratableVariable(variableToExpresionMap.get(iteratableVariable))) {
			return null;
		}
		
		Object iterateObject = variableToExpresionMap.get(iteratableVariable);
		
		if(iterateObject instanceof List) {
			
			List iterateList = (List) iterateObject;
			
			for(int i=0 ; i<iterateList.size() ;i++) {
				variableToExpresionMap.put(loopVariableIndexName, i);
				variableToExpresionMap.put(loopVariableValueName, iterateList.get(i));
				
				System.out.println("variableToExpresionMap -- "+variableToExpresionMap);
				
				WorkflowContext.executeExpression(workflowExpressions, workflowContext);
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
		
		if(var instanceof List) {
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
}
