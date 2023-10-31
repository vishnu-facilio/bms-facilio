package com.facilio.bmsconsole.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.moduleconfig.AddCommissioningLogModule;
import com.facilio.modules.*;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Get;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
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
		return commissioniongList(ids,fetchControllers,pagination,null,null);
	}
	public static List<CommissioningLogContext> commissioniongList(List<Long> ids, boolean fetchControllers, JSONObject pagination,String status,Criteria criteria) throws Exception {

		List<Map<String,Object>>props = new ArrayList<>();
		SelectRecordsBuilder builder = getLogsBuilder(ids,status,criteria);
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
		props = builder.getAsProps();


		if (CollectionUtils.isNotEmpty(props)) {
			List<CommissioningLogContext> logs = FieldUtil.getAsBeanListFromMapList(props, CommissioningLogContext.class);
			if (fetchControllers) {
				List<Long> logIds = new ArrayList<>();
				Set<Long> agentIds = new HashSet<>();
				for(CommissioningLogContext log: logs) {
					if (log.getPoints() != null) {
						setPointContext(log);
					}
					if (log.isLogical()) {
						Map<String,Object> logicalContrller = Collections.singletonMap("name", "Logical");
						log.setControllers(Collections.singletonList(logicalContrller));
					}
					logIds.add(log.getId());
					agentIds.add(log.getAgentId());
				}

				Map<Long, List<Map<String, Object>>> controllerMap = getCommissionedControllers(logIds);
				AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
				Map<Long, FacilioAgent> agentMap = agentBean.getAgentMap(agentIds);

				for(CommissioningLogContext log: logs) {
					List<Map<String, Object>> controllers = controllerMap.get(log.getId());
					if (controllers != null) {
						log.setControllers(controllers);
					}
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

		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (modbean.getModule(AgentConstants.COMMISSIONINGLOG_CONTROLLER)!= null){
			FacilioModule module = modbean.getModule(AgentConstants.COMMISSIONINGLOG_CONTROLLER);
			List<FacilioField>fields = modbean.getAllFields(AgentConstants.COMMISSIONINGLOG_CONTROLLER);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			Map<Long, List<Map<String, Object>>> logControllerMap = new HashMap<>();
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(fields)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("left"), logIds, NumberOperators.EQUALS));
			List<Map<String, Object>> props = builder.get();
			if (props != null) {
				List<Long> controllerIds = props.stream().map(prop -> (long) prop.get("right")).collect(Collectors.toList());
				Map<Long, Map<String, Object>> controllersMap = ResourceAPI.getResourceMapFromIds(controllerIds, true);

				for(Map<String, Object> prop: props) {
					long logId = (long) prop.get("left");
					List<Map<String, Object>> controllers = logControllerMap.get(logId);
					if (controllers == null) {
						controllers = new ArrayList<>();
						logControllerMap.put(logId, controllers);
					}
					controllers.add(controllersMap.get((Long) prop.get("right")));
				}
			}
			return logControllerMap;
		}
		else{
			FacilioModule module = ModuleFactory.getCommissioningLogControllerModule();
			List<FacilioField> fields = FieldFactory.getCommissioningLogControllerFields();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			Map<Long, List<Map<String, Object>>> logControllerMap = new HashMap<>();
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(fields)
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("commissioningLogId"), logIds, NumberOperators.EQUALS));
			List<Map<String, Object>> props = builder.get();
			if (props != null) {
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
			}
			return logControllerMap;
		}
	}
	
	public static void updateLog(CommissioningLogContext log) throws Exception {
//		log.setAgentId(-1);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.COMMISSIONING_LOG);

		List<FacilioField>fields = modBean.getAllFields(module.getName());
		UpdateRecordBuilder updateBuilder = new UpdateRecordBuilder()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(log.getId(), module));
		Map<String,Object>map = FieldUtil.getAsProperties(log);
		updateBuilder.updateViaMap(map);
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
	
	public static Long checkDraftMode(long agentId, List<Long> controllerIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.COMMISSIONING_LOG);

		List<FacilioField> fields = modBean.getAllFields(module.getName());
		fields.add(FieldFactory.getIdField(module));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		if(modBean.getModule(AgentConstants.COMMISSIONINGLOG_CONTROLLER) != null) {
			FacilioModule controllerModule = modBean.getModule(AgentConstants.COMMISSIONINGLOG_CONTROLLER);
			Map<String, FacilioField> controllerFieldMap = FieldFactory.getAsMap(modBean.getAllFields(AgentConstants.COMMISSIONINGLOG_CONTROLLER));

			SelectRecordsBuilder builder = new SelectRecordsBuilder()
					.beanClass(CommissioningLogContext.class)
					.module(module)
					.select(Collections.singletonList(fieldMap.get("id")))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("publishedTime"), CommonOperators.IS_EMPTY));

			if (CollectionUtils.isNotEmpty(controllerIds)) {
				builder.innerJoin(controllerModule.getTableName())
						.on(fieldMap.get("id").getCompleteColumnName() + "=" + controllerFieldMap.get("left").getCompleteColumnName())
						.andCondition(CriteriaAPI.getCondition(controllerFieldMap.get("right"), controllerIds, NumberOperators.EQUALS));
			} else {
				builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(module), String.valueOf(agentId), NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("logical"), String.valueOf(true), BooleanOperators.IS));
			}
			List<CommissioningLogContext>props = builder.get();
			if (props != null  && !props.isEmpty()) {
				return (long)props.get(0).getId();
			}
		}
		else {
			FacilioModule controllerModule = ModuleFactory.getCommissioningLogControllerModule();

			Map<String, FacilioField> controllerFieldMap = FieldFactory.getAsMap(FieldFactory.getCommissioningLogControllerFields());

			SelectRecordsBuilder builder = new SelectRecordsBuilder()
					.beanClass(CommissioningLogContext.class)
					.module(module)
					.select(Collections.singletonList(fieldMap.get("id")))
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("publishedTime"), CommonOperators.IS_EMPTY));

			if (CollectionUtils.isNotEmpty(controllerIds)) {
				builder.innerJoin(controllerModule.getTableName())
						.on(fieldMap.get("id").getCompleteColumnName() + "=" + controllerFieldMap.get("commissioningLogId").getCompleteColumnName())
						.andCondition(CriteriaAPI.getCondition(controllerFieldMap.get("controllerId"), controllerIds, NumberOperators.EQUALS));
			} else {
				builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(module), String.valueOf(agentId), NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("logical"), String.valueOf(true), BooleanOperators.IS));
			}
			List<CommissioningLogContext>props = builder.get();
			if (props != null  && !props.isEmpty()) {
				return (long)props.get(0).getId();
			}
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
		FacilioModule pointModule = AgentConstants.getPointModule();
		if (pointModule == null){
			pointModule = ModuleFactory.getPointModule();
		}
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getIdCondition(pointIds, pointModule));
		
		GetPointRequest getPointRequest = new GetPointRequest()
				.ofType(oldLog.getControllerTypeEnum())
				.limit(-1)
				.withCriteria(criteria);
		List<Point> dbPoints = getPointRequest.getPoints();
		Map<Long, Point> dbPointMap = dbPoints.stream().collect(Collectors.toMap(Point::getId, Function.identity()));
		
		List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<String> mappedDetials = new ArrayList<>(); 
		
		for(Point point: points) {
			Long categoryId = point.getCategoryId();
			Long resourceId = point.getResourceId();
			Long fieldId = point.getFieldId();

			validatePoint(point.getName(), categoryId, resourceId, fieldId, point.getUnit());
			
			Point dbPoint = dbPointMap.get(point.getId());
			Long dbResourceId = dbPoint.getResourceId();
			Long dbFieldId = dbPoint.getFieldId();
			
			if(resourceId != null && fieldId != null) {
				String key = resourceId+"_"+ fieldId;
				if (mappedDetials.contains(key)) {
					ResourceContext resource = ResourceAPI.getResource(resourceId);
					StringBuilder builder = new StringBuilder(modBean.getField(fieldId).getDisplayName())
							.append(" reading of ").append(resource.getName()).append(" is mapped more than once.");
					throw new IllegalArgumentException(builder.toString());
				}
				mappedDetials.add(key);
				
				if (dbResourceId != null && dbFieldId != null) {
					if (!dbResourceId.equals(resourceId) || !dbFieldId.equals(fieldId)) {
						rdmPairs.add(Pair.of(resourceId, modBean.getField(fieldId)));
					}
				}
				// New mapping
				else {
					rdmPairs.add(Pair.of(resourceId, modBean.getField(fieldId)));
				}
			}
		}			
		
		if (!rdmPairs.isEmpty()) {
			return checkRDMType(rdmPairs);
		}
		return null;
	}

	private static void validatePoint(String name, Long categoryId, Long resourceId, Long fieldId, int unit) throws Exception {
		if (categoryId == null && (resourceId != null || fieldId != null || unit > 0) ) {
			throw new IllegalArgumentException(name + " cannot be mapped without selecting category");
		}
		if (unit > 0 && fieldId == null) {
			throw new IllegalArgumentException("Unit for " + name + " cannot be mapped without selecting reading");
		}
	}
	@Deprecated
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

	public static Map<Long, String> getParent(Set<Long> ids,String moduleName) throws Exception {
		if (moduleName.equals("asset")){
			moduleName = ContextNames.RESOURCE;
		}
		if (!ids.isEmpty()) {
			FacilioChain chain = FacilioChainFactory.getPickListChain();
			Context picklistContext = chain.getContext();
			picklistContext.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getIdCondition(ids, null));
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
	public static Long getCommissioningListCount(List<Long> ids,String status,Criteria criteria)throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.COMMISSIONING_LOG);

		SelectRecordsBuilder builder = getLogsBuilder(ids, status,criteria);
		builder.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(module));
		builder.select(new ArrayList<>());
		List<Map<String, Object>> result = builder.getAsProps();
		Long count = (long) result.get(0).get(AgentConstants.ID);

		return count;
	}
	public static SelectRecordsBuilder getLogsBuilder(List<Long> ids,String status,Criteria criteria) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.COMMISSIONING_LOG);
		List<FacilioField>fields = modBean.getAllFields(module.getName());
		fields.add(FieldFactory.getIdField());
		if (ids == null || ids.size() > 1) {
			fields.removeIf(field -> field.getName().equals("pointJsonStr") || field.getName().equals("clientMetaStr"));
		}
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder builder = new SelectRecordsBuilder();

		builder.beanClass(CommissioningLogContext.class).module(module)
				.select(fields)
				.orderBy(fieldMap.get("sysCreatedTime").getColumnName() + " desc");
		if (status != null) {
			if (status.equals("draft")) {
				builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("publishedTime"), CommonOperators.IS_EMPTY));
			} else if (status.equals("published")) {
				builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("publishedTime"), CommonOperators.IS_NOT_EMPTY));
			}
		}
		if (ids != null) {
			builder.andCondition(CriteriaAPI.getIdCondition(ids, module));
		}
		if(criteria != null){
			if(criteria.getConditions() != null && !criteria.getConditions().isEmpty()){
				builder.andCriteria(criteria);
			}
		}
		return builder;
	}
}
