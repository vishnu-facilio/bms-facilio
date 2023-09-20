package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentUtil;
import com.facilio.agent.FacilioAgent;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ControlActionMode;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder.BatchUpdateByIdContext;
import com.facilio.db.builder.GenericUpdateRecordBuilder.BatchUpdateContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;
import com.facilio.time.SecondsChronoUnit;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.WorkflowUtil;

public class ReadingsAPI {
	private static final Logger LOGGER = LogManager.getLogger(ReadingsAPI.class.getName());
	public static final int DEFAULT_DATA_INTERVAL = 15; //In Minutes
	public static final SecondsChronoUnit DEFAULT_DATA_INTERVAL_UNIT = new SecondsChronoUnit(DEFAULT_DATA_INTERVAL * 60); 
	private static final int BATCH_SIZE =2000;
	
	public static final String FORMULA_FIELD_TABLE_NAME = "Formula_Readings";	
	
	public static int getOrgDefaultDataIntervalInMin() throws Exception {
		Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		String defaultIntervalProp = orgInfo.get(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		if (defaultIntervalProp == null || defaultIntervalProp.isEmpty()) {
			return ReadingsAPI.DEFAULT_DATA_INTERVAL;
		}
		else {
			return Integer.parseInt(defaultIntervalProp);
		}
	}
	
	public static ReadingInputType getRDMInputTypeFromModuleType(ModuleType type) {
		switch (type) {
			case SCHEDULED_FORMULA:
			case LIVE_FORMULA:
			case SYSTEM_SCHEDULED_FORMULA:
				return ReadingInputType.FORMULA_FIELD;
			case READING_RULE:
				return ReadingInputType.ALARM_POINT_FIELD;
			default:
				return ReadingInputType.WEB;
		}
	}
	
	public static FacilioModule getParentModuleRelFromChildModule(FacilioModule childModule) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getSubModuleRelFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getSubModulesRelModule().getTableName())
				.select(FieldFactory.getSubModuleRelFields())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("childModuleId"), childModule.getModuleId()+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props !=null && !props.isEmpty()) {
			Long parentModuleId = (Long) props.get(0).get("parentModuleId");
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			return modBean.getModule(parentModuleId);
		}
		return null;
	}
	
	public static int updateReadingDataMeta (ReadingDataMeta rdm) throws Exception {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField fieldIdField = fieldMap.get("fieldId");
		
		if (rdm.getReadingTypeEnum() != null && rdm.getReadingTypeEnum() == ReadingType.WRITE) {
			rdm.setIsControllable(true);
			rdm.setControlActionMode(ReadingDataMeta.ControlActionMode.LIVE.getIntVal());
		}
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(module.getTableName())
														.fields(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(rdm.getResourceId()), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(rdm.getFieldId()), PickListOperators.IS))
														;
		
		return updateBuilder.update(FieldUtil.getAsProperties(rdm));
	}
	
	// values should be available for all fields in fieldNamesToUpdate, else will be set as null
	public static int updateReadingDataMetaList (List<ReadingDataMeta> rdms, List<String> fieldNamesToUpdate) throws Exception {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField fieldIdField = fieldMap.get("fieldId");
		
		List<FacilioField> updateFields = new ArrayList<>();
		boolean shouldUpdateReadingType = false;
		for(String fieldName: fieldNamesToUpdate) {
			updateFields.add(fieldMap.get(fieldName));
			if (fieldName.equals("readingType")) {
				shouldUpdateReadingType = true;
				updateFields.add(fieldMap.get("isControllable"));
				updateFields.add(fieldMap.get("controlActionMode"));
			}
		}
		List<FacilioField> whereFields = new ArrayList<>();
		whereFields.add(resourceIdField);
		whereFields.add(fieldIdField);
		
		List<Map<String, Object>> props = FieldUtil.getAsMapList(rdms, ReadingDataMeta.class);
		List<BatchUpdateContext> batchUpdateList = new ArrayList<>();
		for (Map<String, Object> prop: props) {
			BatchUpdateContext updateVal = new BatchUpdateContext();
			
			if (shouldUpdateReadingType) {
				Integer readingType = (Integer) prop.get("readingType");
				if (readingType == null) {
					readingType = ReadingType.READ.getValue();
					prop.put("readingType", readingType);
				}
				setControllableprop(prop, readingType == ReadingType.WRITE.getValue());
				
				updateVal.addUpdateValue("isControllable",  prop.get("isControllable"));
				updateVal.addUpdateValue("controlActionMode",  prop.get("controlActionMode"));
			}
			
			for(String fieldName: fieldNamesToUpdate) {
				updateVal.addUpdateValue(fieldName,  prop.get(fieldName));
			}
			
			updateVal.addWhereValue("resourceId",prop.get("resourceId"));
			updateVal.addWhereValue("fieldId", prop.get("fieldId"));
			
			batchUpdateList.add(updateVal);
		}
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(module.getTableName())
														.fields(updateFields)
														;
		
		return updateBuilder.batchUpdate(whereFields, batchUpdateList);
	}
	
	public static int updateReadingDataMetaInputType (List<ReadingDataMeta> metaList, ReadingDataMeta.ReadingInputType type, ReadingType readingType) throws SQLException {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField fieldIdField = fieldMap.get("fieldId");
		
		Criteria pkCriteriaList = new Criteria();
		for (ReadingDataMeta meta : metaList) {
			Criteria pkCriteria = new Criteria();
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(meta.getResourceId()), PickListOperators.IS));
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(meta.getFieldId()), PickListOperators.IS));
			pkCriteriaList.orCriteria(pkCriteria);
		}
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("inputType", type.getValue());
		
		if (readingType == null) {
			readingType = ReadingType.READ;
		}
		prop.put("readingType", readingType.getValue());
		setControllableprop(prop, readingType == ReadingType.WRITE);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(module.getTableName())
														.fields(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCriteria(pkCriteriaList);
														
		return updateBuilder.update(prop);
	}
	
	private static void setControllableprop(Map<String, Object> prop, boolean isWritable) {
		if (isWritable) {
			prop.put("isControllable", true);
			prop.put("controlActionMode", ReadingDataMeta.ControlActionMode.LIVE.getIntVal());
		}
		else {
			prop.put("isControllable", false);
			prop.put("controlActionMode", null);
		}
	}
	
	public static int updateReadingDataMeta (List<Pair<Long, FacilioField>> resourceFieldPair, ReadingDataMeta rdm) throws Exception {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField fieldIdField = fieldMap.get("fieldId");
		
		Criteria pkCriteriaList = new Criteria();
		for (Pair<Long, FacilioField> pair : resourceFieldPair) {
			Criteria pkCriteria = new Criteria();
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(pair.getLeft()), PickListOperators.IS));
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(pair.getRight().getFieldId()), PickListOperators.IS));
			pkCriteriaList.orCriteria(pkCriteria);
		}
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCriteria(pkCriteriaList);
				
		return updateBuilder.update(FieldUtil.getAsProperties(rdm));
	}
	
	public static int updateReadingDataMeta (long parentId, List<Long> fieldIds, ReadingDataMeta rdm) throws Exception {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField fieldIdField = fieldMap.get("fieldId");
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(parentId), PickListOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldIdField, fieldIds, PickListOperators.IS));
				
		return updateBuilder.update(FieldUtil.getAsProperties(rdm));
	}
	
	public static ReadingDataMeta getReadingDataMeta(long resourceId,FacilioField field) throws Exception {
		List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
		rdmPairs.add(Pair.of(resourceId, field));
		List<ReadingDataMeta> readingMetaList = getReadingDataMetaList(rdmPairs);
		if (readingMetaList != null && !readingMetaList.isEmpty()) {
			return readingMetaList.get(0);
		}
		return null;
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(List<Pair<Long, FacilioField>> rdmPairs) throws Exception {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField fieldIdField = fieldMap.get("fieldId");
		
		Map<Long, FacilioField> fieldIdMap = new HashMap<>();
		Criteria pkCriteriaList = new Criteria();
		for (Pair<Long, FacilioField> pair : rdmPairs) {
			Criteria pkCriteria = new Criteria();
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(pair.getLeft()), PickListOperators.IS));
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(pair.getRight().getFieldId()), PickListOperators.IS));
			pkCriteriaList.orCriteria(pkCriteria);
			fieldIdMap.put(pair.getRight().getFieldId(), pair.getRight());
		}
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCriteria(pkCriteriaList);
		return getReadingDataFromProps(builder.get(), fieldIdMap);
	}
	
	public static ReadingDataMeta getReadingDataMeta(long id) throws Exception {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getIdCondition(id, module));
		List<Map<String, Object>> props = builder.get();
		if(props != null && !props.isEmpty()) {
			return getRDMFromProp(props.get(0), null);
		}
		return null;
	}
	
	public static Map<String, ReadingDataMeta> getReadingDataMetaMap(List<Pair<Long, FacilioField>> rdmPairs) throws Exception {
		
		if (rdmPairs == null || rdmPairs.isEmpty()) {
			return Collections.EMPTY_MAP;
		}
		
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField fieldIdField = fieldMap.get("fieldId");
		
		Map<Long, FacilioField> fieldIdMap = new HashMap<>();
		Criteria pkCriteriaList = new Criteria();
		for (Pair<Long, FacilioField> pair : rdmPairs) {
			Criteria pkCriteria = new Criteria();
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(pair.getLeft()), PickListOperators.IS));
			pkCriteria.addAndCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(pair.getRight().getFieldId()), PickListOperators.IS));
			pkCriteriaList.orCriteria(pkCriteria);
			fieldIdMap.put(pair.getRight().getFieldId(), pair.getRight());
		}
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCriteria(pkCriteriaList);
		List<Map<String, Object>>  res = builder.get();
		return getRDMMapFromProps(res, fieldIdMap);
	}
	
	public static long getReadingDataMetaCount(Long resourceId, boolean excludeEmptyFields, String search, ReadingInputType...inputTypes) throws Exception {
		return getReadingDataMetaCount(Collections.singletonList(resourceId), excludeEmptyFields, false, search, null, inputTypes);
	}
	
	public static long getReadingDataMetaCount(Collection<Long> resourceIds, boolean excludeEmptyFields, boolean unused, String search, ReadingType readingType, ReadingInputType...inputTypes) throws Exception {
		List<Map<String, Object>> props = getRDMProps(resourceIds, null, excludeEmptyFields, unused, true, null, search, readingType, inputTypes);
		if (props != null && !props.isEmpty()) {
			return (long) props.get(0).get("count");
		}
		return 0;
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Long resourceId, Collection<FacilioField> fieldList) throws Exception {
		return getReadingDataMetaList(resourceId, fieldList, false);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Long resourceId, Collection<FacilioField> fieldList, boolean excludeEmptyFields, ReadingInputType...inputTypes) throws Exception {
		return getReadingDataMetaList(resourceId != null && resourceId != -1 ? Collections.singletonList(resourceId) : null, fieldList, excludeEmptyFields, null, null, inputTypes);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Collection<Long> resourceIds, Collection<FacilioField> fieldList, boolean excludeEmptyFields, JSONObject pagination, String search, ReadingInputType...inputTypes) throws Exception {
		return getReadingDataMetaList(resourceIds, fieldList, excludeEmptyFields, pagination, search, null, inputTypes);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Collection<Long> resourceIds, Map<Long, FacilioField> fieldMap, boolean excludeEmptyFields, JSONObject pagination, String search, ReadingInputType...inputTypes) throws Exception {
		return getReadingDataMetaList(resourceIds, fieldMap, excludeEmptyFields, false, pagination, search, null, inputTypes);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Collection<Long> resourceIds, Collection<FacilioField> fieldList, boolean excludeEmptyFields, ReadingType readingType) throws Exception {
		return getReadingDataMetaList(resourceIds, fieldList, excludeEmptyFields, null, null, readingType);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Collection<Long> resourceIds, Collection<FacilioField> fieldList, boolean excludeEmptyFields, JSONObject pagination, String search, ReadingType readingType, ReadingInputType...inputTypes) throws Exception {
		Map<Long, FacilioField> fieldMap = null;
		if (fieldList != null) {
			fieldMap = FieldFactory.getAsIdMap(fieldList);
		}
		return getReadingDataMetaList(resourceIds, fieldMap, excludeEmptyFields, false, pagination, search, readingType, inputTypes);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Collection<Long> resourceIds, Map<Long, FacilioField> fieldMap, boolean excludeEmptyFields, boolean unused,JSONObject pagination, String search, ReadingType readingType, ReadingInputType...inputTypes) throws Exception {
		List<Map<String, Object>> stats = getRDMProps(resourceIds, fieldMap, excludeEmptyFields, unused, false, pagination, search, readingType, inputTypes);
		return getReadingDataFromProps(stats, fieldMap);
	}
	
	private static List<Map<String, Object>> getRDMProps (Collection<Long> resourceIds, Map<Long, FacilioField> fieldMap, boolean excludeEmptyFields, boolean unused, boolean fetchCount, JSONObject pagination, String search, ReadingType readingType, ReadingInputType...inputTypes) throws Exception {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> redingFields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> readingFieldsMap = FieldFactory.getAsMap(redingFields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		;
		
		if (fetchCount) {
			builder.select(FieldFactory.getCountField(module, readingFieldsMap.get("resourceId")));
		}
		else {
			builder.select(redingFields);
		}
				
		if (fieldMap != null) {
			builder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("fieldId"), StringUtils.join(fieldMap.keySet(), ","), NumberOperators.EQUALS));
		}
		
		if(resourceIds != null && !resourceIds.isEmpty()) {
			FacilioField resourceIdField = readingFieldsMap.get("resourceId");
			// LOGGER.debug("Resource Ids in getRDMProps : "+resourceIds+"\nResource Field : "+resourceIdField);
			builder.andCondition(CriteriaAPI.getCondition(resourceIdField, resourceIds, NumberOperators.EQUALS));
		}
		else if (inputTypes != null && inputTypes.length > 0) {
			builder.groupBy(readingFieldsMap.get("fieldId").getCompleteColumnName()).select(Collections.singletonList(readingFieldsMap.get("fieldId")));
		}
		
		Criteria valueCriteria = new Criteria();
		if (unused) {
			valueCriteria.addAndCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), "-1", StringOperators.IS));
		}
		else if (excludeEmptyFields) {
			valueCriteria.addAndCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), "-1", StringOperators.ISN_T));
			valueCriteria.addAndCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), CommonOperators.IS_NOT_EMPTY));
