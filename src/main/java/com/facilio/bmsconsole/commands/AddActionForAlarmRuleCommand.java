package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;

public class AddActionForAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		List<ReadingRuleContext> rules = alarmRule.getAllRuleList();
		
		for( ReadingRuleContext rule :rules) {
			
			List<ActionContext> actions = rule.getActions();
			
			if (actions != null && !actions.isEmpty()) {
				actions = ActionAPI.addActions(actions, rule);
				
				ActionAPI.addWorkflowRuleActionRel(rule.getId(), actions);
				rule.setActions(actions);
			}
		}
		
		
		return false;
	}

}
