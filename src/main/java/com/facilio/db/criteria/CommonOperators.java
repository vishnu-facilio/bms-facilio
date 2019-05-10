package com.facilio.db.criteria;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import com.facilio.util.FacilioUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CommonOperators implements Operator<String> {
	
	//Make sure to check the operator number is unqiue across operators
	
	IS_EMPTY(1, "is empty") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				return columnName+" IS NULL";
			}
			return null;
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, getNullPredicate());
			}
			return null;
		}
	},	
	IS_NOT_EMPTY(2, "is not empty") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				return columnName+" IS NOT NULL";
			}
			return null;
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, PredicateUtils.notPredicate(getNullPredicate()));
			}
			return null;
		}
	};
	
	private static Predicate getNullPredicate() {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				try {
					return FacilioUtil.isEmptyOrNull(object);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		};
	}
	
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
		return false;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		return null;
	}
	
	private CommonOperators(int operatorId, String operator) {
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
}
