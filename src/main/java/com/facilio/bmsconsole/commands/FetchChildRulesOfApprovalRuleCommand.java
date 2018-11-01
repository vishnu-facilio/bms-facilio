package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApproverContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class FetchChildRulesOfApprovalRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ApprovalRuleContext approvalRule = (ApprovalRuleContext) context.remove(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (approvalRule != null) {
			List<WorkflowRuleContext> rules = new ArrayList<>();
			rules.add(approvalRule);
			
			if (approvalRule.getRejectionRuleId() != -1) {
				WorkflowRuleContext onRejectionRule = WorkflowRuleAPI.getWorkflowRule(approvalRule.getRejectionRuleId());
				rules.add(onRejectionRule);
				approvalRule.setRejectionRule(onRejectionRule);
			}
			
			WorkflowRuleContext onApprovalRule = approvalRule;
			List<Long> approverIds = new ArrayList<>();
			while (onApprovalRule != null && onApprovalRule instanceof ApprovalRuleContext) {
				WorkflowRuleContext childRule = null;
				if (((ApprovalRuleContext)onApprovalRule).getApprovalRuleId() != -1) {
					childRule = WorkflowRuleAPI.getWorkflowRule(((ApprovalRuleContext)onApprovalRule).getApprovalRuleId());
					rules.add(childRule);
					((ApprovalRuleContext)onApprovalRule).setApprovalRule(childRule);
				}
				
				List<ApproverContext> approvers = ((ApprovalRuleContext)onApprovalRule).getApprovers();
				approverIds.addAll(approvers.stream().map(ApproverContext::getId).collect(Collectors.toList()));
				
				onApprovalRule = childRule;
			}
			
			context.put(FacilioConstants.ContextNames.APPROVER_ID_LIST, approverIds);
			context.put(FacilioConstants.ContextNames.APPROVAL_RULE, approvalRule);
			context.put(FacilioConstants.ContextNames.WORKFLOW_RULE_LIST, rules);
		}
		return false;
	}

}
