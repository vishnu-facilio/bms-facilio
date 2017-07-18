package com.facilio.bmsconsole.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum StringOperators implements Operator {
	IS("is") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				if(value.contains(",")) {
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
	},
	ISN_T("isn't") {
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
	},
	CONTAINS("contains") {
		@Override
		public String getWhereClause(String field, String value) {
			// TODO Auto-generated method stub
			return contains(field, value, false);
		}

		@Override
		public List<Object> computeValues(String value) {
			// TODO Auto-generated method stub
			return commonComputeValue(value, true, true);
		}
	},
	DOESNT_CONTAIN("doesn't contain") {
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
	},
	STARTS_WITH("starts with") {
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
	},
	ENDS_WITH("ends with") {
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
	}
	;
	
	@Override
	public abstract String getWhereClause(String columnName, String value);

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
}