//			valueCriteria.addOrCondition(CriteriaAPI.getCondition(readingFieldsMap.get("custom"), String.valueOf(true), BooleanOperators.IS));
		}
		if (!valueCriteria.isEmpty()) {
			builder.andCriteria(valueCriteria);
		}
		
		if(readingType != null) {
//			builder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("readingType"), String.valueOf(readingType.getValue()), PickListOperators.IS));
			boolean isControllable = readingType == ReadingType.WRITE;
			builder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("isControllable"), String.valueOf(isControllable), BooleanOperators.IS));
		}
		
		if (inputTypes != null && inputTypes.length > 0) {
			builder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"), getReadingTypes(inputTypes), PickListOperators.IS));
		}
		else {
			builder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),String.valueOf(ReadingInputType.HIDDEN_FORMULA_FIELD.getValue()), PickListOperators.ISN_T));
		}
		
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");

			if (perPage != -1) {
				int offset = ((page-1) * perPage);
				if (offset < 0) {
					offset = 0;
				}
				builder.offset(offset);
				builder.limit(perPage);
			}
		}
		if (search != null) {
			// TODO
		}
		return builder.get();
	}
	
	public static List<ReadingDataMeta> getReadingDataFromProps(List<Map<String, Object>> props, Map<Long, FacilioField> fieldMap) throws Exception {
		if(props != null && !props.isEmpty()) {
			List<ReadingDataMeta> metaList = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				ReadingDataMeta meta = getRDMFromProp(prop, fieldMap);
				metaList.add(meta);
			}
			return metaList;
		}
		return null;
	}
	
	public static String getRDMKey(ReadingDataMeta meta) {
		return getRDMKey(meta.getResourceId(), meta.getField());
	}
	
	public static String getRDMKey(long resourceId, FacilioField field) {
		return getRDMKey(resourceId, field.getFieldId());
	}
	public static String getRDMKey(long resourceId, long fieldId) {
		return resourceId + "_" + fieldId;
	}
	
	private static Map<String, ReadingDataMeta> getRDMMapFromProps (List<Map<String, Object>> props, Map<Long, FacilioField> fieldMap) throws Exception {
		if(props != null && !props.isEmpty()) {
			Map<String, ReadingDataMeta> rdmMap = new HashMap<>();
			for (Map<String, Object> prop : props) {
				ReadingDataMeta meta = getRDMFromProp(prop, fieldMap);
				rdmMap.put(getRDMKey(meta.getResourceId(), meta.getField()), meta);
			}
			return rdmMap;
		}
		return null;
	}
	
	public static List<ReadingDataMeta> getControllableRDMs() throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReadingDataMetaFields())
				.table(ModuleFactory.getReadingDataMetaModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("isControllable"), Boolean.TRUE.toString(), BooleanOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<ReadingDataMeta> readingMetaList = getReadingDataFromProps(props, null);
		return readingMetaList;
	}
	
	private static ReadingDataMeta getRDMFromProp (Map<String, Object> prop, Map<Long, FacilioField> fieldMap) throws Exception {
		ReadingDataMeta meta = FieldUtil.getAsBeanFromMap(prop, ReadingDataMeta.class);
		fillExtendedPropertiesForRDM(meta, fieldMap);
		return meta;
	}
	
	public static void fillExtendedPropertiesForRDM(ReadingDataMeta meta,Map<Long, FacilioField> fieldMap) throws Exception {
		
		Object value = meta.getValue();
		meta.setActualValue((String) value);
		if (meta.isCustom() && meta.getActualValue().equals("-1")) {
			meta.setActualValue(null);
			meta.setValue(null);
//			meta.setTtime(-1);
		}
		FacilioField field;
		if (fieldMap != null) {
			 field = fieldMap.get(meta.getFieldId());
		}
		else {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			field = modBean.getField(meta.getFieldId());
		}
		if (field.getDataTypeEnum() == FieldType.ENUM) {
			if (value != null && value instanceof String) {
				value = Integer.valueOf((String) value);
			}
		}
		try {
			if ((AccountUtil.getCurrentOrg().getId() == 210l || AccountUtil.getCurrentOrg().getId() == 321l) && meta.getTtime() > System.currentTimeMillis()) {
				//int interval = getDataInterval(meta.getResourceId(), field);
				ZonedDateTime zdt = DateTimeUtil.getDateTime();
//				zdt = zdt.truncatedTo(new SecondsChronoUnit(interval * 60));
				zdt = zdt.truncatedTo(DEFAULT_DATA_INTERVAL_UNIT);
				meta.setTtime(DateTimeUtil.getMillis(zdt, true));
			}
		}
		catch(Exception e) {
			LOGGER.info("Error on future rdm date change", e);
		}
		meta.setValue(FacilioUtil.castOrParseValueAsPerType(field, value));
		meta.setField(field);
		
	}
	
	private static String getReadingTypes(ReadingInputType... types) {
		StringJoiner joiner = new StringJoiner(",");
		for (ReadingInputType type : types) {
			joiner.add(String.valueOf(type.getValue()));
		}
		return joiner.toString();
	}
	
	public static void loadReadingParent(Collection<ReadingContext> readings) throws Exception {
		if(readings != null && !readings.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_METER);
			
			try {
				SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
																				.select(fields)
																				.module(module)
																				.beanClass(EnergyMeterContext.class);
				
				Map<Long, EnergyMeterContext> parents = selectBuilder.getAsMap();
				
				for(ReadingContext reading : readings) {
					Long parentId = reading.getParentId();
					if(parentId != null) {
						reading.setParent(parents.get(parentId));
					}
				}
			}
			catch(Exception e) {
				LOGGER.error("Exception occurred ", e);
				throw e;
			}
		}
	}
	
	public static Map<String, Object> getInstanceMapping(String deviceName, String instanceName) throws Exception {
		return getInstanceMapping(deviceName, instanceName,null);
	}
	
	public static Map<String, Object> getInstanceMapping(String deviceName, String instanceName,Long controllerId) throws Exception {

		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCustomWhere("ORGID=?",orgId)
				.andCustomWhere("DEVICE_NAME= ?",deviceName)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), CommonOperators.IS_NOT_EMPTY))
				.andCustomWhere("INSTANCE_NAME=?",instanceName);
		if(controllerId!=null) {
			builder=builder.andCustomWhere("CONTROLLER_ID=?", controllerId);
		}

		List<Map<String, Object>> stats = builder.get();	
		if(stats!=null && !stats.isEmpty()) {

			return stats.get(0); 
		}
		return null;
	}
	
	private static List<Map<String, Object>> getDeviceVsInstances() throws Exception {

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), CommonOperators.IS_NOT_EMPTY))
				.andCustomWhere("ORGID=?",orgId);

		List<Map<String, Object>> stats = builder.get();	
		if(stats!=null && !stats.isEmpty()) {

			return stats; 
		}
		return null;
	}
	
	public static Map<String, Map<String,Map<String,Object>>> getDeviceMapping() throws Exception {
		
		List<Map<String, Object>> deviceVsInstances= getDeviceVsInstances();
		if(deviceVsInstances==null) {
			return null;
		}
		//deviceName: Instancename:stats
		Map<String, Map<String,Map<String,Object>>> deviceMapping=new HashMap<String, Map<String,Map<String,Object>>>();
		
		for(Map<String, Object> data: deviceVsInstances) {
			
			String deviceName=(String)data.remove("device");
			String instanceName= (String)data.remove("instance");
			Map<String,Map<String,Object>> instanceMapping= deviceMapping.get(deviceName);
			if(instanceMapping==null) {
				instanceMapping=new HashMap<String,Map<String,Object>>();
				deviceMapping.put(deviceName, instanceMapping);
			}
			instanceMapping.put(instanceName, data);
		}
		return deviceMapping;
		
	}
	
	public static List<Map<String, Object>> getUnmodeledData(List<String> deviceList) throws Exception {

		List<FacilioField> fields= getUnmodeledFields();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getPointsModule().getTableName())
				.innerJoin(ModuleFactory.getUnmodeledDataModule().getTableName())
				.on("Unmodeled_Data.INSTANCE_ID=Points.ID")
				.andCustomWhere("ORGID=?",orgId)
				.andCondition(CriteriaAPI.getCondition("DEVICE_NAME", "device", StringUtils.join(deviceList,","), StringOperators.IS))
				.orderBy("TTIME");

		List<Map<String, Object>> stats = builder.get();	
		if(stats!=null && !stats.isEmpty()) {
			return stats; 
		}
		return null;
	}
	
	// This is used on update of a reading
	public static Map<String, ReadingDataMeta> updateReadingDataMeta(FacilioModule module,List<FacilioField> fields,ReadingContext reading,String fieldName)throws Exception{
		Map<String, ReadingDataMeta> rdmMap=new HashMap<>();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		Condition condition=CriteriaAPI.getCondition(fieldMap.get("ttime"), String.valueOf(reading.getTtime()),NumberOperators.GREATER_THAN);
		String orderBy=fieldMap.get("ttime").getColumnName() + " desc";
		ReadingContext lastReading = getSingleReading(module, fields, reading,fieldName,condition,orderBy);
		if (lastReading != null) {
			reading = lastReading;
		}
		if (reading != null) {
			ReadingDataMeta rdm = new ReadingDataMeta();
			rdm.setTtime(reading.getTtime());
			FacilioField fField = fieldMap.get(fieldName);
			if (fField != null) {
				Object val = FacilioUtil.castOrParseValueAsPerType(fField, reading.getReadings().entrySet().stream().filter(x->x.getKey().equalsIgnoreCase(fieldName)).findFirst().get().getValue());
				if (val != null) {
					rdm.setValue(val.toString());
					rdm.setReadingDataId(reading.getId());
					rdm.setResourceId(reading.getParentId());
					rdm.setFieldId(fField.getFieldId());
					updateReadingDataMeta(rdm);
					String uniqueKey = getRDMKey(rdm.getResourceId(), fField);
					rdmMap.put(uniqueKey, rdm);
				}
			}
		}
		return rdmMap;
	}

	public static ReadingContext getSingleReading(FacilioModule module, List<FacilioField> fields,ReadingContext reading, String fieldName,Condition condition,String orderBy)throws Exception{
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(fields)
				.module(module)
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(fieldName),CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(reading.getParentId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module),String.valueOf(reading.getId()),NumberOperators.NOT_EQUALS))
				.limit(1);
				if(condition!=null){
					selectBuilder.andCondition(condition);
				}
				if(orderBy!=null){
					selectBuilder.orderBy(orderBy);
				}
				selectBuilder.skipUnitConversion();
				
		return selectBuilder.fetchFirst();
	}
	
	public static ReadingContext getReading(FacilioModule module, List<FacilioField> fields,long id)throws Exception{
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
				.select(fields)
				.module(module)
				.beanClass(ReadingContext.class)
				.andCondition(CriteriaAPI.getIdCondition(id, module)).limit(1);
		return selectBuilder.fetchFirst();
	}

