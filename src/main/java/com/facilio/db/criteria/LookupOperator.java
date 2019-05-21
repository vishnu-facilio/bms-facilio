package com.facilio.db.criteria;

import com.facilio.beans.ModuleBean;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.LookupField;
import com.facilio.fw.BeanFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum LookupOperator implements Operator<Criteria> {

	LOOKUP(35, "lookup");

	private static Logger log = LogManager.getLogger(LookupOperator.class.getName());

	private LookupOperator(int operatorId, String operator) {
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
		// TODO Auto-generated method stub
		return operator;
	}

	@Override
	public String getWhereClause(String fieldName, Criteria value) {
		// TODO Auto-generated method stub
		try {
			if(fieldName != null && !fieldName.isEmpty() && value != null) {
				String[] module = fieldName.split("\\.");
				if(module.length > 1) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					LookupField lookupField = (LookupField) modBean.getField(module[1], module[0]);
					
					FacilioModule lookupModule = lookupField.getLookupModule();
					if(lookupModule == null && lookupField.getSpecialType() != null) {
						lookupModule = modBean.getModule(lookupField.getSpecialType());
					}
					if(module != null) {
						StringBuilder builder = new StringBuilder();
						builder.append(lookupField.getTableName())
								.append(".")
								.append(lookupField.getColumnName())
								.append(" IN (SELECT ID FROM ")
								.append(lookupModule.getTableName())
								.append(" WHERE ")
								.append(value.computeWhereClause())
								.append(")");
						
						return builder.toString();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		}
		return null;
	}

	@Override
	public boolean isDynamicOperator() {
		return false;
	}
	
	@Override
	public boolean isValueNeeded() {
		// TODO Auto-generated method stub
		return true;
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
	public FacilioModulePredicate getPredicate(String fieldName, Criteria value) {
		// TODO Auto-generated method stub
		if(fieldName != null && !fieldName.isEmpty() && value != null) {
			String[] module = fieldName.split("\\.");
			if(module.length > 1) {
				return new FacilioModulePredicate(module[1], value.computePredicate());
			}
		}
		return null;
	}

	private static final Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
	private static Map<String, Operator> initOperatorMap() {
		Map<String, Operator> operatorMap = new HashMap<>();
		operatorMap.putAll(PickListOperators.getAllOperators());
		operatorMap.putAll(BuildingOperator.getAllOperators());
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
