package com.facilio.bmsconsole.criteria;

import org.apache.commons.collections.Predicate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum BooleanOperators implements Operator<String> {
	
	IS(15, "is");
	
	@Override
	public String getWhereClause(String columnName, String value) {
		// TODO Auto-generated method stub
		if(columnName != null && !columnName.isEmpty() && value != null) {
			if("true".equalsIgnoreCase(value)) {
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
	
	@Override
	public FacilioModulePredicate getPredicate(String fieldName, String value) {
		// TODO Auto-generated method stub
		if(fieldName != null && !fieldName.isEmpty() && value != null) {
			if("true".equalsIgnoreCase(value)) {
				return new FacilioModulePredicate(fieldName, new BooleanPredicate(true));
			}
			else {
				return new FacilioModulePredicate(fieldName, new BooleanPredicate(false));
			}
		}
		return null;
	}
	
	private BooleanOperators(int operatorId, String operator) {
		this.operatorId = operatorId;
		this.operator = operator;
	}
	
	private int operatorId;
	@Override
	public int getOperatorId() {
		return operatorId;
	}
	
	private String operator;
	@Override
	public String getOperator() {
		return operator;
	}
	
	@Override
	public boolean isDynamicOperator() {
		return false;
	}
	
	@Override
	public boolean isValueNeeded() {
		// TODO Auto-generated method stub
		return true;
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
	
	public static class BooleanPredicate implements Predicate {

		private boolean val;
		
		public BooleanPredicate(boolean val) {
			// TODO Auto-generated constructor stub
			this.val = val;
		}
		
		@Override
		public boolean evaluate(Object object) {
			// TODO Auto-generated method stub
			if(object != null && object instanceof Boolean) {
				boolean currentVal = (boolean) object;
				return val == currentVal;
			}
			return false;
		}
		
	}
}
