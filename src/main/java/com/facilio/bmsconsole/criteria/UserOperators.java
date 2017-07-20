package com.facilio.bmsconsole.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.fw.UserInfo;

public enum UserOperators implements Operator{
	
	IS("is") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && value != null && !value.isEmpty()) {
				if(value.contains(",")) {
					StringBuilder builder = new StringBuilder();
					builder.append(columnName)
							.append(" IN (");
					replaceLoggedUserInMultpleValues(builder, value);
					builder.append(")");
					return builder.toString();
				}
				else {
					if(value.trim().equals(LOGGED_IN_USER)) {
						value = "?";
					}
					return columnName+" = "+value;
				}
			}
			return null;
		}
	},
	ISN_T("isn't") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(columnName)
						.append(" IS NULL OR ")
						.append(columnName);
				if(value.contains(",")) {
					builder.append(" NOT IN (");
					replaceLoggedUserInMultpleValues(builder, value);
					builder.append(")");
				}
				else {
					if(value.trim().equals(LOGGED_IN_USER)) {
						value = "?";
					}
					builder.append(" != ")
							.append(value);
				}
				builder.append(")");
				return builder.toString();
			}
			return null;
		}
	} 
	;
	
	private static void replaceLoggedUserInMultpleValues(StringBuilder builder, String value) {
		if(value.contains(LOGGED_IN_USER)) {
			String[] values = value.split(",");
			for(int i=0; i<values.length; i++) {
				String val = values[i];
				if(val.trim().equals(LOGGED_IN_USER)) {
					val = "?";
				}
				if(i != 0) {
					builder.append(", ");
				}
				builder.append(val.trim());
			}
		}
		else {
			builder.append(value);
		}
	}
	
	
	@Override
	public abstract String getWhereClause(String columnName, String value);
	
	public static String LOGGED_IN_USER = "${LOGGED_USER}";
	
	@Override
	public String getDynamicParameter() {
		return LOGGED_IN_USER;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		if(value.contains(LOGGED_IN_USER)) {
			List<Object> objs = new ArrayList<>();
			objs.add(UserInfo.getCurrentUser().getOrgUserId());
			return objs;
		}
		else {
			return null;
		}
	}
	
	private UserOperators(String operator) {
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