//	private static final int CURRENT_TIME_BUFFER = 5 * 60 * 1000; // Having 5 min buffer
	private static final int CURRENT_TIME_BUFFER = 9 * 60 * 1000; // 9 mins buffer for Temporary fix
	public static Map<String, ReadingDataMeta> updateReadingDataMeta(List<FacilioField> fieldsList,List<ReadingContext> readingList,Map<String, ReadingDataMeta> metaMap) throws SQLException {

		if(readingList == null || readingList.isEmpty()) {
			return null;
		}
//		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
		Map<String,FacilioField>  fieldMap = FieldFactory.getAsMap(fieldsList);
		Map<String, ReadingDataMeta> uniqueRDMs = new HashMap<>();
		for(ReadingContext readingContext:readingList) {
			long resourceId=readingContext.getParentId();
			long timeStamp=readingContext.getTtime();
			long readingId = readingContext.getId();
			Map<String,Object> readings=  readingContext.getReadings();
			for(Map.Entry<String, Object> reading :readings.entrySet()) {
				FacilioField fField = fieldMap.get(reading.getKey());
				if (fField != null) {
					Object val = FacilioUtil.castOrParseValueAsPerType(fField, reading.getValue());
//						if ((AccountUtil.getCurrentOrg().getId() == 104 && fField.getFieldId() == 490437) || (AccountUtil.getCurrentOrg().getId() == 134)) {
//							LOGGER.info("resourceId: " + resourceId + ", ttime: " + timeStamp + ", current: " + System.currentTimeMillis() + ", value: " + val);
//						}
					if (val != null) {
						long fieldId = fField.getFieldId();
						String uniqueKey = getRDMKey(resourceId, fField);
						if (metaMap != null) {
							ReadingDataMeta meta = metaMap.get(uniqueKey);
							if(meta != null)
							{
								Object lastReading = meta.getValue();
								long lastTimeStamp = meta.getTtime();
								long currentTime = System.currentTimeMillis();
								if (timeStamp > (currentTime + CURRENT_TIME_BUFFER)
										|| (lastReading != null
										&& lastTimeStamp != -1
										&& StringUtils.isNotEmpty(meta.getActualValue())
										&& !"-1".equals(meta.getActualValue())
										&& timeStamp < lastTimeStamp)) {

										LOGGER.debug("Not updating RDM: time" + timeStamp + ", current: " + currentTime + ", readingId: " + readingId + ", resourceId: "+ resourceId + ", fieldId: " +fieldId+ ", Meta: "+meta);

									continue;
								}
							}
						}
						String value = val.toString();

						ReadingDataMeta rdm = uniqueRDMs.get(uniqueKey);
						if (rdm == null || (rdm.getTtime() <= System.currentTimeMillis() && rdm.getTtime() < timeStamp) ) {
							rdm = new ReadingDataMeta();
							rdm.setFieldId(fieldId);
							rdm.setField(fField);
							rdm.setTtime(timeStamp);
							rdm.setValue(value);
							rdm.setReadingDataId(readingId);
							rdm.setResourceId(resourceId);

							uniqueRDMs.put(uniqueKey, rdm);
						}
					}
//						else {
//							LOGGER.debug("Not updating RDM for "+fField.getName()+" from "+readingContext+" because after parsing, value is null");
//						}
				}
			}
		}

//			LOGGER.debug("Unique RDMs : "+uniqueRDMs);
//			if (AccountUtil.getCurrentOrg().getOrgId() == 104 || AccountUtil.getCurrentOrg().getOrgId() == 134) {
//				LOGGER.info("Unique RDMs : "+uniqueRDMs);
//			}
		if (uniqueRDMs.size() == 0) {
			return null;
		}
//		caseUpdateRDM(uniqueRDMs.values());
		batchUpdateRDM(uniqueRDMs.values());
		return uniqueRDMs;
//		}
	}

	private static void batchUpdateRDM(Collection<ReadingDataMeta> uniqueRDMs) throws SQLException {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());
		List<FacilioField> updateFields = new ArrayList<>();
		updateFields.add(fieldMap.get("ttime"));
		updateFields.add(fieldMap.get("value"));
		updateFields.add(fieldMap.get("readingDataId"));

		List<FacilioField> whereFields = new ArrayList<>();
		whereFields.add(fieldMap.get("resourceId"));
		whereFields.add(fieldMap.get("fieldId"));

		List<GenericUpdateRecordBuilder.BatchUpdateContext> batchUpdateList = uniqueRDMs.stream().map(rdm -> {
			GenericUpdateRecordBuilder.BatchUpdateContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateContext();
			updateVal.addUpdateValue("ttime", rdm.getTtime());
			updateVal.addUpdateValue("value", rdm.getValue());
			updateVal.addUpdateValue("readingDataId", rdm.getReadingDataId());

			updateVal.addWhereValue("resourceId", rdm.getResourceId());
			updateVal.addWhereValue("fieldId", rdm.getFieldId());
			return updateVal;
		}).collect(Collectors.toList());

		new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(updateFields)
				.batchUpdate(whereFields, batchUpdateList)
				;
	}

	private static void caseUpdateRDM(Collection<ReadingDataMeta> uniqueRDMs) throws SQLException {
		StringBuilder timeBuilder= new StringBuilder();
		StringBuilder valueBuilder= new StringBuilder();
		StringBuilder idBuilder= new StringBuilder();
		StringJoiner whereClause = new StringJoiner(" OR ");
		int cycle = 0;
		long orgId=AccountUtil.getCurrentOrg().getOrgId();

		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			for (ReadingDataMeta rdm : uniqueRDMs) {
				timeBuilder.append(getCase(rdm.getResourceId(), rdm.getFieldId(), rdm.getTtime(), false));
				valueBuilder.append(getCase(rdm.getResourceId(), rdm.getFieldId(), rdm.getValue(), true));
				idBuilder.append(getCase(rdm.getResourceId(), rdm.getFieldId(), rdm.getReadingDataId(), false));

				StringBuilder builder = new StringBuilder();
				builder.append("(RESOURCE_ID = ")
						.append(rdm.getResourceId())
						.append(" AND FIELD_ID = ")
						.append(rdm.getFieldId())
						.append(")")
				;
				whereClause.add(builder.toString());
				cycle++;
				if (cycle == BATCH_SIZE) {
					updateRDM(timeBuilder, valueBuilder, idBuilder, whereClause, orgId, conn);
					timeBuilder = new StringBuilder();
					idBuilder = new StringBuilder();
					valueBuilder = new StringBuilder();
					whereClause = new StringJoiner("OR");
					cycle = 0;
				}
			}
			if (cycle > 0) {
				updateRDM(timeBuilder, valueBuilder, idBuilder, whereClause, orgId, conn);
			}
		}
	}
	
	private static void updateRDM(StringBuilder timeBuilder, StringBuilder valueBuilder, 
			StringBuilder idBuilder, StringJoiner whereClause, long orgId, Connection conn) throws SQLException{
		String sql = "UPDATE "+ModuleFactory.getReadingDataMetaModule().getTableName()+" SET TTIME = CASE "+timeBuilder.toString()+ " END, "
				+ "VALUE = CASE "+valueBuilder.toString() + " END, "
				+ "READING_DATA_ID = CASE "+idBuilder.toString() + " END "
				+ "WHERE ORGID = "+orgId+" AND ("+whereClause.toString()+")";
		
		if(sql != null && !sql.isEmpty()) {
			LOGGER.debug("################ Update RDM sql : "+sql);
			try(PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
				pstmt.executeUpdate();
			} catch(SQLException e) {
				LOGGER.error("Exception occurred ", e);
				throw new SQLException("Query failed : "+sql, e);
			}
			}
	}
	private static String getCase(long resource,long field, Object value, boolean insertQuote) {

		String caseString= " WHEN RESOURCE_ID = "+resource+" AND FIELD_ID = "+field+" THEN ";
		if(insertQuote) {
			return caseString+"'"+value+"'";
		}
		return caseString+value;
	}
	
	private static List<FacilioField> getUnmodeledFields() {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("device", "DEVICE_NAME",FieldType.STRING ));
		fields.add(FieldFactory.getField("instance", "INSTANCE_NAME",FieldType.STRING ));
		fields.add(FieldFactory.getField("ttime", "TTIME",FieldType.NUMBER ));
		fields.add(FieldFactory.getField("value", "VALUE",FieldType.STRING ));
		return fields;
	}
	
	private static final  List<String> DEFAULT_READING_FIELDS = Collections.unmodifiableList(getDefaultReadingFieldNames());
	public static final List<String> getDefaultReadingFieldNames() {
		List<String> fieldNames = new ArrayList<>();
		List<FacilioField> fields = FieldFactory.getDefaultReadingFields(null);
		for(FacilioField field : fields) {
			fieldNames.add(field.getName());
		}
		fieldNames.add("sysCreatedTime");
		return fieldNames;
	}

	public static final List<FacilioField> filterSystemFields(List<FacilioField> fields) {
		List<String> defaultReadingNames = getDefaultReadingFieldNames();
		return fields.stream()
				.filter(row ->  !defaultReadingNames.contains(row.getName()))
				.collect(Collectors.toList());
	}

	public static List<FacilioField> excludeDefaultAndEmptyReadingFields(List<FacilioField> fields,Long parentId, String filter, Boolean excludeEmptyFields,boolean fetchControllableFields) throws Exception {
		if (CollectionUtils.isEmpty(fields)) {
			return Collections.EMPTY_LIST;
		}
		if (parentId == null) {
			parentId = -1l;
		}
		List<Long> fieldsWithValues = null;
		
		List<ReadingInputType> inputTypes = new ArrayList<>();
		boolean isAvailableFilter = false;
		if (StringUtils.isNotEmpty(filter)) {
			if (filter.equals("connected")) {
				inputTypes.add(ReadingInputType.CONTROLLER_MAPPED);
			}
			else if (filter.equals("formula")) {
				inputTypes.add(ReadingInputType.FORMULA_FIELD);
			}
			else if (filter.equals("available")) {
				isAvailableFilter = true;
				inputTypes.add(ReadingInputType.FORMULA_FIELD);
				inputTypes.add(ReadingInputType.CONTROLLER_MAPPED);
			}
		}
		ReadingInputType[] types = null;
		if (!inputTypes.isEmpty()) {
			types = inputTypes.toArray(new ReadingInputType[inputTypes.size()]);
		}
		if (parentId > -1 || types != null || excludeEmptyFields || fetchControllableFields) {
			List<ReadingDataMeta> readingMetaDatas = null;
			if(fetchControllableFields) {
				readingMetaDatas = getReadingDataMetaList(parentId != null && parentId != -1 ? Collections.singletonList(parentId) : null, fields, excludeEmptyFields,null,null,ReadingType.WRITE, types);
			}
			else {
				readingMetaDatas = getReadingDataMetaList(parentId > 0 ? parentId : null, fields, excludeEmptyFields, types);
			}
			if (readingMetaDatas != null) {
				fieldsWithValues = readingMetaDatas.stream().map(meta -> meta.getFieldId()).collect(Collectors.toList());
			}
		}
		List<FacilioField> fieldsToReturn = new ArrayList<>();
		for(FacilioField field: fields) {
			if (!DEFAULT_READING_FIELDS.contains(field.getName()) ) {
				if (parentId == -1 && types == null && !excludeEmptyFields) {
					fieldsToReturn.add(field);
				}
				else if (fieldsWithValues != null && fieldsWithValues.contains(field.getId())) {
					if (!isAvailableFilter) {
						fieldsToReturn.add(field);
					}
				}
				else if (isAvailableFilter) {
					fieldsToReturn.add(field);
				}
				
		    }
	    }
		return fieldsToReturn;
	}
	
	public static void updateReadingDataMeta(Long assetCategoryId, List<Long> readingModuleIds) throws Exception {

		LOGGER.info("RDM assetCategoryId -- "+assetCategoryId);
		List<ResourceContext> resourcesList = new ArrayList<ResourceContext>();
		if(assetCategoryId == null || assetCategoryId == -1) {
			resourcesList= ResourceAPI.getAllResources();
		}
		else {
			List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(assetCategoryId);
			for(AssetContext asset:assets) {
				resourcesList.add((ResourceContext)asset);
			}
			LOGGER.error("RDM assets -- "+assets);
		}
		LOGGER.info("RDM resources list  -- "+resourcesList);
		updateReadingDataMeta(resourcesList, readingModuleIds);
	}
	
	public static void updateReadingDataMeta(List<ResourceContext> resourcesList) throws Exception {
		updateReadingDataMeta(resourcesList, null);
	}
	
	
	public static void updateReadingDataMetaForMeters(List<V3MeterContext> meterList) throws Exception {
		
		if (meterList == null || meterList.isEmpty()) {
			return;
		}
		
		long orgId=AccountUtil.getCurrentOrg().getOrgId();
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReadingDataMetaModule().getTableName())
				.fields(FieldFactory.getReadingDataMetaFields());
		
		LOGGER.error("meterList size -- "+meterList.size());
		int i=0;
		for(V3MeterContext meter:meterList) {
			
			LOGGER.error(++i +" RDM update running for V3MeterContext -- "+meter.getId());
			
			List<FacilioModule>	finalModuleList= new ArrayList<FacilioModule>();
			
			long meterId = meter.getId();
			
			FacilioContext context = new FacilioContext();
			
			context.put(FacilioConstants.Meter.PARENT_UTILITY_TYPE_ID, meter.getUtilityType().getId());
			FacilioChain getCategoryReadingChain = FacilioChainFactory.getUtilityTypeReadingsChain();
			getCategoryReadingChain.execute(context);

			finalModuleList = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			
			
			if(finalModuleList==null || finalModuleList.isEmpty()) {
				continue;
			}
			for(FacilioModule module:finalModuleList) {
				
				List<FacilioField> fieldList= module.getFields();
				for(FacilioField field:fieldList) {
					
					ReadingDataMeta oldRdm=  getReadingDataMeta(meterId,field);
					if(oldRdm!=null) {
						continue;
					}
					ReadingDataMeta rdm= new ReadingDataMeta();
					rdm.setOrgId(orgId);
					rdm.setFieldId(field.getFieldId());
					rdm.setField(field);
					rdm.setValue("-1");
					rdm.setResourceId(meterId);
					rdm.setTtime(System.currentTimeMillis());
					rdm.setInputType(ReadingInputType.WEB);
					rdm.setCustom(!field.isDefault());
					builder.addRecord(FieldUtil.getAsProperties(rdm));
					LOGGER.info("new rdm entry : " + rdm);
				}
			}
		}
		builder.save();
	}
	
	
	public static void updateReadingDataMeta(List<ResourceContext> resourcesList, List<Long> readingModuleIds) throws Exception {
		
		if (resourcesList == null || resourcesList.isEmpty()) {
			return;
		}
		
		long orgId=AccountUtil.getCurrentOrg().getOrgId();
		
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getReadingDataMetaModule().getTableName())
				.fields(FieldFactory.getReadingDataMetaFields());
		LOGGER.error("resourcesList size -- "+resourcesList.size());
		int i=0;
		for(ResourceContext resource:resourcesList) {
			
			LOGGER.error(++i +" RDM update running for resource -- "+resource.getId());
			List<FacilioModule>	moduleList=null;
			int resourceType = resource.getResourceType();
			long resourceId = resource.getId();
			FacilioContext context = new FacilioContext();
					
			if(resourceType==ResourceContext.ResourceType.SPACE.getValue()) {
				context.put(FacilioConstants.ContextNames.PARENT_ID, resourceId);
				FacilioChain getSpaceSpecifcReadingsChain = FacilioChainFactory.getSpaceReadingsChain();
				getSpaceSpecifcReadingsChain.execute(context);
				moduleList = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			}
			else if(resourceType==ResourceContext.ResourceType.ASSET.getValue()) {
				context.put(FacilioConstants.ContextNames.PARENT_ID, resourceId);
				FacilioChain getSpaceSpecifcReadingsChain = FacilioChainFactory.getAssetReadingsChain();
				getSpaceSpecifcReadingsChain.execute(context);
				moduleList = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			}
			
			List<FacilioModule>	finalModuleList= new ArrayList<FacilioModule>();
			if(readingModuleIds!= null && !readingModuleIds.isEmpty()) {
				for(FacilioModule module: moduleList) {
					if(readingModuleIds.contains(module.getModuleId())) { //filtering user-given modules from allsubmodules
						finalModuleList.add(module);
					}
				}	
			}
			else {
				finalModuleList = moduleList;
			}
			
			if(finalModuleList==null || finalModuleList.isEmpty()) {
				continue;
			}
			for(FacilioModule module:finalModuleList) {
				
				List<FacilioField> fieldList= module.getFields();
				for(FacilioField field:fieldList) {
					
					ReadingDataMeta oldRdm=  getReadingDataMeta(resourceId,field);
					if(oldRdm!=null) {
						continue;
					}
					ReadingDataMeta rdm= new ReadingDataMeta();
					rdm.setOrgId(orgId);
					rdm.setFieldId(field.getFieldId());
					rdm.setField(field);
					rdm.setValue("-1");
					rdm.setResourceId(resourceId);
					rdm.setTtime(System.currentTimeMillis());
					rdm.setInputType(ReadingInputType.WEB);
					rdm.setCustom(!field.isDefault());
					builder.addRecord(FieldUtil.getAsProperties(rdm));
					LOGGER.info("new rdm entry : " + rdm);
				}
			}
		}
		builder.save();
	}
	public static int getDataInterval(long resourceId, FacilioField field, FacilioModule... module) throws Exception { //Return in minutes	
		ReadingContext readingContext = new ReadingContext();
		readingContext.setParentId(resourceId);
		FacilioModule facilioModule = (module != null && module.length > 0) ? module[0] : field.getModule();
		readingContext.setModuleId(facilioModule.getModuleId());
		if (field != null) {
			readingContext.addReading(field.getName(), null);
		}
		Map<Long,FacilioModule> moduleMap = new HashMap<>();
		moduleMap.put(facilioModule.getModuleId(), facilioModule);
		setReadingInterval(Collections.singletonList(readingContext), moduleMap);
		return readingContext.getDataInterval();
	}
	
	public static int getDataInterval(List<WorkflowFieldContext> wFields) throws Exception {
		return getDataInterval(null, wFields);
	}
	
	public static int getDataInterval(WorkflowContext workflow, List<WorkflowFieldContext>... wFields) throws Exception {
		int dataInterval = DEFAULT_DATA_INTERVAL;
		if (workflow.getExpressions() == null) {
			WorkflowUtil.getWorkflowContextFromString(workflow.getWorkflowString(),workflow);
		}
		List<WorkflowFieldContext> workflowFields = wFields != null && wFields.length == 1 ? wFields[0] : WorkflowUtil.getWorkflowField(workflow);
		if (workflowFields != null) {
			List<ReadingContext> readings = new ArrayList<>();
			for(WorkflowFieldContext field: workflowFields) {
				if (field.getResourceId() != -1) {
					ReadingContext reading = new ReadingContext();
					reading.setModuleId(field.getModuleId());
					reading.setParentId(field.getResourceId());
					readings.add(reading);
					if (field.getField() != null && field.getField().getName() != null) {
						reading.addReading(field.getField().getName(), null);
					}
				}
			}
			if (!readings.isEmpty()) {
				setReadingInterval(readings, null);
				return readings.stream().mapToInt(reading -> reading.getDataInterval()).max().getAsInt();
			}
		}
		return dataInterval;
	}
	
	public static void setReadingInterval(Map<String, List<ReadingContext>> readingMap) throws Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Map<Long,FacilioModule> moduleMap = null;
		List<ReadingContext> readingsList = new ArrayList<>();
		for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
			String moduleName = entry.getKey();
			List<ReadingContext> readings = entry.getValue();
			FacilioModule module = bean.getModule(moduleName);
			if (moduleMap == null) {
				moduleMap = new HashMap<>();
			}
			moduleMap.put(module.getModuleId(), module);
			readings.forEach(reading -> reading.setModuleId(module.getModuleId()));
			readingsList.addAll(readings);
		}
		setReadingInterval(readingsList, moduleMap);
	}
	
	public static void setReadingInterval(List<ReadingContext> readings, Map<Long, FacilioModule> moduleMap) throws Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Set<Pair<Long, Long>> pairs = new HashSet<>();
		Map<String, Map<String, FacilioField>> allFieldsMap = new HashMap<>();
		List<ReadingContext> remainingReadings = new ArrayList<>();
		for(ReadingContext reading: readings) {
			Map<String, Object> readingMap = reading.getReadings();
			if (readingMap != null) {
				FacilioModule module = moduleMap != null ? moduleMap.get(reading.getModuleId()) : bean.getModule(reading.getModuleId());
				if (module.getDataInterval() != -1) {
					reading.setDataInterval(module.getDataInterval());
					continue;
				}
				remainingReadings.add(reading);
				
				Map<String, FacilioField> fieldMap = null;
				if (!allFieldsMap.containsKey(module.getName())) {
					List<FacilioField> fields = bean.getAllFields(module.getName());
					if (fields != null) {
						fieldMap = new HashMap<>(FieldFactory.getAsMap(fields));
					}
					allFieldsMap.put(module.getName(), fieldMap);						
				}
				else {
					fieldMap = allFieldsMap.get(module.getName());
				}
				for(Map.Entry<String, Object> entry :readingMap.entrySet()) {
					String name = entry.getKey();
					if (fieldMap != null && fieldMap.get(name) != null) {
						pairs.add(Pair.of(reading.getParentId(), fieldMap.get(name).getFieldId()));
					}
				}
			}
		}
		
		if (remainingReadings.isEmpty()) {
			return;
		}
		
		// Assuming data interval will be set for all readingcontexts or none
		if (remainingReadings.get(0).getDataInterval() <= 0) {
			
			// temp check till pd7 agents are migrated
			if (AccountUtil.getCurrentOrg().getOrgId() == 146l) {
				setOldAgentDataInterval(pairs, remainingReadings);
				return;
			}
			
			int defaultInterval = getDefaultInterval();
			remainingReadings.forEach(reading -> {
				reading.setDataInterval(defaultInterval);
			});
		}
	}
	
	private static void setOldAgentDataInterval(Set<Pair<Long, Long>> pairs, List<ReadingContext> readings) throws Exception {
		int defaultInterval = getDefaultInterval();
		List<Map<String, Object>> instances = TimeSeriesAPI.getMappedInstances(pairs);
		Map<Long, Long> assetVsControllerIdMap = new HashMap<>();
		Set<Long> controllerIds = new HashSet<>();
		if (instances != null && !instances.isEmpty()) {
			instances.forEach(inst -> {
				Long controllerId = (Long) inst.get(FacilioConstants.ContextNames.CONTROLLER_ID);
				if (controllerId != null && controllerId != -1) {
					assetVsControllerIdMap.put((Long) inst.get("resourceId"), controllerId);
					controllerIds.add(controllerId);
				}
			});
		}
		
		
		Map<Long, ControllerContext> controllers = null;
		Set<Long> agentIds = new HashSet<>();
		if (controllerIds != null && !controllerIds.isEmpty()) {
			controllers = ControllerAPI.getControllersMap(controllerIds);
			for(Entry<Long, ControllerContext> entry: controllers.entrySet()){
				ControllerContext controller = entry.getValue();
				if (controller.getAgentId() > 0) {
					agentIds.add(controller.getAgentId());
				}
			}
		}
		
		Map<Long, FacilioAgent> agentsMap = null;
		if (CollectionUtils.isNotEmpty(agentIds)) {
			agentsMap = AgentUtil.getAgentsMap(agentIds);
		}

		
		for (ReadingContext reading : readings) {
			int minuteInterval = defaultInterval;
			if (controllers != null && assetVsControllerIdMap.get(reading.getParentId()) != null) {
				long controllerId = assetVsControllerIdMap.get(reading.getParentId());
				ControllerContext controller = controllers.get(controllerId);
				if (controller.getDataInterval() != -1) {
					minuteInterval = controller.getDataInterval();
				}
				else if (controller.getAgentId() > 0) {
					FacilioAgent agent = agentsMap.get(controller.getAgentId());
					if (agent.getInterval() != null && agent.getInterval() > 0l) {
						minuteInterval = agent.getInterval().intValue();
					}
				}
			}
			reading.setDataInterval(minuteInterval);
		}
	}
	
	
	private static int getDefaultInterval() throws Exception {
		int defaultInterval = DEFAULT_DATA_INTERVAL;
		Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		String defaultIntervalProp = orgInfo.get(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		if (defaultIntervalProp != null && !defaultIntervalProp.isEmpty()) {
			defaultInterval = Integer.parseInt(defaultIntervalProp);
		}
		return defaultInterval;
	}
	
	public static void deleteReadings(long parentId, List<FacilioField> readingFields, List<ReadingContext> readingsList) throws Exception {
		
		List<Long> fieldIds = new ArrayList<>();
		
		List<BatchUpdateByIdContext> batchUpdateList = new ArrayList<>();
		List<FacilioField> updateFields = new ArrayList<>();
		
		boolean isFirst = true;
		for (ReadingContext readingData : readingsList) {
			BatchUpdateByIdContext batchValue = new BatchUpdateByIdContext();
			batchValue.setWhereId(readingData.getId());
			
			
			for(FacilioField field : readingFields) {
				Object value = field.getDataTypeEnum() == FieldType.NUMBER || field.getDataTypeEnum() == FieldType.DECIMAL ? -99 : null;
				batchValue.addUpdateValue(field.getName(), value);
				if (isFirst) {
					fieldIds.add(field.getFieldId());
					updateFields.add(field);
				}
			}
			batchUpdateList.add(batchValue);
			isFirst = false;
		}
		
		FacilioModule module = readingFields.get(0).getModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(updateFields)
				;
		
		updateBuilder.batchUpdateById(batchUpdateList);
		
		ReadingDataMeta rdm = new ReadingDataMeta();
		rdm.setValue("-1");
		rdm.setReadingDataId(-99);
		rdm.setInputType(ReadingInputType.WEB);
		rdm.setReadingType(ReadingType.READ);
		rdm.setIsControllable(false);
		rdm.setControlActionMode(ControlActionMode.SANDBOX.getIntVal());
		
		ReadingsAPI.updateReadingDataMeta(parentId, fieldIds, rdm);
	}
	
	public static List<Map<String, Object>> getReadingInputValues(long resourceId, long fieldId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		ReadingDataMeta rdm = getReadingDataMeta(resourceId, modBean.getField(fieldId));
		return getReadingInputValueProps(Collections.singletonList(rdm.getId()), "rdmId");
	}

	public static List<Map<String, Object>> getReadingInputValues(List<Long> pointIds) throws Exception {
		return getReadingInputValueProps(pointIds, "pointId");
	}

	public static Map<Long,Map<Integer, String>> getReadingIdxVsValuesMap(List<Long> rdmIds) throws Exception {
		List<Map<String, Object>> props = getReadingInputValueProps(rdmIds, "rdmId");
		Map<Long, Map<Integer, String>> rdmValuesMap = null;
		if(props!=null && !props.isEmpty()) {
			rdmValuesMap = new HashMap<>();
			for(Map<String, Object> prop: props) {
				long rdmId = (long) prop.get("rdmId");
				Map<Integer, String> valueMap = rdmValuesMap.get(rdmId);
				if (valueMap == null) {
					valueMap = new HashMap<>();
					rdmValuesMap.put(rdmId, valueMap);
				}
				valueMap.put((int) prop.get("idx"), (String)prop.get("inputValue"));
			}
		}
		return rdmValuesMap;
	}
	
	
	public static Map<Long,Map<String, Integer>> getReadingInputValuesMap(List<Long> ids) throws Exception {
		
		List<Map<String, Object>> props = getReadingInputValueProps(ids, "pointId");
		Map<Long,Map<String, Integer>> parentValuesMap = null;
		if(props!=null && !props.isEmpty()) {
			parentValuesMap = new HashMap<>();
			for(Map<String, Object> prop: props) {
				if(!prop.containsKey("idx")){
					continue;
				}
				long parentId = (long) prop.get("pointId");
				Map<String, Integer> valueMap = parentValuesMap.get(parentId);
				if (valueMap == null) {
					valueMap = new HashMap<>();
					parentValuesMap.put(parentId, valueMap);
				}
				valueMap.put((String)prop.get("inputValue"), (int) prop.get("idx"));
			}
		}
		return parentValuesMap;
	}
	
	private static List<Map<String, Object>> getReadingInputValueProps(List<Long> ids, String fieldName) throws Exception {
		List<FacilioField> fields = FieldFactory.getReadingInputValuesFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioModule module = ModuleFactory.getReadingInputValuesModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(fieldName), ids, NumberOperators.EQUALS))
				;
		return builder.get();
	}
	
	
	public static void addDefaultPMTaskReading(TaskContext task, ReadingContext reading) {
		reading.addReading("woId", task.getParentTicketId());
		reading.addReading("taskId", task.getId());
		reading.addReading("taskUniqueId", task.getUniqueId());
		
		if (task.getResource() != null && task.getResource().getId() > 0) {
			reading.addReading("resourceId", task.getResource().getId());
		}
	}
	
	public static void convertUnitForRdmData (ReadingDataMeta meta) throws Exception {
		if (meta.getField() instanceof NumberField && meta.getActualValue() != null && !meta.getActualValue().equals("-1")) {
			Object value = meta.getValue();
			
			NumberField numberField =  (NumberField)meta.getField();
			if(numberField.getMetric() > 0) {
				Metric metric = Metric.valueOf(numberField.getMetric());
				if(numberField.getUnitId() > 0 && metric.getSiUnitId() > 0) {
					Unit siUnit = Unit.valueOf(metric.getSiUnitId());
					value = UnitsUtil.convert(meta.getValue(), siUnit.getUnitId(), numberField.getUnitId());
				}
				else {
					value = UnitsUtil.convertToOrgDisplayUnitFromSi(meta.getValue(), numberField.getMetric());
				}
			}
			if(value != null) {
				meta.setValue(value);
			}
		}
	}

	public static boolean isCounterField(FacilioField field, String modulename) {
		boolean fieldValidation;
		if (modulename.equalsIgnoreCase(FacilioConstants.ContextNames.ENERGY_DATA_READING)) {
			fieldValidation = field.getName().equals("totalEnergyConsumption") || field.getName().equals("phaseEnergyR") || field.getName().equals("phaseEnergyY") || field.getName().equals("phaseEnergyB");
		} else {
			fieldValidation = (field.getDataTypeEnum() == FieldType.NUMBER || field.getDataTypeEnum() == FieldType.DECIMAL) && ((NumberField) field).isCounterField();
		}
		return fieldValidation;
	}
	
	public static void updateDeltaForCurrentAndNextRecords(FacilioModule module,List<FacilioField> fields,ReadingContext reading,boolean currentReadingUpdate,Long curReadingTime,boolean isEnergyModule,Map<String, ReadingDataMeta> rdmMap, boolean ignoreSplNullHandling, boolean isDeltaReset)throws Exception{
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		ReadingContext lastReading,nextReading;
		for(Map.Entry<String, Object> readingEntry:reading.getReadings().entrySet()){
			String fieldName = readingEntry.getKey();
			FacilioField field = fieldMap.get(fieldName);
			boolean fieldValidation=false;
			if(!isEnergyModule){
				fieldValidation=(field.getDataTypeEnum() == FieldType.NUMBER || field.getDataTypeEnum() == FieldType.DECIMAL) && ((NumberField) field).isCounterField();
			}else{
				fieldValidation=field.getName().equals("totalEnergyConsumption") || field.getName().equals("phaseEnergyR")||field.getName().equals("phaseEnergyY")||field.getName().equals("phaseEnergyB");
			}
			if (field != null && fieldValidation) {
				ReadingDataMeta rdm = rdmMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), field));
				Condition condition = CriteriaAPI.getCondition(fieldMap.get("ttime"), String.valueOf(reading.getTtime()), NumberOperators.LESS_THAN);
				String orderBy = fieldMap.get("ttime").getColumnName() + " desc";
				lastReading = getSingleReading(module, fields, reading, fieldName, condition, orderBy);
				
				condition = CriteriaAPI.getCondition(fieldMap.get("ttime"), String.valueOf(reading.getTtime()),NumberOperators.GREATER_THAN);
				orderBy = fieldMap.get("ttime").getColumnName() + " asc";
				
				nextReading = getSingleReading(module, fields, reading, fieldName, condition, orderBy);
				
				if ((!currentReadingUpdate && !isDeltaReset && ((lastReading!=null&&lastReading.getTtime() > curReadingTime) || (nextReading!=null&&nextReading.getTtime() < curReadingTime))) || ignoreSplNullHandling) {
					//Delta calculation for next reading, If given date is not between last reading date and next reading date
					calculateDeltaBtwReadingsAndUpdate(module,fields,field,lastReading,nextReading);
					if (nextReading != null && rdm != null && nextReading.getId() == rdm.getReadingDataId()) {//Updating delta value in RDM.If Next reading is latest Reading
						updateReadingDataMeta(module, fields,reading,field.getName()+"Delta");
					}
				}
				if (currentReadingUpdate && !ignoreSplNullHandling) {
					if(!isDeltaReset) {
						calculateDeltaBtwReadingsAndUpdate(module, fields, field, lastReading, reading);
					}
					calculateDeltaBtwReadingsAndUpdate(module, fields, field, reading, nextReading);
					if (nextReading != null && nextReading.getId() == rdm.getReadingDataId()) {//Updating delta value in RDM.If Next reading is latest Reading
						updateReadingDataMeta(module, fields,reading,field.getName()+"Delta");
					}
				}
			}
		}
	}
	
	public static void updateReading(FacilioModule module, List<FacilioField> fields, ReadingContext reading)throws Exception {
        UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>().module(module)
				.fields(fields).andCondition(CriteriaAPI.getIdCondition(reading.getId(), module));
		updateBuilder.update(reading);
	}
	
	private static void calculateDeltaBtwReadingsAndUpdate(FacilioModule module, List<FacilioField> fields,FacilioField field,ReadingContext firstReading,ReadingContext secondReading)throws Exception{
		if (secondReading != null) {
			if (field.getDataTypeEnum() == FieldType.DECIMAL) {
				Double secondReadingVal = (Double) FacilioUtil.castOrParseValueAsPerType(field,secondReading.getReadings().entrySet().stream().filter(rd -> rd.getKey().equalsIgnoreCase(field.getName())).findFirst().get().getValue());
				if (firstReading != null) {
					Double firstReadingVal = (Double) FacilioUtil.castOrParseValueAsPerType(field,firstReading.getReadings().entrySet().stream().filter(rd -> rd.getKey().equalsIgnoreCase(field.getName())).findFirst().get().getValue());
					addDeltaValue(secondReading, field.getName(),(secondReadingVal - firstReadingVal) > 0 ? (secondReadingVal - firstReadingVal) : 0.0);
				} else {
					addDeltaValue(secondReading, field.getName(), 0.0);
				}
			}else{
				Long secondReadingVal = (Long) FacilioUtil.castOrParseValueAsPerType(field,secondReading.getReadings().entrySet().stream().filter(rd -> rd.getKey().equalsIgnoreCase(field.getName())).findFirst().get().getValue());
				if (firstReading != null) {
					Long firstReadingVal = (Long) FacilioUtil.castOrParseValueAsPerType(field,firstReading.getReadings().entrySet().stream().filter(rd -> rd.getKey().equalsIgnoreCase(field.getName())).findFirst().get().getValue());
					addDeltaValue(secondReading, field.getName(),(secondReadingVal - firstReadingVal) > 0 ? (secondReadingVal - firstReadingVal) : 0l);
				} else {
					addDeltaValue(secondReading, field.getName(), 0l);
				}
			}
			updateReading(module, fields, secondReading);
			AggregatedEnergyConsumptionUtil.recalculateAggregatedEnergyConsumption(Collections.singletonList(secondReading));
		}
	}
	
	public static void addDeltaValue(ReadingContext reading,String fieldName,Object value){
		 boolean isDeltaKeyPresent= reading.getReadings().entrySet().stream().filter(rd->rd.getKey().equalsIgnoreCase(fieldName+"Delta")).findFirst().isPresent();
			if (isDeltaKeyPresent) {
				reading.getReadings().entrySet().stream().filter(rd -> rd.getKey().equalsIgnoreCase(fieldName + "Delta")).findFirst().get().setValue(value);
			} else {
				reading.addReading((fieldName + "Delta"), value);
			}
	}
	
	public static List<ReadingDataMeta> getConnectedLoggedReadings(Long assetId) throws Exception {
		 FacilioChain latestAssetData = ReadOnlyChainFactory.fetchLatestReadingDataChain();
		 FacilioContext context = latestAssetData.getContext();
		 context.put(FacilioConstants.ContextNames.PARENT_ID, assetId);
		 context.put(FacilioConstants.ContextNames.FILTER, "nonformula");
		 context.put(FacilioConstants.ContextNames.EXCLUDE_EMPTY_FIELDS, true);
		 latestAssetData.execute();
		 List<ReadingDataMeta> rdmList =  (List<ReadingDataMeta>)context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST);
		 if(rdmList == null){
			 return new ArrayList<ReadingDataMeta>();
		 }else{
			return rdmList;
		 }
	 }

	public static ReadingDataMeta getRDMContextForReadingAndResource(long orgId, long timestamp, Long resourceId, ReadingInputType type, FacilioField field) {
		ReadingDataMeta dataMeta = new ReadingDataMeta();
		dataMeta.setOrgId(orgId);
		dataMeta.setResourceId(resourceId);
		dataMeta.setFieldId(field.getFieldId());
		dataMeta.setTtime(timestamp);
		dataMeta.setValue("-1");
		dataMeta.setInputType(type);
		if (!field.isDefault()) {
			dataMeta.setCustom(true);
		}
		if (field instanceof NumberField) {
			dataMeta.setUnit(((NumberField) field).getUnitId());
		}
		return dataMeta;
	}
	
}
