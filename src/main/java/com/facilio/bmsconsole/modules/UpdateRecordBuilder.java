package com.facilio.bmsconsole.modules;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder.GenericJoinBuilder;
import com.facilio.sql.JoinBuilderIfc;
import com.facilio.sql.UpdateBuilderIfc;
import com.facilio.sql.WhereBuilder;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;

public class UpdateRecordBuilder<E extends ModuleBaseWithCustomFields> implements UpdateBuilderIfc<E> {
	
	private GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder();
	private SelectRecordsBuilder<E> selectBuilder = new SelectRecordsBuilder<E>();
	private String moduleName;
	private FacilioModule module;
	private List<FacilioField> fields = new ArrayList<>();
	private WhereBuilder where = new WhereBuilder();
	private boolean withChangeSet = false;
	private boolean withDeleted = false;
	private Map<Long, List<UpdateChangeSet>> changeSet;
	private List<E> oldValues;
	private boolean updated = false;
	
	public UpdateRecordBuilder () {
		// TODO Auto-generated constructor stub
	}
	
	public UpdateRecordBuilder<E> moduleName(String moduleName) {
		this.selectBuilder.moduleName(moduleName);
		this.moduleName = moduleName;
		return this;
	}
	
	public UpdateRecordBuilder<E> module(FacilioModule module) {
		this.selectBuilder.module(module);
		this.module = module;
		return this;
	}
	
	@Override
	public UpdateRecordBuilder<E> table(String tableName) {
		return this;
	}
	
	@Override
	public UpdateRecordBuilder<E> useExternalConnection (Connection conn) {
		builder.useExternalConnection(conn);
		selectBuilder.useExternalConnection(conn);
		return this;
	}

