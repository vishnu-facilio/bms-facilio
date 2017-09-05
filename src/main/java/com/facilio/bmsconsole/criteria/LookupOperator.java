package com.facilio.bmsconsole.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanPredicate;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.LookupField;

public enum LookupOperator implements Operator<Criteria> {

	LOOKUP("lookup");

	private LookupOperator(String operator) {
		// TODO Auto-generated constructor stub
		this.operator = operator;
	}
	
	private String operator;
	@Override
	public String getOperator() {
		// TODO Auto-generated method stub
		return operator;
	}

	@Override
	public String getWhereClause(FacilioField field, Criteria value) {
		// TODO Auto-generated method stub
		if(field != null && value != null) {
			LookupField lookupField = (LookupField) field;
			
			FacilioModule module = lookupField.getLookupModule();
			if(module != null) {
				StringBuilder builder = new StringBuilder();
				builder.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append(" IN (SELECT ID FROM ")
						.append(module.getTableName())
						.append(" WHERE ")
						.append(value.computeWhereClause())
						.append(")");
				
				return builder.toString();
			}
		}
		return null;
	}

	@Override
	public String getDynamicParameter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> computeValues(Criteria value) {
		// TODO Auto-generated method stub
		if(value != null) {
			return value.getComputedValues();
		}
		return null;
	}

	@Override
	public BeanPredicate getPredicate(FacilioField field, Criteria value) {
		// TODO Auto-generated method stub
		if(field != null && value != null) {
			return new BeanPredicate(field.getName(), value.computePredicate());
		}
		return null;
	}

	private static final Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
	private static Map<String, Operator> initOperatorMap() {
		Map<String, Operator> operatorMap = new HashMap<>();
		operatorMap.putAll(PickListOperators.getAllOperators());
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
