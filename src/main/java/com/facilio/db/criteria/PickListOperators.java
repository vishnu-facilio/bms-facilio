package com.facilio.db.criteria;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public enum PickListOperators implements Operator<String> {
	
	IS(36, "is") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				if(value.contains(",") || value.trim().equals(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP)) {
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
				if(value.contains(",") || value.trim().equals(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP)) {
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
	
	private static final long LOGGED_IN_USER_ID = -99;
	private static final long LOGGED_IN_USER_GROUP_ID = -100;
	
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
			return new PickListPredicate(LOGGED_IN_USER_ID);
		}
		else if(value.equals(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP)) {
			return new PickListPredicate(LOGGED_IN_USER_GROUP_ID);
		}
		else {
			return new PickListPredicate(Long.parseLong(value));
		}
	}
	
	private static void replaceLoggedUserInMultpleValues(StringBuilder builder, String value) {
		if(value.contains(FacilioConstants.Criteria.LOGGED_IN_USER) || value.contains(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP)) {
			String[] values = value.trim().split("\\s*,\\s*");
			for(int i=0; i<values.length; i++) {
				String val = values[i];
				if(val.equals(FacilioConstants.Criteria.LOGGED_IN_USER)) {
					val = "?";
				}
				else if (val.equals(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP)) {
					StringBuilder groupBuilder = new StringBuilder();
					try {
						int count = 1;
						List<Group> groups = AccountUtil.getGroupBean().getMyGroups(AccountUtil.getCurrentUser().getId());
						if (groups != null && !groups.isEmpty()) {
							count = groups.size();
						}
						for(int j=0; j< count; j++) {
							if(j != 0) {
								builder.append(", ");
							}
							builder.append("?");
						}
					} catch (Exception e) {
						log.info("Exception occurred ", e);
					}
					val = groupBuilder.toString();
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

	private static Logger log = LogManager.getLogger(PickListOperators.class.getName());

	@Override
	public abstract String getWhereClause(String fieldName, String value);
	
	@Override
	public abstract FacilioModulePredicate getPredicate(String fieldName, String value);
	
	@Override
	public boolean isDynamicOperator() {
		return true;
	}
	
	@Override
	public boolean isValueNeeded() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		if(value.contains(FacilioConstants.Criteria.LOGGED_IN_USER)) {
			List<Object> objs = new ArrayList<>();
			objs.add(AccountUtil.getCurrentUser().getId());
			return objs;
		}
		else if (value.contains(FacilioConstants.Criteria.LOGGED_IN_USER_GROUP)) {
			return (List<Object>) getLoggedInUserGroupIds();
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
					if(id == LOGGED_IN_USER_ID) {
						return currentId == AccountUtil.getCurrentUser().getId();
					}
					else if(id == LOGGED_IN_USER_GROUP_ID) {
						return getLoggedInUserGroupIds().contains(currentId);
					}
					else {
						return currentId == id;
					}
				}
				catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					log.info("Exception occurred ", e);
				}
			}
			return false;
		}
	}
	
	private static List<? extends Object> getLoggedInUserGroupIds () {
		List<Long> ids = null;
		try {
			List<Group> myGroups = AccountUtil.getGroupBean().getMyGroups(AccountUtil.getCurrentUser().getId());
			if (myGroups != null && !myGroups.isEmpty()) {
				ids = myGroups.stream().map(Group::getId).collect(Collectors.toList());
			}
			else {
				ids = Collections.singletonList(-100L);
			}
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return ids;
	}
}
