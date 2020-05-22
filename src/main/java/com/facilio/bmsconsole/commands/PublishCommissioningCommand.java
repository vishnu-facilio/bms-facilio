package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;

public class PublishCommissioningCommand extends FacilioCommand implements PostTransactionCommand {
	
	private static List<Map<String, Object>> migrationPoints;
	
	private FacilioModule module = ModuleFactory.getPointModule();
	private List<FacilioField> fields = FieldFactory.getPointFields();
	
	Map<String, ReadingDataMeta> rdmMap;

	@Override
	public boolean executeCommand(Context context) throws Exception {

		long id = (long) context.get(ContextNames.ID);
		CommissioningLogContext log = CommissioningApi.commissioniongDetails(id);
		validate(log);
		
		long publishTime = System.currentTimeMillis();
		
		List<ReadingDataMeta> writableReadingList = new ArrayList<>();
		List<ReadingDataMeta> unitRdmList = new ArrayList<>();
		List<ReadingDataMeta> remainingReadingList = new ArrayList<>();
		Set<Long> connectedAssetIds = new HashSet<>();
		
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
			Long dbResourceId = null;
			Long dbFieldId = null;
			if (pointMap != null && pointMap.containsKey(pointId)) {
				dbPoint = pointMap.get(pointId);
				writable = (Boolean) dbPoint.getOrDefault(AgentConstants.WRITABLE, false);
				dbResourceId = (Long) dbPoint.get(AgentConstants.RESOURCE_ID);
				dbFieldId = (Long) dbPoint.get(AgentConstants.FIELD_ID);
			}
			
			Long categoryId = (Long) point.get(AgentConstants.ASSET_CATEGORY_ID);
			Long resourceId = (Long) point.get(AgentConstants.RESOURCE_ID);
			Long fieldId = (Long) point.get(AgentConstants.FIELD_ID);
			Long unit = (Long) point.get(AgentConstants.UNIT);
			boolean resourceAvailable = resourceId != null && resourceId > 0;
			boolean fieldAvailable = fieldId != null && fieldId > 0;
			boolean unitChanged = unit != null && unit > 0;
			if ((categoryId != null && categoryId > 0 ) || resourceAvailable || fieldAvailable || unitChanged) {
				
				if (fieldAvailable && resourceAvailable) {
					if (dbPoint != null && dbResourceId != null && dbResourceId > 0 && dbFieldId != null && dbFieldId > 0) {
						if (dbResourceId != resourceId || dbFieldId != fieldId) {
							point.put(ContextNames.PREV_FIELD_ID, dbFieldId);
							point.put(ContextNames.PREV_PARENT_ID, dbResourceId);
							point.put("oldUnit", dbPoint.get(AgentConstants.UNIT));
							migrationPoints.add(point);					
						}
					}
					else {
						migrationPoints.add(point);						
					}
					
					if(point.get("inputValues") != null) {
						List<Map<String, Object>> inputValues = (List<Map<String, Object>>) point.get("inputValues");
						String rdmKey = resourceId+"_"+fieldId;
						inputValuePoints.put(rdmKey, inputValues);
						if (rdmMap == null || !rdmMap.containsKey(rdmKey)) {
							remainingRdmPairs.add(Pair.of(resourceId, modBean.getField(fieldId)));
						}
					}
					
					ReadingDataMeta meta = new ReadingDataMeta();
					meta.setResourceId(resourceId);
					meta.setFieldId(fieldId);
					if (unitChanged) {
						meta.setUnit(unit.intValue());
						meta.setInputType(ReadingInputType.CONTROLLER_MAPPED);
						if (writable) {
							meta.setReadingType(ReadingType.WRITE);
						}
						unitRdmList.add(meta);
					}
					else {
						if (writable) {
							writableReadingList.add(meta);
						}
						else {
							remainingReadingList.add(meta);
						}
					}
					connectedAssetIds.add(resourceId);
				}
				
				updatePoint(point, publishTime);
			}
			
		}
		
