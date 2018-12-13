package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class AddWorkflowRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		rule.setStatus(true);
		rule.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		long ruleId = WorkflowRuleAPI.addWorkflowRule(rule);
		rule.setId(ruleId);
		
		List<ReadingRuleContext> alarmTriggerRules = (List<ReadingRuleContext>) context.get(FacilioConstants.ContextNames.WORKFLOW_ALARM_TRIGGER_RULES);
		ReadingRuleContext alarmClear = (ReadingRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_ALRM_CLEAR_RULE);
		
		if(alarmTriggerRules != null) {
			
			WorkflowRuleContext temp = rule;
			if(alarmClear != null) {
				alarmTriggerRules.add(alarmClear);
			}
			for(ReadingRuleContext alarmTriggerRule :alarmTriggerRules) {
				
				alarmTriggerRule.setParentRuleId(temp.getId());
				alarmTriggerRule.setStatus(true);
				alarmTriggerRule.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				ruleId = WorkflowRuleAPI.addWorkflowRule(alarmTriggerRule);
				alarmTriggerRule.setId(ruleId);
				
				temp = alarmTriggerRule;
			}
			
		}
		
		return false;
	}
}
