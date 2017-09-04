package com.facilio.bmsconsole.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.fw.UserInfo;

public enum UserOperators implements Operator<String> {
	
	IS("is") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field.getColumnName() != null && value != null && !value.isEmpty()) {
				if(value.contains(",")) {
					StringBuilder builder = new StringBuilder();
					builder.append(field.getModuleTableName())
							.append(".")
							.append(field.getColumnName())
							.append(" IN (");
					replaceLoggedUserInMultpleValues(builder, value);
					builder.append(")");
					return builder.toString();
				}
				else {
					if(value.trim().equals(LOGGED_IN_USER)) {
						value = "?";
					}
					return field.getModuleTableName()+"."+field.getColumnName()+" = "+value;
				}
			}
			return null;
		}

		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field.getColumnName() != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), computeUserPredicate(value));
			}
			return null;
		}
	},
	ISN_T("isn't") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field.getColumnName() != null && value != null && !value.isEmpty()) {
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

		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field.getColumnName() != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), PredicateUtils.notPredicate(computeUserPredicate(value)));
			}
			return null;
		}
	} 
	;
	
	private static Predicate computeUserPredicate(String value) {
		if(value.contains(",")) {
			List<Predicate> userPredicates = new ArrayList<>();
			String[] values = value.trim().split("\\s*,\\s*");
			for(String val : values) {
				userPredicates.add(getUserPredicate(val));
			}
			return PredicateUtils.anyPredicate(userPredicates);
		}
		else {
			return getUserPredicate(value);
		}
	}
	
	private static Predicate getUserPredicate(String value) {
		if(value.equals(LOGGED_IN_USER)) {
			return new UserPredicate();
		}
		else {
			return new UserPredicate(Long.parseLong(value));
		}
	}
	
	private static void replaceLoggedUserInMultpleValues(StringBuilder builder, String value) {
		if(value.contains(LOGGED_IN_USER)) {
			String[] values = value.trim().split("\\s*,\\s*");
			for(int i=0; i<values.length; i++) {
				String val = values[i];
				if(val.equals(LOGGED_IN_USER)) {
					val = "?";
				}
				if(i != 0) {
					builder.append(", ");
				}
				builder.append(val);
			}
		}
		else {
			builder.append(value);
		}
	}
	
	
	@Override
	public abstract String getWhereClause(FacilioField field, String value);
	
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
	
	public static class UserPredicate implements Predicate {
		private long userId = -1;
		
		public UserPredicate() {
			// TODO Auto-generated constructor stub
		}
		
		public UserPredicate(long userId) {
			// TODO Auto-generated constructor stub
			this.userId = userId;
		}

		@Override
		public boolean evaluate(Object object) {
			// TODO Auto-generated method stub
			if(object != null) {
				long currentUserid;
				if(object instanceof Long) {
					currentUserid = (long) object;
				}
				else if(object instanceof UserContext) {
					currentUserid = ((UserContext) object).getOrgUserId();
				}
				else {
					return false;
				}
				if(userId == -1) {
					return currentUserid == UserInfo.getCurrentUser().getOrgUserId();
				}
				else {
					return currentUserid == userId;
				}
			}
			return false;
		}
	}
}
