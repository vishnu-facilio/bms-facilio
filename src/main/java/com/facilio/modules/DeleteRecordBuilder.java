package com.facilio.modules;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

import com.facilio.modules.fields.DeleteSupplementHandler;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.modules.fields.UpdateSupplementHandler;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.DeleteBuilderIfc;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericDeleteRecordBuilder.GenericJoinBuilder;
import com.facilio.db.builder.JoinBuilderIfc;
import com.facilio.db.builder.WhereBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class DeleteRecordBuilder<E extends ModuleBaseWithCustomFields> implements DeleteBuilderIfc<E> {
	private GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder();
	private SelectRecordsBuilder<E> selectBuilder = new SelectRecordsBuilder<E>();
	private UpdateRecordBuilder<E> updateBuilder = new UpdateRecordBuilder<E>();
	private String moduleName;
	private FacilioModule module;
	private WhereBuilder where = new WhereBuilder();
	private int level = 1;
	private List<SupplementRecord> deleteSupplements;
	private boolean skipModuleCriteria = false;

	public DeleteRecordBuilder<E> moduleName (String moduleName) {
		selectBuilder.moduleName(moduleName);
		updateBuilder.moduleName(moduleName);
		this.moduleName = moduleName;
		return this;
	}
	
	public DeleteRecordBuilder<E> module (FacilioModule module) {
		selectBuilder.module(module);
		updateBuilder.module(module);
		this.module = module;
		return this;
	}
	
	public DeleteRecordBuilder<E> level(int level) {
		this.level = level;
		return this;
	}
	
	public DeleteRecordBuilder<E> skipModuleCriteria() {
		this.skipModuleCriteria = true;
		selectBuilder.skipModuleCriteria();
		return this;
	}
	
	@Override
	public DeleteRecordBuilder<E> table(String tableName) {
		// TODO Auto-generated method stub
		return this;
	}
	
	@Override
	public DeleteRecordBuilder<E> useExternalConnection (Connection conn) {
		deleteBuilder.useExternalConnection(conn);
		selectBuilder.useExternalConnection(conn);
		updateBuilder.useExternalConnection(conn);
		return this;
	}
	
	@Override
	public JoinRecordBuilder<E> innerJoin(String tableName, boolean delete) {
		return new JoinRecordBuilder<E>(this, deleteBuilder.innerJoin(tableName, delete), selectBuilder.innerJoin(tableName), updateBuilder.innerJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> leftJoin(String tableName, boolean delete) {
		return new JoinRecordBuilder<E>(this, deleteBuilder.leftJoin(tableName, delete), selectBuilder.leftJoin(tableName), updateBuilder.leftJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> rightJoin(String tableName, boolean delete) {
		return new JoinRecordBuilder<E>(this, deleteBuilder.rightJoin(tableName, delete), selectBuilder.rightJoin(tableName), updateBuilder.rightJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> fullJoin(String tableName, boolean delete) {
		return new JoinRecordBuilder<E>(this, deleteBuilder.fullJoin(tableName, delete), selectBuilder.fullJoin(tableName), updateBuilder.fullJoin(tableName));
	}
	
	@Override
	@Deprecated
	public DeleteRecordBuilder<E> andCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}
	
	@Override
	@Deprecated
	public DeleteRecordBuilder<E> orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}
	
	@Override
	public DeleteRecordBuilder<E> andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}
	
	@Override
	public DeleteRecordBuilder<E> orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}
	
	@Override
	public DeleteRecordBuilder<E> andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}
	
	@Override
	public DeleteRecordBuilder<E> orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}

	@Override
	public DeleteRecordBuilder<E> recordsPerBatch (int recordsPerBatch) {
		this.deleteBuilder.recordsPerBatch(recordsPerBatch);
		return this;
	}

	public DeleteRecordBuilder<E> deleteSupplement(SupplementRecord supplement) {
		Objects.requireNonNull(supplement, "Supplement record cannot be null");
		if (deleteSupplements == null) {
			deleteSupplements = new ArrayList<>();
		}
		deleteSupplements.add(supplement);
		return this;
	}

	public DeleteRecordBuilder<E> deleteSupplements(Collection<? extends SupplementRecord> supplements) {
		Objects.requireNonNull(supplements, "Supplement records cannot be null");
		FacilioUtil.throwIllegalArgumentException(supplements.stream().anyMatch(Objects::isNull), "Supplement record cannot be null");
		if (deleteSupplements == null) {
			deleteSupplements = new ArrayList<>();
		}
		deleteSupplements.addAll(supplements);
		return this;
	}

	@Override
	public int delete() throws Exception {
		// TODO Auto-generated method stub
		checkForNull();
		List<Long> ids = getIds();
		return commonDeleteByIds(ids);
	}

	@Override
	public int batchDeleteById(Collection<Long> ids) throws Exception {
		checkForNull();
		return commonDeleteByIds(ids);
	}

	private int commonDeleteByIds (Collection<Long> ids) throws Exception {
		if (CollectionUtils.isNotEmpty(ids)) {
			handleSupplements(ids);
			FacilioModule currentModule = module;
			FacilioModule extendedModule = module.getExtendModule();
			int currentLevel = maxLevel();
			while(extendedModule != null && level < currentLevel) {
//				deleteBuilder.innerJoin(extendedModule.getTableName(), true)
//						.on(currentModule.getTableName()+".ID = "+extendedModule.getTableName()+".ID");
				currentModule = extendedModule;
				extendedModule = extendedModule.getExtendModule();
				currentLevel--;
			}
			deleteBuilder.table(currentModule.getTableName());
			return deleteBuilder.batchDeleteById(ids);
//			if (ids.size() <= recordsPerBatch) {
//				deleteBuilder.andCondition(CriteriaAPI.getIdCondition(ids, currentModule));
//				return deleteBuilder.delete();
//			}
//			else {
//				int deletedRecords = 0;
//				List<List<Long>> chunks = ListUtils.partition(ids, recordsPerBatch);
//				for (List<Long> idList : chunks) {
//					GenericDeleteRecordBuilder chunkDeleteBuilder = new GenericDeleteRecordBuilder(deleteBuilder);
//					deleteBuilder.andCondition(CriteriaAPI.getIdCondition(ids, currentModule));
//					deletedRecords += deleteBuilder.delete();
//				}
//				return deletedRecords;
//			}
		}
		return 0;
	}

	private void handleSupplements(Collection<Long> ids) throws Exception {
		if (CollectionUtils.isNotEmpty(deleteSupplements)) {
			for (SupplementRecord record : deleteSupplements) {
				DeleteSupplementHandler handler = record.newDeleteHandler();
				if (handler != null) {
					handler.deleteSupplements(ids);
				}
			}
		}
	}
	
	public int markAsDelete() throws Exception {
		checkForNull();
		
		if (!module.isTrashEnabled()) {
			throw new IllegalArgumentException("Trash is not enabled for this module and so cannot be marked as delete");
		}

		List<FacilioField> fields = new ArrayList<>(2);
		FacilioField isDeletedField = FieldFactory.getIsDeletedField(module.getParentModule());
		FacilioField deletedTimeField = FieldFactory.getSysDeletedTimeField(module.getParentModule());
		FacilioField deletedByField = FieldFactory.getSysDeletedByField(module.getParentModule());
		fields.add(isDeletedField);
		fields.add(deletedTimeField);
		fields.add(deletedByField);

		Map<String, Object> prop = new HashMap<>();
		prop.put(isDeletedField.getName(), true);
		prop.put(deletedTimeField.getName(), System.currentTimeMillis());
		if (AccountUtil.getCurrentUser() != null) {
			prop.put(deletedByField.getName(), Collections.singletonMap("id", AccountUtil.getCurrentUser().getId()));
		}

		updateBuilder.fields(fields)
						.andCustomWhere(where.getWhereClause(),  where.getValues());
		return updateBuilder.updateViaMap(prop);
	}
	
	private List<Long> getIds() throws Exception {
		FacilioField idField = FieldFactory.getIdField(module);
		selectBuilder.select(Collections.singletonList(idField))
						.andCustomWhere(where.getWhereClause(),  where.getValues())
						.orderBy(idField.getCompleteColumnName())
						;
		List<Map<String, Object>> ids = selectBuilder.getAsProps();
		if (ids != null && !ids.isEmpty()) {
			return ids.stream().map(id -> (Long)id.get("id")).collect(Collectors.toList());
		}
		return null;
	}
	
	private WhereBuilder getWhereWithOrgIdAndModuleId(FacilioModule module) {
		/*FacilioField orgIdField = FieldFactory.getOrgIdField(module);*/
		FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
		WhereBuilder whereCondition = new WhereBuilder();
		/*Condition orgCondition = CriteriaAPI.getCondition(orgIdField, String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS);*/
		/*whereCondition.andCondition(orgCondition);*/
		
		Condition moduleCondition = CriteriaAPI.getCondition(moduleIdField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS);
		whereCondition.andCondition(moduleCondition);
		
		return whereCondition;
	}
	
	private int maxLevel() {
		int level = 1;
		FacilioModule extendedModule = module.getExtendModule();
		while(extendedModule != null) {
			level++;
			extendedModule = extendedModule.getExtendModule();
		}
		return level;
	}
	
	private void checkForNull() throws Exception {
		if(module == null) {
			if(moduleName == null || moduleName.isEmpty()) {
				throw new IllegalArgumentException("Both Module and Module Name cannot be empty");
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			module = modBean.getModule(moduleName);
		}
	}
	
	public static class JoinRecordBuilder<E extends ModuleBaseWithCustomFields> implements JoinBuilderIfc<DeleteRecordBuilder<E>> {
		private DeleteRecordBuilder<E> parentBuilder;
		private GenericJoinBuilder deleteJoinBuilder;
		private SelectRecordsBuilder.JoinRecordBuilder<E> selectJoinBuilder;
		private UpdateRecordBuilder.JoinRecordBuilder<E> updateJoinBuilder;
		
		private JoinRecordBuilder(DeleteRecordBuilder<E> parentBuilder, GenericJoinBuilder deleteJoinBuilder, SelectRecordsBuilder.JoinRecordBuilder<E> selectJoinBuilder, UpdateRecordBuilder.JoinRecordBuilder<E> updateJoinBuilder) {
			this.parentBuilder = parentBuilder;
			this.deleteJoinBuilder = deleteJoinBuilder;
			this.selectJoinBuilder = selectJoinBuilder;
			this.updateJoinBuilder = updateJoinBuilder;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public DeleteRecordBuilder<E> on(String condition) {
			// TODO Auto-generated method stub
			deleteJoinBuilder.on(condition);
			selectJoinBuilder.on(condition);
			updateJoinBuilder.on(condition);
			return parentBuilder;
		}
		
	}
}
