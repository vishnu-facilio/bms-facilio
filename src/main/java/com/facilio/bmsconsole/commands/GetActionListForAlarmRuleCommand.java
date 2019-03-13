package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;

public class GetActionListForAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		ReadingRuleContext alarmTriggerRule = alarmRule.getAlarmTriggerRule();
		List<ActionContext> actions = ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), alarmTriggerRule.getId());
		alarmTriggerRule.setActions(actions);
		
		List<ReadingRuleContext> rules = alarmRule.getAlarmRCARules();
		if(rules != null && !rules.isEmpty()) {
			for (WorkflowRuleContext rule : rules) {
				actions = ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), rule.getId());
				rule.setActions(actions);
			}
		}
		
		List<ReadingAlarmRuleContext> readingAlarmRuleContexts = alarmRule.getReadingAlarmRuleContexts();
		if(readingAlarmRuleContexts != null && !readingAlarmRuleContexts.isEmpty()) {
			for (WorkflowRuleContext rule : readingAlarmRuleContexts) {
				actions = ActionAPI.getAllActionsFromWorkflowRule(AccountUtil.getCurrentOrg().getId(), rule.getId());
				rule.setActions(actions);
			}
		}
		return false;
	}

}
