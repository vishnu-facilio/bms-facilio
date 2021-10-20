package com.facilio.db.criteria.operators;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.LookupField;

public enum RelatedModuleOperator implements Operator<Criteria> {

	RELATED(88, "related") {
		@Override
		public String getWhereClause(String fieldName, Criteria value) {
			return getCondition(fieldName, value, true);
		}
	},
	NOT_RELATED(92, "not related") {
		@Override
		public String getWhereClause(String fieldName, Criteria value) {
			return getCondition(fieldName, value, false);
		}
	};

	private static Logger log = LogManager.getLogger(RelatedModuleOperator.class.getName());

	private RelatedModuleOperator(int operatorId, String operator) {
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
	
	private static String getCondition(String fieldName, Criteria value, boolean isRelated) {
		try {
			if(fieldName != null && !fieldName.isEmpty() && value != null) {
				String[] module = fieldName.split("\\.");
				if(module.length > 1) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					LookupField field = (LookupField) modBean.getField(module[1], module[0]);
					
					if(module != null) {
						StringBuilder builder = new StringBuilder();
						builder.append(field.getLookupModule().getTableName()).append(".ID ");
						if (!isRelated) {
							builder.append(" NOT ");
						}
						builder.append("IN (SELECT ")
								.append(field.getColumnName()).append(" FROM ")
								.append(field.getTableName())
								.append(" WHERE ")
								.append(field.getTableName()).append(".ORGID = ").append(AccountUtil.getCurrentOrg().getOrgId()).append(" AND ")
								.append(field.getTableName()).append(".MODULEID = ").append(field.getModuleId()).append(" AND ")
								.append(field.getCompleteColumnName()).append(" IS NOT NULL AND ")
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
		/*if(fieldName != null && !fieldName.isEmpty() && value != null) {
			String[] module = fieldName.split("\\.");
			if(module.length > 1) {
				return new FacilioModulePredicate(module[1], value.computePredicate());
			}
		}*/
		return null;
	}

	@Override
	public boolean updateFieldNameWithModule() {
		return true;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.CRITERIA;
	}

	@Override
	public void validateValue(Condition condition, Criteria value) {
		if (value == null && condition.getCriteriaValueId() == -1) {
			throw new IllegalArgumentException("Criteria Value cannot be null in Condition with RELATED operator");
		}
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