		if (!unitRdmList.isEmpty()) {
			// TODO update by case
			for(ReadingDataMeta rdm: unitRdmList) {
				ReadingsAPI.updateReadingDataMeta(rdm);
			}
		}
		if (!writableReadingList.isEmpty()) {
			ReadingsAPI.updateReadingDataMetaInputType(writableReadingList, ReadingInputType.CONTROLLER_MAPPED, ReadingType.WRITE);
		}
		if (!remainingReadingList.isEmpty()){
			ReadingsAPI.updateReadingDataMetaInputType(remainingReadingList, ReadingInputType.CONTROLLER_MAPPED, null);
		}
		if (!inputValuePoints.isEmpty()) {
			addInputValueMapping(inputValuePoints, remainingRdmPairs);
		}
		if (!connectedAssetIds.isEmpty()) {
			AssetsAPI.updateAssetConnectionStatus(connectedAssetIds, true);
		}
		
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
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<Long> ids = (List<Long>) log.getPoints().stream().map(point -> (long) ((Map<String, Object>)point).get("id"))
				.collect(Collectors.toList());
		
		Criteria mappedOrWritableCriteria = new Criteria();
		mappedOrWritableCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.RESOURCE_ID), CommonOperators.IS_NOT_EMPTY));
		mappedOrWritableCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.FIELD_ID), CommonOperators.IS_NOT_EMPTY));
		mappedOrWritableCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.WRITABLE), Boolean.TRUE.toString(), BooleanOperators.IS));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getIdCondition(ids, module));
		criteria.andCriteria(mappedOrWritableCriteria);
		
		GetPointRequest getPointRequest = new GetPointRequest()
				.initBuilder(null)
				.limit(-1)
				.withCriteria(criteria)
				;
		return getPointRequest.getPointsData();
	}
	
	
	private void updatePoint(Map<String, Object> point, long publishTime) throws Exception {
		Map<String, Object> pointToUpdate = new HashMap<>();
		Long fieldId = (Long) point.get(AgentConstants.FIELD_ID);
		if (fieldId != null && fieldId > 0) {
			pointToUpdate.put(AgentConstants.FIELD_ID, fieldId);
		}
		Long resourceId = (Long) point.get(AgentConstants.RESOURCE_ID);
		if (resourceId != null && resourceId > 0) {
			pointToUpdate.put(AgentConstants.RESOURCE_ID, resourceId);
		}
		Long unit = (Long) point.get(AgentConstants.UNIT);
		if (unit != null && unit > 0) {
			pointToUpdate.put(AgentConstants.UNIT, unit);
		}
		pointToUpdate.put(AgentConstants.ASSET_CATEGORY_ID, point.get(AgentConstants.ASSET_CATEGORY_ID));
		pointToUpdate.put(AgentConstants.MAPPED_TIME, publishTime);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition((long)point.get("id"), module));
		
		Map<String, Object> prop = FieldUtil.getAsProperties(pointToUpdate);
		updateBuilder.update(prop);
	}
	
	private void updateLog(long publishTime, long id) throws Exception {
		CommissioningLogContext log = new CommissioningLogContext();
		log.setId(id);
		log.setPublishedTime(publishTime);
		CommissioningApi.updateLog(log);
		
		// TODO set count of mapped points
	}
	
	private void addInputValueMapping(Map<String, List<Map<String, Object>>> values, List<Pair<Long, FacilioField>> remainingRdmPairs) throws Exception {
		List<FacilioField> fields = FieldFactory.getReadingInputValuesFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioModule module = ModuleFactory.getReadingInputValuesModule();
		
		if(!remainingRdmPairs.isEmpty()) {
			List<ReadingDataMeta> rdmList = ReadingsAPI.getReadingDataMetaList(remainingRdmPairs);
			rdmMap.putAll(rdmList.stream().collect(Collectors.toMap(rdm -> ReadingsAPI.getRDMKey(rdm), Function.identity())));
		}

		List<Long> inputRdmIds = new ArrayList<>();
		for(Entry<String, List<Map<String, Object>>> entry: values.entrySet()) {
			String rdmKey = entry.getKey();
			long rdmId = rdmMap.get(rdmKey).getId();
			inputRdmIds.add(rdmId);
			entry.getValue().forEach(value -> {
				value.put("rdmId", rdmId);
			});
		}
		
		
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("rdmId"), inputRdmIds, NumberOperators.EQUALS));
		deleteBuilder.delete();
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(fields)
				.table(module.getTableName())
				.addRecords(values.values().stream().flatMap(List::stream).collect(Collectors.toList()));
		insertBuilder.save();
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
					context.put("prevUnit", point.get("prevUnit"));
				}
				
				context.put("id", point.get("id"));
				context.put(ContextNames.FIELD_ID, point.get(AgentConstants.FIELD_ID));
				context.put(ContextNames.PARENT_ID, point.get(AgentConstants.RESOURCE_ID));
				FacilioTimer.scheduleInstantJob("datamigration","MigrateReadingData", context);
			}
		}
		
		return false;
	}

}
