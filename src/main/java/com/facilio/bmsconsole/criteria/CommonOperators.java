package com.facilio.bmsconsole.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.modules.FacilioField;

public enum CommonOperators implements Operator<String> {
	
	IS_EMPTY("is empty") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return field.getColumnName()+" IS NULL";
			}
			return null;
		}
	},	
	IS_NOT_EMPTY("is not empty") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return field.getColumnName()+" IS NOT NULL";
			}
			return null;
		}
	};
	
	@Override
	public abstract String getWhereClause(FacilioField field, String value);
	
	@Override
	public String getDynamicParameter() {
		return null;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		return null;
	}
	
	private CommonOperators(String operator) {
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
		return operatorMap;
	}
	public static Map<String, Operator> getAllOperators() {
		return operatorMap;
	}
}
