package com.facilio.util;

import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class FacilioExpressionWrapper {
	
	private Expression expression;
	List<String> usedVariables;
	public FacilioExpressionWrapper(String expression) {
		// TODO Auto-generated constructor stub
		this.expression = new Expression(expression)
								.setVariableCharacters(".");
		this.usedVariables = this.expression.getUsedVariables();
	}
	
	public List<String> getUsedVariables() {
		return this.usedVariables;
	}
	
	public FacilioExpressionWrapper setVariable(String variable, BigDecimal value) {
		if (this.usedVariables.contains(variable)) {
			expression.setVariable(variable, value);
		}
		return this;
	}
	
	public FacilioExpressionWrapper setVariable(String variable, String value) {
		if (this.usedVariables.contains(variable)) {
			expression.setVariable(variable, value);
		}
		return this;
	}
	
	public FacilioExpressionWrapper setVariablesInBigDecimal(Map<String, BigDecimal> keyValues) {
		for(Map.Entry<String, BigDecimal> entry : keyValues.entrySet()) {
			this.setVariable(entry.getKey(), entry.getValue());
		}
		return this;
	}
	
	public FacilioExpressionWrapper setVariablesInString(Map<String, String> keyValues) {
		for(Map.Entry<String, String> entry : keyValues.entrySet()) {
			this.setVariable(entry.getKey(), entry.getValue());
		}
		return this;
	}
	
	public FacilioExpressionWrapper setVariablesInObject(Map<String, Object> keyValues) {
		for(Map.Entry<String, Object> entry : keyValues.entrySet()) {
			if (entry.getValue() != null) {
				this.setVariable(entry.getKey(), entry.getValue().toString());
			}
		}
		return this;
	}
	
	public BigDecimal evaluate() {
		return expression.eval();
	}
	
}
