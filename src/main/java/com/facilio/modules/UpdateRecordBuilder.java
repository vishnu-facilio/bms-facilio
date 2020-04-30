package com.facilio.modules;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import com.facilio.modules.fields.SupplementRecord;
import com.facilio.modules.fields.UpdateSupplementHandler;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder.GenericJoinBuilder;
import com.facilio.db.builder.JoinBuilderIfc;
import com.facilio.db.builder.UpdateBuilderIfc;
import com.facilio.db.builder.WhereBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;

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
	private E bean;
	private List<SupplementRecord> updateSupplements;
	private List<Long> recordIds = null;
	private boolean isIdsFetched = false;
	private ScopeHandler.ScopeFieldsAndCriteria scopeFieldsAndCriteria;
	private Collection<FacilioModule> joinModules;


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
	@Deprecated
	public UpdateRecordBuilder<E> andCustomWhere(String whereCondition, Object... values) {
		this.where.andCustomWhere(whereCondition, values);
		this.selectBuilder.andCustomWhere(whereCondition, values);
		return this;
	}

	@Override
	@Deprecated
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

	public UpdateRecordBuilder<E> updateSupplement(SupplementRecord supplement) {
		if (updateSupplements == null) {
			updateSupplements = new ArrayList<>();
		}
		updateSupplements.add(supplement);
		return this;
	}

	public UpdateRecordBuilder<E> updateSupplements(Collection<? extends SupplementRecord> supplements) {
		if (updateSupplements == null) {
			updateSupplements = new ArrayList<>();
		}
		updateSupplements.addAll(supplements);
		return this;
	}

	@Override
	public int update(E bean) throws Exception {
		this.bean = bean;
		Map<String, Object> moduleProps = FieldUtil.getAsProperties(bean);
		return updateViaMap(moduleProps);
	}

	private void removeSystemProps (Map<String, Object> moduleProps) {
		moduleProps.remove("orgId");
		moduleProps.remove("moduleId");
		moduleProps.remove("id");
		if (FieldUtil.isSystemFieldsPresent(module)) {
			moduleProps.keySet().removeAll(FieldFactory.getSystemFieldNames());
		}
		if (FieldUtil.isBaseEntityRootModule(module)) {
			moduleProps.keySet().removeAll(FieldFactory.getBaseModuleSystemFieldNames());
		}
	}

	public int updateViaMap(Map<String, Object> props) throws Exception {
		int rowsUpdated = 0;
		if(MapUtils.isNotEmpty(props)) {
			checkForNull();
			Map<String, Object> moduleProps = new HashMap<>(props);
			removeSystemProps(moduleProps);
			scopeFieldsAndCriteria = ScopeHandler.getInstance().updateValuesForUpdateAndGetFieldsAndCriteria(module, joinModules, moduleProps);
			if (!moduleProps.isEmpty()) {
				updateLookupFields(moduleProps, fields);
				moduleProps.put("sysModifiedTime", System.currentTimeMillis());

				if (AccountUtil.getCurrentUser() != null) {
					moduleProps.put("sysModifiedBy", AccountUtil.getCurrentUser().getId());
				}

				if (withChangeSet) {
					recordIds = constructChangeSet(moduleProps);
					if (CollectionUtils.isNotEmpty(recordIds)) {
						WhereBuilder whereCondition = new WhereBuilder();
						whereCondition.andCondition(CriteriaAPI.getIdCondition(recordIds, module));

						rowsUpdated = update(whereCondition, moduleProps);
					}
				}
				else {
					FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
					WhereBuilder whereCondition = new WhereBuilder();
//					Condition orgCondition = CriteriaAPI.getCurrentOrgIdCondition(module);
//					whereCondition.andCondition(orgCondition);

					Condition moduleCondition = CriteriaAPI.getCondition(moduleIdField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS);
					whereCondition.andCondition(moduleCondition);

					if (scopeFieldsAndCriteria != null && scopeFieldsAndCriteria.getCriteria() != null && !scopeFieldsAndCriteria.getCriteria().isEmpty()) {
						whereCondition.andCriteria(scopeFieldsAndCriteria.getCriteria());
					}

					if (module.isTrashEnabled() && !withDeleted) {
						whereCondition.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", String.valueOf(false), BooleanOperators.IS));
					}
					whereCondition.andCustomWhere(where.getWhereClause(), where.getValues());
					rowsUpdated = update(whereCondition, moduleProps);
				}
				handleSupplements(moduleProps);

			}
		}
		updated = true;
		return rowsUpdated;
	}

	private void handleSupplements(Map<String, Object> props) throws Exception {
		if (CollectionUtils.isNotEmpty(updateSupplements)) {
			for (SupplementRecord record : updateSupplements) {
				UpdateSupplementHandler handler = record.newUpdateHandler();
				if (handler != null) {
					if (CollectionUtils.isNotEmpty(fetchIds())) {
						handler.updateSupplements(props, fetchIds());
					}
				}
			}
		}
	}

	private List<Long> fetchIds() throws Exception {
		if (CollectionUtils.isEmpty(recordIds) && !isIdsFetched) {
			List<FacilioField> fields = Collections.singletonList(FieldFactory.getIdField(module));
			this.selectBuilder.select(fields).skipPermission();
			List<Map<String, Object>> oldProps = selectBuilder.getAsProps();
			recordIds = oldProps.stream()
					.map(p -> (Long) p.get("id"))
					.collect(Collectors.toList());
			isIdsFetched = true;
		}
		return recordIds;
	}

	private List<Long> constructChangeSet(Map<String, Object> moduleProps) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> allFields = modBean.getAllFields(module.getName());

		if (oldValues == null) {
			this.selectBuilder.select(allFields);
			selectBuilder.skipPermission();
			oldValues = selectBuilder.get();
		}

		isIdsFetched = true;
		if (CollectionUtils.isNotEmpty(oldValues)) {
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
			FacilioField isDeletedField = FieldFactory.getIsDeletedField(module.getParentModule());
			updateFields.add(isDeletedField);
		}

		updateFields.addAll(FieldUtil.removeMultiRecordFields(fields));
		if (scopeFieldsAndCriteria != null && CollectionUtils.isNotEmpty(scopeFieldsAndCriteria.getFields())) {
			updateFields.addAll(scopeFieldsAndCriteria.getFields());
		}
		if (FieldUtil.isSystemFieldsPresent(module)) {
			updateFields.addAll(FieldFactory.getSystemFields(module));
		}
		if (FieldUtil.isBaseEntityRootModule(module)) {
			updateFields.addAll(FieldFactory.getBaseModuleSystemFields(module));
		}

		FacilioModule prevModule = module;
		FacilioModule extendedModule = module.getExtendModule();
		builder.setBaseTableName(prevModule.getTableName());
		while(extendedModule != null) {
			addJoinModules(Collections.singletonList(extendedModule));
			builder.innerJoin(extendedModule.getTableName())
					.on(prevModule.getTableName()+".ID = "+extendedModule.getTableName()+".ID");
			prevModule = extendedModule;
			extendedModule = extendedModule.getExtendModule();
		}

		builder.andCustomWhere(where.getWhereClause(), where.getValues());

		prevModule = module;
		List<FacilioField> f = new ArrayList<>();
		List<FileField> fileFields = new ArrayList<>();
		int updateCount = 0;
		while (prevModule != null) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder(builder);
			for (FacilioField field : updateFields) {
//				if ((field.getModule() == null && prevModule.getExtendModule() != null)) {
//					continue;
//				}
				// TODO check this again
				if (field.getModule() == null) {
					continue;
				}
				if (prevModule.equals(field.getModule())) {
					f.add(field);
				}
//				if (((prevModule.getExtendModule() == null && field.getModule().getExtendModule() == null) || (field.getModule().equals(prevModule)) )) {
//					f.add(field);
//				}
			}
			updateBuilder.fields(f);
			updateBuilder.table(prevModule.getTableName());

			prevModule = prevModule.getExtendModule();
			if (!f.isEmpty()) {
				updateCount += updateBuilder.update(new HashMap<>(moduleProps));
			}
			f.clear();
			if (CollectionUtils.isNotEmpty(updateBuilder.getFileFields())) {
				fileFields.addAll(updateBuilder.getFileFields());
			}
		}

		removeFileCustomFields(fileFields);

		return updateCount;
	}

	private void removeFileCustomFields(List<FileField> fileFields) {
		if (CollectionUtils.isNotEmpty(fileFields) && bean != null && MapUtils.isNotEmpty(bean.getData())) {
			Map<String, Object> data = bean.getData();
			fileFields = fileFields.stream().filter(field -> !field.isDefault()).collect(Collectors.toList());
			if (!fileFields.isEmpty()) {
				for(FacilioField field : fileFields) {
					if (data.containsKey(field.getName())) {
						data.remove(field.getName());
					}
				}
			}
		}
	}

	private void updateLookupFields(Map<String, Object> moduleProps, List<FacilioField> fields) throws Exception {
		for(FacilioField field : fields) {
			if(field.getDataTypeEnum() == FieldType.LOOKUP) {
				Map<String, Object> lookupProps = (Map<String, Object>) moduleProps.get(field.getName());
				if(lookupProps != null && !lookupProps.isEmpty()) {
					if(lookupProps.get("id") != null) {
						moduleProps.put(field.getName(), lookupProps.get("id"));
					}
				}
				else {
					moduleProps.remove(field.getName());
				}
			}
		}
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
	
	//will be an interface method once i check in in framework
		public void addJoinModules(Collection<FacilioModule> joinModules) {
			if(this.joinModules == null) {
				this.joinModules = new ArrayList<FacilioModule>();  
			}
			this.joinModules.addAll(joinModules);
		}
}
