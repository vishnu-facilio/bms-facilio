package com.facilio.workflows.command;

import org.apache.commons.chain.Context;

import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2API;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class DeleteWorkflowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowContext workflow = (WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT);
		
		if(workflow == null) {
			workflow = (WorkflowUserFunctionContext) context.get(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT);
		}
		
		WorkflowUserFunctionContext userFunction = WorkflowV2API.getUserFunction(workflow.getId());
		
		WorkflowUtil.deleteWorkflow(workflow.getId());
		
		if(userFunction != null) {
			
			BundleUtil.markModifiedComponent(BundleComponentsEnum.FUNCTION, userFunction.getId(), userFunction.getName(), BundleModeEnum.DELETE);
		}
		
		return false;
	}


}