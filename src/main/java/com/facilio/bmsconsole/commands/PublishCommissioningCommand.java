package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.bmsconsole.util.MetersAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.connected.ResourceType;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder.BatchUpdateByIdContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.unitconversion.Unit;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PublishCommissioningCommand extends FacilioCommand implements PostTransactionCommand {
	
	private static List<Map<String, Object>> migrationPoints;

	
	Map<String, ReadingDataMeta> rdmMap;

	@Override
	public boolean executeCommand(Context context) throws Exception {

		long id = (long) context.get(ContextNames.ID);
		CommissioningLogContext log = CommissioningApi.commissioniongDetails(id);
		validate(log);
		context.put(AgentConstants.POINTS,log.getPoints());
		context.put(AgentConstants.AGENT,log.getAgent());
		
		long publishTime = System.currentTimeMillis();
		List<BatchUpdateByIdContext> batchUpdateList = new ArrayList<>();
		
		List<ReadingDataMeta> rdmList = new ArrayList<>();
		Set<Long> connectedAssetIds = new HashSet<>();
		Set<Long> unmappedAssetIds = new HashSet<>();
		
		Map<String, List<Map<String, Object>>> inputValuePoints = new HashMap<>();
		List<Pair<Long, FacilioField>> remainingRdmPairs = new ArrayList<>();

		List<Map<String, Object>> dbPoints = getDbPoints(log);
		Map<Long, Map<String, Object>> pointMap = null;
		if (dbPoints != null) {
			pointMap = dbPoints.stream().collect(Collectors.toMap(point -> (long)point.get("id"), point-> point));
		}
		List<Map<String, Object>> points = log.getPoints();
		migrationPoints = new ArrayList<>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		for(int i = 0; i < points.size(); i++) {
			Map<String, Object> point = (Map<String, Object>) points.get(i);
			long pointId = (long) point.get("id");
			boolean writable = false;
			Map<String, Object> dbPoint = null;
			Long dbCategoryId = null;
			Long dbResourceId = null;
			Long dbFieldId = null;
			if (pointMap != null && pointMap.containsKey(pointId)) {
				dbPoint = pointMap.get(pointId);
				writable = (Boolean) dbPoint.getOrDefault(AgentConstants.WRITABLE, false);
				dbCategoryId = (Long) dbPoint.get(AgentConstants.ASSET_CATEGORY_ID);
				dbResourceId = (Long) dbPoint.get(AgentConstants.RESOURCE_ID);
				dbFieldId = (Long) dbPoint.get(AgentConstants.FIELD_ID);
			}
			
			Long categoryId = (Long) point.get(AgentConstants.ASSET_CATEGORY_ID);
			Long resourceId = (Long) point.get(AgentConstants.RESOURCE_ID);
			Long fieldId = (Long) point.get(AgentConstants.FIELD_ID);
			Long unitId = (Long) point.get(AgentConstants.UNIT);
			Unit unit = null;
			
			boolean isAlreadyCommissioned = dbPoint != null && dbResourceId != null && dbResourceId > 0 && dbFieldId != null && dbFieldId > 0;
			boolean resourceAvailable = resourceId != null && resourceId > 0;
			boolean fieldAvailable = fieldId != null && fieldId > 0;
			boolean unitAvailable = false;
			if (unitId != null && unitId > 0) {
				unitAvailable = true;
				unit = Unit.valueOf(unitId.intValue());
				point.put(AgentConstants.UNIT, unit);
			}
			boolean unitChanged = unitAvailable;
			
			boolean  mappingChanged = true;

			// Case: If any mapping done or if already commissioned and removed mapping
			if (( (categoryId != null && categoryId > 0) ||  (dbCategoryId != null && dbCategoryId > 0) )
					|| resourceAvailable || fieldAvailable || unitAvailable || log.getReadingScope() > 0) {

				// Case 1: point is already mapped
				if (fieldAvailable && resourceAvailable) {
					if (isAlreadyCommissioned) {
						
						// Case 1a: If point is remapped
						if (!dbResourceId.equals(resourceId) || !dbFieldId.equals(fieldId)) {
							point.put(ContextNames.PREV_FIELD_ID, dbFieldId);
							point.put(ContextNames.PREV_PARENT_ID, dbResourceId);
						}
						// Case 1b: If only unit or input value has been changed
						else {
							mappingChanged = false;
						}
						
						Integer dbUnitInt = (Integer) dbPoint.get(AgentConstants.UNIT);
						if (unitAvailable && dbUnitInt != null && dbUnitInt > 0) {
							Unit dbUnit = Unit.valueOf(dbUnitInt);
							point.put("prevUnit", dbUnit);
							if (dbUnit == unit) {
								unitChanged = false;
							}
						}
					}
					
					
					if(point.get("inputValues") != null) {
						List<Map<String, Object>> inputValues = (List<Map<String, Object>>) point.get("inputValues");
						inputValues.forEach(val -> val.put("pointId", pointId));
						String rdmKey = resourceId+"_"+fieldId;
						inputValuePoints.put(rdmKey, inputValues);
						if (rdmMap == null || !rdmMap.containsKey(rdmKey)) {
							remainingRdmPairs.add(Pair.of(resourceId, modBean.getField(fieldId)));
						}
					}
					
					if (mappingChanged || unitChanged) {
						ReadingDataMeta meta = new ReadingDataMeta();
						meta.setResourceId(resourceId);
						meta.setFieldId(fieldId);
						if (unitAvailable) {
							meta.setUnit(unit.getUnitId());
						}
						meta.setInputType(ReadingInputType.CONTROLLER_MAPPED);
						if (writable) {
							meta.setReadingType(ReadingType.WRITE);
						}
						meta.setValue("-1");
						rdmList.add(meta);
						
						migrationPoints.add(point);
						if (mappingChanged) {
							connectedAssetIds.add(resourceId);
						}
						
						point.put("unitOnlyChanged", !mappingChanged);
					}
					
				}

				// Case 2: New commissioning (or recommissioning or removed mapping)
				if(mappingChanged || unitChanged) {
					addPointToBatchUpdateProp(point, batchUpdateList, publishTime , ResourceType.valueOf(log.getReadingScope()));
				}
			}
			
			// Changing the input type in rdm if field or asset removed/changed from mapping
			if (isAlreadyCommissioned && (!resourceAvailable || !fieldAvailable || mappingChanged)) {
				unmappedAssetIds.add(dbResourceId);
				removeMapping(rdmList, dbResourceId, dbFieldId);
			}
		}
		
		
		updatePoint(batchUpdateList);
		
		if (!rdmList.isEmpty()) {
			List<String> fields = Arrays.asList("unit", "inputType", "readingType", "value", "readingDataId");
			ReadingsAPI.updateReadingDataMetaList(rdmList, fields);
		}
		if (!inputValuePoints.isEmpty()) { // for boolean and enum readings, option mapping is added
			addInputValueMapping(inputValuePoints, remainingRdmPairs);
		}
		
		handleConnectionStatus(connectedAssetIds, unmappedAssetIds , ResourceType.valueOf(log.getReadingScope()));
		
		updateLog(publishTime, id);
		

		return false;
	}

	private void validate(CommissioningLogContext log) throws Exception {
		if (log.getPoints() == null || log.getPoints().isEmpty()) {
			throw new IllegalArgumentException("No points mapped");
		}
		rdmMap = CommissioningApi.filterAndValidatePointsOnUpdate(log, null);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getDbPoints(CommissioningLogContext log) throws Exception {
		//1
		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule pointModule = moduleBean.getModule(AgentConstants.POINT);
		List<FacilioField>fields = new ArrayList<>();
		if (pointModule == null){
			pointModule = ModuleFactory.getPointModule();
			fields = FieldFactory.getPointFields();
		}
		else {
			fields = moduleBean.getAllFields(AgentConstants.POINT);
			fields.add(FieldFactory.getIdField(pointModule));
		}
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<Long> ids = (List<Long>) log.getPoints().stream().map(point -> (long) ((Map<String, Object>)point).get("id"))
				.collect(Collectors.toList());
		
		Criteria mappedOrWritableCriteria = new Criteria();
		mappedOrWritableCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.RESOURCE_ID), CommonOperators.IS_NOT_EMPTY));
		mappedOrWritableCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.FIELD_ID), CommonOperators.IS_NOT_EMPTY));
		mappedOrWritableCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.WRITABLE), Boolean.TRUE.toString(), BooleanOperators.IS));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getIdCondition(ids, pointModule));
		criteria.andCriteria(mappedOrWritableCriteria);
		
		GetPointRequest getPointRequest = new GetPointRequest()
				.limit(-1)
				.withCriteria(criteria)
				;
		return getPointRequest.getPointsData();
	}

	private void addPointToBatchUpdateProp(Map<String, Object> point, List<BatchUpdateByIdContext> batchUpdateList, long publishTime , ResourceType scope) {
		BatchUpdateByIdContext batchValue = new BatchUpdateByIdContext();
		batchValue.setWhereId((long) point.get("id"));
		batchValue.addUpdateValue(AgentConstants.MAPPED_TIME, publishTime);

		Long categoryId = (Long) point.get(AgentConstants.ASSET_CATEGORY_ID);
		if (categoryId != null && categoryId > 0) {
			if (scope != null){
				batchValue.addUpdateValue(AgentConstants.READING_SCOPE,scope.getIndex());
			}
			batchValue.addUpdateValue(AgentConstants.ASSET_CATEGORY_ID, point.get(AgentConstants.ASSET_CATEGORY_ID));

			Long resourceId = (Long) point.get(AgentConstants.RESOURCE_ID);
			if (resourceId != null && resourceId > 0) {
				batchValue.addUpdateValue(AgentConstants.RESOURCE_ID, resourceId);
			}

			Long fieldId = (Long) point.get(AgentConstants.FIELD_ID);
			if (fieldId != null && fieldId > 0) {
				batchValue.addUpdateValue(AgentConstants.FIELD_ID, fieldId);

				Unit unit = (Unit) point.get(AgentConstants.UNIT);
				if (unit != null) {
					batchValue.addUpdateValue(AgentConstants.UNIT, unit.getUnitId());
				}
			}
			batchValue.addUpdateValue(AgentConstants.MAPPED_TYPE, PointEnum.MappedType.MANUAL.getIndex());
		}

		batchUpdateList.add(batchValue);
	}
	
	
	private void updatePoint(List<BatchUpdateByIdContext> batchUpdateList) throws Exception {
		if (batchUpdateList.isEmpty()) {
			return;
		}
		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule pointModule = moduleBean.getModule(AgentConstants.POINT);
		List<FacilioField>fields = new ArrayList<>();
		if (pointModule == null){
			pointModule = ModuleFactory.getPointModule();
			fields = FieldFactory.getPointFields();
		}
		else {
			fields = moduleBean.getAllFields(AgentConstants.POINT);
		}
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<FacilioField> updateFields = new ArrayList<>();
		updateFields.add(fieldMap.get(AgentConstants.ASSET_CATEGORY_ID));
		updateFields.add(fieldMap.get(AgentConstants.RESOURCE_ID));
		updateFields.add(fieldMap.get(AgentConstants.FIELD_ID));
		updateFields.add(fieldMap.get(AgentConstants.UNIT));
		updateFields.add(fieldMap.get(AgentConstants.MAPPED_TIME));
		if(fieldMap.containsKey(AgentConstants.MAPPED_TYPE) && fieldMap.get(AgentConstants.MAPPED_TYPE)!=null){
			updateFields.add(fieldMap.get(AgentConstants.MAPPED_TYPE));
		}

		updateFields.add(fieldMap.get(AgentConstants.READING_SCOPE));

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(pointModule.getTableName())
				.fields(updateFields);
		
		updateBuilder.batchUpdateById(batchUpdateList);
	}
	
	private void updateLog(long publishTime, long id) throws Exception {
		CommissioningLogContext log = new CommissioningLogContext();
		log.setId(id);
		log.setPublishedTime(publishTime);
		CommissioningApi.updateLog(log);
		
		// TODO set count of mapped points
	}
	
	private void removeMapping(List<ReadingDataMeta> rdmList, long resourceId, long fieldId) {
		// TODO Check if user confirmed to remove data and add to migration point list
		
		ReadingDataMeta meta = new ReadingDataMeta();
		meta.setResourceId(resourceId);
		meta.setFieldId(fieldId);
		meta.setInputType(ReadingInputType.WEB);
		meta.setReadingType(ReadingType.READ);
		meta.setValue("-1");

		rdmList.add(meta);
	}
	
	private void addInputValueMapping(Map<String, List<Map<String, Object>>> values, List<Pair<Long, FacilioField>> remainingRdmPairs) throws Exception {
		List<FacilioField> fields = FieldFactory.getReadingInputValuesFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioModule module = ModuleFactory.getReadingInputValuesModule();
		
		if(!remainingRdmPairs.isEmpty()) {
			List<ReadingDataMeta> rdmList = ReadingsAPI.getReadingDataMetaList(remainingRdmPairs);
			if (rdmMap == null) {
				rdmMap = new HashMap<>();
			}
			rdmMap.putAll(rdmList.stream().collect(Collectors.toMap(rdm -> ReadingsAPI.getRDMKey(rdm), Function.identity())));
		}

		List<Long> inputRdmIds = new ArrayList<>();
		Set<Long> pointIds = new HashSet<>();
		for(Entry<String, List<Map<String, Object>>> entry: values.entrySet()) {
			String rdmKey = entry.getKey();
			long rdmId = rdmMap.get(rdmKey).getId();
			inputRdmIds.add(rdmId);
			entry.getValue().forEach(value -> {
				value.put("rdmId", rdmId);
				pointIds.add((Long) value.get(AgentConstants.POINT_ID));
			});
		}
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.POINT_ID), pointIds, NumberOperators.EQUALS));

		deleteBuilder.batchDelete(Collections.singletonList(fieldMap.get(AgentConstants.POINT_ID)), pointIds.stream().map(id -> Collections.singletonMap(AgentConstants.POINT_ID, (Object) id)).collect(Collectors.toList()));
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(fields)
				.table(module.getTableName())
				.addRecords(values.values().stream().flatMap(List::stream).collect(Collectors.toList()));
		insertBuilder.save();
	}
	
	private void handleConnectionStatus(Set<Long> connectedIds, Set<Long> unmappedIds,ResourceType scope) throws Exception {
		
		if (!connectedIds.isEmpty()) {
			if (scope == null || scope.equals(ResourceType.ASSET_CATEGORY)){
				AssetsAPI.updateAssetConnectionStatus(connectedIds, true);
			} else if (scope.equals(ResourceType.METER_CATEGORY)) {
				MetersAPI.updateMeterConnectionStatus(connectedIds,true);
			}
		}
		
		unmappedIds.removeAll(connectedIds);
		if (!unmappedIds.isEmpty()) {
			FacilioModule module = ModuleFactory.getReadingDataMetaModule();
			List<FacilioField> fields = FieldFactory.getReadingDataMetaFields();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			FacilioField resourceIdField = fieldMap.get("resourceId");
			
			
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(Collections.EMPTY_LIST)
														.aggregate(BmsAggregateOperators.CommonAggregateOperator.DISTINCT, resourceIdField)
														.andCondition(CriteriaAPI.getCondition(resourceIdField, unmappedIds, PickListOperators.IS))
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("inputType"), String.valueOf(ReadingInputType.CONTROLLER_MAPPED.getValue()), PickListOperators.IS))
														;
			List<Map<String, Object>> props = builder.get();
			if (CollectionUtils.isNotEmpty(props)) {
				List<Long> mappedAssetIds = props.stream().map(prop -> (long) prop.get("resourceId")).collect(Collectors.toList());
				unmappedIds.removeAll(mappedAssetIds);
			}
			if (!unmappedIds.isEmpty()) {
				if (scope == null || scope.equals(ResourceType.ASSET_CATEGORY)){
					AssetsAPI.updateAssetConnectionStatus(unmappedIds, false);
				} else if (scope.equals(ResourceType.METER_CATEGORY)) {
					MetersAPI.updateMeterConnectionStatus(unmappedIds,false);
				}
			}
		}
	}
	

	@Override
	public boolean postExecute() throws Exception {
		
		if (CollectionUtils.isNotEmpty(migrationPoints)) {
			for(Map<String, Object> point: migrationPoints) {
				FacilioContext context = new FacilioContext();
				
				Long oldFieldId = (Long) point.get(ContextNames.PREV_FIELD_ID);
				if (oldFieldId != null) {
					context.put(ContextNames.PREV_FIELD_ID, oldFieldId);
					context.put(ContextNames.PREV_PARENT_ID, point.get(ContextNames.PREV_PARENT_ID));
				}
				context.put("prevUnit", point.get("prevUnit"));
				context.put("unitOnlyChanged", point.get("unitOnlyChanged"));
				
				context.put("id", point.get("id"));
				context.put(ContextNames.FIELD_ID, point.get(AgentConstants.FIELD_ID));
				context.put(ContextNames.PARENT_ID, point.get(AgentConstants.RESOURCE_ID));
				context.put(ContextNames.UNIT, point.get(AgentConstants.UNIT));
				FacilioTimer.scheduleInstantJob("datamigration","MigrateReadingData", context);
			}
		}
		
		return false;
	}

}
