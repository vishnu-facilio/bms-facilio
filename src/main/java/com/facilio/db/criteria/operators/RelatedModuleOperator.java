package com.facilio.db.criteria.operators;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
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
		if(fieldName != null && !fieldName.isEmpty() && value != null) {
			String[] module = fieldName.split("\\.");
			if(module.length > 1) {
				return new FacilioModulePredicate("id", new RelatedModPredcate(value, fieldName));
			}
		}
		return null;
	}

	public static class RelatedModPredcate implements Predicate {
		private Criteria value;
		private String fieldName;
		public RelatedModPredcate(Criteria value,String fieldName) {
			this.value = value;
			this.fieldName = fieldName;
		}

		@Override
		public boolean evaluate(Object object) {
			// TODO Auto-generated method stub
			if(object != null) {
				try {
					if(fieldName != null && !fieldName.isEmpty() && value != null) {
						String[] module = fieldName.split("\\.");
						if(module.length > 1) {
							ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
							LookupField field = (LookupField) modBean.getField(module[1], module[0]);
							FacilioModule mod = modBean.getModule(module[0]);

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
							SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>();
							selectRecordsBuilder.moduleName(module[0]);
							selectRecordsBuilder.beanClass(ModuleBaseWithCustomFields.class);
							selectRecordsBuilder.select(Collections.singletonList(FieldFactory.getIdField(mod)));
							selectRecordsBuilder.andCriteria(value);
							selectRecordsBuilder.andCondition(CriteriaAPI.getCondition(field,String.valueOf(currentId),NumberOperators.EQUALS));
							return CollectionUtils.isNotEmpty(selectRecordsBuilder.get());
						}
					}
				}
				catch(Exception e) {
					log.info("Exception occurred ", e);
				}
			}
			return false;
		}
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

