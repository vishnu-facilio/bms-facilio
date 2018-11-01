package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.ApprovalRulesAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ApproverContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class DeConstructApprovalRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		ApprovalRuleContext rule = (ApprovalRuleContext) context.get(FacilioConstants.ContextNames.APPROVAL_RULE);
		if (rule != null) {
			if (rule.getRejectionRule() != null) {
				rule.setRejectionActions(rule.getRejectionRule().getActions());
			}
			
			List<Long> approverIds = (List<Long>) context.get(FacilioConstants.ContextNames.APPROVER_ID_LIST);
			fetchApproversAndAction(rule, approverIds);
			fetchForms(rule);
		}
		
		return false;
	}
	
	private void fetchApproversAndAction (ApprovalRuleContext rule, List<Long> approverIds) throws Exception {
		Map<Long, List<Long>> approverActionMap = ApprovalRulesAPI.getApproverActionMap(approverIds);
		List<ApproverContext> approvers = new ArrayList<>();
		
		WorkflowRuleContext onApprovalRule = rule;
		while (onApprovalRule != null && onApprovalRule instanceof ApprovalRuleContext) {
			List<ApproverContext> currentApprovers = ((ApprovalRuleContext)onApprovalRule).getApprovers();
			if (currentApprovers != null && !currentApprovers.isEmpty()) {
				Map<Long, ActionContext> actionMap = onApprovalRule.getActions() == null ? null : onApprovalRule.getActions().stream().collect(Collectors.toMap(ActionContext::getId, Function.identity())); 
				
				for (ApproverContext approver : currentApprovers) {
					List<Long> actionIds = approverActionMap == null ? null : approverActionMap.get(approver.getId());
					if (actionIds != null && !actionIds.isEmpty()) {
						List<ActionContext> actions = new ArrayList<>();
						for (Long actionId : actionIds) {
							actions.add(actionMap.get(actionId));
						}
						approver.setActions(actions);
					}
					approvers.add(approver);
				}
			}
			
			WorkflowRuleContext childRule = ((ApprovalRuleContext)onApprovalRule).getApprovalRule();
			onApprovalRule = childRule;
		}
		if (onApprovalRule != null) {
			rule.setApprovalActions(onApprovalRule.getActions());
		}
		rule.setApprovers(approvers);
	}
		
	private void fetchForms (ApprovalRuleContext rule) throws Exception {
		List<Long> formIds = new ArrayList<>();
		if (rule.getApprovalFormId() != -1) {
			formIds.add(rule.getApprovalFormId());
		}
		if (rule.getRejectionFormId() != -1) {
			formIds.add(rule.getRejectionFormId());
		}
		
		if (!formIds.isEmpty()) {
			Map<Long, FacilioForm> forms = FormsAPI.getFormsAsMap(formIds);
			if (rule.getApprovalFormId() != -1) {
				rule.setApprovalForm(forms.get(rule.getApprovalFormId()));
			}
			if (rule.getRejectionFormId() != -1) {
				rule.setRejectionForm(forms.get(rule.getRejectionFormId()));
			}
		}
	}
}
