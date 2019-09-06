package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class ValidateWorkflowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		WorkflowContext workflow = (WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT);
		
		if(workflow == null) {
			workflow = (WorkflowUserFunctionContext) context.get(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT);
		}
		
//		boolean validationResult = workflow.validateWorkflow();
//		if(!validationResult) {
//			context.put(WorkflowV2Util.WORKFLOW_SYNTAX_ERROR, workflow.getErrorListener());
//			return true;
//		}
		return false;
	}

}
