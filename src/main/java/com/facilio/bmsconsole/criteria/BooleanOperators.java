package com.facilio.bmsconsole.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum BooleanOperators implements Operator {
	
	IS("is");
	
	@Override
	public String getWhereClause(String columnName, String value) {
		// TODO Auto-generated method stub
		if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
			if(value == "true") {
				return columnName+" = true";
			}
			else {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(columnName)
						.append(" IS NULL OR ")
						.append(columnName)
						.append(" = false)");
				return builder.toString();
			}
		}
		return null;
	}
	
	private BooleanOperators(String operator) {
		 this.operator = operator;
	}
	
	
	private String operator;
	@Override
	public String getOperator() {
		return operator;
	}
	
	@Override
	public String getDynamicParameter() {
		return null;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		return null;
	}
	
	private static Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
	private static Map<String, Operator> initOperatorMap() {
		Map<String, Operator> operatorMap = new HashMap<>();
		for(Operator operator : values()) {
			operatorMap.put(operator.getOperator(), operator);
		}
		return operatorMap;
	}
	public static Map<String, Operator> getAllOperators() {
		return operatorMap;
	}
}
