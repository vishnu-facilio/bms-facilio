package com.facilio.bmsconsole.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder.GenericJoinBuilder;
import com.facilio.sql.JoinBuilderIfc;
import com.facilio.sql.SelectBuilderIfc;
import com.facilio.sql.WhereBuilder;

public class SelectRecordsBuilder<E extends ModuleBaseWithCustomFields> implements SelectBuilderIfc<E> {
	
	private static final Logger LOGGER = LogManager.getLogger(SelectRecordsBuilder.class.getName());
	private static final int LEVEL = 0;
	
	private GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
	private Class<E> beanClass;
	private List<FacilioField> select;
	private int level = 0;
	private int maxLevel = LEVEL;
	private String moduleName;
	private FacilioModule module;
	private boolean fetchDeleted = false;
	private String groupBy;
	private WhereBuilder where = new WhereBuilder();
	//Need where condition builder for custom field
	
	public SelectRecordsBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public SelectRecordsBuilder (SelectRecordsBuilder<E> selectBuilder) { //Do not call after calling getProps
		this.beanClass = selectBuilder.beanClass;
		this.level = selectBuilder.level;
		this.maxLevel = selectBuilder.maxLevel;
		this.moduleName = selectBuilder.moduleName;
		this.module = selectBuilder.module;
		this.fetchDeleted = selectBuilder.fetchDeleted;
		this.groupBy = selectBuilder.groupBy;
		
		if (selectBuilder.builder != null) {
			this.builder = new GenericSelectRecordBuilder(selectBuilder.builder);
		}
		if (selectBuilder.select != null) {
			this.select = new ArrayList<>(selectBuilder.select);
		}
		if (selectBuilder.where != null) {
			this.where = new WhereBuilder(selectBuilder.where);
		}
	}
	
	public SelectRecordsBuilder (int level) {
		this.level = level;
	}
	
	@Override
	public SelectRecordsBuilder<E> select(List<FacilioField> selectFields) {
		this.select = selectFields;
		return this;
	}
	
