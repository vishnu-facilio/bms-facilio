package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkflowRuleLoggerContext;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext;
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

public class WorkflowRuleLoggerAPI {
	
	public static void addWorkflowRuleLogger(WorkflowRuleLoggerContext workflowRuleLoggerContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleLoggerModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleLoggerFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleLoggerContext);
		insertBuilder.addRecord(props);
		
		insertBuilder.save();
		
		workflowRuleLoggerContext.setId((Long) props.get("id"));
		
	}
	
	public static WorkflowRuleLoggerContext getWorkflowRuleLoggerById (long loggerId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleLoggerFields())
				.table(ModuleFactory.getWorkflowRuleLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+loggerId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {			
			WorkflowRuleLoggerContext workflowRuleLoggerContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowRuleLoggerContext.class);
			return workflowRuleLoggerContext;
		}
		return null;
	}
	
	public static List<WorkflowRuleLoggerContext> getWorkflowRuleLoggerContextByRuleId(long ruleId, Integer ruleJobType) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getWorkflowRuleLoggerFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleLoggerFields())
				.table(ModuleFactory.getWorkflowRuleLoggerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), "" +ruleId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleJobType"),"" +ruleJobType, NumberOperators.EQUALS))
				.orderBy("STATUS");
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<WorkflowRuleLoggerContext> workflowRuleLoggerContextList = new ArrayList<WorkflowRuleLoggerContext>();
		if (props != null && !props.isEmpty()) {		
			workflowRuleLoggerContextList  = FieldUtil.getAsBeanListFromMapList(props, WorkflowRuleLoggerContext.class);		
		}	
		return workflowRuleLoggerContextList;
	}
	
	public static void updateWorkflowRuleLogger(WorkflowRuleLoggerContext workflowRuleLoggerContext) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getWorkflowRuleLoggerModule().getTableName())
				.fields(FieldFactory.getWorkflowRuleLoggerFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", ""+workflowRuleLoggerContext.getId(), NumberOperators.EQUALS));		

		Map<String, Object> props = FieldUtil.getAsProperties(workflowRuleLoggerContext);
		updateBuilder.update(props);
	}
	
	public static void deleteWorkflowRuleLogger(long id) throws SQLException {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();	
		deleteRecordBuilder
			.table(ModuleFactory.getWorkflowRuleLoggerModule().getTableName())
			.andCondition(CriteriaAPI.getCondition("ID", "id", ""+id, NumberOperators.EQUALS));
		
	}
	
	public static WorkflowRuleLoggerContext setWorkflowRuleLoggerContext(long ruleId, long noOfResources, DateRange range, int ruleJobType)
	{
		WorkflowRuleLoggerContext workflowRuleLoggerContext = new WorkflowRuleLoggerContext();
		workflowRuleLoggerContext.setRuleId(ruleId);
		workflowRuleLoggerContext.setNoOfResources(noOfResources);
		workflowRuleLoggerContext.setResolvedResourcesCount(0);
		workflowRuleLoggerContext.setStatus(WorkflowRuleLoggerContext.Status.IN_PROGRESS.getIntVal());
		workflowRuleLoggerContext.setRuleJobType(ruleJobType);
		workflowRuleLoggerContext.setStartTime(range.getStartTime());
		workflowRuleLoggerContext.setEndTime(range.getEndTime());
		workflowRuleLoggerContext.setCreatedBy(AccountUtil.getCurrentUser().getId());
		workflowRuleLoggerContext.setCreatedTime(DateTimeUtil.getCurrenTime());
		workflowRuleLoggerContext.setCalculationStartTime(DateTimeUtil.getCurrenTime());
		return workflowRuleLoggerContext;	
	}

}
