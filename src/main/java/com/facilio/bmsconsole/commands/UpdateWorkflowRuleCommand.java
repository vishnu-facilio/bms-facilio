package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ApprovalRulesAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.SLARuleAPI;
import com.facilio.bmsconsole.util.ScheduledRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.SLARuleContext;
import com.facilio.bmsconsole.workflow.rule.ScheduledRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class UpdateWorkflowRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		if (rule.getRuleTypeEnum() != null && rule.getRuleTypeEnum().versionSupported()) {
			WorkflowRuleContext oldRule = WorkflowRuleAPI.getWorkflowRule(rule.getId(), false, false, false);
			WorkflowRuleContext updateRule = new WorkflowRuleContext();
			updateRule.setId(rule.getId());
			updateRule.setLatestVersion(false);
			updateRule.setStatus(false);
			
			if (oldRule.getVersionGroupId() == -1) {
				updateRule.setVersionGroupId(rule.getId());
			}
			else {
				updateRule.setVersionGroupId(oldRule.getVersionGroupId());
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
		else if (rule instanceof ScheduledRuleContext) {
			rule= ScheduledRuleAPI.updateScheduledRuleWithChildren((ScheduledRuleContext) rule);
		}
		else {
			rule = WorkflowRuleAPI.updateWorkflowRuleWithChildren(rule);
		}
	}

}
