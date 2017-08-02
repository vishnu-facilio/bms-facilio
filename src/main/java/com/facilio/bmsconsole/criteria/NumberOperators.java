package com.facilio.bmsconsole.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.modules.FacilioField;

public enum NumberOperators implements Operator<String> {
	
	EQUALS("=") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				if(value.contains(",")) {
					return field.getColumnName()+" IN ("+value+")";
				}
				else {
					return field.getColumnName()+" = "+value;
				}
			}
			return null;
		}
	},
	NOT_EQUALS("!=") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(field.getColumnName())
						.append(" IS NULL OR ")
						.append(field.getColumnName());
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
	public String getWhereClause(FacilioField field, String value) {
		// TODO Auto-generated method stub
		if(field != null && value != null && !value.isEmpty()) {
			return field.getColumnName()+operator+value;
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
	
	private static final Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
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
