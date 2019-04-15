package com.facilio.bmsconsole.modules;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DeleteBuilderIfc;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericDeleteRecordBuilder.GenericJoinBuilder;
import com.facilio.sql.JoinBuilderIfc;
import com.facilio.sql.WhereBuilder;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteRecordBuilder<E extends ModuleBaseWithCustomFields> implements DeleteBuilderIfc<E> {
	private GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder();
	private SelectRecordsBuilder<E> selectBuilder = new SelectRecordsBuilder<E>();
	private UpdateRecordBuilder<E> updateBuilder = new UpdateRecordBuilder<E>();
	private String moduleName;
	private FacilioModule module;
	private WhereBuilder where = new WhereBuilder();
	private int level = 1;
	
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
	public DeleteRecordBuilder<E> andCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.andCustomWhere(whereCondition, values);
		return this;
	}
	
	@Override
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
	public int delete() throws Exception {
		// TODO Auto-generated method stub
		checkForNull();
		List<Long> ids = getIds();
		
		if (ids != null && !ids.isEmpty()) {
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
//			WhereBuilder orgWhere = getWhereWithOrgIdAndModuleId(currentModule);
			deleteBuilder//.andCustomWhere(orgWhere.getWhereClause(), orgWhere.getValues())
							.andCondition(CriteriaAPI.getIdCondition(ids, currentModule));
			return deleteBuilder.delete();
		}
		return 0;
	}
	
	public int markAsDelete() throws Exception {
		checkForNull();
		
		if (!module.isTrashEnabled()) {
			throw new IllegalArgumentException("Trash is not enabled for this module and so cannot be marked as delete");
		}
		
		FacilioField isDeletedField = FieldFactory.getIsDeletedField(module.getParentModule());
		Map<String, Object> prop = Collections.singletonMap(isDeletedField.getName(), true);
		updateBuilder.fields(Collections.singletonList(isDeletedField))
						.andCustomWhere(where.getWhereClause(),  where.getValues());
		return updateBuilder.updateViaMap(prop);
	}
	
	private List<Long> getIds() throws Exception {
		selectBuilder.select(Collections.singletonList(FieldFactory.getIdField(module)))
						.andCustomWhere(where.getWhereClause(),  where.getValues());
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
