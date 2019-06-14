package com.facilio.workflowv2.util;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowContext.WorkflowUIMode;
import com.facilio.workflows.util.WorkflowUtil;

public class WorkflowV2API {
	
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
