package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class DeleteOldAlarmRuleActionsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		List<ReadingRuleContext> rules = alarmRule.getAlarmTriggerRules();
		
		List<Long> ruleIds = new ArrayList<>();
		if(rules != null && !rules.isEmpty()) {
			for (WorkflowRuleContext rule : rules) {
				ruleIds.add(rule.getId());
			}
			ActionAPI.deleteAllActionsFromWorkflowRules(ruleIds);
		}
		
		return false;
	}

}
