package com.facilio.bmsconsole.criteria;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public enum StringOperators implements Operator<String> {
	IS(3, "is") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				if(value.matches(CONTAINS_COMMA_REGEX)) {
					StringBuilder builder = new StringBuilder();
					builder.append(columnName)
							.append(" IN (");
					splitAndAddQuestionMark(value, builder);
					builder.append(")");
					return builder.toString();
				}
				else {
					return columnName+" = ?";
				}
			}
			return null;
		}

		@Override
		public List<Object> computeValues(String value) {
			// TODO Auto-generated method stub
			return commonComputeValue(value, false, false);
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
	ISN_T(4, "isn't") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(columnName)
						.append(" IS NULL OR ")
						.append(columnName);
				if(value.matches(CONTAINS_COMMA_REGEX)) {
					builder.append(" NOT IN (");
					splitAndAddQuestionMark(value, builder);
					builder.append(")");
				}
				else {
					builder.append(" != ")
							.append("?");
				}
				builder.append(")");
				return builder.toString();
			}
			return null;
		}

		@Override
		public List<Object> computeValues(String value) {
			// TODO Auto-generated method stub
			return commonComputeValue(value, false, false);
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
	CONTAINS(5, "contains") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			return contains(columnName, value, false);
		}

		@Override
		public List<Object> computeValues(String value) {
			// TODO Auto-generated method stub
			return commonComputeValue(value, true, true);
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, computeContainsPredicate(value));
			}
			return null;
		}
	},
	DOESNT_CONTAIN(6, "doesn't contain") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(columnName)
						.append(" IS NULL OR ")
						.append(contains(columnName, value, true))
						.append(")");
				return builder.toString();
			}
			return null;
		}

		@Override
		public List<Object> computeValues(String value) {
			// TODO Auto-generated method stub
			return commonComputeValue(value, true, true);
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, PredicateUtils.notPredicate(computeContainsPredicate(value)));
			}
			return null;
		}
	},
	STARTS_WITH(7, "starts with") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			return contains(columnName, value, false);
		}
		
		@Override
		public List<Object> computeValues(String value) {
			// TODO Auto-generated method stub
			return commonComputeValue(value, false, true);
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				Predicate startsWithPredicate = null;
				if(value.matches(CONTAINS_COMMA_REGEX)) {
					List<Predicate> startsWithPredicates = new ArrayList<>();
					String[] values = value.trim().split(SPLIT_COMMA_REGEX);
					for(String val : values) {
						startsWithPredicates.add(getStartsWithPredicate(val));
					}
					startsWithPredicate = PredicateUtils.anyPredicate(startsWithPredicates);
				}
				else {
					startsWithPredicate = getStartsWithPredicate(value);
				}
				return new FacilioModulePredicate(fieldName, startsWithPredicate);
			}
			return null;
		}
		
		private Predicate getStartsWithPredicate(String value) {
			return new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					if(object != null && object instanceof String) {
						return ((String) object).startsWith(value);
					}
					return false;
				}
			};
		}
	},
	ENDS_WITH(8, "ends with") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			return contains(columnName, value, false);
		}

		@Override
		public List<Object> computeValues(String value) {
			// TODO Auto-generated method stub
			return commonComputeValue(value, true, false);
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				Predicate endsWithPredicate = null;
				if(value.matches(CONTAINS_COMMA_REGEX)) {
					List<Predicate> endsWithPredicates = new ArrayList<>();
					String[] values = value.trim().split(SPLIT_COMMA_REGEX);
					for(String val : values) {
						endsWithPredicates.add(getEndsWithPredicate(val));
					}
					endsWithPredicate = PredicateUtils.anyPredicate(endsWithPredicates);
				}
				else {
					endsWithPredicate = getEndsWithPredicate(value);
				}
				return new FacilioModulePredicate(fieldName, endsWithPredicate);
			}
			return null;
		}
		
		private Predicate getEndsWithPredicate(String value) {
			return new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					if(object != null && object instanceof String) {
						return ((String) object).endsWith(value);
					}
					return false;
				}
			};
		}
	}
	;

	public static final String DELIMITED_COMMA = "\\,";
	private static final String CONTAINS_COMMA_REGEX = ".*(?<!\\\\),.*";
	private static final String SPLIT_COMMA_REGEX = "\\s*(?<!\\\\),\\s*";
	
	@Override
	public abstract String getWhereClause(String columnName, String value);
	
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
	public abstract List<Object> computeValues(String value);
	
	private StringOperators(int operatorId, String operator) {
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
	
	private static List<Object> commonComputeValue(String value, boolean prefix, boolean suffix) {
		if(value != null && !value.isEmpty()) {
			List<Object> valuesList = new ArrayList<>();
			if(value.matches(CONTAINS_COMMA_REGEX)) {
				 String[] values = value.trim().split(SPLIT_COMMA_REGEX);
				 for(String val : values) {
					 valuesList.add(appendPercent(val, prefix, suffix));
				 }
			}
			else {
				valuesList.add(appendPercent(value, prefix, suffix));
			}
			return  valuesList;
		}
		return null;
	}
	
	private static String appendPercent(String val, boolean prefix, boolean suffix) {
		StringBuilder builder = new StringBuilder();
		 if(prefix) {
			 builder.append("%");
		 }
		 builder.append(val.replace(DELIMITED_COMMA,","));
		 if(suffix) {
			 builder.append("%");
		 }
		 return builder.toString();
	}
	
	private static void splitAndAddQuestionMark(String value, StringBuilder builder) {
		String[] values = value.trim().split(SPLIT_COMMA_REGEX);
		for(int i=0; i<values.length; i++) {
			if(i != 0) {
				builder.append(", ");
			}
			builder.append("?");
		}
	}
	
	private static String contains(String columnName, String value, boolean isNot) {
		if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			if(value.matches(CONTAINS_COMMA_REGEX)) {
				builder.append("(");
				String[] values = value.trim().split(SPLIT_COMMA_REGEX);
				for(int i=0; i<values.length; i++) {
					if(i != 0) {
						builder.append(" OR ");
					}
					builder.append(columnName);
					if(isNot) {
						builder.append(" NOT ");
					}
					builder.append(" LIKE ?");
				}
				builder.append(")");
			}
			else {
				if(isNot) {
					return columnName+" NOT LIKE ?";
				}
				else {
					return columnName+" LIKE ?";
				}
			}
			return builder.toString();
		}
		return null;
	}
	
	private static Predicate computeEqualPredicate(String value) {
		if(value.matches(CONTAINS_COMMA_REGEX)) {
			List<Predicate> equalPredicates = new ArrayList<>();
			String[] values = value.trim().split(SPLIT_COMMA_REGEX);
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
				if(object != null && object instanceof String) {
					return ((String) object).equalsIgnoreCase(value);
				}
				return false;
			}
		};
	}
	
	private static Predicate computeContainsPredicate(String value) {
		if(value.matches(CONTAINS_COMMA_REGEX)) {
			List<Predicate> containsPredicates = new ArrayList<>();
			String[] values = value.trim().split(SPLIT_COMMA_REGEX);
			for(String val : values) {
				containsPredicates.add(getContainsPredicate(val));
			}
			return PredicateUtils.anyPredicate(containsPredicates);
		}
		else {
			return getContainsPredicate(value);
		}
	}
	
	private static Predicate getContainsPredicate(String value) {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				if(object != null && object instanceof String) {
					return ((String) object).contains(value);
				}
				return false;
			}
		};
	}
}
