package com.facilio.workflowv2.util;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowContext.WorkflowUIMode;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;

public class WorkflowV2API {
	
	public static WorkflowContext getDefaultWorkflowResult(int defaultWorkflowId,List<Object> params) throws Exception {
		JSONObject workflowJson = (JSONObject)WorkflowV2Util.defaultWorkflows.get(""+defaultWorkflowId);
		String workflowString = (String) workflowJson.get("workflow");
		WorkflowContext workflow = new WorkflowContext();
		workflow.setWorkflowV2String(workflowString);
		return WorkflowV2API.executeWorkflow(workflow, params, null, true, true);
	}
	

	public static WorkflowContext executeWorkflow(WorkflowContext workflowContext,List<Object> params) throws Exception {
		return executeWorkflow(workflowContext, params, null, false, false);
	}
	
	public static WorkflowContext executeWorkflow(WorkflowContext workflowContext,List<Object> params, Map<String, ReadingDataMeta> rdmCache, boolean ignoreNullExpressions, boolean ignoreMarked) throws Exception {
		
		if(workflowContext.getWorkflowV2String() == null && workflowContext.getId() > 0) {
			WorkflowContext wf = WorkflowUtil.getWorkflowContext(workflowContext.getId());
			workflowContext.setWorkflowV2String(wf.getWorkflowV2String());
		}
		workflowContext.setCachedRDM(rdmCache);
		workflowContext.setWorkflowUIMode(WorkflowUIMode.NEW_WORKFLOW);
		workflowContext.setIgnoreMarkedReadings(ignoreMarked);
		
		workflowContext.setParams(params);
		
		workflowContext.setIgnoreNullParams(ignoreNullExpressions);		// check and remove
		
		workflowContext.executeWorkflow();
		
		return workflowContext;
	}
	

}
