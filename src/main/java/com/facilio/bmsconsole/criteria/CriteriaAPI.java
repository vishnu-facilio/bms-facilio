package com.facilio.bmsconsole.criteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
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
	
	private static void addConditions(Map<Integer, Condition> conditions, long parentCriteriaId, long orgId) throws Exception {
		if(conditions != null && !conditions.isEmpty()) {
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table("Conditions")
															.fields(FieldFactory.getConditionFields())
															;
			
			for(Map.Entry<Integer, Condition> conditionEntry : conditions.entrySet()) {
				Condition condition = conditionEntry.getValue();
				
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
				condition.getComputedWhereClause();
				Map<String, Object> conditionProp = FieldUtil.getAsProperties(condition);
				insertBuilder.addRecord(conditionProp);
			}
			insertBuilder.save();
		}
		else {
			throw new IllegalArgumentException("Condition can not be empty for a Criteria");
		}
	}
	
	public static Criteria getCriteria(long orgId, long criteriaId) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(FieldFactory.getCriteriaFields());
		fields.addAll(FieldFactory.getConditionFields());
		
		GenericSelectRecordBuilder criteriaBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table("Conditions")
															.innerJoin("Criteria")
															.on("Conditions.PARENT_CRITERIA_ID = Criteria.CRITERIAID")
															.andCustomWhere("orgId = ? AND criteriaId = ?", orgId, criteriaId);
		
		List<Map<String, Object>> criteriaProps = criteriaBuilder.get();
		
		Criteria criteria = null;
		Map<Integer, Condition> conditions = new HashMap<>();
		if(criteriaProps != null && !criteriaProps.isEmpty()) {
			for(Map<String, Object> props : criteriaProps) {
				if(criteria == null) {
					criteria = FieldUtil.getAsBeanFromMap(props, Criteria.class);
					criteria.setConditions(conditions);
				}
				Condition condition = FieldUtil.getAsBeanFromMap(props, Condition.class);
				conditions.put(condition.getSequence(), condition);
			}
		}
		return criteria;
	}
	
	public static Condition getCurrentOrgIdCondition(FacilioModule module) {
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getOrgIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(String.valueOf(AccountUtil.getCurrentOrg().getOrgId()));
		
		return idCondition;
	}
	
	public static Condition getIdCondition(long id, FacilioModule module) {
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(String.valueOf(id));
		
		return idCondition;
	}
	
	public static Condition getIdCondition(List<Long> ids, FacilioModule module) {
		String id = StringUtils.join(ids, ",");
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(module));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(id);
		
		return idCondition;
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
}
