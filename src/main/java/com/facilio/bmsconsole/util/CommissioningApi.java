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

import org.apache.commons.chain.Context;
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
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;


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
		String message = null; 
		for(ReadingDataMeta rdm: rdmList) {
			switch (rdm.getInputTypeEnum()) {
				case CONTROLLER_MAPPED:
					message = " is already mapped";
					break;
				case FORMULA_FIELD:
				case HIDDEN_FORMULA_FIELD:
					throw new IllegalArgumentException(rdm.getField().getDisplayName() + " is formula field and therefore cannot be mapped");
				case TASK:
					message = " is already used in task and therefore cannot be mapped";
					break;
			}
			if (message != null) {
				ResourceContext resource = ResourceAPI.getResource(rdm.getResourceId());
				StringBuilder builder = new StringBuilder(rdm.getField().getDisplayName())
						.append(" reading of ").append(resource.getName()).append(message);
				throw new IllegalArgumentException(builder.toString());
			}
		}
		return rdmList.stream().collect(Collectors.toMap(rdm -> ReadingsAPI.getRDMKey(rdm), Function.identity()));
	}
	
	public static Long checkDraftMode(List<Long> controllerIds) throws Exception {
		FacilioModule module = ModuleFactory.getCommissioningLogModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getCommissioningLogFields());
		FacilioModule controllerModule = ModuleFactory.getCommissioningLogControllerModule();
		Map<String, FacilioField> controllerFieldMap = FieldFactory.getAsMap(FieldFactory.getCommissioningLogControllerFields());
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.innerJoin(controllerModule.getTableName())
				.on(fieldMap.get("id").getCompleteColumnName()+"="+controllerFieldMap.get("commissioningLogId").getCompleteColumnName())
				.select(Collections.singletonList(fieldMap.get("id")))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("publishedTime"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(controllerFieldMap.get("controllerId"), controllerIds, NumberOperators.EQUALS));
				;
				
		Map<String, Object> props = builder.fetchFirst();
		if (props != null && !props.isEmpty()) {
			return (long) props.get("id");
		}
		return null;
	}
	
	
	public static  Map<String, ReadingDataMeta> filterAndValidatePointsOnUpdate(CommissioningLogContext log, CommissioningLogContext oldLog) throws Exception {
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
			return checkRDMType(rdmPairs);
		}
		return null;
	}
	
	public static Map<Long, String> getResources(Set<Long> resourceIds) throws Exception {
		if (!resourceIds.isEmpty()) {
			FacilioChain chain = FacilioChainFactory.getPickListChain();
			Context picklistContext = chain.getContext();
			picklistContext.put(FacilioConstants.ContextNames.MODULE_NAME, ContextNames.RESOURCE);
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getIdCondition(resourceIds, null));
			picklistContext.put(ContextNames.FILTER_CRITERIA, criteria);
			chain.execute();
			return (Map<Long, String>) picklistContext.get(ContextNames.PICKLIST);
		}
		return null;
	}

	public static Map<Long, Map<String, Object>> getFields(Set<Long> fieldIds) throws Exception {
		if (!fieldIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getFields(fieldIds);
			if (fields != null) {
				Map<Long, Map<String, Object>> fieldMap = new HashMap<>();
				for(FacilioField field: fields) {
					Map<String, Object> fieldDetail = new HashMap<>();
					fieldDetail.put("name", field.getDisplayName());
					fieldDetail.put("dataType", field.getDataType());
					if (field instanceof NumberField) {
						fieldDetail.put("metric", ((NumberField) field).getMetric());
					}
					fieldMap.put(field.getId(), fieldDetail);
				}
				return fieldMap;
			}
		}
		return null;
	}
	
}
