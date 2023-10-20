package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.apiv3.APIv3Config;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.*;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FetchSupplementHandler;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectRecordsBuilder<E extends ModuleBaseWithCustomFields> implements SelectBuilderIfc<E> {

	private static final int DEFAULT_LIMIT = 5000;
	private static final Logger LOGGER = LogManager.getLogger(SelectRecordsBuilder.class.getName());
	
	private GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
	private Class<E> beanClass;
	private Collection<FacilioField> select;
	private Set<FacilioField> actualSelectFields;
	private List<FacilioField> aggrFields = null;
	private List<LookupField> fetchLookup = null;
	private List<SupplementRecord> fetchSupplements;
	private String moduleName;
	private FacilioModule module;
	private boolean fetchDeleted = false;
	private String groupBy;
	private WhereBuilder where = new WhereBuilder();
	private StringBuilder joinBuilder = new StringBuilder();
	private boolean isAggregation = false;
	private Collection<LookupField> lookupFields;
	private int limit = -1;
	private ScopeHandler.ScopeFieldsAndCriteria scopeFieldsAndCriteria;
	private boolean skipModuleCriteria = false;
	private boolean skipScopeCriteria = false;

	private boolean skipPermission;
	private Collection<FacilioModule> joinModules;


	private Set<String> criteriaJoinTables;
	//Need where condition builder for custom field
	
	public SelectRecordsBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public SelectRecordsBuilder (SelectRecordsBuilder<E> selectBuilder) { //Do not call after calling getProps
		this.beanClass = selectBuilder.beanClass;
		this.moduleName = selectBuilder.moduleName;
		this.module = selectBuilder.module;
		this.fetchDeleted = selectBuilder.fetchDeleted;
		this.groupBy = selectBuilder.groupBy;
		this.isAggregation = selectBuilder.isAggregation;
		this.limit = selectBuilder.limit;
		
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

		this.skipPermission = selectBuilder.skipPermission;
		this.joinModules = selectBuilder.joinModules;
		if (selectBuilder.aggrFields != null) {
			this.aggrFields = new ArrayList<>(selectBuilder.aggrFields);
		}
		this.skipModuleCriteria = selectBuilder.skipModuleCriteria;
		this.skipScopeCriteria = selectBuilder.skipScopeCriteria;
	}
	
	@Override
	public SelectRecordsBuilder<E> select(Collection<FacilioField> selectFields) {
		this.select = selectFields;
		return this;
	}

	@Deprecated
	public SelectRecordsBuilder<E> maxLevel(int maxLevel) {
//		this.maxLevel = maxLevel;
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> table(String tableName) {
		return this;
	}

	@Override
	public SelectBuilderIfc<E> tableAlias(String tableAlias) {
		throw new UnsupportedOperationException("This is not supported here");
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

	public JoinRecordBuilder<E> innerJoinQuery(String sql, String alias) {
		joinBuilder.append(" INNER JOIN (")
				.append(sql)
				.append(") AS " + alias)
				.append(" ");
		return new JoinRecordBuilder<E>(this);
	}
	
	@Override
	public JoinRecordBuilder<E> leftJoin(String tableName) {
		joinBuilder.append(" LEFT JOIN ")
				.append(tableName)
				.append(" ");
		addCriteriaTableName(tableName);
		return new JoinRecordBuilder<E>(this);
	}

	public JoinRecordBuilder<E> leftJoinQuery(String sql, String alias) {
		joinBuilder.append(" LEFT JOIN (")
				.append(sql)
				.append(") AS " + alias)
				.append(" ");
		return new JoinRecordBuilder<E>(this);
	}
	
	@Override
	public JoinRecordBuilder<E> rightJoin(String tableName) {
		joinBuilder.append(" RIGHT JOIN ")
				.append(tableName)
				.append(" ");
		addCriteriaTableName(tableName);
		return new JoinRecordBuilder<E>(this);
	}
	
	@Override
	public JoinRecordBuilder<E> fullJoin(String tableName) {
		joinBuilder.append(" FULL JOIN ")
				.append(tableName)
				.append(" ");
		addCriteriaTableName(tableName);
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
		this.limit = limit;
		builder.limit(this.limit);
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

	public SelectRecordsBuilder<E> skipPermission() {
		this.skipPermission = true;
		return this;
	}

	@Override
	public SelectRecordsBuilder<E> skipUnitConversion() {
		this.builder.skipUnitConversion();
		return this;
	}

	@Override
	public SelectBuilderIfc<E> indexHint(String indexHint) {
		this.builder.indexHint(indexHint);
		return this;
	}

	public SelectRecordsBuilder<E> skipModuleCriteria() {
		this.skipModuleCriteria = true;
		return this;
	}

	public SelectRecordsBuilder<E> skipScopeCriteria() {
		this.skipScopeCriteria = true;
		return this;
	}
	
	public SelectRecordsBuilder<E> fetchSupplement(SupplementRecord supplement) {
		Objects.requireNonNull(supplement, "Supplement record cannot be null");
		if (fetchSupplements == null) {
			fetchSupplements = new ArrayList<>();
		}
		fetchSupplements.add(supplement);
		return this;
	}
	
	public SelectRecordsBuilder<E> fetchSupplements(Collection<? extends SupplementRecord> supplements) {
		Objects.requireNonNull(supplements, "Supplement records cannot be null");
		FacilioUtil.throwIllegalArgumentException(supplements.stream().anyMatch(Objects::isNull), "Supplement record cannot be null");
		if (fetchSupplements == null) {
			fetchSupplements = new ArrayList<>();
		}
		fetchSupplements.addAll(supplements);
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

	private List<E> convertPropsToBeans(List<Map<String, Object>> propList) throws Exception {
		long startTime = System.currentTimeMillis();
		List<E> beans = FieldUtil.getAsBeanListFromMapList(propList, beanClass);
		//FieldUtil.getAsBeanListFromMapList(propList, beanClass);
		if (CollectionUtils.isNotEmpty(lookupFields) && CollectionUtils.isNotEmpty(beans)) {
			for (E bean : beans) {
				Map<String, Object> data = bean.getData();
				if (MapUtils.isNotEmpty(data)) {
					for (LookupField lookupField : lookupFields) {
						String lookupName = lookupField.getName();
						Map<String, Object> map = (Map<String, Object>) data.get(lookupName);
						if (map != null) {
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
		LOGGER.trace("Time Taken to convert to bean list in SelectBuilder : "+timeTaken);
		return beans;
	}
	
	@Override
	public List<E> get() throws Exception {
		checkForNullAndSanitize(false);
		try {
			long getStartTime = System.currentTimeMillis();
			List<Map<String, Object>> propList = getAsJustProps(false);
			long getTimeTaken = System.currentTimeMillis() - getStartTime;
			LOGGER.trace("Time Taken to get props in SelectBuilder : "+getTimeTaken);
			return convertPropsToBeans(propList);
		}
		catch (Exception e) {
			LOGGER.error("Error occurred in selecting records for module : "+module);
			throw e;
		}
	}

	@Override
	public BatchResult<E> getInBatches(String orderBy, int batchSize) throws Exception {
		checkForNullAndSanitize(false);
		constructBuilderProps(false);
		return new BatchResult<>(this, builder.getInBatches(orderBy, batchSize), false);
	}

	public BatchResult<Map<String, Object>> getAsPropsInBatches(String orderBy, int batchSize) throws Exception {
		checkForNullAndSanitize(true);
		constructBuilderProps(false);
		return new BatchResult<>(this, builder.getInBatches(orderBy, batchSize), true);
	}

	private Map<Long, E> convertPropsToBeanMap(List<Map<String, Object>> propList) throws Exception {
		Map<Long, E> beanMap = new HashMap<>();
		if(propList != null && propList.size() > 0) {
			for(Map<String, Object> props : propList) {
				E bean = FieldUtil.getAsBeanFromMap(props, beanClass);
				beanMap.put(bean.getId(), bean);
			}
		}
		return beanMap;
	}

	public Map<Long, E> getAsMap() throws Exception {
		checkForNullAndSanitize(false);
		try {
			List<Map<String, Object>> propList = getAsJustProps(false);
			return convertPropsToBeanMap(propList);
		}
		catch (Exception e) {
			LOGGER.error("Error occurred in selecting records as map for module : "+module);
			throw e;
		}
	}

	private Map<Long, Map<String, Object>> convertPropsToMapProps(List<Map<String, Object>> propList) {
		Map<Long, Map<String, Object>> mapProps = new HashMap<>();
		if(propList != null && propList.size() > 0) {
			for(Map<String, Object> props : propList) {
				mapProps.put((Long) props.get("id"), props);
			}
		}
		return mapProps;
	}

	public Map<Long, Map<String, Object>> getAsMapProps() throws Exception {
		checkForNullAndSanitize(true);
		try {
			List<Map<String, Object>> propList = getAsJustProps(true);
			return convertPropsToMapProps(propList);
		}
		catch (Exception e) {
			LOGGER.error("Error occurred in selecting records as map props for module : "+module);
			throw e;
		}
	}
	
	public List<Map<String, Object>> getAsProps() throws Exception {
		checkForNullAndSanitize(true);
		try {
			List<Map<String, Object>> propList = getAsJustProps(true);
			return propList;
		}
		catch (Exception e) {
			LOGGER.error("Error occurred in selecting records as props for module : "+module);
			throw e;
		}
	}

	private Set<FacilioField> computeFields(FacilioField orgIdField, FacilioField moduleIdField, List<FacilioField> deleteFields, FacilioModule extendedModule) {
		Set<FacilioField> selectFields = new HashSet<>();
		if (!isAggregation) {
			selectFields.add(orgIdField);
			selectFields.add(moduleIdField);
			selectFields.add(FieldFactory.getIdField(module));
			if (FieldUtil.isSystemFieldsPresent(module)) {
				selectFields.addAll(FieldFactory.getSystemPointFields(module.getParentModule()));
			}
			if (module.getTypeEnum() == FacilioModule.ModuleType.BASE_ENTITY) {
				FacilioModule rootModule = module;
				if (extendedModule != null) {
					rootModule = extendedModule;
				}
				selectFields.addAll(FieldFactory.getBaseModuleSystemFields(rootModule));
			}
			if (module.isTrashEnabled()) {
				selectFields.addAll(deleteFields);
			}

			if (scopeFieldsAndCriteria != null && CollectionUtils.isNotEmpty(scopeFieldsAndCriteria.getFields())) {
				selectFields.addAll(scopeFieldsAndCriteria.getFields());
			}
			
			if (FieldUtil.isSiteIdFieldPresent(module)) {
				selectFields.add(FieldFactory.getSiteIdField(module));
			}
		}

		if (CollectionUtils.isNotEmpty(select)) {
			selectFields.addAll(FieldUtil.removeMultiRecordFields(select));
		}
		if (CollectionUtils.isNotEmpty(fetchLookup)) {
			selectFields.addAll(fetchLookup);
		}
		if (CollectionUtils.isNotEmpty(fetchSupplements)) {
			for (SupplementRecord extra : fetchSupplements) {
				if (extra.selectField() != null) {
					selectFields.add(extra.selectField());
				}
			}
		}
		if (CollectionUtils.isNotEmpty(aggrFields)) {
			selectFields.addAll(aggrFields);
		}
		return selectFields;
	}

	private WhereBuilder computeWhere (FacilioField orgIdField, FacilioField moduleIdField, FacilioField isDeletedField) {
		WhereBuilder whereCondition = new WhereBuilder();
		Condition orgCondition = CriteriaAPI.getCondition(orgIdField, String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS);
		whereCondition.andCondition(orgCondition);

		Condition moduleCondition = CriteriaAPI.getCondition(moduleIdField, String.valueOf(module.getModuleId()), NumberOperators.EQUALS);
		whereCondition.andCondition(moduleCondition);

		// Doing this separately for extended module to avoid unnecessary field creation
		long commonModuleId = module.hideFromParents() ? module.getModuleId() : -1;
		FacilioModule extendedModule = module.getExtendModule();
		while (extendedModule != null) {
			if (commonModuleId == -1 && extendedModule.hideFromParents()) {
				commonModuleId = extendedModule.getModuleId();
			}

			whereCondition.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), extendedModule));
			whereCondition.andCondition(CriteriaAPI.getModuleIdIdCondition(commonModuleId == -1 ? extendedModule.getModuleId() : commonModuleId, extendedModule));

			extendedModule = extendedModule.getExtendModule();
		}

		if (module.isTrashEnabled() && !fetchDeleted) {
			whereCondition.andCondition(CriteriaAPI.getCondition(isDeletedField, String.valueOf(false), BooleanOperators.IS));
		}

		if (scopeFieldsAndCriteria != null && scopeFieldsAndCriteria.getCriteria() != null && !scopeFieldsAndCriteria.getCriteria().isEmpty()) {
			whereCondition.andCriteria(scopeFieldsAndCriteria.getCriteria());
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

		if (module.getCriteria() != null && !skipModuleCriteria) {
			whereCondition.andCriteria(module.getCriteria());
		}

		whereCondition.andCustomWhere(where.getWhereClause(), where.getValues());
		return whereCondition;
	}

	private void handlePermissionAndScope() {
		if (!skipPermission) {
			if (AccountUtil.getCurrentUser() != null) {
				if (module.getName().equals(FacilioConstants.ContextNames.WORK_ORDER)) {
					Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(module.getName());
					if (scopeCriteria != null) {
						builder.andCriteria(scopeCriteria);
					}

					if (AccountUtil.getCurrentUser().getAppDomain() != null && AccountUtil.getCurrentUser().getAppDomain().getAppDomainTypeEnum() == AppDomainType.FACILIO && AccountUtil.getCurrentUser().getRole() != null) {
						Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria(module.getName(), "read");
						if (permissionCriteria != null) {
							builder.andCriteria(permissionCriteria);
						}
					}
				}
			}
		}
	}

	void populateExtras (List<Map<String, Object>> props, boolean isMap) throws Exception {
		if (CollectionUtils.isNotEmpty(props)) {
			handleLookup(props, isMap);
			handleSupplements(props, isMap);
		}
	}
	
	private List<Map<String, Object>> getAsJustProps(boolean isMap) throws Exception {
		constructBuilderProps(false);
		List<Map<String, Object>> props = builder.get();
		populateExtras(props, isMap);
		return props;
	}

	private void handleLookup (List<Map<String, Object>> props, boolean isMap) {
		lookupFields = actualSelectFields.stream().filter(f -> f.getDataTypeEnum() == FieldType.LOOKUP).map(f -> (LookupField)f).collect(Collectors.toSet());
		if (CollectionUtils.isNotEmpty(lookupFields)) {
			for (Map<String, Object> record : props) {
				for (LookupField field : lookupFields) {
					try {
						Long recordId = (Long) record.get(field.getName());
						if (recordId != null) {
							Map<String, Object> lookupRecord = null;
							if (isMap) {
								lookupRecord = new HashMap<>();
								lookupRecord.put("id", recordId);
							} else {
								lookupRecord = Collections.singletonMap("id", recordId);
							}
							record.put(field.getName(), lookupRecord);
						}
					}
					catch (Exception e) {
						LOGGER.error(MessageFormat.format("Error occurred during lookup computation of field : {0}\nLookup Fields : {1}", field, lookupFields), e);
						throw e;
					}
				}
			}
		}
	}

	public String constructQueryString() throws Exception {
		constructBuilderProps(true);
		return builder.constructSelectStatement();
	}

	@Override
	public Object[] paramValues() {
		return builder.paramValues();
	}

	private boolean queryConstructed = false;
	private void constructBuilderProps(boolean isJustQueryConstruction) throws Exception {
		if (!queryConstructed) {
			queryConstructed = true;
			FacilioField orgIdField = AccountConstants.getOrgIdField(module);
			FacilioField moduleIdField = FieldFactory.getModuleIdField(module);
			List<FacilioField> deleteFields = null;
			FacilioField isDeletedField = null;
			if (module.isTrashEnabled()) {
				deleteFields = new ArrayList<>();
				FacilioModule parentModule = module.getParentModule();
				isDeletedField = FieldFactory.getIsDeletedField(parentModule);
				deleteFields.add(isDeletedField);
				deleteFields.add(FieldFactory.getSysDeletedTimeField(parentModule));
				if(V3RecordAPI.markAsDeleteEnabled(parentModule)) {
					deleteFields.add(FieldFactory.getSysDeletedPeopleByField(parentModule));
				} else {
					deleteFields.add(FieldFactory.getSysDeletedByField(parentModule));
				}
			}
			
			FacilioModule prevModule = module;
			FacilioModule extendedModule = module.getExtendModule();
			while (extendedModule != null) {
				addJoinModules(Collections.singletonList(extendedModule));
				builder.innerJoin(extendedModule.getTableName())
						.on(prevModule.getTableName() + ".ID = " + extendedModule.getTableName() + ".ID");
				prevModule = extendedModule;
				extendedModule = extendedModule.getExtendModule();
			}

			if(!skipScopeCriteria) {
				scopeFieldsAndCriteria = ScopeHandler.getInstance().getFieldsAndCriteriaForSelect(module, joinModules);
			}

			Set<FacilioField> selectFields = computeFields(orgIdField, moduleIdField, deleteFields, extendedModule);
			builder.select(selectFields);

			builder.groupBy(groupBy);

			builder.table(module.getTableName());

			if (!isJustQueryConstruction && limit == -1 && !module.getTypeEnum().isReadingType()) {
				builder.limit(DEFAULT_LIMIT);
			}

			WhereBuilder whereCondition = computeWhere(orgIdField, moduleIdField, isDeletedField);
			builder.andCustomWhere(whereCondition.getWhereClause(), whereCondition.getValues());
			handlePermissionAndScope();

			builder.getJoinBuilder().append(joinBuilder.toString());
			actualSelectFields = selectFields;

			if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 405 && module.getTableName().equals("Readings_3")) {
				builder.indexHint("USE INDEX (READINGS_3_PARENT_ID_TTIME_INDEX)");
			}
		}
		else {
			throw new IllegalArgumentException("Query already constructed");
		}
	}

	private void handleSupplements(List<Map<String, Object>> propList, boolean isMap) throws Exception {
		if (CollectionUtils.isNotEmpty(fetchSupplements)) {
			List<Pair<SupplementRecord, FetchSupplementHandler>> handlers = new ArrayList<>();
			for (SupplementRecord fetchExtra : fetchSupplements) {
				FetchSupplementHandler handler = fetchExtra.newFetchHandler();
				if (handler != null) {
					handlers.add(Pair.of(fetchExtra, handler));
					for (Map<String, Object> props : propList) {
						handler.processRecord(props);
					}
				}
			}

			for (Pair<SupplementRecord, FetchSupplementHandler> handler : handlers) {
				handler.getRight().fetchSupplements(isMap);
			}

			for (Pair<SupplementRecord, FetchSupplementHandler> handler : handlers) {
				for(Map<String, Object> props : propList) {
					handler.getRight().updateRecordWithSupplement(props);
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
	
	private void checkForNullAndSanitize(boolean isProps) throws Exception {
		if(!isProps) {
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

		fetchSupplements(SupplementRecord.filterSystemSupplement(select));
		if (fetchSupplements != null) {
			fetchSupplements = fetchSupplements.stream().filter(SupplementRecord.distinctSupplementRecord()).collect(Collectors.toList());
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return builder.toString();
	}

	public static class BatchResult<E> implements BatchResultIfc<E> {
		private SelectRecordsBuilder parentBuilder;
		private GenericSelectRecordBuilder.GenericBatchResult bs;
		private boolean isProps;

		private BatchResult(SelectRecordsBuilder parentBuilder, GenericSelectRecordBuilder.GenericBatchResult bs, boolean isProps) {
			this.parentBuilder = parentBuilder;
			this.bs = bs;
			this.isProps = isProps;
		}

		@Override
		public boolean hasNext() throws Exception {
			return bs.hasNext();
		}

		private List<Map<String, Object>> getAsJustProps() throws Exception {
			List<Map<String, Object>> props = bs.get();
			parentBuilder.populateExtras(props, isProps);
			return props;
		}

		@Override
		public List<E> get() throws Exception {
			try {
				List<Map<String, Object>> props = getAsJustProps();
				if (isProps) {
					return (List<E>) props;
				} else {
					return (List<E>) parentBuilder.convertPropsToBeans(props);
				}
			}
			catch (Exception e) {
				LOGGER.error("Error occurred in selecting records" + (isProps ? " (as props)" : "") + " in batches for module : " + parentBuilder.module);
				throw e;
			}
		}

		public Map<Long, E> getAsMap() throws Exception {
			try {
				List<Map<String, Object>> props = getAsJustProps();
				if (isProps) {
					return (Map<Long, E>) parentBuilder.convertPropsToMapProps(props);
				} else {
					return (Map<Long, E>) parentBuilder.convertPropsToBeanMap(props);
				}
			}
			catch (Exception e) {
				LOGGER.error("Error occurred in selecting records" + (isProps ? " (as props)" : "") + " as map in batches for module : " + parentBuilder.module);
				throw e;
			}
		}
	}

	public static class JoinRecordBuilder<E extends ModuleBaseWithCustomFields> implements JoinBuilderIfc<SelectRecordsBuilder<E>> {
		private SelectRecordsBuilder<E> parentBuilder;
//		private GenericJoinBuilder joinBuilder;
		
		private JoinRecordBuilder(SelectRecordsBuilder<E> parentBuilder) {
			this.parentBuilder = parentBuilder;
//			this.joinBuilder = joinBuilder;
			// TODO Auto-generated constructor stub
		}
		
		/**
		 * set Alias for join table
		 */
		public JoinRecordBuilder<E> alias(String alias) {
			this.parentBuilder.joinBuilder.append(" ")
											.append(alias)
											.append(" ")
											;
			return this;
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

	public void addWhereValue(List<Object> values, int position) {
		builder.addWhereValue(values, position);
	}

	//will be an interface method once i check in in framework
	public void addJoinModules(Collection<FacilioModule> joinModules) {
		if(this.joinModules == null) {
			this.joinModules = new ArrayList<FacilioModule>();  
		}
		this.joinModules.addAll(joinModules);
	}
	
}
