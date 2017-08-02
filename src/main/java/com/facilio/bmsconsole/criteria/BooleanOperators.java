package com.facilio.bmsconsole.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;

public enum BooleanOperators implements Operator<String> {
	
	IS("is");
	
	@Override
	public String getWhereClause(FacilioField field, String value) {
		// TODO Auto-generated method stub
		if(field != null && value != null) {
			if("true".equalsIgnoreCase(value)) {
				return field.getColumnName()+" = true";
			}
			else {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(field.getColumnName())
						.append(" IS NULL OR ")
						.append(field.getColumnName())
						.append(" = false)");
				return builder.toString();
			}
		}
		return null;
	}
	
	private BooleanOperators(String operator) {
		 this.operator = operator;
	}
	
	
	private String operator;
	@Override
	public String getOperator() {
		return operator;
	}
	
	@Override
	public String getDynamicParameter() {
		return null;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		return null;
	}
	
	private static Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
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
