package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
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
import com.facilio.time.SecondsChronoUnit;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflows.util.WorkflowUtil;

public class ReadingsAPI {
	private static final Logger LOGGER = LogManager.getLogger(ReadingsAPI.class.getName());
	public static final int DEFAULT_DATA_INTERVAL = 15; //In Minutes
	public static final SecondsChronoUnit DEFAULT_DATA_INTERVAL_UNIT = new SecondsChronoUnit(DEFAULT_DATA_INTERVAL * 60); 
	private static final int BATCH_SIZE =2000;
	
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
			default:
				return ReadingInputType.WEB;
		}
	}
	
	public static int updateReadingDataMeta (ReadingDataMeta rdm) throws Exception {
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField resourceIdField = fieldMap.get("resourceId");
		FacilioField fieldIdField = fieldMap.get("fieldId");
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(module.getTableName())
														.fields(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(resourceIdField, String.valueOf(rdm.getResourceId()), PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(fieldIdField, String.valueOf(rdm.getFieldId()), PickListOperators.IS))
														;
		
		return updateBuilder.update(FieldUtil.getAsProperties(rdm));
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
		
		if (readingType != null) {
			prop.put("readingType", readingType.getValue());
		}
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.table(module.getTableName())
														.fields(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCriteria(pkCriteriaList);
														
		return updateBuilder.update(prop);
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
		FacilioModule module = ModuleFactory.getReadingDataMetaModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReadingDataMetaFields())
				.table(ModuleFactory.getReadingDataMetaModule().getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere(module.getTableName()+".RESOURCE_ID = ?", resourceId)
				.andCustomWhere(module.getTableName()+".FIELD_ID = ?", field.getFieldId());
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<ReadingDataMeta> readingMetaList = getReadingDataFromProps(props, Collections.singletonMap(field.getId(), field));
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
		return getRDMMapFromProps(builder.get(), fieldIdMap);
	}
	
	public static long getReadingDataMetaCount(Long resourceId, boolean excludeEmptyFields, String search, ReadingInputType...inputTypes) throws Exception {
		return getReadingDataMetaCount(Collections.singletonList(resourceId), excludeEmptyFields, search, inputTypes);
	}
	
	public static long getReadingDataMetaCount(Collection<Long> resourceIds, boolean excludeEmptyFields, String search, ReadingInputType...inputTypes) throws Exception {
		List<Map<String, Object>> props = getRDMProps(resourceIds, null, excludeEmptyFields, true, null, search, null, inputTypes);
		if (props != null && !props.isEmpty()) {
			return (long) props.get(0).get("count");
		}
		return 0;
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Long resourceId, Collection<FacilioField> fieldList) throws Exception {
		return getReadingDataMetaList(resourceId, fieldList, false);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Long resourceId, Collection<FacilioField> fieldList, boolean excludeEmptyFields, ReadingInputType...inputTypes) throws Exception {
		return getReadingDataMetaList(Collections.singletonList(resourceId), fieldList, excludeEmptyFields, null, null, inputTypes);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Collection<Long> resourceIds, Collection<FacilioField> fieldList, boolean excludeEmptyFields, JSONObject pagination, String search, ReadingInputType...inputTypes) throws Exception {
		return getReadingDataMetaList(resourceIds, fieldList, excludeEmptyFields, pagination, search, null, inputTypes);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Collection<Long> resourceIds, Map<Long, FacilioField> fieldMap, boolean excludeEmptyFields, JSONObject pagination, String search, ReadingInputType...inputTypes) throws Exception {
		return getReadingDataMetaList(resourceIds, fieldMap, excludeEmptyFields, pagination, search, null, inputTypes);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Collection<Long> resourceIds, Collection<FacilioField> fieldList, boolean excludeEmptyFields, ReadingType readingType) throws Exception {
		return getReadingDataMetaList(resourceIds, fieldList, excludeEmptyFields, null, null, readingType);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Collection<Long> resourceIds, Collection<FacilioField> fieldList, boolean excludeEmptyFields, JSONObject pagination, String search, ReadingType readingType, ReadingInputType...inputTypes) throws Exception {
		Map<Long, FacilioField> fieldMap = null;
		if (fieldList != null) {
			fieldMap = FieldFactory.getAsIdMap(fieldList);
		}
		return getReadingDataMetaList(resourceIds, fieldMap, excludeEmptyFields, pagination, search, readingType, inputTypes);
	}
	
	public static List<ReadingDataMeta> getReadingDataMetaList(Collection<Long> resourceIds, Map<Long, FacilioField> fieldMap, boolean excludeEmptyFields, JSONObject pagination, String search, ReadingType readingType, ReadingInputType...inputTypes) throws Exception {
		List<Map<String, Object>> stats = getRDMProps(resourceIds, fieldMap, excludeEmptyFields, false, pagination, search, readingType, inputTypes);
		return getReadingDataFromProps(stats, fieldMap);
	}
	
	private static List<Map<String, Object>> getRDMProps (Collection<Long> resourceIds, Map<Long, FacilioField> fieldMap, boolean excludeEmptyFields, boolean fetchCount, JSONObject pagination, String search, ReadingType readingType, ReadingInputType...inputTypes) throws Exception {
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
		
		if (excludeEmptyFields) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), "-1", StringOperators.ISN_T));
			criteria.addAndCondition(CriteriaAPI.getCondition(readingFieldsMap.get("value"), CommonOperators.IS_NOT_EMPTY));
			criteria.addOrCondition(CriteriaAPI.getCondition(readingFieldsMap.get("inputType"),String.valueOf(ReadingInputType.IS_MANUAL_DATA.getValue()), PickListOperators.IS));
			builder.andCriteria(criteria);
		}
		
		if(readingType != null) {
			builder.andCondition(CriteriaAPI.getCondition(readingFieldsMap.get("readingType"), String.valueOf(readingType.getValue()), PickListOperators.IS));
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
	
	private static List<ReadingDataMeta> getReadingDataFromProps(List<Map<String, Object>> props, Map<Long, FacilioField> fieldMap) throws Exception {
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
	
	public static String getRDMKey(long resourceId, FacilioField field) {
		return resourceId+"_"+field.getFieldId();
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
	
	private static ReadingDataMeta getRDMFromProp (Map<String, Object> prop, Map<Long, FacilioField> fieldMap) throws Exception {
		ReadingDataMeta meta = FieldUtil.getAsBeanFromMap(prop, ReadingDataMeta.class);
		Object value = meta.getValue();
		meta.setActualValue((String) value);
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
		meta.setValue(FieldUtil.castOrParseValueAsPerType(field, value));
		meta.setField(field);
		return meta;
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
	
	public static Map<String, ReadingDataMeta> updateReadingDataMeta(List<FacilioField> fieldsList,List<ReadingContext> readingList,Map<String, ReadingDataMeta> metaMap) throws SQLException {


		if(readingList == null || readingList.isEmpty()) {
			return null;
		}
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
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
						Object val = FieldUtil.castOrParseValueAsPerType(fField, reading.getValue());
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
									if (timeStamp > currentTime
											|| (lastReading != null 
											&& lastTimeStamp != -1 
											&& !"-1".equals(meta.getActualValue()) 
											&& timeStamp < lastTimeStamp)) {
//										if (AccountUtil.getCurrentOrg().getId() == 104 || AccountUtil.getCurrentOrg().getId() == 134) {
//											LOGGER.info("Not updating: time" + timeStamp + ", current: " + currentTime + ", readingId: " + readingId + ", resourceId: "+ resourceId);
//										}
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
			StringBuilder timeBuilder= new StringBuilder();
			StringBuilder valueBuilder= new StringBuilder();
			StringBuilder idBuilder= new StringBuilder();
			StringJoiner whereClause = new StringJoiner(" OR ");
			int cycle = 0;
			long orgId=AccountUtil.getCurrentOrg().getOrgId();

			for (ReadingDataMeta rdm : uniqueRDMs.values()) {
				
				timeBuilder.append(getCase(rdm.getResourceId(),rdm.getFieldId(),rdm.getTtime(),false));
				valueBuilder.append(getCase(rdm.getResourceId(),rdm.getFieldId(),rdm.getValue(),true));
				idBuilder.append(getCase(rdm.getResourceId(),rdm.getFieldId(),rdm.getReadingDataId(),false));
				
				StringBuilder builder = new StringBuilder();
				builder.append("(RESOURCE_ID = ")
						.append(rdm.getResourceId())
						.append(" AND FIELD_ID = ")
						.append(rdm.getFieldId())
						.append(")")
						;
				whereClause.add(builder.toString());
				cycle++;
				if(cycle == BATCH_SIZE) {
					updateRDM(timeBuilder, valueBuilder,idBuilder, whereClause, orgId,conn);
					timeBuilder = new StringBuilder();
					idBuilder = new StringBuilder();
					valueBuilder = new StringBuilder();
					whereClause = new StringJoiner("OR");
					cycle = 0;
				}
			}
			if(cycle > 0) {
				updateRDM(timeBuilder, valueBuilder,idBuilder, whereClause, orgId,conn);
			}
			return uniqueRDMs;
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
	private static final List<String> getDefaultReadingFieldNames() {
		List<String> fieldNames = new ArrayList<>();
		List<FacilioField> fields = FieldFactory.getDefaultReadingFields(null);
		for(FacilioField field : fields) {
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}
	
	public static List<FacilioField> excludeDefaultAndEmptyReadingFields(List<FacilioField> fields,Long parentId) throws Exception {
		List<Long> fieldsWithValues = null;
		if (parentId != null && parentId > -1) {
			List<ReadingDataMeta> readingMetaDatas = getReadingDataMetaList(parentId, fields, true);
			if (readingMetaDatas != null) {
				fieldsWithValues = readingMetaDatas.stream().map(meta -> meta.getFieldId()).collect(Collectors.toList());
			}
		}
		List<FacilioField> fieldsToReturn = new ArrayList<>();
		if(fields != null) {
			for(FacilioField field: fields) {
				if ((parentId == null || (fieldsWithValues != null && fieldsWithValues.contains(field.getId()))) && !DEFAULT_READING_FIELDS.contains(field.getName()) ) {
//					  if(field.getName().equals("sysCreatedTime") || field.getName().equals("marked")) {
//						continue;
//				       }
						fieldsToReturn.add(field);
			    }
		    }
		}
		return fieldsToReturn;
	}
	
	public static void updateReadingDataMeta() throws Exception {

		List<ResourceContext> resourcesList= ResourceAPI.getAllResources();
		updateReadingDataMeta(resourcesList);
	}
	public static void updateReadingDataMeta(List<ResourceContext> resourcesList) throws Exception {
		
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
				Chain getSpaceSpecifcReadingsChain = FacilioChainFactory.getSpaceReadingsChain();
				getSpaceSpecifcReadingsChain.execute(context);
				moduleList = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			}
			else if(resourceType==ResourceContext.ResourceType.ASSET.getValue()) {
				context.put(FacilioConstants.ContextNames.PARENT_ID, resourceId);
				Chain getSpaceSpecifcReadingsChain = FacilioChainFactory.getAssetReadingsChain();
				getSpaceSpecifcReadingsChain.execute(context);
				moduleList = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
			}
			if(moduleList==null || moduleList.isEmpty()) {
				continue;
			}
			for(FacilioModule module:moduleList) {
				
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
					builder.addRecord(FieldUtil.getAsProperties(rdm));
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
		return (int) readingContext.getDatum("interval");
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
				return readings.stream().mapToInt(reading -> (int) reading.getDatum("interval")).max().getAsInt();
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
//		Map<Long, Integer> intervalMap = new HashMap<>();
		int defaultInterval = DEFAULT_DATA_INTERVAL;
		Map<String, String> orgInfo = CommonCommandUtil.getOrgInfo(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		String defaultIntervalProp = orgInfo.get(FacilioConstants.OrgInfoKeys.DEFAULT_DATA_INTERVAL);
		if (defaultIntervalProp != null && !defaultIntervalProp.isEmpty()) {
			defaultInterval = Integer.parseInt(defaultIntervalProp);
		}
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Set<Pair<Long, Long>> pairs = new HashSet<>();
		Map<String, Map<String, FacilioField>> allFieldsMap = new HashMap<>();
		for(ReadingContext reading: readings) {
			Map<String, Object> readingMap = reading.getReadings();
			if (readingMap != null) {
				FacilioModule module = moduleMap != null ? moduleMap.get(reading.getModuleId()) : bean.getModule(reading.getModuleId());
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
		if (controllerIds != null && !controllerIds.isEmpty()) {
			controllers = ControllerAPI.getControllersMap(controllerIds);
		}
		
		for (ReadingContext reading : readings) {
			FacilioModule module = moduleMap != null ? moduleMap.get(reading.getModuleId()) : bean.getModule(reading.getModuleId());
			int minuteInterval = defaultInterval;
			if (module.getDataInterval() != -1) {
				minuteInterval = module.getDataInterval();
			}
			else if (controllers != null && assetVsControllerIdMap.get(reading.getParentId()) != null) {
				long controllerId = assetVsControllerIdMap.get(reading.getParentId());
				ControllerContext controller = controllers.get(controllerId);
				if (controller.getDataInterval() != -1) {
					minuteInterval = controller.getDataInterval();
				}
			}
			reading.setDatum("interval", minuteInterval);
//			intervalMap.put(reading.getId(), minuteInterval);
		}
	}
	
	public static void deleteReadings(long parentId, List<FacilioField> assetFields, FacilioModule module, List<FacilioField> fields, Map<String, FacilioField> fieldMap, Boolean... deleteReadings) throws Exception {
		ReadingDataMeta rdm = new ReadingDataMeta();
		List<Long> fieldIds = new ArrayList<>();
		ReadingContext reading = new ReadingContext();
		assetFields.forEach(field -> {
			Object value;
			if (field.getDataTypeEnum() == FieldType.NUMBER || field.getDataTypeEnum() == FieldType.DECIMAL) {
				value = -99;
			}
			else {
				value = null;
			}
			reading.addReading(field.getName(), value);
			fieldIds.add(field.getFieldId());
		});
		
		if (deleteReadings == null || deleteReadings.length == 0 || deleteReadings[0]) {
			
			if (fields == null) {
				ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				fields = bean.getAllFields(module.getName());
				fieldMap = FieldFactory.getAsMap(fields);
			}
			
			UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS));
			
			updateBuilder.update(reading);
			
			rdm.setValue("-1");
			rdm.setReadingDataId(-99);
		}
		
		rdm.setInputType(ReadingInputType.WEB);
		
		ReadingsAPI.updateReadingDataMeta(parentId, fieldIds, rdm);
	}
	
	public static Map<Long,Map<Integer, String>> getReadingIdxVsValuesMap(List<Long> rdmIds) throws Exception {
		List<Map<String, Object>> props = getReadingInputValueProps(rdmIds);
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
	
	public static Map<Long,Map<String, Integer>> getReadingInputValuesMap(List<Long> rdmIds) throws Exception {
		
		List<Map<String, Object>> props = getReadingInputValueProps(rdmIds);
		Map<Long,Map<String, Integer>> rdmValuesMap = null;
		if(props!=null && !props.isEmpty()) {
			rdmValuesMap = new HashMap<>();
			for(Map<String, Object> prop: props) {
				long rdmId = (long) prop.get("rdmId");
				Map<String, Integer> valueMap = rdmValuesMap.get(rdmId);
				if (valueMap == null) {
					valueMap = new HashMap<>();
					rdmValuesMap.put(rdmId, valueMap);
				}
				valueMap.put((String)prop.get("inputValue"), (int) prop.get("idx"));
			}
		}
		return rdmValuesMap;
	}
	
	private static List<Map<String, Object>> getReadingInputValueProps(List<Long> rdmIds) throws Exception {
		List<FacilioField> fields = FieldFactory.getReadingInputValuesFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioModule module = ModuleFactory.getReadingInputValuesModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("rdmId"), rdmIds, NumberOperators.EQUALS))
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
		if (meta.getField() instanceof NumberField) {
			Object value = meta.getValue();
			
			NumberField numberField =  (NumberField)meta.getField();
			if(numberField.getMetric() > 0) {
				
				if(numberField.getUnitId() > 0) {
					Unit siUnit = Unit.valueOf(Metric.valueOf(numberField.getMetric()).getSiUnitId());
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
}
