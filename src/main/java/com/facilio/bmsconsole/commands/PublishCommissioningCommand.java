package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class PublishCommissioningCommand extends FacilioCommand implements PostTransactionCommand {
	
	private static List<Map<String, Object>> unmodelledPoints;
	private static List<Map<String, Object>> modelledPoints;
	
	private FacilioModule module = ModuleFactory.getPointModule();
	private List<FacilioField> fields = FieldFactory.getPointFields();

	@Override
	public boolean executeCommand(Context context) throws Exception {

		long id = (long) context.get(ContextNames.ID);
		CommissioningLogContext log = CommissioningApi.commissioniongDetails(id);
		validate(log);
		
		long publishTime = System.currentTimeMillis();
		
		List<ReadingDataMeta> writableReadingList = new ArrayList<>();
		List<ReadingDataMeta> unitRdmList = new ArrayList<>();
		List<ReadingDataMeta> remainingReadingList = new ArrayList<>();

		List<Map<String, Object>> dbPoints = getDbPoints(log);
		Map<Long, Map<String, Object>> pointMap = null;
		if (dbPoints != null) {
			pointMap = dbPoints.stream().collect(Collectors.toMap(point -> (long)point.get("id"), point-> point));
		}
		for(int i = 0; i < log.getPoints().size(); i++) {
			Map<String, Object> point = (Map<String, Object>) log.getPoints().get(i);
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
						point.put("oldCategoryId", dbPoint.get(AgentConstants.ASSET_CATEGORY_ID));
						point.put("oldFieldId", dbFieldId);
						point.put("oldResourceId", dbResourceId);
						point.put("oldUnit", dbPoint.get(AgentConstants.UNIT));
						if (modelledPoints == null) {
							modelledPoints = new ArrayList<>();
						}
						modelledPoints.add(point);
					}
					else {
						if (unmodelledPoints == null) {
							unmodelledPoints = new ArrayList<>();
						}
						unmodelledPoints.add(point);
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
					}
					else {
						if (writable) {
							writableReadingList.add(meta);
						}
						else {
							remainingReadingList.add(meta);
						}
					}
				}
				updatePoint(point, publishTime);
			}
			
		}
		
		if (!unitRdmList.isEmpty()) {
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
		
		updateLog(publishTime, id);
		
		// TODO
		// Migration 
		// Enum

		return false;
	}

	private void validate(CommissioningLogContext log) throws Exception {
		if (log.getPoints() == null || log.getPoints().isEmpty()) {
			throw new IllegalArgumentException("No points mapped");
		}
		if (log.getPublishedTime() != -1) {
			throw new IllegalArgumentException("This log has already been published");
		}
		
		CommissioningApi.checkRDMType(log.getPoints());
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
	

	@Override
	public boolean postExecute() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
