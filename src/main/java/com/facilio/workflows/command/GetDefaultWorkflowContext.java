package com.facilio.workflows.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.command.FacilioCommand;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class GetDefaultWorkflowContext extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		int defaultWorkflowId = (int) context.get("defaultWorkflowId");
		JSONObject workflowJson = (JSONObject) WorkflowV2Util.getDefaultWorkflow(defaultWorkflowId);
		String workflowString = (String) workflowJson.get("workflow");
		WorkflowContext workflow = new WorkflowContext();
		workflow.setWorkflowV2String(workflowString);
		workflow.setIsV2Script(true);
		
		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		return false;
		
	}

}
