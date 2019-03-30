package com.facilio.bmsconsole.criteria;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import java.util.*;

public enum NumberOperators implements Operator<String> {
	
	EQUALS(9, "=") {
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

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, computeEqualPredicate(value));
			}
			return null;
		}
	},
	NOT_EQUALS(10, "!=") {
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

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, PredicateUtils.notPredicate(computeEqualPredicate(value)));
			}
			return null;
		}
	},
	LESS_THAN(11, "<") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null) {
							double doubleVal = Double.parseDouble(value);
							double currentVal = Double.parseDouble(object.toString());
							return currentVal < doubleVal;
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	LESS_THAN_EQUAL(12, "<=") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null) {
							double doubleVal = Double.parseDouble(value);
							double currentVal = Double.parseDouble(object.toString());
							return currentVal <= doubleVal;
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	GREATER_THAN(13, ">") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null) {
							double doubleVal = Double.parseDouble(value);
							double currentVal = Double.parseDouble(object.toString());
							return currentVal > doubleVal;
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	GREATER_THAN_EQUAL(14, ">=") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null) {
							double doubleVal = Double.parseDouble(value);
							double currentVal = Double.parseDouble(object.toString());
							return currentVal >= doubleVal;
						}
						return false;
					}
				});
			}
			return null;
		}
	}
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
	public abstract FacilioModulePredicate getPredicate(String fieldName, String value);
	
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
	
	private NumberOperators(int operatorId, String operator) {
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
	
	private static Predicate computeEqualPredicate(String value) {
		if(value.contains(",")) {
			List<Predicate> equalPredicates = new ArrayList<>();
			String[] values = value.trim().split("\\s*,\\s*");
			for(String val : values) {
				equalPredicates.add(getEqualPredicate(val));
			}
			return PredicateUtils.anyPredicate(equalPredicates);
		}
		else {
			return getEqualPredicate(value);
		}
	}
	
	private static Predicate getEqualPredicate(String value) {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				// TODO Auto-generated method stub
				if(object != null) {
					double doubleVal = Double.parseDouble(value);
					double currentVal = Double.parseDouble(object.toString());
					return currentVal == doubleVal;
				}
				return false;
			}
		};
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
