package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext.ApprovalOrder;
import com.facilio.bmsconsole.workflow.rule.ApproverContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;

public class ConstructApprovalRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ApprovalRuleContext rule = (ApprovalRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (rule.getApprovers() == null || rule.getApprovers().isEmpty()) {
			throw new IllegalArgumentException("Atleast one approver is required for for adding Approval rule");
		}
		context.put(FacilioConstants.ContextNames.APPROVER_LIST, rule.getApprovers());
		if (rule.getApprovers().size() > 1 && rule.isAllApprovalRequired()) {
			if (rule.getApprovalOrderEnum() == null) {
				throw new IllegalArgumentException("Approval Order is mandatory when everyone's approval is required");
			}
			
			if (rule.getApprovalOrderEnum() == ApprovalOrder.SEQUENTIAL) {
				serialConstruction(rule, (FacilioContext) context);
			}
			else {
				defaultConstruction(rule, (FacilioContext) context);
			}
		}
		else {
			defaultConstruction(rule, (FacilioContext) context);
		}
		return false;
	}
	
	private void serialConstruction (ApprovalRuleContext rule, FacilioContext context) throws Exception {
		List<ApproverContext> approvers = rule.getApprovers();
		List<ActionContext> approvalActions = rule.getApprovalActions();
		ApprovalRuleContext currentRule = null;
		int i = 1;
		for (ApproverContext approver : approvers) {
			if (currentRule == null) {
				currentRule = rule;
			}
			else {
				ApprovalRuleContext childRule = new ApprovalRuleContext();
				childRule.setName(rule.getName()+"_Child_Approval_Rule_"+(i++));
				childRule.setRuleType(RuleType.CHILD_APPROVAL_RULE);
				currentRule.setApprovalRule(childRule);
				currentRule = childRule;
			}
			currentRule.setApprovers(Collections.singletonList(approver));
			currentRule.setActions(approver.getActions());
			currentRule.setApprovalActions(null);
			currentRule.setRejectionActions(rule.getRejectionActions());
			currentRule.setApprovalForm(rule.getApprovalForm());
			currentRule.setRejectionForm(rule.getRejectionForm());
			currentRule.setApprovalButton(rule.getApprovalButton());
			currentRule.setRejectionButton(rule.getRejectionButton());
		}
		currentRule.setApprovalActions(approvalActions);
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, rule.getActions());
	}

	private void defaultConstruction (ApprovalRuleContext rule, FacilioContext context) throws Exception {
		List<ActionContext> actions = new ArrayList<>();
		for (ApproverContext approver : rule.getApprovers()) {
			if (approver.getActions() != null) {
				actions.addAll(approver.getActions());
			}
		}
		context.put(FacilioConstants.ContextNames.WORKFLOW_ACTION_LIST, actions);
	}
}
