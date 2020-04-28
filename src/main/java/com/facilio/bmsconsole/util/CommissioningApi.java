package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;


public class CommissioningApi {
	public static CommissioningLogContext commissioniongDetails(long id) throws Exception {
		return commissioniongDetails(id, true);
	}
	
	public static CommissioningLogContext commissioniongDetails(long id, boolean fetchControllers) throws Exception {
		List<CommissioningLogContext> logs = commissioniongList(Collections.singletonList(id), fetchControllers, null);
		if (CollectionUtils.isNotEmpty(logs)) {
			return logs.get(0);
		}
		return null;
	}
	
	public static List<CommissioningLogContext> commissioniongList(List<Long> ids, boolean fetchControllers, JSONObject pagination) throws Exception {
		FacilioModule module = ModuleFactory.getCommissioningLogModule();
		List<FacilioField> fields = FieldFactory.getCommissioningLogFields();
		if (ids == null || ids.size() > 1) {
			fields.removeIf(field -> field.getName().equals("pointJsonStr") || field.getName().equals("clientMetaStr"));
		}
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.orderBy(fieldMap.get("sysCreatedTime").getColumnName() + " desc");
		if (ids != null) {
			builder.andCondition(CriteriaAPI.getIdCondition(ids, module));
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
		
		List<Map<String, Object>> props = builder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			List<CommissioningLogContext> logs = FieldUtil.getAsBeanListFromMapList(props, CommissioningLogContext.class);
			if (fetchControllers) {
				List<Long> logIds = new ArrayList<>();
				Set<Long> agentIds = new HashSet<>();
				for(CommissioningLogContext log: logs) {
					if (log.getPoints() != null) {
						setPointContext(log);
					}
					
					logIds.add(log.getId());
					agentIds.add(log.getAgentId());
				}
				
				Map<Long, List<Map<String, Object>>> controllerMap = getCommissionedControllers(logIds);
				List<FacilioAgent> agents = AgentApiV2.getAgents(agentIds);
				Map<Long, FacilioAgent> agentMap = agents.stream().collect(Collectors.toMap(FacilioAgent::getId, Function.identity()));
				
				for(CommissioningLogContext log: logs) {
					log.setControllers(controllerMap.get(log.getId()));
					log.setAgent(agentMap.get(log.getAgentId()));
				}
			}
			
			return logs;
		}
		
		return null;
	}
	
	public static void setPointContext(CommissioningLogContext log) throws Exception {
		Class pointType = PointsAPI.getPointType(log.getControllerTypeEnum());
		log.setPointList(FieldUtil.getAsBeanListFromJsonArray(log.getPoints(), pointType));
	}
	
