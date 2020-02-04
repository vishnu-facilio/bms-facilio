package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ApprovalRulesAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.SLARuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateWorkflowRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (rule.getRuleTypeEnum() != null && rule.getRuleTypeEnum().versionSupported()) {
			WorkflowRuleContext oldRule = WorkflowRuleAPI.getWorkflowRule(rule.getId(), false, false);
			WorkflowRuleContext updateRule = new WorkflowRuleContext();
			updateRule.setId(rule.getId());
			updateRule.setLatestVersion(false);
			updateRule.setStatus(false);
			
			if (oldRule.getVersionGroupId() == -1) {
				updateRule.setVersionGroupId(rule.getId());
				updateRule.setVersionNumber(1);
				rule.setVersionNumber(2);
			}
			else {
				updateRule.setVersionGroupId(oldRule.getVersionGroupId());
				rule.setVersionNumber(oldRule.getVersionNumber()+1);
			}
			rule.setVersionGroupId(updateRule.getVersionGroupId());
			rule.setId(-1);
			WorkflowRuleAPI.updateWorkflowRule(updateRule);
		}
		else {
			updateRule(rule);
		}
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}
	
	private void updateRule (WorkflowRuleContext rule) throws Exception {
		rule.setRuleType(null); //Type is not allowed to be changed
		if (rule instanceof ReadingRuleContext) {
			rule = ReadingRuleAPI.updateReadingRuleWithChildren((ReadingRuleContext) rule);
		}
		else if (rule instanceof SLARuleContext) {
			rule = SLARuleAPI.updateSLARuleWithChildren((SLARuleContext) rule);
		}
		else if (rule instanceof ApprovalRuleContext) {
			rule = ApprovalRulesAPI.updateApprovalRuleWithChldren((ApprovalRuleContext) rule);
		}
		else if (rule instanceof StateFlowRuleContext) {
			WorkflowRuleAPI.updateWorkflowRuleWithChildren(rule);
			WorkflowRuleAPI.updateExtendedRule(rule, ModuleFactory.getStateFlowModule(), FieldFactory.getStateFlowFields());
		}
		else if (rule instanceof StateflowTransitionContext) {
			rule = ApprovalRulesAPI.updateStateflowTransitionRuleWithChildren((StateflowTransitionContext) rule);
		}
		else if (rule instanceof CustomButtonRuleContext) {
			rule = ApprovalRulesAPI.updateCustomButtonRuleWithChildren((CustomButtonRuleContext) rule);
		}
		else if (rule instanceof AlarmWorkflowRuleContext) {
			rule = ReadingRuleAPI.updateAlarmWorkflowRule((AlarmWorkflowRuleContext) rule);
		}
		else if (rule instanceof SLAWorkflowCommitmentRuleContext) {
			WorkflowRuleAPI.updateExtendedRule(rule, ModuleFactory.getSLAWorkflowRuleModule(), FieldFactory.getSLAWorkflowRuleFields());
		}
		else {
			rule = WorkflowRuleAPI.updateWorkflowRuleWithChildren(rule);
		}
	}

}
