package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class AddActionsForWorkflowRule implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		List<ActionContext> actions = (List<ActionContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST);
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		
		if (actions != null && !actions.isEmpty()) {
			actions = ActionAPI.addActions(actions, rule);
			if(rule != null) {
				ActionAPI.addWorkflowRuleActionRel(rule.getId(), actions);
				rule.setActions(actions);
			}
			else {
				context.put(FacilioConstants.ContextNames.ACTIONS_LIST, actions);
			}
		}
		else {
				
			actions = rule.getActions();
			
			if (actions != null && !actions.isEmpty()) {
				actions = ActionAPI.addActions(actions, rule);
				
				ActionAPI.addWorkflowRuleActionRel(rule.getId(), actions);
				rule.setActions(actions);
			}
		}
		
		return false;
	}
	
	
	
}
