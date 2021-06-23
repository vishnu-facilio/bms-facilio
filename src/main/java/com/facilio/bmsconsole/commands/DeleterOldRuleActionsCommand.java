package com.facilio.bmsconsole.commands;

import java.util.Collections;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class DeleterOldRuleActionsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (rule != null && rule.getId() != -1) {
			ActionAPI.deleteAllActionsFromWorkflowRules(Collections.singletonList(rule.getId()));
		}
		return false;
	}

}
