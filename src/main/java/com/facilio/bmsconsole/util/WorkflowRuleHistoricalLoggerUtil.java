package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LoggerContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class WorkflowRuleHistoricalLoggerUtil {
	
	public static void addWorkflowRuleHistoricalLogger(WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleHistoricalLoggerFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleHistoricalLoggerContext);
		insertBuilder.addRecord(props);
		
		insertBuilder.save();
		
		workflowRuleHistoricalLoggerContext.setId((Long) props.get("id"));
		
	}
	
	public static WorkflowRuleHistoricalLoggerContext getWorkflowRuleHistoricalLoggerById (long loggerRuleId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+loggerRuleId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowRuleHistoricalLoggerContext.class);
			return workflowRuleHistoricalLogger;
		}
		return null;
	}
	
	public static List<WorkflowRuleHistoricalLoggerContext> getActiveRuleHistoricalLogger(long ruleId, List<Long> resourceIds) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleHistoricalLoggerFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), resourceIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ WorkflowRuleHistoricalLoggerContext.Status.IN_PROGRESS.getIndex(), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<WorkflowRuleHistoricalLoggerContext> workflowRuleHistoricalLoggerList = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleHistoricalLoggerContext.class);
			return workflowRuleHistoricalLoggerList;
		}
		return null;
	}
	
	public static List<Long> getGroupedRuleResourceWorkflowRuleHistoricalLoggerIds(long ruleResourceLoggerId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleHistoricalLoggerFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(Collections.singletonList(fieldMap.get("id")))
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleResourceLoggerId"), "" +ruleResourceLoggerId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<Long> workflowRuleHistoricalLoggerIds = new ArrayList<Long>();
		
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props ) {
				Long workflowRuleHistoricalLoggerContextId = (Long) prop.get("id");
				workflowRuleHistoricalLoggerIds.add(workflowRuleHistoricalLoggerContextId);
			}
		}
		return workflowRuleHistoricalLoggerIds;
	}
	
	public static List<Long> getActiveGroupedRuleResourceWorkflowRuleHistoricalLoggerIds(long ruleResourceLoggerId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleHistoricalLoggerFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(Collections.singletonList(fieldMap.get("id")))
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ WorkflowRuleHistoricalLoggerContext.Status.IN_PROGRESS.getIndex(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleResourceLoggerId"), "" +ruleResourceLoggerId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<Long> workflowRuleHistoricalLoggerIds = new ArrayList<Long>();
		
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props ) {
				Long workflowRuleHistoricalLoggerContextId = (Long) prop.get("id");
				workflowRuleHistoricalLoggerIds.add(workflowRuleHistoricalLoggerContextId);
			}
		}
		return workflowRuleHistoricalLoggerIds;
	}

	public static List<WorkflowRuleHistoricalLoggerContext> getGroupedRuleResourceWorkflowRuleHistoricalLogger(long ruleResourceLoggerId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleHistoricalLoggerFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleResourceLoggerId"), "" +ruleResourceLoggerId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<WorkflowRuleHistoricalLoggerContext> workflowRuleHistoricalLoggerContextList = new ArrayList<WorkflowRuleHistoricalLoggerContext>();
		
		if (props != null && !props.isEmpty()) {
			
			List<Long> resourceIds = new ArrayList<Long>();	
			for(Map<String, Object> prop : props ) {
				WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = FieldUtil.getAsBeanFromMap(prop, WorkflowRuleHistoricalLoggerContext.class);
				workflowRuleHistoricalLoggerContextList.add(workflowRuleHistoricalLoggerContext);
				resourceIds.add(workflowRuleHistoricalLoggerContext.getResourceId());
			}
			
			List<ResourceContext> resources = ResourceAPI.getResources(resourceIds,true);
			Map<Long, ResourceContext> resourcesMap = new LinkedHashMap<Long, ResourceContext>();
			
			for(ResourceContext resource:resources)
			{
				resourcesMap.put(resource.getId(), resource);
			}
			
			for(WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext :workflowRuleHistoricalLoggerContextList) {
				workflowRuleHistoricalLoggerContext.setResourceContext(resourcesMap.get(workflowRuleHistoricalLoggerContext.getResourceId()));
			}
		}
		return workflowRuleHistoricalLoggerContextList;
	}
	
	public static List<WorkflowRuleHistoricalLoggerContext> getGroupedWorkflowRuleHistoricalLogger(long loggerGroupId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("LOGGER_GROUP_ID", "loggerGroupId", ""+loggerGroupId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<WorkflowRuleHistoricalLoggerContext> workflowRuleHistoricalLoggerContextList = new ArrayList<WorkflowRuleHistoricalLoggerContext>();
		
		if (props != null && !props.isEmpty()) {
			
			List<Long> resourceIds = new ArrayList<Long>();	
			for(Map<String, Object> prop : props ) {
				WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = FieldUtil.getAsBeanFromMap(prop, WorkflowRuleHistoricalLoggerContext.class);
				workflowRuleHistoricalLoggerContextList.add(workflowRuleHistoricalLoggerContext);
				resourceIds.add(workflowRuleHistoricalLoggerContext.getResourceId());
			}
			
			List<ResourceContext> resources = ResourceAPI.getResources(resourceIds,true);
			Map<Long, ResourceContext> resourcesMap = new LinkedHashMap<Long, ResourceContext>();
			
			for(ResourceContext resource:resources)
			{
				resourcesMap.put(resource.getId(), resource);
			}
			
			for(WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext :workflowRuleHistoricalLoggerContextList) {
				workflowRuleHistoricalLoggerContext.setResourceContext(resourcesMap.get(workflowRuleHistoricalLoggerContext.getResourceId()));
			}
		}
		return workflowRuleHistoricalLoggerContextList;
	}
	
	public static List<WorkflowRuleHistoricalLoggerContext> getGroupedInProgressWorkflowRuleHistoricalLoggers(long loggerGroupId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("LOGGER_GROUP_ID", "loggerGroupId", ""+loggerGroupId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ WorkflowRuleHistoricalLoggerContext.Status.IN_PROGRESS.getIndex(), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<WorkflowRuleHistoricalLoggerContext> workflowRuleHistoricalLoggerContextList = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleHistoricalLoggerContext.class);
			return workflowRuleHistoricalLoggerContextList;
		}
		return null;
	}
	
	public static WorkflowRuleHistoricalLoggerContext getWorkflowRuleHistoricalLogger(long id) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowRuleHistoricalLoggerContext.class);
			return workflowRuleHistoricalLogger;
		}
		return null;
	}
	
	public static List<WorkflowRuleHistoricalLoggerContext> getAllWorkflowRuleHistoricalLogger() throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.orderBy("STATUS");
				
				List<Map<String, Object>> props = selectBuilder.get();
				if (props != null && !props.isEmpty()) {
					List<WorkflowRuleHistoricalLoggerContext> workflowRuleHistoricalLoggerContextList = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleHistoricalLoggerContext.class);
					return workflowRuleHistoricalLoggerContextList;
				}
				return null;
	}
	
	public static Collection<WorkflowRuleHistoricalLoggerContext> getAllParentWorkflowRuleHistoricalLogger(long ruleId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("LOGGER_GROUP_ID", "loggerGroupId", "id", NumberOperators.EQUALS))
				.orderBy("STATUS,CREATED_TIME DESC");
				
				List<Map<String, Object>> props = selectBuilder.get();
				List<WorkflowRuleHistoricalLoggerContext> workflowRuleParentHistoricalLoggerContextList = new ArrayList<WorkflowRuleHistoricalLoggerContext>();
				if (props != null && !props.isEmpty()) {
					workflowRuleParentHistoricalLoggerContextList = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleHistoricalLoggerContext.class);
				}
				
				Map<Long, WorkflowRuleHistoricalLoggerContext> workflowRuleHistoricalLoggerContextMap = new HashMap <Long, WorkflowRuleHistoricalLoggerContext>();
				for(WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext :workflowRuleParentHistoricalLoggerContextList)
				{
					workflowRuleHistoricalLoggerContextMap.put(workflowRuleHistoricalLoggerContext.getLoggerGroupId(), workflowRuleHistoricalLoggerContext);
				}
				
				
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleHistoricalLoggerFields());
				
				FacilioField countField = BmsAggregateOperators.CommonAggregateOperator.COUNT.getSelectField(fieldMap.get("loggerGroupId"));
				countField.setName("count");
				FacilioField sumField = BmsAggregateOperators.NumberAggregateOperator.SUM.getSelectField(fieldMap.get("alarmCount"));
				sumField.setName("sum");
				List<FacilioField> selectFields = new ArrayList<FacilioField>();
				selectFields.add(countField);
				selectFields.add(fieldMap.get("loggerGroupId"));
				selectFields.add(sumField);
				
				selectBuilder = new GenericSelectRecordBuilder()
						.select(selectFields)
						.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
						.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
						.groupBy("LOGGER_GROUP_ID");
				
				
				List<Map<String, Object>> propsList = selectBuilder.get();
				
				if(!propsList.isEmpty())
				{
					for(Map<String, Object> prop :propsList)
					{
						WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = workflowRuleHistoricalLoggerContextMap.get((long) prop.get("loggerGroupId"));
						if(workflowRuleHistoricalLoggerContext != null)
						{
							if(prop.get("count") != null) {
								workflowRuleHistoricalLoggerContext.setResourceLogCount((long) prop.get("count"));	
							}
							if(prop.get("sum") != null)
							{
								workflowRuleHistoricalLoggerContext.setTotalChildAlarmCount(Integer.valueOf(String.valueOf(prop.get("sum"))));
							}
							
							workflowRuleHistoricalLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.IN_PROGRESS.getIndex());
							
							long loggerGroupId = (long) prop.get("loggerGroupId");
							List<WorkflowRuleHistoricalLoggerContext> activeChildLoggers = getGroupedInProgressWorkflowRuleHistoricalLoggers(loggerGroupId);
							if(activeChildLoggers == null || activeChildLoggers.isEmpty())
							{
								workflowRuleHistoricalLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.RESOLVED.getIndex());
								workflowRuleHistoricalLoggerContext.setResolvedLogCount(workflowRuleHistoricalLoggerContext.getResourceLogCount());					
							}
							else
							{
								if(activeChildLoggers != null && workflowRuleHistoricalLoggerContext.getResourceLogCount() > 0 && workflowRuleHistoricalLoggerContext.getResourceLogCount() >= activeChildLoggers.size())
								{
									long resolvedResourceCount = workflowRuleHistoricalLoggerContext.getResourceLogCount() - activeChildLoggers.size();
									workflowRuleHistoricalLoggerContext.setResolvedLogCount(resolvedResourceCount);
								}
							}
						}			
					}		
				}
				
				return workflowRuleHistoricalLoggerContextMap.values();
	}
	
	public static void updateRuleLoggerContextToResolvedState(WorkflowRuleHistoricalLoggerContext parentRuleResourceLoggerContext) throws Exception {
		parentRuleResourceLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
		parentRuleResourceLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.RESOLVED.getIndex());
		updateWorkflowRuleHistoricalLogger(parentRuleResourceLoggerContext);	
	}
	
	public static void updateWorkflowRuleHistoricalLogger(WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+workflowRuleHistoricalLogger.getId(), NumberOperators.EQUALS));		

		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleHistoricalLogger);
		updateBuilder.update(props);
	}
	
	public static int updateEventGeneratingParentWorkflowRuleHistoricalLogger(WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+workflowRuleHistoricalLogger.getId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ WorkflowRuleHistoricalLoggerContext.Status.EVENT_GENERATING_STATE.getIndex(), NumberOperators.EQUALS));

		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleHistoricalLogger);
		int rowsUpdated = updateBuilder.update(props);
		return rowsUpdated;
	}
	
	public static void deleteWorkflowRuleHistoricalLogger(long id) throws SQLException {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
		.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));
		
	}
	
	public static void deleteReadingAlarm(long ruleId, Long startTime, Long endTime, long resourceId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.READING_ALARM);
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.innerJoin("Event", true)
				.on("Event.ALARM_ID = Reading_Alarms.ID")
				.innerJoin("Alarms", false)
				.on("Alarms.ID = Reading_Alarms.ID")
				.innerJoin("Tickets", true)
				.on("Tickets.ID = Reading_Alarms.ID")
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("Tickets.RESOURCE_ID", "resource", ""+resourceId, NumberOperators.EQUALS));
		
		Criteria criteria = new Criteria();
		
		Criteria subCriteria = new Criteria();
		subCriteria.addAndCondition(CriteriaAPI.getCondition("Alarms.CREATED_TIME", "createdTime", ""+endTime, NumberOperators.LESS_THAN));
		subCriteria.addAndCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", ""+startTime, NumberOperators.GREATER_THAN));
		
		criteria.addOrCondition(CriteriaAPI.getCondition("Alarms.CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));
		criteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", startTime+","+endTime, DateOperators.BETWEEN));	
		criteria.orCriteria(subCriteria);
		
		builder.andCriteria(criteria);
		builder.delete();		
		
	}

	public static void deleteAlarmOccurrencesWithEdgeCases(long ruleId, Long startTime, Long endTime, long resourceId) throws Exception {
		
		NewAlarmAPI.deleteIntervalBasedAlarmOccurrences(ruleId, startTime, endTime, resourceId);	
	}
	
	public static WorkflowRuleHistoricalLoggerContext setWorkflowRuleHistoricalLoggerContext(long ruleId, DateRange range,Long resourceId, long loggerGroupId)
	{
		WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = new WorkflowRuleHistoricalLoggerContext();
		workflowRuleHistoricalLoggerContext.setRuleId(ruleId);
		workflowRuleHistoricalLoggerContext.setType(WorkflowRuleHistoricalLoggerContext.Type.READING_RULE.getIndex());
		workflowRuleHistoricalLoggerContext.setResourceId(resourceId);
		workflowRuleHistoricalLoggerContext.setStatus(WorkflowRuleHistoricalLoggerContext.Status.IN_PROGRESS.getIndex());
		workflowRuleHistoricalLoggerContext.setLoggerGroupId(loggerGroupId);
		workflowRuleHistoricalLoggerContext.setStartTime(range.getStartTime());
		workflowRuleHistoricalLoggerContext.setEndTime(range.getEndTime());
		workflowRuleHistoricalLoggerContext.setCreatedBy(AccountUtil.getCurrentUser().getId());
		workflowRuleHistoricalLoggerContext.setCreatedTime(DateTimeUtil.getCurrenTime());
		return workflowRuleHistoricalLoggerContext;	
	}
}
