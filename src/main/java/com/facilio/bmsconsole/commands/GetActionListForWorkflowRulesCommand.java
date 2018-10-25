package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class GetActionListForWorkflowRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<WorkflowRuleContext> rules = (List<WorkflowRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST);
		if (rules == null) {
			WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
			if (rule != null) {
				rules = Collections.singletonList(rule);
			}
		}
		
		if(rules != null && !rules.isEmpty()) {
			for (WorkflowRuleContext rule : rules) {
				switch (rule.getRuleTypeEnum()) {
					case APPROVAL_RULE:
						ApprovalRuleContext approvalRule = (ApprovalRuleContext) rule;
						if (approvalRule.getApprovalRuleId() != -1) {
							approvalRule.setApprovalActions(ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), approvalRule.getApprovalRuleId()));
						}
						if (approvalRule.getRejectionRuleId() != -1) {
							approvalRule.setRejectionActions(ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), approvalRule.getRejectionRuleId()));
						}
						List<ActionContext> actions = ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), rule.getId());
						rule.setActions(actions);
						break;
					default:
						 actions = ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), rule.getId());
						rule.setActions(actions);
						break;
				}
			}
		}
		return false;
	}

}
