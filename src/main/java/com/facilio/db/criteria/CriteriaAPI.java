package com.facilio.db.criteria;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.enums.Version;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.*;
import com.facilio.db.util.DBConf;
import com.facilio.modules.*;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiCurrencyField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class CriteriaAPI extends BaseCriteriaAPI {
	
	public static long addCriteria(Criteria criteria) throws Exception {
		return addCriteria(criteria, AccountUtil.getCurrentOrg().getOrgId());
	}
	
	public static long addCriteria(Criteria criteria, long orgId) throws Exception {
		if(criteria != null) {
			criteria.validatePattern();
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

	public static void updateConditionField(String moduleName,Criteria criteria) throws Exception{

		if (criteria == null){
			return;
		}

		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for (String key : criteria.getConditions().keySet()){
			Condition condition = criteria.getConditions().get(key);
			FacilioField field = moduleBean.getField(condition.getFieldName(),moduleName);
			if(field!=null) {
				condition.setField(field);
			}
		}

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
						if(condition.getField()==null){condition.setField(field);}
						condition.setColumnName(field.getCompleteColumnName());
					}
					else {
						throw new IllegalArgumentException("Both module name and fieldName cannot be null in Condition");
					}
				}
				if (condition.getOperator() == null) {
					throw new IllegalArgumentException("Operator cannot be null in Condition");
				}
				if(condition.getField() != null &&  condition.getField() instanceof MultiCurrencyField){
					condition.getField().setColumnName(((MultiCurrencyField)condition.getField()).getBaseCurrencyValueColumnName());
					condition.setColumnName(condition.getField().getCompleteColumnName());
				}
				if (condition.getOperator().isValueNeeded() && condition.getValue() == null && condition.getCriteriaValue() == null && condition.getJsonValue() == null) {
					throw new IllegalArgumentException("Value cannot be null for Condition with operator "+condition.getOperator());
				}
				condition.validateValue();
				
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
						Criteria criteriaValue=condition.getCriteriaValue();
						if(condition.getOperator() instanceof LookupOperator && condition.getField()!=null && ((LookupField)condition.getField()).getLookupModule()!=null)
						{
							criteriaValue.getConditions().get("1").setModuleName(((LookupField)condition.getField()).getLookupModule().getName());
						}
						long criteriaValueId = addCriteria(criteriaValue, orgId);
						condition.setCriteriaValueId(criteriaValueId);
					}
					else {
						condition.setCriteriaValueId(condition.getCriteriaValue().getCriteriaId());
					}
				}
				if (condition.getOperator().isDynamicOperator() || condition.isExpressionValue()) {
					condition.updateFieldNameWithModule();
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
	
	public static void deleteCriteria (long id) throws Exception {
		FacilioModule module = ModuleFactory.getCriteriaModule();
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition("CRITERIAID", "criteriaId", String.valueOf(id), NumberOperators.EQUALS))
														;
		deleteBuilder.delete();
	}

	public static void batchDeleteCriteria (List<Long> ids) throws Exception {
		if (CollectionUtils.isEmpty(ids)) {
			return;
		}
		FacilioModule module = ModuleFactory.getCriteriaModule();
		FacilioField criteriaId = FieldFactory.getAsMap(FieldFactory.getCriteriaFields()).get("criteriaId");
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
													.table(module.getTableName())
													;
		deleteBuilder.batchDelete(Collections.singletonList(criteriaId), ids.stream().map(i -> Collections.singletonMap(criteriaId.getName(), (Object) i)).collect(Collectors.toList()));
	}
	
	public static Criteria getCriteria(long id) throws Exception {
		return getCriteria(DBConf.getInstance().getCurrentOrg().getId(), id);
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
//															.andCondition(getCurrentOrgIdCondition(criteriaModule))
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
				if(condition.getCriteriaValueId() > 0) {
					condition.setCriteriaValue(getCriteria(orgId, condition.getCriteriaValueId()));
				}
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
//															.andCondition(getCurrentOrgIdCondition(criteriaModule))
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
		return getCondition(AccountConstants.getOrgIdField(module), String.valueOf(orgId), NumberOperators.EQUALS);
	}
	
	public static Condition getSiteIdCondition(Collection<Long> siteIds, FacilioModule module) {
		return getCondition(AccountConstants.getSiteIdField(module), siteIds, NumberOperators.EQUALS);
	}

	public static Condition getModuleIdIdCondition(long moduleId, FacilioModule module) {
		return getCondition(FieldFactory.getModuleIdField(module), String.valueOf(moduleId), NumberOperators.EQUALS);
	}
	
	public static Condition getCurrentSiteIdCondition(FacilioModule module) {
		return getCondition(FieldFactory.getSiteIdField(module), String.valueOf(AccountUtil.getCurrentSiteId()), NumberOperators.EQUALS);
	}
	
	public static Condition getIdCondition(long id, FacilioModule module) {
		return getIdCondition(String.valueOf(id), module);
	}
	
	public static Condition getIdCondition(Collection<Long> ids, FacilioModule module) {
		return getIdCondition(StringUtils.join(ids, ","), module);
	}
	
	public static Condition getIdCondition(String idList, FacilioModule module) {
		return getCondition(FieldFactory.getIdField(module), idList, NumberOperators.EQUALS);
	}

	public static Condition getNameCondition(String name, FacilioModule module) {
		return getCondition(FieldFactory.getNameField(module), name, StringOperators.IS);
	}

	public static Condition getNameCondition(Collection<String>names,FacilioModule module) {
		return getNameCondition(StringUtils.join(names,","),module);
	}

	public static String getCurrentBuildVersionCriteria() {
		Long currentVersionId = Version.getCurrentVersion() != null ? Version.getCurrentVersion().getVersionId() : null;

		FacilioField versionField = FieldFactory.getBuildVersionField(null);
		String versionFieldColumnName = versionField.getColumnName();

		StringBuilder joinCondition = new StringBuilder();
		joinCondition.append("( ");
		joinCondition.append(versionFieldColumnName).append(" IS NULL ");

		if(currentVersionId != null && currentVersionId > 0) {
			joinCondition.append(" OR ")
					.append(versionFieldColumnName)
					.append(" & ")
					.append(currentVersionId)
					.append(" = ")
					.append(currentVersionId);
		}
		joinCondition.append(" )");

		return joinCondition.toString();
	}

	public static FacilioField fetchFieldFromFQFieldName(String fullyQualifiedFieldName) throws Exception {
		String[] module = fullyQualifiedFieldName.split("\\.");
		FacilioUtil.throwIllegalArgumentException(module.length <= 1, "Invalid Fully Qualified name for field while parsing from operator");
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField(module[1], module[0]);
		return field;
	}
	public static Object setLookupFieldsData(Criteria criteria, Object record) throws Exception
	{
		if(record instanceof Map)
		{
			record=FieldUtil.getAsBeanFromMap((Map<String, Object>) record,ModuleBaseWithCustomFields.class);
		}
		ModuleBean modBean= (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<String> lookupFieldName = new ArrayList<>();
		if(criteria!=null && !criteria.isEmpty())
		{
			lookupFieldName.addAll(criteria.getConditions().values().stream().filter(i -> i.getOperator() instanceof LookupOperator).map(i -> i.getFieldName()).collect(Collectors.toSet()));
		}
		if(!lookupFieldName.isEmpty() && lookupFieldName.size()>=1)
		{
			for(String name:lookupFieldName)
			{
				String[] fieldName=name.split("\\.");
				FacilioField lookupField=modBean.getField(fieldName[1],fieldName[0]);
				Object propertyVal=FieldUtil.getValue((ModuleBaseWithCustomFields) record,lookupField);
				if(propertyVal!=null) {
					long recordId= (long) FieldUtil.getAsProperties(propertyVal).get("id");
					Object lookupFieldData = FieldUtil.getLookupVal((LookupField) lookupField, recordId);
					FieldUtil.setValue((ModuleBaseWithCustomFields) record,lookupField,lookupFieldData);
				}
			}
		}
		return record;
	}
}
