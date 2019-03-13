package com.facilio.bmsconsole.modules;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.JoinBuilderIfc;
import com.facilio.sql.SelectBuilderIfc;
import com.facilio.sql.WhereBuilder;

public class SelectRecordsBuilder<E extends ModuleBaseWithCustomFields> implements SelectBuilderIfc<E> {
	
	private static final Logger LOGGER = LogManager.getLogger(SelectRecordsBuilder.class.getName());
	private static final int LEVEL = 0;
	
	private GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
	private Class<E> beanClass;
	private Collection<FacilioField> select;
	private int level = 0;
	private int maxLevel = LEVEL;
	private String moduleName;
	private FacilioModule module;
	private boolean fetchDeleted = false;
	private String groupBy;
	private WhereBuilder where = new WhereBuilder();
	private StringBuilder joinBuilder = new StringBuilder();
	private boolean isAggregation = false;
	private List<LookupFieldMeta> fetchLookup = new ArrayList<>();
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
		this.joinBuilder = selectBuilder.joinBuilder;
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
		return new JoinRecordBuilder<E>(this);
	}
	
	@Override
	public JoinRecordBuilder<E> rightJoin(String tableName) {
		joinBuilder.append(" RIGHT JOIN ")
				.append(tableName)
				.append(" ");
		return new JoinRecordBuilder<E>(this);
	}
	
	@Override
	public JoinRecordBuilder<E> fullJoin(String tableName) {
		joinBuilder.append(" FULL JOIN ")
				.append(tableName)
				.append(" ");
		return new JoinRecordBuilder<E>(this);
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
	
	public SelectRecordsBuilder<E> setAggregation() {
		isAggregation = true;
		return this;
	}
	
	@Override
	public SelectRecordsBuilder<E> forUpdate() {
		this.builder.forUpdate();
		return this;
	}
	
	public SelectRecordsBuilder<E> fetchLookup(LookupFieldMeta field) {
		this.fetchLookup.add(field);
		return this;
	}
	
	public SelectRecordsBuilder<E> fetchLookups(Collection<LookupFieldMeta> fields) {
		this.fetchLookup.addAll(fields);
		return this;
	}
	
	@Override
	public List<E> get() throws Exception {
		checkForNull(true);
		long getStartTime = System.currentTimeMillis();
		List<Map<String, Object>> propList = getAsJustProps(false);
		long getTimeTaken = System.currentTimeMillis() - getStartTime;
		LOGGER.debug("Time Taken to get props in SelectBuilder : "+getTimeTaken);
		
		long startTime = System.currentTimeMillis();
		List<E> beans = FieldUtil.getAsBeanListFromMapList(propList, beanClass);
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time Taken to convert to bean list in SelectBuilder : "+timeTaken);
		return beans;
	}
	
	public Map<Long, E> getAsMap() throws Exception {
		checkForNull(true);
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
	
	private Map<String, LookupField> getLookupFields() {
		Map<String, LookupField> lookupFields = new HashMap<>();
		for(FacilioField field : select) {
			if(field.getDataTypeEnum() == FieldType.LOOKUP) {
				lookupFields.put(field.getName(), (LookupField) field);
			}
		}
		return lookupFields;
	}
	
	private List<Map<String, Object>> getAsJustProps(boolean isMap) throws Exception {
		FacilioField orgIdField = FieldFactory.getOrgIdField(module);
		FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
		FacilioField siteIdField = FieldFactory.getSiteIdField(module);
		
		long currentSiteId = AccountUtil.getCurrentSiteId();

		Set<FacilioField> selectFields = new HashSet<>();
		if (!isAggregation) {
			selectFields.add(orgIdField);
			selectFields.add(moduleIdField);
		}
		
		if (FieldUtil.isSiteIdFieldPresent(module) && (currentSiteId > 0 || (groupBy == null || groupBy.isEmpty()) ) && !isAggregation) {
			selectFields.add(siteIdField);
		}
		
		if ((groupBy == null || groupBy.isEmpty()) && !isAggregation) {
			selectFields.add(FieldFactory.getIdField(module));
		}
		else {
			StringBuilder moduleGroupBy = new StringBuilder();

			moduleGroupBy.append(orgIdField.getCompleteColumnName())
							.append(",")
							.append(moduleIdField.getCompleteColumnName())
							.append(",");

			if (FieldUtil.isSiteIdFieldPresent(module) && currentSiteId > 0) {
				moduleGroupBy.append(siteIdField.getCompleteColumnName())
					.append(",");
			}
			moduleGroupBy.append(groupBy);

//			moduleGroupBy.append(moduleIdField.getCompleteColumnName())
//						.append(",")
//						.append(groupBy);

			
			builder.groupBy(moduleGroupBy.toString());
		}
		
		selectFields.addAll(select);
		
		if (!fetchLookup.isEmpty()) {
			selectFields.addAll(fetchLookup);
		}
		
		builder.select(selectFields);
		
		WhereBuilder whereCondition = new WhereBuilder();
		Condition orgCondition = CriteriaAPI.getCondition(orgIdField, String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS);
		whereCondition.andCondition(orgCondition);
		
		Condition moduleCondition = CriteriaAPI.getCondition(moduleIdField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS);
		whereCondition.andCondition(moduleCondition);

		if (FieldUtil.isSiteIdFieldPresent(module) && currentSiteId > 0) {
			Condition siteCondition = CriteriaAPI.getCondition(siteIdField, String.valueOf(currentSiteId), NumberOperators.EQUALS);
			whereCondition.andCondition(siteCondition);
		}
		
		if (module.isTrashEnabled()) {
			FacilioField isDeletedField = FieldFactory.getIsDeletedField(module.getParentModule());
			
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
		
		builder.getJoinBuilder().append(joinBuilder.toString());
		
		builder.andCustomWhere(whereCondition.getWhereClause(), whereCondition.getValues());
		List<Map<String, Object>> props = builder.get();
		handleLookup(props, isMap);
		return props;
	}
	
	private void handleLookup (List<Map<String, Object>> propList, boolean isMap) throws Exception {
		if(propList != null && propList.size() > 0) {
			Map<String, LookupField> lookupFields = getLookupFields();
			if(lookupFields.size() > 0) {
				Map<String, LookupFieldMeta> lookups = fetchLookup.isEmpty() ? Collections.EMPTY_MAP : fetchLookup.stream().collect(Collectors.toMap(LookupFieldMeta::getName, Function.identity()));
				lookupFields.putAll(lookups);
				Map<String, Set<Long>> lookupIds = new HashMap<>();
				for(Map<String, Object> props : propList) {
					for(LookupField lookupField : lookupFields.values()) {
						Long recordId = (Long) props.get(lookupField.getName());
						if (recordId != null) {
							if(level < maxLevel || lookupField instanceof LookupFieldMeta) {
								addToLookupIds(lookupField, recordId, lookupIds);
							}
							else {
								Object val = isMap ? FieldUtil.getEmptyLookedUpProp(recordId) : FieldUtil.getEmptyLookupVal((LookupField) lookupField, recordId);
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
								if(level < maxLevel || lookupField instanceof LookupFieldMeta) {
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
}
