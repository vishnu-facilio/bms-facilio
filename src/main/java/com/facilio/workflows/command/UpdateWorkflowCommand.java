package com.facilio.workflows.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class UpdateWorkflowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowContext workflow = (WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT);
		
		WorkflowUtil.updateWorkflow(workflow);
		return false;
	}
}