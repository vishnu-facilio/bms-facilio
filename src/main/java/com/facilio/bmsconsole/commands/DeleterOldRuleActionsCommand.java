package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class DeleterOldRuleActionsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ActionContext> actions = (List<ActionContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_ACTION);
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		
		if (actions != null) {
			ActionAPI.deleteAllActionsFromWorkflowRules(Collections.singletonList(rule.getId()));
		}
		return false;
	}

}
