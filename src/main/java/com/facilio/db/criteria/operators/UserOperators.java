package com.facilio.db.criteria.operators;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public enum UserOperators implements Operator<String> {
	
	ROLE_IS(87, "role is") {
		@Override
		public String getWhereClause(String columnName, String value) {
			try {
				if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
					StringBuilder builder = new StringBuilder();
					builder.append(columnName)
							.append(" IN (")
							.append(fetchOrgUserBuilder(value, -1).constructSelectStatement())
							.append(")");
					
					return builder.toString();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			}
			return null;
		}


		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new UserPredicate(value));
			}
			return null;
		}
	}
	;
	
	
	private UserOperators(int operatorId, String operator) {
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
	public abstract String getWhereClause(String columnName, String value);
	
	@Override
	public abstract FacilioModulePredicate getPredicate(String fieldName, String value);

	@Override
	public boolean isDynamicOperator() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isValueNeeded() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<Object> computeValues(String value) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static Logger log = LogManager.getLogger(UserOperators.class.getName());
	
	private static GenericSelectRecordBuilder fetchOrgUserBuilder (String roles, long currentUserId) throws Exception {
		List<FacilioField> fields = AccountConstants.getOrgUserAppsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioModule userModule = AccountConstants.getOrgUserAppsModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(Collections.singletonList(fieldMap.get("ouid")))
				.table(userModule.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("roleId"), roles, NumberOperators.EQUALS))
				;
		if (currentUserId != -1) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ouid"), String.valueOf(currentUserId), NumberOperators.EQUALS));
		}
		
		return selectBuilder;
	}
	
	public static class UserPredicate implements Predicate {
		private String roles;
		
		public UserPredicate(String roles) {
			this.roles = roles;
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
					GenericSelectRecordBuilder builder = fetchOrgUserBuilder(roles, currentId);
					List<Map<String, Object>> props = builder.get();
					return CollectionUtils.isNotEmpty(props);
				}
				catch(Exception e) {
					log.info("Exception occurred ", e);
				}
			}
			return false;
		}
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

}