	private static Map<Long, List<Map<String, Object>>> getCommissionedControllers(List<Long> logIds) throws Exception {
		FacilioModule module = ModuleFactory.getCommissioningLogControllerModule();
		List<FacilioField> fields = FieldFactory.getCommissioningLogControllerFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		Map<Long, List<Map<String, Object>>> logControllerMap = new HashMap<>();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("commissioningLogId"), logIds, NumberOperators.EQUALS));
		List<Map<String, Object>> props = builder.get();
		List<Long> controllerIds = props.stream().map(prop -> (long) prop.get("controllerId")).collect(Collectors.toList());
		Map<Long, Map<String, Object>> controllersMap = ResourceAPI.getResourceMapFromIds(controllerIds, true);
		
		for(Map<String, Object> prop: props) {
			long logId = (long) prop.get("commissioningLogId");
			List<Map<String, Object>> controllers = logControllerMap.get(logId);
			if (controllers == null) {
				controllers = new ArrayList<>();
				logControllerMap.put(logId, controllers);
			}
			controllers.add(controllersMap.get((Long) prop.get("controllerId")));
		}
		return logControllerMap;
	}
	
	public static void updateLog(CommissioningLogContext log) throws Exception {
		log.setAgentId(-1);
		FacilioModule module = ModuleFactory.getCommissioningLogModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getCommissioningLogFields())
				.andCondition(CriteriaAPI.getIdCondition(log.getId(), module));

		Map<String, Object> prop = FieldUtil.getAsProperties(log);
		updateBuilder.update(prop);
	}

	public static Map<String, ReadingDataMeta> checkRDMType(List<Pair<Long, FacilioField>> rdmPairs) throws Exception {
		List<ReadingDataMeta> rdmList = ReadingsAPI.getReadingDataMetaList(rdmPairs);
			for(ReadingDataMeta rdm: rdmList) {
				switch (rdm.getInputTypeEnum()) {
				case CONTROLLER_MAPPED:
					throw new IllegalArgumentException(rdm.getField().getDisplayName() + " is already mapped");
				case FORMULA_FIELD:
				case HIDDEN_FORMULA_FIELD:
					throw new IllegalArgumentException(rdm.getField().getDisplayName() +" is formula field and therefore cannot be mapped");
			}
		}
		return rdmList.stream().collect(Collectors.toMap(rdm -> ReadingsAPI.getRDMKey(rdm), Function.identity()));
	}
	
	
	public static void filterAndValidatePointsOnUpdate(CommissioningLogContext log, CommissioningLogContext oldLog) throws Exception {
		if (oldLog == null) {
			oldLog = log;
		}
		if (oldLog.getPublishedTime() != -1) {
			throw new IllegalArgumentException("This has already been published");
		}
		
		List<Point> points = log.getPointList();
		List<Long> pointIds = points.stream().map(Point::getId).collect(Collectors.toList());
		FacilioModule module = ModuleFactory.getPointModule();
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getIdCondition(pointIds, module));
		
		GetPointRequest getPointRequest = new GetPointRequest()
				.ofType(oldLog.getControllerTypeEnum())
				.limit(-1)
				.withCriteria(criteria);
		List<Point> dbPoints = getPointRequest.getPoints();
		Map<Long, Point> dbPointMap = dbPoints.stream().collect(Collectors.toMap(Point::getId, Function.identity()));
		
		List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
		JSONArray finalPoints = new JSONArray();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(Point point: points) {
			Long categoryId = point.getCategoryId();
			Long resourceId = point.getResourceId();
			Long fieldId = point.getFieldId();
			int unit = point.getUnit();
			
			Point dbPoint = dbPointMap.get(point.getId());
			Long dbResourceId = dbPoint.getResourceId();
			Long dbFieldId = dbPoint.getFieldId();
			
			if (dbResourceId != null && dbFieldId != null) {
				if (resourceId == null || fieldId == null) {
					// TODO Change rdm type
				}
				else if (!dbResourceId.equals(resourceId) || !dbFieldId.equals(fieldId)) {
					rdmPairs.add(Pair.of(resourceId, modBean.getField(fieldId)));
				}
			}
			else if (resourceId != null && fieldId != null) {		// New mapping
				rdmPairs.add(Pair.of(resourceId, modBean.getField(fieldId)));
			}
			
			/*if ((dbResourceId != null && (resourceId == null || !dbResourceId.equals(resourceId))) || 
					(dbFieldId != null && (fieldId == null || !dbFieldId.equals(fieldId))) ) {
				// TODO Change rdm type
//				finalPoints.add(point);
				if (resourceId != null && fieldId != null) {	// Both values are changed here
					rdmPairs.add(Pair.of(resourceId, modBean.getField(fieldId)));
				}
			}
			else if (resourceId != null && fieldId != null) {		// New mapping
				rdmPairs.add(Pair.of(resourceId, modBean.getField(fieldId)));
			}*/
			/*else if (resourceId != null || fieldId != null || categoryId != null || dbPoint.getUnit() != unit) {
				finalPoints.add(point);
			}*/
		}
//		log.setPoints(finalPoints);
		
		if (!rdmPairs.isEmpty()) {
			checkRDMType(rdmPairs);
		}
	}
	
}
