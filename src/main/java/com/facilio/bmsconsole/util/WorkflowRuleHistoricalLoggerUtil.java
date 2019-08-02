package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
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
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

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
	
	public static WorkflowRuleHistoricalLoggerContext getActiveWorkflowRuleHistoricalLogger(Long resourceId, long ruleId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", ""+resourceId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ WorkflowRuleHistoricalLoggerContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowRuleHistoricalLoggerContext.class);
			return workflowRuleHistoricalLogger;
		}
		return null;
	}
	
	public static List<WorkflowRuleHistoricalLoggerContext> getGroupedWorkflowRuleHistoricalLogger(long loggerGroupId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("LOGGER_GROUP_ID", "loggerGroupId", ""+loggerGroupId, NumberOperators.EQUALS));
		
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
				.orderBy("STATUS");
				
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
				
				FacilioField countField = AggregateOperator.CommonAggregateOperator.COUNT.getSelectField(fieldMap.get("loggerGroupId"));
				countField.setName("count");
				List<FacilioField> selectFields = new ArrayList<FacilioField>();
				selectFields.add(countField);
				selectFields.add(fieldMap.get("loggerGroupId"));
				
				selectBuilder = new GenericSelectRecordBuilder()
						.select(selectFields)
						.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
						.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
						.groupBy("LOGGER_GROUP_ID");
				
				
				List<Map<String, Object>> propsList = selectBuilder.get();
				
				for(Map<String, Object> prop :propsList)
				{
					WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLoggerContext = workflowRuleHistoricalLoggerContextMap.get((long) prop.get("loggerGroupId"));
					workflowRuleHistoricalLoggerContext.setResourceLogCount((long) prop.get("count"));
				}
				
				return workflowRuleHistoricalLoggerContextMap.values();
	}
	
	public static void updateWorkflowRuleHistoricalLogger(WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+workflowRuleHistoricalLogger.getId(), NumberOperators.EQUALS));		

		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleHistoricalLogger);
		updateBuilder.update(props);
	}
	
	
	public static void deleteWorkflowRuleHistoricalLogger(long id) throws SQLException {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
		.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));
		
	}
	
	public static void deleteReadingAlarm(long ruleId, long startTime, long endTime, long resourceId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.READING_ALARM);
		
		DeleteRecordBuilder<ReadingAlarmContext> builder = new DeleteRecordBuilder<ReadingAlarmContext>()
				.module(module)
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", ""+resourceId, NumberOperators.EQUALS));
		
		
		Criteria criteria = new Criteria();
		
		Criteria subCriteria = new Criteria();
		subCriteria.addAndCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", ""+endTime, NumberOperators.LESS_THAN));
		subCriteria.addAndCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", ""+startTime, NumberOperators.GREATER_THAN));
		
		criteria.addOrCondition(CriteriaAPI.getCondition("CREATED_TIME", "createdTime", startTime+","+endTime, DateOperators.BETWEEN));
		criteria.addOrCondition(CriteriaAPI.getCondition("CLEARED_TIME", "clearedTime", startTime+","+endTime, DateOperators.BETWEEN));
		criteria.orCriteria(subCriteria);
		
		builder.andCriteria(criteria);
		
		
				
		builder.delete();
		
	}
	

}
