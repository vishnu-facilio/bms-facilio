package com.facilio.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.udojava.evalex.Expression;

public class FacilioExpressionWrapper {
	
	private Expression expression;
	public FacilioExpressionWrapper(String expression) {
		// TODO Auto-generated constructor stub
		this.expression = new Expression(expression)
								.setVariableCharacters(".");
	}
	
	public List<String> getUsedVariables() {
		return expression.getUsedVariables();
	}
	
	public FacilioExpressionWrapper setVariable(String variable, BigDecimal value) {
		expression.setVariable(variable, value);
		return this;
	}
	
	public FacilioExpressionWrapper setVariable(String variable, String value) {
		expression.setVariable(variable, value);
		return this;
	}
	
	public FacilioExpressionWrapper setVariablesInBigDecimal(Map<String, BigDecimal> keyValues) {
		for(Map.Entry<String, BigDecimal> entry : keyValues.entrySet()) {
			expression.setVariable(entry.getKey(), entry.getValue());
		}
		return this;
	}
	
	public FacilioExpressionWrapper setVariablesInString(Map<String, String> keyValues) {
		for(Map.Entry<String, String> entry : keyValues.entrySet()) {
			expression.setVariable(entry.getKey(), entry.getValue());
		}
		return this;
	}
	
	public FacilioExpressionWrapper setVariablesInObject(Map<String, Object> keyValues) {
		for(Map.Entry<String, Object> entry : keyValues.entrySet()) {
			expression.setVariable(entry.getKey(), entry.getValue().toString());
		}
		return this;
	}
	
	public BigDecimal evaluate() {
		return expression.eval();
	}
	
}