	@Override
	public JoinRecordBuilder<E> innerJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, selectBuilder.innerJoin(tableName), builder.innerJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> leftJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, selectBuilder.leftJoin(tableName), builder.leftJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> rightJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, selectBuilder.rightJoin(tableName), builder.rightJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> fullJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, selectBuilder.fullJoin(tableName), builder.fullJoin(tableName));
	}

	@Override
	public UpdateRecordBuilder<E> andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		this.selectBuilder.andCustomWhere(whereCondition, values);
		return this;
	}
	
	@Override
	public UpdateRecordBuilder<E> orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		this.selectBuilder.orCustomWhere(whereCondition, values);
		return this;
	}

	@Override
	public UpdateRecordBuilder<E> andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		this.selectBuilder.andCondition(condition);
		return this;
	}

	@Override
	public UpdateRecordBuilder<E> orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		this.selectBuilder.orCondition(condition);
		return this;
	}

	@Override
	public UpdateRecordBuilder<E> andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		this.selectBuilder.andCriteria(criteria);
		return this;
	}

	@Override
	public UpdateRecordBuilder<E> orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		this.selectBuilder.orCriteria(criteria);
		return this;
	}

	@Override
	public UpdateRecordBuilder<E> fields(List<FacilioField> fields) {
		this.fields = fields;
		return this;
	}
	
	public UpdateRecordBuilder<E> withChangeSet(Class<E> beanClass) {
		this.withChangeSet = true;
		selectBuilder.beanClass(beanClass);
		return this;
	}
	
	public UpdateRecordBuilder<E> withChangeSet(List<E> oldValues) {
		this.withChangeSet = true;
		this.oldValues = oldValues;
		return this;
	}
	
	public UpdateRecordBuilder<E> withDeleted() {
		this.withDeleted = true;
		selectBuilder.fetchDeleted();
		return this;
	}
	
	public Map<Long, List<UpdateChangeSet>> getChangeSet() {
		
		if (!updated) {
			throw new IllegalArgumentException("Update first and then get change set.");
		}
		
		return changeSet;
	}
	
	public List<E> getOldValues() {
		
		if (!updated) {
			throw new IllegalArgumentException("Update first and then get old values.");
		}
		
		return oldValues;
	}
 	
	@Override
	public int update(E bean) throws Exception {
		Map<String, Object> moduleProps = FieldUtil.getAsProperties(bean);
		return update(moduleProps);
	}
	
	public int update(Map<String, Object> props) throws Exception {
		updated = true;
		if(props != null) {
			checkForNull();
			
			Map<String, Object> moduleProps = new HashMap<>(props);
			moduleProps.remove("orgId");
			moduleProps.remove("moduleId");
			moduleProps.remove("id");
			if (FieldUtil.isSiteIdFieldPresent(module) && AccountUtil.getCurrentSiteId() != -1) {
				moduleProps.remove("siteId");
			}
			
			if (!moduleProps.isEmpty()) {
				updateLookupFields(moduleProps, fields);
				moduleProps.put("sysModifiedTime", System.currentTimeMillis());
				
				if (withChangeSet) {
					List<Long> ids = constructChangeSet(moduleProps);
					if (ids != null && !ids.isEmpty()) {
						WhereBuilder whereCondition = new WhereBuilder();
						whereCondition.andCondition(CriteriaAPI.getIdCondition(ids, module));
					
						return update(whereCondition, moduleProps);
					}
				}
				else {
					FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
					WhereBuilder whereCondition = new WhereBuilder();
					Condition orgCondition = CriteriaAPI.getCurrentOrgIdCondition(module);
					whereCondition.andCondition(orgCondition);
					
					Condition moduleCondition = CriteriaAPI.getCondition(moduleIdField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS);
					whereCondition.andCondition(moduleCondition);

					long currentSiteId = AccountUtil.getCurrentSiteId();
					if (FieldUtil.isSiteIdFieldPresent(module) && currentSiteId > 0) {
						Condition siteIdCondition = CriteriaAPI.getCurrentSiteIdCondition(module);
						whereCondition.andCondition(siteIdCondition);
					}

					if (module.isTrashEnabled() && !withDeleted) {
						whereCondition.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", String.valueOf(false), BooleanOperators.IS));
					}
					whereCondition.andCustomWhere(where.getWhereClause(), where.getValues());
					return update(whereCondition, moduleProps);
				}
			}
		}
		return 0;
	}
	
	private List<Long> constructChangeSet(Map<String, Object> moduleProps) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
		
		if (oldValues == null) {
			this.selectBuilder.select(allFields);
			oldValues = selectBuilder.get();
		}
		
		if (oldValues != null && !oldValues.isEmpty()) {
			List<Long> ids = new ArrayList<>();
			changeSet = new HashMap<>();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			Set<String> fieldNames = fieldMap.keySet();
			for (E bean : oldValues) {
				long id = bean.getId();
				ids.add(id);
				
				Map<String, Object> oldProp = FieldUtil.getAsProperties(bean);
				updateLookupFields(oldProp, allFields);
				
				MapDifference<String, Object> difference = Maps.difference(oldProp, moduleProps);
				List<UpdateChangeSet> currentChangeList = new ArrayList<>();
				getNewValues(id, fieldNames, fieldMap, difference.entriesOnlyOnRight(), currentChangeList);
				getDifference(id, fieldNames, fieldMap, difference.entriesDiffering(), currentChangeList);
				changeSet.put(id, currentChangeList);
			}
			
			return ids;
		}
		return null;
	}
	
	private void getNewValues(long recordId, Set<String> fieldNames, Map<String, FacilioField> fieldMap, Map<String, Object> difference, List<UpdateChangeSet> changeList) {
		for(Map.Entry<String, Object> entry : difference.entrySet()) {
			String fieldName = entry.getKey();
			if (fieldNames.contains(fieldName)) {
				UpdateChangeSet currentChange = new UpdateChangeSet();
				currentChange.setFieldId(fieldMap.get(fieldName).getFieldId());
				currentChange.setNewValue(entry.getValue());
				currentChange.setRecordId(recordId);
				changeList.add(currentChange);
			}
		}
	}
	
	private void getDifference(long recordId, Set<String> fieldNames, Map<String, FacilioField> fieldMap, Map<String, ValueDifference<Object>> difference, List<UpdateChangeSet> changeList) {
		for(Map.Entry<String, ValueDifference<Object>> entry : difference.entrySet()) {
			String fieldName = entry.getKey();
			if (fieldNames.contains(fieldName)) {
				UpdateChangeSet currentChange = new UpdateChangeSet();
				currentChange.setFieldId(fieldMap.get(fieldName).getFieldId());
				currentChange.setOldValue(entry.getValue().leftValue());
				currentChange.setNewValue(entry.getValue().rightValue());
				currentChange.setRecordId(recordId);
				changeList.add(currentChange);
			}
		}
	}
	
	private int update(WhereBuilder where, Map<String, Object> moduleProps) throws SQLException {
		Set<FacilioField> updateFields = new HashSet<>();
		if (module.isTrashEnabled()) {
			FacilioField isDeletedField = FieldFactory.getIsDeletedField();
			updateFields.add(isDeletedField);
		}
		
		updateFields.addAll(fields);
		if (FieldUtil.isSiteIdFieldPresent(module) && AccountUtil.getCurrentSiteId() == -1) {
			updateFields.add(FieldFactory.getSiteIdField(module));
		}
		
		FacilioModule prevModule = module;
		FacilioModule extendedModule = module.getExtendModule();
		builder.setBaseTableName(prevModule.getTableName());
		while(extendedModule != null) {
			builder.innerJoin(extendedModule.getTableName())
					.on(prevModule.getTableName()+".ID = "+extendedModule.getTableName()+".ID");
			if (FieldUtil.isSiteIdFieldPresent(extendedModule) && AccountUtil.getCurrentSiteId() == -1) {
				updateFields.add(FieldFactory.getSiteIdField(extendedModule));
			}
			prevModule = extendedModule;
			extendedModule = extendedModule.getExtendModule();
		}
		
		builder.andCustomWhere(where.getWhereClause(), where.getValues());
		
		prevModule = module;
		List<FacilioField> f = new ArrayList<>();
		int updateCount = 0;
		while (prevModule != null) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder(builder);
			for (FacilioField field : updateFields) {
				if ((field.getModule() == null && prevModule.getExtendModule() != null)) {
					continue;
				}
				if (((prevModule.getExtendModule() == null && field.getExtendedModule() == null) || (field.getExtendedModule().equals(prevModule)) )) {
					f.add(field);
				}
			}
			updateBuilder.fields(f);
			updateBuilder.table(prevModule.getTableName());
			
			prevModule = prevModule.getExtendModule();
			if (!f.isEmpty()) {
				updateCount += updateBuilder.update(moduleProps);
			}
			f.clear();
		}
		
		
		return updateCount;
	}
	
	private void updateLookupFields(Map<String, Object> moduleProps, List<FacilioField> fields) throws Exception {
		for(FacilioField field : fields) {
			if(field.getDataTypeEnum() == FieldType.LOOKUP) {
				Map<String, Object> lookupProps = (Map<String, Object>) moduleProps.get(field.getName()); 
				if(lookupProps != null && !lookupProps.isEmpty()) {
					if(lookupProps.get("id") != null) {
						moduleProps.put(field.getName(), lookupProps.get("id"));
					}
					//Lookup fields should not be updated while updating the parent field. This was the behaviour when ticket was a lookup in workorder/ alarm. It's no longer required and so I'm commenting it out
					/*else {
						LookupField lookupField = (LookupField) field;
						if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
							//Not sure if we are handling update of special fields like this
						}
						else {
							ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
							List<FacilioField> lookupBeanFields = modBean.getAllFields(lookupField.getLookupModule().getName());
							
							UpdateRecordBuilder<ModuleBaseWithCustomFields> lookupUpdateBuilder = new UpdateRecordBuilder<>()
																									.moduleName(lookupField.getLookupModule().getName())
																									.table(lookupField.getLookupModule().getTableName())
																									.fields(lookupBeanFields)
																									.andCustomWhere(getParentWhereClauseForLookup(field.getColumnName()), where.getValues());
							lookupUpdateBuilder.update(lookupProps);
						}
						moduleProps.remove(field.getName());
					}*/
				}
				else {
					moduleProps.remove(field.getName());
				}
			}
		}
	}
	
	private String getParentWhereClauseForLookup(String columnName) {
		StringBuilder builder = new StringBuilder();
		builder.append(" ID in (SELECT ")
				.append(columnName)
				.append(" FROM ")
				.append(module.getTableName())
				.append(" WHERE ")
				.append(where.getWhereClause())
				.append(")");
		
		return builder.toString();
	}
	
	private void checkForNull() throws Exception {
		if(fields == null || fields.size() < 1) {
			throw new IllegalArgumentException("Fields cannot be null or empty");
		}
		
		if(module == null) {
			if(moduleName == null || moduleName.isEmpty()) {
				throw new IllegalArgumentException("Both Module and Module Name cannot be empty");
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			module = modBean.getModule(moduleName);
		}
	}
	
	public static class JoinRecordBuilder<E extends ModuleBaseWithCustomFields> implements JoinBuilderIfc<UpdateRecordBuilder<E>> {
		private UpdateRecordBuilder<E> parentBuilder;
		private GenericJoinBuilder joinBuilder;
		private SelectRecordsBuilder.JoinRecordBuilder<E> selectJoinBuilder;
		
		private JoinRecordBuilder(UpdateRecordBuilder<E> parentBuilder, SelectRecordsBuilder.JoinRecordBuilder<E> selectJoinBuilder, GenericJoinBuilder joinBuilder) {
			this.parentBuilder = parentBuilder;
			this.joinBuilder = joinBuilder;
			this.selectJoinBuilder = selectJoinBuilder;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public UpdateRecordBuilder<E> on(String condition) {
			// TODO Auto-generated method stub
			joinBuilder.on(condition);
			selectJoinBuilder.on(condition);
			return parentBuilder;
		}
		
	}
}
