package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class AddAlarmRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
		
		fillDefaultPropsForPreRequsite(preRequsiteRule);
		preRequsiteRule.setClearAlarm(alarmRule.isAutoClear());
		long ruleId = WorkflowRuleAPI.addWorkflowRule(preRequsiteRule);
		
		preRequsiteRule.setRuleGroupId(ruleId);
		WorkflowRuleAPI.updateWorkflowRule(preRequsiteRule);
		
		ReadingRuleAPI.addTriggerAndClearRule(alarmRule);
		return false;
	}
	
	private void fillDefaultPropsForPreRequsite(ReadingRuleContext preRequsiteRule) {
		preRequsiteRule.setStatus(true);
		preRequsiteRule.setRuleType(WorkflowRuleContext.RuleType.READING_RULE);
		preRequsiteRule.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
	}

}
