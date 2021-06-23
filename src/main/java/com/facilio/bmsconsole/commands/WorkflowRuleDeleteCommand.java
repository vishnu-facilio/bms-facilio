package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class WorkflowRuleDeleteCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
        WorkflowRuleContext workflowRuleContext = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);

        if (workflowRuleContext != null) {
            WorkflowRuleAPI.deleteWorkflowRule(workflowRuleContext.getId());
        }
		return false;
	}

}
