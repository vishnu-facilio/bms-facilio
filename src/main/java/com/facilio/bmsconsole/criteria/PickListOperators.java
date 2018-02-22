package com.facilio.bmsconsole.criteria;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public enum PickListOperators implements Operator<String> {
	
	IS(36, "is") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				if(value.contains(",")) {
					StringBuilder builder = new StringBuilder();
					builder.append(columnName)
							.append(" IN (");
					replaceLoggedUserInMultpleValues(builder, value);
					builder.append(")");
					return builder.toString();
				}
				else {
					if(value.trim().equals(FacilioConstants.Criteria.LOGGED_IN_USER)) {
						value = "?";
					}
					return columnName+" = "+value;
				}
			}
			return null;
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, computeUserPredicate(value));
			}
			return null;
		}
	},
	ISN_T(37, "isn't") {
		@Override
		public String getWhereClause(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(fieldName)
						.append(" IS NULL OR ")
						.append(fieldName);
				if(value.contains(",")) {
					builder.append(" NOT IN (");
					replaceLoggedUserInMultpleValues(builder, value);
					builder.append(")");
				}
				else {
					if(value.trim().equals(FacilioConstants.Criteria.LOGGED_IN_USER)) {
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
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, PredicateUtils.notPredicate(computeUserPredicate(value)));
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
		if(value.equals(FacilioConstants.Criteria.LOGGED_IN_USER)) {
			return new PickListPredicate(FacilioConstants.Criteria.LOGGED_IN_USER_ID);
		}
		else {
			return new PickListPredicate(Long.parseLong(value));
		}
	}
	
	private static void replaceLoggedUserInMultpleValues(StringBuilder builder, String value) {
		if(value.contains(FacilioConstants.Criteria.LOGGED_IN_USER)) {
			String[] values = value.trim().split("\\s*,\\s*");
			for(int i=0; i<values.length; i++) {
				String val = values[i];
				if(val.equals(FacilioConstants.Criteria.LOGGED_IN_USER)) {
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
	public abstract String getWhereClause(String fieldName, String value);
	
	@Override
	public abstract FacilioModulePredicate getPredicate(String fieldName, String value);
	
	@Override
	public boolean isDynamicOperator() {
		return true;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		if(value.contains(FacilioConstants.Criteria.LOGGED_IN_USER)) {
			List<Object> objs = new ArrayList<>();
			objs.add(AccountUtil.getCurrentUser().getId());
			return objs;
		}
		else {
			return null;
		}
	}
	
	private PickListOperators(int operatorId, String operator) {
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
		return operatorMap;
	}
	public static Map<String, Operator> getAllOperators() {
		return operatorMap;
	}
	
	public static class PickListPredicate implements Predicate {
		private long id = -1;
		
		public PickListPredicate(long id) {
			// TODO Auto-generated constructor stub
			this.id = id;
		}

		@Override
		public boolean evaluate(Object object) {
			// TODO Auto-generated method stub
			if(object != null) {
				try {
					long currentId;
					if(object instanceof Long) {
						currentId = (long) object;
					}
					else if(PropertyUtils.isReadable(object, "id")) {
						currentId = (long) PropertyUtils.getProperty(object, "id");
					}
					else {
						return false;
					}
					if(id == FacilioConstants.Criteria.LOGGED_IN_USER_ID) {
						return currentId == AccountUtil.getCurrentUser().getId();
					}
					else {
						return currentId == id;
					}
				}
				catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
			return false;
		}
	}
}
