package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;

public class AddActionForAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		List<ActionContext> actions = ActionAPI.addActions(alarmRule.getAddAlarmActions(), alarmRule.getPreRequsite());
		
		for( ReadingRuleContext rule :alarmRule.getAlarmTriggerRules()) {
			
			if (actions != null && !actions.isEmpty()) {
				ActionAPI.addWorkflowRuleActionRel(rule.getId(), actions);
				rule.setActions(actions);
			}
		}
		
		if(alarmRule.getAlarmClearRule() != null) {
			ActionContext actionContext = new ActionContext();
			actionContext.setActionType(ActionType.CLEAR_ALARM);
			actionContext.setStatus(true);
			
			actions = ActionAPI.addActions(Collections.singletonList(actionContext), alarmRule.getAlarmClearRule());
			
			ActionAPI.addWorkflowRuleActionRel(alarmRule.getAlarmClearRule().getId(), actions);
			
			alarmRule.getAlarmClearRule().setActions(actions);
		}
		
		
		return false;
	}

}