	public SelectRecordsBuilder<E> maxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
		return this;
	}
	
	@Override
	public SelectRecordsBuilder<E> table(String tableName) {
		return this;
	}

	@Override
	public JoinRecordBuilder<E> innerJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, builder.innerJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> leftJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, builder.leftJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> rightJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, builder.rightJoin(tableName));
	}
	
	@Override
	public JoinRecordBuilder<E> fullJoin(String tableName) {
		return new JoinRecordBuilder<E>(this, builder.fullJoin(tableName));
	}
	
	@Override
	public SelectRecordsBuilder<E> andCustomWhere(String where, Object... values) {
		this.where.andCustomWhere(where, values);
		return this;
	}
	
	@Override
	public SelectRecordsBuilder<E> orCustomWhere(String whereCondition, Object... values) {
		// TODO Auto-generated method stub
		this.where.orCustomWhere(whereCondition, values);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> andCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.andCondition(condition);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> orCondition(Condition condition) {
		// TODO Auto-generated method stub
		where.orCondition(condition);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> andCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.andCriteria(criteria);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> orCriteria(Criteria criteria) {
		// TODO Auto-generated method stub
		where.orCriteria(criteria);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> groupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}
	
	@Override
	public SelectRecordsBuilder<E> having(String having) {
		builder.having(having);
		return this;
	}
	
	@Override
	public SelectRecordsBuilder<E> orderBy(String orderBy) {
		builder.orderBy(orderBy);
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> limit(int limit) {
		builder.limit(limit);
		return this;
	}
	
	@Override
	public SelectRecordsBuilder<E> offset(int offset) {
		builder.offset(offset);
		return this;
	}
	
	public SelectRecordsBuilder<E> moduleName(String moduleName) {
		this.moduleName = moduleName;
		return this;
	}
	
	public SelectRecordsBuilder<E> module(FacilioModule module) {
		this.module = module;
		return this;
	}
	
	public SelectRecordsBuilder<E> beanClass(Class<E> beanClass) {
		this.beanClass = beanClass;
		return this;
	}
	
	public SelectRecordsBuilder<E> fetchDeleted() {
		fetchDeleted = true;
		return this;
	}
	
	@Override
	public List<E> get() throws Exception {
		checkForNull(true);
		long getStartTime = System.currentTimeMillis();
		List<Map<String, Object>> propList = getAsJustProps();
		long getTimeTaken = System.currentTimeMillis() - getStartTime;
		LOGGER.debug("Time Taken to get props in SelectBuilder : "+getTimeTaken);
		
		if(propList != null) {
			long lookupStartTime = System.currentTimeMillis();
			List<FacilioField> lookupFields = getLookupFields();
			for(Map<String, Object> props : propList) {
				for(FacilioField lookupField : lookupFields) {
					Long recordId = (Long) props.remove(lookupField.getName());
					if(recordId != null) {
						Object lookedupObj = null;
						if(level < maxLevel) {
							lookedupObj = FieldUtil.getLookupVal((LookupField) lookupField, recordId, level+1);
						}
						else {
							lookedupObj = getEmptyLookupVal((LookupField) lookupField, recordId);
						}
						if(lookedupObj != null) {
							props.put(lookupField.getName(), lookedupObj);
						}
					}
				}
			}
			long lookupTimeTaken = System.currentTimeMillis() - lookupStartTime;
			LOGGER.debug("Time Taken to convert lookup Fields in SelectBuilder : "+lookupTimeTaken);
		}
		long startTime = System.currentTimeMillis();
		List<E> beans = FieldUtil.getAsBeanListFromMapList(propList, beanClass);
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time Taken to convert to bean list in SelectBuilder : "+timeTaken);
		return beans;
	}
	
	public Map<Long, E> getAsMap() throws Exception {
		checkForNull(true);
		List<Map<String, Object>> propList = getAsJustProps();
		
		Map<Long, E> beanMap = new HashMap<>();
		
		if(propList != null && propList.size() > 0) {
			List<FacilioField> lookupFields = getLookupFields();
			for(Map<String, Object> props : propList) {
				for(FacilioField lookupField : lookupFields) {
					Long recordId = (Long) props.remove(lookupField.getName());
					if(recordId != null) {
						Object lookedupObj = null;
						if(level < maxLevel) {
							lookedupObj = FieldUtil.getLookupVal((LookupField) lookupField, recordId, level+1);
						}
						else {
							lookedupObj = getEmptyLookupVal((LookupField) lookupField, recordId);
						}
						if(lookedupObj != null) {
							props.put(lookupField.getName(), lookedupObj);
						}
					}
				}
				E bean = FieldUtil.getAsBeanFromMap(props, beanClass);
				beanMap.put(bean.getId(), bean);
			}
		}
		return beanMap;
	}
	
	public Map<Long, Map<String, Object>> getAsMapProps() throws Exception {
		checkForNull(false);
		List<Map<String, Object>> propList = getAsJustProps();
		
		Map<Long, Map<String, Object>> mapProps = new HashMap<>();
		
		if(propList != null && propList.size() > 0) {
			List<FacilioField> lookupFields = getLookupFields();
			for(Map<String, Object> props : propList) {
				for(FacilioField lookupField : lookupFields) {
					Long recordId = (Long) props.remove(lookupField.getName());
					if(recordId != null) {
						Map<String, Object> lookedupProp = null;
						if(level < maxLevel) {
							lookedupProp = FieldUtil.getLookupProp((LookupField) lookupField, recordId, level+1);
						}
						else {
							lookedupProp = new HashMap<>();
							lookedupProp.put("id", recordId);
						}
						if(lookedupProp != null) {
							props.put(lookupField.getName(), lookedupProp);
						}
					}
				}
				mapProps.put((Long) props.get("id"), props);
			}
		}
		return mapProps;
	}
	
	public List<Map<String, Object>> getAsProps() throws Exception {
		checkForNull(false);
		List<Map<String, Object>> propList = getAsJustProps();
		
		if(propList != null && propList.size() > 0) {
			List<FacilioField> lookupFields = getLookupFields();
			if(lookupFields.size() > 0) {
				for(Map<String, Object> props : propList) {
					for(FacilioField lookupField : lookupFields) {
						Long recordId = (Long) props.remove(lookupField.getName());
						if(recordId != null) {
							Map<String, Object> lookedupProp = null;
							if(level < maxLevel) {
								lookedupProp = FieldUtil.getLookupProp((LookupField) lookupField, recordId, level+1);
							}
							else {
								lookedupProp = new HashMap<>();
								lookedupProp.put("id", recordId);
							}
							if(lookedupProp != null) {
								props.put(lookupField.getName(), lookedupProp);
							}
						}
					}
				}
			}
		}
		return propList;
	}
	
	private List<FacilioField> getLookupFields() {
		List<FacilioField> lookupFields = new ArrayList<>();
		for(FacilioField field : select) {
			if(field.getDataTypeEnum() == FieldType.LOOKUP) {
				lookupFields.add(field);
			}
		}
		return lookupFields;
	}
	
	private List<Map<String, Object>> getAsJustProps() throws Exception {
		FacilioField orgIdField = FieldFactory.getOrgIdField(module);
		FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
		FacilioField siteIdField = FieldFactory.getSiteIdField(module);
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(orgIdField);
		selectFields.add(moduleIdField);
		
		if (FieldUtil.isSiteIdFieldPresent(module)) {
			selectFields.add(siteIdField);
		}
		
		if (groupBy == null || groupBy.isEmpty()) {
			selectFields.add(FieldFactory.getIdField(module));
		}
		else {
			StringBuilder moduleGroupBy = new StringBuilder();
			moduleGroupBy.append(orgIdField.getCompleteColumnName())
							.append(",")
							.append(moduleIdField.getCompleteColumnName())
							.append(",")
							.append(groupBy);
			
			builder.groupBy(moduleGroupBy.toString());
		}
		
		selectFields.addAll(select);
		builder.select(selectFields);
		
		WhereBuilder whereCondition = new WhereBuilder();
		Condition orgCondition = CriteriaAPI.getCondition(orgIdField, String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS);
		whereCondition.andCondition(orgCondition);
		
		Condition moduleCondition = CriteriaAPI.getCondition(moduleIdField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS);
		whereCondition.andCondition(moduleCondition);
		
		long currentSiteId = AccountUtil.getCurrentSiteId();
		
		if (FieldUtil.isSiteIdFieldPresent(module) && currentSiteId > 0) {
			Condition siteCondition = CriteriaAPI.getCondition(siteIdField, String.valueOf(currentSiteId), NumberOperators.EQUALS);
			whereCondition.andCondition(siteCondition);
		}
		
		if (module.isTrashEnabled()) {
			FacilioField isDeletedField = FieldFactory.getIsDeletedField();
			
			if (groupBy == null || groupBy.isEmpty()) {
				selectFields.add(isDeletedField);
			}
			
			if (!fetchDeleted) {
				whereCondition.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", String.valueOf(false), BooleanOperators.IS));
			}
		}
		
		whereCondition.andCustomWhere(where.getWhereClause(), where.getValues());
		
		builder.table(module.getTableName());
		
		FacilioModule prevModule = module;
		FacilioModule extendedModule = module.getExtendModule();
		while(extendedModule != null) {
			builder.innerJoin(extendedModule.getTableName())
					.on(prevModule.getTableName()+".ID = "+extendedModule.getTableName()+".ID");
			prevModule = extendedModule;
			extendedModule = extendedModule.getExtendModule();
		}
		
		builder.andCustomWhere(whereCondition.getWhereClause(), whereCondition.getValues());
		return builder.get();
	}
	
	private Object getEmptyLookupVal(LookupField lookupField, long id) throws Exception {
		if(id > 0) {
			if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
				return LookupSpecialTypeUtil.getEmptyLookedupObject(lookupField.getSpecialType(), id);
			}
			else {
				Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModuleName(lookupField.getLookupModule().getName());
				if(moduleClass != null) {
					ModuleBaseWithCustomFields lookedupModule = moduleClass.newInstance();
					lookedupModule.setId(id);
					return lookedupModule;
				}
				else {
					throw new IllegalArgumentException("Unknown Module Name in Lookup field "+lookupField);
				}
			}
		}
		else {
			return null;
		}
	}
	
	private void checkForNull(boolean checkBean) throws Exception {
		if(checkBean) {
			if(beanClass == null) {
				throw new IllegalArgumentException("Bean class object cannot be null");
			}
		}
		
		if(select == null || select.size() <= 0) {
			throw new IllegalArgumentException("Select Fields cannot be null or empty");
		}
		
		if(module == null) {
			if(moduleName == null || moduleName.isEmpty()) {
				throw new IllegalArgumentException("Both Module and Module Name cannot be empty");
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			module = modBean.getModule(moduleName);
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return builder.toString();
	}
	
	public static class JoinRecordBuilder<E extends ModuleBaseWithCustomFields> implements JoinBuilderIfc<SelectRecordsBuilder<E>> {
		private SelectRecordsBuilder<E> parentBuilder;
		private GenericJoinBuilder joinBuilder;
		
		private JoinRecordBuilder(SelectRecordsBuilder<E> parentBuilder, GenericJoinBuilder joinBuilder) {
			this.parentBuilder = parentBuilder;
			this.joinBuilder = joinBuilder;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public SelectRecordsBuilder<E> on(String condition) {
			// TODO Auto-generated method stub
			joinBuilder.on(condition);
			return parentBuilder;
		}
		
	}
}
