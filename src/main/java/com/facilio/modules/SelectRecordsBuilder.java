package com.facilio.modules;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.db.criteria.operators.CommonOperators;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.JoinBuilderIfc;
import com.facilio.db.builder.SelectBuilderIfc;
import com.facilio.db.builder.WhereBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class SelectRecordsBuilder<E extends ModuleBaseWithCustomFields> implements SelectBuilderIfc<E> {
	
	private static final Logger LOGGER = LogManager.getLogger(SelectRecordsBuilder.class.getName());
	private static final int LEVEL = 0;
	
	private GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
	private Class<E> beanClass;
	private Collection<FacilioField> select;
	private List<FacilioField> aggrFields = null;
	private List<LookupField> fetchLookup = null;
	private int level = 0;
	private int maxLevel = LEVEL;
	private String moduleName;
	private FacilioModule module;
	private boolean fetchDeleted = false;
	private String groupBy;
	private WhereBuilder where = new WhereBuilder();
	private StringBuilder joinBuilder = new StringBuilder();
	private boolean isAggregation = false;
	private Map<String, LookupField> lookupFields;
	private boolean includeNullOrgAndModule;

	private Set<String> criteriaJoinTables;
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
		this.isAggregation = selectBuilder.isAggregation;
		
		if (selectBuilder.builder != null) {
			this.builder = new GenericSelectRecordBuilder(selectBuilder.builder);
		}
		if (selectBuilder.select != null) {
			this.select = new ArrayList<>(selectBuilder.select);
		}
		if (selectBuilder.where != null) {
			this.where = new WhereBuilder(selectBuilder.where);
		}
		this.joinBuilder = new StringBuilder(selectBuilder.joinBuilder);
		this.includeNullOrgAndModule = selectBuilder.includeNullOrgAndModule;
	}
	
	public SelectRecordsBuilder (int level) {
		this.level = level;
	}
	
	@Override
	public SelectRecordsBuilder<E> select(Collection<FacilioField> selectFields) {
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
	public SelectRecordsBuilder<E> useExternalConnection (Connection conn) {
		builder.useExternalConnection(conn);
		return this;
	}
	
	@Override
	public JoinRecordBuilder<E> innerJoin(String tableName) {
		joinBuilder.append(" INNER JOIN ")
				.append(tableName)
				.append(" ");
		return new JoinRecordBuilder<E>(this);
	}
	
	@Override
	public JoinRecordBuilder<E> leftJoin(String tableName) {
		joinBuilder.append(" LEFT JOIN ")
				.append(tableName)
				.append(" ");
		addCriteriaTableName(tableName);
		includeNullOrgAndModule = true;
//		builder.includeNullOrgAndModule();
		return new JoinRecordBuilder<E>(this);
	}
	
	@Override
	public JoinRecordBuilder<E> rightJoin(String tableName) {
		joinBuilder.append(" RIGHT JOIN ")
				.append(tableName)
				.append(" ");
		addCriteriaTableName(tableName);
		includeNullOrgAndModule = true;
//		builder.includeNullOrgAndModule();
		return new JoinRecordBuilder<E>(this);
	}
	
	@Override
	public JoinRecordBuilder<E> fullJoin(String tableName) {
		joinBuilder.append(" FULL JOIN ")
				.append(tableName)
				.append(" ");
		addCriteriaTableName(tableName);
//		builder.includeNullOrgAndModule();
		includeNullOrgAndModule = true;
		return new JoinRecordBuilder<E>(this);
	}
	
	@Override
	@Deprecated
	public SelectRecordsBuilder<E> andCustomWhere(String where, Object... values) {
		this.where.andCustomWhere(where, values);
		return this;
	}
	
	@Override
	@Deprecated
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
		this.isAggregation = true;
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
	
	public SelectRecordsBuilder<E> setAggregation() {
		isAggregation = true;
		return this;
	}
	
	@Override
	public SelectRecordsBuilder<E> forUpdate() {
		this.builder.forUpdate();
		return this;
	}
	
	public SelectRecordsBuilder<E> fetchLookup(LookupField field) {
		if (fetchLookup == null) {
			fetchLookup = new ArrayList<>();
		}
		fetchLookup.add(field);
		return this;
	}
	
	public SelectRecordsBuilder<E> fetchLookups(Collection<? extends  LookupField> fields) {
		if (fetchLookup == null) {
			fetchLookup = new ArrayList<>();
		}
		this.fetchLookup.addAll(fields);
		return this;
	}

	private void addCriteriaTableName(String tableName) {
		if (criteriaJoinTables == null) {
			criteriaJoinTables = new HashSet<>();
		}
		criteriaJoinTables.add(tableName);
	}

	@Override
	public SelectRecordsBuilder<E> aggregate (AggregateOperator aggr, FacilioField field) throws Exception {
		FacilioField aggrField = aggr.getSelectField(field);
		if (aggrFields == null) {
			aggrFields = new ArrayList<>();
		}
		aggrFields.add(aggrField);
		isAggregation = true;
		return this;
	}
	
	@Override
	public List<E> get() throws Exception {
		checkForNull(true);
		try {
			long getStartTime = System.currentTimeMillis();
			List<Map<String, Object>> propList = getAsJustProps(false);
			long getTimeTaken = System.currentTimeMillis() - getStartTime;
			LOGGER.debug("Time Taken to get props in SelectBuilder : "+getTimeTaken);
			
			long startTime = System.currentTimeMillis();
			List<E> beans = FieldUtil.getAsBeanListFromMapList(propList, beanClass);
			if (MapUtils.isNotEmpty(lookupFields) && CollectionUtils.isNotEmpty(beans)) {
				for (E bean : beans) {
					Map<String, Object> data = bean.getData();
					if (MapUtils.isNotEmpty(data)) {
						for (String lookupName : lookupFields.keySet()) {
							Map<String, Object> map = (Map<String, Object>) data.get(lookupName);
							if (map != null) {
								LookupField lookupField = lookupFields.get(lookupName);
								if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
									Object lookupObject = LookupSpecialTypeUtil.getEmptyLookedupObject(lookupField.getSpecialType(), -1);
									data.put(lookupName, FieldUtil.getAsBeanFromMap(map, lookupObject.getClass()));
								}
								else {
									Class classFromModule = FacilioConstants.ContextNames.getClassFromModule(lookupField.getLookupModule());
									data.put(lookupName, FieldUtil.getAsBeanFromMap(map, classFromModule));
								}
							}
						}
					}
				}
			}
			long timeTaken = System.currentTimeMillis() - startTime;
			LOGGER.debug("Time Taken to convert to bean list in SelectBuilder : "+timeTaken);
			return beans;
		}
		catch (Exception e) {
			LOGGER.error("Error occurred in selecting records for module : "+module);
			throw e;
		}
	}
	
	public Map<Long, E> getAsMap() throws Exception {
		checkForNull(true);
		try {
			List<Map<String, Object>> propList = getAsJustProps(false);
			Map<Long, E> beanMap = new HashMap<>();
			if(propList != null && propList.size() > 0) {
				for(Map<String, Object> props : propList) {
					E bean = FieldUtil.getAsBeanFromMap(props, beanClass);
					beanMap.put(bean.getId(), bean);
				}
			}
			return beanMap;
		}
		catch (Exception e) {
			LOGGER.error("Error occurred in selecting records as map for module : "+module);
			throw e;
		}
	}
	
	public Map<Long, Map<String, Object>> getAsMapProps() throws Exception {
		checkForNull(false);
		List<Map<String, Object>> propList = getAsJustProps(true);
		
		Map<Long, Map<String, Object>> mapProps = new HashMap<>();
		if(propList != null && propList.size() > 0) {
			for(Map<String, Object> props : propList) {
				mapProps.put((Long) props.get("id"), props);
			}
		}
		return mapProps;
	}
	
	public List<Map<String, Object>> getAsProps() throws Exception {
		checkForNull(false);
		List<Map<String, Object>> propList = getAsJustProps(true);
		return propList;
	}
	
	private Map<String, LookupField> getLookupFields(Collection<FacilioField> selectFields) {
		Map<String, LookupField> lookupFields = new HashMap<>();
		for(FacilioField field : selectFields) {
			if(field.getDataTypeEnum() == FieldType.LOOKUP) {
				lookupFields.put(field.getName(), (LookupField) field);
			}
		}
		return lookupFields;
	}

	private Set<FacilioField> computeFields(FacilioField orgIdField, FacilioField moduleIdField, FacilioField siteIdField, List<FacilioField> deleteFields) {
		Set<FacilioField> selectFields = new HashSet<>();
		if (!isAggregation) {
			selectFields.add(orgIdField);
			selectFields.add(moduleIdField);
			if (FieldUtil.isSiteIdFieldPresent(module)) {
				selectFields.add(siteIdField);
			}
			selectFields.add(FieldFactory.getIdField(module));
			if (FieldUtil.isSystemFieldsPresent(module)) {
				selectFields.addAll(FieldFactory.getSystemFields(module));
			}
			if (FieldUtil.isBaseEntityRootModule(module)) {
				selectFields.addAll(FieldFactory.getBaseModuleSystemFields(module));
			}
			if (module.isTrashEnabled()) {
				selectFields.addAll(deleteFields);
			}
		}

		if (CollectionUtils.isNotEmpty(select)) {
			selectFields.addAll(select);
		}
		if (CollectionUtils.isNotEmpty(fetchLookup)) {
			selectFields.addAll(fetchLookup);
		}
		if (CollectionUtils.isNotEmpty(aggrFields)) {
			selectFields.addAll(aggrFields);
		}
		return selectFields;
	}

	private WhereBuilder computeWhere (FacilioField orgIdField, FacilioField moduleIdField, FacilioField siteIdField, FacilioField isDeletedField, boolean includeNullOrgAndModule) {
		WhereBuilder whereCondition = new WhereBuilder();
		Condition orgCondition = CriteriaAPI.getCondition(orgIdField, String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS);
		if (includeNullOrgAndModule) {
			Criteria c = new Criteria();
			c.addOrCondition(orgCondition);
			c.addOrCondition(CriteriaAPI.getCondition(orgIdField, CommonOperators.IS_EMPTY));
			whereCondition.andCriteria(c);
		}
		else {
			whereCondition.andCondition(orgCondition);
		}

		Condition moduleCondition = CriteriaAPI.getCondition(moduleIdField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS);
		if (includeNullOrgAndModule) {
			Criteria c = new Criteria();
			c.addOrCondition(moduleCondition);
			c.addOrCondition(CriteriaAPI.getCondition(moduleIdField, CommonOperators.IS_EMPTY));
			whereCondition.andCriteria(c);
		}
		else {
			whereCondition.andCondition(moduleCondition);
		}

		long currentSiteId = AccountUtil.getCurrentSiteId();
		if (FieldUtil.isSiteIdFieldPresent(module) && currentSiteId > 0) {
			Condition siteCondition = CriteriaAPI.getCondition(siteIdField, String.valueOf(currentSiteId), NumberOperators.EQUALS);
			whereCondition.andCondition(siteCondition);
		}

		if (module.isTrashEnabled() && !fetchDeleted) {
//			if (isDeletedField != null) {
			whereCondition.andCondition(CriteriaAPI.getCondition(isDeletedField, String.valueOf(false), BooleanOperators.IS));
//			} else {
//				whereCondition.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", String.valueOf(false), BooleanOperators.IS));
//			}
		}

		if (CollectionUtils.isNotEmpty(criteriaJoinTables)) {
			for (String tableName : criteriaJoinTables) {
				Criteria c = new Criteria();
				String orgIdColumnName = tableName + ".ORGID";
				c.addOrCondition(CriteriaAPI.getCondition(orgIdColumnName, "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS));
				c.addOrCondition(CriteriaAPI.getCondition(orgIdColumnName, "orgId", "", CommonOperators.IS_EMPTY));
				whereCondition.andCriteria(c);
			}
		}

		whereCondition.andCustomWhere(where.getWhereClause(), where.getValues());
		return whereCondition;
	}

	private void handlePermissionAndScope() {
		if (AccountUtil.getCurrentUser() != null) {
			if (module.getName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {
				Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(module.getName());
				if(scopeCriteria != null) {
					builder.andCriteria(scopeCriteria);
				}

				if (AccountUtil.getCurrentAccount().getUser().getUserType() != 2 && AccountUtil.getCurrentUser().getRole() != null) {
					Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria(module.getName(),"read");
					if(permissionCriteria != null) {
						builder.andCriteria(permissionCriteria);
					}
				}
			}
		}
	}
	
	private List<Map<String, Object>> getAsJustProps(boolean isMap) throws Exception {
		FacilioField orgIdField = AccountConstants.getOrgIdField(module);
		FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
		FacilioField siteIdField = null;
		if (FieldUtil.isSiteIdFieldPresent(module)) {
			siteIdField = FieldFactory.getSiteIdField(module);
		}

		List<FacilioField> deleteFields = null;
		FacilioField isDeletedField = null;
		if (module.isTrashEnabled()) {
			deleteFields = new ArrayList<>();
			FacilioModule parentModule = module.getParentModule();
			isDeletedField = FieldFactory.getIsDeletedField(parentModule);
			deleteFields.add(isDeletedField);
			deleteFields.add(FieldFactory.getSysDeletedTimeField(parentModule));
			deleteFields.add(FieldFactory.getSysDeletedByField(parentModule));
		}
		
		Set<FacilioField> selectFields = computeFields(orgIdField, moduleIdField, siteIdField, deleteFields);
		builder.select(selectFields);

		builder.groupBy(groupBy);

		builder.table(module.getTableName());

		WhereBuilder whereCondition = computeWhere(orgIdField, moduleIdField, siteIdField, isDeletedField, includeNullOrgAndModule);
		builder.andCustomWhere(whereCondition.getWhereClause(), whereCondition.getValues());
		handlePermissionAndScope();
		
		FacilioModule prevModule = module;
		FacilioModule extendedModule = module.getExtendModule();
		while(extendedModule != null) {
			builder.innerJoin(extendedModule.getTableName())
					.on(prevModule.getTableName()+".ID = "+extendedModule.getTableName()+".ID");
			prevModule = extendedModule;
			extendedModule = extendedModule.getExtendModule();
		}
		builder.getJoinBuilder().append(joinBuilder.toString());
		
		List<Map<String, Object>> props = builder.get();
		handleLookup(selectFields, props, isMap);
		return props;
	}
	
	private void handleLookup (Collection<FacilioField> selectFields, List<Map<String, Object>> propList, boolean isMap) throws Exception {
		if(propList != null && propList.size() > 0) {
			lookupFields = getLookupFields(selectFields);
			if(lookupFields.size() > 0) {
				Map<String, LookupField> lookups = CollectionUtils.isEmpty(fetchLookup) ? Collections.EMPTY_MAP : fetchLookup.stream().collect(Collectors.toMap(LookupField::getName, Function.identity()));
				lookupFields.putAll(lookups);
				Map<String, Set<Long>> lookupIds = new HashMap<>();
				for(Map<String, Object> props : propList) {
					for(LookupField lookupField : lookupFields.values()) {
						Long recordId = (Long) props.get(lookupField.getName());
						if (recordId != null) {
							if(level < maxLevel || lookups.containsKey(lookupField.getName())) {
								addToLookupIds(lookupField, recordId, lookupIds);
							}
							else {
								Object val = isMap || !lookupField.isDefault() ? FieldUtil.getEmptyLookedUpProp(recordId) : FieldUtil.getEmptyLookupVal((LookupField) lookupField, recordId);
								props.put(lookupField.getName(), val);
							}
						}
					}
				}
				
				if (!lookupIds.isEmpty()) {
					Map<String, Map<Long, ? extends Object>> lookedUpVals = new HashMap<>();
					for (Map.Entry<String, Set<Long>> entry : lookupIds.entrySet()) {
						lookedUpVals.put(entry.getKey(), FieldUtil.getLookupProps(lookupFields.get(entry.getKey()), entry.getValue(), isMap, level + 1));
					}
					
					for(Map<String, Object> props : propList) {
						for(String fieldName : lookupIds.keySet()) {
							LookupField lookupField = lookupFields.get(fieldName);
							Long recordId = (Long) props.get(lookupField.getName());
							if (recordId != null) {
								if(level < maxLevel || lookups.containsKey(lookupField.getName())) {
									props.put(lookupField.getName(), getLookupVal(lookupField, recordId, lookedUpVals));
								}
							}
						}
					}
				}
			}
		}
	}
	
	private Object getLookupVal (LookupField field, long recordId, Map<String, Map<Long, ? extends Object>> lookedUpVals) {
		Map<Long, ? extends Object> valueMap = lookedUpVals.get(field.getName());
		if (valueMap != null) {
			return valueMap.get(recordId);
		}
		else {
			LOGGER.info("Lookup val map for field : "+field.getName()+" is null. This is not supposed to happen");
		}
		return null;
	}
	
	private void addToLookupIds (LookupField field, long recordId, Map<String, Set<Long>> lookupIds) {
		String key = field.getName();
		Set<Long> ids = lookupIds.get(key);
		if (ids == null) {
			ids = new HashSet<>();
			lookupIds.put(key, ids);
		}
		ids.add(recordId);
	}
	
	private void checkForNull(boolean checkBean) throws Exception {
		if(checkBean) {
			if(beanClass == null) {
				throw new IllegalArgumentException("Bean class object cannot be null");
			}
		}
		
		if(CollectionUtils.isEmpty(select) && CollectionUtils.isEmpty(aggrFields) && CollectionUtils.isEmpty(fetchLookup)) {
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
//		private GenericJoinBuilder joinBuilder;
		
		private JoinRecordBuilder(SelectRecordsBuilder<E> parentBuilder) {
			this.parentBuilder = parentBuilder;
//			this.joinBuilder = joinBuilder;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public SelectRecordsBuilder<E> on(String condition) {
			// TODO Auto-generated method stub
			parentBuilder.joinBuilder.append("ON ")
				.append(condition)
				.append(" ");
			return parentBuilder;
		}
		
	}

	public E fetchFirst() throws Exception {
		List<E> list = get();
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}
}
