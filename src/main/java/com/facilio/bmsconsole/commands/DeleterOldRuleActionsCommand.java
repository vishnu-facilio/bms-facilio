package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class DeleterOldRuleActionsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (rule != null && rule.getId() != -1) {
			ActionAPI.deleteAllActionsFromWorkflowRules(Collections.singletonList(rule.getId()));
		}
		return false;
	}

}
