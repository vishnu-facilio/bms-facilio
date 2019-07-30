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
	
	public static void addWorkflowRuleHistoricalLogger(WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleHistoricalLoggerFields());
	
		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleHistoricalLogger);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		workflowRuleHistoricalLogger.setId((Long) props.get("id"));
		
	}
	
	
	public static WorkflowRuleHistoricalLoggerContext getWorkflowRuleHistoricalLogger(long parentassetId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLoggerFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", ""+parentassetId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			WorkflowRuleHistoricalLoggerContext workflowRuleHistoricalLogger = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowRuleHistoricalLoggerContext.class);
			return workflowRuleHistoricalLogger;
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
