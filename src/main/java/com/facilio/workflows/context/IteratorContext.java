package com.facilio.workflows.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONArray;

import com.facilio.bmsconsole.commands.CalculateDerivationCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.workflows.util.WorkflowUtil;

public class IteratorContext implements WorkflowExpression {

	/**
	 * 
	 */
	
	public Object clone() throws CloneNotSupportedException{  
		return super.clone();  
	}  
	private static final long serialVersionUID = 1L;
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
	public void setWorkflowExpressions(List<WorkflowExpression> workflowExpressions) throws Exception {
		
		this.expressions = workflowExpressions;
	}
	public void addExpression(WorkflowExpression expression) {
		expressions = expressions == null ? new ArrayList<>() : expressions; 
		
		expressions.add(expression);
	}
	
	public Object execute(WorkflowContext workflowContext) throws Exception {
		
		Map<String, Object> variableToExpresionMap = workflowContext.getVariableResultMap();
		if(iteratableVariable == null || variableToExpresionMap.get(iteratableVariable) == null || !isIteratableVariable(variableToExpresionMap.get(iteratableVariable))) {
			return null;
		}
		
		Object iterateObject = variableToExpresionMap.get(iteratableVariable);
		
		if(iterateObject instanceof Collection) {
			
			List iterateList = new ArrayList((Collection)iterateObject);
			
			for(int i=0 ; i<iterateList.size() ;i++) {
				variableToExpresionMap.put(loopVariableIndexName, i);
				variableToExpresionMap.put(loopVariableValueName, iterateList.get(i));
				
//				LOGGER.log(Level.SEVERE, "variableToExpresionMap -- "+variableToExpresionMap);
				List<WorkflowExpression> newExpressions = new ArrayList<WorkflowExpression>(expressions.size());
			    for (WorkflowExpression expression : expressions) {
			    	newExpressions.add((WorkflowExpression)expression.clone());
			    }
			    WorkflowUtil.executeExpression(newExpressions, workflowContext);
			}
			variableToExpresionMap.remove(loopVariableIndexName);
			variableToExpresionMap.remove(loopVariableValueName);
			
		}
		else if(iterateObject instanceof Map) {
			Map iterateMap = (Map) iterateObject;
			for(Object key :iterateMap.keySet() ) {
				variableToExpresionMap.put(loopVariableIndexName, key);						// index acts as key for Map Iteration 
				variableToExpresionMap.put(loopVariableValueName, iterateMap.get(key));
				
				List<WorkflowExpression> newExpressions = new ArrayList<WorkflowExpression>(expressions.size());
			    for (WorkflowExpression expression : expressions) {
			    	newExpressions.add((WorkflowExpression)expression.clone());
			    }
			    WorkflowUtil.executeExpression(newExpressions, workflowContext);
			}
			variableToExpresionMap.remove(loopVariableIndexName);
			variableToExpresionMap.remove(loopVariableValueName);
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
	@Override
	public int getWorkflowExpressionType(){
		// TODO Auto-generated method stub
		return WorkflowExpressionType.ITERATION.getValue();
	}
}
