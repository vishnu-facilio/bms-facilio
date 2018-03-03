package com.facilio.workflows.context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.workflows.util.WorkflowUtil;
import com.udojava.evalex.Expression;

public class WorkflowContext {

	List<ParameterContext> parameters;
	public List<ParameterContext> getParameters() {
		return parameters;
	}

	public void setParameters(List<ParameterContext> parameters) {
		this.parameters = parameters;
	}
	
	public void addParamater(ParameterContext parameter) {
		if(this.parameters == null) {
			this.parameters = new ArrayList<>();
		}
		this.parameters.add(parameter);
	}

	List<ExpressionContext> expressions;
	
	String resultEvaluator;

	public List<ExpressionContext> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<ExpressionContext> expressions) {
		this.expressions = expressions;
	}
	public void addExpression(ExpressionContext expression) {
		if(expressions == null) {
			expressions = new ArrayList<>();
		}
		expressions.add(expression);
	}

	public String getResultEvaluator() {
		return resultEvaluator;
	}

	public void setResultEvaluator(String resultEvaluator) {
		this.resultEvaluator = resultEvaluator;
	}
	
	public Object getResult() throws Exception {
		
		Object result = null;
		
		if(getResultEvaluator() == null && isSingleExpression()) {
			return expressions.get(0).getResult();
		}
		else {
			Map<String,String> variableToExpresionMap = new HashMap<String,String>();
			for(ExpressionContext expressionContext:expressions) {
				
				Object res = expressionContext.getResult();
				if(res != null) {
					String subExpResult = res.toString();
					variableToExpresionMap.put(expressionContext.getName(), subExpResult);
				}
				else {
					variableToExpresionMap.put(expressionContext.getName(), "0");
				}
			}
			System.out.println("variableToExpresionMap --- "+variableToExpresionMap+" \n\n"+"expString --- "+getResultEvaluator());
			result =  evaluateExpression(getResultEvaluator(),variableToExpresionMap);
			System.out.println("result --- "+result);
			return result;
		}
	}
	
	public Object evaluateExpression(String exp,Map<String,String> variablesMap) {
		Expression expression = new Expression(exp);
		for(String key : variablesMap.keySet()) {
			String value = variablesMap.get(key);
			expression.with(""+key, value);
		}
		BigDecimal result = expression.eval();
		return result.doubleValue();
	}
	
	public boolean isSingleExpression() {
		if(expressions != null && expressions.size() == 1) {
			return true;
		}
		return false;
	}
	
	public boolean isMapReturnWorkflow() {
		if(expressions != null && expressions.size() == 1 && expressions.get(0).getFieldName() == null && expressions.get(0).getCriteria() != null && expressions.get(0).getModuleName() != null) {
			return true;
		}
		return false;
	}
	
	public boolean isListReturnWorkflow() {
		if(expressions != null && expressions.size() == 1 && expressions.get(0).getFieldName() != null && expressions.get(0).getCriteria() != null && expressions.get(0).getModuleName() != null && expressions.get(0).getAggregateString() == null) {
			return true;
		}
		return false;
	}
	
	public boolean isBooleanReturnWorkflow() {
		if(getResultEvaluator() != null) {
			for(String opperator: WorkflowUtil.getComparisionOpperator()) {
				if(getResultEvaluator().contains(opperator)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isSingleValueReturnWorkflow() {
		if(getResultEvaluator() != null) {
			if(isBooleanReturnWorkflow()) {
				return false;
			}
			return true;
		}
		else {
			if(expressions != null && expressions.size() == 1 && expressions.get(0).getFieldName() != null && expressions.get(0).getCriteria() != null && expressions.get(0).getModuleName() != null && expressions.get(0).getAggregateString() != null) {
				return true;
			}
		}
		return false;
	}
}
