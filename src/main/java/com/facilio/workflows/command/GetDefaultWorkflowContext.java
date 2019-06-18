package com.facilio.workflows.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class GetDefaultWorkflowContext implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		int defaultWorkflowId = (int) context.get("defaultWorkflowId");
		JSONObject workflowJson = (JSONObject) WorkflowV2Util.defaultWorkflows.get(""+defaultWorkflowId);
		String workflowString = (String) workflowJson.get("workflow");
		WorkflowContext workflow = new WorkflowContext();
		workflow.setWorkflowV2String(workflowString);
		
		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		return false;
		
	}

}
