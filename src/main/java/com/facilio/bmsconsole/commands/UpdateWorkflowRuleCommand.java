package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.scoringrule.ScoringRuleAPI;
import com.facilio.bmsconsole.scoringrule.ScoringRuleContext;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.readingrule.faulttowo.ReadingRuleWorkOrderRelContext;
import com.facilio.readingrule.faulttowo.RuleWoAPI;
import org.apache.commons.chain.Context;

public class UpdateWorkflowRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		WorkflowRuleContext oldRule = WorkflowRuleAPI.getWorkflowRule(rule.getId());
		if (rule.getActivityTypeEnum() == null) {
			rule.setEvent(oldRule.getEvent());
		}
		
		if (rule.getRuleTypeEnum() != null && rule.getRuleTypeEnum().versionSupported()) {
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
			updateRule(rule, oldRule);
			rule.setRuleType(oldRule.getRuleType());
		}
		context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, rule);
		context.put(FacilioConstants.ContextNames.RESULT, "success");
		return false;
	}
	
	private void updateRule (WorkflowRuleContext rule, WorkflowRuleContext oldRule) throws Exception {
		rule.setRuleType(null); //Type is not allowed to be changed
		if (rule instanceof ReadingRuleContext) {
			rule = ReadingRuleAPI.updateReadingRuleWithChildren((ReadingRuleContext) rule, (ReadingRuleContext)oldRule);
		}
		else if (rule instanceof SLARuleContext) {
			rule = SLARuleAPI.updateSLARuleWithChildren((SLARuleContext) rule, (SLARuleContext) oldRule);
		}
		else if (rule instanceof ApprovalRuleContext) {
			rule = ApprovalRulesAPI.updateApprovalRuleWithChldren((ApprovalRuleContext) rule, (ApprovalRuleContext)oldRule);
		}
		else if (rule instanceof StateFlowRuleContext || rule instanceof ApprovalStateFlowRuleContext) {
			WorkflowRuleAPI.updateWorkflowRuleWithChildren(rule, oldRule);
			WorkflowRuleAPI.updateExtendedRule(rule, ModuleFactory.getStateFlowModule(), FieldFactory.getStateFlowFields());
		}
		else if (rule instanceof StateflowTransitionContext) {
			rule = ApprovalRulesAPI.updateStateflowTransitionRuleWithChildren((StateflowTransitionContext) rule, (StateflowTransitionContext)oldRule);
			WorkflowRuleAPI.updateTransitionActionSequence((StateflowTransitionContext) rule);
		}
		else if (rule instanceof CustomButtonRuleContext) {
			rule = ApprovalRulesAPI.updateCustomButtonRuleWithChildren((CustomButtonRuleContext) rule, (CustomButtonRuleContext) oldRule);
		}
		else if (rule instanceof AlarmWorkflowRuleContext) {
			rule = ReadingRuleAPI.updateAlarmWorkflowRule((AlarmWorkflowRuleContext) rule, (AlarmWorkflowRuleContext) oldRule);
		}
		else if (rule instanceof SLAWorkflowCommitmentRuleContext) {
			SLAWorkflowAPI.updateSLACommitmentRule((SLAWorkflowCommitmentRuleContext) rule, (SLAWorkflowCommitmentRuleContext) oldRule);
		}
		else if (rule instanceof SLAPolicyContext) {
			SLAWorkflowAPI.updateSLAPolicyRule((SLAPolicyContext) rule, (SLAPolicyContext) oldRule);
		}
		else if (rule instanceof ScoringRuleContext) {
			ScoringRuleAPI.updateScoringRule((ScoringRuleContext) rule, (ScoringRuleContext) oldRule);
		}else if(rule instanceof SystemButtonRuleContext){
			rule = ApprovalRulesAPI.updateSystemButtonRuleWithChildren((SystemButtonRuleContext) rule,(SystemButtonRuleContext) oldRule);
		}
		else if(rule instanceof TransactionRuleContext){
			WorkflowRuleAPI.updateTransactionRuleWithChildren((TransactionRuleContext) rule,(TransactionRuleContext) oldRule);
		}
		else if(rule instanceof ReadingRuleWorkOrderRelContext){
			rule= RuleWoAPI.updateWoDetails((ReadingRuleWorkOrderRelContext)rule,(ReadingRuleWorkOrderRelContext)oldRule);
		}
		else {
			rule = WorkflowRuleAPI.updateWorkflowRuleWithChildren(rule, oldRule);
		}
	}

}
