package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLogsContext;
import com.facilio.bmsconsole.context.WorkflowRuleLoggerContext;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class WorkflowRuleResourceLoggerAPI {
	
	public static void addWorkflowRuleResourceLogger(WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleResourceLoggerModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleResourceLoggerFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleResourceLoggerContext);
		insertBuilder.addRecord(props);
	
		insertBuilder.save();
		
		workflowRuleResourceLoggerContext.setId((Long) props.get("id"));
	
	}
	
	public static WorkflowRuleResourceLoggerContext getWorkflowRuleResourceLoggerById (long parentLogId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleResourceLoggerFields())
				.table(ModuleFactory.getWorkflowRuleResourceLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+parentLogId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowRuleResourceLoggerContext.class);
			return workflowRuleResourceLoggerContext;
		}
		return null;
	}
	
	public static List<WorkflowRuleResourceLoggerContext> getWorkflowRuleResourceLogsByParentRuleLoggerId(long parentRuleLoggerId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleResourceLoggerFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleResourceLoggerFields())
				.table(ModuleFactory.getWorkflowRuleResourceLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentRuleLoggerId"), "" +parentRuleLoggerId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<WorkflowRuleResourceLoggerContext> workflowRuleResourceLoggerContext = new ArrayList<WorkflowRuleResourceLoggerContext>();
		if (props != null && !props.isEmpty()) {		
			workflowRuleResourceLoggerContext  = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleResourceLoggerContext.class);		
		}	
		return workflowRuleResourceLoggerContext;
	}
	
	public static List<Map<String, Object>> getResolvedWorkflowRuleResourceLogsAndAlarmCountByParentRuleLoggerId(long parentRuleLoggerId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleResourceLoggerFields());

		FacilioField countField = BmsAggregateOperators.CommonAggregateOperator.COUNT.getSelectField(fieldMap.get("status"));
		countField.setName("count");
		FacilioField sumField = BmsAggregateOperators.NumberAggregateOperator.SUM.getSelectField(fieldMap.get("alarmCount"));
		sumField.setName("sum");
		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.add(fieldMap.get("parentRuleLoggerId"));
		selectFields.add(countField);
		selectFields.add(sumField);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table(ModuleFactory.getWorkflowRuleResourceLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentRuleLoggerId"), "" +parentRuleLoggerId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), "" +WorkflowRuleResourceLoggerContext.Status.RESOLVED.getIntVal(), NumberOperators.EQUALS))
				.groupBy("PARENT_RULE_LOGGER_ID, STATUS");
		
		List<Map<String, Object>> props = selectBuilder.get();
		return props;
	}
	
	public static List<WorkflowRuleResourceLoggerContext> getWorkflowRuleResourceLogsByParentRuleLoggerAndResourceId(long parentRuleLoggerId, long resourceId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleResourceLoggerFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleResourceLoggerFields())
				.table(ModuleFactory.getWorkflowRuleResourceLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentRuleLoggerId"), "" +parentRuleLoggerId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), "" +resourceId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<WorkflowRuleResourceLoggerContext> workflowRuleResourceLoggerContext = new ArrayList<WorkflowRuleResourceLoggerContext>();
		if (props != null && !props.isEmpty()) {		
			workflowRuleResourceLoggerContext  = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleResourceLoggerContext.class);		
		}	
		return workflowRuleResourceLoggerContext;
	}
	
	public static List<WorkflowRuleResourceLoggerContext> getActiveWorkflowRuleResourceLogsByParentRuleLoggerAndResourceId(long parentRuleLoggerId, List<Long> resourceIds) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleResourceLoggerFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleResourceLoggerFields())
				.table(ModuleFactory.getWorkflowRuleResourceLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentRuleLoggerId"), "" +parentRuleLoggerId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), resourceIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), "" +WorkflowRuleResourceLoggerContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<WorkflowRuleResourceLoggerContext> workflowRuleResourceLoggerContext = new ArrayList<WorkflowRuleResourceLoggerContext>();
		if (props != null && !props.isEmpty()) {		
			workflowRuleResourceLoggerContext  = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleResourceLoggerContext.class);		
		}	
		return workflowRuleResourceLoggerContext;
	}
	
	public static void updateWorkflowRuleResourceLoggerContext(WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleResourceLoggerModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleResourceLoggerFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+workflowRuleResourceLoggerContext.getId(), NumberOperators.EQUALS));		

		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleResourceLoggerContext);
		updateBuilder.update(props);
	}
	
	public static int updateEventGeneratingParentWorkflowRuleResourceLoggerContext(WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleResourceLoggerModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleResourceLoggerFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+workflowRuleResourceLoggerContext.getId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ WorkflowRuleResourceLoggerContext.Status.EVENT_GENERATING_STATE.getIntVal(), NumberOperators.EQUALS));

		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleResourceLoggerContext);
		int rowsUpdated = updateBuilder.update(props);
		return rowsUpdated;
	}
	
	public static void updateWorkflowRuleResourceContextState(WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext, int status) throws Exception {
		workflowRuleResourceLoggerContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
		workflowRuleResourceLoggerContext.setStatus(status);
		updateWorkflowRuleResourceLoggerContext(workflowRuleResourceLoggerContext);	
	}
	
	public static void deleteWorkflowRuleResourceLoggerContext(long id) throws SQLException {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();	
		deleteRecordBuilder
			.table(ModuleFactory.getWorkflowRuleResourceLoggerModule().getTableName())
			.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));		
	}
	
	public static WorkflowRuleResourceLoggerContext setworkflowRuleResourceLoggerContext(long parentRuleLoggerId, Long resourceId, DateRange modifiedRange)
	{
		WorkflowRuleResourceLoggerContext workflowRuleResourceLoggerContext = new WorkflowRuleResourceLoggerContext();
		workflowRuleResourceLoggerContext.setParentRuleLoggerId(parentRuleLoggerId);
		workflowRuleResourceLoggerContext.setResourceId(resourceId);
		workflowRuleResourceLoggerContext.setStatus(WorkflowRuleResourceLoggerContext.Status.IN_PROGRESS.getIntVal());
		workflowRuleResourceLoggerContext.setCalculationStartTime(DateTimeUtil.getCurrenTime());
		
		if(modifiedRange != null)
		{
			workflowRuleResourceLoggerContext.setModifiedStartTime(modifiedRange.getStartTime());
			workflowRuleResourceLoggerContext.setModifiedEndTime(modifiedRange.getEndTime());		
		}
		return workflowRuleResourceLoggerContext;	
	}

}
