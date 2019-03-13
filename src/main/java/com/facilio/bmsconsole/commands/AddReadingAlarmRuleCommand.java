package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.constants.FacilioConstants;

public class AddReadingAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		if(alarmRule.getReadingAlarmRuleContext() != null) {
			alarmRule.getReadingAlarmRuleContext().setReadingRuleGroupId(alarmRule.getPreRequsite().getRuleGroupId());
			WorkflowRuleAPI.addWorkflowRule(alarmRule.getReadingAlarmRuleContext());
			
			List<ActionContext> actions = ActionAPI.addActions(alarmRule.getReadingAlarmRuleContext().getActions(), alarmRule.getReadingAlarmRuleContext());
			ActionAPI.addWorkflowRuleActionRel(alarmRule.getReadingAlarmRuleContext().getId(), actions);
		}
		
		return false;
	}

}
