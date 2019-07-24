package com.facilio.workflowv2.util;

import java.util.List;
import java.util.Map;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.ScheduledWorkflowContext;

public class WorkflowV2API {
	
	public static ScheduledWorkflowContext getScheduledWorkflowContext(long scheduledWorkflowId,Boolean isActive) throws Exception {
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(FieldFactory.getScheduledWorkflowFields())
				.table(ModuleFactory.getScheduledWorkflowModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(scheduledWorkflowId, ModuleFactory.getScheduledWorkflowModule()));
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getScheduledWorkflowFields());
		if(isActive != null) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("isActive"), isActive.toString(), BooleanOperators.IS));
		}
		
		List<Map<String, Object>> props = select.get();
		
		ScheduledWorkflowContext scheduledWorkflowContext = null;
		if(props != null && !props.isEmpty()) {
			scheduledWorkflowContext = FieldUtil.getAsBeanFromMap(props.get(0), ScheduledWorkflowContext.class);
		}
		return scheduledWorkflowContext;
	}
	
//	public static WorkflowContext getDefaultWorkflowResult(int defaultWorkflowId,List<Object> params) throws Exception {
//		JSONObject workflowJson = (JSONObject)WorkflowV2Util.defaultWorkflows.get(""+defaultWorkflowId);
//		String workflowString = (String) workflowJson.get("workflow");
//		WorkflowContext workflow = new WorkflowContext();
//		workflow.setWorkflowV2String(workflowString);
//		return WorkflowV2API.executeWorkflow(workflow, params, null, true, true);
//	}

//	public static WorkflowContext executeWorkflow(WorkflowContext workflowContext,List<Object> params) throws Exception {
//		return executeWorkflow(workflowContext, params, null, false, false);
//	}
	
//	public static WorkflowContext executeWorkflow(WorkflowContext workflowContext,List<Object> params, Map<String, ReadingDataMeta> rdmCache, boolean ignoreNullExpressions, boolean ignoreMarked) throws Exception {
//		
//		if(workflowContext.getWorkflowV2String() == null && workflowContext.getId() > 0) {
//			WorkflowContext wf = WorkflowUtil.getWorkflowContext(workflowContext.getId());
//			workflowContext.setWorkflowV2String(wf.getWorkflowV2String());
//		}
//		workflowContext.setCachedRDM(rdmCache);
//		workflowContext.setWorkflowUIMode(WorkflowUIMode.NEW_WORKFLOW);
//		workflowContext.setIgnoreMarkedReadings(ignoreMarked);
//		
//		workflowContext.setParams(params);
//		
//		workflowContext.setIgnoreNullParams(ignoreNullExpressions);		// check and remove
//		
//		workflowContext.executeWorkflow();
//		
//		return workflowContext;
//	}
	

}
