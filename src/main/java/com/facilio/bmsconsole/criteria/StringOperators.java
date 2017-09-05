package com.facilio.bmsconsole.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import com.facilio.bmsconsole.modules.FacilioField;

public enum StringOperators implements Operator<String> {
	IS("is") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				if(value.contains(",")) {
					StringBuilder builder = new StringBuilder();
					builder.append(field.getModuleTableName())
							.append(".")
							.append(field.getColumnName())
							.append(" IN (");
					splitAndAddQuestionMark(value, builder);
					builder.append(")");
					return builder.toString();
				}
				else {
					return field.getModuleTableName()+"."+field.getColumnName()+" = ?";
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
		public BeanPredicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), computeEqualPredicate(value));
			}
			return null;
		}
	},
	ISN_T("isn't") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append(" IS NULL OR ")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName());
				if(value.contains(",")) {
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
		public BeanPredicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), PredicateUtils.notPredicate(computeEqualPredicate(value)));
			}
			return null;
		}
	},
	CONTAINS("contains") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return contains(field.getModuleTableName()+"."+field.getColumnName(), value, false);
		}

		@Override
		public List<Object> computeValues(String value) {
			// TODO Auto-generated method stub
			return commonComputeValue(value, true, true);
		}

		@Override
		public BeanPredicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), computeContainsPredicate(value));
			}
			return null;
		}
	},
	DOESNT_CONTAIN("doesn't contain") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append(" IS NULL OR ")
						.append(contains(field.getModuleTableName()+"."+field.getColumnName(), value, true))
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
		public BeanPredicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), PredicateUtils.notPredicate(computeContainsPredicate(value)));
			}
			return null;
		}
	},
	STARTS_WITH("starts with") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return contains(field.getColumnName(), value, false);
		}
		
		@Override
		public List<Object> computeValues(String value) {
			// TODO Auto-generated method stub
			return commonComputeValue(value, false, true);
		}

		@Override
		public BeanPredicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				Predicate startsWithPredicate = null;
				if(value.contains(",")) {
					List<Predicate> startsWithPredicates = new ArrayList<>();
					String[] values = value.trim().split("\\s*,\\s*");
					for(String val : values) {
						startsWithPredicates.add(getStartsWithPredicate(val));
					}
					startsWithPredicate = PredicateUtils.anyPredicate(startsWithPredicates);
				}
				else {
					startsWithPredicate = getStartsWithPredicate(value);
				}
				return new BeanPredicate(field.getName(), startsWithPredicate);
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
	ENDS_WITH("ends with") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return contains(field.getColumnName(), value, false);
		}

		@Override
		public List<Object> computeValues(String value) {
			// TODO Auto-generated method stub
			return commonComputeValue(value, true, false);
		}

		@Override
		public BeanPredicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				Predicate endsWithPredicate = null;
				if(value.contains(",")) {
					List<Predicate> endsWithPredicates = new ArrayList<>();
					String[] values = value.trim().split("\\s*,\\s*");
					for(String val : values) {
						endsWithPredicates.add(getEndsWithPredicate(val));
					}
					endsWithPredicate = PredicateUtils.anyPredicate(endsWithPredicates);
				}
				else {
					endsWithPredicate = getEndsWithPredicate(value);
				}
				return new BeanPredicate(field.getName(), endsWithPredicate);
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
	
	@Override
	public abstract String getWhereClause(FacilioField field, String value);

	@Override
	public String getDynamicParameter() {
		return null;
	}
 	
	@Override
	public abstract List<Object> computeValues(String value);
	
	private StringOperators(String operator) {
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
	
	private static List<Object> commonComputeValue(String value, boolean prefix, boolean suffix) {
		if(value != null && !value.isEmpty()) {
			List<Object> valuesList = new ArrayList<>();
			if(value.contains(",")) {
				 String[] values = value.trim().split("\\s*,\\s*");
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
		 builder.append(val);
		 if(suffix) {
			 builder.append("%");
		 }
		 return builder.toString();
	}
	
	private static void splitAndAddQuestionMark(String value, StringBuilder builder) {
		String[] values = value.trim().split("\\s*,\\s*");
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
			if(value.contains(",")) {
				builder.append("(");
				String[] values = value.trim().split("\\s*,\\s*");
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
				if(object != null && object instanceof String) {
					return ((String) object).equalsIgnoreCase(value);
				}
				return false;
			}
		};
	}
	
	private static Predicate computeContainsPredicate(String value) {
		if(value.contains(",")) {
			List<Predicate> containsPredicates = new ArrayList<>();
			String[] values = value.trim().split("\\s*,\\s*");
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
