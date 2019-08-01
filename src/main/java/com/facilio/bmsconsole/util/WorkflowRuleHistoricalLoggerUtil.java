package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLoggerContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

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
	
	public static List<WorkflowRuleHistoricalLoggerContext> getAllParentWorkflowRuleHistoricalLogger(long ruleId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("RULE_ID", "ruleId", ""+ruleId, NumberOperators.EQUALS))
				.orderBy("STATUS");
				
				List<Map<String, Object>> props = selectBuilder.get();
				if (props != null && !props.isEmpty()) {
					List<WorkflowRuleHistoricalLoggerContext> workflowRuleHistoricalLoggerContextList = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleHistoricalLoggerContext.class);
					return workflowRuleHistoricalLoggerContextList;
				}
				return null;
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
	

}
