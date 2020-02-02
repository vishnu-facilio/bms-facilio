package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.WorkflowRuleHistoricalLogsContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class WorkflowRuleHistoricalLogsAPI {
	
	public static void addWorkflowRuleHistoricalLogsContext(WorkflowRuleHistoricalLogsContext workflowRuleHistoricalLogsContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleHistoricalLogsModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleHistoricalLogsFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleHistoricalLogsContext);
		insertBuilder.addRecord(props);
	
		insertBuilder.save();
		
		workflowRuleHistoricalLogsContext.setId((Long) props.get("id"));
	
	}
	
	public static WorkflowRuleHistoricalLogsContext getWorkflowRuleHistoricalLogsContextById (long loggerId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLogsFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLogsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+loggerId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			WorkflowRuleHistoricalLogsContext workflowRuleHistoricalLogsContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowRuleHistoricalLogsContext.class);
			return workflowRuleHistoricalLogsContext;
		}
		return null;
	}
	
	public static List<WorkflowRuleHistoricalLogsContext> getWorkflowRuleHistoricalLogsByParentRuleResourceId(long parentRuleResourceId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleHistoricalLogsFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleHistoricalLogsFields())
				.table(ModuleFactory.getWorkflowRuleHistoricalLogsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentRuleResourceId"), "" +parentRuleResourceId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<WorkflowRuleHistoricalLogsContext> workflowRuleHistoricalLogsContextList = new ArrayList<WorkflowRuleHistoricalLogsContext>();
		if (props != null && !props.isEmpty()) {		
			workflowRuleHistoricalLogsContextList  = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleHistoricalLogsContext.class);		
		}	
		return workflowRuleHistoricalLogsContextList;
	}
	
	public static List<Long> getActiveWorkflowRuleHistoricalLogsByParentRuleResourceId(long parentRuleResourceId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleHistoricalLogsFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(Collections.singletonList(fieldMap.get("id")))
				.table(ModuleFactory.getWorkflowRuleHistoricalLogsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", ""+ WorkflowRuleHistoricalLogsContext.Status.IN_PROGRESS.getIntVal(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentRuleResourceId"), "" +parentRuleResourceId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<Long> workflowRuleHistoricalLogIds = new ArrayList<Long>();
		
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props ) {
				Long workflowRuleHistoricalLogContextId = (Long) prop.get("id");
				workflowRuleHistoricalLogIds.add(workflowRuleHistoricalLogContextId);
			}
		}
		return workflowRuleHistoricalLogIds;
	}
	
	public static void updateWorkflowRuleHistoricalLogsContext(WorkflowRuleHistoricalLogsContext workflowRuleHistoricalLogsContext) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleHistoricalLogsModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleHistoricalLogsFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+workflowRuleHistoricalLogsContext.getId(), NumberOperators.EQUALS));		

		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleHistoricalLogsContext);
		updateBuilder.update(props);
	}
	
	public static void updateWorkflowRuleHistoricalLogsContextToResolvedState(WorkflowRuleHistoricalLogsContext workflowRuleHistoricalLogsContext, int status) throws Exception {
		workflowRuleHistoricalLogsContext.setCalculationEndTime(DateTimeUtil.getCurrenTime());
		workflowRuleHistoricalLogsContext.setStatus(status);
		updateWorkflowRuleHistoricalLogsContext(workflowRuleHistoricalLogsContext);	
	}
	
	public static void deleteWorkflowRuleHistoricalLogsContext(long id) throws SQLException {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();	
		deleteRecordBuilder
			.table(ModuleFactory.getWorkflowRuleHistoricalLogsModule().getTableName())
			.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));		
	}
	
	public static WorkflowRuleHistoricalLogsContext setworkflowRuleHistoricalLogsContext(long parentRuleResourceId, DateRange splitRange, Integer logState)
	{
		WorkflowRuleHistoricalLogsContext workflowRuleHistoricalLogsContext = new WorkflowRuleHistoricalLogsContext();
		workflowRuleHistoricalLogsContext.setParentRuleResourceId(parentRuleResourceId);
		workflowRuleHistoricalLogsContext.setStatus(WorkflowRuleHistoricalLogsContext.Status.IN_PROGRESS.getIntVal());
		workflowRuleHistoricalLogsContext.setSplitStartTime(splitRange.getStartTime());
		workflowRuleHistoricalLogsContext.setSplitEndTime(splitRange.getEndTime());
		if(logState != null)
		{
			workflowRuleHistoricalLogsContext.setLogState(logState);
		}
		return workflowRuleHistoricalLogsContext;	
	}

}
