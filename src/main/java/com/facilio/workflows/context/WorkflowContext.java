package com.facilio.workflows.context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.workflows.util.WorkflowUtil;
import com.udojava.evalex.Expression;

public class WorkflowContext {
	
	public static String VARIABLE_PLACE_HOLDER = "\\$\\{.+\\}";

	Long id;
	Long orgId;
	String workflowString;
	List<ParameterContext> parameters;
	List<ExpressionContext> expressions;
	Map<String,Object> variableResultMap;
	String resultEvaluator;

	public Map<String, Object> getVariableResultMap() {
		return variableResultMap;
	}

	public void setVariableResultMap(Map<String, Object> variableToExpresionMap) {
		this.variableResultMap = variableToExpresionMap;
	}
	public void addVariableResultMap(String key,Object value) {
		if(this.variableResultMap == null) {
			this.variableResultMap = new HashMap<>();
		}
		this.variableResultMap.put(key, value);
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getWorkflowString() {
		return workflowString;
	}

	public void setWorkflowString(String workflowString) {
		this.workflowString = workflowString;
	}

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

	public Object executeWorkflow() throws Exception {
		
		Object result = null;
		
		variableResultMap = new HashMap<String,Object>();
		for(ParameterContext parameter:parameters) {
			variableResultMap.put(parameter.getName(), parameter.getValue());
		}
		if (expressions != null) {
			for(int i=0; i<expressions.size(); i++) {
				
				ExpressionContext expressionContext = expressions.get(i);
				
				expressionContext = fillParamterAndParseExpressionContext(expressionContext);
				expressionContext.setVariableToExpresionMap(variableResultMap);
				
				Object res = expressionContext.executeExpression();
				if(res == null) {
					res = "0";
				}
				variableResultMap.put(expressionContext.getName(), res);
				
				ParameterContext parameterContext = new ParameterContext();
				parameterContext.setName(expressionContext.getName());
				parameterContext.setValue(variableResultMap.get(expressionContext.getName()));
				this.addParamater(parameterContext);
				
				if(i ==0 && getResultEvaluator() == null && isSingleExpression()) {
					return res;
				}
			}
		}
		System.out.println("variableToExpresionMap --- "+variableResultMap+" \n\n"+"expString --- "+getResultEvaluator());
		
		result =  evaluateExpression(getResultEvaluator(),variableResultMap);
		System.out.println("result --- "+result);
		return result;
	}
	
	private ExpressionContext fillParamterAndParseExpressionContext(ExpressionContext expressionContext) throws Exception {
		
		String expressionString = expressionContext.getExpressionString();
		System.out.println("BEFORE STRING --- "+expressionString);
		
		if(expressionContext.getExpressionString().split(VARIABLE_PLACE_HOLDER).length > 1) {
			for(ParameterContext parameter :parameters) {
				String val = null;
				if (parameter.getValue() != null) {
					val = parameter.getValue().toString();
				}
				else {
					val = "";
				}
				
				String var = "${"+parameter.getName()+"}";
				String varRegex = "\\$\\{"+parameter.getName()+"\\}";
				if(expressionString.contains(var)) {
					expressionString = expressionString.replaceAll(varRegex, val);
				}
			}
		}
		System.out.println("AFTER STRING --- "+expressionString);
		expressionContext = WorkflowUtil.getExpressionContextFromExpressionString(expressionString);
		
		return expressionContext;
	}

	public Object evaluateExpression(String exp,Map<String,Object> variablesMap) {

		System.out.println("EXPRESSION STRING IS -- "+exp+" variablesMap -- "+variablesMap);
		if(exp == null) {
			return null;
		}
		Expression expression = new Expression(exp);
		for(String key : variablesMap.keySet()) {
			String value = "0";
			if(variablesMap.get(key) != null) {
				value = variablesMap.get(key).toString();
			}
			expression.with(key, value);
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
