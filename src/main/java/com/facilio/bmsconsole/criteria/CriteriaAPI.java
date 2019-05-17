package com.facilio.bmsconsole.criteria;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class CriteriaAPI {
	
	public static long addCriteria(Criteria criteria, long orgId) throws Exception {
		if(criteria != null) {
			criteria.setCriteriaId(-1);
			criteria.setOrgId(orgId);
			Map<String, Object> criteriaProp = FieldUtil.getAsProperties(criteria);
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table("Criteria")
															.fields(FieldFactory.getCriteriaFields())
															.addRecord(criteriaProp);
			
			insertBuilder.save();
			long criteriaId = (long) criteriaProp.get("id");
			addConditions(criteria.getConditions(), criteriaId, orgId);
			
			return criteriaId;
		}
		return -1;
	}
	
	private static void addConditions(Map<String, Condition> conditions, long parentCriteriaId, long orgId) throws Exception {
		if(conditions != null && !conditions.isEmpty()) {
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table("Conditions")
															.fields(FieldFactory.getConditionFields())
															;
			
			for(Map.Entry<String, Condition> conditionEntry : conditions.entrySet()) {
				Condition condition = conditionEntry.getValue();
				
				if (condition.getFieldName() == null || condition.getFieldName().isEmpty()) {
					throw new IllegalArgumentException("Field Name cannot be null in Condition");
				}
				if (condition.getColumnName() == null || condition.getColumnName().isEmpty()) {
					if (condition.getModuleName() != null && !condition.getModuleName().isEmpty()) {
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						FacilioField field = modBean.getField(condition.getFieldName(), condition.getModuleName());
						condition.setColumnName(field.getColumnName());
					}
					else {
						throw new IllegalArgumentException("Both module name and fieldName cannot be null in Condition");
					}
				}
				
				if (condition.getOperator() == null) {
					throw new IllegalArgumentException("Operator cannot be null in Condition");
				}
				if (condition.getOperator() == LookupOperator.LOOKUP && condition.getCriteriaValue() == null && condition.getCriteriaValueId() == -1) {
					throw new IllegalArgumentException("Criteria Value cannot be null in Condition with LOOKUP operator");
				}
				if (condition.getOperator() != LookupOperator.LOOKUP && condition.getOperator().isValueNeeded() && (condition.getValue() == null || condition.getValue().isEmpty())) {
					throw new IllegalArgumentException("Value cannot be null for Condition with operator "+condition.getOperator());
				}
				
				int sequence = -1;
				Object key = conditionEntry.getKey();
				if(key instanceof Integer) {
					sequence = (int) key;
				}
				else if(key instanceof String) {
					sequence = Integer.parseInt((String) key);
				}
				
				condition.setSequence(sequence);
				condition.setConditionId(-1);
				condition.setParentCriteriaId(parentCriteriaId);
				
				if(condition.getCriteriaValue() != null) {
					if (condition.getCriteriaValue().getCriteriaId() == -1) {
						long criteriaValueId = addCriteria(condition.getCriteriaValue(), orgId);
						condition.setCriteriaValueId(criteriaValueId);
					}
					else {
						condition.setCriteriaValueId(condition.getCriteriaValue().getCriteriaId());
					}
				}
				if (condition.getOperator().isDynamicOperator()) {
					condition.setComputedWhereClause(null);
				}
				else {
					condition.computeAndGetWhereClause();
				}
				Map<String, Object> conditionProp = FieldUtil.getAsProperties(condition);
				insertBuilder.addRecord(conditionProp);
			}
			insertBuilder.save();
		}
		else {
			throw new IllegalArgumentException("Condition can not be empty for a Criteria");
		}
	}
	
	public static void deleteCriteria (long id) throws SQLException {
		FacilioModule module = ModuleFactory.getCriteriaModule();
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition("CRITERIAID", "criteriaId", String.valueOf(id), NumberOperators.EQUALS))
														;
		deleteBuilder.delete();
	}
	
	public static Criteria getCriteria(long orgId, long id) throws Exception {
		
		FacilioModule criteriaModule = ModuleFactory.getCriteriaModule();
		FacilioModule conditionModule = ModuleFactory.getConditionsModule();
		
		List<FacilioField> fields = FieldFactory.getCriteriaFields();
		FacilioField criteriaId = FieldFactory.getAsMap(fields).get("criteriaId");
		fields.addAll(FieldFactory.getConditionFields());
		
		GenericSelectRecordBuilder criteriaBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(conditionModule.getTableName())
															.innerJoin(criteriaModule.getTableName())
															.on(conditionModule.getTableName()+".PARENT_CRITERIA_ID = "+criteriaModule.getTableName()+".CRITERIAID")
															.andCondition(getCurrentOrgIdCondition(criteriaModule))
															.andCondition(getCondition(criteriaId, String.valueOf(id), PickListOperators.IS))
															;
		
		List<Map<String, Object>> criteriaProps = criteriaBuilder.get();
		
		Criteria criteria = null;
		Map<String, Condition> conditions = new HashMap<>();
		if(criteriaProps != null && !criteriaProps.isEmpty()) {
			for(Map<String, Object> props : criteriaProps) {
				if(criteria == null) {
					criteria = FieldUtil.getAsBeanFromMap(props, Criteria.class);
					criteria.setConditions(conditions);
				}
				Condition condition = FieldUtil.getAsBeanFromMap(props, Condition.class);
				conditions.put(String.valueOf(condition.getSequence()), condition);
			}
		}
		return criteria;
	}
	
	public static Map<Long, Criteria> getCriteriaAsMap(Collection<Long> ids) throws Exception {
		
		FacilioModule criteriaModule = ModuleFactory.getCriteriaModule();
		FacilioModule conditionModule = ModuleFactory.getConditionsModule();
		
		List<FacilioField> fields = FieldFactory.getCriteriaFields();
		FacilioField criteriaId = FieldFactory.getAsMap(fields).get("criteriaId");
		fields.addAll(FieldFactory.getConditionFields());
		
		GenericSelectRecordBuilder criteriaBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(conditionModule.getTableName())
															.innerJoin(criteriaModule.getTableName())
															.on(conditionModule.getTableName()+".PARENT_CRITERIA_ID = "+criteriaModule.getTableName()+".CRITERIAID")
															.andCondition(getCurrentOrgIdCondition(criteriaModule))
															.andCondition(getCondition(criteriaId, ids, PickListOperators.IS))
															;
		
		List<Map<String, Object>> criteriaProps = criteriaBuilder.get();
	
		
		List<Long> criteriaValues = new ArrayList<>();
		if (criteriaProps != null && !criteriaProps.isEmpty()) {
			Map<Long, Criteria> criteriaMap = new HashMap<>();
			for(Map<String, Object> props : criteriaProps) {
				Long criteriaValueId = (Long) props.get("criteriaValueId");
				if (criteriaValueId != null && criteriaValueId != -1) {
					criteriaValues.add(criteriaValueId);
				}
				Condition condition = FieldUtil.getAsBeanFromMap(props, Condition.class);
				
				Criteria parentCriteria = criteriaMap.get(condition.getParentCriteriaId());
				if (parentCriteria == null) {
					parentCriteria = FieldUtil.getAsBeanFromMap(props, Criteria.class);
					parentCriteria.setConditions(new HashMap<>());
					
					criteriaMap.put(parentCriteria.getCriteriaId(), parentCriteria);
				}
				parentCriteria.getConditions().put(String.valueOf(condition.getSequence()), condition);
			}
			if (!criteriaValues.isEmpty()) {
				Map<Long, Criteria> criteriaValueMap = CriteriaAPI.getCriteriaAsMap(criteriaValues);

				for (Criteria criteria : criteriaMap.values())  {
					for (Entry <String,Condition> entry : criteria.getConditions().entrySet()) {
						Condition condition = entry.getValue();
						if (condition.getCriteriaValueId() != -1) {
							condition.setCriteriaValue(criteriaValueMap.get(condition.getCriteriaValueId()));
						}
					}
					
				}
			}
			return criteriaMap;
		}
		return null;
	}
	
	public static Condition getOrgIdCondition(long orgId, FacilioModule module) {
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getOrgIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(String.valueOf(orgId));
		
		return idCondition;
	}
	
	public static Condition getCurrentOrgIdCondition(FacilioModule module) {
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getOrgIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(String.valueOf(AccountUtil.getCurrentOrg().getOrgId()));
		
		return idCondition;
	}
	
	public static Condition getCurrentSiteIdCondition(FacilioModule module) {
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getSiteIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(String.valueOf(AccountUtil.getCurrentSiteId()));
		
		return idCondition;
	}
	
	public static Condition getIdCondition(long id, FacilioModule module) {
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(String.valueOf(id));
		
		return idCondition;
	}
	
	public static Condition getIdCondition(Collection<Long> ids, FacilioModule module) {
		String id = StringUtils.join(ids, ",");
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(id);
		
		return idCondition;
	}
	
	public static Condition getIdCondition(String idList, FacilioModule module) {
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(idList);
		return idCondition;
	}
	
	public static Condition getCondition( FacilioField field,Operator operator) {
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(operator);
		return condition;
	}
	
	public static Condition getCondition( FacilioField field,Collection<Long> values,Operator operator) {
		if (values != null && !values.isEmpty()) {
			String val = StringUtils.join(values, ",");
			return getCondition(field, val, operator);
		}
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static Condition getCondition (FacilioField field,String valueList,Operator operator) {
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(operator);
		condition.setValue(valueList);
		return condition;
	}
	
	@SuppressWarnings("rawtypes")
	public static Condition getCondition (String colName,String fieldName,String valueList,Operator operator)
	{
		Condition condition = new Condition();
		condition.setFieldName(fieldName);
		condition.setColumnName(colName);
		condition.setOperator(operator);
		condition.setValue(valueList);
		return condition;
	}
	
	@SuppressWarnings("rawtypes")
	public static Condition getConditionFromList (String colName,String fieldName,Collection<Long> values,Operator operator)
	{
		String val = StringUtils.join(values, ",");
		return getCondition(colName, fieldName, val, operator);
	}
	
	public static Condition getCondition (FacilioField field,Criteria value,Operator operator) {
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(operator);
		condition.setCriteriaValue(value);
		return condition;
	}
}
