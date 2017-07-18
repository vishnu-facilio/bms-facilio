package com.facilio.bmsconsole.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum NumberOperators implements Operator {
	
	EQUALS("=") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				if(value.contains(",")) {
					return columnName+" IN ("+value+")";
				}
				else {
					return columnName+" = "+value;
				}
			}
			return null;
		}
	},
	NOT_EQUALS("!=") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(columnName)
						.append(" IS NULL OR ")
						.append(columnName);
				if(value.contains(",")) {
					builder.append(" NOT IN (")
							.append(value)
							.append(")");
				}
				else {
					builder.append(" != ")
							.append(value);
				}
				builder.append(")");
				return builder.toString();
			}
			return null;
		}
	},
	LESS_THAN("<"),
	LESS_THAN_EQUAL("<="),
	GREATER_THAN(">"),
	GREATER_THAN_EQUAL(">=")
	;
	
	@Override
	public String getWhereClause(String columnName, String value) {
		// TODO Auto-generated method stub
		if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
			return columnName+operator+value;
		}
		return null;
	};
	
	@Override
	public String getDynamicParameter() {
		return null;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		return null;
	}
	
	private NumberOperators(String operator) {
		 this.operator = operator;
	}
	
	private String operator;
	@Override
	public String getOperator() {
		return operator;
	}
	
	private static Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
	private static Map<String, Operator> initOperatorMap() {
		Map<String, Operator> operatorMap = new HashMap<>();
		for(Operator operator : values()) {
			operatorMap.put(operator.getOperator(), operator);
		}
		operatorMap.putAll(CommonOperators.getAllOperators());
		return operatorMap;
	}
	public static Map<String, Operator> getAllOperators() {
		return operatorMap;
	}
}
